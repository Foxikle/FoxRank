package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import java.util.*;
import java.util.logging.Level;

import static me.foxikle.foxrank.Rank.*;

public class FoxRank extends JavaPlugin implements Listener {

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




    private static void setupTeams(){
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        try {
            Team dT = board.registerNewTeam("DEFAULTRankTeam");
            dT.setColor(ChatColor.GRAY);
            dT.setPrefix(DEFAULT.getPrefix());
            Team oT = board.registerNewTeam("ownerTeam");
            oT.setColor(ChatColor.RED);
            oT.setPrefix(OWNER.getPrefix());
            Team aT = board.registerNewTeam("adminTeam");
            aT.setColor(ChatColor.RED);
            aT.setPrefix(ADMIN.getPrefix());
            Team moT = board.registerNewTeam("moderatorTeam");
            moT.setColor(ChatColor.DARK_GREEN);
            moT.setPrefix(MODERATOR.getPrefix());
            Team yT = board.registerNewTeam("youtubeTeam");
            yT.setColor(ChatColor.RED);
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
            MvpTeam =  mT;
            VippTeam = vpT;
            VipTeam = vT;
        } catch(IllegalArgumentException ignored){
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
            MvpTeam =  mT;
            VippTeam = vpT;
            VipTeam = vT;
        }



    }


    Map <String, Integer> rankList = new HashMap<>();

    public static void setTeam(Player player, String teamID){
            System.out.println("setTeam called!");

        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(board);



        if(teamID.equals("OWNER")){
            OwnerTeam.addEntry(player.getName());
        } else if(teamID.equals("ADMIN")){
            AdminTeam.addEntry(player.getName());
        } else if(teamID.equals("MODERATOR")){
            ModeratorTeam.addEntry(player.getName());
        } else if(teamID.equals("YOUTUBE")){
            YoutubeTeam.addEntry(player.getName());
        } else if(teamID.equals("TWITCH")){
            TwitchTeam.addEntry(player.getName());
        } else if(teamID.equals("MVP_PLUS")){
            MvppTeam.addEntry(player.getName());
        } else if(teamID.equals("MVP")){
            MvpTeam.addEntry(player.getName());
        } else if(teamID.equals("VIP_PLUS")){
            VippTeam.addEntry(player.getName());
        } else if(teamID.equals("VIP")){
            VipTeam.addEntry(player.getName());
        } else if(teamID.equals("DEFAULT")){
            DefualtTeam.addEntry(player.getName());
        }
    }

    static Map<Player, Rank> ranks = new HashMap<>();
    public static FoxRank instance;
    private Nick nick;
    private JoinLeaveMsgs msg;

    @Override
    public void onEnable() {
        setupTeams();
        String[] foo = new String[]{ "OWNER", "ADMIN", "MODERATOR", "YOUTUBE", "TWITCH", "MVP_PLUS", "MVP", "VIP_PLUS", "VIP", "DEFAULT" };
        rankList.put(foo[0], 1);
        rankList.put(foo[1], 2);
        rankList.put(foo[2], 3);
        rankList.put(foo[3], 4);
        rankList.put(foo[4], 5);
        rankList.put(foo[5], 6);
        rankList.put(foo[6], 7);
        rankList.put(foo[7], 8);
        rankList.put(foo[8], 9);
        rankList.put(foo[9], 10);


        this.saveResource("config.yml", false);
        instance = this;
        nick = new Nick();
        msg = new JoinLeaveMsgs();
        getServer().getPluginManager().registerEvents(this,this);
        getServer().getPluginManager().registerEvents(msg,this);

        reloadConfig();
        for(Player p : this.getServer().getOnlinePlayers()) {
            loadRank(p);
        }
        getCommand("nick").setExecutor(nick);
    }
    public static FoxRank getInstance(){
        return instance;
    }

    @Override
    public void onDisable() {
        for(Player p : this.getServer().getOnlinePlayers()) {
            saveRank(p);
        }
    }


