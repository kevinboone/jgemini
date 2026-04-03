# Installing and running JGemini

## Installation

JGemini is supplied as a single Java JAR file. It requires no specific installation:
just copy the JAR file to any convenient directory. You may want to create a 
[configuration file](config_file.md), but there are reasonable defaults.

The only additional set-up that might be necessary is to 
[install emoji fonts](emoji_support.md).

## Running JGemini without installing

So long as you have a Java JVM installed, you can run JGemini from a prompt
like this:

    java -jar /path/to/jgemini-x.y.jar

where x.y is the version number (currently 2.0).

If your desktop associates Java JAR files with the `.jar` extension, you might
be able to launch JGemini from a file manager or program manager,
just by clicking the JAR.

If you're running from a prompt line, you can specify a URL or file to load:

    java -jar /path/to/jgemini-2.0.jar gemini://larsthebear.me/ 

For convenience, local filenames don't need a full URL -- just the filename
will do. JGemini will expand it to a URL internally. If the name of a local
file ends in `.gmi`, it is treated as Gemtext, otherwise as plain text in the
platform's default encoding. The filename doesn't matter so much for content fetched
from a server, as the server will usually indicate the type of the content
along with its response.

## Installing with Linux desktop integration 

You'll need to create a script or batch file that launches JGemini using the
`java` command. You'll need to make it known to the platform by providing a
`.desktop` file, typically in `/usr/share/applications`.  There's a sample
`.desktop` file in the `samples` directory, along with sample icon files, which
typically go in `/usr/share/pixmaps` or one of its subdirectories.

You can run (as `root`) the `samples/install-linux.sh` script in the source
code bundle, which will do the basic set-up, copying the relevant files to the
usual places. However, you'll probably need to tell your desktop environment
that you've made changes, and there's no standard way to do that. 

None of this installation is necessary if you just want to run JGemini from a
command prompt.

[Documentation index](index.md)

 


