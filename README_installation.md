# Installing and running JGemini

## Installing the Java JVM

For Linux and similar platforms, you can almost certainly get a Java JVM from
your package manager, if it isn't installed by default.  On Debian-like systems
you should be able to run

    # sudo apt install jdk-27

If you can run `java` at a prompt and not see `command not found`, you're
good to go.

For Windows and other platforms, you can get the JVM from
[Oracle's download site](https://www.oracle.com/java/technologies/downloads/).
If you're not familiar with Java, I recommend downloading the version with an executable 
installer (that is, the `.exe` version), because this will set up the Windows desktop
to associate Java JAR files automatically.

## Obtaining the JGemini binary

Get the JGemini JAR file [from GitHub](https://github.com/kevinboone/jgemini/releases).
If you're not building from source, you only need the `.jar` file. The same JAR
file serves all platforms.

Download the JAR file and save it to any convenient directory.

## Running JGemini on Linux

You can run JGemini from the command prompt like this:

    java -jar /path/to/jgemini-x.y.jar

where x.y is the version number (currently 3.0).

If your system associates Java JAR files with the `.jar` extension, you might
be able to launch JGemini from a file manager or program manager or whatever.
If you're running from a command line, you can specify a URL or file to load:

    java -jar /path/to/jgemini-1.0.jar file:///path/to/file.gmi

or

    java -jar /path/to/jgemini-1.0.jar gemini://host:port/path.gmi

For convenience, local filenames don't need a full URL -- just the filename
will do. JGemini will expand it to a URL internally. If the name of a local
file ends in `.gmi`, it is treated as Gemtext, otherwise as plain text in
platform-default encoding. The filename does not matter with content fetched
from a server, as the server will usually indicate the type of the content.

If you want to install "properly" on Linux, there's a sample installation
script and `.desktop` file in the `samples` directory of the source code
bundle.

## Running JGemini on Windows 

If you installed the Java JVM using the executable installer, just double-click the
JGemini JAR file in whatever directory you saved it. You could create a link to
the JAR on the Windows desktop if you want, but there's no particular need to.

## Uninstallation

Just delete the JAR file. 

