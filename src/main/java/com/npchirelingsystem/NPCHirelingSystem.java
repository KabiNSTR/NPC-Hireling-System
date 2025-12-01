package com.npchirelingsystem;

import com.npchirelingsystem.economy.EconomyProvider;
import com.npchirelingsystem.economy.InternalEconomyProvider;
import com.npchirelingsystem.economy.VaultEconomyProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class NPCHirelingSystem extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static EconomyProvider economyProvider;
    private static NPCHirelingSystem instance;

    @Override
    public void onEnable() {
        instance = this;
        
        setupEconomy();
        
        // Register commands and events here
        // getCommand("hire").setExecutor(new HireCommand());
        
        getLogger().info("NPCHirelingSystem enabled! Using economy: " + economyProvider.getName());
    }

    @Override
    public void onDisable() {
        log.info("[%s] Disabled Version %s".formatted(getDescription().getName(), getDescription().getVersion()));
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null && rsp.getProvider() != null) {
                economyProvider = new VaultEconomyProvider(rsp.getProvider());
                getLogger().info("Vault found! Hooked into " + rsp.getProvider().getName());
                return;
            }
        }
        
        // Fallback to internal economy
        economyProvider = new InternalEconomyProvider(this);
        getLogger().info("Vault not found or no economy plugin detected. Using internal economy system.");
    }

    public static EconomyProvider getEconomy() {
        return economyProvider;
    }
    
    public static NPCHirelingSystem getInstance() {
        return instance;
    }
}
