/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.internal.PolylineShapeIterator;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class PolylineShape
implements Shape {
    protected int[] x;
    protected int[] y;
    protected int np;

    public PolylineShape(int[] arrn, int[] arrn2, int n) {
        this.np = n;
        this.x = new int[this.np];
        this.y = new int[this.np];
        System.arraycopy(arrn, 0, this.x, 0, this.np);
        System.arraycopy(arrn2, 0, this.y, 0, this.np);
    }

    public Rectangle2D getBounds2D() {
        int[] arrn = this.rect();
        return arrn == null ? null : new Rectangle2D.Double(arrn[0], arrn[1], arrn[2], arrn[3]);
    }

    public Rectangle getBounds() {
        return this.getBounds2D().getBounds();
    }

    private int[] rect() {
        if (this.np == 0) {
            return null;
        }
        int n = this.x[0];
        int n2 = this.y[0];
        int n3 = this.x[0];
        int n4 = this.y[0];
        for (int i = 1; i < this.np; ++i) {
            if (this.x[i] < n) {
                n = this.x[i];
            } else if (this.x[i] > n3) {
                n3 = this.x[i];
            }
            if (this.y[i] < n2) {
                n2 = this.y[i];
                continue;
            }
            if (this.y[i] <= n4) continue;
            n4 = this.y[i];
        }
        return new int[]{n, n2, n3 - n, n4 - n2};
    }

    public boolean contains(double d, double d2) {
        return false;
    }

    public boolean contains(Point2D point2D) {
        return false;
    }

    public boolean contains(double d, double d2, double d3, double d4) {
        return false;
    }

    public boolean contains(Rectangle2D rectangle2D) {
        return false;
    }

    public boolean intersects(double d, double d2, double d3, double d4) {
        return this.intersects(new Rectangle2D.Double(d, d2, d3, d4));
    }

    public boolean intersects(Rectangle2D rectangle2D) {
        if (this.np == 0) {
            return false;
        }
        Line2D.Double double_ = new Line2D.Double(this.x[0], this.y[0], this.x[0], this.y[0]);
        for (int i = 1; i < this.np; ++i) {
            double_.setLine(this.x[i - 1], this.y[i - 1], this.x[i], this.y[i]);
            if (!double_.intersects(rectangle2D)) continue;
            return true;
        }
        return false;
    }

    public PathIterator getPathIterator(AffineTransform affineTransform) {
        return new PolylineShapeIterator(this, affineTransform);
    }

    public PathIterator getPathIterator(AffineTransform affineTransform, double d) {
        return new PolylineShapeIterator(this, affineTransform);
    }
}

