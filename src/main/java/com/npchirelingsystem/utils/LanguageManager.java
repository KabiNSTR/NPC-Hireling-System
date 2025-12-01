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
        String lang = plugin.getConfig().getString("language", "en");
        
        File messagesFile = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages_" + lang + ".yml", false);
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
