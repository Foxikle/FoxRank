package me.foxikle.foxrank;

import org.bukkit.ChatColor;

public enum Rank {

    NONE(ChatColor.GRAY + "", 1, 0),
    OWNER(ChatColor.DARK_RED + "[OWNER] ", 100, 100),
    ADMIN(ChatColor.RED + "[ADMIN] ", 90, 90),
    MODERATOR(ChatColor.DARK_GREEN + "[MOD] ", 80, 80),
    YOUTUBE(ChatColor.RED + "[" + ChatColor.WHITE + "YOUTUBE" + ChatColor.RED + "]", 70, 70),
    TWITCH(ChatColor.DARK_PURPLE + "[" + ChatColor.WHITE + "TWITCH" + ChatColor.DARK_PURPLE + "]", 70, 70),
    MVPp(ChatColor.AQUA + "[MVP" + ChatColor.WHITE + "+" + ChatColor.AQUA + "] ", 65, 65),
    MVP(ChatColor.AQUA + "[MVP]", 60, 60),
    VIPp(ChatColor.GREEN + "[VIP" + ChatColor.GOLD + "+" + ChatColor.GREEN + "] ", 45, 45),
    VIP(ChatColor.GREEN + "[VIP] ", 40, 40);

    private final String prefix;
    private final int powerLevel;
    private final int rankID;

    Rank(String prefix, int powerLevel, int rankID) {
        this.prefix = prefix;
        this.powerLevel = powerLevel;
        this.rankID = rankID;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getRankID() {
        return rankID;
    }

    public int getPowerLevel(){
        return powerLevel;
    }
}
