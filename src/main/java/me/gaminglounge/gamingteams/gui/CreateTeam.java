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
import me.gaminglounge.gamingteams.Gamingteams;
import me.gaminglounge.guiapi.GuiFromMap;
import me.gaminglounge.itembuilder.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class CreateTeam implements InventoryHolder {
    private MiniMessage mm;

    public Inventory inv;

    public CreateTeam(Player p) {
        mm = MiniMessage.miniMessage();

        Map<Integer, ItemStack> items = new HashMap<>();

        items.put(
                11,
                new ItemBuilder(Material.GREEN_WOOL)
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "createteamgui.add.name")))
                        .addBothClickEvent("GamingTeams:create_team")
                        .build());

        items.put(
                15,
                new ItemBuilder(Material.PAPER)
                        .setName(mm
                                .deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                        "createteamgui.invites.name")))
                        .addBothClickEvent("GamingTeams:list_invites")
                        .build());

        items.put(
                22,
                new ItemBuilder(Material.BARRIER)
                        .setName(mm.deserialize(Language.getValue(Gamingteams.INSTANCE, p,
                                "teamgui.close.name")))
                        .addBothClickEvent("ItemBuilder:close")
                        .build());

        inv = new GuiFromMap(this, 3).setItems(items).getInventory();

    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

}
