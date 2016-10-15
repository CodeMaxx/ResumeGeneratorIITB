/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.ArabicLigaturizer;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.BidiOrder;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfFont;
import com.lowagie.text.pdf.PdfLine;
import java.util.ArrayList;
import java.util.Collection;

public class BidiLine {
    protected int runDirection;
    protected int pieceSize = 256;
    protected char[] text = new char[this.pieceSize];
    protected PdfChunk[] detailChunks = new PdfChunk[this.pieceSize];
    protected int totalTextLength = 0;
    protected byte[] orderLevels = new byte[this.pieceSize];
    protected int[] indexChars = new int[this.pieceSize];
    protected ArrayList chunks = new ArrayList();
    protected int indexChunk = 0;
    protected int indexChunkChar = 0;
    protected int currentChar = 0;
    protected int storedRunDirection;
    protected char[] storedText = new char[0];
    protected PdfChunk[] storedDetailChunks = new PdfChunk[0];
    protected int storedTotalTextLength = 0;
    protected byte[] storedOrderLevels = new byte[0];
    protected int[] storedIndexChars = new int[0];
    protected int storedIndexChunk = 0;
    protected int storedIndexChunkChar = 0;
    protected int storedCurrentChar = 0;
    protected boolean shortStore;
    protected static final IntHashtable mirrorChars = new IntHashtable();
    protected int arabicOptions;

    public BidiLine() {
    }

    public BidiLine(BidiLine bidiLine) {
        this.runDirection = bidiLine.runDirection;
        this.pieceSize = bidiLine.pieceSize;
        this.text = (char[])bidiLine.text.clone();
        this.detailChunks = (PdfChunk[])bidiLine.detailChunks.clone();
        this.totalTextLength = bidiLine.totalTextLength;
        this.orderLevels = (byte[])bidiLine.orderLevels.clone();
        this.indexChars = (int[])bidiLine.indexChars.clone();
        this.chunks = new ArrayList(bidiLine.chunks);
        this.indexChunk = bidiLine.indexChunk;
        this.indexChunkChar = bidiLine.indexChunkChar;
        this.currentChar = bidiLine.currentChar;
        this.storedRunDirection = bidiLine.storedRunDirection;
        this.storedText = (char[])bidiLine.storedText.clone();
        this.storedDetailChunks = (PdfChunk[])bidiLine.storedDetailChunks.clone();
        this.storedTotalTextLength = bidiLine.storedTotalTextLength;
        this.storedOrderLevels = (byte[])bidiLine.storedOrderLevels.clone();
        this.storedIndexChars = (int[])bidiLine.storedIndexChars.clone();
        this.storedIndexChunk = bidiLine.storedIndexChunk;
        this.storedIndexChunkChar = bidiLine.storedIndexChunkChar;
        this.storedCurrentChar = bidiLine.storedCurrentChar;
        this.shortStore = bidiLine.shortStore;
        this.arabicOptions = bidiLine.arabicOptions;
    }

    public boolean isEmpty() {
        return this.currentChar >= this.totalTextLength && this.indexChunk >= this.chunks.size();
    }

    public void clearChunks() {
        this.chunks.clear();
        this.totalTextLength = 0;
        this.currentChar = 0;
    }

    public boolean getParagraph(int n) {
        byte[] arrby;
        int n2;
        Object object;
        this.runDirection = n;
        this.currentChar = 0;
        this.totalTextLength = 0;
        boolean bl = false;
        while (this.indexChunk < this.chunks.size()) {
            object = (PdfChunk)this.chunks.get(this.indexChunk);
            BaseFont baseFont = object.font().getFont();
            arrby = object.toString();
            n2 = arrby.length();
            while (this.indexChunkChar < n2) {
                char c = arrby.charAt(this.indexChunkChar);
                char c2 = (char)baseFont.getUnicodeEquivalent(c);
                if (c2 == '\r' || c2 == '\n') {
                    if (c2 == '\r' && this.indexChunkChar + 1 < n2 && arrby.charAt(this.indexChunkChar + 1) == '\n') {
                        ++this.indexChunkChar;
                    }
                    ++this.indexChunkChar;
                    if (this.indexChunkChar >= n2) {
                        this.indexChunkChar = 0;
                        ++this.indexChunk;
                    }
                    bl = true;
                    if (this.totalTextLength != 0) break;
                    this.detailChunks[0] = object;
                    break;
                }
                this.addPiece(c, (PdfChunk)object);
                ++this.indexChunkChar;
            }
            if (bl) break;
            this.indexChunkChar = 0;
            ++this.indexChunk;
        }
        if (this.totalTextLength == 0) {
            return bl;
        }
        this.totalTextLength = this.trimRight(0, this.totalTextLength - 1) + 1;
        if (this.totalTextLength == 0) {
            return true;
        }
        if (n == 2 || n == 3) {
            if (this.orderLevels.length < this.totalTextLength) {
                this.orderLevels = new byte[this.pieceSize];
                this.indexChars = new int[this.pieceSize];
            }
            ArabicLigaturizer.processNumbers(this.text, 0, this.totalTextLength, this.arabicOptions);
            object = new BidiOrder(this.text, 0, this.totalTextLength, n == 3 ? 1 : 0);
            arrby = object.getLevels();
            n2 = 0;
            while (n2 < this.totalTextLength) {
                this.orderLevels[n2] = arrby[n2];
                this.indexChars[n2] = n2++;
            }
            this.doArabicShapping();
            this.mirrorGlyphs();
        }
        this.totalTextLength = this.trimRightEx(0, this.totalTextLength - 1) + 1;
        return true;
    }

