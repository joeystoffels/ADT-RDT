package nl.nickhartjes.persistence;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.Measurement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class MSSqlPersistence extends PersistenceAdapter {

    private List<Long> writeTimes = new ArrayList<>();
    private List<Long> readTimes = new ArrayList<>();

    private final String collectionName = this.getProperties().getProperty("collection");

    private Connection connection;

    public MSSqlPersistence() {
        try {
            String connectionUrl = getProperties().getProperty("mssql.URI");
            connection = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void save(List<Measurement> measurements) {

        String insertStatement = "INSERT INTO dbo." + collectionName + " (timestamp, value) VALUES (?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertStatement)) {
            log.info("********** MSSQL actions **********");
            connection.setAutoCommit(false);

            // Insert sample records
            for (Measurement measurement : measurements) {

                java.sql.Date date = new java.sql.Date(measurement.getTimestamp().getTime().getTime());
                stmt.setDate(1, date);
                stmt.setDouble(2, measurement.getValue());

                // Add statement to batch
                stmt.addBatch();
            }
            // execute batch
            long startTime = System.nanoTime();

            stmt.executeBatch();
            connection.commit();

            long writeDuration = System.nanoTime() - startTime;
            logWriteDuration("MSSQL", writeDuration);
            writeTimes.add(writeDuration);

            long readStartTime = System.nanoTime();

            readAll();

            long readDuration = System.nanoTime() - readStartTime;
            logReadDuration("MSSQL", readDuration);
            readTimes.add(readDuration);

        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void readAll() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeQuery("SELECT * FROM Crypto");
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void drop() {
        log.info("MSSQL deleting entries...");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM crypto");
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

}
