package me.gaminglounge.gamingteams.commands;

import dev.jorel.commandapi.CommandAPICommand;
import me.gaminglounge.gamingteams.Gamingteams;

public class TeamCommand {

    public TeamCommand() {

        new CommandAPICommand("GamingTeams:team")
            .withPermission(Gamingteams.CONFIG.getString("Commands.Team.permission"))
            .withAliases(Gamingteams.CONFIG.getStringList("BroadcastCommand.Aliases").toArray(num -> new String[num]))
        .register();

    }
    
}
