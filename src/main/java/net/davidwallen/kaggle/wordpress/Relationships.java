package net.davidwallen.kaggle.wordpress;

import org.neo4j.graphdb.RelationshipType;

/**
 *
 * An enum defining the relationships represented in the Wordpress challenge.
 * These relationships are based on the blogs, posts, users, and the "likes".
 *
 * @author surferdwa
 */
public enum Relationships implements RelationshipType {

  LIKES_POST, //A relationship between a Person and a Post.
  HAS_POST, //Relationship between a Blog and a Post.
}