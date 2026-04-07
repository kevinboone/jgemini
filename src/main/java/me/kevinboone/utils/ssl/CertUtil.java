/*=========================================================================
  
  JGemini

  CertUtil

  Various utilities for managing keystores and passwords. This
  class makes extensive use of the BouncyCastle library, and 
  the BC provider must have been registered before calling any
  of these methods. For example:

  Security.addProvider (new BouncyCastleProvider());  

  Copyright (c)2027 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.utils.ssl;

import java.io.*;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import java.util.Date;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.*;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.*;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CertUtil
{

/*=========================================================================
  
  makeKeyPair

  A helper function to make a key pair with the specificed algorith
    and key length.

=========================================================================*/
public static KeyPair makeKeyPair (String algorithm, int keysize) 
   throws NoSuchAlgorithmException 
  {
  KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance 
    (algorithm, new BouncyCastleProvider());
  keyPairGenerator.initialize (keysize); 
  return keyPairGenerator.generateKeyPair();
  }

/*=========================================================================
  
  makeKeyStore

  Make a Java keystore and populate it with a key pair and a certificate. 

=========================================================================*/
public static KeyStore makeKeyStore (String type, KeyPair keyPair, 
    char[] pwChars, String alias, X509Certificate x509Cert) 
      throws KeyStoreException, IOException, NoSuchAlgorithmException,
        CertificateException
  {
  KeyStore keyStore = KeyStore.getInstance (type);
  keyStore.load (null, pwChars);
  keyStore.setKeyEntry (alias, keyPair.getPrivate(), pwChars, 
    new X509Certificate[] {x509Cert});
  return keyStore;
  }

/*=========================================================================
  
  makeSelfSignedCert 

  Make a certificate with ten-year validity, starting now.

=========================================================================*/
public static X509Certificate makeSelfSignedCert (KeyPair keyPair, 
      X500Name subject, String signatureAlgorithm) 
    throws IOException, CertificateException, OperatorCreationException
  {
  BigInteger sn = new BigInteger (Long.SIZE, new SecureRandom());

  X500Name issuer = subject;

  PublicKey keyPublic = keyPair.getPublic();
  byte[] keyPublicEncoded = keyPublic.getEncoded();
  SubjectPublicKeyInfo keyPublicInfo = SubjectPublicKeyInfo.getInstance 
    (keyPublicEncoded);

  long notBefore = System.currentTimeMillis();
  long notAfter = notBefore + (1000L * 3600 * 24 * 365 * 10); // Ten years

  try 
    (
    ByteArrayInputStream ist = new ByteArrayInputStream (keyPublicEncoded);
    ASN1InputStream ais = new ASN1InputStream(ist)
    )
    {
    ASN1Sequence asn1Sequence = (ASN1Sequence) ais.readObject();

    SubjectPublicKeyInfo subjectPublicKeyInfo 
      = SubjectPublicKeyInfo.getInstance(asn1Sequence);
    SubjectKeyIdentifier subjectPublicKeyId 
      = new BcX509ExtensionUtils().createSubjectKeyIdentifier 
        (subjectPublicKeyInfo);

    X509v3CertificateBuilder certBuilder 
      = new X509v3CertificateBuilder (issuer, sn, new Date(notBefore), 
        new Date (notAfter), subject, keyPublicInfo);
    ContentSigner contentSigner 
      = new  JcaContentSignerBuilder 
        (signatureAlgorithm).build(keyPair.getPrivate());

    X509CertificateHolder certHolder = certBuilder
      .addExtension (Extension.basicConstraints, 
        true, new BasicConstraints(true))
      .addExtension (Extension.subjectKeyIdentifier, false, subjectPublicKeyId)
      .build (contentSigner);

     return new JcaX509CertificateConverter().setProvider 
       (new BouncyCastleProvider()).getCertificate (certHolder);
     }
   }

/*=========================================================================
  
  saveKeyStore 

  Write a keystore to file, applying the password.

=========================================================================*/
public static void saveKeyStore (String filename, 
    KeyStore keyStore, char[] pwChars) throws IOException, 
      NoSuchAlgorithmException, KeyStoreException, CertificateException
  {
  FileOutputStream fos = new FileOutputStream (filename);
  keyStore.store (fos, pwChars);
  fos.close();
  }

/*=========================================================================
  
  getSubject

  Wraps a DN in a Bouncy Castle X500 object 

=========================================================================*/
private static X500Name getSubject (String dn) 
  {
  return new X500Name (dn);
  }

/*=========================================================================
  
  makeSelfSignedCertKeystore

  type:     JKS or PKCS12
  filename: the output filename, which should ideally have an extension
            that matches the type (e.g., .jks or .p12)
  dn:       The distinguished name in the certificate, e.g., "CN=test" 
  alias:    The name of the certificate in the keystore
  password: The keystore password. This can be empty, but a keystore
            always has a password, even if it's an empty one.

=========================================================================*/
public static void makeSelfSignedCertKeystore (String type, 
    String filename, String dn, String alias, String password) 
      throws Exception 
  {
  try
    {
    KeyPair keyPair = makeKeyPair ("RSA", 2048);
    X509Certificate x509Cert = makeSelfSignedCert (keyPair, 
      getSubject (dn), "SHA256WithRSA");
    char[] pwChars = password.toCharArray();
    KeyStore keyStore = makeKeyStore (type, keyPair, pwChars, alias, x509Cert);
    saveKeyStore (filename, keyStore, pwChars);
    }
  catch (NoSuchAlgorithmException e){} // Can't happen
  }

/*=========================================================================
  
  verifyKeystore 

  Check that the keystore is a valid file, and the that specified
    password opens it

=========================================================================*/
  public static void verifyKeystore (String filename, String password)
      throws IOException, KeyStoreException, CertificateException 
    {
    try
      (
      FileInputStream fis = new FileInputStream (filename);
      )
      {
      char[] pwChars = password.toCharArray();
      KeyStore keyStore = KeyStore.getInstance (KeyStore.getDefaultType());
      keyStore.load (fis, pwChars);
      }
    catch (NoSuchAlgorithmException e)
      {
      // This can't happen -- it's a bogus throw
      }
    }

}

