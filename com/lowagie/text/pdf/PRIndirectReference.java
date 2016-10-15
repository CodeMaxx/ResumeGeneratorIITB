/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PRIndirectReference
extends PdfIndirectReference {
    protected PdfReader reader;

    PRIndirectReference(PdfReader pdfReader, int n, int n2) {
        this.type = 10;
        this.number = n;
        this.generation = n2;
        this.reader = pdfReader;
    }

    PRIndirectReference(PdfReader pdfReader, int n) {
        this(pdfReader, n, 0);
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        int n = pdfWriter.getNewObjectNumber(this.reader, this.number, this.generation);
        outputStream.write(PdfEncodings.convertToBytes("" + n + " 0 R", null));
    }

    public PdfReader getReader() {
        return this.reader;
    }

    public void setNumber(int n, int n2) {
        this.number = n;
        this.generation = n2;
    }
}

