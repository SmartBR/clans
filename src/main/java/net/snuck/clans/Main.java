package net.snuck.clans;

import me.saiintbrisson.bukkit.command.BukkitFrame;
import net.milkbowl.vault.economy.Economy;
import net.snuck.clans.command.ClanChatCommand;
import net.snuck.clans.command.ClanCommand;
import net.snuck.clans.database.*;
import net.snuck.clans.database.manager.*;
import net.snuck.clans.event.PlayerDamageListener;
import net.snuck.clans.event.PlayerJoinListener;
import net.snuck.clans.event.PlayerLeftListener;
import net.snuck.clans.object.*;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static IData iData;
    private static Economy econ = null;
    private static PluginManager pm;

    private static final HashMap<String, ClanPlayer> playerCache = new HashMap<>();
    private static final HashMap<String, Clan> clanCache = new HashMap<>();

    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        plugin = this;
        init();

    }

    private void init() {
        pm = Bukkit.getPluginManager();
        saveDefaultConfig();

        if(!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(getPlugin());
            return;
        }

        setupDatabase();
        setupEvents();

        CacheManager.loadUsersCache();
        CacheManager.loadClansCache();

        BukkitFrame frame = new BukkitFrame(getPlugin());

        frame.registerCommands(new ClanCommand());
        frame.registerCommands(new ClanChatCommand());


        log.info(String.format("[%s] - Enabled version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private void setupEvents() {
        pm.registerEvents(new PlayerJoinListener(), getPlugin());
        pm.registerEvents(new PlayerLeftListener(), getPlugin());
        pm.registerEvents(new PlayerDamageListener(), getPlugin());
    }

    private void setupDatabase() {
        String type = getConfig().getString("database.type");

        assert type != null;
        if(type.equalsIgnoreCase("mysql")) {
                String host = getConfig().getString("database.host");
                int port = getConfig().getInt("database.port");
                String user = getConfig().getString("database.user");
                String password = getConfig().getString("database.password");
                String database = getConfig().getString("database.database");

                iData = new MySQL(host, port, user, password, database);
        } else if(type.equalsIgnoreCase("sqlite")) {
            iData = new SQLite();
        }

        iData.open();

        PlayerSQLManager.createTable("users", "uuid varchar(36), clan_id varchar(36), role varchar(16)");
        PlayerSQLManager.createTable("clans", "id varchar(36), tag varchar(3), name varchar(16)");
        PlayerSQLManager.createTable("invites", "player_id varchar(36), clan_id varchar(36), invite_id int NOT NULL AUTO_INCREMENT, PRIMARY KEY (invite_id)");
    }

    @Override
    public void onDisable() {
        CacheManager.saveUsersCache();
        CacheManager.saveClansCache();

        iData.close();
        HandlerList.unregisterAll();

        log.info(String.format("[%s] - Disabled version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static IData getiData() {
        return iData;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static HashMap<String, ClanPlayer> getPlayerCache() {
        return playerCache;
    }

    public static HashMap<String, Clan> getClanCache() {
        return clanCache;
    }
}
