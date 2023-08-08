package me.foxikle.foxrank;

import me.clip.placeholderapi.PlaceholderAPI;
import me.foxikle.foxrank.Data.DataManager;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
            if (label.equalsIgnoreCase("logs") || label.equalsIgnoreCase("foxrank:logs")) {
                invs.clear();
                items.clear();
                if (!plugin.getConfig().getBoolean("DisableLogsCommand")) {
                    if (sender instanceof Player player) {
                        playerPages.remove(player);
                        if (player.hasPermission("foxrank.logging.use")) {
                            if (args.length < 2) {
                                plugin.syntaxMap.put(player.getUniqueId(), "/logs <player> <logtype>");
                               player.sendMessage(plugin.getSyntaxMessage(player));
                            } else {
                                if (Bukkit.getOfflinePlayer(DataManager.getUUID(args[0])) != null) {
                                    OfflinePlayer target = Bukkit.getOfflinePlayer(DataManager.getUUID(args[0]));
                                    plugin.targetMap.put(player.getUniqueId(), target.getUniqueId());
                                    plugin.logTypeMap.put(player.getUniqueId(), args[1]);
                                    if (options.contains(args[1])) {
                                        final DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                        entries = plugin.getDm().getLogEntries(DataManager.getUUID(args[0]));
                                        if (args[1].equalsIgnoreCase("MUTE")) {
                                            if (!player.hasPermission("foxrank.logging.mute")) {
                                                player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                return;
                                            }
                                            entries.removeIf(e -> e.type() != EntryType.MUTE);
                                            if (entries.isEmpty()) {
                                                player.sendMessage(plugin.getMessage("LogsNoData", player));
                                                return;
                                            }

                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();
                                                OfflinePlayer op = Bukkit.getOfflinePlayer(e.staff());
                                                Rank staffRank = plugin.getPlayerData(e.staff()).getRank();
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eMute ID: §b" + e.id()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eIssued by: " + staffRank.getPrefix() + op.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eIssued for: §6" + e.option1()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eDuration: §6" + plugin.getFormattedExpiredString(e.duration(), e.time())));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Mute", player);
                                        } else if (args[1].equalsIgnoreCase("UNMUTE")) {
                                            if (!player.hasPermission("foxrank.logging.unmute")) {
                                                player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                return;
                                            }
                                            entries.removeIf(e -> e.type() != EntryType.UNMUTE);
                                            if (entries.isEmpty()) {
                                                player.sendMessage(plugin.getMessage("LogsNoData", player));
                                            }
                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.FILLED_MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();
                                                Rank staffRank = plugin.getPlayerData(e.staff()).getRank();
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eUnmute ID: §b" + e.id()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eUnmuted by: " + staffRank.getPrefix() + (Bukkit.getOfflinePlayer(e.staff()).getName())));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Unmute", player);
                                        } else if (args[1].equalsIgnoreCase("NICKNAME")) {
                                            if (!player.hasPermission("foxrank.logging.nickname")) {
                                                player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                return;
                                            }
                                            player.sendMessage("entries size: " + entries.size());
                                            entries.removeIf(e -> e.type() != EntryType.NICKNAME);
                                            if (entries.isEmpty()) {
                                                player.sendMessage(plugin.getMessage("LogsNoData", player));
                                            }
                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.NAME_TAG);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();

                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eNickName: §b" + e.option1()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', plugin.getPlayerData(player.getUniqueId()).getRank().getPrefix() + player.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eSkin: §6" + e.option2()));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Nickname", player);
                                        } else if (args[1].equalsIgnoreCase("BAN")) {
                                            if (!player.hasPermission("foxrank.logging.ban")) {
                                                player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                return;
                                            }
                                            entries.removeIf(e -> e.type() != EntryType.BAN);
                                            if (entries.isEmpty()) {
                                                player.sendMessage(plugin.getMessage("LogsNoData", player));
                                            }
                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.FILLED_MAP);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();

                                                Rank staffRank = plugin.getPlayerData(e.staff()).getRank();

                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§ePlayer: §b" + plugin.getPlayerData(player.getUniqueId()).getRank().getPrefix() + player.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eStaff: " + staffRank.getPrefix() + Bukkit.getOfflinePlayer(e.staff()).getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eReason: §b" + e.option1()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eDuration: §b" + plugin.getFormattedExpiredString(e.duration(), e.time())));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eSilent: §b" + e.option2()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eBan ID: §b" + e.id()));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Ban", player);
                                        } else if (args[1].equalsIgnoreCase("UNBAN")) {
                                            if (!player.hasPermission("foxrank.logging.unban")) {
                                                player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                                                return;
                                            }
                                            entries.removeIf(e -> e.type() != EntryType.UNBAN);
                                            if (entries.isEmpty()) {
                                                player.sendMessage(plugin.getMessage("LogsNoData", player));
                                            }
                                            for (Entry e : entries) {
                                                ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
                                                ItemMeta meta = item.getItemMeta();
                                                Date date = Date.from(e.time());
                                                meta.setDisplayName(f.format(date));
                                                List<String> lore = new ArrayList<>();
                                                Rank staffRank = plugin.getPlayerData(e.staff()).getRank();
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§ePlayer: §b" + plugin.getPlayerData(player.getUniqueId()).getRank().getPrefix() + player.getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eStaff: §b" + staffRank.getPrefix() + Bukkit.getOfflinePlayer(e.staff()).getName()));
                                                lore.add(ChatColor.translateAlternateColorCodes('§', "§eBan ID: §b" + e.id()));
                                                meta.setLore(lore);
                                                NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
                                                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "CARD");
                                                item.setItemMeta(meta);
                                                items.add(item);
                                            }
                                            makeInventories("Unban", player);
                                        }
                                        playerPages.put(player, 0);
                                        openInv(player, invs.get(playerPages.get(player)));
                                        });
                                    } else {
                                        plugin.syntaxMap.put(player.getUniqueId(), "/logs <player> <logtype>");
                                        player.sendMessage(plugin.getSyntaxMessage(player));
                                    }
                                } else {
                                    plugin.syntaxMap.put(player.getUniqueId(), "/logs <player> <logtype>");
                                    player.sendMessage(plugin.getSyntaxMessage(player));
                                }
                            }
                        } else {
                            player.sendMessage(plugin.getMessage("NoPermissionMessage", player));
                        }
                    }
                } else {
                    sender.sendMessage(plugin.getMessage("CommandDisabledMessage", (Player) sender));
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
            playerNames.addAll(plugin.players);
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
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "PANE");
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

    private void makeInventories(String type, Player rp) {

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§eNext Page"));
        NamespacedKey key = new NamespacedKey(plugin, "NoClickey");
        nextMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "next");
        next.setItemMeta(nextMeta);

        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§ePrevious Page"));
        prevMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "prev");
        prev.setItemMeta(prevMeta);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('§', "§c§lClose"));
        closeMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "close");
        close.setItemMeta(closeMeta);
        sortItems();
        int pagesint = (int) Math.ceil(items.size() / 28.0);
        for (int i = 0; i < pagesint; i++) {
            Inventory inv = addBorder(Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(rp, plugin.getConfig().getString("LogsMenuName") + "   (" + (i + 1) + "/" + pagesint + ")"))));
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
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Player player = (Player) e.getWhoClicked();
        if (meta.getPersistentDataContainer().getKeys().contains(key)) {
            playerPages.putIfAbsent(player, 0);
            int page = playerPages.get(player);
            e.setCancelled(true);
            if (pdc.get(key, PersistentDataType.STRING) != null) {
                String data = pdc.get(key, PersistentDataType.STRING);
                switch (data) {
                    case "next" -> {
                        if (invs.size() >= page + 1) {
                            player.openInventory(invs.get(page + 1));
                            playerPages.remove(player);
                            playerPages.put(player, page + 1);
                        }
                    }
                    case "prev" -> {
                        player.openInventory(invs.get(page - 1));
                        playerPages.remove(player);
                        playerPages.put(player, page - 1);
                    }
                    case "close" -> player.closeInventory();
                }
            }
        }
    }
}

