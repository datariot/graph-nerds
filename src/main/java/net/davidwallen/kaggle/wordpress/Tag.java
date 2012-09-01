package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 *
 * A tag is a fine grained description of a post.
 * 
 * @author surferdwa
 */
public class Tag extends BaseModel {

  public static final String TYPE = "tag";

  public Tag(Node underlyingNode, String uid, Index<Node> index) {
    super(underlyingNode, uid, TYPE);
    index.add(underlyingNode, Properties.UID.name(), uid);
  }
  
  public Tag(Node underlyingNode) {
    super(underlyingNode);
  }

}
