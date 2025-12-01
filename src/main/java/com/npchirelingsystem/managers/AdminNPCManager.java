package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminNPCManager {

    private final NPCHirelingSystem plugin;
    private final List<UUID> adminNPCs = new ArrayList<>();
    private final File file;
    private FileConfiguration config;

    public AdminNPCManager(NPCHirelingSystem plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "admin_npcs.yml");
        loadNPCs();
    }

    public void createAdminNPC(Location loc, String name) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setProfession(Villager.Profession.LIBRARIAN);
        
        adminNPCs.add(villager.getUniqueId());
        saveNPCs();
    }

    public boolean isAdminNPC(Entity entity) {
        return adminNPCs.contains(entity.getUniqueId());
    }

    public void removeAdminNPC(Entity entity) {
        if (isAdminNPC(entity)) {
            adminNPCs.remove(entity.getUniqueId());
            entity.remove();
            saveNPCs();
        }
    }

    private void saveNPCs() {
        config = new YamlConfiguration();
        List<String> list = new ArrayList<>();
        for (UUID uuid : adminNPCs) {
            list.add(uuid.toString());
        }
        config.set("npcs", list);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNPCs() {
        if (!file.exists()) return;
        config = YamlConfiguration.loadConfiguration(file);
        List<String> list = config.getStringList("npcs");
        for (String s : list) {
            try {
                adminNPCs.add(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                // Ignore invalid UUIDs
            }
        }
    }
    
    public void despawnAll() {
        for (UUID uuid : adminNPCs) {
            Entity e = Bukkit.getEntity(uuid);
            if (e != null) e.remove();
        }
    }
}
