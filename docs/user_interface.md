# JGemini's user interface

JGemini's user interface should be familiar if you've ever used a web browser
from the mid-90s. JGemini is deliberately, and unashamedly, old-fashioned.

To navigate to a site (capsule, gopherhole, etc), just enter (or copy/paste)
its URL into the URL bar at the top of the window. The "Home", "Back", and
"Reload" buttons on the toolbar do what you'd expect. 

If direct searching from the URL bar is enabled (by default it is), you can enter
a search string directly into the URL bar. JGemini has to guess at what is a
search term and what a URL, and sometimes it will get this wrong, which is why
this feature can be disabled.

Use the `X` button on the toolbar, or the _Go|Stop_ menu command, to cancel a
page transfer in progress.

Left-clicking a link loads the relevant document into the viewer window if it
is of a supported type, or prompts you for what action to take if it isn't.
Right-clicking brings up a menu with additional actions. For more information
on these topics, see the page on 
[handling non-document files and streams](non_document_files.md).

Keyboard navigation keys should behave in a familiar way; use ctrl+Home and
ctrl+End to move to the top and bottom of the document. 

To zoom in and out, use ctrl+[ and ctrl+]. Note that this doesn't change the
canvas or window size: text will be redrawn to suit the new font sizes. This
can take a little while, particularly if there are large, embedded images.

There are individual documentation pages for the various
[JGemini dialogs](dialogs.md).

[Documentation index](index.md)


