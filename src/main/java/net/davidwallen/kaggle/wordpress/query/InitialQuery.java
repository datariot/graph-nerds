/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.davidwallen.kaggle.wordpress.query;

import net.davidwallen.kaggle.wordpress.importer.TrainUsers;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author dallen
 */
public class InitialQuery {
  
  private static GraphDatabaseService graphDb;
  private static ExecutionEngine engine;

  public static void main(final String[] args) {
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(TrainUsers.DB_PATH);
    
    registerShutdownHook();
    
    engine = new ExecutionEngine( graphDb );
    try {
      ExecutionResult result = engine.execute(
              "start person=node:node_auto_index(uid = '33496512') " +
              "match person-[:LIKES_POST]->()<-[:LIKES_POST]-similar " +
              "return similar"
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
