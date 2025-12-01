package com.npchirelingsystem.models;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;

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
    private boolean isFollowing = false;
    
    // Upgrade System
    private int level = 1;
    private int xp = 0;
    private int skillPoints = 0;
    private int dropRateUpgrade = 0; // Each point = +5% chance
    private int rareDropUpgrade = 0; // Each point = +1% rare chance

    public HirelingNPC(UUID ownerId, String name, String profession, double wage) {
        this.ownerId = ownerId;
        this.name = name;
        this.profession = profession;
        this.wage = wage;
        
        String title = NPCHirelingSystem.getLang().getRaw("npc_inventory_title").replace("%name%", name);
        if (title.length() > 32) title = title.substring(0, 32);
        this.inventory = Bukkit.createInventory(null, 27, title);
        
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
        infoMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("npc_stats"));
        infoMeta.setLore(Arrays.asList(
            "§7Name: " + name,
            "§7Profession: " + profession,
            "§7Wage: " + wage,
            "§7Level: §a" + level,
            "§7XP: §b" + xp + "/" + getNextLevelXp(),
            "§7Status: " + (isFollowing ? "Following" : "Stationary")
        ));
        info.setItemMeta(infoMeta);
        inventory.setItem(22, info);

        // Follow Toggle
        ItemStack follow = new ItemStack(Material.COMPASS);
        ItemMeta followMeta = follow.getItemMeta();
        followMeta.setDisplayName("§eToggle Follow");
        followMeta.setLore(Arrays.asList("§7Click to make NPC follow/stay."));
        follow.setItemMeta(followMeta);
        inventory.setItem(24, follow);
        
        // Upgrade Button
        ItemStack upgrade = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta upgradeMeta = upgrade.getItemMeta();
        upgradeMeta.setDisplayName("§bSkill Tree");
        upgradeMeta.setLore(Arrays.asList("§7Click to open upgrades."));
        upgrade.setItemMeta(upgradeMeta);
        inventory.setItem(20, upgrade);
        
        // Fire Button
        ItemStack fire = new ItemStack(Material.RED_WOOL);
        ItemMeta fireMeta = fire.getItemMeta();
        fireMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("fire_npc"));
        fireMeta.setLore(Arrays.asList(NPCHirelingSystem.getLang().getRaw("fire_lore")));
        fire.setItemMeta(fireMeta);
        inventory.setItem(26, fire);
    }
    
    public int getNextLevelXp() {
        return level * 100;
    }
    
    public void addXp(int amount) {
        this.xp += amount;
        if (this.xp >= getNextLevelXp()) {
            this.xp -= getNextLevelXp();
            this.level++;
            this.skillPoints++;
            Player owner = Bukkit.getPlayer(ownerId);
            if (owner != null) owner.sendMessage("§aYour hireling " + name + " leveled up to " + level + "! (+1 Skill Point)");
            setupMenu();
        }
    }
    
    public int getLevel() { return level; }
    public int getSkillPoints() { return skillPoints; }
    public boolean spendSkillPoint() {
        if (skillPoints > 0) {
            skillPoints--;
            return true;
        }
        return false;
    }
    
    public int getDropRateUpgrade() { return dropRateUpgrade; }
    public int getRareDropUpgrade() { return rareDropUpgrade; }
    
    public void upgradeDropRate() { dropRateUpgrade++; }
    public void upgradeRareDrop() { rareDropUpgrade++; }
    
    public void toggleFollow() {
        this.isFollowing = !this.isFollowing;
        setupMenu(); // Update lore
    }

    public boolean isFollowing() {
        return isFollowing;
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

        LivingEntity entity = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
        entity.setCustomName(name + " (" + profession + ")");
        entity.setCustomNameVisible(true);
        entity.setAI(true); 
        
        if (entity instanceof Zombie) {
            Zombie zombie = (Zombie) entity;
            zombie.setAdult();
            zombie.setSilent(true);
            // zombie.setShouldBurnInDay(false); // Removed as it might not be available
            
            equipNPC(zombie);
        }

        this.entityUuid = entity.getUniqueId();
        this.lastLocation = spawnLoc;
    }

    private void equipNPC(LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        equipment.clear();
        
        ItemStack helmet = null;
        ItemStack chest = null;
        ItemStack legs = null;
        ItemStack boots = null;
        ItemStack hand = null;
        ItemStack offhand = null;
        
        switch (profession.toLowerCase()) {
            case "miner":
                helmet = new ItemStack(Material.IRON_HELMET);
                chest = new ItemStack(Material.IRON_CHESTPLATE);
                legs = new ItemStack(Material.IRON_LEGGINGS);
                boots = new ItemStack(Material.IRON_BOOTS);
                hand = new ItemStack(Material.IRON_PICKAXE);
                break;
            case "guard":
                helmet = new ItemStack(Material.IRON_HELMET);
                chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                legs = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                boots = new ItemStack(Material.IRON_BOOTS);
                hand = new ItemStack(Material.IRON_SWORD);
                offhand = new ItemStack(Material.SHIELD);
                break;
            case "farmer":
                helmet = new ItemStack(Material.LEATHER_HELMET);
                chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                legs = new ItemStack(Material.LEATHER_LEGGINGS);
                boots = new ItemStack(Material.LEATHER_BOOTS);
                hand = new ItemStack(Material.IRON_HOE);
                break;
            case "hunter":
                helmet = new ItemStack(Material.LEATHER_HELMET);
                chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                legs = new ItemStack(Material.LEATHER_LEGGINGS);
                boots = new ItemStack(Material.LEATHER_BOOTS);
                hand = new ItemStack(Material.BOW);
                break;
            case "lumberjack":
                helmet = new ItemStack(Material.LEATHER_HELMET);
                chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                legs = new ItemStack(Material.LEATHER_LEGGINGS);
                boots = new ItemStack(Material.LEATHER_BOOTS);
                hand = new ItemStack(Material.IRON_AXE);
                break;
            case "fisherman":
                helmet = new ItemStack(Material.TURTLE_HELMET);
                chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                legs = new ItemStack(Material.LEATHER_LEGGINGS);
                boots = new ItemStack(Material.LEATHER_BOOTS);
                hand = new ItemStack(Material.FISHING_ROD);
                break;
        }
        
        if (helmet != null) {
            ItemMeta meta = helmet.getItemMeta();
            meta.setUnbreakable(true);
            helmet.setItemMeta(meta);
            equipment.setHelmet(helmet);
        }
        if (chest != null) equipment.setChestplate(chest);
        if (legs != null) equipment.setLeggings(legs);
        if (boots != null) equipment.setBoots(boots);
        if (hand != null) equipment.setItemInMainHand(hand);
        if (offhand != null) equipment.setItemInOffHand(offhand);
        
        // Ensure they don't drop items on death
        equipment.setHelmetDropChance(0f);
        equipment.setChestplateDropChance(0f);
        equipment.setLeggingsDropChance(0f);
        equipment.setBootsDropChance(0f);
        equipment.setItemInMainHandDropChance(0f);
        equipment.setItemInOffHandDropChance(0f);
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
