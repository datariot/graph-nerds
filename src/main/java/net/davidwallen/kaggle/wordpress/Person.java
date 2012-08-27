package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * Class for representing a user in the wordpress.com graph. A user can like
 * blog posts.
 * 
 * @author surferdwa
 */
public class Person {
  
  public final String UID = "uid";
  public final String IN_TEST_SET = "inTestSet";
  
  private Node underlyingNode;

  public Person(Node underlyingNode, Integer id) {
    this.underlyingNode = underlyingNode;
    this.underlyingNode.setProperty(UID, id);
  }
  
  public Person(Node underlyingNode) {
    this.underlyingNode = underlyingNode;
  }

  /**
   * Get the value of underlyingNode
   *
   * @return the value of underlyingNode
   */
  public Node getUnderlyingNode() {
    return underlyingNode;
  }
  
  /**
   * Get the id of the user.
   *
   * @return the value of id
   */
  public Integer getId() {
    return (Integer)this.underlyingNode.getProperty(UID);
  }
  
  public void likes(Post post) {
    this.underlyingNode.createRelationshipTo(post.getUnderlyingNode(), Relationships.LIKES_POST);
  }

  public void setInTestSet(Boolean testSet) {
    this.underlyingNode.setProperty(IN_TEST_SET, testSet);
  }

}