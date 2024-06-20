# JGemini FAQ

_How do I set the home page?_

Add an entry `url.home` to the configuration file -- either the
system-level configuration file or the user configuration file

_Where is the configuration file?_

On Linux, the system-level configuration file is
`/etc/jgemini/jgemini.properties`, and the user-level configuration file is
`$HOME/.jgemini.properties` (note the period at the start of the filename).
On systems other than Windows -- I don't know. This is a matter for the
Java JVM.

_Why does JGemini not remember the window size/position?_

Because I didn't implement this. In fact, JGemini is
_completely stateless_ by design. It remembers nothing -- no settings,
no cache, nothing. This is intentional, although it won't suit
everyone.

_How do I install JGemini?_

You don't need to do anything in particular. Just copy the program's
JAR file `jgemini-1.0.jar` to any convenient directory, and run it
using `java -jar jgemini-1.0.jar`.

If you want to be able to run JGemini by just running `jgemini` at the
prompt, you'll need to create a script that runs the Java JVM, and
puts the JAR file in a useful location. 

If you want to be able to click something on a desktop, or in a menu,
you'll need more steps. Most Linux desktops have a particular layout
of application files, and expect to find a `.desktop` file in some
particular place -- often `/usr/share/applications`. There is a 
sample `.desktop` file in the `samples` directory.

The `.desktop` file defines the program's icon files, and these
need to go in specific locations as well. Again, there are reasonably
standard locations.

You can run (as `root`) the `install-linux.sh` script, which will do
the basic set-up, copying the relevant files to the usual places. However,
you'll probably need to tell your desktop environment that you've made
changes, and there's no standard way to do that. With the Gnome
desktop, I find that the changes get picked up eventually, but it can
take minutes to hours. You can speed this up using Gnome-specific
tricks, but I'm not going to explain them here, because they'll have 
changed by the time you read this.

_How do I open a local .gmi file?_

From the command line, just run

    java -jar /path/to/jgemini-1.0.jar [filename]

If you've followed the desktop setup steps outlined above, you should
just be able to click the `.gmi` file in a file manager.

_How do I store bookmarks?_

Sorry, you can't. JGemini is stateless by design, and doesn't save
any information. You can create a `.gmi` file containing only 
bookmarks, stored some place in your home directory. Then you can,
if you wish, set it as the home page as described above.

_What Java version do I need?_

Any Java 8 or later should be fine. Because Gemini is based on TLS, 
you might find old, or cryptographically limited, JVMs don't work
properly. 

_The screen font is too small. What do I do?_

Edit the font settings in the either of the configuration files.
If you don't have a configuration file, use the one in the
`samples` directory as a template.

_Does JGemini handle images?_

Yes, but it doesn't in-line them. It just displays a clickable
link with the image's accompanying text. Clicking the link saves
the image and hands it over to the desktop to display. What software
the desktop uses to do that is not under the control of JGemini. 

Some Gemini clients in-line images, but this is a nuisace in situations
where the page author has linked an enormous image. Neither the GMI
page format nor the Gemini protocol provide any way for a Gemini client
to know how large an image is, except by downloading it.

The option to have JGemini in-line images -- with the user's 
agreement -- is something I've considered,
but so far I haven't been motivated to implement it. 

_What happens if I follow a non-Gemini link in a GMI page?_

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

_Is there a progress indicator?_

It would be nice to see the progress of large downloads but, so far
as I know, the Gemini protocol has no provision for reporting the
size of the content. Clients are expected to read until the server
closes the connection. This makes it hard to report, or even to know,
how much of the content has been read. 





















