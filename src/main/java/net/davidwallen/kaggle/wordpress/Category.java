package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 *
 * A category is a broad description for posts.
 * 
 * @author surferdwa
 */
public class Category extends BaseModel {

  public static final String TYPE = "category";

  public Category(Node underlyingNode, String uid, Index<Node> index) {
    super(underlyingNode, uid, TYPE);
    index.add(underlyingNode, Properties.UID.name(), uid);
  }
  
  public Category(Node underlyingNode) {
    super(underlyingNode);
  }

}
