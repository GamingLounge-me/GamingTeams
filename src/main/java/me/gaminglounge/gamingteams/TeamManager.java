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

    public void invite(Player p, int team) {
        HashMap<Player, HashMap<Integer, Long>> a = new HashMap<>();
        HashMap<Integer, Long> b = new HashMap<>();
        b.put(team, System.currentTimeMillis() + (300 * 1_000));
        a.put(p, b);

        invites.add(a);
    }

    public boolean accept(Player p, int team) {
        HashMap<Player, HashMap<Integer, Long>> a = new HashMap<>();
        for (HashMap<Player, HashMap<Integer, Long>> b : invites) {
            if (b.containsValue(p) && b.get(p).containsValue(team)) {
                a = b;
                break;
            }
        }
        return a.get(p).get(team) <= System.currentTimeMillis();
    }

}
