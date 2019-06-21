package nl.nickhartjes.statistics;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.StatisticEntry;

import java.text.NumberFormat;
import java.util.Locale;

@Slf4j
public class LogExporter implements ExportAdapter {

    private int batchCounter = -1;

    @Override
    public void addStatistiscsEntry(StatisticEntry statisticEntry) {

        if (batchCounter != statisticEntry.getBatch()) {
            this.batchCounter = statisticEntry.getBatch();
            log.info("------------------------------------------------------------");
            log.info("------- Batch nr " + batchCounter + "   Total: " + NumberFormat.getNumberInstance(Locale.US).format(batchCounter * statisticEntry.getBatchSize()));
            log.info("------------------------------------------------------------");
        }

        log.info("Name: {}, Action: {}, NanoSeconds: {}, Miliseconds: {}, Seconds: {}",
                statisticEntry.getName(),
                statisticEntry.getAction(),
                statisticEntry.getNanoseconds(),
                statisticEntry.getMiliseconds(),
                statisticEntry.getSeconds());
    }

    @Override
    public void finish() {

    }
}
