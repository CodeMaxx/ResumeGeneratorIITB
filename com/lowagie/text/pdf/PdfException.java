/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;

public class PdfException
extends DocumentException {
    private static final long serialVersionUID = 6767433960955483999L;

    public PdfException(Exception exception) {
        super(exception);
    }

    PdfException() {
    }

    PdfException(String string) {
        super(string);
    }
}

