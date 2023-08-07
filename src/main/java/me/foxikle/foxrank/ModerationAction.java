package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import me.foxikle.foxrank.Data.PlayerData;
import me.foxikle.foxrank.events.ModerationActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.hash;

public class ModerationAction {

    private static final FoxRank plugin = FoxRank.getInstance();

    public static void mutePlayer(Player mutee, Instant duration, String reason, Player admin) {
        PlayerData pd = plugin.getPlayerData(mutee.getUniqueId());
        pd.setMuted(true);
        pd.setMuteDuration(duration);
        pd.setMuteReason(reason);
        String id = Integer.toString(hash("FoxRank:" + mutee.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> FoxRank.getInstance().getDm().mutePlayer(mutee.getUniqueId(), duration, reason));
        Logging.addLogEntry(EntryType.MUTE, mutee.getUniqueId(), admin.getUniqueId(), duration, reason, null, id);

        String muteMessage;
        if(duration == null) {
            muteMessage = plugin.getMessage("PermanentMuteMessage", mutee);
        } else {
            muteMessage = plugin.getMessage("MuteMessage", mutee);
        }

        if (plugin.bungeecord) {
            plugin.getPluginChannelListener().sendMessage(admin, mutee.getName(), muteMessage);
        } else {
            mutee.sendMessage(muteMessage);
        }
    }

    public static void unmutePlayer(Player unmutee, Player staff) {
        PlayerData pd = plugin.getPlayerData(unmutee.getUniqueId());
        pd.setMuted(false);
        String id = Integer.toString(hash("FoxRank:" + unmutee.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().unmutePlayer(unmutee.getUniqueId()));
        Logging.addLogEntry(EntryType.UNMUTE, unmutee.getUniqueId(), staff.getUniqueId(), null, null, null, id);
        if (plugin.bungeecord) {
            plugin.getPluginChannelListener().sendMessage(staff, unmutee.getName(), plugin.getMessage("UnmuteRecieverMessage", unmutee));
        } else {
            unmutee.sendMessage(plugin.getMessage("UnmuteRecieverMessage", unmutee.getPlayer()));
        }
        staff.sendMessage(plugin.getMessage("UnmuteSenderMessage", unmutee.getPlayer()));
    }

    public static void unmuteOfflinePlayer(OfflinePlayer unmutee, Player staff) {
        PlayerData pd = plugin.getPlayerData(unmutee.getUniqueId());
        pd.setMuted(false);
        String id = Integer.toString(hash("FoxRank:" + unmutee.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().unmutePlayer(unmutee.getUniqueId()));
        Logging.addLogEntry(EntryType.UNMUTE, unmutee.getUniqueId(), staff.getUniqueId(), null, null, null, id);
        staff.sendMessage(plugin.getMessage("UnmuteSenderMessage", unmutee));
    }

    public static void muteOfflinePlayer(OfflinePlayer mutee, Instant duration, String reason, Player admin) {
        PlayerData pd = plugin.getPlayerData(mutee.getUniqueId());
        pd.setMuted(true);
        pd.setMuteDuration(duration);
        pd.setMuteReason(reason);
        String id = Integer.toString(hash("FoxRank:" + mutee.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().mutePlayer(mutee.getUniqueId(), duration, reason));
        if (plugin.bungeecord) {
            String muteMessage;
            if(duration == null) {
                muteMessage = plugin.getMessage("PermanentMuteMessage", mutee);
            } else {
                muteMessage = plugin.getMessage("MuteMessage", mutee);
            }
            plugin.getPluginChannelListener().sendMessage(admin, mutee.getName(), muteMessage);
        }
        Logging.addLogEntry(EntryType.MUTE, mutee.getUniqueId(), admin.getUniqueId(), duration, reason, null, id);
        admin.sendMessage(plugin.getMessage("MuteSenderMessage", mutee));
    }

    public static void unbanPlayer(UUID uuid, UUID staff) {
        if (staff == null) {
            staff = uuid;
        }
        PlayerData pd = plugin.getPlayerData(uuid);
        pd.setBanned(false);
        String id = Integer.toString(hash("FoxRank:" + uuid + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin,  () -> plugin.getDm().unbanPlayer(uuid));
        plugin.getPlayerData(uuid).setBanned(false);
        Logging.addLogEntry(EntryType.UNBAN, uuid, staff, null, null, null, id);
    }

    public static void banPlayer(@Nullable Player banner, Player banee, boolean silent, String reasonStr, Instant duration, String broadcastReason, String banID) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().banPlayer(banee.getUniqueId(), reasonStr, banID, duration));
        PlayerData pd = plugin.getPlayerData(banee.getUniqueId());
        pd.setBanned(true);
        pd.setBanID(banID);
        pd.setBanDuration(duration);
        pd.setBanReason(reasonStr);
        plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(banee, banner, plugin.getPlayerData(banee.getUniqueId()).getRank(), (banner == null ? null : plugin.getRank(banner)), me.foxikle.foxrank.events.ModerationAction.BAN, reasonStr, duration, banID));
        if (!plugin.bannedPlayers.contains(banee.getUniqueId())) {
            if (plugin.bungeecord) {
                plugin.getPluginChannelListener().kickPlayer(Iterables.getLast(Bukkit.getOnlinePlayers()), banee.getName(), plugin.getMessage(duration == null ? "PermBanMessageFormat" : "TempBanMessageFormat", banee));
            } else {
                banee.kickPlayer(plugin.getMessage(duration == null ? "PermBanMessageFormat" : "TempBanMessageFormat", banee));
            }
            if (!broadcastReason.equalsIgnoreCase("SECURITY")) {
                if (silent) {
                    if (banner != null) {
                        banner.sendMessage(plugin.getMessage("SilentBanSenderMessage", banee));
                    }
                } else {
                    if (broadcastReason.replace("_", "").equalsIgnoreCase("CUSTOM")) { // todo: redo this pls to use messages
                        Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + banee.getName() + " was removed from your game.");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + banee.getName() + " was removed from your game for " + broadcastReason.replace("_", ""));
                    }
                    banee.getWorld().playSound(banner.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1000f, 1);
                    if (banner.getPlayer() != null)
                        banner.sendMessage(plugin.getMessage("BanSenderMessage", banee));
                }
            } else {
                banner.sendMessage(plugin.getMessage("SecurityBanSenderMessage", banee));
            }
            Logging.addLogEntry(EntryType.BAN, banee.getUniqueId(), banner.getUniqueId(), duration, reasonStr, String.valueOf(silent), banID);
        }
    }

