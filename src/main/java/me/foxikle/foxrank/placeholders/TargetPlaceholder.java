package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TargetPlaceholder extends PlaceholderExpansion {
    private final FoxRank plugin;

    public TargetPlaceholder(FoxRank plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "target"; // %playerrank%
    }

    @Override
    public @NotNull String getName() {
        return "target";
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
        OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.targetMap.get(player.getUniqueId()));
        if (params.equalsIgnoreCase("name")) {
            return target.getName();
        } else if (params.equalsIgnoreCase("rank_prefix")) {
            return plugin.getPlayerData(target.getUniqueId()).getRank().getPrefix();
        } else if (params.equalsIgnoreCase("rank_id")) {
            return  plugin.getPlayerData(target.getUniqueId()).getRank().getId();
        } else if (params.equalsIgnoreCase("rank_color")) {
            return plugin.getPlayerData(target.getUniqueId()).getRank().getColor().toString();
        }
        return null;
    }
}
