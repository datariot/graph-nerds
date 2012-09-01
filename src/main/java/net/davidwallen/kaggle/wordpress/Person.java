package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

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
  public static final String LIKE_DATE = "dt";

  public Person(Node underlyingNode, String uid, Index<Node> index) {
    super(underlyingNode, uid, TYPE);
    index.add(underlyingNode, Properties.UID.name(), uid);
  }

  public Person(Node underlyingNode) {
    super(underlyingNode);
  }
  
  public void likes(Post post, String date) {
    Relationship like = this.underlyingNode.createRelationshipTo(post.getUnderlyingNode(), Relationships.LIKES_POST);
    like.setProperty(LIKE_DATE, date);
  }

  public void setInTestSet(Boolean testSet) {
    this.underlyingNode.setProperty(IN_TEST_SET, testSet);
  }

}