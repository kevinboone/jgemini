# JGemini FAQ

## Installation

_How do I install JGemini?_

You don't need to do anything in particular. Just copy the program's JAR file
`jgemini-2.0.jar` to any convenient directory, and run it using `java -jar
jgemini-2.0.jar`. Or, on Windows, just double-click the JAR file in the
file manager.

You may prefer a more integrated installation; for more information, see the
[page on installation](installing_and_running.md).

_What Java version do I need?_

Any Java 11 or later should be fine. Because the Gemini protocol is based on
TLS, you might find old, or cryptographically limited, JVMs don't work
properly. 

_Why can't JGemini provide its own emoji fonts?_

Installing an emoji font is a slight nuisance, but it should be a one-time
task. There are redistributable emoji fonts that JGemini could bundle, but
loading a font explicitly doesn't make the document viewer use it. 

JGemini's document viewer is based on Java's built-in HTML renderer. Converting
all incoming documents to HTML is flexible, and allows for future expansion.
After all, pretty much anything that can be viewed can be converted to HTML.
Unfortunately, the built-in document viewer won't use anything except platform
fonts, so bundling emoji fonts wouldn't be helpful. 

## Basic features

_How do I set the home page?_

If the relevant page is in the viewer, select _File|Set as Home Page_ from the
menu. Alternatively, add an entry `url.home` to the [configuration
file](config_file.md). 

_Why does JGemini not store my URL history between sessions?_

People who use Gemini and similar systems are often quite concerned about
matters related to privacy. Storing browsing history, etc., is a potential
privacy hazard. Although there are ways to mitigate this hazard, I thought
it would be easier, and safer, if JGemini simply didn't store it at all,
unless expressly configured by the user.

To allow JGemini to store URL history, add `history.enabled=1` to the 
[configuration file](config_file.md). 

Please note that there is no way to make this change using the JGemini
user interface -- it's deliberately been left as something slightly
awkward, so you can't do it by accident.

_How do I open a local .gmi file?_

From the command line, just run

    java -jar /path/to/jgemini-2.0.jar [filename]

Depending on how you installed JGemini, you might simply be able to click on
a `.gmi` file in a file manager.

_How does JGemini handle images?_

With Markdown documents, images are in-lined into the text by default. With
Gemtext documents, in-lining behaviour is controlled by the configuration file
property `gemtext.inline.images`, which takes values `yes` or `no`.

If the images are in-lined, they are all displayed with the same width,
controlled by the property `inline.image.width`. This is because Gemtext
provides no way to set a particular image size, and it often isn't helpful to
let the image display at its full size.

_How do I see an image at full size?_

Right-click the caption or URI under the image, and select _Open_ 
or _Open in new window_ from the menu. This will display the image without size
constraints.

_How do I download an image?_

Right-click the caption or URI under the image, or the link to the image, and
select _Download_.  The _File|Save_ menu won't work on a window that contains
only an image, for tedious technical reasons.

_Why can't I see a progress indicator when downloading a large document or image?_

None of the protocols JGemini supports provide any indication of the size of the
file, so there's no way for JGemini to know how close the download is to
being completed. JGemini does display the amount of data transferred so
far in the status bar, but only every 16kB -- and many Gemini/Gopher/etc
files are not even that large.

_What happens if I follow a non-Gemini link in a Gemtext page?_

If it's a link to a protocol that Java understands, like `http:`, then JGemini
will pass the link to the desktop, which will usually do something useful. If
it's not a recognized protocol, like `telnet:`, nothing much will happen. This
is a limitation in Java's HTML renderer -- it's not easy to fix in JGemini,
without reimplementing the whole renderer. 

_What document types can JGemini display?_

Gemtext, plain text, Markdown, and gophermaps. In a client-server interaction,
the file type is determined by the response, with protocols that provide that
information. For local files, it's just based on the filename extension. The
same is true for protocols like Gopher, which don't give detailed file type
information.

