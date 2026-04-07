# "Attach identity to keystore" dialog 

> *Note*  
> If you're unsure what JGemini means by "identity", please see 
> the page on [identity](identity.md) first.

Use the _Attach identity to keystore_ dialog to create a new identity, based on
the client certificate in an _existing_ Java keystore.  You can then assign
that new identity to a particular remote server in in the 
[Set identity](set_identity_dialog.md) dialog.  You'll need to do this if, for
example, you want to import an identity you created using some other
application.

If you want to create a new identity from scratch, which will create the
keystore -- use the [New identity](new_identity_dialog.md) dialog.

In the _Attach identity to keystore_ dialog you can use the file picker to
navigate to the keystore file.  Nothing stops you selecting a file that isn't a
Java keystore, but you'll get some bizarre error messages if you do.

You'll need to enter a name, which is what will appear in the list of
identities in the _Set identity_ dialog.

You'll also need to enter the password for the keystore, which you would have
assigned when you created it.

[Documentation index](index.md)
