package net.davidwallen.kaggle.wordpress;

import java.util.Collection;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 *
 * A post is an article in a blog. A post can be liked by a person on the
 * wordpress.com network.
 * 
 * @author surferdwa
 */
public class Post extends BaseModel {

  public static final String TYPE = "post";
  public static final String DATE = "date";
  public static final String TITLE = "title";
  public static final String URL = "url";
  
  public Post(Node underlyingNode, String uid, Index<Node> index) {
    super(underlyingNode, uid, TYPE);
    index.add(underlyingNode, Properties.UID.name(), uid);
  }
  
  public Post(Node underlyingNode) {
    super(underlyingNode);
  }

  public void setDate(String date) {
    this.underlyingNode.setProperty(DATE, date);
  }

  public void setURL(String url) {
    this.underlyingNode.setProperty(URL, url);
  }

  public void setTitle(String title) {
    this.underlyingNode.setProperty(TITLE, title);
  }

  public void setLanguage(Language language) {
    this.underlyingNode.createRelationshipTo(language.underlyingNode, Relationships.LANGUAGE);
  }

  public void addTags(Collection<Tag> tags) {
    for(Tag tag : tags) {
      this.underlyingNode.createRelationshipTo(tag.underlyingNode, Relationships.TAGGED);
    }
  }

  public void addCategories(Collection<Category> categories) {
    for(Category category : categories) {
      this.underlyingNode.createRelationshipTo(category.underlyingNode, Relationships.IN_CATEGORY);
    }
  }

  public void setAuthor(Person author) {
    author.underlyingNode.createRelationshipTo(underlyingNode, Relationships.POSTED);
  }

}
