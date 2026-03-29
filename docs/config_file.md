# Configuration file

By design, JGemini provides no interactive configuration, so the configuration
file is important -- particularly if you need to use client certificates
for authentication.

## Location

On Linux, JGemini will read a system-level properties file, if it exists, and
then a user configuration file. On Windows, and perhaps other platforms,
there's no defined location for a system-level configuration file, so only the
user configuration file is read.

The name of the user configuration file is `.jgemini.properties`, in the
location that Java JVM recognizes as the "home" directory. On Linux, that's
whatever is stored in the `$HOME` environment variable. On Windows, it might be
`C:\users\username`; it's hard to be sure, because this has changed between
different Windows versions.

There is a sample configuration file in the source code bundle, in the
`samples` directory. 

## List of settings

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

