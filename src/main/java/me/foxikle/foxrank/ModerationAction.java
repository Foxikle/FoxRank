package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import me.clip.placeholderapi.PlaceholderAPI;
import me.foxikle.foxrank.events.ModerationActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.hash;
import static org.bukkit.ChatColor.*;

public class ModerationAction {

    private static final FoxRank plugin = FoxRank.getInstance();

    public static void mutePlayer(RankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> FoxRank.getInstance().getDm().mutePlayer(rp.getUniqueId(), duration, reason));
        Logging.addLogEntry(EntryType.MUTE, rp.getUniqueId(), admin.getUniqueId(), duration, reason, null, id);

        String muteMessage;
        if(duration == null) {
            muteMessage = plugin.getMessage("PermanentMuteMessage", rp.getPlayer());
        } else {
            muteMessage = plugin.getMessage("MuteMessage", rp.getPlayer());
        }

        if (plugin.bungeecord) {
            plugin.getPluginChannelListener().sendMessage(admin.getPlayer(), rp.getPlayer().getName(), muteMessage);
        } else {
            rp.sendMessage(muteMessage);
        }
    }

    public static void unmutePlayer(RankedPlayer rp, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().unmutePlayer(rp.getUniqueId()));
        Logging.addLogEntry(EntryType.UNMUTE, rp.getUniqueId(), staff.getUniqueId(), null, null, null, id);
        if (plugin.bungeecord) {
            plugin.getPluginChannelListener().sendMessage(staff.getPlayer(), rp.getPlayer().getName(), plugin.getMessage("UnmuteRecieverMessage", rp.getPlayer()));
        } else {
            rp.sendMessage(plugin.getMessage("UnmuteRecieverMessage", rp.getPlayer()));
        }
        staff.sendMessage(plugin.getMessage("UnmuteSenderMessage", rp.getPlayer()));
    }

    public static void unmuteOfflinePlayer(OfflinePlayer p, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + p.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().unmutePlayer(p.getUniqueId()));
        Logging.addLogEntry(EntryType.UNMUTE, p.getUniqueId(), staff.getUniqueId(), null, null, null, id);
        staff.sendMessage(plugin.getMessage("UnmuteSenderMessage", p));
    }

    public static void muteOfflinePlayer(OfflineRankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().mutePlayer(rp.getUniqueId(), duration, reason));
        if (plugin.bungeecord) {
            String muteMessage;
            if(duration == null) {
                muteMessage = plugin.getMessage("PermanentMuteMessage", rp.getOfflinePlayer());
            } else {
                muteMessage = plugin.getMessage("MuteMessage", rp.getOfflinePlayer());
            }
            plugin.getPluginChannelListener().sendMessage(admin.getPlayer(), rp.getName(), muteMessage);
        }
        Logging.addLogEntry(EntryType.MUTE, rp.getUniqueId(), admin.getUniqueId(), duration, reason, null, id);
        admin.sendMessage(plugin.getMessage("MuteSenderMessage", rp.getOfflinePlayer()));
    }

    public static void unbanPlayer(UUID uuid, UUID staff) {
        if (staff == null) {
            staff = uuid;
        }
        String id = Integer.toString(hash("FoxRank:" + uuid + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin,  () -> plugin.getDm().unbanPlayer(uuid));
        plugin.getPlayerData(uuid).setBanned(false);
        Logging.addLogEntry(EntryType.UNBAN, uuid, staff, null, null, null, id);
    }

    public static void banPlayer(@Nullable RankedPlayer banner, Player banee, boolean silent, String reasonStr, Instant duration, String broadcastReason, String banID) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().banPlayer(banee.getUniqueId(), reasonStr, banID, duration));
        plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(banee, banner.getPlayer(), new RankedPlayer(banee.getPlayer(), plugin).getRank(), (banner == null ? null : banner.getRank()), me.foxikle.foxrank.events.ModerationAction.BAN, reasonStr, duration, banID));
        if (!plugin.bannedPlayers.contains(banee.getUniqueId())) {
            if (plugin.bungeecord) {
                plugin.getPluginChannelListener().kickPlayer(Iterables.getLast(Bukkit.getOnlinePlayers()), banee.getName(), plugin.getMessage(duration == null ? "PermBanMessageFormat" : "TempBanMessageFormat", banee));
            } else {
                banee.kickPlayer(plugin.getMessage(duration == null ? "PermBanMessageFormat" : "TempBanMessageFormat", banee));
            }
            if (!broadcastReason.equalsIgnoreCase("SECURITY")) {
                if (silent) {
                    if (banner.getPlayer() != null) {
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

    public static void banOfflinePlayer(RankedPlayer banner, OfflinePlayer banee, String reasonStr, Instant duration, String broadcastReason, String banID) {
        plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(banee.getPlayer(), banner.getPlayer(), new RankedPlayer(banee.getPlayer(), plugin).getRank(), banner.getRank(), me.foxikle.foxrank.events.ModerationAction.BAN, reasonStr, duration, banID));
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
