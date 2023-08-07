package me.foxikle.foxrank;

import me.foxikle.foxrank.Data.DataManager;
import me.foxikle.foxrank.events.ModerationAction;
import me.foxikle.foxrank.events.ModerationActionEvent;
import org.bukkit.Bukkit;
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

public class Mute implements CommandExecutor, TabCompleter {

    private final FoxRank plugin;

    public Mute(FoxRank plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("mute")) {
            Instant expires;
            String reason;
            if (!plugin.getConfig().getBoolean("DisableMute")) {
                if (sender instanceof Player player) {
                    if (args.length >= 2) {
                        if (Bukkit.getPlayerExact(args[0]) != null) {
                            Player mutee = Bukkit.getPlayerExact(args[0]);
                            plugin.targetMap.put(player.getUniqueId(), mutee.getUniqueId());
                            Rank muteeRank = plugin.getPlayerData(mutee.getUniqueId()).getRank();
                            Rank playerRank = plugin.getPlayerData(player.getUniqueId()).getRank();
                            if (muteeRank.getPowerlevel() >= playerRank.getPowerlevel()) {
                                player.sendMessage(plugin.getMessage("MutePlayerWithHigherRankMessage", player));
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
                                                me.foxikle.foxrank.ModerationAction.mutePlayer(mutee, expires, reason, player);
                                                plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(player, mutee, plugin.getPlayerData(mutee.getUniqueId()).getRank(), plugin.getPlayerData(player.getUniqueId()).getRank(), ModerationAction.MUTE, reason, expires, null));
                                            } else {
                                                plugin.syntaxMap.put(player.getUniqueId(), "/mute <player> <duration (`1d`, `6h`, `30m`)> [reason]");
                                                player.sendMessage(plugin.getSyntaxMessage(player));
                                                return false;
                                            }
                                        }
                                    } else {
                                        player.sendMessage(plugin.getMessage("MuteCommandPlayerAlreadyMuted", mutee));
                                    }
                            }
                        } else if (Bukkit.getOfflinePlayer(DataManager.getUUID(args[0])) != null) {
                            OfflinePlayer mutee = Bukkit.getOfflinePlayer(DataManager.getUUID(args[0]));

                            Rank muteeRank = plugin.getPlayerData(mutee.getUniqueId()).getRank();
                            Rank playerRank = plugin.getPlayerData(player.getUniqueId()).getRank();
                            if (muteeRank.getPowerlevel() >= playerRank.getPowerlevel()) {
                                player.sendMessage(plugin.getMessage("MutePlayerWithHigherRankMessage", player)); //todo: confirm the papi argument in player
                            } else {

                                    if (!plugin.getPlayerData(mutee.getUniqueId()).isMuted()) {
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
                                            plugin.syntaxMap.put(player.getUniqueId(), "/mute <player> <duration (`1d`, `6h`, `30m`)> [reason]");
                                            player.sendMessage(plugin.getSyntaxMessage(player));
                                            return false;
                                        }
                                        if (args.length >= 3) {
                                            ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
                                            list.remove(1);
                                            list.remove(0);
                                            reason = String.join(" ", list);
                                            me.foxikle.foxrank.ModerationAction.muteOfflinePlayer(mutee, expires, reason, player);
                                            player.sendMessage(plugin.getMessage("MuteSenderMessage", mutee));
                                            plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(((Player) sender).getPlayer(), mutee.getPlayer(), muteeRank, playerRank, ModerationAction.MUTE, reason, expires, null));
                                        }
                                    } else {
                                        player.sendMessage(plugin.getMessage("MuteCommandPlayerAlreadyMuted", mutee));
                                    }
                            }
                        } else {
                            plugin.syntaxMap.put(player.getUniqueId(), "/mute <player> <duration> [reason]");
                            player.sendMessage(plugin.getSyntaxMessage(player));
                        }
                    } else {
                        plugin.syntaxMap.put(player.getUniqueId(), "/mute <player> <duration> [reason]");
                        player.sendMessage(plugin.getSyntaxMessage(player));
                    }
                }
            } else {
                sender.sendMessage(plugin.getMessage("CommandDisabledMessage", (Player) sender));
            }
        } else if (label.equalsIgnoreCase("me")) {
            if (sender instanceof Player player) {
                return plugin.getPlayerData(player.getUniqueId()).isMuted();
            }
            return false;
        } else if (label.equalsIgnoreCase("say")) {
            if (sender instanceof Player player) {
                return plugin.getPlayerData(player.getUniqueId()).isMuted();
            }
            return false;
        }
        if (label.equalsIgnoreCase("immuted")) {
            if (!plugin.getConfig().getBoolean("DisableImMuted")) {
                if (sender instanceof Player player) {
                    if (!plugin.getPlayerData(player.getUniqueId()).isMuted()) {
                        player.sendMessage(plugin.getMessage("ImmutedCommandNotMutedMessage", player));
                    } else if (args.length >= 1) {
                        Player receiver = Bukkit.getPlayerExact(args[0]);
                        plugin.targetMap.put(player.getUniqueId(), receiver.getUniqueId());
                        if (plugin.playerNames.contains(args[0])) {

                            String to = plugin.getMessage("IAmMutedCommandMessageToMuted", player);
                            String from = plugin.getMessage("IAmMutedCommandMessageFromMuted", player);

                            if (plugin.bungeecord) {
                                plugin.getPluginChannelListener().sendMessage(player, args[0], from);
                            } else {
                                receiver.sendMessage(from);
                            }
                            player.sendMessage(to);
                        }
                    } else {
                        plugin.syntaxMap.put(player.getUniqueId(), "/immuted <player>");
                        player.sendMessage(plugin.getSyntaxMessage(player));
                    }
                    return true;
                }
            } else {
                sender.sendMessage(plugin.getMessage("CommandDisabledMessage", (Player) sender));
            }
        } else if (label.equalsIgnoreCase("unmute")) {
            if (!plugin.getConfig().getBoolean("DisableUnmute")) {
                if (sender instanceof Player player) {
                    if (player.hasPermission("foxrank.moderation.mute.unmute")) {
                        if (args.length >= 1) {
                            if (Bukkit.getPlayerExact(args[0]) != null) {
                                Player receiver = Bukkit.getPlayerExact(args[0]);
                                plugin.targetMap.put(player.getUniqueId(), receiver.getUniqueId());
                                Rank muteeRank = plugin.getPlayerData(receiver.getUniqueId()).getRank();
                                Rank playerRank = plugin.getPlayerData(player.getUniqueId()).getRank();
                                if (!plugin.getPlayerData(receiver.getUniqueId()).isMuted()) {
                                    player.sendMessage(plugin.getMessage("UnmuteCommandPlayerNotMuted", player));
                                } else {
                                    me.foxikle.foxrank.ModerationAction.unmutePlayer(receiver, player);
                                    plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(player, receiver.getPlayer(), muteeRank, playerRank, ModerationAction.UNMUTE, null, null, null));
                                    player.sendMessage(plugin.getMessage("UnmuteSenderMessage", player));
                                }
                            } else if (Bukkit.getOfflinePlayer(DataManager.getUUID(args[0])) != null) {
                                OfflinePlayer receiver = Bukkit.getOfflinePlayer(DataManager.getUUID(args[0]));
                                Rank muteeRank = plugin.getPlayerData(receiver.getUniqueId()).getRank();
                                Rank playerRank = plugin.getPlayerData(player.getUniqueId()).getRank();
                                if (!plugin.getPlayerData(receiver.getUniqueId()).isMuted()) {
                                    player.sendMessage(plugin.getMessage("UnmuteCommandPlayerNotMuted", player));
                                } else {
                                    me.foxikle.foxrank.ModerationAction.unmuteOfflinePlayer(receiver, player);
                                    plugin.getServer().getPluginManager().callEvent(new ModerationActionEvent(player, receiver.getPlayer(), muteeRank, playerRank, ModerationAction.UNMUTE, null, null, null));
                                    player.sendMessage(plugin.getMessage("UnmuteSenderMessage", player));
                                }
                            }
                        } else {
                            plugin.syntaxMap.put(player.getUniqueId(), "/unmute <player>");
                            player.sendMessage(plugin.getSyntaxMessage(player));
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
                return plugin.playerNames;
            }
        } else if (command.getLabel().equalsIgnoreCase("unmute")) {
            if (args.length == 1) {
                return plugin.playerNames;
            }
        }
        return new ArrayList<>();
    }
}
