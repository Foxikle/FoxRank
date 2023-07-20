package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import com.google.gson.JsonParser;
import me.foxikle.foxrank.Data.DataManager;
import me.foxikle.foxrank.events.RankChangeEvent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
    public DataManager dm;

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
        return playerRanks.get(player);
    }

    public void setRank(Player player, Rank rank) {
        if (getRank(player) != null)
            this.getServer().getPluginManager().callEvent(new RankChangeEvent(player, rank, getRank(player)));
        playerRanks.put(player, rank);
        dm.setStoredRank(player.getUniqueId(), rank);
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


        setupTeams();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getPluginManager().registerEvents(new JoinLeaveMsgs(this), this);
            getServer().getPluginManager().registerEvents(new Logs(this), this);
            getServer().getPluginManager().registerEvents(new Listeners(this), this);
        }, 20);
        reloadConfig();

        getCommand("nick").setExecutor(new Nick());
        getCommand("vanish").setExecutor(new Vanish());
        getCommand("setrank").setExecutor(new SetRank(this));
        getCommand("mute").setExecutor(new Mute());
        getCommand("me").setExecutor(new Mute());
        getCommand("say").setExecutor(new Mute());
        getCommand("immuted").setExecutor(new Mute());
        getCommand("unmute").setExecutor(new Mute());
        getCommand("logs").setExecutor(new Logs(this));
        getCommand("ban").setExecutor(new Ban(this));
        getCommand("unban").setExecutor(new Unban());

        Bukkit.getServicesManager().register(FoxRank.class, this, this, ServicePriority.Normal);
        Metrics metrics = new Metrics(this, 19157);
    }

    @Override
    public void onDisable() {
        for (Player p : this.getServer().getOnlinePlayers()) {
            dm.saveRank(p);
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

    private void setupTeams() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        for (int i = 0; i < ranks.size() - 1; i++) {
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
                Bukkit.broadcastMessage(rank.getColor().name());
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

    public void sendNoPermissionMessage(int powerLevel, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("NoPermissionMessage").replace("$POWERLEVEL", String.valueOf(powerLevel)).replace("\\n", "\n")));
    }

    public void sendMissingArgsMessage(String command, String args, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("MissingArgsMessage").replace("$COMMAND", command).replace("$ARGS", args)));
    }

    public void sendInvalidArgsMessage(String args, RankedPlayer rp) {
        rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("InvalidArgumentMessage").replace("$ARGTYPE", args)));
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
        return Iterables.getLast(ranks.values(), null);
    }

    @Override
    public FileConfiguration getConfig() {
        return dm.getConfig();
    }
}
