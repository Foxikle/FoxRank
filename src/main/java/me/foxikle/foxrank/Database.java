package me.foxikle.foxrank;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    private final String host = FoxRank.getInstance().getConfig().getString("sqlHost");
    private final String port = FoxRank.getInstance().getConfig().getString("sqlPort");
    private final String database = FoxRank.getInstance().getConfig().getString("sqlName");
    private final String username = FoxRank.getInstance().getConfig().getString("sqlUsername");
    private final String password = FoxRank.getInstance().getConfig().getString("sqlPassword");
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
            ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS foxrankplayerdata (name VARCHAR(16), uuid VARCHAR(36), rankid VARCHAR(36), isvanished BOOLEAN, isnicked BOOLEAN, ismuted BOOLEAN, isbanned BOOLEAN, muteduration TEXT, mutereason TEXT, nickname TEXT, nicknamerank TEXT, nicknameskin TEXT, banduration TEXT, banreason TEXT, banid TEXT, PRIMARY KEY(uuid))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void createBannedPlayersTable() {
        PreparedStatement ps;
        try {
            ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS foxrankbannedplayers (uuids TEXT, id VARCHAR(100), PRIMARY KEY(id))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected List<OfflinePlayer> getStoredBannedPlayers() {
        List<OfflinePlayer> returnme = new ArrayList<>();
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT uuid FROM foxrankplayerdata WHERE id=?");
            ps.setString(1, "bannedPlayers");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String str = rs.getString("bannedPlayers");
                List<String> list1 = List.of(str.split(":"));

                for (String s : list1) {
                    returnme.add(Bukkit.getOfflinePlayer(UUID.fromString(s)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnme;
    }

    protected void setStoredBannedPlayers(List<OfflinePlayer> players) {
        List<UUID> uuids = new ArrayList<>();
        try {
            for (OfflinePlayer p : players) {
                uuids.add(p.getUniqueId());
            }
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET uuids = ? WHERE id=?");
            ps.setString(1, uuids.toString());
            ps.setString(2, "bannedPlayers");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void addPlayerData(RankedPlayer rp) {
        UUID uuid = rp.getUniqueId();
        try {
            if (!exists(uuid)) {
                PreparedStatement ps = getConnection().prepareStatement("INSERT IGNORE INTO foxrankplayerdata (name, uuid) VALUES (?,?)");
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
            PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM foxrankplayerdata WHERE uuid = ?");
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
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET rankid = ? WHERE uuid=?");
            ps.setString(1, rank.getRankID());
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredVanishedState(UUID uuid, boolean isVanished) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET isvanished = ? WHERE uuid = ?");
            ps.setBoolean(1, isVanished);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredNicknameState(UUID uuid, boolean isNicked) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET isnicked = ? WHERE uuid = ?");
            ps.setBoolean(1, isNicked);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredMuteState(UUID uuid, boolean isMuted) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET ismuted = ? WHERE uuid = ?");
            ps.setBoolean(1, isMuted);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredBanState(UUID uuid, boolean isBanned) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET isbanned = ? WHERE uuid = ?");
            ps.setBoolean(1, isBanned);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void setStoredMuteData(UUID uuid, boolean isMuted, String reason, Instant duration) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET ismuted = ?, muteduration = ?, mutereaosn = ? WHERE uuid=?");
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
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET isbanned = ?, banduration = ?, banreason = ?, banid = ? WHERE uuid = ?");
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
            PreparedStatement ps = getConnection().prepareStatement("UPDATE foxrankplayerdata SET isnicked = ?, nickname = ?, nicknamerank = ?, nicknameskin = ? WHERE uuid = ?");
            ps.setBoolean(1, isNicked);
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
            PreparedStatement ps = getConnection().prepareStatement("SELECT rankid FROM foxrankplayerdata WHERE uuid=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Rank.ofString(rs.getString("rankid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT;
    }

    protected boolean getStoredMuteStatus(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT ismuted FROM foxrankplayerdata WHERE uuid=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("ismuted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean getStoredBanStatus(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT isbanned FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isbanned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean getStoredNicknameStatus(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT isnicked FROM foxrankplayerdata WHERE uuid=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isnicked");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean getStoredVanishStatus(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT isvanished FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isvanished");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected String getStoredNickname(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT nickname FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected String getStoredNicknameSkin(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT nicknameskin FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nicknameskin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected Rank getStoredNicknameRank(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT nicknamerank FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Rank.ofString(rs.getString("nicknamerank"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT;
    }

    protected String getStoredMuteDuration(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT muteduration FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("muteduration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }

    protected String getStoredMuteReason(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT mutereason FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("mutereason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }

    protected String getStoredBanID(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT banid FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("banid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }

    protected String getStoredBanDuration(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT banduration FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("banduration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }

    protected String getStoredBanReason(UUID uuid) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT banreason FROM foxrankplayerdata WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("banreason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.DEFAULT.getRankID();
    }
}
