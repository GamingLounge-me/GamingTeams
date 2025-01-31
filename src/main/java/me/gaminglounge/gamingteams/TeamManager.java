package me.gaminglounge.gamingteams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.gaminglounge.teamslistener.TeamsJoinPlayer;

public class TeamManager {

    // Player = Invited Player, integer = Team ID, Long = Timeout Invite
    private HashMap<Player, HashMap<Integer, Long>> invites;

    public TeamManager() {
        invites = new HashMap<>();
    }

    public boolean invite(Player p, int team) {
        if (hasInvite(p, team))
            return false;
        HashMap<Integer, Long> b = new HashMap<>();
        b.put(team, System.currentTimeMillis() + (300 * 1_000));
        invites.put(p, b);

        return true;
    }

    public boolean accept(Player p, int team) {
        if (invites.containsKey(p) && invites.get(p).containsKey(team) &&
                invites.get(p).get(team) >= System.currentTimeMillis()) {
            DataBasePool.addPlayerToTeam(Gamingteams.INSTANCE.basePool, team, p.getUniqueId());
            Bukkit.getServer().getPluginManager().callEvent(new TeamsJoinPlayer(team, p.getUniqueId()));
            return true;
        } else
            return false;
    }

    public boolean hasInvite(Player p, int team) {
        return invites.containsKey(p) && invites.get(p).containsKey(team);
    }

    public void removeInvite(Player p, int team) {
        if (invites.containsKey(p) && invites.get(p).containsKey(team)) {
            invites.get(p).remove(team);
        }
    }

    public List<Integer> listInvites(Player p) {
        List<Integer> a = new ArrayList<>();

        if (invites.containsKey(p)) {
            invites.get(p).forEach((integer, aLong) -> {
                a.add(integer);
            });
        }

        return a;
    }

}
