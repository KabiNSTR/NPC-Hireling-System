package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.NPCManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
        String settingsTitle = NPCHirelingSystem.getLang().getRaw("settings_gui_title");
        String wageTitle = NPCHirelingSystem.getLang().getRaw("wage_gui_title");
        
        if (title.equals(hireTitle)) {
            handleHireClick(event);
        } else if (title.equals(adminTitle)) {
            handleAdminClick(event);
        } else if (title.equals(settingsTitle)) {
            handleSettingsClick(event);
        } else if (title.equals(wageTitle)) {
            handleWageEditClick(event);
        } else if (title.startsWith("Loot Editor: ")) {
            handleLootEditClick(event);
        } else if (title.equals("Loot Editor: Select Job")) {
            handleLootJobSelect(event);
        } else if (title.endsWith("'s Inventory")) {
            handleNPCMenuClick(event);
        }
    }
    
    private void handleSettingsClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        
        if (item.getType() == Material.BOOK) { // Language
            String current = NPCHirelingSystem.getInstance().getConfig().getString("lang", "en");
            String next = current.equals("en") ? "ru" : "en";
            NPCHirelingSystem.getInstance().getConfig().set("lang", next);
            NPCHirelingSystem.getInstance().saveConfig();
            NPCHirelingSystem.getInstance().reloadPlugin();
            SettingsGUI.open(player); // Re-open to update text
        } else if (item.getType() == Material.GOLD_INGOT) { // Wages
            WageEditorGUI.open(player);
        } else if (item.getType() == Material.CHEST) { // Loot
            LootEditorGUI.openJobSelector(player);
        } else if (item.getType() == Material.ARROW) { // Back
            AdminGUI.open(player, npcManager);
        }
    }
    
    private void handleWageEditClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        
        if (item.getType() == Material.ARROW) {
            SettingsGUI.open(player);
            return;
        }
        
        String job = null;
        if (item.getType() == Material.IRON_PICKAXE) job = "miner";
        else if (item.getType() == Material.IRON_SWORD) job = "guard";
        else if (item.getType() == Material.IRON_HOE) job = "farmer";
        else if (item.getType() == Material.BOW) job = "hunter";
        
        if (job != null) {
            FileConfiguration config = NPCHirelingSystem.getInstance().getConfig();
            double current = config.getDouble("wages." + job, 10.0);
            
            if (event.isLeftClick()) current += 1.0;
            else if (event.isRightClick()) current -= 1.0;
            
            if (current < 0) current = 0;
            
            config.set("wages." + job, current);
            NPCHirelingSystem.getInstance().saveConfig();
            WageEditorGUI.open(player); // Refresh
        }
    }
    
    private void handleLootJobSelect(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        
        if (item.getType() == Material.ARROW) {
            SettingsGUI.open(player);
            return;
        }
        
        String name = item.getItemMeta().getDisplayName();
        if (name.contains("miner")) LootEditorGUI.openEditor(player, "miner");
        else if (name.contains("farmer")) LootEditorGUI.openEditor(player, "farmer");
        else if (name.contains("hunter")) LootEditorGUI.openEditor(player, "hunter");
    }
    
    private void handleLootEditClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        String job = title.replace("Loot Editor: ", "");
        
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            
            if (item.getType() == Material.ARROW) {
                LootEditorGUI.openJobSelector(player);
                return;
            }
            
            if (item.getType() == Material.EXPERIENCE_BOTTLE) {
                FileConfiguration config = NPCHirelingSystem.getInstance().getConfig();
                int chance = config.getInt("jobs." + job + ".chance", 10);
                if (event.isLeftClick()) chance += 1;
                else if (event.isRightClick()) chance -= 1;
                if (chance < 0) chance = 0;
                if (chance > 100) chance = 100;
                config.set("jobs." + job + ".chance", chance);
                NPCHirelingSystem.getInstance().saveConfig();
                LootEditorGUI.openEditor(player, job);
                return;
            }
            
            // Remove item
            if (item.getType() != Material.PAPER && item.getType() != Material.AIR) {
                FileConfiguration config = NPCHirelingSystem.getInstance().getConfig();
                List<String> items = config.getStringList("jobs." + job + ".items");
                items.remove(item.getType().name());
                config.set("jobs." + job + ".items", items);
                NPCHirelingSystem.getInstance().saveConfig();
                LootEditorGUI.openEditor(player, job);
            }
        } else {
            // Add item from player inventory
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() != Material.AIR) {
                FileConfiguration config = NPCHirelingSystem.getInstance().getConfig();
                List<String> items = config.getStringList("jobs." + job + ".items");
                if (!items.contains(item.getType().name())) {
                    items.add(item.getType().name());
                    config.set("jobs." + job + ".items", items);
                    NPCHirelingSystem.getInstance().saveConfig();
                    LootEditorGUI.openEditor(player, job);
                }
            }
        }
    }

    private void handleNPCMenuClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            int slot = event.getSlot();
            // Prevent clicking in system slots (18-26)
            if (slot >= 18) {
                event.setCancelled(true);
                
                if (slot == 26) { // Fire button
                    Player player = (Player) event.getWhoClicked();
                    // Find the NPC
                    for (HirelingNPC npc : npcManager.getAllHirelings()) {
                        if (npc.getInventory().equals(event.getClickedInventory())) {
                            npcManager.fireNPC(npc);
                            player.sendMessage(NPCHirelingSystem.getLang().get("fire_success"));
                            player.closeInventory();
                            return;
                        }
                    }
                }
            }
        } else if (event.isShiftClick()) {
            // Prevent shift-clicking items INTO the system slots
            // Actually, just prevent shift-clicking into top inventory if it's full or targeting system slots
            // For simplicity, allow shift click but if it lands in 18-26 it will be cancelled by the slot check above? 
            // No, shift click moves item automatically.
            // Let's just cancel shift-click if top inventory is the destination
            if (event.getView().getTopInventory().equals(event.getInventory())) {
                 // If player shift-clicks in their own inventory, it tries to move to top
                 // We should allow it only if it goes to 0-17
                 // This is complex to handle perfectly, so for now let's just allow it. 
                 // The system slots are already filled with glass panes, so items won't go there.
            }
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
        
        FileConfiguration config = NPCHirelingSystem.getInstance().getConfig();

        if (name.equals(minerName)) {
            double wage = config.getDouble("wages.miner", 10.0);
            npcManager.hireNPC(player, "ARMORER", wage);
        } else if (name.equals(guardName)) {
            double wage = config.getDouble("wages.guard", 15.0);
            npcManager.hireNPC(player, "WEAPONSMITH", wage);
        } else if (name.equals(farmerName)) {
            double wage = config.getDouble("wages.farmer", 8.0);
            npcManager.hireNPC(player, "FARMER", wage);
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
        
        if (name.equals(NPCHirelingSystem.getLang().getRaw("admin_settings"))) {
            SettingsGUI.open(player);
            return;
        }
        
        // Handle firing logic (simple check if it's a head)
        if (item.getType() == org.bukkit.Material.PLAYER_HEAD) {
            player.sendMessage("§cFeature to fire specific NPC via GUI is coming soon! Use command.");
        }
    }
}
