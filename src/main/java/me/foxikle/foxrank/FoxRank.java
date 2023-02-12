package me.foxikle.foxrank;

import com.google.gson.JsonParser;
import me.foxikle.foxrank.events.RankChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;

import static java.util.Objects.hash;
import static me.foxikle.foxrank.Rank.*;
import static org.bukkit.ChatColor.*;

public class FoxRank extends JavaPlugin implements Listener {

    protected static FoxRank instance;
    protected final boolean disableRankVis = this.getConfig().getBoolean("DisableRankVisiblity");
    protected boolean useDb = this.getConfig().getBoolean("useSQLStorage");
    protected Database db;
    static Map<Player, Rank> ranks = new HashMap<>();
    private static Team DefualtTeam = null;
    private static Team OwnerTeam = null;
    private static Team AdminTeam = null;
    private static Team ModeratorTeam = null;
    private static Team YoutubeTeam = null;
    private static Team TwitchTeam = null;
    private static Team MvppTeam = null;
    private static Team MvpTeam = null;
    private static Team VippTeam = null;
    private static Team VipTeam = null;

    protected static FoxRank getInstance() {
        return instance;
    }

    protected static void setTeam(Player player, String teamID) {
        if (!getInstance().disableRankVis) {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            player.setScoreboard(board);
            switch (teamID) {
                case "OWNER" -> OwnerTeam.addEntry(player.getName());
                case "ADMIN" -> AdminTeam.addEntry(player.getName());
                case "MODERATOR" -> ModeratorTeam.addEntry(player.getName());
                case "YOUTUBE" -> YoutubeTeam.addEntry(player.getName());
                case "TWITCH" -> TwitchTeam.addEntry(player.getName());
                case "MVP_PLUS" -> MvppTeam.addEntry(player.getName());
                case "MVP" -> MvpTeam.addEntry(player.getName());
                case "VIP_PLUS" -> VippTeam.addEntry(player.getName());
                case "VIP" -> VipTeam.addEntry(player.getName());
                case "DEFAULT" -> DefualtTeam.addEntry(player.getName());
            }
        }
    }

    protected static Rank getRank(Player player) {
        return ranks.get(player);
    }

    protected static Rank getOfflineRank(OfflinePlayer player) {
        if (getInstance().useDb) {
            return getInstance().db.getStoredRank(player.getUniqueId());
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            return Rank.ofString(yml.getString("Rank"));
        }
    }

