package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;
    private CounterService counterService;
    private GaugeService gaugeService;

    public TimeEntryController(TimeEntryRepository timeEntryRepository, CounterService counterService, GaugeService gaugeService) {
        this.timeEntryRepository = timeEntryRepository;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }

    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntry) {
        TimeEntry entryCreated = timeEntryRepository.create(timeEntry);
        if (entryCreated != null) {
            counterService.increment("TimeEntry.created");
            gaugeService.submit("TimeEntries.count", timeEntryRepository.list().size());
            return new ResponseEntity(entryCreated, HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        counterService.increment("TimeEntry.listed");
        return ResponseEntity.ok(timeEntryRepository.list());
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable(value="id") long id) {
        TimeEntry found = timeEntryRepository.find(id);
        if (found != null) {
            counterService.increment("TimeEntry.read");
            return new ResponseEntity(found, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity update(@PathVariable(value="id") long id, @RequestBody TimeEntry timeEntry) {
        TimeEntry updated = timeEntryRepository.update(id, timeEntry);
        if (updated != null) {
            counterService.increment("TimeEntry.updated");
            return ResponseEntity.ok(updated);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable(value="id") long id) {
        timeEntryRepository.delete(id);
        counterService.increment("TimeEntry.deleted");
        gaugeService.submit("TimeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
