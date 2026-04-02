/*=========================================================================
  
  JGemini

  MainWindow

  Copyright (c)2021-6 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.protocol.*;
import me.kevinboone.jgemini.converters.*;
import me.kevinboone.utils.mime.MimeUtil;
import net.fellbaum.jemoji.*;

public class MainWindow extends JFrame implements ConfigChangeListener
  {
  private final static String DIALOG_CAPTION = Strings.APP_NAME;
  private final static String WINDOW_CAPTION = Strings.APP_NAME;
  private final static String EMPTY_WINDOW_TEXT = Strings.EMPTY_WINDOW_TEXT;

  private JEditorPane jEditorPane;
  private URL baseUri = null;
  private TopBar topBar;
  private StatusBar statusBar;
  private JMenuBar menuBar;
  private Stack<URL> backlinks = new Stack<URL>();
  private SwingWorker loadWorker = null;
  private String searchText = "java"; // TODO
  private int searchPos = 0;
  private ResponseContent lastContent = null; // Last successful download
  private javax.swing.Timer loadTimer = null;
  private Config config = Config.getConfig();
  private String displayName = null; // Derived from URI or loaded content
  private BookmarkHandler bookmarkHandler 
    = new DefaultBookmarkHandler(this);


/*=========================================================================
  
  Constructor

=========================================================================*/
  public MainWindow ()
    {
    super();

    Logger.in();

    jEditorPane = new JEditorPane();
    // Suppress the tiny border around the editor, which is visible
    //   in dark more
    jEditorPane.setBorder (BorderFactory.createEmptyBorder (0, 0, 0, 0)); 
    HTMLEditorKit kit = new HTMLEditorKit();
    jEditorPane.setEditorKit (kit);
    jEditorPane.setEditable (false);

    // In order to use ctrl+h and backspace in the menu, we have
    //  to disable them in the editor, even when (sigh) is it set
    //  to read-only
    fiddleWithKeyMap (kit);
  
    // Add a listener for "hyperlink" events. These amount to left-clicks
    //   on links in the editor. We need a separate, custom class
    //   (sigh) for right-clicks. Don'cha just love Swing?
    jEditorPane.addHyperlinkListener (new HyperlinkListener()
          {
          public void hyperlinkUpdate(HyperlinkEvent e) 
            {
            if (e.getEventType() == HyperlinkEvent.EventType.ENTERED)
              {
              handleLinkHover (e.getURL());
              }
            else if (e.getEventType() == HyperlinkEvent.EventType.EXITED)
              {
              handleLinkUnhover (e.getURL());
              }
            else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
              {
              handleLinkClick (e.getURL());
              }
            } 
          });
   
    // Add a custom listener for right-click events on links
    jEditorPane.addMouseListener (new RightMouseLinkListener()
      {
      public void clicked (String href, int x, int y)
        {
        Logger.log (getClass().getName(), Logger.DEBUG, "Right click");
        handleRightClick (href, x, y);
        }
      });

    MainWindow mainWindow = this;

    // Get focus to the editor whenever this frame gets focus. We
    //   need this to happen, so that the navigation keys work
    addWindowListener (new WindowAdapter() 
      {
      public void windowOpened(WindowEvent e) 
        {
        Logger.log (getClass().getName(), Logger.DEBUG, 
           "Request focus on editor");
        jEditorPane.requestFocus();
        }
      public void windowClosing (WindowEvent e) 
        {
        Config.getConfig().removeConfigChangeListener (mainWindow); 
        dispose();
        }
      });

    applyInitialStyles ();

    // Create the frame components and layout.
    topBar = new TopBar(this);
    statusBar = new StatusBar();
    StatusHandler.getInstance().addListener (statusBar);
    JScrollPane scrollPane = new JScrollPane (jEditorPane);

    getContentPane().add(topBar, BorderLayout.NORTH);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(statusBar, BorderLayout.SOUTH);

    Document doc = kit.createDefaultDocument();
    jEditorPane.setDocument(doc);
    jEditorPane.setText ("<p align=\"center\">" + EMPTY_WINDOW_TEXT + "</p>");

    setSize (config.getWindowWidth(), 
       config.getWindowHeight());
    setLocationByPlatform (true); // Let desktop position me

    //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    createMenuBar();
    setJMenuBar (menuBar);
    setTitle (WINDOW_CAPTION);

    // Set the frame's icon from a file in the JAR
    URL iconURL = getClass().getResource ("/images/jgemini.png");
    ImageIcon icon = new ImageIcon (iconURL);
    setIconImage (icon.getImage());

    topBar.loadHistoryFile();
    config.addConfigChangeListener (this); 

    Logger.out();
    }


/*=========================================================================
  
  about 

=========================================================================*/
  public void about()
    {
    String s = "<html><head></head><body style='margin: 20'>";
    s += "<h1>" + Strings.APP_NAME + "</h1>";
    s += "<h3>" + Strings.VERSION + " " + Config.VERSION + "</h3>";
    s += "<p>" + Strings.ABOUT_MESSAGE + "</p>\n";
    s += "<p>&nbsp;</p></body>\n";
    JOptionPane.showMessageDialog (this, s, 
         DIALOG_CAPTION, JOptionPane.INFORMATION_MESSAGE); 
    }


/*=========================================================================
  
  bookmarkPage

=========================================================================*/
  public void bookmarkPage() 
    {
    if (displayName == null)
      {
      // TODO : prompt for display name??
      }
    if (displayName != null)
      {
      try
        {
        String bookmarkName = displayName;
        if (config.emojiStripBookmark())
          bookmarkName = EmojiManager.removeAllEmojis (bookmarkName);
        if (bookmarkHandler.addBookmark (bookmarkName, baseUri))
          setStatus (Strings.BOOKMARK_ADDED);
        else
          setStatus (Strings.ALREADY_BOOKMARKED);
        }
      catch (IOException e)
        {
        JOptionPane.showMessageDialog (this, e.getMessage(), 
           DIALOG_CAPTION, JOptionPane.ERROR_MESSAGE); 
        }
      }
    }


/*=========================================================================
  
  cancelLoad

=========================================================================*/
  private void cancelLoad ()
    {
    Logger.in();
    if (loadWorker != null)
      {
System.out.println ("LOADWORKER not null");
      Logger.log (getClass().getName(), Logger.DEBUG, 
         "Cancelling loadWorker");
      loadWorker.cancel (true);
      if (loadTimer != null) loadTimer.stop();
      loadTimer = null;
      }
    else
      {
      Logger.log (getClass().getName(), Logger.DEBUG, "No load to cancel");
      }
    Logger.out();
    }

/*=========================================================================
  
  configChanged 

=========================================================================*/
  public void configChanged() 
    {
    Logger.in();
    applyInitialStyles();
    String s = jEditorPane.getText ();
    jEditorPane.setText (s);
    Logger.out();
    }

