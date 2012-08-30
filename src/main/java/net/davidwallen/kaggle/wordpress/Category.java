package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * A category is a broad description for posts.
 * 
 * @author surferdwa
 */
public class Category extends BaseModel {

  public static final String TYPE = "category";

  public Category(Node underlyingNode, String uid) {
    super(underlyingNode, uid, TYPE);
  }
  
  public Category(Node underlyingNode) {
    super(underlyingNode);
  }

}
