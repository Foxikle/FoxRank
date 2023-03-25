package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.foxikle.foxrank.FoxRank.getRank;

public class SetRank implements CommandExecutor, TabExecutor {

    private final FoxRank foxRank = FoxRank.getInstance();
    List<String> rankList = Arrays.asList("OWNER", "ADMIN", "MODERATOR", "YOUTUBE", "TWITCH", "MVP_PLUS", "MVP", "VIP_PLUS", "VIP", "DEFAULT");

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        RankedPlayer rankedPlayer = new RankedPlayer(player);
        if (label.equalsIgnoreCase("setrank")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableSetRank")) {
                if (sender instanceof Player) {
                    if (rankedPlayer.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("SetRankPermissions")) {
                        if (args.length < 1) {
                            FoxRank.getInstance().sendMissingArgsMessage("/setrank", "<rankID> [player]", new RankedPlayer(player));
                        } else if (args.length == 1) {
                            if (rankList.contains(args[0])) {
                                foxRank.setRank(player, Rank.valueOf(args[0]));
                                rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("RankIsNowMessage").replace("$RANK", Rank.valueOf(args[0]).getPrefix())));
                                foxRank.loadRank(player);
                            } else {
                                FoxRank.getInstance().sendInvalidArgsMessage("Rank", new RankedPlayer(player));
                            }
                        } else if (args.length >= 2) {
                            if (foxRank.bungeecord) {
                                FoxRank.getInstance().setRankOfflinePlayer(Bukkit.getOfflinePlayer(args[1]), Rank.ofString(args[0]));
                                FoxRank.getPluginChannelListener().sendUpdateDataMessage(args[1]);
                                FoxRank.getPluginChannelListener().sendMessage(player, args[1], ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("RankIsNowMessage").replace("$RANK", Rank.valueOf(args[0]).getPrefix())));
                                return true;
                            }
                            Player p = Bukkit.getServer().getPlayer(args[1]);
                            if (getRank(player).getPowerLevel() > getRank(p).getPowerLevel()) {
                                if (rankList.contains(args[0])) {
                                    Rank rank = Rank.valueOf(args[0]);
                                    foxRank.setRank(p, rank);
                                    new RankedPlayer(p).sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("RankIsNowMessage").replace("$RANK", Rank.valueOf(args[0]).getPrefix())));
                                    foxRank.loadRank(p);
                                } else {
                                    FoxRank.getInstance().sendInvalidArgsMessage("Rank", new RankedPlayer(player));
                                }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Your rank must be higher than " + new RankedPlayer(p).getPrefix() + new RankedPlayer(p).getName() + " to change their rank!");
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
            return FoxRank.getInstance().getPlayerNames((Player) sender);
        }
        return new ArrayList<>();
    }
}
