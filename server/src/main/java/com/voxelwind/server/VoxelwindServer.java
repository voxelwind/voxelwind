package com.voxelwind.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.plugin.PluginManager;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.server.command.CommandManager;
import com.voxelwind.api.server.command.sources.ConsoleCommandExecutorSource;
import com.voxelwind.api.server.event.EventManager;
import com.voxelwind.api.server.event.server.ServerInitializeEvent;
import com.voxelwind.api.server.event.server.ServerStartEvent;
import com.voxelwind.server.command.VoxelwindCommandManager;
import com.voxelwind.server.command.VoxelwindConsoleCommandExecutorSource;
import com.voxelwind.server.command.builtin.GiveItemTestCommand;
import com.voxelwind.server.command.builtin.VersionCommand;
import com.voxelwind.server.event.VoxelwindEventManager;
import com.voxelwind.server.game.item.VoxelwindItemStackBuilder;
import com.voxelwind.server.game.level.LevelCreator;
import com.voxelwind.server.game.level.LevelManager;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.provider.FlatworldChunkProvider;
import com.voxelwind.server.game.level.provider.MemoryLevelDataProvider;
import com.voxelwind.server.game.level.provider.anvil.AnvilChunkProvider;
import com.voxelwind.server.game.level.provider.anvil.AnvilLevelDataProvider;
import com.voxelwind.server.network.listeners.McpeOverRakNetNetworkListener;
import com.voxelwind.server.network.listeners.NetworkListener;
import com.voxelwind.server.network.util.NativeCodeFactory;
import com.voxelwind.server.network.listeners.RconNetworkListener;
import com.voxelwind.server.network.session.SessionManager;
import com.voxelwind.server.plugin.VoxelwindPluginManager;
import io.netty.channel.epoll.Epoll;
import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoxelwindServer implements Server {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindServer.class);
    private final SessionManager sessionManager = new SessionManager();
    private final LevelManager levelManager = new LevelManager();
    private final ScheduledExecutorService timerService = Executors.unconfigurableScheduledExecutorService(
            Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Voxelwind Ticker").setDaemon(true).build()));
    private List<NetworkListener> listeners = new CopyOnWriteArrayList<>();
    private VoxelwindLevel defaultLevel;
    private final VoxelwindPluginManager pluginManager = new VoxelwindPluginManager(this);
    private final VoxelwindEventManager eventManager = new VoxelwindEventManager();
    private final ConsoleCommandExecutorSource consoleCommandExecutorSource = new VoxelwindConsoleCommandExecutorSource();
    private final VoxelwindCommandManager commandManager = new VoxelwindCommandManager();
    private VoxelwindConfiguration configuration;

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
        commandManager.register("giveitem", new GiveItemTestCommand());

        // Load configuration.
        Path configFile = Paths.get("voxelwind.json");
        try {
            configuration = VoxelwindConfiguration.load(configFile);
        } catch (NoSuchFileException e) {
            configuration = VoxelwindConfiguration.defaultConfiguration();
            VoxelwindConfiguration.save(configFile, configuration);
        }

        // Load plugins.
        try {
            Path pluginPath = Paths.get("plugins");
            if (Files.notExists(pluginPath)) {
                Files.createDirectory(pluginPath);
            }
            pluginManager.loadPlugins(pluginPath);
            pluginManager.getAllPlugins().forEach(p -> eventManager.register(p.getPlugin(), p.getPlugin()));
        } catch (Exception e) {
            LOGGER.error("Can't load plugins", e);
        }

        // Fire the initialize event
        eventManager.fire(ServerInitializeEvent.INSTANCE);

        // Bind to a port.
        McpeOverRakNetNetworkListener listener = new McpeOverRakNetNetworkListener(this, configuration.getBindHost(), configuration.getPort(),
                configuration.isUseSoReuseport());
        listener.bind();
        listeners.add(listener);

        if (configuration.getRcon().isEnabled()) {
            RconNetworkListener rconListener = new RconNetworkListener(this, configuration.getRcon().getPassword().getBytes(StandardCharsets.UTF_8));
            rconListener.bind();
            listeners.add(rconListener);
        }
        configuration.getRcon().clearPassword();

        // Start the example level.
        //defaultLevel = new VoxelwindLevel(this, new LevelCreator("test",
        //        new AnvilChunkProvider(Paths.get("/Users/andrew/Library/Application Support/minecraft/saves/test-mca")),
        //        AnvilLevelDataProvider.load(Paths.get("/Users/andrew/Library/Application Support/minecraft/saves/test-mca/level.dat"))));
        defaultLevel = new VoxelwindLevel(this, new LevelCreator("test", FlatworldChunkProvider.INSTANCE, new MemoryLevelDataProvider()));
        levelManager.register(defaultLevel);
        levelManager.start(defaultLevel);

        LOGGER.info("Now alive on {}.", listener.getAddress());

        timerService.scheduleAtFixedRate(sessionManager::onTick, 50, 50, TimeUnit.MILLISECONDS);

        // Send another event.
        eventManager.fire(ServerStartEvent.INSTANCE);

        // Sleep forever for now until we have a console reader.
        while (true) {
            Thread.sleep(1000);
        }
    }

    public VoxelwindLevel getDefaultLevel() {
        return defaultLevel;
    }

    @Override
    public String getName() {
        return "Voxelwind";
    }

    @Override
    public String getVersion() {
        return "0.0.1 (Layer of Fog)";
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
    public Collection<Player> getAllOnlinePlayers() {
        return sessionManager.allPlayers();
    }

    public VoxelwindConfiguration getConfiguration() {
        return configuration;
    }
}
