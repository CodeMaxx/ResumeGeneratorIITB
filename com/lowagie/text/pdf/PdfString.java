/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PdfString
extends PdfObject {
    protected String value = "";
    protected String originalValue = null;
    protected String encoding = "PDF";
    protected int objNum = 0;
    protected int objGen = 0;
    protected boolean hexWriting = false;

    public PdfString() {
        super(3);
    }

    public PdfString(String string) {
        super(3);
        this.value = string;
    }

    public PdfString(String string, String string2) {
        super(3);
        this.value = string;
        this.encoding = string2;
    }

    public PdfString(byte[] arrby) {
        super(3);
        this.value = PdfEncodings.convertToString(arrby, null);
        this.encoding = "";
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        byte[] arrby = this.getBytes();
        PdfEncryption pdfEncryption = null;
        if (pdfWriter != null) {
            pdfEncryption = pdfWriter.getEncryption();
        }
        if (pdfEncryption != null && !pdfEncryption.isEmbeddedFilesOnly()) {
            arrby = pdfEncryption.encryptByteArray(arrby);
        }
        if (this.hexWriting) {
            ByteBuffer byteBuffer = new ByteBuffer();
            byteBuffer.append('<');
            int n = arrby.length;
            for (int i = 0; i < n; ++i) {
                byteBuffer.appendHex(arrby[i]);
            }
            byteBuffer.append('>');
            outputStream.write(byteBuffer.toByteArray());
        } else {
            outputStream.write(PdfContentByte.escapeString(arrby));
        }
    }

    public String toString() {
        return this.value;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String toUnicodeString() {
        if (this.encoding != null && this.encoding.length() != 0) {
            return this.value;
        }
        this.getBytes();
        if (this.bytes.length >= 2 && this.bytes[0] == -2 && this.bytes[1] == -1) {
            return PdfEncodings.convertToString(this.bytes, "UnicodeBig");
        }
        return PdfEncodings.convertToString(this.bytes, "PDF");
    }

    void setObjNum(int n, int n2) {
        this.objNum = n;
        this.objGen = n2;
    }

    void decrypt(PdfReader pdfReader) {
        PdfEncryption pdfEncryption = pdfReader.getDecrypt();
        if (pdfEncryption != null) {
            this.originalValue = this.value;
            pdfEncryption.setHashKey(this.objNum, this.objGen);
            this.bytes = PdfEncodings.convertToBytes(this.value, null);
            this.bytes = pdfEncryption.decryptByteArray(this.bytes);
            this.value = PdfEncodings.convertToString(this.bytes, null);
        }
    }

    public byte[] getBytes() {
        if (this.bytes == null) {
            this.bytes = this.encoding != null && this.encoding.equals("UnicodeBig") && PdfEncodings.isPdfDocEncoding(this.value) ? PdfEncodings.convertToBytes(this.value, "PDF") : PdfEncodings.convertToBytes(this.value, this.encoding);
        }
        return this.bytes;
    }

    public byte[] getOriginalBytes() {
        if (this.originalValue == null) {
            return this.getBytes();
        }
        return PdfEncodings.convertToBytes(this.originalValue, null);
    }

    public PdfString setHexWriting(boolean bl) {
        this.hexWriting = bl;
        return this;
    }

    public boolean isHexWriting() {
        return this.hexWriting;
    }
}

