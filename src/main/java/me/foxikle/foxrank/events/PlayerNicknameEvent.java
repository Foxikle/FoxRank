package me.foxikle.foxrank.events;

import me.foxikle.foxrank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerNicknameEvent extends Event {
    private final Player player;
    private final String newNick;
    private final Rank rank;

    public PlayerNicknameEvent(Player player, String newNick, Rank rank) {
        this.player = player;
        this.newNick = newNick;
        this.rank = rank;
    }

    /**
     * <p> Gets the new nickname of the player.
     * This event is called before the name is changed.
     * </p>
     *
     * @return the nickname
     * @since 1.8.1
     */
    public String getNickname() {
        return this.newNick;
    }

    /**
     * <p> Gets the player who changed their nickname.
     * </p>
     *
     * @return the player
     * @since 1.8.1
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * <p> Gets the player's Rank
     * </p>
     *
     * @return the rank of player
     * @see Rank Please see the {@link Rank}
     * @since 1.8.1
     */
    public Rank getRank() {
        return this.rank;
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
