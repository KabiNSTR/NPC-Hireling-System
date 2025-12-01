package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.ContractManager.Contract;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestManager implements Listener {

    private final NPCHirelingSystem plugin;
    private final Map<UUID, ActiveQuest> activeQuests = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 300000; // 5 minutes

    public QuestManager(NPCHirelingSystem plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startQuestTicker();
    }

    public void startQuest(Player player, Contract contract) {
        if (isOnCooldown(player)) {
            long remaining = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
            player.sendMessage(ChatColor.RED + "You must wait " + remaining + "s before starting another contract.");
            return;
        }

        if (activeQuests.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already have an active contract!");
            return;
        }

        Location targetLoc = generateRandomLocation(player.getLocation());
        ActiveQuest quest = new ActiveQuest(contract, targetLoc);
        activeQuests.put(player.getUniqueId(), quest);

        player.sendMessage(NPCHirelingSystem.getLang().get("quest_go_to")
                .replace("%x%", String.valueOf(targetLoc.getBlockX()))
                .replace("%z%", String.valueOf(targetLoc.getBlockZ())));
    }

    private Location generateRandomLocation(Location center) {
        double x = center.getX() + (Math.random() * 200 - 100);
        double z = center.getZ() + (Math.random() * 200 - 100);
        Location loc = new Location(center.getWorld(), x, center.getWorld().getHighestBlockYAt((int)x, (int)z) + 1, z);
        return loc;
    }

    private void startQuestTicker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, ActiveQuest> entry : activeQuests.entrySet()) {
                    Player p = Bukkit.getPlayer(entry.getKey());
                    if (p == null || !p.isOnline()) continue;
                    
                    ActiveQuest quest = entry.getValue();
                    if (quest.spawned || quest.completed) continue;

                    if (p.getLocation().distance(quest.targetLoc) < 30) {
                        spawnQuestMobs(quest);
                        quest.spawned = true;
                        p.sendMessage(ChatColor.YELLOW + "Targets spotted! Eliminate them!");
                    }
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }

    private void spawnQuestMobs(ActiveQuest quest) {
        EntityType type = EntityType.ZOMBIE; // Default
        if (quest.contract.type == ContractManager.ContractType.MOB_KILL) {
            // Could map contract description to entity types
        }

        for (int i = 0; i < 3; i++) { // Spawn 3 enemies
            Location spawnLoc = quest.targetLoc.clone().add(Math.random()*6-3, 0, Math.random()*6-3);
            spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc) + 1);
            
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, "Rogue Mercenary");
            npc.spawn(spawnLoc);
            npc.setProtected(false);
            
            // Add Sentinel trait if available
            if (CitizensAPI.getTraitFactory().getTrait("sentinel") != null) {
                npc.addTrait(CitizensAPI.getTraitFactory().getTrait("sentinel"));
                // We would configure sentinel here (set targets, etc)
                // For now, we assume default sentinel or manual configuration isn't fully automated without the jar dependency
            }
            
            quest.questMobIds.put(npc.getUniqueId(), true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata("NPC")) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
            if (npc == null) return;

            for (Map.Entry<UUID, ActiveQuest> entry : activeQuests.entrySet()) {
                ActiveQuest quest = entry.getValue();
                if (quest.questMobIds.containsKey(npc.getUniqueId())) {
                    quest.questMobIds.remove(npc.getUniqueId());
                    quest.killCount++;
                    
                    Player p = Bukkit.getPlayer(entry.getKey());
                    if (p != null) {
                        p.sendMessage(ChatColor.GREEN + "Target eliminated! (" + quest.killCount + "/3)");
                        if (quest.questMobIds.isEmpty()) {
                            quest.completed = true;
                            p.sendMessage(ChatColor.GOLD + "Contract Complete! Return to the menu to claim your reward.");
                        }
                    }
                    break;
                }
            }
        }
    }

    public boolean hasActiveQuest(Player player) {
        return activeQuests.containsKey(player.getUniqueId());
    }

    public boolean isQuestCompleted(Player player) {
        ActiveQuest quest = activeQuests.get(player.getUniqueId());
        return quest != null && quest.completed;
    }

    public void claimReward(Player player) {
        ActiveQuest quest = activeQuests.get(player.getUniqueId());
        if (quest != null && quest.completed) {
            NPCHirelingSystem.getEconomy().deposit(player.getUniqueId(), quest.contract.reward);
            player.sendMessage(ChatColor.GOLD + "You received " + quest.contract.reward + " coins!");
            activeQuests.remove(player.getUniqueId());
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_TIME);
        }
    }
    
    public boolean isOnCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId()) && cooldowns.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    public static class ActiveQuest {
        Contract contract;
        Location targetLoc;
        boolean spawned = false;
        boolean completed = false;
        int killCount = 0;
        Map<UUID, Boolean> questMobIds = new HashMap<>();

        public ActiveQuest(Contract contract, Location targetLoc) {
            this.contract = contract;
            this.targetLoc = targetLoc;
        }
    }
}
