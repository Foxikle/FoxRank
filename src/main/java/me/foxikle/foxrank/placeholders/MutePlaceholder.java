package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public class MutePlaceholder extends PlaceholderExpansion {
    private final FoxRank plugin;

    public MutePlaceholder(FoxRank plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mute"; // %playerrank%
    }

    @Override
    public @NotNull String getName() {
        return "mute";
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
        UUID muted = plugin.targetMap.get(player.getUniqueId());
        if(muted == null)
            muted = player.getUniqueId();
        if (params.equalsIgnoreCase("duration")) {
            return plugin.getFormattedExpiredString(plugin.getPlayerData(muted).getMuteDuration(), Instant.now());
        } else if (params.equalsIgnoreCase("reason")) {
            return plugin.getPlayerData(muted).getMuteReason();
        }
        return null;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        UUID muted = plugin.targetMap.get(player.getUniqueId());
        if(muted == null)
            muted = player.getUniqueId();
        if (params.equalsIgnoreCase("duration")) {
            return plugin.getFormattedExpiredString(plugin.getPlayerData(muted).getMuteDuration(), Instant.now());
        } else if (params.equalsIgnoreCase("reason")) {
            return plugin.getPlayerData(muted).getMuteReason();
        }
        return null;
    }
}
