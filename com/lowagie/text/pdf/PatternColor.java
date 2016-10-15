/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.PdfPatternPainter;

public class PatternColor
extends ExtendedColor {
    private static final long serialVersionUID = -1185448552860615964L;
    PdfPatternPainter painter;

    public PatternColor(PdfPatternPainter pdfPatternPainter) {
        super(4, 0.5f, 0.5f, 0.5f);
        this.painter = pdfPatternPainter;
    }

    public PdfPatternPainter getPainter() {
        return this.painter;
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public int hashCode() {
        return this.painter.hashCode();
    }
}

