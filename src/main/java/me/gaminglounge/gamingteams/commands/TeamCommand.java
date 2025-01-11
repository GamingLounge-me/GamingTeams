package me.gaminglounge.gamingteams.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import me.gaminglounge.gamingteams.Gamingteams;

public class TeamCommand {

    public TeamCommand() {
        Gamingteams.CONFIG.getStringList("Commands.Team.aliases").forEach(action -> CommandAPI.unregister(action));

        new CommandAPICommand("GamingTeams:team")
            .withPermission(Gamingteams.CONFIG.getString("Commands.Team.permission"))
            .withAliases(Gamingteams.CONFIG.getStringList("Commands.Team.aliases").toArray(num -> new String[num]))
        .register();

    }
    
}