/*=========================================================================
  
  createMenuBar

=========================================================================*/
  private void createMenuBar ()
    {
    menuBar = new JMenuBar();

    // TODO: we need to change the accelerator keys if the menu
    //   text changes.

    // File|Settings submenu
    JMenu settingsSubMenu = new JMenu (Strings.SETTINGS); 
    settingsSubMenu.setMnemonic (KeyEvent.VK_E);
    JMenuItem editMenuItem = new JMenuItem (Strings.EDIT + "..."); 
    editMenuItem.addActionListener((event) -> editSettings());
    editMenuItem.setMnemonic (KeyEvent.VK_E);
    JMenuItem reloadMenuItem = new JMenuItem (Strings.RELOAD); 
    reloadMenuItem.addActionListener((event) -> reloadSettings());
    reloadMenuItem.setMnemonic (KeyEvent.VK_R);
    settingsSubMenu.add (editMenuItem);
    settingsSubMenu.add (reloadMenuItem);

    // File menu
    JMenu fileMenu = new JMenu(Strings.FILE);
    fileMenu.setMnemonic (KeyEvent.VK_F);
    JMenuItem newMenuItem = new JMenuItem (Strings.NEW);
    newMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    newMenuItem.setMnemonic (KeyEvent.VK_N);
    newMenuItem.addActionListener((event) -> newWindow());
    fileMenu.add (newMenuItem);
    JMenuItem openMenuItem = new JMenuItem (Strings.OPEN_LINK);
    openMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    openMenuItem.setMnemonic (KeyEvent.VK_O);
    openMenuItem.addActionListener((event) -> openLink());
    fileMenu.add (openMenuItem);
    JMenuItem saveMenuItem = new JMenuItem (Strings.SAVE);
    saveMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    saveMenuItem.setMnemonic (KeyEvent.VK_S);
    saveMenuItem.addActionListener((event) -> save());
    fileMenu.add (saveMenuItem);
    fileMenu.add (new JSeparator());
    JMenuItem setAsHomeMenuItem = new JMenuItem (Strings.SET_AS_HOME);
    setAsHomeMenuItem.setMnemonic (KeyEvent.VK_H);
    setAsHomeMenuItem.addActionListener((event) -> setAsHome());
    fileMenu.add (setAsHomeMenuItem);
    fileMenu.add (new JSeparator());
    fileMenu.add (settingsSubMenu);
    fileMenu.add (new JSeparator());
    JMenuItem closeMenuItem = new JMenuItem (Strings.CLOSE);
    closeMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_W, ActionEvent.CTRL_MASK));
    closeMenuItem.setMnemonic (KeyEvent.VK_C);
    closeMenuItem.addActionListener((event) -> 
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
    fileMenu.add (closeMenuItem);
    JMenuItem exitMenuItem = new JMenuItem (Strings.EXIT);
    exitMenuItem.setMnemonic (KeyEvent.VK_X);
    exitMenuItem.addActionListener((event) -> System.exit(0));
    fileMenu.add (exitMenuItem);

    // Edit menu
    JMenu editMenu = new JMenu (Strings.EDIT);
    editMenu.setMnemonic (KeyEvent.VK_E);
    JMenuItem selectAllMenuItem = new JMenuItem (Strings.SELECT_ALL);
    selectAllMenuItem.setMnemonic (KeyEvent.VK_A);
    selectAllMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    selectAllMenuItem.addActionListener((event) -> jEditorPane.selectAll());
    editMenu.add (selectAllMenuItem);
    JMenuItem copyMenuItem = new JMenuItem (Strings.COPY);
    copyMenuItem.setMnemonic (KeyEvent.VK_C);
    copyMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    copyMenuItem.addActionListener((event) -> jEditorPane.copy());
    editMenu.add (copyMenuItem);
    JMenuItem findMenuItem = new JMenuItem (Strings.FIND_IN_PAGE);
    findMenuItem.setMnemonic (KeyEvent.VK_F);
    findMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    findMenuItem.addActionListener((event) -> find());
    editMenu.add (findMenuItem);
    JMenuItem findNextMenuItem = new JMenuItem (Strings.FIND_NEXT);
    findNextMenuItem.setMnemonic (KeyEvent.VK_N);
    findNextMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_G, ActionEvent.CTRL_MASK));
    findNextMenuItem.addActionListener((event) -> findNext());
    editMenu.add (findNextMenuItem);

    // View menu
    JMenu viewMenu = new JMenu (Strings.VIEW);
    viewMenu.setMnemonic (KeyEvent.VK_V);
    JMenuItem zoomInMenuItem = new JMenuItem (Strings.ZOOM_IN);
    zoomInMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_OPEN_BRACKET, ActionEvent.CTRL_MASK));
    zoomInMenuItem.setMnemonic (KeyEvent.VK_I);
    zoomInMenuItem.addActionListener((event) -> zoomIn());
    viewMenu.add (zoomInMenuItem);
    JMenuItem zoomOutMenuItem = new JMenuItem (Strings.ZOOM_OUT);
    zoomOutMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_CLOSE_BRACKET, ActionEvent.CTRL_MASK));
    zoomOutMenuItem.setMnemonic (KeyEvent.VK_O);
    zoomOutMenuItem.addActionListener((event) -> zoomOut());
    viewMenu.add (zoomOutMenuItem);
    JMenuItem refreshMenuItem = new JMenuItem (Strings.REFRESH);
    refreshMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_R, ActionEvent.CTRL_MASK));
    refreshMenuItem.addActionListener((event) -> refresh());
    viewMenu.add (refreshMenuItem);

    // Bookmark menu
    JMenu bookmarksMenu = new JMenu (Strings.BOOKMARKS);
    bookmarksMenu.setMnemonic (KeyEvent.VK_B);
    JMenuItem showBookmarksMenuItem = new JMenuItem (Strings.SHOW_ALL);
    showBookmarksMenuItem.setMnemonic (KeyEvent.VK_S);
    showBookmarksMenuItem.addActionListener((event) -> showBookmarks());
    JMenuItem editBookmarksMenuItem = new JMenuItem (Strings.EDIT + "...");
    editBookmarksMenuItem.setMnemonic (KeyEvent.VK_E);
    editBookmarksMenuItem.addActionListener((event) -> editBookmarks());
    JMenuItem bookmarkPageMenuItem = new JMenuItem (Strings.BOOKMARK_THIS_PAGE);
    bookmarkPageMenuItem.setMnemonic (KeyEvent.VK_B);
    bookmarkPageMenuItem.addActionListener((event) -> bookmarkPage());

    bookmarksMenu.addMenuListener (new javax.swing.event.MenuListener()
      {
      public void menuCanceled (javax.swing.event.MenuEvent e)
        {
        }
      public void menuSelected (javax.swing.event.MenuEvent e)
        {
        bookmarksMenu.removeAll();
	bookmarksMenu.add (showBookmarksMenuItem);
	bookmarksMenu.add (editBookmarksMenuItem);
	bookmarksMenu.add (new JSeparator());
	bookmarksMenu.add (bookmarkPageMenuItem);
	bookmarksMenu.add (new JSeparator());
        int n = bookmarkHandler.getBookmarkCount();
        for (int i = 0; i < n && i < config.getBookmarkMaxMenu(); i++)
          {
          GemLink link = bookmarkHandler.getBookmarkLink (i);
          String text = link.getText();
          if (text.length() > 23)
            text = text.substring (0, 20) + "...";
          JMenuItem item;
          if (i < 10)
            item = new JMenuItem ("" + i + " " + text);
          else
            item = new JMenuItem (text);
	  bookmarksMenu.add (item);
          item.addActionListener((event) -> loadURI(link.getUri()));
          if (i < 10)
            item.setMnemonic (KeyEvent.VK_0 + i);
          }
        }

      public void menuDeselected (javax.swing.event.MenuEvent e)
        {
        }
      });

    // Go menu
    JMenu goMenu = new JMenu (Strings.GO);
    goMenu.setMnemonic (KeyEvent.VK_G);
    JMenuItem backMenuItem = new JMenuItem (Strings.BACK);
    backMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_BACK_SPACE, 0));
    backMenuItem.addActionListener((event) -> goBack());
    goMenu.add (backMenuItem);
    JMenuItem homeMenuItem = new JMenuItem (Strings.HOME);
    homeMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_H, ActionEvent.CTRL_MASK));
    homeMenuItem.addActionListener((event) -> goHome());
    goMenu.add (homeMenuItem);
    JMenuItem rootMenuItem = new JMenuItem (Strings.ROOT);
    rootMenuItem.setMnemonic (KeyEvent.VK_R);
    rootMenuItem.addActionListener((event) -> goRoot());
    goMenu.add (rootMenuItem);
    goMenu.add (new JSeparator());
    JMenuItem stopMenuItem = new JMenuItem (Strings.STOP);
    stopMenuItem.setMnemonic (KeyEvent.VK_S);
    stopMenuItem.addActionListener((event) -> goStop());
    goMenu.add (stopMenuItem);

    // Help menu
    JMenu helpMenu = new JMenu (Strings.HELP);
    helpMenu.setMnemonic (KeyEvent.VK_H);
    JMenuItem helpMenuItem = new JMenuItem (Strings.DOCUMENTATION);
    helpMenuItem.setMnemonic (KeyEvent.VK_D);
    helpMenuItem.addActionListener ((event) -> help());
    helpMenu.add (helpMenuItem);
    helpMenu.add (new JSeparator());
    JMenuItem aboutMenuItem = new JMenuItem (Strings.ABOUT 
      + " " + Strings.APP_NAME + "...");
    aboutMenuItem.setMnemonic (KeyEvent.VK_A);
    aboutMenuItem.addActionListener((event) -> about());
    helpMenu.add (aboutMenuItem);

    menuBar.add (fileMenu);
    menuBar.add (editMenu);
    menuBar.add (viewMenu);
    menuBar.add (bookmarksMenu);
    menuBar.add (goMenu);
    menuBar.add (helpMenu);
    }


