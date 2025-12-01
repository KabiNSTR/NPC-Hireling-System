package com.npchirelingsystem.gui;

import com.npchirelingsystem.managers.ContractManager;
import com.npchirelingsystem.managers.ContractManager.Contract;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ContractGUI {

    public static void open(Player player, ContractManager manager) {
        Inventory inv = Bukkit.createInventory(null, 27, "Trade Contracts");

        List<Contract> contracts = manager.getContracts();
        
        int[] slots = {11, 13, 15};
        
        for (int i = 0; i < contracts.size(); i++) {
            if (i >= slots.length) break;
            
            Contract c = contracts.get(i);
            ItemStack item = new ItemStack(c.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6Contract #" + (i + 1));
            meta.setLore(Arrays.asList(
                "§7Request: §f" + c.getAmount() + "x " + c.getMaterial().name(),
                "§7Reward: §e" + String.format("%.2f", c.getReward()) + " coins",
                "",
                "§aClick to fulfill!"
            ));
            item.setItemMeta(meta);
            
            inv.setItem(slots[i], item);
        }

        // Fillers
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }

        player.openInventory(inv);
    }
}
