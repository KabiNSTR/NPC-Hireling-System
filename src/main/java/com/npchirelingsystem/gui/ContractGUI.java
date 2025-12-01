package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.ContractManager;
import com.npchirelingsystem.managers.ContractManager.Contract;
import com.npchirelingsystem.managers.ContractManager.ContractCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ContractGUI {

    public static void open(Player player, ContractManager manager, ContractCategory category) {
        Inventory inv = Bukkit.createInventory(null, 54, NPCHirelingSystem.getLang().getRaw("contracts_prefix") + category.name());

        fillBorders(inv);

        // Category Tabs
        inv.setItem(10, createTab(Material.IRON_PICKAXE, "Сбор", category == ContractCategory.GATHERING));
        inv.setItem(11, createTab(Material.IRON_SWORD, "Охота", category == ContractCategory.HUNTING));
        inv.setItem(12, createTab(Material.COMPASS, "Исследование", category == ContractCategory.EXPLORATION));
        inv.setItem(13, createTab(Material.WITHER_SKELETON_SKULL, "Боссы", category == ContractCategory.BOSS));

        // Reputation Info
        ItemStack rep = new ItemStack(Material.BOOK);
        ItemMeta repMeta = rep.getItemMeta();
        repMeta.setDisplayName(ChatColor.GOLD + "Репутация: " + manager.getReputation(player));
        repMeta.setLore(Arrays.asList(ChatColor.GRAY + "Высокая репутация открывает", ChatColor.GRAY + "лучшие контракты!"));
        rep.setItemMeta(repMeta);
        inv.setItem(4, rep);

        List<Contract> contracts = manager.getContracts(category);
        
        int[] slots = {29, 30, 31, 32, 33}; 
        
        for (int i = 0; i < contracts.size(); i++) {
            if (i >= slots.length) break;
            
            Contract c = contracts.get(i);
            Material icon = c.material != null ? c.material : Material.PAPER;
            if (c.type == ContractManager.ContractType.MOB_KILL) icon = Material.ZOMBIE_HEAD;
            if (c.type == ContractManager.ContractType.BOSS_KILL) icon = Material.DRAGON_HEAD;
            
            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(c.rarity.color + c.description);
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Тип: " + ChatColor.WHITE + c.type.name(),
                ChatColor.GRAY + "Редкость: " + c.rarity.color + c.rarity.name(),
                "",
                ChatColor.GRAY + "Награда: " + ChatColor.GOLD + String.format("%.2f", c.reward) + " монет",
                ChatColor.GRAY + "Репутация: " + ChatColor.AQUA + "+" + (int)(5 * c.rarity.multiplier),
                "",
                ChatColor.YELLOW + "Нажмите, чтобы принять"
            ));
            item.setItemMeta(meta);
            
            inv.setItem(slots[i], item);
        }

        // Back
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("contract_back"));
        back.setItemMeta(backMeta);
        inv.setItem(49, back);

        // Quest Status / Claim Reward
        if (NPCHirelingSystem.getQuestManager().isQuestCompleted(player)) {
            ItemStack claim = new ItemStack(Material.GOLD_BLOCK);
            ItemMeta meta = claim.getItemMeta();
            meta.setDisplayName("§6§lЗабрать награду");
            meta.setLore(Arrays.asList("§7Нажмите, чтобы забрать награду!"));
            claim.setItemMeta(meta);
            inv.setItem(40, claim);
        } else if (NPCHirelingSystem.getQuestManager().hasActiveQuest(player)) {
            ItemStack status = new ItemStack(Material.COMPASS);
            ItemMeta meta = status.getItemMeta();
            meta.setDisplayName("§eКвест выполняется");
            meta.setLore(Arrays.asList("§7Проверьте чат для деталей."));
            status.setItemMeta(meta);
            inv.setItem(40, status);
        }

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

    private static void fillBorders(Inventory inv) {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= inv.getSize() - 9 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, glass);
            }
        }
    }
}
