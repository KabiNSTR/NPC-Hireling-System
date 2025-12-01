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
            Material[] items = {Material.COBBLESTONE, Material.COAL, Material.WHEAT, Material.OAK_LOG};
            Material mat = items[random.nextInt(items.length)];
            int amount = 32 + random.nextInt(32);
            return new Contract(ContractType.ITEM_DELIVERY, "Gather " + amount + " " + mat.name(), mat, amount, 100.0);
        } else if (category == ContractCategory.HUNTING) {
            return new Contract(ContractType.MOB_KILL, "Kill 10 Zombies at Outpost", null, 10, 250.0);
        } else {
            return new Contract(ContractType.LEGENDARY_DELIVERY, "Deliver Secret Package", Material.PAPER, 1, 1000.0);
        }
    }

    public void acceptContract(Player player, Contract contract) {
        if (contract.type == ContractType.MOB_KILL) {
            Location target = player.getLocation().add(random.nextInt(2000)-1000, 0, random.nextInt(2000)-1000);
            target.setY(target.getWorld().getHighestBlockYAt(target) + 1);
            NPCHirelingSystem.getQuestManager().startKillQuest(player, target, contract.amount);
        } else if (contract.type == ContractType.LEGENDARY_DELIVERY) {
            Location target = player.getLocation().add(random.nextInt(2000)-1000, 0, random.nextInt(2000)-1000);
            target.setY(target.getWorld().getHighestBlockYAt(target) + 1);
            NPCHirelingSystem.getQuestManager().startLegendaryQuest(player, target);
        }
        player.sendMessage("§aContract Accepted: " + contract.description);
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
