package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NicknamePlaceholder extends PlaceholderExpansion {
    private final FoxRank plugin;

    public NicknamePlaceholder(FoxRank plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nickname"; // %nickname%
    }

    @Override
    public @NotNull String getName() {
        return "nickname";
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
        if (params.isEmpty() || params.isBlank()) {
            return plugin.getPlayerData(player.getUniqueId()).getNickname();
        } else if (params.equalsIgnoreCase("attempt")) {
            return plugin.attemptedNicknameMap.get(player.getUniqueId());
        }
        return null;
    }
}
