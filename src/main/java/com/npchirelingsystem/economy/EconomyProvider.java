package com.npchirelingsystem.economy;

import java.util.UUID;

public interface EconomyProvider {
    boolean has(UUID player, double amount);
    void withdraw(UUID player, double amount);
    void deposit(UUID player, double amount);
    double getBalance(UUID player);
    String format(double amount);
    String getName();
}
