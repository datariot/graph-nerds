package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * A tag is a fine grained description of a post.
 * 
 * @author surferdwa
 */
public class Tag extends BaseModel {

  public static final String TYPE = "tag";

  public Tag(Node underlyingNode, String uid) {
    super(underlyingNode, uid, TYPE);
  }
  
  public Tag(Node underlyingNode) {
    super(underlyingNode);
  }

}
