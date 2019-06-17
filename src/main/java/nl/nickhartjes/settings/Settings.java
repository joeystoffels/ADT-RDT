package nl.nickhartjes.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {

  protected Properties properties;

  public Settings() {
    this.loadProperties();
  }

  private void loadProperties() {
    try {
      this.properties = new Properties();
      InputStream in = Settings.class.getResourceAsStream("/application.properties");
      this.properties.load(in);
      in.close();
    } catch (IOException e) {
      throw new AssertionError("Could not load application.properties.");
    }
  }

  public String get(String settingsName) {
    return this.properties.getProperty(settingsName);
  }
}
