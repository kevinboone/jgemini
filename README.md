# JGemini

A Java-based graphical browser for the Gemini protocol.

Version 1.0b, Kevin Boone, June 2024 

## Why another Gemini client?

I use Linux. Although there are a number of existing graphical clients
that purport to run on Linux, none of them worked for me. I appreciate
that there are console clients and, frankly, the kind of material that
is available on Gemini is readable on the console.

Except -- I don't like reading large amounts of fixed-pitch text very much. 
In addition, being unable to follow links by clicking with a mouse is
a nuisance for me. I'd like a client that renders text nicely and
supports a mouse and, y'know, looks like it was written after 
1970. 

So, thinking it would be something I could knock up in a lunch-break,
I decided to write my own. Whether that was a wise course of action
remains to be seen.

## What is JGemini?

JGemini is a very rudimentary, barely-functional graphical client for
the Gemini protocol. It looks and feels rather like the very first
graphical Web browsers from the 90s. It supports all the features it
is required to support by the draft Gemini specification, and little else.
It is, however, useable. 

## Pre-requisites

To run JGemini you'll need a computer with some kind of graphical
desktop, and a Java JVM. JGemini should work with any Java version after
8.0. I've mostly tested it with OpenJDK Java 11. If you want to build 
JGemini from source, you'll probably need Maven. 

## Features

- Handles Gemtext and plain (usually UTF-8) text
- Rudimentary support for Markdown
- Can render local Gemtext files as well as server content
- Text styling can be configured to suit the display and user preference
- Uses anti-aliased font rendering for a smoother text appearance
- Fetches documents in the background to improve user interface responsiveness
- Supports text selection with cut-and-paste
- Search in document
- Downloaded documents can be saved
- Allows multiple windows

## Running JGemini

So long as you have a Java JVM installed, you can run JGemini from a 
prompt like this:

    java -jar /path/to/jgemini-1.0.jar

where x.y is the version number (currently 1.0).

If your system associates Java JAR files with the `.jar` extension, you
might be able to launch JGemini from a file manager or program manager
or whatever. If you're running from a prompt, you can specify a
URL or file to load:

    java -jar /path/to/jgemini-1.0.jar file:///path/to/file.gmi

or

    java -jar /path/to/jgemini-1.0.jar gemini://host:port/path.gmi

For convenience, local filenames don't need a full URL -- just the filename
will do. JGemini will expand it to a URL internally. If the name of a local
file ends in `.gmi`, it is treated as Gemtext, otherwise as 
plain text in platform-default encoding. The filename does not matter
with content fetched from a server, and the server will indicate the
type of the content.

If you want to install "properly" on Linux, there's a sample installation
script and `.desktop` file in the `samples` directory of the source
code bundle.

## Configuration

The only configuration (at present) is via properties files. 
JGemini will read a system-level properties file, if it exists,
at `/etc/jgemini/jgemini.properties`, and then a user properties
file at $HOME/.jgemini.properties if it exists.
The interpretation of "$HOME" on systems other than Linux is variable.
On Windows, it might be "C:\users\username". There is a sample configuration
file in the source
code bundle, in the `samples` directory. I hope that the settings
in that file are pretty self-explanatory.

If you want to change the default home page, add an entry `url.home`
to either configuration file.

