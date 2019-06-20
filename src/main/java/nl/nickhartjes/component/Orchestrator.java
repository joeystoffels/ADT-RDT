package nl.nickhartjes.component;

import lombok.extern.slf4j.Slf4j;
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

    private long nrDataEntries;
    private int batchSize;

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

        persistence = new Persistence();
        String collection = properties.getProperty("collection");

        persistence.add(new MongoPersistence(properties.getProperty("mongo.URI"), properties.getProperty("mongo.database"), collection));
        persistence.add(new InfluxPersistence(properties.getProperty("influxdb.URI"), properties.getProperty("influxdb.username"), properties.getProperty("influxdb.password"), collection));
        persistence.add(new MSSqlPersistence(properties.getProperty("mssql.URI"), collection));

        DatabaseTestConfig dataCreatorConfig = new DatabaseTestConfig(nrDataEntries, batchSize, 10, 11);
        databaseTest = new DatabaseTest(dataCreatorConfig, persistence);

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
            log.info("Avg batch write time:   " + stats.getAverageBatchWriteTime() / 1000000 + "ms");
            log.info("Avg read all time:      " + stats.getAverageReadTime() / 1000000 + "ms");
            log.info("Total batch write time: " + stats.getTotalBatchWriteTime() + "ms");
            log.info("Total read all time:    " + stats.getTotalReadAllTime() + "ms \n");
        }


    }

}
