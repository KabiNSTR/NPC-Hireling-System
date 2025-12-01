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

public class MyHirelingsGUI {

    public static void open(Player player, NPCManager npcManager) {
        String title = NPCHirelingSystem.getLang().getRaw("my_hirelings_title");
        Inventory inv = Bukkit.createInventory(null, 54, title);

        List<HirelingNPC> myHirelings = npcManager.getHirelings(player.getUniqueId());
        
        if (myHirelings.isEmpty()) {
            ItemStack empty = new ItemStack(Material.BARRIER);
            ItemMeta meta = empty.getItemMeta();
            meta.setDisplayName(NPCHirelingSystem.getLang().getRaw("no_hirelings"));
            empty.setItemMeta(meta);
            inv.setItem(22, empty);
        } else {
            int slot = 0;
            for (HirelingNPC npc : myHirelings) {
                if (slot >= 45) break;
                
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName(NPCHirelingSystem.getLang().getRaw("hireling_item_name").replace("%name%", npc.getName()));
                meta.setLore(Arrays.asList(
                    NPCHirelingSystem.getLang().getRaw("hireling_item_lore_profession").replace("%profession%", npc.getProfession()),
                    NPCHirelingSystem.getLang().getRaw("hireling_item_lore_level").replace("%level%", String.valueOf(npc.getLevel())),
                    NPCHirelingSystem.getLang().getRaw("hireling_item_lore_status").replace("%status%", npc.isFollowing() ? NPCHirelingSystem.getLang().getRaw("status_following") : NPCHirelingSystem.getLang().getRaw("status_stationary")),
                    "",
                    NPCHirelingSystem.getLang().getRaw("hireling_item_lore_click")
                ));
                head.setItemMeta(meta);
                inv.setItem(slot++, head);
            }
        }

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("back_button"));
        back.setItemMeta(backMeta);
        inv.setItem(49, back);

        player.openInventory(inv);
    }
}