Note that the static configuration of the program's 
appearance is pretty crude; there
is, so far, not even a way to zoom in or out at run-time. However, as
Gemini provides no way for an author to control the text appearance
(that's one of its strengths), text size and font, etc., should
be a one-time setting, even if it takes a bit of trial and error. 

Here are a few notes on the individual settings.

JGemini only uses six text styles for the document display, 
denoted "body" (normal text), "h1...h3"
(headings) "pre" (for preformatted text) and "a" (for links). The 
fonts that can be applied to these styles are JVM fonts, which may
be named differently to platform fonts (and, in some cases, are
more extensive). To get a list all JVM fonts, run JGemini with the
Java command-line switch 

    -Djgemini.dumpfonts 

THe settings `ui.control_font` and `ui.user_font` control the appearance
of the user interface elements other than the document display. Broadly,
the "user" font applies to text entry control, while the "control" font
applies to everything else (menus, buttons...). I'm sorry about this,
but the font configuration here is Java-like ("name size"), while the
document display styles are CSS-link ("size name"). In addition, while
the document font sizes allow qualifiers ("px", "em") the control
fonts do not -- they are generic Java sizes. This all probably 
sounds more complicated than it really is: the sample configuration 
file should make it clear. 

## Implementation oddities

Although the Gemtext format specification makes no provision for 
formatting other than links and headers, I often see people using
markdown-style emphasis markup, such as the asterisk and underscore, 
in Gemtext documents.
Consequently, JGemini respects those marks, and shows the text in
bold and italic respectively. In principle, this could cause problems
if these characters were used and _weren't_ intend to emphasise text 

## Caveats

Oh, where to start...

JGemini is intended for Linux. It _should_ work on any platform with
a relatively modern JVM, but I don't care about anything except 
Linux. That's not bias -- there are already a number of perfectly 
satisfactory clients for many different platforms. For all I know, there
might be perfectly satisfactory clients for Linux too -- I just couldn't
find one.

There is _no_ TLS certificate check. JGemini uses encrypted TLS 
communication because Gemini demands this. However, my experience is
that most Gemini servers do not issue recognized certificates.
So, rather than prompting the user to confirm every site, JGemini
simply carries out no certificate checks. Is this a security problem?
Sure. If you're planning world domination, or think governments are
monitoring your communication, this isn't the software
to use. 

There is no bookmark support yet.

In fact, JGemini saves not state at all, not even the window size. 
If you don't like the default window size, modify `window.w` and
`window.h` in the configuration file.

JGemini is designed to be operated with a mouse. The only keystrokes
that the program recognizes, apart from menu short-cuts, 
 are up, down, page-up, page-down.

JGemini is based on Java features that have not changed since about 
2005. Frankly, I'm surprised some of them still exist in the JDK.
The user interface is based on that old warhorse, Java Swing. Internally,
Gemtext is converted to HTML, and displayed using the Swing built-in
HTML viewer. That viewer has not been updated since HTML 3 was a new
think but, to be honest, that's more than enough to show Gemtext
content. In any event, it's possible that these features will be removed
from Java at some point, and relegated to optional downloads.

JGemini has no separate "download" function. It is not designed for 
retrieving large documents. Although it will download a file and
store it if it needs to invoke another program to handle it, 
files that it can display, are displayed, however, large they are. 
If you start downloading a large file that _can't_ be displayed,
then any navigation will cancel the download. Sorry, but it's a
browser, not a download manager.

In order to keep the user interface responsive, all the content-fetching
is done asynchronously, in background threads. It's not always easy
to see if a download is still in progress. If you try to download 
something else while an existing download is ongoing, JGemini will try
to cancel the previous transfer. It probably won't succeed, Java being
what it is, and the old transfer will continue to run in the background
until it completes, and then does nothing. This doesn't affect how
JGemini looks to the user -- it just means that a bunch of moribund
network operations can be going on invisibly.

JGemini only displays text formats: Gemtext, plain text, and
rudimentary support for Markdown. When content is fetched from a
server, Gemtext is signalled by a content type of `text/gemini`, plain
text if `text/plain`, and Markdown is `text/markdown`.
For local files, Gemtext is signalled by a filename
ending in `.gmi` and Markdown as `.md`; everything else is treated as
plain text.  For every other form of content, JGemini stores the
downloaded data in a temporary file, and uses Java's desktop integration to
launch it. What happens (if anything) if you click a link to an image, for
example, depends entirely on how the desktop is configured.

JGemini only supports the Gemini protocol, using URLs that begin `gemini://`. 
If a Gemtext document contains links to any other kind of protocol that
Java understands (file:, http:...)
then, again, JGemini delegates to the desktop. If you follow an
`http` link, for example, that should invoke a Web browser; but, again,
this is not under the control of JGemini. If the URL is not one that
Java understands (e.g., gopher:) then JGemini will not even attempt
to follow the link, not even by invoking the desktop. Technically, this
is because Java can't even construct an instance of java.net.URL to pass
to the desktop.

JGemini follows redirections. Although the "best practices" guide for
Gemini warns against redirections they are, in fact, commonplace. 
JGemini does not do very well at avoiding redirection loops -- it
will just try to follow them indefinitely. Apart from in the "torture test"
I haven't actually encountered any redirection loops, so I'm not 
unnuly concerned about this yet.

The Gemini protocol only supports one (very simple) method for supplying
user input. Those sites that handle user input expects a single line 
of user input appended to the request URL after a '?' character. If the
expected input is missing, the Gemini server is supposed to respond
with status 10 or 11, and the client should prompt the user and retry
the request. JGemini does exactly that, and no more. 

There is no caching of any kind, either on disk or in memory. This is
a feature, not a bug. The problem is that the Gemini protocol does not
allow content to be timestamped, so there is no robust way to implement
a cache. The client would have absolutely no way to know whether the 
data in the cache is up to date. Even the "back" button causes the 
previous page to be re-requested. In future, I might implement some form
of optimistic caching, but I'm not sure it would be safe. 

Text search is only case-insensitive, and forward.

JGemini supports multiple windows, but not tabs. I hate tabs, and have
no plan to implement them. Using multiple windows looks this same, from
the user perspective, as opening multiple instances of JGemini. However,
since all windows share a JVM, the multi-window approach uses much
less memory.

Markdown support is _very_ rudimentary. Although it looks simple, the
Markdown format is considerably more difficult to lay out neatly in a
viewer than Gemtext is (and, of course, plain text is easiest of all).
This situation can be improved, if there's any interest.

The Markdown viewer does not support relative links, only full links
with a protocol and a path. This seems the right approach to me, because
authors providing Markdown files are probably expecting them to be
used in an external viewer, where relative links will make no sense.

JGemini does not support feeds of any kind. If you select a feed,
you'll probably get a page of XML.

## Building JGemini 

To build JGemini from source, you'll need Maven, configured for the
usual repositories (although JGemini has few dependencies). Then,
in the source directory: 

     mvn package

## Closing remarks

There's a lot that could be done to JGemini to make it a more useful,
more aesthetically pleasing application. If there's interest, I can
probably do more work on it. Right now, howver, it's good enough for
my purposes.


## Change log

Version 0.1 -- March 2021 -- first release

Version 0.1a -- October 2021
- Added code to apply SNI header to the TLS communication (contributed by omar-polo) 

Version 0.2a -- June 2024
- Added a system-level configuration file
- Changed the default homepage
- Improved the documentation slightly