/*=========================================================================
  
  clearStatus    

=========================================================================*/
  private void clearStatus ()
    {
    statusBar.clearStatus ();
    }


/*=========================================================================
  
  editBookmarks 

=========================================================================*/
  private void editBookmarks()
    {
    try
      {
      bookmarkHandler.editBookmarks();
      }
    catch (Exception e)
      {
      JOptionPane.showMessageDialog (this, e.getMessage(), 
         DIALOG_CAPTION, JOptionPane.ERROR_MESSAGE); 
      }
    }

/*=========================================================================
  
  enableTopBar

  Call once initial set-up is done. Otherwise the act of populating
  the history combobox makes it fire an event, which makes it load
  the first thing in the history. Sigh. Bloody Swing.

=========================================================================*/
  public void enableTopBar ()
    {
    Logger.in();
    topBar.enable();
    Logger.out();
    }


/*=========================================================================
  
   editSettings 

=========================================================================*/
  protected void editSettings()
    {
    Logger.in();

    try
      {
      Config.getConfig().ensureUserConfigFileExists();
      EditFileDialog d = new EditFileDialog (this, 
        Strings.EDIT_CONFIG_FILE,
        Config.getConfig().getUserConfigFilename());
      d.setVisible (true);
      if (d.didSave())
        Config.getConfig().load();
      }
    catch (Exception e) 
      {
      JOptionPane.showMessageDialog (this, e.getMessage(), // TODO -- expand
         DIALOG_CAPTION, JOptionPane.ERROR_MESSAGE); 
      }

    Logger.out();
    }

/*=========================================================================
  
   goHome

   Go back to the home page

=========================================================================*/
  protected void goHome ()
    {
    Logger.in();
    loadURI (config.getHomePage());
    Logger.out();
    }

/*=========================================================================
  
  getDisplayNameFromURI 

=========================================================================*/
public String getDisplayNameFromURI (URL uri)
  {
  String displayName = null;
  String path = uri.getPath();
  if (path.startsWith ("/~"))
    {
    String temp = path.substring (2);
    int i = temp.indexOf ("/"); 
    if (i >= 0)
      temp = temp.substring (0, i);
    displayName = temp;
    }
  else
    {
    displayName = uri.getHost();
    if (displayName.length() == 0) displayName = null;
    }

  int i = path.lastIndexOf ("/"); 
  if (i >= 0)
    {
    String temp = path.substring (i);
    if (temp.startsWith("/")) temp = temp.substring(1);
    if (displayName == null)
      displayName = temp;
    else if (!temp.equals ("/") && temp.length() > 0)
      displayName = displayName + ": " + temp; 
    }

  return displayName;
  }

/*=========================================================================
  
   goBack

   Go back to the previous page, if there was one.

=========================================================================*/
  protected void goBack ()
    {
    Logger.in();
    // Note that the current URL -- at there always will be one -- will
    //   be at the top of the stack. So we have to take that off, then get
    //   to the previous URL. If we take it off and there _isn'_ a 
    //   previous URL, we can't go anywhere, so we have to put the current
    //   URL back on the stack.
    URL current = backlinks.pop();
    if (backlinks.size() > 0)
      {
      URL backUrl = backlinks.pop();
      if (Logger.isDebug())
        Logger.log (getClass().getName(), Logger.DEBUG, "Back URL " + backUrl);
      loadURI (backUrl);
      }
    else
      {
      if (Logger.isDebug())
        Logger.log (getClass().getName(), Logger.DEBUG, 
          "back-link stack is empty");
      backlinks.push (current);
      }
    Logger.out();
    }

/*=========================================================================
  
   getRootUri 

   Get the site root. What that means depends on the URI. In particular,
   URIs containing a username (host:port/~fred) have their root at the
   user's top-level directory, not the server's top-level directory. 
=========================================================================*/
URL getRootUri (URL baseUri) throws MalformedURLException
  {
  String path = baseUri.getPath();
  if (path.startsWith ("/~"))
    {
    String temp = path.substring (2);
    int i = temp.indexOf ("/"); 
    temp = temp.substring (0, i >= 0 ? i : 0);
    java.net.URL newUrl = new URL (baseUri, "/~" + temp + "/");
    return newUrl;
    }
  else
    {
    java.net.URL newUrl = new URL (baseUri, "/"); 
    return newUrl;
    }
  }

/*=========================================================================
  
   goRoot

=========================================================================*/
  protected void goRoot()
    {
    Logger.in();
    try
      {
      loadURI (getRootUri (baseUri));
      }
    catch (Exception e)
      {
      e.printStackTrace();
      }
    Logger.out();
    }

/*=========================================================================
  
   goStop

=========================================================================*/
  protected void goStop()
    {
    Logger.in();
    cancelLoad();
    Logger.out();
    }

/*=========================================================================
  
  handleStatus10 

  Deal with "Status 10" responses, that require further input.
  We just prompt the user for a string, and then repeat the
  original request.

=========================================================================*/
  private void handleStatus10 (boolean hide, String prompt, URL retryUrl)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, 
        "Handling status 10, hide= " + hide + "with prompt=" 
           + prompt + ", url=" + retryUrl);

    int startingCount = retryUrl.toString().getBytes().length;
    // Max URL is 1024 for Gemini
    TextEntryDialog d = new TextEntryDialog (this, 1024 - startingCount);
    d.setVisible (true);
    String str = d.getInput();
    if(str != null)
      {
      if (Logger.isDebug())
        Logger.log (getClass().getName(), Logger.DEBUG, 
          "Retrying URL " + retryUrl);
      loadFromUri (retryUrl, str); 
      }
    Logger.out();
    }

