# "Select action" dialog 

You'll see this dialog if you try to open a document that JGemini can't handle
internally. You can indicate what action JGemini should take and, optionally,
specify that JGemini should do the same thing with all documents of the same
type.

If you choose to download without prompting for a filename, JGemini generates a
filename in its 'downloads' directory, usually `$HOME/.jgemini/downloads`.
JGemini will make the filename from the 'path' part of the URL and, if there is
already a file with the same name, it will add a number to the name.

If you choose to hand the document off to the desktop, JGemini will download to
a temporary file in the platform's default temporary directory, and then try to
delete the file when it closes.

Your choice for specific document types is stored in the 
[configuration file](config_file.md).

[Documentation index](index.md)


