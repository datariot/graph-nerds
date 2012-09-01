package net.davidwallen.kaggle.wordpress.query;

import net.davidwallen.kaggle.wordpress.importer.TrainUsers;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * This query will traverse the path Person -- [Likes] -- Post -- [Has_Post] -- Blog
 * to find the the blogs associated with the posts the source person has liked.
 * 
 * @author surferdwa
 */
public class BlogsPersonLikes {
  
  private static GraphDatabaseService graphDb;
  private static ExecutionEngine engine;

  public static void main(final String[] args) {
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(TrainUsers.DB_PATH);
    
    registerShutdownHook();
    
    engine = new ExecutionEngine( graphDb );
    try {
      ExecutionResult result = engine.execute(
              "start person=node:node_auto_index(UID = '33496512') " +
              "match person-[:LIKES_POST]->()<-[:HAS_POST]-blog " +
              "return distinct blog"
            );
      System.out.println(result);
    } finally {
      graphDb.shutdown();
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
