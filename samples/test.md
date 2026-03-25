# Markdown test page 

## Basic stuff

Here is **some emphatic text**. Here is some _underlined or italic_ text. Here is some
`technical` text (probably monospaced).

This is a short line with a line break after.  
This is the line that immediately follows it.

This is a long line that should be wrapped.
This is a long line that should be wrapped.
This is a long line that should be wrapped.
This is a long line that should be wrapped.
This is a long line that should be wrapped.
This is a long line that should be wrapped.

~~~
This is a preformated section, line 1
line 2
line 3
~~~

    This is monospaced because it starts with four indents.

> This is some quoted text.
> It should render as a single line.
 
This is a bulleted list:

* This is bullet 1
* This is bullet 2
* This is bullet 3

This section finishes with a horizontal rule.

***

## Links

This is a [link to my Gemini home page](/index.gmi), in the form `/index.gmi`. This is 
[the same link](index.gmi), but specified as `index.gmi`. 

And this is [a link to BBS](gemini://bbs.geminispace.org), specified as an absolute Gemini URL. 

Here is my email address: <mailto:lars@larsthebear.me>.

## Images

The following is an image with an absolute URL (and it ought to have a caption):

![Photo of a pocket media player](gemini://larsthebear.me/img/d2.jpg "Photo of a pocket media player")

Here is the same image as a relative URL:

![](img/d2.jpg)

This image should be a clickable link to a Gemtext page:

[![This is the alt text](img/pronto.jpg "This is the caption")](gemini://larsthebear.me/pronto.gmi)

## Tables

The following should be rendered as a table.

| Header row 1 | Header row 2|
| ------------ | ----------- |
| row 1 col 1  | row 1 col 2 |
| row 2 col 1  | row 2 col 2 |
| row 3 col 1  | row 3 col 2 |


