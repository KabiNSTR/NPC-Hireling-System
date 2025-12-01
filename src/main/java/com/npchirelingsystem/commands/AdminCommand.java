package com.npchirelingsystem.commands;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.economy.EconomyProvider;
import com.npchirelingsystem.gui.AdminGUI;
import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.managers.AdminNPCManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private final NPCHirelingSystem plugin;
    private final NPCManager npcManager;
    private final AdminNPCManager adminNPCManager;

    public AdminCommand(NPCHirelingSystem plugin, NPCManager npcManager, AdminNPCManager adminNPCManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
        this.adminNPCManager = adminNPCManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("npchirelingsystem.admin")) {
            sender.sendMessage(NPCHirelingSystem.getLang().get("no_permission"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPlugin();
            sender.sendMessage(NPCHirelingSystem.getLang().get("reload_success"));
            return true;
        }
        
        if (args.length > 0 && args[0].equalsIgnoreCase("eco")) {
            handleEcoCommand(sender, args);
            return true;
        }
        
        if (args.length > 0 && args[0].equalsIgnoreCase("create")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /npcadmin create <name>");
                return true;
            }
            String name = args[1];
            Player player = (Player) sender;
            adminNPCManager.createAdminNPC(player.getLocation(), name);
            player.sendMessage("§aAdmin NPC '" + name + "' created at your location.");
            return true;
        }

        if (sender instanceof Player) {
            AdminGUI.open((Player) sender, npcManager);
        } else {
            sender.sendMessage("Console can only use /npcadmin reload or /npcadmin eco");
        }
        
        return true;
    }
    
    private void handleEcoCommand(CommandSender sender, String[] args) {
        // /npcadmin eco <give|take|set|balance> <player> [amount]
        if (args.length < 3) {
            sender.sendMessage(NPCHirelingSystem.getLang().get("eco_invalid_args"));
            return;
        }
        
        String sub = args[1].toLowerCase();
        String playerName = args[2];
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
             sender.sendMessage(NPCHirelingSystem.getLang().get("eco_player_not_found"));
             return;
        }
        
        EconomyProvider eco = NPCHirelingSystem.getEconomy();
        
        if (sub.equals("balance")) {
            double bal = eco.getBalance(target.getUniqueId());
            sender.sendMessage(NPCHirelingSystem.getLang().get("eco_balance")
                    .replace("%player%", target.getName())
                    .replace("%amount%", String.valueOf(bal)));
            return;
        }
        
        if (args.length < 4) {
            sender.sendMessage(NPCHirelingSystem.getLang().get("eco_invalid_args"));
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount.");
            return;
        }
        
        switch (sub) {
            case "give":
                eco.deposit(target.getUniqueId(), amount);
                sender.sendMessage(NPCHirelingSystem.getLang().get("eco_give")
                        .replace("%player%", target.getName())
                        .replace("%amount%", String.valueOf(amount)));
                break;
            case "take":
                eco.withdraw(target.getUniqueId(), amount);
                sender.sendMessage(NPCHirelingSystem.getLang().get("eco_take")
                        .replace("%player%", target.getName())
                        .replace("%amount%", String.valueOf(amount)));
                break;
            case "set":
                // Try to set if internal, otherwise simulate
                double current = eco.getBalance(target.getUniqueId());
                if (amount > current) {
                    eco.deposit(target.getUniqueId(), amount - current);
                } else {
                    eco.withdraw(target.getUniqueId(), current - amount);
                }
                sender.sendMessage(NPCHirelingSystem.getLang().get("eco_set")
                        .replace("%player%", target.getName())
                        .replace("%amount%", String.valueOf(amount)));
                break;
            default:
                sender.sendMessage(NPCHirelingSystem.getLang().get("eco_invalid_args"));
                break;
        }
    }
}
