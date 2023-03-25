package me.foxikle.foxrank.events;

import me.foxikle.foxrank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ModerationActionEvent extends Event {
    private final Player player;
    private final Player executor;
    private final Rank rank;
    private final Rank executorRank;
    private final ModerationAction action;

    public ModerationActionEvent(Player player, Player executor, Rank rank, Rank executorRank, ModerationAction action) {
        this.player = player;
        this.executor = executor;
        this.executorRank = executorRank;
        this.rank = rank;
        this.action = action;
    }

    /**
     * <p> Gets the player who moderated.
     * </p>
     *
     * @return the player
     * @since 1.8.1
     */
    public Player getExecutor() {
        return this.executor;
    }

    /**
     * <p> Gets the rank of the player
     * </p>
     *
     * @return the rank of player
     * @see Rank Please see the {@link me.foxikle.foxrank.Rank}
     * @since 1.8.1
     */
    public Rank getExecutorRank() {
        return this.executorRank;
    }

    /**
     * <p> Gets the player who was moderated.
     * </p>
     *
     * @return the player
     * @since 1.8.1
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * <p> Gets the rank of the moderated player
     * </p>
     *
     * @return the rank of moderated player
     * @see Rank Please see the {@link me.foxikle.foxrank.Rank}
     * @since 1.8.1
     */
    public Rank getRank() {
        return this.rank;
    }

    /**
     * <p> Gets the type of action performed
     * </p>
     *
     * @return the type of moderation action performed.
     * @see ModerationAction Please see the {@link me.foxikle.foxrank.events.ModerationAction}
     * @since 1.8.1
     */
    public ModerationAction getAction() {
        return action;
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

