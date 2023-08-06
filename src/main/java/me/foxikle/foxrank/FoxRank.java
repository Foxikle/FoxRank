package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import com.google.gson.JsonParser;
import me.clip.placeholderapi.PlaceholderAPI;
import me.foxikle.foxrank.Data.DataManager;
import me.foxikle.foxrank.Data.PlayerData;
import me.foxikle.foxrank.events.RankChangeEvent;
import me.foxikle.foxrank.placeholders.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;

public class FoxRank extends JavaPlugin implements Listener {

    public static PluginChannelListener pcl;
    private static FoxRank instance;
    public boolean disableRankVis;
    public List<Player> vanishedPlayers = new ArrayList<>();
    public boolean bungeecord = false;
    public Map<String, Rank> ranks = new HashMap<>();
    public Map<Player, Rank> playerRanks = new HashMap<>();
    public List<Team> rankTeams = new ArrayList<>();
    public Map<String, Team> teamMappings = new HashMap<>();
    public Map<String, Integer> powerLevels = new HashMap<>();
    public Map<UUID, Set<PermissionAttachment>> permissions = new HashMap<>();

    // placeholders
    public Map<UUID, String> logTypeMap = new HashMap<>();
    public Map<UUID, String> attemptedBanPresetMap = new HashMap<>();
    public Map<UUID, UUID> banMap = new HashMap<>();
    public Map<UUID, UUID> muteMap = new HashMap<>();
    public Map<UUID, String> syntaxMap = new HashMap<>();
    public Map<UUID, UUID> targetMap = new HashMap<>();
    public Map<UUID, String> attemptedNicknameMap = new HashMap<>();

    // data cache
    private Map<UUID, PlayerData> playerData = new HashMap<>();
    public List<UUID> bannedPlayers = new ArrayList<>();
    public List<String> players = new ArrayList<>();

    private DataManager dm;

    protected List<String> playerNames = new ArrayList<>();

    public static FoxRank getInstance() {
        return instance;
    }

    public void setTeam(Player player, String teamID) {
        if (!getInstance().disableRankVis) {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            player.setScoreboard(board);
            if (teamMappings.containsKey(teamID)) {
                teamMappings.get(teamID).addEntry(player.getName());
            }
        }
    }

    public Rank getRank(Player player) {
        return playerRanks.get(player) == null ? getDefaultRank() : playerRanks.get(player);
    }

