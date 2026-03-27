# JGemini FAQ

_How do I install JGemini?_

You don't need to do anything in particular. Just copy the program's JAR file
`jgemini-1.0.jar` to any convenient directory, and run it using `java -jar
jgemini-1.0.jar`.

If you want to be able to run JGemini by just running `jgemini` at the prompt,
you'll need to create a script that runs the Java JVM, and puts the JAR file in
a useful location. 

If you want to be able to click something on a desktop, or in a menu, you'll
need more steps. Most Linux desktops have a particular layout of application
files, and expect to find a `.desktop` file in some particular place -- often
`/usr/share/applications`. There is a sample `.desktop` file in the `samples`
directory.

The `.desktop` file defines the program's icon files, and these need to go in
specific locations as well. Again, there are reasonably standard locations.

You can run (as `root`) the `install-linux.sh` script, which will do the basic
set-up, copying the relevant files to the usual places. However, you'll
probably need to tell your desktop environment that you've made changes, and
there's no standard way to do that. With the Gnome desktop, I find that the
changes get picked up eventually, but it can take minutes to hours. You can
speed this up using Gnome-specific tricks, but I'm not going to explain them
here, because they'll have changed by the time you read this.

_How do I set the home page?_

Add an entry `url.home` to the configuration file -- either the
system-level configuration file or the user configuration file

_Where is the configuration file?_

On Linux, the system-level configuration file is
`/etc/jgemini/jgemini.properties`, and the user-level configuration file is
`$HOME/.jgemini.properties` (note the period at the start of the filename).  On
systems other than Windows -- I don't know. This is a matter for the Java JVM.

_Why does JGemini not remember the window size/position?_

JGemini is _completely stateless_ by design. It remembers nothing -- no
settings, no history, no cache, nothing.  This is intentional (see below),
although it won't suit everyone.

_Why does JGemini not store anything from the user's session?_

People who use Gemini and similar systems are often quite concerned about
matters related to privacy. Storing browsing history, etc., is a potential
privacy hazard. Although there are ways to mitigate this hazard, I thought
it would be easier, and safer, of JGemini simply didn't store anything at all. 

I have to point out, however, that files that JGemini downloads, other than
documents, might be stored in a temporary directory, if JGemini has to hand
them off to the platform to be viewed. Images of types that JGemini supports
internally are not saved anywhere, except in memory.

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

Edit the font settings in the either of the configuration files.
If you don't have a configuration file, use the one in the
`samples` directory as a template.

_Does JGemini handle images?_

Yes. With Markdown documents, images are in-lined into the text by 
default. With Gemtext documents, in-lining behaviour is controlled by
the configuration file property `gemtext.inline.images`, which takes
values `1` (yes) or `0` (no).

If the images are in-lined, they will all be displayed with the same width,
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
being completed.

_What happens if I follow a non-Gemini link in a Gemtext page?_

If it's a link to a protocol that Java understands, like `http:`, then
JGemini will pass the link to the desktop, while will usually do
something useful. If it's not a recognized protocol, like `gopher:`,
nothing much will happen. This is a limitation in Java's HTML
renderer -- it's not easy to fix in JGemini, without reimplementing
the whole renderer. 

_What document types can JGemini display?_

GMI, plain text and, to a lesser extent, Markdown. In a client-server
interaction, the file type is determined by the response. For local
files, it's just based on the filename extension.

_Can I see the server's certificate?_

Not easily, and JGemini doesn't check anything in the certificate, 
anyway. If there ever comes a time when Gemini servers start using
verifiable certificates as a matter of course, I might change that.

_How do I send a client certificate with my requests?_

Please see the separate document README.client\_certs.

_Is there a progress indicator?_

It would be nice to see the progress of large downloads but, so far
as I know, the Gemini protocol has no provision for reporting the
size of the content. Clients are expected to read until the server
closes the connection. This makes it hard to report, or even to know,
how much of the content has been read. 

_Why aren't emojis displayed properly?_

You need the appropriate fonts and configuration -- not all fonts
contain glyphs for the emoji characters. I usually install Segoe UI Emoji.
On Linux I use the `font-manager` utility to add the font's TTF file to the
appropriate place. 

Note that Java Swing is pretty fussy about what fonts it's prepared to use.
However, once there is a suitable font in place, you don't have to tell 
JGemini to use it -- the JVM is smart enough to find a font that contains
the relevant glyphs if the usual font does not have them.

_How do I change the fonts?_

Edit the configuration file. Look for lines beginning `style.`.

_How do I find out what fonts are available?_
 
    java -jar jgemini-0.1.jar -Djgemini.dumpfonts 

_How do I go to the top-level directory of a Gemini capsule?_

The Lagrange client displays a caption bar which you can click to navigate to
the root level of a Gemini capsule. This is convenient, but the bar takes up a
lot of screen space. JGemini provides a menu command Go|Root which has the same
effect. 

_Why don't the Home/End keys work?_

Some keystrokes only work when the input focus is in the document viewer window
(and not, for example, in the URL bar). Just click the document.

