package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ContractManager {

    private final Map<ContractCategory, List<Contract>> contracts = new HashMap<>();
    private final Map<UUID, Integer> playerReputation = new HashMap<>(); // 0 - 1000+
    private final Random random = new Random();
    private long lastRefresh = 0;
    private static final long REFRESH_COOLDOWN = 10 * 60 * 1000; // 10 minutes

    public enum ContractCategory {
        GATHERING, HUNTING, EXPLORATION, BOSS
    }

    public enum ContractType {
        ITEM_DELIVERY, MOB_KILL, BIOME_EXPLORE, BOSS_KILL
    }

    public enum ContractRarity {
        COMMON(ChatColor.GRAY, 1.0),
        UNCOMMON(ChatColor.GREEN, 1.5),
        RARE(ChatColor.BLUE, 2.5),
        EPIC(ChatColor.DARK_PURPLE, 4.0),
        LEGENDARY(ChatColor.GOLD, 8.0);

        public final ChatColor color;
        public final double multiplier;

        ContractRarity(ChatColor color, double multiplier) {
            this.color = color;
            this.multiplier = multiplier;
        }
    }

    public ContractManager() {
        refreshContracts();
    }

    public void refreshContracts() {
        contracts.clear();
        for (ContractCategory cat : ContractCategory.values()) {
            List<Contract> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) { // 5 options per category
                list.add(generateContract(cat));
            }
            contracts.put(cat, list);
        }
        lastRefresh = System.currentTimeMillis();
    }

    public List<Contract> getContracts(ContractCategory category) {
        if (System.currentTimeMillis() - lastRefresh > REFRESH_COOLDOWN) {
            refreshContracts();
        }
        return contracts.getOrDefault(category, new ArrayList<>());
    }

    public int getReputation(Player player) {
        return playerReputation.getOrDefault(player.getUniqueId(), 0);
    }

    public void addReputation(Player player, int amount) {
        playerReputation.put(player.getUniqueId(), getReputation(player) + amount);
    }

    private ContractRarity rollRarity(int reputation) {
        double roll = random.nextDouble() * 100;
        // Reputation increases chance of better tiers
        double bonus = reputation / 50.0; 

        if (roll < 1 + bonus / 2) return ContractRarity.LEGENDARY;
        if (roll < 5 + bonus) return ContractRarity.EPIC;
        if (roll < 15 + bonus * 2) return ContractRarity.RARE;
        if (roll < 40 + bonus * 3) return ContractRarity.UNCOMMON;
        return ContractRarity.COMMON;
    }

    private Contract generateContract(ContractCategory category) {
        ContractRarity rarity = rollRarity(0); // Default base rarity, could pass player rep if generating per-player
        // Since contracts are global currently, we use a base random. 
        // Ideally, we'd generate per player, but for this system let's keep it global with random rarities.
        rarity = rollRarity(random.nextInt(500)); // Simulate "average" reputation for global pool

        if (category == ContractCategory.GATHERING) {
            Material[] items = {Material.COBBLESTONE, Material.COAL, Material.WHEAT, Material.OAK_LOG, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND, Material.EMERALD, Material.OBSIDIAN, Material.BLAZE_ROD};
            Material mat = items[random.nextInt(items.length)];
            
            int baseAmount = 16;
            if (mat == Material.DIAMOND || mat == Material.EMERALD || mat == Material.BLAZE_ROD) baseAmount = 2;
            
            int amount = (int) (baseAmount * rarity.multiplier * (0.8 + random.nextDouble() * 0.4));
            double reward = amount * 5.0 * rarity.multiplier;
            
            return new Contract(ContractType.ITEM_DELIVERY, rarity, 
                    "Соберите " + amount + " " + mat.name().toLowerCase().replace("_", " "), 
                    mat, amount, reward, mat.name());

        } else if (category == ContractCategory.HUNTING) {
            String[] mobs = {"Zombie", "Skeleton", "Spider", "Creeper", "Enderman", "Witch", "Blaze"};
            String[] mobNames = {"Зомби", "Скелетов", "Пауков", "Криперов", "Эндерменов", "Ведьм", "Ифритов"};
            int idx = random.nextInt(mobs.length);
            String mob = mobs[idx];
            String mobName = mobNames[idx];
            
            int baseAmount = 5;
            int amount = (int) (baseAmount * rarity.multiplier);
            double reward = amount * 15.0 * rarity.multiplier;

            return new Contract(ContractType.MOB_KILL, rarity,
                    "Уничтожьте " + amount + " " + mobName,
                    null, amount, reward, mob);

        } else if (category == ContractCategory.EXPLORATION) {
            String[] biomes = {"Desert", "Forest", "Jungle", "Swamp", "Taiga", "Plains", "Savanna"};
            String[] biomeNames = {"Пустыню", "Лес", "Джунгли", "Болото", "Тайгу", "Равнины", "Саванну"};
            int idx = random.nextInt(biomes.length);
            String biome = biomes[idx];
            String biomeName = biomeNames[idx];
            double reward = 200.0 * rarity.multiplier;

            return new Contract(ContractType.BIOME_EXPLORE, rarity,
                    "Разведайте " + biomeName,
                    Material.COMPASS, 1, reward, biome);

        } else { // BOSS
            String[] bosses = {"Bandit King", "Corrupted Knight", "Shadow Assassin"};
            String[] bossNames = {"Короля Бандитов", "Порочного Рыцаря", "Теневого Ассасина"};
            int idx = random.nextInt(bosses.length);
            String boss = bosses[idx];
            String bossName = bossNames[idx];
            double reward = 1000.0 * rarity.multiplier;

            return new Contract(ContractType.BOSS_KILL, rarity,
                    "Победите " + bossName,
                    Material.WITHER_SKELETON_SKULL, 1, reward, boss);
        }
    }

    public void acceptContract(Player player, Contract contract) {
        if (contract.type == ContractType.ITEM_DELIVERY) {
            if (player.getInventory().containsAtLeast(new ItemStack(contract.material), contract.amount)) {
                player.getInventory().removeItem(new ItemStack(contract.material, contract.amount));
                NPCHirelingSystem.getEconomy().deposit(player.getUniqueId(), contract.reward);
                addReputation(player, (int) (5 * contract.rarity.multiplier));
                player.sendMessage(ChatColor.GREEN + "Контракт выполнен! Награда: " + ChatColor.GOLD + String.format("%.2f", contract.reward));
                player.sendMessage(ChatColor.AQUA + "+Репутация");
            } else {
                player.sendMessage(ChatColor.RED + "Вам нужно " + contract.amount + " " + contract.material.name() + ".");
            }
            return;
        } else {
            // All other types are quests
            NPCHirelingSystem.getQuestManager().startQuest(player, contract);
        }
        player.sendMessage(ChatColor.GREEN + "Контракт принят: " + contract.description);
    }

    public static class Contract {
        public ContractType type;
        public ContractRarity rarity;
        public String description;
        public Material material; // For delivery or icon
        public int amount;
        public double reward;
        public UUID id;
        public String target; // New field for logic (Biome name, Mob name, etc.)

        public Contract(ContractType type, ContractRarity rarity, String description, Material material, int amount, double reward) {
            this(type, rarity, description, material, amount, reward, null);
        }

        public Contract(ContractType type, ContractRarity rarity, String description, Material material, int amount, double reward, String target) {
            this.type = type;
            this.rarity = rarity;
            this.description = description;
            this.material = material;
            this.amount = amount;
            this.reward = reward;
            this.id = UUID.randomUUID();
            this.target = target;
        }
    }
}
