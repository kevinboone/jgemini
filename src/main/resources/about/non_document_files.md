# Handling non-document files in JGemini 

JGemini's viewer window handles text and images. Of course, small-net documents,
even from Gopher, can present a browser with files of many other types.

If you enter a URL, or click a link, that corresponds to a file of a type
that Gemini can't handle internally, you'll see a menu of choices.

- "Hand off to desktop". JGemini saves the document to a temporary file, 
  and invokes the desktop's default action for it. The desktop will
  usually launch an application; JGemini doesn't allow you to choose
  the application, but your operating system probably does.
- "Prompt for a filename and save". You can choose where to save the
  file.
- "Save without prompting". JGemini saves the file in a default location,
  choosing a new filename if a file of the same name exists. The default
  download location is usually `$HOME/.jgemini/downloads`.
- "Stream to player". JGemini will run an application and pass the 
  remote data to it, at whatever speed the application accepts.
  This provides a rudimentary method of media stream, using a helper
  application like VLC or `ffplay`. For more information, see the
  [page on media and streaming](media_and_streaming.md).

If you see this menu, you'll also see a check-box which controls whether you
want JGemini to perform the same action for the same document type in future.
If you change your mind, you can use the [Settings dialog](settings_dialog.md)
to reset the file handling choices.

Rather than left-clicking a link and then making a choice how to handle the
file, you can right-click, and see other choices. These choices will depend on
the type of the document, as best JGemini can determine it. Since there's no
way for JGemini to know what a link provides except by fetching it, it will use
the link filename as a guide to what choices to offer. For example, only common
media formats will get a "Stream to player" option. The right-click menu also
allows a choice for documents that JGemini supports internally: you can open
them in a different window, for example.

[Documentation index](index.md)



