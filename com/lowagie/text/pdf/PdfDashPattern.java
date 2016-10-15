/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PdfDashPattern
extends PdfArray {
    private float dash = -1.0f;
    private float gap = -1.0f;
    private float phase = -1.0f;

    public PdfDashPattern() {
    }

    public PdfDashPattern(float f) {
        super(new PdfNumber(f));
        this.dash = f;
    }

    public PdfDashPattern(float f, float f2) {
        super(new PdfNumber(f));
        this.add(new PdfNumber(f2));
        this.dash = f;
        this.gap = f2;
    }

    public PdfDashPattern(float f, float f2, float f3) {
        super(new PdfNumber(f));
        this.add(new PdfNumber(f2));
        this.dash = f;
        this.gap = f2;
        this.phase = f3;
    }

    public void add(float f) {
        this.add(new PdfNumber(f));
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        outputStream.write(91);
        if (this.dash >= 0.0f) {
            new PdfNumber(this.dash).toPdf(pdfWriter, outputStream);
            if (this.gap >= 0.0f) {
                outputStream.write(32);
                new PdfNumber(this.gap).toPdf(pdfWriter, outputStream);
            }
        }
        outputStream.write(93);
        if (this.phase >= 0.0f) {
            outputStream.write(32);
            new PdfNumber(this.phase).toPdf(pdfWriter, outputStream);
        }
    }
}

