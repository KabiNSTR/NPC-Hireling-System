package com.npchirelingsystem.tasks;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class JobTask extends BukkitRunnable {

    private final NPCManager npcManager;
    private final Random random = new Random();

    public JobTask(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public void run() {
        for (HirelingNPC npc : npcManager.getAllHirelings()) {
            if (npc.getEntityUuid() == null) continue;
            
            Entity entity = Bukkit.getEntity(npc.getEntityUuid());
            if (entity == null || !entity.isValid()) continue;

            switch (npc.getProfession().toUpperCase()) {
                case "FARMER":
                case "SHEPHERD":
                case "FISHERMAN":
                    doFarming(npc);
                    break;
                case "ARMORER":
                case "TOOLSMITH":
                case "WEAPONSMITH":
                case "MASON":
                    doMining(npc);
                    break;
                case "BUTCHER":
                case "LEATHERWORKER":
                    doHunting(npc, entity);
                    break;
                default:
                    // Guards or others
                    doGuarding(npc, entity);
                    break;
            }
        }
    }

    private void addItemToStorage(HirelingNPC npc, ItemStack item) {
        // Try to add to slots 0-17
        for (int i = 0; i < 18; i++) {
            ItemStack slot = npc.getInventory().getItem(i);
            if (slot == null || slot.getType() == Material.AIR) {
                npc.getInventory().setItem(i, item);
                return;
            } else if (slot.isSimilar(item) && slot.getAmount() < slot.getMaxStackSize()) {
                int space = slot.getMaxStackSize() - slot.getAmount();
                if (space >= item.getAmount()) {
                    slot.setAmount(slot.getAmount() + item.getAmount());
                    return;
                } else {
                    slot.setAmount(slot.getMaxStackSize());
                    item.setAmount(item.getAmount() - space);
                    // Continue to next slot with remaining amount
                }
            }
        }
    }

    private void doFarming(HirelingNPC npc) {
        int chance = NPCHirelingSystem.getInstance().getConfig().getInt("jobs.farmer.chance", 10);
        if (random.nextInt(100) < chance) { 
            List<String> items = NPCHirelingSystem.getInstance().getConfig().getStringList("jobs.farmer.items");
            if (items.isEmpty()) items = List.of("WHEAT", "CARROT", "POTATO");
            
            String matName = items.get(random.nextInt(items.size()));
            Material mat = Material.getMaterial(matName);
            if (mat != null) {
                addItemToStorage(npc, new ItemStack(mat, 1));
            }
        }
    }

    private void doMining(HirelingNPC npc) {
        int chance = NPCHirelingSystem.getInstance().getConfig().getInt("jobs.miner.chance", 5);
        if (random.nextInt(100) < chance) { 
            List<String> items = NPCHirelingSystem.getInstance().getConfig().getStringList("jobs.miner.items");
            if (items.isEmpty()) items = List.of("COBBLESTONE", "COAL");

            String matName = items.get(random.nextInt(items.size()));
            Material mat = Material.getMaterial(matName);
            if (mat != null) {
                addItemToStorage(npc, new ItemStack(mat, 1));
            }
        }
    }
    
    private void doHunting(HirelingNPC npc, Entity entity) {
         int chance = NPCHirelingSystem.getInstance().getConfig().getInt("jobs.hunter.chance", 5);
         if (random.nextInt(100) < chance) {
             List<String> items = NPCHirelingSystem.getInstance().getConfig().getStringList("jobs.hunter.items");
             if (items.isEmpty()) items = List.of("BEEF", "PORKCHOP");

             String matName = items.get(random.nextInt(items.size()));
             Material mat = Material.getMaterial(matName);
             if (mat != null) {
                 addItemToStorage(npc, new ItemStack(mat, 1));
             }
         }
    }

    private void doGuarding(HirelingNPC npc, Entity entity) {
        List<Entity> nearby = entity.getNearbyEntities(10, 5, 10);
        for (Entity target : nearby) {
            if (target instanceof Monster) {
                ((LivingEntity) target).damage(5.0, entity); // Deal 2.5 hearts damage
                // Play effect?
                entity.getWorld().spawnParticle(org.bukkit.Particle.CRIT, target.getLocation().add(0, 1, 0), 5);
                break; // Attack one at a time
            }
        }
    }
}
