/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentParser;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DocumentFont
extends BaseFont {
    private HashMap metrics = new HashMap();
    private String fontName;
    private PRIndirectReference refFont;
    private PdfDictionary font;
    private IntHashtable uni2byte = new IntHashtable();
    private IntHashtable diffmap;
    private float Ascender = 800.0f;
    private float CapHeight = 700.0f;
    private float Descender = -200.0f;
    private float ItalicAngle = 0.0f;
    private float llx = -50.0f;
    private float lly = -200.0f;
    private float urx = 100.0f;
    private float ury = 900.0f;
    private boolean isType0 = false;
    private BaseFont cjkMirror;
    private static String[] cjkNames = new String[]{"HeiseiMin-W3", "HeiseiKakuGo-W5", "STSong-Light", "MHei-Medium", "MSung-Light", "HYGoThic-Medium", "HYSMyeongJo-Medium", "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular"};
    private static String[] cjkEncs = new String[]{"UniJIS-UCS2-H", "UniJIS-UCS2-H", "UniGB-UCS2-H", "UniCNS-UCS2-H", "UniCNS-UCS2-H", "UniKS-UCS2-H", "UniKS-UCS2-H", "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H"};
    private static String[] cjkNames2 = new String[]{"MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular"};
    private static String[] cjkEncs2 = new String[]{"UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H", "UniCNS-UTF16-H", "UniGB-UTF16-H", "UniKS-UTF16-H", "UniJIS-UTF16-H"};
    private static final int[] stdEnc = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36, 37, 38, 8217, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 8216, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 161, 162, 163, 8260, 165, 402, 167, 164, 39, 8220, 171, 8249, 8250, 64257, 64258, 0, 8211, 8224, 8225, 183, 0, 182, 8226, 8218, 8222, 8221, 187, 8230, 8240, 0, 191, 0, 96, 180, 710, 732, 175, 728, 729, 168, 0, 730, 184, 0, 733, 731, 711, 8212, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 198, 0, 170, 0, 0, 0, 0, 321, 216, 338, 186, 0, 0, 0, 0, 0, 230, 0, 0, 0, 305, 0, 0, 322, 248, 339, 223, 0, 0, 0, 0};

    DocumentFont(PRIndirectReference pRIndirectReference) {
        this.encoding = "";
        this.fontSpecific = false;
        this.refFont = pRIndirectReference;
        this.fontType = 4;
        this.font = (PdfDictionary)PdfReader.getPdfObject(pRIndirectReference);
        this.fontName = PdfName.decodeName(((PdfName)PdfReader.getPdfObject(this.font.get(PdfName.BASEFONT))).toString());
        PdfName pdfName = (PdfName)PdfReader.getPdfObject(this.font.get(PdfName.SUBTYPE));
        if (PdfName.TYPE1.equals(pdfName) || PdfName.TRUETYPE.equals(pdfName)) {
            this.doType1TT();
        } else {
            for (int i = 0; i < cjkNames.length; ++i) {
                if (!this.fontName.startsWith(cjkNames[i])) continue;
                this.fontName = cjkNames[i];
                try {
                    this.cjkMirror = BaseFont.createFont(this.fontName, cjkEncs[i], false);
                }
                catch (Exception var4_5) {
                    throw new ExceptionConverter(var4_5);
                }
                return;
            }
            String string = PdfName.decodeName(((PdfName)PdfReader.getPdfObject(this.font.get(PdfName.ENCODING))).toString());
            for (int j = 0; j < cjkEncs2.length; ++j) {
                if (!string.startsWith(cjkEncs2[j])) continue;
                try {
                    if (j > 3) {
                        j -= 4;
                    }
                    this.cjkMirror = BaseFont.createFont(cjkNames2[j], cjkEncs2[j], false);
                }
                catch (Exception var5_7) {
                    throw new ExceptionConverter(var5_7);
                }
                return;
            }
            if (PdfName.TYPE0.equals(pdfName) && string.equals("Identity-H")) {
                this.processType0(this.font);
                this.isType0 = true;
            }
        }
    }

    private void processType0(PdfDictionary pdfDictionary) {
        try {
            byte[] arrby = PdfReader.getStreamBytes((PRStream)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.TOUNICODE)));
            PdfArray pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.DESCENDANTFONTS));
            PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)pdfArray.getArrayList().get(0));
            PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.DW));
            int n = 1000;
            if (pdfNumber != null) {
                n = pdfNumber.intValue();
            }
            IntHashtable intHashtable = this.readWidths((PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.W)));
            PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.FONTDESCRIPTOR));
            this.fillFontDesc(pdfDictionary3);
            this.fillMetrics(arrby, intHashtable, n);
        }
        catch (Exception var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    private IntHashtable readWidths(PdfArray pdfArray) {
        IntHashtable intHashtable = new IntHashtable();
        if (pdfArray == null) {
            return intHashtable;
        }
        ArrayList arrayList = pdfArray.getArrayList();
        for (int i = 0; i < arrayList.size(); ++i) {
            PdfObject pdfObject;
            int n;
            int n2 = ((PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(i))).intValue();
            if ((pdfObject = PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(++i))).isArray()) {
                ArrayList arrayList2 = ((PdfArray)pdfObject).getArrayList();
                for (n = 0; n < arrayList2.size(); ++n) {
                    int n3 = ((PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arrayList2.get(n))).intValue();
                    intHashtable.put(n2++, n3);
                }
                continue;
            }
            int n4 = ((PdfNumber)pdfObject).intValue();
            n = ((PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(++i))).intValue();
            while (n2 <= n4) {
                intHashtable.put(n2, n);
                ++n2;
            }
        }
        return intHashtable;
    }

    private String decodeString(PdfString pdfString) {
        if (pdfString.isHexWriting()) {
            return PdfEncodings.convertToString(pdfString.getBytes(), "UnicodeBigUnmarked");
        }
        return pdfString.toUnicodeString();
    }

    private void fillMetrics(byte[] arrby, IntHashtable intHashtable, int n) {
        try {
            PdfContentParser pdfContentParser = new PdfContentParser(new PRTokeniser(arrby));
            PdfObject pdfObject = null;
            PdfObject pdfObject2 = null;
            while ((pdfObject = pdfContentParser.readPRObject()) != null) {
                if (pdfObject.type() == 200) {
                    int n3;
                    int n2;
                    char c;
                    int n4;
                    String string;
                    String string2;
                    if (pdfObject.toString().equals("beginbfchar")) {
                        n4 = ((PdfNumber)pdfObject2).intValue();
                        for (n2 = 0; n2 < n4; ++n2) {
                            string2 = this.decodeString((PdfString)pdfContentParser.readPRObject());
                            string = this.decodeString((PdfString)pdfContentParser.readPRObject());
                            if (string.length() != 1) continue;
                            n3 = string2.charAt(0);
                            c = string.charAt(string.length() - 1);
                            int pdfObject3 = n;
                            if (intHashtable.containsKey(n3)) {
                                pdfObject3 = intHashtable.get(n3);
                            }
                            this.metrics.put(new Integer(c), new int[]{n3, pdfObject3});
                        }
                        continue;
                    }
                    if (!pdfObject.toString().equals("beginbfrange")) continue;
                    n4 = ((PdfNumber)pdfObject2).intValue();
                    for (n2 = 0; n2 < n4; ++n2) {
                        int n5;
                        Object object;
                        string2 = this.decodeString((PdfString)pdfContentParser.readPRObject());
                        string = this.decodeString((PdfString)pdfContentParser.readPRObject());
                        n3 = string2.charAt(0);
                        c = string.charAt(0);
                        PdfObject pdfObject3 = pdfContentParser.readPRObject();
                        if (pdfObject3.isString()) {
                            object = this.decodeString((PdfString)pdfObject3);
                            if (object.length() != 1) continue;
                            n5 = object.charAt(object.length() - 1);
                            while (n3 <= c) {
                                int n6 = n;
                                if (intHashtable.containsKey(n3)) {
                                    n6 = intHashtable.get(n3);
                                }
                                this.metrics.put(new Integer(n5), new int[]{n3++, n6});
                                ++n5;
                            }
                            continue;
                        }
                        object = ((PdfArray)pdfObject3).getArrayList();
                        n5 = 0;
                        while (n5 < object.size()) {
                            String string3 = this.decodeString((PdfString)object.get(n5));
                            if (string3.length() == 1) {
                                char c2 = string3.charAt(string3.length() - 1);
                                int n7 = n;
                                if (intHashtable.containsKey(n3)) {
                                    n7 = intHashtable.get(n3);
                                }
                                this.metrics.put(new Integer(c2), new int[]{n3, n7});
                            }
                            ++n5;
                            ++n3;
                        }
                    }
                    continue;
                }
                pdfObject2 = pdfObject;
            }
        }
        catch (Exception var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    private void doType1TT() {
        PdfNumber pdfNumber;
        PdfDictionary pdfDictionary;
        Object object2;
        reference var7_11;
        int n;
        Object object;
        PdfObject pdfObject = PdfReader.getPdfObject(this.font.get(PdfName.ENCODING));
        if (pdfObject == null) {
            this.fillEncoding(null);
        } else if (pdfObject.isName()) {
            this.fillEncoding((PdfName)pdfObject);
        } else {
            pdfDictionary = (PdfDictionary)pdfObject;
            pdfObject = PdfReader.getPdfObject(pdfDictionary.get(PdfName.BASEENCODING));
            if (pdfObject == null) {
                this.fillEncoding(null);
            } else {
                this.fillEncoding((PdfName)pdfObject);
            }
            pdfNumber = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.DIFFERENCES));
            if (pdfNumber != null) {
                this.diffmap = new IntHashtable();
                object2 = pdfNumber.getArrayList();
                n = 0;
                for (int i = 0; i < object2.size(); ++i) {
                    var7_11 = (reference)((PdfObject)object2.get(i));
                    if (var7_11.isNumber()) {
                        n = ((PdfNumber)var7_11).intValue();
                        continue;
                    }
                    object = GlyphList.nameToUnicode(PdfName.decodeName(((PdfName)var7_11).toString()));
                    if (object != null && object.length > 0) {
                        this.uni2byte.put((int)object[0], n);
                        this.diffmap.put((int)object[0], n);
                    }
                    ++n;
                }
            }
        }
        pdfDictionary = (PdfArray)PdfReader.getPdfObject(this.font.get(PdfName.WIDTHS));
        pdfNumber = (PdfNumber)PdfReader.getPdfObject(this.font.get(PdfName.FIRSTCHAR));
        object2 = (PdfNumber)PdfReader.getPdfObject(this.font.get(PdfName.LASTCHAR));
        if (BuiltinFonts14.containsKey(this.fontName)) {
            BaseFont baseFont;
            void var7_13;
            try {
                baseFont = BaseFont.createFont(this.fontName, "Cp1252", false);
            }
            catch (Exception var6_8) {
                throw new ExceptionConverter(var6_8);
            }
            int[] arrn = this.uni2byte.toOrderedKeys();
            boolean n2 = false;
            while (++var7_13 < arrn.length) {
                object = this.uni2byte.get(arrn[var7_13]);
                this.widths[object] = baseFont.getRawWidth((int)object, GlyphList.unicodeToName(arrn[var7_13]));
            }
            if (this.diffmap != null) {
                void var7_15;
                arrn = this.diffmap.toOrderedKeys();
                boolean bl = false;
                while (++var7_15 < arrn.length) {
                    object = this.diffmap.get(arrn[var7_15]);
                    this.widths[object] = baseFont.getRawWidth((int)object, GlyphList.unicodeToName(arrn[var7_15]));
                }
                this.diffmap = null;
            }
            this.Ascender = baseFont.getFontDescriptor(1, 1000.0f);
            this.CapHeight = baseFont.getFontDescriptor(2, 1000.0f);
            this.Descender = baseFont.getFontDescriptor(3, 1000.0f);
            this.ItalicAngle = baseFont.getFontDescriptor(4, 1000.0f);
            this.llx = baseFont.getFontDescriptor(5, 1000.0f);
            this.lly = baseFont.getFontDescriptor(6, 1000.0f);
            this.urx = baseFont.getFontDescriptor(7, 1000.0f);
            this.ury = baseFont.getFontDescriptor(8, 1000.0f);
        }
        if (pdfNumber != null && object2 != null && pdfDictionary != null) {
            n = pdfNumber.intValue();
            ArrayList arrayList = pdfDictionary.getArrayList();
            for (var7_11 = 0; var7_11 < arrayList.size(); ++var7_11) {
                this.widths[n + var7_11] = ((PdfNumber)arrayList.get((int)var7_11)).intValue();
            }
        }
        this.fillFontDesc((PdfDictionary)PdfReader.getPdfObject(this.font.get(PdfName.FONTDESCRIPTOR)));
    }

    private void fillFontDesc(PdfDictionary pdfDictionary) {
        PdfArray pdfArray;
        if (pdfDictionary == null) {
            return;
        }
        PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ASCENT));
        if (pdfNumber != null) {
            this.Ascender = pdfNumber.floatValue();
        }
        if ((pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.CAPHEIGHT))) != null) {
            this.CapHeight = pdfNumber.floatValue();
        }
        if ((pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.DESCENT))) != null) {
            this.Descender = pdfNumber.floatValue();
        }
        if ((pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ITALICANGLE))) != null) {
            this.ItalicAngle = pdfNumber.floatValue();
        }
        if ((pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.FONTBBOX))) != null) {
            float f;
            ArrayList arrayList = pdfArray.getArrayList();
            this.llx = ((PdfNumber)arrayList.get(0)).floatValue();
            this.lly = ((PdfNumber)arrayList.get(1)).floatValue();
            this.urx = ((PdfNumber)arrayList.get(2)).floatValue();
            this.ury = ((PdfNumber)arrayList.get(3)).floatValue();
            if (this.llx > this.urx) {
                f = this.llx;
                this.llx = this.urx;
                this.urx = f;
            }
            if (this.lly > this.ury) {
                f = this.lly;
                this.lly = this.ury;
                this.ury = f;
            }
        }
    }

    private void fillEncoding(PdfName pdfName) {
        if (PdfName.MAC_ROMAN_ENCODING.equals(pdfName) || PdfName.WIN_ANSI_ENCODING.equals(pdfName)) {
            byte[] arrby = new byte[256];
            for (int i = 0; i < 256; ++i) {
                arrby[i] = (byte)i;
            }
            String string = "Cp1252";
            if (PdfName.MAC_ROMAN_ENCODING.equals(pdfName)) {
                string = "MacRoman";
            }
            String string2 = PdfEncodings.convertToString(arrby, string);
            char[] arrc = string2.toCharArray();
            for (int j = 0; j < 256; ++j) {
                this.uni2byte.put(arrc[j], j);
            }
        } else {
            for (int i = 0; i < 256; ++i) {
                this.uni2byte.put(stdEnc[i], i);
            }
        }
    }

    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }

    public float getFontDescriptor(int n, float f) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getFontDescriptor(n, f);
        }
        switch (n) {
            case 1: 
            case 9: {
                return this.Ascender * f / 1000.0f;
            }
            case 2: {
                return this.CapHeight * f / 1000.0f;
            }
            case 3: 
            case 10: {
                return this.Descender * f / 1000.0f;
            }
            case 4: {
                return this.ItalicAngle;
            }
            case 5: {
                return this.llx * f / 1000.0f;
            }
            case 6: {
                return this.lly * f / 1000.0f;
            }
            case 7: {
                return this.urx * f / 1000.0f;
            }
            case 8: {
                return this.ury * f / 1000.0f;
            }
            case 11: {
                return 0.0f;
            }
            case 12: {
                return (this.urx - this.llx) * f / 1000.0f;
            }
        }
        return 0.0f;
    }

    public String[][] getFullFontName() {
        return new String[][]{{"", "", "", this.fontName}};
    }

    public String[][] getAllNameEntries() {
        return new String[][]{{"4", "", "", "", this.fontName}};
    }

    public int getKerning(int n, int n2) {
        return 0;
    }

    public String getPostscriptFontName() {
        return this.fontName;
    }

    int getRawWidth(int n, String string) {
        return 0;
    }

    public boolean hasKernPairs() {
        return false;
    }

    void writeFont(PdfWriter pdfWriter, PdfIndirectReference pdfIndirectReference, Object[] arrobject) throws DocumentException, IOException {
    }

    public PdfStream getFullFontStream() {
        return null;
    }

    public int getWidth(int n) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getWidth(n);
        }
        if (this.isType0) {
            int[] arrn = (int[])this.metrics.get(new Integer(n));
            if (arrn != null) {
                return arrn[1];
            }
            return 0;
        }
        return super.getWidth(n);
    }

    public int getWidth(String string) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getWidth(string);
        }
        if (this.isType0) {
            char[] arrc = string.toCharArray();
            int n = arrc.length;
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                int[] arrn = (int[])this.metrics.get(new Integer(arrc[i]));
                if (arrn == null) continue;
                n2 += arrn[1];
            }
            return n2;
        }
        return super.getWidth(string);
    }

    byte[] convertToBytes(String string) {
        if (this.cjkMirror != null) {
            return PdfEncodings.convertToBytes(string, "UnicodeBigUnmarked");
        }
        if (this.isType0) {
            char[] arrc = string.toCharArray();
            int n = arrc.length;
            byte[] arrby = new byte[n * 2];
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                int[] arrn = (int[])this.metrics.get(new Integer(arrc[i]));
                if (arrn == null) continue;
                int n3 = arrn[0];
                arrby[n2++] = (byte)(n3 / 256);
                arrby[n2++] = (byte)n3;
            }
            if (n2 == arrby.length) {
                return arrby;
            }
            byte[] arrby2 = new byte[n2];
            System.arraycopy(arrby, 0, arrby2, 0, n2);
            return arrby2;
        }
        char[] arrc = string.toCharArray();
        byte[] arrby = new byte[arrc.length];
        int n = 0;
        for (int i = 0; i < arrc.length; ++i) {
            if (!this.uni2byte.containsKey(arrc[i])) continue;
            arrby[n++] = (byte)this.uni2byte.get(arrc[i]);
        }
        if (n == arrby.length) {
            return arrby;
        }
        byte[] arrby3 = new byte[n];
        System.arraycopy(arrby, 0, arrby3, 0, n);
        return arrby3;
    }

    byte[] convertToBytes(int n) {
        if (this.cjkMirror != null) {
            return PdfEncodings.convertToBytes((char)n, "UnicodeBigUnmarked");
        }
        if (this.isType0) {
            int[] arrn = (int[])this.metrics.get(new Integer(n));
            if (arrn != null) {
                int n2 = arrn[0];
                return new byte[]{(byte)(n2 / 256), (byte)n2};
            }
            return new byte[0];
        }
        if (this.uni2byte.containsKey(n)) {
            return new byte[]{(byte)this.uni2byte.get(n)};
        }
        return new byte[0];
    }

    PdfIndirectReference getIndirectReference() {
        return this.refFont;
    }

    public boolean charExists(int n) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.charExists(n);
        }
        if (this.isType0) {
            return this.metrics.containsKey(new Integer(n));
        }
        return super.charExists(n);
    }

    public void setPostscriptFontName(String string) {
    }

    public boolean setKerning(int n, int n2, int n3) {
        return false;
    }

    public int[] getCharBBox(int n) {
        return null;
    }

    protected int[] getRawCharBBox(int n, String string) {
        return null;
    }
}

