package me.gaminglounge.gamingteams.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import me.gaminglounge.gamingteams.DataBasePool;
import me.gaminglounge.gamingteams.Gamingteams;
import me.gaminglounge.gamingteams.gui.CreateTeam;
import me.gaminglounge.gamingteams.gui.TeamGUI;

public class TeamCommand {

	public TeamCommand() {

		Gamingteams.CONFIG.getStringList("Commands.Team.aliases").forEach(command -> CommandAPI.unregister(command));

		new CommandAPICommand("GamingTeams:team")
				.withPermission(Gamingteams.CONFIG.getString("gamingteams.commands.team"))
				.withAliases(Gamingteams.CONFIG.getStringList("Commands.Team.aliases").toArray(num -> new String[num]))
				.executesPlayer((p, args) -> {
					int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
					if (id == 0) {
						p.openInventory(
								new CreateTeam(p).getInventory());
					} else {
						p.openInventory(
								new TeamGUI(p).getInventory());
					}

				})
				.register();

	}

}
