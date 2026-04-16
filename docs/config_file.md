# Configuration file

> *Note*  
> You can manage most of JGemini's configuration using the main user interface,
> so there's not much need to hack on the configuration file manually.
> However, it's still exposed and documented, for more complex usage scenarios,
> and for editing settings that are deliberately not exposed to the user.  Be
> aware that careless hacking on the configuration file can break things in
> non-obvious ways.

Although you can use any text editor to edit the configuration file, there is a
built-in editor: use the _File|Settings|Edit_ menu to see it. When you exit the
editor by selecting "Save" (ctrl+S), JGemini saves the contents of the editor
to the configuration file, and then reloads the settings. 

Please note that not all settings take effect immediately -- some need a
restart.

If you prefer to edit the configuration using a text editor, you can use
_File|Settings|Reload_ to activate your changes.

However you edit the settings file, it might be a good idea to have this page
of the documentation open in a separate window, so you can see what settings to
change.

If you use Notepad to edit the configuration on Windows, be aware that Notepad
likes to add `.txt` to all filenames, so you might need to rename the file
after editing. 

## Location of the configuration file

On Linux, JGemini will read a system-level properties file, if it exists, and
then the user configuration file. On Windows, and perhaps other platforms,
there's no defined location for a system-level configuration file, so only the
user configuration file is read.

The name of the user configuration file is `jgemini.properties`, in the
directory `.jgemini`, in the location that the Java JVM recognizes as the
"home" directory. On Linux, that's whatever is stored in the `$HOME`
environment variable. On Windows, it's typically `C:\users\{username}`. So the
configuration file will usually be

    $HOME/.jgemini/jgemini.properties

on Linux, and probably

    c:\users\{username}\.jgemini\jgemini.properties

on Windows.


## List of settings

Please note that 'binary' configuration values can take any of the values
'1', 'yes', 'on', '0', 'no', 'off'.

`bookmark.file`  
Set a custom bookmark filename, rather than `bookmarks.gmi` in the `.jgemini`
directory. 

`clientcert`  
Control which client certificate to send, for a specified identity. There can be
any number of these entries. The format is:

    clientcert.{ident}=/path/to/keystore.jks password 

See also the entry for `ident`.

For more information, see the page on [client certificates](client_certs.md).

`emoji.strip.bookmarks`  
If set to 'yes', JGemini strips Unicode emojis from new bookmarks before
storing them. It also prevents emojis being displayed in the Bookmarks menu in
the main user interface. 

The default is to leave emojis as they are, which might require the
installation of additional fonts. 

`gemtext.inline.images`  
Controls whether to display images in-line in Gemtext documents.  Note that
images are always displayed in-line in Markdown documents, and never in
gophermaps. The default is  'yes', to display in-line images. 

`handler`  
Specifies how to handle content that JGemini does not natively support.
The `handler.` prefix is followed by a MIME type, such as `handler.audio/mpeg`.
The value is a number between -1 and 3, with the following meanings.

~~~
-1 prompt the user every time 
0  save in the downloads directory without prompting
1  prompt for a filename as save in that file
2  stream the content out to the stream helper
3  save a temporary file, and hand it off to the desktop
~~~

`history.enabled`  
Determines whether the URL history is saved to a file between sessions. If it
is saved, it will be to the file `$HOME/.jgemini/jgemini.history`, unless you
set a custom file.  The default is 'no' -- history is not saved.

`history.file`  
The full pathname of a file to which to save the URL history, if you
don't want to use the default location. The default filename is
`jgemini.history` in the `.jgemini` directory.

`history.size`  
Number of lines of URL history to save, if saving is enabled. The
default is 30.

`ident.{hostname}`  
Assign an identity name to a particular hostname. To use the same identity on
all hosts, use `ident.any`. See the entry for `clientcert` to see how to assign
an identity to a client certificate.

`inline.image.width`  
Sets the width of in-line images in pixels. The height is determined automatically,
based on this width and the aspect ratio. Default: "600". 

`log.level`  
Controls debug logging. Takes values '0' (fatal errors) to '3' (copious
debugging).  Default is "1". Bear in mind that you'll only see any of this
logging if you run the program from a prompt. 

`stream.player`  
Command or path of the application that will receive the data stream, when you
tell JGemini to stream a file, rather than download it. This setting is mostly
used for media playback. On Windows, you'll almost certainly have to give the
full pathname. 

The application needs to be capable or receiving data on its standard input
channel, and you need to give whatever command-line arguments will make it do
that. Both `vlc -` and `ffplay -` work on Linux; `vlc.exe` works on Windows,
but please be aware that some versions don't accept data on standard input.

The application chosen really needs to have a graphical user interface.  It
will work if it doesn't, but you'll have no way to control it once playback has
started.

`ui.control_font`  
Sets the font for user interface elements like buttons and menus.  Default is
`Sans 20; Emoji 20`. You can set multiple fonts like this:

    ui.control_font=Sans 20; Segoe UI Emoji 20

The user interface will try to use the first font if it can, that is, if the
font contains the necessary characters. If it can't, it will try other fonts in
the list.

`ui.document.font.size`  
Sets the base size for document fonts, in pixels. Other document elements, like
headings, will have their sizes based on this setting. If you're using a custom
stylesheet for the document display, this setting may have no effect, because
you may have overridden it. Default: 16 pixels.

`ui.icon.size`  
The size of toolbar icons in pixels. You will need to restart JGemini
for changes to this setting to take effect. Default: 24 pixels.

`ui.icons.mono`  
Sets whether toolbar icons are shown in monochrome. Default: false.

`ui.user_font`  
Sets the font for text entry boxes.  Default is `Sans 20; Emoji 20`. For the
format, see `ui.control_font`.

`url.home`  
Home page. This must be a full URL, including protocol, or an absolute pathname
for a local file. The default is to show an internal welcome page.

`urlbar.search.enabled`  
Default is 'yes' -- search from the URL bar is enabled.

`urlbar.search.url`  
The URL to which the search expression is appended. Default is 
`gemini://tlgs.one/search`.

`window.w`  
Width of the main window, in pixels. Default: 1200 pixels. Please be aware that
this will be overwritten if you resize the window at runtime.

`window.h`   
Height of the main window, in pixels.  Default: 900 pixels.  Please be aware
that this will be overwritten if you resize the window at runtime.

