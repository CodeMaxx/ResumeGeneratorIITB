/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.SplitCharacter;
import com.lowagie.text.pdf.PdfChunk;

public class DefaultSplitCharacter
implements SplitCharacter {
    public static final SplitCharacter DEFAULT = new DefaultSplitCharacter();

    public boolean isSplitCharacter(int n, int n2, int n3, char[] arrc, PdfChunk[] arrpdfChunk) {
        char c = this.getCurrentCharacter(n2, arrc, arrpdfChunk);
        if (c <= ' ' || c == '-' || c == '\u2010') {
            return true;
        }
        if (c < '\u2002') {
            return false;
        }
        return c >= '\u2002' && c <= '\u200b' || c >= '\u2e80' && c < '\ud7a0' || c >= '\uf900' && c < '\ufb00' || c >= '\ufe30' && c < '\ufe50' || c >= '\uff61' && c < '\uffa0';
    }

    protected char getCurrentCharacter(int n, char[] arrc, PdfChunk[] arrpdfChunk) {
        if (arrpdfChunk == null) {
            return arrc[n];
        }
        return (char)arrpdfChunk[Math.min(n, arrpdfChunk.length - 1)].getUnicodeEquivalent(arrc[n]);
    }
}

