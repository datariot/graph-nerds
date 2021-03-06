package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 *
 * A blog is a container for posts.
 * 
 * @author surferdwa
 */
public class Blog extends BaseModel {
  
  public static final String TYPE = "blog";
  public static final String NAME = "name";

  public Blog(Node underlyingNode, String uid, Index<Node> index) {
    super(underlyingNode, uid, TYPE);
    index.add(underlyingNode, Properties.UID.name(), uid);
  }
    
  public Blog(Node underlyingNode) {
    super(underlyingNode);
  }

  public void has(Post post) {
    this.underlyingNode.createRelationshipTo(post.getUnderlyingNode(), Relationships.HAS_POST);
  }

  public void setName(String blogName) {
    this.underlyingNode.setProperty(NAME, blogName);
  }
  
}
