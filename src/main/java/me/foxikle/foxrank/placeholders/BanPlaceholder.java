package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public class BanPlaceholder extends PlaceholderExpansion {
    private final FoxRank plugin;

    public BanPlaceholder(FoxRank plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ban"; // %playerrank%
    }

    @Override
    public @NotNull String getName() {
        return "ban";
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
        UUID banned = plugin.banMap.get(player.getUniqueId());
        if (params.equalsIgnoreCase("duration")) {
            return plugin.getFormattedExpiredString(Instant.parse(plugin.dm.getStoredBanDuration(banned)), Instant.now());
        } else if (params.equalsIgnoreCase("reason")) {
            return plugin.dm.getStoredBanReason(banned);
        } else if (params.equalsIgnoreCase("id")) {
            return plugin.dm.getStoredBanID(banned);
        } else if (params.equalsIgnoreCase("preset")) {
            return plugin.attemptedBanPresetMap.get(player.getUniqueId());
        }
        return null;
    }
}
