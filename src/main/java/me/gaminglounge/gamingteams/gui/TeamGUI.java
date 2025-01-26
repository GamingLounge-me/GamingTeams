package me.gaminglounge.gamingteams.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.gaminglounge.configapi.Language;
import me.gaminglounge.gamingteams.DataBasePool;
import me.gaminglounge.gamingteams.Gamingteams;
import me.gaminglounge.guiapi.GuiFromMap;
import me.gaminglounge.itembuilder.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TeamGUI implements InventoryHolder {
    private static MiniMessage mm;

    public Inventory inv;
    public Player p;
    public int teamID;
    public Component name, tag;

    public TeamGUI(Player p) {
        mm = MiniMessage.miniMessage();
        this.p = p;

        this.teamID = DataBasePool.getTeam(Gamingteams.INSTANCE.basePool, p.getUniqueId());
        this.name = mm.deserialize(DataBasePool.getName(Gamingteams.INSTANCE.basePool, teamID));
        this.tag = mm.deserialize(DataBasePool.getTag(Gamingteams.INSTANCE.basePool, teamID));

        Map<Integer, ItemStack> items = new HashMap<>();
        items.put(
                11,
                new ItemBuilder(Material.OAK_SIGN)
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.name.name")))
                        .addLoreLine(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.name.lore")))
                        .addBothClickEvent("GamingTeams:change_name")
                        .build());
        items.put(
                15,
                new ItemBuilder(Material.NAME_TAG)
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.tag.name")))
                        .addLoreLine(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.tag.lore")))
                        .addBothClickEvent("GamingTeams:change_tag")
                        .build());
        items.put(
                22,
                new ItemBuilder(p.getUniqueId())
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.players.name")))
                        .addBothClickEvent("GamingTeams:open_player_list")
                        .build());
        items.put(
                40,
                new ItemBuilder(Material.BARRIER)
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.close.name")))
                        .addBothClickEvent("ItemBuilder:close")
                        .build());
        items.put(
                36,
                new ItemBuilder(Material.DARK_OAK_DOOR)
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.leave.name")))
                        .addBothClickEvent("GamingTeams:leave")
                        .build());
        items.put(
                44,
                new ItemBuilder(Material.RED_DYE)
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.remove.name")))
                        .addBothClickEvent("GamingTeams:remove_team")
                        .build());

        inv = new GuiFromMap(this, 5).setItems(items).setInventoryName(name.append(Component.text(" | ")).append(tag))
                .getInventory();

    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

}
