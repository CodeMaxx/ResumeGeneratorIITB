/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;

public class GrayColor
extends ExtendedColor {
    private static final long serialVersionUID = -6571835680819282746L;
    private float gray;
    public static final GrayColor GRAYBLACK = new GrayColor(0.0f);
    public static final GrayColor GRAYWHITE = new GrayColor(1.0f);

    public GrayColor(int n) {
        this((float)n / 255.0f);
    }

    public GrayColor(float f) {
        super(1, f, f, f);
        this.gray = GrayColor.normalize(f);
    }

    public float getGray() {
        return this.gray;
    }

    public boolean equals(Object object) {
        return object instanceof GrayColor && ((GrayColor)object).gray == this.gray;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.gray);
    }
}

