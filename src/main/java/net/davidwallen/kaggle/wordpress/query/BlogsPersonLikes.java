package net.davidwallen.kaggle.wordpress.query;

import net.davidwallen.kaggle.wordpress.importer.TrainPosts;
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
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(TrainPosts.DB_PATH);
    
    registerShutdownHook();
    
    engine = new ExecutionEngine( graphDb );
    try {
      ExecutionResult result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->post<-[:POSTED]-author, post<-[:LIKES_POST]-liker " +
              "return post.title as title, author.UID as author, count(liker) as likes " +
              "order by likes desc"
            );
      System.out.println(result);
      
      result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->post-[:IN_CATEGORY]->category " +
              "return distinct category.UID as Categories " +
              "order by Categories asc"
            );
      System.out.println(result);
      
      result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->post-[:TAGGED]->tag " +
              "return distinct tag.UID as Tags " +
              "order by Tags asc"
            );
      System.out.println(result);
   
      result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->()<-[:HAS_POST]-blog-[:HAS_POST]-post " +
              "return distinct blog.name as blog, count(post) as posts " +
              "order by posts desc"
            );
      System.out.println(result);
      
      result = engine.execute(
              "start person=node:people(UID = '24602792') " +
              "match person-[:LIKES_POST]->()<-[:POSTED]-author, author-[:LIKES_POST]->post<-[:LIKES_POST]-liker " +
              "return post.title as title, count(liker) as likes " +
              "order by likes desc"
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
