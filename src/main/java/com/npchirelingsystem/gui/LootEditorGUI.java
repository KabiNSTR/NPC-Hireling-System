package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LootEditorGUI {

    public static void openJobSelector(Player player) {
        String title = "Loot Editor: Select Job";
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack miner = createItem(Material.IRON_PICKAXE, "miner");
        ItemStack farmer = createItem(Material.IRON_HOE, "farmer");
        ItemStack hunter = createItem(Material.BOW, "hunter");

        inv.setItem(11, miner);
        inv.setItem(13, farmer);
        inv.setItem(15, hunter);
        
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(26, back);

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e" + name);
        item.setItemMeta(meta);
        return item;
    }

    public static void openEditor(Player player, String job) {
        String title = NPCHirelingSystem.getLang().getRaw("loot_gui_title").replace("%job%", job);
        Inventory inv = Bukkit.createInventory(null, 54, title);
        FileConfiguration config = NPCHirelingSystem.getInstance().getConfig();

        // Chance Item
        int chance = config.getInt("jobs." + job + ".chance", 10);
        ItemStack chanceItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta chanceMeta = chanceItem.getItemMeta();
        chanceMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("loot_chance").replace("%chance%", String.valueOf(chance)));
        chanceMeta.setLore(Arrays.asList(NPCHirelingSystem.getLang().getRaw("wage_click_change")));
        chanceItem.setItemMeta(chanceMeta);
        inv.setItem(49, chanceItem);

        // Load Items
        List<String> items = config.getStringList("jobs." + job + ".items");
        for (String matName : items) {
            Material mat = Material.getMaterial(matName);
            if (mat != null) {
                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add(NPCHirelingSystem.getLang().getRaw("loot_click_remove"));
                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.addItem(item);
            }
        }
        
        // Info/Add Hint
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("loot_add_item"));
        info.setItemMeta(infoMeta);
        inv.setItem(53, info);
        
        // Back
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(45, back);

        player.openInventory(inv);
    }
}
