/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfString;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public abstract class PdfSigGenericPKCS
extends PdfSignature {
    protected String hashAlgorithm;
    protected String provider = null;
    protected PdfPKCS7 pkcs;
    protected String name;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String digestEncryptionAlgorithm;

    public PdfSigGenericPKCS(PdfName pdfName, PdfName pdfName2) {
        super(pdfName, pdfName2);
    }

    public void setSignInfo(PrivateKey privateKey, Certificate[] arrcertificate, CRL[] arrcRL) {
        try {
            this.pkcs = new PdfPKCS7(privateKey, arrcertificate, arrcRL, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(this.get(PdfName.SUBFILTER)));
            this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
            if (PdfName.ADBE_X509_RSA_SHA1.equals(this.get(PdfName.SUBFILTER))) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                for (int i = 0; i < arrcertificate.length; ++i) {
                    byteArrayOutputStream.write(arrcertificate[i].getEncoded());
                }
                byteArrayOutputStream.close();
                this.setCert(byteArrayOutputStream.toByteArray());
                this.setContents(this.pkcs.getEncodedPKCS1());
            } else {
                this.setContents(this.pkcs.getEncodedPKCS7());
            }
            this.name = PdfPKCS7.getSubjectFields(this.pkcs.getSigningCertificate()).getField("CN");
            if (this.name != null) {
                this.put(PdfName.NAME, new PdfString(this.name, "UnicodeBig"));
            }
            this.pkcs = new PdfPKCS7(privateKey, arrcertificate, arrcRL, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(this.get(PdfName.SUBFILTER)));
            this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
        }
        catch (Exception var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    public void setExternalDigest(byte[] arrby, byte[] arrby2, String string) {
        this.externalDigest = arrby;
        this.externalRSAdata = arrby2;
        this.digestEncryptionAlgorithm = string;
    }

    public String getName() {
        return this.name;
    }

    public PdfPKCS7 getSigner() {
        return this.pkcs;
    }

    public byte[] getSignerContents() {
        if (PdfName.ADBE_X509_RSA_SHA1.equals(this.get(PdfName.SUBFILTER))) {
            return this.pkcs.getEncodedPKCS1();
        }
        return this.pkcs.getEncodedPKCS7();
    }

    public static class PPKMS
    extends PdfSigGenericPKCS {
        public PPKMS() {
            super(PdfName.ADOBE_PPKMS, PdfName.ADBE_PKCS7_SHA1);
            this.hashAlgorithm = "SHA1";
        }

        public PPKMS(String string) {
            this();
            this.provider = string;
        }
    }

    public static class PPKLite
    extends PdfSigGenericPKCS {
        public PPKLite() {
            super(PdfName.ADOBE_PPKLITE, PdfName.ADBE_X509_RSA_SHA1);
            this.hashAlgorithm = "SHA1";
            this.put(PdfName.R, new PdfNumber(65541));
        }

        public PPKLite(String string) {
            this();
            this.provider = string;
        }
    }

    public static class VeriSign
    extends PdfSigGenericPKCS {
        public VeriSign() {
            super(PdfName.VERISIGN_PPKVS, PdfName.ADBE_PKCS7_DETACHED);
            this.hashAlgorithm = "MD5";
            this.put(PdfName.R, new PdfNumber(65537));
        }

        public VeriSign(String string) {
            this();
            this.provider = string;
        }
    }

}

