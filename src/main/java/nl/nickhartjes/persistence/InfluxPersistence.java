package nl.nickhartjes.persistence;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.exceptions.DatabaseError;
import nl.nickhartjes.models.Measurement;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
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

    public InfluxPersistence(String influxUri, String databaseUser, String databasePassword, String collection, int batchSize) {
        this.collection = collection;

        influxDB = InfluxDBFactory.connect(influxUri, databaseUser, databasePassword);
        influxDB.enableBatch(BatchOptions.DEFAULTS.actions(batchSize).flushDuration(100));
    }

    @Override
    public long save(List<Measurement> measurements) {
        long writeDuration = 0;

        try {
            long startTime = System.nanoTime();

            for (Measurement measurement : measurements) {
                Point point =
                        Point.measurement(collection)
                                .time(measurement.getTimestamp().getTimeInMillis(), TimeUnit.MILLISECONDS)
                                .addField("value", measurement.getValue())
                                .build();

                this.influxDB.write("han", "autogen", point);
            }

            writeDuration = System.nanoTime() - startTime;
            writeTimes.add(writeDuration);
        } catch (InfluxDBException e) {
            log.error("Exception occurred when saving batch to Influx!" + e.getMessage());
            throw new DatabaseError(e, this);
        }

        return writeDuration;
    }

    @Override
    public long readAll() {
        long readDuration = 0;

        try {
            long readStartTime = System.nanoTime();

            Query query = new Query("SELECT * FROM Crypto", collection);
            influxDB.query(query);

            readDuration = System.nanoTime() - readStartTime;
            readTimes.add(readDuration);
        } catch (InfluxDBException e) {
            log.error("Exception occurred when reading all data from Influx!" + e.getMessage());
            throw new DatabaseError(e, this);
        }

        return readDuration;
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
