# Bookmarks

JGemini has simple bookmark support.  Unless you change the location in the
[configuration file](config_file.md), bookmarks are stored in a single file
`$HOME/.jgemini/bookmarks.gmi`. This file is also cached in memory to avoid
repeated file reads, which has implications for editing (see below).

The bookmarks file is a Gemtext document. Every time you add a bookmark,
JGemini writes a new Gemtext link to the end of the bookmarks file.  The format
of the link is

    => {URI} {text}

When you bookmark a page, JGemini tries to infer the bookmark text from the
contents of the page and, if it can't, it derives something from the page URI.
You can edit the text to be more descriptive if you prefer. You can also
re-order the links, and add headings and explanations. 

You can edit the bookmarks file using any text editor, or using the built-in
editor which you can activate using the Bookmarks|Edit... menu command. A
proper text editor will be more versatile, but using the built-in editor saves
you the hassle of hunting around for the bookmarks file. In addition, using the
built-in editor allows JGemini to keep its internal cache in sync with the file
contents.  Editing the bookmarks file with a text editor, with JGemini running,
could have odd results.

When you show bookmarks using Bookmarks|Show all, JGemini just renders the
bookmark file as if it were any other Gemtext document.

JGemini also shows a portion of the bookmarks file in the bookmarks menu.  By
default it shows the first ten items, but you can change this by modifying the
setting `bookmark.max.menu` in the [configuration file](config_file.md).  The
first ten items are assigned the keyboard accelerators '0' to '9'.

It's therefore worth editing the bookmarks, to put your most commonly-used
ones at the top.


[Documentation index](index.md)



