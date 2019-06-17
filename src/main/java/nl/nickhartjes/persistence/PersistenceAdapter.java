package nl.nickhartjes.persistence;

import nl.nickhartjes.models.Measurement;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public abstract class PersistenceAdapter {

  protected Properties properties;

  private InputStream inputStream;

  public PersistenceAdapter() {
    this.loadProperties();
  }

  private void loadProperties() {
    try {
      this.properties = new Properties();
      InputStream in = PersistenceAdapter.class.getResourceAsStream("/application.properties");
      this.properties.load(in);
      in.close();
    } catch (IOException e) {
      throw new AssertionError("Could not load application.properties.");
    }
  }

  public abstract void save(List<Measurement> measurements);

  public abstract void close();

  public abstract List<Long> getWriteTimes();
}
