package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerRankPlaceholder extends PlaceholderExpansion {
    private final FoxRank plugin;

    public PlayerRankPlaceholder(FoxRank plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "playerrank"; // %playerrank%
    }

    @Override
    public @NotNull String getName() {
        return "Player Rank";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Foxikle";
    }

    @Override
    public @NotNull String getVersion() {
        return "v1.9.6";
    }

    @Override
    public boolean persist() {
        return false;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("prefix")) {
            return plugin.getRank(player).getPrefix();
        } else if (params.equalsIgnoreCase("color")) {
            return String.valueOf(plugin.getRank(player).getColor());
        } else if (params.equalsIgnoreCase("id")) {
            return plugin.getRank(player).getId();
        } else if (params.equalsIgnoreCase("textcolor")) {
            return String.valueOf(plugin.getRank(player).getTextColor());
        }
        return null;
    }
}
