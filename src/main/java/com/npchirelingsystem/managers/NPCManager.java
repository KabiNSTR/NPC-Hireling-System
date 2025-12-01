package com.npchirelingsystem.managers;

import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NPCManager {
    
    private final Map<UUID, List<HirelingNPC>> hirelings = new HashMap<>();

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
}
