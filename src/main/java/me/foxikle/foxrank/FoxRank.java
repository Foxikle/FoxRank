package me.foxikle.foxrank;

import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;

import static java.util.Objects.hash;
import static me.foxikle.foxrank.Rank.*;
import static org.bukkit.ChatColor.*;

public class FoxRank extends JavaPlugin implements Listener {

    protected static FoxRank instance;
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
        if (!instance.getConfig().getBoolean("DisableRankVisibility")) {
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
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return Rank.ofString(yml.getString("Rank"));
    }

    @Override
    public void onEnable() {
        setupTeams();
        if (!new File("plugins/FoxRank/config.yml").exists()) {
            this.saveResource("config.yml", false);
        } else if (!new File("plugins/FoxRank/auditlog.yml").exists()) {
            this.saveResource("auditlog.yml", false);
        } else if (!new File("plugins/FoxRank/bannedPlayers.yml").exists()) {
            this.saveResource("bannedPlayers.yml", false);
        }
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new Vanish(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMsgs(), this);
        getServer().getPluginManager().registerEvents(new Logs(), this);
        reloadConfig();
        for (Player p : this.getServer().getOnlinePlayers()) {
            loadRank(p);
            actionBar.setupActionBar(p);
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
    }

    private void setupTeams() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        if (!this.getConfig().getBoolean("DisableRankVisibility")) {
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
        ranks.put(player, rank);
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("Rank", rank.getRankID());
        if (this.getConfig().getBoolean("DisableRankVisibility")) {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getDisplayName());
        } else {
            player.setDisplayName(rank.getPrefix() + player.getName());
            player.setPlayerListName(rank.getPrefix() + player.getName());
        }
        try {
            yml.save(file);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    protected void setRankOfflinePlayer(OfflinePlayer player, Rank rank) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("Rank", rank.getRankID());
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadRank(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String rankID = yml.getString("Rank");
        if (rankID != null) {

            Rank rank = ofString(rankID);
            setTeam(player, rank.getRankID());
            setRank(player, rank);
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "rankID is null");
        }
    }

    protected void saveRank(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("Rank", getRank(player).getRankID());
        try {
            yml.save(file);
        } catch (IOException error) {
            error.printStackTrace();
        }
        ranks.remove(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        saveRank(p);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        String eventMessage = e.getMessage();
        String newMessage;
        Player player = e.getPlayer();
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        boolean disableVisibility = this.getConfig().getBoolean("DisableRankVisibility");
        if (isMuted(player.getUniqueId())) {
            Instant date = Instant.parse(yml.getString("MuteDuration"));
            Instant now = Instant.now();
            if (date.isBefore(now)) {
                unmutePlayer(new RankedPlayer(player), new RankedPlayer(player));
            } else {
                String reason = yml.getString("MuteReason");
                String border = RED + "" + STRIKETHROUGH + "                                                                   ";
                String muteMessage = FoxRank.getInstance().getConfig().getString("ChatWhileMutedMessage").replace("$LINE", border);
                muteMessage = muteMessage.replace("\\n", "\n");
                muteMessage = muteMessage.replace("$DURATION", getFormattedExpiredString(Instant.parse(yml.getString("MuteDuration")), Instant.now()));
                muteMessage = muteMessage.replace("$REASON", reason);
                muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
                player.sendMessage(muteMessage);
                return;
            }
        }
        if (yml.getString("isNicked").equals("true")) {
            if (!disableVisibility) {
                if (ofString(yml.getString("Nickname-Rank")) == DEFAULT) {
                    newMessage = ChatColor.GRAY + yml.getString("Nickname") + ": " + eventMessage;
                    Bukkit.broadcastMessage(newMessage);
                } else if (ofString(yml.getString("Nickname-Rank")) != DEFAULT) {
                    newMessage = ofString(yml.getString("Nickname-Rank")).getPrefix() + "" + yml.getString("Nickname") + ChatColor.RESET + ": " + eventMessage;
                    Bukkit.broadcastMessage(newMessage);
                }
            } else {
                Bukkit.broadcastMessage(yml.getString("Nickname") + ": " + e.getMessage());
            }
        } else {

            if (!disableVisibility) {
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

    @EventHandler
    public void OnPlayerLogin(PlayerJoinEvent e) {
        this.setupTeams();
        Player p = e.getPlayer();
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
        actionBar.setupActionBar(p);
        loadRank(p);
        if (this.isMuted(p.getUniqueId())) {
            if (this.getMuteDuration(p.getUniqueId()).isBefore(Instant.now())) {
                this.unmutePlayer(new RankedPlayer(p), new RankedPlayer(p));
            }
        }
    }

    protected boolean isVanished(UUID uuid) {
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isVanished");
    }

    protected boolean isNicked(UUID uuid) {
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isNicked");
    }

    protected boolean isBanned(UUID uuid) {
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isBanned");
    }

    protected boolean isMuted(UUID uuid) {
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isMuted");
    }

    protected String getMuteReason(UUID uuid) {
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getString("MuteReason");
    }

    protected Instant getMuteDuration(UUID uuid) {
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return Instant.parse(yml.getString("MuteDuration"));
    }

    protected void mutePlayer(RankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT);
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
        addMuteLogEntry(rp, duration, reason, admin, id);

        String border = RED + "" + STRIKETHROUGH + "                                                                   ";
        String muteMessage = FoxRank.getInstance().getConfig().getString("MuteMessage").replace("$LINE", border);
        muteMessage = muteMessage.replace("\\n", "\n");
        muteMessage = muteMessage.replace("$DURATION", getFormattedExpiredString(duration, Instant.now()));
        muteMessage = muteMessage.replace("$REASON", reason);
        muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
        rp.sendMessage(muteMessage);

    }

    protected void unmutePlayer(RankedPlayer rp, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT);
        File file = new File("plugins/FoxRank/PlayerData/" + rp.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("isMuted", false);
        addUnmuteLogEntry(rp, staff, id);
        String border = GREEN + "" + STRIKETHROUGH + "                                                                     ";
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnmuteRecieverMessage").replace("$LINE", border).replace("\\n", "\n")));
        try {
            yml.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp.getUniqueId() + "'s Mute status at line");
            e.printStackTrace();

        }
    }

    protected void unmuteOfflinePlayer(OfflinePlayer offlinePlayer, RankedPlayer staff) {
        String id = Integer.toString(hash("FoxRank:" + offlinePlayer.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT);
        File file = new File("plugins/FoxRank/PlayerData/" + offlinePlayer.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("isMuted", false);
        addOfflinePlayerUnmuteLogEntry(new OfflineRankedPlayer(offlinePlayer), staff, id);
        try {
            yml.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + offlinePlayer.getUniqueId() + "'s Mute status at line");
            e.printStackTrace();

        }
    }

    protected void muteOfflinePlayer(OfflineRankedPlayer rp, Instant duration, String reason, RankedPlayer admin) {
        String id = Integer.toString(hash("FoxRank:" + rp.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT);
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
        addOfflinePlayerMuteLogEntry(rp, duration, reason, admin, id);
    }

    protected void addOfflinePlayerMuteLogEntry(OfflineRankedPlayer player, Instant expires, String reason, RankedPlayer admin, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("MUTE." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("ID", ID);
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("Expires", expires.toString());
        section.addDefault("Reason", reason);
        section.addDefault("Staff", admin.getRank().getPrefix() + admin.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addMuteLogEntry(RankedPlayer player, Instant expires, String reason, RankedPlayer admin, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("MUTE." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("ID", ID);
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("Expires", expires.toString());
        section.addDefault("Reason", reason);
        section.addDefault("Staff", admin.getRank().getPrefix() + admin.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addUnmuteLogEntry(RankedPlayer player, RankedPlayer staff, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("UNMUTE." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("ID", ID);
        section.addDefault("Staff", staff.getRank().getPrefix() + staff.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addOnlineBanLogEntry(RankedPlayer banned, RankedPlayer staff, Instant when, String reason, String duration, String ID, boolean silent) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("BAN." + banned.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Staff", staff.getName());
        section.addDefault("Player", banned.getName());
        section.addDefault("Reason", reason);
        section.addDefault("Duration", duration);
        section.addDefault("Silent", silent);
        section.addDefault("When", when.toString());
        section.addDefault("ID", ID);
        section.addDefault("Type", "ONLINE");
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addOfflineBanLogEntry(OfflineRankedPlayer banned, RankedPlayer staff, Instant when, String reason, String duration, String ID, boolean silent) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("BAN." + banned.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Staff", staff.getName());
        section.addDefault("Player", banned.getName());
        section.addDefault("Reason", reason);
        section.addDefault("Duration", duration);
        section.addDefault("Silent", silent);
        section.addDefault("When", when.toString());
        section.addDefault("ID", ID);
        section.addDefault("Type", "OFFLINE");
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addOfflinePlayerUnmuteLogEntry(OfflineRankedPlayer player, RankedPlayer staff, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("UNMUTE." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("ID", ID);
        section.addDefault("Staff", staff.getRank().getPrefix() + staff.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addNicknameLogEntry(RankedPlayer player, String newNick, String rankID, String skinOption) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("NICKNAME." + player.getUniqueId() + "." + Integer.toString(hash("FoxRank:" + player.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT));
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getUniqueId().toString());
        section.addDefault("Nickname", newNick);
        section.addDefault("RankID", rankID);
        section.addDefault("Skin", skinOption);
        section.addDefault("Date", Instant.now().toString());
        yml.options().copyDefaults(true);

        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addUnbanLogEntry(OfflineRankedPlayer player, RankedPlayer staff, String ID) {
        File file = new File("plugins/FoxRank/auditlog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.createSection("UNBAN." + player.getUniqueId() + "." + ID);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        section.addDefault("Player", player.getRank().getPrefix() + player.getName());
        section.addDefault("Date", Instant.now().toString());
        section.addDefault("ID", ID);
        section.addDefault("Staff", staff.getRank().getPrefix() + staff.getName());
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendNoPermissionMessage(int powerLevel, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', this.getConfig().getString("NoPermissionMessage").replace("$POWERLEVEL", powerLevel + "").replace("\\n", "\n")));
    }

    protected void sendMissingArgsMessage(String command, String args, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', this.getConfig().getString("MissingArgsMessage").replace("$COMMAND", command + "").replace("$ARGS", args)));
    }

    protected void sendInvalidArgsMessage(String args, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', this.getConfig().getString("InvalidArgumentMessage").replace("$ARGTYPE", args)));
    }

    protected String getTrueName(UUID uuid) {
        URL url;
        try {
            url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            return new JsonParser().parse(reader).getAsJsonObject().get("name").getAsString();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot get a player's name from Mojang");
        }
        return null;
    }

    protected String getNickname(UUID uuid) {
        if (isNicked(uuid)) {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            return yml.getString("Nickname");
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

    @EventHandler
    public void BanHandler(AsyncPlayerPreLoginEvent e) {
        if (isBanned(e.getUniqueId())) {
            String uuid = e.getUniqueId().toString();
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            String reason = yml.getString("BanReason");
            String duration = yml.getString("BanDuration");
            String id = yml.getString("BanID");
            String bumper = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
            if (duration == null) {
                System.out.println("Duration Null!");
                String finalMessage = this.getConfig().getString("PermBanMessageFormat")
                        .replace("$SERVER_NAME", this.getConfig().getString("ServerName"))
                        .replace("$REASON", reason)
                        .replace("$APPEAL_LINK", this.getConfig().getString("BanAppealLink"))
                        .replace("$ID", id)
                        .replace("\\n", "\n");
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('§', bumper + finalMessage + bumper));
            } else {
                Instant inst = Instant.parse(duration);
                if (Instant.now().isAfter(inst)) {
                    yml.set("isBanned", false);

                    File bannedPlayersFile = new File("plugins/FoxRank/bannedPlayers.yml");
                    YamlConfiguration bannedPlayersyml = YamlConfiguration.loadConfiguration(bannedPlayersFile);
                    List<String> list = bannedPlayersyml.getStringList("CurrentlyBannedPlayers");
                    if (list.contains(e.getUniqueId().toString())) {
                        System.out.println(e.getUniqueId() + "<-- UUID");

                        if (list.remove(e.getUniqueId().toString())) {
                            bannedPlayersyml.set("CurrentlyBannedPlayers", list);
                            System.out.println("Removed.");
                            try {
                                bannedPlayersyml.save(bannedPlayersFile);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    try {
                        yml.save(file);
                        bannedPlayersyml.save(bannedPlayersFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.allow();
                    return;
                }
                String finalMessage = this.getConfig().getString("TempBanMessageFormat")
                        .replace("$DURATION", getFormattedExpiredString(inst, Instant.now()))
                        .replace("$SERVER_NAME", this.getConfig().getString("ServerName"))
                        .replace("$REASON", reason)
                        .replace("$APPEAL_LINK", this.getConfig().getString("BanAppealLink"))
                        .replace("$ID", id)
                        .replace("\\n", "\n");
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('§', bumper + finalMessage + bumper));
            }
        } else {
            e.allow();
        }
    }

    public List<OfflinePlayer> getBannedPlayers() {
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