    public static void banOfflinePlayer(Player banner, OfflinePlayer banee, String reasonStr, Instant duration, String broadcastReason, String banID) {
        plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(banee.getPlayer(), banner.getPlayer(), plugin.getPlayerData(banee.getUniqueId()).getRank(), plugin.getPlayerData(banner.getUniqueId()).getRank(), me.foxikle.foxrank.events.ModerationAction.BAN, reasonStr, duration, banID));
        PlayerData pd = plugin.getPlayerData(banee.getUniqueId());
        pd.setBanned(true);
        pd.setBanID(banID);
        pd.setBanDuration(duration);
        pd.setBanReason(reasonStr);
        if (plugin.bungeecord) {
            plugin.getPluginChannelListener().kickPlayer(Iterables.getLast(Bukkit.getOnlinePlayers()), banee.getName(), plugin.getMessage(duration == null ? "PermBanMessageFormat" : "TempBanMessageFormat", banee));
        }
        if (broadcastReason.equalsIgnoreCase("SECURITY")) {
            banner.sendMessage(plugin.getMessage("SecurityBanSenderMessage", banee));
        } else {
            banner.sendMessage(plugin.getMessage("BanSenderMessage", banee));
        }
        Logging.addLogEntry(EntryType.BAN, banee.getUniqueId(), banner.getUniqueId(), duration, reasonStr, "true", banID);
        if (!plugin.bannedPlayers.contains(banee.getUniqueId())) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().banPlayer(banee.getUniqueId(), reasonStr, banID, duration));
        }
    }
}
