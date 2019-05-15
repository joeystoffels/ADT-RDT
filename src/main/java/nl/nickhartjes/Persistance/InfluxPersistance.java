package nl.nickhartjes.Persistance;

import com.mongodb.client.MongoCollection;
import nl.nickhartjes.Models.Measurement;
import org.bson.Document;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfluxPersistance extends PersistanceAdapter {

  private InfluxDB influxDB;
  private MongoCollection<Document> collection;

  public InfluxPersistance() {
    super();

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
      this.influxDB.write("han", "autogen", point);
    }
  }

  @Override
  public void close() {
    this.influxDB.close();
  }
}
