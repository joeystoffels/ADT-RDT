package nl.nickhartjes.persistence;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nl.nickhartjes.models.Measurement;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoPersistence extends PersistenceAdapter {

  private MongoClient mongoClient;
  private MongoCollection<Document> collection;

  private List<Long> writeTimes = new ArrayList<>();

  public MongoPersistence() {
    String mongoURI = this.properties.getProperty("mongo.URI");
    String databaseName = this.properties.getProperty("database");
    String collectionName = this.properties.getProperty("collection");

    mongoClient = new MongoClient(new MongoClientURI(mongoURI));
    MongoDatabase database = mongoClient.getDatabase(databaseName);
    this.collection = database.getCollection(collectionName);
  }

  @Override
  public void save(List<Measurement> measurements) {
    List<Document> documents = new ArrayList<>();
    for (Measurement measurement : measurements) {
      Document document = new Document();
      document.append("timestamp", measurement.getTimestamp().getTime());
      document.append("value", measurement.getValue());

      Date startDate = new Date();

      documents.add(document);

      Date endDate = new Date();
      writeTimes.add(endDate.getTime() - startDate.getTime());
    }

    if (!documents.isEmpty()) {
      this.collection.insertMany(documents);
    }
  }

  @Override
  public void close() {
    this.mongoClient.close();
  }

  @Override
  public List<Long> getWriteTimes() {
    return writeTimes;
  }
}
