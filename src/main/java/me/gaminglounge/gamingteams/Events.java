package me.gaminglounge.gamingteams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.destroystokyo.paper.profile.PlayerProfile;

import me.gaminglounge.configapi.Language;
import me.gaminglounge.gamingteams.gui.PlayerManagement;
import me.gaminglounge.gamingteams.gui.TeamGUI;
import me.gaminglounge.guiapi.ErrorGUI;
import me.gaminglounge.guiapi.Pagenation;
import me.gaminglounge.guiapi.SubmitPromt;
import me.gaminglounge.itembuilder.ItemBuilder;
import me.gaminglounge.itembuilder.ItemBuilderManager;
import me.gaminglounge.playerinputapi.UseNextChatInput;
import me.gaminglounge.teamslistener.TeamsLeftPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Events {
    private static MiniMessage mm;

    private static final NamespacedKey joinID = new NamespacedKey("gamingteams", "join_team_id");

    public Events() {
        mm = MiniMessage.miniMessage();

        ItemBuilderManager.addBothClickEvent("GamingTeams:leave", (e) -> {
            e.setCancelled(true);
            if (e.getInventory().getHolder() instanceof TeamGUI tg) {
                Player p = (Player) e.getWhoClicked();
                p.closeInventory();
                UUID uuid = p.getUniqueId();
                int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, uuid);

                if (isOwner(p, id)) {
                    p.openInventory(
                            new ErrorGUI(tg.getInventory(), p,
                                    mm.deserialize(Language.getValue(
                                            Gamingteams.INSTANCE, p,
                                            "ownerCannotLeave")))
                                    .getInventory());
                    return;
                }

                DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool, id, uuid);
                Bukkit.getServer().getPluginManager().callEvent(new TeamsLeftPlayer(id, uuid));
                PlaceholderManager.reset(p);
                Gamingteams.INSTANCE.manager.removeInvite(p, id);
                p.sendMessage(mm.deserialize(
                        Language.getValue(Gamingteams.INSTANCE, p, "leaftTeam", true)));
                List<OfflinePlayer> list = DataBasePool
                        .getMembersOfflinePlayer(Gamingteams.INSTANCE.basePool, id);
                list.forEach(action -> {
                    if (action.isOnline()) {
                        ((Player) action).sendMessage(mm.deserialize(
                                Language.getValue(Gamingteams.INSTANCE, (Player) action,
                                        "playerLeft",
                                        true),
                                Placeholder.component("player", p.displayName())));
                    }
                });

            }
        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:create_team", (e) -> {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            UUID uuid = p.getUniqueId();
            int team = DataBasePool.addTeam(
                    Gamingteams.INSTANCE.basePool,
                    uuid,
                    uuid.toString(),
                    "none");
            DataBasePool.addPlayerToTeam(
                    Gamingteams.INSTANCE.basePool,
                    team,
                    uuid);

            p.openInventory(
                    new TeamGUI(p).getInventory());

        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:remove_team", (e) -> {
            e.setCancelled(true);
            if (e.getInventory().getHolder() instanceof TeamGUI tg) {
                if (isOwner(tg.p, tg.teamID)) {
                    Player p = (Player) e.getWhoClicked();

                    p.openInventory(
                            new SubmitPromt(e.getInventory(), p, (event) -> {
                                DataBasePool.removeTeam(
                                        Gamingteams.INSTANCE.basePool,
                                        tg.teamID,
                                        p.getUniqueId());
                                DataBasePool.removePlayerToTeam(
                                        Gamingteams.INSTANCE.basePool,
                                        tg.teamID,
                                        p.getUniqueId());
                                p.sendMessage(mm.deserialize(
                                        Language.getValue(Gamingteams.INSTANCE,
                                                tg.p, "removedTeam",
                                                true)));
                            }).getInventory());
                    return;
                }

                Player p = (Player) e.getWhoClicked();
                p.openInventory(
                        new ErrorGUI(e.getInventory(), p,
                                mm.deserialize(Language.getValue(Gamingteams.INSTANCE,
                                        p, "notOwner")))
                                .getInventory());

            }
        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:change_name", (e) -> {
            e.setCancelled(true);
            Inventory inv = e.getInventory();
            if (inv.getHolder() instanceof TeamGUI tg) {
                Player p = (Player) e.getWhoClicked();
                UUID uuid = p.getUniqueId();

                if (!DataBasePool.isOwner(Gamingteams.INSTANCE.basePool, uuid,
                        tg.teamID)) {
                    p.openInventory(
                            new ErrorGUI(inv, p, mm.deserialize(
                                    Language.getValue(
                                            Gamingteams.INSTANCE,
                                            p, "notOwner",
                                            true)))
                                    .getInventory());
                    return;
                }

                p.closeInventory();
                String exit = Language.getValue(Gamingteams.INSTANCE, p, "chatinput.exit");
                new UseNextChatInput(p)
                        .sendMessage(mm.deserialize(
                                Language.getValue(Gamingteams.INSTANCE, p,
                                        "chatinput.changename.question", true),
                                Placeholder.component("exit", Component.text(exit))))
                        .setChatEvent((event, name) -> {
                            if (name.equalsIgnoreCase(exit)) {
                                p.sendMessage(
                                        mm.deserialize(Language.getValue(
                                                Gamingteams.INSTANCE, p,
                                                "chatinput.cancel")));
                                return;
                            }

                            Pattern ptm = Pattern.compile("[a-zA-Z0-9_ #:</>]{1,64}");
                            if (!ptm.matcher(LegacyComponentSerializer.legacySection().serialize(mm.deserialize(name)))
                                    .matches()) {
                                p.sendMessage(
                                        mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "regex.error"),
                                                Placeholder.component("regex", Component.text(ptm.toString()))));
                                return;
                            }

                            PlaceholderManager.reset(tg.teamID);

                            DataBasePool.setName(
                                    Gamingteams.INSTANCE.basePool,
                                    name,
                                    uuid);
                            Bukkit.getScheduler().runTask(Gamingteams.INSTANCE,
                                    () -> {
                                        p.openInventory(new TeamGUI(p)
                                                .getInventory());
                                    });
                        })
                        .capture();
            }
        });
        ItemBuilderManager.addBothClickEvent("GamingTeams:change_tag", (e) -> {
            e.setCancelled(true);
            Inventory inv = e.getInventory();
            if (inv.getHolder() instanceof TeamGUI tg) {
                Player p = (Player) e.getWhoClicked();
                UUID uuid = p.getUniqueId();

                if (!DataBasePool.isOwner(Gamingteams.INSTANCE.basePool, uuid,
                        tg.teamID)) {
                    p.openInventory(
                            new ErrorGUI(inv, p, mm.deserialize(
                                    Language.getValue(
                                            Gamingteams.INSTANCE,
                                            p, "notOwner",
                                            true)))
                                    .getInventory());
                    return;
                }

                p.closeInventory();
                String exit = Language.getValue(Gamingteams.INSTANCE, p, "chatinput.exit");
                new UseNextChatInput(p)
                        .sendMessage(mm.deserialize(
                                Language.getValue(Gamingteams.INSTANCE, p,
                                        "chatinput.changename.question", true),
                                Placeholder.component("exit", Component.text(exit))))
                        .setChatEvent((event, name) -> {
                            if (name.equalsIgnoreCase(exit)) {
                                p.sendMessage(
                                        mm.deserialize(Language.getValue(
                                                Gamingteams.INSTANCE, p,
                                                "chatinput.cancel")));
                                return;
                            }

                            Pattern ptm = Pattern.compile("[a-zA-Z0-9_ #:</>]{1,5}");
                            if (!ptm.matcher(LegacyComponentSerializer.legacySection().serialize(mm.deserialize(name)))
                                    .matches()) {
                                p.sendMessage(
                                        mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p, "regex.error"),
                                                Placeholder.component("regex", Component.text(ptm.toString()))));
                                return;
                            }

                            PlaceholderManager.reset(tg.teamID);

                            DataBasePool.setTag(
                                    Gamingteams.INSTANCE.basePool,
                                    name,
                                    uuid);
                            Bukkit.getScheduler().runTask(Gamingteams.INSTANCE,
                                    () -> {
                                        p.openInventory(new TeamGUI(p)
                                                .getInventory());
                                    });
                        })
                        .capture();
            }

        });
        ItemBuilderManager.addBothClickEvent("GamingTeams:change_owner", (e) -> {
            e.setCancelled(true);
            if (e.getInventory().getHolder() instanceof PlayerManagement pm) {
                Player p = (Player) e.getWhoClicked();

                p.openInventory(
                        new SubmitPromt(e.getInventory(), p, (event) -> {
                            DataBasePool.setOwner(
                                    Gamingteams.INSTANCE.basePool,
                                    pm.target.getUniqueId(),
                                    p.getUniqueId());
                        }).getInventory());

            }

        });
        ItemBuilderManager.addBothClickEvent("GamingTeams:open_player_list", (e) -> {
            e.setCancelled(true);
            if (e.getInventory().getHolder() instanceof TeamGUI tg) {
                List<UUID> list = DataBasePool.getMembersUUIDs(Gamingteams.INSTANCE.basePool,
                        tg.teamID);
                List<ItemStack> items = new ArrayList<>();

                for (UUID a : list) {
                    PlayerProfile prof = Bukkit.getOfflinePlayer(a).getPlayerProfile();
                    if (!prof.completeFromCache()) {
                        prof.complete();
                    }

                    items.add(
                            new ItemBuilder(a)
                                    .setName(Component.text(prof.getName()))
                                    .addBothClickEvent(
                                            "GamingTeams:open_player_management")
                                    .build());
                }

                Inventory tmp = new Pagenation(tg.p)
                        .setBackInv(tg.inv)
                        .setItems(items)
                        .getInventory();

                tmp.setItem(
                        0,
                        new ItemBuilder(Material.SKELETON_SKULL)
                                .setName(mm
                                        .deserialize(Language.getValue(
                                                Gamingteams.INSTANCE,
                                                (Player) e.getWhoClicked(),
                                                "InvitePlayer")))
                                .addBothClickEvent("GamingTeams:invite_player")
                                .build());

                tg.p.openInventory(tmp);
            }

        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:invite_player", (e) -> {
            e.setCancelled(true);

            List<ItemStack> items = new ArrayList<>();

            Bukkit.getOnlinePlayers().forEach(action -> {
                items.add(
                        new ItemBuilder(action.getUniqueId())
                                .setName(action.displayName())
                                .addBothClickEvent("GamingTeams:invite")
                                .build());
            });

            e.getWhoClicked().openInventory(
                    new Pagenation((Player) e.getWhoClicked())
                            .setBackInv(e.getInventory())
                            .setItems(items)
                            .getInventory());

        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:invite", (e) -> {
            e.setCancelled(true);
            int team = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, e.getWhoClicked().getUniqueId());

            SkullMeta skull = (SkullMeta) e.getCurrentItem().getItemMeta();
            OfflinePlayer p = skull.getOwningPlayer();
            if (p.isOnline()) {
                if (!Gamingteams.INSTANCE.manager.invite((Player) p, team)) {
                    e.getWhoClicked().openInventory(
                            new ErrorGUI(e.getInventory(), (Player) e.getWhoClicked(),
                                    mm.deserialize(
                                            Language.getValue(
                                                    Gamingteams.INSTANCE,
                                                    (Player) e.getWhoClicked(),
                                                    "alreadyHasInvite"),
                                            Placeholder.component("player",
                                                    ((Player) p).displayName())))
                                    .getInventory());
                    return;
                }
                ((Player) p).sendMessage(
                        mm.deserialize(Language.getValue(Gamingteams.INSTANCE, (Player) p,
                                "gotTeamInvite", true)));
                if (e.getInventory().getHolder() instanceof Pagenation pg) {
                    pg.removeItem(e.getCurrentItem());
                    pg.refeshPage();
                }
                e.getWhoClicked().openInventory(
                        new ErrorGUI(e.getInventory(), (Player) e.getWhoClicked(),
                                mm.deserialize(
                                        Language.getValue(Gamingteams.INSTANCE,
                                                (Player) e.getWhoClicked(),
                                                "invitedPlayer"),
                                        Placeholder.component("player",
                                                ((Player) p).displayName())))
                                .getInventory());
            }
        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:list_invites", (e) -> {
            e.setCancelled(true);
            List<Integer> invites = Gamingteams.INSTANCE.manager.listInvites((Player) e.getWhoClicked());
            List<ItemStack> items = new ArrayList<>();

            for (int id : invites) {
                ItemStack tmp = new ItemBuilder(Material.GREEN_BANNER)
                        .setName(mm.deserialize(DataBasePool
                                .getName(Gamingteams.INSTANCE.basePool, id)))
                        .addBothClickEvent("GamingTeams:join_team")
                        .build();

                ItemMeta meta = tmp.getItemMeta();
                meta.getPersistentDataContainer().set(joinID, PersistentDataType.INTEGER, id);
                tmp.setItemMeta(meta);

                items.add(tmp);
            }

            Player p = (Player) e.getWhoClicked();
            p.openInventory(
                    new Pagenation(p)
                            .setBackInv(e.getInventory())
                            .setItems(items)
                            .getInventory());

        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:join_team", (e) -> {
            if (e.getInventory().getHolder() instanceof Pagenation pg) {
                e.setCancelled(true);
                PersistentDataContainer container = e.getCurrentItem().getItemMeta()
                        .getPersistentDataContainer();
                if (container.has(joinID)) {
                    Player p = (Player) e.getWhoClicked();
                    int id = container.get(joinID, PersistentDataType.INTEGER);
                    if (Gamingteams.INSTANCE.manager.accept(p, id)) {
                        PlaceholderManager.reset(p);
                        p.closeInventory();
                        List<OfflinePlayer> list = DataBasePool.getMembersOfflinePlayer(
                                Gamingteams.INSTANCE.basePool,
                                id);
                        list.forEach(action -> {
                            if (action.isOnline()) {
                                ((Player) action).sendMessage(mm.deserialize(
                                        Language.getValue(Gamingteams.INSTANCE,
                                                p, "playerJoined",
                                                true),
                                        Placeholder.component("player",
                                                p.displayName())));
                            }
                        });
                    } else {
                        System.err.println(id);
                        pg.removeItem(e.getCurrentItem());
                        pg.refeshPage();
                        p.openInventory(
                                new ErrorGUI(e.getInventory(), p,
                                        mm.deserialize(Language.getValue(
                                                Gamingteams.INSTANCE, p,
                                                "noInvite")))
                                        .getInventory());
                    }
                }
            }
        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:open_team_gui", (e) -> {
            e.setCancelled(true);
            if (e.getInventory().getHolder() instanceof PlayerManagement pm) {
                e.getWhoClicked().openInventory(pm.returnInv);
            }
        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:remove_player", (e) -> {
            e.setCancelled(true);
            if (e.getInventory().getHolder() instanceof PlayerManagement pm) {
                OfflinePlayer p = pm.target;

                List<OfflinePlayer> list = DataBasePool
                        .getMembersOfflinePlayer(Gamingteams.INSTANCE.basePool, pm.teamID);
                DataBasePool.removePlayerToTeam(Gamingteams.INSTANCE.basePool, pm.teamID,
                        p.getUniqueId());
                Bukkit.getServer().getPluginManager().callEvent(new TeamsLeftPlayer(pm.teamID, p.getUniqueId()));
                if (p.isOnline())
                    PlaceholderManager.reset(p.getPlayer());
                Gamingteams.INSTANCE.manager.removeInvite((Player) p, pm.teamID);
                list.forEach(action -> {
                    if (action.isOnline()) {
                        (action.getPlayer()).sendMessage(mm.deserialize(
                                Language.getValue(Gamingteams.INSTANCE, (Player) action,
                                        "playerLeft",
                                        true),
                                Placeholder.component("player",
                                        Component.text(p.getName()))));
                    }
                });
                if (pm.returnInv instanceof Pagenation pg) {
                    pg.removeItem(e.getCurrentItem());
                    pg.refeshPage();
                }
                e.getWhoClicked().openInventory(pm.returnInv);
            }
        });

        ItemBuilderManager.addBothClickEvent("GamingTeams:open_player_management", (e) -> {
            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();
            int id = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
            SkullMeta skull = (SkullMeta) e.getCurrentItem().getItemMeta();

            if (skull.getOwningPlayer() != null && skull.getOwningPlayer().isOnline()
                    && p == (Player) skull.getOwningPlayer()) {
                p.openInventory(
                        new ErrorGUI(e.getInventory(), p,
                                mm.deserialize(Language.getValue(
                                        Gamingteams.INSTANCE, p,
                                        "noOpenSelf")))
                                .getInventory());
                return;
            }

            if (!isOwner(p, id)) {
                p.openInventory(
                        new ErrorGUI(e.getInventory(), p,
                                mm.deserialize(Language.getValue(
                                        Gamingteams.INSTANCE, p,
                                        "notOwner")))
                                .getInventory());
                return;
            }

            e.getWhoClicked().openInventory(
                    new PlayerManagement((Player) e.getWhoClicked(),
                            skull.getOwningPlayer(), id, e.getInventory())
                            .getInventory());
        });

    }

    public boolean isOwner(Player p, int id) {
        return DataBasePool.isOwner(
                Gamingteams.INSTANCE.basePool,
                p.getUniqueId(),
                id);
    }

}
