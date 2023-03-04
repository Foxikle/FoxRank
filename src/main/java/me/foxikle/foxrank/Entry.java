package me.foxikle.foxrank;

import org.apache.commons.lang.SerializationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Entry implements Serializable {
    private final EntryType type;
    private final UUID involved;
    private final UUID staff;
    private final Instant duration;
    private final Instant time;
    private final String option1;
    private final String option2;
    private final String id;

    public Entry(@Nonnull EntryType type, @Nonnull UUID involved, @Nonnull Instant time, @Nullable Instant duration, @Nullable String option1, @Nullable String option2, @Nullable UUID staff, @Nullable String id) {
        this.type = type;
        this.involved = involved;
        this.staff = staff;
        this.option1 = option1;
        this.option2 = option2;
        this.time = time;
        this.duration = duration;
        this.id = id;
    }

    protected static Entry deserialize(String s) throws IllegalArgumentException, SerializationException {
        List<String> list = Arrays.asList(s.split("%")); // WATCH OUT FOR ERRORS HERE !!!!!
        if (list.size() >= 5) {
            switch (Objects.requireNonNull(EntryType.ofString(list.get(0)))) {
                case BAN -> {
                    if (list.size() >= 8) {
                        return new Entry(EntryType.BAN, UUID.fromString(list.get(1)), Instant.parse(list.get(4)), Instant.parse(list.get(5)), list.get(6), list.get(7), UUID.fromString(list.get(2)), list.get(3));
                    }
                    throw new IllegalArgumentException("Expected 8 arguments but found " + list.size());
                }
                case UNBAN -> {
                    return new Entry(EntryType.UNBAN, UUID.fromString(list.get(1)), Instant.parse(list.get(4)), null, null, null, UUID.fromString(list.get(2)), list.get(3));
                }
                case MUTE -> {
                    if (list.size() >= 7) {
                        return new Entry(EntryType.MUTE, UUID.fromString(list.get(1)), Instant.parse(list.get(4)), Instant.parse(list.get(5)), list.get(6), null, UUID.fromString(list.get(2)), list.get(3));
                    }
                    throw new IllegalArgumentException("Expected 8 arguments but found " + list.size());
                }
                case UNMUTE -> {
                    return new Entry(EntryType.UNMUTE, UUID.fromString(list.get(1)), Instant.parse(list.get(4)), null, null, null, UUID.fromString(list.get(2)), list.get(3));
                }
                case NICKNAME -> {
                    if (list.size() >= 6) {
                        return new Entry(EntryType.NICKNAME, UUID.fromString(list.get(1)), Instant.parse(list.get(3)), null, list.get(4), list.get(5), UUID.fromString(list.get(1)), null);
                    }
                    throw new IllegalArgumentException("Expected 6 arguments but found " + list.size());
                }
            }
            throw new IllegalArgumentException("Expected at least 5 arguments but found " + list.size());
        }
        throw new SerializationException("String \n" + s + "\n is not deserializable");
    }

    public EntryType getType() {
        return type;
    }

    public Instant getDuration() {
        return duration;
    }

    public Instant getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public UUID getInvolved() {
        return involved;
    }

    public UUID getStaff() {
        return staff;
    }

    @Override
    public String toString() {
        return serialize();
    }

    protected String serialize() {
        String s = "%";
        return switch (type) {
            case BAN ->
//              <--- option1 = reason | option2 = silent --->
                    type + s + involved + s + staff + s + id + s + time + s + duration.toString() + s + option1 + s + option2;
            case UNBAN, UNMUTE ->
//                <--- option1 = Nothing | option2 = Nothing --->
                    type + s + involved + s + staff + s + id + s + time;
            case MUTE ->
//               <--- option1 = reason | option2 = Nothing --->
                    type + s + involved + s + staff + s + id + s + time + s + duration.toString() + s + option1;
            case NICKNAME ->
//                <--- option1 = nick | option2 = skin --->
                    type + s + involved + s + staff + s + time + s + option1 + s + option2;
        };
    }
}
