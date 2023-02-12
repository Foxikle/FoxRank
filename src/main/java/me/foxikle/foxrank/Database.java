package me.foxikle.foxrank;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class Database {
    private final String host = "";
    private final String port = "";
    private final String database = "";
    private final String username = "";
    private final String password = "";
    private Connection connection;

    protected boolean isConnected() {
        return (connection != null);
    }

    protected void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected())
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
    }

    protected void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected Connection getConnection() {
        return connection;
    }

    protected void createPlayerDataTable() {
        PreparedStatement ps;
        try {
            ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS foxrankplayerdata (NAME TINYTEXT, UUID TINYTEXT, RANK TINYTEXT, ISVANISHED BOOL, ISNICKED BOOL, ISMUTED BOOL, ISBANNED BOOL, MUTEDURATION TINYTEXT, MUTEREASON TINYTEXT, NICKNAME TINYTEXT, NICKNAME-RANK TINYTEXT, NICKNAME-SKIN TINYTEXT, BANDURATION TINYTEXT, BANREASON TINYTEXT, BANID TINYTEXT, PRIMARY KEY (UUID)");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void addPlayerData(RankedPlayer rp) {
        UUID uuid = rp.getUniqueId();
        try {
            if (!exists(uuid)) {
                PreparedStatement ps = getConnection().prepareStatement("INSERT IGNORE INFO foxrankplayerdata (NAME, UUID) VALUES (?,?)");
                ps.setString(1, rp.getName());
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM foxrankplayerdata WHERE UUID = ?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void setStoredRank(UUID uuid, Rank rank) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET RANK = ? WHERE UUID=?");
            ps.setString(1, rank.getRankID());
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredVanishedState(UUID uuid, boolean isVanished) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET ISVANISHED = ? WHERE UUID=?");
            ps.setBoolean(1, isVanished);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredMuteData(UUID uuid, boolean isMuted, String reason, Instant duration) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET ISMUTED = ?, MUTEDURATION = ?, MUTEREASON = ? WHERE UUID=?");
            ps.setBoolean(1, isMuted);
            ps.setString(2, duration.toString());
            ps.setString(3, reason);
            ps.setString(4, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredBanData(UUID uuid, boolean isBanned, String reason, Instant duration, String ID) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET ISMUTED = ?, BANDURATION = ?, BANREASON = ?, BANID = ? WHERE UUID=?");
            ps.setBoolean(1, isBanned);
            ps.setString(2, duration.toString());
            ps.setString(3, reason);
            ps.setString(4, ID);
            ps.setString(5, uuid.toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredNicknameData(UUID uuid, boolean isNicked, Rank rank, String newNick, String skin) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET ISNICKED = ?, NICKNAME = ?, NICKNAME-RANK = ?, NICKNAME-SKIN = ? WHERE UUID=?");
            ps.setBoolean(1, true);
            ps.setString(2, newNick);
            ps.setString(3, rank.getRankID());
            ps.setString(4, skin);
            ps.setString(5, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected Rank getStoredRank(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT RANK FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Rank.ofString(rs.getString("RANK"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT;
    }

    protected boolean getStoredMuteStatus(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT ISMUTED FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("ISMUTED");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean getStoredBanStatus(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT ISBANNED FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("ISBANNED");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean getStoredNicknameStatus(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT ISNICKED FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("ISNICKED");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean getStoredVanishStatus(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT ISVANISHED FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("ISVANISHED");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected String getStoredNickname(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT NICKNAME FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("NICKNAME");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected String getStoredNicknameSkin(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT NICKNAME-SKIN FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("NICKNAME-SKIN");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected Rank getStoredNicknameRank(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT NICKNAME-RANK FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Rank.ofString(rs.getString("NICKNAME-RANK"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT;
    }

    protected String getStoredMuteDuration(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT MUTEDURATION FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("MUTEDURATION");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }

    protected String getStoredMuteReason(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT MUTEREASON FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("MUTEREASON");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }

    protected String getStoredBanID(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT BANID FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("BANID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }

    protected String getStoredBanDuration(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT BANDURATION FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("BANDURATION");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }

    protected String getStoredBanReason(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT BANREASON FROM foxrankplayerdata WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("BANREASON");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }
}
