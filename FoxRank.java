package me.foxikle.foxrank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class FoxRank extends JavaPlugin implements Listener {

    Map<Player, Rank> ranks = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);

        reloadConfig();
        for(Player p : this.getServer().getOnlinePlayers()) {
            loadRank(p);
        }

    }

    @Override
    public void onDisable() {
        for(Player p : this.getServer().getOnlinePlayers()) {
            saveRank(p);
        }
    }


    public void setRank(Player player, Rank rank) {
        ranks.put(player, rank);
        getConfig().set(player.getUniqueId() + ".rank", getRank(player));
        saveConfig();

    }

    public Rank getRank(Player player) {
        return ranks.get(player);
    }

    public void loadRank(Player player) {
        Object rankObj = getConfig().get(player.getUniqueId() + ".rank");
        System.out.println("rankObj is " + rankObj);
        Rank rank = rankObj == null ? Rank.NONE : (Rank) rankObj;
        player.setDisplayName(rank.getPrefix() + player.getName());
        player.setPlayerListName(rank.getPrefix() + player.getName());
        setRank(player, rank);
    }

    public void saveRank(Player player) {
        System.out.println("About to set " + getRank(player));
        getConfig().set(player.getUniqueId() + ".rank", getRank(player));
        System.out.println("Just set " + getConfig().get(player.getUniqueId() + ".rank"));
        saveConfig();

        ranks.remove(player);
    }

    @EventHandler
    public void onJoin (PlayerJoinEvent event) {
        Player p = event.getPlayer();
        loadRank(p);
        System.out.println("Ranks loaded!");
        System.out.println(getRank(p));
    }

    @EventHandler
    public void onLeave (PlayerQuitEvent event) {
        Player p = event.getPlayer();
        System.out.println(getRank(p));
        saveRank(p);
        System.out.println("Ranks saved!");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (this.getConfig().getString(e.getPlayer().getName()) != null) {
          //  e.getPlayer().setDisplayName(getConfig().getString(e.getPlayer().getName()) + ChatColor.RESET);

            String eventMessage = e.getMessage();
            Player player = e.getPlayer();

            String format = "<group-prefix><player>:<message> + ChatColor.RESET";
//replacing your values
            format.replace("<player>", player.getName());
            format.replace("<group-prefix>", getRank(player).getPrefix());
            format.replace("<message>", eventMessage);
            e.setFormat(format);
            e.setMessage("we are writing something before each message" + eventMessage);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        System.out.println("the sender for setrank is " + player.getName());
        if (label.equalsIgnoreCase("setrank")) {
            if (sender instanceof Player) {
                if (args.length > 1) {
                    player.sendMessage(ChatColor.RED + "Usage /setrank <rank name>");
                } else {
                    if (player.hasPermission("rank.use")) {
                        setRank(player, Rank.valueOf(args[0]));
                        player.sendMessage("Your rank is now set to: " + ChatColor.BOLD + args[0]);
                        loadRank(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have the suitable permissions to use this command.");
                        player.sendMessage(ChatColor.RED + "Please contact a server administrator is you think this is a mistake.");
                    }
                }
            }
            return true;
        }
        return false;
    }

}
