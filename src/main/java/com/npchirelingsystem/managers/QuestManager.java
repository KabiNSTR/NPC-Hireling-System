package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestManager {

    private final NPCHirelingSystem plugin;
    private final Map<UUID, ActiveQuest> activeQuests = new HashMap<>();

    public QuestManager(NPCHirelingSystem plugin) {
        this.plugin = plugin;
        startQuestTicker();
    }

    public void startKillQuest(Player player, Location targetLoc, int mobCount) {
        ActiveQuest quest = new ActiveQuest(QuestType.KILL, targetLoc);
        quest.mobCount = mobCount;
        activeQuests.put(player.getUniqueId(), quest);
        player.sendMessage("§e[Quest] §fGo to coordinates: X:" + targetLoc.getBlockX() + " Z:" + targetLoc.getBlockZ() + " to find the monsters!");
    }

    public void startLegendaryQuest(Player player, Location targetLoc) {
        ActiveQuest quest = new ActiveQuest(QuestType.LEGENDARY, targetLoc);
        activeQuests.put(player.getUniqueId(), quest);
        player.sendMessage("§6[Legendary] §fDeliver the item to the mysterious contact at X:" + targetLoc.getBlockX() + " Z:" + targetLoc.getBlockZ());
    }

    private void startQuestTicker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, ActiveQuest> entry : activeQuests.entrySet()) {
                    Player p = Bukkit.getPlayer(entry.getKey());
                    if (p == null || !p.isOnline()) continue;
                    
                    ActiveQuest quest = entry.getValue();
                    if (quest.completed || quest.stage != 0) continue; // Already spawned or done

                    if (p.getLocation().distance(quest.targetLoc) < 20) {
                        // Player arrived
                        if (quest.type == QuestType.KILL) {
                            spawnQuestMobs(quest);
                        } else if (quest.type == QuestType.LEGENDARY) {
                            spawnLegendaryNPC(quest);
                        }
                        quest.stage = 1; // Mobs/NPC spawned
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 100L); // Check every 5s
    }

    private void spawnQuestMobs(ActiveQuest quest) {
        for (int i = 0; i < quest.mobCount; i++) {
            quest.targetLoc.getWorld().spawnEntity(quest.targetLoc.clone().add(Math.random()*5, 1, Math.random()*5), EntityType.ZOMBIE);
        }
        // In a real system we would track these specific entities to know when they die.
        // For simplicity, we'll just say "Kill any zombies nearby" or assume they did it.
        // Let's auto-complete for now after 1 minute or require them to return.
        quest.completed = true; // Simplified: Arriving spawns them, now go back.
    }

    private void spawnLegendaryNPC(ActiveQuest quest) {
        Zombie npc = (Zombie) quest.targetLoc.getWorld().spawnEntity(quest.targetLoc, EntityType.ZOMBIE);
        npc.setCustomName("§6Mysterious Contact");
        npc.setCustomNameVisible(true);
        npc.setAI(false);
        // Despawn after 2 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.isValid()) npc.remove();
            }
        }.runTaskLater(plugin, 2400L);
    }

    public enum QuestType { KILL, LEGENDARY }

    public static class ActiveQuest {
        QuestType type;
        Location targetLoc;
        int mobCount;
        int stage = 0; // 0 = traveling, 1 = arrived/fighting, 2 = done
        boolean completed = false;

        public ActiveQuest(QuestType type, Location targetLoc) {
            this.type = type;
            this.targetLoc = targetLoc;
        }
    }
    
    public boolean hasActiveQuest(Player player) {
        return activeQuests.containsKey(player.getUniqueId());
    }
    
    public void completeQuest(Player player) {
        activeQuests.remove(player.getUniqueId());
    }
}