    @Override
    public void onEnable() {
        if (useDb) {
            try {
                db.connect();
            } catch (ClassNotFoundException | SQLException ignored) {
                Bukkit.getLogger().log(Level.SEVERE, "Invalid database credentials. Disabling plugin.");
                this.getPluginLoader().disablePlugin(this);
                return;
            }
            Bukkit.getLogger().log(Level.INFO, "Database connected.");
            db.createPlayerDataTable();
            db.createBannedPlayersTable();

        } else {
            if (!new File("plugins/FoxRank/auditlog.yml").exists()) {
                this.saveResource("auditlog.yml", false);
            } else if (!new File("plugins/FoxRank/bannedPlayers.yml").exists()) {
                this.saveResource("bannedPlayers.yml", false);
            }
        }
        if (!new File("plugins/FoxRank/config.yml").exists()) {
            this.saveResource("config.yml", false);
        }
        setupTeams();
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new Vanish(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMsgs(), this);
        getServer().getPluginManager().registerEvents(new Logs(), this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        reloadConfig();
        for (Player p : this.getServer().getOnlinePlayers()) {
            db.addPlayerData(new RankedPlayer(p));
            loadRank(p);
            ActionBar.setupActionBar(p);
        }
        getCommand("nick").setExecutor(new Nick());
        getCommand("vanish").setExecutor(new Vanish());
        getCommand("setrank").setExecutor(new SetRank());
        getCommand("mute").setExecutor(new Mute());
        getCommand("me").setExecutor(new Mute());
        getCommand("say").setExecutor(new Mute());
        getCommand("immuted").setExecutor(new Mute());
        getCommand("unmute").setExecutor(new Mute());
        getCommand("logs").setExecutor(new Logs());
        getCommand("ban").setExecutor(new Ban());
        getCommand("unban").setExecutor(new Unban());
    }

    @Override
    public void onDisable() {
        for (Player p : this.getServer().getOnlinePlayers()) {
            saveRank(p);
        }
        DefualtTeam.unregister();
        OwnerTeam.unregister();
        AdminTeam.unregister();
        ModeratorTeam.unregister();
        YoutubeTeam.unregister();
        TwitchTeam.unregister();
        MvppTeam.unregister();
        MvpTeam.unregister();
        VippTeam.unregister();
        VipTeam.unregister();
        db.disconnect();
    }

    void setupTeams() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        if (!disableRankVis) {
            try {
                Team dT = board.registerNewTeam(DEFAULT.getRankID());
                dT.setColor(ChatColor.GRAY);
                dT.setPrefix(DEFAULT.getPrefix());
                Team oT = board.registerNewTeam(OWNER.getRankID());
                oT.setColor(RED);
                oT.setPrefix(OWNER.getPrefix());
                Team aT = board.registerNewTeam(ADMIN.getRankID());
                aT.setColor(RED);
                aT.setPrefix(ADMIN.getPrefix());
                Team moT = board.registerNewTeam(MODERATOR.getRankID());
                moT.setColor(ChatColor.DARK_GREEN);
                moT.setPrefix(MODERATOR.getPrefix());
                Team yT = board.registerNewTeam(YOUTUBE.getRankID());
                yT.setColor(RED);
                yT.setPrefix(YOUTUBE.getPrefix());
                Team tT = board.registerNewTeam(TWITCH.getRankID());
                tT.setColor(ChatColor.DARK_PURPLE);
                tT.setPrefix(TWITCH.getPrefix());
                Team mpT = board.registerNewTeam(MVP_PLUS.getRankID());
                mpT.setColor(ChatColor.AQUA);
                mpT.setPrefix(MVP_PLUS.getPrefix());
                Team mT = board.registerNewTeam(MVP.getRankID());
                mT.setColor(ChatColor.AQUA);
                mT.setPrefix(MVP.getPrefix());
                Team vpT = board.registerNewTeam(VIP_PLUS.getRankID());
                vpT.setColor(ChatColor.GREEN);
                vpT.setPrefix(VIP_PLUS.getPrefix());
                Team vT = board.registerNewTeam(VIP.getRankID());
                vT.setColor(ChatColor.GREEN);
                vT.setPrefix(VIP.getPrefix());
                DefualtTeam = dT;
                OwnerTeam = oT;
                AdminTeam = aT;
                ModeratorTeam = moT;
                YoutubeTeam = yT;
                TwitchTeam = tT;
                MvppTeam = mpT;
                MvpTeam = mT;
                VippTeam = vpT;
                VipTeam = vT;
            } catch (IllegalArgumentException ignored) {
                Team dT = board.getTeam(DEFAULT.getRankID());
                Team oT = board.getTeam(OWNER.getRankID());
                Team aT = board.getTeam(ADMIN.getRankID());
                Team moT = board.getTeam(MODERATOR.getRankID());
                Team yT = board.getTeam(YOUTUBE.getRankID());
                Team tT = board.getTeam(TWITCH.getRankID());
                Team mpT = board.getTeam(MVP_PLUS.getRankID());
                Team mT = board.getTeam(MVP.getRankID());
                Team vpT = board.getTeam(VIP_PLUS.getRankID());
                Team vT = board.getTeam(VIP.getRankID());
                dT.setColor(ChatColor.GRAY);
                dT.setPrefix(DEFAULT.getPrefix());
                oT.setColor(RED);
                oT.setPrefix(OWNER.getPrefix());
                aT.setColor(RED);
                aT.setPrefix(ADMIN.getPrefix());
                moT.setColor(ChatColor.DARK_GREEN);
                moT.setPrefix(MODERATOR.getPrefix());
                yT.setColor(RED);
                yT.setPrefix(YOUTUBE.getPrefix());
                tT.setColor(ChatColor.DARK_PURPLE);
                tT.setPrefix(TWITCH.getPrefix());
                mpT.setColor(ChatColor.AQUA);
                mpT.setPrefix(MVP_PLUS.getPrefix());
                mT.setColor(ChatColor.AQUA);
                mT.setPrefix(MVP.getPrefix());
                vpT.setColor(ChatColor.GREEN);
                vpT.setPrefix(VIP_PLUS.getPrefix());
                vT.setColor(ChatColor.GREEN);
                vT.setPrefix(VIP.getPrefix());
                DefualtTeam = dT;
                OwnerTeam = oT;
                AdminTeam = aT;
                ModeratorTeam = moT;
                YoutubeTeam = yT;
                TwitchTeam = tT;
                MvppTeam = mpT;
                MvpTeam = mT;
                VippTeam = vpT;
                VipTeam = vT;
            }
        } else {
            try {
                Team dT = board.registerNewTeam(DEFAULT.getRankID());
                dT.setColor(ChatColor.WHITE);
                dT.setPrefix(" ");
                Team oT = board.registerNewTeam(OWNER.getRankID());
                oT.setColor(ChatColor.WHITE);
                oT.setPrefix(" ");
                Team aT = board.registerNewTeam(ADMIN.getRankID());
                aT.setColor(ChatColor.WHITE);
                aT.setPrefix(" ");
                Team moT = board.registerNewTeam(MODERATOR.getRankID());
                moT.setColor(ChatColor.WHITE);
                moT.setPrefix(" ");
                Team yT = board.registerNewTeam(YOUTUBE.getRankID());
                yT.setColor(ChatColor.WHITE);
                yT.setPrefix(" ");
                Team tT = board.registerNewTeam(TWITCH.getRankID());
                tT.setColor(ChatColor.WHITE);
                tT.setPrefix(" ");
                Team mpT = board.registerNewTeam(MVP_PLUS.getRankID());
                mpT.setColor(ChatColor.WHITE);
                mpT.setPrefix(" ");
                Team mT = board.registerNewTeam(MVP.getRankID());
                mT.setColor(ChatColor.WHITE);
                mT.setPrefix(" ");
                Team vpT = board.registerNewTeam(VIP_PLUS.getRankID());
                vpT.setColor(ChatColor.WHITE);
                vpT.setPrefix(" ");
                Team vT = board.registerNewTeam(VIP.getRankID());
                vT.setColor(ChatColor.WHITE);
                vT.setPrefix(" ");
                DefualtTeam = dT;
                OwnerTeam = oT;
                AdminTeam = aT;
                ModeratorTeam = moT;
                YoutubeTeam = yT;
                TwitchTeam = tT;
                MvppTeam = mpT;
                MvpTeam = mT;
                VippTeam = vpT;
                VipTeam = vT;
            } catch (IllegalArgumentException ignored) {
                Team dT = board.getTeam(DEFAULT.getRankID());
                Team oT = board.getTeam(OWNER.getRankID());
                Team aT = board.getTeam(ADMIN.getRankID());
                Team moT = board.getTeam(MODERATOR.getRankID());
                Team yT = board.getTeam(YOUTUBE.getRankID());
                Team tT = board.getTeam(TWITCH.getRankID());
                Team mpT = board.getTeam(MVP_PLUS.getRankID());
                Team mT = board.getTeam(MVP.getRankID());
                Team vpT = board.getTeam(VIP_PLUS.getRankID());
                Team vT = board.getTeam(VIP.getRankID());
                DefualtTeam = dT;
                OwnerTeam = oT;
                AdminTeam = aT;
                ModeratorTeam = moT;
                YoutubeTeam = yT;
                TwitchTeam = tT;
                MvppTeam = mpT;
                MvpTeam = mT;
                VippTeam = vpT;
                VipTeam = vT;
                dT.setColor(ChatColor.WHITE);
                dT.setPrefix("");
                oT.setColor(ChatColor.WHITE);
                oT.setPrefix("");
                aT.setColor(ChatColor.WHITE);
                aT.setPrefix("");
                moT.setColor(ChatColor.WHITE);
                moT.setPrefix("");
                yT.setColor(ChatColor.WHITE);
                yT.setPrefix("");
                tT.setColor(ChatColor.WHITE);
                tT.setPrefix("");
                mpT.setColor(ChatColor.WHITE);
                mpT.setPrefix("");
                mT.setColor(ChatColor.WHITE);
                mT.setPrefix("");
                vpT.setColor(ChatColor.WHITE);
                vpT.setPrefix("");
                vT.setColor(ChatColor.WHITE);
                vT.setPrefix("");
            }
        }
    }

