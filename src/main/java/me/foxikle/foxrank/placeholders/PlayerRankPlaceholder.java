package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
        UUID uuid = player.getUniqueId();
        if (params.equalsIgnoreCase("prefix")) {
            return plugin.getRank(player).getPrefix();
        } else if (params.equalsIgnoreCase("color")) {
            return String.valueOf(plugin.getPlayerData(uuid).getRank().getColor());
        } else if (params.equalsIgnoreCase("id")) {
            return plugin.getRank(player).getId();
        } else if (params.equalsIgnoreCase("textcolor")) {
            return String.valueOf(plugin.getRank(player).getTextColor());
        }
        return null;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        UUID uuid = player.getUniqueId();
        if (params.equalsIgnoreCase("prefix")) {
            return plugin.getPlayerData(uuid).getRank().getPrefix();
        } else if (params.equalsIgnoreCase("color")) {
            return String.valueOf(plugin.getPlayerData(uuid).getRank().getColor());
        } else if (params.equalsIgnoreCase("id")) {
            return plugin.getPlayerData(uuid).getRank().getId();
        } else if (params.equalsIgnoreCase("textcolor")) {
            return String.valueOf(plugin.getPlayerData(uuid).getRank().getTextColor());
        }
        return null;
    }
}
