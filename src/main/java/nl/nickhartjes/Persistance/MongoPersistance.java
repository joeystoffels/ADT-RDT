package nl.nickhartjes.Persistance;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nl.nickhartjes.Models.Measurement;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoPersistance extends PersistanceAdapter {

  private MongoClient mongoClient;
  private MongoCollection<Document> collection;

  public MongoPersistance() {
    super();

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
      documents.add(document);
    }

    if (!documents.isEmpty()) {
      this.collection.insertMany(documents);
    }
  }

  @Override
  public void close() {
    this.mongoClient.close();
  }
}