    public void addChunk(PdfChunk pdfChunk) {
        this.chunks.add(pdfChunk);
    }

    public void addChunks(ArrayList arrayList) {
        this.chunks.addAll(arrayList);
    }

    public void addPiece(char c, PdfChunk pdfChunk) {
        if (this.totalTextLength >= this.pieceSize) {
            char[] arrc = this.text;
            PdfChunk[] arrpdfChunk = this.detailChunks;
            this.pieceSize *= 2;
            this.text = new char[this.pieceSize];
            this.detailChunks = new PdfChunk[this.pieceSize];
            System.arraycopy(arrc, 0, this.text, 0, this.totalTextLength);
            System.arraycopy(arrpdfChunk, 0, this.detailChunks, 0, this.totalTextLength);
        }
        this.text[this.totalTextLength] = c;
        this.detailChunks[this.totalTextLength++] = pdfChunk;
    }

    public void save() {
        if (this.indexChunk > 0) {
            if (this.indexChunk >= this.chunks.size()) {
                this.chunks.clear();
            } else {
                --this.indexChunk;
                while (this.indexChunk >= 0) {
                    this.chunks.remove(this.indexChunk);
                    --this.indexChunk;
                }
            }
            this.indexChunk = 0;
        }
        this.storedRunDirection = this.runDirection;
        this.storedTotalTextLength = this.totalTextLength;
        this.storedIndexChunk = this.indexChunk;
        this.storedIndexChunkChar = this.indexChunkChar;
        this.storedCurrentChar = this.currentChar;
        boolean bl = this.shortStore = this.currentChar < this.totalTextLength;
        if (!this.shortStore) {
            if (this.storedText.length < this.totalTextLength) {
                this.storedText = new char[this.totalTextLength];
                this.storedDetailChunks = new PdfChunk[this.totalTextLength];
            }
            System.arraycopy(this.text, 0, this.storedText, 0, this.totalTextLength);
            System.arraycopy(this.detailChunks, 0, this.storedDetailChunks, 0, this.totalTextLength);
        }
        if (this.runDirection == 2 || this.runDirection == 3) {
            if (this.storedOrderLevels.length < this.totalTextLength) {
                this.storedOrderLevels = new byte[this.totalTextLength];
                this.storedIndexChars = new int[this.totalTextLength];
            }
            System.arraycopy(this.orderLevels, this.currentChar, this.storedOrderLevels, this.currentChar, this.totalTextLength - this.currentChar);
            System.arraycopy(this.indexChars, this.currentChar, this.storedIndexChars, this.currentChar, this.totalTextLength - this.currentChar);
        }
    }

    public void restore() {
        this.runDirection = this.storedRunDirection;
        this.totalTextLength = this.storedTotalTextLength;
        this.indexChunk = this.storedIndexChunk;
        this.indexChunkChar = this.storedIndexChunkChar;
        this.currentChar = this.storedCurrentChar;
        if (!this.shortStore) {
            System.arraycopy(this.storedText, 0, this.text, 0, this.totalTextLength);
            System.arraycopy(this.storedDetailChunks, 0, this.detailChunks, 0, this.totalTextLength);
        }
        if (this.runDirection == 2 || this.runDirection == 3) {
            System.arraycopy(this.storedOrderLevels, this.currentChar, this.orderLevels, this.currentChar, this.totalTextLength - this.currentChar);
            System.arraycopy(this.storedIndexChars, this.currentChar, this.indexChars, this.currentChar, this.totalTextLength - this.currentChar);
        }
    }

