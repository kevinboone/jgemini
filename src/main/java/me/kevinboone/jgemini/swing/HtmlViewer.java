/*=========================================================================
  
  JGemini

  HtmlViewer

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import me.kevinboone.jgemini.protocol.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;
import java.io.*;
import java.util.*;

public class HtmlViewer extends JFrame 
  {
  private final static String DIALOG_CAPTION = Config.APP_NAME;
  private final static String WINDOW_CAPTION = Config.APP_NAME;
  private final static String EMPTY_WINDOW_TEXT = 
    Config.APP_NAME + ": a browser for the Gemini protocol";
  private JEditorPane jEditorPane;
  private URL baseUrl = null;
  private TopBar topBar;
  private StatusBar statusBar;
  private JMenuBar menuBar;
  private Stack<URL> backlinks = new Stack<URL>();
  private SwingWorker loadWorker = null;
  private String searchText = "java"; // TODO
  private int searchPos = 0;


/*=========================================================================
  
  clearStatus    

=========================================================================*/
  private void clearStatus ()
    {
    statusBar.clearStatus ();
    }

/*=========================================================================
  
  renderMarkdown

  Convert the markdown text to HTML and display it

=========================================================================*/
  private void renderMarkdown (byte[] content, String encoding)
    {
    if (content.length > 0)
      {
      try
        {
        Logger.log (getClass(), "Converting markdown text to HTML");
        String stringForm;
        Logger.log (getClass(), "Encoding is " + encoding);
        if (encoding != null && encoding.length() > 0)
          stringForm = new String (content, encoding);
        else
          stringForm = new String (content);
        String html = new MarkdownConverter(baseUrl).markdownToHtml 
           (stringForm, null); // Some work to do here
        setHtml (html);
        }
      catch (UnsupportedEncodingException e)
        {
        setHtml ("[Server returned a response with an unsupported encoding: " 
          + encoding + "]");
        }
      }
    else
      {
      setHtml ("[Server returned a valid, but empty, response]");
      }
    }

/*=========================================================================
  
  renderPlain

  Convert the plain text to HTML and display it

=========================================================================*/
  private void renderPlain (byte[] content, String encoding)
    {
    if (content.length > 0)
      {
      try
        {
        Logger.log (getClass(), "Converting plain text to HTML");
        String stringForm;
        Logger.log (getClass(), "Encoding is " + encoding);
        if (encoding != null && encoding.length() > 0)
          stringForm = new String (content, encoding);
        else
          stringForm = new String (content);
        String html = new TextConverter().textToHtml 
           (stringForm, null); // Some work to do here
        setHtml (html);
        }
      catch (UnsupportedEncodingException e)
        {
        setHtml ("[Server returned a response with an unsupported encoding: " 
          + encoding + "]");
        }
      }
    else
      {
      setHtml ("[Server returned a valid, but empty, response]");
      }
    }

/*=========================================================================
  
  renderGemtext  

  Convert the text returned from the server in a text/gemini response
  to HTML, and display it. Assume the specified encoding, unless encoding
  is null, in which case assume platform encoding.

=========================================================================*/
  private void renderGemtext (byte[] content, String encoding)
    {
    if (content.length > 0)
      {
      try
        {
        Logger.log (getClass(), "Converting GemText to HTML");
        String stringForm;
        Logger.log (getClass(), "Encoding is " + encoding);
        if (encoding != null && encoding.length() > 0)
          stringForm = new String (content, encoding);
        else
          stringForm = new String (content);
        String html = new GemConverter (baseUrl).gemToHtml 
           (stringForm, null); // Some work to do here
        setHtml (html);
        }
      catch (UnsupportedEncodingException e)
        {
        setHtml ("[Server returned a response with an unsupported encoding: " 
          + encoding + "]");
        }
      }
    else
      {
      setHtml ("[Server returned a valid, but empty, response]");
      }
    }

/*=========================================================================
  
   openLink 

   Prompt the user for a URL, and try to open it

=========================================================================*/
  private void openLink ()
    {
    Logger.log (getClass(), "Prompt for link");
    String url = JOptionPane.showInputDialog (this, "Enter a Gemini URL:", 
      DIALOG_CAPTION, 1);
    if (url != null)
      {
      try
        {
        loadURL (new URL(url));
        }
      catch (Exception e)
        {
        reportException (url, e);
        }
      }
    }

