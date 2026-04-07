# JGemini's user interface

JGemini's user interface should be familiar if you've ever used a web browser
from the mid-90s.

To navigate to a site (capsule, gopherhole, etc), just enter (or copy/paste) its
URL into the URL bar at the top of the window. The "Home" and "Back" buttons
on the toolbar do what you'd expect. 

JGemini will display the amount of data it's received from the server, in
increments of 16kB. Many documents used with small net protocols are smaller
than this, so all you'll see is "Loading..." in the status bar.

Use the `X` button on the toolbar, or the _Go|Stop_ menu command, to cancel a
transfer in progress.

Left-clicking a link loads the relevant document into the viewer window if it
is of a supported type, or passes it to the desktop if not. Right-clicking
brings up a menu with additional actions, including 'Download'.

Keyboard navigation keys should behave in a familiar way; use ctrl+Home and
ctrl+End to move to the top and bottom of the document. 

To zoom in and out, use ctrl+[ and ctrl+]. Note that this doesn't change the
canvas or window size: text will be redrawn to suit the new font sizes. This
can take a little while, particularly if there are large, embedded images.

There are individual documentation pages for the various
[JGemini dialogs](dialogs.md).

[Documentation index](index.md)


