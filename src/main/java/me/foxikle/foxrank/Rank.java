package me.foxikle.foxrank;

import com.google.gson.Gson;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;

import java.util.List;

public class Rank {

    private int pwrlvl;
    private String prefix;
    private String id;
    private int color;
    private int textColor;
    private boolean nicknameable;
    private List<String> permissionNodes;

    public Rank(int pwrlvl, String prefix, String id, int color, int textColor, boolean nicknameable, List<String> permissionNodes) {
        this.pwrlvl = pwrlvl;
        this.prefix = prefix;
        this.id = id;
        this.color = color;
        this.textColor = textColor;
        this.nicknameable = nicknameable;
        this.permissionNodes = permissionNodes;
    }

    public static Rank of(String str) {
        if(FoxRank.USE_DATABASE) {
            Rank rank = new Gson().fromJson(str, Rank.class);
            rank.setPrefix(ChatColor.translateAlternateColorCodes('&', rank.getPrefix()));
            return rank;
        } else {
            if (FoxRank.getInstance().ranks.containsKey(str)) {
                return FoxRank.getInstance().ranks.get(str);
            } else {
                return FoxRank.getInstance().getDefaultRank();
            }
        }
    }

    public static Rank fromID(String str) {
        if (FoxRank.getInstance().ranks.containsKey(str)) {
            return FoxRank.getInstance().ranks.get(str);
        } else {
            return FoxRank.getInstance().getDefaultRank();
        }
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

    public NamedTextColor getColor() {
        return NamedTextColor.namedColor(color);
    }

    public NamedTextColor getTextColor() {
        return NamedTextColor.namedColor(textColor);
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

    public String toJson() {
        return new Gson().toJson(this);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setColor(NamedTextColor color) {
        this.color = color.value();
    }

    public void setNicknameable(boolean nicknameable) {
        this.nicknameable = nicknameable;
    }

    public void setPermissionNodes(List<String> permissionNodes) {
        this.permissionNodes = permissionNodes;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPowerLevel(int pwrlvl) {
        this.pwrlvl = pwrlvl;
    }

    public void setTextColor(NamedTextColor textColor) {
        this.textColor = textColor.value();
    }
}
