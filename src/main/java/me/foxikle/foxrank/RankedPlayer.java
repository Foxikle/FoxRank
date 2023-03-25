package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

public class RankedPlayer implements CustomPlayer{
    private final Player player;
    private final Rank rank;

    public RankedPlayer(Player player) {
        this(player, FoxRank.getRank(player));
    }

    public RankedPlayer(Player player, Rank rank) {
        this.player = player;
        this.rank = rank;
    }

    /**
     Get the Player's name

     @return String The player's name
     **/
    public String getName() {
        return FoxRank.getInstance().getTrueName(player.getUniqueId());
    }

    /**
     Get the Player's current Display-Name

     @return String The Player's currently assigned display name
     **/
    public String getDisplayName() {
        return this.getPlayer().getDisplayName();
    }

    /**
     Checks the Player's online-status

     @return boolean true if online otherwise false
     **/

    public boolean isOnline() {
        return Bukkit.getOfflinePlayer(this.player.getUniqueId()).isOnline();
    }

    /**
     Get the Player object

     @return Player the player instance
     **/

    public Player getPlayer() {
        return Bukkit.getPlayerExact(this.getName());
    }
    /**
     Get the player's rank

     @return rank get the player's rank
     **/

    @Override
    public Rank getRank() {
        return this.rank;
    }
    /**
     Sets the player's rank
     @param rank Rank to set the player's rank
     **/

    @Override
    public void setRank(Rank rank) {
        FoxRank.instance.setRank(this.getPlayer(), rank);
    }

    /**
     Gets the player's rank's prefix.

     @return String the player's rank's prefix.
     **/
    @Override
    public String getPrefix() {
        return this.rank.getPrefix();
    }

    /**
     Gets the player's power level.

     @return int The player's power level.
     **/
    @Override
    public int getPowerLevel() {
        return this.rank.getPowerLevel();
    }

    /**
     Get if the player is nicked

     @return boolean if the player is nicked
     **/
    @Override
    public boolean isNicked() {
        return FoxRank.getInstance().isNicked(getPlayer().getUniqueId());
    }

    /**
     Get if the player is vanished

     @return boolean if the player is vanished
     **/
    @Override
    public boolean isVanished() {
        return FoxRank.instance.isVanished(getPlayer().getUniqueId());
    }

    /**
     Get if the player is muted

     @return boolean if the player is muted
     **/
    @Override
    public boolean isMuted() {
        return FoxRank.instance.isMuted(getPlayer().getUniqueId());
    }

    /**
     Get the player's UUID

     @return UUID the player's UUID
     **/
    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * Mutes a player for the specified duration and reason.
     *
     * @param duration Duration to be muted for.
     * @param staff    The player who's executing the mute.
     * @param reason   Reason to mute the player.
     **/
    @Override
    public void mutePlayer(RankedPlayer staff, Instant duration, String reason) {
        ModerationAction.mutePlayer(this, duration, reason, staff);
    }

    /**
     * Unmutes the player.
     *
     * @param staff The player who's executing the unmute
     **/
    @Override
    public void unmutePlayer(RankedPlayer staff) {
        ModerationAction.unmutePlayer(this, staff);
    }

    /**
     *
     */
    @Override
    public String getNickname() {
        return FoxRank.getInstance().getNickname(player.getUniqueId());
    }

    /**
     * Get the player's RankID.
     *
     * @return String the player's current RankID
     **/
    @Override
    public String getRankId() {
        return FoxRank.getRank(this.player).getRankID();
    }


    /**
     * Get the player's current location
     *
     * @return Location the current player's location
     **/
    public Location getLocation() {
        return this.getPlayer().getLocation();
    }

    /**
     * Gets the player's last mute reason.
     * NOTE: This is LAST mute reason, even if the player is NOT muted.
     * @return String last mute reason
     **/
    @Override
    public String getMuteReason() {
        return FoxRank.getInstance().getMuteReason(player.getUniqueId());
    }

    /**
     * Gets the player's last mute duration.
     * NOTE: This is LAST mute duration, even if the player is NOT muted.
     * @return Instant last mute duration
     **/
    @Override
    public Instant getMuteDuration() {
        return FoxRank.instance.getMuteDuration(player.getUniqueId());
    }

    /**Gets a formatted string of when the player's mute will expire.
     * If the player is not muted will return `0s`.
     * @return String Formatted until mute expires.
     */
    @Override
    public String getFormattedMuteDuration() {
        return FoxRank.getInstance().getFormattedExpiredString(this.getMuteDuration(), Instant.now());
    }

    /**
     * Sends the player a message of the provided content
     *
     * @param content String to send to the player.
     **/
    public void sendMessage(String content){
        this.player.sendMessage(content);
    }
}