package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

import static org.bukkit.ChatColor.*;

public class Mute implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("mute")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableMute")) {
                if (sender instanceof Player player) {
                    if (args.length >= 2) {
                        if (Bukkit.getPlayerExact(args[0]) != null) {
                            Player mutee = Bukkit.getPlayerExact(args[0]);

                            RankedPlayer rp = new RankedPlayer(player);
                            RankedPlayer mrp = new RankedPlayer(mutee);
                            if (mrp.getPowerLevel() >= rp.getPowerLevel()) {
                                rp.sendMessage(RED + "You cannot mute a player with a higher or equal power level.");
                            } else {
                                if (rp.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("MutePermissions")) {
                                    File file = new File("plugins/FoxRank/PlayerData/" + mutee.getUniqueId() + ".yml");
                                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                                    if (yml.getString("isMuted").equalsIgnoreCase("false")) {
                                        yml.set("isMuted", true);
                                        try {
                                            yml.save(file);
                                        } catch (IOException error) {
                                            Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + player.getName() + "'s Muted state.");
                                        }
                                        //Duration d = Duration.parse();
                                        if (args[1].contains("d") || args[1].contains("h") || args[1].contains("m")) {
                                            String durStr = args[1];

                                            if (args[1].contains("d")) {
                                                durStr = durStr.replace("d", "");

                                                int durInt = Integer.parseInt(durStr);
                                                Instant expires = new Date().toInstant().plusSeconds((long) durInt * 24 * 60 * 60);
                                                yml.set("MuteDuration", expires.toString());
                                                try {
                                                    yml.save(file);
                                                } catch (IOException error) {
                                                    Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + player.getName() + "'s Muted state.");
                                                }
                                            } else if (args[1].contains("h")) {
                                                durStr = durStr.replace("h", "");
                                                int durInt = Integer.parseInt(durStr);
                                                Instant expires = new Date().toInstant().plusSeconds((long) durInt * 60 * 60);
                                                yml.set("MuteDuration", expires.toString());
                                                try {
                                                    yml.save(file);
                                                } catch (IOException error) {
                                                    Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + player.getName() + "'s Muted state.");
                                                }
                                            } else if (args[1].contains("m")) {
                                                durStr = durStr.replace("m", "");
                                                int durInt = Integer.parseInt(durStr);
                                                Instant expires = new Date().toInstant().plusSeconds((long) durInt * 60);
                                                yml.set("MuteDuration", expires.toString());
                                                try {
                                                    yml.save(file);
                                                } catch (IOException error) {
                                                    Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + player.getName() + "'s Muted state.");
                                                }
                                            }
                                        } else {
                                            rp.sendMessage(RED + args[1] + " is not a valid duration. Ex. `1d`, `6h`, `30m`");
                                        }
                                        if (args.length >= 3) {
                                            ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
                                            list.remove(1); //removes the player's name from the reason
                                            list.remove(0); // removes the duration from the reason string
                                            String reason = String.join(" ", list);
                                            Bukkit.broadcastMessage(reason);
                                            yml.set("MuteReason", reason);
                                            try {
                                                yml.save(file);
                                            } catch (IOException error) {
                                                Bukkit.getLogger().log(Level.SEVERE, "ERROR could not save " + player.getName() + "'s Muted state.");
                                            }
                                            FoxRank.getInstance().addMuteLogEntry(rp.getUniqueId() + " Was muted at " + Instant.now() + "for " + reason + ", they will be muted until " + yml.get("MuteDuration"));
                                        }
                                    } else {
                                        rp.sendMessage(RED + "This player is already muted!");
                                    }
                                } else {
                                    player.sendMessage(RED + "You must have a power level of " + FoxRank.getInstance().getConfig().getInt("MutePermissions") + " or higher to use this command.");
                                    player.sendMessage(RED + "Please contact a server administrator is you think this is a mistake.");
                                }
                            }
                        } else {
                            sender.sendMessage(RED + "You need to specify a valid player!");
                        }
                    } else {
                        sender.sendMessage(RED + "Missing arguments! /mute <player> <duration> [reason]");
                    }
                } else {
                    sender.sendMessage(RED + "You cannot do this!");
                }
            } else {
                sender.sendMessage(RED + "This command is currently disabled.");
            }
        } else if (label.equalsIgnoreCase("me")) {
            if (sender instanceof Player player) {
                return FoxRank.getInstance().isMuted(player);
            }
            return false;
        } else if (label.equalsIgnoreCase("say")) {
            if (sender instanceof Player player) {
                return FoxRank.getInstance().isMuted(player);
            }
            return false;
        }
        if (label.equalsIgnoreCase("immuted")) {
            if (sender instanceof Player player) {
                if (!FoxRank.getInstance().isMuted(player)) {
                    player.sendMessage(RED + "You are not currently muted.");
                } else if (args.length >= 1) {
                    if (Bukkit.getPlayerExact(args[0]) != null) {
                        Player receiver = Bukkit.getPlayerExact(args[0]);
                        receiver.sendMessage(LIGHT_PURPLE + "from " + FoxRank.getRank(player).getPrefix() + player.getName() + RESET + ": " + YELLOW + "Hey! I'm unable to chat right now because I'm currently muted.");
                        player.sendMessage(LIGHT_PURPLE + "to " + FoxRank.getRank(receiver).getPrefix() + receiver.getName() + RESET + ": " + YELLOW + "Hey! I'm unable to chat right now because I'm currently muted.");
                    }
                } else {
                    player.sendMessage(RED + "Missing arguments! /immuted <player>");
                }
                return true;
            } else {
                sender.sendMessage(RED + "You cannot do this!");
            }
        } else if (label.equalsIgnoreCase("unmute")) {
            if (sender instanceof Player player) {
                RankedPlayer rp = new RankedPlayer(player);
                if(rp.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("UnmutePermissions")) {
                    if (args.length >= 1) {
                        if (Bukkit.getPlayerExact(args[0]) != null) {
                            Player receiver = Bukkit.getPlayerExact(args[0]);
                            if (!FoxRank.getInstance().isMuted(receiver)) {
                                player.sendMessage(RED + receiver.getName() + " is not currently muted.");
                            } else {
                                FoxRank.getInstance().unmutePlayer(new RankedPlayer(receiver));
                            }
                        }
                    } else {
                        player.sendMessage(RED + "Missing arguments! /unmute <player>");
                    }
                    return true;
                } else {
                    player.sendMessage(RED + "You do not have the suitable permissions to run this command.");
                }
            } else {
                sender.sendMessage(RED + "You cannot do this!");
            }
        }
        return false;
    }
}
