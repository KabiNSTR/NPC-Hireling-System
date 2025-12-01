package com.npchirelingsystem.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class HirelingNPC {
    private final UUID ownerId;
    private final String name;
    private final String profession;
    private final double wage;
    private UUID entityUuid;
    private Location lastLocation;
    private final Inventory inventory;

    public HirelingNPC(UUID ownerId, String name, String profession, double wage) {
        this.ownerId = ownerId;
        this.name = name;
        this.profession = profession;
        this.wage = wage;
        this.inventory = Bukkit.createInventory(null, 27, name + "'s Inventory");
        setupMenu();
    }
    
    private void setupMenu() {
        // Fill bottom row with gray glass pane
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        
        for (int i = 18; i < 27; i++) {
            inventory.setItem(i, filler);
        }
        
        // Info Item
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§eNPC Stats");
        infoMeta.setLore(Arrays.asList(
            "§7Name: " + name,
            "§7Profession: " + profession,
            "§7Wage: " + wage
        ));
        info.setItemMeta(infoMeta);
        inventory.setItem(22, info);
        
        // Fire Button
        ItemStack fire = new ItemStack(Material.RED_WOOL);
        ItemMeta fireMeta = fire.getItemMeta();
        fireMeta.setDisplayName("§cFire NPC");
        fireMeta.setLore(Arrays.asList("§7Click to fire this worker"));
        fire.setItemMeta(fireMeta);
        inventory.setItem(26, fire);
    }
    
    public HirelingNPC(UUID ownerId, String name, String profession, double wage, Location lastLocation) {
        this(ownerId, name, profession, wage);
        this.lastLocation = lastLocation;
    }
    
    public void setInventoryContents(ItemStack[] contents) {
        if (contents != null) {
            this.inventory.setContents(contents);
            // Re-apply menu buttons to ensure they are there
            setupMenu();
        }
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public UUID getEntityUuid() {
        return entityUuid;
    }

    public void spawn(Location location) {
        if (entityUuid != null && Bukkit.getEntity(entityUuid) != null) {
            return; // Already spawned
        }
        
        Location spawnLoc = (lastLocation != null) ? lastLocation : location;
        if (spawnLoc == null) return;

        LivingEntity entity = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.VILLAGER);
        entity.setCustomName(name + " (" + profession + ")");
        entity.setCustomNameVisible(true);
        entity.setAI(true); // Can be set to false if we want them static
        
        if (entity instanceof Villager) {
            try {
                ((Villager) entity).setProfession(Villager.Profession.valueOf(profession.toUpperCase()));
            } catch (IllegalArgumentException e) {
                ((Villager) entity).setProfession(Villager.Profession.FARMER);
            }
        }

        this.entityUuid = entity.getUniqueId();
        this.lastLocation = spawnLoc;
    }

    public void despawn() {
        if (entityUuid != null) {
            var entity = Bukkit.getEntity(entityUuid);
            if (entity != null) {
                this.lastLocation = entity.getLocation(); // Save location before removing
                entity.remove();
            }
            entityUuid = null;
        }
    }
    
    public Location getLastLocation() {
        if (entityUuid != null) {
            var entity = Bukkit.getEntity(entityUuid);
            if (entity != null) {
                return entity.getLocation();
            }
        }
        return lastLocation;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public double getWage() {
        return wage;
    }
    
    public String getProfession() {
        return profession;
    }
}
