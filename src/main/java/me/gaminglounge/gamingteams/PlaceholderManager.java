package me.gaminglounge.gamingteams;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderManager extends PlaceholderExpansion {
    private Gamingteams plugin;

    private Map<Player, Integer> teams;
    private Map<Integer, String> tag;
    private Map<Integer, String> name;

    public PlaceholderManager(Gamingteams plugin) {
        this.plugin = plugin;

        teams = new HashMap<>();
        tag = new HashMap<>();
        name = new HashMap<>();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "gamingteams";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Jon1Games";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.3";
    }

    @Override
    public String getRequiredPlugin() {
        return Gamingteams.INSTANCE.getName();
    }

    // @Override
    // public boolean persist() {
    // return true; //
    // }

    @Override
    public boolean canRegister() {
        return (plugin = (Gamingteams) Bukkit.getPluginManager().getPlugin(getRequiredPlugin())) != null;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null)
            return "";
        if (!player.isOnline())
            return "";

        Player p = player.getPlayer();
        if (p == null)
            return "";

        switch (identifier.toLowerCase()) {
            case "tag":
                return getTag(p);
            case "name":
                return getName(p);
            default:
                return null;
        }
    }

    public static int getTeam(Player p) {
        if (!Gamingteams.ph.teams.containsKey(p)) {
            Gamingteams.ph.teams.put(p, DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId()));
        }
        return Gamingteams.ph.teams.get(p);
    }

    public static String getTag(Player p) {
        return getTag(getTeam(p));
    }

    public static String getName(Player p) {
        return getName(getTeam(p));
    }

    public static String getTag(int team) {
        if (!Gamingteams.ph.tag.containsKey(team)) {
            Gamingteams.ph.tag.put(team,
                    DataBasePool.getTag(Gamingteams.INSTANCE.basePool, team));
        }
        return Gamingteams.ph.tag.get(team);
    }

    public static String getName(int team) {
        if (!Gamingteams.ph.name.containsKey(team)) {
            Gamingteams.ph.name.put(team,
                    DataBasePool.getName(Gamingteams.INSTANCE.basePool, team));
        }
        return Gamingteams.ph.name.get(team);
    }

    public static void reset(int team) {
        if (Gamingteams.ph.name.containsKey(team)) {
            Gamingteams.ph.name.remove(team);
        }
        if (Gamingteams.ph.tag.containsKey(team)) {
            Gamingteams.ph.tag.remove(team);
        }
        Bukkit.getScheduler().runTask(Gamingteams.INSTANCE, () -> {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Stuff")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "Stuff:reload TeamDisplayName");
            }
        });
    }

    public static void reset(Player p) {
        if (Gamingteams.ph.teams.containsKey(p)) {
            Gamingteams.ph.teams.remove(p);
        }
    }

}
