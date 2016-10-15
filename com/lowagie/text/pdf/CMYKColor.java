/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ExtendedColor;

public class CMYKColor
extends ExtendedColor {
    private static final long serialVersionUID = 5940378778276468452L;
    float cyan;
    float magenta;
    float yellow;
    float black;

    public CMYKColor(int n, int n2, int n3, int n4) {
        this((float)n / 255.0f, (float)n2 / 255.0f, (float)n3 / 255.0f, (float)n4 / 255.0f);
    }

    public CMYKColor(float f, float f2, float f3, float f4) {
        super(2, 1.0f - f - f4, 1.0f - f2 - f4, 1.0f - f3 - f4);
        this.cyan = CMYKColor.normalize(f);
        this.magenta = CMYKColor.normalize(f2);
        this.yellow = CMYKColor.normalize(f3);
        this.black = CMYKColor.normalize(f4);
    }

    public float getCyan() {
        return this.cyan;
    }

    public float getMagenta() {
        return this.magenta;
    }

    public float getYellow() {
        return this.yellow;
    }

    public float getBlack() {
        return this.black;
    }

    public boolean equals(Object object) {
        if (!(object instanceof CMYKColor)) {
            return false;
        }
        CMYKColor cMYKColor = (CMYKColor)object;
        return this.cyan == cMYKColor.cyan && this.magenta == cMYKColor.magenta && this.yellow == cMYKColor.yellow && this.black == cMYKColor.black;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.cyan) ^ Float.floatToIntBits(this.magenta) ^ Float.floatToIntBits(this.yellow) ^ Float.floatToIntBits(this.black);
    }
}

