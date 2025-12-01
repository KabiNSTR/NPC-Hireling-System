package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NPCManager {
    
    private final Map<UUID, List<HirelingNPC>> hirelings = new HashMap<>();
    private final DataManager dataManager;

    public NPCManager(NPCHirelingSystem plugin) {
        this.dataManager = new DataManager(plugin);
    }

    public void hireNPC(Player player, String profession, double wage) {
        HirelingNPC npc = new HirelingNPC(player.getUniqueId(), player.getName() + "'s Hireling", profession, wage);
        npc.spawn(player.getLocation());
        
        hirelings.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(npc);
        
        String msg = com.npchirelingsystem.NPCHirelingSystem.getLang().get("hired_message")
                .replace("%profession%", profession)
                .replace("%wage%", String.valueOf(wage));
        player.sendMessage(msg);
    }

    public void fireNPC(HirelingNPC npc) {
        npc.despawn();
        List<HirelingNPC> list = hirelings.get(npc.getOwnerId());
        if (list != null) {
            list.remove(npc);
        }
    }

    public List<HirelingNPC> getAllHirelings() {
        List<HirelingNPC> all = new ArrayList<>();
        for (List<HirelingNPC> list : hirelings.values()) {
            all.addAll(list);
        }
        return all;
    }
    
    public List<HirelingNPC> getHirelings(UUID ownerId) {
        return hirelings.getOrDefault(ownerId, new ArrayList<>());
    }
    
    public void saveAll() {
        // Despawn all to update locations and remove entities
        for (List<HirelingNPC> list : hirelings.values()) {
            for (HirelingNPC npc : list) {
                npc.despawn(); // This updates lastLocation inside the object
            }
        }
        dataManager.saveHirelings(hirelings);
    }
    
    public void loadAll() {
        Map<UUID, List<HirelingNPC>> loaded = dataManager.loadHirelings();
        this.hirelings.clear();
        this.hirelings.putAll(loaded);
        
        // Respawn them
        for (List<HirelingNPC> list : hirelings.values()) {
            for (HirelingNPC npc : list) {
                // Only spawn if we have a valid location
                if (npc.getLastLocation() != null) {
                    npc.spawn(npc.getLastLocation());
                }
            }
        }
    }
}
