package com.npchirelingsystem.economy;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class InternalEconomyProvider implements EconomyProvider {

    private final NPCHirelingSystem plugin;
    private File balancesFile;
    private FileConfiguration balancesConfig;

    public InternalEconomyProvider(NPCHirelingSystem plugin) {
        this.plugin = plugin;
        setupFile();
    }

    private void setupFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        balancesFile = new File(plugin.getDataFolder(), "balances.yml");
        if (!balancesFile.exists()) {
            try {
                balancesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create balances.yml", e);
            }
        }
        balancesConfig = YamlConfiguration.loadConfiguration(balancesFile);
    }

    private void saveFile() {
        try {
            balancesConfig.save(balancesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save balances.yml", e);
        }
    }

    @Override
    public boolean has(UUID player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public void withdraw(UUID player, double amount) {
        double current = getBalance(player);
        setBalance(player, current - amount);
    }

    @Override
    public void deposit(UUID player, double amount) {
        double current = getBalance(player);
        setBalance(player, current + amount);
    }

    @Override
    public double getBalance(UUID player) {
        return balancesConfig.getDouble(player.toString(), 0.0);
    }

    private void setBalance(UUID player, double amount) {
        balancesConfig.set(player.toString(), amount);
        saveFile();
    }

    @Override
    public String format(double amount) {
        return String.format("%.2f coins", amount);
    }

    @Override
    public String getName() {
        return "Internal Economy";
    }
}
