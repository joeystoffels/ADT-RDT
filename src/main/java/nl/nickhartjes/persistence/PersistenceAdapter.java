package nl.nickhartjes.persistence;

import nl.nickhartjes.models.Measurement;

import java.util.List;

public interface PersistenceAdapter {

    long save(List<Measurement> measurements);

    long readAll();
    void close();
    void drop();
    List<Long> getWriteTimes();
    List<Long> getReadTimes();

}
