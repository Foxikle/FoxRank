package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Listeners implements Listener {

    private final FoxRank plugin;

    public Listeners(FoxRank plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        plugin.dm.saveRank(p);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (plugin.getConfig().getBoolean("DisableChatFormatting")) return;
        if (e.isCancelled()) {
            return;
        }
        plugin.dm.handleEventMessage(e);
    }

    @EventHandler
    public void OnPlayerLogin(PlayerJoinEvent e) {
        //TODO: This should probably be re-worked ._.
        Player p = e.getPlayer();
        plugin.dm.updatePlayerName(e.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.setRank(p, plugin.dm.getStoredRank(p.getUniqueId()));
            ActionBar.setupActionBar(p);
            plugin.setTeam(e.getPlayer(), plugin.getRank(e.getPlayer()).getId());
            if (plugin.dm.isMuted(p.getUniqueId())) {
                if (plugin.dm.getMuteDuration(p.getUniqueId()).isBefore(Instant.now())) {
                    ModerationAction.unmutePlayer(new RankedPlayer(p, plugin), new RankedPlayer(p, plugin));
                }
            }
            if (Bukkit.getOnlinePlayers().size() == 1) {
                if (plugin.bungeecord) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> FoxRank.pcl.getPlayers(Iterables.getFirst(Bukkit.getOnlinePlayers(), null)), 30);
                }
            }

            if (plugin.dm.isNicked(p.getUniqueId())) {
                Nick.changeName(plugin.dm.getNickname(p.getUniqueId()), p);
                Nick.loadSkin(p);
                Nick.refreshPlayer(p);
                plugin.setTeam(p, plugin.dm.getNicknameRank(p.getUniqueId()).getId());
            }
            if (plugin.dm.isVanished(p.getUniqueId())) {
                plugin.vanishedPlayers.add(p);
                for (Player player : plugin.vanishedPlayers) {
                    player.hidePlayer(plugin, p);
                }
            }
        });
        for (Player player : plugin.vanishedPlayers) {
            p.hidePlayer(plugin, player);
        }
    }

    @EventHandler
    public void BanHandler(AsyncPlayerPreLoginEvent e) {
        if (plugin.dm.isBanned(e.getUniqueId())) {
            UUID uuid = e.getUniqueId();
            String bumper = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
            String reason = plugin.dm.getStoredBanReason(uuid);
            String duration = plugin.dm.getStoredBanDuration(uuid);
            String id = plugin.dm.getStoredBanID(uuid);

            if (duration == null) {
                String finalMessage = plugin.getConfig().getString("PermBanMessageFormat")
                        .replace("$SERVER_NAME", plugin.getConfig().getString("ServerName"))
                        .replace("$REASON", reason)
                        .replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink"))
                        .replace("$ID", id)
                        .replace("\\n", "\n");
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('§', bumper + finalMessage + bumper));
            } else {
                Instant inst = Instant.parse(duration);
                if (Instant.now().isAfter(inst)) {
                    ModerationAction.unbanPlayer(uuid, null);
                    List<UUID> list = plugin.dm.getBannedPlayers();
                    if (list.contains(e.getUniqueId())) {
                        list.remove(e.getUniqueId());
                        plugin.dm.setBannedPlayers(list);
                        return;
                    }
                    e.allow();
                    return;
                }
                String finalMessage = plugin.getConfig().getString("TempBanMessageFormat")
                        .replace("$DURATION", plugin.getFormattedExpiredString(inst, Instant.now()))
                        .replace("$SERVER_NAME", plugin.getConfig().getString("ServerName"))
                        .replace("$REASON", reason)
                        .replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink"))
                        .replace("$ID", id)
                        .replace("\\n", "\n");
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('§', bumper + finalMessage + bumper));
            }
        } else {
            e.allow();
        }
    }
}
