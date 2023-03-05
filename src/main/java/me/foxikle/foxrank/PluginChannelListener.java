package me.foxikle.foxrank;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;

public class PluginChannelListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, @Nonnull Player player, @Nonnull byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equals("PlayerList")) {
            String server = in.readUTF();
            String[] playerList = in.readUTF().split(", ");
        }
    }

    public void getPlayers(Player executer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");
        executer.sendPluginMessage(FoxRank.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void kickPlayer(Player executor, Player toBeKicked, String reason) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("KickPlayer");
        out.writeUTF(toBeKicked.getName());
        out.writeUTF(reason);
    }
}