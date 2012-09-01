package net.davidwallen.kaggle.wordpress.importer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.davidwallen.kaggle.wordpress.Blog;
import net.davidwallen.kaggle.wordpress.Person;
import net.davidwallen.kaggle.wordpress.Post;
import net.davidwallen.kaggle.wordpress.Properties;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.ReadableIndex;

/**
 *
 * Import the TrainUsers.json file.
 * 
 * {  "inTestSet": false,
 *    "uid": "34168956",
 *    "likes": [
 *      { "blog": "18164949",
 *        "post_id": "1740778",
 *        "like_dt": "2012-04-05 14:10:56"}
 *     ]
 * }
 * 
 * @author surferdwa
 */
public class TrainUsers {

  public static final String DB_PATH = "neo4j-store";
  private static final String FILE_PATH = "trainUsers.json";
  
  private static final String LIKES = "likes";
  private static final String BLOG = "blog";
  private static final String POST = "post_id";
  private static final String JSON_ID = "uid";
  private static final String UID = Properties.UID.name();
  private static final String TYPE = Properties.TYPE.name();
  
  private static GraphDatabaseService graphDb;

  public static void main(final String[] args) {
    try {
      // START SNIPPET: startDb
      graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
      AutoIndexer<Node> nodeAutoIndexer = graphDb.index().getNodeAutoIndexer();
      nodeAutoIndexer.startAutoIndexingProperty(UID);
      nodeAutoIndexer.startAutoIndexingProperty(TYPE);
      nodeAutoIndexer.setEnabled(true);
      ReadableIndex<Node> index = nodeAutoIndexer.getAutoIndex();
      
      registerShutdownHook();

      BufferedReader in = new BufferedReader(new FileReader(FILE_PATH));
      JsonFactory jsonFactory = new JsonFactory();

      String userJson;
      while ((userJson = in.readLine()) != null) {
        JsonParser jsonParser = jsonFactory.createJsonParser(userJson);
        Boolean testSet = false;
        Person person = null;
        Blog blog = null;
        Post post = null;
        Transaction tx = graphDb.beginTx();
        try {
          while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jsonParser.getCurrentName();
            if (JSON_ID.equals(fieldname)) {
              jsonParser.nextToken();
              String uid = jsonParser.getText();
              person = makePersonFromUID(index, uid, testSet);
            } else if (Person.IN_TEST_SET.equals(fieldname)) {
              jsonParser.nextToken();
              testSet = jsonParser.getBooleanValue();
            } else if (LIKES.equals(fieldname)) {
              jsonParser.nextToken();
              // likes is array, loop until token equal to "]"
              while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                  fieldname = jsonParser.getCurrentName();
                  if (POST.equals(fieldname)) {
                    jsonParser.nextToken();
                    String uid = jsonParser.getText();
                    post = makePostFromUID(index, uid);
                  } else if (BLOG.equals(fieldname)) {
                    jsonParser.nextToken();
                    String uid = jsonParser.getText();
                    blog = makeBlogFromUID(index, uid);
                  } 
                }
                blog.has(post);
                person.likes(post);
              }
            }
          }
          tx.success();
        } finally {
          jsonParser.close();
          tx.finish();
        }
      }
    } catch (JsonGenerationException ex) {
      Logger.getLogger(TrainUsers.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(TrainUsers.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  private static void shutdown() {
    graphDb.shutdown();
  }

  private static void registerShutdownHook() {
    // Registers a shutdown hook for the Neo4j and index service instances
    // so that it shuts down nicely when the VM exits (even if you
    // "Ctrl-C" the running example before it's completed)
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        shutdown();
      }
    });
  }

  private static Person makePersonFromUID(ReadableIndex<Node> index, String uid, Boolean testSet) {
    Person person;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      person = new Person(node);
    } else {
      person = new Person(graphDb.createNode(), uid);
      person.setInTestSet(testSet);
    }
    return person;
  }

  private static Blog makeBlogFromUID(ReadableIndex<Node> index, String uid) {
    Blog blog;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      blog = new Blog(node);
    } else {
       blog = new Blog(graphDb.createNode(), uid);
    }
    return blog;
  }

  private static Post makePostFromUID(ReadableIndex<Node> index, String uid) {
    Post post;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      post = new Post(node);
    } else {
      post = new Post(graphDb.createNode(), uid);
    }
    return post;
  }
}