/*=========================================================================
  
   goHome

   Go back to the home page

=========================================================================*/
  protected void goHome ()
    {
    loadURL (Config.getConfig().getHomePage());
    }

/*=========================================================================
  
   goBack

   Go back to the previous page, if there was one.

=========================================================================*/
  protected void goBack ()
    {
    Logger.log (getClass(), "goBack()");
    // Note that the current URL -- at there always will be one -- will
    //   be at the top of the stack. So we have to take that off, then get
    //   to the previous URL. If we take it off and there _isn'_ a 
    //   previous URL, we can't go anywhere, so we have to put the current
    //   URL back on the stack.
    URL current = backlinks.pop();
    if (backlinks.size() > 0)
      {
      URL backUrl = backlinks.pop();
      Logger.log (getClass(), "goBack(): got back URL " + backUrl);
      loadURL (backUrl);
      }
    else
      {
      Logger.log (getClass(), "goBack(): back-link stack is empty");
      backlinks.push (current);
      }
    }

/*=========================================================================
  
  handleStatus10 

  Deal with "Status 10" responses, that require further input.
  We just prompt the user for a string, and then repeat the
  original request.

=========================================================================*/
  private void handleStatus10 (boolean hide, String prompt, URL retryUrl)
    {
    Logger.log (getClass(), "handleStatus10(): Handling status 10, hide= " 
      + hide + "with promt=" + prompt + ", url=" + retryUrl);

    String str = JOptionPane.showInputDialog (this, prompt, DIALOG_CAPTION, 1);
    if(str != null)
      {
      try
        {
        Logger.log (getClass(), "handleStatus10(): Retrying URL " + retryUrl);
        loadGemini (retryUrl, URLEncoder.encode (str, "UTF-8")); 
        }
      catch (UnsupportedEncodingException e)
        {
        // Screw that. If the JVM doesn't support UTF8, we're in big trouble
        }
      }
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
    Logger.log (getClass(), "handleRedirect(): Redirect to " + url);
    loadURL (url);
    }

/*=========================================================================
  
  handleUnsupportedMime

  At present, any response type other than text/gemini is written to
  a temp file, and then the desktop is invoked on it. What happens
  after that is down to the interaction between the JVM and the
  platform.

=========================================================================*/
  private void handleUnsupportedMime (URL url, String mime, byte[] content)
    {
    try
      {
System.out.println ("mime=" + mime);
      String ext = FileUtil.getDefaultExtension (mime);
System.out.println (" ext=" + ext);
      File tempFile = File.createTempFile ("gemini-", "." + ext);
      tempFile.deleteOnExit();
      FileUtil.byteArrayToFile (tempFile, content);
      Desktop.getDesktop().browse (java.net.URI.create ("file://" + tempFile));
      }
    catch (Exception e)
      {
      reportException (url.toString(), e);
      }
    }

/*=========================================================================
  
  loadGeminiContent
  
  Make the request on the server, and pack the results into a 
  GeminiContent object. The results may include an exception -- this
  method itself must not throw any exception

=========================================================================*/
  private GeminiContent loadGeminiContent (URL url)
    {
    GeminiContent gc = new GeminiContent (url);
    try
      {
      URLConnection conn = url.openConnection();
      byte[] content = (byte[]) conn.getContent(); 
      String mime = conn.getContentType();
      gc.setMime (mime);
      gc.setContent (content);
      }
    catch (Exception e)
      {
      gc.setException (e);
      }
    return gc;
    }

/*=========================================================================
  
  cancelLoad

=========================================================================*/
  private void cancelLoad ()
    {
    Logger.log (getClass(), "cancelLoad())");
    if (loadWorker != null)
      {
      Logger.log (getClass(), "Cancelling load");
      loadWorker.cancel (true);
      }
    else
      {
      Logger.log (getClass(), "No load to cancel");
      }
    }

/*=========================================================================
  
  getEncodingFromMime

=========================================================================*/
  private String getEncodingFromMime (String mime)
    {
    String[] args = mime.split (";");
    for (int i = 0; i < args.length; i++)
      {
      String arg = args[i].trim();
      if (arg.startsWith ("charset="))
        return arg.substring (8);
      }

    return null;
    }

