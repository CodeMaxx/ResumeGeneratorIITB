/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.internal.PolylineShape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.NoSuchElementException;

public class PolylineShapeIterator
implements PathIterator {
    protected PolylineShape poly;
    protected AffineTransform affine;
    protected int index;

    PolylineShapeIterator(PolylineShape polylineShape, AffineTransform affineTransform) {
        this.poly = polylineShape;
        this.affine = affineTransform;
    }

    public int currentSegment(double[] arrd) {
        if (this.isDone()) {
            throw new NoSuchElementException("line iterator out of bounds");
        }
        int n = this.index == 0 ? 0 : 1;
        arrd[0] = this.poly.x[this.index];
        arrd[1] = this.poly.y[this.index];
        if (this.affine != null) {
            this.affine.transform(arrd, 0, arrd, 0, 1);
        }
        return n;
    }

    public int currentSegment(float[] arrf) {
        if (this.isDone()) {
            throw new NoSuchElementException("line iterator out of bounds");
        }
        int n = this.index == 0 ? 0 : 1;
        arrf[0] = this.poly.x[this.index];
        arrf[1] = this.poly.y[this.index];
        if (this.affine != null) {
            this.affine.transform(arrf, 0, arrf, 0, 1);
        }
        return n;
    }

    public int getWindingRule() {
        return 1;
    }

    public boolean isDone() {
        return this.index >= this.poly.np;
    }

    public void next() {
        ++this.index;
    }
}

