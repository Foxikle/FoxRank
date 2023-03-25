package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static me.foxikle.foxrank.FoxRank.getRank;
import static me.foxikle.foxrank.FoxRank.pcl;
import static me.foxikle.foxrank.Rank.DEFAULT;
import static me.foxikle.foxrank.Rank.ofString;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.STRIKETHROUGH;

public class Listeners implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        FoxRank.getInstance().saveRank(p);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        String eventMessage = e.getMessage();
        String newMessage;
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (FoxRank.getInstance().useDb) {
            if (FoxRank.getInstance().isMuted(uuid)) {
                Instant date = Instant.parse(FoxRank.getInstance().db.getStoredMuteDuration(uuid));
                Instant now = Instant.now();
                if (date.isBefore(now)) {
                    ModerationAction.unmutePlayer(new RankedPlayer(player), new RankedPlayer(player));
                } else {
                    String reason = FoxRank.getInstance().db.getStoredMuteReason(uuid);
                    String border = RED + "" + STRIKETHROUGH + "                                                                   ";
                    String muteMessage = FoxRank.getInstance().getConfig().getString("ChatWhileMutedMessage").replace("$LINE", border);
                    muteMessage = muteMessage.replace("\\n", "\n");
                    muteMessage = muteMessage.replace("$DURATION", FoxRank.getInstance().getFormattedExpiredString(date, Instant.now()));
                    muteMessage = muteMessage.replace("$REASON", reason);
                    muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
                    player.sendMessage(muteMessage);
                    return;
                }
            }
            if (FoxRank.getInstance().db.getStoredNicknameStatus(uuid)) {
                Rank rank = FoxRank.getInstance().db.getStoredNicknameRank(uuid);
                String nick = FoxRank.getInstance().db.getStoredNickname(uuid);
                if (!FoxRank.getInstance().disableRankVis) {
                    if (rank == DEFAULT) {
                        newMessage = ChatColor.GRAY + nick + ": " + eventMessage;
                        Bukkit.broadcastMessage(newMessage);
                    } else {
                        newMessage = rank.getPrefix() + "" + e.getPlayer().getName() + ChatColor.RESET + ": " + eventMessage;
                        Bukkit.broadcastMessage(newMessage);
                    }
                } else {
                    Bukkit.broadcastMessage(nick + ": " + e.getMessage());
                }
            } else {

                if (!FoxRank.getInstance().disableRankVis) {
                    if (getRank(player) != DEFAULT) {
                        newMessage = getRank(player).getPrefix() + player.getName() + ChatColor.RESET + ": " + eventMessage;
                        Bukkit.broadcastMessage(newMessage);
                    } else if (getRank(player) == DEFAULT) {
                        newMessage = getRank(player).getPrefix() + player.getName() + ChatColor.RESET + "" + ChatColor.GRAY + ": " + eventMessage;
                        Bukkit.broadcastMessage(newMessage);
                    }
                } else {
                    Bukkit.broadcastMessage(player.getName() + ": " + e.getMessage());
                }
            }
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

            if (FoxRank.getInstance().isMuted(player.getUniqueId())) {
                Instant date = Instant.parse(yml.getString("MuteDuration"));
                Instant now = Instant.now();
                if (date.isBefore(now)) {
                    ModerationAction.unmutePlayer(new RankedPlayer(player), new RankedPlayer(player));
                } else {
                    String reason = yml.getString("MuteReason");
                    String border = RED + "" + STRIKETHROUGH + "                                                                   ";
                    String muteMessage = FoxRank.getInstance().getConfig().getString("ChatWhileMutedMessage").replace("$LINE", border);
                    muteMessage = muteMessage.replace("\\n", "\n");
                    muteMessage = muteMessage.replace("$DURATION", FoxRank.getInstance().getFormattedExpiredString(date, Instant.now()));
                    muteMessage = muteMessage.replace("$REASON", reason);
                    muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
                    player.sendMessage(muteMessage);
                    return;
                }
            }
            if (yml.getBoolean("isNicked")) {
                Rank rank = ofString(yml.getString("Nickname-Rank"));
                String nick = yml.getString("Nickname");
                if (!FoxRank.getInstance().disableRankVis) {

                    if (rank == DEFAULT) {
                        newMessage = ChatColor.GRAY + nick + ": " + eventMessage;
                        Bukkit.broadcastMessage(newMessage);
                    } else {
                        newMessage = rank.getPrefix() + "" + e.getPlayer().getName() + ChatColor.RESET + ": " + eventMessage;
                        Bukkit.broadcastMessage(newMessage);
                    }
                } else {
                    Bukkit.broadcastMessage(nick + ": " + e.getMessage());
                }
            } else {

                if (!FoxRank.instance.disableRankVis) {
                    if (getRank(player) != DEFAULT) {
                        newMessage = getRank(player).getPrefix() + player.getName() + ChatColor.RESET + ": " + eventMessage;
                        Bukkit.broadcastMessage(newMessage);
                    } else if (getRank(player) == DEFAULT) {
                        newMessage = getRank(player).getPrefix() + player.getName() + ChatColor.RESET + "" + ChatColor.GRAY + ": " + eventMessage;
                        Bukkit.broadcastMessage(newMessage);
                    }
                } else {
                    Bukkit.broadcastMessage(player.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void OnPlayerLogin(PlayerJoinEvent e) {
        FoxRank.getInstance().setupTeams();
        Player p = e.getPlayer();
        if (FoxRank.getInstance().useDb) {
            FoxRank.getInstance().db.addPlayerData(new RankedPlayer(p));
        } else {

            File file = new File("plugins/FoxRank/PlayerData/" + p.getUniqueId() + ".yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.addDefault("Name", p.getName());
            yml.addDefault("UUID", p.getUniqueId().toString());
            yml.addDefault("Rank", "DEFAULT");
            yml.addDefault("isVanished", false);
            yml.addDefault("isNicked", false);
            yml.addDefault("isMuted", false);
            yml.addDefault("MuteDuration", "");
            yml.addDefault("MuteReason", "");
            yml.addDefault("Nickname", p.getName());
            yml.addDefault("Nickname-Rank", "DEFAULT");
            yml.addDefault("Nickname-Skin", "");
            yml.addDefault("BanDuration", "");
            yml.addDefault("BanReason", "");
            yml.addDefault("BanID", "");
            yml.addDefault("isBanned", false);
            yml.options().copyDefaults(true);
            try {
                yml.save(file);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        ActionBar.setupActionBar(p);
        FoxRank.getInstance().loadRank(p);
        if (FoxRank.getInstance().isMuted(p.getUniqueId())) {
            if (FoxRank.getInstance().getMuteDuration(p.getUniqueId()).isBefore(Instant.now())) {
                ModerationAction.unmutePlayer(new RankedPlayer(p), new RankedPlayer(p));
            }
        }
        if (Bukkit.getOnlinePlayers().size() == 1) {

            Bukkit.getScheduler().runTaskLater(FoxRank.getInstance(), () -> {
                pcl.getPlayers(Iterables.getFirst(Bukkit.getOnlinePlayers(), null));
            }, 30);
        }
    }

    @EventHandler
    public void BanHandler(AsyncPlayerPreLoginEvent e) {
        if (FoxRank.getInstance().isBanned(e.getUniqueId())) {
            UUID uuid = e.getUniqueId();
            String bumper = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
            String reason;
            String duration;
            String id;
            Database db = FoxRank.instance.db;
            if (FoxRank.getInstance().useDb) {
                reason = db.getStoredBanReason(uuid);
                duration = db.getStoredBanDuration(uuid);
                id = db.getStoredBanID(uuid);
            } else {

                File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                reason = yml.getString("BanReason");
                duration = yml.getString("BanDuration");
                id = yml.getString("BanID");
            }

            if (duration == null) {
                String finalMessage = FoxRank.getInstance().getConfig().getString("PermBanMessageFormat")
                        .replace("$SERVER_NAME", FoxRank.getInstance().getConfig().getString("ServerName"))
                        .replace("$REASON", reason)
                        .replace("$APPEAL_LINK", FoxRank.getInstance().getConfig().getString("BanAppealLink"))
                        .replace("$ID", id)
                        .replace("\\n", "\n");
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('§', bumper + finalMessage + bumper));
            } else {
                Instant inst = Instant.parse(duration);
                if (Instant.now().isAfter(inst)) {
                    ModerationAction.unbanPlayer(uuid, null);
                    if (FoxRank.getInstance().useDb) {
                        List<OfflinePlayer> list = db.getStoredBannedPlayers();
                        if (list.contains(Bukkit.getOfflinePlayer(e.getUniqueId()))) {
                            list.remove(Bukkit.getOfflinePlayer(e.getUniqueId()));
                            db.setStoredBannedPlayers(list);
                            return;
                        }
                    } else {
                        File bannedPlayersFile = new File("plugins/FoxRank/bannedPlayers.yml");
                        YamlConfiguration bannedPlayersyml = YamlConfiguration.loadConfiguration(bannedPlayersFile);
                        List<String> list = bannedPlayersyml.getStringList("CurrentlyBannedPlayers");
                        if (list.contains(e.getUniqueId().toString())) {
                            if (list.remove(e.getUniqueId().toString())) {
                                bannedPlayersyml.set("CurrentlyBannedPlayers", list);
                                try {
                                    bannedPlayersyml.save(bannedPlayersFile);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    e.allow();
                    return;
                }
                String finalMessage = FoxRank.getInstance().getConfig().getString("TempBanMessageFormat")
                        .replace("$DURATION", FoxRank.getInstance().getFormattedExpiredString(inst, Instant.now()))
                        .replace("$SERVER_NAME", FoxRank.getInstance().getConfig().getString("ServerName"))
                        .replace("$REASON", reason)
                        .replace("$APPEAL_LINK", FoxRank.getInstance().getConfig().getString("BanAppealLink"))
                        .replace("$ID", id)
                        .replace("\\n", "\n");
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('§', bumper + finalMessage + bumper));
            }
        } else {
            e.allow();
        }
    }
}
