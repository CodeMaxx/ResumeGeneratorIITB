/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;

public class BadElementException
extends DocumentException {
    private static final long serialVersionUID = -799006030723822254L;

    public BadElementException(Exception exception) {
        super(exception);
    }

    BadElementException() {
    }

    public BadElementException(String string) {
        super(string);
    }
}

