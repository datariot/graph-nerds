package net.davidwallen.kaggle.wordpress.query;

import net.davidwallen.kaggle.wordpress.importer.TrainPosts;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * This query traverses the path Person -- [Likes] -- Post -- [Likes] -- Person
 * to return a list of people who like the same post as the source node.
 * 
 * @author surferdwa
 */
public class ShortestPath {
  
  private static GraphDatabaseService graphDb;
  private static ExecutionEngine engine;

  public static void main(final String[] args) {
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(TrainPosts.DB_PATH);
    
    registerShutdownHook();
    
    engine = new ExecutionEngine( graphDb );
    try {
      ExecutionResult result = engine.execute(
              "start cats=node:tags('UID:cats'), toaster=node:tags('UID:toaster') " +
              "match p = shortestPath( cats-[*..15]-toaster ) " +
              "return p"
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
