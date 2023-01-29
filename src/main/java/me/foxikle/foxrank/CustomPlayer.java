package me.foxikle.foxrank;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

interface CustomPlayer {

    Player getPlayer();

    Rank getRank();

    void setRank(Rank rank);

    String getPrefix();

    int getPowerLevel();

    boolean isNicked();

    boolean isVanished();

    boolean isMuted();

    UUID getUniqueId();

    void mutePlayer(RankedPlayer staff, Instant duration, String reason);

    void unmutePlayer(RankedPlayer staff);

    @Deprecated
    void mutePlayer(Instant duration, String reason);

    @Deprecated
    void unmutePlayer();

    String getNickname();

    String getRankId();

    String getName();

    String getDisplayName();

    Location getLocation();

    String getMuteReason();

    Instant getMuteDuration();

    String getFormattedMuteDuration();
}
