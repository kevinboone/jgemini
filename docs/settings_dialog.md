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

## Media tab

Here you can specify the application that will receive the media data stream,
when you tell JGemini to stream content rather than downloading it. You should
include any command-line arguments you need, to make the player read from its
standard input, rather than from a file or URL. On both `vlc` and `ffplay`, use
the `-` command-line switch for this.

On Windows, you'll almost certainly need to give the full path of the `.exe`
file.

The button "Clear handling defaults" resets any choice you may previously
have made about how specific types of content should be handled.

## Feeds tab

Use this tab to change the behaviour of the feed aggregator (see 
[Feeds and subscriptions](feeds_and_subscriptions.md) for more information.

For more information on media support in JGemini, see the 
[Media and streaming](media_and_streaming.md) page.

[Documentation index](index.md)

