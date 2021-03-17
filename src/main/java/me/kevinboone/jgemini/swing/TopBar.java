/*=========================================================================
  
  JGemini

  TopBar

  The top bar contains the navigation buttons and URL edit box 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.swing;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class TopBar extends JPanel
  {
  private JTextField urlBox;
  private HtmlViewer htmlViewer;

  public TopBar (HtmlViewer htmlViewer)
    {
    super();
    this.htmlViewer = htmlViewer;
    setLayout (new GridBagLayout());

    urlBox = new JTextField ("");
    urlBox.setMargin (new Insets (5, 5, 5, 5));
    urlBox.addActionListener(new ActionListener() 
      {
      @Override
      public void actionPerformed (ActionEvent e) 
         {
         String url = urlBox.getText();
         if (url.length() > 0)
           htmlViewer.loadURL (url); 
         }
      });

    java.net.URL backImgURL = getClass().getResource("/images/back.png");
    ImageIcon backIcon = new ImageIcon (backImgURL);
    JButton backButton = new JButton (backIcon);
    backButton.addActionListener((event) -> htmlViewer.goBack());
    java.net.URL homeImgURL = getClass().getResource("/images/home.png");
    ImageIcon homeIcon = new ImageIcon (homeImgURL);
    JButton homeButton = new JButton (homeIcon);
    homeButton.addActionListener((event) -> htmlViewer.goHome());
    java.net.URL refreshImgURL = getClass().getResource("/images/refresh.png");
    ImageIcon refreshIcon = new ImageIcon (refreshImgURL);
    JButton refreshButton = new JButton (refreshIcon);
    refreshButton.addActionListener((event) -> htmlViewer.refresh());

    GridBagConstraints c = new GridBagConstraints();
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets (0, 15, 0, 15);

    add (backButton);
    add (homeButton);
    add (refreshButton);
    add (urlBox, c);
    }

  public void showUrl (String url)
    {
    urlBox.setText (url);
    }
  }
