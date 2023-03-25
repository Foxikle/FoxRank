package me.foxikle.foxrank;

import me.foxikle.foxrank.events.PlayerVanishEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Vanish implements CommandExecutor, Listener {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("vanish")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableVanish")) {
                if (sender instanceof Player player) {
                    RankedPlayer rp = new RankedPlayer(player);
                    if (rp.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("VanishPermissions")) {
                        if (FoxRank.getInstance().useDb) {
                            Database db = FoxRank.getInstance().db;
                            if (db.getStoredVanishStatus(player.getUniqueId())) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnvanishMessage")));
                                ActionBar.setupActionBar(player);
                                db.setStoredVanishedState(player.getUniqueId(), false);
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    p.showPlayer(FoxRank.getInstance(), player);
                                }

                            } else {
                                db.setStoredVanishedState(player.getUniqueId(), true);
                                FoxRank.getInstance().getServer().getPluginManager().callEvent(new PlayerVanishEvent(player, rp.getRank()));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("VanishMessage")));
                                ActionBar.setupActionBar(player);
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    p.hidePlayer(FoxRank.getInstance(), player);
                                }
                            }
                        } else {
                            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                            if (yml.getString("isVanished").equals("true")) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnvanishMessage")));
                                yml.set("isVanished", false);
                                try {
                                    yml.save(file);
                                } catch (IOException error) {
                                    Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + player.getName() + "'s Vanished state.");
                                }
                                ActionBar.setupActionBar(player);

                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    p.showPlayer(FoxRank.getInstance(), player);
                                }

                            } else if (yml.getString("isVanished").equals("false")) {
                                yml.set("isVanished", true);
                                FoxRank.getInstance().getServer().getPluginManager().callEvent(new PlayerVanishEvent(player, rp.getRank()));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("VanishMessage")));
                                ActionBar.setupActionBar(player);
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    p.hidePlayer(FoxRank.getInstance(), player);
                                }
                                try {
                                    yml.save(file);
                                } catch (IOException error) {
                                    Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + player.getName() + "'s Vanished state.");
                                }
                            }
                        }
                    } else {
                        FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("VanishPermissions"), rp);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("CommandDisabledMessage")));
            }
        }
        return false;
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            File file1 = new File("plugins/FoxRank/PlayerData/" + p1.getUniqueId() + ".yml");
            YamlConfiguration yml1 = YamlConfiguration.loadConfiguration(file1);
            if (yml1.getBoolean("isVanished")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.hidePlayer(FoxRank.getInstance(), p1);
                }
            }
        }
    }
}
