package net.snuck.clans;

import lombok.Getter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import net.milkbowl.vault.economy.Economy;
import net.snuck.clans.command.ClanChatCommand;
import net.snuck.clans.command.ClanCommand;
import net.snuck.clans.database.*;
import net.snuck.clans.database.manager.*;
import net.snuck.clans.event.PlayerDamageListener;
import net.snuck.clans.event.PlayerJoinListener;
import net.snuck.clans.event.PlayerLeftListener;
import net.snuck.clans.hook.ClanExpansion;
import net.snuck.clans.object.*;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    @Getter private static Main plugin;
    @Getter private static IData iData;
    @Getter private static Economy econ = null;
    @Getter private static PluginManager pm;

    @Getter private static final HashMap<String, ClanPlayer> playerCache = new HashMap<>();
    @Getter private static final HashMap<String, Clan> clanCache = new HashMap<>();

    @Getter private static final Logger log = Logger.getLogger("Minecraft");

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

        if(pm.getPlugin("PlaceholderAPI") != null) {
            new ClanExpansion().register();
        }

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
        (iData = type.equalsIgnoreCase("mysql") ? new MySQL(
                getConfig().getString("database.host"),
                getConfig().getInt("database.port"),
                getConfig().getString("database.user"),
                getConfig().getString("database.password"),
                getConfig().getString("database.database")
        ) : new SQLite()).open();

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
}
