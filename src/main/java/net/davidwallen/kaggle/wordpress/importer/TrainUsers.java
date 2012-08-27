/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.AutoIndexer;

/**
 *
 * @author dallen
 */
public class TrainUsers {

  private static final String DB_PATH = "neo4j-store";
  private static final String FILE_PATH = "trainUsers.json";
  private static final String UID = "uid";
  private static final String IN_TEST_SET = "inTestSet";
  private static final String LIKES = "likes";
  private static final String BLOG = "blog";
  private static final String POST = "post_id";
  
  private static GraphDatabaseService graphDb;

  public static void main(final String[] args) {
    try {
      // START SNIPPET: startDb
      graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
      AutoIndexer<Node> nodeAutoIndexer = graphDb.index().getNodeAutoIndexer();
      nodeAutoIndexer.startAutoIndexingProperty(UID);
      nodeAutoIndexer.setEnabled(true);
      
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
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
          String fieldname = jsonParser.getCurrentName();
          if (UID.equals(fieldname)) {
            jsonParser.nextToken();
            int uid = Integer.parseInt(jsonParser.getText());
            person = new Person(graphDb.createNode(), uid);
            person.setInTestSet(testSet);
          } else if (IN_TEST_SET.equals(fieldname)) {
            jsonParser.nextToken();
            testSet = jsonParser.getBooleanValue();
          } else if (LIKES.equals(fieldname)) {
            jsonParser.nextToken();
            // likes is array, loop until token equal to "]"
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
              while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                fieldname = jsonParser.getCurrentName();
                if (BLOG.equals(fieldname)) {
                  jsonParser.nextToken();
                  int uid = Integer.parseInt(jsonParser.getText());
                  blog = new Blog(graphDb.createNode(), uid);
                } else if (POST.equals(fieldname)) {
                  jsonParser.nextToken();
                  int uid = Integer.parseInt(jsonParser.getText());
                  post = new Post(graphDb.createNode(), uid);
                }
              }
              blog.has(post);
              person.likes(post);
            }
          }
        }
        jsonParser.close();
        tx.finish();
      }
      graphDb.shutdown();
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
}
