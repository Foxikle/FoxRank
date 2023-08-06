package me.foxikle.foxrank.events;

import me.foxikle.foxrank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public class ModerationActionEvent extends Event {
    private final Player player;
    private final Player executor;
    private final Rank rank;
    private final Rank executorRank;
    private final ModerationAction action;
    private final String reason;
    private final Instant duration;
    private final String id;

    public ModerationActionEvent(@Nonnull Player player, @Nullable Player executor, @Nonnull Rank rank, @Nullable Rank executorRank, @Nonnull ModerationAction action, @Nullable String reason, @Nullable Instant duration, @Nullable String id) {
        this.player = player;
        this.executor = executor;
        this.executorRank = executorRank;
        this.rank = rank;
        this.action = action;
        this.reason = reason;
        this.duration = duration;
        this.id = id;
    }

    /**
     * <p> Gets the player who moderated.
     * </p>
     *
     * @return the player may be null
     * @since 1.8.1
     */
    @Nullable
    public Player getExecutor() {
        return this.executor;
    }

    /**
     * <p> Gets the rank of the player
     * </p>
     *
     * @return the rank of player
     * @see Rank Please see the {@link Rank}
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
     * @see Rank Please see the {@link Rank}
     * @since 1.9.5
     */
    public Rank getRank() {
        return this.rank;
    }

    /**
     * <p> Gets the reason provided
     * </p>
     *
     * @return String the reason provided in the command arguments
     * @since 1.9.3
     */
    @Nullable
    public String getReason() {
        return reason;
    }

    /**
     * <p> Gets id generated
     * </p>
     *
     * @return String the id generated in the processing of the action
     * @since 1.9.3
     */
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * <p> Gets the expiry date
     * </p> returns null if not applicable. (unban, unmute)
     *
     * @return Instant the expiry date of the action
     * @since 1.9.3
     */
    @Nullable
    public Instant getDuration() {
        return duration;
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

