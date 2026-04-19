/*========================================================================
  
  JGemini

  DefaultFeedHandler 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.time.format.DateTimeFormatter;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;

/** The main feed handler. This class is responsible for adding
    feeds, checking whether a URL is already in the feeds list, 
    aggregating feeds into viewable pages, and
    raising the feed editor (which is just a file editor). 
    See FeedHandler for definitions of the public methods. 
*/
public class DefaultFeedHandler implements FeedHandler 
  {
  private static Config config = Config.getConfig();

  /** fileWasUpdated is set whenever a new line is written to the
     feed file, and at start-up. When set, it causes calls like
     getFeedCount to reload from file. */
  private boolean fileWasUpdated = true;

  private Vector<GemLink> feeds = new Vector<GemLink>();

  private static DefaultFeedHandler instance = null;

  private final static ResourceBundle captionsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Captions");

  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

  private SwingWorker worker = null;

  private FeedManagerStatusListener feedManagerStatusListener = null;

  private static SimpleDateFormat iso8601 
    = new SimpleDateFormat("yyyy-MM-dd");

   private static StatusHandler statusHandler = StatusHandler.getInstance();

   private static SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd"); 

/*=========================================================================
  
  Constructor

=========================================================================*/
  /** Can not be instantiated. */
  private DefaultFeedHandler ()
    {
    }

/*=========================================================================
  
  addFeedManagerStatusListener

=========================================================================*/
  @Override
  public void addFeedManagerStatusListener (FeedManagerStatusListener l)
    {
    feedManagerStatusListener = l;
    } 

/*=========================================================================
  
  addFeed 

=========================================================================*/
  /** addFeed() returns true if a new feed is added. The only
      reason not to add the feed -- other than an exception --
      is that the URI is already feeded. */
  @Override
  public boolean addFeed (String displayName, URL uri) throws IOException
    {
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "addFeed(): " + displayName + " " + uri);

    if (isInFeedList (uri)) return false;

    String feedsFile = config.getFeedsFile();
    String newFeed = "=> " + uri + " " + displayName + "\n";
    FileUtil.appendStringToFile (feedsFile, newFeed);
    fileWasUpdated = true;
    return true;
    }

/*=========================================================================
  
  cancelUpdate 

=========================================================================*/
  @Override
  public void cancelUpdate()
    {
    Logger.in();
    if (worker != null)
      {
      Logger.log (getClass().getName(), Logger.DEBUG, "Cancelling SwingWorker");
      worker.cancel (true);
      }
    else
      {
      Logger.log (getClass().getName(), Logger.DEBUG, "Nothing to cancel");
      }
    Logger.out();
    }

/*=========================================================================
  
  editFeeds 

=========================================================================*/
  /** Raise a file editor that targets the feeds file. 
  */
  @Override
  public void editFeeds() throws IOException
    {
    config.ensureFeedsFileExists();
    String feedsFile = config.getFeedsFile();
    EditFileDialog d = new EditFileDialog (null, 
        captionsBundle.getString ("feeds"),
          feedsFile, Constants.DOC_EDIT_BOOKMARKS);
    d.setVisible (true);
    if (d.didSave())
      {
      fileWasUpdated = true;
      }
    }

/*=========================================================================
  
  getInstance 

=========================================================================*/
  public static DefaultFeedHandler getInstance()
    {
    if (instance == null)
      instance = new DefaultFeedHandler();
    return instance;
    }

/*=========================================================================
  
  getFeedCount 

=========================================================================*/
  public int getFeedCount()
    {
    Logger.log (getClass().getName(), Logger.DEBUG, "getFeedCount()");
    readFromFile();
    return feeds.size();
    }

/*=========================================================================
  
  getFeedLink

=========================================================================*/
  /** Get a specific feed by its index. This method will throw an exception 
      if index &lt; 0 or &gt; the size of the feed list. It should only 
      called after a call to getFeedCount(), which will also reload 
      from file if necessary. 
  */
  @Override
  public GemLink getFeedLink (int index)
    {
    readFromFile();
    return feeds.elementAt (index);
    }

/*=========================================================================
  
  isInList 

=========================================================================*/
  /** isInList() returns true if the URI is definitely in the feed list, and
      false if not, or uncertain. This method throws no exceptions, so
      file-handling errors are quietly ignored. */
  @Override
  public boolean isInFeedList (URL uri)
    {
    String sUri = uri.toString();
    readFromFile();
    int l = feeds.size();
    for (int i = 0; i < l; i++)
      {
      GemLink link = feeds.elementAt(i);
      if (link.getUri().equals (sUri)) return true;
      }
    return false;
    }

/*=========================================================================
  
  isRunning 

=========================================================================*/
  @Override
  public boolean isRunning()
    {
    return (worker != null);
    }

