/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;

class PdfFont
implements Comparable {
    private BaseFont font;
    private float size;
    protected Image image;
    protected float hScale = 1.0f;

    PdfFont(BaseFont baseFont, float f) {
        this.size = f;
        this.font = baseFont;
    }

    public int compareTo(Object object) {
        if (this.image != null) {
            return 0;
        }
        if (object == null) {
            return -1;
        }
        try {
            PdfFont pdfFont = (PdfFont)object;
            if (this.font != pdfFont.font) {
                return 1;
            }
            if (this.size() != pdfFont.size()) {
                return 2;
            }
            return 0;
        }
        catch (ClassCastException var3_3) {
            return -2;
        }
    }

    float size() {
        if (this.image == null) {
            return this.size;
        }
        return this.image.getScaledHeight();
    }

    float width() {
        return this.width(32);
    }

    float width(int n) {
        if (this.image == null) {
            return this.font.getWidthPoint(n, this.size) * this.hScale;
        }
        return this.image.getScaledWidth();
    }

    float width(String string) {
        if (this.image == null) {
            return this.font.getWidthPoint(string, this.size) * this.hScale;
        }
        return this.image.getScaledWidth();
    }

    BaseFont getFont() {
        return this.font;
    }

    void setImage(Image image) {
        this.image = image;
    }

    static PdfFont getDefaultFont() {
        try {
            BaseFont baseFont = BaseFont.createFont("Helvetica", "Cp1252", false);
            return new PdfFont(baseFont, 12.0f);
        }
        catch (Exception var0_1) {
            throw new ExceptionConverter(var0_1);
        }
    }

    void setHorizontalScaling(float f) {
        this.hScale = f;
    }
}