    public void mirrorGlyphs() {
        for (int i = 0; i < this.totalTextLength; ++i) {
            int n;
            if ((this.orderLevels[i] & 1) != 1 || (n = mirrorChars.get(this.text[i])) == 0) continue;
            this.text[i] = (char)n;
        }
    }

    public void doArabicShapping() {
        int n = 0;
        int n2 = 0;
        block0 : do {
            int n3;
            int n4;
            if (n < this.totalTextLength && ((n3 = this.text[n]) < 1536 || n3 > 1791)) {
                if (n != n2) {
                    this.text[n2] = this.text[n];
                    this.detailChunks[n2] = this.detailChunks[n];
                    this.orderLevels[n2] = this.orderLevels[n];
                }
                ++n;
                ++n2;
                continue;
            }
            if (n >= this.totalTextLength) {
                this.totalTextLength = n2;
                return;
            }
            n3 = n++;
            while (n < this.totalTextLength && (n4 = this.text[n]) >= 1536 && n4 <= 1791) {
                ++n;
            }
            n4 = n - n3;
            int n5 = ArabicLigaturizer.arabic_shape(this.text, n3, n4, this.text, n2, n4, this.arabicOptions);
            if (n3 != n2) {
                int n6 = 0;
                do {
                    if (n6 >= n5) continue block0;
                    this.detailChunks[n2] = this.detailChunks[n3];
                    this.orderLevels[n2++] = this.orderLevels[n3++];
                    ++n6;
                } while (true);
            }
            n2 += n5;
        } while (true);
    }

    public PdfLine processLine(float f, float f2, int n, int n2, int n3) {
        HyphenationEvent hyphenationEvent;
        boolean bl;
        int[] arrn;
        this.arabicOptions = n3;
        this.save();
        boolean bl2 = bl = n2 == 3;
        if (this.currentChar >= this.totalTextLength) {
            boolean bl3 = this.getParagraph(n2);
            if (!bl3) {
                return null;
            }
            if (this.totalTextLength == 0) {
                ArrayList<PdfChunk> arrayList = new ArrayList<PdfChunk>();
                PdfChunk pdfChunk = new PdfChunk("", this.detailChunks[0]);
                arrayList.add(pdfChunk);
                return new PdfLine(0.0f, 0.0f, 0.0f, n, true, arrayList, bl);
            }
        }
        float f3 = f2;
        int n4 = -1;
        if (this.currentChar != 0) {
            this.currentChar = this.trimLeftEx(this.currentChar, this.totalTextLength - 1);
        }
        int n5 = this.currentChar;
        int n6 = 0;
        PdfChunk pdfChunk = null;
        float f4 = 0.0f;
        PdfChunk pdfChunk2 = null;
        boolean bl4 = false;
        boolean bl5 = false;
        while (this.currentChar < this.totalTextLength) {
            pdfChunk = this.detailChunks[this.currentChar];
            bl5 = Utilities.isSurrogatePair(this.text, this.currentChar);
            n6 = bl5 ? pdfChunk.getUnicodeEquivalent(Utilities.convertToUtf32(this.text, this.currentChar)) : pdfChunk.getUnicodeEquivalent(this.text[this.currentChar]);
            if (!PdfChunk.noPrint(n6)) {
                f4 = bl5 ? pdfChunk.getCharWidth(n6) : pdfChunk.getCharWidth(this.text[this.currentChar]);
                bl4 = pdfChunk.isExtSplitCharacter(n5, this.currentChar, this.totalTextLength, this.text, this.detailChunks);
                if (bl4 && Character.isWhitespace((char)n6)) {
                    n4 = this.currentChar;
                }
                if (f2 - f4 < 0.0f) break;
                if (bl4) {
                    n4 = this.currentChar;
                }
                f2 -= f4;
                pdfChunk2 = pdfChunk;
                if (pdfChunk.isTab()) {
                    Object[] arrobject = (Object[])pdfChunk.getAttribute("TAB");
                    float f5 = ((Float)arrobject[1]).floatValue();
                    boolean bl6 = (Boolean)arrobject[2];
                    if (bl6 && f5 < f3 - f2) {
                        return new PdfLine(0.0f, f3, f2, n, true, this.createArrayOfPdfChunks(n5, this.currentChar - 1), bl);
                    }
                    this.detailChunks[this.currentChar].adjustLeft(f);
                    f2 = f3 - f5;
                }
                if (bl5) {
                    ++this.currentChar;
                }
            }
            ++this.currentChar;
        }
        if (pdfChunk2 == null) {
            ++this.currentChar;
            if (bl5) {
                ++this.currentChar;
            }
            return new PdfLine(0.0f, f3, 0.0f, n, false, this.createArrayOfPdfChunks(this.currentChar - 1, this.currentChar - 1), bl);
        }
        if (this.currentChar >= this.totalTextLength) {
            return new PdfLine(0.0f, f3, f2, n, true, this.createArrayOfPdfChunks(n5, this.totalTextLength - 1), bl);
        }
        int n7 = this.trimRightEx(n5, this.currentChar - 1);
        if (n7 < n5) {
            return new PdfLine(0.0f, f3, f2, n, false, this.createArrayOfPdfChunks(n5, this.currentChar - 1), bl);
        }
        if (n7 == this.currentChar - 1 && (hyphenationEvent = (HyphenationEvent)pdfChunk2.getAttribute("HYPHENATION")) != null && (arrn = this.getWord(n5, n7)) != null) {
            float f6 = f2 + this.getWidth(arrn[0], this.currentChar - 1);
            String string = hyphenationEvent.getHyphenatedWordPre(new String(this.text, arrn[0], arrn[1] - arrn[0]), pdfChunk2.font().getFont(), pdfChunk2.font().size(), f6);
            String string2 = hyphenationEvent.getHyphenatedWordPost();
            if (string.length() > 0) {
                PdfChunk pdfChunk3 = new PdfChunk(string, pdfChunk2);
                this.currentChar = arrn[1] - string2.length();
                return new PdfLine(0.0f, f3, f6 - pdfChunk2.font().width(string), n, false, this.createArrayOfPdfChunks(n5, arrn[0] - 1, pdfChunk3), bl);
            }
        }
        if (n4 == -1 || n4 >= n7) {
            return new PdfLine(0.0f, f3, f2 + this.getWidth(n7 + 1, this.currentChar - 1), n, false, this.createArrayOfPdfChunks(n5, n7), bl);
        }
        this.currentChar = n4 + 1;
        n7 = this.trimRightEx(n5, n4);
        if (n7 < n5) {
            n7 = this.currentChar - 1;
        }
        return new PdfLine(0.0f, f3, f3 - this.getWidth(n5, n7), n, false, this.createArrayOfPdfChunks(n5, n7), bl);
    }

