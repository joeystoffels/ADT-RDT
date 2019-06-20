package nl.nickhartjes.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Stats {

    private final String persistenceType;
    private final long nrDataPoints;
    private final int batchSize;
    private final List<Long> batchWriteTimes;
    private final List<Long> cumulativeWriteTimes;
    private final List<Long> readAllTimes;
    private final double averageBatchWriteTime;

    public Stats(String persistenceType, long nrDataPoints, int batchSize, List<Long> batchWriteTimes, List<Long> readAllTimes) {
        this.persistenceType = persistenceType;
        this.nrDataPoints = nrDataPoints;
        this.batchSize = batchSize;
        this.batchWriteTimes = batchWriteTimes;
        this.cumulativeWriteTimes = getCumulativeWriteTimes(batchWriteTimes);
        this.readAllTimes = readAllTimes;
        this.averageBatchWriteTime = cumulativeWriteTimes.get(cumulativeWriteTimes.size()-1) / ((double) nrDataPoints / batchSize);
    }

    private List<Long> getCumulativeWriteTimes(List<Long> batchWriteTimes) {
        long counter = 0;
        List<Long> list = new ArrayList<>();

        for (long batchWriteTime : batchWriteTimes) {
            counter = counter + batchWriteTime;
            list.add(counter);
        }

        return list;
    }
}
