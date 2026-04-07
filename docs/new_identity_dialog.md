# "New identity" dialog 

>*Note*  
> If you're unsure what JGemini means by "identity", or what
> a client certificate is, please see 
> the page on [identity](identity.md) first.

You can use the _New identity_ dialog to have JGemini create a new client
certificate in a new keystore, without any external tools. If you
prefer to use external tools, see the page on 
[client certificates](client_certs.md).

## Using the _New identity_ dialog

The _New identity_ dialog collects three pieces of information:

* The identity name. This can be any combination of letters, digits,
  and the '\_' character. This name is meaningful only to you and
  JGemini; it's the name that appears in the identity list in
  the [Set identity](set_identity_dialog.md) dialog. JGemini will use
  the identity name in the configuration file, and as a filename for
  the keystore it will generate. By default, this file will end up
  in `$HOME/.jgemini/idents'.
* The name for the certificate. This must be in the form of an X.509
  'distinguished name', and it must be valid. However, for the purposes
  of Gemini, a simple name of the form "CN=My name" will be sufficient,
  and JGemini fills in the "CN=" part for you.
* A password, which will be used to protect the keystore that JGemini
  generates. 

Once you've created the identity, you can assign it to a particular remote
server in in the [Set identity](set_identity_dialog.md) dialog.

## Limitations of the identity manager

JGemini's certificate manager can only create self-signed certificates
(which is what most Gemini servers expect) with a ten-year validity
period. 

The _New identity_ dialog will try to prevent you entering unworkable
information, and it won't allow you to create duplicate identities.
However, you'll be able to break it if you try hard enough. 

If you've already created a keystore, perhaps using the Java `keytool`
program, don't use this dialog. You can use an existing keystore with the 
[Attach identity](attach_identity_dialog.md) dialog.

The identity manager provides no method to delete keystores.

[Documentation index](index.md)

