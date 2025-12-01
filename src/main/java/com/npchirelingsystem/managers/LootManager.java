package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class LootManager {

    private final NPCHirelingSystem plugin;
    private final Random random = new Random();

    public LootManager(NPCHirelingSystem plugin) {
        this.plugin = plugin;
        loadDefaults();
    }

    private void loadDefaults() {
        FileConfiguration config = plugin.getConfig();
        if (!config.contains("jobs.miner.items")) {
            addDefault("miner", Material.COAL, 1, 5, 50.0);
            addDefault("miner", Material.RAW_IRON, 1, 3, 30.0);
            addDefault("miner", Material.COBBLESTONE, 5, 16, 80.0);
            addDefault("miner", Material.DIAMOND, 1, 1, 1.0);
        }
        if (!config.contains("jobs.farmer.items")) {
            addDefault("farmer", Material.WHEAT, 2, 8, 60.0);
            addDefault("farmer", Material.CARROT, 2, 6, 50.0);
            addDefault("farmer", Material.POTATO, 2, 6, 50.0);
        }
        if (!config.contains("jobs.hunter.items")) {
            addDefault("hunter", Material.LEATHER, 1, 3, 40.0);
            addDefault("hunter", Material.BEEF, 1, 3, 40.0);
            addDefault("hunter", Material.PORKCHOP, 1, 3, 40.0);
            addDefault("hunter", Material.FEATHER, 1, 5, 50.0);
        }
        if (!config.contains("jobs.lumberjack.items")) {
            addDefault("lumberjack", Material.OAK_LOG, 2, 8, 70.0);
            addDefault("lumberjack", Material.STICK, 1, 5, 50.0);
            addDefault("lumberjack", Material.APPLE, 1, 2, 10.0);
        }
        if (!config.contains("jobs.fisherman.items")) {
            addDefault("fisherman", Material.COD, 1, 3, 60.0);
            addDefault("fisherman", Material.SALMON, 1, 2, 40.0);
            addDefault("fisherman", Material.TROPICAL_FISH, 1, 1, 10.0);
        }
        if (!config.contains("jobs.guard.items")) {
            addDefault("guard", Material.ROTTEN_FLESH, 1, 3, 30.0);
            addDefault("guard", Material.BONE, 1, 2, 20.0);
            addDefault("guard", Material.ARROW, 1, 5, 20.0);
        }
    }

    private void addDefault(String job, Material mat, int min, int max, double chance) {
        String path = "jobs." + job + ".items." + mat.name();
        plugin.getConfig().set(path + ".min", min);
        plugin.getConfig().set(path + ".max", max);
        plugin.getConfig().set(path + ".chance", chance);
        plugin.saveConfig();
    }

    public static class LootItem {
        public Material material;
        public int min;
        public int max;
        public double chance;

        public LootItem(Material material, int min, int max, double chance) {
            this.material = material;
            this.min = min;
            this.max = max;
            this.chance = chance;
        }
    }

    public List<LootItem> getLootTable(String profession) {
        List<LootItem> lootList = new ArrayList<>();
        FileConfiguration config = plugin.getConfig();
        String path = "jobs." + profession + ".items";

        if (config.isConfigurationSection(path)) {
            ConfigurationSection section = config.getConfigurationSection(path);
            for (String key : section.getKeys(false)) {
                Material mat = Material.getMaterial(key);
                if (mat != null) {
                    int min = section.getInt(key + ".min", 1);
                    int max = section.getInt(key + ".max", 1);
                    double chance = section.getDouble(key + ".chance", 50.0);
                    lootList.add(new LootItem(mat, min, max, chance));
                }
            }
        }
        return lootList;
    }

    public void addLootItem(String profession, Material mat) {
        FileConfiguration config = plugin.getConfig();
        String path = "jobs." + profession + ".items." + mat.name();
        config.set(path + ".min", 1);
        config.set(path + ".max", 3);
        config.set(path + ".chance", 50.0);
        plugin.saveConfig();
    }

    public void removeLootItem(String profession, Material mat) {
        FileConfiguration config = plugin.getConfig();
        String path = "jobs." + profession + ".items." + mat.name();
        config.set(path, null);
        plugin.saveConfig();
    }
    
    public void updateLootItem(String profession, Material mat, int min, int max, double chance) {
        FileConfiguration config = plugin.getConfig();
        String path = "jobs." + profession + ".items." + mat.name();
        config.set(path + ".min", min);
        config.set(path + ".max", max);
        config.set(path + ".chance", chance);
        plugin.saveConfig();
    }

    public List<ItemStack> generateLoot(String profession) {
        List<ItemStack> drops = new ArrayList<>();
        List<LootItem> table = getLootTable(profession);

        for (LootItem item : table) {
            if (random.nextDouble() * 100 <= item.chance) {
                int amount = item.min + random.nextInt(item.max - item.min + 1);
                if (amount > 0) {
                    drops.add(new ItemStack(item.material, amount));
                }
            }
        }
        return drops;
    }
}
