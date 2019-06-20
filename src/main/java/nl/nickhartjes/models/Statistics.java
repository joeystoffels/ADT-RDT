package nl.nickhartjes.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Statistics {

    private final String persistenceType;
    private final long nrDataPoints;
    private final int batchSize;
    private final List<Long> batchWriteTimes;
    private final List<Long> cumulativeWriteTimes;
    private final List<Long> cumulativeReadTimes;
    private final List<Long> readAllTimes;
    private final double averageBatchWriteTime;
    private final double averageReadTime;
    private final long totalBatchWriteTime;
    private final long totalReadAllTime;

    public Statistics(String persistenceType, long nrDataPoints, int batchSize, List<Long> batchWriteTimes, List<Long> readAllTimes) {
        this.persistenceType = persistenceType;
        this.nrDataPoints = nrDataPoints;
        this.batchSize = batchSize;
        this.batchWriteTimes = batchWriteTimes;
        this.readAllTimes = readAllTimes;
        this.cumulativeWriteTimes = getCumulativeTimes(batchWriteTimes);
        this.cumulativeReadTimes = getCumulativeTimes(readAllTimes);
        this.averageBatchWriteTime = cumulativeWriteTimes.get(cumulativeWriteTimes.size()-1) / ((double) nrDataPoints / batchSize);
        this.averageReadTime = cumulativeReadTimes.get(cumulativeReadTimes.size()-1) / ((double) nrDataPoints / batchSize);
        this.totalBatchWriteTime = cumulativeWriteTimes.get(cumulativeWriteTimes.size() - 1) / 1000000;
        this.totalReadAllTime = cumulativeReadTimes.get(cumulativeReadTimes.size() - 1) / 1000000;
    }

    private List<Long> getCumulativeTimes(List<Long> batchWriteTimes) {
        long counter = 0;
        List<Long> list = new ArrayList<>();

        for (long batchWriteTime : batchWriteTimes) {
            counter = counter + batchWriteTime;
            list.add(counter);
        }

        return list;
    }
}
