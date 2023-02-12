package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Unban implements CommandExecutor, TabExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("unban")) {
            if (sender instanceof Player player) {
                RankedPlayer staff = new RankedPlayer(player);
                if (staff.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("UnbanPermissions")) {
                    if (args.length >= 1) {
                        Bukkit.getServer().getOfflinePlayer(args[0]);
                        OfflineRankedPlayer orp = new OfflineRankedPlayer(Bukkit.getServer().getOfflinePlayer(args[0]));
                        if (FoxRank.getInstance().useDb) {
                            if (FoxRank.getInstance().useDb) {
                                List<OfflinePlayer> players = FoxRank.getInstance().db.getStoredBannedPlayers();
                                FoxRank.getInstance().db.setStoredBannedPlayers(players);
                                FoxRank.getInstance().db.setStoredBanState(orp.getUniqueId(), false);
                            }
                        } else {
                            File file = new File("plugins/FoxRank/bannedPlayers.yml");
                            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                            List<String> list = yml.getStringList("CurrentlyBannedPlayers");
                            if (list.contains(orp.getUniqueId().toString())) {
                                list.remove(orp.getUniqueId().toString());
                                yml.set("CurrentlyBannedPlayers", list);
                                File file1 = new File("plugins/FoxRank/PlayerData/" + orp.getUniqueId() + ".yml");
                                YamlConfiguration yml1 = YamlConfiguration.loadConfiguration(file1);
                                yml1.set("isBanned", false);
                                try {
                                    yml.save(file);
                                    yml1.save(file1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                FoxRank.getInstance().getConfig().getString("UnbanCommandNotBanned");
                            }
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
            List<String> finalList = new java.util.ArrayList<>(List.of());
            if (FoxRank.getInstance().useDb) {
                for (OfflinePlayer player : FoxRank.getInstance().db.getStoredBannedPlayers()) {
                    finalList.add(player.getName());
                }
            } else {
                File file = new File("plugins/FoxRank/bannedPlayers.yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                for (String uuid : yml.getStringList("CurrentlyBannedPlayers")) {
                    finalList.add(FoxRank.getInstance().getTrueName(UUID.fromString(uuid)));
                }
            }
            return finalList;
        } else {
            return null;
        }
    }
}
