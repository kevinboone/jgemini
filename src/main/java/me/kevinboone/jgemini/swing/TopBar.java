/*=========================================================================
  
  JGemini

  TopBar

  The top bar contains the navigation buttons and URL combo box 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.swing;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import me.kevinboone.jgemini.base.*;

public class TopBar extends JPanel
  {
  private JComboBox urlBox;
  private MainWindow mainWindow;
  private boolean urlbarEnabled = true;

  public TopBar (MainWindow mainWindow)
    {
    super();
    Logger.in();
    this.mainWindow = mainWindow;
    setLayout (new GridBagLayout());

    urlBox = new JComboBox ();
    urlBox.setEditable (true);
    //urlBox.setMargin (new Insets (5, 5, 5, 5));
    urlBox.addActionListener(new ActionListener() 
      {
      @Override
      public void actionPerformed (ActionEvent e) 
         {
         // This is really ugly. The combobox fires _two_ events in succession
         //   when you hit 'enter' on it -- a change event and an edit event.
         // This causes two loads in quick succession, which leads to a 
         //   a pointless thread cancellation. 
         if (urlbarEnabled && e.getActionCommand().equals ("comboBoxChanged"))
           {
           Logger.log (getClass().getName(), Logger.DEBUG, "TopBar actionPerformed");
	   String url = (String)urlBox.getEditor().getItem();
	   if (url.length() > 0)
	     mainWindow.loadURI (url); 
           }
         }
      });

    java.net.URL backImgURL = getClass().getResource("/images/back.png");
    ImageIcon backIcon = new ImageIcon (backImgURL);
    JButton backButton = new JButton (backIcon);
    backButton.addActionListener((event) -> mainWindow.goBack());
    java.net.URL homeImgURL = getClass().getResource("/images/home.png");
    ImageIcon homeIcon = new ImageIcon (homeImgURL);
    JButton homeButton = new JButton (homeIcon);
    homeButton.addActionListener((event) -> mainWindow.goHome());
    java.net.URL refreshImgURL = getClass().getResource("/images/refresh.png");
    ImageIcon refreshIcon = new ImageIcon (refreshImgURL);
    JButton refreshButton = new JButton (refreshIcon);
    refreshButton.addActionListener((event) -> mainWindow.refresh());
    java.net.URL stopImgURL = getClass().getResource("/images/stop.png");
    ImageIcon stopIcon = new ImageIcon (stopImgURL);
    JButton stopButton = new JButton (stopIcon);
    stopButton.addActionListener((event) -> mainWindow.goStop());

    GridBagConstraints c = new GridBagConstraints();
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets (0, 15, 0, 15);

    add (backButton);
    add (homeButton);
    add (refreshButton);
    add (stopButton);
    add (urlBox, c);

    Logger.out();
    }

  /** Loads the URL combo box from the history file, if there is one.
      Not having a history file specified, or failing to load one that is,
      will not be reported to the user as an error. */
  public void loadHistoryFile()
    {
    Logger.in();

    urlbarEnabled = false; 

    Config config = Config.getConfig();
    String historyFile = config.getHistoryFile();

    Logger.log (getClass().getName(), Logger.DEBUG, "Loading history from: " + historyFile);

    try 
      {
      BufferedReader br = new BufferedReader 
        (new InputStreamReader (new FileInputStream (new File (historyFile))));

      String s = br.readLine();
      while (s != null)
        {
        addToHistory (s);
        s = br.readLine();
        }

      br.close();
      }
    catch (Exception e)
      {
      Logger.log (getClass().getName(), Logger.DEBUG, e.toString());
      }

    urlBox.setSelectedIndex (-1);
    urlbarEnabled = true; 
    Logger.out();
    }

  /** Saves the history file if one is specified. We'll report an error
      if there is a history file, but it can't be written. */
  private void saveHistoryFile()
    {
    Logger.in();

    Config config = Config.getConfig();

    if (!config.historyEnabled()) return;

    String historyFile = config.getHistoryFile();

    Logger.log (getClass().getName(), 
      Logger.INFO, "Saving history to " + historyFile);

    try
      {
      PrintWriter out = new PrintWriter (historyFile);
      int l = urlBox.getItemCount();
      for (int i = 0; i < l; i++)
	{
	String s = (String)urlBox.getItemAt (i);
        out.println (s);
	}
      out.close();
      }
    catch (Exception e)
      {
      JOptionPane.showMessageDialog (this, "Could not write history file: " 
         +  e.toString(), Strings.APP_NAME, JOptionPane.ERROR_MESSAGE); 
      }
    Logger.out();
    }

  private void addToHistory (String url)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "URI=" + url);

    urlbarEnabled = false;

    int l = urlBox.getItemCount();
    Config config = Config.getConfig();
    int max = config.getHistorySize();
    boolean found = false;
    for (int i = 0; i < l && !found; i++)
      {
      String s = (String)urlBox.getItemAt (i);
      if (s.equals (url)) found = true;
      }

    if (found)
      {
      Logger.log (getClass().getName(), Logger.INFO, 
        "URI already present in history");
      }
    else
      {
      Logger.log (getClass().getName(), Logger.DEBUG, 
        "adding URI to history");
      if (l >= max)
        urlBox.removeItemAt (0);
      urlBox.addItem (url);
      }

    urlbarEnabled = true;
    Logger.out();
    }

  /** showUrl Gets called from MainWindow whenever the user selects a new URL.
      This may not be the exact thing the user typed: the URL may have been
      sanitized. We will add the new URL to the combo box, provided it isn't
      already there, and provided we don't have too many entries already.
      I need to think about whether very similar URLs ought to be added. */
  public void showUrl (String url)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, "URI=" + url);
    Config config = Config.getConfig();
    addToHistory (url);
    String historyFile = config.getHistoryFile();
    if (historyFile != null)
       {
       Logger.log (getClass().getName(), Logger.INFO, 
          "There is a history file: save changes");
       saveHistoryFile();
       }

    urlBox.getEditor().setItem (url);
    Logger.out();
    }
  }
