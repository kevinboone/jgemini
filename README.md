# JGemini

A Java-based graphical browser for Gemini and other 'small net' protocols

Version 3.0.0, Kevin Boone, April 2026

**Note**  
Version 3.0 is radically different from earlier releases. I've added a lot of
code in a relatively short time, as well as refactoring a lot of the original
code. Version 3.0 is, I hope, a better application by far than it predecessors,
but adding new features this quickly comes with risks.  Although JGemini is
stable enough for my own purposes, it's plausible that I've broken features
that worked before. If that's the case, do please report the bugs. Thank you.

## What is JGemini?

JGemini is a graphical client for the Gemini, Spartan, Gopher, and "Nightfall
Express" (`nex`) protocols.  It looks and behaves rather like the first
graphical Web browsers from the 90s. So far as I know, it supports all the
features it is required to support by the various specifications, and little
else.  It's useable, though: I use it every day.

## Why another Gemini client?

There's no good reason. I wrote JGemini a long time ago, before there were any
reasonable graphical clients for the Gemini protocol for Linux. Times have
changed, and there are a number of clients for Linux that are superior to
JGemini: Lagrange and Alhena, for example.  I maintain JGemini for my own
experiments -- I doubt it would be much use to anybody else. It has the small,
putative advantage over other clients that it natively supports Markdown
documents as well as Gemtext.

## Prerequisites

To run JGemini you'll need a computer with some kind of graphical desktop, and
a Java JVM. JGemini should work with any Java version 11.0 or later.
I've tested it with OpenJDK versions 11-27. If you want to build JGemini from
source, you'll probably need Maven. 

JGemini is intended for Linux. It works on other platforms with a relatively
modern JVM, including Windows 11, but I don't care about, or do much testing
on, anything except Linux. 

If you're looking at Gemini/Spartan capsules that use Unicode emojis -- and
many do -- you'll probably need to ensure that your computer has fonts that
contain the relevant glyphs; see `docs/emoji_support.md` for more information.

If you want to stream Gemini radio and similar content, you'll need a media
player that JGemini can send a data stream to; VLC and `ffmpeg` both seem to
work.

## Features

- No specific installation procedure (if you have a Java JVM) -- JGemini is
  supplied as a single Java JAR file
- Supports Gemini, Spartan, Gopher, and `nex` protocols
- Handles Gemtext, CommonMark Markdown, and plain (usually UTF-8) text
- Renders local files as well as remote content
- Authentication using per-server client certificates
- Built-in client certificate manager, which can create new certificates and
  incorporate existing ones
- Text styling can be configured to suit the display and user preference
- Uses anti-aliased font rendering for a smoother text appearance
- Fetches documents asynchronously to improve user interface responsiveness
- Download manager
- Text selection with cut-and-paste
- Search in document
- Supports multiple windows
- Search directly from the URL bar
- Saves little state by default, for privacy
- Reasonably comprehensive documentation with built-in viewer
- Rudimentary bookmark support, with built-in editor
- Rudimentary media streaming support, using an external player
- Parses and displays Atom feeds

## Installing and running JGemini

JGemini does not need to be installed, beyond copying its JAR file to some
convenient place. However, a more formal installation might be more convenient.
For more information, please see the separate document
`README_installation.md`. In essence, just run the JGemini JAR file using the
Java JVM.

## Configuration

Nearly all JGemini's features can be configured using the Settings dialog in
the main user interface. It should no longer be necessary to edit the
configuration file very often, if at all. However, it's still possible, it's
still documented, and JGemini has a built-in file editor for doing it, should
the need arise.

## Image support

JGemini supports JPEG, PNG, and GIF images internally, including animated
versions.

For Gemini and Spartan, supported images will be displayed in-line in the
document by default.  Links to other types -- and to other types of document
entirely -- will just be displayed as a link.

For compatibility with other Gemini browsers, JGemini can be configured _not_
to in-line images from a Gemtext document into the text if preferred.  The
configuration property is `gemtext.inline.images=1|0`. If images are not
in-lined, they will be rendered as links, just as for unsupported image types.

Images referenced from gophermaps (directories) will never be in-lined.  It's
common to find gophermaps with huge numbers of huge images, because Gopher
authors don't expect images to be in-lined.

Images in-lined into a document are all displayed with the same (configurable)
size. This is because none of the document formats which JGemini supports allow
an image size to be specified, and it looks odd if they're all different sizes.

If you choose to open a document explicitly, it will be displayed at its
full size.

Images are fetched asynchronously. It's not always easy to tell when this is
happening, except that images areas on the screen will be blank. If you select
to open an image in a new window, you'll see a blank screen until the image is
fully downloaded. Please be patient -- Gemini does not provide a way to know in
advance how large the image is, so JGemini can't report progress. 

