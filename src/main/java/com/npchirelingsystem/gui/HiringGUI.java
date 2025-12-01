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
        Inventory inv = Bukkit.createInventory(null, 27, title);

        String minerName = NPCHirelingSystem.getLang().getRaw("npc_miner");
        String guardName = NPCHirelingSystem.getLang().getRaw("npc_guard");
        String farmerName = NPCHirelingSystem.getLang().getRaw("npc_farmer");
        String hunterName = NPCHirelingSystem.getLang().getRaw("npc_hunter");
        String lumberName = NPCHirelingSystem.getLang().getRaw("npc_lumberjack");
        String fisherName = NPCHirelingSystem.getLang().getRaw("npc_fisherman");
        
        String clickHire = NPCHirelingSystem.getLang().getRaw("click_to_hire");
        String wageLore = NPCHirelingSystem.getLang().getRaw("wage_lore");

        double minerWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.miner", 10.0);
        double guardWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.guard", 15.0);
        double farmerWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.farmer", 8.0);
        double hunterWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.hunter", 12.0);
        double lumberWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.lumberjack", 9.0);
        double fisherWage = NPCHirelingSystem.getInstance().getConfig().getDouble("wages.fisherman", 9.0);

        inv.setItem(10, createItem(Material.IRON_PICKAXE, minerName, wageLore.replace("%wage%", String.valueOf(minerWage)), clickHire));
        inv.setItem(11, createItem(Material.IRON_SWORD, guardName, wageLore.replace("%wage%", String.valueOf(guardWage)), clickHire));
        inv.setItem(12, createItem(Material.WHEAT, farmerName, wageLore.replace("%wage%", String.valueOf(farmerWage)), clickHire));
        inv.setItem(14, createItem(Material.BOW, hunterName, wageLore.replace("%wage%", String.valueOf(hunterWage)), clickHire));
        inv.setItem(15, createItem(Material.IRON_AXE, lumberName, wageLore.replace("%wage%", String.valueOf(lumberWage)), clickHire));
        inv.setItem(16, createItem(Material.FISHING_ROD, fisherName, wageLore.replace("%wage%", String.valueOf(fisherWage)), clickHire));

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
