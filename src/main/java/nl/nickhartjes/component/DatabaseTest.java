package nl.nickhartjes.component;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.exporter.ExportAdapter;
import nl.nickhartjes.exporter.Exporter;
import nl.nickhartjes.models.DatabaseTestConfig;
import nl.nickhartjes.models.Measurement;
import nl.nickhartjes.persistence.Persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class DatabaseTest {

    private final Persistence persistence;
    private final Exporter exporter;
    private final DatabaseTestConfig config;

    private final Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.

    DatabaseTest(DatabaseTestConfig config, Persistence persistence, Exporter exporter) {
        this.config = config;
        this.persistence = persistence;
        this.exporter = exporter;
    }

    void writeAndReadData() {
        // Setup
        double randomNum;
        double startValue = config.getStartValue();
        double upperBoundValue = config.getUpperBoundValue();
        List<Measurement> measurements = new ArrayList<>();
        long startTime = System.nanoTime();
        int batchCounter = 0;

        // Execute
        for (int count = 1; count <= config.getNrDataPoints(); count++) {
            randomNum = getRandomNum(startValue, upperBoundValue);
            measurements.add(new Measurement(new Timestamp(calendar.getTime().getTime()), randomNum));

            if ((count % config.getBatchSize()) == 0) {
                batchCounter++;

                persistence.save(measurements, batchCounter, config.getBatchSize(), exporter);
                persistence.readAll(batchCounter, config.getBatchSize(), exporter);

                measurements.clear();
            }

            calendar.add(Calendar.MILLISECOND, 10);
            startValue = getMin(randomNum);
            upperBoundValue = getMax(randomNum);
        }

        for (ExportAdapter exportAdapter : exporter.getExportAdapters()) {
            exportAdapter.finish();
        }

        long stopTime = System.nanoTime();

        log.info("Total duration: " + Converter.nanosecondsToMilliseconds(stopTime - startTime) + "ms \n");
    }

    private double getMin(double nr) {
        return nr / 100 * 95;
    }

    private double getMax(double nr) {
        return nr / 100 * 102;
    }

    private double getRandomNum(double startValue, double upperBoundValue) {
        return ThreadLocalRandom.current().nextDouble(startValue, upperBoundValue + 1);
    }
}
