package me.foxikle.foxrank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Fox;

import javax.annotation.Nullable;
import java.util.List;

public class Rank {

    private final int pwrlvl;
    private final String prefix;
    private final String id;
    private final ChatColor color;
    private final ChatColor textColor;
    private final boolean nicknameable;
    private final List<String> permissionNodes;

    public Rank(int pwrlvl, String prefix, String id, ChatColor color, ChatColor textColor, boolean nicknameable, List<String> permissionNodes) {
        this.pwrlvl = pwrlvl;
        this.prefix = prefix;
        this.id = id;
        this.color = color;
        this.textColor = textColor;
        this.nicknameable = nicknameable;
        this.permissionNodes = permissionNodes;
    }

    public static Rank of(String str) {
        if (FoxRank.getInstance().ranks.containsKey(str)) {
            return FoxRank.getInstance().ranks.get(str);
        } else {
            return FoxRank.getInstance().getDefaultRank();
        }
    }

    @Nullable
    public static Rank ofStrict(String str) {
        return FoxRank.getInstance().ranks.getOrDefault(str, null);
    }

    public static boolean exists(String id) {
        return FoxRank.getInstance().ranks.containsKey(id);
    }

    public String getPrefix() {
        return prefix;
    }

    public int getPowerlevel() {
        return pwrlvl;
    }

    public String getId() {
        return id;
    }

    public ChatColor getColor() {
        return color;
    }

    public ChatColor getTextColor() {
        return textColor;
    }

    public boolean isNicknameable() {
        return nicknameable;
    }

    public List<String> getPermissionNodes() {
        return permissionNodes;
    }

    public boolean addPermissionNode(String node){
        return permissionNodes.add(node);
    }

    public boolean removePermissionNode(String node) {
        return permissionNodes.remove(node);
    }
}
