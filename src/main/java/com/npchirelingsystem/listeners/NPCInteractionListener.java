package com.npchirelingsystem.listeners;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class NPCInteractionListener implements Listener {

    private final NPCManager npcManager;

    public NPCInteractionListener(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        for (HirelingNPC npc : npcManager.getAllHirelings()) {
            if (npc.getEntityUuid() != null && npc.getEntityUuid().equals(entity.getUniqueId())) {
                // Allow targeting owner for "Follow" mechanic
                if (event.getTarget() instanceof Player) {
                    Player target = (Player) event.getTarget();
                    if (target.getUniqueId().equals(npc.getOwnerId()) && npc.isFollowing()) {
                        return; // Allow
                    }
                }
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        for (HirelingNPC npc : npcManager.getAllHirelings()) {
            if (npc.getEntityUuid() != null && npc.getEntityUuid().equals(damager.getUniqueId())) {
                event.setCancelled(true); // Prevent NPC from hurting anyone
                return;
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        // Check if this entity is a hireling
        for (HirelingNPC npc : npcManager.getAllHirelings()) {
            if (npc.getEntityUuid() != null && npc.getEntityUuid().equals(entity.getUniqueId())) {
                event.setCancelled(true);
                
                // Check ownership
                if (!npc.getOwnerId().equals(player.getUniqueId()) && !player.hasPermission("npchirelingsystem.admin")) {
                    player.sendMessage(NPCHirelingSystem.getLang().get("not_your_hireling"));
                    return;
                }
                
                // Open Inventory
                player.openInventory(npc.getInventory());
                return;
            }
        }
    }
}
