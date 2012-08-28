/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.Node;

/**
 *
 * @author dallen
 */
public class BaseModel {
  protected Node underlyingNode;

  public BaseModel(Node underlyingNode, String uid, String type) {
    this.underlyingNode = underlyingNode;
    this.underlyingNode.setProperty(Properties.UID.name(), uid);
    this.underlyingNode.setProperty(Properties.TYPE.name(), type);
  }
  
  public BaseModel(Node underlyingNode) {
    this.underlyingNode = underlyingNode;
  }

  /**
   * Get the id of the object.
   *
   * @return the value of id
   */
  public String getId() {
    return (String) this.underlyingNode.getProperty(Properties.UID.name());
  }

  /**
   * Get the underlying Neo4j Node
   *
   * @return the value of underlyingNode
   */
  public Node getUnderlyingNode() {
    return underlyingNode;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    final String uid = this.getId();
    hash = 23 * hash + (uid != null ? uid.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BaseModel other = (BaseModel) obj;
    final String otherUid = other.getId();
    final String thisUid = this.getId();
    if (thisUid != otherUid && (thisUid == null || !thisUid.equals(otherUid))) {
      return false;
    }
    return true;
  }
}
