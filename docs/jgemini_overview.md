# JGemini overview

JGemini is a browser for various [small net protocols](smallnet.md).
At present it supports Gemini, Gopher, Spartan, and Nightfall Express, but
others should be easy to add. It can display plain text, Gemtext, 
Markdown, and images; documents with other formats are handed off to
the desktop to handle.

JGemini is implemented in Java, and should run on any platform with a
graphical display and a reasonably modern (JDK 11 or later) Java JVM.

JGemini is distributed as a single Java JAR file, and requires 
[no specific installation](installing_and_running.md). 

## Features

- Supplied as a single Java JAR file
- Supports Gemini, Spartan, Gopher, and `nex` protocols
- Displays Gemtext, CommonMark Markdown, and plain text
- Renders local files as well as remote content
- Authenticates using per-server client certificates
- Has a built-in client certificate manager, which can create new certificates and
  incorporate existing ones
- Uses anti-aliased font rendering for a smoother text appearance
- Text styling can be configured to suit the display and user preference
- Fetches documents asynchronously to improve user interface responsiveness
- Text selection with cut-and-paste
- Search in document
- Downloaded documents can be saved to file
- Supports multiple windows
- Search directly from the URL bar
- Saves almost no state by default, for privacy
- Reasonably comprehensive documentation, with built-in viewer 
- Rudimentary bookmark support
- Interprets Atom feeds


[Documentation index](index.md)


