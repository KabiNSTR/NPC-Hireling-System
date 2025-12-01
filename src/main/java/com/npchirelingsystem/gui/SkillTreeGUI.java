package com.npchirelingsystem.gui;

import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SkillTreeGUI {

    public static void open(Player player, HirelingNPC npc) {
        Inventory inv = Bukkit.createInventory(null, 27, "Skill Tree: " + npc.getName());

        // Fill background
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        // Info Icon
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§e" + npc.getName() + "'s Skills");
        infoMeta.setLore(Arrays.asList(
            "§7Level: §a" + npc.getLevel(),
            "§7Skill Points: §b" + npc.getSkillPoints()
        ));
        info.setItemMeta(infoMeta);
        inv.setItem(4, info);

        // Drop Rate Upgrade
        ItemStack dropRate = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta dropMeta = dropRate.getItemMeta();
        dropMeta.setDisplayName("§6Efficiency Training");
        dropMeta.setLore(Arrays.asList(
            "§7Increases item drop rate.",
            "§7Current Level: " + npc.getDropRateUpgrade(),
            "§7Bonus: +" + (npc.getDropRateUpgrade() * 5) + "%",
            "",
            "§eCost: 1 Skill Point",
            npc.getSkillPoints() > 0 ? "§aClick to Upgrade" : "§cNot enough points"
        ));
        dropRate.setItemMeta(dropMeta);
        inv.setItem(11, dropRate);

        // Rare Drop Upgrade
        ItemStack rareDrop = new ItemStack(Material.DIAMOND);
        ItemMeta rareMeta = rareDrop.getItemMeta();
        rareMeta.setDisplayName("§bLuck Training");
        rareMeta.setLore(Arrays.asList(
            "§7Increases rare drop chance.",
            "§7Current Level: " + npc.getRareDropUpgrade(),
            "§7Bonus: +" + (npc.getRareDropUpgrade() * 1) + "%",
            "",
            "§eCost: 1 Skill Point",
            npc.getSkillPoints() > 0 ? "§aClick to Upgrade" : "§cNot enough points"
        ));
        rareDrop.setItemMeta(rareMeta);
        inv.setItem(15, rareDrop);

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack");
        back.setItemMeta(backMeta);
        inv.setItem(22, back);

        player.openInventory(inv);
    }
}
