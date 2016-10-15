/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.CJKFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TrueTypeFontUnicode;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

class FontDetails {
    PdfIndirectReference indirectReference;
    PdfName fontName;
    BaseFont baseFont;
    TrueTypeFontUnicode ttu;
    CJKFont cjkFont;
    byte[] shortTag;
    HashMap longTag;
    IntHashtable cjkTag;
    int fontType;
    boolean symbolic;
    protected boolean subset = true;

    FontDetails(PdfName pdfName, PdfIndirectReference pdfIndirectReference, BaseFont baseFont) {
        this.fontName = pdfName;
        this.indirectReference = pdfIndirectReference;
        this.baseFont = baseFont;
        this.fontType = baseFont.getFontType();
        switch (this.fontType) {
            case 0: 
            case 1: {
                this.shortTag = new byte[256];
                break;
            }
            case 2: {
                this.cjkTag = new IntHashtable();
                this.cjkFont = (CJKFont)baseFont;
                break;
            }
            case 3: {
                this.longTag = new HashMap();
                this.ttu = (TrueTypeFontUnicode)baseFont;
                this.symbolic = baseFont.isFontSpecific();
            }
        }
    }

    PdfIndirectReference getIndirectReference() {
        return this.indirectReference;
    }

    PdfName getFontName() {
        return this.fontName;
    }

    BaseFont getBaseFont() {
        return this.baseFont;
    }

    byte[] convertToBytes(String string) {
        byte[] arrby = null;
        switch (this.fontType) {
            case 5: {
                return this.baseFont.convertToBytes(string);
            }
            case 0: 
            case 1: {
                arrby = this.baseFont.convertToBytes(string);
                int n = arrby.length;
                for (int i = 0; i < n; ++i) {
                    this.shortTag[arrby[i] & 255] = 1;
                }
                break;
            }
            case 2: {
                int n = string.length();
                for (int i = 0; i < n; ++i) {
                    this.cjkTag.put(this.cjkFont.getCidCode(string.charAt(i)), 0);
                }
                arrby = this.baseFont.convertToBytes(string);
                break;
            }
            case 4: {
                arrby = this.baseFont.convertToBytes(string);
                break;
            }
            case 3: {
                try {
                    int n;
                    int n2 = string.length();
                    int[] arrn = null;
                    char[] arrc = new char[n2];
                    int n3 = 0;
                    if (this.symbolic) {
                        arrby = PdfEncodings.convertToBytes(string, "symboltt");
                        n2 = arrby.length;
                        for (n = 0; n < n2; ++n) {
                            arrn = this.ttu.getMetricsTT(arrby[n] & 255);
                            if (arrn == null) continue;
                            this.longTag.put(new Integer(arrn[0]), new int[]{arrn[0], arrn[1], this.ttu.getUnicodeDifferences(arrby[n] & 255)});
                            arrc[n3++] = (char)arrn[0];
                        }
                    } else {
                        for (n = 0; n < n2; ++n) {
                            int n4;
                            if (Utilities.isSurrogatePair(string, n)) {
                                n4 = Utilities.convertToUtf32(string, n);
                                ++n;
                            } else {
                                n4 = string.charAt(n);
                            }
                            arrn = this.ttu.getMetricsTT(n4);
                            if (arrn == null) continue;
                            int n5 = arrn[0];
                            Integer n6 = new Integer(n5);
                            if (!this.longTag.containsKey(n6)) {
                                this.longTag.put(n6, new int[]{n5, arrn[1], n4});
                            }
                            arrc[n3++] = (char)n5;
                        }
                    }
                    String string2 = new String(arrc, 0, n3);
                    arrby = string2.getBytes("UnicodeBigUnmarked");
                    break;
                }
                catch (UnsupportedEncodingException var3_6) {
                    throw new ExceptionConverter(var3_6);
                }
            }
        }
        return arrby;
    }

    void writeFont(PdfWriter pdfWriter) {
        try {
            switch (this.fontType) {
                case 5: {
                    this.baseFont.writeFont(pdfWriter, this.indirectReference, null);
                    break;
                }
                case 0: 
                case 1: {
                    int n;
                    int n2;
                    for (n2 = 0; n2 < 256 && this.shortTag[n2] == 0; ++n2) {
                    }
                    for (n = 255; n >= n2 && this.shortTag[n] == 0; --n) {
                    }
                    if (n2 > 255) {
                        n2 = 255;
                        n = 255;
                    }
                    this.baseFont.writeFont(pdfWriter, this.indirectReference, new Object[]{new Integer(n2), new Integer(n), this.shortTag, this.subset});
                    break;
                }
                case 2: {
                    this.baseFont.writeFont(pdfWriter, this.indirectReference, new Object[]{this.cjkTag});
                    break;
                }
                case 3: {
                    this.baseFont.writeFont(pdfWriter, this.indirectReference, new Object[]{this.longTag, this.subset});
                }
            }
        }
        catch (Exception var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    public boolean isSubset() {
        return this.subset;
    }

    public void setSubset(boolean bl) {
        this.subset = bl;
    }
}

