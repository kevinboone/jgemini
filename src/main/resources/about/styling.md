# Styling JGemini's document window

You can style JGemini's main document window using CSS stylesheets. Be aware,
though, that Java's CSS support is pretty ancient, and only a limited range of
styles are useable. In addition, "modern" features, like being able to set
sizes in "em" units, aren't supported.

JGemini has two default themes, called 'light' (the default) and 'dark'. 
To select one of these, use the following setting in the 
[configuration file](config_file,md):

    ui.document.theme=light
or
    ui.document.theme=dark

To change the font sizes with the built-in themes, use a setting such as

    ui.document.font.size=14

This size is in pixels, and is for ordinary, unformatted text. Elements
like headings will have sizes based on this figure, scaled appropriately.

The only other supported these setting is `custom`. If you use this setting, you
must also supply a stylesheet as a CSS file.

    ui.document.custom.css=/path/to/stylesheet.css

The CSS files for the built-in themes are in the `samples/` directory in the
source code bundle. You could use these as the basis for customization.  Note
that the built-in themes have place-holders for sizes, so that multiple sizes
can be set using one configuration entry; custom themes can use these
place-holders, too. Of course, you could just the size of each element
independently, if you prefer.

JGemini uses the following styleable elements: 

- "a" -- links 
- "blockquote" -- block quotes
- "body" -- background and layout
- "code" -- technical terms, 
- "h1...h3" -- headings 
- "p" -- ordinary text
- "pre" -- pre-formatted text
- "ul" -- lists

The fonts that can be applied to these styles are JVM fonts, which may be named
differently to platform fonts (and, in some cases, are more extensive). To get
a list all JVM fonts, run JGemini like this: 

    java -Djgemini.dumpfonts -jar jgemini-1.0.jar

You can test changes to a custom stylesheet by refreshing the display (ctrl+R).

[Documentation index](index.md)

