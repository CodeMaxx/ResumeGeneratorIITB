/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfObject;

public class PdfNull
extends PdfObject {
    public static final PdfNull PDFNULL = new PdfNull();
    private static final String CONTENT = "null";

    public PdfNull() {
        super(8, "null");
    }

    public String toString() {
        return "null";
    }
}

