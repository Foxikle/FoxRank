package me.foxikle.foxrank;

import me.foxikle.foxrank.events.ModerationAction;
import me.foxikle.foxrank.events.ModerationActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Mute implements CommandExecutor, TabCompleter {

    private final FoxRank plugin;

    public Mute(FoxRank plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("mute")) {
            Instant expires;
            String reason;
            if (!plugin.getConfig().getBoolean("DisableMute")) {
                if (sender instanceof Player player) {
                    if (args.length >= 2) {
                        if (Bukkit.getPlayerExact(args[0]) != null) {
                            Player mutee = Bukkit.getPlayerExact(args[0]);
                            RankedPlayer rp = new RankedPlayer(player, plugin);
                            RankedPlayer mrp = new RankedPlayer(mutee, plugin);
                            if (mrp.getPowerLevel() >= rp.getPowerLevel()) {
                                rp.sendMessage(plugin.getMessage("MutePlayerWithHigherRankMessage", player)); //todo: confirm the papi argument in player
                            } else {
                                    if (!plugin.getPlayerData(mutee.getUniqueId()).isMuted()) {
                                        if (args[1].contains("d") || args[1].contains("h") || args[1].contains("m") || args[1].equalsIgnoreCase("-1")) {
                                            expires = Instant.now();
                                            String durStr = args[2];
                                            if (args[1].equalsIgnoreCase("-1")) {
                                                if (!player.hasPermission("foxrank.moderation.mute.permanent")) {
                                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                    return true;
                                                }

                                                expires = null;
                                            } else if (args[1].contains("d")) {
                                                if (!player.hasPermission("foxrank.moderation.mute.temporary")) {
                                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                    return true;
                                                }
                                                durStr = durStr.replace("d", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 24 * 60 * 60);
                                            } else if (args[1].contains("h")) {
                                                if (!player.hasPermission("foxrank.moderation.mute.temporary")) {
                                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                    return true;
                                                }
                                                durStr = durStr.replace("h", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60 * 60);
                                            } else if (args[1].contains("m")) {
                                                if (!player.hasPermission("foxrank.moderation.mute.temporary")) {
                                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                    return true;
                                                }
                                                durStr = durStr.replace("m", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60);
                                            }
                                            if (args.length >= 3) {
                                                ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
                                                list.remove(1);
                                                list.remove(0);
                                                reason = String.join(" ", list);
                                                me.foxikle.foxrank.ModerationAction.mutePlayer(mrp, expires, reason, rp);
                                                plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(((Player) sender).getPlayer(), mrp.getPlayer(), mrp.getRank(), rp.getRank(), ModerationAction.MUTE, reason, expires, null));
                                            } else {
                                                plugin.sendInvalidArgsMessage(args[1] + " Ex. `1d`, `6h`, `30m`", rp);
                                                return false;
                                            }
                                        }
                                    } else {
                                        player.sendMessage(plugin.getMessage("MuteCommandPlayerAlreadyMuted", mutee));
                                    }
                            }
                        } else if (Bukkit.getOfflinePlayer(plugin.dm.getUUID(args[0])) != null) {
                            OfflinePlayer mutee = Bukkit.getOfflinePlayer(plugin.dm.getUUID(args[0]));

                            RankedPlayer rp = new RankedPlayer(player, plugin);
                            OfflineRankedPlayer mrp = new OfflineRankedPlayer(mutee);
                            if (mrp.getPowerLevel() >= rp.getPowerLevel()) {
                                rp.sendMessage(plugin.getMessage("MutePlayerWithHigherRankMessage", mutee));
                            } else {

                                    if (!plugin.dm.isMuted(mutee.getUniqueId())) {
                                        expires = Instant.now();
                                        if (args[1].contains("d") || args[1].contains("h") || args[1].contains("m") || args[1].equalsIgnoreCase("-1")) {
                                            expires = Instant.now();
                                            String durStr = args[2];
                                            if (args[1].equalsIgnoreCase("-1")) {
                                                if (!player.hasPermission("foxrank.moderation.mute.permanent")) {
                                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                    return true;
                                                }

                                                expires = null;
                                            } else if (args[1].contains("d")) {
                                                if (!player.hasPermission("foxrank.moderation.mute.temporary")) {
                                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                    return true;
                                                }
                                                durStr = durStr.replace("d", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 24 * 60 * 60);
                                            } else if (args[1].contains("h")) {
                                                if (!player.hasPermission("foxrank.moderation.mute.temporary")) {
                                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                    return true;
                                                }
                                                durStr = durStr.replace("h", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60 * 60);
                                            } else if (args[1].contains("m")) {
                                                if (!player.hasPermission("foxrank.moderation.mute.temporary")) {
                                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                    return true;
                                                }
                                                durStr = durStr.replace("m", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = Instant.now().plusSeconds((long) durInt * 60);
                                            }
                                        } else {
                                            plugin.sendInvalidArgsMessage(args[1] + " Ex. `1d`, `6h`, `30m`", rp);
                                            return false;
                                        }
                                        if (args.length >= 3) {
                                            ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
                                            list.remove(1);
                                            list.remove(0);
                                            reason = String.join(" ", list);
                                            me.foxikle.foxrank.ModerationAction.muteOfflinePlayer(mrp, expires, reason, rp);
                                            rp.sendMessage(plugin.getMessage("MuteSenderMessage", mutee));
                                            plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(((Player) sender).getPlayer(), mrp.getOfflinePlayer().getPlayer(), mrp.getRank(), rp.getRank(), ModerationAction.MUTE, reason, expires, null));
                                        }
                                    } else {
                                        player.sendMessage(plugin.getMessage("MuteCommandPlayerAlreadyMuted", mutee));
                                    }
                            }
                        } else {
                            plugin.sendInvalidArgsMessage("Player.", new RankedPlayer(((Player) sender).getPlayer(), plugin));
                        }
                    } else {
                        plugin.sendMissingArgsMessage("/mute", "<player> <duration> [reason]", new RankedPlayer((Player) sender, plugin));
                    }
                }
            } else {
                sender.sendMessage(plugin.getMessage("CommandDisabledMessage", (Player) sender));
            }
        } else if (label.equalsIgnoreCase("me")) {
            if (sender instanceof Player player) {
                return plugin.dm.isMuted(player.getUniqueId());
            }
            return false;
        } else if (label.equalsIgnoreCase("say")) {
            if (sender instanceof Player player) {
                return plugin.dm.isMuted(player.getUniqueId());
            }
            return false;
        }
        if (label.equalsIgnoreCase("immuted")) {
            if (!plugin.getConfig().getBoolean("DisableImMuted")) {
                if (sender instanceof Player player) {
                    if (!plugin.dm.isMuted(player.getUniqueId())) {
                        player.sendMessage(plugin.getMessage("ImmutedCommandNotMutedMessage", player));
                    } else if (args.length >= 1) {
                        if (plugin.dm.getPlayerNames((Player) sender).contains(args[0])) {

                            String to = plugin.getMessage("IAmMutedCommandMessageToMuted", player);
                            String from = plugin.getMessage("IAmMutedCommandMessageFromMuted", player);

                            if (plugin.bungeecord) {
                                plugin.getPluginChannelListener().sendMessage(player, args[0], from);
                            } else {
                                Player receiver = Bukkit.getPlayerExact(args[0]);
                                receiver.sendMessage(from);
                            }
                            player.sendMessage(to);
                        }
                    } else {
                        plugin.sendMissingArgsMessage("/immuted", "<player>", new RankedPlayer(player, plugin));
                    }
                    return true;
                }
            } else {
                sender.sendMessage(plugin.getMessage("CommandDisabledMessage", (Player) sender));
            }
        } else if (label.equalsIgnoreCase("unmute")) {
            if (!plugin.getConfig().getBoolean("DisableUnmute")) {
                if (sender instanceof Player player) {
                    RankedPlayer rp = new RankedPlayer(player, plugin);
                    if (player.hasPermission("foxrank.moderation.mute.unmute")) {
                        if (args.length >= 1) {
                            if (Bukkit.getPlayerExact(args[0]) != null) {
                                Player receiver = Bukkit.getPlayerExact(args[0]);
                                if (!plugin.dm.isMuted(receiver.getUniqueId())) {
                                    player.sendMessage(plugin.getMessage("UnmuteCommandPlayerNotMuted", player));
                                } else {
                                    me.foxikle.foxrank.ModerationAction.unmutePlayer(new RankedPlayer(receiver, plugin), rp);
                                    plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(player, receiver.getPlayer(), new OfflineRankedPlayer(receiver).getRank(), rp.getRank(), ModerationAction.UNMUTE, null, null, null));
                                    player.sendMessage(plugin.getMessage("UnmuteSenderMessage", player));
                                }
                            } else if (Bukkit.getOfflinePlayer(plugin.dm.getUUID(args[0])) != null) {
                                OfflinePlayer receiver = Bukkit.getOfflinePlayer(plugin.dm.getUUID(args[0]));
                                if (!plugin.dm.isMuted(receiver.getUniqueId())) {
                                    player.sendMessage(plugin.getMessage("UnmuteCommandPlayerNotMuted", player));
                                } else {
                                    me.foxikle.foxrank.ModerationAction.unmuteOfflinePlayer(receiver, rp);
                                    plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(player, receiver.getPlayer(), new OfflineRankedPlayer(receiver).getRank(), rp.getRank(), ModerationAction.UNMUTE, null, null, null));
                                    player.sendMessage(plugin.getMessage("UnmuteSenderMessage", player));
                                }
                            }
                        } else {
                            plugin.sendMissingArgsMessage("/unmute", "<player>", rp);
                        }
                        return true;
                    } else {
                        player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                    }
                }
            } else {
                sender.sendMessage(plugin.getMessage("CommandDisabledMessage", (Player) sender));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        if (command.getLabel().equalsIgnoreCase("mute")) {
            if (args.length == 2) {
                List<String> arguments = new ArrayList<>();
                arguments.add("1d");
                arguments.add("30m");
                arguments.add("1h");
                return arguments;

            } else if (args.length == 1) {
                List<String> playerNames = new ArrayList<>(plugin.dm.getPlayerNames((Player) sender));
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    playerNames.add(player.getName());
                }
                return playerNames;
            }
        } else if (command.getLabel().equalsIgnoreCase("unmute")) {
            if (args.length == 1) {
                List<String> playerNames = new ArrayList<>();
                List<UUID> uuids = plugin.dm.getUUIDs();
                for (UUID uuid : uuids) {
                    if (plugin.dm.isMuted(uuid)) {
                        playerNames.add(plugin.getTrueName(uuid));
                    }
                }
                return playerNames;
            }
        }
        return new ArrayList<>();
    }
}
