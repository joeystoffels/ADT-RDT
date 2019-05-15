package nl.nickhartjes.Persistance;

import nl.nickhartjes.Models.Measurement;

import java.util.ArrayList;
import java.util.List;

public class Persistance {

  private List<PersistanceAdapter> persistanceAdapters;

  public Persistance() {
    persistanceAdapters = new ArrayList<>();
  }

  public void add(PersistanceAdapter persistanceAdapter){
    this.persistanceAdapters.add(persistanceAdapter);
  }

  public void save(List<Measurement> measurementList) {
    for (PersistanceAdapter persistanceAdepter : persistanceAdapters ) {
        persistanceAdepter.save(measurementList);
    }
  }

  public void close() {
    for (PersistanceAdapter persistanceAdepter : persistanceAdapters ) {
      persistanceAdepter.close();
    }
  }
}
