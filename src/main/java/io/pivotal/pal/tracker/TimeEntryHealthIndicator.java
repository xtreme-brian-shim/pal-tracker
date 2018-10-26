package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {

    private final TimeEntryRepository timeEntryRepository;

    public TimeEntryHealthIndicator(TimeEntryRepository timeEntryRepo) {
        this.timeEntryRepository = timeEntryRepo;
    }

    @Override
    public Health health() {
        if (timeEntryRepository.list().size() < 5) {
            return Health.up().build();
        }
            return Health.down().build();
    }

}
