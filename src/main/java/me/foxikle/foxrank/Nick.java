package me.foxikle.foxrank;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class Nick implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("nick")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("nick.use")) {
                    if (args.length == 0) {
                        openWarningBook(player);

                    } else if(args.length == 1){
                        if (args[0].equals("RANDOM")) {
                            changeName("RANDOM", player);
                            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                            yml.set("isNicked", true);
                            yml.set("Nickname", "RANDOM");
                            try {
                                yml.save(file);
                            } catch (IOException error) {
                                error.printStackTrace();
                            }
                        } else if (args[0].equalsIgnoreCase("set")) {
                            createAnvil(player);
                        } else if (args[0].equalsIgnoreCase("reset")) {
                            System.out.println(player.getUniqueId() + "Tried to reset nickname. This is: " + player.getDisplayName());

                            URL url = null;
                            try {
                                url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId() + "?unsigned=false");
                                InputStreamReader reader = new InputStreamReader(url.openStream());
                                String realName = new JsonParser().parse(reader).getAsJsonObject().get("name").getAsString();
                                changeSkin(player, realName);
                                changeName(realName, player);
                                refreshPlayer(player);
                            } catch (IOException e) {
                                Bukkit.getLogger().log(Level.SEVERE, "Cannot get a player's name form Mojang");
                            }

                            File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                            yml.set("isNicked", false);
                            try {
                                yml.save(file);
                            } catch (IOException error) {
                                error.printStackTrace();
                            }
                        } else if (args[0].equals("agree")) {
                            openRankBook(player);
                        }
                    } else if (args.length >= 2) {
                        if (args[0].equalsIgnoreCase("rank")) {
                            if (args[1].equals("DEFAULT")) {
                                File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                                yml.set("Nickname-Rank", Rank.DEFAULT.getRankID());
                                try {
                                    yml.save(file);
                                } catch (IOException error) {
                                    error.printStackTrace();
                                }
                                openNameBook(player);
                            } else if (args[1].equals("VIP")) {
                                File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                                yml.set("Nickname-Rank", Rank.VIP.getRankID());
                                try {
                                    yml.save(file);
                                } catch (IOException error) {
                                    error.printStackTrace();
                                }
                                openNameBook(player);
                            } else if (args[1].equals("VIP_PLUS")) {
                                File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                                yml.set("Nickname-Rank", Rank.VIP_PLUS.getRankID());
                                try {
                                    yml.save(file);
                                } catch (IOException error) {
                                    error.printStackTrace();
                                }
                                openNameBook(player);
                            } else if (args[1].equals("MVP")) {
                                File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                                yml.set("Nickname-Rank", Rank.MVP.getRankID());
                                try {
                                    yml.save(file);
                                } catch (IOException error) {
                                    error.printStackTrace();
                                }
                                openNameBook(player);
                            } else if (args[1].equals("MVP_PLUS")) {
                                File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                                yml.set("Nickname-Rank", Rank.MVP_PLUS.getRankID());
                                try {
                                    yml.save(file);
                                } catch (IOException error) {
                                    error.printStackTrace();
                                }
                                openNameBook(player);
                            }
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have the suitable permissions to use this command.");
                }
            }
            return true;
        }
        return false;
    }

    public static void openRankBook(Player player) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();


        TextComponent textCompnent = new TextComponent("Let's get you set up  \nwith your nickname! \nFirst, you'll need to \nchoose which" + ChatColor.BOLD + " RANK " + ChatColor.RESET + "you would like to be \nshown as when nicked.");
        textCompnent.setColor(ChatColor.BLACK);

        TextComponent defaultComponent = new TextComponent("\n\n »" + ChatColor.GRAY + " DEFAULT");
        defaultComponent.setColor(ChatColor.BLACK);
        defaultComponent.setColor(ChatColor.UNDERLINE);
        defaultComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick rank DEFAULT"));
        defaultComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to chose DEFAULT").color(ChatColor.GREEN).create()));

        TextComponent VIPComment = new TextComponent("\n » " + ChatColor.GREEN + "VIP");
        VIPComment.setColor(ChatColor.BLACK);
        VIPComment.setColor(ChatColor.UNDERLINE);
        VIPComment.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick rank VIP"));
        VIPComment.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to chose VIP").color(ChatColor.GREEN).create()));

        TextComponent VIPPlusComponent = new TextComponent("\n »" + ChatColor.GREEN + " VIP" + ChatColor.GOLD + "+");
        VIPPlusComponent.setColor(ChatColor.BLACK);
        VIPPlusComponent.setColor(ChatColor.UNDERLINE);
        VIPPlusComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick rank VIP_PLUS"));
        VIPPlusComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to chose VIP+").color(ChatColor.GREEN).create()));

        TextComponent MVPComponent = new TextComponent("\n »" + ChatColor.AQUA + " MVP");
        MVPComponent.setColor(ChatColor.BLACK);
        MVPComponent.setColor(ChatColor.UNDERLINE);
        MVPComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick rank MVP"));
        MVPComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to chose MVP").color(ChatColor.GREEN).create()));

        TextComponent MVPPlusComponent = new TextComponent("\n »" + ChatColor.AQUA + " MVP" + ChatColor.RED + "+");
        MVPPlusComponent.setColor(ChatColor.BLACK);
        MVPPlusComponent.setColor(ChatColor.UNDERLINE);
        MVPPlusComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick rank MVP_PLUS"));
        MVPPlusComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to chose MVP+").color(ChatColor.GREEN).create()));

        textCompnent.addExtra(defaultComponent);
        textCompnent.addExtra(VIPComment);
        textCompnent.addExtra(VIPPlusComponent);
        textCompnent.addExtra(MVPComponent);
        textCompnent.addExtra(MVPPlusComponent);
        BaseComponent[] pages = new BaseComponent[]{textCompnent};

        meta.spigot().addPage(pages);

        meta.setTitle("Nickname Book");
        meta.setAuthor("Emperical");
        book.setItemMeta(meta);
        book.setItemMeta(meta);

        player.openBook(book);

    }

    public static void openWarningBook(Player player) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();


        TextComponent textCompnent = new TextComponent("Nicknames allow you to\n play with a different\n username to not get \nrecognized. \n\nAll rules still apply. \nYou can still be reported abd all name \nhistory is stored.");
        textCompnent.setColor(ChatColor.BLACK);

        TextComponent continueComponent = new TextComponent(ChatColor.BOLD + "\n\n » " + ChatColor.RESET + "" + ChatColor.BLACK + "I understand,\n setup my nickname.");
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

    public static void openNameBook(Player player) {

        ItemStack nickbook1 = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta nickBookMeta1 = (BookMeta) nickbook1.getItemMeta();


        TextComponent rndmMsg = new TextComponent("\nRANDOM NICKNAME");
        rndmMsg.setColor(ChatColor.LIGHT_PURPLE);
        rndmMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick RANDOM"));
        rndmMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click for a random nickname").color(ChatColor.GREEN).create()));

        TextComponent setMsg = new TextComponent("\n SET NICKNAME \n \n");
        setMsg.setColor(ChatColor.RED);
        setMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick set"));
        setMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click for a custom nickname").color(ChatColor.GREEN).create()));


        setMsg.addExtra(rndmMsg);
        BaseComponent[] pages = new BaseComponent[]{setMsg};

        nickBookMeta1.spigot().addPage(pages);

        nickBookMeta1.setTitle("Nickname Book");
        nickBookMeta1.setAuthor("Emperical");
        nickbook1.setItemMeta(nickBookMeta1);
        nickbook1.setItemMeta(nickBookMeta1);
        player.openBook(nickbook1);

    }

    public static void changeName(String name, Player player) {
        if (name.length() <= 16) {
            GameProfile profile = ((CraftPlayer) player).getHandle().getGameProfile();

            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", getSkin(name));

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

            refreshPlayer(player);

            player.sendMessage(ChatColor.GREEN + "Your nickname has been set to " + ChatColor.BOLD + name);
        } else {
            player.sendMessage(ChatColor.RED + "Your nickname cannot be longer than 16 characters");
        }
    }

    public static void changeSkin(Player player, String skin) {
        GameProfile profile = ((CraftPlayer) player).getHandle().getGameProfile();

        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", getSkin(skin));
        refreshPlayer(player);
    }

    public static void refreshPlayer(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
            connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
            p.hidePlayer(FoxRank.getInstance(), player);
            p.showPlayer(FoxRank.getInstance(), player);
            connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
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
            System.out.println("Name was valid");
            return new Property("textures", value, signature);
        } catch (Exception e) {
            System.out.println("Name was invalid");

            return new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMjAwNzA2NTgyNiwKICAicHJvZmlsZUlkIiA6ICJjOTAzOGQzZjRiMTg0M2JiYjUwNTU5ZGE3MWFjMTk2MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUQk5SY29vbGNhdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYjA1ZjJlYjM1MmY2YzJlNWM5MThjNzQ4MjU2ZDIzMWFjNzEyODE5NDhlNWFmNzRkMTQ2YTg4ZDc3NTBmNDkwIgogICAgfQogIH0KfQ==", "YpxtT6K8I3+nVvc2fr0m3CLyi2FcuMCIFNK7Z233P0LUWdTlmvQazHSXbpGe2LWBwGAF3snKry9UAuPSYHWWfJ1ygOWrqeyDeM3JT6Cv+QAmqFj3NJZbXF2NP9a1csH2v8hgQvvhJV1hLukGTu301zQnKBiZoYk8tKuFbfqwIXdKevyDoc0dTo9P91O7ZychEFOjKiEWbetQWhOpzwzGOnaynToeC8WkSvoQ3vzuhtEx3emjVzcGGGozkeGTygbeny9kDtBGXzBQJ7uEui8XtaXRwSoQj2cUMQ0KNsQNRNdo9I1BymYvxxqxJtc8AnW2ubccXMxWlABNIGgX5mrbKMOlRa/y1y/zDK1hbA9beQUm7ljP38O4eMUrFkAYkNNoOnFQrmAddofhpqDtJPwSk/rYlALl61qPqk3t9xKcR2b3Vi/gnV/r8pG0B3oo3KFZVJ6qzXVE/rmh/bfRL5HMJX5lZ+NCCvi3eo9ckJjQDdHOo8fgvAxvwBqNvdHCceioR7XgWOpAFr0Ns6MNsJIYFoiMscQQj0OI694MdtOnmQSTuozlm/oBxObiFfR4fsOW3oH2xS/HzrF3S6U3ydY0AmpBvEv7IJOJEwOoHFd2kNKnD7vOT0+jllk9D06dnB0euDiuIhRZgY6d2UXoeR/bFxD3XLndjuG7oBZcFxq0+18=");

        }
    }

    public static void createAnvil(Player p) {
        new AnvilGUI.Builder()
                .onClose(player -> {
                })
                .onComplete((player, text) -> {

                    File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    yml.set("isNicked", true);
                    yml.set("Nickname", text);
                    finalizeNick(player, text);
                    try {
                        yml.save(file);
                    } catch (IOException error) {
                        error.printStackTrace();
                    }
                    return AnvilGUI.Response.close();
                })
                .preventClose()
                .text(p.getName())
                .itemLeft(new ItemStack(Material.NAME_TAG))
                .onLeftInputClick(player -> player.sendMessage(" "))
                .onRightInputClick(player -> player.sendMessage(" "))
                .title("Enter your nickname.")
                .plugin(FoxRank.getInstance())
                .open(p);
    }

    private static void finalizeNick(Player player, String name){
        File file = new File("plugins/FoxRank/PlayerData/" + player.getUniqueId() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("isNicked", true);
        try{
            yml.save(file);
        } catch (IOException error){
            error.printStackTrace();
        }
        changeName(name, player);
        changeSkin(player, name);

        FoxRank.setTeam(player, yml.getString("Nickname-Rank"));

    }
}
