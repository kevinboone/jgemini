/*=========================================================================
  
  JGemini

  DownloadsDialog 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.event.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.Constants;

/** DownloadsDialog shows the contents of the DownloadsMonitor as a 
    JFrame. The main display is a Box wrapped in a Scrollpane.
    Each Download gets associated with an instance of DownloadComponent,
    which shows information about the transfer and provides buttons
    to manage it. DownloadsDialog implements DownloadMonitorListener,
    and registers itself with the DownloadMonitor to get updates 
    on new and changed transfers.    
*/
public class DownloadsDialog extends JFrame implements DownloadMonitorListener
  {
  private final static ResourceBundle captionsBundle = 
      ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Captions");
  private final static ResourceBundle dialogsBundle = 
      ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");
  private final static String caption = captionsBundle.getString ("downloads");

  private static DownloadsDialog instance = null;
  private static DownloadMonitor downloadMonitor 
    = DefaultDownloadMonitor.getInstance();  
  private JScrollPane scrollPane;
  private Box listBox;

  /*=========================================================================
    
    Constructor 

  =========================================================================*/
  private DownloadsDialog()
    {
    Logger.log (getClass().getName(), Logger.DEBUG, 
         "Instantiating DownloadDialog");

    DownloadsDialog self = this;
    setTitle (caption);

    setLayout (new BorderLayout());
    addWindowListener (new WindowAdapter() 
      {
      public void windowOpened(WindowEvent e) 
	{
	}

      public void windowClosing (WindowEvent e) 
	{
	Logger.log (getClass().getName(), Logger.DEBUG, 
	     "DownloadDialog closing");

        // It's crucial that we remove ourselves from the 
        //   DownloadMonitor, else this window won't
        //   ever be properly finalized, and this will stop
        //   JGemini shutting down.
        downloadMonitor.removeListener (self);
	dispose();
	instance = null;
	}
      });

    downloadMonitor.addListener (self);
    
    scrollPane = new JScrollPane (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    listBox = new Box (BoxLayout.Y_AXIS);
      
    scrollPane.setViewportView (listBox);

    JPanel buttonBox = new JPanel();

    JButton helpButton = JGeminiDialog.createButton ("downloads_help"); 
    helpButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e)
        {
        handleDocs();
        }
      });
    buttonBox.add (helpButton);

    JButton closeButton = JGeminiDialog.createButton ("downloads_close"); 
    InputMap inputMap = closeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = closeButton.getActionMap();
    inputMap.put ((javax.swing.KeyStroke)dialogsBundle.getObject
      ("downloads_close_accel"), "doClick");
    inputMap.put (KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0), "doClick");
    actionMap.put("doClick", new AbstractAction() 
      {
      @Override
      public void actionPerformed(ActionEvent e) 
        {
        closeButton.doClick(); 
        }
      });
    closeButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e)
        {
        self.dispatchEvent(new WindowEvent(self, WindowEvent.WINDOW_CLOSING));
        }
      });

    buttonBox.add (closeButton);
    JButton clearButton = JGeminiDialog.createButton ("downloads_clear"); 
    clearButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e)
        {
        clearDownloads();
        }
      });
    buttonBox.add (clearButton);

    setPreferredSize (new Dimension (700, 400)); // TODO
    setLocationByPlatform (true); // Let desktop position me
    add (scrollPane, BorderLayout.CENTER);
    add (buttonBox, BorderLayout.SOUTH);

    // We might be creating this dialog with a bunch of downloads already
    //   in the monitor.
    syncWithDownloadMonitor();

    pack();
    setResizable (true); 
    }


  /*=========================================================================
    
    clearDownloads 

  =========================================================================*/
  /** Just delegate this to the DownloadMonitor. It will call back through
      its Listener to update us of progress. */
  public void clearDownloads()
    {
    downloadMonitor.clear();
    }

  /*=========================================================================
    
    downloadAdded 

  =========================================================================*/
  /** Called by the DownloadMonitor when a new Downloads starts. We'll
      create a new DownloadComponent for it, and add it to the UI. */
  @Override
  public void downloadAdded (Download d)
    {
    Logger.log (getClass().getName(), Logger.DEBUG, 
	     "Download added to my list");

    DownloadComponent existing = getDownloadComponent (d);
    if (existing != null) return; // Shouldn't happen, unless we are 
                                  // notified twice

    DownloadComponent comp = DownloadComponent.create (d);
    comp.setFields();
    listBox.add (comp);
    SwingUtilities.invokeLater(() -> 
      {
      JScrollBar bar = scrollPane.getVerticalScrollBar();
      bar.setValue(bar.getMaximum());
      });
    }

  /*=========================================================================
    
    downloadChanged

  =========================================================================*/
  /** Called by the DownloadMonitor when it in turn gets a change notification
      from a Download. We must find the item in our list that corresponds
      to the specific Download, and update it. 
  */
  @Override
  public void downloadChanged (Download d)
    {
    Logger.log (getClass().getName(), Logger.DEBUG, 
	     "Download changed in my list");

    DownloadComponent existing = getDownloadComponent (d);
    if (existing == null)
      {
      // Shouldn't happen, unless we are 
      // didn't get notified of creation 
      Logger.log (getClass().getName(), Logger.WARNING, 
        "Internal error: notified about a change download not in my list");
      return;
      }

    existing.setFields();
    }

  /*=========================================================================
    
    downloadRemoved

  =========================================================================*/
  /** Called by the DownloadMonitor when it removes a download from its
      list.  We must find the item in our list that corresponds
      to the specific Download, and remove it here, too. 
  */
  @Override
  public void downloadRemoved (Download d)
    {
    Logger.log (getClass().getName(), Logger.DEBUG, 
      "Download removed");

    DownloadComponent comp = getDownloadComponent (d);
    if (comp != null)
      {
      listBox.remove (comp);
      listBox.repaint();
      } 
    else
      {
      Logger.log (getClass().getName(), Logger.WARNING, 
         "Internal error: attempt to remove a download that was not the DownloadDialog list");
      }
    }

  /*=========================================================================
    
    getInstance 

  =========================================================================*/
  public static DownloadsDialog getInstance()
    {
    if (instance == null)
      instance = new DownloadsDialog();

    return instance;
    }

  /*=========================================================================
    
    syncWithDownloadMonitor 

  =========================================================================*/
    /** Set the state of our list to match the DownloadMonitor. */
    private void syncWithDownloadMonitor()
      {
      // First we'll check that all the downloads in the monitor
      //   have corresponding components in our list

      int n = downloadMonitor.getDownloadCount();

      for (int i = 0; i < n; i++)
        {
        Download d = downloadMonitor.getDownload (i);
        DownloadComponent comp = getDownloadComponent (d);
        if (comp == null)
          {
          listBox.add (DownloadComponent.create (d));
          }
        }

      // Then we'l check whether any of the components in our
      //   list need to be removed, because they no longer
      //   appear in the monitor

      Component[] components = listBox.getComponents();
      int l = components.length;
      for (int i = 0; i < l; i++)
	{
        DownloadComponent comp = (DownloadComponent)components[i];
        if (downloadMonitor.indexOfDownload (comp.getDownload()) < 0)
          {
          listBox.remove (comp);
          }
        }
      }

  /** Find the component in our list that matches a particular
      Download in the monitor.
  */
  private DownloadComponent getDownloadComponent (Download d)
    {
    Component[] components = listBox.getComponents();
    int l = components.length;
    for (int i = 0; i < l; i++)
      {
      DownloadComponent existing = (DownloadComponent) components[i];
      if (existing.getDownload() == d) return existing;
      }
    return null; 
    }

  private void handleDocs()
    {
    MainWindow.newWindow (Constants.DOC_DOWNLOADS_DIALOG,
      "Downloads dialog"); // Caption will not show when loaded
    }

  } // End of DownloadsDialog

