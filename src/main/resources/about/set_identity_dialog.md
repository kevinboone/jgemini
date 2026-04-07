# "Set identity" dialog 

> *Note*  
> If you're unsure what "identity" amounts to, please see the
> page on [identity](identity.md) in JGemini.

The Gemini protocol uses client certificates to authenticate users.  The 
_Set identity_ dialog allows you to assign a particular identity -- a
particular client certificate -- to a particular server.

To set the identity, you must have a page on the server open in the document
viewer. After setting the identity, you'll need to reload the page, to have
the change take effect.

> *Note*  
> Although you'll be viewing a particular page or URL, JGemini assigns identity
> at the hostname level, not at the page level. You can't assign different
> identities to different pages on the same host, nor to different protocols or
> ports on the same host.

If you don't assign any identity to a host -- which is the default -- JGemini
will not attempt to send a client certificate to that host.  If you assign an
identity to a host that does not require one, that's unlikely to cause a
problem.

Technically, a JGemini identity is a Java keystore containing a client
certificate and its private key, along with the password used to unlock the
keystore.  Each identity has a name, just for ease of reference.  Therefore, to
create an identity you need three things:

* A name. This can be any alphanumeric characters along with a few
  special characters like the underscore (\_). The name is arbitrary, and
  need not correspond to any identity information in the keystore. This name
  just appears in the list in the dialog.
* The filename of a Java keystore. This must be a file that already exists,
  whether you created it in JGemini or in some other way. You can use the
  file picker to navigate to the keystore file.
* A password. This can be any sequence of characters. Don't use a password
  that you use for anything else, as JGemini doesn't go to great lengths
  to conceal it. 

The _Set identity_ dialog lists all the identities (client certificates) so
far defined, if any. Along with these it shows two others: `<Unassigned>`
and `<None>`. Use the `Unassigned` identity to remove any previous identity
associated with a host. The `<None>` identity will be explained later.

If you want to use the same identity for _all_ hosts, create the 
special identity `any` in the 
[Attach identity to existing keystore](attach_identity_dialog.md) dialog.
The identity does not appear in the list in the _Set identity_ dialog box,
because there's no need: it's only applied when there is no better match.

You can assign the `<None>` identity to a host that you specifically
_don't_ want to send a certificate to, even if you've defined the 
`all` identity, which would otherwise apply. There's little reason 
to do this, but it's possible.

Click the _New_ button to create an entirely new identity. You'll need to do
this at least once, if you want to use client certificates, even if it's only
to create the `any` identity. You can choose to create the identity from an
existing Java keystore -- which may not have been created in JGemini -- or to
have JGemini create the client certificate and keystore from scratch.

You can also create and map identities by editing the 
[configuration file](config_file.md) with the settings editor.
See the [client certificates](client_certs.md) page for information
on doing this.

[Documentation index](index.md)

