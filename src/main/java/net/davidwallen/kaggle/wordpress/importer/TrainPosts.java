package net.davidwallen.kaggle.wordpress.importer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
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
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.ReadableIndex;

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

  public static final String DB_PATH = "neo4j-store";
  private static final String FILE_PATH = "trainPosts.json";
  
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
  private static final String BLOG_NAME = "blogname";
  private static final String UID = Properties.UID.name();
  private static final String TYPE = Properties.UID.name();
  
  private static GraphDatabaseService graphDb;

  public static void main(final String[] args) {
    try {
      // START SNIPPET: startDb
      graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
      AutoIndexer<Node> nodeAutoIndexer = graphDb.index().getNodeAutoIndexer();
      nodeAutoIndexer.startAutoIndexingProperty(UID);
      nodeAutoIndexer.startAutoIndexingProperty(TYPE);
      nodeAutoIndexer.setEnabled(true);
      ReadableIndex<Node> index = nodeAutoIndexer.getAutoIndex();
      
      registerShutdownHook();

      BufferedReader in = new BufferedReader(new FileReader(FILE_PATH));
      JsonFactory jsonFactory = new JsonFactory();

      String userJson;
      while ((userJson = in.readLine()) != null) {
        JsonParser jsonParser = jsonFactory.createJsonParser(userJson);
        Transaction tx = graphDb.beginTx();
        try {
          parseLine(jsonParser, index);
          tx.success();
        } finally {
          jsonParser.close();
          tx.finish();
        }
      }
      in.close();
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

  private static Person makePerson(ReadableIndex<Node> index, String uid) {
    Person person;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      person = new Person(node);
    } else {
      person = new Person(graphDb.createNode(), uid);
    }
    return person;
  }

  private static Blog makeBlog(ReadableIndex<Node> index, String uid) {
    Blog blog;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      blog = new Blog(node);
    } else {
       blog = new Blog(graphDb.createNode(), uid);
    }
    return blog;
  }

  private static Post makePost(ReadableIndex<Node> index, String uid) {
    Post post;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      post = new Post(node);
    } else {
      post = new Post(graphDb.createNode(), uid);
    }
    return post;
  }

  private static Language makeLanguage(ReadableIndex<Node> index, String uid) {
    Language language;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      language = new Language(node);
    } else {
      language = new Language(graphDb.createNode(), uid);
    }
    return language;
  }

  private static Tag makeTag(ReadableIndex<Node> index, String uid) {
    Tag tag;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      tag = new Tag(node);
    } else {
      tag = new Tag(graphDb.createNode(), uid);
    }
    return tag;
  }

  private static Category makeCategory(ReadableIndex<Node> index, String uid) {
    Category category;
    Node node = index.get(UID, uid).getSingle();
    if(node != null) {
      category = new Category(node);
    } else {
      category = new Category(graphDb.createNode(), uid);
    }
    return category;
  }

  private static void parseLine(JsonParser jsonParser, ReadableIndex<Node> index) throws IOException {
    Person author = null;
    Blog blog = null;
    Post post = null;
    String date = null;
    String url = null;
    String title = null;
    String blogName = null;
    Language language = null;
    final Collection<Tag> tags = new LinkedList<Tag>();
    final Collection<Category> categories = new LinkedList<Category>();
    
    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      String fieldname = jsonParser.getCurrentName();
      if (AUTHOR.equals(fieldname)) {
        jsonParser.nextToken();
        author = makePerson(index, jsonParser.getText());
      }else if (BLOG.equals(fieldname)) {
        jsonParser.nextToken();
        String uid = jsonParser.getText();
        blog = makeBlog(index, uid);
      } else if (POST.equals(fieldname)) {
        jsonParser.nextToken();
        String uid = jsonParser.getText();
        post = makePost(index, uid);
      } else if (DATE.equals(fieldname)) {
        jsonParser.nextToken();
        date = jsonParser.getText();
      } else if (LANGUAGE.equals(fieldname)) {
        jsonParser.nextToken();
        language = makeLanguage(index, jsonParser.getText());
      } else if (URL.equals(fieldname)) {
        jsonParser.nextToken();
        url = jsonParser.getText();
      } else if (TITLE.equals(fieldname)) {
        jsonParser.nextToken();
        title = jsonParser.getText();
      } else if (BLOG_NAME.equals(fieldname)) {
        jsonParser.nextToken();
        blogName = jsonParser.getText();
      } else if (TAGS.equals(fieldname)) {
        // likes is array, loop until token equal to "]"
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
          jsonParser.nextToken();
          tags.add(makeTag(index, jsonParser.getText()));
        }
      } else if (CATEGORIES.equals(fieldname)) {
        // likes is array, loop until token equal to "]"
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
          jsonParser.nextToken();
          categories.add(makeCategory(index, jsonParser.getText()));
        }
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
