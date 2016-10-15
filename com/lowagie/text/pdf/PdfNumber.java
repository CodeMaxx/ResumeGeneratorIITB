/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfObject;

public class PdfNumber
extends PdfObject {
    private double value;

    public PdfNumber(String string) {
        super(2);
        try {
            this.value = Double.parseDouble(string.trim());
            this.setContent(string);
        }
        catch (NumberFormatException var2_2) {
            throw new RuntimeException(string + " is not a valid number - " + var2_2.toString());
        }
    }

    public PdfNumber(int n) {
        super(2);
        this.value = n;
        this.setContent(String.valueOf(n));
    }

    public PdfNumber(double d) {
        super(2);
        this.value = d;
        this.setContent(ByteBuffer.formatDouble(d));
    }

    public PdfNumber(float f) {
        this((double)f);
    }

    public int intValue() {
        return (int)this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public float floatValue() {
        return (float)this.value;
    }

    public void increment() {
        this.value += 1.0;
        this.setContent(ByteBuffer.formatDouble(this.value));
    }
}

