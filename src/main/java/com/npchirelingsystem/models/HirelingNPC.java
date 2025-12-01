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

    public HirelingNPC(UUID ownerId, String name, String profession, double wage) {
        this.ownerId = ownerId;
        this.name = name;
        this.profession = profession;
        this.wage = wage;
    }

    public void spawn(Location location) {
        if (entityUuid != null && Bukkit.getEntity(entityUuid) != null) {
            return; // Already spawned
        }

        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        entity.setCustomName(name + " (" + profession + ")");
        entity.setCustomNameVisible(true);
        entity.setAI(true); // Can be set to false if we want them static
        
        if (entity instanceof Villager) {
            ((Villager) entity).setProfession(Villager.Profession.valueOf(profession.toUpperCase()));
        }

        this.entityUuid = entity.getUniqueId();
    }

    public void despawn() {
        if (entityUuid != null) {
            var entity = Bukkit.getEntity(entityUuid);
            if (entity != null) {
                entity.remove();
            }
            entityUuid = null;
        }
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
