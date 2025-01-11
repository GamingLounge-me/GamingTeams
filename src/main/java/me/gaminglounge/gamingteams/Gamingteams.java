package me.gaminglounge.gamingteams;
 
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager; 
import org.bukkit.plugin.java.JavaPlugin; 
 
public final class Gamingteams extends JavaPlugin { 
 
    public static Gamingteams INSTANCE; 
    public static FileConfiguration CONFIG;
    public static TeamManager manager;
 
    @Override
    public void onLoad() {
        INSTANCE = this; 
        CONFIG = this.getConfig();

        manager = new TeamManager();
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
