/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.OutputStreamEncryption;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPublicKeyRecipient;
import com.lowagie.text.pdf.PdfPublicKeySecurityHandler;
import com.lowagie.text.pdf.StandardDecryption;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;

public class PdfEncryption {
    public static final int STANDARD_ENCRYPTION_40 = 2;
    public static final int STANDARD_ENCRYPTION_128 = 3;
    public static final int AES_128 = 4;
    private static final byte[] pad = new byte[]{40, -65, 78, 94, 78, 117, -118, 65, 100, 0, 78, 86, -1, -6, 1, 8, 46, 46, 0, -74, -48, 104, 62, -128, 47, 12, -87, -2, 100, 83, 105, 122};
    private static final byte[] salt = new byte[]{115, 65, 108, 84};
    private static final byte[] metadataPad = new byte[]{-1, -1, -1, -1};
    byte[] key;
    int keySize;
    byte[] mkey;
    byte[] extra = new byte[5];
    MessageDigest md5;
    byte[] ownerKey = new byte[32];
    byte[] userKey = new byte[32];
    protected PdfPublicKeySecurityHandler publicKeyHandler = null;
    int permissions;
    byte[] documentID;
    static long seq = System.currentTimeMillis();
    private int revision;
    private ARCFOUREncryption arcfour = new ARCFOUREncryption();
    private int keyLength;
    private boolean encryptMetadata;
    private boolean embeddedFilesOnly;
    private int cryptoMode;

