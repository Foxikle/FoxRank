package me.foxikle.foxrank;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;

public class ActionBar {

    private static final FoxRank plugin = FoxRank.getInstance();

    protected static void setupActionBar(Player player) {
        if (!plugin.getConfig().getBoolean("DisableActionbar")) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                new BukkitRunnable() {
                    public void run() {
                        if (!player.isOnline()) return;
                        if (plugin.dm.isNicked(player.getUniqueId()) && plugin.dm.isVanished(player.getUniqueId())) {
                            player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "NICKED" + ChatColor.WHITE + " & " + ChatColor.RED + "VANISHED" + ChatColor.WHITE + "."));
                        } else if (plugin.dm.isVanished(player.getUniqueId())) {
                            player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "VANISHED" + ChatColor.WHITE + "."));
                        } else if (plugin.dm.isNicked(player.getUniqueId())) {
                            player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "NICKED" + ChatColor.WHITE + "."));
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20);
            });
        }
    }
}
