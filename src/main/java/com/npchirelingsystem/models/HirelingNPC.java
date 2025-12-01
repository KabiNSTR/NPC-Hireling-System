package com.npchirelingsystem.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.UUID;

public class HirelingNPC {
    private final UUID ownerId;
    private final String name;
    private final String profession;
    private final double wage;
    private UUID entityUuid;
    private Location lastLocation;

    public HirelingNPC(UUID ownerId, String name, String profession, double wage) {
        this.ownerId = ownerId;
        this.name = name;
        this.profession = profession;
        this.wage = wage;
    }
    
    public HirelingNPC(UUID ownerId, String name, String profession, double wage, Location lastLocation) {
        this(ownerId, name, profession, wage);
        this.lastLocation = lastLocation;
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
