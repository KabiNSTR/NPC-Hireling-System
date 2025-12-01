package com.npchirelingsystem.utils;

import com.npchirelingsystem.NPCHirelingSystem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {

    private final NPCHirelingSystem plugin;
    private FileConfiguration messagesConfig;

    public LanguageManager(NPCHirelingSystem plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        String lang = plugin.getConfig().getString("lang", "en");
        
        File localesDir = new File(plugin.getDataFolder(), "locales");
        if (!localesDir.exists()) {
            localesDir.mkdirs();
        }
        
        File messagesFile = new File(localesDir, "messages_" + lang + ".yml");
        
        if (!messagesFile.exists()) {
            // Try to save from resource if it exists in jar
            try {
                plugin.saveResource("locales/messages_" + lang + ".yml", false);
            } catch (IllegalArgumentException e) {
                // If resource not found in jar at locales/ path, try root path and move it?
                // Or just assume the user provided a valid lang.
                // For now, let's try to save the default ones if they are missing.
                if (lang.equals("en") || lang.equals("ru")) {
                     // We need to handle the resource path correctly. 
                     // In the JAR, they are at root. We want them in locales/ on disk.
                     plugin.saveResource("messages_" + lang + ".yml", true); // Save to root first
                     File rootFile = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
                     if (rootFile.exists()) {
                         rootFile.renameTo(messagesFile);
                     }
                }
            }
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        // Load defaults from jar to ensure new keys exist
        InputStream defConfigStream = plugin.getResource("messages_" + lang + ".yml");
        if (defConfigStream != null) {
            messagesConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
        }
    }

    public String get(String key) {
        String prefix = messagesConfig.getString("prefix", "&8[&6NPC Hireling&8] &r");
        String msg = messagesConfig.getString(key, "&cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }
    
    public String getRaw(String key) {
        String msg = messagesConfig.getString(key, "&cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
