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

    public static FoxRank instance;
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

    private static void setupTeams() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        try {
            Team dT = board.registerNewTeam("DEFAULTRankTeam");
            dT.setColor(ChatColor.GRAY);
            dT.setPrefix(DEFAULT.getPrefix());
            Team oT = board.registerNewTeam("ownerTeam");
            oT.setColor(RED);
            oT.setPrefix(OWNER.getPrefix());
            Team aT = board.registerNewTeam("adminTeam");
            aT.setColor(RED);
            aT.setPrefix(ADMIN.getPrefix());
            Team moT = board.registerNewTeam("moderatorTeam");
            moT.setColor(ChatColor.DARK_GREEN);
            moT.setPrefix(MODERATOR.getPrefix());
            Team yT = board.registerNewTeam("youtubeTeam");
            yT.setColor(RED);
            yT.setPrefix(YOUTUBE.getPrefix());
            Team tT = board.registerNewTeam("twitchTeam");
            tT.setColor(ChatColor.DARK_PURPLE);
            tT.setPrefix(TWITCH.getPrefix());
            Team mpT = board.registerNewTeam("mvp_plusTeam");
            mpT.setColor(ChatColor.AQUA);
            mpT.setPrefix(MVP_PLUS.getPrefix());
            Team mT = board.registerNewTeam("mvpTeam");
            mT.setColor(ChatColor.AQUA);
            mT.setPrefix(MVP.getPrefix());
            Team vpT = board.registerNewTeam("vip_plusTeam");
            vpT.setColor(ChatColor.GREEN);
            vpT.setPrefix(VIP_PLUS.getPrefix());
            Team vT = board.registerNewTeam("vipTeam");
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
            Team dT = board.getTeam("DEFAULTRankTeam");
            Team oT = board.getTeam("ownerTeam");
            Team aT = board.getTeam("adminTeam");
            Team moT = board.getTeam("moderatorTeam");
            Team yT = board.getTeam("youtubeTeam");
            Team tT = board.getTeam("twitchTeam");
            Team mpT = board.getTeam("mvp_plusTeam");
            Team mT = board.getTeam("mvpTeam");
            Team vpT = board.getTeam("vip_plusTeam");
            Team vT = board.getTeam("vipTeam");
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
    }

    protected static void setTeam(Player player, String teamID) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(board);

        if (teamID.equals("OWNER")) {
            OwnerTeam.addEntry(player.getName());
        } else if (teamID.equals("ADMIN")) {
            AdminTeam.addEntry(player.getName());
        } else if (teamID.equals("MODERATOR")) {
            ModeratorTeam.addEntry(player.getName());
        } else if (teamID.equals("YOUTUBE")) {
            YoutubeTeam.addEntry(player.getName());
        } else if (teamID.equals("TWITCH")) {
            TwitchTeam.addEntry(player.getName());
        } else if (teamID.equals("MVP_PLUS")) {
            MvppTeam.addEntry(player.getName());
        } else if (teamID.equals("MVP")) {
            MvpTeam.addEntry(player.getName());
        } else if (teamID.equals("VIP_PLUS")) {
            VippTeam.addEntry(player.getName());
        } else if (teamID.equals("VIP")) {
            VipTeam.addEntry(player.getName());
        } else if (teamID.equals("DEFAULT")) {
            DefualtTeam.addEntry(player.getName());
        }
    }

    public static FoxRank getInstance() {
        return instance;
    }

    public static void setRank(Player player, Rank rank) {
        ranks.put(player, rank);
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("Rank", rank.getRankID());
        try {
            yml.save(file);
        } catch (IOException error) {
            error.printStackTrace();
        }

    }

    protected static Rank getRank(Player player) {
        return ranks.get(player);
    }

    protected static void loadRank(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String rankID = yml.getString("Rank");
        if (rankID != null) {

            Rank rank = getRankFromString(rankID);
            player.setDisplayName(rank.getPrefix() + player.getName());
            player.setPlayerListName(rank.getPrefix() + player.getName());
            setTeam(player, rank.getRankID());
            setRank(player, rank);
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "rankID is null");
        }
    }

    protected static void saveRank(Player player) {
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
        getCommand("setrank").setExecutor(new setRank());
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

        if(isMuted(player)){
            Instant date = Instant.parse(yml.getString("MuteDuration"));
            Instant now = Instant.now();
            if(date.isBefore(now)){
                unmutePlayer(new RankedPlayer(player));
            } else {
                String border = RED + "" + STRIKETHROUGH + "                                                                    ";
                player.sendMessage(border);
                player.sendMessage(RED + "\nYou are currently muted for " + yml.get("MuteReason"));
                player.sendMessage(GRAY + "Your mute will expire in " + RED + getFormattedExpiredString(date));
                player.sendMessage(border);
                return;
            }
        }

        if (yml.getString("isNicked").equals("true")) {
            if (getRankFromString(yml.getString("Nickname-Rank")) == DEFAULT) {
                newMessage = ChatColor.GRAY + yml.getString("Nickname") + ": " + eventMessage;
                Bukkit.broadcastMessage(newMessage);
            } else if (getRankFromString(yml.getString("Nickname-Rank")) != DEFAULT) {
                newMessage = getRankFromString(yml.getString("Nickname-Rank")).getPrefix() + "" + yml.getString("Nickname") + ChatColor.RESET + ": " + eventMessage;
                Bukkit.broadcastMessage(newMessage);
            }
        } else {
            if (getRank(player) != DEFAULT) {
                newMessage = getRank(player).getPrefix() + player.getName() + ChatColor.RESET + ": " + eventMessage;
                Bukkit.broadcastMessage(newMessage);
            } else if (getRank(player) == DEFAULT) {
                newMessage = getRank(player).getPrefix() + player.getName() + ChatColor.RESET + "" + ChatColor.GRAY + ": " + eventMessage;
                Bukkit.broadcastMessage(newMessage);
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
        temp = temp.minusSeconds(minutes* 60);
        long seconds = ChronoUnit.SECONDS.between(now, temp);
    String str = "";
    if(days != 0){
        str = str + days + "d ";
    }
    if (hours != 0) {
        str = str + hours + "h ";
    }
    if (minutes != 0) {
        str = str + minutes + "m ";
    }
    if(seconds != 0){
        str = str + seconds + "s";
    }
        return str;
    }

    @EventHandler
    public static void OnPlayerLogin(PlayerJoinEvent e) {
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
        if(getInstance().isMuted(p)){
            if(getInstance().getMuteDuration(p).isBefore(Instant.now())); getInstance().unmutePlayer(new RankedPlayer(p));
        }
    }

    protected boolean isVanished(Player player) {

        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        if (yml.getBoolean("isVanished")) return true;

        return false;
    }

    protected boolean isNicked(Player player) {

        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        if (yml.getBoolean("isNicked")) return true;

        return false;
    }
    protected boolean isMuted(Player player){
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        if (yml.getBoolean("isMuted")) return true;

        return false;
    }

    protected String getMuteReason(Player player){
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getString("MuteReason");
    }
    protected Instant getMuteDuration(Player player){
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return Instant.parse(yml.getString("MuteDuration"));
    }
    protected void mutePlayer(RankedPlayer rp, Instant duration, String reason){
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
        String border = RED + "" + STRIKETHROUGH + "                                                                     ";
        rp.sendMessage(border);
        rp.sendMessage(RED + "You are now MUTED for " + reason);
        rp.sendMessage(GRAY + "Your mute will expire in " + RED + getFormattedExpiredString(duration));
        rp.sendMessage(border);

    }
    protected void unmutePlayer(RankedPlayer rp){
        File file = new File("plugins/FoxRank/PlayerData/" + rp.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("isMuted", false);
        addMuteLogEntry(rp.getUniqueId() + " Was unmuted at " + Instant.now());
        String border = GREEN + "" + STRIKETHROUGH +  "                                                                     ";
        rp.sendMessage(border + RESET + GREEN + "\nYou are now UNMUTED.\n" + border);
        try {
            yml.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + rp.getUniqueId() + "'s Mute status at line 384 in FoxRank.java");
            e.printStackTrace();

        }
    }
    protected void addMuteLogEntry(String entry){
        File file = new File("plugins/FoxRank/muteLog.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.createSection(entry);
        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}