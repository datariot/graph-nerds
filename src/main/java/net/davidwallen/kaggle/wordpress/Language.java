package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * The language a post is written in.
 * 
 * @author surferdwa
 */
public class Language extends BaseModel {

  public static final String TYPE = "language";

  public Language(Node underlyingNode, String uid) {
    super(underlyingNode, uid, TYPE);
  }
  
  public Language(Node underlyingNode) {
    super(underlyingNode);
  }

}
