# JGemini design notes

## Overall philosophy

When I started coding JGemini, back in 2020, I was interested to discover
whether it was really possible to implement a workable client "in a weekend",
as the founders of Gemini claimed. It turns out that it was, but it wasn't a
very _good_ client. I made a number of design decisions that I wouldn't have
made, had I been planning something that might have taken a year.  Or, as it
turns out, six years.  It's not clear to me that these were entirely stupid
decisions, but I doubt I would make them again.

The first, and most foundational decision was to use Java's built-in HTML
viewer as the main document viewer. It's relatively easy to convert Gemtext to
HTML, and the built-in viewer supports user-defined styles and in-line images. 

A side-effect of this decision was that all the protocols I would handle --
originally only Gemini, but now several others -- would have to be integrated
into Java's URL handling infrastructure. The HTML viewer could display
hyperlinks, but only for protocols that the JVM actually recognizes. 

That meant that  I had to create subclasses of `URLConnection` for each
protocol JGemini supports, plus all the factories and glue code to make the JVM
use them. `URLConnection` is an ugly class, because it doesn't have a specific
"close" method that other classes can call. 

What this means is that it's relatively easy for JGemini to leak connections,
particular in error situations. I'm not at all happy about this approach, but I
seem to be stuck with it now. So far as I know, creating subclasses of
`URLConnection` is the only way to make links work with the Java HTML viewer.

So what JGemini really consists of are an HTML viewer, a set of classes that
convert Gemtext, etc., to HTML, and a set of URL handlers for the different
protocols. Everything else in the application is just supporting infrastructure
for these things.

Another fundamental design decision was that JGemini be capable of distribution
as a single, "fat" JAR file, and be totally platform-neutral. That is, I never
want to have to maintain different versions for different platforms, or even
include different libraries. This means using no native code, or any libraries
that include native code.

## Asynchronous handling

The Java Swing user interface model is essentially single-threaded. There's no
really effective way to have multiple threads interacting with the same user
interface elements. But if an application blocks the UI's one thread, then the
whole UI comes to a standstill. Since Gemini requests can take seconds to
minute to complete, blocking the entire UI is not really very elegant. We
really need some way to allow the user interface to remain useable while work
is being done in the background.

To assist with this common requirement, Swing provides a class called
`SwingWorker`, which _can_ run some of its methods in background threads. All
the asynchronous work in JGemini is done in anonymous subclasses of
`SwingWorker`. 

When we call the `execute()` method of `SwingWorker`, the Swing framework runs
its `doInBackground()` method in a separate thread. This method should make
sparing, if any, changes to the user interface. When `doInBackground()`
completes, the framework schedules a call to the `done()` method, but on the
main UI thread. 

So all the connection and data collection gets done in the `doInBackground()`
method, and then `done()` updates the UI.

So a request to fetch a document, for example, that is to be rendered in the
main viewer creates a `URLConnection` for the appropriate protocol, then
collects the data, all in `doInBackground()`. The UI remains responsive during
this process. `doInBackground()` populates a `ResponseContent` object with data
gathered from the request, which gets passed to `done()` when the request
completes.

While this is going on, a timer updates the status bar to say how much data has
been transferred, and to let the user know that something is going on.  This
process uses the `SwingWorker` publish-process method, where the application
calls `publish()` with updates, and the framework calls `process()`
periodically on the main UI thread to process them.

The publish-process method is itself asynchronous, and it's likely that
multiple calls to `publish()` end up in a single call to `process()`.  However,
since we're only sending status updates, it doesn't matter all that much how
timely they are.

## Download manager infrastructure

All downloads and transfers other than the main page content are handled using
a download manager. There are three parts to this:

- Instances of SwingFileDownload that do the actual work
- The DefaultDownloadMonitor, which aggregates downloads and
  tracks their changes
- The DownloadDialog, which receives updates from DefaultDownloadManger
  when transfers change state

DownloadDialog shows each instance of SwingFileDownload as a panel component
in a ScrollPane. It implements the DownloadMonitorListener interface, and
registers with DefaultDownloadMonitor to get updates. The dialog also
interacts direcly with SwingFileDownload instances -- to cancel a 
transfer, for example. When this happens, the download notifies DefaultDownloadMonitor,
which then notifies DownloadDialog as a listener. To there's a kind of
closed-loop process, where each update passes through the whole chain
of components.

It's a stupidly complicated system, but I couldn't find a way to simplify
it much, without losing functionality.

## Handling unsupported content types

JGemini only handles a small number of document types -- Gemtext, Markdown,
JPEG, etc. But a Gemini/Gopher/whatever server may supply other types, even
including video streams. In general, there's no way to tell the content of a
response until we start to read the response although, in some cases, the URL
pathname gives a clue.

So JGemini will have issued the request, and started to read the response,
before it knows whether it can handle it, or what the user wants to do if it
can't. Content that JGemini _can_ handle needs to be loaded into memeory;
content it can't handle needs to be saved to file, or streamed out, or whatever
the user chooses. There's no point asking the user what to do _before_ making
the request, and once we've started, it's too late.

The way JGemini handles this situation is to read the response as far as the
content type. Then, if it's something it can't handle, it terminates the
connection, prompts the user for the required action, and repeats the request
from the start.

Although this looks fine from the user perspective, it does have the effect of
sometimes making two requests to fetch a particular document. In principle,
JGemini could begin the transfer, then leave the connection open whilst
prompting the user, then continue with the transfer in the selected way. In
practice, I found that servers were timing out in the time it took to make that
decision. So, while the way JGemini deals with foreign content is sub-optimal,
I don't really see a better way, given the other fundamental design decisions. 

## Streaming

I'm not going to implement a media player in JGemini -- there are already
many perfectly satisfactory media players. There are Java
libraries for interacting with players like VLC, but I'm not sure I want
to force the use of a specific player.

Instead, JGemini just opens a connection to the server, starts an external
player process, and passes data from one to the other in 16kB blocks. Why 16kB?
I don't know -- it just seemed to work reasonably well.

All the data transfer has to be done in a background thread, and this has to be
a separate thread from the one that might be used for downloading content to
the viewer. JGemini uses the same `SwingWorker` approach for downloading this
content as it does for documents but now, if you continue to use the document
viewer window whilst it's streaming media as well, you do run the risk of
illegal concurrent access to the user interface.

Again, this hasn't so far been a problem in practice, but I'm open to the
possibility that it might be for some users. 

There's also a problem in coordinating the life-cycles of the JGemini
application and the external player. This is hard to get right, and I'm aware
that I haven't, not entirely. Sometimes media playback will prevent JGemini
closing cleanly. At other times, closing the player will result in annoying
error messages in the JGemini UI.

There isn't a huge amount of streaming content in the Gemini world at present.
If that changes, I might have to rethink how JGemini handles streaming.

