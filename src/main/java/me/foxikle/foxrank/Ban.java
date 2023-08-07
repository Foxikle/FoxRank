package me.foxikle.foxrank;

import me.foxikle.foxrank.Data.DataManager;
import org.bukkit.Bukkit;
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


public class Ban implements CommandExecutor, TabCompleter {
    private final FoxRank plugin;
    private final FileConfiguration yml;
    private boolean silent = false;

    public Ban(FoxRank plugin) {
        this.plugin = plugin;
        this.yml = plugin.getConfig();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("ban") || label.equalsIgnoreCase("foxrank:ban")) {
            if (sender instanceof Player banner) {
                    if (args.length >= 4) {
                        String banID;
                        Instant expires;
                        String reason;
                        if (Bukkit.getServer().getPlayer(args[0]) != null) {
                            Player banee = Bukkit.getServer().getPlayer(args[0]);
                            plugin.targetMap.put(banner.getUniqueId(), banee.getUniqueId());
                            Rank baneeRank = plugin.getPlayerData(banee.getUniqueId()).getRank();
                            Rank staffRank = plugin.getPlayerData(banner.getUniqueId()).getRank();
                            if (banee.hasPermission("foxrank.moderation.ban.immune")){
                                banner.sendMessage(plugin.getMessage("CannotBanImmunePlayer", banner));
                                return true;
                            } else if (baneeRank.getPowerlevel() >= staffRank.getPowerlevel()) {
                                banner.sendMessage(plugin.getMessage("CannotBanSuperiorPlayerMessage", banner));
                                return true;
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
                                    plugin.attemptedBanPresetMap.put(banner.getUniqueId(), args[1]);
                                    banner.sendMessage(plugin.getMessage("NoPresetBanReason", banner));
                                    return true;
                                }
                                if (args[2].contains("d") || args[2].contains("h") || args[2].contains("m") || args[2].equalsIgnoreCase("-1")) {
                                    expires = Instant.now();
                                    String durStr = args[2];
                                    if (args[2].equalsIgnoreCase("-1")) {
                                        if (!banner.hasPermission("foxrank.moderation.ban.permanent")) {
                                            banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                            return true;
                                        }

                                        expires = null;
                                    } else if (args[2].contains("d")) {
                                        if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                            banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                            return true;
                                        }
                                        durStr = durStr.replace("d", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 24 * 60 * 60);
                                    } else if (args[2].contains("h")) {
                                        if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                            banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                            return true;
                                        }
                                        durStr = durStr.replace("h", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 60 * 60);
                                    } else if (args[2].contains("m")) {
                                        if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                            banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                            return true;
                                        }
                                        durStr = durStr.replace("m", "");
                                        int durInt = Integer.parseInt(durStr);
                                        expires = Instant.now().plusSeconds((long) durInt * 60);
                                    }
                                } else {
                                    plugin.syntaxMap.put(banner.getUniqueId(), "/ban <player> <preset> <duration (`30d`, `24h`, `30m`)> <SILENT/PUBLIC>");
                                    banner.sendMessage(plugin.getSyntaxMessage(banner));
                                    return false;

                                }
                                if (args[3].equalsIgnoreCase("SILENT")) {
                                    silent = true;
                                } else if (args[3].equalsIgnoreCase("PUBLIC")) {
                                    silent = false;
                                } else {
                                    plugin.syntaxMap.put(banner.getUniqueId(), "/ban <player> <preset> <duration (`30d`, `24h`, `30m`)> <SILENT/PUBLIC>");
                                    banner.sendMessage(plugin.getSyntaxMessage(banner));
                                }
                                reason = removeUnderScore(reason);
                                banID = getBanID(banee);
                                ModerationAction.banPlayer(banner, banee, silent, reason, expires, reason, banID);

                            }
                        } else {
                            if (plugin.players.contains(args[0])) {
                                OfflinePlayer banee = Bukkit.getServer().getOfflinePlayer(DataManager.getUUID(args[0]));
                                if (banee != null) {
                                    Rank baneeRank = plugin.getPlayerData(banee.getUniqueId()).getRank();
                                    Rank staffRank = plugin.getPlayerData(banner.getUniqueId()).getRank();
                                    if (baneeRank.getPermissionNodes().contains("foxrank.moderation.ban.immune")){
                                        banner.sendMessage(plugin.getMessage("CannotBanImmunePlayer", banner));
                                        return true;
                                    } else if (baneeRank.getPowerlevel() >= staffRank.getPowerlevel()) {
                                        banner.sendMessage(plugin.getMessage("CannotBanSuperiorPlayerMessage", banner));
                                        return true;
                                    } else {
                                        ConfigurationSection section = yml.getConfigurationSection("BanReasons");
                                        if (section.contains(args[1])) {
                                            reason = section.getString(args[1]);
                                        } else {
                                            banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                            return true;
                                        }
                                        if (args[2].contains("d") || args[2].contains("h") || args[2].contains("m") || args[2].equalsIgnoreCase("-1")) {
                                            expires = Instant.now();
                                            String durStr = args[2];
                                            if (args[1].equalsIgnoreCase("-1")) {
                                                if (!banner.hasPermission("foxrank.moderation.ban.permanent")) {
                                                    banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                                    return true;
                                                }
                                                expires = null;
                                            } else if (args[2].contains("d")) {
                                                if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                                    banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                                    return true;
                                                }
                                                durStr = durStr.replace("d", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 24 * 60 * 60);
                                            } else if (args[2].contains("h")) {
                                                if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                                    banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                                    return true;
                                                }
                                                durStr = durStr.replace("h", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60 * 60);
                                            } else if (args[2].contains("m")) {
                                                if (!banner.hasPermission("foxrank.moderation.ban.temporary")) {
                                                    banner.sendMessage(plugin.getMessage("NoPermissionMessage", banner));
                                                    return true;
                                                }
                                                durStr = durStr.replace("m", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60);
                                            }
                                        } else {
                                            plugin.syntaxMap.put(banner.getUniqueId(), "/ban <player> <preset> <duration (`30d`, `24h`, `30m`)> <SILENT/PUBLIC>");
                                            banner.sendMessage(plugin.getSyntaxMessage(banner));
                                            return false;

                                        }
                                        if (args[3].equalsIgnoreCase("SILENT")) {
                                            silent = true;
                                        } else if (args[3].equalsIgnoreCase("PUBLIC")) {
                                            silent = false;
                                        } else {
                                            plugin.syntaxMap.put(banner.getUniqueId(), "/ban <player> <preset> <duration (`30d`, `24h`, `30m`)> <SILENT/PUBLIC>");
                                            banner.sendMessage(plugin.getSyntaxMessage(banner));
                                        }
                                        reason = removeUnderScore(reason);
                                        banID = getBanID(banee);
                                        ModerationAction.banOfflinePlayer(banner, banee, reason, expires, reason, banID);

                                    }
                                }
                            } else {
                                plugin.syntaxMap.put(banner.getUniqueId(), "/ban <player> <preset> <duration (`30d`, `24h`, `30m`)> <SILENT/PUBLIC>");
                                banner.sendMessage(plugin.getSyntaxMessage(banner));
                            }
                        }
                    } else {
                        plugin.syntaxMap.put(banner.getUniqueId(), "/ban <player> <preset> <duration (`30d`, `24h`, `30m`)> <SILENT/PUBLIC>");
                        banner.sendMessage(plugin.getSyntaxMessage(banner));
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
            return plugin.players;
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
