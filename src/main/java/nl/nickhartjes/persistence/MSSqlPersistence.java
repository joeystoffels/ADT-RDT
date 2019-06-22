package nl.nickhartjes.persistence;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.exceptions.DatabaseError;
import nl.nickhartjes.models.Measurement;

import java.sql.*;
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
        String insertStatement = "INSERT INTO dbo." + collection + " (timestamp, value) VALUES (?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertStatement)) {
            connection.setAutoCommit(false);

            // Insert sample records
            for (Measurement measurement : measurements) {
                java.sql.Date date = new java.sql.Date(measurement.getTimestamp().getTime().getTime());
                stmt.setDate(1, date);
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
        long readDuration = 0;
        String readStatement = "SELECT * FROM Crypto";

        try (PreparedStatement stmt = connection.prepareStatement(readStatement)) {
            long readStartTime = System.nanoTime();

            stmt.execute();

            readDuration = System.nanoTime() - readStartTime;
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
            stmt.execute("DELETE FROM crypto");
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

}
