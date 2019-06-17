package nl.nickhartjes;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.component.DataCreator;
import nl.nickhartjes.component.DataCreatorConfig;
import nl.nickhartjes.persistence.MSSqlPersistence;
import nl.nickhartjes.persistence.Persistence;

@Slf4j
public class Main {

  public static void main(String[] args) {

    Persistence persistence = new Persistence();
    //persistence.add(new MongoPersistance());
    //persistence.add(new InfluxPersistence());
    persistence.add(new MSSqlPersistence());

    DataCreatorConfig dataCreatorConfig = new DataCreatorConfig(10000000L, 100, 10, 11);

    DataCreator dataCreator = new DataCreator(dataCreatorConfig, persistence);
    dataCreator.execute();

    persistence.close();

    log.info("" + persistence.fetchStats());
  }

}
