# Getting started with identity

Usually you'll need to think about "identity" when you're looking at a Gemini
site or service that requires authentication. The service will report, perhaps,
that your client needs to submit a client certificate.

If you're unsure what a client certificate is, please see the page on
[identity](identity.md). However, for simple scenarios you can just have the
built-in identity manager carry out the necessary steps.

You'll need to start with the page or resource that requires authentication
open in the document viewer. Then click the "person" icon in the toolbar, or
use the _Tools|Set/manage identity_ menu command.

This will bring up the identity manager, set up for the current page. You'll
see a list of possible identities; if this is your first time using JGemini,
the list will contain only `<Unassigned>` and `<None>`, neither of which helps
in this situation.

You'll need to create a new identity, and have it appear in the identity list. To
do this click _New_ then, from the pop-up menu, _Create a new identity in a new
keystore_.

This will bring up the [New identity](new_identity_dialog.md) dialog.  Enter a name
for the identity -- this is the name that will appear in the list of
identities. Then enter a name for the client certificate; this can be anything
you like, but it's the name you'll be presenting to the remote server, so it
needs a bit of thought.  This name will almost always have to begin with `CN=`,
so the dialog fills this part in automatically. In most cases "CN=My Name"
will be sufficient.

Assign a password to the keystore that JGemini is going to generate.  You don't
necessarily need to remember it, since JGemini will store it. Knowing the
password is only important if you plan to manipulate the keystore with other
tools.

When you click _Submit_, all being well, JGemini will generate the
necessary files, and return to the _Set identity_ dialog, where you can assign
the newly-created identity to the remote host.

Please note that JGemini's identity manager is somewhat rudimentary. 
It can create new identities, switch identities for remote hosts, 
and assign an existing keystore to an identity. What it can't do is
to delete existing identity entries. To do that you'll need
to edit the [configuration file](config_file.md).


[Documentation index](index.md)


