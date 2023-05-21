package me.foxikle.foxrank.events;

import me.foxikle.foxrank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankChangeEvent extends Event {
    private final Player player;
    private final Rank newRank;
    private final Rank oldRank;

    public RankChangeEvent(Player player, Rank newRank, Rank oldRank) {
        this.player = player;
        this.newRank = newRank;
        this.oldRank = oldRank;
    }

    /**
     * <p> Gets the player who' rank changed.
     * </p>
     *
     * @return the player
     * @since 1.8.1
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * <p> Gets the new rank of the player
     * </p>
     *
     * @return the new rank of player
     * @see Rank Please see the {@link me.foxikle.foxrank.Rank}
     * @since 1.8.1
     */
    public Rank getNewRank() {
        return newRank;
    }

    /**
     * <p> Gets the old rank of the player
     * </p>
     *
     * @return the old rank of player
     * @see Rank Please see the {@link me.foxikle.foxrank.Rank}
     * @since 1.9.3
     */
    public Rank getOldRank() {
        return oldRank;
    }

    /**
     * getHandlers
     *
     * @return HandlerList the handlers of this event.
     */
    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }
}
