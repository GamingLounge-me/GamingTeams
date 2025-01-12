package me.gaminglounge.gamingteams;
 
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager; 
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.gaminglounge.gamingteams.commands.TeamCommand; 
 
public final class Gamingteams extends JavaPlugin { 
 
    public static Gamingteams INSTANCE; 
    public static FileConfiguration CONFIG;
    public static TeamManager manager;
    public DataBasePool basePool;
 
    @Override
    public void onLoad() {
        INSTANCE = this; 
        this.saveDefaultConfig();
        CONFIG = this.getConfig();

        basePool = new DataBasePool();
        basePool.init();

        try {
            basePool.createTablePlayer();
            basePool.createTableTeams();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        manager = new TeamManager();

        if (!CommandAPI.isLoaded()) CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        new TeamCommand();
    }

    @Override
    public void onEnable() {
        // this.listener();
    }

    @Override
    public void onDisable() {
        
    }

    public void listener() {
        PluginManager pm = getServer().getPluginManager();

        // pm.registerEvents(new InvClickEvent(), this);
    } 
} 
