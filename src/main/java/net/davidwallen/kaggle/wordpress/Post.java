package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * A post is an article in a blog. A post can be liked by a person on the
 * wordpress.com network.
 * 
 * @author surferdwa
 */
public class Post extends BaseModel {

  public static final String TYPE = "post";

  public Post(Node underlyingNode, String uid) {
    super(underlyingNode, uid, TYPE);
  }
  
  public Post(Node underlyingNode) {
    super(underlyingNode);
  }

}
