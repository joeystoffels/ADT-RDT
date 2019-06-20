package nl.nickhartjes.persistence;

import nl.nickhartjes.models.Measurement;

import java.util.List;

public interface PersistenceAdapter {

    void save(List<Measurement> measurements);
    void readAll();
    void close();
    void drop();
    List<Long> getWriteTimes();
    List<Long> getReadTimes();

}
