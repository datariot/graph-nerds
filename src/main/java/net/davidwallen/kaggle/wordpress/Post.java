package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * A post is an article in a blog. A post can be liked by a person on the
 * wordpress.com network.
 * 
 * @author surferdwa
 */
public class Post {

  public final String UID = "uid";
  
  private Node underlyingNode;

  public Post(Node underlyingNode, Integer id) {
    this.underlyingNode = underlyingNode;
    this.underlyingNode.setProperty(UID, id);
  }
    
  /**
   * Get underlying Neo4j Node
   *
   * @return the value of underlyingNode
   */
  public Node getUnderlyingNode() {
    return underlyingNode;
  }

  /**
   * The ID assigned on the wordpress.com challenge.
   * 
   * @return the post ID
   */
  public Integer getID() {
    return (Integer)this.underlyingNode.getProperty(UID);
  }
  
}
