package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.foxikle.foxrank.FoxRank.getRank;
import static me.foxikle.foxrank.FoxRank.loadRank;
import static me.foxikle.foxrank.Rank.ADMIN;

public class setRank implements CommandExecutor, TabExecutor {

    Map<String, Integer> rankList = new HashMap<>();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String[] foo = new String[]{"OWNER", "ADMIN", "MODERATOR", "YOUTUBE", "TWITCH", "MVP_PLUS", "MVP", "VIP_PLUS", "VIP", "DEFAULT"};
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

        Player player = (Player) sender;
        RankedPlayer rankedPlayer = new RankedPlayer(player);

        if (label.equalsIgnoreCase("setrank")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableSetRank")) {
                if (sender instanceof Player) {
                    if (rankedPlayer.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("SetRankPermissions")) {
                        if (args.length < 1) {
                            player.sendMessage(ChatColor.RED + "Usage /setrank <rankID> <(player)>");
                        } else if (args.length == 1) {
                            if (rankList.containsKey(args[0])) {
                                FoxRank.setRank(player, Rank.valueOf(args[0]));
                                player.sendMessage("Your rank is now " + Rank.valueOf(args[0]).getPrefix());
                                loadRank(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "Invalid Rank: Please use the tab completions.");
                            }
                        } else if (args.length >= 2) {
                            if (getRank(player) == ADMIN) {
                                if (Bukkit.getServer().getPlayer(args[1]) != null) {
                                    Player p = Bukkit.getServer().getPlayer(args[1]);
                                    if (rankList.containsKey(args[0])) {
                                        FoxRank.setRank(p, Rank.valueOf(args[0]));
                                        p.sendMessage("Your rank is now " + Rank.valueOf(args[0]).getPrefix());
                                        loadRank(player);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Invalid Rank: Please use the tab completions.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Could not find that player.");
                                }
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You must have a power level of " + FoxRank.getInstance().getConfig().getInt("SetRankPermissions") + " or higher to use this command.");
                        player.sendMessage(ChatColor.RED + "Please contact a server administrator is you think this is a mistake.");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "This command is currently disabled.");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("OWNER");
            arguments.add("ADMIN");
            arguments.add("MODERATOR");
            arguments.add("YOUTUBE");
            arguments.add("TWITCH");
            arguments.add("MVP_PLUS");
            arguments.add("MVP");
            arguments.add("VIP_PLUS");
            arguments.add("VIP");
            arguments.add("DEFAULT");

            return arguments;

        } else if (args.length == 2) {
            List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (int i = 0; i < players.length; i++) {
                playerNames.add(players[i].getName());
            }
            return playerNames;
        }
        return null;
    }
}
