package net.davidwallen.kaggle.wordpress.query;

import net.davidwallen.kaggle.wordpress.importer.TrainPosts;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * This query looks at various aspects of the graph from a person object.
 * 
 * @author surferdwa
 */
public class PersonInfo {
  
  private static GraphDatabaseService graphDb;
  private static ExecutionEngine engine;

  public static void main(final String[] args) {
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(TrainPosts.DB_PATH);
    
    registerShutdownHook();
    
    engine = new ExecutionEngine( graphDb );
    try {
      //Determine Posts a person likes, the author, and total post likes.
      ExecutionResult result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->post<-[:POSTED]-author, post<-[:LIKES_POST]-liker, post<-[:HAS_POST]-blog " +
              "return post.title as title, blog.name as blog, author.UID as author, count(liker) as likes " +
              "order by likes desc"
            );
      System.out.println(result);
      
      //Find categories for the posts a person has liked.
      result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->post-[:IN_CATEGORY]->category " +
              "return distinct category.UID as Categories " +
              "order by Categories asc"
            );
      System.out.println(result);
      
      //Find tags for the posts a person has liked.
      result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->()-[:TAGGED]->tag " +
              "return distinct tag.UID as Tags " +
              "order by Tags asc"
            );
      System.out.println(result);
   
      //Find blogs a person likes, how many posts that blog has, and how many likes.
      result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->()<-[:HAS_POST]-blog-[:HAS_POST]-()<-[:LIKES_POST]-liker " +
              "return distinct blog.name as blog, count(liker) as likes " +
              "order by likes desc"
            );
      System.out.println(result);
      
      //Find the posts that the authors like.
      result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->()<-[:POSTED]-()-[:LIKES_POST]->post<-[:LIKES_POST]-liker " +
              "return post.title as title, count(liker) as likes " +
              "order by likes desc " +
              "limit 10"
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
