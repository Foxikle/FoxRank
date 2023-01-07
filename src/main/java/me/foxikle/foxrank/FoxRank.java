package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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

    private void setupTeams() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        if(!this.getConfig().getBoolean("DisableRankVisibility")) {
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

    protected void setRank(Player player, Rank rank) {
        ranks.put(player, rank);
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("Rank", rank.getRankID());
       if(this.getConfig().getBoolean("DisableRankVisibility")){
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

    protected static Rank getRank(Player player) {
        return ranks.get(player);
    }

    protected void loadRank(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String rankID = yml.getString("Rank");
        if (rankID != null) {

            Rank rank = getRankFromString(rankID);
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

    @Override
    public void onEnable() {
        setupTeams();
        this.saveResource("config.yml", false);
        this.saveResource("NicknameLog.yml", false);
        this.saveResource("muteLog.yml", false);
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new Vanish(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMsgs(), this);
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
    }

    @Override
    public void onDisable() {
        for (Player p : this.getServer().getOnlinePlayers()) {
            saveRank(p);
        }
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
        if (isMuted(player)) {
            Instant date = Instant.parse(yml.getString("MuteDuration"));
            Instant now = Instant.now();
            if (date.isBefore(now)) {
                unmutePlayer(new RankedPlayer(player));
            } else {
                String reason = yml.getString("MuteReason");
                String border = RED + "" + STRIKETHROUGH + "                                                                   ";
                String muteMessage = FoxRank.getInstance().getConfig().getString("MuteMessage").replace("LINE", border);
                muteMessage = muteMessage.replace("\\n", "\n");
                muteMessage = muteMessage.replace("DURATION", getFormattedExpiredString(Instant.parse(yml.getString("MuteDuration"))));
                muteMessage = muteMessage.replace("REASON", reason);
                muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
                player.sendMessage(muteMessage);
                return;
            }
        }
        if (yml.getString("isNicked").equals("true")) {
            if (!disableVisibility) {
                if (getRankFromString(yml.getString("Nickname-Rank")) == DEFAULT) {
                    newMessage = ChatColor.GRAY + yml.getString("Nickname") + ": " + eventMessage;
                    Bukkit.broadcastMessage(newMessage);
                } else if (getRankFromString(yml.getString("Nickname-Rank")) != DEFAULT) {
                    newMessage = getRankFromString(yml.getString("Nickname-Rank")).getPrefix() + "" + yml.getString("Nickname") + ChatColor.RESET + ": " + eventMessage;
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

    protected String getFormattedExpiredString(Instant date) {

        Instant now = Instant.now();
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
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException error) {
            error.printStackTrace();
        }
        actionBar.setupActionBar(p);
        loadRank(p);
        if (this.isMuted(p)) {
            if (this.getMuteDuration(p).isBefore(Instant.now())) {
                this.unmutePlayer(new RankedPlayer(p));
            }
        }
    }

    protected boolean isVanished(Player player) {

        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        return yml.getBoolean("isVanished");
    }

    protected boolean isNicked(Player player) {

        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        return yml.getBoolean("isNicked");
    }

    protected boolean isMuted(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        return yml.getBoolean("isMuted");
    }

    protected String getMuteReason(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getString("MuteReason");
    }

    protected Instant getMuteDuration(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return Instant.parse(yml.getString("MuteDuration"));
    }

    protected void mutePlayer(RankedPlayer rp, Instant duration, String reason) {
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
        addMuteLogEntry(rp.getUniqueId() + " Was muted at " + Instant.now() + "for " + reason + ", they will be muted until " + duration);

        String border = RED + "" + STRIKETHROUGH + "                                                                   ";
        String muteMessage = FoxRank.getInstance().getConfig().getString("MuteMessage").replace("LINE", border);
        muteMessage = muteMessage.replace("\\n", "\n");
        muteMessage = muteMessage.replace("DURATION", getFormattedExpiredString(duration));
        muteMessage = muteMessage.replace("REASON", reason);
        muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
        rp.sendMessage(muteMessage);

    }

    protected void unmutePlayer(RankedPlayer rp) {
        File file = new File("plugins/FoxRank/PlayerData/" + rp.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("isMuted", false);
        addMuteLogEntry(rp.getUniqueId() + " Was unmuted at " + Instant.now());
        String border = GREEN + "" + STRIKETHROUGH + "                                                                     ";
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnmuteRecieverMessage").replace("LINE", border).replace("\\n", "\n")));
        try {
            yml.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp.getUniqueId() + "'s Mute status at line");
            e.printStackTrace();

        }
    }

    protected void addMuteLogEntry(String entry) {
        File file = new File("plugins/FoxRank/muteLog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.createSection(entry);
        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void sendNoPermissionMessage(int powerLevel, RankedPlayer rp){
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', this.getConfig().getString("NoPermissionMessage").replace("POWERLEVEL", powerLevel + "").replace("\\n", "\n")));
    }
    protected void sendMissingArgsMessage(String command, String args, RankedPlayer rp){
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', this.getConfig().getString("MissingArgsMessage").replace("COMMAND", command + "").replace("ARGS", args)));
    }
    protected void sendInvalidArgsMessage(String args, RankedPlayer rp){
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', this.getConfig().getString("InvalidArgumentMessage").replace("ARGTYPE", args)));
    }
}