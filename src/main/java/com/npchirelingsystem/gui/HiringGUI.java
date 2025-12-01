package com.npchirelingsystem.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class HiringGUI {

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "Hire an NPC");

        inv.setItem(2, createItem(Material.IRON_PICKAXE, "§bMiner", "§7Wage: §e10.0/min", "§7Click to hire!"));
        inv.setItem(4, createItem(Material.IRON_SWORD, "§cGuard", "§7Wage: §e15.0/min", "§7Click to hire!"));
        inv.setItem(6, createItem(Material.WHEAT, "§aFarmer", "§7Wage: §e8.0/min", "§7Click to hire!"));

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
