package com.npchirelingsystem.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class VaultEconomyProvider implements EconomyProvider {

    private final Economy economy;

    public VaultEconomyProvider(Economy economy) {
        this.economy = economy;
    }

    @Override
    public boolean has(UUID player, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        return economy.has(offlinePlayer, amount);
    }

    @Override
    public void withdraw(UUID player, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        economy.withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public void deposit(UUID player, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        economy.depositPlayer(offlinePlayer, amount);
    }

    @Override
    public double getBalance(UUID player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        return economy.getBalance(offlinePlayer);
    }

    @Override
    public String format(double amount) {
        return economy.format(amount);
    }

    @Override
    public String getName() {
        return "Vault";
    }
}
