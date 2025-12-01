package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.ContractManager.Contract;
import com.npchirelingsystem.managers.ContractManager.ContractType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
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
            player.sendMessage(ChatColor.RED + "Вы должны подождать " + remaining + "с перед началом нового контракта.");
            return;
        }

        if (activeQuests.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "У вас уже есть активный контракт!");
            return;
        }

        Location targetLoc = generateRandomLocation(player.getLocation());
        ActiveQuest quest = new ActiveQuest(contract, targetLoc);
        activeQuests.put(player.getUniqueId(), quest);

        if (contract.type == ContractType.BIOME_EXPLORE) {
            // Use the Russian description directly as it now contains the biome name
            player.sendMessage(ChatColor.GREEN + "Квест начат: " + contract.description);
        } else {
            player.sendMessage(NPCHirelingSystem.getLang().get("quest_go_to")
                    .replace("%x%", String.valueOf(targetLoc.getBlockX()))
                    .replace("%z%", String.valueOf(targetLoc.getBlockZ())));
        }
    }

    private Location generateRandomLocation(Location center) {
        double x = center.getX() + (Math.random() * 400 - 200);
        double z = center.getZ() + (Math.random() * 400 - 200);
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
                    if (quest.completed) continue;

                    if (quest.contract.type == ContractType.BIOME_EXPLORE) {
                        String biomeName = p.getLocation().getBlock().getBiome().name();
                        String targetBiome = quest.contract.target.toUpperCase().replace(" ", "_");
                        
                        if (biomeName.contains(targetBiome)) {
                            quest.completed = true;
                            p.sendMessage(ChatColor.GOLD + "Биом найден! Контракт выполнен! Вернитесь в меню, чтобы забрать награду.");
                        }
                    } else if (!quest.spawned) {
                        if (p.getLocation().distance(quest.targetLoc) < 30) {
                            spawnQuestMobs(quest);
                            quest.spawned = true;
                            p.sendMessage(ChatColor.YELLOW + "Цели обнаружены! Уничтожьте их!");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }

    private void spawnQuestMobs(ActiveQuest quest) {
        EntityType type = EntityType.ZOMBIE;
        int count = 3;
        String name = "Наемник-Отступник";
        double health = 20.0;
        double damage = 5.0;

        if (quest.contract.type == ContractType.BOSS_KILL) {
            type = EntityType.WITHER_SKELETON;
            count = 1;
            String bossName = quest.contract.target;
            if (bossName.equals("Bandit King")) bossName = "Король Бандитов";
            else if (bossName.equals("Corrupted Knight")) bossName = "Порочный Рыцарь";
            else if (bossName.equals("Shadow Assassin")) bossName = "Теневой Ассасин";

            name = ChatColor.RED + "☠ " + bossName + " ☠";
            health = 100.0 * quest.contract.rarity.multiplier;
            damage = 10.0 * quest.contract.rarity.multiplier;
        } else {
            // Scale normal mobs with rarity
            count = (int) (3 * quest.contract.rarity.multiplier);
            health = 20.0 * quest.contract.rarity.multiplier;
        }

        for (int i = 0; i < count; i++) {
            Location spawnLoc = quest.targetLoc.clone().add(Math.random()*6-3, 0, Math.random()*6-3);
            spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc) + 1);
            
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
            npc.spawn(spawnLoc);
            npc.setProtected(false);
            
            // Configure Sentinel if available
            Trait sentinel = CitizensAPI.getTraitFactory().getTrait("sentinel");
            if (sentinel != null) {
                npc.addTrait(sentinel);
                // We can't easily set Sentinel stats via API without casting to SentinelTrait which requires the jar
                // But we can set Bukkit attributes
            }
            
            if (npc.getEntity() instanceof org.bukkit.entity.LivingEntity) {
                org.bukkit.entity.LivingEntity le = (org.bukkit.entity.LivingEntity) npc.getEntity();
                le.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                le.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
                le.setHealth(health);
            }
            
            quest.questMobIds.put(npc.getUniqueId(), true);
        }
        quest.targetKillCount = count;
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
                        p.sendMessage(ChatColor.GREEN + "Цель устранена! (" + quest.killCount + "/" + quest.targetKillCount + ")");
                        if (quest.questMobIds.isEmpty()) {
                            quest.completed = true;
                            p.sendMessage(ChatColor.GOLD + "Контракт выполнен! Вернитесь в меню, чтобы забрать награду.");
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
            
            // Add Reputation
            plugin.getContractManager().addReputation(player, (int) (10 * quest.contract.rarity.multiplier));
            
            player.sendMessage(ChatColor.GOLD + "Вы получили " + String.format("%.2f", quest.contract.reward) + " монет!");
            player.sendMessage(ChatColor.AQUA + "+Репутация");
            
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
        int targetKillCount = 0;
        Map<UUID, Boolean> questMobIds = new HashMap<>();

        public ActiveQuest(Contract contract, Location targetLoc) {
            this.contract = contract;
            this.targetLoc = targetLoc;
        }
    }
}
