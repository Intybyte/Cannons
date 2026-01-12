package at.pavlov.cannons;

import at.pavlov.cannons.API.CannonsAPI;
import at.pavlov.cannons.Enum.MessageEnum;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonDesign;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.cannon.DesignStorage;
import at.pavlov.cannons.commands.CannonsCommandManager;
import at.pavlov.cannons.commands.Commands;
import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.config.UserMessages;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.cannons.dao.AsyncTaskManager;
import at.pavlov.cannons.dao.PersistenceDatabase;
import at.pavlov.cannons.exchange.ExchangeLoader;
import at.pavlov.cannons.hooks.VaultHook;
import at.pavlov.cannons.hooks.movecraft.MovecraftHook;
import at.pavlov.cannons.hooks.movecraft.type.CannonProperties;
import at.pavlov.cannons.hooks.movecraftcombat.MovecraftCombatHook;
import at.pavlov.cannons.hooks.papi.PlaceholderAPIHook;
import at.pavlov.cannons.listener.BlockListener;
import at.pavlov.cannons.listener.EntityListener;
import at.pavlov.cannons.listener.PlayerListener;
import at.pavlov.cannons.listener.RedstoneListener;
import at.pavlov.cannons.listener.UpdateNotifier;
import at.pavlov.cannons.metric.CannonMetrics;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.cannons.projectile.ProjectileManager;
import at.pavlov.cannons.projectile.ProjectileStorage;
import at.pavlov.cannons.projectile.definitions.ProjectileDefinitionLoader;
import at.pavlov.cannons.scheduler.FakeBlockHandler;
import at.pavlov.cannons.scheduler.ProjectileObserver;
import at.pavlov.cannons.utils.CannonSelector;
import at.pavlov.cannons.utils.TimeUtils;
import at.pavlov.internal.CLogger;
import at.pavlov.internal.HookManager;
import at.pavlov.internal.Key;
import at.pavlov.internal.ModrinthUpdateChecker;
import at.pavlov.internal.key.registries.Registries;
import at.pavlov.internal.projectile.definition.KeyedDefaultProjectile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.logging.Logger;

public final class Cannons extends JavaPlugin {
    private final Logger logger = Logger.getLogger("Minecraft");
    private final String cannonDatabase = "cannonlist_2_4_6";
    private final String whitelistDatabase = "whitelist_2_4_6";
    private PluginManager pm;
    private boolean debugMode = false;
    private Config config;
    private FireCannon fireCannon;
    private ProjectileObserver observer;
    private CannonsAPI cannonsAPI;
    @Getter
    private final HookManager hookManager = new HookManager();
    // database
    private PersistenceDatabase persistenceDatabase;
    private Connection connection = null;
    @Getter
    private volatile Boolean isLatest;
    @Getter
    private ModrinthUpdateChecker updateChecker;
    @Getter
    private boolean folia = false;

    public static Cannons getPlugin() {
        return (Cannons) Bukkit.getPluginManager().getPlugin("Cannons");
    }

    public static void logSDebug(String msg) {
        if (getPlugin().isDebugMode())
            Cannons.logger().info(msg);
    }

    public static Logger logger() {
        return Cannons.getPlugin().getLogger();
    }

    public void onLoad() {
        Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.setFrozen(false);
        for (EntityType type : EntityType.values()) {
            if (!type.isSpawnable()) continue;
            NamespacedKey key = type.getKey();
            Key cannonKey = Key.from(key.toString());

            if (!Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.has(cannonKey)) {
                Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.register(
                    new KeyedDefaultProjectile(cannonKey)
                );
            }
        }
        Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.setFrozen(true);

        CLogger.logger = this.getLogger();
        // must be done in onLoad because "movecraft"
        AsyncTaskManager.initialize(this);
        Config.initialize(this);
        this.config = Config.getInstance();
        UserMessages.initialize(this);
        CannonManager.initialize(this);

        initUpdater();

        if (config.isMovecraftEnabled()) {
            try {
                Class.forName("net.countercraft.movecraft.craft.type.property.Property");
                CannonProperties.register();
            } catch (Exception ignored) {
            }

        }

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (Exception ignored) {
        }
    }

    private void initUpdater() {
        var taskManager = AsyncTaskManager.get();
        updateChecker = new ModrinthUpdateChecker(this.getLogger());
        taskManager.async.submit(() -> {
            isLatest = updateChecker.isLatest(this
                    .getPluginDescription()
                    .getVersion()
            );
        });
    }

