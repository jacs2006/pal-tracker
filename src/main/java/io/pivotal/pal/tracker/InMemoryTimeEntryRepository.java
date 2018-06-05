package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> timeEntryMap = new HashMap<>();
    private List<TimeEntry> timeEntryList = new ArrayList<>();
    private long timeEntry = 0L;

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        timeEntry.setId(++this.timeEntry);

        this.timeEntryMap.put(timeEntry.getId(), timeEntry);

        this.timeEntryList.add(timeEntry);

        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {

        return this.timeEntryMap.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return this.timeEntryList;
    }

    @Override
    public TimeEntry update(long id, TimeEntry updateEntry) {

        updateEntry.setId(id);
        this.timeEntryMap.put(id, updateEntry);

        return this.timeEntryMap.get(id);
    }

    @Override
    public void delete(long id) {
        timeEntryList.remove(timeEntryMap.get(id));
        timeEntryMap.remove(id);
    }
}
