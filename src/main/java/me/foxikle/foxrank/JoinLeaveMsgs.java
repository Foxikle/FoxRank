package me.foxikle.foxrank;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveMsgs implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerJoinEvent event) {
        if(!FoxRank.getInstance().getConfig().getBoolean("DisableJoinMessages")) {
            event.setJoinMessage(null);
        }
        event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getName() + " joined the game.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!FoxRank.getInstance().getConfig().getBoolean("DisableLeaveMessages")) {
            event.setQuitMessage(null);
        }
        event.setQuitMessage(ChatColor.YELLOW + event.getPlayer().getName() + " left the game.");
    }
}
