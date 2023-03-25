package me.foxikle.foxrank;

import me.foxikle.foxrank.events.ModerationAction;
import me.foxikle.foxrank.events.ModerationActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.util.Objects.hash;

public class Ban implements CommandExecutor, TabExecutor {
    private final FileConfiguration yml = FoxRank.getInstance().getConfig();
    private String reason = "No reason specified.";
    private Instant expires;
    private boolean silent = false;
    private String banID = null;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("ban")) {
            if (sender instanceof Player banner) {
                RankedPlayer staff = new RankedPlayer(banner);
                if (staff.getPowerLevel() >= yml.getInt("BanPermissions")) {
                    if (args.length >= 4) {
                        if (Bukkit.getServer().getPlayer(args[0]) != null) {
                            Player banee = Bukkit.getServer().getPlayer(args[0]);
                            RankedPlayer baneeRp = new RankedPlayer(banee);
                            if (baneeRp.getPowerLevel() >= staff.getPowerLevel()) {
                                staff.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("BanPlayerWithHigherPowerLevelMessage")));
                            } else {
                                if (args[1].equalsIgnoreCase("SECURITY")) {
                                    reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                } else if (args[1].equalsIgnoreCase("HACKING")) {
                                    reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                } else if (args[1].equalsIgnoreCase("DUPING")) {
                                    reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                } else if (args[1].equalsIgnoreCase("BUG_ABUSE")) {
                                    reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                } else if (args[1].equalsIgnoreCase("INAPPROPRIATE_COSMETICS")) {
                                    reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                } else if (args[1].equalsIgnoreCase("INAPPROPRIATE_BUILD")) {
                                    reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                } else if (args[1].equalsIgnoreCase("BOOSTING")) {
                                    reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                } else if (args[1].equalsIgnoreCase("CUSTOM")) {
                                    ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
                                    list.remove(0);
                                    list.remove(0);
                                    list.remove(0);
                                    list.remove(0);
                                    reason = String.join(" ", list);
                                } else {
                                    reason = args[1];
                                }
                                if (args[2].contains("d") || args[2].contains("h") || args[2].contains("m") || args[2].equalsIgnoreCase("-1")) {
                                    expires = Instant.now();
                                    String durStr = args[2];
                                    if (args[2].equalsIgnoreCase("-1")) {
                                        expires = null;
                                    } else if (args[2].contains("d")) {
                                        durStr = durStr.replace("d", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 24 * 60 * 60);
                                    } else if (args[2].contains("h")) {
                                        durStr = durStr.replace("h", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 60 * 60);
                                    } else if (args[2].contains("m")) {
                                        durStr = durStr.replace("m", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 60);
                                    }
                                } else {
                                    FoxRank.getInstance().sendInvalidArgsMessage(args[2] + " Ex. `30d`, `24h`, `30m`", staff);
                                    return false;

                                }
                                if (args[3].equalsIgnoreCase("SILENT")) {
                                    silent = true;
                                } else if (args[3].equalsIgnoreCase("PUBLIC")) {
                                    silent = false;
                                } else {
                                    FoxRank.getInstance().sendInvalidArgsMessage("<SILENT/PUBLIC>", staff);
                                }
                                reason = removeUnderScore(reason);
                                banID = getBanID(banee);
                                banPlayer(staff, banee, silent, reason, expires, args[1]);

                            }
                        } else {
                            if (Bukkit.getOfflinePlayer(FoxRank.getInstance().getUUID(args[0])) != null) {
                                System.out.println(Arrays.toString(args));
                                OfflinePlayer banee = Bukkit.getServer().getOfflinePlayer(FoxRank.getInstance().getUUID(args[0]));
                                if (banee != null) {
                                    OfflineRankedPlayer baneeRp = new OfflineRankedPlayer(banee);
                                    if (baneeRp.getPowerLevel() >= staff.getPowerLevel()) {
                                        staff.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("BanPlayerWithHigherPowerLevelMessage")));
                                    } else {
                                        if (args[1].equalsIgnoreCase("SECURITY")) {
                                            reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                        } else if (args[1].equalsIgnoreCase("HACKING")) {
                                            reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                        } else if (args[1].equalsIgnoreCase("DUPING")) {
                                            reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                        } else if (args[1].equalsIgnoreCase("BUG_ABUSE")) {
                                            reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                        } else if (args[1].equalsIgnoreCase("INAPPROPRIATE_COSMETICS")) {
                                            reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                        } else if (args[1].equalsIgnoreCase("INAPPROPRIATE_BUILD")) {
                                            reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                        } else if (args[1].equalsIgnoreCase("BOOSTING")) {
                                            reason = yml.getConfigurationSection("BanReasons").getString(args[1]);
                                        } else {
                                            reason = args[1];
                                        }
                                        if (args[2].contains("d") || args[2].contains("h") || args[2].contains("m") || args[2].equalsIgnoreCase("-1")) {
                                            expires = Instant.now();
                                            String durStr = args[2];
                                            if (args[1].equalsIgnoreCase("-1")) {
                                                expires = null;
                                            } else if (args[2].contains("d")) {
                                                durStr = durStr.replace("d", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 24 * 60 * 60);
                                            } else if (args[2].contains("h")) {
                                                durStr = durStr.replace("h", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60 * 60);
                                            } else if (args[2].contains("m")) {
                                                durStr = durStr.replace("m", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60);
                                            }
                                        } else {
                                            FoxRank.getInstance().sendInvalidArgsMessage(args[2] + " Ex. `30d`, `24h`, `30m`", staff);
                                            return false;

                                        }
                                        if (args[3].equalsIgnoreCase("SILENT")) {
                                            silent = true;
                                        } else if (args[3].equalsIgnoreCase("PUBLIC")) {
                                            silent = false;
                                        } else {
                                            FoxRank.getInstance().sendInvalidArgsMessage("<SILENT/PUBLIC>", staff);
                                        }
                                        reason = removeUnderScore(reason);
                                        banID = getBanID(banee);
                                        banOfflinePlayer(staff, banee, reason, expires, args[1]);

                                    }
                                }
                            } else {
                                FoxRank.getInstance().sendInvalidArgsMessage("Player", staff);
                            }
                        }
                    } else {
                        FoxRank.getInstance().sendMissingArgsMessage("/ban", "<PLAYER> <REASON> <DURATION> <SILENT/PUBLIC>", staff);
                    }
                } else {
                    FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("BanPermissions"), staff);
                }
                return true;
            }
        } else {
            return onCommand(sender, cmd, label, args);
        }
        return true;
    }

    private void banPlayer(RankedPlayer banner, Player banee, boolean silent, String reasonStr, Instant duration, String broadcastReason) {
        FoxRank.getInstance().getServer().getPluginManager().callEvent(new ModerationActionEvent(banee, banner.getPlayer(), new RankedPlayer(banee.getPlayer()).getRank(), banner.getRank(), ModerationAction.BAN));
        String bumper = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        if (!FoxRank.getInstance().getBannedPlayers().contains(banee)) {
            File file = new File("plugins/FoxRank/bannedPlayers.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            List<String> list = yml.getStringList("CurrentlyBannedPlayers");
            list.add(banee.getUniqueId().toString());
            try {
                yml.set("CurrentlyBannedPlayers", list);
                yml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (duration != null) {
            banee.kickPlayer(bumper + FoxRank.getInstance().getConfig().getString("TempBanMessageFormat").replace("$DURATION", FoxRank.getInstance().getFormattedExpiredString(duration, Instant.now())).replace("$SERVER_NAME", FoxRank.getInstance().getConfig().getString("ServerName")).replace("$REASON", reason).replace("$APPEAL_LINK", FoxRank.getInstance().getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
        } else {
            banee.kickPlayer(bumper + FoxRank.getInstance().getConfig().getString("PermBanMessageFormat").replace("$SERVER_NAME", FoxRank.getInstance().getConfig().getString("ServerName")).replace("$REASON", reason).replace("$APPEAL_LINK", FoxRank.getInstance().getConfig().getString("BanAppealLink")).replace("$ID", banID).replace("\\n", "\n") + bumper);
        }
        if (!broadcastReason.equalsIgnoreCase("SECURITY")) {
            if (silent) {
                banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("SilentBanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", reasonStr)));
            } else {
                if (removeUnderScore(broadcastReason).equalsIgnoreCase("CUSTOM")) {
                    Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + banee.getName() + " was removed from your game.");
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + banee.getName() + " was removed from your game for " + removeUnderScore(broadcastReason));
                }
                banee.getWorld().playSound(banner.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1000f, 1);
                banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("BanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", "'" + reasonStr + "'")));
            }
        } else {
            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("SecurityBanSenderMessage").replace("$PLAYER", banee.getName())));
        }
        File file = new File("plugins/FoxRank/PlayerData/" + banee.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("BanReason", reasonStr);
        yml.set("BanDuration", duration.toString());
        yml.set("BanID", banID);
        yml.set("isBanned", true);
        FoxRank.getInstance().addOnlineBanLogEntry(new RankedPlayer(banee.getPlayer()), banner, Instant.now(), reasonStr, FoxRank.getInstance().getFormattedExpiredString(duration, Instant.now()), banID, silent);

        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void banOfflinePlayer(RankedPlayer banner, OfflinePlayer banee, String reasonStr, Instant duration, String broadcastReason) {
        FoxRank.getInstance().getServer().getPluginManager().callEvent(new ModerationActionEvent(banee.getPlayer(), banner.getPlayer(), new RankedPlayer(banee.getPlayer()).getRank(), banner.getRank(), ModerationAction.BAN));
        File file = new File("plugins/FoxRank/PlayerData/" + banee.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (duration == null) {
            yml.set("BanDuration", null);
        } else {
            yml.set("BanDuration", duration.toString());
        }
        if (broadcastReason.equalsIgnoreCase("SECURITY")) {
            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("SecurityBanSenderMessage").replace("$PLAYER", banee.getName())));
        } else {
            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("BanSenderMessage").replace("$PLAYER", banee.getName()).replace("$REASON", reasonStr)));
        }
        FoxRank.getInstance().addOfflineBanLogEntry(new OfflineRankedPlayer(banee), banner, Instant.now(), reasonStr, FoxRank.getInstance().getFormattedExpiredString(duration, Instant.now()), banID, silent);
        if (!FoxRank.getInstance().getBannedPlayers().contains(banee)) {
            File file1 = new File("plugins/FoxRank/bannedPlayers.yml");
            YamlConfiguration yml1 = YamlConfiguration.loadConfiguration(file1);
            List<String> list = yml1.getStringList("CurrentlyBannedPlayers");
            list.add(banee.getUniqueId().toString());
            try {
                yml1.set("CurrentlyBannedPlayers", list);
                yml1.save(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        yml.set("BanReason", reasonStr);
        yml.set("BanID", banID);
        yml.set("isBanned", true);
        FoxRank.getInstance().addOfflineBanLogEntry(new OfflineRankedPlayer(banee), banner, Instant.now(), reasonStr, FoxRank.getInstance().getFormattedExpiredString(duration, Instant.now()), banID, silent);
        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getBanID(OfflinePlayer banee) {
        return Integer.toString(hash("FoxRank:" + banee.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (Player player : players) {
                playerNames.add(player.getName());
            }
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                if (!player.isBanned()) {
                    playerNames.add(player.getName());
                }
            }

            return playerNames;
        } else if (args.length == 2) {
            List<String> arguments = new ArrayList<>();
            arguments.addAll(FoxRank.getInstance().getConfig().getConfigurationSection("BanReasons").getKeys(false));
            return arguments;
        } else if (args.length == 3) {
            List<String> arguments = new ArrayList<>();
            arguments.addAll(FoxRank.getInstance().getConfig().getConfigurationSection("BanDurations").getKeys(false));
            return arguments;
        } else if (args.length == 4) {
            List<String> arguments = new ArrayList<>();
            arguments.add("SILENT");
            arguments.add("PUBLIC");

            return arguments;
        }
        return null;
    }

    private String removeUnderScore(String string) {
        if (string.contains("_")) {
            string = string.replace("_", " ");
        }
        return string;
    }
}
