package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.NPCManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    private final NPCManager npcManager;

    public GUIListener(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Hire an NPC")) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (!item.hasItemMeta()) return;
        String name = item.getItemMeta().getDisplayName();

        if (name.contains("Miner")) {
            npcManager.hireNPC(player, "ARMORER", 10.0); // Using ARMORER as profession for Miner visual
        } else if (name.contains("Guard")) {
            npcManager.hireNPC(player, "WEAPONSMITH", 15.0);
        } else if (name.contains("Farmer")) {
            npcManager.hireNPC(player, "FARMER", 8.0);
        }
        
        player.closeInventory();
    }
}
