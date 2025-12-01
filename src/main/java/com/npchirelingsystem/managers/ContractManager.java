package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ContractManager {

    private final List<Contract> dailyContracts = new ArrayList<>();
    private final Random random = new Random();
    private long lastRefresh = 0;
    private static final long REFRESH_COOLDOWN = 10 * 60 * 1000; // 10 minutes

    public ContractManager() {
        refreshContracts();
    }

    public void refreshContracts() {
        dailyContracts.clear();
        // Generate 3 random contracts
        for (int i = 0; i < 3; i++) {
            dailyContracts.add(generateRandomContract());
        }
        lastRefresh = System.currentTimeMillis();
    }

    public List<Contract> getContracts() {
        if (System.currentTimeMillis() - lastRefresh > REFRESH_COOLDOWN) {
            refreshContracts();
        }
        return dailyContracts;
    }

    private Contract generateRandomContract() {
        // List of possible items to request (resources that NPCs gather)
        Material[] possibleItems = {
            Material.COBBLESTONE, Material.COAL, Material.RAW_IRON, Material.RAW_COPPER,
            Material.WHEAT, Material.CARROT, Material.POTATO, Material.BEETROOT,
            Material.BEEF, Material.PORKCHOP, Material.CHICKEN, Material.LEATHER,
            Material.OAK_LOG, Material.BIRCH_LOG, Material.STICK, Material.APPLE,
            Material.COD, Material.SALMON
        };

        Material mat = possibleItems[random.nextInt(possibleItems.length)];
        int amount = 16 + random.nextInt(49); // 16 to 64
        double reward = (amount * 0.5) + random.nextInt(50); // Basic pricing logic

        return new Contract(mat, amount, reward);
    }

    public boolean completeContract(Player player, Contract contract) {
        if (!dailyContracts.contains(contract)) return false;

        ItemStack required = new ItemStack(contract.getMaterial(), contract.getAmount());
        if (player.getInventory().containsAtLeast(required, contract.getAmount())) {
            player.getInventory().removeItem(required);
            NPCHirelingSystem.getEconomy().deposit(player.getUniqueId(), contract.getReward());
            
            player.sendMessage("§aContract completed! Received " + contract.getReward() + " coins.");
            dailyContracts.remove(contract); // Remove completed contract
            dailyContracts.add(generateRandomContract()); // Add a new one immediately? Or wait? Let's add new one.
            return true;
        } else {
            player.sendMessage("§cYou don't have enough items!");
            return false;
        }
    }

    public static class Contract {
        private final Material material;
        private final int amount;
        private final double reward;
        private final UUID id;

        public Contract(Material material, int amount, double reward) {
            this.material = material;
            this.amount = amount;
            this.reward = reward;
            this.id = UUID.randomUUID();
        }

        public Material getMaterial() { return material; }
        public int getAmount() { return amount; }
        public double getReward() { return reward; }
        public UUID getId() { return id; }
    }
}
