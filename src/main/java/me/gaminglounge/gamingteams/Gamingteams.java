package me.gaminglounge.gamingteams;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.gaminglounge.configapi.LoadConfig;
import me.gaminglounge.gamingteams.commands.TeamCommand;
import me.gaminglounge.gamingteams.listener.AddClearPlaceholder;

public final class Gamingteams extends JavaPlugin {

    public static Gamingteams INSTANCE;
    public static FileConfiguration CONFIG;
    public static TeamManager manager;
    public static PlaceholderManager ph;
    public DataBasePool basePool;

    @Override
    public void onLoad() {
        INSTANCE = this;
        this.saveDefaultConfig();
        CONFIG = this.getConfig();

        basePool = new DataBasePool();
        basePool.init();

        ph = new PlaceholderManager(this);

        try {
            basePool.createTablePlayer();
            basePool.createTableTeams();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<String, InputStream> lang = new HashMap<>();
        lang.put("en_US.json", this.getResource("lang/en_US.json"));
        LoadConfig.registerLanguage(this, lang);

        new Events();

        manager = new TeamManager();

        if (!CommandAPI.isLoaded())
            CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        new TeamCommand();
    }

    @Override
    public void onEnable() {
        this.listener();
        ph.register();
    }

    @Override
    public void onDisable() {

    }

    public void listener() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new AddClearPlaceholder(), this);
    }
}
