package com.npchirelingsystem.commands;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.gui.ContractGUI;
import com.npchirelingsystem.managers.ContractManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ContractCommand implements CommandExecutor {

    private final ContractManager contractManager;

    public ContractCommand(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        ContractGUI.open(player, contractManager);
        return true;
    }
}
