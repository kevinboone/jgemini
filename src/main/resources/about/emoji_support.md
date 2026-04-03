# Emoji support in JGemini

The widespread use of Unicode emojis in Gemini capsules presents a slight
problem: many platforms don't include fonts that have glyphs for these
characters. It's less of a problem on recent versions of Microsoft
Windows, as these include emoji fonts by default. On other platforms,
particularly Linux, you might need to install additional fonts. 

## Basics: installing an emoji font on Linux

Suitable fonts are Noto Emoji (Google) and Segoe UI Emoji (Microsoft).
Noto Emoji is redistributable; you can get the TTF file 
[from Google](https://fonts.google.com/noto/specimen/Noto+Emoji).
Segoe UI Emoji is not redistributable, but is nevertheless
widely available, for example 
from [online-fonts](https://online-fonts.com/fonts/segoe-ui-emoji).

The way to install fonts depends on the Linux distribution. If
you have the `font-manager` utility, you can just run, for example:

    font-manager --install NotoEmoji-Regular.ttf 

If you have only one emoji font, that should be sufficient set-up.

## More complex configuration

JGemini's default configuration provides for the user interface
to use the fonts "sans" and "emoji". This configuration will
use any font with "sans" in the name and, if this font does not
have the necessary glyphs, any font with "emoji" in the name.

It shouldn't be necessary to change this configuration unless

* You have an emoji font that doesn't have "emoji" in its
  name, or
* You have multiple emoji fonts, and you want to change
  their priority.

For more information on setting the user interface font, see
the [configuration file](config_file.md) page. For 
information on changing fonts in the main document viewer, see
the [styling](styling.md) page.

[Documentation index](index.md)

