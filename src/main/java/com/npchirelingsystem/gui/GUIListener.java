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
        String title = event.getView().getTitle();
        String hireTitle = NPCHirelingSystem.getLang().getRaw("hire_gui_title");
        String adminTitle = NPCHirelingSystem.getLang().getRaw("admin_gui_title");

        if (title.equals(hireTitle)) {
            handleHireClick(event);
        } else if (title.equals(adminTitle)) {
            handleAdminClick(event);
        }
    }

    private void handleHireClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (!item.hasItemMeta()) return;
        String name = item.getItemMeta().getDisplayName();
        
        String minerName = NPCHirelingSystem.getLang().getRaw("npc_miner");
        String guardName = NPCHirelingSystem.getLang().getRaw("npc_guard");
        String farmerName = NPCHirelingSystem.getLang().getRaw("npc_farmer");

        if (name.equals(minerName)) {
            npcManager.hireNPC(player, "ARMORER", 10.0);
        } else if (name.equals(guardName)) {
            npcManager.hireNPC(player, "WEAPONSMITH", 15.0);
        } else if (name.equals(farmerName)) {
            npcManager.hireNPC(player, "FARMER", 8.0);
        }
        
        player.closeInventory();
    }

    private void handleAdminClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        
        if (!item.hasItemMeta()) return;
        String name = item.getItemMeta().getDisplayName();
        
        if (name.equals(NPCHirelingSystem.getLang().getRaw("admin_reload"))) {
            NPCHirelingSystem.getInstance().reloadPlugin();
            player.sendMessage(NPCHirelingSystem.getLang().get("reload_success"));
            player.closeInventory();
            return;
        }
        
        // Handle firing logic (simple check if it's a head)
        if (item.getType() == org.bukkit.Material.PLAYER_HEAD) {
            // Find NPC by name in lore or similar (simplified for now, ideally store UUID in NBT)
            // For this example, we just reload the GUI to simulate action or fire the first matching
            // In a real plugin, use PersistentDataContainer to store NPC UUID on the item
            player.sendMessage("§cFeature to fire specific NPC via GUI is coming soon! Use command.");
        }
    }
}
