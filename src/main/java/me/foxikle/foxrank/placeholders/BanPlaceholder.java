package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.OfflinePlayer;
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
        UUID banned = plugin.targetMap.get(player.getUniqueId());
        if(banned == null)
            banned = player.getUniqueId();
        if (params.equalsIgnoreCase("duration")) {
            return plugin.getFormattedExpiredString(plugin.getPlayerData(banned).getBanDuration(), Instant.now());
        } else if (params.equalsIgnoreCase("reason")) {
            return plugin.getPlayerData(banned).getBanReason();
        } else if (params.equalsIgnoreCase("id")) {
            return plugin.getPlayerData(banned).getBanID();
        } else if (params.equalsIgnoreCase("preset")) {
            return plugin.attemptedBanPresetMap.get(player.getUniqueId());
        }
        return null;
    }
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        UUID banned = plugin.targetMap.get(player.getUniqueId());
        if(banned == null)
            banned = player.getUniqueId();
        if (params.equalsIgnoreCase("duration")) {
            return plugin.getFormattedExpiredString(plugin.getPlayerData(banned).getBanDuration(), Instant.now());
        } else if (params.equalsIgnoreCase("reason")) {
            return plugin.getPlayerData(banned).getBanReason();
        } else if (params.equalsIgnoreCase("id")) {
            return plugin.getPlayerData(banned).getBanID();
        } else if (params.equalsIgnoreCase("preset")) {
            return plugin.attemptedBanPresetMap.get(player.getUniqueId());
        }
        return null;
    }
}
