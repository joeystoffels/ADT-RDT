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
import java.util.Iterator;
import java.util.List;

@Slf4j
@Getter
public class MongoPersistence extends PersistenceAdapter {

    private MongoClient mongoClient;
    private MongoCollection<Document> collection;

    private List<Long> writeTimes = new ArrayList<>();
    private List<Long> readTimes = new ArrayList<>();

    public MongoPersistence() {
        String mongoURI = getProperties().getProperty("mongo.URI");
        String databaseName = getProperties().getProperty("database");
        String collectionName = getProperties().getProperty("collection");

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
            logWriteDuration("Mongo", writeDuration);
            writeTimes.add(writeDuration);
        }

        long readStartTime = System.nanoTime();

        readAll();

        long readDuration = System.nanoTime() - readStartTime;
        logReadDuration("Mongo", readDuration);
        readTimes.add(readDuration);
    }

    @Override
    public void readAll() {
        Iterator iterator = this.collection.find().iterator();
        int counter = 0;

        while (iterator.hasNext()) {
            iterator.next();
            counter++;
        }

        log.info("MONGODB READ COUNTER: " + counter);
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
