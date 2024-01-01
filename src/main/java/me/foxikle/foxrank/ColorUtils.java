package me.foxikle.foxrank;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;

public class ColorUtils {
    public static NamedTextColor ofChatColor(ChatColor color){
        switch (color) {
            case RED -> {
                return NamedTextColor.RED;
            }
            case DARK_RED -> {
                return NamedTextColor.DARK_RED;
            }
            case BLUE -> {
                return NamedTextColor.BLUE;
            }
            case DARK_BLUE -> {
                return NamedTextColor.DARK_BLUE;
            }
            case LIGHT_PURPLE -> {
                return NamedTextColor.LIGHT_PURPLE;
            }
            case DARK_PURPLE -> {
                return NamedTextColor.DARK_PURPLE;
            }
            case GREEN -> {
                return NamedTextColor.GREEN;
            }
            case DARK_GREEN -> {
                return NamedTextColor.DARK_GREEN;
            }
            case YELLOW -> {
                return NamedTextColor.YELLOW;
            }
            case DARK_AQUA -> {
                return NamedTextColor.DARK_AQUA;
            }
            case AQUA -> {
                return NamedTextColor.AQUA;
            }
            case GOLD -> {
                return NamedTextColor.GOLD;
            }
            case WHITE -> {
                return NamedTextColor.WHITE;
            }
            case GRAY -> {
                return NamedTextColor.GRAY;
            }
            case DARK_GRAY -> {
                return NamedTextColor.DARK_GRAY;
            }
            case BLACK -> {
                return NamedTextColor.BLACK;
            }
        }
        return NamedTextColor.WHITE;
    }

    public static ChatColor ofNamedTextColor(NamedTextColor color){
        if (color.equals(NamedTextColor.RED)) {
            return ChatColor.RED;
        } else if (color.equals(NamedTextColor.DARK_RED)) {
            return ChatColor.DARK_RED;
        } else if (color.equals(NamedTextColor.BLUE)) {
            return ChatColor.BLUE;
        } else if (color.equals(NamedTextColor.DARK_BLUE)) {
            return ChatColor.DARK_BLUE;
        } else if (color.equals(NamedTextColor.LIGHT_PURPLE)) {
            return ChatColor.LIGHT_PURPLE;
        } else if (color.equals(NamedTextColor.DARK_PURPLE)) {
            return ChatColor.DARK_PURPLE;
        } else if (color.equals(NamedTextColor.GREEN)) {
            return ChatColor.GREEN;
        } else if (color.equals(NamedTextColor.DARK_GREEN)) {
            return ChatColor.DARK_GREEN;
        } else if (color.equals(NamedTextColor.YELLOW)) {
            return ChatColor.YELLOW;
        } else if (color.equals(NamedTextColor.DARK_AQUA)) {
            return ChatColor.DARK_AQUA;
        } else if (color.equals(NamedTextColor.AQUA)) {
            return ChatColor.AQUA;
        } else if (color.equals(NamedTextColor.GOLD)) {
            return ChatColor.GOLD;
        } else if (color.equals(NamedTextColor.WHITE)) {
            return ChatColor.WHITE;
        } else if (color.equals(NamedTextColor.GRAY)) {
            return ChatColor.GRAY;
        } else if (color.equals(NamedTextColor.DARK_GRAY)) {
            return ChatColor.DARK_GRAY;
        } else if (color.equals(NamedTextColor.BLACK)) {
            return ChatColor.BLACK;
        }
        return ChatColor.WHITE;
    }
}
