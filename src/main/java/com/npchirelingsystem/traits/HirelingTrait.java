package com.npchirelingsystem.traits;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.models.HirelingNPC;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

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
                ItemStack item = new ItemStack(mat, 1);
                
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
                    owner.sendMessage("§a[NPC] " + hireling.getName() + " found " + item.getType().name() + "!");
                }
            }
        }
    }

    @Override
    public void run() {
        if (hireling == null) return;
        if (!this.getNPC().isSpawned()) return;

        tickCounter++;

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
