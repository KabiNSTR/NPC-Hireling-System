package com.npchirelingsystem.traits;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.models.HirelingNPC;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@TraitName("hirelingtrait")
public class HirelingTrait extends Trait {

    private HirelingNPC hireling;
    private int tickCounter = 0;

    public HirelingTrait() {
        super("hirelingtrait");
    }
    
    public void setHireling(HirelingNPC hireling) {
        this.hireling = hireling;
    }
    
    private void generatePassiveResources() {
        if (hireling == null) return;
        
        String prof = hireling.getProfession().toLowerCase();
        Material mat = null;
        
        if (prof.contains("miner")) mat = Material.COAL;
        else if (prof.contains("lumberjack")) mat = Material.OAK_LOG;
        else if (prof.contains("farmer")) mat = Material.WHEAT;
        else if (prof.contains("hunter") || prof.contains("guard")) mat = Material.ROTTEN_FLESH;
        
        if (mat != null) {
            // Calculate chance: Base 50% + 5% per upgrade
            int chance = 50 + (hireling.getDropRateUpgrade() * 5);
            if (java.util.concurrent.ThreadLocalRandom.current().nextInt(100) < chance) {
                // Special Skill: Double Drop
                int amount = 1;
                if (hireling.getSpecialSkillLevel() > 0) {
                    // 10% chance per level to double drop
                    if (java.util.concurrent.ThreadLocalRandom.current().nextInt(100) < (hireling.getSpecialSkillLevel() * 10)) {
                        amount = 2;
                    }
                }
                
                ItemStack item = new ItemStack(mat, amount);
                
                // Rare drop check
                if (hireling.getRareDropUpgrade() > 0) {
                    if (java.util.concurrent.ThreadLocalRandom.current().nextInt(100) < (hireling.getRareDropUpgrade() * 1)) {
                        if (mat == Material.COAL) item.setType(Material.DIAMOND);
                        if (mat == Material.ROTTEN_FLESH) item.setType(Material.GOLD_NUGGET);
                    }
                }
                
                hireling.getInventory().addItem(item);
                
                Player owner = Bukkit.getPlayer(hireling.getOwnerId());
                if (owner != null && owner.isOnline()) {
                    owner.sendMessage(com.npchirelingsystem.NPCHirelingSystem.getLang().get("npc_found_item")
                            .replace("%name%", hireling.getName())
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%item%", item.getType().name()));
                }
            }
        }
    }

    @Override
    public void run() {
        if (hireling == null) return;
        if (!this.getNPC().isSpawned()) return;

        tickCounter++;
        
        // Guard Combat Logic (Every 20 ticks)
        if (tickCounter % 20 == 0 && hireling.getProfession().equalsIgnoreCase("guard")) {
            handleGuardCombat();
        }

        // AI Logic: Follow Owner
        if (hireling.isFollowing()) {
            Player owner = Bukkit.getPlayer(hireling.getOwnerId());
            if (owner != null && owner.isOnline() && owner.getWorld().equals(this.getNPC().getEntity().getWorld())) {
                
                // Update target every 10 ticks to save performance
                if (tickCounter % 10 == 0) {
                    this.getNPC().getNavigator().setTarget(owner, false);
                }

                // Teleport if too far (stuck or owner flew away)
                if (this.getNPC().getEntity().getLocation().distanceSquared(owner.getLocation()) > 400) { // 20 blocks
                    this.getNPC().teleport(owner.getLocation(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
        } else {
            // AI Logic: Stationary / Working
            // Every 200 ticks (10 seconds), gain passive XP if stationary (simulating work)
            if (tickCounter % 200 == 0) {
                hireling.addXp(1);
            }
            
            // Passive Resource Generation (Every 60 seconds = 1200 ticks)
            if (tickCounter % 1200 == 0) {
                generatePassiveResources();
            }
            
            // Look at nearby players
            if (tickCounter % 20 == 0) {
                Player nearest = null;
                double nearestDist = 25; // 5 blocks squared
                
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld().equals(this.getNPC().getEntity().getWorld())) {
                        double dist = p.getLocation().distanceSquared(this.getNPC().getEntity().getLocation());
                        if (dist < nearestDist) {
                            nearest = p;
                            nearestDist = dist;
                        }
                    }
                }
                
                if (nearest != null) {
                    this.getNPC().faceLocation(nearest.getLocation());
                }
            }
        }
    }
    
    private void handleGuardCombat() {
        if (!this.getNPC().isSpawned()) return;
        
        // Scan for monsters within 10 blocks
        double range = 10.0;
        // Special Skill: Strength increases range
        if (hireling.getSpecialSkillLevel() > 0) {
            range += hireling.getSpecialSkillLevel() * 2;
        }
        
        Entity target = null;
        double closestDist = range * range;
        
        for (Entity e : this.getNPC().getEntity().getNearbyEntities(range, range, range)) {
            if (e instanceof Monster) {
                double dist = e.getLocation().distanceSquared(this.getNPC().getEntity().getLocation());
                if (dist < closestDist) {
                    closestDist = dist;
                    target = e;
                }
            }
        }
        
        if (target != null) {
            this.getNPC().getNavigator().setTarget(target, true);
            
            // Apply Strength Effect if level is high enough
            if (hireling.getSpecialSkillLevel() >= 3) {
                LivingEntity entity = (LivingEntity) this.getNPC().getEntity();
                entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0));
            }
        }
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (event.getNPC() == this.getNPC()) {
            if (hireling == null) return;
            
            Player player = event.getClicker();
            if (player.getUniqueId().equals(hireling.getOwnerId()) || player.hasPermission("npchirelingsystem.admin")) {
                player.openInventory(hireling.getInventory());
            } else {
                player.sendMessage(NPCHirelingSystem.getLang().get("not_your_hireling"));
            }
        }
    }
}
