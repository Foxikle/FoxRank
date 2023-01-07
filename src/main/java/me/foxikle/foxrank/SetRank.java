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

public class SetRank implements CommandExecutor, TabExecutor {

    Map<String, Integer> rankList = new HashMap<>();
    private final FoxRank foxRank = FoxRank.getInstance();

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
                            FoxRank.getInstance().sendMissingArgsMessage("/setrank", "<rankID> [player]", new RankedPlayer(player));
                        } else if (args.length == 1) {
                            if (rankList.containsKey(args[0])) {
                                foxRank.setRank(player, Rank.valueOf(args[0]));
                                rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("RankIsNowMessage").replace("RANK", Rank.valueOf(args[0]).getPrefix() )));
                                foxRank.loadRank(player);
                            } else {
                                FoxRank.getInstance().sendInvalidArgsMessage("Rank", new RankedPlayer(player));
                            }
                        } else if (args.length >= 2) {

                                if (Bukkit.getServer().getPlayer(args[1]) != null) {
                                    Player p = Bukkit.getServer().getPlayer(args[1]);
                                    if (getRank(player).getPowerLevel() > getRank(p).getPowerLevel()){
                                    if (rankList.containsKey(args[0])) {
                                        foxRank.setRank(p, Rank.valueOf(args[0]));
                                        new RankedPlayer(p).sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("RankIsNowMessage").replace("RANK", Rank.valueOf(args[0]).getPrefix() )));
                                        foxRank.loadRank(player);
                                        foxRank.loadRank(p);
                                    } else {
                                        FoxRank.getInstance().sendInvalidArgsMessage("Rank", new RankedPlayer(player));
                                    }
                                } else {
                                    FoxRank.getInstance().sendInvalidArgsMessage("Player", new RankedPlayer(player));
                                }
                            }
                        } else {
                            FoxRank.getInstance().sendMissingArgsMessage("/setrank", "<rankID> [player]", new RankedPlayer(player));
                        }
                    } else {
                        FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("SetRankPermissions"), rankedPlayer);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("CommandDisabledMessage")));
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
