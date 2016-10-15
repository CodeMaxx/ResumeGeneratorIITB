/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.PdfShadingPattern;

public class ShadingColor
extends ExtendedColor {
    private static final long serialVersionUID = 4817929454941328671L;
    PdfShadingPattern shadingPattern;

    public ShadingColor(PdfShadingPattern pdfShadingPattern) {
        super(5, 0.5f, 0.5f, 0.5f);
        this.shadingPattern = pdfShadingPattern;
    }

    public PdfShadingPattern getPdfShadingPattern() {
        return this.shadingPattern;
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public int hashCode() {
        return this.shadingPattern.hashCode();
    }
}

