package com.npchirelingsystem;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class NPCHirelingSystem extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static NPCHirelingSystem instance;

    @Override
    public void onEnable() {
        instance = this;
        if (!setupEconomy()) {
            log.severe("[%s] - Disabled due to no Vault dependency found!".formatted(getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Register commands and events here
        // getCommand("hire").setExecutor(new HireCommand());
        
        getLogger().info("NPCHirelingSystem enabled!");
    }

    @Override
    public void onDisable() {
        log.info("[%s] Disabled Version %s".formatted(getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
    
    public static NPCHirelingSystem getInstance() {
        return instance;
    }
}
