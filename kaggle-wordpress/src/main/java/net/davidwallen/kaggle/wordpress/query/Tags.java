package net.davidwallen.kaggle.wordpress.query;

import net.davidwallen.kaggle.wordpress.importer.TrainPosts;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * Queries for exploring the Tag objects.
 * 
 * @author surferdwa
 */
public class Tags {
  
  private static GraphDatabaseService graphDb;
  private static ExecutionEngine engine;

  public static void main(final String[] args) {
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(TrainPosts.DB_PATH);
    
    registerShutdownHook();
    
    engine = new ExecutionEngine( graphDb );
    try {
      
      //A query to find the top "cat" authors.
      ExecutionResult result = engine.execute(
          "start tag=node:tags(UID='cat') " +
          "match tag<-[:TAGGED]-post<-[:POSTED]-author " +
          "return distinct author.UID as CatAuthor, count(post) as count " +
          "order by count desc " +
          "limit 10"
        );
      System.out.println(result);
      
      //A query to find the to "cat" likers.
      result = engine.execute(
          "start tag=node:tags(UID='cat') " +
          "match tag<-[:TAGGED]-post<-[:LIKES_POST]-liker " +
          "return distinct liker.UID as CatLiker, count(liker) as count " +
          "order by count desc " +
          "limit 10"
        );
      System.out.println(result);
      
      //Verify Top Cat Liker
      result = engine.execute(
          "start liker=node:people(UID='28575716'), tag=node:tags(UID='cat') " +
          "match tag<-[:TAGGED]-post<-[:LIKES_POST]-liker " +
          "return distinct post.UID, post.title " +
          "order by post.title"
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
