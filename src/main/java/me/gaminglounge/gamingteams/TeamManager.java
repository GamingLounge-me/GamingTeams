package me.gaminglounge.gamingteams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

public class TeamManager {

    // Player = Invited Player, integer = Team ID, Long = Timeout Invite 
    private List<HashMap<Player, HashMap<Integer, Long>>>  invites;

    public TeamManager() {
        invites = new ArrayList<>();
    }

    public boolean invite(Player p, int team) {
        if (hasInvite(p, team)) return false;
        HashMap<Player, HashMap<Integer, Long>> a = new HashMap<>();
        HashMap<Integer, Long> b = new HashMap<>();
        b.put(team, System.currentTimeMillis() + (300 * 1_000));
        a.put(p, b);

        invites.add(a);
        return true;
    }

    public boolean accept(Player p, int team) {
        for (HashMap<Player, HashMap<Integer, Long>> b : invites) {
            if (
                b.containsKey(p) && b.get(p).containsKey(team) &&
                b.get(p).get(team) >= System.currentTimeMillis()
            ) {
                DataBasePool.addPlayerToTeam(Gamingteams.INSTANCE.basePool, team, p.getUniqueId());
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean hasInvite(Player p, int team) {
        for (HashMap<Player, HashMap<Integer, Long>> b : invites) {
            if (b.containsKey(p) && b.get(p).containsKey(team)) return true;
        }
        return false;
    }

    public void removeInvite(Player p, int team) {
        for (HashMap<Player, HashMap<Integer, Long>> b : invites) {
            if (
                b.containsKey(p) && b.get(p).containsKey(team)
            ) {
                b.remove(p);
            }
        }     
    }

}
