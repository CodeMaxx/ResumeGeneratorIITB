/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1OutputStream
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEREncodable
 *  org.bouncycastle.asn1.DEREncodableVector
 *  org.bouncycastle.asn1.DERInteger
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERObject
 *  org.bouncycastle.asn1.DERObjectIdentifier
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.DERString
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.DERUTCTime
 *  org.bouncycastle.jce.provider.X509CRLParser
 *  org.bouncycastle.jce.provider.X509CertParser
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DEREncodableVector;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.jce.provider.X509CertParser;

public class PdfPKCS7 {
    private byte[] sigAttr;
    private byte[] digestAttr;
    private int version;
    private int signerversion;
    private Set digestalgos;
    private Collection certs;
    private Collection crls;
    private X509Certificate signCert;
    private byte[] digest;
    private MessageDigest messageDigest;
    private String digestAlgorithm;
    private String digestEncryptionAlgorithm;
    private Signature sig;
    private transient PrivateKey privKey;
    private byte[] RSAdata;
    private boolean verified;
    private boolean verifyResult;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private static final String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
    private static final String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
    private static final String ID_MD5 = "1.2.840.113549.2.5";
    private static final String ID_MD2 = "1.2.840.113549.2.2";
    private static final String ID_SHA1 = "1.3.14.3.2.26";
    private static final String ID_RSA = "1.2.840.113549.1.1.1";
    private static final String ID_DSA = "1.2.840.10040.4.1";
    private static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
    private static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    private static final String ID_SIGNING_TIME = "1.2.840.113549.1.9.5";
    private static final String ID_MD2RSA = "1.2.840.113549.1.1.2";
    private static final String ID_MD5RSA = "1.2.840.113549.1.1.4";
    private static final String ID_SHA1RSA = "1.2.840.113549.1.1.5";
    private static final String ID_ADBE_REVOCATION = "1.2.840.113583.1.1.8";
    private String reason;
    private String location;
    private Calendar signDate;
    private String signName;

