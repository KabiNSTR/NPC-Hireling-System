package com.npchirelingsystem.gui;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.managers.ContractManager;
import com.npchirelingsystem.managers.ContractManager.Contract;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GUIListener implements Listener {

    private final NPCManager npcManager;
    private final ContractManager contractManager;

    public GUIListener(NPCManager npcManager, ContractManager contractManager) {
        this.npcManager = npcManager;
        this.contractManager = contractManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        String hireTitle = NPCHirelingSystem.getLang().getRaw("hire_gui_title");
        String adminTitle = NPCHirelingSystem.getLang().getRaw("admin_gui_title");
        String settingsTitle = NPCHirelingSystem.getLang().getRaw("settings_gui_title");
        String wageTitle = NPCHirelingSystem.getLang().getRaw("wage_gui_title");
        
        if (title.equals("NPC Hireling System")) {
            handleMainMenuClick(event);
        } else if (title.equals(hireTitle)) {
            handleHireClick(event);
        } else if (title.equals(adminTitle)) {
            handleAdminClick(event);
        } else if (title.equals(settingsTitle)) {
            handleSettingsClick(event);
        } else if (title.equals(wageTitle)) {
            handleWageEditClick(event);
        } else if (title.startsWith("Loot Editor: ")) {
            if (title.equals("Loot Editor: Select Job")) {
                handleLootJobSelect(event);
            } else {
                handleLootEditClick(event);
            }
        } else if (title.startsWith("Edit Loot: ")) {
            handleLootItemEditClick(event);
        } else if (title.endsWith("'s Inventory")) {
            handleNPCMenuClick(event);
        } else if (title.startsWith("Contracts: ")) {
            handleContractClick(event);
        } else if (title.startsWith("Skill Tree: ")) {
            handleSkillTreeClick(event);
        }
    }
    
    private void handleSkillTreeClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        String npcName = title.replace("Skill Tree: ", "");
        
        HirelingNPC npc = null;
        for (HirelingNPC n : npcManager.getAllHirelings()) {
            if (n.getName().equals(npcName) && n.getOwnerId().equals(player.getUniqueId())) {
                npc = n;
                break;
            }
        }
        
        if (npc == null) {
            player.closeInventory();
            return;
        }
        
        int slot = event.getSlot();
        if (slot == 22) { // Back
            player.openInventory(npc.getInventory());
        } else if (slot == 11) { // Drop Rate
            if (npc.spendSkillPoint()) {
                npc.upgradeDropRate();
                player.sendMessage("§aUpgraded Drop Rate!");
                SkillTreeGUI.open(player, npc);
            } else {
                player.sendMessage("§cNot enough skill points!");
            }
        } else if (slot == 15) { // Rare Drop
            if (npc.spendSkillPoint()) {
                npc.upgradeRareDrop();
                player.sendMessage("§aUpgraded Rare Drop Chance!");
                SkillTreeGUI.open(player, npc);
            } else {
                player.sendMessage("§cNot enough skill points!");
            }
        }
    }
    
    private void handleMainMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        
        if (item.getType() == Material.EMERALD) {
            HiringGUI.open(player);
        } else if (item.getType() == Material.BOOK) {
            // Open list of hirelings (Not implemented yet, maybe just chat list or new GUI)
            player.sendMessage("§eUse /hire to see your hirelings via chat for now.");
        } else if (item.getType() == Material.PAPER) {
            ContractGUI.open(player, contractManager);
        } else if (item.getType() == Material.COMMAND_BLOCK) {
            AdminGUI.open(player, npcManager);
        }
    }
    
    private void handleContractClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        String catName = title.replace("Contracts: ", "");
        ContractManager.ContractCategory category = ContractManager.ContractCategory.valueOf(catName);
        
        int slot = event.getSlot();
        
        // Tabs
        if (slot == 0) ContractGUI.open(player, contractManager, ContractManager.ContractCategory.GATHERING);
        else if (slot == 1) ContractGUI.open(player, contractManager, ContractManager.ContractCategory.HUNTING);
        else if (slot == 2) ContractGUI.open(player, contractManager, ContractManager.ContractCategory.LEGENDARY);
        else if (slot == 44) MainMenuGUI.open(player, true);
        
        // Contracts
        List<Contract> contracts = contractManager.getContracts(category);
        int index = -1;
        if (slot == 19) index = 0;
        else if (slot == 21) index = 1;
        else if (slot == 23) index = 2;
        else if (slot == 25) index = 3;
        
        if (index != -1 && index < contracts.size()) {
            Contract contract = contracts.get(index);
            contractManager.acceptContract(player, contract);
            player.closeInventory();
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
        else if (item.getType() == Material.IRON_AXE) job = "lumberjack";
        else if (item.getType() == Material.FISHING_ROD) job = "fisherman";
        
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
        else if (name.contains("lumberjack")) LootEditorGUI.openEditor(player, "lumberjack");
        else if (name.contains("fisherman")) LootEditorGUI.openEditor(player, "fisherman");
        else if (name.contains("guard")) LootEditorGUI.openEditor(player, "guard");
    }
    
    private void handleLootEditClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        // Assuming title format "Loot Editor: <job>"
        // We need to extract the job name. 
        // Since the title might be localized, this is tricky.
        // But for now, let's assume the prefix is constant or we can infer.
        // The previous code used replace("Loot Editor: ", "").
        // Let's stick to that, but we must ensure the title in LootEditorGUI matches.
        // In LootEditorGUI: NPCHirelingSystem.getLang().getRaw("loot_gui_title").replace("%job%", job)
        // If "loot_gui_title" is "Loot Editor: %job%", then replace works.
        // If it's "Редактор лута: %job%", then replace("Loot Editor: ") fails.
        // I should probably fetch the raw title prefix from lang.
        
        String prefix = NPCHirelingSystem.getLang().getRaw("loot_gui_title").replace("%job%", "");
        String job = title.replace(prefix, "");
        
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            
            if (item.getType() == Material.ARROW) {
                LootEditorGUI.openJobSelector(player);
                return;
            }
            
            if (item.getType() != Material.PAPER && item.getType() != Material.AIR) {
                if (event.isLeftClick()) {
                    LootItemEditorGUI.open(player, job, item.getType());
                } else if (event.isRightClick()) {
                    NPCHirelingSystem.getLootManager().removeLootItem(job, item.getType());
                    LootEditorGUI.openEditor(player, job);
                }
            }
        } else {
            // Add item from player inventory
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() != Material.AIR) {
                NPCHirelingSystem.getLootManager().addLootItem(job, item.getType());
                LootEditorGUI.openEditor(player, job);
            }
        }
    }

    private void handleLootItemEditClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        String title = event.getView().getTitle();
        
        // "Edit Loot: <job> - <material>"
        String[] parts = title.replace("Edit Loot: ", "").split(" - ");
        if (parts.length < 2) return;
        String job = parts[0];
        String matName = parts[1];
        Material mat = Material.getMaterial(matName);
        if (mat == null) return;
        
        com.npchirelingsystem.managers.LootManager lootManager = NPCHirelingSystem.getLootManager();
        List<com.npchirelingsystem.managers.LootManager.LootItem> table = lootManager.getLootTable(job);
        com.npchirelingsystem.managers.LootManager.LootItem target = null;
        for (com.npchirelingsystem.managers.LootManager.LootItem li : table) {
            if (li.material == mat) {
                target = li;
                break;
            }
        }
        
        if (target == null) {
            LootEditorGUI.openEditor(player, job);
            return;
        }
        
        if (!item.hasItemMeta()) return;
        String name = item.getItemMeta().getDisplayName();
        
        if (name.contains("Min")) {
            if (name.contains("+1")) target.min++;
            else if (name.contains("-1")) target.min--;
            if (target.min < 1) target.min = 1;
            if (target.min > target.max) target.max = target.min;
        } else if (name.contains("Max")) {
            if (name.contains("+1")) target.max++;
            else if (name.contains("-1")) target.max--;
            if (target.max < target.min) target.max = target.min;
        } else if (name.contains("Chance")) {
            if (name.contains("+5%")) target.chance += 5.0;
            else if (name.contains("-5%")) target.chance -= 5.0;
            if (target.chance < 0) target.chance = 0;
            if (target.chance > 100) target.chance = 100;
        } else if (item.getType() == Material.ARROW) {
            LootEditorGUI.openEditor(player, job);
            return;
        }
        
        lootManager.updateLootItem(job, mat, target.min, target.max, target.chance);
        LootItemEditorGUI.open(player, job, mat);
    }

    private void handleNPCMenuClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            int slot = event.getSlot();
            if (slot >= 18) {
                event.setCancelled(true);
                
                Player player = (Player) event.getWhoClicked();
                HirelingNPC targetNPC = null;
                for (HirelingNPC npc : npcManager.getAllHirelings()) {
                    if (npc.getInventory().equals(event.getClickedInventory())) {
                        targetNPC = npc;
                        break;
                    }
                }
                
                if (targetNPC == null) return;

                if (slot == 24) { // Follow Toggle
                    targetNPC.toggleFollow();
                    player.sendMessage("§eNPC Follow Mode: " + (targetNPC.isFollowing() ? "§aEnabled" : "§cDisabled"));
                    player.openInventory(targetNPC.getInventory());
                } else if (slot == 20) { // Skill Tree
                    SkillTreeGUI.open(player, targetNPC);
                } else if (slot == 18) { // Special Skill
                    if (targetNPC.spendSkillPoint()) {
                        targetNPC.upgradeSpecialSkill();
                        player.sendMessage("§aUpgraded " + targetNPC.getSpecialSkillName() + "!");
                        // Refresh menu
                        player.openInventory(targetNPC.getInventory());
                    } else {
                        player.sendMessage("§cNot enough skill points!");
                    }
                } else if (slot == 26) { // Fire button
                    npcManager.fireNPC(targetNPC);
                    player.sendMessage(NPCHirelingSystem.getLang().get("fire_success"));
                    player.closeInventory();
                }
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
            npcManager.hireNPC(player, "MINER", wage);
        } else if (name.equals(guardName)) {
            double wage = config.getDouble("wages.guard", 15.0);
            npcManager.hireNPC(player, "GUARD", wage);
        } else if (name.equals(farmerName)) {
            double wage = config.getDouble("wages.farmer", 8.0);
            npcManager.hireNPC(player, "FARMER", wage);
        } else if (name.equals(NPCHirelingSystem.getLang().getRaw("npc_hunter"))) {
            double wage = config.getDouble("wages.hunter", 12.0);
            npcManager.hireNPC(player, "HUNTER", wage);
        } else if (name.equals(NPCHirelingSystem.getLang().getRaw("npc_lumberjack"))) {
            double wage = config.getDouble("wages.lumberjack", 9.0);
            npcManager.hireNPC(player, "LUMBERJACK", wage);
        } else if (name.equals(NPCHirelingSystem.getLang().getRaw("npc_fisherman"))) {
            double wage = config.getDouble("wages.fisherman", 9.0);
            npcManager.hireNPC(player, "FISHERMAN", wage);
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
        
        if (item.getType() == org.bukkit.Material.PLAYER_HEAD) {
            player.sendMessage("§cFeature to fire specific NPC via GUI is coming soon! Use command.");
        }
    }
}
