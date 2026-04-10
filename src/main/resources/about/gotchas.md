# Things to watch out for

## TLS and certificate issues with Gemini

There is _no_ TLS certificate check, not even trust-on-first-use.  JGemini uses
encrypted TLS communication because Gemini demands it. However, most Gemini
servers do not issue certificates that are validated by external authorities.
So, rather than prompting the user to confirm every site, JGemini simply
assumes every server certificate is fine. Is this a security problem?  Sure. If
you're planning world domination, or think governments are spying on your
communication, this isn't the software to use. 

## Please be patient

Most "small net" servers run on low-cost cloud hosts or in people's homes; they
are often not very responsive.

In order to keep the user interface moving, JGemini does all content-fetching
asynchronously in background threads. It's not always easy to see if a
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

## Detecting the document format sometimes involves guesswork 

JGemini displays text in Gemtext, plain text, and Markdown formats, Atom feeds,
and popular image types.  

Where the server provides a content type, JGemini will usually use it.  If the
server doesn't, or can't, provide one, JGemini relies on filename extensions
and guesswork.

Sometimes JGemini will override a clear content type from the server, or a
clear filename.  For example, when it receives a file using `nex`, with a
filename ending in `.txt`, or where there is no filename, JGemini treats the
file as "nex-flavoured text". This format is just like plain text, except that
lines beginning with "=>" are treated as links, as with Gemtext. 

In addition, because Gemini servers often don't report the MIME type of
Markdown correctly, JGemini treats any file it retrieves whose name ends in
`.md` as Markdown, regardless of the MIME type the server reports. 

## JGemini only handles its own protocols

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

## Emoji support can be fiddly to set up

The use of Unicode emojis is widespread in the Gemini world. To see these
properly, you'll need to ensure your operating system has a font with the
appropriate glyphs, like `Segoe UI Emoji` or `Noto Emoji`. 

Because of a limitation in the Java Swing user interface, the document viewer
and the user interface (menus, etc) have different font settings. While merely
installing a font is sufficient to have the document viewer use it, 
the user interface needs to be told specifically to use an emoji-aware font.
This affects primarily bookmarks, which might contain emojis. For more
information, see the documentation of `ui.control_font` in the 
[configuration file](config_file.md) page. 

The default values of `ui.control_font`, etc., include a reference to a
font called `Emoji`. This (probably) isn't a real font name, but should
match any font with "Emoji" in the name. If you install multiple fonts
of this kind, you might have to edit the configuration to specify
a particular one. 

When displaying Gemtext, JGemini uses an arrow symbol to indicate a link that
can be followed, as well as displaying the link text in a highlighted colour.
If the first character of the link text is an emoji, then the link doesn't get
the arrow -- if the author is using emojis to highlight links, the extra arrow
would look odd.  

## Character encoding issues

JGemini has mostly been tested on platforms that use UTF-8 character encoding. 
It should handle documents that it receives with other encodings, so long as
the server indicates the encoding. However, it's less clear what will happen
to transfers _from_ the client. 

Even if the platform does use UTF-8, there's little to stop the user sending
data to a server that can't cope with it. For example, the JVM will almost
certainly let you enter multi-byte characters into the text entry dialog box,
but that doesn't mean the server will understand them -- particularly Gopher.

## Document font size affects only text 

The main document font size, whether it's set in configuration 
or by using ctrl+[ and ctrl+], only affects text. Images will always be
displayed using the fixed size in the configuration, or full-sized if they
are in a page of their own.

## Gopher can be a bit awkward 

The Gopher protocol was invented before the "modern" concept of a URL. Using
contemporary URLs with Gopher servers is fraught with difficulty. Most web
servers (and Gemini servers) don't really distinguish between a URL whose
`path` component is empty, and one where the path is `/`. Gopher servers often
do.

That's awkward, because there needs to be _something_ to separate the
components of a URL, and that something is usually a `/`.

When interpreting a Gopher URL, JGemini struggles to tell the difference
between a `/` that is part of a Gopher selector, and one that just separates
URL components. You might need to try a Gopher URL with, and without, the
leading `/` to get a response.

## Feed rendering is rudimentary

JGemini supports only Atom feeds, and only to the extent of formatting them for
display in the document viewer. Authoring tools for Atom aren't particularly
consistent with one another. For example, some tools fill in the 'publication
date' field, while others use the 'updated date'.  Some supply both. 

Some feeds have multiple URLs for the same entry, and they aren't always easy
to distinguish.

JGemini doesn't distinguish the different dates, or try to disambiguate
multiple URLs for the same entry.

Most significantly, JGemini is not a feed aggregator -- it doesn't provide a
way to subscribe to multiple feeds.

## There is no streaming support

None of the protocols that JGemini supports provide any information about
the length of the data the server is sending. A stream (audio, video) by
its very nature has no end; but JGemini can't distinguish a stream from a
file it could, in principle, download to completion.

As a result, if you follow a link to a stream, JGemini will download it until
you get bored waiting, or it runs out of memory.

## Styling issues

You can switch themes at run-time, using the _Theme_ tab of the
[Settings dialog](settings_dialog.md). However,
not all themes apply all possible styles to all elements. If you do change
themes at runtime, the results might not be _exactly_ as they would be if you
started JGemini from scratch. They should be similar, though.

## Missing features

_There is no download manager_. JGemini will download multiple files concurrently,
but there's no way to see which transfers are active and which completed. Nor
is there a way to stop a particular transfer.


[Documentation index](index.md)


