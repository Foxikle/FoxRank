package me.foxikle.foxrank;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;

public class ActionBar {

    private final FoxRank plugin;

    public ActionBar(FoxRank plugin) {
        this.plugin = plugin;
    }

    public void setupActionBars() {
        if (!plugin.getConfig().getBoolean("DisableActionbar")) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (plugin.getPlayerData(player.getUniqueId()) == null)
                        return;
                    boolean isVanished = plugin.getPlayerData(player.getUniqueId()).isVanished();
                    boolean isNicked = plugin.getPlayerData(player.getUniqueId()).isNicked();
                    if (isNicked && isVanished) {
                        player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "NICKED" + ChatColor.WHITE + " & " + ChatColor.RED + "VANISHED" + ChatColor.WHITE + "."));
                    } else if (isVanished) {
                        player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "VANISHED" + ChatColor.WHITE + "."));
                    } else if (isNicked) {
                        player.spigot().sendMessage(ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "You are currently " + ChatColor.RED + "NICKED" + ChatColor.WHITE + "."));
                    }
                }
            }, 0, 20);
        }
    }
}
