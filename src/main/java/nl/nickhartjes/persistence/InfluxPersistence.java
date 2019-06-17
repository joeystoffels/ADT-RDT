package nl.nickhartjes.persistence;

import com.mongodb.client.MongoCollection;
import nl.nickhartjes.models.Measurement;
import org.bson.Document;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfluxPersistence extends PersistenceAdapter {

  private InfluxDB influxDB;
  private MongoCollection<Document> collection;

  private List<Long> writeTimes = new ArrayList<>();

  public InfluxPersistence() {
    String influxUri = this.properties.getProperty("influxdb.URI");
    String databaseUser = this.properties.getProperty("influxdb.username");
    String databasePassword = this.properties.getProperty("influxdb.password");
    int batchSize = Integer.parseInt(this.properties.getProperty("batchSize"));

    influxDB = InfluxDBFactory.connect(influxUri, databaseUser, databasePassword);
    influxDB.enableBatch(BatchOptions.DEFAULTS.actions(10000).flushDuration(100));
//    influxDB.enableBatch(batchSize, 100, TimeUnit.MILLISECONDS);
  }

  @Override
  public void save(List<Measurement> measurements) {
    for (Measurement measurement : measurements) {
      Point point =
          Point.measurement("crypto")
              .time(measurement.getTimestamp().getTimeInMillis(), TimeUnit.MILLISECONDS)
              .addField("value", measurement.getValue())
              .build();

      Date startDate = new Date();

      this.influxDB.write("han", "autogen", point);

      Date endDate = new Date();
      writeTimes.add(endDate.getTime() - startDate.getTime());
    }
  }

  @Override
  public void close() {
    this.influxDB.close();
  }

  @Override
  public List<Long> getWriteTimes() {
    return writeTimes;
  }
}