    public float getWidth(int n, int n2) {
        char c = '\u0000';
        PdfChunk pdfChunk = null;
        float f = 0.0f;
        while (n <= n2) {
            boolean bl = Utilities.isSurrogatePair(this.text, n);
            if (bl) {
                f += this.detailChunks[n].getCharWidth(Utilities.convertToUtf32(this.text, n));
                ++n;
            } else {
                pdfChunk = this.detailChunks[n];
                c = this.text[n];
                if (!PdfChunk.noPrint(pdfChunk.getUnicodeEquivalent(c))) {
                    f += this.detailChunks[n].getCharWidth(c);
                }
            }
            ++n;
        }
        return f;
    }

    public ArrayList createArrayOfPdfChunks(int n, int n2) {
        return this.createArrayOfPdfChunks(n, n2, null);
    }

    public ArrayList createArrayOfPdfChunks(int n, int n2, PdfChunk pdfChunk) {
        boolean bl;
        boolean bl2 = bl = this.runDirection == 2 || this.runDirection == 3;
        if (bl) {
            this.reorder(n, n2);
        }
        ArrayList<PdfChunk> arrayList = new ArrayList<PdfChunk>();
        PdfChunk pdfChunk2 = this.detailChunks[n];
        PdfChunk pdfChunk3 = null;
        StringBuffer stringBuffer = new StringBuffer();
        int n3 = 0;
        while (n <= n2) {
            char c;
            n3 = bl ? this.indexChars[n] : n;
            pdfChunk3 = this.detailChunks[n3];
            if (!PdfChunk.noPrint(pdfChunk3.getUnicodeEquivalent(c = this.text[n3]))) {
                if (pdfChunk3.isImage() || pdfChunk3.isSeparator() || pdfChunk3.isTab()) {
                    if (stringBuffer.length() > 0) {
                        arrayList.add(new PdfChunk(stringBuffer.toString(), pdfChunk2));
                        stringBuffer = new StringBuffer();
                    }
                    arrayList.add(pdfChunk3);
                } else if (pdfChunk3 == pdfChunk2) {
                    stringBuffer.append(c);
                } else {
                    if (stringBuffer.length() > 0) {
                        arrayList.add(new PdfChunk(stringBuffer.toString(), pdfChunk2));
                        stringBuffer = new StringBuffer();
                    }
                    if (!(pdfChunk3.isImage() || pdfChunk3.isSeparator() || pdfChunk3.isTab())) {
                        stringBuffer.append(c);
                    }
                    pdfChunk2 = pdfChunk3;
                }
            }
            ++n;
        }
        if (stringBuffer.length() > 0) {
            arrayList.add(new PdfChunk(stringBuffer.toString(), pdfChunk2));
        }
        if (pdfChunk != null) {
            arrayList.add(pdfChunk);
        }
        return arrayList;
    }

