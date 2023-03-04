package me.foxikle.foxrank;

import me.foxikle.foxrank.events.ModerationActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

import static java.util.Objects.hash;
import static org.bukkit.ChatColor.*;

public class ModerationAction {
    private static final boolean useDb = FoxRank.getInstance().useDb;
    private static final Database db = FoxRank.instance.db;

    protected static void mutePlayer(RankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredMuteData(rp.getUniqueId(), true, reason, duration);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + rp.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isMuted", true);
            yml.set("MuteDuration", duration.toString());
            yml.set("MuteReason", reason);
            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp.getUniqueId() + "'s Mute status!");
                e.printStackTrace();
            }
        }
        Logging.addLogEntry(EntryType.MUTE, new OfflineRankedPlayer(rp.getPlayer()), admin.getUniqueId(), duration, reason, null, id);

        String border = RED + "" + STRIKETHROUGH + "                                                                   ";
        String muteMessage = FoxRank.getInstance().getConfig().getString("MuteMessage").replace("$LINE", border);
        muteMessage = muteMessage.replace("\\n", "\n");
        muteMessage = muteMessage.replace("$DURATION", FoxRank.getInstance().getFormattedExpiredString(duration, Instant.now()));
        muteMessage = muteMessage.replace("$REASON", reason);
        muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
        rp.sendMessage(muteMessage);

    }

    protected static void unmutePlayer(RankedPlayer rp, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredMuteData(rp.getUniqueId(), false, rp.getMuteReason(), rp.getMuteDuration());
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + rp.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isMuted", false);

            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp.getUniqueId() + "'s Mute status");
                e.printStackTrace();
            }
        }
        Logging.addLogEntry(EntryType.UNMUTE, new OfflineRankedPlayer(rp.getPlayer()), staff.getUniqueId(), null, null, null, id);
        String border = GREEN + "" + STRIKETHROUGH + "                                                                     ";
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnmuteRecieverMessage").replace("$LINE", border).replace("\\n", "\n")));
    }

    protected static void unmuteOfflinePlayer(OfflinePlayer p, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + p.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredMuteData(p.getUniqueId(), false, db.getStoredMuteReason(p.getUniqueId()), Instant.parse(db.getStoredMuteDuration(p.getUniqueId())));
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + p.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isMuted", false);

            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + p.getUniqueId() + "'s Mute status");
                e.printStackTrace();
            }
        }
        Logging.addLogEntry(EntryType.UNMUTE, new OfflineRankedPlayer(p.getPlayer()), staff.getUniqueId(), null, null, null, id);

    }

    protected static void muteOfflinePlayer(OfflineRankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredMuteData(rp.getUniqueId(), true, reason, duration);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + rp.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isMuted", true);
            yml.set("MuteDuration", duration.toString());
            yml.set("MuteReason", reason);
            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp.getUniqueId() + "'s Mute status!");
                e.printStackTrace();
            }
        }
        Logging.addLogEntry(EntryType.MUTE, rp, admin.getUniqueId(), duration, reason, null, id);
    }

    protected static void unbanOfflinePlayer(UUID rp, UUID staff) {
        if (staff == null) {
            staff = rp;
        }
        String id = Integer.toString(hash("FoxRank:" + rp + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredBanData(rp, false, db.getStoredBanReason(rp), Instant.parse(db.getStoredBanDuration(rp)), db.getStoredBanID(rp));
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + rp + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isBanned", false);
            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp + "'s Ban status!");
                e.printStackTrace();
            }
        }
        Logging.addLogEntry(EntryType.UNBAN, new OfflineRankedPlayer(Bukkit.getOfflinePlayer(rp)), staff, null, null, null, id);
    }

    protected static void banPlayer(RankedPlayer banner, Player banee, boolean silent, String reasonStr, Instant duration, String broadcastReason, String banID) {
        FoxRank.getInstance().getServer().getPluginManager().callEvent(new ModerationActionEvent(banee, banner.getPlayer(), new RankedPlayer(banee.getPlayer()).getRank(), banner.getRank(), me.foxikle.foxrank.events.ModerationAction.BAN));
        String bumper = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        if (!FoxRank.getInstance().getBannedPlayers().contains(banee)) {
            if (duration != null) {
                banee.kickPlayer(bumper + FoxRank.getInstance().getConfig().getString("TempBanMessageFormat").replace("$DURATION", FoxRank.getInstance().getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", FoxRank.getInstance().getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", FoxRank.getInstance().getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
            } else {
                banee.kickPlayer(bumper + FoxRank.getInstance().getConfig().getString("PermBanMessageFormat").replace("$SERVER_NAME", FoxRank.getInstance().getConfig().getString("ServerName")).replace("$REASON", reasonStr).replace("$APPEAL_LINK", FoxRank.getInstance().getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
            }
            if (!broadcastReason.equalsIgnoreCase("SECURITY")) {
                if (silent) {
                    banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("SilentBanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", reasonStr)));
                } else {
                    if (broadcastReason.replace("_", "").equalsIgnoreCase("CUSTOM")) {
                        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + banee.getName() + " was removed from your game.");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + banee.getName() + " was removed from your game for " + broadcastReason.replace("_", ""));
                    }
                    banee.getWorld().playSound(banner.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1000f, 1);
                    banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("BanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", "'" + reasonStr + "'")));
                }
            } else {
                banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("SecurityBanSenderMessage").replace("$PLAYER", banee.getName())));
            }
            if (FoxRank.getInstance().useDb) {
                List<OfflinePlayer> player = FoxRank.getInstance().getBannedPlayers();
                player.add(banee);
                FoxRank.getInstance().db.setStoredBannedPlayers(player);
                FoxRank.getInstance().db.setStoredBanData(banee.getUniqueId(), true, reasonStr, duration, banID);
            } else {
                File file = new File("plugins/FoxRank/PlayerData/" + banee.getUniqueId() + ".yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                yml.set("BanReason", reasonStr);
                yml.set("BanDuration", duration.toString());
                yml.set("BanID", banID);
                yml.set("isBanned", true);


                try {
                    yml.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File file1 = new File("plugins/FoxRank/bannedPlayers.yml");
                YamlConfiguration yml1 = YamlConfiguration.loadConfiguration(file1);
                List<String> list = yml1.getStringList("CurrentlyBannedPlayers");
                list.add(banee.getUniqueId().toString());
                try {
                    yml1.set("CurrentlyBannedPlayers", list);
                    yml1.save(file1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Logging.addLogEntry(EntryType.BAN, new OfflineRankedPlayer(banee), banner.getUniqueId(), duration, reasonStr, silent + "", banID);
        }
    }

    protected static void banOfflinePlayer(RankedPlayer banner, OfflinePlayer banee, String reasonStr, Instant duration, String broadcastReason, String banID) {
        FoxRank.getInstance().getServer().getPluginManager().callEvent(new ModerationActionEvent(banee.getPlayer(), banner.getPlayer(), new RankedPlayer(banee.getPlayer()).getRank(), banner.getRank(), me.foxikle.foxrank.events.ModerationAction.BAN));

        if (broadcastReason.equalsIgnoreCase("SECURITY")) {
            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("SecurityBanSenderMessage").replace("$PLAYER", banee.getName())));
        } else {
            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("BanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", reasonStr)));
        }
        Logging.addLogEntry(EntryType.BAN, new OfflineRankedPlayer(banee), banner.getUniqueId(), duration, reasonStr, "true", banID);
        if (!FoxRank.getInstance().getBannedPlayers().contains(banee)) {
            if (FoxRank.getInstance().useDb) {
                List<OfflinePlayer> player = FoxRank.getInstance().getBannedPlayers();
                player.add(banee);
                FoxRank.getInstance().db.setStoredBannedPlayers(player);
            } else {
                File file = new File("plugins/FoxRank/PlayerData/" + banee.getUniqueId() + ".yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                yml.set("BanReason", reasonStr);
                yml.set("BanDuration", duration.toString());
                yml.set("BanID", banID);
                yml.set("isBanned", true);

                try {
                    yml.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
