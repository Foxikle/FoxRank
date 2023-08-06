package me.foxikle.foxrank.Data;

import me.foxikle.foxrank.Rank;

import java.time.Instant;
import java.util.Objects;

public class PlayerData {
    // bans //todo: make sure to unban people too. So don't rely on this
    private final Instant banDuration;
    private final String banReason;
    private final String banID;
    private boolean isBanned;

    // mutes
    private Instant muteDuration;
    private String muteReason;
    private boolean isMuted;

    // vanish
    private boolean isVanished;

    // nicknames
    private boolean isNicked;
    private String nickname;
    private Rank nickanmeRank;
    private String skin;

    // data
    private Rank rank;

    public PlayerData(Instant banDuration, String banReason, String banID, boolean isBanned, Instant muteDuration, String muteReason, boolean isMuted, boolean isVanished, boolean isNicked, String nickname, Rank nickanmeRank, Rank rank, String skin) {
        this.banDuration = banDuration;
        this.banReason = banReason;
        this.banID = banID;
        this.isBanned = isBanned;
        this.muteDuration = muteDuration;
        this.muteReason = muteReason;
        this.isMuted = isMuted;
        this.isVanished = isVanished;
        this.isNicked = isNicked;
        this.nickname = nickname;
        this.nickanmeRank = nickanmeRank;
        this.rank = rank;
        this.skin = skin;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public boolean isNicked() {
        return isNicked;
    }

    public void setNicked(boolean nicked) {
        isNicked = nicked;
    }

    public boolean isVanished() {
        return isVanished;
    }

    public void setVanished(boolean vanished) {
        isVanished = vanished;
    }

    public Instant getBanDuration() {
        return banDuration;
    }

    public Instant getMuteDuration() {
        return muteDuration;
    }

    public void setMuteDuration(Instant muteDuration) {
        this.muteDuration = muteDuration;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public String getBanID() {
        return banID;
    }

    public String getBanReason() {
        return banReason;
    }

    public String getMuteReason() {
        return muteReason;
    }

    public void setMuteReason(String muteReason) {
        this.muteReason = muteReason;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public Rank getNicknameRank() {
        return nickanmeRank;
    }

    public void setNickanmeRank(Rank nickanmeRank) {
        this.nickanmeRank = nickanmeRank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return isBanned() == that.isBanned() && isMuted() == that.isMuted() && isVanished() == that.isVanished() && isNicked() == that.isNicked() && Objects.equals(getBanDuration(), that.getBanDuration()) && Objects.equals(getBanReason(), that.getBanReason()) && Objects.equals(getBanID(), that.getBanID()) && Objects.equals(getMuteDuration(), that.getMuteDuration()) && Objects.equals(getMuteReason(), that.getMuteReason()) && Objects.equals(getNickname(), that.getNickname()) && Objects.equals(getNicknameRank(), that.getNicknameRank()) && Objects.equals(getSkin(), that.getSkin()) && Objects.equals(getRank(), that.getRank());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBanDuration(), getBanReason(), getBanID(), isBanned(), getMuteDuration(), getMuteReason(), isMuted(), isVanished(), isNicked(), getNickname(), getNicknameRank(), getSkin(), getRank());
    }
}
