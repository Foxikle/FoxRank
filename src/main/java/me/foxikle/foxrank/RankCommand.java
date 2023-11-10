package me.foxikle.foxrank;

import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
                            §2§m                       §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                   \s
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
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                            BaseComponent[] space   = new ComponentBuilder(" ").color(ChatColor.RESET).bold(false).italic(false).strikethrough(false).underlined(false).create();
                            ComponentBuilder core = new ComponentBuilder();
                            plugin.ranks.keySet().forEach(s -> {
                                Rank rank = plugin.ranks.get(s);
                                ComponentBuilder builder = new ComponentBuilder(rank.getId()).color(ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT))).append(space).append("▸").bold(true).append(space).append(rank.getPrefix()).append(space).append("[INFO]\n").color(ChatColor.YELLOW)
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eID: " + rank.getId() + "\nPrefix: " + rank.getPrefix() + "§r§e\nPower Level: " + rank.getPowerlevel() + "\nText Color: " + ChatColor.valueOf(rank.getTextColor().toString().toUpperCase(Locale.ROOT)) + "I'm some text!" + "\n§r§eColor: " + ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + "I'm more text!" + "\n§r§bClick to view \npermission nodes!").create()))
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
                                ServerPlayer sp = ((CraftPlayer) p).getHandle();
                                sp.getId();
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
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                     \s
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
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                     \s
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
                        Rank rank = Rank.fromID(args[1]);
                        if (args.length >= 4) {
                            if (rank == null) { // todo: syntax
                                plugin.syntaxMap.put(player.getUniqueId(), "Could not find the rank '" + args[1] + "'!");
                                player.sendMessage(plugin.getSyntaxMessage(player));
                                return true;
                            }
                            String action = args[2].toLowerCase();
                            String input = args[3];
                            switch (action) {
                                case "prefix", "prfx" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.prefix")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    if (input.equalsIgnoreCase("<empty>") || input.equalsIgnoreCase("%empty%")) {
                                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankPrefix(rank, ""));
                                        player.sendMessage(ChatColor.GREEN + "Successfully set " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + "'s prefix to be empty"  + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                    } else {
                                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankPrefix(rank, input + " "));
                                        player.sendMessage(ChatColor.GREEN + "Successfully set " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + "'s prefix to '" + ChatColor.translateAlternateColorCodes('&', input) + ChatColor.GREEN + "'. Run /rank reload to propagate changes");
                                    }
                                }
                                case "powerlevel", "pwrlvl", "pl" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.powerlevel")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    try {
                                        Integer.parseInt(input);
                                    } catch (NumberFormatException ignored) {
                                        player.sendMessage(ChatColor.RED + "Failed to parse an integer from '" + input + "'");
                                    }
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankPowerLevel(rank, Integer.parseInt(input)));
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + "'s powerlevel to " + Integer.parseInt(input) + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "color", "clr" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.color")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    try{
                                        org.bukkit.ChatColor.valueOf(input);
                                    } catch (IllegalArgumentException ignored) {
                                        player.sendMessage(ChatColor.RED + "'" + input + "' is not a valid color!");
                                        return true;
                                    }
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankColor(rank, org.bukkit.ChatColor.valueOf(input)));
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + "'s color to be " + ChatColor.of(input).name() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "textcolor", "txtclr" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.textcolor")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    try{
                                        org.bukkit.ChatColor.valueOf(input);
                                    } catch (IllegalArgumentException ignored) {
                                        player.sendMessage(ChatColor.RED + "'" + input + "' is not a valid color!");
                                        return true;
                                    }
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankTextColor(rank, org.bukkit.ChatColor.valueOf(input)));
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + "'s text color to be " + ChatColor.of(input).name() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "nicknamable", "ncknmbl" -> {
                                    if (!player.hasPermission("foxrank.ranks.modify.nicknamable")) {
                                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                        return true;
                                    }
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankNicknamable(rank, Boolean.getBoolean(input )));
                                    player.sendMessage(ChatColor.GREEN + "Successfully set " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + "'s nicknamability to be " + Boolean.parseBoolean(input) + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "perm", "permission", "prmssn" -> {
                                    if (args.length == 4 && input.equalsIgnoreCase("list")) {
                                        if (!player.hasPermission("foxrank.ranks.modify.list_permissions")) {
                                            player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                            return true;
                                        }
                                        player.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                                        player.sendMessage(ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId()  + ChatColor.DARK_AQUA + "'s permission nodes");
                                        if(rank.getPermissionNodes().isEmpty()) {
                                            player.sendMessage(ChatColor.RED + "No permission nodes!");
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
                                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().addRankPermissionNode(rank, node));
                                            player.sendMessage(ChatColor.GREEN + "Successfully added the permission node '" + node + "' to " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                        } else if (input.equalsIgnoreCase("remove")) {
                                            if (!player.hasPermission("foxrank.ranks.modify.remove_permission")) {
                                                player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                                                return true;
                                            }
                                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().removeRankPermissionNode(rank, node));
                                            player.sendMessage(ChatColor.GREEN + "Successfully removed the permission node '" + node + "' to " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                        }
                                    } else {
                                        plugin.syntaxMap.put(player.getUniqueId(), "/rank modify <Rank> permission <add/remove> <permission.node>");
                                        player.sendMessage(plugin.getSyntaxMessage(player));
                                    }
                                }
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
                        Rank rank = new Rank(0, "", args[1], NamedTextColor.WHITE.value(), NamedTextColor.WHITE.value(), true, new ArrayList<>());
                        plugin.ranks.put(args[1], rank);
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().createRank(rank));
                        player.sendMessage(ChatColor.GREEN + "Sucessfully created the rank '" + rank.getId() + "'. Run /rank reload to propagate changes.");

                    } else if (subCommand.equalsIgnoreCase("remove")) {
                        if (!player.hasPermission("foxrank.ranks.remove")) {
                            player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                            return true;
                        }
                        Rank rank = plugin.ranks.get(args[1]);
                        if (rank == null) { // todo: syntax
                            plugin.syntaxMap.put(player.getUniqueId(), "Could not find the rank '" + args[1] + "'!");
                            player.sendMessage(plugin.getSyntaxMessage(player));
                            return true;
                        }
                        plugin.ranks.remove(args[1]);
                        plugin.teamMappings.get(rank.getId()).unregister();
                        plugin.teamMappings.remove(rank.getId());

                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().removeRank(rank));

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
                            §2§m                       §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                   \s
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
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                            BaseComponent[] space   = new ComponentBuilder(" ").color(ChatColor.RESET).bold(false).italic(false).strikethrough(false).underlined(false).create();
                            ComponentBuilder core = new ComponentBuilder();
                            plugin.ranks.keySet().forEach(s -> {
                                Rank rank = plugin.ranks.get(s);
                                ComponentBuilder builder = new ComponentBuilder(rank.getId()).color(ColorUtils.ofNamedTextColor(rank.getColor()).asBungee()).append(space).append("▸").bold(true).append(space).append(rank.getPrefix()).append(space).append("[INFO]\n").color(ChatColor.YELLOW)
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eID: " + rank.getId() + "\nPrefix: " + rank.getPrefix() + "§r§e\nPower Level: " + rank.getPowerlevel() + "\nText Color: " + ChatColor.valueOf(rank.getTextColor().toString().toUpperCase(Locale.ROOT)) + "I'm some text!" + "\n§r§eColor: " + ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + "I'm more text!" + "\n§r§bClick to view \npermission nodes!").create()))
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
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                     \s
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
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                     \s
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

                        if (args.length >= 4) {
                            Rank rank = Rank.fromID(args[1]);
                            if (rank == null) { // todo: syntax
                                console.sendMessage("Could not find the rank '" + args[1] + "'!");
                                return true;
                            }

                            String action = args[2].toLowerCase();
                            String input = args[3];
                            switch (action) {
                                case "prefix", "prfx" -> {
                                    if (input.equalsIgnoreCase("<empty>") || input.equalsIgnoreCase("%empty%")) {
                                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankPrefix(rank, ""));
                                        console.sendMessage(ChatColor.GREEN + "Successfully set " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + "'s prefix to be empty"  + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                    } else {
                                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankPrefix(rank, input + " "));
                                        console.sendMessage(ChatColor.GREEN + "Successfully set " + ColorUtils.ofNamedTextColor(rank.getColor()) + rank.getId() + ChatColor.GREEN + "'s prefix to '" + ChatColor.translateAlternateColorCodes('&', input) + ChatColor.GREEN + "'. Run /rank reload to propagate changes");
                                    }
                                }
                                case "powerlevel", "pwrlvl", "pl" -> {
                                    try {
                                        Integer.parseInt(input);
                                    } catch (NumberFormatException ignored) {
                                        console.sendMessage(ChatColor.RED + "Failed to parse an integer from '" + input + "'");
                                    }
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankPowerLevel(rank, Integer.parseInt(input)));
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + rank.getId() + ChatColor.GREEN + "'s powerlevel to " + Integer.parseInt(input) + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "color", "clr" -> {
                                    try{
                                        org.bukkit.ChatColor.valueOf(input);
                                    } catch (IllegalArgumentException ignored) {
                                        console.sendMessage(ChatColor.RED + "'" + input + "' is not a valid color!");
                                        return true;
                                    }
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankColor(rank, org.bukkit.ChatColor.valueOf(input)));
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + rank.getId() + ChatColor.GREEN + "'s color to be " + ChatColor.of(input).name() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "textcolor", "txtclr" -> {
                                    try{
                                        org.bukkit.ChatColor.valueOf(input);
                                    } catch (IllegalArgumentException ignored) {
                                        console.sendMessage(ChatColor.RED + "'" + input + "' is not a valid color!");
                                        return true;
                                    }
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankTextColor(rank, org.bukkit.ChatColor.valueOf(input)));
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + rank.getId() + ChatColor.GREEN + "'s text color to be " + ChatColor.of(input).name() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "nicknamable", "ncknmbl" -> {
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().setRankNicknamable(rank, Boolean.parseBoolean(input)));
                                    console.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + rank.getId() + ChatColor.GREEN + "'s nicknamability to be " + Boolean.parseBoolean(input) + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                }
                                case "perm", "permission", "prmssn" -> {
                                    if (args.length == 4 && input.equalsIgnoreCase("list")) {
                                        console.sendMessage( """
                            §2§m                    §r§6§l FoxRank §r§7[§8v2.0-beta2§7] §r§2§m                     \s
                            §r                                 §r§6By Foxikle
                            
   
                            """);
                                        console.sendMessage(ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + rank.getId()  + ChatColor.DARK_AQUA + "'s permission nodes");
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
                                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().addRankPermissionNode(rank, node));
                                            console.sendMessage(ChatColor.GREEN + "Successfully added the permission node '" + node + "' to " + ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + rank.getId() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                        } else if (input.equalsIgnoreCase("remove")) {
                                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().removeRankPermissionNode(rank, node));
                                            console.sendMessage(ChatColor.GREEN + "Successfully removed the permission node '" + node + "' to " + ChatColor.valueOf(rank.getColor().toString().toUpperCase(Locale.ROOT)) + rank.getId() + ChatColor.GREEN + ". Run /rank reload to propagate changes");
                                        }
                                    } else {
                                        console.sendMessage(ChatColor.RED + "rank modify <Rank> permission <add/remove> <permission.node>");
                                    }
                                }
                            }
                        } else {
                            console.sendMessage(ChatColor.RED + "rank modify <RankID> <prefix/powerlevel/color/textcolor/nicknameable/permission>");
                        }
                    } else if (subCommand.equalsIgnoreCase("create")) { // `/rank create ADMIN `
                        Rank rank = new Rank(0, "", args[1], NamedTextColor.WHITE.value(), NamedTextColor.WHITE.value(), true, new ArrayList<>());
                        plugin.ranks.put(args[1], rank);
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().createRank(rank));
                        console.sendMessage(ChatColor.GREEN + "Sucessfully created the rank '" + rank.getId() + "'. Run /rank reload to propagate changes.");

                    } else if (subCommand.equalsIgnoreCase("remove")) {
                        Rank rank = plugin.ranks.get(args[1]);
                        plugin.ranks.remove(args[1]);
                        plugin.teamMappings.get(rank.getId()).unregister();

                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDm().removeRank(rank));

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
                strings.add(org.bukkit.ChatColor.WHITE.name());
                strings.add(org.bukkit.ChatColor.BLACK.name());
                strings.add(org.bukkit.ChatColor.RED.name());
                strings.add(org.bukkit.ChatColor.GOLD.name());
                strings.add(org.bukkit.ChatColor.YELLOW.name());
                strings.add(org.bukkit.ChatColor.GREEN.name());
                strings.add(org.bukkit.ChatColor.DARK_GREEN.name());
                strings.add(org.bukkit.ChatColor.BLUE.name());
                strings.add(org.bukkit.ChatColor.DARK_BLUE.name());
                strings.add(org.bukkit.ChatColor.AQUA.name());
                strings.add(org.bukkit.ChatColor.DARK_AQUA.name());
                strings.add(org.bukkit.ChatColor.LIGHT_PURPLE.name());
                strings.add(org.bukkit.ChatColor.DARK_PURPLE.name());
                strings.add(org.bukkit.ChatColor.DARK_RED.name());
                strings.add(org.bukkit.ChatColor.GRAY.name());
                strings.add(org.bukkit.ChatColor.DARK_GRAY.name());
            } else if (args[2].equalsIgnoreCase("nicknamable")) {
                strings.add("true");
                strings.add("false");
            } else if (args[2].equalsIgnoreCase("permission")) {
                strings.add("list");
                strings.add("add");
                strings.add("remove");
            }
        } else if (args.length == 5) {
            Rank rank = Rank.fromID(args[2]);
            if (args[3].equalsIgnoreCase("remove")) {
                strings.addAll(rank.getPermissionNodes());
            } else if (args[3].equalsIgnoreCase("add")) {
                Bukkit.getPluginManager().getPermissions().forEach(permission -> strings.add(permission.getName()));
            }
        }
        return strings;
    }
}