/*=========================================================================
  
  handleRedirect

  At present, this is easy -- just show the URL. However, we need to
  think about the implications for bookmarking, and also the
  back-link stack. But the big problem is redirect loops, which 
  the program currently doesn't handle at all. It's difficult when all
  the loading happens on background threads.

=========================================================================*/
  private void handleRedirect (URL url)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "Redirect to " + url);
    loadURI (url);
    Logger.out();
    }

/*=========================================================================
  
  handleUnsupportedMime

  At present, any response type other than text/something is written to
  a temp file, and then the desktop is invoked on it. What happens
  after that is down to the interaction between the JVM and the
  platform.

=========================================================================*/
  private void handleUnsupportedMime (URL url, String mime, byte[] content)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "mime=" + mime);
    try
      {
      String ext = FileUtil.getDefaultExtension (mime);
      File tempFile = File.createTempFile ("gemini-", "." + ext);
      tempFile.deleteOnExit();
      if (Logger.isDebug())
        Logger.log (getClass().getName(), Logger.DEBUG, 
          "tempFile is " + tempFile);
      FileUtil.byteArrayToFile (tempFile, content);
      Desktop.getDesktop().browse (java.net.URI.create 
        ("file://" + tempFile));
      }
    catch (Exception e)
      {
      reportException (url.toString(), e);
      }
    Logger.out();
    }


/*=========================================================================
  
   help 

=========================================================================*/
  private void help()
    {
    newWindow ("about:/index.md", WINDOW_CAPTION + ": " 
      + Strings.DOCUMENTATION);
    }

/*=========================================================================
  
   openLink 

   Prompt the user for a URL, and try to open it

=========================================================================*/
  private void openLink ()
    {
    Logger.in();
    String url = JOptionPane.showInputDialog (this, Strings.ENTER_GEMINI_URL, 
      DIALOG_CAPTION, 1);
    if (url != null)
      {
      try
        {
        if (Logger.isDebug())
          Logger.log (getClass().getName(), Logger.DEBUG, "User selected " + url);
        loadURI (new URL(url));
        }
      catch (Exception e)
        {
        reportException (url, e);
        }
      }
    Logger.out();
    }


/*=========================================================================
  
  loadResponseContent
  
  Make the request on the server, and pack the results into a 
  ResponseContent object. The results may include an exception -- this
  method itself must not throw any exception

=========================================================================*/
  private ResponseContent loadResponseContent (URL url)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "url=" + url);
    ResponseContent gc = new ResponseContent (url);
    try
      {
      URLConnection conn = url.openConnection();
      Object o = conn.getContent();
      byte[] content;
      if (o instanceof BufferedInputStream)
        {
        BufferedInputStream bis = (BufferedInputStream)o;
        content = bis.readAllBytes(); 
        }
      else
        content = (byte[]) o; 
      String mime = conn.getContentType();
      gc.setMime (mime);
      gc.setContent (content);
      }
    catch (Exception e)
      {
      // Note that not all exceptions relate to errors. For example,
      //   redirection responses are treated as exceptions.
      gc.setException (e);
      }
    Logger.out();
    return gc;
    }

/*=========================================================================
  
  removeLastContent

=========================================================================*/
  private void removeLastContent()
    {
    lastContent = null;
    }
  
/*=========================================================================
  
  setLastContent

=========================================================================*/
  private void setLastContent (ResponseContent gc)
    {
    lastContent = gc;
    }

