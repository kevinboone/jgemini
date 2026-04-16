# Issues, bugs, and limitations

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

## LIMITATION: Irritating UI gremlins

Java Swing has some odd limitations, which result in the UI not looking as 
nice as it could. For example, if you put a Box layout in a JScrollPane, 
and there aren't enough components in the Box to engage the vertical scroll,
then Swing spaces the components vertically, rather than stacking them. 

It's not a logical problem -- it's just not what we're used to seeing. This
particularly limitation is well-known, and there's no easy solution to it
except for writing your own layout manager.

There are other places where the UI layout is not as orderly as it could be.
Message dialog boxes don't wrap their text unless you make them a fixed size,
in which case you might end up with scroll bars.  And so on. 

None of this is catastrophic -- it just makes the UI look a bit amateurish
in places.

## LIMITATION : Necessary JVM version is unclear

The first version of JGemini supported JDK 1.8 (Java 8). It's a long time since
I used Java 8 in anger, and it's probably that I've subsequently used Java
features that are no longer supported. I'm reasonably happy that it works with
Java 11-25, but I'm not confident about earlier JVMs. 

It's also possible that, even if it were basically function, older JVMs may
lack support for up-to-date TLS versions, which would limit the servers JGemini
could talk to.

