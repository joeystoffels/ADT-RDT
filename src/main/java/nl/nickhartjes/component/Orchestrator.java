package nl.nickhartjes.component;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.exporter.CsvExporter;
import nl.nickhartjes.exporter.Exporter;
import nl.nickhartjes.exporter.LogExporter;
import nl.nickhartjes.models.DatabaseTestConfig;
import nl.nickhartjes.models.Statistics;
import nl.nickhartjes.persistence.InfluxPersistence;
import nl.nickhartjes.persistence.MSSqlPersistence;
import nl.nickhartjes.persistence.MongoPersistence;
import nl.nickhartjes.persistence.Persistence;
import nl.nickhartjes.persistence.PersistenceAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Slf4j
public class Orchestrator {

    private Persistence persistence;
    private DatabaseTest databaseTest;

    private final long nrDataEntries;
    private final int batchSize;

    public Orchestrator() {
        Properties properties = new Properties();

        try {
            InputStream in = PersistenceAdapter.class.getResourceAsStream("/application.properties");
            properties.load(in);
            in.close();
        } catch (IOException e) {
            throw new AssertionError("Could not load application.properties.");
        }

        this.nrDataEntries = Long.valueOf(properties.getProperty("nrDataEntries"));
        this.batchSize = Integer.valueOf(properties.getProperty("batchSize"));
        int startValue = Integer.parseInt(properties.getProperty("startValue"));
        int upperBoundValue = Integer.parseInt(properties.getProperty("upperBoundValue"));

        persistence = new Persistence();
        String collection = properties.getProperty("collection");
        String database = properties.getProperty("database");

        persistence.add(new MongoPersistence(properties.getProperty("mongo.URI"), database, collection));
        persistence.add(new InfluxPersistence(properties.getProperty("influxdb.URI"), properties.getProperty("influxdb.username"), properties.getProperty("influxdb.password"), database, batchSize));
        persistence.add(new MSSqlPersistence(properties.getProperty("mssql.URI"), collection));

        Exporter exporter = new Exporter();
        exporter.add(new LogExporter());
        //exporter.add(new ExcelExporter());
        exporter.add(new CsvExporter());

        DatabaseTestConfig dataCreatorConfig = new DatabaseTestConfig(nrDataEntries, batchSize, startValue, upperBoundValue);
        databaseTest = new DatabaseTest(dataCreatorConfig, persistence, exporter);

        start();
    }

    private void start() {
        persistence.drop();

        databaseTest.writeAndReadData();

        persistence.close();

        for (PersistenceAdapter persistenceAdapter : persistence.getPersistenceAdapters()) {

            List<Long> batchWriteTimes = persistenceAdapter.getWriteTimes();
            List<Long> readAllTimes = persistenceAdapter.getReadTimes();

            Statistics stats = new Statistics(persistenceAdapter.getClass().getSimpleName(), nrDataEntries, batchSize, batchWriteTimes, readAllTimes);

            log.info("Persistence Type:       " + stats.getPersistenceType());
            log.info("Avg batch write time:   " + Converter.nanosecondsToMilliseconds(stats.getAverageBatchWriteTime()) + "ms");
            log.info("Avg read all time:      " + Converter.nanosecondsToMilliseconds(stats.getAverageReadTime()) + "ms");
            log.info("Total batch write time: " + stats.getTotalBatchWriteTime() + "ms");
            log.info("Total read all time:    " + stats.getTotalReadAllTime() + "ms \n");
        }
    }
}
