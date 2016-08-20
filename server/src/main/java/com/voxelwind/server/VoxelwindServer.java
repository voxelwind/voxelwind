package com.voxelwind.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import com.voxelwind.server.event.VoxelwindEventManager;
import com.voxelwind.server.game.level.LevelCreator;
import com.voxelwind.server.game.level.LevelManager;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.provider.FlatworldChunkProvider;
import com.voxelwind.server.game.level.provider.MemoryLevelDataProvider;
import com.voxelwind.server.network.Native;
import com.voxelwind.server.network.NettyVoxelwindNetworkListener;
import com.voxelwind.server.network.rcon.NettyVoxelwindRconListener;
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
    private NettyVoxelwindNetworkListener listener;
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

        if (!Epoll.isAvailable()) {
            LOGGER.warn("Your platform does not support epoll. Server throughput and performance may suffer. To resolve this issue, run your server on Linux.");
        }

        if (!Native.zlib.load()) {
            LOGGER.warn("Your platform does not support native compression. Server throughput and performance may suffer. To resolve this issue, make sure you're using 64-bit Linux.");
        }

        if (!Native.cipher.load()) {
            LOGGER.warn("Your platform does not support native encryption. Server throughput and performance may suffer. To resolve this issue, make sure you're using 64-bit Debian/Ubuntu.");
        }

        VoxelwindServer server = new VoxelwindServer();
        server.boot();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void boot() throws Exception {
        // Say hello.
        LOGGER.info("{} {} is coming online...", getName(), getVersion());

        // Load configuration.
        try {
            configuration = VoxelwindConfiguration.load(Paths.get("config.json"));
        } catch (NoSuchFileException e) {
            configuration = VoxelwindConfiguration.defaultConfiguration();
            VoxelwindConfiguration.save(Paths.get("config.json"), configuration);
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
        listener = new NettyVoxelwindNetworkListener(this, configuration.getBindHost(), configuration.getPort());
        listener.bind();

        if (configuration.getRcon().isEnabled()) {
            NettyVoxelwindRconListener rconListener = new NettyVoxelwindRconListener(this, configuration.getRcon().getPassword().getBytes(StandardCharsets.UTF_8));
            rconListener.bind(configuration.getRcon().getBindHost(), configuration.getRcon().getPort());
        }
        configuration.getRcon().clearPassword();

        // Start the example level.
        defaultLevel = new VoxelwindLevel(new LevelCreator("test", FlatworldChunkProvider.INSTANCE, new MemoryLevelDataProvider()));
        levelManager.register(defaultLevel);
        levelManager.start(defaultLevel);

        LOGGER.info("Now alive on {}.", listener.getAddress());

        timerService.scheduleAtFixedRate(sessionManager::onTick, 50, 50, TimeUnit.MILLISECONDS);

        // Send another event.
        eventManager.fire(ServerStartEvent.INSTANCE);

        Thread.sleep(10000000);
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
        return "0.1 (Layer of Fog)";
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
}
