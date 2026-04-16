/*=========================================================================
  
  JGemini

  StatusBar 

  The usual status bar at the bottom of the window.

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import me.kevinboone.jgemini.base.*;

/** Implements the status bar at the bottom of each window, along
    with the logic to clear messages from it periodically. */ 
public class StatusBar extends JPanel implements StatusListener
  {
  private JLabel label;
  private  ActionListener resetListener = new ActionListener() 
    {
    public void actionPerformed(ActionEvent evt) 
      {
      Logger.log (getClass().getName(), Logger.DEBUG, 
        "Status bar clear timer expired");
      clearStatus();
      }
    };

  public StatusBar ()
    {
    super();
    Logger.in();
    setBorder(new BevelBorder(BevelBorder.LOWERED));
    setLayout (new FlowLayout(FlowLayout.LEFT));
    label = new JLabel();
    label.setText ("OK");
    add (label);
    Logger.out();
    }

  public void setStatus (String status)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, 
      "Set status: " + status);
    label.setText (status);
    label.repaint();
    Timer timer = new Timer (5000, resetListener);
    timer.setRepeats (false);
    timer.start();
    Logger.out();
    }

  @Override
  public void writeStatus (String status)
    {
    Logger.in();
    setStatus (status);
    Logger.out();
    }

  public void clearStatus ()
    {
    Logger.in();
    label.setText ("OK");
    label.repaint();
    Logger.out();
    }

  }

