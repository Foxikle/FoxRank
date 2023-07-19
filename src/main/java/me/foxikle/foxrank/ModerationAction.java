package me.foxikle.foxrank;

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
import static org.bukkit.ChatColor.*;

public class ModerationAction {

    private static final FoxRank plugin = FoxRank.getInstance();

    public static void mutePlayer(RankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        FoxRank.getInstance().dm.mutePlayer(rp.getUniqueId(), duration, reason);
        Logging.addLogEntry(EntryType.MUTE, rp.getUniqueId(), admin.getUniqueId(), duration, reason, null, id);

        String border = RED + String.valueOf(STRIKETHROUGH) + "                                                                   ";
        String muteMessage = plugin.getConfig().getString("MuteMessage").replace("$LINE", border);
        muteMessage = muteMessage.replace("\\n", "\n");
        muteMessage = muteMessage.replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now()));
        muteMessage = muteMessage.replace("$REASON", reason);
        muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
        if (plugin.bungeecord) {
            plugin.getPluginChannelListener().sendMessage(admin.getPlayer(), rp.getPlayer().getName(), muteMessage);
        } else {
            rp.sendMessage(muteMessage);
        }
    }

    public static void unmutePlayer(RankedPlayer rp, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        plugin.dm.unmutePlayer(rp.getUniqueId());
        Logging.addLogEntry(EntryType.UNMUTE, rp.getUniqueId(), staff.getUniqueId(), null, null, null, id);
        String border = GREEN + String.valueOf(STRIKETHROUGH) + "                                                                     ";
        if (plugin.bungeecord) {
            plugin.getPluginChannelListener().sendMessage(staff.getPlayer(), rp.getPlayer().getName(), ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("UnmuteRecieverMessage").replace("$LINE", border).replace("\\n", "\n")));
            return;
        }
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("UnmuteRecieverMessage").replace("$LINE", border).replace("\\n", "\n")));
    }

    public static void unmuteOfflinePlayer(OfflinePlayer p, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + p.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        plugin.dm.unmutePlayer(p.getUniqueId());
        Logging.addLogEntry(EntryType.UNMUTE, p.getUniqueId(), staff.getUniqueId(), null, null, null, id);
    }

    public static void muteOfflinePlayer(OfflineRankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");

        if (plugin.bungeecord) {
            String border = RED + String.valueOf(STRIKETHROUGH) + "                                                                   ";
            String muteMessage = plugin.getConfig().getString("MuteMessage").replace("$LINE", border);
            muteMessage = muteMessage.replace("\\n", "\n");
            muteMessage = muteMessage.replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now()));
            muteMessage = muteMessage.replace("$REASON", reason);
            muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
            plugin.getPluginChannelListener().sendMessage(admin.getPlayer(), rp.getName(), muteMessage);
        }
        plugin.dm.mutePlayer(rp.getUniqueId(), duration, reason);
        Logging.addLogEntry(EntryType.MUTE, rp.getUniqueId(), admin.getUniqueId(), duration, reason, null, id);
    }

    public static void unbanPlayer(UUID uuid, UUID staff) {
        if (staff == null) {
            staff = uuid;
        }
        String id = Integer.toString(hash("FoxRank:" + uuid + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        plugin.dm.unbanPlayer(uuid);
        Logging.addLogEntry(EntryType.UNBAN, uuid, staff, null, null, null, id);
    }

    public static void banPlayer(@Nullable RankedPlayer banner, Player banee, boolean silent, String reasonStr, Instant duration, String broadcastReason, String banID) {

        plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(banee, banner.getPlayer(), new RankedPlayer(banee.getPlayer(), plugin).getRank(), (banner == null ? null : banner.getRank()), me.foxikle.foxrank.events.ModerationAction.BAN, reasonStr, duration, banID));
        String bumper = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        if (!plugin.dm.getBannedPlayers().contains(banee.getUniqueId())) {
            if (duration != null) {
                if (banner == null) {
                    if (plugin.bungeecord) {
                        plugin.getPluginChannelListener().kickPlayer(banee, banee.getName(), bumper + plugin.getConfig().getString("TempBanMessageFormat").replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
                    } else {
                        banee.kickPlayer(bumper + plugin.getConfig().getString("TempBanMessageFormat").replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
                    }
                } else {
                    if (plugin.bungeecord) {
                        plugin.getPluginChannelListener().kickPlayer(banner.getPlayer(), banee.getName(), bumper + plugin.getConfig().getString("TempBanMessageFormat").replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
                    } else {
                        banee.kickPlayer(bumper + plugin.getConfig().getString("TempBanMessageFormat").replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
                    }
                }
            } else {
                if (banner == null) {
                    if (plugin.bungeecord) {
                        plugin.getPluginChannelListener().kickPlayer(banee, banee.getName(), bumper + plugin.getConfig().getString("TempBanMessageFormat").replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
                    } else {
                        banee.kickPlayer(bumper + plugin.getConfig().getString("TempBanMessageFormat").replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
                    }
                } else {
                    if (plugin.bungeecord) {
                        plugin.getPluginChannelListener().kickPlayer(banner.getPlayer(), banee.getName(), bumper + plugin.getConfig().getString("PermBanMessageFormat").replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
                    } else {
                        banee.kickPlayer(bumper + plugin.getConfig().getString("PermBanMessageFormat").replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
                    }
                }
            }
            if (!broadcastReason.equalsIgnoreCase("SECURITY")) { // Maybe remove this? Dunno. ¯\_(ツ)_/¯
                if (silent) {
                    if (banner != null) {
                        banner.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("SilentBanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", reasonStr)));

                    }
                } else {
                    if (broadcastReason.replace("_", "").equalsIgnoreCase("CUSTOM")) {
                        Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + banee.getName() + " was removed from your game.");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + banee.getName() + " was removed from your game for " + broadcastReason.replace("_", ""));
                    }
                    banee.getWorld().playSound(banner.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1000f, 1);
                    if (banner != null)
                        banner.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("BanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", "'" + reasonStr + "'")));
                }
            } else {
                banner.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("SecurityBanSenderMessage").replace("$PLAYER", banee.getName())));
            }
            plugin.dm.banPlayer(banee.getUniqueId(), reasonStr, banID, duration);
            Logging.addLogEntry(EntryType.BAN, banee.getUniqueId(), banner.getUniqueId(), duration, reasonStr, String.valueOf(silent), banID);
        }
    }

    public static void banOfflinePlayer(RankedPlayer banner, OfflinePlayer banee, String reasonStr, Instant duration, String broadcastReason, String banID) {
        plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(banee.getPlayer(), banner.getPlayer(), new RankedPlayer(banee.getPlayer(), plugin).getRank(), banner.getRank(), me.foxikle.foxrank.events.ModerationAction.BAN, reasonStr, duration, banID));
        String bumper = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        if (duration != null) {
            if (plugin.bungeecord) {
                plugin.getPluginChannelListener().kickPlayer(banner.getPlayer(), banee.getName(), bumper + plugin.getConfig().getString("TempBanMessageFormat").replace("$DURATION", plugin.getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
            }
        } else {
            if (plugin.bungeecord) {
                plugin.getPluginChannelListener().kickPlayer(banner.getPlayer(), banee.getName(), bumper + plugin.getConfig().getString("PermBanMessageFormat").replace("$SERVER_NAME", plugin.getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", plugin.getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
            }
        }

        if (broadcastReason.equalsIgnoreCase("SECURITY")) {
            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("SecurityBanSenderMessage").replace("$PLAYER", banee.getName())));
        } else {
            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("BanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", reasonStr)));
        }
        Logging.addLogEntry(EntryType.BAN, banee.getUniqueId(), banner.getUniqueId(), duration, reasonStr, "true", banID);
        if (!plugin.dm.getBannedPlayers().contains(banee)) {
            plugin.dm.banPlayer(banee.getUniqueId(), reasonStr, banID, duration);
        }
    }
}