    public PdfEncryption() {
        try {
            this.md5 = MessageDigest.getInstance("MD5");
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
        this.publicKeyHandler = new PdfPublicKeySecurityHandler();
    }

    public PdfEncryption(PdfEncryption pdfEncryption) {
        this();
        this.mkey = (byte[])pdfEncryption.mkey.clone();
        this.ownerKey = (byte[])pdfEncryption.ownerKey.clone();
        this.userKey = (byte[])pdfEncryption.userKey.clone();
        this.permissions = pdfEncryption.permissions;
        if (pdfEncryption.documentID != null) {
            this.documentID = (byte[])pdfEncryption.documentID.clone();
        }
        this.revision = pdfEncryption.revision;
        this.keyLength = pdfEncryption.keyLength;
        this.encryptMetadata = pdfEncryption.encryptMetadata;
        this.embeddedFilesOnly = pdfEncryption.embeddedFilesOnly;
        this.publicKeyHandler = pdfEncryption.publicKeyHandler;
    }

    public void setCryptoMode(int n, int n2) {
        this.cryptoMode = n;
        this.encryptMetadata = (n & 8) == 0;
        this.embeddedFilesOnly = (n & 24) != 0;
        switch (n &= 7) {
            case 0: {
                this.encryptMetadata = true;
                this.embeddedFilesOnly = false;
                this.keyLength = 40;
                this.revision = 2;
                break;
            }
            case 1: {
                this.embeddedFilesOnly = false;
                this.keyLength = n2 > 0 ? n2 : 128;
                this.revision = 3;
                break;
            }
            case 2: {
                this.keyLength = 128;
                this.revision = 4;
                break;
            }
            default: {
                throw new IllegalArgumentException("No valid encryption mode");
            }
        }
    }

    public int getCryptoMode() {
        return this.cryptoMode;
    }

    public boolean isMetadataEncrypted() {
        return this.encryptMetadata;
    }

    public boolean isEmbeddedFilesOnly() {
        return this.embeddedFilesOnly;
    }

    private byte[] padPassword(byte[] arrby) {
        byte[] arrby2 = new byte[32];
        if (arrby == null) {
            System.arraycopy(pad, 0, arrby2, 0, 32);
        } else {
            System.arraycopy(arrby, 0, arrby2, 0, Math.min(arrby.length, 32));
            if (arrby.length < 32) {
                System.arraycopy(pad, 0, arrby2, arrby.length, 32 - arrby.length);
            }
        }
        return arrby2;
    }

    private byte[] computeOwnerKey(byte[] arrby, byte[] arrby2) {
        byte[] arrby3 = new byte[32];
        byte[] arrby4 = this.md5.digest(arrby2);
        if (this.revision == 3 || this.revision == 4) {
            int n;
            byte[] arrby5 = new byte[this.keyLength / 8];
            for (n = 0; n < 50; ++n) {
                System.arraycopy(this.md5.digest(arrby4), 0, arrby4, 0, arrby5.length);
            }
            System.arraycopy(arrby, 0, arrby3, 0, 32);
            for (n = 0; n < 20; ++n) {
                for (int i = 0; i < arrby5.length; ++i) {
                    arrby5[i] = (byte)(arrby4[i] ^ n);
                }
                this.arcfour.prepareARCFOURKey(arrby5);
                this.arcfour.encryptARCFOUR(arrby3);
            }
        } else {
            this.arcfour.prepareARCFOURKey(arrby4, 0, 5);
            this.arcfour.encryptARCFOUR(arrby, arrby3);
        }
        return arrby3;
    }

    private void setupGlobalEncryptionKey(byte[] arrby, byte[] arrby2, byte[] arrby3, int n) {
        this.documentID = arrby;
        this.ownerKey = arrby3;
        this.permissions = n;
        this.mkey = new byte[this.keyLength / 8];
        this.md5.reset();
        this.md5.update(arrby2);
        this.md5.update(arrby3);
        byte[] arrby4 = new byte[]{(byte)n, (byte)(n >> 8), (byte)(n >> 16), (byte)(n >> 24)};
        this.md5.update(arrby4, 0, 4);
        if (arrby != null) {
            this.md5.update(arrby);
        }
        if (!this.encryptMetadata) {
            this.md5.update(metadataPad);
        }
        byte[] arrby5 = new byte[this.mkey.length];
        System.arraycopy(this.md5.digest(), 0, arrby5, 0, this.mkey.length);
        if (this.revision == 3 || this.revision == 4) {
            for (int i = 0; i < 50; ++i) {
                System.arraycopy(this.md5.digest(arrby5), 0, arrby5, 0, this.mkey.length);
            }
        }
        System.arraycopy(arrby5, 0, this.mkey, 0, this.mkey.length);
    }

    private void setupUserKey() {
        if (this.revision == 3 || this.revision == 4) {
            int n;
            this.md5.update(pad);
            byte[] arrby = this.md5.digest(this.documentID);
            System.arraycopy(arrby, 0, this.userKey, 0, 16);
            for (n = 16; n < 32; ++n) {
                this.userKey[n] = 0;
            }
            for (n = 0; n < 20; ++n) {
                for (int i = 0; i < this.mkey.length; ++i) {
                    arrby[i] = (byte)(this.mkey[i] ^ n);
                }
                this.arcfour.prepareARCFOURKey(arrby, 0, this.mkey.length);
                this.arcfour.encryptARCFOUR(this.userKey, 0, 16);
            }
        } else {
            this.arcfour.prepareARCFOURKey(this.mkey);
            this.arcfour.encryptARCFOUR(pad, this.userKey);
        }
    }

    public void setupAllKeys(byte[] arrby, byte[] arrby2, int n) {
        if (arrby2 == null || arrby2.length == 0) {
            arrby2 = this.md5.digest(PdfEncryption.createDocumentId());
        }
        n |= this.revision == 3 || this.revision == 4 ? -3904 : -64;
        byte[] arrby3 = this.padPassword(arrby);
        byte[] arrby4 = this.padPassword(arrby2);
        this.ownerKey = this.computeOwnerKey(arrby3, arrby4);
        this.documentID = PdfEncryption.createDocumentId();
        this.setupByUserPad(this.documentID, arrby3, this.ownerKey, n &= -4);
    }

    public static byte[] createDocumentId() {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
        long l = System.currentTimeMillis();
        long l2 = Runtime.getRuntime().freeMemory();
        String string = "" + l + "+" + l2 + "+" + seq++;
        return messageDigest.digest(string.getBytes());
    }

    public void setupByUserPassword(byte[] arrby, byte[] arrby2, byte[] arrby3, int n) {
        this.setupByUserPad(arrby, this.padPassword(arrby2), arrby3, n);
    }

    private void setupByUserPad(byte[] arrby, byte[] arrby2, byte[] arrby3, int n) {
        this.setupGlobalEncryptionKey(arrby, arrby2, arrby3, n);
        this.setupUserKey();
    }

    public void setupByOwnerPassword(byte[] arrby, byte[] arrby2, byte[] arrby3, byte[] arrby4, int n) {
        this.setupByOwnerPad(arrby, this.padPassword(arrby2), arrby3, arrby4, n);
    }

    private void setupByOwnerPad(byte[] arrby, byte[] arrby2, byte[] arrby3, byte[] arrby4, int n) {
        byte[] arrby5 = this.computeOwnerKey(arrby4, arrby2);
        this.setupGlobalEncryptionKey(arrby, arrby5, arrby4, n);
        this.setupUserKey();
    }

    public void setupByEncryptionKey(byte[] arrby, int n) {
        this.mkey = new byte[n / 8];
        System.arraycopy(arrby, 0, this.mkey, 0, this.mkey.length);
    }

    public void setHashKey(int n, int n2) {
        this.md5.reset();
        this.extra[0] = (byte)n;
        this.extra[1] = (byte)(n >> 8);
        this.extra[2] = (byte)(n >> 16);
        this.extra[3] = (byte)n2;
        this.extra[4] = (byte)(n2 >> 8);
        this.md5.update(this.mkey);
        this.md5.update(this.extra);
        if (this.revision == 4) {
            this.md5.update(salt);
        }
        this.key = this.md5.digest();
        this.keySize = this.mkey.length + 5;
        if (this.keySize > 16) {
            this.keySize = 16;
        }
    }

    public static PdfObject createInfoId(byte[] arrby) {
        int n;
        ByteBuffer byteBuffer = new ByteBuffer(90);
        byteBuffer.append('[').append('<');
        for (n = 0; n < 16; ++n) {
            byteBuffer.appendHex(arrby[n]);
        }
        byteBuffer.append('>').append('<');
        arrby = PdfEncryption.createDocumentId();
        for (n = 0; n < 16; ++n) {
            byteBuffer.appendHex(arrby[n]);
        }
        byteBuffer.append('>').append(']');
        return new PdfLiteral(byteBuffer.toByteArray());
    }

    public PdfDictionary getEncryptionDictionary() {
        PdfDictionary pdfDictionary = new PdfDictionary();
        if (this.publicKeyHandler.getRecipientsSize() > 0) {
            Object object;
            Object object2;
            PdfArray pdfArray = null;
            pdfDictionary.put(PdfName.FILTER, PdfName.PUBSEC);
            pdfDictionary.put(PdfName.R, new PdfNumber(this.revision));
            try {
                pdfArray = this.publicKeyHandler.getEncodedRecipients();
            }
            catch (Exception var3_4) {
                throw new ExceptionConverter(var3_4);
            }
            if (this.revision == 2) {
                pdfDictionary.put(PdfName.V, new PdfNumber(1));
                pdfDictionary.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
                pdfDictionary.put(PdfName.RECIPIENTS, pdfArray);
            } else if (this.revision == 3 && this.encryptMetadata) {
                pdfDictionary.put(PdfName.V, new PdfNumber(2));
                pdfDictionary.put(PdfName.LENGTH, new PdfNumber(128));
                pdfDictionary.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
                pdfDictionary.put(PdfName.RECIPIENTS, pdfArray);
            } else {
                pdfDictionary.put(PdfName.R, new PdfNumber(4));
                pdfDictionary.put(PdfName.V, new PdfNumber(4));
                pdfDictionary.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S5);
                object = new PdfDictionary();
                object.put(PdfName.RECIPIENTS, pdfArray);
                if (!this.encryptMetadata) {
                    object.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                }
                if (this.revision == 4) {
                    object.put(PdfName.CFM, PdfName.AESV2);
                } else {
                    object.put(PdfName.CFM, PdfName.V2);
                }
                object2 = new PdfDictionary();
                object2.put(PdfName.DEFAULTCRYPTFILER, (PdfObject)object);
                pdfDictionary.put(PdfName.CF, (PdfObject)object2);
                if (this.embeddedFilesOnly) {
                    pdfDictionary.put(PdfName.EFF, PdfName.DEFAULTCRYPTFILER);
                    pdfDictionary.put(PdfName.STRF, PdfName.IDENTITY);
                    pdfDictionary.put(PdfName.STMF, PdfName.IDENTITY);
                } else {
                    pdfDictionary.put(PdfName.STRF, PdfName.DEFAULTCRYPTFILER);
                    pdfDictionary.put(PdfName.STMF, PdfName.DEFAULTCRYPTFILER);
                }
            }
            object = null;
            object2 = null;
            try {
                object = MessageDigest.getInstance("SHA-1");
                object.update(this.publicKeyHandler.getSeed());
                for (int i = 0; i < this.publicKeyHandler.getRecipientsSize(); ++i) {
                    object2 = this.publicKeyHandler.getEncodedRecipient(i);
                    object.update((byte[])object2);
                }
                if (!this.encryptMetadata) {
                    object.update(new byte[]{-1, -1, -1, -1});
                }
            }
            catch (Exception var5_9) {
                throw new ExceptionConverter(var5_9);
            }
            byte[] arrby = object.digest();
            this.setupByEncryptionKey(arrby, this.keyLength);
        } else {
            pdfDictionary.put(PdfName.FILTER, PdfName.STANDARD);
            pdfDictionary.put(PdfName.O, new PdfLiteral(PdfContentByte.escapeString(this.ownerKey)));
            pdfDictionary.put(PdfName.U, new PdfLiteral(PdfContentByte.escapeString(this.userKey)));
            pdfDictionary.put(PdfName.P, new PdfNumber(this.permissions));
            pdfDictionary.put(PdfName.R, new PdfNumber(this.revision));
            if (this.revision == 2) {
                pdfDictionary.put(PdfName.V, new PdfNumber(1));
            } else if (this.revision == 3 && this.encryptMetadata) {
                pdfDictionary.put(PdfName.V, new PdfNumber(2));
                pdfDictionary.put(PdfName.LENGTH, new PdfNumber(128));
            } else {
                if (!this.encryptMetadata) {
                    pdfDictionary.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                }
                pdfDictionary.put(PdfName.R, new PdfNumber(4));
                pdfDictionary.put(PdfName.V, new PdfNumber(4));
                pdfDictionary.put(PdfName.LENGTH, new PdfNumber(128));
                PdfDictionary pdfDictionary2 = new PdfDictionary();
                pdfDictionary2.put(PdfName.LENGTH, new PdfNumber(16));
                if (this.embeddedFilesOnly) {
                    pdfDictionary2.put(PdfName.AUTHEVENT, PdfName.EFOPEN);
                    pdfDictionary.put(PdfName.EFF, PdfName.STDCF);
                    pdfDictionary.put(PdfName.STRF, PdfName.IDENTITY);
                    pdfDictionary.put(PdfName.STMF, PdfName.IDENTITY);
                } else {
                    pdfDictionary2.put(PdfName.AUTHEVENT, PdfName.DOCOPEN);
                    pdfDictionary.put(PdfName.STRF, PdfName.STDCF);
                    pdfDictionary.put(PdfName.STMF, PdfName.STDCF);
                }
                if (this.revision == 4) {
                    pdfDictionary2.put(PdfName.CFM, PdfName.AESV2);
                } else {
                    pdfDictionary2.put(PdfName.CFM, PdfName.V2);
                }
                PdfDictionary pdfDictionary3 = new PdfDictionary();
                pdfDictionary3.put(PdfName.STDCF, pdfDictionary2);
                pdfDictionary.put(PdfName.CF, pdfDictionary3);
            }
        }
        return pdfDictionary;
    }

