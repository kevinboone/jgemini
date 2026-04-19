/*=========================================================================
  
  JGemini

  FeedManagerDialog 

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

/** FeedManagerDialog provides the interface to the FeedManager, and 
    shows the progress of feed aggregation. 
*/
public class FeedManagerDialog extends JFrame 
         implements FeedManagerStatusListener
  {
  private final static ResourceBundle captionsBundle = 
      ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Captions");
  private final static ResourceBundle dialogsBundle = 
      ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");
  private final static String caption = captionsBundle.getString ("feed_manager");
  private FeedHandler feedHandler = DefaultFeedHandler.getInstance();

  private static FeedManagerDialog instance = null;
  private JScrollPane scrollPane;
  private JList<String> list;
  private DefaultListModel model;

  /*=========================================================================
    
    Constructor 

  =========================================================================*/
  private FeedManagerDialog()
    {
    Logger.log (getClass().getName(), Logger.DEBUG, 
         "Instantiating DownloadDialog");

    FeedManagerDialog self = this;
    setTitle (caption);

    feedHandler.addFeedManagerStatusListener (this);

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
        //   FeedManager, else this window won't
        //   ever be properly finalized, and this will stop
        //   JGemini shutting down.
        feedHandler.removeFeedManagerStatusListener (self);
	dispose();
	instance = null;
	}
      });

    scrollPane = new JScrollPane (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    model = new DefaultListModel();
    list = new JList<String>(model); 
      
    scrollPane.setViewportView (list);

    JPanel buttonBox = new JPanel();

    JButton helpButton = JGeminiDialog.createButton ("feed_manager_help"); 
    helpButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e)
        {
        handleDocs();
        }
      });
    buttonBox.add (helpButton);

    JButton closeButton = JGeminiDialog.createButton ("feed_manager_close"); 
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

    JButton updateButton = JGeminiDialog.createButton ("feed_manager_update"); 
    updateButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e)
        {
        updateFeedManager();
        }
      });
    buttonBox.add (updateButton);

    JButton stopButton = JGeminiDialog.createButton ("feed_manager_stop"); 
    stopButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e)
        {
        stopFeedManager();
        }
      });
    buttonBox.add (stopButton);

    setPreferredSize (new Dimension (700, 400)); // TODO
    setLocationByPlatform (true); // Let desktop position me
    add (scrollPane, BorderLayout.CENTER);
    add (buttonBox, BorderLayout.SOUTH);


    pack();
    setResizable (true); 
    }

  /*=========================================================================
    
    cancelled 

  =========================================================================*/
  @Override
  public void cancelled()
    {
    }

  /*=========================================================================
    
    finished 

  =========================================================================*/
  @Override
  public void finished()
    {
    }

  /*=========================================================================
    
    getInstance 

  =========================================================================*/
  public static FeedManagerDialog getInstance()
    {
    if (instance == null)
      instance = new FeedManagerDialog();

    return instance;
    }

  /*=========================================================================
    
    stopFeedManager 

  =========================================================================*/
  private void stopFeedManager()
    {
    if (feedHandler.isRunning())
      {
      feedHandler.cancelUpdate();
      }
    else
      {
      DialogHelper.infoDialog (this, null, 
        dialogsBundle.getString ("feed_manager_not_running"));
      }
    }

  /*=========================================================================
    
    updateFeedManager 

  =========================================================================*/
  private void updateFeedManager()
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
    
    handleDocs 

  =========================================================================*/
  private void handleDocs()
    {
    MainWindow.newWindow (Constants.DOC_FEED_MANAGER_DIALOG,
      "Feed Manager dialog"); // Caption will not show when loaded
    }

  /*=========================================================================
    
    newMessage 

  =========================================================================*/
  @Override
  public void newMessage (String s)
    {
    model.addElement (s);
    SwingUtilities.invokeLater(() -> 
      {
      JScrollBar bar = scrollPane.getVerticalScrollBar();
      bar.setValue(bar.getMaximum());
      });
    }

  /*=========================================================================
    
    started 

  =========================================================================*/
  @Override
  public void started()
    {
    model.removeAllElements();
    }

  } // End of FeedManagerDialog