/*=========================================================================
  
  readFromFile 

=========================================================================*/
  /** Read all the feeds from file, if the "fileWasUpdated" attribute
      is true, that is, if there is reason to think that we need to
      reload. 
  */
  public void readFromFile() 
    {
    // Just creating a new Vector and leaving the old one for GC is
    //   probably quicker than clearing the existing one
    if (fileWasUpdated)
      {
      Logger.log (getClass().getName(), Logger.DEBUG, "readFromFile()");
      feeds = new Vector<GemLink>();
      Logger.log (getClass().getName(), Logger.DEBUG, "Feed data stale -- need to read");
      String feedsFile = config.getFeedsFile();
      try
        {
        BufferedReader br = new BufferedReader (new InputStreamReader 
          (new FileInputStream (feedsFile)));
        String line = br.readLine();
        while (line != null)
          {
          if (line.indexOf ("=>") == 0)
            {
            GemLink link = GemLink.parse (line.substring(2).trim());
            feeds.add (link); 
            }
          line = br.readLine();
          }
        br.close();
        }
      catch (IOException e)
        {
        }
      }
    fileWasUpdated = false;
    }

/*=========================================================================
  
  parseFeedAsAtom 

=========================================================================*/
  private int parseFeedAsAtom (String url, String content,
          TreeSet<FeedPost> allPosts) throws FeedException
    {
    try
      {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      InputStream isX = 
          getClass().getClassLoader().getResourceAsStream ("xslt/atom_to_gmisub.xslt");
      InputStream isT = new ByteArrayInputStream (content.getBytes());
      Source xsltSource = new StreamSource (isX);
      Source xmlSource = new StreamSource (isT);
      Transformer transformer = transformerFactory.newTransformer (xsltSource);
      StreamResult result = new StreamResult();
      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      result.setOutputStream (boas);
      transformer.transform (xmlSource, result);
      isX.close();
      isT.close();
      String ret = boas.toString();
      return parseFeedAsGemlog (url, ret, allPosts);
      }
    catch (Exception e)
      {
      throw new FeedException ("Error parsing atom feed: " + e.getMessage());
      }
    }

/*=========================================================================
  
  parseFeedAsGemlog

=========================================================================*/
  private int parseFeedAsGemlog (String url, String content,
          TreeSet<FeedPost> allPosts) throws FeedException
    {
    int count = 0;
    Date dNow = new Date();
    long now = dNow.getTime(); // msec
    int maxDays = config.getFeedsMaxAge();
    long msecLimit = 1000L * 3600L * 24L * maxDays;
    try
      {
      String displayName = GemUtil.getFirstHeading (content);
      if (displayName == null)
        displayName = FileUtil.getDisplayNameFromURI (new URL (url));

      BufferedReader br = new BufferedReader 
        (new StringReader (content));
      String line = br.readLine();
      while (line != null)
        {
        if (line.indexOf ("=>") == 0)
          {
          GemLink link = GemLink.parse (line.substring(2).trim());
          URL fullURL = new URL (new URL(url), link.getUri()); 
          String text = link.getText();
          try
            {
            //System.out.println ("line=" + line);
            Date d = iso8601.parse (text);
            if (now - d.getTime() < msecLimit)
              {
              // If parse succeds, we can skip ten characters for the date
              text = text.substring (10).trim();
              if (text.startsWith ("-")) text = text.substring(1).trim();
              String articleId = displayName + ": " + text;
              // TODO exclude out-of-date right here
              FeedPost fp = new FeedPost (d, fullURL, articleId);
              allPosts.add (fp);
              count++;
              }
            }
          catch (ParseException e)
            {
            }
          }
        line = br.readLine();
        }
      }
    catch (Exception e) 
      {
      throw new FeedException (e);
      }
    return count;
    }

/*=========================================================================
  
  removeFeedManagerStatusListener

=========================================================================*/
  @Override
  public void removeFeedManagerStatusListener (FeedManagerStatusListener l)
    {
    feedManagerStatusListener = null;
    } 

