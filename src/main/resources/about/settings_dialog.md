# Settings dialog

You can change most aspects of JGemini's operation using the Settings dialog.
However, there are some settings that can't be modified this way, and
must be changed using the [settings editor](edit_settings_dialog.md) 
or by directly editing the [configuration file](config_file.md).

In particular, the Settings dialog can't change:

* the locations of files on disk, such as the bookmarks file, and
* some rarely-changed user interface settings, like the number
  of bookmarks to show on the bookmarks menu.

## Appearance tab

This tab contains general settings related to the appearance of the application.

If you check the box _Remove emojis from bookmarks_, JGemini will remove
emoji characters from page titles before storing them in the bookmarks file. It
will also remove these characters from the main user interface, especially the
_Bookmarks_ tab and the bookmark editor. This is to prevent emoji characters
disturbing the user interface, on systems where it isn't possible to 
[support emojis](emoji_support.md).  

Please note that changing this setting does not remove emojis that have already
been stored, although it removes them from the user interface. 

## Theme tab 

The theme tab allow you to select one of the built-in themes, or provide
a custom CSS file to make your own. If you select "custom" from the
theme list, you must also select a CSS file. If you pick a CSS file
using the file picker, JGemini will automatically select the "custom"
theme in the list.

For more information on custom themes, see the page on [styling](styling.md).

## Home tab

From the home tab, you can enter a home page, or select the current
page to be the home page.

## History tab

From this tab you can select whether URL history is to be saved between
sessions, and how many history items to save. You can also clear any existing
history. 

## Images tab

The settings on this tab control whether, and how, in-line images are rendered
in the document viewer. 

## Search tab

This tab allows you to specify whether you want to be able to carry out
searches directly from the URL bar. You'll need to enter a search provider, if
you want to search this way.

[Documentation index](index.md)

