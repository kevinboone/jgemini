/**
<p>
This package contains classes that handle the various network protocols that
JGemini supports -- Gemini, Gopher, etc. Each protocol has a handler which is a
subclass of URLConnection. There are also some exceptions in this package,
which perhaps ought to be elsewhere. 
</p>
<p>
For each protocol we have a subclass of URLConnection that does the real
work, and a subclass of URLStreamHandler that instantiates it. The
various URLStreamHandler instances are registered with the JVM using
JGeminiStreamHandlerFactory. 
</p>
*/
package me.kevinboone.jgemini.protocol;

