package net.davidwallen.kaggle.wordpress.query;

import java.util.Iterator;
import net.davidwallen.kaggle.wordpress.Blog;
import net.davidwallen.kaggle.wordpress.Category;
import net.davidwallen.kaggle.wordpress.Person;
import net.davidwallen.kaggle.wordpress.Post;
import net.davidwallen.kaggle.wordpress.Properties;
import net.davidwallen.kaggle.wordpress.Tag;
import net.davidwallen.kaggle.wordpress.importer.TrainPosts;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * This query lists all language nodes in the graph. Currently only 'en'... Boo!
 * 
 * @author surferdwa
 */
public class Stats {
  
  private static GraphDatabaseService graphDb;
  
  public static void main(final String[] args) {
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(TrainPosts.DB_PATH);
    registerShutdownHook();
    int blogs = 0;
    int people = 0;
    int posts = 0;
    int tags = 0;
    int categories = 0;
    int likes = 0;
    try {
      GlobalGraphOperations global = GlobalGraphOperations.at(graphDb);
      Iterable<Node> nodes = global.getAllNodes();
      final String typeProperty = Properties.TYPE.name();
      for(Node node :  nodes) {
        if (node.hasProperty(typeProperty)) {
          String type = (String)node.getProperty(typeProperty);
          switch (type) {
            case Blog.TYPE:
              blogs++;
              break;
            case Person.TYPE:
              people++;
              for (Iterator<Relationship> it = node.getRelationships().iterator(); it.hasNext();) {
                it.next();
                likes++;
              }
              break;
            case Post.TYPE:
              posts++;
              break;
            case Tag.TYPE:
              tags++;
              break;
            case Category.TYPE:
              categories++;
              break;
          }
        }
      }
      System.out.println("Blogs = "+blogs);
      System.out.println("People = "+people);
      System.out.println("Posts = "+posts);
      System.out.println("Tags = "+tags);
      System.out.println("Categories = "+categories);
      System.out.println("Likes = "+likes);
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
