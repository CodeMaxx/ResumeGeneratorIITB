/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BaseFont;

public interface HyphenationEvent {
    public String getHyphenSymbol();

    public String getHyphenatedWordPre(String var1, BaseFont var2, float var3, float var4);

    public String getHyphenatedWordPost();
}

