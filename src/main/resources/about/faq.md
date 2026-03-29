# JGemini FAQ

_How do I install JGemini?_

You don't need to do anything in particular. Just copy the program's JAR file
`jgemini-2.0.jar` to any convenient directory, and run it using `java -jar
jgemini-2.0.jar`.

For more information, see the [page on installation](installing_and_running.md).

_How do I set the home page?_

Add an entry `url.home` to the configuration file -- either the system-level
configuration file or the user configuration file

_Why does JGemini not store anything from the user's session?_

People who use Gemini and similar systems are often quite concerned about
matters related to privacy. Storing browsing history, etc., is a potential
privacy hazard. Although there are ways to mitigate this hazard, I thought
it would be easier, and safer, if JGemini simply didn't store anything at all,
unless expressly configured by the user.

I have to point out, however, that files that JGemini downloads, other than
documents, might be stored in a temporary directory, if JGemini has to hand
them off to the platform to be viewed. Images of types that JGemini supports
internally (JPEG, PNG, GIF) are not saved anywhere, except in memory.

URL history is not saved, unless you define `history.file` in the 
configuration file.

_How do I store bookmarks?_

Sorry, you can't. See above. 

You can create a `.gmi` file containing only bookmarks, stored some place in
your home directory or on a server of your choice. Then you can, if you wish,
set it as the home page. This is how I use JGemini.

_How do I open a local .gmi file?_

From the command line, just run

    java -jar /path/to/jgemini-1.0.jar [filename]

If you've followed the desktop setup steps outlined above, you should
just be able to click the `.gmi` file in a file manager.

_What Java version do I need?_

Any Java 11 or later should be fine. Because Gemini is based on TLS, 
you might find old, or cryptographically limited, JVMs don't work
properly. 

_The screen font is too small. What do I do?_

Edit the base font size in the either of the configuration files.  If you don't
have a configuration file, use the one in the `samples` directory as a
template. You can increase or decrease the base font size at runtime
using ctrl-[ and ctrl-]. 

_Does JGemini handle images?_

Yes. With Markdown documents, images are in-lined into the text by 
default. With Gemtext documents, in-lining behaviour is controlled by
the configuration file property `gemtext.inline.images`, which takes
values `1` (yes) or `0` (no).

If the images are in-lined, they are all displayed with the same width,
controlled by the property `inline.image.width`. This is because Gemtext
provides no way to set a particular image size, and it often isn't helpful to
let the image display at its full size.

_How do I see an image at full size?_

Right-click the caption or URI under the image, and select "Open" or "Open in
new window" from the menu. This will display the image without size
constraints.

_How do I download an image?_

Right-click the caption or URI under the image, and select "Download".  The
File|Save menu won't work on a window that contains only an image, for tedious
technical reasons.

_Why can't I see a progress indicator when downloading a large image?_

The Gemini protocol does not provide any indication of the size of the
download, so there's no way for JGemini to know how close the download is to
being completed. JGemini does display the amount of data transferred so
far in the status bar, but only every 16kB -- and many Gemini/Gopher/etc
files are not even that large.

_What happens if I follow a non-Gemini link in a Gemtext page?_

If it's a link to a protocol that Java understands, like `http:`, then JGemini
will pass the link to the desktop, while will usually do something useful. If
it's not a recognized protocol, like `telnet:`, nothing much will happen. This
is a limitation in Java's HTML renderer -- it's not easy to fix in JGemini,
without reimplementing the whole renderer. 

_What document types can JGemini display?_

Gemtext, plain text, Markdown, and gophermaps. In a client-server interaction,
the file type is determined by the response, with protocols that provide that
information. For local files, it's just based on the filename extension. The
same is true for protocols like Gopher, which don't give detailed file type
information.

_Can I see the server's certificate?_

Not easily, and JGemini doesn't check anything in the certificate, anyway. If
there ever comes a time when Gemini servers start using verifiable certificates
as a matter of course, I might change that.

_How do I send a client certificate with my requests?_

Please see the separate [page about client certificates](client_certs.md)

_Why aren't emojis displayed properly?_

You need the appropriate fonts and configuration -- not all fonts contain
glyphs for the emoji characters. `Segoe UI Emoji` is a workable choice. 

Note that Java Swing is pretty fussy about what fonts it's prepared to use.
However, once there is a suitable font in place, you don't have to tell 
JGemini to use it -- the JVM is smart enough to find a font that contains
the relevant glyphs if the usual font does not have them.

_How do I change the fonts?_

You'll have to provide a custom stylesheet in CSS format. See README.styling.

_How do I find out what fonts are available?_
 
    java -jar jgemini-0.1.jar -Djgemini.dumpfonts 

See the separate [page on style](styling.md) for details.

_How do I go to the top-level directory of a Gemini capsule?_

The Lagrange client displays a caption bar which you can click to navigate to
the root level of a Gemini capsule. This is convenient, but the bar takes up a
lot of screen space. Instead, JGemini provides a menu command Go|Root which has
the same effect. JGemini tries to do as Lagrange does, and offer a reasonable
guess at what the "root" path amounts to. For example, in many cases it will
just be `/`. However, in a URI that contains a username (`~fred`), then it's
more useful to interpret the top level of the user's directory as the root,
rather than that of the server as a whole. 

_Why don't the Home/End keys work?_

Some keystrokes only work when the input focus is in the document viewer window
(and not, for example, in the URL bar). Just click the document.

_How do I disable the use of the URL bar for searching, rather than entering URLs?_

In the configuration file, set `urlbar.search.enabled=0`.

_How do I enable full debug logging?_

In the [configuration file](config_file.md), add the setting

    log.level=3

_Where does debug logging go?_

It goes to the standard error stream. You'll probably have to run JGemini from
a command prompt to see and collect it.

[Documentation index](index.md)


