package com.npchirelingsystem.commands;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.gui.AdminGUI;
import com.npchirelingsystem.managers.NPCManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private final NPCHirelingSystem plugin;
    private final NPCManager npcManager;

    public AdminCommand(NPCHirelingSystem plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
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

        if (sender instanceof Player) {
            AdminGUI.open((Player) sender, npcManager);
        } else {
            sender.sendMessage("Console can only use /npcadmin reload");
        }
        
        return true;
    }
}
