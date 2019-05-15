package nl.nickhartjes.Persistance;

import nl.nickhartjes.Models.Measurement;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public abstract class PersistanceAdapter {

  protected Properties properties;

  private InputStream inputStream;

  public PersistanceAdapter() {
    this.loadProperties();
  }

  private void loadProperties() {
    try {
      this.properties = new Properties();
      InputStream in = PersistanceAdapter.class.getResourceAsStream("/application.properties");
      this.properties.load(in);
      in.close();
    } catch (IOException e) {
      throw new AssertionError("Could not load application.properties.");
    }
  }

  public abstract void save(List<Measurement> measurements);

  public abstract void close();
}
