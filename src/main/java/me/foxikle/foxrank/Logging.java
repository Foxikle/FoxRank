package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.hash;

public class Logging {
    protected static void addOfflinePlayerMuteLogEntry(OfflineRankedPlayer player, Instant expires, String reason, RankedPlayer admin, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("MUTE." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("ID", ID);
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("Expires", expires.toString());
        section.addDefault("Reason", reason);
        section.addDefault("Staff", admin.getRank().getPrefix() + admin.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void addMuteLogEntry(RankedPlayer player, Instant expires, String reason, RankedPlayer admin, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("MUTE." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("ID", ID);
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("Expires", expires.toString());
        section.addDefault("Reason", reason);
        section.addDefault("Staff", admin.getRank().getPrefix() + admin.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void addUnmuteLogEntry(RankedPlayer player, RankedPlayer staff, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("UNMUTE." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("ID", ID);
        section.addDefault("Staff", staff.getRank().getPrefix() + staff.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void addOnlineBanLogEntry(RankedPlayer banned, RankedPlayer staff, Instant when, String reason, String duration, String ID, boolean silent) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("BAN." + banned.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Staff", staff.getName());
        section.addDefault("Player", banned.getName());
        section.addDefault("Reason", reason);
        section.addDefault("Duration", duration);
        section.addDefault("Silent", silent);
        section.addDefault("When", when.toString());
        section.addDefault("ID", ID);
        section.addDefault("Type", "ONLINE");
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void addOfflineBanLogEntry(OfflineRankedPlayer banned, RankedPlayer staff, Instant when, String reason, String duration, String ID, boolean silent) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("BAN." + banned.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Staff", staff.getName());
        section.addDefault("Player", banned.getName());
        section.addDefault("Reason", reason);
        section.addDefault("Duration", duration);
        section.addDefault("Silent", silent);
        section.addDefault("When", when.toString());
        section.addDefault("ID", ID);
        section.addDefault("Type", "OFFLINE");
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void addOfflinePlayerUnmuteLogEntry(OfflineRankedPlayer player, RankedPlayer staff, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("UNMUTE." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("ID", ID);
        section.addDefault("Staff", staff.getRank().getPrefix() + staff.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void addNicknameLogEntry(RankedPlayer player, String newNick, String rankID, String skinOption) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("NICKNAME." + player.getUniqueId() + "." + Integer.toString(hash("FoxRank:" + player.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT));
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getUniqueId().toString());
        section.addDefault("Nickname", newNick);
        section.addDefault("RankID", rankID);
        section.addDefault("Skin", skinOption);
        section.addDefault("Date", Instant.now().toString());
        yml.options().copyDefaults(true);

        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void addUnbanLogEntry(OfflineRankedPlayer player, UUID staff, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("UNBAN." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("ID", ID);
        section.addDefault("Staff", FoxRank.getOfflineRank(Bukkit.getPlayer(staff)).getRankID() + Bukkit.getPlayer(staff).getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
