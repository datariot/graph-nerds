package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;


/**
 *
 * The language a post is written in.
 * 
 * @author surferdwa
 */
public class Language extends BaseModel {

  public static final String TYPE = "language";

  public Language(Node underlyingNode, String uid, Index<Node> index) {
    super(underlyingNode, uid, TYPE);
    index.add(underlyingNode, Properties.UID.name(), uid);
  }
  
  public Language(Node underlyingNode) {
    super(underlyingNode);
  }

}
