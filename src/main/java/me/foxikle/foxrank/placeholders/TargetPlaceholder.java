package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.Bukkit;
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
        Player target = Bukkit.getPlayer(plugin.targetMap.get(player.getUniqueId()));
        if (params.equalsIgnoreCase("name")) {
            return target.getName();
        } else if (params.equalsIgnoreCase("rank_prefix")) {
            return plugin.dm.getStoredRank(target.getUniqueId()).getPrefix();
        } else if (params.equalsIgnoreCase("rank_id")) {
            return plugin.dm.getStoredRank(target.getUniqueId()).getId();
        } else if (params.equalsIgnoreCase("rank_color")) {
            return plugin.dm.getStoredRank(target.getUniqueId()).getColor().toString();
        }
        return null;
    }
}
