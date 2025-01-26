package me.gaminglounge.gamingteams.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import me.gaminglounge.gamingteams.DataBasePool;
import me.gaminglounge.gamingteams.Gamingteams;
import me.gaminglounge.gamingteams.gui.CreateTeam;
import me.gaminglounge.gamingteams.gui.TeamGUI;

public class TeamCommand {

	public TeamCommand() {

		Gamingteams.CONFIG.getStringList("Commands.Team.aliases").forEach(action -> CommandAPI.unregister(action));

		new CommandAPICommand("GamingTeams:team")
				.withPermission(Gamingteams.CONFIG.getString("Commands.Team.permission"))
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

/*
 * 
 * .withSubcommand(
 * new CommandAPICommand(Gamingteams.CONFIG.getString(
 * "Commands.Team.SubCommands.join"))
 * .withArguments(new StringArgument(
 * Gamingteams.CONFIG.getString("Commands.Team.Arguments.teams")))
 * .executesPlayer((p, args) -> {
 * String name = (String) args
 * .get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.teams"));
 * UUID uuid = p.getUniqueId();
 * int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
 * if (id != 0) {
 * p.sendMessage(mm.deserialize(
 * Language.getValue(Gamingteams.INSTANCE, p, "alreadyInTeam", true)));
 * return;
 * }
 * id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, name);
 * if (id == 0) {
 * p.sendMessage(mm.deserialize(
 * Language.getValue(Gamingteams.INSTANCE, p, "noTeam", true)));
 * return;
 * }
 * 
 * 
 * 
 * }))
 * 
 * .withSubcommand(
 * new CommandAPICommand(Gamingteams.CONFIG.getString(
 * "Commands.Team.SubCommands.member"))
 * .withSubcommand(
 * new CommandAPICommand(
 * Gamingteams.CONFIG.getString("Commands.Team.SubCommands.add"))
 * .withArguments(new EntitySelectorArgument.OnePlayer(
 * Gamingteams.CONFIG.getString("Commands.Team.Arguments.player")))
 * .executesPlayer((p, args) -> {
 * Player i = (Player) args.get(Gamingteams.CONFIG
 * .getString("Commands.Team.Arguments.player"));
 * 
 * if (p == i) {
 * p.sendMessage(mm.deserialize(
 * Gamingteams.CONFIG.getString("Messages.inviteSelf")));
 * return;
 * }
 * 
 * UUID uuid = p.getUniqueId();
 * int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool,
 * uuid);
 * if (team == 0) {
 * p.sendMessage(mm.deserialize(Language.getValue(
 * Gamingteams.INSTANCE, p, "notInATeam", true)));
 * return;
 * }
 * 
 * if (!DataBasePool.isOwner(
 * Gamingteams.INSTANCE.basePool,
 * uuid,
 * team)) {
 * p.sendMessage(mm.deserialize(Language
 * .getValue(Gamingteams.INSTANCE, p, "notOwner", true)));
 * return;
 * }
 * 
 * if (Gamingteams.INSTANCE.manager.invite(i, team)) {
 * String name = DataBasePool
 * .getName(Gamingteams.INSTANCE.basePool, team);
 * p.sendMessage(mm.deserialize(
 * Language.getValue(Gamingteams.INSTANCE, p,
 * "invitedPlayer", true),
 * Placeholder.component("player", i.displayName())));
 * i.sendMessage(mm.deserialize(
 * Language.getValue(Gamingteams.INSTANCE, p, "invited",
 * true),
 * Placeholder.component("name", mm.deserialize(name))));
 * } else {
 * p.sendMessage(mm.deserialize(Language.getValue(
 * Gamingteams.INSTANCE, p, "alreadyInvited", true)));
 * }
 * }))
 * .withSubcommand(
 * new CommandAPICommand(
 * Gamingteams.CONFIG.getString("Commands.Team.SubCommands.remove"))
 * .withArguments(new OfflinePlayerArgument(
 * Gamingteams.CONFIG.getString("Commands.Team.Arguments.player")))
 * .executesPlayer((p, args) -> {
 * OfflinePlayer i = (OfflinePlayer) args.get(Gamingteams.CONFIG
 * .getString("Commands.Team.Arguments.player"));
 * UUID uuid = i.getUniqueId();
 * 
 * if (p == i) {
 * p.sendMessage(mm.deserialize(Language.getValue(
 * Gamingteams.INSTANCE, p, "removeSelf", true)));
 * return;
 * }
 * 
 * int yourTeam = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool,
 * p.getUniqueId());
 * if (yourTeam == 0)
 * p.sendMessage(mm.deserialize(Language.getValue(
 * Gamingteams.INSTANCE, p, "notInATeam", true)));
 * 
 * int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool,
 * uuid);
 * if (team == 0)
 * p.sendMessage(mm.deserialize(Language.getValue(
 * Gamingteams.INSTANCE, p, "cannotRemovePlayer", true)));
 * 
 * if (team != yourTeam) {
 * p.sendMessage(mm.deserialize(Language.getValue(
 * Gamingteams.INSTANCE, p, "nowInYourTeam", true)));
 * return;
 * }
 * 
 * if (!DataBasePool.isOwner(Gamingteams.INSTANCE.basePool,
 * p.getUniqueId(), yourTeam)) {
 * p.sendMessage(mm.deserialize(Language
 * .getValue(Gamingteams.INSTANCE, p, "notOwner", true)));
 * return;
 * }
 * 
 * DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool,
 * yourTeam, uuid);
 * Component name;
 * if (i.isOnline()) {
 * Gamingteams.INSTANCE.manager.removeInvite((Player) i, team);
 * name = ((Player) i).displayName();
 * ((Player) i).sendMessage(mm.deserialize(
 * Language.getValue(Gamingteams.INSTANCE, p,
 * "playerRemoved", true),
 * Placeholder.component("player", name)));
 * } else {
 * name = net.kyori.adventure.text.Component.text(i.getName());
 * }
 * 
 * List<OfflinePlayer> list = DataBasePool.getMembersOfflinePlayer(
 * Gamingteams.INSTANCE.basePool, team);
 * list.forEach(action -> {
 * if (action.isOnline()) {
 * ((Player) action).sendMessage(mm.deserialize(
 * Language.getValue(Gamingteams.INSTANCE, p,
 * "playerRemoved", true),
 * Placeholder.component("player", name)));
 * }
 * });
 * 
 * }))
 * 
 */
