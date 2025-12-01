package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class LootItemEditorGUI {

    public static void open(Player player, String profession, Material material) {
        String title = "Edit Loot: " + profession + " - " + material.name();
        // Truncate title if too long (32 char limit in older versions, but 1.20 is fine usually)
        if (title.length() > 32) title = title.substring(0, 32);
        
        Inventory inv = Bukkit.createInventory(null, 27, title);
        
        LootManager lootManager = NPCHirelingSystem.getLootManager();
        List<LootManager.LootItem> table = lootManager.getLootTable(profession);
        LootManager.LootItem targetItem = null;
        for (LootManager.LootItem item : table) {
            if (item.material == material) {
                targetItem = item;
                break;
            }
        }
        
        if (targetItem == null) {
            player.sendMessage(NPCHirelingSystem.getLang().getRaw("loot_item_not_found"));
            LootEditorGUI.openEditor(player, profession);
            return;
        }

        // Display Item
        ItemStack display = new ItemStack(material);
        ItemMeta meta = display.getItemMeta();
        meta.setDisplayName(NPCHirelingSystem.getLang().getRaw("loot_item_editor").replace("%item%", material.name()));
        meta.setLore(Arrays.asList(
            "§7Min: §f" + targetItem.min,
            "§7Max: §f" + targetItem.max,
            "§7Chance: §f" + targetItem.chance + "%"
        ));
        display.setItemMeta(meta);
        inv.setItem(4, display);

        // Min Controls
        inv.setItem(10, createButton(Material.RED_STAINED_GLASS_PANE, "§c-1 Min"));
        inv.setItem(11, createButton(Material.GREEN_STAINED_GLASS_PANE, "§a+1 Min"));
        
        // Max Controls
        inv.setItem(13, createButton(Material.RED_STAINED_GLASS_PANE, "§c-1 Max"));
        inv.setItem(14, createButton(Material.GREEN_STAINED_GLASS_PANE, "§a+1 Max"));
        
        // Chance Controls
        inv.setItem(16, createButton(Material.RED_STAINED_GLASS_PANE, "§c-5% Chance"));
        inv.setItem(17, createButton(Material.GREEN_STAINED_GLASS_PANE, "§a+5% Chance"));

        // Back
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(26, back);

        player.openInventory(inv);
    }

    private static ItemStack createButton(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
