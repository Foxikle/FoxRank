package me.foxikle.foxrank;

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
            RankedPlayer rankedPlayer = new RankedPlayer(player, FoxRank.getInstance());
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableSetRank")) {
                if (rankedPlayer.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("SetRankPermissions")) {
                    if (args.length < 1) {
                        FoxRank.getInstance().sendMissingArgsMessage("/setrank", "<rankID> [player]", new RankedPlayer(player, FoxRank.getInstance()));
                    } else if (args.length == 1) {
                        if (FoxRank.getInstance().ranks.containsKey(args[0])) {
                            plugin.setRank(player, Rank.of(args[0]));
                            rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().dm.getConfig().getString("RankIsNowMessage").replace("$RANK", Rank.of(args[0]).getPrefix())));
                        } else {
                            FoxRank.getInstance().sendInvalidArgsMessage("Rank", new RankedPlayer(player, FoxRank.getInstance()));
                        }
                    } else {
                        if (plugin.bungeecord) {
                            plugin.dm.setStoredRank(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), Rank.of(args[0]));
                            plugin.getPluginChannelListener().sendUpdateDataMessage(args[1]);
                            plugin.getPluginChannelListener().sendMessage(player, args[1], ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("RankIsNowMessage").replace("$RANK", Rank.of(args[0]).getPrefix())));
                            return true;
                        }
                        Player p = Bukkit.getServer().getPlayer(args[1]);
                        if (FoxRank.getInstance().getRank(player).getPowerlevel() > FoxRank.getInstance().getRank(p).getPowerlevel()) {
                            if (FoxRank.getInstance().ranks.containsKey(args[0])) {
                                Rank rank = Rank.of(args[0]);
                                plugin.setRank(p, rank);
                                p.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("RankIsNowMessage").replace("$RANK", Rank.of(args[0]).getPrefix())));
                            } else {
                                FoxRank.getInstance().sendInvalidArgsMessage("Rank", new RankedPlayer(player, FoxRank.getInstance()));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Your rank must be higher than " + new RankedPlayer(p, FoxRank.getInstance()).getPrefix() + new RankedPlayer(p, FoxRank.getInstance()).getName() + " to change their rank!");
                        }
                    }
                } else {
                    FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("SetRankPermissions"), rankedPlayer);
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
            arguments.addAll(FoxRank.getInstance().ranks.keySet());
            return arguments;
        } else if (args.length == 2) {
            return FoxRank.getInstance().playerNames;
        }
        return new ArrayList<>();
    }
}
