package me.gaminglounge.gamingteams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.gaminglounge.teamslistener.TeamsJoinPlayer;

public class TeamManager {

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
        if (isInviteValid(p, team)) {
            DataBasePool.addPlayerToTeam(Gamingteams.INSTANCE.basePool, team, p.getUniqueId());
            Gamingteams.INSTANCE.getLogger().log(Level.ALL, p.getName() + " joined the team with the id " + team);
            Bukkit.getServer().getPluginManager().callEvent(new TeamsJoinPlayer(team, p.getUniqueId()));
            return true;
        } else
            return false;
    }

    /**
     * @param player The player which has or will get the invite
     * @param team   the team which the invite is coming from
     * @return if the player has an invite from this team
     */
    public boolean hasInvite(Player player, int team) {
        return invites.containsKey(player) && invites.get(player).containsKey(team);
    }

    /**
     * This method also uses {@code hasInvite()} so you dont need to also check
     * this.
     * 
     * @param player The player which has or will get the invite
     * @param team   the team which the invite is coming from
     * @return if the invite is still valid
     */
    public boolean isInviteValid(Player player, int team) {
        if (hasInvite(player, team)) {
            if (invites.get(player).get(team) < System.currentTimeMillis()) {
                removeInvite(player, team);
                return false;
            } else
                return true;
        } else
            return false;
    }

    /**
     * Removes the invite a player got from an team.
     * 
     * @param player The invited player.
     * @param team   The team that invited the player.
     * @return if the removal was succefully.
     */
    public boolean removeInvite(Player player, int team) {
        if (hasInvite(player, team)) {
            invites.get(player).remove(team);
            return true;
        } else
            return false;
    }

    /**
     * Note that this list isn't filtered by {@code isInviteValid()} so also invites
     * that are expired but not cleared from the invite list are displayed.
     * 
     * @param player
     * @return All invted the player has.
     */
    public List<Integer> listInvites(Player player) {
        List<Integer> a = new ArrayList<>();

        if (invites.containsKey(player)) {
            invites.get(player).forEach((integer, aLong) -> {
                a.add(integer);
            });
        }

        return a;
    }

}
