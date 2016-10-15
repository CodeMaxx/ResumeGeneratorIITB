/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

public class Hyphenation {
    private int[] hyphenPoints;
    private String word;
    private int len;

    Hyphenation(String string, int[] arrn) {
        this.word = string;
        this.hyphenPoints = arrn;
        this.len = arrn.length;
    }

    public int length() {
        return this.len;
    }

    public String getPreHyphenText(int n) {
        return this.word.substring(0, this.hyphenPoints[n]);
    }

    public String getPostHyphenText(int n) {
        return this.word.substring(this.hyphenPoints[n]);
    }

    public int[] getHyphenationPoints() {
        return this.hyphenPoints;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        int n = 0;
        for (int i = 0; i < this.len; ++i) {
            stringBuffer.append(this.word.substring(n, this.hyphenPoints[i])).append('-');
            n = this.hyphenPoints[i];
        }
        stringBuffer.append(this.word.substring(n));
        return stringBuffer.toString();
    }
}

