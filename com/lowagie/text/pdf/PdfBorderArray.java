/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;

public class PdfBorderArray
extends PdfArray {
    public PdfBorderArray(float f, float f2, float f3) {
        this(f, f2, f3, null);
    }

    public PdfBorderArray(float f, float f2, float f3, PdfDashPattern pdfDashPattern) {
        super(new PdfNumber(f));
        this.add(new PdfNumber(f2));
        this.add(new PdfNumber(f3));
        if (pdfDashPattern != null) {
            this.add(pdfDashPattern);
        }
    }
}

