package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class AdminGUI {

    public static void open(Player player, NPCManager npcManager) {
        String title = NPCHirelingSystem.getLang().getRaw("admin_gui_title");
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Reload Button
        ItemStack reloadItem = createItem(Material.REDSTONE_TORCH, NPCHirelingSystem.getLang().getRaw("admin_reload"));
        inv.setItem(8, reloadItem);
        
        // Settings Button
        ItemStack settingsItem = createItem(Material.COMPARATOR, NPCHirelingSystem.getLang().getRaw("admin_settings"));
        inv.setItem(0, settingsItem);

        // List Hirelings
        List<HirelingNPC> allHirelings = npcManager.getAllHirelings();
        int slot = 9;
        for (HirelingNPC npc : allHirelings) {
            if (slot >= 54) break;
            
            String ownerName = "Unknown";
            Player owner = Bukkit.getPlayer(npc.getOwnerId());
            if (owner != null) ownerName = owner.getName();
            else ownerName = Bukkit.getOfflinePlayer(npc.getOwnerId()).getName();

            ItemStack head = createItem(Material.PLAYER_HEAD, 
                "§e" + npc.getName(), 
                "§7Owner: §f" + ownerName,
                "§7Profession: §f" + npc.getProfession(),
                "§7Wage: §6" + npc.getWage(),
                "§cClick to FIRE"
            );
            inv.setItem(slot++, head);
        }

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
