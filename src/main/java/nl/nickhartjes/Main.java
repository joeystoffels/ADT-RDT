package nl.nickhartjes;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.component.DataCreator;
import nl.nickhartjes.component.DataCreatorConfig;
import nl.nickhartjes.models.Stats;
import nl.nickhartjes.persistence.InfluxPersistence;
import nl.nickhartjes.persistence.MSSqlPersistence;
import nl.nickhartjes.persistence.MongoPersistence;
import nl.nickhartjes.persistence.Persistence;
import nl.nickhartjes.persistence.PersistenceAdapter;

import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {

        Persistence persistence = new Persistence();
        persistence.add(new MongoPersistence());
        persistence.add(new InfluxPersistence());
        persistence.add(new MSSqlPersistence());

        persistence.drop();

        DataCreatorConfig dataCreatorConfig = new DataCreatorConfig(2000L, 1000, 10, 11);

        DataCreator dataCreator = new DataCreator(dataCreatorConfig, persistence);
        dataCreator.execute();

        persistence.close();

        for (PersistenceAdapter persistenceAdapter : persistence.getPersistenceAdapters()) {

            List<Long> batchWriteTimes = persistenceAdapter.getWriteTimes();
            List<Long> readAllTimes = persistenceAdapter.getReadTimes();

            Stats stats = new Stats(persistenceAdapter.getClass().getSimpleName(), 2000L, 1000, batchWriteTimes, readAllTimes);

            log.info("Persistence Type:       " + stats.getPersistenceType());
            log.info("Avg batch write time:   " + stats.getAverageBatchWriteTime() / 1000000 + "ms");
            log.info("Total batch write time: " + stats.getCumulativeWriteTimes().get(stats.getCumulativeWriteTimes().size() - 1) / 1000000 + "ms");
            log.info("Total read all time:    " + stats.getReadAllTimes().get(stats.getReadAllTimes().size() - 1) / 1000000 + "ms \n");
        }


    }

}