/*=========================================================================
  
  handleResponseContent 

  We call this when a request has been completed successfully, and we
    have a ResponseContent instance that reflects the response from
    the server. We'll use the MIME type and/or filename to decide what
    to do with the response.

=========================================================================*/
  private void handleResponseContent (URL fullUrl, ResponseContent gc)
    {
    Logger.in();
    String mime = gc.getMime();
    //System.out.println ("MIME=" + mime);
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "Content is " + mime);
    URL url = gc.getURL(); 
    String urlStr = url.toString();
    if (mime.startsWith ("text/gemini") || urlStr.endsWith (".gmi"))
      {
      baseUri = fullUrl; 
      String encoding = MimeUtil.getEncodingFromMime (mime);
      renderGemtext (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      backlinks.push (fullUrl);
      setLastContent (gc);
      }
    else if (mime.startsWith ("text/gophermap")|| urlStr.endsWith (".gopher")) // Not a real MIME
      {
      baseUri = fullUrl; 
      String encoding = MimeUtil.getEncodingFromMime (mime);
      renderGophermap (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      setLastContent (gc);
      backlinks.push (fullUrl);
      }
    else if (mime.startsWith ("text/plain") || urlStr.endsWith (".txt"))
      {
      baseUri = fullUrl; 
      String encoding = MimeUtil.getEncodingFromMime (mime);
      renderPlain (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      backlinks.push (fullUrl);
      setLastContent (gc);
      }
    else if (mime.startsWith ("text/nex")) // Not a real MIME
      {
      baseUri = fullUrl; 
      String encoding = MimeUtil.getEncodingFromMime (mime);
      renderNex (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      setLastContent (gc);
      backlinks.push (fullUrl);
      }
    else if (mime.startsWith ("text/markdown") || urlStr.endsWith (".md"))
      {
      baseUri = fullUrl; 
      String encoding = MimeUtil.getEncodingFromMime (mime);
      renderMarkdown (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      setLastContent (gc);
      backlinks.push (fullUrl);
      }
    else if (mime.startsWith ("application/atom+xml"))
      {
      // MIME types for Atom feeds are often ambiguous, but this
      //   one is not. Just invoke the converter.
      baseUri = fullUrl; 
      String encoding = MimeUtil.getEncodingFromMime (mime);
      renderAtom (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      setLastContent (gc);
      backlinks.push (fullUrl);
      }
    else if (mime.startsWith ("text/xml") 
         || mime.startsWith ("application/xml"))
      {
      String encoding = MimeUtil.getEncodingFromMime (mime);

      // We'll have to look inside the XML, to figure out whether
      //   it's a feed or not

      String xml = "";
      try
        {
        xml = new String (gc.getContent(), encoding);
        }
      catch (UnsupportedEncodingException e)
        {
        xml = new String (gc.getContent());
        }
    
      if (xml.indexOf ("<feed ") >= 0)
        {
        baseUri = fullUrl; 
        renderAtom (gc.getContent(), encoding);
        topBar.showUrl (fullUrl.toString());
        setLastContent (gc);
        backlinks.push (fullUrl);
        }
      else
        {
        handleUnsupportedMime (fullUrl, mime, gc.getContent());
        }
      }
    else if (mime.startsWith ("image/"))
      {
      loadURIEmbedImage (fullUrl);
      }
    else
      {
      handleUnsupportedMime (fullUrl, mime, gc.getContent());
      }
    Logger.out();
    }
  
/*=========================================================================
  
  loadFromUri
  
  We have a gemini:// or spartan:// URL. Load it through its 
    content handler.

  If the qparam arg is non-null, it is appended as a query parameter 
  _as is_.  

=========================================================================*/
  private void loadFromUri (URL url, String qparam)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "loadFromUri(), " + url);

    ActionListener loadTimerListener = new ActionListener() 
      {
      public void actionPerformed (ActionEvent evt) 
        {
        //System.out.println ("loadtimer action performed" + loadTimer);
	setStatus (Strings.LOADING);
        }
      };

    cancelLoad(); // Kill any existing background load
    removeLastContent(); // Delete any data associated with the last request

    try
      {
      if (qparam != null)
        {
        // URLEncoder does seem on its own to generate encodings that Gemini 
        //   services like. They seem to prefer "$20" to "+" for plain spaces.
        //   Since %20 will always work, munge the encoded string to use 
        //   this format.
        URL tempUrl = new URL (url.toString() + "?" + URLEncoder.encode (qparam));
        String sURL = tempUrl.toString().replace("+","%20");
        url = new URL (sURL);
        }

      if (url.getPath().length() == 0)
        {
        //Logger.log (getClass().getName(), Logger.DEBUG, 
        //  "Adding path to URL that lacks one");
        //url = new URL (url.toString() + "/");
        }
      }
    catch (Exception e)
      {
      // Fallen at the first hurdle.
      reportException (url.toString(), e);
      return;
      }

    final URL fullUrl = url;

    // Now set up a SwingWorker to do the load in the background

    loadWorker = new SwingWorker()  
      { 
      ResponseContent gc = null;

      @Override
      protected String doInBackground() throws Exception  
        { 
        Logger.log (getClass().getName(), Logger.DEBUG, 
          "Load worker thread doInBackground()");
        loadTimer = new javax.swing.Timer (1000, loadTimerListener);
        loadTimer.setRepeats (true);
        loadTimer.start(); // TODO
        //System.out.println ("loadtimer start" + loadTimer);
        setStatus (Strings.LOADING + fullUrl);
        gc = loadResponseContent (fullUrl); 
        return "foo"; // Meaningless return
        } 

      @Override
      protected void process (java.util.List chunks) 
        { 
        // Do nothing here (but must be implemented)
        } 

      @Override
      protected void done()  
        { 
        Logger.log (getClass().getName(), Logger.DEBUG, 
          "Load worker thread done()");
	if (loadTimer != null) 
          {
          loadTimer.stop();
          //System.out.println ("loadtimer stop" + loadTimer);
          }
	loadTimer = null;
        clearStatus ();
        if (!isCancelled())
          {
          Exception e = gc.getException();
          if (e == null)
            {
            handleResponseContent (fullUrl, gc);
            setCaptionFromResponse (fullUrl, gc);
            }
         else
            {
            if (e instanceof RetryWithInputException)
              {
              RetryWithInputException e2 = (RetryWithInputException)e;
              handleStatus10 (e2.getHide(), e2.getPrompt(), e2.getURL());
              }
            else if (e instanceof RedirectedException)
              {
              handleRedirect (((RedirectedException)e).getURL());
              }
            else if (e instanceof ErrorResponseException)
              {
              e.printStackTrace();
              reportErrorResponseException (fullUrl.toString(), 
                 (ErrorResponseException)e); 
              }
            else 
              {
              e.printStackTrace();
              reportException (fullUrl.toString(), e); 
              }
            }
          }
        loadWorker = null;
        }
      }; 

    loadWorker.execute();  
    Logger.out();
    }


/*=========================================================================
  
  loadNex
  
  We have a nex:// URL. Load it through its content handler.

  // *** Merged with loadFromUri() ***

=========================================================================*/
/*
  private void loadNex (URL url, String qparam)
    {
    ActionListener loadTimerListener = new ActionListener() 
      {
      public void actionPerformed (ActionEvent evt) 
        {
	setStatus (Strings.LOADING);
        }
      };

    cancelLoad(); // Kill any existing background load
    removeLastContent(); // Delete any data associated with the last request
    try
      {
      Logger.log (getClass().getName(), Logger.DEBUG, "loadNex(), url=" 
        + url.toString() + ", qparam=" + qparam);

      if (qparam != null)
        {
        // URLEncoder does seem on its own to generate encodings that Gemini 
        //   services like. They seem to prefer "$20" to "+" for plain spaces.
        //   Since %20 will always work, munge the encoded string to use 
        //   this format.
        URL tempUrl = new URL (url.toString() + "?" + URLEncoder.encode (qparam));
        String sURL = tempUrl.toString().replace("+","%20");
        url = new URL (sURL);
        }

      if (url.getPath().length() == 0)
        {
        Logger.log (getClass().getName(), Logger.DEBUG, "Adding path to URL that lacks one");
        url = new URL (url.toString() + "/");
        }
      }
    catch (Exception e)
      {
      // Fallen at that first hurdle.
      reportException (url.toString(), e);
      return;
      }

    final URL fullUrl = url;

    loadWorker = new SwingWorker()  
      { 
      ResponseContent gc = null;

      @Override
      protected String doInBackground() throws Exception  
        { 
        loadTimer = new javax.swing.Timer (1000, loadTimerListener);
        loadTimer.setRepeats (true);
        loadTimer.start();
        setStatus (Strings.LOADING + fullUrl);
        gc = loadResponseContent (fullUrl); 
        return "foo"; // Meaningless return
        } 

      @Override
      protected void process (java.util.List chunks) { } 

      @Override
      protected void done()  
        { 
	loadTimer.stop();
	loadTimer = null;
        if (!isCancelled())
          {
          clearStatus ();
          Exception e = gc.getException();
          if (e == null)
            {
            handleResponseContent (fullUrl, gc);
            }
         else
            {
            if (e instanceof ErrorResponseException)
              {
              e.printStackTrace();
              reportErrorResponseException (fullUrl.toString(), 
                 (ErrorResponseException)e); 
              }
            else 
              {
              e.printStackTrace();
              reportException (fullUrl.toString(), e); 
              }
            }
          }
        loadWorker = null;
        }
      }; 

    loadWorker.execute();  
    }
*/

/*=========================================================================
  
  reportException

  Do something vaguely useful with exceptions

=========================================================================*/
  private void reportException (String url, Exception e)
    {
    e.printStackTrace();
    String messageFromServer = e.getMessage();
    if (e instanceof UnknownHostException)
      {
      reportGenError (url, Strings.UNKNOWN_HOST + e.getMessage());
      }
    else
      reportGenError (url, e.getMessage());
    }

/*=========================================================================
  
  reportErrorResponseException

  Do something vaguely useful with server error responses 

=========================================================================*/
  private void reportErrorResponseException (String url, 
           ErrorResponseException e)
    {
    String messageFromServer = e.getMessage();
    String message = "Error response " + e.getStatus() + " from server";
    if (messageFromServer != null && messageFromServer.length() > 0)
      message += ": <i>" + messageFromServer + "</i>";
    e.printStackTrace();
    reportGenError (url, message);
    }

/*=========================================================================
  
  reportGenError

  Do something vaguely useful with error messages

=========================================================================*/
  private void reportGenError (String url, String message)
    {
    StringBuffer sb = new StringBuffer();
    // We need to override styles here, because the styles applied to the
    //   main window -- which is also an HTML viewer -- will also be
    //   partially applied here, and look odd. 
    sb.append ("<html><body width=\"400px\" style=\"margin: 30\">");
    sb.append ("<p><b>");
    // Be aware that a URL could, in theory, be > 1000 characters long.
    // We don't want to put all those into the dialog box.
    if (url.length() > 50)
      url = url.substring (0, 20) + "...";
    sb.append (url);
    sb.append ("</b></p><p></p><p>");
    sb.append (message);
    sb.append ("</p><p></p>");
    JOptionPane.showMessageDialog (this, new String(sb), 
         DIALOG_CAPTION, JOptionPane.ERROR_MESSAGE); 
    sb.append ("</body></html>");
    }

/*=========================================================================
  
   loadForeignURI 

   Any attempt to load a URI that does not start with a supported protocol
   ends up here.  At present, we just use the Java desktop support to try to 
   invoke a handler for it.

=========================================================================*/
  private void loadForeignURL (URL uri)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, "URI=" + uri.toString());
    try 
      {
      Desktop.getDesktop().browse (new URI (uri.toString()));
      }
    catch (Exception e)
      {
      reportException (uri.toString(), e);
      }
    Logger.out();
    }


/*=========================================================================
  
   loadURIEmbedImage


=========================================================================*/
  public void loadURIEmbedImage (URL url)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG,  
         "Embedding image URL into HTML: " + url);

    removeLastContent();
    setHtml ("<img src=\"" + url + "\"/>");
    topBar.showUrl (url.toString());
    backlinks.push (url);
    setCaptionFromResponse (url, null);
    Logger.out();
    }

