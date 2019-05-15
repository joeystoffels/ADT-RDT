package nl.nickhartjes.Persistance;

import nl.nickhartjes.Models.Measurement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MSSqlPersistance extends PersistanceAdapter {

  private Connection connection;

  public MSSqlPersistance() {
    super();
  }

  @Override
  public void save(List<Measurement> measurements) {

    String collectionName = this.properties.getProperty("collection");
    String insertStatement =
        "INSERT INTO dbo." + collectionName + " (timestamp, value) VALUES (?,?)";

    try (Connection conn = getConnection()) {

      conn.setAutoCommit(false);
      try (PreparedStatement stmt = conn.prepareStatement(insertStatement)) {

        // Insert sample records
        for (Measurement measurement : measurements) {

          java.sql.Date date = new java.sql.Date(measurement.getTimestamp().getTime().getTime());
          stmt.setDate(1, date);
          stmt.setDouble(2, measurement.getValue());

          // Add statement to batch
          stmt.addBatch();
        }
        // execute batch
        stmt.executeBatch();
        conn.commit();
        System.out.println("Transaction is commited successfully.");
      } catch (SQLException e) {
        e.printStackTrace();
        if (conn != null) {
          try {
            System.out.println("Transaction is being rolled back.");
            conn.rollback();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() {
    try {
      this.connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private Connection getConnection() {
    try {
      String connectionUrl = this.properties.getProperty("mssql.URI");
      return DriverManager.getConnection(connectionUrl);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
