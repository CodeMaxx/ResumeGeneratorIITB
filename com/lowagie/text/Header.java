/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Meta;

public class Header
extends Meta {
    private StringBuffer name;

    public Header(String string, String string2) {
        super(0, string2);
        this.name = new StringBuffer(string);
    }

    public String getName() {
        return this.name.toString();
    }
}

