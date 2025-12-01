package com.npchirelingsystem.tasks;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.LootManager;
import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTask extends BukkitRunnable {

    private final NPCManager npcManager;
    private final LootManager lootManager;

    public LootTask(NPCManager npcManager, LootManager lootManager) {
        this.npcManager = npcManager;
        this.lootManager = lootManager;
    }

    @Override
    public void run() {
        List<HirelingNPC> allHirelings = npcManager.getAllHirelings();
        
        for (HirelingNPC npc : allHirelings) {
            Player owner = Bukkit.getPlayer(npc.getOwnerId());
            if (owner == null || !owner.isOnline()) continue;
            
            // Check global chance for job
            int chance = NPCHirelingSystem.getInstance().getConfig().getInt("jobs." + npc.getProfession().toLowerCase() + ".chance", 10);
            if (Math.random() * 100 > chance) continue;
            
            List<ItemStack> loot = lootManager.generateLoot(npc.getProfession().toLowerCase());
            if (loot.isEmpty()) continue;
            
            // Give loot
            Map<Integer, ItemStack> leftover = owner.getInventory().addItem(loot.toArray(new ItemStack[0]));
            
            // Drop leftover
            for (ItemStack item : leftover.values()) {
                owner.getWorld().dropItemNaturally(owner.getLocation(), item);
            }
            
            // Notify (optional, maybe too spammy)
            // owner.sendMessage("§aYour " + npc.getProfession() + " found some items!");
        }
    }
}
