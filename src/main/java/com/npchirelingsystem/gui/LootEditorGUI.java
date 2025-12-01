package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LootEditorGUI {

    public static void openJobSelector(Player player) {
        String title = "Loot Editor: Select Job";
        Inventory inv = Bukkit.createInventory(null, 27, title);

        inv.setItem(10, createItem(Material.IRON_PICKAXE, "miner"));
        inv.setItem(11, createItem(Material.IRON_HOE, "farmer"));
        inv.setItem(12, createItem(Material.BOW, "hunter"));
        inv.setItem(13, createItem(Material.IRON_AXE, "lumberjack"));
        inv.setItem(14, createItem(Material.FISHING_ROD, "fisherman"));
        inv.setItem(15, createItem(Material.IRON_SWORD, "guard"));
        
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(26, back);

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e" + name);
        item.setItemMeta(meta);
        return item;
    }

    public static void openEditor(Player player, String job) {
        String title = NPCHirelingSystem.getLang().getRaw("loot_gui_title").replace("%job%", job);
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        LootManager lootManager = NPCHirelingSystem.getLootManager();
        List<LootManager.LootItem> items = lootManager.getLootTable(job);

        for (LootManager.LootItem lootItem : items) {
            ItemStack item = new ItemStack(lootItem.material);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Min: §f" + lootItem.min);
            lore.add("§7Max: §f" + lootItem.max);
            lore.add("§7Chance: §f" + lootItem.chance + "%");
            lore.add("§eLeft-Click to Edit");
            lore.add("§cRight-Click to Remove");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.addItem(item);
        }
        
        // Info/Add Hint
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("loot_add_item"));
        infoMeta.setLore(Arrays.asList("§7Click items in your inventory", "§7to add them to the loot table."));
        info.setItemMeta(infoMeta);
        inv.setItem(53, info);
        
        // Back
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(45, back);

        player.openInventory(inv);
    }
}