/*=========================================================================
  
  loadGemini
  
  We have a gemini:// URL. Load it through its content handler.

  If the qparam arg is non-null, it is appended as a query paramter 
  _as is_.  Probably whatever calls this method will need to URL-encode 
  qparam first. 

=========================================================================*/
  private void loadGemini (URL url, String qparam)
    {
    cancelLoad(); // Kill any existing background load
    try
      {
      Logger.log (getClass(), "loadGemini(), url=" 
        + url.toString() + ", qparam=" + qparam);

      if (qparam != null)
        url = new URL (url.toString() + "?" + qparam);

      if (url.getPath().length() == 0)
        {
        Logger.log (getClass(), "Adding path to URL that lacks one");
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
      GeminiContent gc = null;

      @Override
      protected String doInBackground() throws Exception  
        { 
        setStatus ("Loading " + fullUrl);
        gc = loadGeminiContent (fullUrl); 
        return "foo";
        } 

      @Override
      protected void process (java.util.List chunks) 
        { 
        //statusLabel.setText(String.valueOf(val)); 
        } 

      @Override
      protected void done()  
        { 
        if (!isCancelled())
          {
          clearStatus ();
          Exception e = gc.getException();
          if (e == null)
            {
            String mime = gc.getMime();
            if (mime.startsWith ("text/gemini"))
              {
              baseUrl = fullUrl; 
              // We have to set this here, because renderGemtext
              //  needs it, for forming links in the HTML
              Logger.log (getClass(), "Content is text/gemini");
              String encoding = getEncodingFromMime (mime);
              renderGemtext (gc.getContent(), encoding);
              topBar.showUrl (fullUrl.toString());
              backlinks.push (fullUrl);
              }
            else if (mime.startsWith ("text/plain"))
              {
              baseUrl = fullUrl; 
              // We have to set this here, because renderGemtext
              //  needs it, for forming links in the HTML
              Logger.log (getClass(), "Content is text/plain");
              String encoding = getEncodingFromMime (mime);
              renderPlain (gc.getContent(), encoding);
              topBar.showUrl (fullUrl.toString());
              backlinks.push (fullUrl);
              }
            else if (mime.startsWith ("text/markdown"))
              {
              baseUrl = fullUrl; 
              // We have to set this here, because renderGemtext
              //  needs it, for forming links in the HTML
              Logger.log (getClass(), "Content is text/markdown");
              String encoding = getEncodingFromMime (mime);
              renderMarkdown (gc.getContent(), encoding);
              topBar.showUrl (fullUrl.toString());
              backlinks.push (fullUrl);
              }
            else
              {
              Logger.log (getClass(), "Content is " + mime);
              handleUnsupportedMime (fullUrl, mime, gc.getContent());
              }
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
    }

/*=========================================================================
  
  loadLocalFile 

=========================================================================*/
  public void loadLocalFile (URL url)
    {
    try (InputStream is = url.openConnection().getInputStream())
      {
      ByteArrayOutputStream content_buffer = new ByteArrayOutputStream();

      int nRead;
      byte[] data = new byte[16384];

      while ((nRead = is.read (data, 0, data.length)) != -1) 
        {
        content_buffer.write (data, 0, nRead);
        }

      byte[] content = content_buffer.toByteArray();
      content_buffer.close();

      if (url.toString().endsWith ("gmi"))
        renderGemtext (content, null /* don't know encoding*/);
      else if (url.toString().endsWith ("md"))
        renderMarkdown (content, null /* don't know encoding*/);
      else
        renderPlain (content, null /* don't know encoding*/);
      }
    catch (Exception e)
      {
      reportException (url.toString(), e);
      }
    }

/*=========================================================================
  
  reportException

  Do something vaguely useful with exceptions

=========================================================================*/
  private void reportException (String url, Exception e)
    {
    e.printStackTrace();
    String messageFromServer = e.getMessage();
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
    sb.append ("<html><body width=\"300px\">");
    sb.append ("<p><b>");
    sb.append (url);
    sb.append ("</b></p><p></p>");
    sb.append (message);
    sb.append ("<p></p>");
    JOptionPane.showMessageDialog (this, new String(sb), 
         DIALOG_CAPTION, JOptionPane.ERROR_MESSAGE); 
    sb.append ("</body></html>");
    }

/*=========================================================================
  
   loadForeignURI 

   Any attempt to load a URI that does not start "gemini://" ends up here.
   At present, we just use the Java desktop support to try to invoke
   a handler for it.

=========================================================================*/
  private void loadForeignURL (URL url)
    {
    Logger.log (getClass(), "loadForeignURL(), URL is " + url.toString());
    try 
      {
      Desktop.getDesktop().browse (new URI (url.toString()));
      }
    catch (Exception e)
      {
      reportException (url.toString(), e);
      }
    }

/*=========================================================================
  
   loadURL

   Load any kind of URL. If it doesn't start with gemini://, treat is
   as external, which means invoking the desktop on it.

=========================================================================*/
  public void loadURL (URL url)
    {
    Logger.log (getClass(), "loadURL(), URL is " + url);
    if (url.getProtocol().equals ("gemini"))
      {
      loadGemini (url, null);
      baseUrl = url;
      }
    else if (url.getProtocol().equals ("file"))
      {
      baseUrl = url;
      loadLocalFile (url);
      topBar.showUrl (url.toString());
      backlinks.push (url);
      }
    else
      {
      loadForeignURL (url);
      } 
    }

/*=========================================================================
  
   refresh 

   Load any kind of URL. If it doesn't start with gemini://, treat is
   as external, which means invoking the desktop on it.

=========================================================================*/
  public void refresh()
    {
    Logger.log (getClass(), "Refresh");
    if (baseUrl != null)
      {
      // This is potentially nasty. We have to pop the back-link at TOS
      // because loadURL() will replace it. But loadURL() won't replace
      // it unless the load succeeds -- we don't want a dead link lurking
      // on the stack. But loadURL() won't do this itself -- it will 
      // schedule it to be done when the load completes (if it completes).
      // So we pop the TOS here, with no guarantee that it will actually
      // get put back. In practice, we're refreshing a link that previously
      // loaded OK; so it should be fine. Still, it's a bit ugly.
      backlinks.pop();
      loadURL (baseUrl);
      }
    }

/*=========================================================================
  
   loadURL

   Load any kind of URL. If it doesn't start with gemini://, treat it
   as external, which means invoking the desktop on it.

=========================================================================*/
  public void loadURL (String url)
    {
    try
      {
      loadURL (new URL(url));
      }
    catch (Exception e)
      {
      reportException (url, e);
      }
    }

/*=========================================================================
  
   handleLink 

   Easy -- just load the URL

=========================================================================*/
  public void handleLink (URL url)
    {
    Logger.log (getClass(), "handleLink(), link is " + url);
    if (url != null)
      loadURL (url);
    else
      reportGenError ("unknown", "Could not parse link");
    }

/*=========================================================================
  
   handleLinkHover

   TODO TODO TODO

=========================================================================*/
  private void handleLinkHover (URL linkUrl)
    {
    Logger.log (getClass(), "handleLinkHover(), link is " + linkUrl);
    if (linkUrl != null)
      setStatus (linkUrl.toString());
    }

/*=========================================================================
  
   handleLinkUnhover

   TODO TODO TODO

=========================================================================*/
  private void handleLinkUnhover (URL linkUrl)
    {
    Logger.log (getClass(), "handleLinkUnhover(), link is " + linkUrl);
    if (linkUrl != null)
      clearStatus();
    }

/*=========================================================================
  
  applyStylesFromConfig 

=========================================================================*/
  private void applyStylesFromConfig ()
    {
    HTMLEditorKit kit = (HTMLEditorKit)jEditorPane.getEditorKit();

    StyleSheet styleSheet = kit.getStyleSheet();
    styleSheet.addRule ("body {" 
        + Config.getConfig().getProperty 
            (Config.STYLE_BODY, Config.DEFLT_STYLE_BODY)  + "}");
    styleSheet.addRule ("h1 {" 
        + Config.getConfig().getProperty 
            (Config.STYLE_H1, Config.DEFLT_STYLE_H1)  + "}");
    styleSheet.addRule ("h2 {" 
        + Config.getConfig().getProperty 
            (Config.STYLE_H2, Config.DEFLT_STYLE_H2)  + "}");
    styleSheet.addRule ("h3 {" 
        + Config.getConfig().getProperty 
            (Config.STYLE_H3, Config.DEFLT_STYLE_H3)  + "}");
    styleSheet.addRule ("pre {" 
        + Config.getConfig().getProperty 
            (Config.STYLE_PRE, Config.DEFLT_STYLE_PRE)  + "}");
    styleSheet.addRule ("a {" 
        + Config.getConfig().getProperty 
            (Config.STYLE_A, Config.DEFLT_STYLE_A)  + "}");
    styleSheet.addRule ("a:hover {" 
        + Config.getConfig().getProperty 
            (Config.STYLE_A_HOVER, Config.DEFLT_STYLE_A_HOVER)  + "}");
    }

/*=========================================================================
  
  Constructor

=========================================================================*/
  public HtmlViewer ()
    {
    super();
    Logger.log (getClass(), "HtmlViewer constructor");
    jEditorPane = new JEditorPane();
        
    HTMLEditorKit kit = new HTMLEditorKit();
    jEditorPane.setEditorKit (kit);
    jEditorPane.setEditable (false);

    // In order to use ctrl+h and backspace in the menu, we have
    //  to disable them in the editor, even when (sigh) is it set
    //  to read-only
    InputMap inputMap = jEditorPane.getInputMap();
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
      "none");
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK),
      "none");

    jEditorPane.addHyperlinkListener(new HyperlinkListener()
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
              handleLink (e.getURL());
              }
            } 
          });

    addWindowListener (new WindowAdapter() 
      {
      public void windowOpened(WindowEvent e) 
        {
        jEditorPane.requestFocus();
        }
      });

    applyStylesFromConfig ();

    topBar = new TopBar(this);
    statusBar = new StatusBar();
    JScrollPane scrollPane = new JScrollPane (jEditorPane);
    getContentPane().add(topBar, BorderLayout.NORTH);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(statusBar, BorderLayout.SOUTH);
    Document doc = kit.createDefaultDocument();
    jEditorPane.setDocument(doc);
    jEditorPane.setText (EMPTY_WINDOW_TEXT);
    setSize (Config.getConfig().getWindowWidth(), 
       Config.getConfig().getWindowHeight());
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    createMenuBar();
    setJMenuBar (menuBar);
    setTitle (WINDOW_CAPTION);

    // Set the frame's icon from a file in the JAR
    URL iconURL = getClass().getResource ("/images/jgemini.png");
    ImageIcon icon = new ImageIcon (iconURL);
    setIconImage (icon.getImage());
    }

