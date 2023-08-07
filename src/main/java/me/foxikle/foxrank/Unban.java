package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Unban implements CommandExecutor, TabCompleter {

    private final FoxRank plugin;

    public Unban(FoxRank plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("unban")) {
            if (sender instanceof Player player) {
                if (player.hasPermission("foxrank.moderation.unban")) {
                    if (args.length >= 1) {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
                        plugin.targetMap.put(player.getUniqueId(), op.getUniqueId());
                        if (FoxRank.getInstance().getDm().getBannedPlayers().contains(op.getUniqueId())) {
                            ModerationAction.unbanPlayer(op.getUniqueId(), player.getUniqueId());
                            player.sendMessage(ChatColor.GREEN + op.getName() + " was unbanned.");
                        } else {
                            player.sendMessage(ChatColor.RED + op.getName() + " is not banned!");
                        }
                    } else {
                        FoxRank.getInstance().syntaxMap.put(player.getUniqueId(), "/unban <player>");
                        player.sendMessage(FoxRank.getInstance().getSyntaxMessage(player));
                    }
                } else {
                    player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                }
                return true;
            }
        } else {
            return onCommand(sender, cmd, label, args);
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            //todo: cache banned players
            List<UUID> list = FoxRank.getInstance().getDm().getBannedPlayers();
            List<String> returnme = new ArrayList<>();
            for (UUID uuid : list) {
                returnme.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
            return returnme;
        } else {
            return new ArrayList<>();
        }
    }
}
