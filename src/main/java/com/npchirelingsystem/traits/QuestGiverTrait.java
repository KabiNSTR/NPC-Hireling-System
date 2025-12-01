package com.npchirelingsystem.traits;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.gui.ContractGUI;
import com.npchirelingsystem.managers.ContractManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@TraitName("questgiver")
public class QuestGiverTrait extends Trait {

    public QuestGiverTrait() {
        super("questgiver");
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (event.getNPC() == this.getNPC()) {
            // Open Contract GUI
            ContractManager cm = NPCHirelingSystem.getInstance().getContractManager();
            ContractGUI.open(event.getClicker(), cm);
        }
    }
}
