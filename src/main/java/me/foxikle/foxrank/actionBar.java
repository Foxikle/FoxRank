package me.foxikle.foxrank;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;

public class actionBar {

    public static void setupActionBar(Player player) {
        new BukkitRunnable() {
            public void run() {
                if (FoxRank.getInstance().isNicked(player) && FoxRank.getInstance().isVanished(player)) {
                    player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "NICKED" + ChatColor.WHITE + " & " + ChatColor.RED + "VANISHED" + ChatColor.WHITE + "."));
                } else if (FoxRank.getInstance().isVanished(player)) {
                    player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "VANISHED" + ChatColor.WHITE + "."));
                } else if (FoxRank.getInstance().isNicked(player)) {
                    player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "NICKED" + ChatColor.WHITE + "."));
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(FoxRank.getInstance(), 0, 20);
    }

}
