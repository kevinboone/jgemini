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
import javax.swing.border.EmptyBorder;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.protocol.*;
import me.kevinboone.jgemini.converters.*;
import net.fellbaum.jemoji.*;

/** MainWindow is where most of the work of the Swing UI happens. This
    class maintains the user interface, and handles all the remote
    loading and content rendering. Note that throughout this class I've
    used the terms URI and URL as if they were interchangeable. Sorry. 
*/
public class MainWindow extends JFrame implements ConfigChangeListener
  {
  private final static String DIALOG_CAPTION = Constants.APP_NAME;
  private final static String WINDOW_CAPTION = Constants.APP_NAME;
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");
  private final static ResourceBundle generalBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.General");
  private final static ResourceBundle captionsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Captions");
  private final static ResourceBundle menusBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Menus");
  private final static ResourceBundle dialogsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");

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
    = new DefaultBookmarkHandler (this);
  private FeedHandler feedHandler 
    = DefaultFeedHandler.getInstance(); // Application-wide reference
  private ClientCertHandler clientCertHandler
    = new DefaultClientCertHandler (this);
  private DownloadMonitor downloadMonitor
    = DefaultDownloadMonitor.getInstance(); // Application-wide reference


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
        boolean exit = false;

        if (Main.closingWouldExit())
          {
          exit = true;
          if (!Main.okToExit())
            {
            return; 
            }
          }

        config.removeConfigChangeListener (mainWindow); 

        // I'm reluctant to save the config unless something's
        //   genuinely changed, because saving removes any
        //   comments or layout in the config file
        int oldW = config.getWindowWidth();
        int oldH = config.getWindowHeight();
        Dimension dim = getSize();
        int newW = dim.width;
        int newH = dim.height;
        if (newH != oldH || newW != oldW)
          {
          config.setWindowWidth (newW);
          config.setWindowHeight (newH);
          config.save();
          }
        
        cancelLoad();
        dispose();
        if (exit) Main.exit();
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
    jEditorPane.setText ("<p align=\"center\">" +
       generalBundle.getString ("empty_window_text") + "</p>");

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
  
  applyInitialStyles 

=========================================================================*/
  /** Read the CSS styles from the Config class, and apply them to
      the HTML editor.
  */
  private void applyInitialStyles ()
    {
    Logger.in();
    try
      {
      InputStream is = null; 
      String theme = config.getTheme();
      if (Logger.isDebug())
	Logger.log (getClass().getName(), Logger.DEBUG, "Theme is " + theme);


      if ("custom".equals (theme))
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
        {
        String themeFile = "css/" + theme + ".css";
	is = getClass().getClassLoader().getResourceAsStream (themeFile);
        }

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
      reportGenError (e.toString());
      }

    Logger.out();
    }

/*=========================================================================
  
   back

=========================================================================*/
  /** Go back to the previous page, if there was one.
  */
  public void back()
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
  
  bookmarkPage

=========================================================================*/
  /** Set a bookmark for the current page. We'll need to figure out a 
      name for the bookmark, and optionally strip emoji characters from
      the name. Then we'll just use the current BookmarkHandler to set
      the bookmark. */
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
        if (config.getEmojiStripBookmarks())
          bookmarkName = EmojiManager.removeAllEmojis (bookmarkName);
        if (bookmarkHandler.addBookmark (bookmarkName, baseUri))
          setStatus (messagesBundle.getString ("bookmark_added")); 
        else
          setStatus (messagesBundle.getString ("already_bookmarked")); 
        }
      catch (IOException e)
        {
        reportGenError (e.getMessage());
        }
      }
    }