/*=========================================================================
  
  setHtml

  Set the HTML shown by this viewer to the supplied text 

=========================================================================*/
  public void setHtml (String s)
    {
    Logger.log (getClass(), "Setting HTML");
    jEditorPane.setText (s);
    jEditorPane.setCaretPosition (0);
    }

/*=========================================================================
  
  find

=========================================================================*/
  public void find()
    {
    String text = JOptionPane.showInputDialog (this, "Enter text to search:", 
      DIALOG_CAPTION, 1);
    if (text != null)
      {
      searchPos = 0;
      searchText = text.toLowerCase();
      findNext();
      }
    }

/*=========================================================================
  
  findNext 

=========================================================================*/
  public void findNext ()
    {
    Logger.log (getClass(), "findNext() text is " + searchText);
    if (searchText != null)
      {
      Document doc = jEditorPane.getDocument();
      int findLength = searchText.length();
      if (searchPos + findLength > doc.getLength()) 
        {
        searchPos = 0; // Wrap around to beginning
        setStatus ("Search wrapped around to top");
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
           jEditorPane .moveCaretPosition (searchPos);
           searchPos += findLength;
           }
        else
          setStatus ("Not found");
         }
       catch (BadLocationException e)
         {
         e.printStackTrace();
         }

    }
  }


