/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;

public abstract class PdfObject {
    public static final int BOOLEAN = 1;
    public static final int NUMBER = 2;
    public static final int STRING = 3;
    public static final int NAME = 4;
    public static final int ARRAY = 5;
    public static final int DICTIONARY = 6;
    public static final int STREAM = 7;
    public static final int NULL = 8;
    public static final int INDIRECT = 10;
    public static final String NOTHING = "";
    public static final String TEXT_PDFDOCENCODING = "PDF";
    public static final String TEXT_UNICODE = "UnicodeBig";
    protected byte[] bytes;
    protected int type;
    protected PRIndirectReference indRef;

    protected PdfObject(int n) {
        this.type = n;
    }

    protected PdfObject(int n, String string) {
        this.type = n;
        this.bytes = PdfEncodings.convertToBytes(string, null);
    }

    protected PdfObject(int n, byte[] arrby) {
        this.bytes = arrby;
        this.type = n;
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        if (this.bytes != null) {
            outputStream.write(this.bytes);
        }
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public boolean canBeInObjStm() {
        return this.type >= 1 && this.type <= 6 || this.type == 8;
    }

    public String toString() {
        if (this.bytes == null) {
            return super.toString();
        }
        return PdfEncodings.convertToString(this.bytes, null);
    }

    public int length() {
        return this.toString().length();
    }

    protected void setContent(String string) {
        this.bytes = PdfEncodings.convertToBytes(string, null);
    }

    public int type() {
        return this.type;
    }

    public boolean isNull() {
        return this.type == 8;
    }

    public boolean isBoolean() {
        return this.type == 1;
    }

    public boolean isNumber() {
        return this.type == 2;
    }

    public boolean isString() {
        return this.type == 3;
    }

    public boolean isName() {
        return this.type == 4;
    }

    public boolean isArray() {
        return this.type == 5;
    }

    public boolean isDictionary() {
        return this.type == 6;
    }

    public boolean isStream() {
        return this.type == 7;
    }

    public boolean isIndirect() {
        return this.type == 10;
    }

    public PRIndirectReference getIndRef() {
        return this.indRef;
    }

    public void setIndRef(PRIndirectReference pRIndirectReference) {
        this.indRef = pRIndirectReference;
    }
}

