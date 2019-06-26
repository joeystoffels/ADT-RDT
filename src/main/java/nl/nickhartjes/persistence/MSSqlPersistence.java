package nl.nickhartjes.persistence;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.exceptions.DatabaseError;
import nl.nickhartjes.models.Measurement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class MSSqlPersistence implements PersistenceAdapter {

    private final String collection;
    private final Connection connection;

    private List<Long> writeTimes = new ArrayList<>();
    private List<Long> readTimes = new ArrayList<>();

    public MSSqlPersistence(String uri, String collection) {
        this.collection = collection;

        try {
            connection = DriverManager.getConnection(uri);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new AssertionError("No MSSQL connection available!");
        }
    }

    @Override
    public long save(List<Measurement> measurements) {
        long writeDuration = 0;
        String insertStatement = "INSERT INTO " + collection + " (timestamp, value) VALUES (?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertStatement)) {
            connection.setAutoCommit(false);

            // Insert sample records
            for (Measurement measurement : measurements) {
                java.sql.Timestamp timestamp = new java.sql.Timestamp(measurement.getTimestamp().getTime());
                stmt.setTimestamp(1, timestamp);
                stmt.setDouble(2, measurement.getValue());

                // Add statement to batch
                stmt.addBatch();
            }

            // writeAndReadData batch
            long startTime = System.nanoTime();

            stmt.executeBatch();
            connection.commit();

            writeDuration = System.nanoTime() - startTime;
            writeTimes.add(writeDuration);
        } catch (SQLException e) {
            log.error("Exception occurred when saving batch to MSSQL!" + e.getMessage());
            throw new DatabaseError(e, this);
        }

        return writeDuration;
    }

    @Override
    public long readAll() {
        long readDuration;
        String readStatement = "SELECT * FROM " + collection;

        try (PreparedStatement stmt = connection.prepareStatement(readStatement)) {
            stmt.closeOnCompletion();
            long readStartTime = System.nanoTime();

            ResultSet result = stmt.executeQuery();

            readDuration = System.nanoTime() - readStartTime;

            result.close();
            readTimes.add(readDuration);
        } catch (SQLException e) {
            log.error("Exception occurred when reading all data from MSSQL!" + e.getMessage());
            throw new DatabaseError(e, this);
        }
        return readDuration;
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
            stmt.execute("TRUNCATE TABLE " + collection);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

}