/** DownloadComponent is a small panel that gets attached to a particular
    instance of Download, and shows its status. 
*/ 
class DownloadComponent extends JPanel
  {
  private Download download;
  private JLabel filenameLabel;
  private JLabel urlLabel;
  private JLabel sizeLabel;
  private JLabel statusLabel;

  private Icon stopIcon; 
  private Icon inProgressIcon; 
  private Icon smileIcon; 
  private Icon frownIcon; 
  private Icon queryIcon; 
  private Icon errorIcon; 
  private Icon amberShriekIcon; 
  private Icon folderIcon; 

  private JButton stopButton;
  private JButton folderButton;

  private final static ResourceBundle dialogsBundle = 
      ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");
  private final static ResourceBundle tooltipsBundle = 
      ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Tooltips");

  private DownloadComponent (Download d)
    {
    super();
    download = d;

    URL stopImgUrl = getClass().getResource("/images/stop.png");
    stopIcon = new ImageIcon (stopImgUrl);
    URL inProgressImgUrl = getClass().getResource("/images/in_progress.png");
    inProgressIcon = new ImageIcon (inProgressImgUrl);
    URL smileImgUrl = getClass().getResource("/images/smile.png");
    smileIcon = new ImageIcon (smileImgUrl);
    URL frownImgUrl = getClass().getResource("/images/frown.png");
    frownIcon = new ImageIcon (frownImgUrl);
    URL queryImgUrl = getClass().getResource("/images/query.png");
    queryIcon = new ImageIcon (queryImgUrl);
    URL errorImgUrl = getClass().getResource("/images/error.png");
    errorIcon = new ImageIcon (errorImgUrl);
    URL amberShriekImgUrl = getClass().getResource("/images/amber_shriek.png");
    amberShriekIcon = new ImageIcon (amberShriekImgUrl);
    URL folderImgUrl = getClass().getResource("/images/folder.png");
    folderIcon = new ImageIcon (folderImgUrl);

    setLayout (new BorderLayout());

    Box dataBox = new Box (BoxLayout.Y_AXIS);

    filenameLabel = new JLabel ();
    urlLabel = new JLabel ();
    sizeLabel = new JLabel ();

    dataBox.add (filenameLabel);
    dataBox.add (urlLabel);
    dataBox.add (sizeLabel);

    JPanel buttonBox = new JPanel();
    buttonBox.setBorder (BorderFactory.createEmptyBorder (0, 10, 0, 10));

    statusLabel = new JLabel();
    buttonBox.add (statusLabel);

    stopButton = new JButton (stopIcon);
    stopButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e)
        {
        d.cancel();
        }
      });
    stopButton.setToolTipText (tooltipsBundle.getString ("download_cancel"));
    buttonBox.add (stopButton);

    folderButton = new JButton (folderIcon);
    folderButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e)
        {
        showFolder();
        }
      });
    folderButton.setToolTipText (tooltipsBundle.getString ("download_folder"));
    buttonBox.add (stopButton);
    buttonBox.add (folderButton);

    setFields();

    add (buttonBox, BorderLayout.WEST);
    add (dataBox, BorderLayout.CENTER);

    //setMaximumSize(getPreferredSize());
    }

  protected static DownloadComponent create (Download d)
    {
    DownloadComponent comp = new DownloadComponent(d);
    return comp;
    }

  protected Download getDownload() { return download; }

  /** Set the subcomponents of this component to match
      the state of the associated Download.
  */
  protected void setFields()
    {
    String displayName = download.getTarget().getDisplayName();
    String url = download.getURL();
    int size = download.getSize();
    filenameLabel.setText (displayName);
    urlLabel.setText (url);
    sizeLabel.setText (FileUtil.humanBytes (size));
    switch (download.getStatus())
      {
      case Download.DS_ONGOING:
        statusLabel.setIcon (inProgressIcon);
        stopButton.setEnabled (true);
        folderButton.setEnabled (false);
        break;
      case Download.DS_COMPLETE:
        statusLabel.setIcon (smileIcon);
        stopButton.setEnabled (false);
        DownloadTarget target = download.getTarget();
        if (target.canOpenLocation())
          folderButton.setEnabled (true);
        break;
      case Download.DS_FAILED:
        statusLabel.setIcon (errorIcon);
        stopButton.setEnabled (false);
        folderButton.setEnabled (false);
        break;
      case Download.DS_CANCELLED:
        statusLabel.setIcon (amberShriekIcon);
        stopButton.setEnabled (false);
        folderButton.setEnabled (false);
        break;
      default:
        statusLabel.setIcon (queryIcon);
        stopButton.setEnabled (false);
        folderButton.setEnabled (false);
      }
    }

  /** Handle the 'folder' button. */
  protected void showFolder()
    {
    DownloadTarget target = download.getTarget(); 
    target.openLocation();
    }

  } // End of class DownloadComponent


