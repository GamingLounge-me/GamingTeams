package me.gaminglounge.gamingteams.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.gaminglounge.configapi.Language;
import me.gaminglounge.gamingteams.Gamingteams;
import me.gaminglounge.guiapi.GuiFromMap;
import me.gaminglounge.itembuilder.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerManagement implements InventoryHolder {
    private MiniMessage mm = MiniMessage.miniMessage();

    public Inventory inv, returnInv;
    public OfflinePlayer target;
    public int teamID;

    public PlayerManagement(Player p, OfflinePlayer target, int team, Inventory returnInv) {
        this.teamID = team;
        this.target = target;
        this.returnInv = returnInv;
        Map<Integer, ItemStack> items = new HashMap<>();

        items.put(
                11,
                new ItemBuilder(Material.GOLDEN_HELMET)
                        .setName(mm.deserialize(
                                Language.getValue(Gamingteams.INSTANCE, p,
                                        "playermanagement.makeowner.name")))
                        .addBothClickEvent("GamingTeams:change_owner")
                        .build());

        items.put(
                15,
                new ItemBuilder(Material.RED_DYE)
                        .setName(mm.deserialize(
                                Language.getValue(Gamingteams.INSTANCE, p,
                                        "playermanagement.kick.name")))
                        .addBothClickEvent("GamingTeams:remove_player")
                        .build());

        items.put(
                22,
                new ItemBuilder(Material.BARRIER)
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.close.name")))
                        .addBothClickEvent("GamingTeams:open_team_gui")
                        .build());

        inv = new GuiFromMap(this, 3).setItems(items).getInventory();

    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

}