    protected void setRank(Player player, Rank rank) {
        this.getServer().getPluginManager().callEvent(new RankChangeEvent(player, rank));
        ranks.put(player, rank);
        if (useDb) {
            db.setStoredRank(player.getUniqueId(), rank);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("Rank", rank.getRankID());
            try {
                yml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (disableRankVis) {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getDisplayName());
        } else {
            player.setDisplayName(rank.getPrefix() + player.getName());
            player.setPlayerListName(rank.getPrefix() + player.getName());
        }

    }

    protected void setRankOfflinePlayer(OfflinePlayer player, Rank rank) {
        if (useDb) {
            db.setStoredRank(player.getUniqueId(), rank);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("Rank", rank.getRankID());
            try {
                yml.save(file);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    protected void loadRank(Player player) {
        String rankID;
        if (useDb) {
            rankID = db.getStoredRank(player.getUniqueId()).getRankID();
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            rankID = yml.getString("Rank");
        }
        Rank rank = ofString(rankID);
        setTeam(player, rank.getRankID());
        setRank(player, rank);
    }


    protected void saveRank(Player player) {
        if (useDb) {
            db.setStoredRank(player.getUniqueId(), getRank(player));
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("Rank", getRank(player).getRankID());
            try {
                yml.save(file);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        ranks.remove(player);
    }

    protected String getFormattedExpiredString(Instant date, Instant now) {
        if (date == null) {
            return "Permanant";
        } else {
            Instant temp = date;
            long days = ChronoUnit.DAYS.between(now, temp);
            temp = temp.minusSeconds(days * 24 * 60 * 60);
            long hours = ChronoUnit.HOURS.between(now, temp);
            temp = temp.minusSeconds(hours * 60 * 60);
            long minutes = ChronoUnit.MINUTES.between(now, temp);
            temp = temp.minusSeconds(minutes * 60);
            long seconds = ChronoUnit.SECONDS.between(now, temp);
            String str = "";
            if (days != 0) {
                str = str + days + "d ";
            }
            if (hours != 0) {
                str = str + hours + "h ";
            }
            if (minutes != 0) {
                str = str + minutes + "m ";
            }
            if (seconds != 0) {
                str = str + seconds + "s";
            }
            return str;
        }
    }

    protected boolean isVanished(UUID uuid) {
        if (useDb) {
            return db.getStoredVanishStatus(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            return yml.getBoolean("isVanished");
        }
    }

    protected boolean isNicked(UUID uuid) {
        if (useDb) {
            return db.getStoredNicknameStatus(uuid);
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isNicked");
    }

    protected boolean isBanned(UUID uuid) {
        if (useDb) {
            return db.getStoredBanStatus(uuid);
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isBanned");
    }

    protected boolean isMuted(UUID uuid) {
        if (useDb) {
            return db.getStoredMuteStatus(uuid);
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isMuted");
    }

    protected String getMuteReason(UUID uuid) {
        if (useDb) {
            return db.getStoredMuteReason(uuid);
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getString("MuteReason");
    }

    protected Instant getMuteDuration(UUID uuid) {
        if (useDb) {
            return Instant.parse(db.getStoredMuteDuration(uuid));
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return Instant.parse(yml.getString("MuteDuration"));
    }

    protected void mutePlayer(RankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
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
        Logging.addMuteLogEntry(rp, duration, reason, admin, id);

        String border = RED + "" + STRIKETHROUGH + "                                                                   ";
        String muteMessage = FoxRank.getInstance().getConfig().getString("MuteMessage").replace("$LINE", border);
        muteMessage = muteMessage.replace("\\n", "\n");
        muteMessage = muteMessage.replace("$DURATION", getFormattedExpiredString(duration, Instant.now()));
        muteMessage = muteMessage.replace("$REASON", reason);
        muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
        rp.sendMessage(muteMessage);

    }

    protected void unmutePlayer(RankedPlayer rp, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredMuteData(rp.getUniqueId(), false, getMuteReason(rp.getUniqueId()), getMuteDuration(rp.getUniqueId()));
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
        Logging.addUnmuteLogEntry(rp, staff, id);
        String border = GREEN + "" + STRIKETHROUGH + "                                                                     ";
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnmuteRecieverMessage").replace("$LINE", border).replace("\\n", "\n")));
    }

    protected void unmuteOfflinePlayer(OfflinePlayer p, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + p.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredMuteData(p.getUniqueId(), false, getMuteReason(p.getUniqueId()), getMuteDuration(p.getUniqueId()));
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
        Logging.addOfflinePlayerUnmuteLogEntry(new OfflineRankedPlayer(p), staff, id);
    }

    protected void muteOfflinePlayer(OfflineRankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
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
        Logging.addOfflinePlayerMuteLogEntry(rp, duration, reason, admin, id);
    }

    protected void unbanOfflinePlayer(UUID rp, UUID staff) {
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
        Logging.addUnbanLogEntry(new OfflineRankedPlayer(Bukkit.getOfflinePlayer(rp)), staff, id);
    }

    protected void banOfflinePlayer(OfflineRankedPlayer rp, Instant duration, String reason, RankedPlayer admin, boolean silent) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredBanData(rp.getUniqueId(), true, reason, duration, id);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + rp.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isBanned", true);
            yml.set("BanDuration", duration.toString());
            yml.set("BanReason", reason);
            yml.set("BanID", id);
            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp.getUniqueId() + "'s Mute status!");
                e.printStackTrace();
            }
        }
        Logging.addOfflineBanLogEntry(rp, admin, Instant.now(), reason, duration.toString(), id, silent);
    }

    protected void banOnlinePlayer(RankedPlayer rp, Instant duration, String reason, RankedPlayer admin, boolean silent) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT).replace("-", "");
        if (useDb) {
            db.setStoredBanData(rp.getUniqueId(), true, reason, duration, id);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + rp.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isBanned", true);
            yml.set("BanDuration", duration.toString());
            yml.set("BanReason", reason);
            yml.set("BanID", id);
            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp.getUniqueId() + "'s Mute status!");
                e.printStackTrace();
            }
        }
        Logging.addOnlineBanLogEntry(rp, admin, Instant.now(), reason, duration.toString(), id, silent);
    }


    public void sendNoPermissionMessage(int powerLevel, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("NoPermissionMessage").replace("$POWERLEVEL", powerLevel + "").replace("\\n", "\n")));
    }

    public void sendMissingArgsMessage(String command, String args, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("MissingArgsMessage").replace("$COMMAND", command + "").replace("$ARGS", args)));
    }

