package io.pivotal.pal.tracker;

import java.math.BigDecimal;
import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private HashMap<BigDecimal, TimeEntry> repo = new HashMap();
    private long counter = 0;

    public TimeEntry create(TimeEntry timeEntry) {
        TimeEntry entryToSave =
                new TimeEntry(++counter, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        repo.put(BigDecimal.valueOf(counter), entryToSave);
        return entryToSave;
    }

    public TimeEntry find(long id) {
        return repo.getOrDefault(BigDecimal.valueOf(id), null);
    }

    public List<TimeEntry> list() {
        List resultList = new ArrayList(repo.values());
        Collections.sort(resultList);
        return resultList;
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry found = find(id);
        if (found == null) {
            return null;
        } else {
            TimeEntry updated = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
            repo.put(BigDecimal.valueOf(id), updated);
            return updated;
        }
    }

    public void delete(long id) {
        TimeEntry found = find(id);
        if (found != null) {
            repo.remove(BigDecimal.valueOf(id));
        }
    }

}
