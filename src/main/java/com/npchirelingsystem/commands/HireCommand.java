package com.npchirelingsystem.commands;

import com.npchirelingsystem.gui.MainMenuGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HireCommand implements CommandExecutor {
    private final com.npchirelingsystem.NPCHirelingSystem plugin;

    public HireCommand(com.npchirelingsystem.NPCHirelingSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(com.npchirelingsystem.NPCHirelingSystem.getLang().get("only_players"));
            return true;
        }
        
        if (!sender.hasPermission("npchirelingsystem.hire")) {
            sender.sendMessage(com.npchirelingsystem.NPCHirelingSystem.getLang().get("no_permission"));
            return true;
        }
        
        // Open Main Menu with Admin access allowed (since it's a command)
        MainMenuGUI.open((Player) sender, true);
        return true;
    }
}
