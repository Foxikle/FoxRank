package me.foxikle.foxrank;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveMsgs implements Listener {

    private final FoxRank plugin;

    public JoinLeaveMsgs(FoxRank plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("DisableJoinMessages")) {
            event.setJoinMessage("");
        } else {
            event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getName() + " joined the game.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getConfig().getBoolean("DisableLeaveMessages")) {
            event.setQuitMessage("");
        } else {
            event.setQuitMessage(ChatColor.YELLOW + event.getPlayer().getName() + " left the game.");
        }
    }
}
