package me.foxikle.foxrank;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.foxikle.foxrank.events.PlayerNicknameEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
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

            String message = FoxRank.getInstance().getConfig().getString("NickNameChangedMessage");
            message = message.replace("$NEWNICK", name);
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', message));

        } else {
            String message = FoxRank.getInstance().getConfig().getString("NicknameTooLongMessage");
            message = message.replace("$NICKNAME", name);
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', message));
        }
    }

    protected static void openRankBook(Player player) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        TextComponent textCompnent = new TextComponent("Let's get you set up  with your nickname! First, you'll need to choose which RANK you would like to be shown as when nicked.\n");
        textCompnent.setColor(ChatColor.BLACK);

        for (Rank rank : FoxRank.getInstance().ranks.values()) {
            TextComponent component = new TextComponent("\n\n »" + rank.getId());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick rank " + rank.getId()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to chose " + rank.getId()).color(ChatColor.GREEN).create()));
            textCompnent.addExtra(textCompnent);
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
        meta.setAuthor("Emperical");
        book.setItemMeta(meta);
        book.setItemMeta(meta);
        player.openBook(book);
    }

    protected static void openNameBook(Player player) {

        ItemStack nickbook1 = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) nickbook1.getItemMeta();

        TextComponent rndmMsg = new TextComponent("\n» Use a random name");
        rndmMsg.setColor(ChatColor.LIGHT_PURPLE);
        rndmMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick random"));
        rndmMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click for a random nickname").color(ChatColor.GREEN).create()));

        TextComponent setMsg = new TextComponent("\n» Make a custom name");
        setMsg.setColor(ChatColor.RED);
        setMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick set"));
        setMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click for a custom nickname").color(ChatColor.GREEN).create()));

        setMsg.addExtra(rndmMsg);
        BaseComponent[] pages = new BaseComponent[]{setMsg};

        meta.spigot().addPage(pages);

        meta.setTitle("Nickname Book");
        meta.setAuthor("Emperical");
        nickbook1.setItemMeta(meta);
        nickbook1.setItemMeta(meta);
        player.openBook(nickbook1);
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

        TextComponent SteveAlexComponent = new TextComponent(ChatColor.BOLD + "\n» " + ChatColor.RESET + ChatColor.BLACK + "Steve/Alex skin");
        SteveAlexComponent.setColor(ChatColor.BLACK);
        SteveAlexComponent.setColor(ChatColor.UNDERLINE);
        SteveAlexComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick skin default"));
        SteveAlexComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to select Steve/Alex").color(ChatColor.GREEN).create()));

        TextComponent randomSkinComponent = new TextComponent(ChatColor.BOLD + "\n» " + ChatColor.RESET + ChatColor.BLACK + "Random Skin");
        randomSkinComponent.setColor(ChatColor.BLACK);
        randomSkinComponent.setColor(ChatColor.UNDERLINE);
        randomSkinComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick skin random"));
        randomSkinComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("A random Skin").color(ChatColor.GREEN).create()));


        textCompnent.addExtra(normalSkinComponent);
        textCompnent.addExtra(SteveAlexComponent);
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
        if(!FoxRank.getInstance().getConfig().getStringList("BlacklistedNicknames").contains(name.toLowerCase())) {
            FoxRank.getInstance().dm.setNicknameData(player.getUniqueId(), true, Rank.of(rankID), name, skinOption);
            changeName(name, player);
            FoxRank.getInstance().setTeam(player, rankID);
            Logging.addLogEntry(EntryType.NICKNAME, player.getUniqueId(), null, null, Rank.of(rankID).getPrefix() + name, skinOption, null);
            FoxRank.getInstance().getServer().getPluginManager().callEvent(new PlayerNicknameEvent(player, name, Rank.of(rankID)));
            ActionBar.setupActionBar(player);
            if (FoxRank.getInstance().dm.isVanished(player.getUniqueId())) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.hidePlayer(FoxRank.getInstance(), player);
                }
            } else {
                refreshPlayer(player);
            }
        } else {
            String message = FoxRank.getInstance().getConfig().getString("BlacklistedNicknameMessage");
            message = message.replace("$BLACKLISTEDNAME", name);
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', message));
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
        switch (FoxRank.getInstance().dm.getNicknameSkin(player.getUniqueId())) {
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
            ServerPlayer sp = ((CraftPlayer) p).getHandle();
            ServerGamePacketListenerImpl connection = sp.connection;
            connection.send(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(((CraftPlayer) player).getHandle().getUUID())));
            p.hidePlayer(FoxRank.getInstance(), player);
            p.showPlayer(FoxRank.getInstance(), player);
            connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
        }
    }

    private static Property getSkin(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());

            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
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

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("nick")) {
            if (!FoxRank.getInstance().getConfig().getBoolean("DisableNicknames")) {
                if (sender instanceof Player player) {
                    RankedPlayer rp = new RankedPlayer(player, FoxRank.getInstance());
                    if (rp.getPowerLevel() >= FoxRank.getInstance().getConfig().getInt("NicknamePermissions")) {
                        if (args.length == 0) {
                            openWarningBook(player);

                        } else if (args.length == 1) {
                            if (args[0].equalsIgnoreCase("random")) {
                                List<String> names = FoxRank.getInstance().getConfig().getStringList("RandomNicknameList");
                                String name = names.get(new Random().nextInt(names.size() - 1) + 1);
                                finalizeNick(player, name);

                            } else if (args[0].equalsIgnoreCase("set")) {
                                createAnvil(player);
                            } else if (args[0].equalsIgnoreCase("reset")) {
                                String realName = FoxRank.getInstance().getTrueName(player.getUniqueId());
                                changeSkin(player, realName);
                                changeName(realName, player);
                                FoxRank.getInstance().dm.setNicknameState(player.getUniqueId(), false);
                                if (!FoxRank.getInstance().dm.isVanished(player.getUniqueId()))
                                    refreshPlayer(player);
                            } else if (args[0].equals("agree")) {
                                openRankBook(player);
                            }
                        } else if (args.length >= 2) {
                            if (args[0].equalsIgnoreCase("rank")) {
                                if (FoxRank.getInstance().ranks.containsKey(args[0])) {
                                    rankID = args[0];
                                }
                                openSkinBook(player);
                            } else if (args[0].equalsIgnoreCase("skin")) {
                                if(args[1].equalsIgnoreCase("real")){
                                    changeSkin(player, player.getName());
                                    skinOption = "real";
                                } else if (args[1].equalsIgnoreCase("default")) {
                                    changeSkin(player, null);
                                    skinOption = "default";
                                } else if(args[1].equalsIgnoreCase("random")){
                                    GameProfile profile = ((CraftPlayer) player).getHandle().getGameProfile();

                                    List<String> values = FoxRank.getInstance().getConfig().getStringList("RandomSkinValueList");
                                    List<String> signatures = FoxRank.getInstance().getConfig().getStringList("RandomSkinSignatureList");
                                    if(signatures.size() == values.size()) {
                                        int rnd = new Random().nextInt(values.size()-1) +1;
                                        String value = values.get(rnd);
                                        String signature = signatures.get(rnd);
                                        profile.getProperties().removeAll("textures");
                                        profile.getProperties().put("textures", new Property("textures", value, signature));
                                        //refreshPlayer(player);

                                        skinOption = "random";
                                    } else {
                                        Bukkit.getLogger().log(Level.SEVERE, "Values of RandomSkinSignatureList and RandomSkinValueList are not identical in size. This is NOT a problem with the plugin. It is a configuration eror.");
                                        changeSkin(player, null);
                                    }
                                }
                                openNameBook(player);
                            }
                        }
                    } else {
                        FoxRank.getInstance().sendNoPermissionMessage(FoxRank.getInstance().getConfig().getInt("NicknamePermissions"), rp);
                    }
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('§', FoxRank.getInstance().getConfig().getString("CommandDisabledMessage")));
            }
        }
        return false;
    }
}
