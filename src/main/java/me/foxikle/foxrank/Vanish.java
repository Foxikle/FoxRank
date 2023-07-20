package me.foxikle.foxrank;

import me.foxikle.foxrank.events.PlayerVanishEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Vanish implements CommandExecutor {

    public static void vanishPlayer(Player player) {
        if (FoxRank.getInstance().vanishedPlayers.contains(player)) {
            // they are unvanishing
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnvanishMessage")));
            ActionBar.setupActionBar(player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(FoxRank.getInstance(), player);
            }
            FoxRank.getInstance().vanishedPlayers.remove(player);
            FoxRank.getInstance().dm.setVanishedState(player.getUniqueId(), false);
        } else {
            // they are vanishing
            FoxRank.getInstance().getServer().getPluginManager().callEvent(new PlayerVanishEvent(player, new RankedPlayer(player, FoxRank.getInstance()).getRank()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("VanishMessage")));
            ActionBar.setupActionBar(player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(FoxRank.getInstance(), player);
            }
            FoxRank.getInstance().vanishedPlayers.add(player);
            FoxRank.getInstance().dm.setVanishedState(player.getUniqueId(), true);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("vanish")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableVanish")) {
                if (sender instanceof Player player) {
                    RankedPlayer rp = new RankedPlayer(player, FoxRank.getInstance());
                    if (rp.getPowerLevel() >= FoxRank.getInstance().dm.getConfig().getInt("VanishPermissions")) {
                        vanishPlayer(player);
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
}
