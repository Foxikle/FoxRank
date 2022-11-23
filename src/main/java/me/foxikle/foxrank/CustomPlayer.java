package me.foxikle.foxrank;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

public interface CustomPlayer{

    public Player getPlayer();
    public Rank getRank();
    public void setRank(Rank rank);
    public String getPrefix();
    public int getPowerLevel();
    public boolean isNicked();
    public boolean isVanished();
    public boolean isMuted();

    public UUID getUniqueId();
    public void mutePlayer(Instant duration, String reason);
    public void unmutePlayer();

    public String getRankId();
    public String getName();
    public String getDisplayName();
    public Location getLocation();
    public String getMuteReason();

    public Instant getMuteDuration();
    public String getFormattedMuteDuration();
}
