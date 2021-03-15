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


public class StatusBar extends JPanel
  {
  private JLabel label;
  private  ActionListener resetListener = new ActionListener() 
    {
    public void actionPerformed(ActionEvent evt) 
      {
      Logger.log (this.getClass(), "Timer expired");
      clearStatus();
      }
    };

  public StatusBar ()
    {
    super();
    setBorder(new BevelBorder(BevelBorder.LOWERED));
    setLayout (new FlowLayout(FlowLayout.LEFT));
    label = new JLabel();
    label.setText ("OK");
    add (label);
    }

  public void setStatus (String status)
    {
    Logger.log (this.getClass(), "Set status: " + status);
    label.setText (status);
    label.repaint();
    Timer timer = new Timer (5000, resetListener);
    timer.setRepeats (false);
    timer.start();
    }

  public void clearStatus ()
    {
    Logger.log (this.getClass(), "Clearing status");
    label.setText ("OK");
    label.repaint();
    }

  }

