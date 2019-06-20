package nl.nickhartjes.persistence;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.Measurement;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class InfluxPersistence implements PersistenceAdapter {

    private final InfluxDB influxDB;
    private final String collection;

    private List<Long> writeTimes = new ArrayList<>();
    private List<Long> readTimes = new ArrayList<>();

    public InfluxPersistence(String influxUri, String databaseUser, String databasePassword, String collection) {
        this.collection = collection;

        influxDB = InfluxDBFactory.connect(influxUri, databaseUser, databasePassword);
        influxDB.enableBatch(BatchOptions.DEFAULTS.actions(1000).flushDuration(100));

        // TODO?
        //    int batchSize = Integer.parseInt(this.properties.getProperty("batchSize"));
        //    influxDB.enableBatch(batchSize, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void save(List<Measurement> measurements) {
        log.info("********** INFLUX actions **********");

        long startTime = System.nanoTime();

        for (Measurement measurement : measurements) {
            Point point =
                    Point.measurement(collection)
                            .time(measurement.getTimestamp().getTimeInMillis(), TimeUnit.MILLISECONDS)
                            .addField("value", measurement.getValue())
                            .build();

            this.influxDB.write("han", "autogen", point);
        }

        long writeDuration = System.nanoTime() - startTime;
        log.info("Influx batch write: " + writeDuration + "ns, " + writeDuration / 1000000 + "ms, " + writeDuration / 1000000000 + "s");
        writeTimes.add(writeDuration);
    }

    @Override
    public void readAll() {
        long readStartTime = System.nanoTime();

        Query query = new Query("SELECT * FROM Crypto", collection);
        influxDB.query(query);

        long readDuration = System.nanoTime() - readStartTime;
        log.info("Influx read all: " + readDuration + "ns, " + readDuration / 1000000 + "ms, " + readDuration / 1000000000 + "s");
        readTimes.add(readDuration);
    }

    @Override
    public void close() {
        this.influxDB.close();
    }

    @Override
    public void drop() {
        log.info("Influx deleting entries...");
        Query query = new Query("DELETE FROM Crypto", collection);
        influxDB.query(query);
    }

}
