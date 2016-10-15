/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DEREncodable
 *  org.bouncycastle.asn1.DERInteger
 *  org.bouncycastle.asn1.DERObject
 *  org.bouncycastle.asn1.DERObjectIdentifier
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DEROutputStream
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedContentInfo
 *  org.bouncycastle.asn1.cms.EnvelopedData
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.cms.KeyTransRecipientInfo
 *  org.bouncycastle.asn1.cms.OriginatorInfo
 *  org.bouncycastle.asn1.cms.RecipientIdentifier
 *  org.bouncycastle.asn1.cms.RecipientInfo
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x509.TBSCertificateStructure
 *  org.bouncycastle.asn1.x509.X509Name
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPublicKeyRecipient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;

public class PdfPublicKeySecurityHandler {
    static final int SEED_LENGTH = 20;
    private ArrayList recipients = null;
    private byte[] seed = new byte[20];

    public PdfPublicKeySecurityHandler() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(192, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            System.arraycopy(secretKey.getEncoded(), 0, this.seed, 0, 20);
        }
        catch (NoSuchAlgorithmException var2_3) {
            this.seed = SecureRandom.getSeed(20);
        }
        this.recipients = new ArrayList();
    }

    public static byte[] unescapedString(byte[] arrby) throws BadPdfFormatException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (arrby[0] != 40 && arrby[arrby.length - 1] != 41) {
            throw new BadPdfFormatException("Expect '(' and ')' at begin and end of the string.");
        }
        for (int i = 0; i < arrby.length; ++i) {
            if (arrby[i] == 92) {
                switch (arrby[++i]) {
                    case 98: {
                        byteArrayOutputStream.write(8);
                        break;
                    }
                    case 102: {
                        byteArrayOutputStream.write(12);
                        break;
                    }
                    case 116: {
                        byteArrayOutputStream.write(9);
                        break;
                    }
                    case 110: {
                        byteArrayOutputStream.write(10);
                        break;
                    }
                    case 114: {
                        byteArrayOutputStream.write(13);
                        break;
                    }
                    case 40: {
                        byteArrayOutputStream.write(40);
                        break;
                    }
                    case 41: {
                        byteArrayOutputStream.write(41);
                        break;
                    }
                    case 92: {
                        byteArrayOutputStream.write(92);
                    }
                }
                continue;
            }
            byteArrayOutputStream.write(arrby[i]);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void addRecipient(PdfPublicKeyRecipient pdfPublicKeyRecipient) {
        this.recipients.add(pdfPublicKeyRecipient);
    }

    protected byte[] getSeed() {
        return (byte[])this.seed.clone();
    }

    public int getRecipientsSize() {
        return this.recipients.size();
    }

    public byte[] getEncodedRecipient(int n) throws IOException, GeneralSecurityException {
        PdfPublicKeyRecipient pdfPublicKeyRecipient = (PdfPublicKeyRecipient)this.recipients.get(n);
        byte[] arrby = pdfPublicKeyRecipient.getCms();
        if (arrby != null) {
            return arrby;
        }
        Certificate certificate = pdfPublicKeyRecipient.getCertificate();
        int n2 = pdfPublicKeyRecipient.getPermission();
        int n3 = 3;
        n2 |= n3 == 3 ? -3904 : -64;
        n2 &= -4;
        byte[] arrby2 = new byte[24];
        byte by = (byte)(++n2);
        byte by2 = (byte)(n2 >> 8);
        byte by3 = (byte)(n2 >> 16);
        byte by4 = (byte)(n2 >> 24);
        System.arraycopy(this.seed, 0, arrby2, 0, 20);
        arrby2[20] = by4;
        arrby2[21] = by3;
        arrby2[22] = by2;
        arrby2[23] = by;
        DERObject dERObject = this.createDERForRecipient(arrby2, (X509Certificate)certificate);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DEROutputStream dEROutputStream = new DEROutputStream((OutputStream)byteArrayOutputStream);
        dEROutputStream.writeObject((Object)dERObject);
        arrby = byteArrayOutputStream.toByteArray();
        pdfPublicKeyRecipient.setCms(arrby);
        return arrby;
    }

    public PdfArray getEncodedRecipients() throws IOException, GeneralSecurityException {
        PdfArray pdfArray = new PdfArray();
        byte[] arrby = null;
        for (int i = 0; i < this.recipients.size(); ++i) {
            try {
                arrby = this.getEncodedRecipient(i);
                pdfArray.add(new PdfLiteral(PdfContentByte.escapeString(arrby)));
                continue;
            }
            catch (GeneralSecurityException var4_5) {
                pdfArray = null;
                continue;
            }
            catch (IOException var4_6) {
                pdfArray = null;
            }
        }
        return pdfArray;
    }

    private DERObject createDERForRecipient(byte[] arrby, X509Certificate x509Certificate) throws IOException, GeneralSecurityException {
        String string = "1.2.840.113549.3.2";
        AlgorithmParameterGenerator algorithmParameterGenerator = AlgorithmParameterGenerator.getInstance(string);
        AlgorithmParameters algorithmParameters = algorithmParameterGenerator.generateParameters();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(algorithmParameters.getEncoded("ASN.1"));
        ASN1InputStream aSN1InputStream = new ASN1InputStream((InputStream)byteArrayInputStream);
        DERObject dERObject = aSN1InputStream.readObject();
        KeyGenerator keyGenerator = KeyGenerator.getInstance(string);
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance(string);
        cipher.init(1, (Key)secretKey, algorithmParameters);
        byte[] arrby2 = cipher.doFinal(arrby);
        DEROctetString dEROctetString = new DEROctetString(arrby2);
        KeyTransRecipientInfo keyTransRecipientInfo = this.computeRecipientInfo(x509Certificate, secretKey.getEncoded());
        DERSet dERSet = new DERSet((DEREncodable)new RecipientInfo(keyTransRecipientInfo));
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(new DERObjectIdentifier(string), (DEREncodable)dERObject);
        EncryptedContentInfo encryptedContentInfo = new EncryptedContentInfo(PKCSObjectIdentifiers.data, algorithmIdentifier, (ASN1OctetString)dEROctetString);
        EnvelopedData envelopedData = new EnvelopedData(null, (ASN1Set)dERSet, encryptedContentInfo, null);
        ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.envelopedData, (DEREncodable)envelopedData);
        return contentInfo.getDERObject();
    }

    private KeyTransRecipientInfo computeRecipientInfo(X509Certificate x509Certificate, byte[] arrby) throws GeneralSecurityException, IOException {
        ASN1InputStream aSN1InputStream = new ASN1InputStream((InputStream)new ByteArrayInputStream(x509Certificate.getTBSCertificate()));
        TBSCertificateStructure tBSCertificateStructure = TBSCertificateStructure.getInstance((Object)aSN1InputStream.readObject());
        AlgorithmIdentifier algorithmIdentifier = tBSCertificateStructure.getSubjectPublicKeyInfo().getAlgorithmId();
        IssuerAndSerialNumber issuerAndSerialNumber = new IssuerAndSerialNumber(tBSCertificateStructure.getIssuer(), tBSCertificateStructure.getSerialNumber().getValue());
        Cipher cipher = Cipher.getInstance(algorithmIdentifier.getObjectId().getId());
        cipher.init(1, x509Certificate);
        DEROctetString dEROctetString = new DEROctetString(cipher.doFinal(arrby));
        RecipientIdentifier recipientIdentifier = new RecipientIdentifier(issuerAndSerialNumber);
        return new KeyTransRecipientInfo(recipientIdentifier, algorithmIdentifier, (ASN1OctetString)dEROctetString);
    }
}

