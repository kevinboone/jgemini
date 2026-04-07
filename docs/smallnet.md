# About the "small net" protocols 

These are the protocols that JGemini currently supports.

## Gopher

The Gopher protocol has been in use for nearly forty years and still has a
small, but fiercely loyal following. The world-wide web as we now know it was
largely inspired by Gopher. Gopher 'holes', as they are colloquially called,
nearly always consist mostly of plain, pre-formatted text, which has to be
displayed in a fixed-pitch font. To use Gopher is to step back in time, to the
days where all computer interaction took place through a terminal with an
eighty-column display.

Gopher supports a simple method of text upload from the client to the server,
which makes it possible to implement a search engine.

Gopher lacks the flexible hyper-linking we associate with the web and HTML.
Links from one site to another are defined in 'gophermaps', which have an
awkward, arcane format. 

The Gopher protocol does not usually support encryption, and provides no secure
method of authentication. 

Long-established Gopher servers include [Floodgap](gopher://gopher.floodgap.com:70)
and [SDF](gopher://sdf.org:70/1/).

## Gemini

Gemini is a contemporary, encrypted alternative to Gopher. Most Gemini
'capsules' consist mainly of text, usually in the 'Gemtext' format, although
the protocol can transport any file type.  Gemtext can carry text and links, so
there is no need for an equivalent of the gophermap.   

The Gemtext format can be rendered in a variable-pitch typeface, and does not
require pre-formatting. Browsers can therefore render Gemtext in a way that is
agreeable to read, fits different screen sizes, and looks less old-fashioned
than Gopher's pre-formatted text.  

Because Gemini is encrypted using TLS, browsers can present client certificates
for authentication. This makes it possible for Gemini to provide interactive
services like forums and bulletin boards.

For more information, see the [Gemini protocol home page](gemini://geminiprotocol.net/).

## Spartan

Spartan is a simplified, unencrypted alternative to Gemini. Spartan sites usually
consist mainly of text documents, in the same 'Gemtext' format as Gemini. 

Because it's unencrypted, Spartan is simpler and considerably faster than
Gemini. However, it provides no robust method of authentication, so it's
appropriate largely for static, informational sites, rather than interactive
ones.

## Nightfall Express, nex

Nightfall Express is a trivially-simple, text based document retrieval system with 
a niche following. Like Gopher, it's mostly used with pre-formatted, plain text
files, but with a system for hyper-linking like Gemini's. 

Like Gopher, Nightfall Express is unencrypted. Some sites do allow for the
upload of data from the browser to the server but, because there's no
encryption, this has to be done with care. Because there's no well-documented
standard for data upload, browsers like JGemini can't easily implement such
features.


[Documentation index](about:/index.md)

