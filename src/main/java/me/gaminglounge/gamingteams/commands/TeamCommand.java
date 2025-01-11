package me.gaminglounge.gamingteams.commands;

import java.util.UUID;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
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
                        int team = DataBasePool.getPlayerTeam(
                            Gamingteams.INSTANCE.basePool,
                            pID
                            );

                        if (team == -1) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.alreadyInTeam")));
                            return;
                        }

                        String name = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.name"));
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
                        int team = DataBasePool.getPlayerTeam(Gamingteams.INSTANCE.basePool, pID);

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
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.member"))
                    .withSubcommand(
                        new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.add"))
                        
                        )
                        .withSubcommand(
                            new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.remove"))
                            
                            )
            )
        .register();
    }
    
}
