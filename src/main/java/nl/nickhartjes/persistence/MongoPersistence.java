package nl.nickhartjes.persistence;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.Measurement;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class MongoPersistence implements PersistenceAdapter {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> collection;

    private List<Long> writeTimes = new ArrayList<>();
    private List<Long> readTimes = new ArrayList<>();

    public MongoPersistence(String mongoURI, String databaseName, String collectionName) {
        mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
    }

    @Override
    public void save(List<Measurement> measurements) {
        log.info("********** MONGO actions **********");

        List<Document> documents = new ArrayList<>();

        for (Measurement measurement : measurements) {
            Document document = new Document();
            document.append("timestamp", measurement.getTimestamp().getTime());
            document.append("value", measurement.getValue());
            documents.add(document);
        }

        if (!documents.isEmpty()) {
            long writeStartTime = System.nanoTime();

            this.collection.insertMany(documents);

            long writeDuration = System.nanoTime() - writeStartTime;
            log.info("Mongo batch write: " + writeDuration + "ns, " + writeDuration / 1000000 + "ms, " + writeDuration / 1000000000 + "s");
            writeTimes.add(writeDuration);
        }
    }

    @Override
    public void readAll() {
        long readStartTime = System.nanoTime();

        this.collection.find().iterator();

        long readDuration = System.nanoTime() - readStartTime;
        log.info("Mongo read all: " + readDuration + "ns, " + readDuration / 1000000 + "ms, " + readDuration / 1000000000 + "s");
        readTimes.add(readDuration);
    }

    @Override
    public void close() {
        this.mongoClient.close();
    }

    @Override
    public void drop() {
        log.info("Mongo dropping collection...");
        this.collection.drop();
    }

}
