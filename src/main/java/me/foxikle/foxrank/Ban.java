package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
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


public class Ban implements CommandExecutor, TabCompleter {
    private final FoxRank plugin;
    private final FileConfiguration yml;
    private String reason = "No reason specified.";
    private Instant expires;
    private boolean silent = false;
    private String banID = null;

    public Ban(FoxRank plugin) {
        this.plugin = plugin;
        this.yml = plugin.getConfig();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("ban")) {
            if (sender instanceof Player banner) {
                RankedPlayer staff = new RankedPlayer(banner, plugin);
                    if (args.length >= 4) {
                        if (Bukkit.getServer().getPlayer(args[0]) != null) {
                            Player banee = Bukkit.getServer().getPlayer(args[0]);
                            RankedPlayer baneeRp = new RankedPlayer(banee, plugin);
                            if (banee.hasPermission("foxrank.moderation.ban.immune") || baneeRp.getPowerLevel() >= staff.getPowerLevel()) {
                                staff.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("CannotBanPlayerMessage")));
                            } else {
                                ConfigurationSection section = yml.getConfigurationSection("BanReasons");
                                if (args[1].equalsIgnoreCase("CUSTOM")) {
                                    ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
                                    list.remove(0);
                                    list.remove(0);
                                    list.remove(0);
                                    list.remove(0);
                                    reason = String.join(" ", list);
                                } else if (section.contains(args[1])) {
                                    reason = section.getString(args[1]);
                                } else {
                                    staff.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPresetBanReason")));
                                    return true;
                                }
                                if (args[2].contains("d") || args[2].contains("h") || args[2].contains("m") || args[2].equalsIgnoreCase("-1")) {
                                    expires = Instant.now();
                                    String durStr = args[2];
                                    if (args[2].equalsIgnoreCase("-1")) {
                                        if (!banner.hasPermission("foxrank.moderation.ban.permanent")) {
                                            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPermissionMessage")));
                                            return true;
                                        }

                                        expires = null;
                                    } else if (args[2].contains("d")) {
                                        if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPermissionMessage")));
                                            return true;
                                        }
                                        durStr = durStr.replace("d", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 24 * 60 * 60);
                                    } else if (args[2].contains("h")) {
                                        if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPermissionMessage")));
                                            return true;
                                        }
                                        durStr = durStr.replace("h", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 60 * 60);
                                    } else if (args[2].contains("m")) {
                                        if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                            banner.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPermissionMessage")));
                                            return true;
                                        }
                                        durStr = durStr.replace("m", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 60);
                                    }
                                } else {
                                    plugin.sendInvalidArgsMessage(args[2] + " Ex. `30d`, `24h`, `30m`", staff);
                                    return false;

                                }
                                if (args[3].equalsIgnoreCase("SILENT")) {
                                    silent = true;
                                } else if (args[3].equalsIgnoreCase("PUBLIC")) {
                                    silent = false;
                                } else {
                                    plugin.sendInvalidArgsMessage("<SILENT/PUBLIC>", staff);
                                }
                                reason = removeUnderScore(reason);
                                banID = getBanID(banee);
                                banPlayer(staff, banee, silent, reason, expires, reason, banID);

                            }
                        } else {
                            if (plugin.dm.getPlayers().contains(args[0])) {
                                OfflinePlayer banee = Bukkit.getServer().getOfflinePlayer(plugin.dm.getUUID(args[0]));
                                if (banee != null) {
                                    OfflineRankedPlayer baneeRp = new OfflineRankedPlayer(banee);
                                    if (baneeRp.getPowerLevel() >= staff.getPowerLevel()) {
                                        staff.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("CannotBanPlayerMessage")));
                                    } else {
                                        ConfigurationSection section = yml.getConfigurationSection("BanReasons");
                                        if (section.contains(args[1])) {
                                            reason = section.getString(args[1]);
                                        } else {
                                            staff.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPresetBanReason")));
                                            return true;
                                        }
                                        if (args[2].contains("d") || args[2].contains("h") || args[2].contains("m") || args[2].equalsIgnoreCase("-1")) {
                                            expires = Instant.now();
                                            String durStr = args[2];
                                            if (args[1].equalsIgnoreCase("-1")) {
                                                if (!banner.hasPermission("foxrank.moderation.ban.permanent")) {
                                                    banner.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPermissionMessage")));
                                                    return true;
                                                }
                                                expires = null;
                                            } else if (args[2].contains("d")) {
                                                if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                                    banner.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPermissionMessage")));
                                                    return true;
                                                }
                                                durStr = durStr.replace("d", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 24 * 60 * 60);
                                            } else if (args[2].contains("h")) {
                                                if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                                    banner.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPermissionMessage")));
                                                    return true;
                                                }
                                                durStr = durStr.replace("h", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60 * 60);
                                            } else if (args[2].contains("m")) {
                                                if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                                    banner.sendMessage(ChatColor.translateAlternateColorCodes('§', yml.getString("NoPermissionMessage")));
                                                    return true;
                                                }
                                                durStr = durStr.replace("m", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60);
                                            }
                                        } else {
                                            plugin.sendInvalidArgsMessage(args[2] + " Ex. `30d`, `24h`, `30m`", staff);
                                            return false;

                                        }
                                        if (args[3].equalsIgnoreCase("SILENT")) {
                                            silent = true;
                                        } else if (args[3].equalsIgnoreCase("PUBLIC")) {
                                            silent = false;
                                        } else {
                                            plugin.sendInvalidArgsMessage("<SILENT/PUBLIC>", staff);
                                        }
                                        reason = removeUnderScore(reason);
                                        banID = getBanID(banee);
                                        banOfflinePlayer(staff, banee, reason, expires, reason, banID);

                                    }
                                }
                            } else {
                                plugin.sendInvalidArgsMessage("Player", staff);
                            }
                        }
                    } else {
                        plugin.sendMissingArgsMessage("/ban", "<PLAYER> <REASON> <DURATION> <SILENT/PUBLIC>", staff);
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
            return plugin.dm.getPlayers();
        } else if (args.length == 2) {
            List<String> arguments = new ArrayList<>();
            arguments.addAll(plugin.getConfig().getConfigurationSection("BanReasons").getKeys(false));
            return arguments;
        } else if (args.length == 3) {
            List<String> arguments = new ArrayList<>();
            arguments.addAll(plugin.getConfig().getConfigurationSection("BanDurations").getKeys(false));
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