/*=========================================================================
  
  start 

=========================================================================*/
  @Override
  public void start()
    {
    if (isRunning()) return;
    TreeSet<FeedPost> allPosts = new TreeSet<FeedPost>();
    readFromFile();

    // Let's make sure we can write the aggregated feeds file before
    //   starting work -- it will be irritating to have to wait,
    //   and then not be able to store the results.

    String aggregatedFeedsFile = config.getAggregatedFeedsFile();
    try
      {
      config.ensureAggregatedFeedsFileExists();
      }
    catch (Exception e)
      {
      DialogHelper.errorDialog (null, aggregatedFeedsFile, e.getMessage());
      return;
      }

    if (feedManagerStatusListener != null) 
            feedManagerStatusListener.started(); 
    statusHandler.writeMessage 
      (messagesBundle.getString("feed_aggregation_started"));

    worker = new SwingWorker()  
      { 
      private void reportError (String url, String s)
        { 
        if (url != null) s = url + ": " + s;
        publish (s);
        }

      private void reportException (String url, Exception e)
        { 
        reportError (url, e.toString());
        }

      private void reportUpdate (String url, String s)
        { 
        if (url != null) s = url + ": " + s;
        publish (s);
        }

      @Override
      protected String doInBackground() 
        { 
        int n = feeds.size();
        for (int i = 0; i < n; i++)
          {
          GemLink link = feeds.elementAt (i);
          String url = link.getUri();
          reportUpdate (url, "Fetching content");
          ResponseContent rc = ContentFetcher.fetch (url);
          if (rc.getException() == null)
            {
            String mime = rc.getMime();
            String encoding = "UTF-8";
            if (mime != null)
              encoding = FileUtil.getEncodingFromMime (mime);
            try
              {
              if (mime.startsWith ("text/gemini"))
                {
                String content = new String (rc.getContent(), encoding);
                reportUpdate (url, messagesBundle.getString ("parsing_as_gemlog"));
                int count = parseFeedAsGemlog (url, content, allPosts);
                reportUpdate (url, "Posts added: " + count);
                }
              else if (mime.startsWith ("application/xml"))
                {
                String content = new String (rc.getContent(), encoding);
                reportUpdate (url, messagesBundle.getString ("parsing_as_atom"));
                int count = parseFeedAsAtom (url, content, allPosts);
                reportUpdate (url, "Posts added: " + count);
                }
              else if (mime.startsWith ("text/xml"))
                {
                String content = new String (rc.getContent(), encoding);
                reportUpdate (url, messagesBundle.getString ("parsing_as_atom"));
                int count = parseFeedAsAtom (url, content, allPosts);
                reportUpdate (url, "Posts added: " + count);
                }
              else if (mime.startsWith ("application/atom+xml"))
                {
                reportUpdate (url, messagesBundle.getString ("parsing_as_atom"));
                String content = new String (rc.getContent(), encoding);
                int count = parseFeedAsAtom (url, content, allPosts);
                reportUpdate (url, messagesBundle.getString 
                  ("posts_added") + ": " + count);
                }
              else
                {
                reportError (url, 
                  messagesBundle.getString ("response_not_feed") + ": " + mime);
                }
              }
            catch (UnsupportedEncodingException e)
              {
              reportException (url, e);
              }
            catch (FeedException e)
              {
              reportException (url, e);
              }
            }
          else
            {
            reportException (url, rc.getException());
            }
          }

        return "";
        }

      @Override
      protected void done()  
        { 
        if (isCancelled())
          {
          statusHandler.writeMessage (messagesBundle.getString
            ("feed_aggregation_cancelled"));
          reportUpdate (null, messagesBundle.getString ("cancelled"));
          if (feedManagerStatusListener != null) 
            feedManagerStatusListener.cancelled(); 
          }
        else
          {
          writeAggregatedFeedsFile (allPosts, aggregatedFeedsFile);
          statusHandler.writeMessage (messagesBundle.getString
            ("feed_aggregation_complete"));
          reportUpdate (null, "Post count after depulication: " + allPosts.size());
          reportUpdate (null, messagesBundle.getString ("done"));
          if (feedManagerStatusListener != null) 
            feedManagerStatusListener.finished(); 
          }
        worker = null;
        }

      @Override
      protected void process (java.util.List chunks) 
        { 
        for (Object message : chunks)
          {
          String s = (String) message;
          if (feedManagerStatusListener != null) 
            feedManagerStatusListener.newMessage (s); 
          }
        } 
      }; // End of SwingWorker definition
    worker.execute();
    }

/*=========================================================================
  
  showFeeds 

=========================================================================*/
  /** Show all the feeds by opening the feeds file -- which is
      just Gemtext -- in a new viewer window. */
  @Override
  public void showFeeds() throws IOException
    {
    config.ensureFeedsFileExists();
    String feedsFile = config.getFeedsFile();
    URL u = new File (feedsFile).toURL();
    MainWindow.newWindow (u, captionsBundle.getString ("feeds"));
    }

/*=========================================================================
  
  writeAggregatedFeedsFile 

  Note that this method is called after all the background aggregation
  has been done, and is on the main UI thread.

=========================================================================*/
  private void writeAggregatedFeedsFile (TreeSet<FeedPost> allPosts, 
      String filename)
    {
    try 
      (
      BufferedWriter bw = new BufferedWriter (new FileWriter (filename));
      )
      {
      bw.write ("# ");
      bw.write (messagesBundle.getString("feed_aggregated_title"));
      bw.write ("\n");
      String lastDateStr = null;
      if (allPosts.size() > 0)
        {
        lastDateStr = null;
        for (FeedPost fp : allPosts)
          {
          Date d = fp.getDate();
          String dateStr = sdf.format (d);
          if (!dateStr.equals (lastDateStr))
            {
            bw.write ("## ");
            bw.write (dateStr);
            bw.write ("\n");
            }
          lastDateStr = dateStr;
          bw.write ("=> ");
          bw.write (fp.getUrl().toString());
          bw.write (" ");
          bw.write (fp.getArticleId());
          bw.write ("\n");
          }
        }
      else
        {
        bw.write (messagesBundle.getString ("feed_no_updates"));
        bw.write ("\n");
        }
      bw.flush();
      bw.close();
      }
    catch (Exception e)
      {
      DialogHelper.errorDialog (null, filename, e.getMessage());
      return;
      }
    }
  }


