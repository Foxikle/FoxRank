package me.foxikle.foxrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.time.Instant;
import java.util.UUID;

public class OfflineRankedPlayer implements OfflineCustomPlayer{
    private final OfflinePlayer offlinePlayer;
    private final Rank rank;
    private final YamlConfiguration yml;
    public OfflineRankedPlayer(OfflinePlayer player) throws NullPointerException {
        if (player == null || !player.hasPlayedBefore()) {
            throw new NullPointerException();
        }
        this.offlinePlayer = player;
        this.rank = FoxRank.getOfflineRank(player);
        File file = new File("plugins/FoxRank/PlayerData/" + offlinePlayer.getUniqueId() + ".yml");
        yml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     Get the Player's name

     @return String The player's name
     **/
    public String getName() {
        return FoxRank.getInstance().getTrueName(offlinePlayer.getUniqueId());
    }

    /**
     * @return the player's nickname;
     * If a player is not currently nicked, returns the player's true name.
     */
    @Override
    public String getNickname() {
        return FoxRank.getInstance().getNickname(offlinePlayer.getUniqueId());
    }

    /**
     * @return OfflinePlayer the player the rankedplayer is referring to.
     */
    @Override
    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    /**
     Get the player's rank

     @return rank get the player's rank
     **/

    @Override
    public Rank getRank() {
        return Rank.ofString(yml.getString("Rank"));
    }

    /**
     Sets the player's rank
     @param rank Rank to set the player's rank
     **/

    @Override
    public void setRank(Rank rank) {
        FoxRank.getInstance().setRankOfflinePlayer(offlinePlayer, rank);
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
        return FoxRank.getInstance().isNicked(getOfflinePlayer().getUniqueId());
    }

    /**
     Get if the player is vanished

     @return boolean if the player is vanished
     **/
    @Override
    public boolean isVanished() {
        return FoxRank.instance.isVanished(getOfflinePlayer().getUniqueId());
    }

    /**
     Get if the player is muted

     @return boolean if the player is muted
     **/
    @Override
    public boolean isMuted() {
        return FoxRank.instance.isMuted(getOfflinePlayer().getUniqueId());
    }

    /**
     Get the player's UUID

     @return UUID the player's UUID
     **/
    @Override
    public UUID getUniqueId() {
        return offlinePlayer.getUniqueId();
    }

    /**
     * Mutes a player for the specified duration and reason.
     * @param duration Duration to be muted for.
     * @param staff The player who's executing the mute.
     * @param reason Reason to mute the player.
     **/
    @Override
    public void mutePlayer(RankedPlayer staff, Instant duration, String reason) {
        FoxRank.getInstance().muteOfflinePlayer(this, duration, reason, staff);
    }

    /**
     * Unmutes the player.
     * @param staff The player who's executing the unmute
     **/
    @Override
    public void unmutePlayer(RankedPlayer staff) {
        FoxRank.getInstance().unmuteOfflinePlayer(offlinePlayer, staff);
    }

    /**
     Get the player's RankID.

     @return String the player's current RankID
     **/
    @Override
    public String getRankId() {
        return rank.getRankID();
    }

    /**
     * Gets the player's last mute reason.
     * NOTE: This is LAST mute reason, even if the player is NOT muted.
     * @return String last mute reason
     **/
    @Override
    public String getMuteReason() {
        return FoxRank.getInstance().getMuteReason(offlinePlayer.getUniqueId());
    }

    /**
     * Gets the player's last mute duration.
     * NOTE: This is LAST mute duration, even if the player is NOT muted.
     * @return Instant last mute duration
     **/
    @Override
    public Instant getMuteDuration() {
        return FoxRank.instance.getMuteDuration(offlinePlayer.getUniqueId());
    }

    /**Gets a formatted string of when the player's mute will expire.
     * If the player is not muted will return `0s`.
     * @return String Formatted until mute expires.
     */
    @Override
    public String getFormattedMuteDuration() {
        return FoxRank.getInstance().getFormattedExpiredString(getMuteDuration());
    }

    /**
     * @return boolean if the player is banned.
     */
    @Override
    public boolean isBanned() {
        return FoxRank.instance.isBanned(this.offlinePlayer.getUniqueId());
    }
}
