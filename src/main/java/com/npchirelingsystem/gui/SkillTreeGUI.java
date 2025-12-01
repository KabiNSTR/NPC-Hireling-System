package com.npchirelingsystem.gui;

import com.npchirelingsystem.models.HirelingNPC;
import com.npchirelingsystem.NPCHirelingSystem;
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
        dropMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("skill_efficiency"));
        dropMeta.setLore(Arrays.asList(
            NPCHirelingSystem.getLang().getRaw("skill_efficiency_lore"),
            NPCHirelingSystem.getLang().getRaw("skill_level").replace("%level%", String.valueOf(npc.getDropRateUpgrade())).replace("%max%", "∞"),
            "§7Bonus: +" + (npc.getDropRateUpgrade() * 5) + "%",
            "",
            NPCHirelingSystem.getLang().getRaw("skill_cost"),
            npc.getSkillPoints() > 0 ? NPCHirelingSystem.getLang().getRaw("click_to_upgrade") : NPCHirelingSystem.getLang().getRaw("not_enough_points")
        ));
        dropRate.setItemMeta(dropMeta);
        inv.setItem(11, dropRate);

        // Rare Drop Upgrade
        ItemStack rareDrop = new ItemStack(Material.DIAMOND);
        ItemMeta rareMeta = rareDrop.getItemMeta();
        rareMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("skill_luck"));
        rareMeta.setLore(Arrays.asList(
            NPCHirelingSystem.getLang().getRaw("skill_luck_lore"),
            NPCHirelingSystem.getLang().getRaw("skill_level").replace("%level%", String.valueOf(npc.getRareDropUpgrade())).replace("%max%", "∞"),
            "§7Bonus: +" + (npc.getRareDropUpgrade() * 1) + "%",
            "",
            NPCHirelingSystem.getLang().getRaw("skill_cost"),
            npc.getSkillPoints() > 0 ? NPCHirelingSystem.getLang().getRaw("click_to_upgrade") : NPCHirelingSystem.getLang().getRaw("not_enough_points")
        ));
        rareDrop.setItemMeta(rareMeta);
        inv.setItem(15, rareDrop);

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(22, back);

        player.openInventory(inv);
    }
}