Although the widths of in-line images will all be the same, heights might be
different, to keep the correct aspect ratio.  Because JGemini does not know the
total size of an image until it's been retrieved, the page might redraw after
downloading an in-line image.

Please note that the File|Save menu only saves text. You'll get an error message
if you open an image in a new window, and then try to save it. If you want to
save an image as a file, right-click its link and select 'Download'.

The ability to open an image in its own window is only available for images
whose paths end in a conventional extension, like `.jpg`. This is because
JGemini can't tell, just from a link URI, what kind of data the link provides.
An image file that can't be recognized from its filename will be downloaded and
handed off to the platform to handle.

## URL history

JGemini remembers URLs you visit, either by entering them in the URL bar,
or by following links. By default the history is _not_ saved anywhere,
because it's potentially a privacy risk. You can change this behaviour
using the Settings dialog box. 

## URL bar search

By default, you can enter a search term in the URL bar, rather than a URL, as
with most modern web browsers. You can disable this feature, or change the
search provider, in the configuration file.

There's no perfectly reliable way for JGemini to know whether you've entered
a search term or a URL. Since URLs usually contain periods ('.'), and don't
usually contain spaces, this is used as a guide. However, just as web browsers
do, JGemini will sometimes guess wrongly.

## Key bindings

The usual arrow keys and page up/down should work. You can highlight text
using shift+arrow combinations. To go to the top/bottom of the page,
using ctrl+Home/End. Zoom in/out with ctrlr+[ and ctrl+].

## Text input

When a server prompts for input, JGemini raises a text-entry dialog box.  Since
it's legitimate -- and commonplace -- for input to contain newline characters,
the 'Enter' key does not submit the input, but enters a new line. To submit,
hit ctrl+S.

Since a Gemini URL is limited to a total of 1024 bytes, and that has to include
the `gemini://host/port/path` part, the text input dialog box shows the number
of bytes remaining for user input. As you type, this number will decrease by an
amount which might not clearly be linked to the amount of input. This is a
consequence of the way non-alphanumeric characters have to be encoded, and the
fact that UTF-8 -- which Gemini expects -- is a multi-byte encoding. 

JGemini uses the same text input box for Gemini and Gopher. Some Gopher servers
may behave properly with non-ASCII input, but I suspect that many will not. 

## Protocol notes

### Gopher

JGemini takes any Gopher URI that ends in '/' to be a reference to a gophermap
(directory), and it parses the returned document accordingly.  Except when
following a link from a gophermap, JGemini has no reliable way to know what
kind of data it is retrieving, except to guess it from the filename.
Consequently, filenames are more important with Gopher than with Gemini.
Broadly, JGemini will use file extensions to determine what to do with a file
it gets from a Gopher server, more than is the case with Gemini.

Things are slightly more predictable when reading a gophermap, because this
does give some indication of the file type. So JGemini will try to use
icons to show the type of file the link references -- image, movie, text,
etc.  

JGemini can interpret Gopher URIs with or without a type character. That is,
we might have:

    gopher://foo.bar/file.txt

or

    gopher://foo.bar/0/file.txt

Here the '0', which signifies a text file, is a guide to the _browser_, not the
server. The server never sees the '/0' part of the URI, so it always sends the
same thing. This odd convention came about because we needed a way to
incorporate the type information from a gophermap into a URI.

JGemini always displays text files from Gopher as pre-formatted lines.  Gopher
dates from a time when everybody used 80-column screens, and documents are
generally formatted on that basis.

JGemini supports Gopher queries, typically used with search engines like
Veronica.

The Gopher specification calls for a text file to end with a period (.) on a
line on its own. Not all authors or servers follow this rule. If this line does
exist, JGemini displays it. It would be a hassle for it to have to use
different logic to display text files from Gopher differently to all other text
files.

### nex

Nightfall express is a very rudimentary protocol. Like Gopher, the browser has
to rely on the filename, for deciding how to handle a file. However, there is
no equivalent of a gophermap, so JGemini can't decorate links to indicate the
type of file they reference, except by filename.

In practice, I've not see nex used for anything except plain text.

Like Gopher, text documents used with nex are generally pre-formatted for an
80-column screen, and JGemini displays them as such.

### Gemini

JGemini supports Gemini user input and redirections, as well as ordinary document
retrieval. At present it doesn't support the Titan upload protocol. Gemini
responses include a MIME type so, in principle, JGemini should be able to work
out unambiguously how to handle the document it receives. However, when running
JGemini with a local file, it still has to use the filename to guess the
contents, so filenames are still important.

JGemini tries to annotate links to indicate the type of document they link to.
However, it's common for Gemini authors to use emojis or unicode symbols to
highlight particular functions or contents. So JGemini doesn't annotate links
that appear to start with such characters.  However, this is at best a guess. 

### Spartan