/*=========================================================================
  
   loadURI

   Load any kind of URL. If it doesn't start with gemini://, treat is
   as external, which means invoking the desktop on it.

=========================================================================*/
  public void loadURI (URL uri)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, "URI=" + uri);

    if (GemConverter.isImageUri (uri.toString()))
      {
      loadURIEmbedImage (uri);
      }
    else
      { 
      if (uri.getProtocol().equals ("gemini"))
	{
	loadFromUri (uri, null);
	baseUri = uri;
	}
      else if (uri.getProtocol().equals ("spartan"))
	{
	loadFromUri (uri, null);
	baseUri = uri;
	}
      else if (uri.getProtocol().equals ("gopher"))
	{
        String path = uri.getPath();
        if (path.startsWith ("/7/"))
          {
          TextEntryDialog d = new TextEntryDialog (this, 1024);
          d.setVisible (true);
          String str = d.getInput();
	  if (str != null)
	    {
            try
              {
	      loadFromUri (uri, str); 
              } catch (Exception e){}
	    baseUri = uri;
	    }
          }
        else
          {
	  loadFromUri (uri, null);
	  baseUri = uri;
          }
	}
      else if (uri.getProtocol().equals ("nex"))
	{
	loadFromUri (uri, null);
	baseUri = uri;
	}
      else if (uri.getProtocol().equals ("about"))
	{
	loadFromUri (uri, null);
	baseUri = uri;
	}
      else if (uri.getProtocol().equals ("file"))
	{
	loadFromUri (uri, null);
	baseUri = uri;
	}
      else
	{
	loadForeignURL (uri);
	} 
      }
    Logger.out();
    }

/*=========================================================================
  
   loadURI (String)

   Load any kind of URL. If it doesn't start with gemini://, treat it
   as external, which means invoking the desktop on it.

   Mostly delegates to loadURI (URL)

=========================================================================*/
  public void loadURI (String url)
    {
    // We need to do something if what the user enters doesn't seem
    //   to be a full URL. The following is a pretty crude approach.
    if (!url.contains (":"))
       {
       if ((url.contains (" ") || !url.contains (".")) 
            && Config.getConfig().urlbarSearchEnabled())
         {
         String qparam = URLEncoder.encode (url);
         qparam = qparam.replace("+","%20");
         url = Config.getConfig().getUrlbarSearchUrl() + "?" +
           qparam; 
         }
       else
         url = "gemini://" + url;
       }
    try
      {
      loadURI (new URL(url));
      }
    catch (Exception e)
      {
      reportException (url, e);
      }
    }


/*=========================================================================
  
   zoomIn 

=========================================================================*/
  public void zoomIn()
    {
    Logger.in();
    int n = config.getDocumentBaseFontSize();
    config.setDocumentBaseFontSize (n + 1); 
    applyInitialStyles();
    String s = jEditorPane.getText ();
    jEditorPane.setText (s);
    Logger.out();
    }

/*=========================================================================
  
   zoomOut

=========================================================================*/
  public void zoomOut()
    {
    Logger.in();
    int n = config.getDocumentBaseFontSize();
    if (n > 6)
      {
      config.setDocumentBaseFontSize (n - 1); 
      applyInitialStyles();
      String s = jEditorPane.getText ();
      jEditorPane.setText (s);
      }
    else
      Logger.log (getClass().getName(), Logger.INFO, 
        "Too small to zoom out");
    Logger.out();
    }

/*=========================================================================
  
   refresh 

   Reload the current URL

=========================================================================*/
  public void refresh()
    {
    Logger.in();
    applyInitialStyles ();
    if (baseUri != null)
      {
      // This is potentially nasty. We have to pop the back-link at TOS
      // because loadURI() will replace it. But loadURI() won't replace
      // it unless the load succeeds -- we don't want a dead link lurking
      // on the stack. But loadURI() won't do this itself -- it will 
      // schedule it to be done when the load completes (if it completes).
      // So we pop the TOS here, with no guarantee that it will actually
      // get put back. In practice, we're refreshing a link that previously
      // loaded OK; so it should be fine. Still, it's a bit ugly.
      backlinks.pop();
      loadURI (baseUri);
      }
    Logger.out();
    }

/*=========================================================================
  
   handleLinkClick 

   Easy -- just load the URL

=========================================================================*/
  public void handleLinkClick (URL uri)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, "uri=" + uri);
    if (uri != null)
      loadURI (uri);
    else
      reportGenError (Strings.UNKNOWN, Strings.COULD_NOT_PARSE_URI);
    Logger.out();
    }

/*=========================================================================
  
   handleLinkHover

   Write the link as a status message when the mouse moves over a 
   link in the HTML editor

=========================================================================*/
  private void handleLinkHover (URL linkUri)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, "uri" + linkUri);
    if (linkUri != null)
      setStatus (linkUri.toString());
    Logger.out();
    }

/*=========================================================================
  
   handleLinkUnhover

   // TODO: do we actually need this? Messages in the status area
   // time out automatically

=========================================================================*/
  private void handleLinkUnhover (URL linkUri)
    {
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "uri=" + linkUri);
    if (linkUri != null)
      clearStatus();
    }

/*=========================================================================
  
  applyStylesFromStream

=========================================================================*/
  private void applyStylesFromStream (InputStream is)
    {
    Logger.in();
    HTMLEditorKit kit = (HTMLEditorKit)jEditorPane.getEditorKit();
    StyleSheet styleSheet = kit.getStyleSheet();

    String s = new BufferedReader (new InputStreamReader (is))
        .lines().collect (Collectors.joining("\n"));

    // Generate the various font sizes from the base size, and 
    //   substitute them into the CSS
    int base_font_size = config.getDocumentBaseFontSize();
    s = s.replaceAll ("%%base_font_size%%", "" + base_font_size);
    s = s.replaceAll ("%%h1_font_size%%", "" + (base_font_size * 2));
    s = s.replaceAll ("%%h2_font_size%%", "" + (base_font_size * 5 / 3));
    s = s.replaceAll ("%%h3_font_size%%", "" + (base_font_size * 5 / 4));

    styleSheet.addRule (s); // addRule() can add many rules
    Logger.out();
    }


