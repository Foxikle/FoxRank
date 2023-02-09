package me.foxikle.foxrank.events;

import me.foxikle.foxrank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankChangeEvent extends Event {
    private final Player player;
    private final Rank rank;

    public RankChangeEvent(Player player, Rank rank) {
        this.player = player;
        this.rank = rank;
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
     * <p> Gets the rank of the player
     * </p>
     *
     * @return the rank of player
     * @see Rank Please see the {@link me.foxikle.foxrank.Rank}
     * @since 1.8.1
     */
    public Rank getRank() {
        return rank;
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
