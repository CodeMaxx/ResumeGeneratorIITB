/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;

public class PdfPattern
extends PdfStream {
    PdfPattern(PdfPatternPainter pdfPatternPainter) {
        this(pdfPatternPainter, -1);
    }

    PdfPattern(PdfPatternPainter pdfPatternPainter, int n) {
        PdfNumber pdfNumber = new PdfNumber(1);
        PdfArray pdfArray = pdfPatternPainter.getMatrix();
        if (pdfArray != null) {
            this.put(PdfName.MATRIX, pdfArray);
        }
        this.put(PdfName.TYPE, PdfName.PATTERN);
        this.put(PdfName.BBOX, new PdfRectangle(pdfPatternPainter.getBoundingBox()));
        this.put(PdfName.RESOURCES, pdfPatternPainter.getResources());
        this.put(PdfName.TILINGTYPE, pdfNumber);
        this.put(PdfName.PATTERNTYPE, pdfNumber);
        if (pdfPatternPainter.isStencil()) {
            this.put(PdfName.PAINTTYPE, new PdfNumber(2));
        } else {
            this.put(PdfName.PAINTTYPE, pdfNumber);
        }
        this.put(PdfName.XSTEP, new PdfNumber(pdfPatternPainter.getXStep()));
        this.put(PdfName.YSTEP, new PdfNumber(pdfPatternPainter.getYStep()));
        this.bytes = pdfPatternPainter.toPdf(null);
        this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        try {
            this.flateCompress(n);
        }
        catch (Exception var5_5) {
            throw new ExceptionConverter(var5_5);
        }
    }
}