    public void setRank(Player player, Rank rank) {
        if (getRank(player) != null) {
            this.getServer().getPluginManager().callEvent(new RankChangeEvent(player, rank, getRank(player)));
            clearPermissions(player.getUniqueId());
        }

        playerRanks.put(player, rank);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> dm.setStoredRank(player.getUniqueId(), rank));
        setTeam(player, rank.getId());
    }

    public void loadRank(Player player) {
        Rank rank = getPlayerData(player.getUniqueId()).getRank();
        rank.getPermissionNodes().forEach(s -> player.addAttachment(this, s, true));
        playerRanks.put(player, rank);
        setTeam(player, rank.getId());
    }

    protected PluginChannelListener getPluginChannelListener() {
        return pcl;
    }

    @Override
    public void onEnable() {
        instance = this;
        pcl = new PluginChannelListener();
        dm = new DataManager(this);
        dm.init();
        bungeecord = this.getConfig().getBoolean("bungeecord");
        if (bungeecord) {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", pcl);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new BanPlaceholder(this).register();
            new LinePlaceholder(this).register();
            new LogTypePlaceholder(this).register();
            new MutePlaceholder(this).register();
            new NicknamePlaceholder(this).register();
            new PlayerRankPlaceholder(this).register();
            new ServerPlaceholder(this).register();
            new SyntaxPlaceholder(this).register();
            new TargetPlaceholder(this).register();
        } else {
            getLogger().severe("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if(!checkVersion()) {
            getLogger().warning("Incompatible server version! This WILL break the nickname feature. Do not expect support.");
        }

        setupTeams();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getPluginManager().registerEvents(new JoinLeaveMsgs(this), this);
            getServer().getPluginManager().registerEvents(new Logs(this), this);
            getServer().getPluginManager().registerEvents(new Listeners(this), this);
        }, 20);
        reloadConfig();
        Mute m = new Mute(this);
        getCommand("nick").setExecutor(new Nick());
        getCommand("vanish").setExecutor(new Vanish());
        getCommand("setrank").setExecutor(new SetRank(this));
        getCommand("mute").setExecutor(new Mute(this));
        getCommand("me").setExecutor(m);
        getCommand("say").setExecutor(m);
        getCommand("immuted").setExecutor(m);
        getCommand("unmute").setExecutor(m);
        getCommand("logs").setExecutor(new Logs(this));
        getCommand("ban").setExecutor(new Ban(this));
        getCommand("unban").setExecutor(new Unban());
        getCommand("rank").setExecutor(new RankCommand(this));

        Bukkit.getServicesManager().register(FoxRank.class, this, this, ServicePriority.Normal);
        Metrics metrics = new Metrics(this, 19157);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> dm.setupRanks());
        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.getOnlinePlayers().forEach(p -> Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            this.loadRank(p);
            ActionBar.setupActionBar(p);
            if (getPlayerData(p.getUniqueId()).isMuted()) { //todo: make it support null durations
                if (getPlayerData(p.getUniqueId()).getMuteDuration().isBefore(Instant.now())) {
                    ModerationAction.unmutePlayer(new RankedPlayer(p, this), new RankedPlayer(p, this));
                }
            }
            if (Bukkit.getOnlinePlayers().size() == 1) {
                if (bungeecord) {
                    Bukkit.getScheduler().runTaskLater(this, () -> FoxRank.pcl.getPlayers(Iterables.getFirst(Bukkit.getOnlinePlayers(), null)), 30);
                }
            }

            if (getPlayerData(p.getUniqueId()).isNicked()) {
                Nick.changeName(getPlayerData(p.getUniqueId()).getNickname(), p);
                Bukkit.getScheduler().runTask(this, () -> Nick.loadSkin(p));
                setTeam(p, getPlayerData(p.getUniqueId()).getNicknameRank().getId());
            }
            if (getPlayerData(p.getUniqueId()).isVanished()) {
                Bukkit.getScheduler().runTask(this, () -> Vanish.vanishPlayer(p));
            }
        })), 20);
    }

    private boolean checkVersion(){
        String sversion;
        try{
            sversion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException ex){
            return false;
        }
        return (sversion.equals("v1_20_R1") || sversion.equals("v1_20_1_R1"));
    }

    @Override
    public void onDisable() {
        for (Player p : this.getServer().getOnlinePlayers()) {
            dm.saveRank(p);
            clearPermissions(p.getUniqueId());
        }

        for (Team team : rankTeams) {
            try {
                team.unregister();
            } catch (NullPointerException | IllegalStateException ignored) {
            }
        }

        dm.shutDown();
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getServicesManager().unregister(this);
    }

    public void setupTeams() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        for (int i = 0; i < ranks.size(); i++) {
            Rank rank = (Rank) ranks.values().toArray()[i];
            Team team;
            try {
                team = board.registerNewTeam(i + rank.getId());
            } catch (IllegalArgumentException ignored) {
                board.getTeam(i + rank.getId()).unregister();
                team = board.registerNewTeam(i + rank.getId());
            }
            if (disableRankVis) {
                team.setPrefix("");
                team.setColor(ChatColor.WHITE);
            } else {
                team.setColor(rank.getColor());
                team.setPrefix(rank.getPrefix());
            }
            teamMappings.put(rank.getId(), team);
            rankTeams.add(team);
        }
    }

    public String getFormattedExpiredString(Instant date, Instant now) {
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

    public String getTrueName(UUID uuid) {
        URL url;
        try {
            url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            return new JsonParser().parse(reader).getAsJsonObject().get("name").getAsString();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could get " + uuid + "'s name from Mojang");
        }
        return null;
    }

    /**
     * @return Rank the rank with the LOWEST power level.
     * This is configured in the `ranks.yml` file.
     */
    public Rank getDefaultRank() {
        return Iterables.getLast(ranks.values());
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return dm.getConfig();
    }

    public void clearPermissions(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if(op.isOnline()) {
            Player player = op.getPlayer();
            if(permissions.get(uuid) == null || permissions.get(uuid).isEmpty()) return;
            for (PermissionAttachment pa : permissions.get(uuid)) {
                player.removeAttachment(pa);
            }
        } else {
            permissions.remove(uuid);
        }
    }

    public String getMessage(String path, OfflinePlayer p){
        File file = new File("plugins/FoxRank/messages.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', yml.getString(path))).replace("\\n", "\n");
    }
    public String getMessage(String path, Player p){
        File file = new File("plugins/FoxRank/messages.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', yml.getString(path))).replace("\\n", "\n");
    }

    public DataManager getDm(){
        if(Bukkit.isPrimaryThread()) {
            throw new IllegalThreadStateException("Database call on main thread");
        }
        return dm;
    }
    public void sendCommandDisabled(CommandSender commandSender){
        File file = new File("plugins/FoxRank/messages.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', yml.getString("CommandDisabledMessage")).replace("\\n", "\n"));
    }
    public String getSyntaxMessage(Player player) {
        File file = new File("plugins/FoxRank/messages.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', yml.getString("BadSyntaxMessage"))).replace("\\n", "\n");
    }

    public PlayerData getPlayerData(UUID uuid){
        return playerData.get(uuid);
    }
    public void clearPlayerData(){
         playerData.clear();
    }
    public void addPlayerDataEntry(PlayerData pd, UUID key){
        playerData.put(key, pd);
    }
}