/*=========================================================================
  
  newWindow 

=========================================================================*/
  public void newWindow ()
    {
    HtmlViewer viewer = new HtmlViewer();
    viewer.setVisible (true);
    if (Config.getConfig().getNewWindowMode() == 0)
      viewer.goHome();
    }

/*=========================================================================
  
  about 

=========================================================================*/
  public void about ()
    {
    String s = "<html><head></head><body>";
    s += "<h1>" + Config.APP_NAME + "</h1>";
    s += "<h3>" + "Version " + Config.VERSION + "</h3>";
    s += "<p>A browser for Project Gemini servers and content</p>";
    s += "<p>Maintained by Kevin Boone, and distributed under the terms ";
    s += "of the GNU PUblic Licence, v3.0&nbsp;&nbsp;</p>";
    s += "<p>For more information, see ";
    s +=  "<b>https://kevinboone.me/jgemini.html</b></p>";
    s +=  "<p>&nbsp;</p>";
    s += "</body></html>\n";
    JOptionPane.showMessageDialog (this, s, 
         DIALOG_CAPTION, JOptionPane.INFORMATION_MESSAGE); 
    }

/*=========================================================================
  
  createMenuBar

=========================================================================*/
  private void createMenuBar ()
    {
    menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic (KeyEvent.VK_F);
    JMenuItem newMenuItem = new JMenuItem ("New");
    newMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    newMenuItem.setMnemonic (KeyEvent.VK_N);
    newMenuItem.addActionListener((event) -> newWindow());
    fileMenu.add (newMenuItem);
    JMenuItem openMenuItem = new JMenuItem ("Open link...");
    openMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    openMenuItem.setMnemonic (KeyEvent.VK_O);
    openMenuItem.addActionListener((event) -> openLink());
    fileMenu.add (openMenuItem);
    JMenuItem closeMenuItem = new JMenuItem ("Close");
    closeMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_W, ActionEvent.CTRL_MASK));
    closeMenuItem.setMnemonic (KeyEvent.VK_C);
    closeMenuItem.addActionListener((event) -> dispose());
    fileMenu.add (closeMenuItem);
    JMenuItem exitMenuItem = new JMenuItem ("Exit");
    exitMenuItem.setMnemonic (KeyEvent.VK_X);
    exitMenuItem.addActionListener((event) -> System.exit(0));
    fileMenu.add (exitMenuItem);

    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic (KeyEvent.VK_E);
    JMenuItem selectAllMenuItem = new JMenuItem ("Select all");
    selectAllMenuItem.setMnemonic (KeyEvent.VK_A);
    selectAllMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    selectAllMenuItem.addActionListener((event) -> jEditorPane.selectAll());
    editMenu.add (selectAllMenuItem);
    JMenuItem copyMenuItem = new JMenuItem ("Copy");
    copyMenuItem.setMnemonic (KeyEvent.VK_C);
    copyMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    copyMenuItem.addActionListener((event) -> jEditorPane.copy());
    editMenu.add (copyMenuItem);
    JMenuItem findMenuItem = new JMenuItem ("Find in page...");
    findMenuItem.setMnemonic (KeyEvent.VK_F);
    findMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    findMenuItem.addActionListener((event) -> find());
    editMenu.add (findMenuItem);
    JMenuItem findNextMenuItem = new JMenuItem ("Find next");
    findNextMenuItem.setMnemonic (KeyEvent.VK_N);
    findNextMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_G, ActionEvent.CTRL_MASK));
    findNextMenuItem.addActionListener((event) -> findNext());
    editMenu.add (findNextMenuItem);

    JMenu goMenu = new JMenu("Go");
    goMenu.setMnemonic (KeyEvent.VK_G);
    JMenuItem backMenuItem = new JMenuItem ("Back");
    backMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_BACK_SPACE, 0));
    backMenuItem.addActionListener((event) -> goBack());
    goMenu.add (backMenuItem);
    JMenuItem homeMenuItem = new JMenuItem ("home");
    homeMenuItem.setAccelerator (KeyStroke.getKeyStroke
      (KeyEvent.VK_H, ActionEvent.CTRL_MASK));
    homeMenuItem.addActionListener((event) -> goHome());
    goMenu.add (homeMenuItem);

    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic (KeyEvent.VK_H);
    JMenuItem aboutMenuItem = new JMenuItem ("About " 
      + Config.APP_NAME + "...");
    aboutMenuItem.setMnemonic (KeyEvent.VK_A);
    aboutMenuItem.addActionListener((event) -> about());
    helpMenu.add (aboutMenuItem);

    menuBar.add (fileMenu);
    menuBar.add (editMenu);
    menuBar.add (goMenu);
    menuBar.add (helpMenu);
    }

/*=========================================================================
  
  setStatus    

=========================================================================*/
  private void setStatus (String s)
    {
    statusBar.setStatus (s);
    }


  }

