package me.foxikle.foxrank;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

public interface OfflineCustomPlayer {
    public OfflinePlayer getOfflinePlayer();
    public Rank getRank();
    public void setRank(Rank rank);
    public String getPrefix();
    public int getPowerLevel();
    public boolean isNicked();
    public boolean isVanished();
    public boolean isMuted();
    public UUID getUniqueId();
    public void mutePlayer(RankedPlayer staff, Instant duration, String reason);
    public void unmutePlayer(RankedPlayer staff);
    public String getRankId();
    public String getName();
    public String getNickname();
    public String getMuteReason();
    public Instant getMuteDuration();
    public String getFormattedMuteDuration();
    public boolean isBanned();
}
