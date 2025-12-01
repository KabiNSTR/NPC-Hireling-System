package com.npchirelingsystem.listeners;

import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerListener implements Listener {

    private final NPCManager npcManager;

    public PlayerListener(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        List<HirelingNPC> hirelings = npcManager.getHirelings(event.getPlayer().getUniqueId());
        for (HirelingNPC npc : hirelings) {
            npc.despawn();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        List<HirelingNPC> hirelings = npcManager.getHirelings(event.getPlayer().getUniqueId());
        for (HirelingNPC npc : hirelings) {
            if (npc.getLastLocation() != null) {
                npc.spawn(npc.getLastLocation());
            }
        }
    }
}
