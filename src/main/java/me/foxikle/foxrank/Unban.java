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
import java.util.UUID;

public class Unban implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("unban")) {
            if (sender instanceof Player player) {
                RankedPlayer staff = new RankedPlayer(player, FoxRank.getInstance());
                if (staff.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("UnbanPermissions")) {
                    if (args.length >= 1) {
                        Bukkit.getServer().getOfflinePlayer(args[0]);
                        OfflineRankedPlayer orp = new OfflineRankedPlayer(Bukkit.getServer().getOfflinePlayer(args[0]));
                        if (orp.isBanned()) {
                            ModerationAction.unbanPlayer(orp.getUniqueId(), staff.getUniqueId());
                            staff.sendMessage(ChatColor.GREEN + orp.getName() + " was unbanned.");
                        } else {
                            staff.sendMessage(ChatColor.RED + orp.getName() + " is not banned!");
                        }
                    } else {
                        FoxRank.getInstance().sendInvalidArgsMessage("Player", staff);
                    }
                } else {
                    FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("UnbanPermissions"), staff);
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
            List<UUID> list = FoxRank.getInstance().dm.getBannedPlayers();
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
