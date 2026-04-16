/*=========================================================================
  
  JGemini

  FilteredComboBox

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
  <p>
  This is my attempt at implementing a filtered combo box, that is, one
  where the list changes so that it only shows entries that match what
  the user is typing. It's such a common thing, it blows my mind that
  Swing doesn't have one.
  </p>
  <p>
  I tried and tried to modify the behaviour of the stock combo box to
  do the right thing, but I got nowhere. So in the end I decided to 
  implement a new control. Sadly, it's not a subclass of JComboBox,
  because I just couldn't make that work. However, it has many of
  the same methods on JComboBox that a client class may call.
  </p>
  <p>
  I've tried to make this custom combo box behave like the original.
  Down arrow expands the drop-down list. Escape closes it, whether focus is in
  the text entry box or the list. The down-arrow button opens and closes the
  drop-down list. Losing focus also closes it, as does making a selection. When
  focus is in the drop-down list, you can use the arrow keys to change the
  highlighted entry, and Enter to select.
  </p>
  <p>
  The drop-down list -- a JList -- is embedded in a Swing Popup object.
  Interestingly, we can hide and re-create the Popup with the same contents, 
  that is, the same Jlist each time. So, although we have to change the 
  contents of list repeatedly, we don't have to break it down and
  recreate it. 
  </p>
  <p>
  The items added by the client class are stored in a Vector&lt;String&gt;.
  From this we derive a DefaultListModel that contains only the strings
  that match what the user is typing.
  </p>
  <p>
  Note that the match is CASE SENSITIVE by default. This is easy to
  change.
  </p>
  <p>
  However, it's highly likely that there are features of the real combo box
  that I haven't replicated. It's also highly likely that there are bugs
  in my implementation.
  </p>
*/
public class FilteredComboBox extends JPanel
  {
  // The user's text input
  private JTextField input;
  // A listener that client classes add, to get notification
  //   when our combo box makes a selection
  private ActionListener actionListener = null;
  // `items` is the full list of contents of the combo box, will
  // may differ from what's being displayed, as the display
  // is filtered.
  private Vector<String> items = new Vector<String>();
  private int selectedIndex = -1;
  private Popup popup = null;
  private DefaultListModel<String> model;
  private JPanel listPanel;
  private JList<String> l; // The actual list of displayed items 
  // Some methods set `suppressListeners` to prevent accidental
  //   dispersal of events. It took a lot of trial-and-error to
  //   get this right.
  private boolean suppressListeners = false;

/*=========================================================================
  
  constructor

=========================================================================*/
  public FilteredComboBox()
    {
    super();

    FilteredComboBox self = this;
    setLayout (new BorderLayout());

/* *** Definition of the text input field *** */

    input = new JTextField ();
    input.getDocument().addDocumentListener (new DocumentListener()
      {
      // We need to watch for changes, so we can refresh the drop-down
      //   with new, filtered values
      public void changedUpdate (DocumentEvent e) 
        { if (!suppressListeners) textChanged(); }
      public void removeUpdate (DocumentEvent e) 
        { if (!suppressListeners) textChanged(); }
      public void insertUpdate (DocumentEvent e)
        { if (!suppressListeners) textChanged(); }
      });

    input.addActionListener (new ActionListener()
      {
      // We need to watch for hitting Enter in the 
      //   text input box, because this notifies
      //   client classes that there has been
      //   an input
      public void actionPerformed (ActionEvent e) 
        { 
        destroyPopup(); 
        if (!suppressListeners) notifyListeners(); 
        }
      });

    input.addKeyListener (new KeyListener()
      {
      // We need to watch for the Escape and Down keys,
      //   because they have particular actions.
      public void keyTyped (KeyEvent e){}
      public void keyReleased (KeyEvent e){}
      public void keyPressed (KeyEvent e)
        {
        if (!suppressListeners)
          {
	  int k = e.getKeyCode();
	  if (k == KeyEvent.VK_ESCAPE) destroyPopup();
	  else if (k == KeyEvent.VK_DOWN) 
	    {
            // Down key raises the popup, and sets it to be
            //   focused, so keys work.
            ensurePopupVisible();
	    l.grabFocus();
	    }
          }
        }
      });

    /* It's convenient if setting focus to the text box highlights
       it all. That way, the user can just type to start a new
       text entry, but can also edit using the arrow keys. */
    input.addFocusListener (new FocusListener()
      {
      public void focusGained (FocusEvent e)
        {
        if (!suppressListeners)
          input.selectAll();
        }
      public void focusLost (FocusEvent e) 
        { 
        if (e.getOppositeComponent() != l)
          destroyPopup();
        }
      });

    /* OK, that's the text input box done. */
    add (input, BorderLayout.CENTER);

/* *** Definition of the drop-down button *** */

    java.net.URL iconUrl = getClass().getResource("/images/downarrow.png");
    ImageIcon icon = new ImageIcon (iconUrl);

    JButton button = new JButton (icon);
    add (button, BorderLayout.EAST);
    button.addActionListener (new ActionListener()
      {
      public void actionPerformed (ActionEvent e)
        {
        // We can only figure out whether to show or hide the popup
        //   by looking at whether it's currently visible.
        if (popup == null)
          {
          ensurePopupVisible();
	  l.grabFocus();
          }
        else
          {
          destroyPopup();
          }
        }
      });

/* *** Definition of the drop-down list itself *** */

    // We'll use a new model for this list, which we can
    //   manipulate as the user types text.
    model = new DefaultListModel<String>();
    l = new JList<>(model);
    l.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
    l.setRequestFocusEnabled (true); // If we don't do this, never get focus

    // JList doesn't seem to have a specific listener for mouse
    //   clicks, that's separate from selection changes. So
    //   we have to use our own.
    l.addMouseListener (new MouseInputAdapter()
      {
      @Override
      public void mouseClicked (MouseEvent e)
        {
	if (!suppressListeners)
	  {
	  String s = l.getSelectedValue();
	  input.setText (s);
	  setSelectedIndexFromString (s);
	  destroyPopup();
	  notifyListeners();
	  }
        }
      });

    // We'll also have to listen for the enter key
    l.addKeyListener (new KeyListener()
      {
      public void keyTyped (KeyEvent e){}
      public void keyReleased (KeyEvent e){}
      public void keyPressed (KeyEvent e)
        {
        switch (e.getKeyCode())
          {
          case KeyEvent.VK_ENTER:
	    if (!suppressListeners)
	      {
	      String s = l.getSelectedValue();
	      input.setText (s);
	      setSelectedIndexFromString (s);
	      destroyPopup();
	      notifyListeners();
	      }
            break;
          }
        }
      });

    // We need to be able to close this popup when the list loses
    //   focus. Otherwise the user will have to click the button
    //   every time to close it.
    l.addFocusListener (new FocusListener()
      {
      public void focusGained (FocusEvent e) { }
      public void focusLost (FocusEvent e) 
        { 
        // Don't destroy the popup if we're losing focus to the
        //   drop-down button. If we're losing focus to the
        //   button, it's because the popup is visible, and we've
        //   just clicked the button to close it. If we close it
        //   here, the button will see it's closed, and open it
        //   again. Sigh. Dontcha just love Swing?
        if (e.getOppositeComponent() != button)
          destroyPopup();
        }
      });

    // We also want the Escape key to close the drop-down list
    l.addKeyListener (new KeyListener()
      {
      public void keyTyped (KeyEvent e){}
      public void keyReleased (KeyEvent e){}
      public void keyPressed (KeyEvent e)
        {
        if (!suppressListeners)
          {
	  int k = e.getKeyCode();
	  if (k == KeyEvent.VK_ESCAPE) destroyPopup();
          }
        }
      });

    // Finally, we'll wrap the JList in a scroll pane, because it
    //   doesn't have its own scroll support.
    listPanel = new JPanel();
    listPanel.setLayout (new BorderLayout());
    JScrollPane sp = new JScrollPane (l, 
       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    listPanel.add (sp, BorderLayout.CENTER);
    
    listPanel.show();
    }

/*=========================================================================
  
  createPopup 

  Whenever we create the popup, we have to repopulate the list box 
  model, because we'll be creating it in response to some particular
  input in the text input box.

  The Swing docs say that you shouldn't attempt to move or resize the
  popup, but should create a new one every time something changes.

=========================================================================*/
  private void createPopup()
    {
    if (popup != null) return;

    populateModel (input.getText());

    PopupFactory pf = PopupFactory.getSharedInstance();
    Point pt = new Point (input.getLocation());
    int h = getHeight();
    pt.y += h + 1;
    SwingUtilities.convertPointToScreen (pt, input);
    Dimension d = input.getSize();

    popup = pf.getPopup (this, listPanel, pt.x, pt.y);
    listPanel.setPreferredSize (new Dimension(d.width, 10 * h));
    popup.show();
    }

/*=========================================================================
  
  destroyPopup 

=========================================================================*/
  private void destroyPopup()
    {
    if (popup == null) return;
    popup.hide();
    popup = null;
    }

/*=========================================================================
  
  ensurePopupVisisble 

=========================================================================*/
  private void ensurePopupVisible()
    {
    //if (popup == null && input.hasFocus()) 
    if (popup == null) 
      createPopup();
    }

/*=========================================================================
  
  getItem

=========================================================================*/
  public String getItem()
    {
    return input.getText();
    }

/*=========================================================================
  
  addActionListener 

=========================================================================*/
  public void addActionListener (ActionListener l)
    {
    // We should really maintain a list of listeners, but I very rarely
    //   add more than one.
    actionListener = l;
    }

/*=========================================================================
  
  addItem

=========================================================================*/
  public void addItem (String s)
    {
    items.add (s);
    }

/*=========================================================================
  
  getItemCount 

=========================================================================*/
  public int getItemCount()
    {
    return items.size();
    }

/*=========================================================================
  
  getItemAt 

=========================================================================*/
  public String getItemAt (int n)
    {
    return items.elementAt (n);
    }

/*=========================================================================
  
  populateModel 

  Derive from the main `items` Vector the contents of the DefaultListModel
  that we'll show to the user. This method will be called for each
  user keystroke, so it ought not to be too sluggish. I guess it will be,
  though, when there are many items in the list.

=========================================================================*/
  private void populateModel (String filter)
    {
    boolean oldSuppressListeners = suppressListeners;
    suppressListeners = true;
    boolean focus = input.hasFocus();
    model.removeAllElements(); 
    int n = items.size();
    for (int i = 0; i < n; i++)
      {
      String s = items.elementAt (i);
      if (s.contains (filter) || !focus) 
        model.addElement (s);
      // Case-insensitive version
      //if (s.toLowerCase().contains (filter.toLowerCase()) || !focus)
      //  model.addElement (s);
      }
    suppressListeners = oldSuppressListeners;
    }

/*=========================================================================
  
  removeAllItems

=========================================================================*/
  public void removeAllItems()
    {
    items = new Vector<String>(); 
    }

/*=========================================================================
  
  removeItemAt 

=========================================================================*/
  public void removeItemAt (int n)
    {
    items.removeElementAt (n);
    }

/*=========================================================================
  
  setEditable 

=========================================================================*/
  public void setEditable (boolean f)
    {
    }

/*=========================================================================
  
  setItem

=========================================================================*/
  public void setItem (String s)
    {
    boolean oldSuppressListeners = suppressListeners;
    suppressListeners = true;
    input.setText (s); // TODO 
    suppressListeners = oldSuppressListeners;
    }

/*=========================================================================
  
  setSelectedIndex

=========================================================================*/
  public void setSelectedIndex (int n)
    {
    selectedIndex = n; // TODO
    }

/*=========================================================================
  
  setSelectedIndexFromString

  Set the main selected index from the 

=========================================================================*/
  private void setSelectedIndexFromString (String s)
    { 
    // TODO -- not needed yet
    }

/*=========================================================================
  
  textChanged

=========================================================================*/
  private void textChanged()
    { 
    ensurePopupVisible();
    populateModel (input.getText());
    }

/*=========================================================================
  
  textChanged

=========================================================================*/
  private void notifyListeners()
    {
    if (actionListener != null)
      actionListener.actionPerformed 
        (new ActionEvent (this, 0, "comboBoxChanged"));
    }

  }

