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
public class InfluxPersistence extends PersistenceAdapter {

    private InfluxDB influxDB;
//    private MongoCollection<Document> collection;

    private List<Long> writeTimes = new ArrayList<>();
    private List<Long> readTimes = new ArrayList<>();

    public InfluxPersistence() {
        String influxUri = this.getProperties().getProperty("influxdb.URI");
        String databaseUser = this.getProperties().getProperty("influxdb.username");
        String databasePassword = this.getProperties().getProperty("influxdb.password");
//    int batchSize = Integer.parseInt(this.properties.getProperty("batchSize"));

        influxDB = InfluxDBFactory.connect(influxUri, databaseUser, databasePassword);
        influxDB.enableBatch(BatchOptions.DEFAULTS.actions(1000).flushDuration(100));
//    influxDB.enableBatch(batchSize, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void save(List<Measurement> measurements) {
        log.info("********** INFLUX actions **********");

        long startTime = System.nanoTime();

        for (Measurement measurement : measurements) {
            Point point =
                    Point.measurement("crypto")
                            .time(measurement.getTimestamp().getTimeInMillis(), TimeUnit.MILLISECONDS)
                            .addField("value", measurement.getValue())
                            .build();

            this.influxDB.write("han", "autogen", point);
        }

        long writeDuration = System.nanoTime() - startTime;
        logWriteDuration("Influx", writeDuration);
        writeTimes.add(writeDuration);

        long readStartTime = System.nanoTime();

        readAll();

        long readDuration = System.nanoTime() - readStartTime;
        logReadDuration("Influx", readDuration);
        readTimes.add(readDuration);
    }

    @Override
    public void readAll() {
        Query query = new Query("SELECT * FROM Crypto", "crypto");
        influxDB.query(query);
    }

    @Override
    public void close() {
        this.influxDB.close();
    }

    @Override
    public void drop() {
        log.info("Influx deleting entries...");
        Query query = new Query("DELETE FROM Crypto", "crypto");
        influxDB.query(query);
    }

}
