package nl.nickhartjes.exporter;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.StatisticEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class CsvExporter implements ExportAdapter {

    private Map<String, String> writers = new HashMap<>();

    private String createFileName(String name) {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        return path.substring(0, path.length() - 1) + "files" + File.separator + "data-" + name + "-" + timeStamp + ".csv";
    }

    @Override
    public void addStatisticsEntry(StatisticEntry statisticEntry) {

        try {
            String csvName = statisticEntry.getName() + '-' + statisticEntry.getAction();
            String fileLocation;
            if (statisticEntry.getBatch() == 1) {
                fileLocation = this.createFileName(csvName);
                this.writers.put(csvName, fileLocation);

            } else {
                fileLocation = writers.get(csvName);
            }
            File file = new File(fileLocation);
            CSVWriter writer = new CSVWriter(new FileWriter(file, true), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
            if (statisticEntry.getBatch() == 1) {
                String[] data = {"Batch nr.", "Batch Total", "Seconds", "Milliseconds", "Nanoseconds", "Start timestamp", "End timestamp"};
                writer.writeNext(data);
            }

            String[] data = {
                    Integer.toString(statisticEntry.getBatch()),
                    Integer.toString(statisticEntry.getBatch() * statisticEntry.getBatchSize()),
                    Long.toString(statisticEntry.getSeconds()),
                    Long.toString(statisticEntry.getMilliseconds()),
                    Long.toString(statisticEntry.getNanoseconds()),
                    Long.toString(statisticEntry.getStartTimestamp()),
                    Long.toString(statisticEntry.getEndTimestamp()),
            };
            writer.writeNext(data);
            writer.close();
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    @Override
    public void finish() {
        // Empty log start and endtime
    }
}
