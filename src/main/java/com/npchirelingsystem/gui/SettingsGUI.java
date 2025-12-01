package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SettingsGUI {

    public static void open(Player player) {
        String title = NPCHirelingSystem.getLang().getRaw("settings_gui_title");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Language Button
        ItemStack langItem = new ItemStack(Material.BOOK);
        ItemMeta langMeta = langItem.getItemMeta();
        String currentLang = NPCHirelingSystem.getInstance().getConfig().getString("lang", "en");
        langMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("setting_lang").replace("%lang%", currentLang));
        langMeta.setLore(Arrays.asList(NPCHirelingSystem.getLang().getRaw("lang_click_change")));
        langItem.setItemMeta(langMeta);
        inv.setItem(11, langItem);

        // Wages Button
        ItemStack wageItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta wageMeta = wageItem.getItemMeta();
        wageMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("setting_wages"));
        wageItem.setItemMeta(wageMeta);
        inv.setItem(13, wageItem);

        // Loot Button
        ItemStack lootItem = new ItemStack(Material.CHEST);
        ItemMeta lootMeta = lootItem.getItemMeta();
        lootMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("setting_loot"));
        lootItem.setItemMeta(lootMeta);
        inv.setItem(15, lootItem);

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(26, back);

        player.openInventory(inv);
    }
}
