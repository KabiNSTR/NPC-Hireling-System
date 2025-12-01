package com.npchirelingsystem.tasks;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WageTask extends BukkitRunnable {

    private final NPCManager npcManager;

    public WageTask(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public void run() {
        List<HirelingNPC> allHirelings = new ArrayList<>(npcManager.getAllHirelings());
        
        for (HirelingNPC npc : allHirelings) {
            Player owner = Bukkit.getPlayer(npc.getOwnerId());
            
            if (owner == null || !owner.isOnline()) {
                // Owner offline, maybe pause payment or fire? For now, skip.
                continue;
            }

            double wage = npc.getWage();
            if (NPCHirelingSystem.getEconomy().has(owner.getUniqueId(), wage)) {
                NPCHirelingSystem.getEconomy().withdraw(owner.getUniqueId(), wage);
                String msg = NPCHirelingSystem.getLang().get("payment_message")
                        .replace("%wage%", String.valueOf(wage))
                        .replace("%profession%", npc.getProfession());
                owner.sendMessage(msg);
            } else {
                String msg = NPCHirelingSystem.getLang().get("payment_fail")
                        .replace("%profession%", npc.getProfession());
                owner.sendMessage(msg);
                npcManager.fireNPC(npc);
            }
        }
    }
}
