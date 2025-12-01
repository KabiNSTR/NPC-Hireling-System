package com.npchirelingsystem.listeners;

import com.npchirelingsystem.managers.NPCManager;
import com.npchirelingsystem.models.HirelingNPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

import com.npchirelingsystem.gui.MainMenuGUI;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener {

    private final NPCManager npcManager;
    private final ItemStack menuItem;

    public PlayerListener(NPCManager npcManager) {
        this.npcManager = npcManager;
        this.menuItem = createMenuItem();
    }
    
    private ItemStack createMenuItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(com.npchirelingsystem.NPCHirelingSystem.getLang().getRaw("menu_item_name"));
        meta.setLore(java.util.Arrays.asList(com.npchirelingsystem.NPCHirelingSystem.getLang().getRaw("menu_item_lore")));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        List<HirelingNPC> hirelings = npcManager.getHirelings(event.getPlayer().getUniqueId());
        for (HirelingNPC npc : hirelings) {
            npc.despawn();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Give Menu Item
        event.getPlayer().getInventory().setItem(8, menuItem);
        
        List<HirelingNPC> hirelings = npcManager.getHirelings(event.getPlayer().getUniqueId());
        for (HirelingNPC npc : hirelings) {
            if (npc.getLastLocation() != null) {
                npc.spawn(npc.getLastLocation());
            }
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.isSimilar(menuItem)) {
                event.setCancelled(true);
                // Open Menu WITHOUT Admin access
                MainMenuGUI.open(event.getPlayer(), false);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(menuItem)) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof org.bukkit.entity.Player) {
                ((org.bukkit.entity.Player) event.getWhoClicked()).updateInventory();
            }
        }
        // Also check hotbar swap
        if (event.getHotbarButton() == 8) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof org.bukkit.entity.Player) {
                ((org.bukkit.entity.Player) event.getWhoClicked()).updateInventory();
            }
        }
        // Check if swapping with offhand (F key)
        if (event.getClick() == org.bukkit.event.inventory.ClickType.SWAP_OFFHAND) {
             if (event.getWhoClicked().getInventory().getItemInMainHand().isSimilar(menuItem)) {
                 event.setCancelled(true);
             }
        }
    }
}
