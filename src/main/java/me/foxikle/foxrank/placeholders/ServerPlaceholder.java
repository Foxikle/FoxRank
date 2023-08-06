package me.foxikle.foxrank.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.foxikle.foxrank.FoxRank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerPlaceholder extends PlaceholderExpansion {
    private final FoxRank plugin;

    public ServerPlaceholder(FoxRank plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "server";
    }

    @Override
    public @NotNull String getName() {
        return "Server";
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
        if (params.equalsIgnoreCase("name")) {
            return plugin.getConfig().getString("ServerName");
        } else if (params.equalsIgnoreCase("appeal_link")) {
            return plugin.getConfig().getString("BanAppealLink");
        }
        return null;
    }
}
