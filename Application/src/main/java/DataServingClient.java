import Objects.FilterObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.*;

public class DataServingClient {
    /*
        Singleton Mongo client for UserData database
     */
    private MongoClient client = null;
    private MongoDatabase userData = null;
    private Map<String, MongoCollection> collectionLookup = new HashMap<String, MongoCollection>();

    public String QueryDatabase(String collectionName, List<FilterObject> filtersList) {
        GetClient();
        MongoCollection dbCollection = GetCollection(collectionName);
        // return if no filters
        if (filtersList.isEmpty()) return JSON.serialize(dbCollection.find());
        // else create search filter
        Bson searchQuery = GetSearchQuery(filtersList);
        return JSON.serialize(dbCollection.find(searchQuery));
    }

    private Bson GetSearchQuery(List<FilterObject> filtersList) {
        List<Bson> searchQuery = new ArrayList<>();
        for (FilterObject filter : filtersList) {
            if (filter.operator.equals("eq")) {
                if ( filter.value != null) {
                    // exact value match case
                    searchQuery.add(eq(filter.attribute, IsLong(filter.value) ? new Long(filter.value) : filter.value));
                } else {
                    // range case
                    searchQuery.add(and(gte(filter.attribute, IsLong(filter.range.from) ? new Long(filter.range.from) : filter.range.from),
                        lte(filter.attribute, IsLong(filter.range.to) ? new Long(filter.range.to) : filter.range.to)));
                }
            } else {
                // gte or lte case
                searchQuery.add(filter.operator.equals("gte") ?
                    gte(filter.attribute, IsLong(filter.value) ? new Long(filter.value) : filter.value):
                    lte(filter.attribute, IsLong(filter.value) ? new Long(filter.value) : filter.value));
            }
        }
        return and(searchQuery);
    }

    private MongoCollection GetCollection(String collectionName) {
        if (!collectionLookup.containsKey(collectionName)) {
            collectionLookup.put(collectionName, userData.getCollection(collectionName));
        }
        return collectionLookup.get(collectionName);
    }

    // Not the best method ~ slow performance on regex
    private Boolean IsLong(String str) {
        return Pattern.matches("^-?[0-9]+$", str);
    }

    // Singleton client initiated on first request
    private MongoClient GetClient() {
        if ( client != null ) return client;
        client = new MongoClient("localhost", 27777);
        userData = client.getDatabase("AppropriateDatabaseName");
        return client;
    }
}
