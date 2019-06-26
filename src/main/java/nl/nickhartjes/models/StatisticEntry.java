package nl.nickhartjes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.nickhartjes.component.Converter;

@Data
@AllArgsConstructor
public class StatisticEntry {

    private int batch;
    private int batchSize;
    private String name;
    private String action;
    private long nanoseconds;
    private long milliseconds;
    private long seconds;
    private long startTimestamp;
    private long endTimestamp;

    public StatisticEntry(int batch, int batchSize, String name, String action, long nanoseconds, long startTimestamp, long endTimestamp) {
        this.batch = batch;
        this.batchSize = batchSize;
        this.name = name;
        this.action = action;
        this.nanoseconds = nanoseconds;
        this.milliseconds = Converter.nanosecondsToMilliseconds(nanoseconds);
        this.seconds = Converter.nanosecondsToSeconds(nanoseconds);
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }
}
