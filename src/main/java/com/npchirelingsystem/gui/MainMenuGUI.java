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
        String title = NPCHirelingSystem.getLang().getRaw("main_menu_title");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Fillers
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        // Hire New
        ItemStack hire = createItem(Material.EMERALD, 
            NPCHirelingSystem.getLang().getRaw("menu_hire_name"), 
            NPCHirelingSystem.getLang().getRaw("menu_hire_lore").split("\\|"));
        inv.setItem(11, hire);

        // My Hirelings
        ItemStack list = createItem(Material.BOOK, 
            NPCHirelingSystem.getLang().getRaw("menu_list_name"), 
            NPCHirelingSystem.getLang().getRaw("menu_list_lore").split("\\|"));
        inv.setItem(13, list);

        // Contracts
        ItemStack contracts = createItem(Material.PAPER, 
            NPCHirelingSystem.getLang().getRaw("menu_contracts_name"), 
            NPCHirelingSystem.getLang().getRaw("menu_contracts_lore").split("\\|"));
        inv.setItem(15, contracts);
        
        // Admin (if op and allowed)
        if (allowAdmin && player.hasPermission("npchirelingsystem.admin")) {
            ItemStack admin = createItem(Material.COMMAND_BLOCK, 
                NPCHirelingSystem.getLang().getRaw("menu_admin_name"), 
                NPCHirelingSystem.getLang().getRaw("menu_admin_lore").split("\\|"));
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