/*=========================================================================
  
  applyInitialStyles 

  Read the CSS styles from the Config class, and apply them to
  the HTML editor

=========================================================================*/
  private void applyInitialStyles ()
    {
    Logger.in();
    try
      {
      InputStream is = null; 
      String theme = config.getTheme();
      if (Logger.isDebug())
	Logger.log (getClass().getName(), Logger.DEBUG, "Theme is " + theme);
      if ("dark".equals (theme))
	is = getClass().getClassLoader().getResourceAsStream ("css/dark.css");
      else if ("custom".equals (theme))
	{ 
	String cssFile = config.getCustomCSSFile();
	if (Logger.isDebug())
	  Logger.log (getClass().getName(), Logger.DEBUG, "Using custom theme" + cssFile);
	if (cssFile != null)
	  is = new FileInputStream (new File (cssFile));
	else
          {
	  Logger.log (getClass().getName(), Logger.WARNING, 
             "Config file set custom theme, but no CSS file in configuration"); 
	  throw new IOException ("No CSS file specified for custom theme");
          }
	} 
      else
	is = getClass().getClassLoader().getResourceAsStream ("css/light.css");

      if (is != null)
	{
	applyStylesFromStream (is);
	try {is.close(); } catch (Exception e){};
	}
      else
	{
	throw new IOException ("Can't open stream for CSS stylesheet"); 
	}
      if (is != null) is.close();
      }
    catch (Exception e)
      {
      JOptionPane.showMessageDialog (this, e.toString(), 
         DIALOG_CAPTION, JOptionPane.ERROR_MESSAGE); 
      }

    Logger.out();
    }

/*=========================================================================
  
  fiddleWithKeyMap 

  Make the HTML editor stop grabbing the backspace and ctrl+H keys, which
  it seems to want, and accept the up/down keys, which it doesn't.

  Honestly, I have next to no idea what I'm doing here -- I got this
  working by trial and error.

=========================================================================*/
  void fiddleWithKeyMap (HTMLEditorKit kit)
    {
    Logger.in();

    InputMap inputMap = jEditorPane.getInputMap();
    inputMap.put (KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
      "none");
    inputMap.put (KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK),
      "none");

    KeyStroke keyUp = KeyStroke.getKeyStroke (KeyEvent.VK_UP, 0); 
    inputMap.put (keyUp, kit.upAction);
    KeyStroke keyDown = KeyStroke.getKeyStroke (KeyEvent.VK_DOWN, 0); 
    inputMap.put (keyDown, kit.downAction);

    String keyStrokeAndKey = "UP";
    KeyStroke keyStroke = KeyStroke.getKeyStroke (keyStrokeAndKey);
    jEditorPane.getInputMap().put(keyStroke, keyStrokeAndKey);
    keyStrokeAndKey = "DOWN";
    keyStroke = KeyStroke.getKeyStroke (keyStrokeAndKey);
    jEditorPane.getInputMap().put(keyStroke, keyStrokeAndKey);

    Logger.out();
    }

/*=========================================================================
  
  downloadURI 

  Download a URL to the specified file. This takes place in a background
  thread and, at present, there's no control over it when it's started
  (except by quitting the program).

=========================================================================*/
  void downloadURI (String href, File file)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.INFO, 
        "Downloading " + href + " to " + file);

    SwingWorker dlWorker = new SwingWorker()  
      { 
      byte[] b = null;
      Exception e = null;
      @Override
      protected String doInBackground() 
        { 
        Logger.log (getClass().getName(), Logger.DEBUG, "Download worker doInBackground()");
        try
          {
          e = null;
          b = FileUtil.urlToByteArray (new URL (href)); 
          }
        catch (Exception e1)
          {
          e = e1;
          }
        return "foo"; // Meaningless return
        } 

      @Override
      protected void process (java.util.List chunks) { } 

      @Override
      protected void done()  
        { 
        Logger.log (getClass().getName(), Logger.DEBUG, "Download worker done()");
        if (b != null)
          {
          try
            {
            FileUtil.byteArrayToFile (file, b);
	    setStatus (Strings.SAVED_FILE + " '"  + file + "'");
            } 
          catch (Exception e)
            {
            reportException (href, e);
            }
          }
        else if (e != null)
          {
          reportException (href, e);
          }
        }
      }; 

    dlWorker.execute();  
    Logger.out();
    }

/*=========================================================================
  
  promptDownloadURI 

  Prompt the user for a filename, then start the download process
  for the specified link. 

=========================================================================*/
  void promptDownloadURI (String href)
    {
    Logger.in();
    JFileChooser fc = new JFileChooser();
    int p = href.lastIndexOf ('/');
    String saveFilename;
    if (p >= 0)
      saveFilename = href.substring (p + 1);
    else
      saveFilename = "download";

    fc.setSelectedFile (new File (saveFilename));
    // TODO extract filename
    if (fc.showSaveDialog (this) == JFileChooser.APPROVE_OPTION)
      {
      downloadURI (href, fc.getSelectedFile());
      }
    Logger.out();
    }

/*=========================================================================
  
  handleRightClick 

  Handle right-clicks on links by popping up the link menu.

=========================================================================*/
  void handleRightClick (String href, int x, int y)
    {
    Logger.in();

    JPopupMenu linkMenu = new JPopupMenu ("Link action"); 

    JMenuItem openMenuItem = new JMenuItem (Strings.OPEN);
    openMenuItem.addActionListener ((event) -> loadURI (href));

    JMenuItem openNewMenuItem = new JMenuItem (Strings.OPEN_IN_NEW_WINDOW);
    openNewMenuItem.addActionListener ((event) -> newWindow (href, null));
    
    JMenuItem copyLinkMenuItem = new JMenuItem (Strings.COPY_LINK);
    copyLinkMenuItem.addActionListener 
      ((event) -> Clipboard.copyTextToClipboard (href));

    JMenuItem downloadMenuItem = new JMenuItem (Strings.DOWNLOAD);
    downloadMenuItem.addActionListener((event) -> promptDownloadURI (href));

    linkMenu.add (openMenuItem);
    linkMenu.add (openNewMenuItem);
    linkMenu.add (copyLinkMenuItem);
    linkMenu.add (downloadMenuItem);
    linkMenu.show (jEditorPane, x, y); 

    Logger.out();
    }

/*=========================================================================
  
  setHtml

  Set the HTML shown by this viewer to the supplied text. Scroll to
  the top.

=========================================================================*/
  public void setHtml (String s)
    {
    Logger.in();
    jEditorPane.setText (s);
    jEditorPane.setCaretPosition (0);
    Logger.out();
    }

/*=========================================================================
  
  find

=========================================================================*/
  public void find()
    {
    Logger.in();
    String text = JOptionPane.showInputDialog (this, 
      Strings.ENTER_SEARCH_TEXT, DIALOG_CAPTION, 1);
    if (text != null)
      {
      searchPos = 0;
      searchText = text.toLowerCase();
      findNext();
      }
    Logger.out();
    }

/*=========================================================================
  
  findNext 

=========================================================================*/
  public void findNext ()
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, 
       "text=" + searchText);
    if (searchText != null)
      {
      Document doc = jEditorPane.getDocument();
      int findLength = searchText.length();
      if (searchPos + findLength > doc.getLength()) 
        {
        searchPos = 0; // Wrap around to beginning
        setStatus (Strings.SEARCH_WRAPPED_AROUND);
        }
      try
        {
        boolean found = false;
        while (searchPos + findLength <= doc.getLength()) 
          {
          String match = doc.getText (searchPos, findLength).toLowerCase();
          if (match.equals (searchText)) 
             {
             found = true;
             break;
             }
           searchPos++;
           }
         if (found) 
           {
           Rectangle viewRect = jEditorPane.modelToView (searchPos);
           jEditorPane.scrollRectToVisible (viewRect);
           jEditorPane.setCaretPosition (searchPos + findLength);
           jEditorPane.moveCaretPosition (searchPos);
           searchPos += findLength;
           }
        else
          setStatus (Strings.NOT_FOUND);
         }
       catch (BadLocationException e)
         {
         e.printStackTrace();
         }
      }
    Logger.out();
    }


