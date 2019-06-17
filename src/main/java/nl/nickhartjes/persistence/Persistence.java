package nl.nickhartjes.persistence;

import nl.nickhartjes.models.Measurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Persistence {

  private List<PersistenceAdapter> persistenceAdapters;

  public Persistence() {
    persistenceAdapters = new ArrayList<>();
  }

  public void add(PersistenceAdapter persistenceAdapter){
    this.persistenceAdapters.add(persistenceAdapter);
  }

  public void save(List<Measurement> measurementList) {
    for (PersistenceAdapter persistanceAdepter : persistenceAdapters) {
        persistanceAdepter.save(measurementList);
    }
  }

  public void close() {
    for (PersistenceAdapter persistenceAdapter : persistenceAdapters) {
      //persistanceAdapter.close();
    }
  }

  public Map<Class<?>, List<Long>> fetchStats() {
    Map<Class<?>, List<Long>> map = new HashMap<>();

    for (PersistenceAdapter persistenceAdapter : persistenceAdapters) {
      map.put(persistenceAdapter.getClass(), persistenceAdapter.getWriteTimes());
    }

    return map;
  }
}
