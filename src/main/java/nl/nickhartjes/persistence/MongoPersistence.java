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
    public long save(List<Measurement> measurements) {
        long writeDuration = 0;

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

            writeDuration = System.nanoTime() - writeStartTime;
            writeTimes.add(writeDuration);
        }
        return writeDuration;
    }

    @Override
    public long readAll() {
        long readStartTime = System.nanoTime();

        this.collection.find().iterator();

        long readDuration = System.nanoTime() - readStartTime;
        readTimes.add(readDuration);
        return readDuration;
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
