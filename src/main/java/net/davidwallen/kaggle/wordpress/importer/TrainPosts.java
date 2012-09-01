package net.davidwallen.kaggle.wordpress.importer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.davidwallen.kaggle.wordpress.Blog;
import net.davidwallen.kaggle.wordpress.Category;
import net.davidwallen.kaggle.wordpress.Language;
import net.davidwallen.kaggle.wordpress.Person;
import net.davidwallen.kaggle.wordpress.Post;
import net.davidwallen.kaggle.wordpress.Properties;
import net.davidwallen.kaggle.wordpress.Tag;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;

/**
 *
 * Import the TrainPosts.json file.
 * 
 * {  "date_gmt":"2012-03-28 19:59:45",
 *    "language": "en",
 *    "author": "5",
 *    "url": "http://matt.wordpress.com/?p=3841",
 *    "title": "Fish tacos and spicy slaw",
 *    "blog": "4",
 *    "post_id": "1329369",
 *    "tags": [],
 *    "blogname": "Matt on Not-WordPress",
 *    "date": "2012-03-28 19:59:45",
 *    "content": "HTML IN HERE",
 *    "categories": ["Moblog"],
 *    "likes": [
 *       {"dt": "2012-03-28 21:05:37", "uid": "31367867"}
 *     ]}
 * 
 * @author surferdwa
 */
public class TrainPosts {

  public static final String DB_PATH = "neo4j-store-small";
  private static final String FILE_PATH = "smallTrainPosts.json";
  
  private static final String LIKES = "likes";
  private static final String BLOG = "blog";
  private static final String POST = "post_id";
  private static final String TAGS = "tags";
  private static final String TITLE = "title";
  private static final String URL = "url";
  private static final String AUTHOR = "author";
  private static final String CATEGORIES = "categories";
  private static final String LANGUAGE = "language";
  private static final String DATE = "date_gmt";
  private static final String LIKE_DATE = "dt";
  private static final String BLOG_NAME = "blogname";
  private static final String JSON_UID = "uid";
  private static final String UID = Properties.UID.name();
  
  private static GraphDatabaseService graphDb;
  private static final Map<String,Index<Node>> indexes = new HashMap<>();

  public static void main(final String[] args) {
    try {
      // START SNIPPET: startDb
      graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
      IndexManager indexer = graphDb.index();
      indexes.put(Blog.TYPE, indexer.forNodes( "blogs" ));
      indexes.put(Category.TYPE, indexer.forNodes( "categories" ));
      indexes.put(Language.TYPE, indexer.forNodes( "languages" ));
      indexes.put(Person.TYPE, indexer.forNodes( "people" ));
      indexes.put(Post.TYPE, indexer.forNodes( "posts" ));
      indexes.put(Tag.TYPE, indexer.forNodes( "tags" ));
      
      registerShutdownHook();
      try (BufferedReader in = new BufferedReader(new FileReader(FILE_PATH))) {
        JsonFactory jsonFactory = new JsonFactory();

        String userJson;
        while ((userJson = in.readLine()) != null) {
          JsonParser jsonParser = jsonFactory.createJsonParser(userJson);
          Transaction tx = graphDb.beginTx();
          try {
            parseLine(jsonParser);
            tx.success();
          } finally {
            jsonParser.close();
            tx.finish();
          }
        }
      }
    } catch (JsonGenerationException ex) {
      Logger.getLogger(TrainUsers.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(TrainUsers.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  private static void shutdown() {
    graphDb.shutdown();
  }

  private static void registerShutdownHook() {
    // Registers a shutdown hook for the Neo4j and index service instances
    // so that it shuts down nicely when the VM exits (even if you
    // "Ctrl-C" the running example before it's completed)
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        shutdown();
      }
    });
  }

  private static Person makePerson(String uid) {
    Person person;
    Index<Node> index = indexes.get(Person.TYPE);
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      person = new Person(node);
    } else {
      person = new Person(graphDb.createNode(), uid, index);
    }
    return person;
  }

  private static Blog makeBlog(String uid) {
    Blog blog;
    Index<Node> index = indexes.get(Blog.TYPE);
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      blog = new Blog(node);
    } else {
       blog = new Blog(graphDb.createNode(), uid, index);
    }
    return blog;
  }

  private static Post makePost(String uid) {
    Post post;
    Index<Node> index = indexes.get(Post.TYPE);
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      post = new Post(node);
    } else {
      post = new Post(graphDb.createNode(), uid, index);
    }
    return post;
  }

  private static Language makeLanguage(String uid) {
    Language language;
    Index<Node> index = indexes.get(Language.TYPE);
    Node node = indexes.get(Language.TYPE).get(UID, uid).getSingle();
    if(node != null) {
      language = new Language(node);
    } else {
      language = new Language(graphDb.createNode(), uid, index);
    }
    return language;
  }

  private static Tag makeTag(String uid) {
    Tag tag;
    Index<Node> index = indexes.get(Tag.TYPE);
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      tag = new Tag(node);
    } else {
      tag = new Tag(graphDb.createNode(), uid, index);
    }
    return tag;
  }

  private static Category makeCategory(String uid) {
    Category category;
    Index<Node> index = indexes.get(Category.TYPE);
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      category = new Category(node);
    } else {
      category = new Category(graphDb.createNode(), uid, index);
    }
    return category;
  }

  private static void parseLine(JsonParser jsonParser) throws IOException {
    Person author = null;
    Blog blog = null;
    Post post = null;
    String date = null;
    String url = null;
    String title = null;
    String blogName = null;
    Language language = null;
    final Collection<Tag> tags = new LinkedList<>();
    final Collection<Category> categories = new LinkedList<>();
    jsonParser.nextToken();
    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      jsonParser.nextToken();
      String fieldname = jsonParser.getCurrentName();
      String uid;
      switch (fieldname) {
        case AUTHOR:
          author = makePerson(jsonParser.getText());
          break;
        case BLOG:
          uid = jsonParser.getText();
          blog = makeBlog(uid);
          break;
        case POST:
          uid = jsonParser.getText();
          post = makePost(uid);
          break;
        case DATE:
          date = jsonParser.getText();
          break;
        case LANGUAGE:
          language = makeLanguage(jsonParser.getText());
          break;
        case URL:
          url = jsonParser.getText();
          break;
        case TITLE:
          title = jsonParser.getText();
          break;
        case BLOG_NAME:
          blogName = jsonParser.getText();
          break; 
        case TAGS:
          // likes is array, loop until token equal to "]"
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            tags.add(makeTag(jsonParser.getText()));
          }
          break;
        case CATEGORIES:
          // likes is array, loop until token equal to "]"
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            categories.add(makeCategory(jsonParser.getText()));
          }
          break;
        case LIKES:
          Person liker = null;
          String likeDate = null;
          // likes is array, loop until token equal to "]"
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
              fieldname = jsonParser.getCurrentName();
              switch (fieldname) {
                case JSON_UID:
                  uid = jsonParser.getText();
                  liker = makePerson(uid);
                  break;
                case LIKE_DATE:
                  likeDate = jsonParser.getText();
                  break;
              }
            }
            liker.likes(post, likeDate);
          }
          break;
      }
    }
    post.setDate(date);
    post.setURL(url);
    post.setTitle(title);
    post.setLanguage(language);
    post.addTags(tags);
    post.addCategories(categories);
    post.setAuthor(author);
    blog.has(post);
    if(null!=blogName) {
      blog.setName(blogName);
    }
  }
}
