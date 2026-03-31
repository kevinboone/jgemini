# JGemini release notes

## Version 2.0.2 release notes

This release has rudimentary bookmark support. See `docs/bookmarks.md`.

## Version 2.0.1 release notes

Please be aware that this version changes the location of the user configuration file.
It's been moved into a Gemini-specific directory `$HOME/.jgemini`, which is used
by default for bookmarks and other saved state. I thought grouping the JGemini-specific
files this way would make them easier to manage.

This release also introduces a built-in settings editor. There's still no way to
make most configuration changes other than by hacking on the configuration file,
but at least having a built-in editor makes it a bit less of a nuisance.

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

