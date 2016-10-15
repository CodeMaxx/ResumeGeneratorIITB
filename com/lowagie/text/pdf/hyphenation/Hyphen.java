/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

import java.io.Serializable;

public class Hyphen
implements Serializable {
    private static final long serialVersionUID = -7666138517324763063L;
    public String preBreak;
    public String noBreak;
    public String postBreak;

    Hyphen(String string, String string2, String string3) {
        this.preBreak = string;
        this.noBreak = string2;
        this.postBreak = string3;
    }

    Hyphen(String string) {
        this.preBreak = string;
        this.noBreak = null;
        this.postBreak = null;
    }

    public String toString() {
        if (this.noBreak == null && this.postBreak == null && this.preBreak != null && this.preBreak.equals("-")) {
            return "-";
        }
        StringBuffer stringBuffer = new StringBuffer("{");
        stringBuffer.append(this.preBreak);
        stringBuffer.append("}{");
        stringBuffer.append(this.postBreak);
        stringBuffer.append("}{");
        stringBuffer.append(this.noBreak);
        stringBuffer.append('}');
        return stringBuffer.toString();
    }
}