    public PdfObject getFileID() {
        return PdfEncryption.createInfoId(this.documentID);
    }

    public OutputStreamEncryption getEncryptionStream(OutputStream outputStream) {
        return new OutputStreamEncryption(outputStream, this.key, 0, this.keySize, this.revision);
    }

    public int calculateStreamSize(int n) {
        if (this.revision == 4) {
            return (n & 2147483632) + 32;
        }
        return n;
    }

    public byte[] encryptByteArray(byte[] arrby) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamEncryption outputStreamEncryption = this.getEncryptionStream(byteArrayOutputStream);
            outputStreamEncryption.write(arrby);
            outputStreamEncryption.finish();
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    public StandardDecryption getDecryptor() {
        return new StandardDecryption(this.key, 0, this.keySize, this.revision);
    }

    public byte[] decryptByteArray(byte[] arrby) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            StandardDecryption standardDecryption = this.getDecryptor();
            byte[] arrby2 = standardDecryption.update(arrby, 0, arrby.length);
            if (arrby2 != null) {
                byteArrayOutputStream.write(arrby2);
            }
            if ((arrby2 = standardDecryption.finish()) != null) {
                byteArrayOutputStream.write(arrby2);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    public void addRecipient(Certificate certificate, int n) {
        this.documentID = PdfEncryption.createDocumentId();
        this.publicKeyHandler.addRecipient(new PdfPublicKeyRecipient(certificate, n));
    }

    public byte[] computeUserPassword(byte[] arrby) {
        byte[] arrby2 = this.computeOwnerKey(this.ownerKey, this.padPassword(arrby));
        for (int i = 0; i < arrby2.length; ++i) {
            boolean bl = true;
            for (int j = 0; j < arrby2.length - i; ++j) {
                if (arrby2[i + j] == pad[j]) continue;
                bl = false;
                break;
            }
            if (!bl) continue;
            byte[] arrby3 = new byte[i];
            System.arraycopy(arrby2, 0, arrby3, 0, i);
            return arrby3;
        }
        return arrby2;
    }
}

