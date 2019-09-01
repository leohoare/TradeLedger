import Objects.FilterObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import java.util.*;

public class DataServingClient {
    /*
        Singleton Mongo client for UserData database
     */
    private MongoClient client = null;
    private DB userData = null;
    private Map<String, DBCollection> collectionLookup = new HashMap<String, DBCollection>();

    public String QueryDatabase(String collectionName, List<FilterObject> filtersList) {
        GetClient();
        BasicDBObject searchQuery = GetSearchQuery(filtersList);
        if (!collectionLookup.containsKey(collectionName)) {
            collectionLookup.put(collectionName, userData.getCollection(collectionName));
        }
        DBCollection dbCollection = collectionLookup.get(collectionName);
        return JSON.serialize(dbCollection.find(searchQuery));
    }

    public BasicDBObject GetSearchQuery(List<FilterObject> filtersList) {
        BasicDBObject searchQuery = new BasicDBObject();
        for (FilterObject filter : filtersList) {
            /*
                Two types queries :
                    1. gte/lte a specified value where range isn't allowed
                    2. eq a specified value xor range.
             */
            if (filter.operator.equals("eq")) {
                if ( filter.value != null) {
                    // Exact eq match - hack ~ time is only long data type - solved with object relation mapping
                    searchQuery.put(filter.attribute, filter.attribute.equals("time") ? new Long(filter.value) : filter.value);
                } else {
                    // Range Query
                }
            } else {
                // gte & lte
            }
        }
        return searchQuery;
    }

    public MongoClient GetClient() {
        if (client == null) {
            client = new MongoClient("localhost", 27777);
            userData = client.getDB("AppropriateDatabaseName");
            return client;
        }
        else {
            return client;
        }
    }
}
