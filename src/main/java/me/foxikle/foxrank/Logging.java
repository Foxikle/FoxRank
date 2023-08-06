package me.foxikle.foxrank;

import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.UUID;

public class Logging {
    protected static void addLogEntry(EntryType type, UUID involved, @Nullable UUID staff, @Nullable Instant duration, @Nullable String option, @Nullable String option2, @Nullable String id) {
        Entry entry = new Entry(type, involved, Instant.now(), duration, option, option2, staff, id);
        Bukkit.getScheduler().runTaskAsynchronously(FoxRank.getInstance(), () -> FoxRank.getInstance().getDm().addLoggingEntry(involved, entry));
    }
}