    public void onDisable() {
        AsyncTaskManager.get().scheduler.cancelTasks();
        AsyncTaskManager.get().async.shutdown();

        // save database on shutdown
        logger.info(getLogPrefix() + "Wait until scheduler is finished");
        while (getPlugin().getPersistenceDatabase().isSaveTaskRunning()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info(getLogPrefix() + "Scheduler finished");
        persistenceDatabase.saveAllCannons(false);
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logger.info(getLogPrefix() + "Cannons plugin v" + getPluginDescription().getVersion() + " has been disabled");
        hookManager.disableHooks();
    }

    public void onEnable() {
        ProjectileDefinitionLoader.load();
        long startTime = System.nanoTime();
        pm = getServer().getPluginManager();
        if (!pm.isPluginEnabled("WorldEdit")) {
            //no worldEdit has been loaded. Disable plugin
            this.logSevere(ChatColor.RED + "Please install WorldEdit, else Cannons can't load.");
            this.logSevere(ChatColor.RED + "Plugin is now disabled.");

            pm.disablePlugin(this);
            return;
        }

        TimeUtils.testTime(this::initHooks, this::logDebug, "Hooks initialization");

        ExchangeLoader.registerDefaults();

		DesignStorage.initialize(this);
		ProjectileStorage.initialize(this);
		ProjectileManager.initialize(this);
		CannonSelector.initialize(this);

        DesignStorage.getInstance().loadCannonDesigns();
        ProjectileStorage.getInstance().loadProjectiles();
        CannonManager.getInstance().updateCannons();
        UserMessages.getInstance().loadLanguage();

        CreateExplosion.initialize(this);
        this.fireCannon = new FireCannon(this); //probably more fitting to be a util class
        Aiming.initialize(this);
        this.observer = new ProjectileObserver(this); //this is just a scheduler wrapper
        FakeBlockHandler.initialize(this);
        this.cannonsAPI = new CannonsAPI(this);

        this.persistenceDatabase = new PersistenceDatabase(this);

        TimeUtils.testTime(this::initListeners, this::logDebug, "Listeners initialization");

        TimeUtils.testTime(this::initCommands, this::logDebug, "Commands initialization");

        // Initialize the database
        AsyncTaskManager.get().async.submit(() -> TimeUtils.testTime(() -> {
            try {
                openConnection();
                Statement statement = connection.createStatement();
                statement.close();
                getPlugin().logInfo("Connected to database");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            //create the tables for the database in case they don't exist
            persistenceDatabase.createTables();
            // load cannons from database
            persistenceDatabase.loadCannons();
        }, this::logDebug, "Database connection"));


        // setting up Aiming Mode Task
        Aiming.getInstance().initAimingMode();
        // setting up the Teleporter
        observer.setupScheduler();
        FakeBlockHandler.getInstance().setupScheduler();

        // save cannons
        AsyncTaskManager.get().scheduler.runTaskTimer(() -> persistenceDatabase.saveAllCannons(true), 6000L, 6000L);

        TimeUtils.testTime(this::initMetrics, this::logDebug, "Metrics initialization");
        logDebug("Time to enable cannons: " + new DecimalFormat("0.00").format((System.nanoTime() - startTime) / 1000000.0) + "ms");

        // Plugin succesfully enabled
        logger.info(getLogPrefix() + "Cannons plugin v" + getPluginDescription().getVersion() + " has been enabled");
    }

    private void initMetrics() {
        CannonMetrics metrics = new CannonMetrics(this);
        metrics.setupCharts();
    }

    private void initListeners() {
        BlockListener blockListener = new BlockListener(this);
        PlayerListener playerListener = new PlayerListener(this);
        EntityListener entityListener = new EntityListener(this);
        RedstoneListener redstoneListener = new RedstoneListener(this);
        UpdateNotifier updateNotifier = new UpdateNotifier(this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(redstoneListener, this);
        pm.registerEvents(updateNotifier, this);
    }

    private void initHooks() {
        logDebug("Loading VaultHook");
        VaultHook vaultHook = new VaultHook(this);
        hookManager.registerHook(vaultHook);

        logDebug("Loading MovecraftHook");
        MovecraftHook movecraftHook = new MovecraftHook(this);
        hookManager.registerHook(movecraftHook);

        logDebug("Loading MovecraftCombatHook");
        MovecraftCombatHook movecraftCombatHook = new MovecraftCombatHook(this);
        hookManager.registerHook(movecraftCombatHook);

        logDebug("Loading PlaceholderAPIHook");
        PlaceholderAPIHook placeholderAPIHook = new PlaceholderAPIHook(this);
        hookManager.registerHook(placeholderAPIHook);

        AsyncTaskManager.get().scheduler.runTaskLater(() -> {
            if (!pm.isPluginEnabled("Movecraft-Cannons")) {
                return;
            }

            if (!hookManager.isRegistered(MovecraftHook.class)) {
                return;
            }

            logSevere("Movecraft-Cannons found, disabling hook." +
                    " You don't need to add Movecraft-Cannons anymore as Movecraft support is now embedded," +
                    " we suggest you stop using it as in the future it might stop work properly.");

            if (hookManager.isRegistered(MovecraftCombatHook.class)) {

            }
            movecraftHook.onDisable();
        }, 1L);
    }

    private void initCommands() {
        var cannonsCommandManager = new CannonsCommandManager(this);
        cannonsCommandManager.registerCommand(new Commands(this));
    }

    // set up ebean database
    private void openConnection() throws SQLException, ClassNotFoundException {
        String driver = getConfig().getString("database.driver", "org.sqlite.JDBC");
        String url = getConfig().getString("database.url", "jdbc:sqlite:{DIR}{NAME}.db");
        String username = getConfig().getString("database.username", "bukkit");
        String password = getConfig().getString("database.password", "walrus");
        //String serializable = getConfig().getString("database.isolation", "SERIALIZABLE");

        url = url.replace("{DIR}{NAME}.db", "plugins/Cannons/Cannons.db");

        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        }
    }

    public boolean hasConnection() {
        return this.connection != null;
    }

    public boolean isPluginEnabled() {
        return this.isEnabled();
    }

    public Config getMyConfig() {
        return config;
    }

    public void disablePlugin() {
        pm.disablePlugin(this);
    }

    private String getLogPrefix() {
        return "[" + getPluginDescription().getName() + "] ";
    }

    public void logSevere(String msg) {
        //msg = ChatColor.translateAlternateColorCodes('&', msg);
        this.logger.severe(getLogPrefix() + ChatColor.stripColor(msg));
    }

    public void logInfo(String msg) {
        //msg = ChatColor.translateAlternateColorCodes('&', msg);
        this.logger.info(getLogPrefix() + ChatColor.stripColor(msg));
    }

    public void logDebug(String msg) {
        if (debugMode)
            this.logger.info(getLogPrefix() + ChatColor.stripColor(msg));
    }

    public void broadcast(String msg) {
        this.getServer().broadcastMessage(msg);
    }

    public PluginDescriptionFile getPluginDescription() {
        return this.getDescription();
    }

    public Connection getConnection() {
        return this.connection;
    }

    public PersistenceDatabase getPersistenceDatabase() {
        return persistenceDatabase;
    }

    @Deprecated(forRemoval = true)
    public CannonManager getCannonManager() {
        return CannonManager.getInstance();
    }

    public FireCannon getFireCannon() {
        return fireCannon;
    }

    @Deprecated(forRemoval = true)
    public CreateExplosion getExplosion() {
        return CreateExplosion.getInstance();
    }

    @Deprecated(forRemoval = true)
    public Aiming getAiming() {
        return Aiming.getInstance();
    }

    @Deprecated(forRemoval = true)
    public DesignStorage getDesignStorage() {
        return DesignStorage.getInstance();
    }

    @Deprecated(forRemoval = true)
    public CannonDesign getCannonDesign(Cannon cannon) {
        return getDesignStorage().getDesign(cannon);
    }

    @Deprecated(forRemoval = true)
    public CannonDesign getCannonDesign(String designId) {
        return getDesignStorage().getDesign(designId);
    }

    @Deprecated(forRemoval = true)
    public ProjectileStorage getProjectileStorage() {
        return ProjectileStorage.getInstance();
    }

    public Projectile getProjectile(Cannon cannon, ItemHolder materialHolder) {
        return ProjectileStorage.getProjectile(cannon, materialHolder);
    }

    public Projectile getProjectile(Cannon cannon, ItemStack item) {
        return ProjectileStorage.getProjectile(cannon, item);
    }

    public Cannon getCannon(UUID id) {
        return CannonManager.getCannon(id);
    }

    public void sendMessage(Player player, Cannon cannon, MessageEnum message) {
        UserMessages.getInstance().sendMessage(message, player, cannon);
    }

    public void sendImpactMessage(Player player, Location impact, boolean canceled) {
        UserMessages.getInstance().sendImpactMessage(player, impact, canceled);
    }

    @Deprecated(forRemoval = true)
    public void createCannon(Cannon cannon, boolean saveToDatabase) {
        CannonManager.getInstance().createCannon(cannon, saveToDatabase);
    }

    public ProjectileObserver getProjectileObserver() {
        return observer;
    }

    @Deprecated(forRemoval = true)
    public ProjectileManager getProjectileManager() {
        return ProjectileManager.getInstance();
    }

    public CannonsAPI getCannonsAPI() {
        return cannonsAPI;
    }

    @Deprecated(forRemoval = true)
    public FakeBlockHandler getFakeBlockHandler() {
        return FakeBlockHandler.getInstance();
    }

    public String getCannonDatabase() {
        return cannonDatabase;
    }

    public String getWhitelistDatabase() {
        return whitelistDatabase;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
