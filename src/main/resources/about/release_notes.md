# JGemini release notes

## Version 3.0.0

Version 3 is radically different, both internally and externally,
from previous releases. 

The download logic has been rewritten completely, and there is a new download
manager with its own user interface.  JGemini no longer assumes that every
request will result in a response that will fit entirely in memory -- if you
choose to save a file, JGemini will transfer it in the background, and you can
follow the progress in the Downloads dialog box. The use of the download
manager means that the user no longer has to guess when a transfer has
completed, and JGemini will now resist being closed when there are ongoing
transfers.

There is also preliminary media streaming support in this version, using a
selectable media player application as a helper. At present, VLC and FFMPEG are
known to work (including on Windows). Streaming uses the same download logic as
file downloads, and you can control streaming operations using the download
manager.

Less substantial changes include a new filtering URL bar, extended
documentation, checks to prevent overwriting existing files when downloading,
and a huge number of bug fixes.

## Version 2.0.4

* Added a way to see some information from the server's TLS certificate.
* Added a 'settings' dialog box, which ought to make the former
  raw settings editor mostly superfluous
* Removed a bunch of superfluous menu commands, and rearranged some others
* Window size is now saved when closing a window
* Fixed a number of stupid bugs
* Added a 'useful links' page

## Version 2.0.3 

* Added a new identity manager, which can create new self-signed client certificates
  and keystores to contain them, as well as incorporating existing keystores.
  Identities can be assigned to, and removed from, specific remote hosts.  Please
  note that this change has necessitated a complete change to the format of the
  client certificate information in the configuration file. Sorry about that.
* The documentation has been expanded considerably, and many of the dialog
  boxes now have "Help" buttons that show the relevant documentation page.
* Changed the button accelerators from ctrl-something to the platform
  default (typically alt-something).
* Moved _all_ the user-visible text strings to resource bundles. What a
  horrible job - I should have done it properly from the start

## Version 2.0.2

* Added bookmark support, with a rudimentary bookmark editor.
* Updated documentation, particularly related to emoji support
* Fixed bug in list formatting in Gemtext
* Put limit on status line in Gemini response, to protect
  from broken server
* Fixed (maybe) broken handling of selectors beginning "/" in Gopher
* Added converter for Atom feeds
* Updated documentation concerning emoji fonts
* Added open-source licences to the built-in documentation

## version 2.0.1

* Added "Set as home page" facility
* Changed location of properties file, in a new directory, which
  will also store URL history (but this feature is not enabled by default)
* Added a settings editor that operates directly on the configuration file, although
  this is getting less important as the user interface expands.

## Version 2 

* JGemini now supports Gopher, nex, and Spartan, as well as Gemini
* Debug logging now has finer-grained control
* There's a "stop" button and menu action to cancel transfers in progress
* You can search directly from the URL bar, as regular web browsers do
  (but this can be turned off, if it's a nuisance)
* Window captions now show information about the site, rather than
  just the application name, making it easier to distinguish
  multiple windows
* URL history can now be saved and loaded (but this feature is not
  enabled by default)
* Viewer can now be zoomed in and out, and text is re-flowed to
  suit the size
* Viewer styling is completely different from version 1.0, and separate from the
  main configuration file. See docs/README.styling for more details.

[Documentation index](index.md)

