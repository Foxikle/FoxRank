package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

public class RankedPlayer implements CustomPlayer {
    private final Player player;
    private final String rankID;
    private final int pwrlvl;
    private final FoxRank instance;


    public RankedPlayer(Player player, String rankID, FoxRank instance) {
        this.player = player;
        this.rankID = rankID;
        this.instance = instance;
        this.pwrlvl = FoxRank.getInstance().powerLevels.get(rankID);
    }

    public RankedPlayer(Player player, FoxRank instance) {
        this.player = player;
        this.rankID = instance.getRank(player) == null ? instance.getDefaultRank().getId() : instance.getRank(player).getId();
        this.instance = instance;
        this.pwrlvl = instance.getRank(player) == null ? instance.getDefaultRank().getPowerlevel() : instance.getRank(player).getPowerlevel();
    }

    /**
     * Get the Player's name
     *
     * @return String The player's name
     **/
    public String getName() {
        return instance.getTrueName(player.getUniqueId());
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
     * Get the Player object
     *
     * @return Player the player instance
     **/

    public Player getPlayer() {
        return Bukkit.getPlayerExact(this.getName());
    }

    /**
     * Gets the player's rank's prefix.
     *
     * @return String the player's rank's prefix.
     **/
    @Override
    public String getPrefix() {
        return Rank.of(rankID).getPrefix();
    }

    /**
     Gets the player's power level.

     @return int The player's power level.
     **/
    @Override
    public int getPowerLevel() {
        return this.pwrlvl;
    }

    /**
     Get if the player is nicked

     @return boolean if the player is nicked
     **/
    @Override
    public boolean isNicked() {
        return instance.dm.isNicked(getPlayer().getUniqueId());
    }

    /**
     Get if the player is vanished

     @return boolean if the player is vanished
     **/
    @Override
    public boolean isVanished() {
        return instance.dm.isVanished(getPlayer().getUniqueId());
    }

    /**
     Get if the player is muted

     @return boolean if the player is muted
     **/
    @Override
    public boolean isMuted() {
        return instance.dm.isMuted(getPlayer().getUniqueId());
    }

    /**
     *
     */
    @Override
    public String getNickname() {
        return instance.dm.getNickname(player.getUniqueId());
    }

    /**
     * Get the player's RankID.
     *
     * @return String the player's current RankID
     **/
    @Override
    public String getRankId() {
        return instance.getRank(this.player).getId();
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
     * Gets the player's last mute reason.
     * NOTE: This is LAST mute reason, even if the player is NOT muted.
     * @return String last mute reason
     **/
    @Override
    public String getMuteReason() {
        return instance.dm.getMuteReason(player.getUniqueId());
    }

    /**
     * Gets the player's last mute duration.
     * NOTE: This is LAST mute duration, even if the player is NOT muted.
     * @return Instant last mute duration
     **/
    @Override
    public Instant getMuteDuration() {
        return instance.dm.getMuteDuration(player.getUniqueId());
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
     * Gets a formatted string of when the player's mute will expire.
     * If the player is not muted will return `0s`.
     *
     * @return String Formatted until mute expires.
     */
    @Override
    public String getFormattedMuteDuration() {
        return instance.getFormattedExpiredString(this.getMuteDuration(), Instant.now());
    }

    /**
     * @return
     */
    @Override
    public Rank getRank() {
        return Rank.of(this.rankID);
    }

    /**
     * @param rankID
     */
    @Override
    public void setRank(String rankID) {

    }

    /**
     * @param rank
     */
    @Override
    public void setRank(Rank rank) {

    }

    /**
     * Sends the player a message of the provided content
     *
     * @param content String to send to the player.
     **/
    public void sendMessage(String content) {
        this.player.sendMessage(content);
    }
}