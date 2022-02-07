package me.foxikle.foxrank;

import org.bukkit.ChatColor;

public enum Rank {

    NONE(ChatColor.GRAY + "", 1),
    OWNER(ChatColor.DARK_RED + "[OWNER] ", 100),
    ADMIN(ChatColor.RED + "[ADMIN] ", 10),
    MODERATOR(ChatColor.DARK_GREEN + "[MOD] ", 70),
    MVPp(ChatColor.AQUA + "[MVP" + ChatColor.WHITE + "+" + ChatColor.AQUA + "] ", 65),
    MVP(ChatColor.AQUA + "[MVP]", 60),
    VIPp(ChatColor.GREEN + "[VIP" + ChatColor.GOLD + "+" + ChatColor.GREEN + "] ", 45),
    VIP(ChatColor.GREEN + "[VIP] ", 40);

    private final String prefix;
    private final int powerLevel;

    Rank(String prefix, int powerLevel) {
        this.prefix = prefix;
        this.powerLevel = powerLevel;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getPowerLevel(){
        return powerLevel;
    }
}
