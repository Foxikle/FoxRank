package me.foxikle.foxrank;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logs implements CommandExecutor, TabCompleter, Listener {
    private static final List<Inventory> invs = new ArrayList<>();
    private static final Map<Player, Integer> playerPages = new HashMap<>();
    private final FoxRank plugin;
    private final List<String> options = Arrays.asList("MUTE", "UNMUTE", "NICKNAME", "BAN", "UNBAN");
    private final List<ItemStack> items = new ArrayList<>();
    private List<Entry> entries = new ArrayList<>();

    public Logs(FoxRank plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (label.equalsIgnoreCase("logs")) {
                invs.clear();
                items.clear();
                if (!plugin.getConfig().getBoolean("DisableLogsCommand")) {
                    if (sender instanceof Player player) {
                        playerPages.remove(player);
                        RankedPlayer senderRp = new RankedPlayer(player, plugin);
                        if (senderRp.getPowerLevel() >= plugin.getConfig().getInt("LogsCommandPermissions")) {
                            if (args.length < 2) {
                                plugin.sendMissingArgsMessage("/logs", "<player> [logtype]", senderRp);
                            } else if (args.length >= 2) {
                                if (Bukkit.getOfflinePlayer(plugin.dm.getUUID(args[0])) != null) {
                                    OfflineRankedPlayer rp = new OfflineRankedPlayer(Bukkit.getOfflinePlayer(plugin.dm.getUUID(args[0])));
                                    if (options.contains(args[1])) {
                                        final DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                                        entries = plugin.dm.getLogEntries(rp.getUniqueId());
                                        if (args[1].equalsIgnoreCase("MUTE")) {
                                            entries.removeIf(e -> e.type() != EntryType.MUTE);
                                            if (entries.isEmpty()) {
                                                senderRp.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("LogsNoData").replace("$PLAYER", ChatColor.YELLOW + String.valueOf(ChatColor.ITALIC) + rp.getName()).replace("$LOGTYPE", "mute")));
                                            }

                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();
                                                OfflineRankedPlayer orp = new OfflineRankedPlayer(Bukkit.getOfflinePlayer(e.staff()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eMute ID: §b" + e.id()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eIssued by: " + orp.getPrefix() + orp.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eIssued for: §6" + e.option1()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eDuration: §6" + plugin.getFormattedExpiredString(e.duration(), e.time())));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Mute", rp);
                                        } else if (args[1].equalsIgnoreCase("UNMUTE")) {
                                            entries.removeIf(e -> e.type() != EntryType.UNMUTE);
                                            if (entries.isEmpty()) {
                                                senderRp.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("LogsNoData").replace("$PLAYER", ChatColor.YELLOW + String.valueOf(ChatColor.ITALIC) + rp.getName()).replace("$LOGTYPE", "unmute")));
                                            }
                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.FILLED_MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eUnmute ID: §b" + e.id()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eUnmuted by: " + new OfflineRankedPlayer(Bukkit.getOfflinePlayer(e.staff())).getPrefix() + new OfflineRankedPlayer(Bukkit.getOfflinePlayer(e.staff())).getName()));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Unmute", rp);
                                        } else if (args[1].equalsIgnoreCase("NICKNAME")) {
                                            senderRp.sendMessage("entries size: " + entries.size());
                                            entries.removeIf(e -> e.type() != EntryType.NICKNAME);
                                            if (entries.isEmpty()) {
                                                senderRp.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("LogsNoData").replace("$PLAYER", ChatColor.YELLOW + String.valueOf(ChatColor.ITALIC) + rp.getName()).replace("$LOGTYPE", "nickname")));
                                            }
                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.NAME_TAG);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();

                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eNickName: §b" + e.option1()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', rp.getPrefix() + rp.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eSkin: §6" + e.option2()));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Nickname", rp);
                                        } else if (args[1].equalsIgnoreCase("BAN")) {
                                            entries.removeIf(e -> e.type() != EntryType.BAN);
                                            if (entries.isEmpty()) {
                                                senderRp.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("LogsNoData").replace("$PLAYER", ChatColor.YELLOW + String.valueOf(ChatColor.ITALIC) + rp.getName()).replace("$LOGTYPE", "ban")));
                                            }
                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.FILLED_MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();

                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§ePlayer: §b" + rp.getPrefix() + rp.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eStaff: " + new OfflineRankedPlayer(Bukkit.getOfflinePlayer(e.staff())).getPrefix() + new OfflineRankedPlayer(Bukkit.getOfflinePlayer(e.staff())).getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eReason: §b" + e.option1()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eDuration: §b" + plugin.getFormattedExpiredString(e.duration(), e.time())));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eSilent: §b" + e.option2()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eBan ID: §b" + e.id()));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Ban", rp);
                                        } else if (args[1].equalsIgnoreCase("UNBAN")) {
                                            entries.removeIf(e -> e.type() != EntryType.UNBAN);
                                            if (entries.isEmpty()) {
                                                senderRp.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("LogsNoData").replace("$PLAYER", ChatColor.YELLOW + rp.getName()).replace("$LOGTYPE", "unban")));
                                            }
                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();

                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§ePlayer: §b" + rp.getPrefix() + rp.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eStaff: §b" + new OfflineRankedPlayer(Bukkit.getOfflinePlayer(e.staff())).getPrefix() + new OfflineRankedPlayer(Bukkit.getOfflinePlayer(e.staff())).getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eBan ID: §b" + e.id()));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Unban", rp);
                                        }
                                        playerPages.put(player, 0);
                                        openInv(player, invs.get(playerPages.get(player)));

                                    } else {
                                        plugin.sendInvalidArgsMessage("LogType", senderRp);
                                    }

                                } else {
                                    plugin.sendInvalidArgsMessage("Player", senderRp);
                                }
                            } else {
                                plugin.sendMissingArgsMessage("/logs", "<player/logtype>", senderRp);
                            }
                        } else {
                            plugin.sendNoPermissionMessage(plugin.getConfig().getInt("LogsCommandPermissions"), senderRp);
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("CommandDisabledMessage")));
                }
            }

        });
        return true;
    }

    private void openInv(Player player, Inventory inv) {
        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(inv));
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
            playerNames.addAll(plugin.dm.getPlayerNames((Player) sender));
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        return new ArrayList<>();
    }

    private Inventory addBorder(Inventory inv) {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
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

    private void makeInventories(String type, OfflineRankedPlayer rp) {

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§eNext Page"));
        NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
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
            Inventory inv = addBorder(Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('§', plugin.getConfig().getString("LogsMenuName").replace("$NAME", ChatColor.YELLOW + rp.getName()).replace("$LOGTYPE", type) + "   (" + (i + 1) + "/" + pagesint + ")")));
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
        NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
        CustomItemTagContainer tagContainer = meta.getCustomTagContainer();
        //TODO: change to use persistent data container
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

