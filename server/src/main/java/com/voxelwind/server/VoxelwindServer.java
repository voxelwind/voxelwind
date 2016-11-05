package com.voxelwind.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spotify.futures.CompletableFutures;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockStateBuilder;
import com.voxelwind.api.plugin.PluginManager;
import com.voxelwind.api.server.LevelCreator;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.server.command.CommandManager;
import com.voxelwind.api.server.command.sources.ConsoleCommandExecutorSource;
import com.voxelwind.api.server.event.EventManager;
import com.voxelwind.api.server.event.server.ServerInitializeEvent;
import com.voxelwind.api.server.event.server.ServerStartEvent;
import com.voxelwind.server.command.VoxelwindCommandManager;
import com.voxelwind.server.command.VoxelwindConsoleCommandExecutorSource;
import com.voxelwind.server.command.builtin.GiveCommand;
import com.voxelwind.server.command.builtin.TestCommand;
import com.voxelwind.server.command.builtin.VersionCommand;
import com.voxelwind.server.event.VoxelwindEventManager;
import com.voxelwind.server.game.item.VoxelwindItemStackBuilder;
import com.voxelwind.server.game.level.LevelManager;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.block.VoxelwindBlockStateBuilder;
import com.voxelwind.server.game.level.provider.ChunkProvider;
import com.voxelwind.server.game.level.provider.FlatworldChunkProvider;
import com.voxelwind.server.game.level.provider.LevelDataProvider;
import com.voxelwind.server.game.level.provider.MemoryLevelDataProvider;
import com.voxelwind.server.game.level.provider.anvil.AnvilChunkProvider;
import com.voxelwind.server.game.level.provider.anvil.AnvilLevelDataProvider;
import com.voxelwind.server.network.listeners.McpeOverRakNetNetworkListener;
import com.voxelwind.server.network.listeners.NetworkListener;
import com.voxelwind.server.network.listeners.RconNetworkListener;
import com.voxelwind.server.network.session.SessionManager;
import com.voxelwind.server.network.util.NativeCodeFactory;
import com.voxelwind.server.plugin.VoxelwindPluginManager;
import io.netty.channel.epoll.Epoll;
import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class VoxelwindServer implements Server {
    public static final String VOXELWIND_VERSION = "0.0.1 (Layer of Fog)";
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindServer.class);
    private final SessionManager sessionManager = new SessionManager();
    private final LevelManager levelManager = new LevelManager();
    private final ScheduledExecutorService timerService = Executors.unconfigurableScheduledExecutorService(
            Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Voxelwind Ticker").setDaemon(true).build()));
    private List<NetworkListener> listeners = new CopyOnWriteArrayList<>();
    private final VoxelwindPluginManager pluginManager = new VoxelwindPluginManager(this);
    private final VoxelwindEventManager eventManager = new VoxelwindEventManager();
    private final ConsoleCommandExecutorSource consoleCommandExecutorSource = new VoxelwindConsoleCommandExecutorSource();
    private final VoxelwindCommandManager commandManager = new VoxelwindCommandManager();
    private VoxelwindConfiguration configuration;
    private VoxelwindLevel defaultLevel;

    public static void main(String... args) throws Exception {
        // RakNet doesn't really like IPv6
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        System.setProperty("java.net.preferIPv4Stack", "true");

        // Load native libraries early.
        boolean partiallySupportedLinux = Epoll.isAvailable();
        boolean fullySupportedLinux = NativeCodeFactory.cipher.load();

        if (partiallySupportedLinux) {
            NativeCodeFactory.zlib.load();
            if (fullySupportedLinux) {
                NativeCodeFactory.hash.load();
            } else {
                LOGGER.warn("You are running x64 Linux, but you are not using a fully-supported distribution. Server throughput and performance will be affected. Visit https://wiki.voxelwind.com/why_linux for more information.");
            }
        } else {
            LOGGER.warn("You are not running x64 Linux. Server throughput and performance will be affected. Visit https://wiki.voxelwind.com/why_linux for more information.");
        }

        VoxelwindServer server = new VoxelwindServer();
        server.boot();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    private void boot() throws Exception {
        // Say hello.
        LOGGER.info("{} {} is coming online...", getName(), getVersion());

        // Basic initialization.
        commandManager.register("version", new VersionCommand(this));
        commandManager.register("vwgive", new GiveCommand());
        commandManager.register("vwtest", new TestCommand());

        // Load configuration.
        loadConfiguration();

        // Load all plugins.
        loadPlugins();

        // Fire the initialize event
        eventManager.fire(ServerInitializeEvent.INSTANCE);

        LOGGER.info("Loading worlds...");

        // Start the levels.
        List<CompletableFuture<Level>> loadingLevels = new ArrayList<>();
        String defaultLevelName = null;
        for (Map.Entry<String, VoxelwindConfiguration.LevelConfiguration> entry : configuration.getLevels().entrySet()) {
            LevelCreator creator = LevelCreator.builder()
                    .enableWrite(false)
                    .loadSpawnChunks(entry.getValue().isLoadSpawnChunks())
                    .name(entry.getKey())
                    .type(entry.getValue().getStorage())
                    .worldPath(Paths.get(entry.getValue().getDirectory()))
                    .build();

            loadingLevels.add(createLevel(creator));

            if (entry.getValue().isDefault()) {
                defaultLevelName = entry.getKey();
            }
        }

        if (defaultLevelName == null) {
            LOGGER.fatal("No default level specified. Stopping!");
            System.exit(1);
        }

        for (CompletableFuture<Level> levelFuture : loadingLevels) {
            Level loadedLevel = null;
            try {
                loadedLevel = levelFuture.join();
            } catch (Throwable e) {
                LOGGER.fatal("Unable to load a level, we are halting.", e);
                System.exit(1);
            }
            if (loadedLevel.getName().equals(defaultLevelName)) {
                defaultLevel = (VoxelwindLevel) loadedLevel;
            }
        }

        if (defaultLevel == null) {
            LOGGER.fatal("No default level specified. Stopping!");
            System.exit(1);
        }

        // Bind to a port.
        McpeOverRakNetNetworkListener listener = new McpeOverRakNetNetworkListener(this, configuration.getMcpeListener().getHost(), configuration.getMcpeListener().getPort(),
                configuration.isUseSoReuseport());
        listener.bind();
        listeners.add(listener);

        if (configuration.getRcon().isEnabled()) {
            RconNetworkListener rconListener = new RconNetworkListener(this, configuration.getRcon().getPassword().getBytes(StandardCharsets.UTF_8));
            rconListener.bind();
            listeners.add(rconListener);
        }
        configuration.getRcon().clearPassword();

        LOGGER.info("Now alive on {}.", listener.getAddress());

        timerService.scheduleAtFixedRate(sessionManager::onTick, 50, 50, TimeUnit.MILLISECONDS);

        // Send another event.
        eventManager.fire(ServerStartEvent.INSTANCE);

        // Sleep forever for now until we have a console reader.
        while (true) {
            Thread.sleep(1000);
        }
    }

    private void loadConfiguration() throws Exception {
        Path configFile = Paths.get("voxelwind.json");
        try {
            configuration = VoxelwindConfiguration.load(configFile);
            if (configuration.addMissingFields()) {
                VoxelwindConfiguration.save(configFile, configuration);
            }
        } catch (NoSuchFileException e) {
            configuration = VoxelwindConfiguration.defaultConfiguration();
            VoxelwindConfiguration.save(configFile, configuration);
        }
    }

    private void loadPlugins() throws Exception {
        LOGGER.info("Loading plugins...");
        try {
            Path pluginPath = Paths.get("plugins");
            if (Files.notExists(pluginPath)) {
                Files.createDirectory(pluginPath);
            } else {
                if (!Files.isDirectory(pluginPath)) {
                    LOGGER.info("Plugin location {} is not a directory, continuing without loading plugins.", pluginPath);
                    return;
                }
            }
            pluginManager.loadPlugins(pluginPath);
            pluginManager.getAllPlugins().forEach(p -> eventManager.register(p.getPlugin(), p.getPlugin()));
        } catch (Exception e) {
            LOGGER.error("Can't load plugins", e);
        }
        LOGGER.info("Loaded {} plugins.", pluginManager.getAllPlugins().size());
    }

    @Override
    public String getName() {
        return "Voxelwind";
    }

    @Override
    public String getVersion() {
        return VOXELWIND_VERSION;
    }

    @Override
    public Collection<Player> getPlayers() {
        return sessionManager.allPlayers();
    }

    @Override
    public Collection<Level> getAllLevels() {
        return levelManager.all();
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public ConsoleCommandExecutorSource getConsoleCommandExecutorSource() {
        return consoleCommandExecutorSource;
    }

    @Override
    public ItemStackBuilder createItemStackBuilder() {
        return new VoxelwindItemStackBuilder();
    }

    @Override
    public BlockStateBuilder createBlockStateBuilder() {
        return new VoxelwindBlockStateBuilder();
    }

    @Override
    public Collection<Player> getAllOnlinePlayers() {
        return sessionManager.allPlayers();
    }

    @Override
    public Collection<Level> getLoadedLevels() {
        return levelManager.all();
    }

    @Override
    public CompletableFuture<Level> createLevel(LevelCreator creator) {
        Preconditions.checkNotNull(creator, "creator");
        LOGGER.info("Creating level '{}'...", creator.getName());

        CompletableFuture<LevelDataProvider> stage1 = new CompletableFuture<>();

        ChunkProvider provider;
        switch (creator.getType()) {
            case ANVIL:
                provider = new AnvilChunkProvider(creator.getWorldPath());
                break;
            case FLATWORLD:
                provider = FlatworldChunkProvider.INSTANCE;
                break;
            default:
                throw new IllegalArgumentException("Invalid type");
        }

        // Stage 1: load level data
        if (creator.getType() == LevelCreator.WorldType.ANVIL) {
            ForkJoinPool.commonPool().execute(() -> {
                try {
                    AnvilLevelDataProvider levelDataProvider = AnvilLevelDataProvider.load(creator.getWorldPath().resolve("level.dat"));
                    stage1.complete(levelDataProvider);
                } catch (IOException e) {
                    stage1.completeExceptionally(e);
                }
            });
        } else if (creator.getType() == LevelCreator.WorldType.FLATWORLD) {
            stage1.complete(new MemoryLevelDataProvider());
        }

        // Stage 2: create level
        CompletableFuture<Level> stage2 = stage1.thenApplyAsync(levelDataProvider -> {
            Level level = new VoxelwindLevel(this, creator.getName(), provider, levelDataProvider);
            levelManager.register(level);
            levelManager.start((VoxelwindLevel) level);
            return level;
        });

        // Stage 3: load chunks (if needed)
        if (creator.isLoadSpawnChunks()) {
            CompletableFuture<Level> stage3 = new CompletableFuture<>();
            stage2.whenComplete((level, throwable) -> {
                if (throwable != null) {
                    stage3.completeExceptionally(throwable);
                    return;
                }

                LOGGER.info("Loading spawn chunks for level '{}'...", level.getName());
                int spawnChunkX = level.getSpawnLocation().getFloorX() >> 4;
                int spawnChunkZ = level.getSpawnLocation().getFloorZ() >> 4;
                List<CompletableFuture<?>> loadChunkFutures = new ArrayList<>();
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        loadChunkFutures.add(level.getChunk(spawnChunkX + x, spawnChunkZ + z));
                    }
                }
                CompletableFuture<?> loadingFuture = CompletableFuture.allOf(
                        loadChunkFutures.toArray(new CompletableFuture[loadChunkFutures.size()]));
                loadingFuture.whenComplete((o, throwable2) -> {
                    if (throwable2 != null) {
                        LOGGER.error("Unable to load spawn chunks for level '{}'.", level.getName(), throwable2);
                        stage3.completeExceptionally(throwable2);
                    } else {
                        LOGGER.info("Successfully loaded spawn chunks for level '{}'.", level.getName());
                        stage3.complete(level);
                    }
                });
            });
            return stage3;
        } else {
            return stage2;
        }
    }

    @Override
    public boolean unloadLevel(String name) {
        throw new UnsupportedOperationException();
    }

    public VoxelwindConfiguration getConfiguration() {
        return configuration;
    }

    public VoxelwindLevel getDefaultLevel() {
        return defaultLevel;
    }
}
