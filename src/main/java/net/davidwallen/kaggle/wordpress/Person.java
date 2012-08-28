package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * Class for representing a user in the wordpress.com graph. A user can like
 * blog posts.
 * 
 * @author surferdwa
 */
public class Person extends BaseModel {
  
  public static final String IN_TEST_SET = "inTestSet";
  public static final String TYPE = "person";

  public Person(Node underlyingNode, String uid) {
    super(underlyingNode, uid, TYPE);
  }

  public Person(Node underlyingNode) {
    super(underlyingNode);
  }
  
  public void likes(Post post) {
    this.underlyingNode.createRelationshipTo(post.getUnderlyingNode(), Relationships.LIKES_POST);
  }

  public void setInTestSet(Boolean testSet) {
    this.underlyingNode.setProperty(IN_TEST_SET, testSet);
  }

}