package com.npchirelingsystem.traits;

import com.npchirelingsystem.NPCHirelingSystem;
import com.npchirelingsystem.models.HirelingNPC;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;

@TraitName("hirelingtrait")
public class HirelingTrait extends Trait {

    private HirelingNPC hireling;

    public HirelingTrait() {
        super("hirelingtrait");
    }
    
    public void setHireling(HirelingNPC hireling) {
        this.hireling = hireling;
    }

    @Override
    public void run() {
        if (hireling == null) return;
        
        // AI Logic here (e.g. look at owner if following)
        if (hireling.isFollowing()) {
            // Citizens Navigator handles movement, but we can add extra logic here
        }
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (event.getNPC() == this.getNPC()) {
            if (hireling == null) return;
            
            Player player = event.getClicker();
            if (player.getUniqueId().equals(hireling.getOwnerId()) || player.hasPermission("npchirelingsystem.admin")) {
                player.openInventory(hireling.getInventory());
            } else {
                player.sendMessage(NPCHirelingSystem.getLang().get("not_your_hireling"));
            }
        }
    }
}
