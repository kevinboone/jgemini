# JGemini release notes

## Version 2.0.3

This version adds an identity manager, which can create new self-signed client
certificates and keystores to contain them, as well as incorporating existing
keystores. Identities can be assigned to, and remove from, specific remote
hosts.
 
Please note that this change has necessitated a complete change to the format
of the client certificate information in the configuration file. Sorry about
that.

The documentation has been expanded considerably, and many of the dialog
boxes now have "Help" buttons that show the relevant documentation page.

Version 2.0.3 has a large number of internal changes, that should not be
visible to the user, but which should make the application easier to maintain
in the long term. 

## Version 2.0.2 release notes

This release has rudimentary bookmark support. See `docs/bookmarks.md`.

## Version 2.0.1 release notes

Please be aware that this version changes the location of the user
configuration file.  It's been moved into a Gemini-specific directory
`$HOME/.jgemini`, which is used by default for bookmarks and other saved state.
I thought grouping the JGemini-specific files this way would make them easier
to manage.

This release also introduces a built-in settings editor. There's still no way
to make most configuration changes other than by hacking on the configuration
file, but at least having a built-in editor makes it a bit less of a nuisance.

Certain things will now automatically overwrite the configuration file with new
values. For example, setting the current page as the home page overwrite the
`url.home` setting.

## Version 2.0.0 release notes

- JGemini now supports Gopher, nex, and Spartan, as well as Gemini
- Debug logging now has finer-grained control
- There's a "stop" button and menu action to cancel transfers in progress
- You can search directly from the URL bar, as regular web browsers do
  (but this can be turned off, if it's a nuisance)
- Window captions now show information about the site, rather than
  just the application name, making it easier to distinguish
  multiple windows
- URL history can now be saved and loaded (but this feature is not
  enabled by default)
- Viewer can now be zoomed in and out, and text is re-flowed to
  suit the size
- Viewer styling is completely different from version 1.0, and separate from the
  main configuration file. See docs/README.styling for more details.

