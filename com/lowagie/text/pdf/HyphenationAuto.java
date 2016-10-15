/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.pdf.hyphenation.Hyphenation;
import com.lowagie.text.pdf.hyphenation.Hyphenator;

public class HyphenationAuto
implements HyphenationEvent {
    protected Hyphenator hyphenator;
    protected String post;

    public HyphenationAuto(String string, String string2, int n, int n2) {
        this.hyphenator = new Hyphenator(string, string2, n, n2);
    }

    public String getHyphenSymbol() {
        return "-";
    }

    public String getHyphenatedWordPre(String string, BaseFont baseFont, float f, float f2) {
        int n;
        this.post = string;
        String string2 = this.getHyphenSymbol();
        float f3 = baseFont.getWidthPoint(string2, f);
        if (f3 > f2) {
            return "";
        }
        Hyphenation hyphenation = this.hyphenator.hyphenate(string);
        if (hyphenation == null) {
            return "";
        }
        int n2 = hyphenation.length();
        for (n = 0; n < n2 && baseFont.getWidthPoint(hyphenation.getPreHyphenText(n), f) + f3 <= f2; ++n) {
        }
        if (--n < 0) {
            return "";
        }
        this.post = hyphenation.getPostHyphenText(n);
        return hyphenation.getPreHyphenText(n) + string2;
    }

    public String getHyphenatedWordPost() {
        return this.post;
    }
}

