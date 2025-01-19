package me.gaminglounge.gamingteams.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.sk89q.worldguard.WorldGuard;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.gaminglounge.ItemBuilder;
import me.gaminglounge.configapi.Language;
import me.gaminglounge.gamingteams.DataBasePool;
import me.gaminglounge.gamingteams.Gamingteams;
import me.gaminglounge.guiapi.Pagenation;
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
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "alreadyInTeam", true)));
                            return;
                        }

                        String name = (String) args.get(Gamingteams.CONFIG.getString("Commands.Team.Arguments.name"));

                        if (DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, name) != 0) {
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "teamNameInUse", true)));
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

                        if (WorldGuard.getInstance() != null) {
                            
                        }

                        p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "addTeam", true),
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
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "removedTeam", true)));
                            return;
                        }
                        p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notOwner", true)));
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
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "changeName", true),
                                Placeholder.component("name", mm.deserialize(name))
                            ));
                            return;
                        }
                        p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notOwner", true)));
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
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "changeTag", true),
                                Placeholder.component("tag", mm.deserialize(tag))
                            ));
                            return;
                        }
                        p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notOwner", true)));
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
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "changeOwner", true),
                                Placeholder.component("tag", name)
                            ));
                            return;
                        }
                        p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notOwner", true)));
                    })
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.leave"))
                    .executesPlayer((p, args) -> {
                        UUID uuid = p.getUniqueId();
                        int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                        if (id == 0) {
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notInATeam", true)));
                            return;
                        }

                        if (
                            DataBasePool.isOwner(
                                Gamingteams.INSTANCE.basePool,
                                uuid,
                                id)
                        ) {
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "ownerCannotLeave", true)));
                            return;
                        }

                        DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool, id, uuid);
                        Gamingteams.INSTANCE.manager.removeInvite(p, id);
                        p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "leaftTeam", true)));
                        List<OfflinePlayer> list = DataBasePool.getMembersOfflinePlayer(Gamingteams.INSTANCE.basePool, id);
                        list.forEach(action -> {
                            if (action.isOnline()) { 
                                ((Player) action).sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "playerLeft", true),
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
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "alreadyInTeam", true)));
                            return;
                        }
                        id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, name);
                        if (id == 0) {
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "noTeam", true)));
                            return;
                        }

                        if (Gamingteams.INSTANCE.manager.accept(p, id)) {
                            List<OfflinePlayer> list = DataBasePool.getMembersOfflinePlayer(Gamingteams.INSTANCE.basePool, id);
                            list.forEach(action -> {
                                if (action.isOnline()) { 
                                    ((Player) action).sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "playerJoined", true),
                                        Placeholder.component("player", p.displayName())
                                    ));
                                }
                            });
                        } else {
                            p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "noInvite", true)));
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
                                    p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notInATeam", true)));
                                    return;
                                }

                                if (
                                    !DataBasePool.isOwner(
                                        Gamingteams.INSTANCE.basePool,
                                        uuid,
                                        team)
                                ) {
                                    p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notOwner", true)));
                                    return;                                    
                                }

                                if (Gamingteams.INSTANCE.manager.invite(i, team)) {
                                    String name = DataBasePool.getName(Gamingteams.INSTANCE.basePool, team);
                                    p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "invitedPlayer", true),
                                        Placeholder.component("player", i.displayName())
                                    ));
                                    i.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "invited", true),
                                        Placeholder.component("name", mm.deserialize(name))
                                    ));
                                } else {
                                    p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "alreadyInvited", true)));
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
                                    p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "removeSelf", true)));
                                    return;
                                }

                                int yourTeam = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
                                if (yourTeam == 0) p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notInATeam", true)));

                                int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);
                                if (team == 0) p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "cannotRemovePlayer", true)));

                                if (team != yourTeam) {
                                    p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "nowInYourTeam", true)));
                                    return;
                                }

                                if (!DataBasePool.isOwner(Gamingteams.INSTANCE.basePool, p.getUniqueId(), yourTeam)) {
                                    p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "notOwner", true)));
                                    return;
                                }

                                DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool, yourTeam, uuid);
                                Component name;
                                if (i.isOnline()) {
                                    Gamingteams.INSTANCE.manager.removeInvite((Player) i, team);
                                    name = ((Player) i).displayName();
                                    ((Player) i).sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "playerRemoved", true),
                                        Placeholder.component("player", name)
                                    ));
                                } else {
                                    name = net.kyori.adventure.text.Component.text(i.getName());
                                }

                                List<OfflinePlayer> list = DataBasePool.getMembersOfflinePlayer(Gamingteams.INSTANCE.basePool, team);
                                list.forEach(action -> {
                                    if (action.isOnline()) { 
                                        ((Player) action).sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "playerRemoved", true),
                                            Placeholder.component("player", name)
                                        ));
                                    }
                                });

                            })
                        )
                    .withSubcommand(
                        new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.info"))
                            .executesPlayer((p, args) -> {
                                int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
                                String name = DataBasePool.getName(Gamingteams.INSTANCE.basePool, id);
                                String tag = DataBasePool.getTag(Gamingteams.INSTANCE.basePool, id);
                                OfflinePlayer owner = DataBasePool.getOwner(Gamingteams.INSTANCE.basePool, id);

                                p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "info.name", true),
                                    Placeholder.component("name", mm.deserialize(name))
                                ));
                                p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "info.tag", true),
                                    Placeholder.component("tag", mm.deserialize(tag))
                                ));
                                p.sendMessage(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "info.owner", true),
                                    Placeholder.component("owner", mm.deserialize(owner.getName()))
                                ));                          

                            })
                    )
            )
            .withSubcommand(
                new CommandAPICommand(Gamingteams.CONFIG.getString("Commands.Team.SubCommands.list"))
                    .executesPlayer((p, args) -> {
                        int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
                        List<UUID> list = DataBasePool.getMembersUUIDs(Gamingteams.INSTANCE.basePool, id);
                        List<ItemStack> items = new ArrayList<>();

                        for (UUID a : list) {
                            PlayerProfile prof = Bukkit.getOfflinePlayer(a).getPlayerProfile();
                            if (!prof.completeFromCache()) {
                                prof.complete();
                            }

                            items.add(
                                new ItemBuilder()
                                    .setSkull(a)
                                    .setName(Component.text(prof.getName()))
                                    .build()
                            );   
                        }

                        Inventory inv = new Pagenation()
                            .getInventory();

                        p.openInventory(inv);

                    })
            )
        .register();
    }
    
}