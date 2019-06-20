package nl.nickhartjes.persistence;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.Measurement;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Slf4j
@Getter
public abstract class PersistenceAdapter {

    private final Properties properties;

    PersistenceAdapter() {
        try {
            this.properties = new Properties();
            InputStream in = PersistenceAdapter.class.getResourceAsStream("/application.properties");
            this.properties.load(in);
            in.close();
        } catch (IOException e) {
            throw new AssertionError("Could not load application.properties.");
        }
    }

    void logWriteDuration(String name, long duration) {
        log.info(name + " batch write: " + duration + "ns, " + duration / 1000000 + "ms, " + duration / 1000000000 + "s");
    }

    void logReadDuration(String name, long duration) {
        log.info(name + " read all: " + duration + "ns, " + duration / 1000000 + "ms, " + duration / 1000000000 + "s");
    }

    public abstract void save(List<Measurement> measurements);

    public abstract void readAll();

    public abstract void close();

    public abstract void drop();

    public abstract List<Long> getWriteTimes();

    public abstract List<Long> getReadTimes();
}
