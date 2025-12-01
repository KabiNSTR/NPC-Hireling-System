package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MainMenuGUI {

    public static void open(Player player, boolean allowAdmin) {
        String title = "NPC Hireling System";
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Hire New
        ItemStack hire = createItem(Material.EMERALD, "§aHire New NPC", "§7Click to browse available workers");
        inv.setItem(11, hire);

        // My Hirelings
        ItemStack list = createItem(Material.BOOK, "§eMy Hirelings", "§7Manage your current workers", "§7Upgrades & Settings");
        inv.setItem(13, list);

        // Contracts
        ItemStack contracts = createItem(Material.PAPER, "§6Contracts & Quests", "§7View available jobs", "§7Earn money!");
        inv.setItem(15, contracts);
        
        // Admin (if op and allowed)
        if (allowAdmin && player.hasPermission("npchirelingsystem.admin")) {
            ItemStack admin = createItem(Material.COMMAND_BLOCK, "§cAdmin Panel", "§7Server settings & Management");
            inv.setItem(26, admin);
        }

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
