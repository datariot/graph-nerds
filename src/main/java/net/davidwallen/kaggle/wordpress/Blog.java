package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * A blog is a container for posts.
 * 
 * @author surferdwa
 */
public class Blog {
  
  public final String UID = "uid";
  private Node underlyingNode;

  public Blog(Node underlyingNode, Integer id) {
    this.underlyingNode = underlyingNode;
    this.underlyingNode.setProperty(UID, id);
  }
    
  /**
   * Get the value of underlyingNode
   *
   * @return the value of underlyingNode
   */
  public Node getUnderlyingNode() {
    return underlyingNode;
  }

  /**
   * The ID assigned to the blog.
   * 
   * @return blog ID.
   */
  public Integer getID() {
    return (Integer)this.underlyingNode.getProperty(UID);
  }
  
  public void has(Post post) {
    this.underlyingNode.createRelationshipTo(post.getUnderlyingNode(), Relationships.HAS_POST);
  }
  
}
