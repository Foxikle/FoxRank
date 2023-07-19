package me.foxikle.foxrank;

import org.bukkit.ChatColor;

public class Rank {

    private final int pwrlvl;
    private final String prefix;
    private final String id;
    private final ChatColor color;
    private final ChatColor textColor;

    public Rank(int pwrlvl, String prefix, String id, ChatColor color, ChatColor textColor) {
        this.pwrlvl = pwrlvl;
        this.prefix = prefix;
        this.id = id;
        this.color = color;
        this.textColor = textColor;
    }

    public static Rank of(String str) {
        if (FoxRank.getInstance().ranks.containsKey(str)) {
            return FoxRank.getInstance().ranks.get(str);
        } else {
            return FoxRank.getInstance().getDefaultRank();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public int getPowerlevel() {
        return pwrlvl;
    }

    public String getId() {
        return id;
    }

    public ChatColor getColor() {
        return color;
    }

    public ChatColor getTextColor() {
        return textColor;
    }
}
