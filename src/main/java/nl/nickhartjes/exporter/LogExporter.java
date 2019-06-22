package nl.nickhartjes.exporter;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.StatisticEntry;

import java.text.NumberFormat;
import java.util.Locale;

@Slf4j
public class LogExporter implements ExportAdapter {

    private int batchCounter = -1;

    @Override
    public void addStatisticsEntry(StatisticEntry statisticEntry) {

        if (batchCounter != statisticEntry.getBatch()) {
            this.batchCounter = statisticEntry.getBatch();
            String total = NumberFormat.getNumberInstance(Locale.US).format(batchCounter * (long) statisticEntry.getBatchSize());
            log.info("------------------------------------------------------------");
            log.info("------- Batch nr " + batchCounter + "   Total: " + total);
            log.info("------------------------------------------------------------");
        }

        log.info("Name: {}, Action: {}, NanoSeconds: {}, MilliSeconds: {}, Seconds: {}",
                statisticEntry.getName(),
                statisticEntry.getAction(),
                statisticEntry.getNanoseconds(),
                statisticEntry.getMilliseconds(),
                statisticEntry.getSeconds());
    }

    @Override
    public void finish() {
        // Need implemtation
    }
}
