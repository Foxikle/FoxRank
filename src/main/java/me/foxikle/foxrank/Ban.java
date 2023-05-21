package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.util.Objects.hash;
import static me.foxikle.foxrank.ModerationAction.banOfflinePlayer;
import static me.foxikle.foxrank.ModerationAction.banPlayer;

public class Ban implements CommandExecutor, TabExecutor {
    private final FileConfiguration yml = FoxRank.getInstance().getConfig();
    private String reason = "No reason specified.";
    private Instant expires;
    private boolean silent = false;
    private String banID = null;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("ban")) {
            if (sender instanceof Player banner) {
                RankedPlayer staff = new RankedPlayer(banner, FoxRank.getInstance());
                if (staff.getPowerLevel() >= yml.getInt("BanPermissions")) {
                    if (args.length >= 4) {
                        if (Bukkit.getServer().getPlayer(args[0]) != null) {
                            Player banee = Bukkit.getServer().getPlayer(args[0]);
                            RankedPlayer baneeRp = new RankedPlayer(banee, FoxRank.getInstance());
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
                                banPlayer(staff, banee, silent, reason, expires, reason, banID);

                            }
                        } else {
                            if (FoxRank.getInstance().db.getNames().contains(args[0])) {
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
                                        banOfflinePlayer(staff, banee, reason, expires, reason, banID);

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

    private String getBanID(OfflinePlayer banee) {
        return Integer.toString(hash("FoxRank:" + banee.getName() + ":" + Instant.now()), 16).toUpperCase(Locale.ROOT);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return FoxRank.getInstance().db.getNames();
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
        return new ArrayList<>();
    }

    private String removeUnderScore(String string) {
        return string.replace("_", "");
    }
}
