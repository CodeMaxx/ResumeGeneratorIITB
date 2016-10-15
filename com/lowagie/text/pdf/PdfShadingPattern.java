/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ColorDetails;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class PdfShadingPattern
extends PdfDictionary {
    protected PdfShading shading;
    protected PdfWriter writer;
    protected float[] matrix = new float[]{1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f};
    protected PdfName patternName;
    protected PdfIndirectReference patternReference;

    public PdfShadingPattern(PdfShading pdfShading) {
        this.writer = pdfShading.getWriter();
        this.put(PdfName.PATTERNTYPE, new PdfNumber(2));
        this.shading = pdfShading;
    }

    PdfName getPatternName() {
        return this.patternName;
    }

    PdfName getShadingName() {
        return this.shading.getShadingName();
    }

    PdfIndirectReference getPatternReference() {
        if (this.patternReference == null) {
            this.patternReference = this.writer.getPdfIndirectReference();
        }
        return this.patternReference;
    }

    PdfIndirectReference getShadingReference() {
        return this.shading.getShadingReference();
    }

    void setName(int n) {
        this.patternName = new PdfName("P" + n);
    }

    void addToBody() throws IOException {
        this.put(PdfName.SHADING, this.getShadingReference());
        this.put(PdfName.MATRIX, new PdfArray(this.matrix));
        this.writer.addToBody((PdfObject)this, this.getPatternReference());
    }

    public void setMatrix(float[] arrf) {
        if (arrf.length != 6) {
            throw new RuntimeException("The matrix size must be 6.");
        }
        this.matrix = arrf;
    }

    public float[] getMatrix() {
        return this.matrix;
    }

    public PdfShading getShading() {
        return this.shading;
    }

    ColorDetails getColorDetails() {
        return this.shading.getColorDetails();
    }
}

