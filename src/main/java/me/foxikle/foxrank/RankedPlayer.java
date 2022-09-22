package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RankedPlayer implements CustomPlayer{
    private Player player;
    private Rank rank;

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
        return this.player.getName();
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
        return Bukkit.getOfflinePlayer(this.getName()).isOnline();
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
     **/
    @Override
    public void setRank(Rank rank) {
        FoxRank.setRank(this.getPlayer(), rank);
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
        return FoxRank.getInstance().isNicked(this.getPlayer());
    }

    /**
     Get if the player is vanished

     @return boolean if the player is vanished
     **/
    @Override
    public boolean isVanished() {
        return FoxRank.instance.isVanished(this.getPlayer());
    }

    @Override
    public String getRankId() {
        return FoxRank.getRank(this.player).getRankID();
    }


    /**
     Get the current player's location

     @return Location the current player's location
     **/
    public Location getLocation() {
        return this.getPlayer().getLocation();
    }
}