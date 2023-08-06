package me.foxikle.foxrank;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.Arrays;

public class PluginChannelListener implements PluginMessageListener {

    public static String getServer(Player p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        p.sendPluginMessage(FoxRank.getInstance(), "BungeeCord", out.toByteArray());
        ByteArrayDataInput in = ByteStreams.newDataInput("message".getBytes());
        String server = in.readUTF();
        return server;
    }

    public void getPlayers(Player executer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");
        executer.sendPluginMessage(FoxRank.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, @Nonnull Player player, @Nonnull byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equals("PlayerList")) {
            String server = in.readUTF();
            String[] playerList = in.readUTF().split(", ");
            FoxRank.getInstance().playerNames = Arrays.stream(playerList).toList();
            Bukkit.getScheduler().runTaskLaterAsynchronously(FoxRank.getInstance(), () -> getPlayers(Iterables.getFirst(Bukkit.getOnlinePlayers(), null)), 20);

        } else if (subChannel.equalsIgnoreCase("FoxRankUpdateData")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String somedata = msgin.readUTF();
                if (somedata.equals("updateData")) {
                    Bukkit.getScheduler().runTaskAsynchronously(FoxRank.getInstance(), () -> FoxRank.getInstance().setRank(player, FoxRank.getInstance().getDm().getStoredRank(player.getUniqueId())));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void kickPlayer(Player executor, String toBeKicked, String reason) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("KickPlayer");
        out.writeUTF(toBeKicked);
        out.writeUTF(reason);
        executor.sendPluginMessage(FoxRank.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void sendMessage(Player executor, String toBeMessaged, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Message");
        out.writeUTF(toBeMessaged);
        out.writeUTF(message);
        executor.sendPluginMessage(FoxRank.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void sendUpdateDataMessage(String player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ForwardToPlayer");
        out.writeUTF(player);
        out.writeUTF("FoxRankUpdateData");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF("updateData");
            msgout.writeShort("updateData".getBytes().length);
        } catch (IOException cannotHappen) {
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(FoxRank.getInstance(), "BungeeCord", out.toByteArray());

    }
}