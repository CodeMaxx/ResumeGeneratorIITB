/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfObject;

public class PdfBoolean
extends PdfObject {
    public static final PdfBoolean PDFTRUE = new PdfBoolean(true);
    public static final PdfBoolean PDFFALSE = new PdfBoolean(false);
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    private boolean value;

    public PdfBoolean(boolean bl) {
        super(1);
        if (bl) {
            this.setContent("true");
        } else {
            this.setContent("false");
        }
        this.value = bl;
    }

    public PdfBoolean(String string) throws BadPdfFormatException {
        super(1, string);
        if (string.equals("true")) {
            this.value = true;
        } else if (string.equals("false")) {
            this.value = false;
        } else {
            throw new BadPdfFormatException("The value has to be 'true' of 'false', instead of '" + string + "'.");
        }
    }

    public boolean booleanValue() {
        return this.value;
    }

    public String toString() {
        return this.value ? "true" : "false";
    }
}

