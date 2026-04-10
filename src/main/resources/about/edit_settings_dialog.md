# "Edit settings" dialog

> *Note*  
> As more and more of JGemini's functionality is managed by the main user
> interface, there's less need to edit settings manually.  Incautious hacking
> on the configuration file can break things in non-obvious ways.

The settings editor allows for direct editing of the user settings
(configuration) file, and allows for changes that can't be made directly by the
JGemini user interface. To see the settings editor, use the 
_Tools|Settings|Edit_ menu command.

The settings file is usually `$HOME/.jgemini/jgemini.properties`.

If you prefer, you can edit the settings file with an ordinary text editor. If
you do this when JGemini is running, use the _Tools|Settings|Reload_ menu
command to make JGemini reload the file and update its internal state.

Please be aware that changes to some settings won't take effect unless you
restart JGemini.

For a full list of configuration settings, see the
[configuration_file](config_file.md) page.


[Documentation index](index.md)