    public static void setRank(Player player, Rank rank) {
        ranks.put(player, rank);
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("Rank", rank.getRankID());
        try{
            yml.save(file);
        } catch (IOException error){
            error.printStackTrace();
        }

    }

    public static Rank getRank(Player player) {
        return ranks.get(player);
    }

    public static void loadRank(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String rankID = yml.getString("Rank");
        if (rankID != null) {

        Rank rank = DUMMY.getRankFromString(rankID);
        player.setDisplayName(rank.getPrefix() + player.getName());
        player.setPlayerListName(rank.getPrefix() + player.getName());
        setTeam(player, rank.getRankID());
        setRank(player, rank);
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "rankID is null");
        }
    }

    public static void saveRank(Player player) {
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("Rank", getRank(player).getRankID());
        try{
            yml.save(file);
        } catch (IOException error){
            error.printStackTrace();
        }
        //saveConfig();

        ranks.remove(player);
    }

    @EventHandler
    public void onJoin (PlayerJoinEvent event) {
        Player p = event.getPlayer();
        loadRank(p);
    }

    @EventHandler
    public void onLeave (PlayerQuitEvent event) {
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


        if (yml.getString("isNicked").equals("true")) {
            System.out.println("The player chatting is nicked!");
            if (getRankFromString(yml.getString("Nickname-Rank")) == DEFAULT) {
                newMessage = ChatColor.GRAY + yml.getString("Nickname") + ": " + eventMessage;
                Bukkit.broadcastMessage(newMessage);
            } else if (getRankFromString(yml.getString("Nickname-Rank")) != DEFAULT) {
                newMessage = getRankFromString(yml.getString("Nickname-Rank")).getPrefix() + "" + yml.getString("Nickname") + ChatColor.RESET + ": " + eventMessage;
                Bukkit.broadcastMessage(newMessage);
            }
        } else {
            System.out.println("The player chatting is NOT nicked!");
            if (getRank(player) != DEFAULT) {
                newMessage = getRank(player).getPrefix() + player.getName() + ChatColor.RESET + ": " + eventMessage;
                Bukkit.broadcastMessage(newMessage);
            } else if (getRank(player) == DEFAULT) {
                newMessage = getRank(player).getPrefix() + player.getName() + ChatColor.RESET + "" + ChatColor.GRAY + ": " + eventMessage;
                Bukkit.broadcastMessage(newMessage);
            }
        }
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("setrank")) {
            if (sender instanceof Player) {
                if (args.length > 1) {
                    player.sendMessage(ChatColor.RED + "Usage /setrank <rankID>");
                } else if(rankList.containsKey(args[0])) {
                    if (player.hasPermission("setrank.use")) {
                        setRank(player, Rank.valueOf(args[0]));
                        player.sendMessage("Your rank is now set to: " + ChatColor.BOLD + args[0]);
                        loadRank(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have the suitable permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Please contact a server administrator is you think this is a mistake.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid Rank : <OWNER/ADMIN/MODERATOR/YOUTUBE/TWITCH/MVP_PLUS/MVP/VIP_PLUS/VIP/DEFAULT>");
                }
            }
            return true;
        }
        return false;
    }
    @EventHandler
    public static void OnPlayerLogin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        File file =  new File("plugins/FoxRank/PlayerData/" + p.getUniqueId() + ".yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch(IOException error){
                error.printStackTrace();
            }
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.addDefault("Name", p.getName());
        yml.addDefault("UUID", p.getUniqueId().toString());
        yml.addDefault("Rank", "DEFAULT");
        yml.addDefault("Punishments", "None!");
        yml.addDefault("isNicked", "false");
        yml.addDefault("Nickname", p.getName());
        yml.addDefault("Nickname-Rank", "DEFAULT");
        yml.options().copyDefaults(true);
        try{
            yml.save(file);
        } catch (IOException error){
            error.printStackTrace();
        }

    }
}
