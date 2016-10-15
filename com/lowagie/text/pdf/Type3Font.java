/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.Type3Glyph;
import java.io.IOException;
import java.util.HashMap;

public class Type3Font
extends BaseFont {
    private boolean[] usedSlot;
    private IntHashtable widths3 = new IntHashtable();
    private HashMap char2glyph = new HashMap();
    private PdfWriter writer;
    private float llx = Float.NaN;
    private float lly;
    private float urx;
    private float ury;
    private PageResources pageResources = new PageResources();
    private boolean colorized;

    public Type3Font(PdfWriter pdfWriter, char[] arrc, boolean bl) {
        this(pdfWriter, bl);
    }

    public Type3Font(PdfWriter pdfWriter, boolean bl) {
        this.writer = pdfWriter;
        this.colorized = bl;
        this.fontType = 5;
        this.usedSlot = new boolean[256];
    }

    public PdfContentByte defineGlyph(char c, float f, float f2, float f3, float f4, float f5) {
        if (c == '\u0000' || c > '\u00ff') {
            throw new IllegalArgumentException("The char " + c + " doesn't belong in this Type3 font");
        }
        this.usedSlot[c] = true;
        Integer n = new Integer(c);
        Type3Glyph type3Glyph = (Type3Glyph)this.char2glyph.get(n);
        if (type3Glyph != null) {
            return type3Glyph;
        }
        this.widths3.put(c, (int)f);
        if (!this.colorized) {
            if (Float.isNaN(this.llx)) {
                this.llx = f2;
                this.lly = f3;
                this.urx = f4;
                this.ury = f5;
            } else {
                this.llx = Math.min(this.llx, f2);
                this.lly = Math.min(this.lly, f3);
                this.urx = Math.max(this.urx, f4);
                this.ury = Math.max(this.ury, f5);
            }
        }
        type3Glyph = new Type3Glyph(this.writer, this.pageResources, f, f2, f3, f4, f5, this.colorized);
        this.char2glyph.put(n, type3Glyph);
        return type3Glyph;
    }

    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }

    public float getFontDescriptor(int n, float f) {
        return 0.0f;
    }

    public String[][] getFullFontName() {
        return new String[][]{{"", "", "", ""}};
    }

    public String[][] getAllNameEntries() {
        return new String[][]{{"4", "", "", "", ""}};
    }

    public int getKerning(int n, int n2) {
        return 0;
    }

    public String getPostscriptFontName() {
        return "";
    }

    protected int[] getRawCharBBox(int n, String string) {
        return null;
    }

    int getRawWidth(int n, String string) {
        return 0;
    }

    public boolean hasKernPairs() {
        return false;
    }

    public boolean setKerning(int n, int n2, int n3) {
        return false;
    }

    public void setPostscriptFontName(String string) {
    }

    void writeFont(PdfWriter pdfWriter, PdfIndirectReference pdfIndirectReference, Object[] arrobject) throws DocumentException, IOException {
        int n;
        int n2;
        if (this.writer != pdfWriter) {
            throw new IllegalArgumentException("Type3 font used with the wrong PdfWriter");
        }
        for (n = 0; n < this.usedSlot.length && !this.usedSlot[n]; ++n) {
        }
        if (n == this.usedSlot.length) {
            throw new DocumentException("No glyphs defined for Type3 font");
        }
        for (n2 = this.usedSlot.length - 1; n2 >= n && !this.usedSlot[n2]; --n2) {
        }
        int[] arrn = new int[n2 - n + 1];
        int[] arrn2 = new int[n2 - n + 1];
        int n3 = 0;
        int n4 = 0;
        int n5 = n;
        while (n5 <= n2) {
            if (this.usedSlot[n5]) {
                arrn2[n3++] = n5;
                arrn[n4] = this.widths3.get(n5);
            }
            ++n5;
            ++n4;
        }
        PdfArray pdfArray = new PdfArray();
        PdfDictionary pdfDictionary = new PdfDictionary();
        int n6 = -1;
        for (int i = 0; i < n3; ++i) {
            int n7 = arrn2[i];
            if (n7 > n6) {
                n6 = n7;
                pdfArray.add(new PdfNumber(n6));
            }
            ++n6;
            int n8 = arrn2[i];
            String string = GlyphList.unicodeToName(n8);
            if (string == null) {
                string = "a" + n8;
            }
            PdfName pdfName = new PdfName(string);
            pdfArray.add(pdfName);
            Type3Glyph type3Glyph = (Type3Glyph)this.char2glyph.get(new Integer(n8));
            PdfStream pdfStream = new PdfStream(type3Glyph.toPdf(null));
            pdfStream.flateCompress(this.compressionLevel);
            PdfIndirectReference pdfIndirectReference2 = pdfWriter.addToBody(pdfStream).getIndirectReference();
            pdfDictionary.put(pdfName, pdfIndirectReference2);
        }
        PdfDictionary pdfDictionary2 = new PdfDictionary(PdfName.FONT);
        pdfDictionary2.put(PdfName.SUBTYPE, PdfName.TYPE3);
        if (this.colorized) {
            pdfDictionary2.put(PdfName.FONTBBOX, new PdfRectangle(0.0f, 0.0f, 0.0f, 0.0f));
        } else {
            pdfDictionary2.put(PdfName.FONTBBOX, new PdfRectangle(this.llx, this.lly, this.urx, this.ury));
        }
        pdfDictionary2.put(PdfName.FONTMATRIX, new PdfArray(new float[]{0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f}));
        pdfDictionary2.put(PdfName.CHARPROCS, pdfWriter.addToBody(pdfDictionary).getIndirectReference());
        PdfDictionary pdfDictionary3 = new PdfDictionary();
        pdfDictionary3.put(PdfName.DIFFERENCES, pdfArray);
        pdfDictionary2.put(PdfName.ENCODING, pdfWriter.addToBody(pdfDictionary3).getIndirectReference());
        pdfDictionary2.put(PdfName.FIRSTCHAR, new PdfNumber(n));
        pdfDictionary2.put(PdfName.LASTCHAR, new PdfNumber(n2));
        pdfDictionary2.put(PdfName.WIDTHS, pdfWriter.addToBody(new PdfArray(arrn)).getIndirectReference());
        if (this.pageResources.hasResources()) {
            pdfDictionary2.put(PdfName.RESOURCES, pdfWriter.addToBody(this.pageResources.getResources()).getIndirectReference());
        }
        pdfWriter.addToBody((PdfObject)pdfDictionary2, pdfIndirectReference);
    }

    public PdfStream getFullFontStream() {
        return null;
    }

    byte[] convertToBytes(String string) {
        char[] arrc = string.toCharArray();
        byte[] arrby = new byte[arrc.length];
        int n = 0;
        for (int i = 0; i < arrc.length; ++i) {
            char c = arrc[i];
            if (!this.charExists(c)) continue;
            arrby[n++] = (byte)c;
        }
        if (arrby.length == n) {
            return arrby;
        }
        byte[] arrby2 = new byte[n];
        System.arraycopy(arrby, 0, arrby2, 0, n);
        return arrby2;
    }

    byte[] convertToBytes(int n) {
        if (this.charExists(n)) {
            return new byte[]{(byte)n};
        }
        return new byte[0];
    }

    public int getWidth(int n) {
        if (!this.widths3.containsKey(n)) {
            throw new IllegalArgumentException("The char " + n + " is not defined in a Type3 font");
        }
        return this.widths3.get(n);
    }

    public int getWidth(String string) {
        char[] arrc = string.toCharArray();
        int n = 0;
        for (int i = 0; i < arrc.length; ++i) {
            n += this.getWidth(arrc[i]);
        }
        return n;
    }

    public int[] getCharBBox(int n) {
        return null;
    }

    public boolean charExists(int n) {
        if (n > 0 && n < 256) {
            return this.usedSlot[n];
        }
        return false;
    }

    public boolean setCharAdvance(int n, int n2) {
        return false;
    }
}

