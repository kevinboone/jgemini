# JGemini -- using client certificates

>*Note*  
> If you're unsure what JGemini means by "identity", or what a client
> certificate is, please see the page on [identity](identity.md) first.

Some Gemini-based services identify the user by a client certificate. They have
to, since Gemini doesn't provide any other method of authentication.

As with everything else in JGemini, you can control which certificate to send
to each server by editing the [configuration file](config_file.md). 
However, in most cases you won't need to: it's usually easier to manage 
identity using the [Set identity](set_identity_dialog.md) dialog.  
For first steps using JGemini's
built-in identity manager, please see 
[Getting started with identity](getting_started_with_identity.md).

The rest of this page describes the technical details of managing client
certificates in JGemini, should you need to do it manually.

Each client certificate must be placed in its own Java keystore, along with its 
associated private key. You can use the same keystore file with multiple
services, or with all services if you wish. It doesn't hurt to send a client
certificate to a Gemini server that doesn't need one -- it will simply be
ignored. However, this could notionally be a [privacy concern](privacy.md) (but
probably isn't).

JGemini supports per-host assignment of client certificates, but not per-page
or per-protocol assignment.

## Generating a client certificate keystore manually

> *Note*  
> You can use the built-in identity manager to do this for simple scenarios.

You can generate a new Java keystore at the command line as follows:

    keytool -alg rsa -keystore {keystore_file.jks}

You'll be prompted for the identity information to include. The resulting file will
include both the certificate and the matching private key. It doesn't matter
what the certificates are named within the keystore, because JGemini assumes that
there is exactly one client certificate in each keystore file.

During keystore creation, you'll be prompted to set a password, which you should 
remember for later.

## Importing a certificate from another application

You can use this method to important an identity from Lagrange, for example.

Most non-Java applications store certificates and keys in `.pem` format. These
are text files, and you should concatenate the public key(s) and private keys
into a single file, then convert this combined `.pem` into a PKCS12 certificate
using `openssl`:

    openssl pkcs12 -export -inkey private_key.pem -in public_cert.pem -name foo -out foo.p12

Finally, convert this new PKCS12 certificate to Java JKS format:

    keytool -importkeystore -srckeystore foo.p12 -srcstoretype pkcs12 -destkeystore foo.jks

> *Note*  
> Some versions of the Java JVM can read PKCS12 keystores, so this conversion
>   might not be needed. It will do no harm to convert the keystore, though.

During keystore conversion, you'll be prompted to set a password, which you
should remember for later. In the command above, `foo.jks` is the JKS keystore
that JGemini needs to know about. You can put this in any convenient directory. 

## Using an existing keystore with JGemini

You can do this using the built-in identity manager, or by editing the user
configuration file.

### Assigning an identity using the identity manager

With the relevant remote site open in the document viewer, select
the _Tools/Set/Manage identity..._ menu command. Click _New_ and
_Assign identity to an existing keystore_. In the
[New identity](new_identity_dialog.md) dialog, assign the identity
a name, locate the keystore file using the file picker, and 
enter the keystore password. Then you can assign this identity to remote
hosts.

### Editing the configuration file

As an alternative to using the built-in identity manager, you can edit the
configuration file. An entry in the configuration file that maps a particular
hostname to an identity looks this:

    ident.{hostname}={my_identity_on_hostname}

For example:

    ident.bbs.geminispace.org=my_name

That is, the left-hand side of the line gives the hostname, preceded by
`ident.`, while the right-hand side is the name of the identity to be applied
to that host. The identity name can be any string. To map the identity string to
some particular client certificate:

    clientcert.{my_identity_on_hostname}={certificate_file.jks} {keystore_password}

For example: 

    clientcert.my_name=/path/to/bbs.jks changeit

Naturally, this configuration scheme will fail if there's white-space in the
filename. The keystore password is the one you gave to `keytool` when you
created or imported the certificate.

You can also specify a fall-back identity, which JGemini will use if no other
identity applies to a particular server: 

    clientcert.any=/path/to/my_keystore.jks changeit

If a hostname has no assigned client identity, and there is no fallback, 
JGemini won't send a certificate to that host.

What if you want to send a client certificate to every host _except_
some few? JGemini caters for this situation as well. We define a fallback
identity as above, and then assign an identity of `none` to a specific
host:

    ident.acme.com=none

This scheme is a little complicated, but it allows many-to-many mapping between
hostnames and identities. If you have a single client certificate, and you
want to send it to all hosts, the only configuration needed is 
`clientcert.any`, as shown above.


[Documentation index](index.md)


