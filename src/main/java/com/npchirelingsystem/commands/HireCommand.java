package com.npchirelingsystem.commands;

import com.npchirelingsystem.gui.HiringGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HireCommand implements CommandExecutor {
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
        
        HiringGUI.open((Player) sender);
        return true;
    }
}
