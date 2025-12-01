package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.ContractManager;
import com.npchirelingsystem.managers.ContractManager.Contract;
import com.npchirelingsystem.managers.ContractManager.ContractCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ContractGUI {

    public static void open(Player player, ContractManager manager, ContractCategory category) {
        Inventory inv = Bukkit.createInventory(null, 45, NPCHirelingSystem.getLang().getRaw("contracts_prefix") + category.name());

        // Category Tabs
        inv.setItem(0, createTab(Material.IRON_PICKAXE, NPCHirelingSystem.getLang().getRaw("contract_tab_gathering"), category == ContractCategory.GATHERING));
        inv.setItem(1, createTab(Material.IRON_SWORD, NPCHirelingSystem.getLang().getRaw("contract_tab_hunting"), category == ContractCategory.HUNTING));
        inv.setItem(2, createTab(Material.NETHER_STAR, NPCHirelingSystem.getLang().getRaw("contract_tab_legendary"), category == ContractCategory.LEGENDARY));

        List<Contract> contracts = manager.getContracts(category);
        
        int[] slots = {19, 21, 23, 25}; // 4 slots just in case, though we generate 3
        
        for (int i = 0; i < contracts.size(); i++) {
            if (i >= slots.length) break;
            
            Contract c = contracts.get(i);
            Material icon = c.material != null ? c.material : Material.PAPER;
            if (c.type == ContractManager.ContractType.MOB_KILL) icon = Material.ZOMBIE_HEAD;
            
            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(NPCHirelingSystem.getLang().getRaw("contract_item_name").replace("%type%", c.type.name()));
            meta.setLore(Arrays.asList(
                NPCHirelingSystem.getLang().getRaw("contract_lore_type").replace("%type%", c.type.name()),
                NPCHirelingSystem.getLang().getRaw("contract_lore_desc").replace("%desc%", c.description),
                NPCHirelingSystem.getLang().getRaw("contract_lore_reward").replace("%reward%", String.format("%.2f", c.reward)),
                "",
                NPCHirelingSystem.getLang().getRaw("contract_lore_click")
            ));
            item.setItemMeta(meta);
            
            inv.setItem(slots[i], item);
        }

        // Back
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("contract_back"));
        back.setItemMeta(backMeta);
        inv.setItem(44, back);

        player.openInventory(inv);
    }
    
    public static void open(Player player, ContractManager manager) {
        open(player, manager, ContractCategory.GATHERING);
    }

    private static ItemStack createTab(Material mat, String name, boolean active) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((active ? "§a" : "§7") + name);
        if (active) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }
}
