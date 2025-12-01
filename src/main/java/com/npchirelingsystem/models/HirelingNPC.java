package com.npchirelingsystem.models;

import com.npchirelingsystem.NPCHirelingSystem;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
    private int npcId = -1;
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
    private int specialSkillLevel = 0; // Profession specific skill

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
            NPCHirelingSystem.getLang().getRaw("info_name").replace("%name%", name),
            NPCHirelingSystem.getLang().getRaw("info_profession").replace("%profession%", profession),
            NPCHirelingSystem.getLang().getRaw("info_wage").replace("%wage%", String.valueOf(wage)),
            NPCHirelingSystem.getLang().getRaw("info_level").replace("%level%", String.valueOf(level)),
            NPCHirelingSystem.getLang().getRaw("info_xp").replace("%xp%", String.valueOf(xp)).replace("%max%", String.valueOf(getNextLevelXp())),
            NPCHirelingSystem.getLang().getRaw("info_status").replace("%status%", isFollowing ? NPCHirelingSystem.getLang().getRaw("status_following") : NPCHirelingSystem.getLang().getRaw("status_stationary"))
        ));
        info.setItemMeta(infoMeta);
        inventory.setItem(22, info);

        // Follow Toggle
        ItemStack follow = new ItemStack(Material.COMPASS);
        ItemMeta followMeta = follow.getItemMeta();
        followMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("toggle_follow"));
        followMeta.setLore(Arrays.asList(NPCHirelingSystem.getLang().getRaw("toggle_follow_lore")));
        follow.setItemMeta(followMeta);
        inventory.setItem(24, follow);
        
        // Upgrade Button
        ItemStack upgrade = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta upgradeMeta = upgrade.getItemMeta();
        upgradeMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("skill_tree"));
        upgradeMeta.setLore(Arrays.asList(NPCHirelingSystem.getLang().getRaw("skill_tree_lore")));
        upgrade.setItemMeta(upgradeMeta);
        inventory.setItem(20, upgrade);
        
        // Special Skill Button
        ItemStack special = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta specialMeta = special.getItemMeta();
        specialMeta.setDisplayName(NPCHirelingSystem.getLang().getRaw("special_skill").replace("%skill%", getSpecialSkillName()));
        specialMeta.setLore(Arrays.asList(
            NPCHirelingSystem.getLang().getRaw("special_level").replace("%level%", String.valueOf(specialSkillLevel)),
            NPCHirelingSystem.getLang().getRaw("skill_cost"),
            NPCHirelingSystem.getLang().getRaw("special_effect"),
            "§7" + getSpecialSkillName() + " ability.",
            "",
            NPCHirelingSystem.getLang().getRaw("click_to_upgrade")
        ));
        special.setItemMeta(specialMeta);
        inventory.setItem(18, special);
        
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
            if (owner != null) owner.sendMessage(NPCHirelingSystem.getLang().get("level_up")
                    .replace("%name%", name)
                    .replace("%level%", String.valueOf(level)));
            setupMenu();
        }
    }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; setupMenu(); }
    
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; setupMenu(); }
    
    public int getSkillPoints() { return skillPoints; }
    public void setSkillPoints(int skillPoints) { this.skillPoints = skillPoints; setupMenu(); }
    
    public boolean spendSkillPoint() {
        if (skillPoints > 0) {
            skillPoints--;
            return true;
        }
        return false;
    }
    
    public int getDropRateUpgrade() { return dropRateUpgrade; }
    public void setDropRateUpgrade(int dropRateUpgrade) { this.dropRateUpgrade = dropRateUpgrade; }
    
    public int getRareDropUpgrade() { return rareDropUpgrade; }
    public void setRareDropUpgrade(int rareDropUpgrade) { this.rareDropUpgrade = rareDropUpgrade; }
    
    public int getSpecialSkillLevel() { return specialSkillLevel; }
    public void setSpecialSkillLevel(int level) { this.specialSkillLevel = level; }
    public void upgradeSpecialSkill() { specialSkillLevel++; }
    
    public String getSpecialSkillName() {
        switch (profession.toLowerCase()) {
            case "miner": return "Deep Mining";
            case "lumberjack": return "Sharp Axe";
            case "farmer": return "Green Thumb";
            case "hunter": return "Tracker";
            case "guard": return "Strength";
            case "fisherman": return "Lure";
            default: return "Special Ability";
        }
    }
    
    public void upgradeDropRate() { dropRateUpgrade++; }
    public void upgradeRareDrop() { rareDropUpgrade++; }
    
    public void toggleFollow() {
        this.isFollowing = !this.isFollowing;
        setupMenu(); // Update lore
        
        if (npcId != -1) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc != null && npc.isSpawned()) {
                if (isFollowing) {
                    Player owner = Bukkit.getPlayer(ownerId);
                    if (owner != null) {
                        npc.getNavigator().setTarget(owner, true);
                    }
                } else {
                    npc.getNavigator().cancelNavigation();
                }
            }
        }
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
    
    public int getNpcId() {
        return npcId;
    }
    
    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public void spawn(Location location) {
        if (npcId != -1) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc != null) {
                if (!npc.isSpawned()) {
                    npc.spawn(lastLocation != null ? lastLocation : location);
                }
                return;
            }
        }
        
        Location spawnLoc = (lastLocation != null) ? lastLocation : location;
        if (spawnLoc == null) return;

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(spawnLoc);
        
        this.npcId = npc.getId();
        this.entityUuid = npc.getUniqueId();
        this.lastLocation = spawnLoc;
        
        // Add Hireling Trait
        if (!npc.hasTrait(com.npchirelingsystem.traits.HirelingTrait.class)) {
            npc.addTrait(com.npchirelingsystem.traits.HirelingTrait.class);
        }
        npc.getTrait(com.npchirelingsystem.traits.HirelingTrait.class).setHireling(this);
        
        equipNPC(npc);
    }

    private void equipNPC(NPC npc) {
        if (!npc.hasTrait(Equipment.class)) {
            npc.addTrait(Equipment.class);
        }
        Equipment equipment = npc.getTrait(Equipment.class);
        
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
            equipment.set(Equipment.EquipmentSlot.HELMET, helmet);
        }
        if (chest != null) equipment.set(Equipment.EquipmentSlot.CHESTPLATE, chest);
        if (legs != null) equipment.set(Equipment.EquipmentSlot.LEGGINGS, legs);
        if (boots != null) equipment.set(Equipment.EquipmentSlot.BOOTS, boots);
        if (hand != null) equipment.set(Equipment.EquipmentSlot.HAND, hand);
        if (offhand != null) equipment.set(Equipment.EquipmentSlot.OFF_HAND, offhand);
    }

    public void despawn() {
        if (npcId != -1) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc != null) {
                this.lastLocation = npc.getStoredLocation();
                npc.destroy();
            }
            npcId = -1;
            entityUuid = null;
        }
    }
    
    public Location getLastLocation() {
        if (npcId != -1) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc != null && npc.isSpawned()) {
                return npc.getStoredLocation();
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