/*=========================================================================
  
  newWindow 

  Open a new window with the specified URL in String form

=========================================================================*/
  public static void newWindow (String uri, String caption)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (MainWindow.class.getName(), Logger.DEBUG, "uri=" + uri);
    MainWindow viewer = new MainWindow();
    viewer.setVisible (true);
    viewer.loadURI (uri);
    if (caption != null) viewer.setTitle (caption);
    Logger.out();
    }

/*=========================================================================
  
  newWindow 

  Open a new window with the specified URL in URL form

=========================================================================*/
  public static void newWindow (URL uri, String caption)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (MainWindow.class.getName(), 
        Logger.DEBUG, "uri=" + uri.toString());
    MainWindow viewer = new MainWindow();
    viewer.setVisible (true);
    viewer.loadURI (uri);
    if (caption != null) viewer.setTitle (caption);
    Logger.out();
    }

/*=========================================================================
  
  newWindow 

  Open a new window home page window 

=========================================================================*/
  public static void newWindow ()
    {
    Logger.in();
    MainWindow viewer = new MainWindow();
    viewer.setVisible (true);
    if (Config.getConfig().getNewWindowMode() == 0)
      viewer.goHome();
    Logger.out();
    }

/*=========================================================================
  
  reloadSettings 

=========================================================================*/
  public void reloadSettings()
    {
    Logger.in();
    Config.getConfig().load();
    Logger.out();
    }

/*=========================================================================
  
  save 

=========================================================================*/
  public void save()
    {
    Logger.in();
    if (lastContent != null)
      {
      String ext = FileUtil.getDefaultExtension (lastContent.getMime());
      JFileChooser fc = new JFileChooser();
      javax.swing.filechooser.FileFilter filter = 
        new FileNameExtensionFilter (lastContent.getMime(), ext);
      fc.addChoosableFileFilter (filter);
      if (fc.showSaveDialog (this) == JFileChooser.APPROVE_OPTION)
        {
	Logger.log (getClass().getName(), Logger.DEBUG, "Save file " + fc.getSelectedFile());
	try
	  {
	  FileUtil.byteArrayToFile 
	     (fc.getSelectedFile(), lastContent.getContent());
	  setStatus ("Wrote file " + fc.getSelectedFile());
	  }
	catch (IOException e)
	  {
	  reportException (fc.getSelectedFile().toString(), e);
	  }
	}
      }
    else
      {
      JOptionPane.showMessageDialog (this, Strings.SAVE_ONLY_TEXT_MESSAGE,
        Strings.NO_TEXT_TO_SAVE, JOptionPane.ERROR_MESSAGE);
      }
    Logger.out();
    }

/*=========================================================================
  
  renderToHtml

  Invoke a converter to render the response to HTML 

=========================================================================*/
  private String renderToHtml (Converter converter, 
        byte[] content, String encoding)
    {
    Logger.in();
    if (content.length > 0)
      {
      try
        {
	String stringForm;
	Logger.log (getClass().getName(), Logger.DEBUG, "renderToHtml(), Encoding is " + encoding);
	if (encoding != null && encoding.length() > 0)
	  stringForm = new String (content, encoding);
	else
	  stringForm = new String (content);
        return converter.toHtml (stringForm); 
        }
      catch (UnsupportedEncodingException e)
        {
	Logger.log (getClass().getName(), Logger.WARNING, 
          "renderToHtml(), Encoding is " + encoding);
        Logger.out();
        return Strings.UNSUP_ENCODING_RESP + ": " + encoding;
        }
      }
    else
      {
      Logger.out();
      return Strings.EMPTY_RESP;
      }
    }

/*=========================================================================
  
  renderPlain

  Convert the plain text to HTML and display it

=========================================================================*/
  private void renderPlain (byte[] content, String encoding)
    {
    Logger.in();
    setHtml (renderToHtml (new TextConverter(), content, encoding));
    Logger.out();
    }

/*=========================================================================
  
  renderAtom

  Convert the Atom feed to HTML and display it

=========================================================================*/
  private void renderAtom (byte[] content, String encoding)
    {
    Logger.in();
    setHtml (renderToHtml (new AtomConverter(baseUri), content, encoding));
    Logger.out();
    }


/*=========================================================================
  
  renderNex

  Convert the Nex-flavoured plain text to HTML and display it

=========================================================================*/
  private void renderNex (byte[] content, String encoding)
    {
    Logger.in();
    setHtml (renderToHtml (new NexConverter (baseUri), content, encoding));
    Logger.out();
    }

/*=========================================================================
  
  renderGemtext  

  Convert the text returned from the server in a text/gemini response
  to HTML, and display it. Assume the specified encoding, unless encoding
  is null, in which case assume platform encoding.

=========================================================================*/
  private void renderGemtext (byte[] content, String encoding)
    {
    Logger.in();
    setHtml (renderToHtml (new GemConverter (baseUri), content, encoding));
    Logger.out();
    }

/*=========================================================================
  
  renderGophermap

=========================================================================*/
  private void renderGophermap (byte[] content, String encoding)
    {
    Logger.in();
    setHtml (renderToHtml (new GophermapConverter (baseUri), content, encoding));
    Logger.out();
    }

/*=========================================================================
  
  renderMarkdown

  Convert the markdown text to HTML and display it

=========================================================================*/
  private void renderMarkdown (byte[] content, String encoding)
    {
    Logger.in();
    setHtml (renderToHtml (new MarkdownConverter (baseUri), content, encoding));
    Logger.out();
    }

/*=========================================================================
  
  setAsHome

=========================================================================*/
void setAsHome ()
  {
  Logger.in();
  if (baseUri != null)
    {
    config.setHomePage (baseUri.toString());
    config.save();
    }
  Logger.out();
  }

/*=========================================================================
  
  setCaptionFromHostname

  Sets the window caption and the value of displayName. Uses the response
  if there is one, otherwise just bases the result on the URI.

=========================================================================*/
void setCaptionFromResponse (URL uri, ResponseContent gc)
  {
  Logger.in();
  String caption = null;

  if (gc != null)
    {
    // TODO: extract a caption from the response data
    byte[] content = gc.getContent();
    if (content != null && content.length > 0)
      {
      String mime = gc.getMime();
      String encoding = MimeUtil.getEncodingFromMime (mime);
      byte[] start = Arrays.copyOfRange(content, 0, 512);
      String s = "";
      try
        {
        s = new String (start, encoding);
        }
      catch (UnsupportedEncodingException e) 
        {
        s = new String (start);
        }
      
      displayName = GemUtil.getFirstHeading (s); 
      if (displayName != null)
        caption = WINDOW_CAPTION + ": " + displayName;
      }
    }

  if (caption == null)
    {
    displayName = getDisplayNameFromURI (uri);
    if (displayName != null)
      caption = WINDOW_CAPTION + ": " + displayName;
    }  

  if (caption == null)
    {
    caption = WINDOW_CAPTION; 
    }  

  setTitle (caption);
  Logger.out();
  }

/*=========================================================================
  
  setStatus    

=========================================================================*/
  private void setStatus (String s)
    {
    Logger.in();
    statusBar.setStatus (s);
    Logger.out();
    }

/*=========================================================================
  
  showBookmarks 

=========================================================================*/
  private void showBookmarks()
    {
    Logger.in();
    try
      {
      bookmarkHandler.showBookmarks();
      }
    catch (Exception e)
      {
      JOptionPane.showMessageDialog (this, e.getMessage(), 
         DIALOG_CAPTION, JOptionPane.ERROR_MESSAGE); 
      }
    Logger.out();
    }

  }

