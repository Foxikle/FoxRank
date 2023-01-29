package me.foxikle.foxrank;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class Logs implements CommandExecutor, TabExecutor, Listener {
    private static final List<Inventory> invs = new ArrayList<>();
    private static final Map<Player, Integer> playerPages = new HashMap<>();
    private final FoxRank foxRank = FoxRank.getInstance();
    private final List<String> options = Arrays.asList("MUTE", "UNMUTE", "NICKNAME", "BAN", "UNBAN");
    private final List<ItemStack> items = new ArrayList<>();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("logs")) {
            invs.clear();
            items.clear();
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableLogsCommand")) {
                if (sender instanceof Player player) {
                    playerPages.remove(player);
                    RankedPlayer rankedPlayer = new RankedPlayer(player);
                    File file = new File("plugins/FoxRank/auditlog.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    if (rankedPlayer.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("LogsCommandPermissions")) {
                        if (args.length < 2) {
                            FoxRank.getInstance().sendMissingArgsMessage("/logs", "<player> [logtype]", new RankedPlayer(player));
                        } else if (args.length >= 2) {
                            if (Bukkit.getPlayerExact(args[0]) != null) {
                                RankedPlayer rp = new RankedPlayer(Bukkit.getPlayerExact(args[0]));
                                if (options.contains(args[1])) {
                                    final DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                                    if (args[1].equalsIgnoreCase("MUTE")) {
                                        ConfigurationSection section;
                                        HashSet<String> muteList;
                                        try {
                                            section = yml.getConfigurationSection("MUTE." + rp.getUniqueId().toString());
                                            muteList = new HashSet<>(section.getKeys(false));
                                        } catch (NullPointerException ignored) {
                                            rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "mute")));
                                            return true;
                                        }
                                        for (String str : muteList) {
                                            ItemStack item = new ItemStack(Material.MAP);
                                            ItemMeta meta = item.getItemMeta();
                                            ConfigurationSection itemSection = section.getConfigurationSection(str);
                                            Date date = Date.from(Instant.parse(Objects.requireNonNull(itemSection.getString("Date"))));
                                            meta.setDisplayName(f.format(date));
                                            List<String> lore = new ArrayList<>();
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eMute ID: §b" + itemSection.getString("ID")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eIssued by: " + itemSection.getString("Staff")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eIssued for: §6" + itemSection.getString("Reason")));
                                            meta.setLore(lore);
                                            NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                            meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                            item.setItemMeta(meta);
                                            items.add(item);
                                        }
                                        makeInventories("Mute", rp);
                                    } else if (args[1].equalsIgnoreCase("UNMUTE")) {
                                        ConfigurationSection section;
                                        HashSet<String> muteList;
                                        try {
                                            section = yml.getConfigurationSection("UNMUTE." + rp.getUniqueId().toString());
                                            muteList = new HashSet<>(section.getKeys(false));
                                        } catch (NullPointerException ignored) {
                                            rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "unmute")));
                                            return true;
                                        }
                                        for (String str : muteList) {
                                            ItemStack item = new ItemStack(Material.FILLED_MAP);
                                            ItemMeta meta = item.getItemMeta();
                                            ConfigurationSection itemSection = section.getConfigurationSection(str);
                                            Date date = Date.from(Instant.parse(itemSection.getString("Date")));
                                            meta.setDisplayName(f.format(date));
                                            List<String> lore = new ArrayList<>();
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eUnmute ID: §b" + itemSection.getString("ID")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eUnmuted by: " + itemSection.getString("Staff")));
                                            meta.setLore(lore);
                                            NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                            meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                            item.setItemMeta(meta);
                                            items.add(item);
                                        }
                                        makeInventories("Unmute", rp);
                                    } else if (args[1].equalsIgnoreCase("NICKNAME")) {
                                        ConfigurationSection section;
                                        HashSet<String> muteList;
                                        try {
                                            section = yml.getConfigurationSection("NICKNAME." + rp.getUniqueId().toString());
                                            muteList = new HashSet<>(section.getKeys(false));
                                        } catch (NullPointerException ignored) {
                                            rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "nickname")));
                                            return true;
                                        }
                                        for (String str : muteList) {
                                            ItemStack item = new ItemStack(Material.NAME_TAG);
                                            ItemMeta meta = item.getItemMeta();
                                            ConfigurationSection itemSection = section.getConfigurationSection(str);
                                            Date date = Date.from(Instant.parse(itemSection.getString("Date")));
                                            meta.setDisplayName(f.format(date));
                                            List<String> lore = new ArrayList<>();

                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eNickName: §b" + itemSection.getString("Nickname")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eRank : §a" + Rank.ofString(itemSection.getString("RankID")).getPrefix()));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', rp.getPrefix() + rp.getName()));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eSkin: §6" + itemSection.getString("Skin")));
                                            meta.setLore(lore);
                                            NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                            meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                            item.setItemMeta(meta);
                                            items.add(item);
                                        }
                                        makeInventories("Nickname", rp);
                                    } else if (args[1].equalsIgnoreCase("BAN")) {
                                        ConfigurationSection section;
                                        HashSet<String> muteList;
                                        try {
                                            section = yml.getConfigurationSection("BAN." + rp.getUniqueId().toString());
                                            muteList = new HashSet<>(section.getKeys(false));
                                        } catch (NullPointerException ignored) {
                                            rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "ban")));
                                            return true;
                                        }
                                        for (String str : muteList) {
                                            ItemStack item = new ItemStack(Material.FILLED_MAP);
                                            ItemMeta meta = item.getItemMeta();
                                            ConfigurationSection itemSection = section.getConfigurationSection(str);
                                            Date date = Date.from(Instant.parse(itemSection.getString("When")));
                                            meta.setDisplayName(f.format(date));
                                            List<String> lore = new ArrayList<>();

                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§ePlayer: §b" + rp.getPrefix() + rp.getName()));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eType: §b" + itemSection.getString("Type")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eReason: §b" + itemSection.getString("Reason")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eDuration: §b" + itemSection.getString("Duration")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eSilent: §b" + itemSection.getString("Silent")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eBan ID: §b" + itemSection.getString("ID")));
                                            meta.setLore(lore);
                                            NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                            meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                            item.setItemMeta(meta);
                                            items.add(item);
                                        }
                                        makeInventories("Ban", rp);
                                    } else if (args[1].equalsIgnoreCase("UNBAN")) {
                                        ConfigurationSection section;
                                        HashSet<String> muteList;
                                        try {
                                            section = yml.getConfigurationSection("UNBAN." + rp.getUniqueId().toString());
                                            muteList = new HashSet<>(section.getKeys(false));
                                        } catch (NullPointerException ignored) {
                                            rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "unban")));
                                            return true;
                                        }
                                        for (String str : muteList) {
                                            ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
                                            ItemMeta meta = item.getItemMeta();
                                            ConfigurationSection itemSection = section.getConfigurationSection(str);
                                            Date date = Date.from(Instant.parse(itemSection.getString("Date")));
                                            meta.setDisplayName(f.format(date));
                                            List<String> lore = new ArrayList<>();

                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§ePlayer: §b" + rp.getPrefix() + rp.getName()));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eStaff: §b" + itemSection.getString("Staff")));
                                            lore.add(ChatColor.translateAlternateColorCodes('§', "§eBan ID: §b" + itemSection.getString("ID")));
                                            meta.setLore(lore);
                                            NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                            meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                            item.setItemMeta(meta);
                                            items.add(item);
                                        }
                                        makeInventories("Unban", rp);
                                    }
                                    playerPages.put(player, 0);
                                    player.openInventory(invs.get(playerPages.get(player)));
                                    return true;
                                } else {
                                    FoxRank.getInstance().sendInvalidArgsMessage("LogType", rankedPlayer);
                                    return false;
                                }

                            } else if (Bukkit.getOfflinePlayer(FoxRank.getInstance().getUUID(args[0])) != null) {
                                UUID uuid = FoxRank.getInstance().getUUID(args[0]);
                                if (uuid != null) {
                                    OfflineRankedPlayer rp = new OfflineRankedPlayer(Bukkit.getOfflinePlayer(uuid));
                                    if (options.contains(args[1])) {
                                        final DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                                        if (args[1].equalsIgnoreCase("MUTE")) {
                                            ConfigurationSection section;
                                            HashSet<String> muteList;
                                            try {
                                                section = yml.getConfigurationSection("MUTE." + rp.getUniqueId().toString());
                                                muteList = new HashSet<>(section.getKeys(false));
                                            } catch (NullPointerException ignored) {
                                                rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "unmute")));
                                                return true;
                                            }
                                            for (String str : muteList) {
                                                ItemStack item = new ItemStack(Material.MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                ConfigurationSection itemSection = section.getConfigurationSection(str);
                                                Date date = Date.from(Instant.parse(itemSection.getString("Date")));
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eMute ID: §b" + itemSection.getString("ID")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eIssued by: " + itemSection.getString("Staff")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eIssued for: §6" + itemSection.getString("Reason")));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Mute", rp);
                                        } else if (args[1].equalsIgnoreCase("UNMUTE")) {
                                            ConfigurationSection section;
                                            HashSet<String> muteList;
                                            try {
                                                section = yml.getConfigurationSection("UNMUTE." + rp.getUniqueId().toString());
                                                muteList = new HashSet<>(section.getKeys(false));
                                            } catch (NullPointerException ignored) {
                                                rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "unmute")));
                                                return true;
                                            }
                                            for (String str : muteList) {
                                                ItemStack item = new ItemStack(Material.FILLED_MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                ConfigurationSection itemSection = section.getConfigurationSection(str);
                                                Date date = Date.from(Instant.parse(itemSection.getString("Date")));
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eUnmute ID: §b" + itemSection.getString("ID")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eUnmuted by: " + itemSection.getString("Staff")));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Unmute", rp);
                                        } else if (args[1].equalsIgnoreCase("NICKNAME")) {
                                            ConfigurationSection section;
                                            HashSet<String> muteList;
                                            try {
                                                section = yml.getConfigurationSection("NICKNAME." + rp.getUniqueId().toString());
                                                muteList = new HashSet<>(section.getKeys(false));
                                            } catch (NullPointerException ignored) {
                                                rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "nickname")));
                                                return true;
                                            }
                                            for (String str : muteList) {
                                                ItemStack item = new ItemStack(Material.NAME_TAG);
                                                ItemMeta meta = item.getItemMeta();
                                                ConfigurationSection itemSection = section.getConfigurationSection(str);
                                                Date date = Date.from(Instant.parse(itemSection.getString("Date")));
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();

                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eNickName: §b" + itemSection.getString("Nickname")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eRank : §a" + Rank.ofString(itemSection.getString("RankID")).getPrefix()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', rp.getPrefix() + rp.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eSkin: §6" + itemSection.getString("Skin")));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Nickname", rp);
                                        } else if (args[1].equalsIgnoreCase("BAN")) {
                                            ConfigurationSection section;
                                            HashSet<String> muteList;
                                            try {
                                                section = yml.getConfigurationSection("BAN." + rp.getUniqueId().toString());
                                                muteList = new HashSet<>(section.getKeys(false));
                                            } catch (NullPointerException ignored) {
                                                rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "ban")));
                                                return true;
                                            }
                                            for (String str : muteList) {
                                                ItemStack item = new ItemStack(Material.FILLED_MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                ConfigurationSection itemSection = section.getConfigurationSection(str);
                                                Date date = Date.from(Instant.parse(itemSection.getString("When")));
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();

                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§ePlayer: §b" + rp.getPrefix() + rp.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eType: §b" + itemSection.getString("Type")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eReason: §b" + itemSection.getString("Reason")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eDuration: §b" + itemSection.getString("Duration")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eSilent: §b" + itemSection.getString("Silent")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eBan ID: §b" + itemSection.getString("ID")));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Ban", rp);
                                        } else if (args[1].equalsIgnoreCase("UNBAN")) {
                                            ConfigurationSection section;
                                            HashSet<String> muteList;
                                            try {
                                                section = yml.getConfigurationSection("UNBAN." + rp.getUniqueId().toString());
                                                muteList = new HashSet<>(section.getKeys(false));
                                            } catch (NullPointerException ignored) {
                                                rankedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("LogsNoData").replace("$PLAYER", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", "unban")));
                                                return true;
                                            }
                                            for (String str : muteList) {
                                                ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
                                                ItemMeta meta = item.getItemMeta();
                                                ConfigurationSection itemSection = section.getConfigurationSection(str);
                                                Date date = Date.from(Instant.parse(itemSection.getString("Date")));
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();

                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§ePlayer: §b" + rp.getPrefix() + rp.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eStaff: §b" + itemSection.getString("Staff")));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eBan ID: §b" + itemSection.getString("ID")));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Unban", rp);
                                        }
                                        playerPages.put(player, 0);
                                        player.openInventory(invs.get(playerPages.get(player)));
                                        return true;
                                    } else {
                                        FoxRank.getInstance().sendInvalidArgsMessage("LogType", rankedPlayer);
                                        return false;
                                    }
                                }
                            } else {
                                FoxRank.getInstance().sendInvalidArgsMessage("Player", rankedPlayer);
                            }
                        }
                    } else {
                        FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("LogsCommandPermissions"), rankedPlayer);
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
        if (args.length == 2) {
            List<String> arguments = new ArrayList<>();
            arguments.add("MUTE");
            arguments.add("UNMUTE");
            arguments.add("NICKNAME");
            arguments.add("BAN");
            arguments.add("UNBAN");
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
        return null;
    }

    private Inventory addBorder(Inventory inv) {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
        meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "PANE");
        meta.setDisplayName(" ");
        lore.add("");
        meta.setLore(lore);
        item.setItemMeta(meta);

        for (int x = 0; x < inv.getSize(); x++) {
            if ((x < 9 || x > inv.getSize() - 9 || x % 9 == 0 || (x + 1) % 9 == 0) && inv.getItem(x) == null) {
                inv.setItem(x, item);
            }
        }
        return inv;
    }

    private void makeInventories(String type, RankedPlayer rp) {

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§eNext Page"));
        NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
        nextMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "next");
        next.setItemMeta(nextMeta);

        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§ePrevious Page"));
        prevMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "prev");
        prev.setItemMeta(prevMeta);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§c§lClose"));
        closeMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "close");
        close.setItemMeta(closeMeta);
        sortItems();
        int pagesint = (int) Math.ceil(items.size() / 28.0);
        for (int i = 0; i < pagesint; i++) {
            Inventory inv = addBorder(Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('§', foxRank.getConfig().getString("LogsMenuName").replace("$NAME", rp.getName()).replace("$LOGTYPE", type) + "   (" + (i + 1) + "/" + pagesint + ")")));
            if (i < pagesint - 1) {
                inv.setItem(53, next);
            }
            if (i >= 1) {
                inv.setItem(45, prev);
            }
            inv.setItem(49, close);
            for (int x = 0; x < 28; x++) {
                if (items.size() >= 1) {
                    inv.addItem(items.get(0));
                } else {
                    break;
                }
                items.remove(0);
            }
            invs.add(inv);
        }
    }

    private void makeInventories(String type, OfflineRankedPlayer rp) {

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§eNext Page"));
        NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
        nextMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "next");
        next.setItemMeta(nextMeta);

        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§ePrevious Page"));
        prevMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "prev");
        prev.setItemMeta(prevMeta);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§c§lClose"));
        closeMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "close");
        close.setItemMeta(closeMeta);
        sortItems();
        int pagesint = (int) Math.ceil(items.size() / 28.0);
        for (int i = 0; i < pagesint; i++) {
            Inventory inv = addBorder(Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('§', foxRank.getConfig().getString("LogsMenuName").replace("$NAME", rp.getRank().getPrefix() + rp.getName()).replace("$LOGTYPE", type) + "   (" + (i + 1) + "/" + pagesint + ")")));
            if (i < pagesint - 1) {
                inv.setItem(53, next);
            }
            if (i >= 1) {
                inv.setItem(45, prev);
            }
            inv.setItem(49, close);
            for (int x = 0; x < 28; x++) {
                if (items.size() >= 1) {
                    inv.addItem(items.get(0));
                } else {
                    break;
                }
                items.remove(0);
            }
            invs.add(inv);
        }
    }

    private void sortItems() {
        Map<String, ItemStack> map = new HashMap<>();
        for (ItemStack is : items) {
            map.put(is.getItemMeta().getDisplayName(), is);
        }
        List<String> names = new ArrayList<>();
        names.addAll(map.keySet());
        List<ItemStack> itemStackList = new ArrayList<>();
        names.sort(new Comparator<>() {
            final DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        for (String name : names) {
            itemStackList.add(map.get(name));
        }
        for (ItemStack item : itemStackList) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§r§a" + meta.getDisplayName()));
            item.setItemMeta(meta);
        }
        items.clear();
        items.addAll(itemStackList);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        ItemMeta meta = e.getCurrentItem().getItemMeta();
        NamespacedKey key = new NamespacedKey(foxRank, "NoClickey");
        CustomItemTagContainer tagContainer = meta.getCustomTagContainer();
        Player player = (Player) e.getWhoClicked();
        if (meta.getPersistentDataContainer().getKeys().contains(key)) {
            playerPages.putIfAbsent(player, 0);
            int page = playerPages.get(player);
            e.setCancelled(true);
            if (tagContainer.getCustomTag(key, ItemTagType.STRING) != null) {
                if (tagContainer.getCustomTag(key, ItemTagType.STRING).equals("next")) {
                    if (invs.size() >= page + 1) {
                        player.openInventory(invs.get(page + 1));
                        playerPages.remove(player);
                        playerPages.put(player, page + 1);
                    }
                } else if (tagContainer.getCustomTag(key, ItemTagType.STRING).equals("prev")) {
                    player.openInventory(invs.get(page - 1));
                    playerPages.remove(player);
                    playerPages.put(player, page - 1);
                } else if (tagContainer.getCustomTag(key, ItemTagType.STRING).equals("close")) {
                    player.closeInventory();
                }
            }
        }
    }
}