/*=========================================================================
  
  cancelLoad

=========================================================================*/
  /** Cancel any asynchronous transfer that is in progress. Note that 
      this does not apply to media-streaming transfers, nor to transfers
      of embedded images in documents -- these are handled using 
      completely different mechanisms. */
  private void cancelLoad ()
    {
    Logger.in();
    if (loadWorker != null)
      {
      //System.out.println ("Debug message: loadworker not null. If this");
      //System.out.println (" wasn't the result of cancelling a request,");
      //System.out.println (" please log a bug!");
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
  
  chooseAndDownloadURI 

=========================================================================*/
  /** Prompt the user for a filename, then download the URI to that file
  */
  private void chooseAndDownloadURI (URL uri)
    {
    JFileChooser fc = new JFileChooser();
    String path = uri.getPath();
    File p = new File (path);
    fc.setSelectedFile (new File(p.getName()));
    fc.setCurrentDirectory (new File(config.getDownloadsDir()));
    if (fc.showSaveDialog (this) == JFileChooser.APPROVE_OPTION)
      {
      File file = fc.getSelectedFile();
      if (file.exists())
        {
        String message = messagesBundle.getString ("query_overwrite_file");
        if (JOptionPane.showConfirmDialog (null, message, Constants.APP_NAME,
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
            != JOptionPane.YES_OPTION)
          return;
        }
      downloadURI (uri.toString(), file, null);
      }
    }

/*=========================================================================
  
  chooseAndDownloadURI 

=========================================================================*/
  /** Prompt the user for a filename, then download the URI to that file
  */
  private void chooseAndDownloadURI (String uri)
    {
    try
      {
      chooseAndDownloadURI (new URL(uri)); 
      } 
    catch (MalformedURLException e) {}
    }

/*=========================================================================
  
  configChanged 

=========================================================================*/
  /** MainWindow acts as a ConfigChangeListener, so must implement
      configChanged(). This method is called by the Config class when
      anybody asks it to fire its change listeners. This mechanism allows
      us to propagate configuration changes (e.g., themese) made in
      one window across all open windows. */
  public void configChanged (int ccMode) 
    {
    Logger.in();
    if (ccMode > ConfigChangeListener.CCMODE_NOUPDATE)
      {
      applyInitialStyles();
      if (ccMode > ConfigChangeListener.CCMODE_REFRESH)
        {
        menuCommandReload();
        }
      else
        {
        String s = jEditorPane.getText ();
        jEditorPane.setText (s);
        }
      }
    Logger.out();
    }

/*=========================================================================
  
  createMenuItem

=========================================================================*/
  /** A helper class for creating a menu item with translatable text,
      an optional mnemonic, and an optional accelerator. */
  private JMenuItem createMenuItem (String menu_name)
    {
    String mnemonicKey = menu_name + "_mnemonic";
    String accelKey = menu_name + "_accel";
    JMenuItem menuItem = new JMenuItem 
      (menusBundle.getString (menu_name));
    if (menusBundle.containsKey (mnemonicKey))
      {
      Object o = menusBundle.getObject (mnemonicKey);
      menuItem.setMnemonic ((int)o);
      }
    if (menusBundle.containsKey (accelKey))
      {
      Object o = menusBundle.getObject (accelKey);
      menuItem.setAccelerator ((javax.swing.KeyStroke)o);
      }
    return menuItem;
    }

/*=========================================================================
  
  createTopLevelMenu

=========================================================================*/
  /** A helper class for creating a menu with translatable text,
      and an optional mnemonic. */
  private JMenu createTopLevelMenu (String menu_name)
    {
    String mnemonicKey = menu_name + "_mnemonic";
    JMenu menu = new JMenu (menusBundle.getString (menu_name));
    Object o = menusBundle.getObject (mnemonicKey);
    if (o != null) menu.setMnemonic ((int)o);
    return menu;
    }

/*=========================================================================
  
  createMenuBar

=========================================================================*/
  /** Create the main menu.
  */
  private void createMenuBar ()
    {
    menuBar = new JMenuBar();

    // TODO: we need to change the accelerator keys if the menu
    //   text changes.

    // File|Settings submenu
    JMenu settingsSubMenu = createTopLevelMenu ("settings");
    JMenuItem editMenuItem = createMenuItem ("settings_edit"); 
    editMenuItem.addActionListener((event) -> editSettings());
    JMenuItem reloadMenuItem = createMenuItem ("settings_reload"); 
    reloadMenuItem.addActionListener((event) -> reloadSettings());
    settingsSubMenu.add (editMenuItem);
    settingsSubMenu.add (reloadMenuItem);

    // File menu
    JMenu fileMenu = createTopLevelMenu ("file");

    JMenuItem newMenuItem = createMenuItem ("file_new");
    newMenuItem.addActionListener((event) -> menuCommandNewWindow());
    fileMenu.add (newMenuItem);
    JMenuItem openMenuItem = createMenuItem ("file_open_link");
    openMenuItem.addActionListener((event) -> menuCommandOpenLink());
    fileMenu.add (openMenuItem);
    JMenuItem saveMenuItem = createMenuItem ("file_save");
    saveMenuItem.addActionListener((event) -> menuCommandSave());
    fileMenu.add (saveMenuItem);
    fileMenu.add (new JSeparator());
    JMenuItem closeMenuItem = createMenuItem ("file_close"); 
    closeMenuItem.addActionListener((event) -> 
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
    fileMenu.add (closeMenuItem);
    JMenuItem exitMenuItem = createMenuItem ("file_exit"); 
    exitMenuItem.addActionListener((event) -> menuCommandExit());
    fileMenu.add (exitMenuItem);

    // Edit menu
    JMenu editMenu = createTopLevelMenu ("edit");

    JMenuItem selectAllMenuItem = createMenuItem ("edit_select_all"); 
    selectAllMenuItem.addActionListener((event) -> jEditorPane.selectAll());
    editMenu.add (selectAllMenuItem);
    JMenuItem copyMenuItem = createMenuItem ("edit_copy"); 
    copyMenuItem.addActionListener((event) -> jEditorPane.copy());
    editMenu.add (copyMenuItem);
    JMenuItem findMenuItem = createMenuItem ("edit_find_in_page");
    findMenuItem.addActionListener((event) -> find());
    editMenu.add (findMenuItem);
    JMenuItem findNextMenuItem = createMenuItem ("edit_find_next"); 
    findNextMenuItem.addActionListener((event) -> findNext());
    editMenu.add (findNextMenuItem);
    editMenu.add (new JSeparator());
    JMenuItem editSettingsMenuItem = createMenuItem ("edit_settings"); 
    editSettingsMenuItem.addActionListener((event) -> menuCommandSettings());
    editMenu.add (editSettingsMenuItem);

    // View menu
    JMenu viewMenu = createTopLevelMenu ("view");

    JMenuItem zoomInMenuItem = createMenuItem ("view_zoom_in"); 
    zoomInMenuItem.addActionListener((event) -> menuCommandZoomIn());
    viewMenu.add (zoomInMenuItem);
    JMenuItem zoomOutMenuItem = createMenuItem ("view_zoom_out"); 
    zoomOutMenuItem.addActionListener((event) -> menuCommandZoomOut());
    viewMenu.add (zoomOutMenuItem);
    viewMenu.add (new JSeparator());
    JMenuItem refreshMenuItem = createMenuItem ("view_refresh"); 
    refreshMenuItem.addActionListener((event) -> menuCommandReload());
    viewMenu.add (refreshMenuItem);

    // Bookmark menu
    JMenu bookmarksMenu = createTopLevelMenu ("bookmarks");

    JMenuItem showBookmarksMenuItem = createMenuItem ("bookmarks_show_all"); 
    showBookmarksMenuItem.addActionListener((event) -> showBookmarks());
    JMenuItem editBookmarksMenuItem = createMenuItem ("bookmarks_edit"); 
    editBookmarksMenuItem.addActionListener((event) -> editBookmarks());
    JMenuItem bookmarkPageMenuItem = createMenuItem ("bookmarks_this_page"); 
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
          if (config.getEmojiStripBookmarks())
            text = EmojiManager.removeAllEmojis (text);
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

    // Feeds menu
    JMenu feedsMenu = createTopLevelMenu ("feeds");
    JMenuItem viewAggregatedFeedsMenuItem = createMenuItem ("feeds_view_aggregated"); 
    viewAggregatedFeedsMenuItem.addActionListener((event) -> menuCommandViewAggregatedFeeds());
    feedsMenu.add (viewAggregatedFeedsMenuItem);
    JMenuItem editFeedsMenuItem = createMenuItem ("feeds_edit"); 
    editFeedsMenuItem.addActionListener((event) -> menuCommandEditFeeds());
    feedsMenu.add (editFeedsMenuItem);
    feedsMenu.add (new JSeparator());
    JMenuItem subscribePageMenuItem = createMenuItem ("feeds_subscribe"); 
    subscribePageMenuItem.addActionListener((event) -> menuCommandSubscribePage());
    feedsMenu.add (subscribePageMenuItem);
    feedsMenu.add (new JSeparator());
    JMenuItem aggregateFeedsMenuItem = createMenuItem ("feeds_aggregate"); 
    aggregateFeedsMenuItem.addActionListener((event) -> menuCommandAggregateFeeds());
    feedsMenu.add (aggregateFeedsMenuItem);

    // Go menu
    JMenu goMenu = createTopLevelMenu ("go");

    JMenuItem backMenuItem = createMenuItem ("go_back"); 
    backMenuItem.addActionListener((event) -> menuCommandBack());
    goMenu.add (backMenuItem);
    JMenuItem homeMenuItem = createMenuItem ("go_home"); 
    homeMenuItem.addActionListener((event) -> menuCommandHome());
    goMenu.add (homeMenuItem);
    JMenuItem rootMenuItem = createMenuItem ("go_root"); 
    rootMenuItem.addActionListener((event) -> menuCommandRoot());
    goMenu.add (rootMenuItem);
    goMenu.add (new JSeparator());
    JMenuItem stopMenuItem = createMenuItem ("go_stop"); 
    stopMenuItem.addActionListener((event) -> menuCommandStop());
    goMenu.add (stopMenuItem);

    // Tools menu
    JMenu toolsMenu = createTopLevelMenu ("tools");

    JMenuItem identityMenuItem = createMenuItem ("tools_identity"); 
    identityMenuItem.addActionListener((event) -> menuCommandIdent());
    toolsMenu.add (identityMenuItem);
    JMenuItem serverCertMenuItem = createMenuItem ("tools_server_cert"); 
    serverCertMenuItem.addActionListener((event) -> menuCommandServerCert());
    toolsMenu.add (serverCertMenuItem);
    toolsMenu.add (new JSeparator());
    toolsMenu.add (settingsSubMenu);
    toolsMenu.add (new JSeparator());
    JMenuItem downloadsMenuItem = createMenuItem ("tools_downloads"); 
    downloadsMenuItem.addActionListener((event) -> menuCommandDownloads());
    toolsMenu.add (downloadsMenuItem);
    JMenuItem feedManagerMenuItem = createMenuItem ("tools_feed_manager"); 
    feedManagerMenuItem.addActionListener((event) -> menuCommandFeedManager());
    toolsMenu.add (feedManagerMenuItem);

    // Help menu
    JMenu helpMenu = createTopLevelMenu ("help");

    JMenuItem helpMenuItem = createMenuItem ("help_docs"); 
    helpMenuItem.addActionListener ((event) -> menuCommandDocs());
    helpMenu.add (helpMenuItem);
    JMenuItem releaseNotesMenuItem = createMenuItem ("help_release_notes"); 
    releaseNotesMenuItem.addActionListener ((event) -> menuCommandReleaseNotes());
    helpMenu.add (releaseNotesMenuItem);
    helpMenu.add (new JSeparator());
    JMenuItem aboutMenuItem = createMenuItem ("help_about"); 
    aboutMenuItem.addActionListener((event) -> menuCommandAbout());
    helpMenu.add (aboutMenuItem);

    menuBar.add (fileMenu);
    menuBar.add (editMenu);
    menuBar.add (viewMenu);
    menuBar.add (bookmarksMenu);
    menuBar.add (feedsMenu);
    menuBar.add (goMenu);
    menuBar.add (toolsMenu);
    menuBar.add (helpMenu);
    }

/*=========================================================================
  
  clearHistory

=========================================================================*/
  protected void clearHistory ()
    {
    topBar.clearHistory();
    }

/*=========================================================================
  
  clearStatus    

=========================================================================*/
  private void clearStatus ()
    {
    statusBar.clearStatus ();
    }

/*=========================================================================
  
  downloadURI 

=========================================================================*/
  /**
  Download a URL to the specified file. This takes place in a background
  thread and, at present, there's no control over it when it's started
  (except by quitting the program). However, if "ch" is non-null, the
  background thread will run the completion handler once the download
  is finished. This will take place on the user interface thread, so
  it should be safe to update the UI.
  */
  void downloadURI (String href, File file, CompletionHandler ch)
    {
    Logger.in();
    SwingFileDownload sfd = new SwingFileDownload (this, href, 
      new FileDownloadTarget (file), ch);
    setStatus (messagesBundle.getString ("downloading"));
    sfd.start();
    Logger.out();
    }

/*=========================================================================
  
  downloadURIToDownloads 

=========================================================================*/
/** Get a sensible filename in the Downloads directory, and then
    call downloadURI.
*/
void downloadURIToDownloads (URL url)
  {
  String path = url.getPath();
  File p = new File (path);
  String name = p.getName();
  String extension = "";
  int i = name.lastIndexOf('.');
  if (i > 0) 
    {
    extension = name.substring (i);
    name = name.substring (0, i);
    }
  
  i = 0; 
  String tryFilename;
  do
    {
    tryFilename = config.getDownloadsDir() + File.separator + 
       name + ((i == 0) ? "" : "(" + i + ")") + extension;
    i++;
    } while (new File(tryFilename).exists());

  downloadURI (url.toString(), new File(tryFilename), null);
  }

/*=========================================================================
  
  downloadAndInvokeDesktop 

=========================================================================*/
/** Download the URL to a temporary file, and then invoke the
    desktop on it. Because the download is asynchronous, we'll use
    a completion handler to do the actual desktop operation once
    it's finished. 
*/
private void downloadAndInvokeDesktop (URL url)
  {
  String path = url.getPath();
  String extension = null;
  int i = path.lastIndexOf('.');
  if (i > 0) 
    {
    extension = path.substring (i);
    }
  if (extension == null) extension = ".tmp";

  try
    {
    File tempFile = File.createTempFile ("gemini-", extension);
    tempFile.deleteOnExit();
    downloadURI (url.toString(), tempFile, new CompletionHandler()
      {
      public void complete()
        {
        try
          {
          Desktop.getDesktop().browse (java.net.URI.create 
            ("file://" + tempFile));
          }
        catch (Exception e)
          {
          reportException (url.toString(), e);
          }
        }
      });
    }
  catch (IOException e)
    {
    reportException (null, e);
    }

  }

/*=========================================================================
  
  editBookmarks 

=========================================================================*/
  /** Raise the bookmark editor UI. 
  */
  private void editBookmarks()
    {
    try
      {
      bookmarkHandler.editBookmarks();
      }
    catch (Exception e)
      {
      reportGenError (e.getMessage());
      }
    }

/*=========================================================================
  
  enableTopBar

=========================================================================*/
  /** 
  Call this once initial set-up is done. Otherwise the act of populating
  the history combobox makes it fire an event, which makes it load
  the first thing in the history. Sigh. Bloody Swing.
  */
  public void enableTopBar ()
    {
    Logger.in();
    topBar.enable();
    Logger.out();
    }

/*=========================================================================
  
   editSettings 

=========================================================================*/
  /** Raise the settings editor UI. 
  */
  protected void editSettings()
    {
    Logger.in();

    try
      {
      Config.getConfig().ensureUserConfigFileExists();
      EditFileDialog d = new EditFileDialog (this, 
        captionsBundle.getString ("edit_config_file"), 
          Config.getConfig().getUserConfigFilename(), 
            Constants.DOC_EDIT_SETTINGS);
      d.setVisible (true);
      if (d.didSave())
        Config.getConfig().load();
      }
    catch (Exception e) 
      {
      reportGenError (e.getMessage());
      }

    Logger.out();
    }

/*=========================================================================
  
  ensureDownloadsDialogVisible 

=========================================================================*/
/** Create the application-wide downloads dialog if necessary, or 
    bring it to the top if it already exists. */
void ensureDownloadsDialogVisible()
  {
  Logger.in();
  DownloadsDialog downloadsDialog = DownloadsDialog.getInstance();
  downloadsDialog.setVisible (true); 
  Logger.out();
  }

/*=========================================================================
  
  ensureFeedManagerDialogVisible 

=========================================================================*/
/** Create the application-wide feeds manager dialog if necessary, or 
    bring it to the top if it already exists. */
void ensureFeedManagerDialogVisible()
  {
  Logger.in();
  FeedManagerDialog feedManagerDialog = FeedManagerDialog.getInstance();
  feedManagerDialog.setVisible (true); 
  Logger.out();
  }

/*=========================================================================
  
  fiddleWithKeyMap 

=========================================================================*/
  /**
  Make the HTML editor stop grabbing the backspace and ctrl+H keys, which
  it seems to want, and accept the up/down keys, which it doesn't.
  Honestly, I have next to no idea what I'm doing here -- I got this
  working by trial and error.
  */
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
  
   getCurrentURI 

=========================================================================*/
  /** This method gets the URL that is currently in the display. It's
      used by other classes in this package. 
  */
  protected URL getCurrentURI()
    {
    return baseUri;
    }

/*=========================================================================
  
   home

=========================================================================*/
  /**
  Go to the home page. "protected" because this method is used
  by TopBar.
  */
  protected void home()
    {
    Logger.in();
    loadURI (config.getHomePage());
    Logger.out();
    }

/*=========================================================================
  
  find

=========================================================================*/
  private void find()
    {
    Logger.in();
    String text = JOptionPane.showInputDialog (this, 
      dialogsBundle.getString ("enter_search_text") + ":", DIALOG_CAPTION, 1);
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
  private void findNext ()
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
        setStatus (messagesBundle.getString ("search_wrapped_around"));
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
          setStatus (messagesBundle.getString ("not_found"));
         }
       catch (BadLocationException e)
         {
         Logger.log (getClass().getName(), Logger.WARNING, e.getMessage()); 
         }
      }
    Logger.out();
    }

/*=========================================================================
  
   getRootUri 

=========================================================================*/
  /**
   Get the site root. What that means depends on the URI. In particular,
   URIs containing a username (host:port/~fred) have their root at the
   user's top-level directory, not the server's top-level directory. 
  */
  private URL getRootUri (URL baseUri) throws MalformedURLException
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
  
  handleStatus10 

=========================================================================*/
  /**
  Deal with "Status 10" responses, that require further input.
  We just prompt the user for a string, and then repeat the
  original request.
  */
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
    // TODO remove this function -- it should never get called and,
    //   if it does, there's not much useful that JGemini can do
    //   with the data. We should just put up an error message.
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
  
  loadResponseContent
  
  Make the request on the server, and pack the results into a 
  ResponseContent object. The results may include an exception -- this
  method itself must not throw any exception

=========================================================================*/
  private ResponseContent loadResponseContent (URL url, URLConnection conn)
    {
    Logger.in();
    ResponseContent gc = new ResponseContent (url);
    try
      {
      Object o = conn.getContent();
      byte[] content;
      if (o instanceof InputStream)
        {
        InputStream is = (InputStream)o;
        content = FileUtil.readInputStreamFully ("Page", is);
        is.close();
        }
      else
        {
        content = (byte[]) o; 
        }
      String mime = conn.getContentType();
      gc.setMime (mime);
      gc.setContent (content);
      // Changed: we can't rely on baseUri at this point
      //String proto = baseUri.getProtocol();
      String proto = url.getProtocol();
      // getRequestProperty() fails in a weird way on file: URIs.
      if (!"file".equals (proto))
        gc.setCertinfo (conn.getRequestProperty ("certinfo"));
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

=========================================================================*/
  /** We call this when a request has been completed successfully, and we
  have a ResponseContent instance that reflects the response from
  the server. We'll use the MIME type and/or filename to decide what
  to do with the response.
  */
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
      String encoding = FileUtil.getEncodingFromMime (mime);
      renderGemtext (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      backlinks.push (fullUrl);
      setLastContent (gc);
      }
    else if (mime.startsWith ("text/gophermap")|| 
        urlStr.endsWith (".gopher")) // Not a real MIME
      {
      baseUri = fullUrl; 
      String encoding = FileUtil.getEncodingFromMime (mime);
      renderGophermap (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      setLastContent (gc);
      backlinks.push (fullUrl);
      }
    else if (mime.startsWith ("text/plain") || urlStr.endsWith (".txt"))
      {
      baseUri = fullUrl; 
      String encoding = FileUtil.getEncodingFromMime (mime);
      renderPlain (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      backlinks.push (fullUrl);
      setLastContent (gc);
      }
    else if (mime.startsWith ("text/nex")) // Not a real MIME
      {
      baseUri = fullUrl; 
      String encoding = FileUtil.getEncodingFromMime (mime);
      renderNex (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      setLastContent (gc);
      backlinks.push (fullUrl);
      }
    else if (mime.startsWith ("text/markdown") || urlStr.endsWith (".md"))
      {
      baseUri = fullUrl; 
      String encoding = FileUtil.getEncodingFromMime (mime);
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
      String encoding = FileUtil.getEncodingFromMime (mime);
      renderAtom (gc.getContent(), encoding);
      topBar.showUrl (fullUrl.toString());
      setLastContent (gc);
      backlinks.push (fullUrl);
      }
    else if (mime.startsWith ("text/xml") 
         || mime.startsWith ("application/xml"))
      {
      String encoding = FileUtil.getEncodingFromMime (mime);

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
        // We'll only get here if the remote file is XML,
        //   but isn't a supported feed.
        handleUnsupportedMime (fullUrl, mime, gc.getContent());
        }
      }
    else if (mime.startsWith ("image/"))
      {
      loadURIEmbedImage (fullUrl);
      }
    else
      {
      // We should never get here, because we've checked the mime type
      //   after establishing the connection, and prompted the
      //   user how to proceed.
      handleUnsupportedMime (fullUrl, mime, gc.getContent());
      }
    jEditorPane.requestFocus();
    Logger.out();
    }
  
/*=========================================================================
  
  loadFromUri
  
=========================================================================*/
  /** We have a gemini:// or spartan:// URL. Load it through its 
      content handler.
      If the qparam arg is non-null, it is appended as a query parameter 
      after URL-encoding (which is fussy in Gemini).  
  */
  private void loadFromUri (URL url, String qparam)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, 
         "loadFromUri(), " + url);

    ActionListener loadTimerListener = new ActionListener() 
      {
      public void actionPerformed (ActionEvent evt) 
        {
	setStatus (messagesBundle.getString ("loading"));
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
        URL tempUrl = new URL (url.toString() + "?" 
          + URLEncoder.encode (qparam));
        String sURL = tempUrl.toString().replace("+","%20");
        url = new URL (sURL);
        }
      }
    catch (Exception e)
      {
      // Fallen at the first hurdle.
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
	Logger.log (getClass().getName(), Logger.DEBUG, 
	  "Load worker thread doInBackground()");
	try
	  {
	  loadTimer = new javax.swing.Timer (1000, loadTimerListener);
	  loadTimer.setRepeats (true);
	  loadTimer.start(); 
	  setStatus (messagesBundle.getString ("loading") + " " + fullUrl);
	  URLConnection conn = fullUrl.openConnection();
	  conn.connect();
          String contentType = conn.getContentType();
          String sUrl = fullUrl.toString();
          if (!FileUtil.canHandleContent (sUrl, contentType))
            {
            int action = promptGetAction (contentType);
            throw new HandleDifferentlyException (action); 
            }
	  gc = loadResponseContent (fullUrl, conn); 
	  }
        catch (Exception e)
          {
          gc = new ResponseContent (fullUrl);
          gc.setException (e);
          }
	return ""; // Meaningless return
	} 

      @Override
      protected void done()  
	{ 
	Logger.log (getClass().getName(), Logger.DEBUG, 
	  "Load worker thread done()");
	if (loadTimer != null) 
	  {
	  loadTimer.stop();
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
	      // Load worked must have finished, if we get this far
	      loadWorker = null;
	      RetryWithInputException e2 = (RetryWithInputException)e;
	      handleStatus10 (e2.getHide(), e2.getPrompt(), e2.getURL());
	      }
	    else if (e instanceof RedirectedException)
	      {
	      loadWorker = null;
	      handleRedirect (((RedirectedException)e).getURL());
	      }
	    else if (e instanceof ErrorResponseException)
	      {
	      reportErrorResponseException (fullUrl.toString(), 
		 (ErrorResponseException)e); 
	      }
	    else if (e instanceof HandleDifferentlyException)
	      {
              HandleDifferentlyException e2 = (HandleDifferentlyException)e;
              int a = e2.getAction();
              switch (a)
                {
                case ContentHandlerAction.CHA_NONE:
                  break;
                case ContentHandlerAction.CHA_PROMPTSAVE:
                  chooseAndDownloadURI (fullUrl);
                  break;
                case ContentHandlerAction.CHA_SAVE:
                  downloadURIToDownloads (fullUrl);
                  break;
                case ContentHandlerAction.CHA_DESKTOP:
                  downloadAndInvokeDesktop (fullUrl); 
                  break;
                case ContentHandlerAction.CHA_STREAM:
                  streamOut (fullUrl);
                  break;
                }
	      }
	    else 
	      {
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
  
   handleLinkClick 

=========================================================================*/
  private void handleLinkClick (URL uri)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, "uri=" + uri);
    if (uri != null)
      loadURI (uri);
    else
      reportGenError (messagesBundle.getString ("unknown_url"), 
        messagesBundle.getString ("could_not_parse_uri"));
    Logger.out();
    }

/*=========================================================================
  
   handleLinkHover

=========================================================================*/
  /**
  Write the link as a status message when the mouse moves over a 
  link in the HTML editor
  */
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
  
   loadForeignURI 

=========================================================================*/
  /** Any attempt to load a URI that does not start with a supported protocol
   ends up here.  At present, we just use the Java desktop support to try to 
   invoke a handler for it.
  */
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
  /** Given the URL of an image, generate an HTML document that loads
      that image. We'll use this when the user asks to open an image
      in a new window. */
  private void loadURIEmbedImage (URL url)
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

=========================================================================*/
  /** Load any kind of URL that we support into this viewer window. If
      it's not a protocol we support, save the file and invoke the desktop.
      However, this should rarely happen, because the user will already
      have been prompted how to handle the file, unless its a tricky
      format like XML -- only some of which we handle. 
  */
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
        // Changed: don't set baseUri here, but when the response completes
	//baseUri = uri;
	}
      else if (uri.getProtocol().equals ("spartan"))
	{
	loadFromUri (uri, null);
	//baseUri = uri;
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
	  //  baseUri = uri;
	    }
          }
        else
          {
	  loadFromUri (uri, null);
	  //baseUri = uri;
          }
	}
      else if (uri.getProtocol().equals ("nex"))
	{
	loadFromUri (uri, null);
	//baseUri = uri;
	}
      else if (uri.getProtocol().equals ("about"))
	{
	loadFromUri (uri, null);
	//baseUri = uri;
	}
      else if (uri.getProtocol().equals ("file"))
	{
	loadFromUri (uri, null);
	//baseUri = uri;
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

=========================================================================*/
  /** Load any kind of URL that we support into this viewer window. 
  */
  public void loadURI (String url)
    {
    // We need to do something if what the user enters doesn't seem
    //   to be a full URL. The following is a pretty crude approach.
    if (!url.contains (":"))
       {
       if ((url.contains (" ") || !url.contains (".")) 
            && Config.getConfig().getUrlbarSearchEnabled())
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
  
  manageIdentity 

=========================================================================*/
  /** This method is "protected" because TopBar calls it from a 
      button handler. */
  protected void manageIdentity()
    {
    Logger.in();
    clientCertHandler.manageIdentity (baseUri);
    Logger.out();
    }

/*=========================================================================
  
  menuCommandAggregateFeeds

=========================================================================*/
  private void menuCommandAggregateFeeds()
    {
    if (feedHandler.isRunning())
      {
      DialogHelper.infoDialog (this, null, 
        dialogsBundle.getString ("feed_manager_already_running"));
      }
    else
      {
      feedHandler.start();
      }
    }

/*=========================================================================
  
  menuCommandAbout 

=========================================================================*/
  private void menuCommandAbout()
    {
    String aboutMessage = messagesBundle.getString ("about"); 
    String versionText = generalBundle.getString ("version_uc"); 
    String s = Constants.APP_NAME + " " + versionText 
      + " " + Constants.VERSION + "\n\n"; 
    s += aboutMessage + "\n";

    JTextArea textArea = new JTextArea (s);
    textArea.setWrapStyleWord (true);
    textArea.setLineWrap (true);
    textArea.setEditable (false);
    textArea.setRows (Constants.DIALOG_ROWS);
    textArea.setColumns (Constants.DIALOG_COLS * 2);
    textArea.setBorder(new EmptyBorder (20, 20, 20, 20));

    JScrollPane scrollPane = new JScrollPane (textArea);

    JOptionPane.showMessageDialog (this, scrollPane, 
         DIALOG_CAPTION, JOptionPane.INFORMATION_MESSAGE); 
    }

/*=========================================================================
  
   menuCommandBack

=========================================================================*/
  private void menuCommandBack()
    {
    Logger.in();
    back();
    Logger.out();
    }

/*=========================================================================
  
   menuCommandDocs 

=========================================================================*/
  private void menuCommandDocs()
    {
    newWindow (Constants.DOC_INDEX, 
      captionsBundle.getString ("documentation"));
    }

/*=========================================================================
  
   menuCommandDownloads

=========================================================================*/
  private void menuCommandDownloads()
    {
    ensureDownloadsDialogVisible();
    }

/*=========================================================================
  
  menuCommandEditFeeds 

=========================================================================*/
  /** Raise the bookmark editor UI. 
  */
  private void menuCommandEditFeeds()
    {
    try
      {
      feedHandler.editFeeds();
      }
    catch (Exception e)
      {
      reportGenError (e.getMessage());
      }
    }

/*=========================================================================
  
   menuCommandFeedManager

=========================================================================*/
  private void menuCommandFeedManager()
    {
    ensureFeedManagerDialogVisible();
    }

/*=========================================================================
  
   menuCommandExit

=========================================================================*/
  private void menuCommandExit()
    {
    if (Main.okToExit())
      {
      Main.exit(); 
      }
    }

/*=========================================================================
  
   menuCommandReleaseNotes

=========================================================================*/
  private void menuCommandReleaseNotes()
    {
    newWindow (Constants.DOC_RELEASE_NOTES, 
      captionsBundle.getString ("release_notes"));
    }

/*=========================================================================
  
   menuCommandIdent

=========================================================================*/
  private void menuCommandIdent()
    {
    Logger.in();
    manageIdentity();
    Logger.out();
    }

/*=========================================================================
  
   menuCommandHome 

=========================================================================*/
  private void menuCommandHome()
    {
    Logger.in();
    home();
    Logger.out();
    }


/*=========================================================================
  
  menuCommandNewWindow 

=========================================================================*/
  private static void menuCommandNewWindow ()
    {
    Logger.in();
    MainWindow viewer = new MainWindow();
    viewer.setVisible (true);
    if (Config.getConfig().getNewWindowMode() == 0)
      viewer.home();
    Logger.out();
    }

/*=========================================================================
  
   menuCommandOpenLink 

   Prompt the user for a URL, and try to open it

=========================================================================*/
  private void menuCommandOpenLink ()
    {
    Logger.in();
    String url = JOptionPane.showInputDialog (this, 
      dialogsBundle.getString ("enter_gemini_url") + ":", DIALOG_CAPTION, 1);
    if (url != null)
      {
      try
        {
        if (Logger.isDebug())
          Logger.log (getClass().getName(), Logger.DEBUG, 
            "User selected " + url);
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
  
   menuCommandReload

   Reload the current URL

=========================================================================*/
  private void menuCommandReload()
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
  
   menuCommandRoot

=========================================================================*/
  private void menuCommandRoot()
    {
    Logger.in();
    try
      {
      loadURI (getRootUri (baseUri));
      }
    catch (Exception e)
      {
      // The exception has already been shown 
      }
    Logger.out();
    }

/*=========================================================================
  
   menuCommandSubscribePage

=========================================================================*/
  private void menuCommandSubscribePage()
    {
    Logger.in();
    if (displayName != null)
      {
      try
        {
        String feedName = displayName;
        if (config.getEmojiStripBookmarks())
          feedName = EmojiManager.removeAllEmojis (feedName);
        if (feedHandler.addFeed (displayName, baseUri))
          setStatus (messagesBundle.getString ("feed_added")); 
        else
          setStatus (messagesBundle.getString ("already_subscribed")); 
        }
      catch (IOException e)
        {
        reportGenError (e.getMessage());
        }
      }
    }

/*=========================================================================
  
   menuCommandStop

=========================================================================*/
  private void menuCommandStop()
    {
    Logger.in();
    stop();
    Logger.out();
    }

/*=========================================================================
  
  menuCommandSave 

=========================================================================*/
  private void menuCommandSave()
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
	Logger.log (getClass().getName(), Logger.DEBUG, 
          "Save file " + fc.getSelectedFile());
	try
	  {
	  FileUtil.byteArrayToFile 
	     (fc.getSelectedFile(), lastContent.getContent());
	  setStatus (messagesBundle.getString ("wrote_file") 
             + " " + fc.getSelectedFile());
	  }
	catch (IOException e)
	  {
	  reportException (fc.getSelectedFile().toString(), e);
	  }
	}
      }
    else
      {
      reportGenError (messagesBundle.getString ("save_only_text_message"));
      }
    Logger.out();
    }

/*=========================================================================
  
   menuCommandSettings

=========================================================================*/
  private void menuCommandSettings()
    {
    Logger.in();
    SettingsDialog d = new SettingsDialog (this);
    d.setVisible (true);
    Logger.out();
    }

/*=========================================================================
  
   menuCommandServerCert

=========================================================================*/
  private void menuCommandServerCert()
    {
    Logger.in();
    if (lastContent == null)
      {
      reportGenInfo (messagesBundle.getString ("page_not_remote"));
      }
    else
      {
      String certinfo = lastContent.getCertinfo();
      if (certinfo == null)
        {
        reportGenInfo (messagesBundle.getString ("no_certinfo"));
        }
      else
        {   
        reportGenInfo (certinfo);
        }
      }
    Logger.out();
    }

/*=========================================================================
  
  menuCommandViewAggregatedFeeds

=========================================================================*/
  private void menuCommandViewAggregatedFeeds()
    {
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
    loadURI ("file://" + config.getAggregatedFeedsFile()); 
    }

/*=========================================================================
  
   menuCommandZoomIn 

=========================================================================*/
  private void menuCommandZoomIn()
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
  
   menuCommandZoomOut

=========================================================================*/
  private void menuCommandZoomOut()
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
  
  promptGetHandler

=========================================================================*/
  private int promptGetAction (String contentType)
    {
    //ContentHandlerAction handler = 
    //   ContentHandlerAction.getHandler (contentType);
    // Remove anything after the first space in the content type;
    //   we don't want different handlers for different 
    //   charsets (probably)
    int p = contentType.indexOf (' ');
    if (p >= 0)
      contentType = contentType.substring (0, p).trim();

    int action = config.getContentHandlerAction (contentType);

    if (action < 0)
      {
      SelectActionDialog d = new SelectActionDialog (this, contentType);
      d.setVisible (true);
      action = d.getAction(); 
      if (action >= 0 && d.getAlways())
        {
        config.setContentHandlerAction (contentType, action);
        config.save();
        }
      }
    
    return action;
    }

/*=========================================================================
  
   refresh 

=========================================================================*/
  /** Reload the current URL. That is, fetch the data from the server
      again, and render it in the viewer again.
  */
  public void refresh()
    {
    Logger.in();
    // XXX System.out.println ("baseur=" + baseUri);
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
  
  handleRightClick 

=========================================================================*/
  /** Handle right-clicks on links by popping up the link context menu.
  */
  private void handleRightClick (String href, int x, int y)
    {
    Logger.in();

    JPopupMenu linkMenu = new JPopupMenu ("Link action"); // title not seen

    JMenuItem openMenuItem = 
      new JMenuItem (menusBundle.getString ("context_open"));
    openMenuItem.addActionListener ((event) -> loadURI (href));

    JMenuItem openNewMenuItem = 
      new JMenuItem (menusBundle.getString ("context_open_in_new_window"));
    openNewMenuItem.addActionListener ((event) -> newWindow (href, null));
    
    JMenuItem copyLinkMenuItem = 
      new JMenuItem (menusBundle.getString ("context_copy_link"));
    copyLinkMenuItem.addActionListener 
      ((event) -> Clipboard.copyTextToClipboard (href));

    JMenuItem downloadMenuItem = 
      new JMenuItem (menusBundle.getString ("context_download"));
    downloadMenuItem.addActionListener((event) -> chooseAndDownloadURI (href));

    linkMenu.add (openMenuItem);
    linkMenu.add (openNewMenuItem);
    linkMenu.add (copyLinkMenuItem);
    linkMenu.add (downloadMenuItem);

    String contentType = FileUtil.guessMimeTypeFromFilename (href); 
    if (contentType.startsWith ("audio") || contentType.startsWith ("video"))
      {
      JMenuItem streamMenuItem = 
	new JMenuItem (menusBundle.getString ("context_stream"));
      streamMenuItem.addActionListener((event) -> streamOut (href));
      linkMenu.add (streamMenuItem);
      }

    linkMenu.show (jEditorPane, x, y); 

    Logger.out();
    }

/*=========================================================================
  
  setHtml

=========================================================================*/
  /**
  Set the HTML shown by this viewer to the supplied text. Scroll to
  the top.
  */
  public void setHtml (String s)
    {
    Logger.in();
    jEditorPane.setText (s);
    jEditorPane.setCaretPosition (0);
    Logger.out();
    }

/*=========================================================================
  
  newWindow 

=========================================================================*/
  /**
  Open a new window with the specified URL.
  */
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

=========================================================================*/
  /**
  Open a new window with the specified URL.
  */
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
  
  reloadSettings 

=========================================================================*/
  /** Tell the config class to reload its file and update itself. 
  */ 
  public void reloadSettings()
    {
    Logger.in();
    Config.getConfig().load();
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
	Logger.log (getClass().getName(), Logger.DEBUG, 
          "renderToHtml(), Encoding is " + encoding);
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
        return messagesBundle.getString ("unsup_encoding_resp") 
          + ": " + encoding;
        }
      }
    else
      {
      Logger.out();
      return messagesBundle.getString ("empty_resp");
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
    setHtml (renderToHtml (new GophermapConverter (baseUri), 
      content, encoding));
    Logger.out();
    }

/*=========================================================================
  
  renderMarkdown

=========================================================================*/
  private void renderMarkdown (byte[] content, String encoding)
    {
    Logger.in();
    setHtml (renderToHtml (new MarkdownConverter (baseUri), 
      content, encoding));
    Logger.out();
    }

/*=========================================================================
  
  reportException

=========================================================================*/
  /** Do something vaguely useful with exceptions. 
  */
  private void reportException (String url, Exception e)
    {
    Logger.log (getClass().getName(), Logger.WARNING, e.getMessage()); 
    String messageFromServer = e.getMessage();
    if (e instanceof UnknownHostException)
      {
      reportGenError (url, messagesBundle.getString ("unknown_host") 
        + ": " + e.getMessage());
      }
    else
      reportGenError (url, e.getMessage());
    }

/*=========================================================================
  
  reportErrorResponseException

=========================================================================*/
  /** Do something vaguely useful with error messages that were returned
      by the remote server. */ 
  private void reportErrorResponseException (String url, 
           ErrorResponseException e)
    {
    String messageFromServer = e.getMessage();
    String message = "Error response " + e.getStatus() + " from server";
    if (messageFromServer != null && messageFromServer.length() > 0)
      message += ": " + messageFromServer;
    Logger.log (getClass().getName(), Logger.WARNING, message); 
    reportGenError (url, message);
    }

/*=========================================================================
  
  reportGenError

=========================================================================*/
  /** Do something vaguely useful with error messages
  */
  protected void reportGenError (String message)
    {
    reportGenError (null, message);
    }

/*=========================================================================
  
  reportGenError

=========================================================================*/
  /** Do something vaguely useful with error messages
  */
  protected void reportGenError (String url, String message)
    {
    DialogHelper.errorDialog (this, url, message);
    }

/*=========================================================================
  
  reportGenInfo

=========================================================================*/
  /**
  Do something vaguely useful with information messages.
  */
  protected void reportGenInfo (String url, String message)
    {
    DialogHelper.infoDialog (this, url, message);
    }

/*=========================================================================
  
  reportGenInfo

=========================================================================*/
  /**
  Do something vaguely useful with information messages.
  */
  protected void reportGenInfo (String message)
    {
    reportGenInfo (null, message);
    }

/*=========================================================================
  
  setCaptionFromResponse

=========================================================================*/
  /**
  Sets the window caption and the value of displayName. Uses the response
  if there is one, otherwise just bases the result on the URI.
  */
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
	String encoding = FileUtil.getEncodingFromMime (mime);
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
      displayName = FileUtil.getDisplayNameFromURI (uri);
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
  /** Set a message to the status bar. It will automatically be
      cleared after a time by the status-clearing timer. */
  protected void setStatus (String s)
    {
    Logger.in();
    statusBar.setStatus (s);
    Logger.out();
    }

/*=========================================================================
  
  showBookmarks 

=========================================================================*/
  /** Raise the bookmark viewer. In practice we just show the bookmark
      Gemtext file in a document viewer. */
  private void showBookmarks()
    {
    Logger.in();
    try
      {
      bookmarkHandler.showBookmarks();
      }
    catch (Exception e)
      {
      reportGenError (e.getMessage());
      }
    Logger.out();
    }

/*=========================================================================
  
   stop

=========================================================================*/
  /** Stop the current transfer. In practice, right now we just call
      cancelLoad, but I've used a separate method in case there's
      additional, UI-related work to do in future. */
  public void stop()
    {
    Logger.in();
    cancelLoad();
    Logger.out();
    }

/*=========================================================================
  
   streamOut 

=========================================================================*/
  /* Start a streaming operation to a media player for the specified
     URL. We'll start the player process and gets its stdin channel;
     then, on a background thread, we read from the URL and write to
     the processes stdin. */ 
  public void streamOut (URL url)
    {
    Logger.in();

    String player = config.getStreamPlayer();
    if (player == null)
      {
      reportGenError (messagesBundle.getString ("no_stream_player"));
      return;
      }

    SwingFileDownload sfd = new SwingFileDownload (this, url.toString(), 
      new ApplicationDownloadTarget (player), null);
    setStatus (messagesBundle.getString ("streaming"));
    sfd.start();
    Logger.out();
    }

/*=========================================================================
  
   streamOut 

=========================================================================*/
  /* Start a streaming operation to a media player for the specified
     URL. We'll start the player process and gets its stdin channel;
     then, on a background thread, we read from the URL and write to
     the processes stdin. */ 
  public void streamOut (String url)
    {
    Logger.in();

    String player = config.getStreamPlayer();
    if (player == null)
      {
      reportGenError (messagesBundle.getString ("no_stream_player"));
      return;
      }

    SwingFileDownload sfd = new SwingFileDownload (this, url, 
      new ApplicationDownloadTarget (player), null);
    setStatus (messagesBundle.getString ("streaming"));
    sfd.start();
    Logger.out();
    }

  }

