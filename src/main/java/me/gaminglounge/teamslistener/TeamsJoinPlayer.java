package me.gaminglounge.teamslistener;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamsJoinPlayer extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Integer teamID;
    private final UUID member;

    public TeamsJoinPlayer(Integer teamID, UUID member) {
        this.teamID = teamID;
        this.member = member;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public UUID joinedMember() {
        return member;
    }
/**
 * @deprecated This was wrongfully named removed, use <code>joinedMember()</code> instead.
 */
@Deprecated(forRemoval = true)
    public UUID removedMember() {
        return joinedMember();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
