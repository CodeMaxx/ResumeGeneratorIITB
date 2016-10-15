/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;

public class PdfRectangle
extends PdfArray {
    private float llx = 0.0f;
    private float lly = 0.0f;
    private float urx = 0.0f;
    private float ury = 0.0f;

    public PdfRectangle(float f, float f2, float f3, float f4, int n) {
        if (n == 90 || n == 270) {
            this.llx = f2;
            this.lly = f;
            this.urx = f4;
            this.ury = f3;
        } else {
            this.llx = f;
            this.lly = f2;
            this.urx = f3;
            this.ury = f4;
        }
        super.add(new PdfNumber(this.llx));
        super.add(new PdfNumber(this.lly));
        super.add(new PdfNumber(this.urx));
        super.add(new PdfNumber(this.ury));
    }

    public PdfRectangle(float f, float f2, float f3, float f4) {
        this(f, f2, f3, f4, 0);
    }

    public PdfRectangle(float f, float f2, int n) {
        this(0.0f, 0.0f, f, f2, n);
    }

    public PdfRectangle(float f, float f2) {
        this(0.0f, 0.0f, f, f2, 0);
    }

    public PdfRectangle(Rectangle rectangle, int n) {
        this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), n);
    }

    public PdfRectangle(Rectangle rectangle) {
        this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), 0);
    }

    public Rectangle getRectangle() {
        return new Rectangle(this.left(), this.bottom(), this.right(), this.top());
    }

    public boolean add(PdfObject pdfObject) {
        return false;
    }

    public float left() {
        return this.llx;
    }

    public float right() {
        return this.urx;
    }

    public float top() {
        return this.ury;
    }

    public float bottom() {
        return this.lly;
    }

    public float left(int n) {
        return this.llx + (float)n;
    }

    public float right(int n) {
        return this.urx - (float)n;
    }

    public float top(int n) {
        return this.ury - (float)n;
    }

    public float bottom(int n) {
        return this.lly + (float)n;
    }

    public float width() {
        return this.urx - this.llx;
    }

    public float height() {
        return this.ury - this.lly;
    }

    public PdfRectangle rotate() {
        return new PdfRectangle(this.lly, this.llx, this.ury, this.urx, 0);
    }
}

