package me.gaminglounge.gamingteams.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gaminglounge.gamingteams.PlaceholderManager;

public class AddClearPlaceholder implements Listener {

    @EventHandler
    public void onJoin(PlayerQuitEvent e) {
        PlaceholderManager.reset(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlaceholderManager.reset(e.getPlayer());
    }

}
