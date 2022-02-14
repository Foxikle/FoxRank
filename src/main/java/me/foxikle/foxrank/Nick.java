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
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.InputStreamReader;
import java.net.URL;

import static org.bukkit.entity.EntityType.ARMOR_STAND;
import static org.bukkit.entity.EntityType.SILVERFISH;

public class Nick implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("nick")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("nick.use")) {
                    if(args.length == 0) {
                        openBook(player);
                    } else {

                        if (args[0].equals("098098098882822668842231536845")){
                            changeName("RANDOM", player);
                        } else if(args[0].equals("98164375184972654831645721981")){
                            createAnvil(player);
                            player.sendMessage(ChatColor.RED + "Opening Anvil!");
                        }
                    }
                    player.sendMessage(ChatColor.GOLD + "Nicking!!");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have the suitable permissions to use this command.");
                }
            }
            return true;
        }
        return false;
    }
    public static void openBook(Player player){

        ItemStack nickbook1 = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta nickBookMeta1 = (BookMeta) nickbook1.getItemMeta();


        TextComponent rndmMsg = new TextComponent("\nRANDOM NICKNAME");
        rndmMsg.setColor(ChatColor.LIGHT_PURPLE);
        rndmMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick 098098098882822668842231536845"));
        rndmMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click for a random nickname").color(ChatColor.GREEN).create()));

        TextComponent setMsg = new TextComponent("\n SET NICKNAME \n \n");
        setMsg.setColor(ChatColor.RED);
        setMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick 98164375184972654831645721981"));
        setMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click for a custom nickname").color(ChatColor.GREEN).create()));


        setMsg.addExtra(rndmMsg);
        BaseComponent[] pages = new BaseComponent[]{setMsg};

        nickBookMeta1.spigot().addPage(pages);
       // pages.add(SetMsg);

        nickBookMeta1.setTitle("Nickname Book");
        nickBookMeta1.setAuthor("Imperical");
        nickbook1.setItemMeta(nickBookMeta1);
        nickbook1.setItemMeta(nickBookMeta1);
        player.openBook(nickbook1);

    }

    public static void changeName(String name, Player player) {
        GameProfile profile = ((CraftPlayer)player).getHandle().getGameProfile();

        /*LivingEntity d = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), SILVERFISH);
        d.setInvisible(true);
        d.setSilent(true);

        ArmorStand e = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), ARMOR_STAND);
        e.setMarker(true);
        e.setVisible(false);
        e.setCustomName(name);
        e.setCustomNameVisible(true);

        player.setPassenger(d);
        d.setPassenger(e);
         */
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", getSkin(name, player));

        player.setDisplayName(name);
        player.setPlayerListName(name);
        player.setCustomName(name);
        player.sendMessage(ChatColor.GREEN + "Your nickname has been set to " + ChatColor.BOLD + name);

        for (Player p : Bukkit.getOnlinePlayers()) {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
            System.out.println("connection established");
            connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()));
            p.hidePlayer(FoxRank.getInstance(), player);
            p.showPlayer(FoxRank.getInstance(), player);
            System.out.println("removed player");
            connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, ((CraftPlayer)player).getHandle()));
            System.out.println("added player back");

        }


    }

    private static Property getSkin( String name, Player player){
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
        } catch (Exception e){
            System.out.println("Name was invalid");

            return new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMjAwNzA2NTgyNiwKICAicHJvZmlsZUlkIiA6ICJjOTAzOGQzZjRiMTg0M2JiYjUwNTU5ZGE3MWFjMTk2MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUQk5SY29vbGNhdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYjA1ZjJlYjM1MmY2YzJlNWM5MThjNzQ4MjU2ZDIzMWFjNzEyODE5NDhlNWFmNzRkMTQ2YTg4ZDc3NTBmNDkwIgogICAgfQogIH0KfQ==", "YpxtT6K8I3+nVvc2fr0m3CLyi2FcuMCIFNK7Z233P0LUWdTlmvQazHSXbpGe2LWBwGAF3snKry9UAuPSYHWWfJ1ygOWrqeyDeM3JT6Cv+QAmqFj3NJZbXF2NP9a1csH2v8hgQvvhJV1hLukGTu301zQnKBiZoYk8tKuFbfqwIXdKevyDoc0dTo9P91O7ZychEFOjKiEWbetQWhOpzwzGOnaynToeC8WkSvoQ3vzuhtEx3emjVzcGGGozkeGTygbeny9kDtBGXzBQJ7uEui8XtaXRwSoQj2cUMQ0KNsQNRNdo9I1BymYvxxqxJtc8AnW2ubccXMxWlABNIGgX5mrbKMOlRa/y1y/zDK1hbA9beQUm7ljP38O4eMUrFkAYkNNoOnFQrmAddofhpqDtJPwSk/rYlALl61qPqk3t9xKcR2b3Vi/gnV/r8pG0B3oo3KFZVJ6qzXVE/rmh/bfRL5HMJX5lZ+NCCvi3eo9ckJjQDdHOo8fgvAxvwBqNvdHCceioR7XgWOpAFr0Ns6MNsJIYFoiMscQQj0OI694MdtOnmQSTuozlm/oBxObiFfR4fsOW3oH2xS/HzrF3S6U3ydY0AmpBvEv7IJOJEwOoHFd2kNKnD7vOT0+jllk9D06dnB0euDiuIhRZgY6d2UXoeR/bFxD3XLndjuG7oBZcFxq0+18=");

        }
    }
    public static void createAnvil(Player p){
        new AnvilGUI.Builder()
                .onClose(player -> {
                })
                .onComplete((player, text) -> {
                    changeName(text, player);
                    return AnvilGUI.Response.close();
                })
                .preventClose()
                .text(p.getName())
                .itemLeft(new ItemStack(Material.SHEARS))
                .itemRight(new ItemStack(Material.SHEARS))
                .onLeftInputClick(player -> player.sendMessage(" "))
                .onRightInputClick(player -> player.sendMessage(" "))
                .title("Enter your nickname.")
                .plugin(FoxRank.getInstance())
                .open(p);
    }
}
