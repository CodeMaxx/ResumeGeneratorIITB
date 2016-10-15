/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfWriter;

class ColorDetails {
    PdfIndirectReference indirectReference;
    PdfName colorName;
    PdfSpotColor spotcolor;

    ColorDetails(PdfName pdfName, PdfIndirectReference pdfIndirectReference, PdfSpotColor pdfSpotColor) {
        this.colorName = pdfName;
        this.indirectReference = pdfIndirectReference;
        this.spotcolor = pdfSpotColor;
    }

    PdfIndirectReference getIndirectReference() {
        return this.indirectReference;
    }

    PdfName getColorName() {
        return this.colorName;
    }

    PdfObject getSpotColor(PdfWriter pdfWriter) {
        return this.spotcolor.getSpotObject(pdfWriter);
    }
}

