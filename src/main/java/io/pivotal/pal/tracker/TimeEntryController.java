package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TimeEntryController {

    private final CounterService counterService;
    private final GaugeService gaugeService;
    private TimeEntryRepository timeEntryRepository;

    public TimeEntryController(@Autowired TimeEntryRepository timeEntryRepository,
                               @Autowired CounterService counterService,
                               @Autowired GaugeService gaugeService) {
        this.timeEntryRepository = timeEntryRepository;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }

    @PostMapping("/time-entries")
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry){
        TimeEntry createdTimeEntry = timeEntryRepository.create(timeEntry);
        counterService.increment("TimeEntry.created");
        gaugeService.submit("timeEntries.count", timeEntryRepository.list().size());

        return new ResponseEntity<>(createdTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id){
        TimeEntry entry = timeEntryRepository.find(id);
        if(null == entry)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        counterService.increment("TimeEntry.read");
        return new ResponseEntity<>(entry, HttpStatus.OK);
    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable long id, @RequestBody TimeEntry expected){
        TimeEntry updated = timeEntryRepository.update(id, expected);
        if(null == updated)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        counterService.increment("TimeEntry.updated");
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list(){
        counterService.increment("TimeEntry.listed");
        return new ResponseEntity<>(timeEntryRepository.list(), HttpStatus.OK);
    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long id){
        counterService.increment("TimeEntry.deleted");
        gaugeService.submit("timeEntries.count", timeEntryRepository.list().size());
        timeEntryRepository.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
