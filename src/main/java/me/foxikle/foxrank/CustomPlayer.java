package me.foxikle.foxrank;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface CustomPlayer{

    public Player getPlayer();
    public Rank getRank();
    public void setRank(Rank rank);
    public String getPrefix();
    public int getPowerLevel();
    public boolean isNicked();
    public boolean isVanished();
    public String getRankId();
    public String getName();
    public String getDisplayName();
    public Location getLocation();
}
