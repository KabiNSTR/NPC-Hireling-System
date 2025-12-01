package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class WageEditorGUI {

    public static void open(Player player) {
        String title = NPCHirelingSystem.getLang().getRaw("wage_gui_title");
        Inventory inv = Bukkit.createInventory(null, 27, title);
        FileConfiguration config = NPCHirelingSystem.getInstance().getConfig();

        String[] jobs = {"miner", "guard", "farmer", "hunter"};
        int[] slots = {10, 12, 14, 16};
        Material[] icons = {Material.IRON_PICKAXE, Material.IRON_SWORD, Material.IRON_HOE, Material.BOW};

        for (int i = 0; i < jobs.length; i++) {
            String job = jobs[i];
            double wage = config.getDouble("wages." + job, 10.0);
            
            ItemStack item = new ItemStack(icons[i]);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(NPCHirelingSystem.getLang().getRaw("wage_edit")
                    .replace("%job%", job)
                    .replace("%amount%", String.valueOf(wage)));
            meta.setLore(Arrays.asList(NPCHirelingSystem.getLang().getRaw("wage_click_change")));
            item.setItemMeta(meta);
            
            inv.setItem(slots[i], item);
        }

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(26, back);

        player.openInventory(inv);
    }
}
