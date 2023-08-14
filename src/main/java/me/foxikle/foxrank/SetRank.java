package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetRank implements CommandExecutor, TabCompleter {

    private final FoxRank plugin;

    public SetRank(FoxRank plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableSetRank")) {
                if (player.hasPermission("foxrank.ranks.setrank")) {
                    if (args.length < 1) {
                        FoxRank.getInstance().syntaxMap.put(player.getUniqueId(), "/setrank <RankID> [Player]");
                        player.sendMessage(FoxRank.getInstance().getSyntaxMessage(player));
                    } else if (args.length == 1) {
                        if(!Rank.exists(args[0])) {
                            sender.sendMessage(ChatColor.RED + "The rank '" + args[0] + "' does not exist!");
                        }
                        Rank rank = plugin.getRank(player);
                        if (FoxRank.getInstance().ranks.containsKey(args[0])) {
                            if (Rank.of(args[0]).getPowerlevel() > rank.getPowerlevel()) {
                                player.sendMessage(plugin.getMessage("PromoteSelfMessage", player));
                                return true;
                            } else if (Rank.of(args[0]).getPowerlevel() == rank.getPowerlevel()) {
                                player.sendMessage(plugin.getMessage("AlreadyRankMessage", player));
                                return true;
                            }
                            plugin.setRank(player, Rank.of(args[0]));
                            player.sendMessage(plugin.getMessage("RankIsNowMessage", player));
                        } else {
                            plugin.syntaxMap.put(player.getUniqueId(), "/setrank <RankID> [Player]");
                            player.sendMessage(FoxRank.getInstance().getSyntaxMessage(player));
                        }
                    } else {
                        if(!Rank.exists(args[0])) {
                            sender.sendMessage(ChatColor.RED + "The rank '" + args[0] + "' does not exist!");
                        }
                        if (plugin.bungeecord) {
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setStoredRank(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), Rank.of(args[0])));
                            plugin.getPluginChannelListener().sendUpdateDataMessage(args[1]);
                            plugin.getPluginChannelListener().sendMessage(player, args[1], plugin.getMessage("RankIsNowMessage", player));
                            return true;
                        }
                        Player p = Bukkit.getServer().getPlayer(args[1]);
                        if (FoxRank.getInstance().getRank(player).getPowerlevel() > FoxRank.getInstance().getRank(p).getPowerlevel()) {
                            if (FoxRank.getInstance().ranks.containsKey(args[0])) {
                                plugin.setRank(p, Rank.of(args[0]));
                                p.sendMessage(plugin.getMessage("RankIsNowMessage", p));
                            } else {
                                plugin.syntaxMap.put(player.getUniqueId(), "/setrank <RankID> [Player]");
                                player.sendMessage(FoxRank.getInstance().getSyntaxMessage(player));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Your rank must be higher than " + plugin.getRank(p).getPrefix() + p.getName() + " to change their rank!");
                        }
                    }
                } else {
                    player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                }
            } else {
                sender.sendMessage(plugin.getMessage("CommandDisabledMessage", player));
            }
        } else {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableSetRank")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid Arguments: /setrank <rankID> <player>");
                } else {
                    if(!Rank.exists(args[0])) {
                        sender.sendMessage(ChatColor.RED + "The rank '" + args[0] + "' does not exist!");
                    }
                    if (plugin.bungeecord) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            plugin.getPlayerData(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).setRank(Rank.of(args[0]));
                            plugin.getDm().setStoredRank(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), Rank.of(args[0]));
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                plugin.getPluginChannelListener().sendUpdateDataMessage(args[1]);
                                //todo: fix this probably VV
                                plugin.getPluginChannelListener().sendMessage(Iterables.getLast(Bukkit.getOnlinePlayers()), args[1], plugin.getMessage("RankIsNowMessage", Bukkit.getOfflinePlayer(args[1])));
                            }, 20);
                        });
                         return true;
                    } else {
                        Player p = Bukkit.getServer().getPlayer(args[1]);

                        if (FoxRank.getInstance().ranks.containsKey(args[0])) {
                            Rank rank = Rank.of(args[0]);
                            plugin.setRank(p, rank);
                            p.sendMessage(plugin.getMessage("RankIsNowMessage", p));
                        } else {
                            sender.sendMessage(ChatColor.RED + "Invalid Rank provided");
                        }
                    }
                }
            } else {
                sender.sendMessage(plugin.getMessage("CommandDisabledMessage", Bukkit.getOfflinePlayer(args[1])));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.addAll(FoxRank.getInstance().ranks.keySet());
            return arguments;
        } else if (args.length == 2) {
            return FoxRank.getInstance().players;
        }
        return new ArrayList<>();
    }
}
