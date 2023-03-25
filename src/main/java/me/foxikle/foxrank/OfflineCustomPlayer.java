package me.foxikle.foxrank;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.UUID;

public interface OfflineCustomPlayer {
    OfflinePlayer getOfflinePlayer();

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

    String getRankId();

    String getName();

    String getNickname();

    String getMuteReason();

    Instant getMuteDuration();

    String getFormattedMuteDuration();

    boolean isBanned();
}
