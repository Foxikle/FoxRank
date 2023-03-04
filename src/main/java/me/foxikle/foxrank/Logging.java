package me.foxikle.foxrank;

import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Logging {
    protected static void addLogEntry(EntryType type, OfflineRankedPlayer involved, @Nullable UUID staff, @Nullable Instant duration, @Nullable String option, @Nullable String option2, @Nullable String id) {
        Entry entry = new Entry(type, involved.getUniqueId(), Instant.now(), duration, option, option2, staff, id);
        if (FoxRank.getInstance().useDb) {
            FoxRank.getInstance().db.addEntry(involved.getUniqueId(), entry);
        } else {
            File file = new File("plugins/FoxRank/auditlog.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.getStringList(involved.getUniqueId().toString()).add(entry.serialize());
            try {
                yml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static List<Entry> getLogEntries(UUID uuid) {
        if (FoxRank.getInstance().useDb) {
            return FoxRank.getInstance().db.getEntries(uuid);
        } else {
            File file = new File("plugins/FoxRank/auditlog.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            List<String> str = yml.getStringList(uuid.toString());
            List<Entry> returnme = new ArrayList<>();
            for (String s : str) {
                returnme.add(Entry.deserialize(s));
            }
            return returnme;
        }
    }
}
