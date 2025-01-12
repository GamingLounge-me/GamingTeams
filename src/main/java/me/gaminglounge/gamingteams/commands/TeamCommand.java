package me.gaminglounge.gamingteams.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.gaminglounge.gamingteams.DataBasePool;
import me.gaminglounge.gamingteams.Gamingteams;
import net.kyori.adventure.text.Component;
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

                        if (team != 0) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.alreadyInTeam")));
                            return;
                        }

                        String name = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.name"));

                        if (DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, name) != 0) {
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
                            DataBasePool.isOwner(
                                Gamingteams.INSTANCE.basePool,
                                pID,
                                team)
                        ) {
                            DataBasePool.removeTeam(
                                Gamingteams.INSTANCE.basePool,
                                team,
                                pID
                            );
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
                    .withArguments(new StringArgument(Gamingteams.CONFIG.getString("Commands.Team.Arguments.name")))
                    .executesPlayer((p, args) -> {
                        String name = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.name"));
                        UUID uuid = p.getUniqueId();
                        int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                        if (
                            DataBasePool.isOwner(Gamingteams.INSTANCE.basePool, uuid, team)
                        ) {
                            DataBasePool.setName(
                                Gamingteams.INSTANCE.basePool,
                                name,
                                uuid
                                );
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
                    .withArguments(new StringArgument(Gamingteams.CONFIG.getString("Commands.Team.Arguments.tag")))
                    .executesPlayer((p, args) -> {
                        String tag = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.tag"));
                        UUID uuid = p.getUniqueId();
                        int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                        if (
                            DataBasePool.isOwner(Gamingteams.INSTANCE.basePool, uuid, team)
                        ) {
                            DataBasePool.setTag(
                                Gamingteams.INSTANCE.basePool,
                                tag,
                                p.getUniqueId()
                                );
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.changeTag"),
                                Placeholder.component("tag", mm.deserialize(tag))
                            ));
                            return;
                        }
                        p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notOwner")));
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.owner"))
                    .withArguments(new OfflinePlayerArgument(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player")))
                    .executesPlayer((p, args) -> {
                        OfflinePlayer owner = (OfflinePlayer) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player"));
                        net.kyori.adventure.text.Component name;
                        UUID uuid = p.getUniqueId();
                        int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                        if (
                            DataBasePool.isOwner(Gamingteams.INSTANCE.basePool, uuid, team)
                        ) {
                            DataBasePool.setOwner(
                                Gamingteams.INSTANCE.basePool,
                                p.getUniqueId(),
                                owner.getUniqueId()
                                );
                            if (owner.isOnline()) {
                                name = ((Player) owner).displayName();
                            } else {
                                name = net.kyori.adventure.text.Component.text(owner.getName());
                            }
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.changeOwner"),
                                Placeholder.component("tag", name)
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
                        if (id == 0) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notInATeam")));
                            return;
                        }

                        if (
                            DataBasePool.isOwner(
                                Gamingteams.INSTANCE.basePool,
                                uuid,
                                id)
                        ) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.ownerCannotLeave")));
                            return;
                        }

                        DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool, id, uuid);
                        Gamingteams.INSTANCE.manager.removeInvite(p, id);
                        p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.leaftTeam")));
                        List<OfflinePlayer> list = DataBasePool.getMembersOfflinePlayer(Gamingteams.INSTANCE.basePool, id);
                        list.forEach(action -> {
                            if (action.isOnline()) { 
                                ((Player) action).sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.playerLeft"),
                                    Placeholder.component("player", p.displayName())
                                ));
                            }
                        });
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.join"))
                    .withArguments(new StringArgument(Gamingteams.CONFIG.getString("Commands.Team.Arguments.teams"))) 
                    .executesPlayer((p, args) -> {
                        String name = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.teams"));
                        UUID uuid = p.getUniqueId();
                        int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                        if (id != 0) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.alreadyInTeam")));
                            return;
                        }
                        id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, name);
                        if (id == 0) {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.noTeam")));
                            return;
                        }

                        if (Gamingteams.INSTANCE.manager.accept(p, id)) {
                            List<OfflinePlayer> list = DataBasePool.getMembersOfflinePlayer(Gamingteams.INSTANCE.basePool, id);
                            list.forEach(action -> {
                                if (action.isOnline()) { 
                                    ((Player) action).sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.playerJoined"),
                                        Placeholder.component("player", p.displayName())
                                    ));
                                }
                            });
                        } else {
                            p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.noInvite")));
                        }
                        
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.member"))
                    .withSubcommand(
                        new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.add"))
                            .withArguments(new EntitySelectorArgument.OnePlayer(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player")))
                            .executesPlayer((p, args) -> {
                                Player i = (Player) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player"));
                                
                                if (p == i) {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.inviteSelf")));
                                    return;
                                }

                                UUID uuid = p.getUniqueId();
                                int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                                if (team == 0) {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notInATeam")));
                                    return;
                                }

                                if (
                                    !DataBasePool.isOwner(
                                        Gamingteams.INSTANCE.basePool,
                                        uuid,
                                        team)
                                ) {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notOwner")));
                                    return;                                    
                                }

                                if (Gamingteams.INSTANCE.manager.invite(i, team)) {
                                    String name = DataBasePool.getName(Gamingteams.INSTANCE.basePool, team);
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.invitedPlayer"),
                                        Placeholder.component("player", i.displayName())
                                    ));
                                    i.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.invited"),
                                        Placeholder.component("name", mm.deserialize(name))
                                    ));
                                } else {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.alreadyInvited")));
                                }
                            })
                        )
                    .withSubcommand(
                        new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.remove"))
                            .withArguments(new OfflinePlayerArgument(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player")))
                            .executesPlayer((p, args) -> {
                                OfflinePlayer i = (OfflinePlayer) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.player"));
                                UUID uuid = i.getUniqueId();

                                if (p == i) {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.removeSelf")));
                                    return;
                                }

                                int yourTeam = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
                                if (yourTeam == 0) p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notInATeam")));

                                int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                                if (team == 0) p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.cannotRemovePlayer")));

                                if (team != yourTeam) {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.nowInYourTeam")));
                                    return;
                                }

                                if (!DataBasePool.isOwner(Gamingteams.INSTANCE.basePool, p.getUniqueId(), yourTeam)) {
                                    p.sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.notOwner")));
                                    return;
                                }

                                DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool, yourTeam, uuid);
                                Component name;
                                if (i.isOnline()) {
                                    Gamingteams.INSTANCE.manager.removeInvite((Player) i, team);
                                    name = ((Player) i).displayName();
                                    ((Player) i).sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.playerRemoved"),
                                        Placeholder.component("player", name)
                                    ));
                                } else {
                                    name = net.kyori.adventure.text.Component.text(i.getName());
                                }

                                List<OfflinePlayer> list = DataBasePool.getMembersOfflinePlayer(Gamingteams.INSTANCE.basePool, team);
                                list.forEach(action -> {
                                    if (action.isOnline()) { 
                                        ((Player) action).sendMessage(mm.deserialize(Gamingteams.CONFIG.getString("Messages.playerRemoved"),
                                            Placeholder.component("player", name)
                                        ));
                                    }
                                });

                            })
                        )
            )
        .register();
    }
    
}
