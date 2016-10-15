/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.draw;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.draw.VerticalPositionMark;
import java.awt.Color;

public class LineSeparator
extends VerticalPositionMark {
    protected float lineWidth = 1.0f;
    protected float percentage = 100.0f;
    protected Color lineColor;
    protected int alignment = 1;

    public LineSeparator(float f, float f2, Color color, int n, float f3) {
        this.lineWidth = f;
        this.percentage = f2;
        this.lineColor = color;
        this.alignment = n;
        this.offset = f3;
    }

    public LineSeparator() {
    }

    public void draw(PdfContentByte pdfContentByte, float f, float f2, float f3, float f4, float f5) {
        pdfContentByte.saveState();
        this.drawLine(pdfContentByte, f, f3, f5);
        pdfContentByte.restoreState();
    }

    public void drawLine(PdfContentByte pdfContentByte, float f, float f2, float f3) {
        float f4;
        float f5 = this.getPercentage() < 0.0f ? - this.getPercentage() : (f2 - f) * this.getPercentage() / 100.0f;
        switch (this.getAlignment()) {
            case 0: {
                f4 = 0.0f;
                break;
            }
            case 2: {
                f4 = f2 - f - f5;
                break;
            }
            default: {
                f4 = (f2 - f - f5) / 2.0f;
            }
        }
        pdfContentByte.setLineWidth(this.getLineWidth());
        if (this.getLineColor() != null) {
            pdfContentByte.setColorStroke(this.getLineColor());
        }
        pdfContentByte.moveTo(f4 + f, f3 + this.offset);
        pdfContentByte.lineTo(f4 + f5 + f, f3 + this.offset);
        pdfContentByte.stroke();
    }

    public float getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(float f) {
        this.lineWidth = f;
    }

    public float getPercentage() {
        return this.percentage;
    }

    public void setPercentage(float f) {
        this.percentage = f;
    }

    public Color getLineColor() {
        return this.lineColor;
    }

    public void setLineColor(Color color) {
        this.lineColor = color;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int n) {
        this.alignment = n;
    }
}

