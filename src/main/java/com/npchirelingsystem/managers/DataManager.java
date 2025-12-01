package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {

    private final NPCHirelingSystem plugin;
    private File file;
    private FileConfiguration config;

    public DataManager(NPCHirelingSystem plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "hirelings.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create hirelings.yml", e);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveHirelings(Map<UUID, List<HirelingNPC>> hirelings) {
        config.set("hirelings", null); // Clear old data

        for (Map.Entry<UUID, List<HirelingNPC>> entry : hirelings.entrySet()) {
            String ownerId = entry.getKey().toString();
            List<Map<String, Object>> npcList = new ArrayList<>();

            for (HirelingNPC npc : entry.getValue()) {
                Map<String, Object> npcData = new HashMap<>();
                npcData.put("name", npc.getName());
                npcData.put("profession", npc.getProfession());
                npcData.put("wage", npc.getWage());
                npcData.put("location", npc.getLastLocation());
                npcData.put("inventory", npc.getInventory().getContents());
                
                // Skill Tree Data
                npcData.put("level", npc.getLevel());
                npcData.put("xp", npc.getXp());
                npcData.put("skillPoints", npc.getSkillPoints());
                npcData.put("dropRateUpgrade", npc.getDropRateUpgrade());
                npcData.put("rareDropUpgrade", npc.getRareDropUpgrade());
                npcData.put("specialSkillLevel", npc.getSpecialSkillLevel());
                
                npcList.add(npcData);
            }
            
            config.set("hirelings." + ownerId, npcList);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save hirelings.yml", e);
        }
    }

    public Map<UUID, List<HirelingNPC>> loadHirelings() {
        Map<UUID, List<HirelingNPC>> hirelings = new HashMap<>();
        
        if (!config.contains("hirelings")) return hirelings;

        ConfigurationSection section = config.getConfigurationSection("hirelings");
        if (section == null) return hirelings;

        for (String ownerIdStr : section.getKeys(false)) {
            try {
                UUID ownerId = UUID.fromString(ownerIdStr);
                List<Map<?, ?>> npcList = section.getMapList(ownerIdStr);
                List<HirelingNPC> npcs = new ArrayList<>();

                for (Map<?, ?> npcData : npcList) {
                    String name = (String) npcData.get("name");
                    String profession = (String) npcData.get("profession");
                    double wage = (Double) npcData.get("wage");
                    Location location = (Location) npcData.get("location");
                    
                    HirelingNPC npc = new HirelingNPC(ownerId, name, profession, wage, location);
                    
                    // Load Skill Tree Data
                    if (npcData.containsKey("level")) npc.setLevel((int) npcData.get("level"));
                    if (npcData.containsKey("xp")) npc.setXp((int) npcData.get("xp"));
                    if (npcData.containsKey("skillPoints")) npc.setSkillPoints((int) npcData.get("skillPoints"));
                    if (npcData.containsKey("dropRateUpgrade")) npc.setDropRateUpgrade((int) npcData.get("dropRateUpgrade"));
                    if (npcData.containsKey("rareDropUpgrade")) npc.setRareDropUpgrade((int) npcData.get("rareDropUpgrade"));
                    if (npcData.containsKey("specialSkillLevel")) npc.setSpecialSkillLevel((int) npcData.get("specialSkillLevel"));
                    
                    if (npcData.containsKey("inventory")) {
                        List<ItemStack> invList = (List<ItemStack>) npcData.get("inventory");
                        if (invList != null) {
                            npc.setInventoryContents(invList.toArray(new ItemStack[0]));
                        }
                    }
                    
                    npcs.add(npc);
                }
                
                hirelings.put(ownerId, npcs);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error loading hirelings for " + ownerIdStr, e);
            }
        }
        
        return hirelings;
    }
}
