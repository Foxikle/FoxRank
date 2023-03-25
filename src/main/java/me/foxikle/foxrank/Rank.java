package me.foxikle.foxrank;

import org.bukkit.ChatColor;

public enum Rank {
    DUMMY(ChatColor.LIGHT_PURPLE + "", 0, "DUMMY"),
    DEFAULT(ChatColor.GRAY + "", 1, "DEFAULT"),
    OWNER(ChatColor.RED + "[OWNER] ", 100, "OWNER"),
    ADMIN(ChatColor.RED + "[ADMIN] ", 90, "ADMIN"),
    MODERATOR(ChatColor.DARK_GREEN + "[MOD] ", 80, "MODERATOR"),
    YOUTUBE(ChatColor.RED + "[" + ChatColor.WHITE + "YOUTUBE" + ChatColor.RED + "] ", 70, "YOUTUBE"),
    TWITCH(ChatColor.DARK_PURPLE + "[" + ChatColor.WHITE + "TWITCH" + ChatColor.DARK_PURPLE + "] ", 70, "TWITCH"),
    MVP_PLUS(ChatColor.AQUA + "[MVP" + ChatColor.WHITE + "+" + ChatColor.AQUA + "] ", 65, "MVP_PLUS"),
    MVP(ChatColor.AQUA + "[MVP] ", 60, "MVP"),
    VIP_PLUS(ChatColor.GREEN + "[VIP" + ChatColor.GOLD + "+" + ChatColor.GREEN + "] ", 45, "VIP_PLUS"),
    VIP(ChatColor.GREEN + "[VIP] ", 40, "VIP");

    private final String prefix;
    private final int powerLevel;
    private final String rankID;

    Rank(String prefix, int powerLevel, String rankID) {
        this.prefix = prefix;
        this.powerLevel = powerLevel;
        this.rankID = rankID;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRankID() {
        return rankID;
    }

    public static Rank ofString(String s) {
        if (s == null) return DEFAULT;
        if (s.equalsIgnoreCase("DEFAULT")) {
            return Rank.DEFAULT;
        } else if (s.equalsIgnoreCase("OWNER")) {
            return Rank.OWNER;
        } else if (s.equalsIgnoreCase("ADMIN")) {
            return Rank.ADMIN;
        } else if (s.equalsIgnoreCase("MODERATOR")) {
            return Rank.MODERATOR;
        } else if (s.equalsIgnoreCase("YOUTUBE")) {
            return Rank.YOUTUBE;
        } else if (s.equalsIgnoreCase("TWITCH")) {
            return Rank.TWITCH;
        } else if (s.equalsIgnoreCase("MVP_PLUS")) {
            return Rank.MVP_PLUS;
        } else if (s.equalsIgnoreCase("MVP")) {
            return Rank.MVP;
        } else if (s.equalsIgnoreCase("VIP_PLUS")) {
            return Rank.VIP_PLUS;
        } else if (s.equalsIgnoreCase("VIP")) {
            return Rank.VIP;
        } else {
            return Rank.DEFAULT;
        }
    }

    public int getPowerLevel() {
        return powerLevel;
    }
}
