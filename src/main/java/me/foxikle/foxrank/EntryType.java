package me.foxikle.foxrank;

public enum EntryType {
    BAN,
    UNBAN,
    MUTE,
    UNMUTE,
    NICKNAME;

    public static EntryType ofString(String s) {
        if (s.equalsIgnoreCase("BAN")) {
            return BAN;
        } else if (s.equalsIgnoreCase("UNBAN")) {
            return UNBAN;
        } else if (s.equalsIgnoreCase("MUTE")) {
            return MUTE;
        } else if (s.equalsIgnoreCase("UNMUTE")) {
            return UNMUTE;
        } else if (s.equalsIgnoreCase("NICKNAME")) {
            return NICKNAME;
        }
        return null;
    }
}
