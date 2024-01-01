package me.foxikle.foxrank;

import me.foxikle.foxrank.events.PlayerVanishEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;

public class Vanish implements CommandExecutor {

    public static void vanishPlayer(Player player) {
        if (FoxRank.getInstance().vanishedPlayers.contains(player)) {
            // they are unvanishing
            player.sendMessage(FoxRank.getInstance().getMessage("UnvanishMessage", player));
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(FoxRank.getInstance(), player);
            }
            FoxRank.getInstance().vanishedPlayers.remove(player);
            FoxRank.getInstance().getPlayerData(player.getUniqueId()).setVanished(false);
            Bukkit.getScheduler().runTaskAsynchronously(FoxRank.getInstance(), () -> FoxRank.getInstance().getDm().setVanishedState(player.getUniqueId(), false));
        } else {
            // they are vanishing
            FoxRank.getInstance().getServer().getPluginManager().callEvent(new PlayerVanishEvent(player, FoxRank.getInstance().getRank(player)));
            player.sendMessage(FoxRank.getInstance().getMessage("VanishMessage", player));
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(FoxRank.getInstance(), player);
            }
            FoxRank.getInstance().vanishedPlayers.add(player);
            FoxRank.getInstance().getPlayerData(player.getUniqueId()).setVanished(true);
            Bukkit.getScheduler().runTaskAsynchronously(FoxRank.getInstance(), () -> FoxRank.getInstance().getDm().setVanishedState(player.getUniqueId(), true));
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player player) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableVanish")) {
               if (player.hasPermission("foxrank.vanish")) {
                   vanishPlayer(player);
               } else {
                   FoxRank.getInstance().getMessage("NoPermissionMessage", player);
               }
            } else {
                FoxRank.getInstance().sendCommandDisabled(sender);
            }
        }
        return false;
    }
}
