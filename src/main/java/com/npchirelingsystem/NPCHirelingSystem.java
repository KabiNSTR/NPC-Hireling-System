package com.npchirelingsystem;

import com.npchirelingsystem.commands.AdminCommand;
import com.npchirelingsystem.commands.HireCommand;
import com.npchirelingsystem.economy.EconomyProvider;
import com.npchirelingsystem.economy.InternalEconomyProvider;
import com.npchirelingsystem.economy.VaultEconomyProvider;
import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.utils.LanguageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class NPCHirelingSystem extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static EconomyProvider economyProvider;
    private static NPCHirelingSystem instance;
    private static LanguageManager languageManager;
    private NPCManager npcManager;
    private com.npchirelingsystem.managers.ContractManager contractManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Load config and language
        saveDefaultConfig();
        languageManager = new LanguageManager(this);
        
        setupEconomy();
        
        this.npcManager = new NPCManager(this);
        this.npcManager.loadAll();
        
        this.contractManager = new com.npchirelingsystem.managers.ContractManager();
        
        // Register commands and events here
        getCommand("hire").setExecutor(new HireCommand());
        getCommand("npcadmin").setExecutor(new AdminCommand(this, npcManager));
        getCommand("contracts").setExecutor(new com.npchirelingsystem.commands.ContractCommand(contractManager));
        
        getServer().getPluginManager().registerEvents(new com.npchirelingsystem.gui.GUIListener(npcManager, contractManager), this);
        getServer().getPluginManager().registerEvents(new com.npchirelingsystem.listeners.PlayerListener(npcManager), this);
        getServer().getPluginManager().registerEvents(new com.npchirelingsystem.listeners.NPCInteractionListener(npcManager), this);
        
        // Start wage task (every 60 seconds = 1200 ticks)
        new com.npchirelingsystem.tasks.WageTask(npcManager).runTaskTimer(this, 1200L, 1200L);
        
        // Start job task (every 5 seconds = 100 ticks)
        new com.npchirelingsystem.tasks.JobTask(npcManager).runTaskTimer(this, 100L, 100L);
        
        getLogger().info("NPCHirelingSystem enabled! Using economy: " + economyProvider.getName());
    }

    @Override
    public void onDisable() {
        if (npcManager != null) {
            npcManager.saveAll();
        }
        log.info("[%s] Disabled Version %s".formatted(getDescription().getName(), getDescription().getVersion()));
    }
    
    public void reloadPlugin() {
        reloadConfig();
        languageManager.loadMessages();
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
    
    public static LanguageManager getLang() {
        return languageManager;
    }
}
