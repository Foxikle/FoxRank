package me.foxikle.foxrank;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.InputStreamReader;
import java.net.URL;

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

        TextComponent setMsg = new TextComponent("\nSET NICKNAME");
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

        GameProfile gameProfile = new GameProfile(player.getUniqueId(), name);
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((CraftWorld) player.getWorld()).getHandle();
        ServerPlayer nickedPlayer = new ServerPlayer(server, world, gameProfile);

        player.setDisplayName(name);
        player.setPlayerListName(name);
        player.setCustomName(name);
        player.sendMessage(ChatColor.GREEN + "Your nickname has been set to " + ChatColor.BOLD + name);
// packet is the new instance of PacketPlayOutPlayerInfo
// infoList is the list retrieved from PacketPlayOutPlayerInfo
// playerInfoDataConstr is the PacketPlayOutPlayerInfo constructor
// ping is the amount of ping the player have currently
// gameMode is the EnumGameMode object of the player
// text is the text parsed to IChatBaseComponent

        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()));
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, nickedPlayer));

    }
    private static String[] getSkin(Player player, String name){
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());

            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            String[] text = new String[] {texture, signature};
            return new String[] {texture, signature};
        } catch (Exception e){
            e.printStackTrace();
            return new String[] {null, null};
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