    public void sendInvalidArgsMessage(String args, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("InvalidArgumentMessage").replace("$ARGTYPE", args)));
    }

    protected String getTrueName(UUID uuid) {
        URL url;
        try {
            url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            return new JsonParser().parse(reader).getAsJsonObject().get("name").getAsString();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could get a player's name from Mojang");
        }
        return null;
    }

    protected String getNickname(UUID uuid) {
        if (isNicked(uuid)) {
            if (useDb) {
                return db.getStoredNickname(uuid);
            } else {
                File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                return yml.getString("Nickname");
            }
        } else {
            return getTrueName(uuid);
        }
    }

    protected UUID getUUID(String name) {
        URL url;
        InputStreamReader reader = null;
        try {
            url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String raw = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
        UUID uid = UUID.fromString(raw.substring(0, 8) + "-" + raw.substring(8, 12) + "-" + raw.substring(12, 16) + "-" + raw.substring(16, 20) + "-" + raw.substring(20, 32));
        return uid;
    }


    public List<OfflinePlayer> getBannedPlayers() {
        if (useDb) {
            return db.getStoredBannedPlayers();
        }
        File file = new File("plugins/FoxRank/bannedPlayers.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        List<OfflinePlayer> players = List.of();
        if (yml.getStringList("BannedPlayers").isEmpty()) {
            return List.of();
        }
        for (String str : yml.getStringList("BannedPlayers")) {
            players.add(Bukkit.getOfflinePlayer(UUID.fromString(str)));
        }
        return players;
    }
}