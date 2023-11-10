package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import me.foxikle.foxrank.Data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class Listeners implements Listener {

    private final FoxRank plugin;

    public Listeners(FoxRank plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().saveRank(p));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (plugin.getConfig().getBoolean("DisableChatFormatting")) return;
        if (e.isCancelled()) {
            return;
        }
        plugin.getDm().handleEventMessage(e);
    }

    @EventHandler
    public void OnPlayerLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDm().setupPlayerInfoStorage(p);
            plugin.getDm().updatePlayerName(e.getPlayer());
            plugin.getDm().cacheUserData(p.getUniqueId());
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                plugin.loadRank(p);
                if (plugin.getPlayerData(p.getUniqueId()).isMuted()) {
                    if (plugin.getPlayerData(p.getUniqueId()).getMuteDuration().isBefore(Instant.now())) {
                        ModerationAction.unmutePlayer(p, p);
                    }
                }
                if (Bukkit.getOnlinePlayers().size() == 1) {
                    if (plugin.bungeecord) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> FoxRank.pcl.getPlayers(Iterables.getFirst(Bukkit.getOnlinePlayers(), null)), 30);
                    }
                }

                if (plugin.getPlayerData(p.getUniqueId()).isNicked()) {
                    Nick.changeName(plugin.getPlayerData(p.getUniqueId()).getNickname(), p);
                    Bukkit.getScheduler().runTask(plugin, () -> Nick.loadSkin(p));
                    plugin.setTeam(p, plugin.getPlayerData(p.getUniqueId()).getNicknameRank().getId());
                }
                if (plugin.getDm().isVanished(p.getUniqueId())) {
                    Bukkit.getScheduler().runTask(plugin, () -> Vanish.vanishPlayer(p));
                }
            }, 1);
        });

        for (Player player : plugin.vanishedPlayers) {
            p.hidePlayer(plugin, player);
        }
    }

    @EventHandler
    public void BanHandler(AsyncPlayerPreLoginEvent e) {
        PlayerData data = plugin.getPlayerData(e.getUniqueId());
        if(data == null) {  // We don't have data, so they can't be banned
            e.allow();
            return;
        }
        if (data.isBanned()) {
            UUID uuid = e.getUniqueId();

            if (data.getBanDuration() == null) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, plugin.getMessage("PermBanMessageFormat", Bukkit.getOfflinePlayer(uuid)));
            } else {
                String duration = data.getBanDuration().toString();
                Instant inst = Instant.parse(duration);
                if (Instant.now().isAfter(inst)) {
                    ModerationAction.unbanPlayer(uuid, uuid);
                    Set<UUID> set = plugin.getDm().getBannedPlayers();
                    if (set.contains(e.getUniqueId())) {
                        set.remove(e.getUniqueId());
                        plugin.getDm().setBannedPlayers(set);
                        return;
                    }
                    e.allow();
                    return;
                }
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, plugin.getMessage("TempBanMessageFormat", Bukkit.getOfflinePlayer(uuid)));
            }
        } else {
            e.allow();
        }
    }
}
