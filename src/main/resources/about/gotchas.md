# Things to watch out for

## TLS and certificate issues with Gemini

There is no TLS certificate check, not even trust-on-first-use.  JGemini uses
encrypted TLS communication because Gemini demands it. However, most Gemini
servers do not issue certificates that are validated by external authorities.
So, rather than prompting the user to confirm every site, JGemini simply
assumes every server certificate is fine. Is this a security problem?  Sure. If
you're planning world domination, or think governments are monitoring your
communication, this isn't the software to use. 

## Please be patient

Most "small net" servers run on low-cost cloud hosts or in people's homes; they
are often not very responsive.

In order to keep the user interface responsive, all the content-fetching is
done asynchronously, in background threads. It's not always easy to see if a
download is still in progress, and this can cause problems with slow servers.

If you try to download something else while an existing download is ongoing,
JGemini will try to cancel the previous transfer.  It probably won't succeed,
Java being what it is, and the old transfer will likely continue to run in the
background until it completes, and then do nothing. This doesn't affect how
JGemini looks to the user -- it just means that a bunch of moribund network
operations can be going on invisibly.

To make things worse, there is no caching of any kind, either on disk or in
memory.  The protocols JGemini supports do not allow content to be timestamped,
so there is no robust way to implement a cache.  This means that JGemini makes
more requests on the server than a regular web browser would.  Even the "back"
button causes the previous page to be re-requested. 

Please be patient.

## Issues related for file format detection

JGemini only displays text in Gemtext, plain text, and Markdown formats, and
popular image types.  When content is fetched from a server, Gemtext is
signalled by a content type of `text/gemini`, plain text is `text/plain`, and
Markdown is `text/markdown`.  For local files, Gemtext is signalled by a
filename ending in `.gmi` and Markdown as `.md`; any other local file is
treated as plain text.  For every other form of content, JGemini stores the
downloaded data in a temporary file, and uses Java's desktop integration to
launch it.  What happens (if anything) if you click a link to an audio file ,
for example, depends entirely on how the desktop is configured.

As a slight exception to the above, files received using `nex`, with a filename
ending in `.txt`, or where there is no filename, are treated as "nex-flavoured
text". This format is just like plain text, except that lines beginning with
"=>" are treated as links, as with Gemtext. The `nex` protocol does not supply
a content type in the response, so we only have the filename to guess the
contents. 

Note that, because Gemini servers often don't report the MIME type of Markdown
correctly, JGemini treats any file it retrieves whose name ends in `.md` as
Markdown, regardless of the MIME type the server reports. 

JGemini only handles URLs with protocols `gemini://`, `spartan://`,
`gopher://`, or `nex://`.  If a Gemtext document contains links to another kind
of protocol that Java understands (`file:`, `http:...`) then JGemini delegates
to the desktop. If you follow an `http` link, for example, that should invoke a
Web browser; but, again, this is not under the control of JGemini. If the URL
is not one that Java understands (e.g., `gophers:`) then JGemini will not even
attempt to follow the link, not even by invoking the desktop. The reason is
somewhat technical but, in essence, Java can't even construct an instance of
`java.net.URL` containing the URI to pass to the desktop.

## In-document search issues

Text search is only case-insensitive, and forward.

## Emoji support

Use of Unicode emojis is widespread in the Gemini world. To see these properly,
you'll need to ensure your operating system has a font with the appropriate
glyphs, like `Segoe UI Emoji`. 

When displaying Gemtext, JGemini uses an arrow symbol to indicate a link that
can be followed, as well as displaying the link text in a highlighted colour.
If the first character of the link text is an emoji, then the link doesn't get
the arrow -- if the author is using emojis to highlight links, the extra arrow
would look odd.  However, it's not as easy to detect emojis in Java as it ought
to be (before Java 21), and sometimes this test doesn't work properly.

## Character encoding issues

JGemini has mostly been tested on platforms that use UTF-8 character encoding. 
It should handle documents that it receives with other encodings, so long as
the server indicates the encoding. However, it's less clear what will happen
to transfers _from_ the client. 

Even if the platform does use UTF-8, there's little to stop the user sending
data to a server that can't cope with it. For example, the JVM will almost
certainly let you enter multi-byte characters into the text entry dialog box,
but that doesn't mean the server will understand them -- particularly Gopher.

## Missing features

There is no bookmark support yet.  In fact, JGemini saves no runtime state at
all, not even the window size.  

JGemini does not support feeds of any kind. If you select a feed, you'll
probably get a page of XML sent to the platform's default web browser.

It would be useful if at least some configuration changes could be made
using JGemini's user interface, rather than by editing the configuration file.

[Documentation index](index.md)


