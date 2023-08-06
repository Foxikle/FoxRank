package me.foxikle.foxrank.Data;

import com.google.gson.JsonParser;
import me.foxikle.foxrank.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DataManager {

    private final FoxRank plugin;
    private final FileConfiguration config;
    private boolean useDatabase;
    private Database db;

    //TODO: Make sure to query database ASYNC!!!
    //TODO: Look into caching the files in memory

    public DataManager(FoxRank plugin) {
        this.plugin = plugin;
        File configFile = new File("plugins/FoxRank/config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void init() {
        plugin.disableRankVis = config.getBoolean("DisableRankVisiblity");
        plugin.bungeecord = config.getBoolean("bungeecord");
        useDatabase = config.getBoolean("UseSQLStorage");

        if (!new File("plugins/FoxRank/ranks.yml").exists()) {
            plugin.saveResource("ranks.yml", false);
        }

        if (useDatabase) {
            // setup database itself
            db = new Database(plugin);
            try {
                db.connect();
            } catch (ClassNotFoundException | SQLException ignored) {
                Bukkit.getLogger().log(Level.SEVERE, "[FoxRank] Invalid database credentials. Disabling plugin.");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }

            Bukkit.getLogger().info("[FoxRank] Database connected.");
            // setup datatables
            db.createPlayerDataTable();
            db.createBannedPlayersTable();
            db.createAuditLogTable();

            // load things into memory
        } else {
            if (!new File("plugins/FoxRank/auditlog.yml").exists()) {
                plugin.saveResource("auditlog.yml", false);
            } else if (!new File("plugins/FoxRank/bannedPlayers.yml").exists()) {
                plugin.saveResource("bannedPlayers.yml", false);
            }
        }
        setupRanks();
    }

    private Rank getStoredRank(UUID uuid) {
        if (useDatabase) {
            return db.getStoredRank(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            if (file.exists()) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                return Rank.of(yml.getString("Rank"));
            } else {
                throw new IllegalArgumentException("UUID, " + uuid + "does not have any data.");
            }
        }
    }

    public void shutDown() {
        db.disconnect();
    }

    public void setStoredRank(UUID uuid, Rank rank) {
        if (useDatabase) {
            db.setStoredRank(uuid, rank);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("Rank", rank.getId());
            try {
                yml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setupRanks() {
        File file = new File("plugins/FoxRank/ranks.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection yml = yamlConfiguration.getConfigurationSection("Ranks");

        Set<String> keys = yml.getKeys(false);
        Map<String, Integer> powerlevelMappy = new HashMap<>();
        Map<Rank, Integer> rankMappy = new HashMap<>();
        for (String str : keys) {
            ConfigurationSection section = yml.getConfigurationSection(str);
            Rank rank = new Rank(section.getInt("powerLevel"), ChatColor.translateAlternateColorCodes('&', section.getString("prefix")), section.getString("id"), ChatColor.getByChar(section.getString("color").charAt(0)), ChatColor.getByChar(section.getString("ChatTextColor")), section.getBoolean("nicknamable"), section.getStringList("permissions"));
            powerlevelMappy.put(rank.getId(), rank.getPowerlevel());
            rankMappy.put(rank, rank.getPowerlevel());
        }
        plugin.ranks = getSortedRanks(rankMappy);
        plugin.powerLevels = sortByValue(powerlevelMappy);

        for (UUID uuid : getUUIDs()) {
            cacheUserData(uuid);
        }
        plugin.bannedPlayers.addAll(getBannedPlayers());
        plugin.players.addAll(getPlayers());
    }

    public void cacheUserData(UUID uuid) {
        plugin.addPlayerDataEntry(
                new PlayerData(
                        getStoredBanDuration(uuid),
                        getStoredBanReason(uuid),
                        getStoredBanID(uuid),
                        isBanned(uuid),
                        getMuteDuration(uuid),
                        getMuteReason(uuid),
                        isMuted(uuid),
                        isVanished(uuid),
                        isNicked(uuid),
                        getNickname(uuid),
                        getNicknameRank(uuid),
                        getStoredRank(uuid),
                        getNicknameSkin(uuid)
                ), uuid);
    }

    private Map<String, Rank> getSortedRanks(Map<Rank, Integer> unsortMap) {
        List<Map.Entry<Rank, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        //TODO: write your own comparator that counts from high -> low
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        Map<String, Rank> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Rank, Integer> entry : list) {
            sortedMap.put(entry.getKey().getId(), entry.getKey());
        }
        return sortedMap;
    }

    private Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        //TODO: write your own comparator that counts from high -> low
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public void saveRank(Player player) {
        if (useDatabase) {
            setStoredRank(player.getUniqueId(), plugin.getRank(player));
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("Rank", plugin.getRank(player).getId());
            try {
                yml.save(file);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        plugin.playerRanks.remove(player);
    }

    // getters for basic data from player. Might want to cache this tbh

    public boolean isVanished(UUID uuid) {
        if (useDatabase) {
            return db.getStoredVanishStatus(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            return yml.getBoolean("isVanished");
        }
    }

    private boolean isNicked(UUID uuid) {
        if (useDatabase) {
            return db.getStoredNicknameStatus(uuid);
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isNicked");
    }

    private boolean isBanned(UUID uuid) {
        if (useDatabase) {
            return db.getStoredBanStatus(uuid);
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isBanned");
    }

    private boolean isMuted(UUID uuid) {
        if (useDatabase) {
            return db.getStoredMuteStatus(uuid);
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getBoolean("isMuted");
    }

    private String getMuteReason(UUID uuid) {
        if (useDatabase) {
            return db.getStoredMuteReason(uuid);
        }
        File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getString("MuteReason");
    }

    @Nullable
    private Instant getMuteDuration(UUID uuid) {
        String data;
        if (useDatabase) {
            data = db.getStoredMuteDuration(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            data = yml.getString("MuteDuration");
        }
        if(data == null)
            return null;
        return Instant.parse(data);
    }

    private String getNickname(UUID uuid) {
        if (isNicked(uuid)) {
            if (useDatabase) {
                return db.getStoredNickname(uuid);
            } else {
                File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                return yml.getString("Nickname");
            }
        } else {
            return plugin.getTrueName(uuid);
        }
    }

    private Rank getNicknameRank(UUID uuid) {
        if (useDatabase) {
            return db.getStoredNicknameRank(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            return Rank.of(yml.getString("Nickname-Rank"));
        }
    }

    public UUID getUUID(String name) {
        URL url;
        InputStreamReader reader = null;
        try {
            url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String raw = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
        return UUID.fromString(raw.substring(0, 8) + "-" + raw.substring(8, 12) + "-" + raw.substring(12, 16) + "-" + raw.substring(16, 20) + "-" + raw.substring(20, 32));
    }


    public List<UUID> getBannedPlayers() {
        if (useDatabase) {
            return db.getStoredBannedPlayers();
        }
        File file = new File("plugins/FoxRank/bannedPlayers.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        List<UUID> players = new ArrayList<>();
        if (yml.getStringList("CurrentlyBannedPlayers").isEmpty()) {
            return new ArrayList<>();
        }
        for (String str : yml.getStringList("CurrentlyBannedPlayers")) {
            players.add(UUID.fromString(str));
        }
        return players;
    }

    public void setBannedPlayers(List<UUID> uuids) {
        if (useDatabase) {
            db.setStoredBannedPlayers(uuids);
        } else {
            File file = new File("plugins/FoxRank/bannedPlayers.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            List<String> uuidStrings = new ArrayList<>();
            for (UUID u : uuids) {
                uuidStrings.add(u.toString());
            }
            yml.set("CurrentlyBannedPlayers", uuidStrings);
        }
    }

    public List<String> getPlayerNames(Player player) {
        if (plugin.bungeecord) {
            // BAD!
            //TODo: Re-work this please. (And require a proxy plugin!)
            return getPlayerNames(player);
        } else {
            List<String> returnme = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player1 -> returnme.add(player1.getName()));
            return returnme;
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // setters!
    public void setVanishedState(UUID uuid, boolean vanished) {
        if (useDatabase) {
            db.setStoredVanishedState(uuid, vanished);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            if (yml.getBoolean("isVanished")) {
                yml.set("isVanished", false);
                try {
                    yml.save(file);
                } catch (IOException error) {
                    Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + uuid + "'s Vanished state.");
                }
            } else if (!yml.getBoolean("isVanished")) {
                yml.set("isVanished", true);
                try {
                    yml.save(file);
                } catch (IOException error) {
                    Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + uuid + "'s Vanished state.");
                }
            }
        }
    }

    public void setNicknameData(@NotNull UUID uuid, boolean isNicked, @NotNull Rank rank, String newNick, String skin) {
        if (useDatabase) {
            db.setStoredNicknameData(uuid, isNicked, rank, newNick, skin);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isNicked", isNicked);
            yml.set("Nickname-Skin", skin);
            yml.set("Nickname", newNick);
            yml.set("NicknameRank", rank.getId());
            try {
                yml.save(file);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    public void setNicknameState(@NotNull UUID uuid, boolean isNicked) {
        if (useDatabase)
            db.setStoredNicknameState(uuid, isNicked);
        else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isNicked", isNicked);
            try {
                yml.save(file);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    private String getNicknameSkin(UUID uuid) {
        if (useDatabase) {
            return db.getStoredNicknameSkin(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            return yml.getString("Nickname-Skin");
        }
    }

    /**
     * @return List<UUID> List of UUIDs there is data for.
     */

    public List<UUID> getUUIDs() {
        if (useDatabase) {
            return db.getUUIDs();
        } else {
            List<UUID> returnme = new ArrayList<>();
            for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                File file = new File("/plugins/FoxRank/PlayerData/" + op.getUniqueId() + ".yml");
                if (file.exists()) returnme.add(op.getUniqueId());
            }
            return returnme;
        }
    }

    public void addLoggingEntry(UUID involved, Entry entry) {
        if (useDatabase) {
            db.addEntry(involved, entry);
        } else {
            File file = new File("plugins/FoxRank/auditlog.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.getStringList(involved.toString()).add(entry.serialize());
            try {
                yml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Entry> getLogEntries(UUID uuid) {
        if (useDatabase) {
            return db.getEntries(uuid);
        } else {
            File file = new File("plugins/FoxRank/auditlog.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            List<String> str = yml.getStringList(uuid.toString());
            List<Entry> returnme = new ArrayList<>();
            for (String s : str) {
                returnme.add(Entry.deserialize(s));
            }
            return returnme;
        }
    }

    public void setupPlayerInfoStorage(Player player) {
        if (useDatabase) {
            db.addPlayerData(player.getUniqueId());
        } else {

            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.addDefault("Name", player.getName());
            yml.addDefault("UUID", player.getUniqueId().toString());
            yml.addDefault("Rank", plugin.getDefaultRank().getId());
            yml.addDefault("isVanished", false);
            yml.addDefault("isNicked", false);
            yml.addDefault("isMuted", false);
            yml.addDefault("MuteDuration", "");
            yml.addDefault("MuteReason", "");
            yml.addDefault("Nickname", player.getName());
            yml.addDefault("Nickname-Rank", plugin.getDefaultRank().getId());
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
    }

    public void handleEventMessage(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        String eventMessage = e.getMessage();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (useDatabase) {
            if (isMuted(uuid)) {
                Instant date = Instant.parse(db.getStoredMuteDuration(uuid));
                Instant now = Instant.now();
                if (date.isBefore(now)) {
                    ModerationAction.unmutePlayer(new RankedPlayer(player, plugin), new RankedPlayer(player, plugin));
                } else {
                    String reason = db.getStoredMuteReason(uuid);
                    String border = ChatColor.RED + String.valueOf(ChatColor.STRIKETHROUGH) + "                                                                   ";
                    String muteMessage = plugin.getConfig().getString("ChatWhileMutedMessage").replace("$LINE", border);
                    muteMessage = muteMessage.replace("\\n", "\n");
                    muteMessage = muteMessage.replace("$DURATION", plugin.getFormattedExpiredString(date, Instant.now()));
                    muteMessage = muteMessage.replace("$REASON", reason);
                    muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
                    player.sendMessage(muteMessage);
                    return;
                }
            }
            if (db.getStoredNicknameStatus(uuid)) {
                Rank rank = db.getStoredNicknameRank(uuid);
                String nick = db.getStoredNickname(uuid);
                if (plugin.disableRankVis) {
                    Bukkit.broadcastMessage(nick + ": " + e.getMessage());
                } else {
                    Bukkit.broadcastMessage(rank.getPrefix() + ChatColor.RESET + rank.getColor() + player.getName() + ChatColor.RESET + ": " + rank.getTextColor() + eventMessage);
                }
            } else {
                Rank rank = plugin.getRank(player);
                if (!plugin.disableRankVis) {
                    if (rank == null) return;
                    if (plugin.getRank(player) != plugin.getDefaultRank()) {
                        Bukkit.broadcastMessage(rank.getPrefix() + ChatColor.RESET + rank.getColor() + player.getName() + ChatColor.RESET + ": " + rank.getTextColor() + eventMessage);
                    } else if (plugin.getRank(player) == plugin.getDefaultRank()) {
                        Bukkit.broadcastMessage(plugin.getRank(player).getPrefix() + ChatColor.RESET + rank.getColor() + player.getDisplayName() + ChatColor.RESET + plugin.getDefaultRank().getTextColor() + ": " + eventMessage);
                    }
                } else {
                    Bukkit.broadcastMessage(player.getName() + ": " + e.getMessage());
                }
            }
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

            if (isMuted(player.getUniqueId())) {
                Instant date = Instant.parse(yml.getString("MuteDuration"));
                Instant now = Instant.now();
                if (date.isBefore(now)) {
                    ModerationAction.unmutePlayer(new RankedPlayer(player, plugin), new RankedPlayer(player, plugin));
                } else {
                    String reason = yml.getString("MuteReason");
                    String border = ChatColor.RED + String.valueOf(ChatColor.STRIKETHROUGH) + "                                                                   ";
                    String muteMessage = plugin.getConfig().getString("ChatWhileMutedMessage").replace("$LINE", border);
                    muteMessage = muteMessage.replace("\\n", "\n");
                    muteMessage = muteMessage.replace("$DURATION", plugin.getFormattedExpiredString(date, Instant.now()));
                    muteMessage = muteMessage.replace("$REASON", reason);
                    muteMessage = ChatColor.translateAlternateColorCodes('§', muteMessage);
                    player.sendMessage(muteMessage);
                    return;
                }
            }
            if (yml.getBoolean("isNicked")) {
                Rank rank = Rank.of(yml.getString("Nickname-Rank"));
                String nick = yml.getString("Nickname");
                if (!plugin.disableRankVis) {
                    Bukkit.broadcastMessage(rank.getPrefix() + ChatColor.RESET + rank.getColor() + e.getPlayer().getName() + ChatColor.RESET + ": " + rank.getTextColor() + eventMessage);
                } else {
                    Bukkit.broadcastMessage(nick + ": " + e.getMessage());
                }
            } else {
                Rank rank = Rank.of(yml.getString("Nickname-Rank"));
                if (!plugin.disableRankVis) {
                    if (plugin.getRank(player) != plugin.getDefaultRank()) {
                        Bukkit.broadcastMessage(rank.getPrefix() + ChatColor.RESET + rank.getColor() + player.getName() + ChatColor.RESET + ": " + e.getMessage());
                    } else if (plugin.getRank(player) == plugin.getDefaultRank()) {
                        Bukkit.broadcastMessage(rank.getPrefix() + ChatColor.RESET + rank.getColor() + player.getName() + ChatColor.RESET + ": " + plugin.getDefaultRank().getTextColor() + e.getMessage());
                    }
                } else {
                    Bukkit.broadcastMessage(player.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    private String getStoredBanReason(UUID uuid) {
        if (useDatabase) {
            return db.getStoredBanReason(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            return yml.getString("BanReason");
        }
    }

    private Instant getStoredBanDuration(UUID uuid) {
        String data;
        if (useDatabase) {
            data = db.getStoredBanDuration(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            data = yml.getString("BanDuration");
        }
        if(data == null)
            return null;
        return Instant.parse(data);
    }

    private String getStoredBanID(UUID uuid) {
        if (useDatabase) {
            return db.getStoredBanID(uuid);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            return yml.getString("BanID");
        }
    }

    public List<String> getPlayers() {
        if (useDatabase) {
            return db.getNames();
        } else {
            List<String> strings = new ArrayList<>();
            try {
                for (String s : listFiles()) {
                    File file = new File("plugins/FoxRank/PlayerData/" + s);
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    strings.add(yml.getString("name"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return strings;
        }
    }

    private Set<String> listFiles() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get("plugins/FoxRank/PlayerData/"))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    public void updatePlayerName(Player player) {
        if (!useDatabase) {
            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("Name", player.getName());
        }
    }

    public void mutePlayer(UUID uuid, Instant duration, String reason) {
        if (useDatabase) {
            db.setStoredMuteData(uuid, true, reason, duration);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isMuted", true);
            yml.set("MuteDuration", duration.toString());
            yml.set("MuteReason", reason);
            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + uuid + "'s Mute status!");
                e.printStackTrace();
            }
        }
    }

    public void unmutePlayer(UUID uuid) {
        if (useDatabase) {
            db.setStoredMuteState(uuid, false);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isMuted", false);

            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + uuid + "'s Mute status");
                e.printStackTrace();
            }
        }
    }

    public void unbanPlayer(UUID uuid) {
        if (useDatabase) {
            List<UUID> bannedPlayers = db.getStoredBannedPlayers();
            bannedPlayers.remove(uuid);
            db.setStoredBannedPlayers(bannedPlayers);
            db.setStoredBanState(uuid, false);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("isBanned", false);
            try {
                yml.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save " + uuid + "'s Ban status!");
                e.printStackTrace();
            }
        }
    }

    public void banPlayer(UUID uuid, String reason, String id, Instant duration) {
        if (useDatabase) {
            List<UUID> bannedPlayers = db.getStoredBannedPlayers();
            bannedPlayers.add(uuid);
            db.setStoredBannedPlayers(bannedPlayers);
            db.setStoredBanData(uuid, true, reason, duration, id);
        } else {
            File file = new File("plugins/FoxRank/PlayerData/" + uuid + ".yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("BanReason", reason);
            yml.set("BanDuration", duration.toString());
            yml.set("BanID", id);
            yml.set("isBanned", true);


            try {
                yml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file1 = new File("plugins/FoxRank/bannedPlayers.yml");
            YamlConfiguration yml1 = YamlConfiguration.loadConfiguration(file1);
            List<String> list = yml1.getStringList("CurrentlyBannedPlayers");
            list.add(uuid.toString());
            try {
                yml1.set("CurrentlyBannedPlayers", list);
                yml1.save(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
