# Issues, bugs, and limitations

## BUG: Occasional hangs on exit

Occasionally, JGemini will not exit properly, and will continue to run 
when all its windows are closed. I can see from the debug logging that the
background loading thread is still running, even though all the loads seem
to have completed. 

So far, the problem has only affected Gopher. I speculate that Gopher
servers tend to be slow-ish, and tend to continue long-ish documents.
I think the problem is associated with the interaction between the
thread that keeps the "Loading..." message on the display, and the
thread that clears the status bar.

Unfortunately, I can't reproduce the problem at will.

It's not a big deal when running JGemini from a prompt, because you can
just hit ctrl+C. I've not seen it on Windows which is good, I guess, 
because you probably don't have a prompt to kill the program from.

## BUG : JGemini will break on a redirection loop

JGemini follows redirections. Although the "best practices" guide for Gemini
warns against redirections they are, in fact, commonplace.  JGemini does not do
very well at avoiding redirection loops -- it will just try to follow them
indefinitely. Apart from in the "torture test" I haven't actually encountered
any redirection loops, so I'm not unduly concerned about this yet.

## BUG : In-line images don't get a link with Markdown documents

When JGemini displays an image on-line from a Gemtext document, it also
displays a link to the image. You can right-click the link to see further
actions, like "Download".

JGemini in-lines images into Markdown documents, but doesn't display the link.
This is because it uses the standard `commonmark` library to convert Markdown
to HTML, and it's fiddly to change the way it generates HTML. It's easy enough
to add attributes to an existing element -- JGemini does this to fix the size
of images, for example -- but changing the HTML completely is much more fiddly.

This bug is fixable, but it's a lot of work for uncertain benefit.

## LIMITATION : TLS certificate issues 

There is no TLS certificate check, not even trust-on-first-use.  JGemini uses
encrypted TLS communication because Gemini demands it. However, my experience
is that most Gemini servers do not issue certificates validated by external
authorities.  So, rather than prompting the user to confirm every site, JGemini
simply assumes every server certificate is fine. Is this a security problem?
Sure. If you're planning world domination, or think governments are monitoring
your communication, this isn't the software to use. 

It might be nice if JGemini could show the user information about the server
certificate.

## LIMITATION : Encoding issues

I don't know how JGemini will behave on platforms that don't use UTF-8
character encoding. Whenever a textual response from a server is converted to a
Java String for processing it's possible, and advisable, to define the encoding
of the source document. I've taken some trouble to do that, when the source
encoding is available, but I may have overlooked some instances. 

Moreover, servers don't always indicate the encoding in their response.  Gopher
can't, and Gemini server often don't. In these cases I've assumed UTF-8, since
this seems the safest approach. But I can envisage situations where UTF-8 might
be incorrect. 

## LIMITATION : Content detection issues

It isn't always possible for JGemini to work out what type of document it's
receiving. Gopher provides only limited type information and, if JGemini
is loading a local file, there's none at all.

In both cases JGemini falls back on guessing the type from the filename, but
that's often not definitive. In some cases thee won't even be a filename in the
URI -- the URI might denote a directory, but the returned data still has to be
interpreted. 

JGemini does a fair bit of guesswork in this area, and makes a lot of
assumptions.

In addition, even in protocols like Gemini where there is, in principle, a
definitive content type, it's often incorrect. Gemini servers often denote
Markdown documents as plain text, for example. This is unhelpful, and JGemini
tries to avoid the problem by interpreting all responses for files whose names
end in `.md` as Markdown.

Again, this is an assumption, and it may be a bad one; but it's difficult to
know what else to do.

## LIMITATION : Java can't handle unknown protocols

JGemini itself only supports the Gemini, Spartan, Gopher, and nex protocols, using
URLs that begin `gemini://`, `spartan://`, `gopher://`, or `nex://`.  If a
Gemtext document contains links to URLs with other protocols _that Java understands_
(`file:`, `http:...`) then JGemini delegates to the desktop. If you follow an
`http` link, for example, that should invoke a Web browser; but, again, this is
not under the control of JGemini. If the URL is not one that Java understands
(e.g., `gophers:`) then JGemini will not even attempt to follow the link, not even
by invoking the desktop. The reason is somewhat technical but, in essence, Java
can't even construct an instance of `java.net.URL` containing the URI to pass
to the desktop.

## LIMITATION : No caching

There is no caching of any kind, either on disk or in memory.  The Gemini
protocol does not allow content to be timestamped, so there is no robust way to
implement a cache. The client would have absolutely no way to know whether the
data in the cache is up to date.  Even the "back" button causes the previous
page to be re-requested. In future, I might implement some form of optimistic
caching, but I'm not sure it would be robust. 

## LIMITATION : Search in document is inflexible

Text search is only case-insensitive, and forward. It would be nice if
searching were more configuration.

## LIMITATION : No tab support

JGemini supports multiple windows, but not tabs. I hate tabs, and have no plan
to implement them. Using multiple windows looks the same, from the user
perspective, as opening multiple instances of JGemini. However, since all
windows share a JVM, the multi-window approach uses much less memory.

## LIMITATION : Some style/appearance changes can't be made without a restart

If you edit the configuration file, JGemini will reload the changed settings.
Some changes will take effect immediately, but some require a restart.
In particular, changes to the user interface fonts require a restart.

This is because it appears only to be possible to change elements of the Swing
"pluggable look and feel" before components are created. There are various
kluges that are supposed to work around this, but none of them worked when I
tried.

## LIMITATION : No download manager

JGemini is happy to download multiple documents concurrently. However, there's
no way to know which transfers are ongoing, and which have completed. In the
long term, we need a separate download manager.

## LIMITATION : Necessary JVM version is unclear

The first version of JGemini supported JDK 1.8 (Java 8). It's a long time since
I used Java 8 in anger, and it's probably that I've subsequently used Java
features that are no longer supported. I'm reasonably happy that it works with
Java 11-25, but I'm not confident about earlier JVMs. 

It's also possible that, even if it were basically function, older JVMs may
lack support for up-to-date TLS versions, which would limit the servers JGemini
could talk to.

