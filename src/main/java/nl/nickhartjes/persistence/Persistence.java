package nl.nickhartjes.persistence;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.exceptions.DatabaseError;
import nl.nickhartjes.exporter.ExportAdapter;
import nl.nickhartjes.exporter.Exporter;
import nl.nickhartjes.models.Measurement;
import nl.nickhartjes.models.StatisticEntry;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Persistence {

    private List<PersistenceAdapter> persistenceAdapters;

    public Persistence() {
        persistenceAdapters = new ArrayList<>();
    }

    public void add(PersistenceAdapter persistenceAdapter) {
        this.persistenceAdapters.add(persistenceAdapter);
    }

    public void save(List<Measurement> measurementList, int batch, int batchSize, Exporter exporter) {
        try {
            for (PersistenceAdapter persistenceAdapter : persistenceAdapters) {
                long startTimestamp = System.nanoTime();
                long executionTime = persistenceAdapter.save(measurementList);
                long endTimestamp = System.nanoTime();

                StatisticEntry entry = new StatisticEntry(batch,
                        batchSize,
                        persistenceAdapter.getClass().getSimpleName(),
                        "batch write",
                        executionTime,
                        startTimestamp,
                        endTimestamp);
                for (ExportAdapter exportAdapter : exporter.getExportAdapters()) {
                    exportAdapter.addStatisticsEntry(entry);
                }
            }
        } catch (DatabaseError e) {
            log.warn(e.getAdapter().getClass().getSimpleName() + " has been removed from the test!");
            persistenceAdapters.remove(e.getAdapter());
        }
    }

    public void close() {
        for (PersistenceAdapter persistenceAdapter : persistenceAdapters) {
            persistenceAdapter.close();
        }
    }

    public void drop() {
        for (PersistenceAdapter persistenceAdapter : persistenceAdapters) {
            persistenceAdapter.drop();
        }
    }

    public void readAll(int batch, int batchSize, Exporter exporter) {
        try {
            for (PersistenceAdapter persistenceAdapter : persistenceAdapters) {
                long startTimestamp = System.nanoTime();
                long executionTime = persistenceAdapter.readAll();
                long endTimestamp = System.nanoTime();

                StatisticEntry entry = new StatisticEntry(batch,
                        batchSize,
                        persistenceAdapter.getClass().getSimpleName(),
                        "read all",
                        executionTime,
                        startTimestamp,
                        endTimestamp);
                for (ExportAdapter exportAdapter : exporter.getExportAdapters()) {
                    exportAdapter.addStatisticsEntry(entry);
                }
            }
        } catch (DatabaseError e) {
            e.getAdapter().close();
            log.warn(e.getAdapter().getClass().getSimpleName() + " has been removed from the test!");
            persistenceAdapters.remove(e.getAdapter());
        }
    }

    public List<PersistenceAdapter> getPersistenceAdapters() {
        return persistenceAdapters;
    }

}