_How do I go to the top-level directory of a Gemini capsule?_

Use the Go|Root menu command.  JGemini tries to do the same as the Lagrange
browser, and use a reasonable guess at what the "root" path amounts to. In many
cases it will just be `/`. However, in a URI that contains a username
(`~fred`), then it's more useful to interpret the top level of the user's
directory as the root, rather than that of the service as a whole. 

_Why don't the Home/End keys work?_

Some keystrokes only work when the input focus is in the document viewer window
(and not, for example, in the URL bar). Just click the document.

_How do I disable the use of the URL bar for searching?_

In the configuration file, set `urlbar.search.enabled=0`.

## Configuration

_How do I change the fonts?_

You'll have to provide a custom stylesheet in CSS format. See the
page on [styling](styling.md). 

_How do I find out what fonts are available?_
 
    java -jar jgemini-2.0.jar -Djgemini.dumpfonts 

_The screen font is too small. What should I do?_

Edit the base font size in the configuration file.  You can increase or
decrease the base font size at runtime using ctrl-[ and ctrl-]. 

## Bookmarks

_In the "Bookmark|Edit feature, in what format are bookmarks stored?_

Bookmarks are stored in the local bookmarks file as links in a Gemtext document. 
Every time you bookmark a page, JGemini just appends a new link for the
bookmark. You can edit the bookmarks however you wish, so long as it remains
a valid Gemtext document, and you don't mind having new links added to the
end. 

_Where are bookmarks stored?_

Unless you change this in the configuration file, they are stored in a single
file in the `$HOME/.jgemeni` directory called `bookmarks.gmi`.

## TLS and security

_Can I see the server's certificate?_

Not easily, and JGemini doesn't check anything in the certificate, anyway. If
there ever comes a time when Gemini servers start using verifiable certificates
as a matter of course, this feature might be added. 

_How do I send a client certificate with my requests?_

Please see the page on [identity](identity.md) to get started. 

_What does the error message "Badly formatted directory" mean in the_
New identity _dialog box?_

You've entered an invalid name in the _Name for certificate_ field.  A
"directory" here refers to a _user_ directory, and has nothing to do with files
or folders. The name that goes into the client certificate has strict
formatting rules but, for Gemini purposes, a name of the form "CN=My name" will
almost always be sufficient.

_How do I import an identity from another browser like Lagrange?_

At present, this is a rather fiddly, manual process, because most applications
that handle TLS certificates store them in the "PEM" format, while JGemini
requires PKCS12 (or Java's proprietary 'JKS' format).

See the section "Importing a certificate from another application" in the
[client certificates](client_certs.md) page.

_Does JGemini expose keystore passwords?_

Yes. This is a clear security limitation, but it's no worse -- and perhaps
slightly better -- than those clients which don't use any password protection
on certificates at all.

## Emoji support 

_Why aren't emojis displayed properly in the document viewer?_

You need the appropriate fonts and configuration -- not all fonts contain
glyphs for the emoji characters. `Segoe UI Emoji` is a workable choice. 

Note that Java Swing is pretty fussy about what fonts it's prepared to use.
However, once there is a suitable font in place, you don't have to tell JGemini
to use it in the document viewer -- the viewer is smart enough to find a font
that contains the relevant glyphs if the main display font does not have them.

_Why aren't emojis displayed properly in the bookmark editor and menu?_

Unfortunately, the main Java Swing user interface is not as clever as the document
viewer. It isn't sufficient just to install the font -- you might also have to
tell the user interface to use it. For more information, see the separate page
on [emoji support](emoji_support.md).


## Debugging

_How do I enable full debug logging?_

In the [configuration file](config_file.md), add the setting

    log.level=3

Please be aware that this will slow JGemini down considerably.

_Where does debug logging go?_

It goes to the standard error stream. You'll probably have to run JGemini from
a command prompt to see and collect the information.

[Documentation index](index.md)


