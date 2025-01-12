package me.gaminglounge.gamingteams.commands;

import java.util.UUID;

import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.gaminglounge.gamingteams.DataBasePool;
import me.gaminglounge.gamingteams.Gamingteams;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TeamCommand {

    MiniMessage mm;

    public TeamCommand() {
        mm = MiniMessage.miniMessage();

        Gamingteams.CONFIG.getStringList("Commands.Team.aliases").forEach(action -> CommandAPI.unregister(action));

        new CommandAPICommand("GamingTeams:team")
            .withPermission(Gamingteams.CONFIG.getString("Commands.Team.permission"))
            .withAliases(Gamingteams.CONFIG.getStringList("Commands.Team.aliases").toArray(num -> new String[num]))
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.add"))
                    .withArguments(new StringArgument(Gamingteams.CONFIG.getString("Commands.Team.Arguments.name")))
                    .withArguments(new StringArgument(Gamingteams.CONFIG.getString("Commands.Team.Arguments.tag")))
                    .executesPlayer((p, args) -> {
                        UUID pID = p.getUniqueId();
                        int team = DataBasePool.getTeam(
                            Gamingteams.INSTANCE.basePool,
                            pID
                            );

                        if (team != -1) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.alreadyInTeam")));
                            return;
                        }

                        String name = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.name"));

                        if (DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, name) != -1) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.teamNameInUse")));
                            return;
                        }
                        team = DataBasePool.addTeam(
                            Gamingteams.INSTANCE.basePool,
                            pID,
                            name,
                            (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.tag"))
                            );
                        DataBasePool.addPlayerToTeam(
                            Gamingteams.INSTANCE.basePool,
                            team,
                            pID
                            );
                        p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.addTeam"),
                            Placeholder.component("name", mm.deserialize(name))
                        ));
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.remove"))
                    .executesPlayer((p, args) -> {
                        UUID pID = p.getUniqueId();
                        int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, pID);

                        if (
                            DataBasePool.removeTeam(
                                Gamingteams.INSTANCE.basePool,
                                team,
                                pID
                            )
                        ) {
                            DataBasePool.removePlayerToTeam(
                                Gamingteams.INSTANCE.basePool,
                                team,
                                pID
                                );
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.removedTeam")));
                            return;
                        }
                        p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notOwner")));
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.name"))
                    .executesPlayer((p, args) -> {
                        String name = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.name"));
                        if (
                            DataBasePool.setName(
                                Gamingteams.INSTANCE.basePool,
                                name,
                                p.getUniqueId()
                                )
                        ) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.changeName"),
                                Placeholder.component("name", mm.deserialize(name))
                            ));
                            return;
                        }
                        p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notOwner")));
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.tag"))
                    .executesPlayer((p, args) -> {
                        String tag = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.tag"));
                        if (
                            DataBasePool.setTag(
                                Gamingteams.INSTANCE.basePool,
                                tag,
                                p.getUniqueId()
                                )
                        ) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.changeTag"),
                                Placeholder.component("tag", mm.deserialize(tag))
                            ));
                            return;
                        }
                        p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notOwner")));
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.leave"))
                    .executesPlayer((p, args) -> {
                        UUID uuid = p.getUniqueId();
                        int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                        if (id == -1) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notInATeam")));
                            return;
                        }
                        DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool, id, uuid);
                        p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.leaftTeam")));
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.member"))
                    .withSubcommand(
                        new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.add"))
                            .withArguments(new EntitySelectorArgument.OnePlayer(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player")))
                            .executesPlayer((p, args) -> {
                                Player i = (Player) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player"));
                                int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
                                String name = DataBasePool.getName(Gamingteams.INSTANCE.basePool, team);
                                if (team == -1 || name == null) p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notInATeam")));
                                if (Gamingteams.INSTANCE.manager.invite(p, team)) {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.invitedPlayer"),
                                        Placeholder.component("player", i.displayName())
                                    ));
                                    i.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.invited"),
                                        Placeholder.component("name", mm.deserialize(name))
                                    ));
                                }
                            })
                        )
                    .withSubcommand(
                        new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.remove"))
                            .withArguments(new EntitySelectorArgument.OnePlayer(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player")))
                            .executesPlayer((p, args) -> {
                                Player i = (Player) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player"));
                                UUID uuid = i.getUniqueId();

                                int yourTeam = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
                                if (yourTeam == -1) p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notInATeam")));

                                int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                                if (team == -1) p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.cannotRemovePlayer")));

                                if (!DataBasePool.isOwner(Gamingteams.INSTANCE.basePool, uuid, yourTeam)) {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notOwner")));
                                    return;
                                }

                                DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool, yourTeam, uuid);

                            })
                        )
            )
        .register();
    }
    
}