    public PdfPKCS7(byte[] arrby, byte[] arrby2, String string) {
        try {
            X509CertParser x509CertParser = new X509CertParser();
            x509CertParser.engineInit((InputStream)new ByteArrayInputStream(arrby2));
            this.certs = x509CertParser.engineReadAll();
            this.signCert = (X509Certificate)this.certs.iterator().next();
            this.crls = new ArrayList();
            ASN1InputStream aSN1InputStream = new ASN1InputStream((InputStream)new ByteArrayInputStream(arrby));
            this.digest = ((DEROctetString)aSN1InputStream.readObject()).getOctets();
            this.sig = string == null ? Signature.getInstance("SHA1withRSA") : Signature.getInstance("SHA1withRSA", string);
            this.sig.initVerify(this.signCert.getPublicKey());
        }
        catch (Exception var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    public PdfPKCS7(byte[] arrby, String string) {
        try {
            DERObject dERObject;
            X509Certificate x509Certificate;
            ASN1Sequence aSN1Sequence;
            DERObjectIdentifier dERObjectIdentifier;
            ASN1InputStream aSN1InputStream = new ASN1InputStream((InputStream)new ByteArrayInputStream(arrby));
            try {
                dERObject = aSN1InputStream.readObject();
            }
            catch (IOException var5_6) {
                throw new IllegalArgumentException("can't decode PKCS7SignedData object");
            }
            if (!(dERObject instanceof ASN1Sequence)) {
                throw new IllegalArgumentException("Not a valid PKCS#7 object - not a sequence");
            }
            ASN1Sequence aSN1Sequence2 = (ASN1Sequence)dERObject;
            DERObjectIdentifier dERObjectIdentifier2 = (DERObjectIdentifier)aSN1Sequence2.getObjectAt(0);
            if (!dERObjectIdentifier2.getId().equals("1.2.840.113549.1.7.2")) {
                throw new IllegalArgumentException("Not a valid PKCS#7 object - not signed data");
            }
            ASN1Sequence aSN1Sequence3 = (ASN1Sequence)((DERTaggedObject)aSN1Sequence2.getObjectAt(1)).getObject();
            this.version = ((DERInteger)aSN1Sequence3.getObjectAt(0)).getValue().intValue();
            this.digestalgos = new HashSet();
            Enumeration enumeration = ((ASN1Set)aSN1Sequence3.getObjectAt(1)).getObjects();
            while (enumeration.hasMoreElements()) {
                aSN1Sequence = (ASN1Sequence)enumeration.nextElement();
                dERObjectIdentifier = (DERObjectIdentifier)aSN1Sequence.getObjectAt(0);
                this.digestalgos.add(dERObjectIdentifier.getId());
            }
            aSN1Sequence = new X509CertParser();
            aSN1Sequence.engineInit((InputStream)new ByteArrayInputStream(arrby));
            this.certs = aSN1Sequence.engineReadAll();
            dERObjectIdentifier = new X509CRLParser();
            dERObjectIdentifier.engineInit((InputStream)new ByteArrayInputStream(arrby));
            this.crls = dERObjectIdentifier.engineReadAll();
            ASN1Sequence aSN1Sequence4 = (ASN1Sequence)aSN1Sequence3.getObjectAt(2);
            if (aSN1Sequence4.size() > 1) {
                DEROctetString dEROctetString = (DEROctetString)((DERTaggedObject)aSN1Sequence4.getObjectAt(1)).getObject();
                this.RSAdata = dEROctetString.getOctets();
            }
            int n = 3;
            while (aSN1Sequence3.getObjectAt(n) instanceof DERTaggedObject) {
                ++n;
            }
            ASN1Set aSN1Set = (ASN1Set)aSN1Sequence3.getObjectAt(n);
            if (aSN1Set.size() != 1) {
                throw new IllegalArgumentException("This PKCS#7 object has multiple SignerInfos - only one is supported at this time");
            }
            ASN1Sequence aSN1Sequence5 = (ASN1Sequence)aSN1Set.getObjectAt(0);
            this.signerversion = ((DERInteger)aSN1Sequence5.getObjectAt(0)).getValue().intValue();
            ASN1Sequence aSN1Sequence6 = (ASN1Sequence)aSN1Sequence5.getObjectAt(1);
            BigInteger bigInteger = ((DERInteger)aSN1Sequence6.getObjectAt(1)).getValue();
            ASN1TaggedObject aSN1TaggedObject = this.certs.iterator();
            while (aSN1TaggedObject.hasNext()) {
                x509Certificate = (X509Certificate)aSN1TaggedObject.next();
                if (!bigInteger.equals(x509Certificate.getSerialNumber())) continue;
                this.signCert = x509Certificate;
                break;
            }
            if (this.signCert == null) {
                throw new IllegalArgumentException("Can't find signing certificate with serial " + bigInteger.toString(16));
            }
            this.digestAlgorithm = ((DERObjectIdentifier)((ASN1Sequence)aSN1Sequence5.getObjectAt(2)).getObjectAt(0)).getId();
            n = 3;
            if (aSN1Sequence5.getObjectAt(n) instanceof ASN1TaggedObject) {
                aSN1TaggedObject = (ASN1TaggedObject)aSN1Sequence5.getObjectAt(n);
                x509Certificate = (ASN1Sequence)aSN1TaggedObject.getObject();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ASN1OutputStream aSN1OutputStream = new ASN1OutputStream((OutputStream)byteArrayOutputStream);
                try {
                    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                    for (int i = 0; i < x509Certificate.size(); ++i) {
                        aSN1EncodableVector.add(x509Certificate.getObjectAt(i));
                    }
                    aSN1OutputStream.writeObject((Object)new DERSet((DEREncodableVector)aSN1EncodableVector));
                    aSN1OutputStream.close();
                }
                catch (IOException var21_25) {
                    // empty catch block
                }
                this.sigAttr = byteArrayOutputStream.toByteArray();
                for (int i = 0; i < x509Certificate.size(); ++i) {
                    ASN1Sequence aSN1Sequence7 = (ASN1Sequence)x509Certificate.getObjectAt(i);
                    if (!((DERObjectIdentifier)aSN1Sequence7.getObjectAt(0)).getId().equals("1.2.840.113549.1.9.4")) continue;
                    ASN1Set aSN1Set2 = (ASN1Set)aSN1Sequence7.getObjectAt(1);
                    this.digestAttr = ((DEROctetString)aSN1Set2.getObjectAt(0)).getOctets();
                    break;
                }
                if (this.digestAttr == null) {
                    throw new IllegalArgumentException("Authenticated attribute is missing the digest.");
                }
                ++n;
            }
            this.digestEncryptionAlgorithm = ((DERObjectIdentifier)((ASN1Sequence)aSN1Sequence5.getObjectAt(n++)).getObjectAt(0)).getId();
            this.digest = ((DEROctetString)aSN1Sequence5.getObjectAt(n)).getOctets();
            if (this.RSAdata != null || this.digestAttr != null) {
                this.messageDigest = string == null || string.startsWith("SunPKCS11") ? MessageDigest.getInstance(this.getHashAlgorithm()) : MessageDigest.getInstance(this.getHashAlgorithm(), string);
            }
            this.sig = string == null ? Signature.getInstance(this.getDigestAlgorithm()) : Signature.getInstance(this.getDigestAlgorithm(), string);
            this.sig.initVerify(this.signCert.getPublicKey());
        }
        catch (Exception var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public PdfPKCS7(PrivateKey privateKey, Certificate[] arrcertificate, CRL[] arrcRL, String string, String string2, boolean bl) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {
        int n;
        this.privKey = privateKey;
        if (string.equals("MD5")) {
            this.digestAlgorithm = "1.2.840.113549.2.5";
        } else if (string.equals("MD2")) {
            this.digestAlgorithm = "1.2.840.113549.2.2";
        } else if (string.equals("SHA")) {
            this.digestAlgorithm = "1.3.14.3.2.26";
        } else if (string.equals("SHA1")) {
            this.digestAlgorithm = "1.3.14.3.2.26";
        } else {
            throw new NoSuchAlgorithmException("Unknown Hash Algorithm " + string);
        }
        this.signerversion = 1;
        this.version = 1;
        this.certs = new ArrayList();
        this.crls = new ArrayList();
        this.digestalgos = new HashSet();
        this.digestalgos.add(this.digestAlgorithm);
        this.signCert = (X509Certificate)arrcertificate[0];
        for (n = 0; n < arrcertificate.length; ++n) {
            this.certs.add(arrcertificate[n]);
        }
        if (arrcRL != null) {
            for (n = 0; n < arrcRL.length; ++n) {
                this.crls.add(arrcRL[n]);
            }
        }
        if (privateKey != null) {
            this.digestEncryptionAlgorithm = privateKey.getAlgorithm();
            if (this.digestEncryptionAlgorithm.equals("RSA")) {
                this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
            } else if (this.digestEncryptionAlgorithm.equals("DSA")) {
                this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
            } else {
                throw new NoSuchAlgorithmException("Unknown Key Algorithm " + this.digestEncryptionAlgorithm);
            }
        }
        if (bl) {
            this.RSAdata = new byte[0];
            this.messageDigest = string2 == null || string2.startsWith("SunPKCS11") ? MessageDigest.getInstance(this.getHashAlgorithm()) : MessageDigest.getInstance(this.getHashAlgorithm(), string2);
        }
        if (privateKey != null) {
            this.sig = string2 == null ? Signature.getInstance(this.getDigestAlgorithm()) : Signature.getInstance(this.getDigestAlgorithm(), string2);
            this.sig.initSign(privateKey);
        }
    }

    public void update(byte[] arrby, int n, int n2) throws SignatureException {
        if (this.RSAdata != null || this.digestAttr != null) {
            this.messageDigest.update(arrby, n, n2);
        } else {
            this.sig.update(arrby, n, n2);
        }
    }

    public boolean verify() throws SignatureException {
        if (this.verified) {
            return this.verifyResult;
        }
        if (this.sigAttr != null) {
            this.sig.update(this.sigAttr);
            if (this.RSAdata != null) {
                byte[] arrby = this.messageDigest.digest();
                this.messageDigest.update(arrby);
            }
            this.verifyResult = Arrays.equals(this.messageDigest.digest(), this.digestAttr) && this.sig.verify(this.digest);
        } else {
            if (this.RSAdata != null) {
                this.sig.update(this.messageDigest.digest());
            }
            this.verifyResult = this.sig.verify(this.digest);
        }
        this.verified = true;
        return this.verifyResult;
    }

    public Certificate[] getCertificates() {
        return this.certs.toArray(new X509Certificate[this.certs.size()]);
    }

    public Collection getCRLs() {
        return this.crls;
    }

    public X509Certificate getSigningCertificate() {
        return this.signCert;
    }

    public int getVersion() {
        return this.version;
    }

    public int getSigningInfoVersion() {
        return this.signerversion;
    }

    public String getDigestAlgorithm() {
        String string = this.digestEncryptionAlgorithm;
        if (this.digestEncryptionAlgorithm.equals("1.2.840.113549.1.1.1") || this.digestEncryptionAlgorithm.equals("1.2.840.113549.1.1.4") || this.digestEncryptionAlgorithm.equals("1.2.840.113549.1.1.2") || this.digestEncryptionAlgorithm.equals("1.2.840.113549.1.1.5")) {
            string = "RSA";
        } else if (this.digestEncryptionAlgorithm.equals("1.2.840.10040.4.1")) {
            string = "DSA";
        }
        return this.getHashAlgorithm() + "with" + string;
    }

    public String getHashAlgorithm() {
        String string = this.digestAlgorithm;
        if (this.digestAlgorithm.equals("1.2.840.113549.2.5") || this.digestAlgorithm.equals("1.2.840.113549.1.1.4")) {
            string = "MD5";
        } else if (this.digestAlgorithm.equals("1.2.840.113549.2.2") || this.digestAlgorithm.equals("1.2.840.113549.1.1.2")) {
            string = "MD2";
        } else if (this.digestAlgorithm.equals("1.3.14.3.2.26") || this.digestAlgorithm.equals("1.2.840.113549.1.1.5")) {
            string = "SHA1";
        }
        return string;
    }

    public static KeyStore loadCacertsKeyStore() {
        return PdfPKCS7.loadCacertsKeyStore(null);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static KeyStore loadCacertsKeyStore(String string) {
        file = new File(System.getProperty("java.home"), "lib");
        file = new File(file, "security");
        file = new File(file, "cacerts");
        fileInputStream = null;
        try {
            try {
                fileInputStream = new FileInputStream(file);
                keyStore2 = string == null ? KeyStore.getInstance("JKS") : KeyStore.getInstance("JKS", string);
                keyStore2.load(fileInputStream, null);
                keyStore = keyStore2;
                var6_6 = null;
            }
            catch (Exception exception) {
                throw new ExceptionConverter(exception);
            }
            try {
                if (fileInputStream == null) return keyStore;
                fileInputStream.close();
                return keyStore;
            }
            catch (Exception var7_8) {
                // empty catch block
            }
            return keyStore;
        }
        catch (Throwable throwable) {
            var6_7 = null;
            ** try [egrp 2[TRYBLOCK] [3 : 103->114)] { 
lbl26: // 1 sources:
            if (fileInputStream == null) throw throwable;
            fileInputStream.close();
            throw throwable;
lbl29: // 1 sources:
            catch (Exception var7_9) {
                // empty catch block
            }
            throw throwable;
        }
    }

    public static String verifyCertificate(X509Certificate x509Certificate, Collection collection, Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        if (x509Certificate.hasUnsupportedCriticalExtension()) {
            return "Has unsupported critical extension";
        }
        try {
            x509Certificate.checkValidity(calendar.getTime());
        }
        catch (Exception var3_3) {
            return var3_3.getMessage();
        }
        if (collection != null) {
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                if (!((CRL)iterator.next()).isRevoked(x509Certificate)) continue;
                return "Certificate revoked";
            }
        }
        return null;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static Object[] verifyCertificates(Certificate[] arrcertificate, KeyStore keyStore, Collection collection, Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        int i = 0;
        while (i < arrcertificate.length) {
            Object object;
            int n;
            X509Certificate x509Certificate = (X509Certificate)arrcertificate[i];
            String string = PdfPKCS7.verifyCertificate(x509Certificate, collection, calendar);
            if (string != null) {
                return new Object[]{x509Certificate, string};
            }
            try {
                Enumeration<String> enumeration = keyStore.aliases();
                while (enumeration.hasMoreElements()) {
                    X509Certificate x509Certificate2;
                    object = enumeration.nextElement();
                    if (!keyStore.isCertificateEntry((String)object) || PdfPKCS7.verifyCertificate(x509Certificate2 = (X509Certificate)keyStore.getCertificate((String)object), collection, calendar) != null) continue;
                    {
                        catch (Exception exception) {}
                    }
                    try {
                        x509Certificate.verify(x509Certificate2.getPublicKey());
                        return null;
                    }
                    catch (Exception var10_14) {
                        continue;
                    }
                }
            }
            catch (Exception var7_9) {
                // empty catch block
            }
            for (n = 0; n < arrcertificate.length; ++n) {
                if (n == i) continue;
                object = (X509Certificate)arrcertificate[n];
                try {
                    x509Certificate.verify(object.getPublicKey());
                    break;
                }
                catch (Exception var9_13) {
                    // empty catch block
                }
            }
            if (n == arrcertificate.length) {
                return new Object[]{x509Certificate, "Cannot be verified against the KeyStore or the certificate chain"};
            }
            ++i;
        }
        return new Object[]{null, "Invalid state. Possible circular certificate chain"};
    }

    private static DERObject getIssuer(byte[] arrby) {
        try {
            ASN1InputStream aSN1InputStream = new ASN1InputStream((InputStream)new ByteArrayInputStream(arrby));
            ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
            return (DERObject)aSN1Sequence.getObjectAt(aSN1Sequence.getObjectAt(0) instanceof DERTaggedObject ? 3 : 2);
        }
        catch (IOException var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    private static DERObject getSubject(byte[] arrby) {
        try {
            ASN1InputStream aSN1InputStream = new ASN1InputStream((InputStream)new ByteArrayInputStream(arrby));
            ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
            return (DERObject)aSN1Sequence.getObjectAt(aSN1Sequence.getObjectAt(0) instanceof DERTaggedObject ? 5 : 4);
        }
        catch (IOException var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public static X509Name getIssuerFields(X509Certificate x509Certificate) {
        try {
            return new X509Name((ASN1Sequence)PdfPKCS7.getIssuer(x509Certificate.getTBSCertificate()));
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public static X509Name getSubjectFields(X509Certificate x509Certificate) {
        try {
            return new X509Name((ASN1Sequence)PdfPKCS7.getSubject(x509Certificate.getTBSCertificate()));
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public byte[] getEncodedPKCS1() {
        try {
            this.digest = this.externalDigest != null ? this.externalDigest : this.sig.sign();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ASN1OutputStream aSN1OutputStream = new ASN1OutputStream((OutputStream)byteArrayOutputStream);
            aSN1OutputStream.writeObject((Object)new DEROctetString(this.digest));
            aSN1OutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (Exception var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public void setExternalDigest(byte[] arrby, byte[] arrby2, String string) {
        this.externalDigest = arrby;
        this.externalRSAdata = arrby2;
        if (string != null) {
            if (string.equals("RSA")) {
                this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
            } else if (string.equals("DSA")) {
                this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
            } else {
                throw new ExceptionConverter(new NoSuchAlgorithmException("Unknown Key Algorithm " + string));
            }
        }
    }

    public byte[] getEncodedPKCS7() {
        return this.getEncodedPKCS7(null, null);
    }

    public byte[] getEncodedPKCS7(byte[] arrby, Calendar calendar) {
        try {
            ASN1EncodableVector aSN1EncodableVector;
            ASN1OutputStream aSN1OutputStream;
            ASN1EncodableVector aSN1EncodableVector2;
            ASN1EncodableVector aSN1EncodableVector3;
            ASN1EncodableVector aSN1EncodableVector4;
            Iterator iterator;
            if (this.externalDigest != null) {
                this.digest = this.externalDigest;
                if (this.RSAdata != null) {
                    this.RSAdata = this.externalRSAdata;
                }
            } else if (this.externalRSAdata != null && this.RSAdata != null) {
                this.RSAdata = this.externalRSAdata;
                this.sig.update(this.RSAdata);
                this.digest = this.sig.sign();
            } else {
                if (this.RSAdata != null) {
                    this.RSAdata = this.messageDigest.digest();
                    this.sig.update(this.RSAdata);
                }
                this.digest = this.sig.sign();
            }
            ASN1EncodableVector aSN1EncodableVector5 = new ASN1EncodableVector();
            ASN1EncodableVector aSN1EncodableVector6 = this.digestalgos.iterator();
            while (aSN1EncodableVector6.hasNext()) {
                aSN1EncodableVector = new ASN1EncodableVector();
                aSN1EncodableVector.add((DEREncodable)new DERObjectIdentifier((String)aSN1EncodableVector6.next()));
                aSN1EncodableVector.add((DEREncodable)DERNull.INSTANCE);
                aSN1EncodableVector5.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector));
            }
            aSN1EncodableVector6 = new ASN1EncodableVector();
            aSN1EncodableVector6.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.7.1"));
            if (this.RSAdata != null) {
                aSN1EncodableVector6.add((DEREncodable)new DERTaggedObject(0, (DEREncodable)new DEROctetString(this.RSAdata)));
            }
            aSN1EncodableVector = new DERSequence((DEREncodableVector)aSN1EncodableVector6);
            aSN1EncodableVector6 = new ASN1EncodableVector();
            DERSet dERSet = this.certs.iterator();
            while (dERSet.hasNext()) {
                aSN1EncodableVector4 = new ASN1InputStream((InputStream)new ByteArrayInputStream(((X509Certificate)dERSet.next()).getEncoded()));
                aSN1EncodableVector6.add((DEREncodable)aSN1EncodableVector4.readObject());
            }
            dERSet = new DERSet((DEREncodableVector)aSN1EncodableVector6);
            aSN1EncodableVector4 = new ASN1EncodableVector();
            aSN1EncodableVector4.add((DEREncodable)new DERInteger(this.signerversion));
            aSN1EncodableVector6 = new ASN1EncodableVector();
            aSN1EncodableVector6.add((DEREncodable)PdfPKCS7.getIssuer(this.signCert.getTBSCertificate()));
            aSN1EncodableVector6.add((DEREncodable)new DERInteger(this.signCert.getSerialNumber()));
            aSN1EncodableVector4.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector6));
            aSN1EncodableVector6 = new ASN1EncodableVector();
            aSN1EncodableVector6.add((DEREncodable)new DERObjectIdentifier(this.digestAlgorithm));
            aSN1EncodableVector6.add((DEREncodable)new DERNull());
            aSN1EncodableVector4.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector6));
            if (arrby != null && calendar != null) {
                aSN1EncodableVector2 = new ASN1EncodableVector();
                aSN1EncodableVector6 = new ASN1EncodableVector();
                aSN1EncodableVector6.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.3"));
                aSN1EncodableVector6.add((DEREncodable)new DERSet((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.7.1")));
                aSN1EncodableVector2.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector6));
                aSN1EncodableVector6 = new ASN1EncodableVector();
                aSN1EncodableVector6.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.5"));
                aSN1EncodableVector6.add((DEREncodable)new DERSet((DEREncodable)new DERUTCTime(calendar.getTime())));
                aSN1EncodableVector2.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector6));
                aSN1EncodableVector6 = new ASN1EncodableVector();
                aSN1EncodableVector6.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.4"));
                aSN1EncodableVector6.add((DEREncodable)new DERSet((DEREncodable)new DEROctetString(arrby)));
                aSN1EncodableVector2.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector6));
                if (!this.crls.isEmpty()) {
                    aSN1EncodableVector6 = new ASN1EncodableVector();
                    aSN1EncodableVector6.add((DEREncodable)new DERObjectIdentifier("1.2.840.113583.1.1.8"));
                    aSN1EncodableVector3 = new ASN1EncodableVector();
                    iterator = this.crls.iterator();
                    while (iterator.hasNext()) {
                        aSN1OutputStream = new ASN1InputStream((InputStream)new ByteArrayInputStream(((X509CRL)iterator.next()).getEncoded()));
                        aSN1EncodableVector3.add((DEREncodable)aSN1OutputStream.readObject());
                    }
                    aSN1EncodableVector6.add((DEREncodable)new DERSet((DEREncodable)new DERSequence((DEREncodable)new DERTaggedObject(true, 0, (DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector3)))));
                    aSN1EncodableVector2.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector6));
                }
                aSN1EncodableVector4.add((DEREncodable)new DERTaggedObject(false, 0, (DEREncodable)new DERSet((DEREncodableVector)aSN1EncodableVector2)));
            }
            aSN1EncodableVector6 = new ASN1EncodableVector();
            aSN1EncodableVector6.add((DEREncodable)new DERObjectIdentifier(this.digestEncryptionAlgorithm));
            aSN1EncodableVector6.add((DEREncodable)new DERNull());
            aSN1EncodableVector4.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector6));
            aSN1EncodableVector4.add((DEREncodable)new DEROctetString(this.digest));
            aSN1EncodableVector2 = new ASN1EncodableVector();
            aSN1EncodableVector2.add((DEREncodable)new DERInteger(this.version));
            aSN1EncodableVector2.add((DEREncodable)new DERSet((DEREncodableVector)aSN1EncodableVector5));
            aSN1EncodableVector2.add((DEREncodable)aSN1EncodableVector);
            aSN1EncodableVector2.add((DEREncodable)new DERTaggedObject(false, 0, (DEREncodable)dERSet));
            if (!this.crls.isEmpty()) {
                aSN1EncodableVector6 = new ASN1EncodableVector();
                aSN1EncodableVector3 = this.crls.iterator();
                while (aSN1EncodableVector3.hasNext()) {
                    iterator = new ASN1InputStream((InputStream)new ByteArrayInputStream(((X509CRL)aSN1EncodableVector3.next()).getEncoded()));
                    aSN1EncodableVector6.add((DEREncodable)iterator.readObject());
                }
                aSN1EncodableVector3 = new DERSet((DEREncodableVector)aSN1EncodableVector6);
                aSN1EncodableVector2.add((DEREncodable)new DERTaggedObject(false, 1, (DEREncodable)aSN1EncodableVector3));
            }
            aSN1EncodableVector2.add((DEREncodable)new DERSet((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector4)));
            aSN1EncodableVector3 = new ASN1EncodableVector();
            aSN1EncodableVector3.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.7.2"));
            aSN1EncodableVector3.add((DEREncodable)new DERTaggedObject(0, (DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector2)));
            iterator = new ByteArrayOutputStream();
            aSN1OutputStream = new ASN1OutputStream((OutputStream)((Object)iterator));
            aSN1OutputStream.writeObject((Object)new DERSequence((DEREncodableVector)aSN1EncodableVector3));
            aSN1OutputStream.close();
            return iterator.toByteArray();
        }
        catch (Exception var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public byte[] getAuthenticatedAttributeBytes(byte[] arrby, Calendar calendar) {
        try {
            ByteArrayOutputStream byteArrayOutputStream;
            Object object;
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
            aSN1EncodableVector2.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.3"));
            aSN1EncodableVector2.add((DEREncodable)new DERSet((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.7.1")));
            aSN1EncodableVector.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector2));
            aSN1EncodableVector2 = new ASN1EncodableVector();
            aSN1EncodableVector2.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.5"));
            aSN1EncodableVector2.add((DEREncodable)new DERSet((DEREncodable)new DERUTCTime(calendar.getTime())));
            aSN1EncodableVector.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector2));
            aSN1EncodableVector2 = new ASN1EncodableVector();
            aSN1EncodableVector2.add((DEREncodable)new DERObjectIdentifier("1.2.840.113549.1.9.4"));
            aSN1EncodableVector2.add((DEREncodable)new DERSet((DEREncodable)new DEROctetString(arrby)));
            aSN1EncodableVector.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector2));
            if (!this.crls.isEmpty()) {
                aSN1EncodableVector2 = new ASN1EncodableVector();
                aSN1EncodableVector2.add((DEREncodable)new DERObjectIdentifier("1.2.840.113583.1.1.8"));
                byteArrayOutputStream = new ASN1EncodableVector();
                object = this.crls.iterator();
                while (object.hasNext()) {
                    ASN1InputStream aSN1InputStream = new ASN1InputStream((InputStream)new ByteArrayInputStream(((X509CRL)object.next()).getEncoded()));
                    byteArrayOutputStream.add((DEREncodable)aSN1InputStream.readObject());
                }
                aSN1EncodableVector2.add((DEREncodable)new DERSet((DEREncodable)new DERSequence((DEREncodable)new DERTaggedObject(true, 0, (DEREncodable)new DERSequence((DEREncodableVector)byteArrayOutputStream)))));
                aSN1EncodableVector.add((DEREncodable)new DERSequence((DEREncodableVector)aSN1EncodableVector2));
            }
            byteArrayOutputStream = new ByteArrayOutputStream();
            object = new ASN1OutputStream((OutputStream)byteArrayOutputStream);
            object.writeObject((Object)new DERSet((DEREncodableVector)aSN1EncodableVector));
            object.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (Exception var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String string) {
        this.reason = string;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String string) {
        this.location = string;
    }

    public Calendar getSignDate() {
        return this.signDate;
    }

    public void setSignDate(Calendar calendar) {
        this.signDate = calendar;
    }

    public String getSignName() {
        return this.signName;
    }

    public void setSignName(String string) {
        this.signName = string;
    }

    public static class X509NameTokenizer {
        private String oid;
        private int index;
        private StringBuffer buf = new StringBuffer();

        public X509NameTokenizer(String string) {
            this.oid = string;
            this.index = -1;
        }

        public boolean hasMoreTokens() {
            return this.index != this.oid.length();
        }

        public String nextToken() {
            int n;
            if (this.index == this.oid.length()) {
                return null;
            }
            boolean bl = false;
            boolean bl2 = false;
            this.buf.setLength(0);
            for (n = this.index + 1; n != this.oid.length(); ++n) {
                char c = this.oid.charAt(n);
                if (c == '\"') {
                    if (!bl2) {
                        bl = !bl;
                    } else {
                        this.buf.append(c);
                    }
                    bl2 = false;
                    continue;
                }
                if (bl2 || bl) {
                    this.buf.append(c);
                    bl2 = false;
                    continue;
                }
                if (c == '\\') {
                    bl2 = true;
                    continue;
                }
                if (c == ',') break;
                this.buf.append(c);
            }
            this.index = n;
            return this.buf.toString().trim();
        }
    }

    public static class X509Name {
        public static final DERObjectIdentifier C = new DERObjectIdentifier("2.5.4.6");
        public static final DERObjectIdentifier O = new DERObjectIdentifier("2.5.4.10");
        public static final DERObjectIdentifier OU = new DERObjectIdentifier("2.5.4.11");
        public static final DERObjectIdentifier T = new DERObjectIdentifier("2.5.4.12");
        public static final DERObjectIdentifier CN = new DERObjectIdentifier("2.5.4.3");
        public static final DERObjectIdentifier SN = new DERObjectIdentifier("2.5.4.5");
        public static final DERObjectIdentifier L = new DERObjectIdentifier("2.5.4.7");
        public static final DERObjectIdentifier ST = new DERObjectIdentifier("2.5.4.8");
        public static final DERObjectIdentifier SURNAME = new DERObjectIdentifier("2.5.4.4");
        public static final DERObjectIdentifier GIVENNAME = new DERObjectIdentifier("2.5.4.42");
        public static final DERObjectIdentifier INITIALS = new DERObjectIdentifier("2.5.4.43");
        public static final DERObjectIdentifier GENERATION = new DERObjectIdentifier("2.5.4.44");
        public static final DERObjectIdentifier UNIQUE_IDENTIFIER = new DERObjectIdentifier("2.5.4.45");
        public static final DERObjectIdentifier EmailAddress;
        public static final DERObjectIdentifier E;
        public static final DERObjectIdentifier DC;
        public static final DERObjectIdentifier UID;
        public static HashMap DefaultSymbols;
        public HashMap values = new HashMap();

        public X509Name(ASN1Sequence aSN1Sequence) {
            Enumeration enumeration = aSN1Sequence.getObjects();
            while (enumeration.hasMoreElements()) {
                ASN1Set aSN1Set = (ASN1Set)enumeration.nextElement();
                for (int i = 0; i < aSN1Set.size(); ++i) {
                    ASN1Sequence aSN1Sequence2 = (ASN1Sequence)aSN1Set.getObjectAt(i);
                    String string = (String)DefaultSymbols.get((Object)aSN1Sequence2.getObjectAt(0));
                    if (string == null) continue;
                    ArrayList<String> arrayList = (ArrayList<String>)this.values.get(string);
                    if (arrayList == null) {
                        arrayList = new ArrayList<String>();
                        this.values.put(string, arrayList);
                    }
                    arrayList.add(((DERString)aSN1Sequence2.getObjectAt(1)).getString());
                }
            }
        }

        public X509Name(String string) {
            X509NameTokenizer x509NameTokenizer = new X509NameTokenizer(string);
            while (x509NameTokenizer.hasMoreTokens()) {
                String string2 = x509NameTokenizer.nextToken();
                int n = string2.indexOf(61);
                if (n == -1) {
                    throw new IllegalArgumentException("badly formated directory string");
                }
                String string3 = string2.substring(0, n).toUpperCase();
                String string4 = string2.substring(n + 1);
                ArrayList<String> arrayList = (ArrayList<String>)this.values.get(string3);
                if (arrayList == null) {
                    arrayList = new ArrayList<String>();
                    this.values.put(string3, arrayList);
                }
                arrayList.add(string4);
            }
        }

        public String getField(String string) {
            ArrayList arrayList = (ArrayList)this.values.get(string);
            return arrayList == null ? null : (String)arrayList.get(0);
        }

        public ArrayList getFieldArray(String string) {
            ArrayList arrayList = (ArrayList)this.values.get(string);
            return arrayList == null ? null : arrayList;
        }

        public HashMap getFields() {
            return this.values;
        }

        public String toString() {
            return this.values.toString();
        }

        static {
            E = X509Name.EmailAddress = new DERObjectIdentifier("1.2.840.113549.1.9.1");
            DC = new DERObjectIdentifier("0.9.2342.19200300.100.1.25");
            UID = new DERObjectIdentifier("0.9.2342.19200300.100.1.1");
            DefaultSymbols = new HashMap();
            DefaultSymbols.put(C, "C");
            DefaultSymbols.put(O, "O");
            DefaultSymbols.put(T, "T");
            DefaultSymbols.put(OU, "OU");
            DefaultSymbols.put(CN, "CN");
            DefaultSymbols.put(L, "L");
            DefaultSymbols.put(ST, "ST");
            DefaultSymbols.put(SN, "SN");
            DefaultSymbols.put(EmailAddress, "E");
            DefaultSymbols.put(DC, "DC");
            DefaultSymbols.put(UID, "UID");
            DefaultSymbols.put(SURNAME, "SURNAME");
            DefaultSymbols.put(GIVENNAME, "GIVENNAME");
            DefaultSymbols.put(INITIALS, "INITIALS");
            DefaultSymbols.put(GENERATION, "GENERATION");
        }
    }

}

