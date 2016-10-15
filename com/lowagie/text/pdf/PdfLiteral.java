/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class PdfLiteral
extends PdfObject {
    private int position;

    public PdfLiteral(String string) {
        super(0, string);
    }

    public PdfLiteral(byte[] arrby) {
        super(0, arrby);
    }

    public PdfLiteral(int n) {
        super(0, (byte[])null);
        this.bytes = new byte[n];
        Arrays.fill(this.bytes, 32);
    }

    public PdfLiteral(int n, String string) {
        super(n, string);
    }

    public PdfLiteral(int n, byte[] arrby) {
        super(n, arrby);
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        if (outputStream instanceof OutputStreamCounter) {
            this.position = ((OutputStreamCounter)outputStream).getCounter();
        }
        super.toPdf(pdfWriter, outputStream);
    }

    public int getPosition() {
        return this.position;
    }

    public int getPosLength() {
        if (this.bytes != null) {
            return this.bytes.length;
        }
        return 0;
    }
}

