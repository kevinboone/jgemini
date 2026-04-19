# Media and streaming 

JGemini has no built-in media player. However, it does have features that make
it easier to use an external player with media streams.  `ffmpeg` and `vlc`
both work with JGemini on Linux, and at least some versions of these
applications work on at least some versions of Microsoft Windows.

It's not problematic to play a media _file_: just download it using JGemini,
and run whatever media player you like. If you know that the link you're
going to follow is to a file, not a stream, it's almost certainly better to
do this, than to stream the data. 

Streaming audio, such as Gemini radio stations, present more of a problem.

When you click a link to a media document in JGemini's viewer, you'll be asked
what to do with it. Options including saving a file, and streaming to a player.
There is no way JGemini can make the determination itself, because none of the
protocols that JGemini supports provide any content-length indication. JGemini
can't know -- it has no way to know -- whether the document it is receiving
from the server will ever complete, or will just continue indefinitely.

Consequently, JGemini leaves this decision to the user.

If you choose to stream the file, then JGemini will run the selected media
player application, and pass the network data from the server to its standard
input channel. It will keep doing this so long as data is available, and 
JGemini keeps running, or until you cancel the transfer in the download
manager.

It follows that the player application must be able to accept data on standard
input. Both `vlc` and `ffplay` can do this, but some Windows releases are
deficient in this area. You'll need to give a command-line that tells the
player to use standard input.  The default is `vlc -`, but on Windows you'll
need to change that to give the full pathname of the `.exe` file.

You can make this change using the Settings dialog, or by editing the
`stream.player` setting in the [configuration file](config_file.md).

Although JGemini does work with players that have no graphical user interface,
this isn't recommended: without a user interface, there's no way to control (or
even stop) the player once it's started, beyond cancelling the streaming
transfer in the download manager.

JGemini won't stop you opening multiple media streams, but your player might. 

Please be aware that streaming support is a new feature, and there is work
to to, to make it fully robust.

[Documentation index](index.md)



