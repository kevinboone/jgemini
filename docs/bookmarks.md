# Bookmarks

JGemini has rudimentary bookmark support.  Unless you change the location in
the [configuration file](config_file.md), bookmarks are stored in a single file
`$HOME/.jgemini/bookmarks.gmi`.

The bookmarks file is a Gemtext document. Every time you add a bookmark,
JGemini writes a new Gemtext link to the end of the bookmarks file.  The format
of the link is

    => {URI} {text}

When you bookmark a page, JGemini tries to infer the link text from the
contents of the page and, if it can't, it derives something from the page URI.
You can edit the text to be more descriptive if you prefer. You can also
re-order the links, and add headings and explanations. 

You can edit the bookmarks file using any text editor, or using the built-in
editor which you can active using the Bookmarks|Edit menu command. A proper
text editor will be more versatile, but using the built-in editor saves you the
hassle of hunting around for the file.

When you show bookmarks using Bookmarks|Show, JGemini just renders the bookmark
file as if it were any other Gemtext document.


[Documentation index](index.md)