Spartan is a simplified form of Gemini, without the TLS encryption. JGemini
won't do user input with with Spartan, because the specification doesn't
provide any way to do input or upload. 

## Implementation oddities

JGemini relies heavily on Java features that haven't changed since about 2005.
Frankly, I'm surprised some of them still exist in the JDK.  The user interface
is based on that old warhorse, Java Swing. Internally, all document files are
converted to HTML, and displayed using Swing's built-in HTML viewer. This
viewer has not been updated for decades, but it's more than adequate to show
Gemtext and Markdown content. In any event, it's possible that these features
will be removed from Java at some point, or relegated to optional downloads.
I'll find a way to deal with that, if the situation arises.

## Caveats and limitations

Please see the separate document ISSUES.txt.

## Building JGemini 

To build JGemini from source, you'll need Maven, configured for the usual
repositories (although JGemini has few dependencies). Then, in the source
directory: 

     mvn package

This will generate the compiled JARs in `target/`.

## Author and legal

JGemini is maintained by Kevin Boone, and distributed under the terms of the
GNU Public Licence, v3.0. 

The binary distribution includes a number of open-source libraries and
media items. For full details, please see `docs/author_and_legal.md`.

There is, of course, no warranty of any kind.

## Change log

Version 0.1 -- March 2021 -- first release

Version 0.1a -- October 2021
- Added code to apply SNI header to the TLS communication (contributed by omar-polo) 

Version 0.1c -- June 2024
- Added a system-level configuration file
- Changed the default homepage
- Improved the documentation slightly

Version 0.1d -- March 2026
- Updated documentation
- Added support for client certificate selection
- Fixed a bug with redirection
- Fix a bug with URL encoding in uploads

Version 0.1e -- March 2026
- Improved Markdown support 
- Added Go|Root
- Added styling for block quotes
- Improved the text entry dialog box, and added a character count
- Updated documentation a little

Version 1.0f -- March 2026
- Added inline image support in Gemtext
- Added configuration for image size
- Added internal image handling for certain types of file
- Added dark configuration sample
- Added an indication of amount transferred in Gemini connection

Version 2.0.0 -- March 2026
- Added preliminary Spartan, Gopher, and nex support
- Made Go|Root work better with URIs with usernames
- Added zoom in/out
- Completely changed the way styling works
- Took out handling of _ and * in Gemtext, because too many capsules used
  these in non-emphasizing ways.
- Implemented search from URL bar (which can be disabled)
- Let the desktop set the main window position, so JGemini windows don't
  appear on top of one another.
- Added documentation viewer

Version 2.0.1 -- April 2026
- Added rudimentary settings editor
- Added "Set as home page" facility
- Changed location of properties file, in a new directory, which
  will also store URL history by default

Version 2.0.2 -- April 2026
- Added rudimentary bookmark support
- Updated documentation, particularly related to emoji support
- Fixed bug in list formatting in Gemtext
- Put limit on status line in Gemini response, to protect
  against broken server
- Fixed (maybe) broken handling of selectors beginning "/" in Gopher
- Added converter for Atom feeds
- Updated documentation concerning emoji fonts
- Added open-source licences to the built-in documentation

Version 2.0.3 -- April 2026
- Changed the button accelerators from ctrl-something to the platform
  default (typically alt-something).
- Moved _all_ the user-visible text strings to resource bundles. What a
  horrible job -- I should have done it properly from the start
- Added rudimentary client identity manager
- Dialog-specific documentation

Version 2.0.4 -- April 2026
- Added a way to show server certificate information
- Added a 'settings' dialog box
- Removed a bunch of superfluous menu commands, and rearranged some others
- Window size is now saved when closing a window
- Fixed a number of stupid bugs
- Added a "useful links" page

Version 2.0.5 -- April 2026
- Tidied up the connection logic
- Intercepted requests for unhandled content types, and pop up a 'file save'
  dialog box

Version 3.0.0 -- April 2026

- Download code completely rewritten
- Download manager implemented
- Rudimentary media streaming support: JGemini can launch a media player like VLC,
  and stream data into it retrieved using any of the protocols it supports
- URL bar now filters the URL history as the user types. This was surprisingly 
  nasty to implement, and might be buggy
- Started to move comments to proper javadocs, and added a javadoc goal to the
  Maven `pom.xml`. The `javadocs.sh` script runs the Maven
  goal, and moves the generated documentation to `javadocs/`
- Toolbar icon size is now configurable: setting `ui.icon.size`
- Toolbar icons can be colour/mono, using the setting `ui.icons.mono`. Colour
  is the default
- JGemini now does a proper "System.exit()" when it detects that no top-level windows are 
  open. This should prevent the program hanging on exit because Swing leaves 
  dangling threads that can't easily be closed. When exiting, JGemini cleans up
  incomplete file transfers

