package me.foxikle.foxrank;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.foxikle.foxrank.events.PlayerNicknameEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class Nick implements CommandExecutor {
    private static String skinOption;
    private static String rankID;

    protected static void changeName(String name, Player player) {
        if (name.length() <= 16) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerPlayer sp = craftPlayer.getHandle();
            GameProfile profile = sp.getGameProfile();

            try {
                Field field = profile.getClass().getDeclaredField("name");
                field.setAccessible(true);
                field.set(profile, name);
            } catch (Exception e) {
                e.printStackTrace();
            }

            player.setDisplayName(player.getName());
            player.setPlayerListName(name);
            player.setCustomName(player.getName());

            player.sendMessage(FoxRank.getInstance().getMessage("NickNameChangedMessage", player));
        } else {
            player.sendMessage(FoxRank.getInstance().getMessage("NicknameTooLongMessage", player));
        }
    }

    protected static void openRankBook(Player player) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        TextComponent textCompnent = new TextComponent("Let's get you set up  with your nickname! First, you'll need to choose which RANK you would like to be shown as when nicked.\n");
        textCompnent.setColor(ChatColor.BLACK);

        for (Rank rank : FoxRank.getInstance().ranks.values()) {
            if (rank.isNicknameable()) {
                TextComponent component = new TextComponent(ChatColor.BLACK + "\n » " + (rank.getPrefix().isBlank() ? rank.getColor() + rank.getId() : rank.getPrefix()));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick rank " + rank.getId()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to choose " + rank.getId()).color(rank.getColor().asBungee()).create()));
                textCompnent.addExtra(component);
            }
        }

        BaseComponent[] pages = new BaseComponent[]{textCompnent};

        meta.spigot().addPage(pages);

        meta.setTitle("Nickname Book");
        meta.setAuthor("FoxRank");
        book.setItemMeta(meta);
        book.setItemMeta(meta);

        player.openBook(book);

    }

    protected static void openWarningBook(Player player) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        TextComponent textCompnent = new TextComponent("Nicknames allow you to play with a different username to not get recognized. \n\nAll rules still apply. You can still be reported and all name history is stored.");
        textCompnent.setColor(ChatColor.BLACK);

        TextComponent continueComponent = new TextComponent(ChatColor.BOLD + "\n\n » " + ChatColor.RESET + ChatColor.BLACK + "I understand, setup my nickname.");
        continueComponent.setColor(ChatColor.BLACK);
        continueComponent.setColor(ChatColor.UNDERLINE);
        continueComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick agree"));
        continueComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to agree").color(ChatColor.GREEN).create()));

        textCompnent.addExtra(continueComponent);
        BaseComponent[] pages = new BaseComponent[]{textCompnent};

        meta.spigot().addPage(pages);

        meta.setTitle("Nickname Book");
        meta.setAuthor("Foxikle");
        book.setItemMeta(meta);
        book.setItemMeta(meta);
        player.openBook(book);
    }

    protected static void openNameBook(Player player) { //TODO: make this better

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        TextComponent textCompnent = new TextComponent("Alright, now you'll \nneed to choose the\n " + ChatColor.BOLD + "NAME " + ChatColor.RESET + "to use!\n");
        textCompnent.setColor(ChatColor.BLACK);

        if(player.hasPermission("foxrank.nicknames.name.custom")) {
            TextComponent customComponent = new TextComponent(ChatColor.BOLD + "\n » " + ChatColor.RESET + ChatColor.BLACK + "Use a custom name.");
            customComponent.setColor(ChatColor.BLACK);
            customComponent.setColor(ChatColor.UNDERLINE);
            customComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick set"));
            customComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to choose a custom name.").color(ChatColor.GREEN).create()));
            textCompnent.addExtra(customComponent);
        }

        if(player.hasPermission("foxrank.nicknames.name.random")) {
            TextComponent randomNameComponent = new TextComponent(ChatColor.BOLD + "\n » " + ChatColor.RESET + ChatColor.BLACK + "Use a random name.");
            randomNameComponent.setColor(ChatColor.BLACK);
            randomNameComponent.setColor(ChatColor.UNDERLINE);
            randomNameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick random"));
            randomNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to choose a random name.").color(ChatColor.GREEN).create()));
            textCompnent.addExtra(randomNameComponent);
        }
        if(FoxRank.getInstance().getPlayerData(player.getUniqueId()).getNickname() != null && player.hasPermission("foxrank.nicknames.name.reuse")) {
            String name = FoxRank.getInstance().getPlayerData(player.getUniqueId()).getNickname();
            TextComponent reuseComponent = new TextComponent(ChatColor.BOLD + "\n » " + ChatColor.RESET + ChatColor.BLACK + "Reuse '" + name + "'");
            reuseComponent.setColor(ChatColor.BLACK);
            reuseComponent.setColor(ChatColor.UNDERLINE);
            reuseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick reuse"));
            reuseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to reuse '" + name + "'" ).color(ChatColor.GREEN).create()));
            textCompnent.addExtra(reuseComponent);
        }
        TextComponent goBackComponent = new TextComponent("To go back to being\n your usual self, type:\n " + ChatColor.BOLD + "/nick reset");
        textCompnent.addExtra(goBackComponent);
        BaseComponent[] pages = new BaseComponent[]{textCompnent};

        meta.spigot().addPage(pages);

        meta.setTitle("Nickname Book");
        meta.setAuthor("Foxikle");
        book.setItemMeta(meta);
        book.setItemMeta(meta);
        player.openBook(book);
    }

    protected static void openSkinBook(Player player) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        TextComponent textCompnent = new TextComponent("Awesome! Now, which SKIN would you like to have while nicked?");
        textCompnent.setColor(ChatColor.BLACK);

        TextComponent normalSkinComponent = new TextComponent(ChatColor.BOLD + "\n» " + ChatColor.RESET + ChatColor.BLACK + "My normal Skin");
        normalSkinComponent.setColor(ChatColor.BLACK);
        normalSkinComponent.setColor(ChatColor.UNDERLINE);
        normalSkinComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick skin real"));
        normalSkinComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Your normal Skin").color(ChatColor.GREEN).create()));

        TextComponent DefaultComponent = new TextComponent(ChatColor.BOLD + "\n» " + ChatColor.RESET + ChatColor.BLACK + "Steve/Alex skin");
        DefaultComponent.setColor(ChatColor.BLACK);
        DefaultComponent.setColor(ChatColor.UNDERLINE);
        DefaultComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick skin default"));
        DefaultComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to select Steve/Alex").color(ChatColor.GREEN).create()));

        TextComponent randomSkinComponent = new TextComponent(ChatColor.BOLD + "\n» " + ChatColor.RESET + ChatColor.BLACK + "Random Skin");
        randomSkinComponent.setColor(ChatColor.BLACK);
        randomSkinComponent.setColor(ChatColor.UNDERLINE);
        randomSkinComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick skin random"));
        randomSkinComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("A random Skin").color(ChatColor.GREEN).create()));


        textCompnent.addExtra(normalSkinComponent);
        textCompnent.addExtra(DefaultComponent);
        textCompnent.addExtra(randomSkinComponent);
        BaseComponent[] pages = new BaseComponent[]{textCompnent};

        meta.spigot().addPage(pages);

        meta.setTitle("Nickname Book");
        meta.setAuthor("Foxikle");
        book.setItemMeta(meta);
        book.setItemMeta(meta);
        player.openBook(book);
    }

    private static void finalizeNick(Player player, String name){
        FoxRank.getInstance().attemptedNicknameMap.put(player.getUniqueId(), name);
        if(!FoxRank.getInstance().getConfig().getStringList("BlacklistedNicknames").contains(name.toLowerCase())) {
            FoxRank.getInstance().getPlayerData(player.getUniqueId()).setNicked(true);
            FoxRank.getInstance().getPlayerData(player.getUniqueId()).setNicknameRank(Rank.of(rankID));
            FoxRank.getInstance().getPlayerData(player.getUniqueId()).setNickname(name);
            FoxRank.getInstance().getPlayerData(player.getUniqueId()).setSkin(skinOption);
            Bukkit.getScheduler().runTaskAsynchronously(FoxRank.getInstance(), () -> FoxRank.getInstance().getDm().setNicknameData(player.getUniqueId(), true, Rank.of(rankID), name, skinOption));
            changeName(name, player);
            FoxRank.getInstance().setTeam(player, rankID);
            Logging.addLogEntry(EntryType.NICKNAME, player.getUniqueId(), null, null, Rank.of(rankID).getPrefix() + name, skinOption, null);
            FoxRank.getInstance().getServer().getPluginManager().callEvent(new PlayerNicknameEvent(player, name, Rank.of(rankID)));
            ActionBar.setupActionBar(player);
            if (FoxRank.getInstance().getPlayerData(player.getUniqueId()).isVanished()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.hidePlayer(FoxRank.getInstance(), player);
                }
            } else {
                refreshPlayer(player);
            }
        } else {
            player.sendMessage(FoxRank.getInstance().getMessage("BlacklistedNicknameMessage", player));
        }
    }

    protected static void changeSkin(Player player, String skin) {
        GameProfile profile = ((CraftPlayer) player).getHandle().getGameProfile();

        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", getSkin(skin));
    }

    protected static void loadSkin(Player player) {
            GameProfile profile = ((CraftPlayer) player).getHandle().getGameProfile();
            profile.getProperties().removeAll("textures");
        switch (FoxRank.getInstance().getPlayerData(player.getUniqueId()).getSkin()) {
            case "real":
                profile.getProperties().put("textures", getSkin(FoxRank.getInstance().getTrueName(player.getUniqueId())));
            case "random":
                List<String> values = FoxRank.getInstance().getConfig().getStringList("RandomSkinValueList");
                List<String> signatures = FoxRank.getInstance().getConfig().getStringList("RandomSkinSignatureList");
                if (signatures.size() == values.size()) {
                    int rnd = new Random().nextInt(values.size() - 1) + 1;
                    String value = values.get(rnd);
                    String signature = signatures.get(rnd);
                    profile.getProperties().put("textures", new Property("textures", value, signature));
                    }
                case "default":
                    profile.getProperties().put("textures", getSkin(null));
            }
            refreshPlayer(player);
    }

    protected static void refreshPlayer(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(FoxRank.getInstance(), player);
            p.showPlayer(FoxRank.getInstance(), player);
        }
    }

    private static Property getSkin(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = JsonParser.parseReader(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());

            JsonObject property = JsonParser.parseReader(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String value = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new Property("textures", value, signature);
        } catch (Exception e) {
            return new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMjAwNzA2NTgyNiwKICAicHJvZmlsZUlkIiA6ICJjOTAzOGQzZjRiMTg0M2JiYjUwNTU5ZGE3MWFjMTk2MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUQk5SY29vbGNhdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYjA1ZjJlYjM1MmY2YzJlNWM5MThjNzQ4MjU2ZDIzMWFjNzEyODE5NDhlNWFmNzRkMTQ2YTg4ZDc3NTBmNDkwIgogICAgfQogIH0KfQ==", "YpxtT6K8I3+nVvc2fr0m3CLyi2FcuMCIFNK7Z233P0LUWdTlmvQazHSXbpGe2LWBwGAF3snKry9UAuPSYHWWfJ1ygOWrqeyDeM3JT6Cv+QAmqFj3NJZbXF2NP9a1csH2v8hgQvvhJV1hLukGTu301zQnKBiZoYk8tKuFbfqwIXdKevyDoc0dTo9P91O7ZychEFOjKiEWbetQWhOpzwzGOnaynToeC8WkSvoQ3vzuhtEx3emjVzcGGGozkeGTygbeny9kDtBGXzBQJ7uEui8XtaXRwSoQj2cUMQ0KNsQNRNdo9I1BymYvxxqxJtc8AnW2ubccXMxWlABNIGgX5mrbKMOlRa/y1y/zDK1hbA9beQUm7ljP38O4eMUrFkAYkNNoOnFQrmAddofhpqDtJPwSk/rYlALl61qPqk3t9xKcR2b3Vi/gnV/r8pG0B3oo3KFZVJ6qzXVE/rmh/bfRL5HMJX5lZ+NCCvi3eo9ckJjQDdHOo8fgvAxvwBqNvdHCceioR7XgWOpAFr0Ns6MNsJIYFoiMscQQj0OI694MdtOnmQSTuozlm/oBxObiFfR4fsOW3oH2xS/HzrF3S6U3ydY0AmpBvEv7IJOJEwOoHFd2kNKnD7vOT0+jllk9D06dnB0euDiuIhRZgY6d2UXoeR/bFxD3XLndjuG7oBZcFxq0+18=");
        }
    }

    protected static void createAnvil(Player p) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    finalizeNick(stateSnapshot.getPlayer(), stateSnapshot.getText());
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                })
                .text(p.getName())
                .itemLeft(new ItemStack(Material.NAME_TAG))
                .title("Enter your nickname.")
                .preventClose()
                .plugin(FoxRank.getInstance())
                .open(p);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("nick")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableNicknames")) {
                if (sender instanceof Player player) {
                    if (player.hasPermission("foxrank.nicknames.use")) {
                        if (args.length == 0) {
                            openWarningBook(player);

                        } else if (args.length == 1) {
                            if (args[0].equalsIgnoreCase("random")) {
                                List<String> names = FoxRank.getInstance().getConfig().getStringList("RandomNicknameList");
                                String name = names.get(new Random().nextInt(names.size() - 1) + 1);
                                finalizeNick(player, name);

                            } else if (args[0].equalsIgnoreCase("reuse")) {
                                processSkinChange(FoxRank.getInstance().getPlayerData(player.getUniqueId()).getSkin(), player);
                                changeName(FoxRank.getInstance().getPlayerData(player.getUniqueId()).getNickname(), player);
                                FoxRank.getInstance().getPlayerData(player.getUniqueId()).setNicked(true);
                                if (FoxRank.getInstance().ranks.containsKey(FoxRank.getInstance().getPlayerData(player.getUniqueId()).getNicknameRank().getId())) {
                                    rankID = FoxRank.getInstance().getPlayerData(player.getUniqueId()).getNicknameRank().getId();
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(FoxRank.getInstance(), () -> FoxRank.getInstance().getDm().setNicknameState(player.getUniqueId(), true));
                                if (!FoxRank.getInstance().getPlayerData(player.getUniqueId()).isVanished())
                                    refreshPlayer(player);
                            } else if (args[0].equalsIgnoreCase("set")) {
                                createAnvil(player);
                            } else if (args[0].equalsIgnoreCase("reset")) {
                                String realName = FoxRank.getInstance().getTrueName(player.getUniqueId());
                                changeSkin(player, realName);
                                changeName(realName, player);
                                FoxRank.getInstance().getPlayerData(player.getUniqueId()).setNicked(false);
                                Bukkit.getScheduler().runTaskAsynchronously(FoxRank.getInstance(), () -> FoxRank.getInstance().getDm().setNicknameState(player.getUniqueId(), false));
                                if (!FoxRank.getInstance().getPlayerData(player.getUniqueId()).isVanished())
                                    refreshPlayer(player);
                            } else if (args[0].equals("agree")) {
                                openRankBook(player);
                            }
                        } else {
                            if (args[0].equalsIgnoreCase("rank")) {
                                if (FoxRank.getInstance().ranks.containsKey(args[1])) {
                                    rankID = args[1];
                                }
                                openSkinBook(player);
                            } else if (args[0].equalsIgnoreCase("skin")) {
                                processSkinChange(args[1], player);
                                openNameBook(player);
                            }
                        }
                    } else {
                        player.sendMessage(FoxRank.getInstance().getMessage("NoPermissionMessage", player));
                    }
                }
                return true;
            } else {
                sender.sendMessage(FoxRank.getInstance().getMessage("CommandDisabledMessage", (Player) sender));
            }
        }
        return false;
    }

    private static void processSkinChange(String option, Player player){
        if (option.equalsIgnoreCase("real")) {
            changeSkin(player, player.getName());
            skinOption = "real";
        } else if (option.equalsIgnoreCase("default")) {
            changeSkin(player, null);
            skinOption = "default";
        } else if(option.equalsIgnoreCase("random")){
            GameProfile profile = ((CraftPlayer) player).getHandle().getGameProfile();

            List<String> values = FoxRank.getInstance().getConfig().getStringList("RandomSkinValueList");
            List<String> signatures = FoxRank.getInstance().getConfig().getStringList("RandomSkinSignatureList");
            if(signatures.size() == values.size()) {
                int rnd = new Random().nextInt(values.size()-1) +1;
                String value = values.get(rnd);
                String signature = signatures.get(rnd);
                profile.getProperties().removeAll("textures");
                profile.getProperties().put("textures", new Property("textures", value, signature));

                skinOption = "random";
            } else {
                Bukkit.getLogger().log(Level.SEVERE, "Values of RandomSkinSignatureList and RandomSkinValueList are not identical in size. This is NOT a problem with the plugin. It is a configuration eror.");
                changeSkin(player, null);
            }
        }
    }
}
