package me.foxikle.foxrank;

import me.foxikle.foxrank.events.ModerationAction;
import me.foxikle.foxrank.events.ModerationActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Mute implements CommandExecutor, TabExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("mute")) {
            Instant expires;
            String reason;
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableMute")) {
                if (sender instanceof Player player) {
                    if (args.length >= 2) {
                        if (Bukkit.getPlayerExact(args[0]) != null) {
                            Player mutee = Bukkit.getPlayerExact(args[0]);
                            RankedPlayer rp = new RankedPlayer(player);
                            RankedPlayer mrp = new RankedPlayer(mutee);
                            if (mrp.getPowerLevel() >= rp.getPowerLevel()) {
                                rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("MutePlayerWithHigherPowerLevelMessage")));
                            } else {
                                if (rp.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("MutePermissions")) {
                                    if (!FoxRank.getInstance().isMuted(mutee.getUniqueId())) {
                                        expires = Instant.now();
                                        if (args[1].contains("d") || args[1].contains("h") || args[1].contains("m")) {
                                            String durStr = args[1];
                                            if (args[1].contains("d")) {
                                                durStr = durStr.replace("d", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = new Date().toInstant().plusSeconds((long) durInt * 24 * 60 * 60);
                                            } else if (args[1].contains("h")) {
                                                durStr = durStr.replace("h", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = new Date().toInstant().plusSeconds((long) durInt * 60 * 60);
                                            } else if (args[1].contains("m")) {
                                                durStr = durStr.replace("m", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = new Date().toInstant().plusSeconds((long) durInt * 60);
                                            }
                                        } else {
                                            FoxRank.getInstance().sendInvalidArgsMessage(args[1] + " Ex. `1d`, `6h`, `30m`", rp);
                                            return false;
                                        }
                                        if (args.length >= 3) {
                                            ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
                                            list.remove(1);
                                            list.remove(0);
                                            reason = String.join(" ", list);
                                            mrp.mutePlayer(rp, expires, reason);
                                            FoxRank.getInstance().getServer().getPluginManager().callEvent(new ModerationActionEvent(((Player) sender).getPlayer(), mrp.getPlayer(), mrp.getRank(), rp.getRank(), ModerationAction.MUTE));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("MuteCommandPlayerAlreadyMuted").replace("$PLAYER", mrp.getName())));
                                    }
                                } else {
                                    FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("MutePermissions"), rp);
                                }
                            }
                        } else if (Bukkit.getOfflinePlayer(FoxRank.getInstance().getUUID(args[0])) != null) {
                            OfflinePlayer mutee = Bukkit.getOfflinePlayer(FoxRank.getInstance().getUUID(args[0]));

                            RankedPlayer rp = new RankedPlayer(player);
                            OfflineRankedPlayer mrp = new OfflineRankedPlayer(mutee);
                            if (mrp.getPowerLevel() >= rp.getPowerLevel()) {
                                rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("MutePlayerWithHigherPowerLevelMessage")));
                            } else {
                                if (rp.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("MutePermissions")) {
                                    if (FoxRank.instance.isMuted(mutee.getUniqueId())) {
                                        expires = Instant.now();
                                        if (args[1].contains("d") || args[1].contains("h") || args[1].contains("m")) {
                                            String durStr = args[1];
                                            if (args[1].contains("d")) {
                                                durStr = durStr.replace("d", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = new Date().toInstant().plusSeconds((long) durInt * 24 * 60 * 60);
                                            } else if (args[1].contains("h")) {
                                                durStr = durStr.replace("h", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = new Date().toInstant().plusSeconds((long) durInt * 60 * 60);
                                            } else if (args[1].contains("m")) {
                                                durStr = durStr.replace("m", "");
                                                int durInt = Integer.parseInt(durStr);
                                                expires = new Date().toInstant().plusSeconds((long) durInt * 60);
                                            }
                                        } else {
                                            FoxRank.getInstance().sendInvalidArgsMessage(args[1] + " Ex. `1d`, `6h`, `30m`", rp);
                                            return false;
                                        }
                                        if (args.length >= 3) {
                                            ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
                                            list.remove(1);
                                            list.remove(0);
                                            reason = String.join(" ", list);
                                            mrp.mutePlayer(rp, expires, reason);
                                            rp.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("muteSenderMessage").replace("$PLAYER", mrp.getName()).replace("$REASON", reason)));
                                            FoxRank.getInstance().getServer().getPluginManager().callEvent(new ModerationActionEvent(((Player) sender).getPlayer(), mrp.getOfflinePlayer().getPlayer(), mrp.getRank(), rp.getRank(), ModerationAction.MUTE));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("MuteCommandPlayerAlreadyMuted").replace("$PLAYER", mrp.getName())));
                                    }
                                } else {
                                    FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("MutePermissions"), rp);
                                }
                            }
                        } else {
                            FoxRank.getInstance().sendInvalidArgsMessage("You must specify a valid player.", new RankedPlayer(((Player) sender).getPlayer()));
                        }
                    } else {
                        FoxRank.getInstance().sendMissingArgsMessage("/mute", "<player> <duration> [reason]", new RankedPlayer((Player) sender));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("CommandDisabledMessage")));
            }
        } else if (label.equalsIgnoreCase("me")) {
            if (sender instanceof Player player) {
                return FoxRank.getInstance().isMuted(player.getUniqueId());
            }
            return false;
        } else if (label.equalsIgnoreCase("say")) {
            if (sender instanceof Player player) {
                return FoxRank.getInstance().isMuted(player.getUniqueId());
            }
            return false;
        }
        if (label.equalsIgnoreCase("immuted")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableImMuted")) {
                if (sender instanceof Player player) {
                    if (!FoxRank.getInstance().isMuted(player.getUniqueId())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("ImmutedCommandNotMutedMessage")));
                    } else if (args.length >= 1) {
                        if (Bukkit.getPlayerExact(args[0]) != null) {
                            Player receiver = Bukkit.getPlayerExact(args[0]);
                            RankedPlayer rrp = new RankedPlayer(receiver);
                            RankedPlayer mrp = new RankedPlayer(player);
                            String to = FoxRank.getInstance().getConfig().getString("IAmMutedCommandMessageToMuted");
                            String from = FoxRank.getInstance().getConfig().getString("IAmMutedCommandMessageFromMuted");

                            if (FoxRank.getInstance().getConfig().getBoolean("DisableRankVisibility")) {
                                to = to.replace("$RECEIVERRANKPREFIX", "");
                                from = from.replace("$MUTEDUSERRANKPREFIX", "");
                            } else {
                                from = from.replace("$MUTEDUSERRANKPREFIX", mrp.getRank().getPrefix());
                                to = to.replace("$RECEIVERRANKPREFIX", rrp.getRank().getPrefix());
                            }

                            to = to.replace("$RECIEVER", rrp.getName());
                            to = to.replace("$MUTEDUSER", mrp.getName());
                            to = ChatColor.translateAlternateColorCodes('§', to);


                            from = from.replace("$RECIEVER", rrp.getName());
                            from = from.replace("$MUTEDUSER", mrp.getName());
                            from = ChatColor.translateAlternateColorCodes('§', from);

                            rrp.sendMessage(from);
                            mrp.sendMessage(to);
                        }
                    } else {
                        FoxRank.getInstance().sendMissingArgsMessage("/immuted", "<player>", new RankedPlayer(player));
                    }
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("CommandDisabledMessage")));
            }
        } else if (label.equalsIgnoreCase("unmute")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableUnmute")) {
                if (sender instanceof Player player) {
                    RankedPlayer rp = new RankedPlayer(player);
                    if (rp.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("UnmutePermissions")) {
                        if (args.length >= 1) {
                            if (Bukkit.getPlayerExact(args[0]) != null) {
                                Player receiver = Bukkit.getPlayerExact(args[0]);
                                if (!FoxRank.getInstance().isMuted(receiver.getUniqueId())) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnmuteCommandPlayerNotMuted").replace("$PLAYER", receiver.getName())));
                                } else {
                                    FoxRank.getInstance().unmutePlayer(new RankedPlayer(receiver), rp);
                                    FoxRank.getInstance().getServer().getPluginManager().callEvent(new ModerationActionEvent(((Player) sender).getPlayer(), receiver.getPlayer(), new OfflineRankedPlayer(receiver).getRank(), rp.getRank(), ModerationAction.UNMUTE));
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnmuteSenderMessage").replace("$PLAYER", receiver.getName())));
                                }
                            } else if (Bukkit.getOfflinePlayer(FoxRank.getInstance().getUUID(args[0])) != null) {
                                OfflinePlayer receiver = Bukkit.getOfflinePlayer(FoxRank.getInstance().getUUID(args[0]));
                                if (!FoxRank.getInstance().isMuted(receiver.getUniqueId())) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnmuteCommandPlayerNotMuted").replace("$PLAYER", receiver.getName())));
                                } else {
                                    FoxRank.getInstance().unmuteOfflinePlayer(receiver, rp);
                                    FoxRank.getInstance().getServer().getPluginManager().callEvent(new ModerationActionEvent(((Player) sender).getPlayer(), receiver.getPlayer(), new OfflineRankedPlayer(receiver).getRank(), rp.getRank(), ModerationAction.UNMUTE));
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("UnmuteSenderMessage").replace("$PLAYER", receiver.getName())));
                                }
                            }
                        } else {
                            FoxRank.getInstance().sendMissingArgsMessage("/unmute", "<player>", rp);
                        }
                        return true;
                    } else {
                        FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("UnmutePermissions"), rp);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("CommandDisabledMessage")));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getLabel().equalsIgnoreCase("mute")) {
            if (args.length == 2) {
                List<String> arguments = new ArrayList<>();
                arguments.add("1d");
                arguments.add("30m");
                arguments.add("1h");
                return arguments;

            } else if (args.length == 1) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    playerNames.add(player.getName());
                }
                return playerNames;
            }
        } else if (command.getLabel().equalsIgnoreCase("unmute")) {
            if (args.length == 1) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (new RankedPlayer(player).isMuted()) playerNames.add(player.getName());
                }
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    if (new OfflineRankedPlayer(player).isMuted()) playerNames.add(player.getName());
                }
                return playerNames;
            }
        }
        return null;
    }
}
