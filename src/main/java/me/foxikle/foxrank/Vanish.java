package me.foxikle.foxrank;

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
            if (sender instanceof Player) {
                Player player = (Player) sender;
                File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                if(yml.getString("isVanished").equals("true")){
                    player.sendMessage(ChatColor.YELLOW + "You materialized out of the ether!");
                    yml.set("isVanished", false);
                    try {
                        yml.save(file);
                    } catch (IOException error) {
                        Bukkit.getLogger().log(Level.SEVERE,"ERROR could not save " + player.getName() + "'s Vanished state");
                    }

                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.showPlayer(FoxRank.getInstance(), player);
                    }

                } else if(yml.getString("isVanished").equals("false")){
                    yml.set("isVanished", true);
                    player.sendMessage(ChatColor.YELLOW + "You vanished into the shadows.");
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.hidePlayer(FoxRank.getInstance(), player);
                    }
                    try {
                        yml.save(file);
                    } catch (IOException error) {
                        Bukkit.getLogger().log(Level.SEVERE,"ERROR could not save " + player.getName() + "'s Vanished state");
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            File file1 = new File("plugins/FoxRank/PlayerData/" + p1.getUniqueId() + ".yml");
            YamlConfiguration yml1 = YamlConfiguration.loadConfiguration(file1);
            if (yml1.getString("isVanished").equals("true")){
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p1.showPlayer(FoxRank.getInstance(), p);
                }
            }
        }
    }
}
