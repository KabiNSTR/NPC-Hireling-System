package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminNPCManager {

    private final NPCHirelingSystem plugin;
    private final List<Integer> adminNPCIds = new ArrayList<>();
    private final File file;
    private FileConfiguration config;

    public AdminNPCManager(NPCHirelingSystem plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "admin_npcs.yml");
        loadNPCs();
    }

    public void createAdminNPC(Location loc, String name) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, name);
        npc.addTrait(com.npchirelingsystem.traits.QuestGiverTrait.class);
        npc.spawn(loc);
        
        adminNPCIds.add(npc.getId());
        saveNPCs();
    }

    public boolean isAdminNPC(Entity entity) {
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
        return npc != null && adminNPCIds.contains(npc.getId());
    }

    public void removeAdminNPC(Entity entity) {
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
        if (npc != null && adminNPCIds.contains(npc.getId())) {
            adminNPCIds.remove((Integer) npc.getId());
            npc.destroy();
            saveNPCs();
        }
    }

    private void saveNPCs() {
        config = new YamlConfiguration();
        config.set("npcs", adminNPCIds);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNPCs() {
        if (!file.exists()) return;
        config = YamlConfiguration.loadConfiguration(file);
        adminNPCIds.clear();
        adminNPCIds.addAll(config.getIntegerList("npcs"));
    }
    
    public void despawnAll() {
        // We don't destroy them, just let Citizens handle them.
        // Or we can despawn them if we want them to disappear on disable.
        for (Integer id : adminNPCIds) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(id);
            if (npc != null) {
                npc.despawn();
            }
        }
    }
}