    public int[] getWord(int n, int n2) {
        int n3;
        int n4 = n2;
        for (n3 = n2; n3 < this.totalTextLength && Character.isLetter(this.text[n3]); ++n3) {
        }
        if (n3 == n2) {
            return null;
        }
        while (n4 >= n && Character.isLetter(this.text[n4])) {
            --n4;
        }
        return new int[]{++n4, n3};
    }

    public int trimRight(int n, int n2) {
        char c;
        int n3;
        for (n3 = n2; n3 >= n && BidiLine.isWS(c = (char)this.detailChunks[n3].getUnicodeEquivalent(this.text[n3])); --n3) {
        }
        return n3;
    }

    public int trimLeft(int n, int n2) {
        char c;
        int n3;
        for (n3 = n; n3 <= n2 && BidiLine.isWS(c = (char)this.detailChunks[n3].getUnicodeEquivalent(this.text[n3])); ++n3) {
        }
        return n3;
    }

    public int trimRightEx(int n, int n2) {
        int n3;
        char c = '\u0000';
        for (n3 = n2; n3 >= n && (BidiLine.isWS(c = (char)this.detailChunks[n3].getUnicodeEquivalent(this.text[n3])) || PdfChunk.noPrint(c)); --n3) {
        }
        return n3;
    }

    public int trimLeftEx(int n, int n2) {
        int n3;
        char c = '\u0000';
        for (n3 = n; n3 <= n2 && (BidiLine.isWS(c = (char)this.detailChunks[n3].getUnicodeEquivalent(this.text[n3])) || PdfChunk.noPrint(c)); ++n3) {
        }
        return n3;
    }

    public void reorder(int n, int n2) {
        int n3;
        int n4;
        int n5;
        int n6 = n4 = this.orderLevels[n];
        int n7 = n4;
        int n8 = n4;
        for (n3 = n + 1; n3 <= n2; ++n3) {
            n5 = this.orderLevels[n3];
            if (n5 > n4) {
                n4 = n5;
            } else if (n5 < n6) {
                n6 = n5;
            }
            n7 = (byte)(n7 & n5);
            n8 = (byte)(n8 | n5);
        }
        if ((n8 & 1) == 0) {
            return;
        }
        if ((n7 & 1) == 1) {
            this.flip(n, n2 + 1);
            return;
        }
        n6 = (byte)(n6 | 1);
        while (n4 >= n6) {
            n3 = n;
            do {
                if (n3 <= n2 && this.orderLevels[n3] < n4) {
                    ++n3;
                    continue;
                }
                if (n3 > n2) break;
                for (n5 = n3 + 1; n5 <= n2 && this.orderLevels[n5] >= n4; ++n5) {
                }
                this.flip(n3, n5);
                n3 = n5 + 1;
            } while (true);
            n4 = (byte)(n4 - 1);
        }
    }

    public void flip(int n, int n2) {
        int n3 = (n + n2) / 2;
        --n2;
        while (n < n3) {
            int n4 = this.indexChars[n];
            this.indexChars[n] = this.indexChars[n2];
            this.indexChars[n2] = n4;
            ++n;
            --n2;
        }
    }

    public static boolean isWS(char c) {
        return c <= ' ';
    }

