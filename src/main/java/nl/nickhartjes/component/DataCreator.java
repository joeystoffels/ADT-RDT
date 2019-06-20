package nl.nickhartjes.component;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.Measurement;
import nl.nickhartjes.persistence.Persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class DataCreator {

    private final Persistence persistence;
    private final DataCreatorConfig config;

    private Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.

    public DataCreator(DataCreatorConfig config, Persistence persistence) {
        this.config = config;
        this.persistence = persistence;
    }

    public void execute() {

        double randomNum;
        double startValue = config.getStartValue();
        double upperBoundValue = config.getUpperBoundValue();
        List<Measurement> measurements = new ArrayList<>();
        long startTime = System.nanoTime();
        int batchCounter = 0;

        for (int count = 0; count < config.getNrDataPoints(); count++) {
            randomNum = getRandomNum(startValue, upperBoundValue);
            measurements.add(new Measurement(calendar, randomNum));

            if ((count % config.getBatchSize()) == 0) {
                batchCounter++;

                log.info("------------------------------------------------------------");
                log.info("------- Batch nr " + batchCounter + ", Total data entries: " + count);
                log.info("------------------------------------------------------------");

                persistence.save(measurements);
                measurements.clear();

                log.info("End of batch nr "+ batchCounter + ", " + config.getBatchSize() + " entries added to databases! \n");
            }

            calendar.add(Calendar.SECOND, 1);
            startValue = getMin(randomNum);
            upperBoundValue = getMax(randomNum);
        }

        log.info("Total duration: " + (System.nanoTime() - startTime) / 1000000 + "ms \n" );
    }

    private double getMin(double nr) {
        return nr / 100 * 95;
    }

    private double getMax(double nr) {
        return nr / 100 * 102;
    }

    private double getRandomNum(double startValue, double upperBoundValue) {
        return ThreadLocalRandom.current().nextDouble(startValue, upperBoundValue + 1);
    }
}