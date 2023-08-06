package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RankCommand implements CommandExecutor, TabExecutor {


    private final FoxRank plugin;

    public RankCommand(FoxRank plugin) {
        this.plugin = plugin;
    }

    /**
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return if the command was handled
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.performCommand("rank help");
            } else {
                if (args.length == 1) { // simple commands
                    String arg = args[0].toLowerCase();
                    switch (arg) {
                        case "help" -> {
                            player.sendMessage("I'll do this later");
                        }
                        case "list" -> {
                            plugin.ranks.keySet().forEach(player::sendMessage);
                        }
                        case "reload" -> {


                            for (Player p : Bukkit.getOnlinePlayers()) {
                                plugin.clearPermissions(p.getUniqueId());
                            }

                            plugin.teamMappings.values().forEach(Team::unregister);
                            plugin.teamMappings.clear();
                            plugin.ranks.clear();
                            plugin.clearPlayerData();
                            plugin.playerRanks.clear();
                            plugin.rankTeams.clear();
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setupRanks());
                            plugin.setupTeams();


                            Bukkit.getOnlinePlayers().forEach(p -> Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                                plugin.loadRank(p);
                                if (plugin.getPlayerData(p.getUniqueId()).isNicked()) {
                                    Nick.changeName(plugin.getPlayerData(p.getUniqueId()).getNickname(), p);
                                    Bukkit.getScheduler().runTask(plugin, () -> Nick.loadSkin(p));
                                    plugin.setTeam(p, plugin.getPlayerData(p.getUniqueId()).getNicknameRank().getId());
                                }
                            }, 10));
                        }
                    }
                } else { // `/rank modify ADMIN prefix <prefix>`
                    String subCommand = args[0].toLowerCase();
                    if (subCommand.equalsIgnoreCase("modify")) {
                        if (!player.hasPermission("foxrank.ranks.modify")) {
                            player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                            return true;
                        }
                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        if (args.length >= 4) {
                            Rank rank = Rank.ofStrict(args[1]);
                            if (rank == null) { // todo: syntax
                                player.sendMessage(ChatColor.RED + "Could not find the rank '" + args[1] + "'!");
                                return true;
                            }
                            ConfigurationSection section = yml.getConfigurationSection("Ranks").getConfigurationSection(rank.getId());
                            if (section == null) {
                                throw new NullPointerException("The configuration secton for rank '" + rank.getId() + "' is null. Please report the following stacktrace to Foxikle.");
                            }
                            String action = args[2].toLowerCase();
                            String input = args[3];
                            switch (action) {
                                case "prefix", "prfx" -> section.set("prefix", input);
                                case "powerlevel", "pwrlvl" -> section.set("powerLevel", input);
                                case "color", "clr" -> section.set("color", input);
                                case "textcolor", "txtclr" -> section.set("ChatTextColor", input);
                                case "nicknamable", "ncknmbl" -> section.set("nicknamable", input);
                                case "perm", "permission", "prmssn" -> {
                                    if (args.length == 4 && input.equalsIgnoreCase("list")) {
                                        // todo print out nodes
                                    } else if (args.length >= 5) {
                                        String node = args[4];
                                        if (input.equalsIgnoreCase("add")) {
                                            rank.addPermissionNode(node);
                                        } else if (input.equalsIgnoreCase("remove")) {
                                            rank.removePermissionNode(node);
                                        }
                                    } else {
                                        plugin.syntaxMap.put(player.getUniqueId(), "/rank modify <Rank> permission <add/remove> <permission.node>");
                                        player.sendMessage(plugin.getSyntaxMessage(player));
                                    }
                                }
                            }
                            try {
                                yml.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            plugin.syntaxMap.put(player.getUniqueId(), "/rank modify <RankID> <prefix/powerlevel/color/textcolor/nicknameable/permission>");
                            player.sendMessage(plugin.getSyntaxMessage(player));
                        }
                    } else if (subCommand.equalsIgnoreCase("create")) { // `/rank create ADMIN `
                        if (!player.hasPermission("foxrank.ranks.create")) {
                            player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                            return true;
                        }
                        Rank rank = new Rank(0, "", args[1], ChatColor.getByChar('f'), ChatColor.getByChar('f'), true, new ArrayList<>());
                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        ConfigurationSection section = yml.getConfigurationSection("Ranks").createSection(args[1]);
                        section.set("prefix", rank.getPrefix());
                        section.set("powerLevel", rank.getPowerlevel());
                        section.set("color", rank.getColor().getChar());
                        section.set("ChatTextColor", rank.getTextColor().getChar());
                        section.set("nicknamable", rank.isNicknameable());
                        section.set("permissions", rank.getPermissionNodes());
                        try {
                            yml.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (subCommand.equalsIgnoreCase("remove")) {
                        if (!player.hasPermission("foxrank.ranks.remove")) {
                            player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                            return true;
                        }
                        plugin.ranks.remove(args[1]);

                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        yml.getConfigurationSection("Ranks").set(args[1], null);

                        //todo: remove team, etc.
                    }
                }
            }
        } else {
            //todo: add console support.
        }
        return false;
    }

    /**
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed
     * @return The list of the arguments to tab complete
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //rank modify ADMIN permission add hello.*
        List<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.add("help");
            strings.add("list");
            strings.add("reload");
            strings.add("modify");
            strings.add("remove");
            strings.add("create");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("modify")) || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("add")) {
            strings.addAll(plugin.ranks.keySet());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("modify")) {
            strings.add("prefix");
            strings.add("powerlevel");
            strings.add("color");
            strings.add("textcolor");
            strings.add("nicknamable");
            strings.add("permission");
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("color") || args[2].equalsIgnoreCase("textcolor")) {
                strings.add(ChatColor.translateAlternateColorCodes('&', "&00"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&11"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&22"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&33"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&44"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&55"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&66"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&77"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&88"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&99"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&aa"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&bb"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&cc"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&dd"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&ee"));
                strings.add(ChatColor.translateAlternateColorCodes('&', "&ff"));
            } else if (args[2].equalsIgnoreCase("nicknamable")) {
                strings.add("true");
                strings.add("false");
            } else if (args[2].equalsIgnoreCase("permission")) {
                strings.add("list");
                strings.add("add");
                strings.add("remove");
            }
        } else if (args.length == 5) {
            Rank rank = Rank.ofStrict(args[2]);
            if (args[3].equalsIgnoreCase("remove")) {
                strings.addAll(rank.getPermissionNodes());
            } else if (args[3].equalsIgnoreCase("add")) {
                Bukkit.getPluginManager().getPermissions().forEach(permission -> strings.add(permission.getName()));
            }
        }
        return strings;
    }
}
