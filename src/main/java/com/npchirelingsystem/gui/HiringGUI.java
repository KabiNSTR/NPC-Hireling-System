package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class HiringGUI {

    public static void open(Player player) {
        String title = NPCHirelingSystem.getLang().getRaw("hire_gui_title");
        Inventory inv = Bukkit.createInventory(null, 9, title);

        String minerName = NPCHirelingSystem.getLang().getRaw("npc_miner");
        String guardName = NPCHirelingSystem.getLang().getRaw("npc_guard");
        String farmerName = NPCHirelingSystem.getLang().getRaw("npc_farmer");
        String clickHire = NPCHirelingSystem.getLang().getRaw("click_to_hire");
        String wageLore = NPCHirelingSystem.getLang().getRaw("wage_lore");

        double minerWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.miner", 10.0);
        double guardWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.guard", 15.0);
        double farmerWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.farmer", 8.0);

        inv.setItem(2, createItem(Material.IRON_PICKAXE, minerName, wageLore.replace("%wage%", String.valueOf(minerWage)), clickHire));
        inv.setItem(4, createItem(Material.IRON_SWORD, guardName, wageLore.replace("%wage%", String.valueOf(guardWage)), clickHire));
        inv.setItem(6, createItem(Material.WHEAT, farmerName, wageLore.replace("%wage%", String.valueOf(farmerWage)), clickHire));

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