    static {
        mirrorChars.put(40, 41);
        mirrorChars.put(41, 40);
        mirrorChars.put(60, 62);
        mirrorChars.put(62, 60);
        mirrorChars.put(91, 93);
        mirrorChars.put(93, 91);
        mirrorChars.put(123, 125);
        mirrorChars.put(125, 123);
        mirrorChars.put(171, 187);
        mirrorChars.put(187, 171);
        mirrorChars.put(8249, 8250);
        mirrorChars.put(8250, 8249);
        mirrorChars.put(8261, 8262);
        mirrorChars.put(8262, 8261);
        mirrorChars.put(8317, 8318);
        mirrorChars.put(8318, 8317);
        mirrorChars.put(8333, 8334);
        mirrorChars.put(8334, 8333);
        mirrorChars.put(8712, 8715);
        mirrorChars.put(8713, 8716);
        mirrorChars.put(8714, 8717);
        mirrorChars.put(8715, 8712);
        mirrorChars.put(8716, 8713);
        mirrorChars.put(8717, 8714);
        mirrorChars.put(8725, 10741);
        mirrorChars.put(8764, 8765);
        mirrorChars.put(8765, 8764);
        mirrorChars.put(8771, 8909);
        mirrorChars.put(8786, 8787);
        mirrorChars.put(8787, 8786);
        mirrorChars.put(8788, 8789);
        mirrorChars.put(8789, 8788);
        mirrorChars.put(8804, 8805);
        mirrorChars.put(8805, 8804);
        mirrorChars.put(8806, 8807);
        mirrorChars.put(8807, 8806);
        mirrorChars.put(8808, 8809);
        mirrorChars.put(8809, 8808);
        mirrorChars.put(8810, 8811);
        mirrorChars.put(8811, 8810);
        mirrorChars.put(8814, 8815);
        mirrorChars.put(8815, 8814);
        mirrorChars.put(8816, 8817);
        mirrorChars.put(8817, 8816);
        mirrorChars.put(8818, 8819);
        mirrorChars.put(8819, 8818);
        mirrorChars.put(8820, 8821);
        mirrorChars.put(8821, 8820);
        mirrorChars.put(8822, 8823);
        mirrorChars.put(8823, 8822);
        mirrorChars.put(8824, 8825);
        mirrorChars.put(8825, 8824);
        mirrorChars.put(8826, 8827);
        mirrorChars.put(8827, 8826);
        mirrorChars.put(8828, 8829);
        mirrorChars.put(8829, 8828);
        mirrorChars.put(8830, 8831);
        mirrorChars.put(8831, 8830);
        mirrorChars.put(8832, 8833);
        mirrorChars.put(8833, 8832);
        mirrorChars.put(8834, 8835);
        mirrorChars.put(8835, 8834);
        mirrorChars.put(8836, 8837);
        mirrorChars.put(8837, 8836);
        mirrorChars.put(8838, 8839);
        mirrorChars.put(8839, 8838);
        mirrorChars.put(8840, 8841);
        mirrorChars.put(8841, 8840);
        mirrorChars.put(8842, 8843);
        mirrorChars.put(8843, 8842);
        mirrorChars.put(8847, 8848);
        mirrorChars.put(8848, 8847);
        mirrorChars.put(8849, 8850);
        mirrorChars.put(8850, 8849);
        mirrorChars.put(8856, 10680);
        mirrorChars.put(8866, 8867);
        mirrorChars.put(8867, 8866);
        mirrorChars.put(8870, 10974);
        mirrorChars.put(8872, 10980);
        mirrorChars.put(8873, 10979);
        mirrorChars.put(8875, 10981);
        mirrorChars.put(8880, 8881);
        mirrorChars.put(8881, 8880);
        mirrorChars.put(8882, 8883);
        mirrorChars.put(8883, 8882);
        mirrorChars.put(8884, 8885);
        mirrorChars.put(8885, 8884);
        mirrorChars.put(8886, 8887);
        mirrorChars.put(8887, 8886);
        mirrorChars.put(8905, 8906);
        mirrorChars.put(8906, 8905);
        mirrorChars.put(8907, 8908);
        mirrorChars.put(8908, 8907);
        mirrorChars.put(8909, 8771);
        mirrorChars.put(8912, 8913);
        mirrorChars.put(8913, 8912);
        mirrorChars.put(8918, 8919);
        mirrorChars.put(8919, 8918);
        mirrorChars.put(8920, 8921);
        mirrorChars.put(8921, 8920);
        mirrorChars.put(8922, 8923);
        mirrorChars.put(8923, 8922);
        mirrorChars.put(8924, 8925);
        mirrorChars.put(8925, 8924);
        mirrorChars.put(8926, 8927);
        mirrorChars.put(8927, 8926);
        mirrorChars.put(8928, 8929);
        mirrorChars.put(8929, 8928);
        mirrorChars.put(8930, 8931);
        mirrorChars.put(8931, 8930);
        mirrorChars.put(8932, 8933);
        mirrorChars.put(8933, 8932);
        mirrorChars.put(8934, 8935);
        mirrorChars.put(8935, 8934);
        mirrorChars.put(8936, 8937);
        mirrorChars.put(8937, 8936);
        mirrorChars.put(8938, 8939);
        mirrorChars.put(8939, 8938);
        mirrorChars.put(8940, 8941);
        mirrorChars.put(8941, 8940);
        mirrorChars.put(8944, 8945);
        mirrorChars.put(8945, 8944);
        mirrorChars.put(8946, 8954);
        mirrorChars.put(8947, 8955);
        mirrorChars.put(8948, 8956);
        mirrorChars.put(8950, 8957);
        mirrorChars.put(8951, 8958);
        mirrorChars.put(8954, 8946);
        mirrorChars.put(8955, 8947);
        mirrorChars.put(8956, 8948);
        mirrorChars.put(8957, 8950);
        mirrorChars.put(8958, 8951);
        mirrorChars.put(8968, 8969);
        mirrorChars.put(8969, 8968);
        mirrorChars.put(8970, 8971);
        mirrorChars.put(8971, 8970);
        mirrorChars.put(9001, 9002);
        mirrorChars.put(9002, 9001);
        mirrorChars.put(10088, 10089);
        mirrorChars.put(10089, 10088);
        mirrorChars.put(10090, 10091);
        mirrorChars.put(10091, 10090);
        mirrorChars.put(10092, 10093);
        mirrorChars.put(10093, 10092);
        mirrorChars.put(10094, 10095);
        mirrorChars.put(10095, 10094);
        mirrorChars.put(10096, 10097);
        mirrorChars.put(10097, 10096);
        mirrorChars.put(10098, 10099);
        mirrorChars.put(10099, 10098);
        mirrorChars.put(10100, 10101);
        mirrorChars.put(10101, 10100);
        mirrorChars.put(10197, 10198);
        mirrorChars.put(10198, 10197);
        mirrorChars.put(10205, 10206);
        mirrorChars.put(10206, 10205);
        mirrorChars.put(10210, 10211);
        mirrorChars.put(10211, 10210);
        mirrorChars.put(10212, 10213);
        mirrorChars.put(10213, 10212);
        mirrorChars.put(10214, 10215);
        mirrorChars.put(10215, 10214);
        mirrorChars.put(10216, 10217);
        mirrorChars.put(10217, 10216);
        mirrorChars.put(10218, 10219);
        mirrorChars.put(10219, 10218);
        mirrorChars.put(10627, 10628);
        mirrorChars.put(10628, 10627);
        mirrorChars.put(10629, 10630);
        mirrorChars.put(10630, 10629);
        mirrorChars.put(10631, 10632);
        mirrorChars.put(10632, 10631);
        mirrorChars.put(10633, 10634);
        mirrorChars.put(10634, 10633);
        mirrorChars.put(10635, 10636);
        mirrorChars.put(10636, 10635);
        mirrorChars.put(10637, 10640);
        mirrorChars.put(10638, 10639);
        mirrorChars.put(10639, 10638);
        mirrorChars.put(10640, 10637);
        mirrorChars.put(10641, 10642);
        mirrorChars.put(10642, 10641);
        mirrorChars.put(10643, 10644);
        mirrorChars.put(10644, 10643);
        mirrorChars.put(10645, 10646);
        mirrorChars.put(10646, 10645);
        mirrorChars.put(10647, 10648);
        mirrorChars.put(10648, 10647);
        mirrorChars.put(10680, 8856);
        mirrorChars.put(10688, 10689);
        mirrorChars.put(10689, 10688);
        mirrorChars.put(10692, 10693);
        mirrorChars.put(10693, 10692);
        mirrorChars.put(10703, 10704);
        mirrorChars.put(10704, 10703);
        mirrorChars.put(10705, 10706);
        mirrorChars.put(10706, 10705);
        mirrorChars.put(10708, 10709);
        mirrorChars.put(10709, 10708);
        mirrorChars.put(10712, 10713);
        mirrorChars.put(10713, 10712);
        mirrorChars.put(10714, 10715);
        mirrorChars.put(10715, 10714);
        mirrorChars.put(10741, 8725);
        mirrorChars.put(10744, 10745);
        mirrorChars.put(10745, 10744);
        mirrorChars.put(10748, 10749);
        mirrorChars.put(10749, 10748);
        mirrorChars.put(10795, 10796);
        mirrorChars.put(10796, 10795);
        mirrorChars.put(10797, 10796);
        mirrorChars.put(10798, 10797);
        mirrorChars.put(10804, 10805);
        mirrorChars.put(10805, 10804);
        mirrorChars.put(10812, 10813);
        mirrorChars.put(10813, 10812);
        mirrorChars.put(10852, 10853);
        mirrorChars.put(10853, 10852);
        mirrorChars.put(10873, 10874);
        mirrorChars.put(10874, 10873);
        mirrorChars.put(10877, 10878);
        mirrorChars.put(10878, 10877);
        mirrorChars.put(10879, 10880);
        mirrorChars.put(10880, 10879);
        mirrorChars.put(10881, 10882);
        mirrorChars.put(10882, 10881);
        mirrorChars.put(10883, 10884);
        mirrorChars.put(10884, 10883);
        mirrorChars.put(10891, 10892);
        mirrorChars.put(10892, 10891);
        mirrorChars.put(10897, 10898);
        mirrorChars.put(10898, 10897);
        mirrorChars.put(10899, 10900);
        mirrorChars.put(10900, 10899);
        mirrorChars.put(10901, 10902);
        mirrorChars.put(10902, 10901);
        mirrorChars.put(10903, 10904);
        mirrorChars.put(10904, 10903);
        mirrorChars.put(10905, 10906);
        mirrorChars.put(10906, 10905);
        mirrorChars.put(10907, 10908);
        mirrorChars.put(10908, 10907);
        mirrorChars.put(10913, 10914);
        mirrorChars.put(10914, 10913);
        mirrorChars.put(10918, 10919);
        mirrorChars.put(10919, 10918);
        mirrorChars.put(10920, 10921);
        mirrorChars.put(10921, 10920);
        mirrorChars.put(10922, 10923);
        mirrorChars.put(10923, 10922);
        mirrorChars.put(10924, 10925);
        mirrorChars.put(10925, 10924);
        mirrorChars.put(10927, 10928);
        mirrorChars.put(10928, 10927);
        mirrorChars.put(10931, 10932);
        mirrorChars.put(10932, 10931);
        mirrorChars.put(10939, 10940);
        mirrorChars.put(10940, 10939);
        mirrorChars.put(10941, 10942);
        mirrorChars.put(10942, 10941);
        mirrorChars.put(10943, 10944);
        mirrorChars.put(10944, 10943);
        mirrorChars.put(10945, 10946);
        mirrorChars.put(10946, 10945);
        mirrorChars.put(10947, 10948);
        mirrorChars.put(10948, 10947);
        mirrorChars.put(10949, 10950);
        mirrorChars.put(10950, 10949);
        mirrorChars.put(10957, 10958);
        mirrorChars.put(10958, 10957);
        mirrorChars.put(10959, 10960);
        mirrorChars.put(10960, 10959);
        mirrorChars.put(10961, 10962);
        mirrorChars.put(10962, 10961);
        mirrorChars.put(10963, 10964);
        mirrorChars.put(10964, 10963);
        mirrorChars.put(10965, 10966);
        mirrorChars.put(10966, 10965);
        mirrorChars.put(10974, 8870);
        mirrorChars.put(10979, 8873);
        mirrorChars.put(10980, 8872);
        mirrorChars.put(10981, 8875);
        mirrorChars.put(10988, 10989);
        mirrorChars.put(10989, 10988);
        mirrorChars.put(10999, 11000);
        mirrorChars.put(11000, 10999);
        mirrorChars.put(11001, 11002);
        mirrorChars.put(11002, 11001);
        mirrorChars.put(12296, 12297);
        mirrorChars.put(12297, 12296);
        mirrorChars.put(12298, 12299);
        mirrorChars.put(12299, 12298);
        mirrorChars.put(12300, 12301);
        mirrorChars.put(12301, 12300);
        mirrorChars.put(12302, 12303);
        mirrorChars.put(12303, 12302);
        mirrorChars.put(12304, 12305);
        mirrorChars.put(12305, 12304);
        mirrorChars.put(12308, 12309);
        mirrorChars.put(12309, 12308);
        mirrorChars.put(12310, 12311);
        mirrorChars.put(12311, 12310);
        mirrorChars.put(12312, 12313);
        mirrorChars.put(12313, 12312);
        mirrorChars.put(12314, 12315);
        mirrorChars.put(12315, 12314);
        mirrorChars.put(65288, 65289);
        mirrorChars.put(65289, 65288);
        mirrorChars.put(65308, 65310);
        mirrorChars.put(65310, 65308);
        mirrorChars.put(65339, 65341);
        mirrorChars.put(65341, 65339);
        mirrorChars.put(65371, 65373);
        mirrorChars.put(65373, 65371);
        mirrorChars.put(65375, 65376);
        mirrorChars.put(65376, 65375);
        mirrorChars.put(65378, 65379);
        mirrorChars.put(65379, 65378);
    }
}

