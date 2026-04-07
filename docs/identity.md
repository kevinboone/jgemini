# The concept of "identity" 

> *Note*  
> For first steps using the built-in identity manager, please see
> [Getting started with identity](getting_started_with_identity.md).
> For more advanced configuration, see the page on 
> [client certificates](client_certs.md).

The Gemini protocol provides no interactive authentication mechanism: unlike
most web-based interactive services, you can't send user ID/email/password
information in response to a challenge from the server.

Instead, the protocol relies on TLS _client certificates_. To maintain
continuity between requests, and to control access to resources, a Gemini
browser is expected to send the same client certificate to a remote host, every
time the user makes a request on that host. 

A client certificate contains a minimum of information about the user --
usually just a user ID -- encoded in a cryptographically strong way. It would
be exceptionally difficult to impersonate another user by spoofing the user's
certificate. To be able to use a client certificate at all, a Gemini browser
has to have access to its accompanying _private key_ This is private
information that is never sent over the network. The client certificate and the
associated private key are nearly always generated as a pair, and used
together.

Client certificates have to be _signed_, that is, the certificate has to be
marked as trusted by some other certificate. In the Gemini world, client
certificates are almost always self-signed, that is, the certificate is
completely self-contained, relying on no other authority. Consequently, when
JGemini creates a client certificate, it always creates a self-signed one.

A client certificate and private key are far too complicated for a user to be
able to remember them, unlike a user ID/password combination. Certificates have
to be stored somewhere, in some recognized format.  JGemini, being a Java
program, stores certificates in Java keystores. Java supports several different
types of keystore and JGemini can read them all. But what it writes, when it
creates a new keystore, is a PKCS12 file, with a filename ending in `.p12`. You
could, if you wanted, read this keystore file with other software tools, 

Similarly you can, if you wish, use external tools to create a keystore in any
format Java understands, and tell JGemini to use it.  You might have to do that
if you want to import a client certificate from some other
application.

It's important to understand that, although a keystore can contain many
different certificates with many different purposes, JGemini doesn't expect to
find more than one client certificate in any keystore.  JGemini will always use
the first certificate it finds in a keystore, regardless how many there are. 

As a result, creating a new "identity" in JGemini amounts to creating a
keystore that contains exactly one client certificate. Because keystores are
password-protected, the identity also includes the password that accompanies
the keystore. The keystore location and password are stored in the
[configuration file](config_file.md) under a particular name, and it is that
name you assign to a particular remote host, to establish your identity on that
host.


[Documentation index](index.md)


