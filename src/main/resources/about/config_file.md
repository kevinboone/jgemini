# Configuration file

By design, JGemini provides little interactive configuration, so the configuration
file is important -- particularly if you need to use client certificates
for authentication.

Although you can use any text editor to edit the configuration file, there is
a built-in editor: use the File|Settings|Edit menu. When you exit the
editor by selecting "Save" (ctrl+S), JGemini saves the contents of the
editor to the configuration file, and then reloads the settings. 

Please note that not all settings take effect immediately -- some need a
restart.

If you prefer to edit the configuration using a text editor, you can use
File|Settings|Reload to activate your changes.

However you edit the settings file, it might be a good idea to have this
page open in a separate window, so you can see what settings to change.

If you have no configuration file at all -- which will be the case the
first time you run JGemini -- the program will create a template that
you can edit. Please be aware, however, that the comments in the template
don't get saved when you save the configuration from within JGemini.
If you want to leave the comments intact, it might be better to locate
the configuration file, and edit it using a text editor.

If you use Notepad to edit the configuration on Windows, be aware that Notepad
likes to add `.txt` to all filenames, so you might need to rename the file
after editing. 

## Location of the configuration file

On Linux, JGemini will read a system-level properties file, if it exists, and
then the user configuration file. On Windows, and perhaps other platforms,
there's no defined location for a system-level configuration file, so only the
user configuration file is read.

The name of the user configuration file is `jgemini.properties`, in the directory 
`.jgemini`, in the location that the Java JVM recognizes as the "home"
directory. On Linux, that's whatever is stored in the `$HOME` environment
variable. On Windows, it's typically `C:\users\{username}`. So the configuration
file will be

    $HOME/.jgemini/jgemini.properties

on Linux, and probably

    c:\users\{username}\.jgemini\jgemini.properties

on Windows.


## List of settings

`bookmark.file`  
Set a custom bookmark filename, rather than `bookmarks.gmi` in the `.jgemini`
directory. 

`clientcert`  
Control which client certificate to send, for a specified host. There can be
any number of these entries. The format is:

    clientcert.foo.bar=/path/to/keystore.jks password 

where `foo.bar` is the hostname. The password is that of the keystore. To specify
a fallback certificate that matches all hosts, use:

    clientcert.*=/path/to/keystore.jks password 

For more information, see the page on [client certificates](client_certs.md).

`gemtext.inline.images`  
Controls whether to display images in-line in Gemtext documents: '1' for yes,
'0' for no. Note that images are always displayed in-line in Markdown
documents, and never in gophermaps. Default is 'yes'.

`history.enabled`  
Set to "1" (enabled) or "0" (disabled) to control whether the URL history
is saved to a file between sessions. If it is saved, it will be to the 
file `$HOME/.jgemini/jgemini.history`, unless you set a custom file.

`history.file`  
The full pathname of a file to which to save the URL history, if you
don't want to use the default location. The default filename is
`jgemini.history` in the `.jgemini` directory.

`history.size`  
Number of lines of URL history to save, if saving is enabled. The
default is 30.

`inline.image.width`  
Sets the width of in-line images in pixels. The height is determined automatically,
based on this width and the aspect ratio. Default: "600". 

`log.level`  
Controls debug logging. Takes values '0' (fatal errors) to '3' (copious
debugging).  Default is "1". Bear in mind that you'll only see any of this
logging if you run the program from a prompt. 

`ui.control_font`  
Sets the font for user interface elements like buttons and menus.  Default is
`Liberation Sans 20`.

`ui.document.font.size`  
Sets the base size for document fonts, in pixels. Other document elements, like
headings, will have their sizes based on this setting. If you're using a custom
stylesheet for the document display, this setting may have no effect, because
you may have overridden it. Default: 16 pixels.

`ui.user_font`  
Sets the font for text entry boxes.  Default is Default is `Liberation Sans 20`.

`url.home`  
Home page. This must be a full URL, including protocol, or an absolute pathname
for a local file. 

`urlbar.search.enabled`  
'1' to enable search from URL bar, or '0' to disable it. Default is enabled.

`urlbar.search.url`  
The URL to which the search expression is appended. Default is 
`gemini://tlgs.one/search`.

`window.w`  
Width of the main window, in pixels. Default: 1200 pixels.

`window.h`   
Height of the main window, in pixels.  Default: 900 pixels.

