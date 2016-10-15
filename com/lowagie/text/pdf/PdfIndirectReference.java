/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfObject;

public class PdfIndirectReference
extends PdfObject {
    protected int number;
    protected int generation = 0;

    protected PdfIndirectReference() {
        super(0);
    }

    PdfIndirectReference(int n, int n2, int n3) {
        super(0, "" + n2 + " " + n3 + " R");
        this.number = n2;
        this.generation = n3;
    }

    PdfIndirectReference(int n, int n2) {
        this(n, n2, 0);
    }

    public int getNumber() {
        return this.number;
    }

    public int getGeneration() {
        return this.generation;
    }

    public String toString() {
        return "" + this.number + " " + this.generation + " R";
    }
}

