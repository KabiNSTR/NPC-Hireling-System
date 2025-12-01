package com.npchirelingsystem.managers;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
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
    private final Random random = new Random();
    private long lastRefresh = 0;
    private static final long REFRESH_COOLDOWN = 10 * 60 * 1000; // 10 minutes

    public enum ContractCategory {
        GATHERING, HUNTING, LEGENDARY
    }

    public enum ContractType {
        ITEM_DELIVERY, MOB_KILL, LEGENDARY_DELIVERY
    }

    public ContractManager() {
        refreshContracts();
    }

    public void refreshContracts() {
        contracts.clear();
        for (ContractCategory cat : ContractCategory.values()) {
            List<Contract> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
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

    private Contract generateContract(ContractCategory category) {
        if (category == ContractCategory.GATHERING) {
            Material[] items = {Material.COBBLESTONE, Material.COAL, Material.WHEAT, Material.OAK_LOG, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND, Material.EMERALD};
            Material mat = items[random.nextInt(items.length)];
            int amount = 16 + random.nextInt(48);
            if (mat == Material.DIAMOND || mat == Material.EMERALD) amount = 1 + random.nextInt(5);
            
            double reward = amount * 5.0;
            if (mat == Material.DIAMOND) reward *= 20;
            if (mat == Material.EMERALD) reward *= 25;
            if (mat == Material.GOLD_ORE) reward *= 5;
            
            return new Contract(ContractType.ITEM_DELIVERY, NPCHirelingSystem.getLang().getRaw("contract_desc_gather")
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%item%", mat.name()), mat, amount, reward);
        } else if (category == ContractCategory.HUNTING) {
            String[] mobs = {"Zombie", "Skeleton", "Spider", "Creeper", "Enderman"};
            String mob = mobs[random.nextInt(mobs.length)];
            int amount = 5 + random.nextInt(15);
            return new Contract(ContractType.MOB_KILL, NPCHirelingSystem.getLang().getRaw("contract_desc_kill")
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%mob%", mob), null, amount, amount * 25.0);
        } else {
            return new Contract(ContractType.LEGENDARY_DELIVERY, NPCHirelingSystem.getLang().getRaw("contract_desc_legendary"), Material.PAPER, 1, 1000.0 + random.nextInt(2000));
        }
    }

    public void acceptContract(Player player, Contract contract) {
        if (contract.type == ContractType.ITEM_DELIVERY) {
            if (player.getInventory().containsAtLeast(new ItemStack(contract.material), contract.amount)) {
                player.getInventory().removeItem(new ItemStack(contract.material, contract.amount));
                NPCHirelingSystem.getEconomy().deposit(player.getUniqueId(), contract.reward);
                player.sendMessage(NPCHirelingSystem.getLang().get("contract_completed").replace("%reward%", String.valueOf(contract.reward)));
            } else {
                player.sendMessage(NPCHirelingSystem.getLang().get("contract_missing_items")
                        .replace("%amount%", String.valueOf(contract.amount))
                        .replace("%item%", contract.material.name()));
            }
            return;
        } else if (contract.type == ContractType.MOB_KILL || contract.type == ContractType.LEGENDARY_DELIVERY) {
            NPCHirelingSystem.getQuestManager().startQuest(player, contract);
        }
        // player.sendMessage(NPCHirelingSystem.getLang().get("contract_accepted").replace("%description%", contract.description));
    }

    public static class Contract {
        public ContractType type;
        public String description;
        public Material material; // For delivery
        public int amount;
        public double reward;
        public UUID id;

        public Contract(ContractType type, String description, Material material, int amount, double reward) {
            this.type = type;
            this.description = description;
            this.material = material;
            this.amount = amount;
            this.reward = reward;
            this.id = UUID.randomUUID();
        }
    }
}
