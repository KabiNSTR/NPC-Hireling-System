# 🛡️ NPC Hireling System v3.3

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spigot](https://img.shields.io/badge/Spigot-1.20.4-yellow?style=for-the-badge&logo=spigot)
![Citizens](https://img.shields.io/badge/Citizens-2.0.33+-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**NPC Hireling System** is the ultimate RPG companion plugin for your Minecraft server. Transform static Citizens NPCs into dynamic, leveling, working hirelings that can mine, farm, hunt, and fight by your side.

With the **v3.3 Content Update**, the world becomes even more alive with a deep Contract system, Reputation mechanics, and challenging Boss fights!

---

## ✨ Key Features

### 🧠 Advanced AI & Professions
Turn any NPC into a specialized worker. They gain XP, level up, and unlock skills!
*   **⛏️ Miner**: Automatically mines ores and stone.
*   **🌾 Farmer**: Harvests and replants crops.
*   **🪓 Lumberjack**: Fells trees and replants saplings.
*   **⚔️ Guard**: Protects you from monsters and hostile players.
*   **🏹 Hunter**: Hunts animals for food and resources.
*   **🎣 Fisherman**: Catches fish near water sources.

### 📜 Contract System 2.0 (NEW!)
A fully overhauled questing board with deep progression.
*   **Dynamic Rarity**: Contracts come in **Common**, **Uncommon**, **Rare**, **Epic**, and **Legendary** tiers.
*   **Reputation System**: Earn reputation to unlock higher-tier contracts with massive rewards.
*   **4 Unique Categories**:
    *   **Gathering**: Collect resources.
    *   **Hunting**: Eliminate specific mobs.
    *   **Exploration**: Scout distant biomes.
    *   **Boss Hunt**: Defeat powerful, custom-scripted bosses.

### ⚔️ Quest System 2.0 (NEW!)
*   **Smart Bosses**: Face off against the **Bandit King**, **Shadow Assassin**, and more. They scale with contract rarity!
*   **Biome Detection**: Exploration quests automatically complete when you discover the target biome.
*   **Sentinel Integration**: Enemies fight back with advanced AI (requires Sentinel).

### 🎒 RPG Mechanics
*   **Skill Trees**: Upgrade your hirelings' **Drop Rate**, **Speed**, and **Combat Power**.
*   **Inventory System**: Every hireling has a personal inventory you can access.
*   **Wages**: Hirelings expect payment! Manage your economy or they will go on strike.
*   **Remote Management**: Use the "My Hirelings" menu to manage your workers from anywhere.

---

## 📸 Gallery

| Main Menu | Contract Board | Skill Tree |
|:---:|:---:|:---:|
| *Manage your empire* | *Accept daily quests* | *Upgrade your workers* |

---

## 🚀 Installation

1.  **Prerequisites**:
    *   Java 21+
    *   Spigot/Paper 1.20.4+
    *   [Citizens](https://ci.citizensnpcs.co/job/Citizens2/) (Required)
    *   [Sentinel](https://ci.citizensnpcs.co/job/Sentinel/) (Highly Recommended for combat)
    *   [Vault](https://www.spigotmc.org/resources/vault.34315/) (Optional, for economy)

2.  **Setup**:
    *   Download `NPCHirelingSystem-3.3.jar`.
    *   Place it in your `plugins/` folder.
    *   Restart the server.

---

## 💻 Commands & Permissions

| Command | Alias | Description | Permission |
|:---|:---|:---|:---|
| `/hire <npc>` | `/h` | Hire the selected NPC. | `npchirelingsystem.hire` |
| `/contracts` | `/c` | Open the Contract Board. | `npchirelingsystem.contracts` |
| `/npcadmin` | `/nadmin` | Open the Admin Panel. | `npchirelingsystem.admin` |

---

## ⚙️ Configuration

The `config.yml` allows you to tweak every aspect of the plugin:
*   **Wages**: Set how much each profession costs per day.
*   **Loot Tables**: Customize what miners and hunters find.
*   **Language**: Fully translatable messages.

---

## 🤝 Contributing

Found a bug? Have a suggestion? Feel free to open an issue or submit a pull request!

**Created by KabiNSTR**
