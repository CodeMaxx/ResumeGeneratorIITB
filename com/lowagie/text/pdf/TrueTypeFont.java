/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.TrueTypeFontSubSet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class TrueTypeFont
extends BaseFont {
    static final String[] codePages = new String[]{"1252 Latin 1", "1250 Latin 2: Eastern Europe", "1251 Cyrillic", "1253 Greek", "1254 Turkish", "1255 Hebrew", "1256 Arabic", "1257 Windows Baltic", "1258 Vietnamese", null, null, null, null, null, null, null, "874 Thai", "932 JIS/Japan", "936 Chinese: Simplified chars--PRC and Singapore", "949 Korean Wansung", "950 Chinese: Traditional chars--Taiwan and Hong Kong", "1361 Korean Johab", null, null, null, null, null, null, null, "Macintosh Character Set (US Roman)", "OEM Character Set", "Symbol Character Set", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "869 IBM Greek", "866 MS-DOS Russian", "865 MS-DOS Nordic", "864 Arabic", "863 MS-DOS Canadian French", "862 Hebrew", "861 MS-DOS Icelandic", "860 MS-DOS Portuguese", "857 IBM Turkish", "855 IBM Cyrillic; primarily Russian", "852 Latin 2", "775 MS-DOS Baltic", "737 Greek; former 437 G", "708 Arabic; ASMO 708", "850 WE/Latin 1", "437 US"};
    protected boolean justNames = false;
    protected HashMap tables;
    protected RandomAccessFileOrArray rf;
    protected String fileName;
    protected boolean cff = false;
    protected int cffOffset;
    protected int cffLength;
    protected int directoryOffset;
    protected String ttcIndex;
    protected String style = "";
    protected FontHeader head = new FontHeader();
    protected HorizontalHeader hhea = new HorizontalHeader();
    protected WindowsMetrics os_2 = new WindowsMetrics();
    protected int[] GlyphWidths;
    protected int[][] bboxes;
    protected HashMap cmap10;
    protected HashMap cmap31;
    protected HashMap cmapExt;
    protected IntHashtable kerning = new IntHashtable();
    protected String fontName;
    protected String[][] fullName;
    protected String[][] allNameEntries;
    protected String[][] familyName;
    protected double italicAngle;
    protected boolean isFixedPitch = false;
    protected int underlinePosition;
    protected int underlineThickness;

    protected TrueTypeFont() {
    }

    TrueTypeFont(String string, String string2, boolean bl, byte[] arrby) throws DocumentException, IOException {
        this(string, string2, bl, arrby, false);
    }

    TrueTypeFont(String string, String string2, boolean bl, byte[] arrby, boolean bl2) throws DocumentException, IOException {
        this.justNames = bl2;
        String string3 = TrueTypeFont.getBaseName(string);
        String string4 = TrueTypeFont.getTTCName(string3);
        if (string3.length() < string.length()) {
            this.style = string.substring(string3.length());
        }
        this.encoding = string2;
        this.embedded = bl;
        this.fileName = string4;
        this.fontType = 1;
        this.ttcIndex = "";
        if (string4.length() < string3.length()) {
            this.ttcIndex = string3.substring(string4.length() + 1);
        }
        if (this.fileName.toLowerCase().endsWith(".ttf") || this.fileName.toLowerCase().endsWith(".otf") || this.fileName.toLowerCase().endsWith(".ttc")) {
            this.process(arrby);
            if (!bl2 && this.embedded && this.os_2.fsType == 2) {
                throw new DocumentException(this.fileName + this.style + " cannot be embedded due to licensing restrictions.");
            }
        } else {
            throw new DocumentException(this.fileName + this.style + " is not a TTF, OTF or TTC font file.");
        }
        if (!this.encoding.startsWith("#")) {
            PdfEncodings.convertToBytes(" ", string2);
        }
        this.createEncoding();
    }

    protected static String getTTCName(String string) {
        int n = string.toLowerCase().indexOf(".ttc,");
        if (n < 0) {
            return string;
        }
        return string.substring(0, n + 4);
    }

    void fillTables() throws DocumentException, IOException {
        int[] arrn = (int[])this.tables.get("head");
        if (arrn == null) {
            throw new DocumentException("Table 'head' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(arrn[0] + 16);
        this.head.flags = this.rf.readUnsignedShort();
        this.head.unitsPerEm = this.rf.readUnsignedShort();
        this.rf.skipBytes(16);
        this.head.xMin = this.rf.readShort();
        this.head.yMin = this.rf.readShort();
        this.head.xMax = this.rf.readShort();
        this.head.yMax = this.rf.readShort();
        this.head.macStyle = this.rf.readUnsignedShort();
        arrn = (int[])this.tables.get("hhea");
        if (arrn == null) {
            throw new DocumentException("Table 'hhea' does not exist " + this.fileName + this.style);
        }
        this.rf.seek(arrn[0] + 4);
        this.hhea.Ascender = this.rf.readShort();
        this.hhea.Descender = this.rf.readShort();
        this.hhea.LineGap = this.rf.readShort();
        this.hhea.advanceWidthMax = this.rf.readUnsignedShort();
        this.hhea.minLeftSideBearing = this.rf.readShort();
        this.hhea.minRightSideBearing = this.rf.readShort();
        this.hhea.xMaxExtent = this.rf.readShort();
        this.hhea.caretSlopeRise = this.rf.readShort();
        this.hhea.caretSlopeRun = this.rf.readShort();
        this.rf.skipBytes(12);
        this.hhea.numberOfHMetrics = this.rf.readUnsignedShort();
        arrn = (int[])this.tables.get("OS/2");
        if (arrn == null) {
            throw new DocumentException("Table 'OS/2' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(arrn[0]);
        int n = this.rf.readUnsignedShort();
        this.os_2.xAvgCharWidth = this.rf.readShort();
        this.os_2.usWeightClass = this.rf.readUnsignedShort();
        this.os_2.usWidthClass = this.rf.readUnsignedShort();
        this.os_2.fsType = this.rf.readShort();
        this.os_2.ySubscriptXSize = this.rf.readShort();
        this.os_2.ySubscriptYSize = this.rf.readShort();
        this.os_2.ySubscriptXOffset = this.rf.readShort();
        this.os_2.ySubscriptYOffset = this.rf.readShort();
        this.os_2.ySuperscriptXSize = this.rf.readShort();
        this.os_2.ySuperscriptYSize = this.rf.readShort();
        this.os_2.ySuperscriptXOffset = this.rf.readShort();
        this.os_2.ySuperscriptYOffset = this.rf.readShort();
        this.os_2.yStrikeoutSize = this.rf.readShort();
        this.os_2.yStrikeoutPosition = this.rf.readShort();
        this.os_2.sFamilyClass = this.rf.readShort();
        this.rf.readFully(this.os_2.panose);
        this.rf.skipBytes(16);
        this.rf.readFully(this.os_2.achVendID);
        this.os_2.fsSelection = this.rf.readUnsignedShort();
        this.os_2.usFirstCharIndex = this.rf.readUnsignedShort();
        this.os_2.usLastCharIndex = this.rf.readUnsignedShort();
        this.os_2.sTypoAscender = this.rf.readShort();
        this.os_2.sTypoDescender = this.rf.readShort();
        if (this.os_2.sTypoDescender > 0) {
            this.os_2.sTypoDescender = - this.os_2.sTypoDescender;
        }
        this.os_2.sTypoLineGap = this.rf.readShort();
        this.os_2.usWinAscent = this.rf.readUnsignedShort();
        this.os_2.usWinDescent = this.rf.readUnsignedShort();
        this.os_2.ulCodePageRange1 = 0;
        this.os_2.ulCodePageRange2 = 0;
        if (n > 0) {
            this.os_2.ulCodePageRange1 = this.rf.readInt();
            this.os_2.ulCodePageRange2 = this.rf.readInt();
        }
        if (n > 1) {
            this.rf.skipBytes(2);
            this.os_2.sCapHeight = this.rf.readShort();
        } else {
            this.os_2.sCapHeight = (int)(0.7 * (double)this.head.unitsPerEm);
        }
        arrn = (int[])this.tables.get("post");
        if (arrn == null) {
            this.italicAngle = (- Math.atan2(this.hhea.caretSlopeRun, this.hhea.caretSlopeRise)) * 180.0 / 3.141592653589793;
            return;
        }
        this.rf.seek(arrn[0] + 4);
        short s = this.rf.readShort();
        int n2 = this.rf.readUnsignedShort();
        this.italicAngle = (double)s + (double)n2 / 16384.0;
        this.underlinePosition = this.rf.readShort();
        this.underlineThickness = this.rf.readShort();
        this.isFixedPitch = this.rf.readInt() != 0;
    }

    String getBaseFont() throws DocumentException, IOException {
        int[] arrn = (int[])this.tables.get("name");
        if (arrn == null) {
            throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(arrn[0] + 2);
        int n = this.rf.readUnsignedShort();
        int n2 = this.rf.readUnsignedShort();
        for (int i = 0; i < n; ++i) {
            int n3 = this.rf.readUnsignedShort();
            int n4 = this.rf.readUnsignedShort();
            int n5 = this.rf.readUnsignedShort();
            int n6 = this.rf.readUnsignedShort();
            int n7 = this.rf.readUnsignedShort();
            int n8 = this.rf.readUnsignedShort();
            if (n6 != 6) continue;
            this.rf.seek(arrn[0] + n2 + n8);
            if (n3 == 0 || n3 == 3) {
                return this.readUnicodeString(n7);
            }
            return this.readStandardString(n7);
        }
        File file = new File(this.fileName);
        return file.getName().replace(' ', '-');
    }

    String[][] getNames(int n) throws DocumentException, IOException {
        int n2;
        int[] arrn = (int[])this.tables.get("name");
        if (arrn == null) {
            throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(arrn[0] + 2);
        int n3 = this.rf.readUnsignedShort();
        int n4 = this.rf.readUnsignedShort();
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        for (int i = 0; i < n3; ++i) {
            n2 = this.rf.readUnsignedShort();
            int n5 = this.rf.readUnsignedShort();
            int n6 = this.rf.readUnsignedShort();
            int n7 = this.rf.readUnsignedShort();
            int n8 = this.rf.readUnsignedShort();
            int n9 = this.rf.readUnsignedShort();
            if (n7 != n) continue;
            int n10 = this.rf.getFilePointer();
            this.rf.seek(arrn[0] + n4 + n9);
            String string = n2 == 0 || n2 == 3 || n2 == 2 && n5 == 1 ? this.readUnicodeString(n8) : this.readStandardString(n8);
            arrayList.add(new String[]{String.valueOf(n2), String.valueOf(n5), String.valueOf(n6), string});
            this.rf.seek(n10);
        }
        String[][] arrstring = new String[arrayList.size()][];
        for (n2 = 0; n2 < arrayList.size(); ++n2) {
            arrstring[n2] = (String[])arrayList.get(n2);
        }
        return arrstring;
    }

    String[][] getAllNames() throws DocumentException, IOException {
        int n;
        int[] arrn = (int[])this.tables.get("name");
        if (arrn == null) {
            throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(arrn[0] + 2);
        int n2 = this.rf.readUnsignedShort();
        int n3 = this.rf.readUnsignedShort();
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        for (int i = 0; i < n2; ++i) {
            n = this.rf.readUnsignedShort();
            int n4 = this.rf.readUnsignedShort();
            int n5 = this.rf.readUnsignedShort();
            int n6 = this.rf.readUnsignedShort();
            int n7 = this.rf.readUnsignedShort();
            int n8 = this.rf.readUnsignedShort();
            int n9 = this.rf.getFilePointer();
            this.rf.seek(arrn[0] + n3 + n8);
            String string = n == 0 || n == 3 || n == 2 && n4 == 1 ? this.readUnicodeString(n7) : this.readStandardString(n7);
            arrayList.add(new String[]{String.valueOf(n6), String.valueOf(n), String.valueOf(n4), String.valueOf(n5), string});
            this.rf.seek(n9);
        }
        String[][] arrstring = new String[arrayList.size()][];
        for (n = 0; n < arrayList.size(); ++n) {
            arrstring[n] = (String[])arrayList.get(n);
        }
        return arrstring;
    }

    void checkCff() {
        int[] arrn = (int[])this.tables.get("CFF ");
        if (arrn != null) {
            this.cff = true;
            this.cffOffset = arrn[0];
            this.cffLength = arrn[1];
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void process(byte[] arrby) throws DocumentException, IOException {
        this.tables = new HashMap();
        try {
            int n;
            int n2;
            this.rf = arrby == null ? new RandomAccessFileOrArray(this.fileName) : new RandomAccessFileOrArray(arrby);
            if (this.ttcIndex.length() > 0) {
                n2 = Integer.parseInt(this.ttcIndex);
                if (n2 < 0) {
                    throw new DocumentException("The font index for " + this.fileName + " must be positive.");
                }
                String string = this.readStandardString(4);
                if (!string.equals("ttcf")) {
                    throw new DocumentException(this.fileName + " is not a valid TTC file.");
                }
                this.rf.skipBytes(4);
                n = this.rf.readInt();
                if (n2 >= n) {
                    throw new DocumentException("The font index for " + this.fileName + " must be between 0 and " + (n - 1) + ". It was " + n2 + ".");
                }
                this.rf.skipBytes(n2 * 4);
                this.directoryOffset = this.rf.readInt();
            }
            this.rf.seek(this.directoryOffset);
            n2 = this.rf.readInt();
            if (n2 != 65536 && n2 != 1330926671) {
                throw new DocumentException(this.fileName + " is not a valid TTF or OTF file.");
            }
            int n3 = this.rf.readUnsignedShort();
            this.rf.skipBytes(6);
            for (n = 0; n < n3; ++n) {
                String string = this.readStandardString(4);
                this.rf.skipBytes(4);
                int[] arrn = new int[]{this.rf.readInt(), this.rf.readInt()};
                this.tables.put(string, arrn);
            }
            this.checkCff();
            this.fontName = this.getBaseFont();
            this.fullName = this.getNames(4);
            this.familyName = this.getNames(1);
            this.allNameEntries = this.getAllNames();
            if (!this.justNames) {
                this.fillTables();
                this.readGlyphWidths();
                this.readCMaps();
                this.readKerning();
                this.readBbox();
                this.GlyphWidths = null;
            }
            Object var8_8 = null;
            if (this.rf == null) return;
        }
        catch (Throwable var7_10) {
            Object var8_9 = null;
            if (this.rf == null) throw var7_10;
            this.rf.close();
            if (this.embedded) throw var7_10;
            this.rf = null;
            throw var7_10;
        }
        this.rf.close();
        if (this.embedded) return;
        this.rf = null;
    }

    protected String readStandardString(int n) throws IOException {
        byte[] arrby = new byte[n];
        this.rf.readFully(arrby);
        try {
            return new String(arrby, "Cp1252");
        }
        catch (Exception var3_3) {
            throw new ExceptionConverter(var3_3);
        }
    }

    protected String readUnicodeString(int n) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < (n /= 2); ++i) {
            stringBuffer.append(this.rf.readChar());
        }
        return stringBuffer.toString();
    }

    protected void readGlyphWidths() throws DocumentException, IOException {
        int[] arrn = (int[])this.tables.get("hmtx");
        if (arrn == null) {
            throw new DocumentException("Table 'hmtx' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(arrn[0]);
        this.GlyphWidths = new int[this.hhea.numberOfHMetrics];
        for (int i = 0; i < this.hhea.numberOfHMetrics; ++i) {
            this.GlyphWidths[i] = this.rf.readUnsignedShort() * 1000 / this.head.unitsPerEm;
            this.rf.readUnsignedShort();
        }
    }

    protected int getGlyphWidth(int n) {
        if (n >= this.GlyphWidths.length) {
            n = this.GlyphWidths.length - 1;
        }
        return this.GlyphWidths[n];
    }

    private void readBbox() throws DocumentException, IOException {
        int[] arrn;
        int n;
        int n2;
        int[] arrn2 = (int[])this.tables.get("head");
        if (arrn2 == null) {
            throw new DocumentException("Table 'head' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(arrn2[0] + 51);
        boolean bl = this.rf.readUnsignedShort() == 0;
        arrn2 = (int[])this.tables.get("loca");
        if (arrn2 == null) {
            return;
        }
        this.rf.seek(arrn2[0]);
        if (bl) {
            n = arrn2[1] / 2;
            arrn = new int[n];
            for (n2 = 0; n2 < n; ++n2) {
                arrn[n2] = this.rf.readUnsignedShort() * 2;
            }
        } else {
            n = arrn2[1] / 4;
            arrn = new int[n];
            for (n2 = 0; n2 < n; ++n2) {
                arrn[n2] = this.rf.readInt();
            }
        }
        if ((arrn2 = (int[])this.tables.get("glyf")) == null) {
            throw new DocumentException("Table 'glyf' does not exist in " + this.fileName + this.style);
        }
        n = arrn2[0];
        this.bboxes = new int[arrn.length - 1][];
        for (n2 = 0; n2 < arrn.length - 1; ++n2) {
            int n3 = arrn[n2];
            if (n3 == arrn[n2 + 1]) continue;
            this.rf.seek(n + n3 + 2);
            this.bboxes[n2] = new int[]{this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm};
        }
    }

    void readCMaps() throws DocumentException, IOException {
        int n;
        int[] arrn = (int[])this.tables.get("cmap");
        if (arrn == null) {
            throw new DocumentException("Table 'cmap' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(arrn[0]);
        this.rf.skipBytes(2);
        int n2 = this.rf.readUnsignedShort();
        this.fontSpecific = false;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        for (n = 0; n < n2; ++n) {
            int n7 = this.rf.readUnsignedShort();
            int n8 = this.rf.readUnsignedShort();
            int n9 = this.rf.readInt();
            if (n7 == 3 && n8 == 0) {
                this.fontSpecific = true;
                n5 = n9;
            } else if (n7 == 3 && n8 == 1) {
                n4 = n9;
            } else if (n7 == 3 && n8 == 10) {
                n6 = n9;
            }
            if (n7 != 1 || n8 != 0) continue;
            n3 = n9;
        }
        if (n3 > 0) {
            this.rf.seek(arrn[0] + n3);
            n = this.rf.readUnsignedShort();
            switch (n) {
                case 0: {
                    this.cmap10 = this.readFormat0();
                    break;
                }
                case 4: {
                    this.cmap10 = this.readFormat4();
                    break;
                }
                case 6: {
                    this.cmap10 = this.readFormat6();
                }
            }
        }
        if (n4 > 0) {
            this.rf.seek(arrn[0] + n4);
            n = this.rf.readUnsignedShort();
            if (n == 4) {
                this.cmap31 = this.readFormat4();
            }
        }
        if (n5 > 0) {
            this.rf.seek(arrn[0] + n5);
            n = this.rf.readUnsignedShort();
            if (n == 4) {
                this.cmap10 = this.readFormat4();
            }
        }
        if (n6 > 0) {
            this.rf.seek(arrn[0] + n6);
            n = this.rf.readUnsignedShort();
            switch (n) {
                case 0: {
                    this.cmapExt = this.readFormat0();
                    break;
                }
                case 4: {
                    this.cmapExt = this.readFormat4();
                    break;
                }
                case 6: {
                    this.cmapExt = this.readFormat6();
                    break;
                }
                case 12: {
                    this.cmapExt = this.readFormat12();
                }
            }
        }
    }

    HashMap readFormat12() throws IOException {
        HashMap<Integer, int[]> hashMap = new HashMap<Integer, int[]>();
        this.rf.skipBytes(2);
        int n = this.rf.readInt();
        this.rf.skipBytes(4);
        int n2 = this.rf.readInt();
        for (int i = 0; i < n2; ++i) {
            int n3 = this.rf.readInt();
            int n4 = this.rf.readInt();
            int n5 = this.rf.readInt();
            for (int j = n3; j <= n4; ++j) {
                int[] arrn;
                arrn = new int[]{n5++, this.getGlyphWidth(arrn[0])};
                hashMap.put(new Integer(j), arrn);
            }
        }
        return hashMap;
    }

    HashMap readFormat0() throws IOException {
        HashMap<Integer, int[]> hashMap = new HashMap<Integer, int[]>();
        this.rf.skipBytes(4);
        for (int i = 0; i < 256; ++i) {
            int[] arrn;
            arrn = new int[]{this.rf.readUnsignedByte(), this.getGlyphWidth(arrn[0])};
            hashMap.put(new Integer(i), arrn);
        }
        return hashMap;
    }

    HashMap readFormat4() throws IOException {
        int n;
        HashMap<Integer, int[]> hashMap = new HashMap<Integer, int[]>();
        int n2 = this.rf.readUnsignedShort();
        this.rf.skipBytes(2);
        int n3 = this.rf.readUnsignedShort() / 2;
        this.rf.skipBytes(6);
        int[] arrn = new int[n3];
        for (int i = 0; i < n3; ++i) {
            arrn[i] = this.rf.readUnsignedShort();
        }
        this.rf.skipBytes(2);
        int[] arrn2 = new int[n3];
        for (int j = 0; j < n3; ++j) {
            arrn2[j] = this.rf.readUnsignedShort();
        }
        int[] arrn3 = new int[n3];
        for (int k = 0; k < n3; ++k) {
            arrn3[k] = this.rf.readUnsignedShort();
        }
        int[] arrn4 = new int[n3];
        for (int i2 = 0; i2 < n3; ++i2) {
            arrn4[i2] = this.rf.readUnsignedShort();
        }
        int[] arrn5 = new int[n2 / 2 - 8 - n3 * 4];
        for (n = 0; n < arrn5.length; ++n) {
            arrn5[n] = this.rf.readUnsignedShort();
        }
        for (n = 0; n < n3; ++n) {
            for (int i3 = arrn2[n]; i3 <= arrn[n] && i3 != 65535; ++i3) {
                int n4;
                int[] arrn6;
                if (arrn4[n] == 0) {
                    n4 = i3 + arrn3[n] & 65535;
                } else {
                    int n5 = n + arrn4[n] / 2 - n3 + i3 - arrn2[n];
                    if (n5 >= arrn5.length) continue;
                    n4 = arrn5[n5] + arrn3[n] & 65535;
                }
                arrn6 = new int[]{n4, this.getGlyphWidth(arrn6[0])};
                hashMap.put(new Integer(this.fontSpecific ? ((i3 & 65280) == 61440 ? i3 & 255 : i3) : i3), arrn6);
            }
        }
        return hashMap;
    }

    HashMap readFormat6() throws IOException {
        HashMap<Integer, int[]> hashMap = new HashMap<Integer, int[]>();
        this.rf.skipBytes(4);
        int n = this.rf.readUnsignedShort();
        int n2 = this.rf.readUnsignedShort();
        for (int i = 0; i < n2; ++i) {
            int[] arrn;
            arrn = new int[]{this.rf.readUnsignedShort(), this.getGlyphWidth(arrn[0])};
            hashMap.put(new Integer(i + n), arrn);
        }
        return hashMap;
    }

    void readKerning() throws IOException {
        int[] arrn = (int[])this.tables.get("kern");
        if (arrn == null) {
            return;
        }
        this.rf.seek(arrn[0] + 2);
        int n = this.rf.readUnsignedShort();
        int n2 = arrn[0] + 4;
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            this.rf.seek(n2 += n3);
            this.rf.skipBytes(2);
            n3 = this.rf.readUnsignedShort();
            int n4 = this.rf.readUnsignedShort();
            if ((n4 & 65527) != 1) continue;
            int n5 = this.rf.readUnsignedShort();
            this.rf.skipBytes(6);
            for (int j = 0; j < n5; ++j) {
                int n6 = this.rf.readInt();
                int n7 = this.rf.readShort() * 1000 / this.head.unitsPerEm;
                this.kerning.put(n6, n7);
            }
        }
    }

    public int getKerning(int n, int n2) {
        int[] arrn = this.getMetricsTT(n);
        if (arrn == null) {
            return 0;
        }
        int n3 = arrn[0];
        arrn = this.getMetricsTT(n2);
        if (arrn == null) {
            return 0;
        }
        int n4 = arrn[0];
        return this.kerning.get((n3 << 16) + n4);
    }

    int getRawWidth(int n, String string) {
        int[] arrn = this.getMetricsTT(n);
        if (arrn == null) {
            return 0;
        }
        return arrn[1];
    }

    protected PdfDictionary getFontDescriptor(PdfIndirectReference pdfIndirectReference, String string, PdfIndirectReference pdfIndirectReference2) {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        pdfDictionary.put(PdfName.ASCENT, new PdfNumber(this.os_2.sTypoAscender * 1000 / this.head.unitsPerEm));
        pdfDictionary.put(PdfName.CAPHEIGHT, new PdfNumber(this.os_2.sCapHeight * 1000 / this.head.unitsPerEm));
        pdfDictionary.put(PdfName.DESCENT, new PdfNumber(this.os_2.sTypoDescender * 1000 / this.head.unitsPerEm));
        pdfDictionary.put(PdfName.FONTBBOX, new PdfRectangle(this.head.xMin * 1000 / this.head.unitsPerEm, this.head.yMin * 1000 / this.head.unitsPerEm, this.head.xMax * 1000 / this.head.unitsPerEm, this.head.yMax * 1000 / this.head.unitsPerEm));
        if (pdfIndirectReference2 != null) {
            pdfDictionary.put(PdfName.CIDSET, pdfIndirectReference2);
        }
        if (this.cff) {
            if (this.encoding.startsWith("Identity-")) {
                pdfDictionary.put(PdfName.FONTNAME, new PdfName(string + this.fontName + "-" + this.encoding));
            } else {
                pdfDictionary.put(PdfName.FONTNAME, new PdfName(string + this.fontName + this.style));
            }
        } else {
            pdfDictionary.put(PdfName.FONTNAME, new PdfName(string + this.fontName + this.style));
        }
        pdfDictionary.put(PdfName.ITALICANGLE, new PdfNumber(this.italicAngle));
        pdfDictionary.put(PdfName.STEMV, new PdfNumber(80));
        if (pdfIndirectReference != null) {
            if (this.cff) {
                pdfDictionary.put(PdfName.FONTFILE3, pdfIndirectReference);
            } else {
                pdfDictionary.put(PdfName.FONTFILE2, pdfIndirectReference);
            }
        }
        int n = 0;
        if (this.isFixedPitch) {
            n |= true;
        }
        n |= this.fontSpecific ? 4 : 32;
        if ((this.head.macStyle & 2) != 0) {
            n |= 64;
        }
        if ((this.head.macStyle & 1) != 0) {
            n |= 262144;
        }
        pdfDictionary.put(PdfName.FLAGS, new PdfNumber(n));
        return pdfDictionary;
    }

    protected PdfDictionary getFontBaseType(PdfIndirectReference pdfIndirectReference, String string, int n, int n2, byte[] arrby) {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONT);
        if (this.cff) {
            pdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE1);
            pdfDictionary.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
        } else {
            pdfDictionary.put(PdfName.SUBTYPE, PdfName.TRUETYPE);
            pdfDictionary.put(PdfName.BASEFONT, new PdfName(string + this.fontName + this.style));
        }
        pdfDictionary.put(PdfName.BASEFONT, new PdfName(string + this.fontName + this.style));
        if (!this.fontSpecific) {
            for (int i = n; i <= n2; ++i) {
                if (this.differences[i].equals(".notdef")) continue;
                n = i;
                break;
            }
            if (this.encoding.equals("Cp1252") || this.encoding.equals("MacRoman")) {
                pdfDictionary.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
            } else {
                PdfDictionary pdfDictionary2 = new PdfDictionary(PdfName.ENCODING);
                PdfArray pdfArray = new PdfArray();
                boolean bl = true;
                for (int j = n; j <= n2; ++j) {
                    if (arrby[j] != 0) {
                        if (bl) {
                            pdfArray.add(new PdfNumber(j));
                            bl = false;
                        }
                        pdfArray.add(new PdfName(this.differences[j]));
                        continue;
                    }
                    bl = true;
                }
                pdfDictionary2.put(PdfName.DIFFERENCES, pdfArray);
                pdfDictionary.put(PdfName.ENCODING, pdfDictionary2);
            }
        }
        pdfDictionary.put(PdfName.FIRSTCHAR, new PdfNumber(n));
        pdfDictionary.put(PdfName.LASTCHAR, new PdfNumber(n2));
        PdfArray pdfArray = new PdfArray();
        for (int i = n; i <= n2; ++i) {
            if (arrby[i] == 0) {
                pdfArray.add(new PdfNumber(0));
                continue;
            }
            pdfArray.add(new PdfNumber(this.widths[i]));
        }
        pdfDictionary.put(PdfName.WIDTHS, pdfArray);
        if (pdfIndirectReference != null) {
            pdfDictionary.put(PdfName.FONTDESCRIPTOR, pdfIndirectReference);
        }
        return pdfDictionary;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected byte[] getFullFont() throws IOException {
        byte[] arrby;
        RandomAccessFileOrArray randomAccessFileOrArray = null;
        try {
            randomAccessFileOrArray = new RandomAccessFileOrArray(this.rf);
            randomAccessFileOrArray.reOpen();
            byte[] arrby2 = new byte[randomAccessFileOrArray.length()];
            randomAccessFileOrArray.readFully(arrby2);
            arrby = arrby2;
            Object var5_4 = null;
        }
        catch (Throwable var4_8) {
            Object var5_5 = null;
            try {
                if (randomAccessFileOrArray != null) {
                    randomAccessFileOrArray.close();
                }
            }
            catch (Exception var6_7) {
                // empty catch block
            }
            throw var4_8;
        }
        try {
            if (randomAccessFileOrArray != null) {
                randomAccessFileOrArray.close();
            }
        }
        catch (Exception var6_6) {
            // empty catch block
        }
        return arrby;
    }

    protected static int[] compactRanges(ArrayList arrayList) {
        int n;
        ArrayList<int[]> arrayList2 = new ArrayList<int[]>();
        for (n = 0; n < arrayList.size(); ++n) {
            int[] arrn = (int[])arrayList.get(n);
            for (int i = 0; i < arrn.length; i += 2) {
                arrayList2.add(new int[]{Math.max(0, Math.min(arrn[i], arrn[i + 1])), Math.min(65535, Math.max(arrn[i], arrn[i + 1]))});
            }
        }
        for (n = 0; n < arrayList2.size() - 1; ++n) {
            for (int i = n + 1; i < arrayList2.size(); ++i) {
                int[] arrn;
                int[] arrn2 = (int[])arrayList2.get(n);
                if ((arrn2[0] < (arrn = (int[])arrayList2.get(i))[0] || arrn2[0] > arrn[1]) && (arrn2[1] < arrn[0] || arrn2[0] > arrn[1])) continue;
                arrn2[0] = Math.min(arrn2[0], arrn[0]);
                arrn2[1] = Math.max(arrn2[1], arrn[1]);
                arrayList2.remove(i);
                --i;
            }
        }
        int[] arrn = new int[arrayList2.size() * 2];
        for (int i = 0; i < arrayList2.size(); ++i) {
            int[] arrn3 = (int[])arrayList2.get(i);
            arrn[i * 2] = arrn3[0];
            arrn[i * 2 + 1] = arrn3[1];
        }
        return arrn;
    }

    protected void addRangeUni(HashMap hashMap, boolean bl, boolean bl2) {
        if (!(bl2 || this.subsetRanges == null && this.directoryOffset <= 0)) {
            int[] arrn;
            int[] arrn2;
            if (this.subsetRanges == null && this.directoryOffset > 0) {
                int[] arrn3 = new int[2];
                arrn3[0] = 0;
                arrn2 = arrn3;
                arrn3[1] = 65535;
            } else {
                arrn2 = arrn = TrueTypeFont.compactRanges(this.subsetRanges);
            }
            HashMap hashMap2 = !this.fontSpecific && this.cmap31 != null ? this.cmap31 : (this.fontSpecific && this.cmap10 != null ? this.cmap10 : (this.cmap31 != null ? this.cmap31 : this.cmap10));
            Iterator iterator = hashMap2.entrySet().iterator();
            while (iterator.hasNext()) {
                int[] arrn4;
                Map.Entry entry = iterator.next();
                int[] arrn5 = (int[])entry.getValue();
                Integer n = new Integer(arrn5[0]);
                if (hashMap.containsKey(n)) continue;
                int n2 = (Integer)entry.getKey();
                boolean bl3 = true;
                for (int i = 0; i < arrn.length; i += 2) {
                    if (n2 < arrn[i] || n2 > arrn[i + 1]) continue;
                    bl3 = false;
                    break;
                }
                if (bl3) continue;
                if (bl) {
                    int[] arrn6 = new int[3];
                    arrn6[0] = arrn5[0];
                    arrn6[1] = arrn5[1];
                    arrn4 = arrn6;
                    arrn6[2] = n2;
                } else {
                    arrn4 = null;
                }
                hashMap.put(n, arrn4);
            }
        }
    }

    void writeFont(PdfWriter pdfWriter, PdfIndirectReference pdfIndirectReference, Object[] arrobject) throws DocumentException, IOException {
        boolean bl;
        int n = (Integer)arrobject[0];
        int n2 = (Integer)arrobject[1];
        byte[] arrby = (byte[])arrobject[2];
        boolean bl2 = bl = (Boolean)arrobject[3] != false && this.subset;
        if (!bl) {
            n = 0;
            n2 = arrby.length - 1;
            for (int i = 0; i < arrby.length; ++i) {
                arrby[i] = 1;
            }
        }
        PdfIndirectReference pdfIndirectReference2 = null;
        PdfDictionary pdfDictionary = null;
        PdfIndirectObject pdfIndirectObject = null;
        String string = "";
        if (this.embedded) {
            if (this.cff) {
                pdfDictionary = new BaseFont.StreamFont(this.readCffFont(), "Type1C", this.compressionLevel);
                pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
                pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
            } else {
                int[] arrn;
                if (bl) {
                    string = TrueTypeFont.createSubsetPrefix();
                }
                HashMap<Integer, Object> hashMap = new HashMap<Integer, Object>();
                for (int i = n; i <= n2; ++i) {
                    if (arrby[i] == 0) continue;
                    arrn = null;
                    if (this.specialMap != null) {
                        int[] arrn2 = GlyphList.nameToUnicode(this.differences[i]);
                        if (arrn2 != null) {
                            arrn = this.getMetricsTT(arrn2[0]);
                        }
                    } else {
                        arrn = this.fontSpecific ? this.getMetricsTT(i) : this.getMetricsTT(this.unicodeDifferences[i]);
                    }
                    if (arrn == null) continue;
                    hashMap.put(new Integer(arrn[0]), null);
                }
                this.addRangeUni(hashMap, false, bl);
                byte[] arrby2 = null;
                if (bl || this.directoryOffset != 0 || this.subsetRanges != null) {
                    arrn = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), hashMap, this.directoryOffset, true, !bl);
                    arrby2 = arrn.process();
                } else {
                    arrby2 = this.getFullFont();
                }
                arrn = new int[]{arrby2.length};
                pdfDictionary = new BaseFont.StreamFont(arrby2, arrn, this.compressionLevel);
                pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
                pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
            }
        }
        if ((pdfDictionary = this.getFontDescriptor(pdfIndirectReference2, string, null)) != null) {
            pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
            pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        }
        pdfDictionary = this.getFontBaseType(pdfIndirectReference2, string, n, n2, arrby);
        pdfWriter.addToBody((PdfObject)pdfDictionary, pdfIndirectReference);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected byte[] readCffFont() throws IOException {
        RandomAccessFileOrArray randomAccessFileOrArray = new RandomAccessFileOrArray(this.rf);
        byte[] arrby = new byte[this.cffLength];
        try {
            randomAccessFileOrArray.reOpen();
            randomAccessFileOrArray.seek(this.cffOffset);
            randomAccessFileOrArray.readFully(arrby);
        }
        finally {
            try {
                randomAccessFileOrArray.close();
            }
            catch (Exception var3_3) {}
        }
        return arrby;
    }

    public PdfStream getFullFontStream() throws IOException, DocumentException {
        if (this.cff) {
            return new BaseFont.StreamFont(this.readCffFont(), "Type1C", this.compressionLevel);
        }
        byte[] arrby = this.getFullFont();
        int[] arrn = new int[]{arrby.length};
        return new BaseFont.StreamFont(arrby, arrn, this.compressionLevel);
    }

    public float getFontDescriptor(int n, float f) {
        switch (n) {
            case 1: {
                return (float)this.os_2.sTypoAscender * f / (float)this.head.unitsPerEm;
            }
            case 2: {
                return (float)this.os_2.sCapHeight * f / (float)this.head.unitsPerEm;
            }
            case 3: {
                return (float)this.os_2.sTypoDescender * f / (float)this.head.unitsPerEm;
            }
            case 4: {
                return (float)this.italicAngle;
            }
            case 5: {
                return f * (float)this.head.xMin / (float)this.head.unitsPerEm;
            }
            case 6: {
                return f * (float)this.head.yMin / (float)this.head.unitsPerEm;
            }
            case 7: {
                return f * (float)this.head.xMax / (float)this.head.unitsPerEm;
            }
            case 8: {
                return f * (float)this.head.yMax / (float)this.head.unitsPerEm;
            }
            case 9: {
                return f * (float)this.hhea.Ascender / (float)this.head.unitsPerEm;
            }
            case 10: {
                return f * (float)this.hhea.Descender / (float)this.head.unitsPerEm;
            }
            case 11: {
                return f * (float)this.hhea.LineGap / (float)this.head.unitsPerEm;
            }
            case 12: {
                return f * (float)this.hhea.advanceWidthMax / (float)this.head.unitsPerEm;
            }
            case 13: {
                return (float)(this.underlinePosition - this.underlineThickness / 2) * f / (float)this.head.unitsPerEm;
            }
            case 14: {
                return (float)this.underlineThickness * f / (float)this.head.unitsPerEm;
            }
            case 15: {
                return (float)this.os_2.yStrikeoutPosition * f / (float)this.head.unitsPerEm;
            }
            case 16: {
                return (float)this.os_2.yStrikeoutSize * f / (float)this.head.unitsPerEm;
            }
            case 17: {
                return (float)this.os_2.ySubscriptYSize * f / (float)this.head.unitsPerEm;
            }
            case 18: {
                return (float)(- this.os_2.ySubscriptYOffset) * f / (float)this.head.unitsPerEm;
            }
            case 19: {
                return (float)this.os_2.ySuperscriptYSize * f / (float)this.head.unitsPerEm;
            }
            case 20: {
                return (float)this.os_2.ySuperscriptYOffset * f / (float)this.head.unitsPerEm;
            }
        }
        return 0.0f;
    }

    public int[] getMetricsTT(int n) {
        if (this.cmapExt != null) {
            return (int[])this.cmapExt.get(new Integer(n));
        }
        if (!this.fontSpecific && this.cmap31 != null) {
            return (int[])this.cmap31.get(new Integer(n));
        }
        if (this.fontSpecific && this.cmap10 != null) {
            return (int[])this.cmap10.get(new Integer(n));
        }
        if (this.cmap31 != null) {
            return (int[])this.cmap31.get(new Integer(n));
        }
        if (this.cmap10 != null) {
            return (int[])this.cmap10.get(new Integer(n));
        }
        return null;
    }

    public String getPostscriptFontName() {
        return this.fontName;
    }

    public String[] getCodePagesSupported() {
        long l = ((long)this.os_2.ulCodePageRange2 << 32) + ((long)this.os_2.ulCodePageRange1 & 0xFFFFFFFFL);
        int n = 0;
        long l2 = 1;
        for (int i = 0; i < 64; ++i) {
            if ((l & l2) != 0 && codePages[i] != null) {
                ++n;
            }
            l2 <<= 1;
        }
        String[] arrstring = new String[n];
        n = 0;
        l2 = 1;
        for (int j = 0; j < 64; ++j) {
            if ((l & l2) != 0 && codePages[j] != null) {
                arrstring[n++] = codePages[j];
            }
            l2 <<= 1;
        }
        return arrstring;
    }

    public String[][] getFullFontName() {
        return this.fullName;
    }

    public String[][] getAllNameEntries() {
        return this.allNameEntries;
    }

    public String[][] getFamilyFontName() {
        return this.familyName;
    }

    public boolean hasKernPairs() {
        return this.kerning.size() > 0;
    }

    public void setPostscriptFontName(String string) {
        this.fontName = string;
    }

    public boolean setKerning(int n, int n2, int n3) {
        int[] arrn = this.getMetricsTT(n);
        if (arrn == null) {
            return false;
        }
        int n4 = arrn[0];
        arrn = this.getMetricsTT(n2);
        if (arrn == null) {
            return false;
        }
        int n5 = arrn[0];
        this.kerning.put((n4 << 16) + n5, n3);
        return true;
    }

    protected int[] getRawCharBBox(int n, String string) {
        HashMap hashMap = null;
        hashMap = string == null || this.cmap31 == null ? this.cmap10 : this.cmap31;
        if (hashMap == null) {
            return null;
        }
        int[] arrn = (int[])hashMap.get(new Integer(n));
        if (arrn == null || this.bboxes == null) {
            return null;
        }
        return this.bboxes[arrn[0]];
    }

    protected static class WindowsMetrics {
        short xAvgCharWidth;
        int usWeightClass;
        int usWidthClass;
        short fsType;
        short ySubscriptXSize;
        short ySubscriptYSize;
        short ySubscriptXOffset;
        short ySubscriptYOffset;
        short ySuperscriptXSize;
        short ySuperscriptYSize;
        short ySuperscriptXOffset;
        short ySuperscriptYOffset;
        short yStrikeoutSize;
        short yStrikeoutPosition;
        short sFamilyClass;
        byte[] panose = new byte[10];
        byte[] achVendID = new byte[4];
        int fsSelection;
        int usFirstCharIndex;
        int usLastCharIndex;
        short sTypoAscender;
        short sTypoDescender;
        short sTypoLineGap;
        int usWinAscent;
        int usWinDescent;
        int ulCodePageRange1;
        int ulCodePageRange2;
        int sCapHeight;

        protected WindowsMetrics() {
        }
    }

    protected static class HorizontalHeader {
        short Ascender;
        short Descender;
        short LineGap;
        int advanceWidthMax;
        short minLeftSideBearing;
        short minRightSideBearing;
        short xMaxExtent;
        short caretSlopeRise;
        short caretSlopeRun;
        int numberOfHMetrics;

        protected HorizontalHeader() {
        }
    }

    protected static class FontHeader {
        int flags;
        int unitsPerEm;
        short xMin;
        short yMin;
        short xMax;
        short yMax;
        int macStyle;

        protected FontHeader() {
        }
    }

}

