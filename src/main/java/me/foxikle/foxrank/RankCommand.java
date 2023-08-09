package me.foxikle.foxrank;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
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

public class RankCommand implements TabExecutor {


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
                return true;
            } else {
                if (args.length == 1) { // simple commands
                    String arg = args[0].toLowerCase();
                    switch (arg) {
                        case "help" -> {
                                if(!player.hasPermission("foxrank.commands.help")){
                                    player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                    return true;
                                }
                                player.sendMessage( """
                            §2§m                       §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                   \s
                            §r                                 §r§6By Foxikle
   
                            """);
                                BaseComponent[] space   = new ComponentBuilder(" : ").color(ChatColor.WHITE).create();
                                ComponentBuilder help   = new ComponentBuilder("\n  -  /rank help").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Displays this message").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Displays this message").color(ChatColor.AQUA).create());
                                ComponentBuilder manage = new ComponentBuilder("\n  -  /rank list").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Lists the current ranks").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Lists the current ranks").color(ChatColor.AQUA).create());
                                ComponentBuilder create = new ComponentBuilder("\n  -  /rank create <RankID>").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Creates a rank").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Creates a rank").color(ChatColor.AQUA).create());
                                ComponentBuilder delete = new ComponentBuilder("\n  -  /rank remove <RankID>").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Deletes the specified Rank").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Deletes the specified Rank").color(ChatColor.AQUA).create());
                                ComponentBuilder edit   = new ComponentBuilder("\n  -  /rank modify <RankID> [args]").color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank help modify")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to run /rank help modify").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Modifies the rank. Run '/rank help modify' for more info").color(ChatColor.AQUA).create());
                                ComponentBuilder reload = new ComponentBuilder("\n  -  /rank reload").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reloads the plugin.").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Reloads the ranks and plugin.").color(ChatColor.AQUA).create());

                                ComponentBuilder close  = new ComponentBuilder("\n§2§m                                                                                ");
                                help.append(manage.create()).append(create.create()).append(delete.create()).append(edit.create()).append(reload.create()).append(close.create());
                                player.spigot().sendMessage(help.create());
                            return true;
                        }
                        case "list" -> {
                            if(!player.hasPermission("foxrank.commands.list")){
                                player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                return true;
                            }
                            player.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                            BaseComponent[] space   = new ComponentBuilder(" ").color(ChatColor.RESET).bold(false).italic(false).strikethrough(false).underlined(false).create();
                            ComponentBuilder core = new ComponentBuilder();
                            plugin.ranks.keySet().forEach(s -> {
                                Rank rank = plugin.ranks.get(s);
                                ComponentBuilder builder = new ComponentBuilder(rank.getId()).color(rank.getColor().asBungee()).append(space).append("▸").bold(true).append(space).append(rank.getPrefix()).append(space).append("[INFO]\n").color(ChatColor.YELLOW)
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eID: " + rank.getId() + "\nPrefix: " + rank.getPrefix() + "§r§e\nPower Level: " + rank.getPowerlevel() + "\nText Color: " + rank.getTextColor() + "I'm some text!" + "\n§r§eColor: " + rank.getColor() + "I'm more text!" + "\n§r§bClick to view \npermission nodes!").create()))
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank modify " + rank.getId() + " permission list")).append("").reset().event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().create()));
                                core.append(builder.create());
                            });
                            ComponentBuilder close  = new ComponentBuilder("\n§2§m                                                                                ");
                            core.append(close.create());
                            player.spigot().sendMessage(core.create());
                            return true;
                        }
                        case "reload" -> {
                            if(!player.hasPermission("foxrank.commands.reload")){
                                player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                return true;
                            }
                            player.sendMessage(ChatColor.YELLOW + "Reloading FoxRank!");
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                plugin.clearPermissions(p.getUniqueId());
                            }

                            plugin.teamMappings.values().forEach(Team::unregister);
                            plugin.teamMappings.clear();
                            plugin.ranks.clear();
                            plugin.clearPlayerData();
                            plugin.playerRanks.clear();
                            plugin.rankTeams.clear();
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                plugin.getDm().reloadConfig();
                                plugin.getDm().setupRanks();
                                Bukkit.getOnlinePlayers().forEach(p -> Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                                    plugin.setupTeams();
                                    plugin.getDm().cacheUserData(p.getUniqueId());
                                    plugin.loadRank(p);
                                    if (plugin.getPlayerData(p.getUniqueId()).isNicked()) {
                                        Nick.changeName(plugin.getPlayerData(p.getUniqueId()).getNickname(), p);
                                        Bukkit.getScheduler().runTask(plugin, () -> Nick.loadSkin(p));
                                        plugin.setTeam(p, plugin.getPlayerData(p.getUniqueId()).getNicknameRank().getId());
                                    }
                                }, 5));
                            });
                            player.sendMessage(ChatColor.GREEN + "FoxRank sucessfully reloaded!");
                            return true;
                        }
                    }
                } else { // `/rank modify ADMIN prefix <prefix>`
                    String subCommand = args[0].toLowerCase();
                    if(subCommand.equalsIgnoreCase("help")) {
                        if(!player.hasPermission("foxrank.commands.help")){
                            player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                            return true;
                        }
                        String helpSection = args[1].toLowerCase();
                        if (helpSection.equalsIgnoreCase("modify")){
                            if(args.length >= 3) {
                                String arg = args[2].toLowerCase();
                                player.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                                switch (arg) {
                                    case "prefix" -> player.sendMessage(ChatColor.YELLOW + "Modifies the ranks prefix. The syntax is: '/rank modify <RankID> prefix <Prefix>'");
                                    case "color" -> player.sendMessage(ChatColor.YELLOW + "Modifies the ranks color. This is the team color (name tag, tab name color, and chat name color) The syntax is: '/rank modify <RankID> color <Color code; (a-f, 0-9)>'");
                                    case "textcolor" -> player.sendMessage(ChatColor.YELLOW + "Modifies the ranks textcolor. This is the color shown in chat. The syntax is: '/rank modify <RankID> textcolor <Color code; (a-f, 0-9)>'");
                                    case "nicknamable" -> player.sendMessage(ChatColor.YELLOW + "Determines if the rank will appear in the nickname book. The syntax is: '/rank modify <RankID> nicknamable <true/false>'");
                                    case "powerlevel" -> player.sendMessage(ChatColor.YELLOW + "Modifies the ranks internal permission. This determines the order of the tab list and the 'seinority'. Meaning moderators cannot punish people of equal or higher rank. The syntax is: '/rank modify <RankID> powerlevel <powerlevel (Any number)>'");
                                    case "permission" -> {
                                        if(args.length >= 4) {
                                            String subArg = args[3].toLowerCase();
                                            switch (subArg) {
                                                case "list" -> player.sendMessage(ChatColor.YELLOW + "Lists all of the permission nodes attached to a rank. Syntax: '/rank modify <RankID> permission list'");
                                                case "remove" -> player.sendMessage(ChatColor.YELLOW + "Removes the specified permission node attached to a rank. Syntax: '/rank modify <RankID> permission remove <permission.node>'");
                                                case "add" -> player.sendMessage(ChatColor.YELLOW + "Adds the specified permission node attached to a rank. Syntax: '/rank modify <RankID> permission add <permission.node>'");
                                                default -> player.sendMessage(ChatColor.RED + "The sub command '" + args[3] + "' was not found. The available sub commands are: 'list', 'remove', and 'add'." );
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.YELLOW + "Manages permissions for the ranks. The 3 sub commands are: 'list', 'remove', and 'add'.");
                                        }
                                    }
                                    default -> player.sendMessage(ChatColor.RED + "The sub command '" + args[2] + "' was not found. The available sub commands are: 'prefix', 'color', 'textcolor', 'nicknamable', 'powerlevel', and 'permission'." );
                                }
                                player.sendMessage("§2§m                                                                                ");
                            } else {
                                player.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                                player.sendMessage(ChatColor.YELLOW + "The modify sub commad is the center of rank configuration. " +
                                        "There are 6 sub command below modify. To learn more about any of them, run the command '/rank" +
                                        " help modify <sub command>. The 6 sub commands are: 'prefix', 'color', 'textcolor', 'nicknamable'," +
                                        " 'powerlevel', and 'permission'.");
                                player.sendMessage("§2§m                                                                                ");
                            }
                        }
                    } else if (subCommand.equalsIgnoreCase("modify")) {

                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        if (args.length >= 4) {
                            Rank rank = Rank.ofStrict(args[1]);
                            if (rank == null) { // todo: syntax
                                plugin.syntaxMap.put(player.getUniqueId(), "Could not find the rank '" + args[1] + "'!");
                                player.sendMessage(plugin.getSyntaxMessage(player));
                                return true;
                            }
                            ConfigurationSection section = yml.getConfigurationSection("Ranks").getConfigurationSection(rank.getId());
                            if (section == null) {
                                throw new NullPointerException("The configuration secton for rank '" + rank.getId() + "' is null. Please report the following stacktrace to Foxikle.");
                            }
                            String action = args[2].toLowerCase();
                            String input = args[3];
                            switch (action) {
                                case "prefix", "prfx" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.prefix")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    section.set("prefix", input + " ");
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s prefix to '" + ChatColor.translateAlternateColorCodes('&', input) + ChatColor.GREEN + "'. Run /rank reload to propagate changes");
                                }
                                case "powerlevel", "pwrlvl", "pl" -> {
                                    if (!player.hasPermission("foxrank.ranks.powerlevel")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    try {
                                        Integer.parseInt(input);
                                    } catch (NumberFormatException ignored) {
                                        player.sendMessage(ChatColor.RED + "Failed to parse an integer from '" + input + "'");
                                    }
                                    section.set("powerLevel", Integer.parseInt(input));
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s powerlevel to " + Integer.parseInt(input) + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "color", "clr" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.color")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    section.set("color", input);
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s color to be " + ChatColor.getByChar(input.charAt(0)).toString() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "textcolor", "txtclr" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.textcolor")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    section.set("ChatTextColor", input);
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s text color to be " + ChatColor.getByChar(input.charAt(0)).toString() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "nicknamable", "ncknmbl" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.nicknamable")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    section.set("nicknamable", Boolean.parseBoolean(input));
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s nicknamability to be " + Boolean.parseBoolean(input) + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "perm", "permission", "prmssn" -> {
                                    if (args.length == 4 && input.equalsIgnoreCase("list")) {
                                        if (!player.hasPermission("foxrank.ranks.modify.list_permissions")) {
                                            player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                            return true;
                                        }
                                        player.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                                        player.sendMessage(rank.getColor() + rank.getId()  + ChatColor.DARK_AQUA + "'s permission nodes");
                                        if(rank.getPermissionNodes().isEmpty()) {
                                            player.sendMessage(ChatColor.RED + "No nodes!");
                                        } else {
                                            for (String s : rank.getPermissionNodes()) {
                                                player.sendMessage("▸ " + s);
                                            }
                                        }
                                        player.sendMessage("§2§m                                                                                ");
                                    } else if (args.length >= 5) {
                                        String node = args[4];
                                        if (input.equalsIgnoreCase("add")) {
                                            if (!player.hasPermission("foxrank.ranks.modify.add_permission")) {
                                                player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                                return true;
                                            }
                                            rank.addPermissionNode(node);
                                            player.sendMessage(ChatColor.GREEN + "Successfully added the permission node '" + node + "' to " + rank.getColor() + rank.getId() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                        } else if (input.equalsIgnoreCase("remove")) {
                                            if (!player.hasPermission("foxrank.ranks.modify.remove_permission")) {
                                                player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                                return true;
                                            }
                                            rank.removePermissionNode(node);
                                            player.sendMessage(ChatColor.GREEN + "Successfully removed the permission node '" + node + "' to " + rank.getColor() + rank.getId() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
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
                        Rank rank = new Rank(0, "", args[1], org.bukkit.ChatColor.getByChar('f'), org.bukkit.ChatColor.getByChar('f'), true, new ArrayList<>());
                        plugin.ranks.put(args[1], rank);
                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        ConfigurationSection section = yml.getConfigurationSection("Ranks").createSection(args[1]);
                        section.set("prefix", rank.getPrefix());
                        section.set("powerLevel", rank.getPowerlevel());
                        section.set("id", rank.getId());
                        section.set("color", rank.getColor().getChar());
                        section.set("ChatTextColor", rank.getTextColor().getChar());
                        section.set("nicknamable", rank.isNicknameable());
                        section.set("permissions", rank.getPermissionNodes());
                        try {
                            yml.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(ChatColor.GREEN + "Sucessfully created the rank '" + rank.getId() + "'. Run /rank reload to propagate changes.");

                    } else if (subCommand.equalsIgnoreCase("remove")) {
                        if (!player.hasPermission("foxrank.ranks.remove")) {
                            player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                            return true;
                        }
                        Rank rank = plugin.ranks.get(args[1]);
                        plugin.ranks.remove(args[1]);
                        plugin.teamMappings.get(rank.getId()).unregister();

                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        yml.getConfigurationSection("Ranks").set(rank.getId(), null);

                        try {
                            yml.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        player.sendMessage(ChatColor.GREEN + "Sucessfully deleted the rank '" + rank.getId() + "'. Run /rank reload to propagate changes");
                    }
                }
            }
        } else {
            ConsoleCommandSender console = (ConsoleCommandSender) sender;
            if (args.length == 0) {
                Bukkit.dispatchCommand(console, "rank help");
                return true;
            } else {
                if (args.length == 1) { // simple commands
                    String arg = args[0].toLowerCase();
                    switch (arg) {
                        case "help" -> {
                            console.sendMessage( """
                            §2§m                       §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                   \s
                            §r                                 §r§6By Foxikle
   
                            """);
                            BaseComponent[] space   = new ComponentBuilder(" : ").color(ChatColor.WHITE).create();
                            ComponentBuilder help   = new ComponentBuilder("\n  -  /rank help").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Displays this message").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Displays this message").color(ChatColor.AQUA).create());
                            ComponentBuilder manage = new ComponentBuilder("\n  -  /rank list").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Lists the current ranks").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Lists the current ranks").color(ChatColor.AQUA).create());
                            ComponentBuilder create = new ComponentBuilder("\n  -  /rank create <RankID>").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Creates a rank").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Creates a rank").color(ChatColor.AQUA).create());
                            ComponentBuilder delete = new ComponentBuilder("\n  -  /rank remove <RankID>").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Deletes the specified Rank").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Deletes the specified Rank").color(ChatColor.AQUA).create());
                            ComponentBuilder edit   = new ComponentBuilder("\n  -  /rank modify <RankID> [args]").color(ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank help modify")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to run /rank help modify").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Modifies the rank. Run '/rank help modify' for more info").color(ChatColor.AQUA).create());
                            ComponentBuilder reload = new ComponentBuilder("\n  -  /rank reload").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reloads the plugin.").color(ChatColor.AQUA).create())).append(space).append(new ComponentBuilder("Reloads the ranks and plugin.").color(ChatColor.AQUA).create());

                            ComponentBuilder close  = new ComponentBuilder("\n§2§m                                                                                ");
                            help.append(manage.create()).append(create.create()).append(delete.create()).append(edit.create()).append(reload.create()).append(close.create());
                            console.spigot().sendMessage(help.create());
                            return true;
                        }
                        case "list" -> {
                            console.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                            BaseComponent[] space   = new ComponentBuilder(" ").color(ChatColor.RESET).bold(false).italic(false).strikethrough(false).underlined(false).create();
                            ComponentBuilder core = new ComponentBuilder();
                            plugin.ranks.keySet().forEach(s -> {
                                Rank rank = plugin.ranks.get(s);
                                ComponentBuilder builder = new ComponentBuilder(rank.getId()).color(rank.getColor().asBungee()).append(space).append("▸").bold(true).append(space).append(rank.getPrefix()).append(space).append("[INFO]\n").color(ChatColor.YELLOW)
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eID: " + rank.getId() + "\nPrefix: " + rank.getPrefix() + "§r§e\nPower Level: " + rank.getPowerlevel() + "\nText Color: " + rank.getTextColor() + "I'm some text!" + "\n§r§eColor: " + rank.getColor() + "I'm more text!" + "\n§r§bClick to view \npermission nodes!").create()))
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank modify " + rank.getId() + " permission list")).append("").reset().event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().create()));
                                core.append(builder.create());
                            });
                            ComponentBuilder close  = new ComponentBuilder("\n§2§m                                                                                ");
                            core.append(close.create());
                            console.spigot().sendMessage(core.create());
                            return true;
                        }
                        case "reload" -> {
                            console.sendMessage(ChatColor.YELLOW + "Reloading FoxRank!");
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                plugin.clearPermissions(p.getUniqueId());
                            }

                            plugin.teamMappings.values().forEach(Team::unregister);
                            plugin.teamMappings.clear();
                            plugin.ranks.clear();
                            plugin.clearPlayerData();
                            plugin.playerRanks.clear();
                            plugin.rankTeams.clear();
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                plugin.getDm().reloadConfig();
                                plugin.getDm().setupRanks();
                                Bukkit.getOnlinePlayers().forEach(p -> Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                                    plugin.setupTeams();
                                    plugin.getDm().cacheUserData(p.getUniqueId());
                                    plugin.loadRank(p);
                                    if (plugin.getPlayerData(p.getUniqueId()).isNicked()) {
                                        Nick.changeName(plugin.getPlayerData(p.getUniqueId()).getNickname(), p);
                                        Bukkit.getScheduler().runTask(plugin, () -> Nick.loadSkin(p));
                                        plugin.setTeam(p, plugin.getPlayerData(p.getUniqueId()).getNicknameRank().getId());
                                    }
                                }, 5));
                            });
                            console.sendMessage(ChatColor.GREEN + "FoxRank sucessfully reloaded!");
                            return true;
                        }
                    }
                } else { // `/rank modify ADMIN prefix <prefix>`
                    String subCommand = args[0].toLowerCase();
                    if(subCommand.equalsIgnoreCase("help")) {
                        String helpSection = args[1].toLowerCase();
                        if (helpSection.equalsIgnoreCase("modify")){
                            if(args.length >= 3) {
                                String arg = args[2].toLowerCase();
                                console.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                                switch (arg) {
                                    case "prefix" -> console.sendMessage(ChatColor.YELLOW + "Modifies the ranks prefix. The syntax is: '/rank modify <RankID> prefix <Prefix>'");
                                    case "color" -> console.sendMessage(ChatColor.YELLOW + "Modifies the ranks color. This is the team color (name tag, tab name color, and chat name color) The syntax is: '/rank modify <RankID> color <Color code; (a-f, 0-9)>'");
                                    case "textcolor" -> console.sendMessage(ChatColor.YELLOW + "Modifies the ranks textcolor. This is the color shown in chat. The syntax is: '/rank modify <RankID> textcolor <Color code; (a-f, 0-9)>'");
                                    case "nicknamable" -> console.sendMessage(ChatColor.YELLOW + "Determines if the rank will appear in the nickname book. The syntax is: '/rank modify <RankID> nicknamable <true/false>'");
                                    case "powerlevel" -> console.sendMessage(ChatColor.YELLOW + "Modifies the ranks internal permission. This determines the order of the tab list and the 'seinority'. Meaning moderators cannot punish people of equal or higher rank. The syntax is: '/rank modify <RankID> powerlevel <powerlevel (Any number)>'");
                                    case "permission" -> {
                                        if(args.length >= 4) {
                                            String subArg = args[3].toLowerCase();
                                            switch (subArg) {
                                                case "list" -> console.sendMessage(ChatColor.YELLOW + "Lists all of the permission nodes attached to a rank. Syntax: '/rank modify <RankID> permission list'");
                                                case "remove" -> console.sendMessage(ChatColor.YELLOW + "Removes the specified permission node attached to a rank. Syntax: '/rank modify <RankID> permission remove <permission.node>'");
                                                case "add" -> console.sendMessage(ChatColor.YELLOW + "Adds the specified permission node attached to a rank. Syntax: '/rank modify <RankID> permission add <permission.node>'");
                                                default -> console.sendMessage(ChatColor.RED + "The sub command '" + args[3] + "' was not found. The available sub commands are: 'list', 'remove', and 'add'." );
                                            }
                                        } else {
                                            console.sendMessage(ChatColor.YELLOW + "Manages permissions for the ranks. The 3 sub commands are: 'list', 'remove', and 'add'.");
                                        }
                                    }
                                    default -> console.sendMessage(ChatColor.RED + "The sub command '" + args[2] + "' was not found. The available sub commands are: 'prefix', 'color', 'textcolor', 'nicknamable', 'powerlevel', and 'permission'." );
                                }
                                console.sendMessage("§2§m                                                                                ");
                            } else {
                                console.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                                console.sendMessage(ChatColor.YELLOW + "The modify sub commad is the center of rank configuration. " +
                                        "There are 6 sub command below modify. To learn more about any of them, run the command '/rank" +
                                        " help modify <sub command>. The 6 sub commands are: 'prefix', 'color', 'textcolor', 'nicknamable'," +
                                        " 'powerlevel', and 'permission'.");
                                console.sendMessage("§2§m                                                                                ");
                            }
                        }
                    } else if (subCommand.equalsIgnoreCase("modify")) {

                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        if (args.length >= 4) {
                            Rank rank = Rank.ofStrict(args[1]);
                            if (rank == null) { // todo: syntax
                                console.sendMessage("Could not find the rank '" + args[1] + "'!");
                                return true;
                            }
                            ConfigurationSection section = yml.getConfigurationSection("Ranks").getConfigurationSection(rank.getId());
                            if (section == null) {
                                throw new NullPointerException("The configuration secton for rank '" + rank.getId() + "' is null. Please report the following stacktrace to Foxikle.");
                            }
                            String action = args[2].toLowerCase();
                            String input = args[3];
                            switch (action) {
                                case "prefix", "prfx" -> {
                                    section.set("prefix", input + " ");
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s prefix to '" + ChatColor.translateAlternateColorCodes('&', input) + ChatColor.GREEN + "'. Run /rank reload to propagate changes");
                                }
                                case "powerlevel", "pwrlvl", "pl" -> {
                                    try {
                                        Integer.parseInt(input);
                                    } catch (NumberFormatException ignored) {
                                        console.sendMessage(ChatColor.RED + "Failed to parse an integer from '" + input + "'");
                                    }
                                    section.set("powerLevel", Integer.parseInt(input));
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s powerlevel to " + Integer.parseInt(input) + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "color", "clr" -> {
                                    section.set("color", input);
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s color to be " + ChatColor.getByChar(input.charAt(0)).toString() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "textcolor", "txtclr" -> {
                                    section.set("ChatTextColor", input);
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s text color to be " + ChatColor.getByChar(input.charAt(0)).toString() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "nicknamable", "ncknmbl" -> {
                                    section.set("nicknamable", Boolean.parseBoolean(input));
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + rank.getColor() + rank.getId() + ChatColor.GREEN + "'s nicknamability to be " + Boolean.parseBoolean(input) + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "perm", "permission", "prmssn" -> {
                                    if (args.length == 4 && input.equalsIgnoreCase("list")) {
                                        console.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta1§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                                        console.sendMessage(rank.getColor() + rank.getId()  + ChatColor.DARK_AQUA + "'s permission nodes");
                                        if(rank.getPermissionNodes().isEmpty()) {
                                            console.sendMessage(ChatColor.RED + "No nodes!");
                                        } else {
                                            for (String s : rank.getPermissionNodes()) {
                                                console.sendMessage("▸ " + s);
                                            }
                                        }
                                        console.sendMessage("§2§m                                                                                ");
                                    } else if (args.length >= 5) {
                                        String node = args[4];
                                        if (input.equalsIgnoreCase("add")) {
                                            rank.addPermissionNode(node);
                                            console.sendMessage(ChatColor.GREEN + "Successfully added the permission node '" + node + "' to " + rank.getColor() + rank.getId() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                        } else if (input.equalsIgnoreCase("remove")) {
                                            rank.removePermissionNode(node);
                                            console.sendMessage(ChatColor.GREEN + "Successfully removed the permission node '" + node + "' to " + rank.getColor() + rank.getId() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                        }
                                    } else {
                                        console.sendMessage(ChatColor.RED + "rank modify <Rank> permission <add/remove> <permission.node>");
                                    }
                                }
                            }
                            try {
                                yml.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            console.sendMessage(ChatColor.RED + "rank modify <RankID> <prefix/powerlevel/color/textcolor/nicknameable/permission>");
                        }
                    } else if (subCommand.equalsIgnoreCase("create")) { // `/rank create ADMIN `
                        Rank rank = new Rank(0, "", args[1], org.bukkit.ChatColor.getByChar('f'), org.bukkit.ChatColor.getByChar('f'), true, new ArrayList<>());
                        plugin.ranks.put(args[1], rank);
                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        ConfigurationSection section = yml.getConfigurationSection("Ranks").createSection(args[1]);
                        section.set("prefix", rank.getPrefix());
                        section.set("powerLevel", rank.getPowerlevel());
                        section.set("id", rank.getId());
                        section.set("color", rank.getColor().getChar());
                        section.set("ChatTextColor", rank.getTextColor().getChar());
                        section.set("nicknamable", rank.isNicknameable());
                        section.set("permissions", rank.getPermissionNodes());
                        try {
                            yml.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        console.sendMessage(ChatColor.GREEN + "Sucessfully created the rank '" + rank.getId() + "'. Run /rank reload to propagate changes.");

                    } else if (subCommand.equalsIgnoreCase("remove")) {
                        Rank rank = plugin.ranks.get(args[1]);
                        plugin.ranks.remove(args[1]);
                        plugin.teamMappings.get(rank.getId()).unregister();

                        File file = new File("plugins/FoxRank/ranks.yml");
                        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                        yml.getConfigurationSection("Ranks").set(rank.getId(), null);

                        try {
                            yml.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        console.sendMessage(ChatColor.GREEN + "Sucessfully deleted the rank '" + rank.getId() + "'. Run /rank reload to propagate changes");
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside a command block, this will be the player, not
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
        } else if (args.length == 2){
            if(args[0].equalsIgnoreCase("modify") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("add")) {
                strings.addAll(plugin.ranks.keySet());
            } else if (args[0].equalsIgnoreCase("help")) {
                strings.add("modify");
            }
        } else if (args.length == 3){
            if(args[0].equalsIgnoreCase("modify") || args[1].equalsIgnoreCase("modify")) {
                strings.add("prefix");
                strings.add("powerlevel");
                strings.add("color");
                strings.add("textcolor");
                strings.add("nicknamable");
                strings.add("permission");
            }
        } else if (args.length == 4) {
            if(args[0].equalsIgnoreCase("help")) {
                if (args[2].equalsIgnoreCase("permission")) {
                    strings.add("list");
                    strings.add("add");
                    strings.add("remove");
                }
            } else if (args[2].equalsIgnoreCase("color") || args[2].equalsIgnoreCase("textcolor")) {
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
