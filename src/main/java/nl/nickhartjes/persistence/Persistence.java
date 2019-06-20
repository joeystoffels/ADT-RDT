package nl.nickhartjes.persistence;

import nl.nickhartjes.models.Measurement;

import java.util.ArrayList;
import java.util.List;

public class Persistence {

    private List<PersistenceAdapter> persistenceAdapters;

    public Persistence() {
        persistenceAdapters = new ArrayList<>();
    }

    public void add(PersistenceAdapter persistenceAdapter) {
        this.persistenceAdapters.add(persistenceAdapter);
    }

    public void save(List<Measurement> measurementList) {
        for (PersistenceAdapter persistenceAdepter : persistenceAdapters) {
            persistenceAdepter.save(measurementList);
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

    public void readAll() {
        for (PersistenceAdapter persistenceAdapter : persistenceAdapters) {
            persistenceAdapter.readAll();
        }
    }

    public List<PersistenceAdapter> getPersistenceAdapters() {
        return persistenceAdapters;
    }

}


