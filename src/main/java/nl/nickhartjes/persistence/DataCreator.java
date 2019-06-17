package nl.nickhartjes.persistence;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.Measurement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        double min = config.getStartValue();
        double max = config.getUpperBoundValue();
        List<Measurement> measurements = new ArrayList<>();
        Date startDate = new Date();

        for (int count = 0; count < config.getNrDataPoints(); count++) {
            randomNum = ThreadLocalRandom.current().nextDouble(min, max + 1);

            measurements.add(new Measurement(calendar, randomNum));

            if ((count % config.getBatchSize()) == 0) {
                //log.info("Count: " + count);
                persistence.save(measurements);
                measurements.clear();
            }

            calendar.add(Calendar.SECOND, 1);
            min = getMin(randomNum);
            max = getMax(randomNum);
        }

        Date endDate = new Date();
        long duration = endDate.getTime() - startDate.getTime();

        log.info("duration: " + duration + "ms");
    }

    private double getMin(double nr) {
        return nr / 100 * 95;
    }

    private double getMax(double nr) {
        return nr / 100 * 102;
    }
}
