/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.CJKFont;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.EnumerateTTC;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TrueTypeFont;
import com.lowagie.text.pdf.TrueTypeFontUnicode;
import com.lowagie.text.pdf.Type1Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class BaseFont {
    public static final String COURIER = "Courier";
    public static final String COURIER_BOLD = "Courier-Bold";
    public static final String COURIER_OBLIQUE = "Courier-Oblique";
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
    public static final String HELVETICA = "Helvetica";
    public static final String HELVETICA_BOLD = "Helvetica-Bold";
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
    public static final String SYMBOL = "Symbol";
    public static final String TIMES_ROMAN = "Times-Roman";
    public static final String TIMES_BOLD = "Times-Bold";
    public static final String TIMES_ITALIC = "Times-Italic";
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
    public static final String ZAPFDINGBATS = "ZapfDingbats";
    public static final int ASCENT = 1;
    public static final int CAPHEIGHT = 2;
    public static final int DESCENT = 3;
    public static final int ITALICANGLE = 4;
    public static final int BBOXLLX = 5;
    public static final int BBOXLLY = 6;
    public static final int BBOXURX = 7;
    public static final int BBOXURY = 8;
    public static final int AWT_ASCENT = 9;
    public static final int AWT_DESCENT = 10;
    public static final int AWT_LEADING = 11;
    public static final int AWT_MAXADVANCE = 12;
    public static final int UNDERLINE_POSITION = 13;
    public static final int UNDERLINE_THICKNESS = 14;
    public static final int STRIKETHROUGH_POSITION = 15;
    public static final int STRIKETHROUGH_THICKNESS = 16;
    public static final int SUBSCRIPT_SIZE = 17;
    public static final int SUBSCRIPT_OFFSET = 18;
    public static final int SUPERSCRIPT_SIZE = 19;
    public static final int SUPERSCRIPT_OFFSET = 20;
    public static final int FONT_TYPE_T1 = 0;
    public static final int FONT_TYPE_TT = 1;
    public static final int FONT_TYPE_CJK = 2;
    public static final int FONT_TYPE_TTUNI = 3;
    public static final int FONT_TYPE_DOCUMENT = 4;
    public static final int FONT_TYPE_T3 = 5;
    public static final String IDENTITY_H = "Identity-H";
    public static final String IDENTITY_V = "Identity-V";
    public static final String CP1250 = "Cp1250";
    public static final String CP1252 = "Cp1252";
    public static final String CP1257 = "Cp1257";
    public static final String WINANSI = "Cp1252";
    public static final String MACROMAN = "MacRoman";
    public static final int[] CHAR_RANGE_LATIN = new int[]{0, 383, 8192, 8303, 8352, 8399, 64256, 64262};
    public static final int[] CHAR_RANGE_ARABIC = new int[]{0, 127, 1536, 1663, 8352, 8399, 64336, 64511, 65136, 65279};
    public static final int[] CHAR_RANGE_HEBREW = new int[]{0, 127, 1424, 1535, 8352, 8399, 64285, 64335};
    public static final int[] CHAR_RANGE_CYRILLIC = new int[]{0, 127, 1024, 1327, 8192, 8303, 8352, 8399};
    public static final boolean EMBEDDED = true;
    public static final boolean NOT_EMBEDDED = false;
    public static final boolean CACHED = true;
    public static final boolean NOT_CACHED = false;
    public static final String RESOURCE_PATH = "com/lowagie/text/pdf/fonts/";
    public static final char CID_NEWLINE = '\u7fff';
    protected ArrayList subsetRanges;
    int fontType;
    public static final String notdef = ".notdef";
    protected int[] widths = new int[256];
    protected String[] differences = new String[256];
    protected char[] unicodeDifferences = new char[256];
    protected int[][] charBBoxes = new int[256][];
    protected String encoding;
    protected boolean embedded;
    protected int compressionLevel = -1;
    protected boolean fontSpecific = true;
    protected static HashMap fontCache = new HashMap<K, V>();
    protected static final HashMap BuiltinFonts14 = new HashMap<K, V>();
    protected boolean forceWidthsOutput = false;
    protected boolean directTextToByte = false;
    protected boolean subset = true;
    protected boolean fastWinansi = false;
    protected IntHashtable specialMap;

    protected BaseFont() {
    }

    public static BaseFont createFont() throws DocumentException, IOException {
        return BaseFont.createFont("Helvetica", "Cp1252", false);
    }

    public static BaseFont createFont(String string, String string2, boolean bl) throws DocumentException, IOException {
        return BaseFont.createFont(string, string2, bl, true, null, null);
    }

    public static BaseFont createFont(String string, String string2, boolean bl, boolean bl2, byte[] arrby, byte[] arrby2) throws DocumentException, IOException {
        return BaseFont.createFont(string, string2, bl, bl2, arrby, arrby2, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BaseFont createFont(String string, String string2, boolean bl, boolean bl2, byte[] arrby, byte[] arrby2, boolean bl3) throws DocumentException, IOException {
        HashMap hashMap;
        boolean bl4;
        String string3 = BaseFont.getBaseName(string);
        string2 = BaseFont.normalizeEncoding(string2);
        boolean bl5 = BuiltinFonts14.containsKey(string);
        boolean bl6 = bl4 = bl5 ? false : CJKFont.isCJKFont(string3, string2);
        if (bl5 || bl4) {
            bl = false;
        } else if (string2.equals("Identity-H") || string2.equals("Identity-V")) {
            bl = true;
        }
        BaseFont baseFont = null;
        BaseFont baseFont2 = null;
        String string4 = string + "\n" + string2 + "\n" + bl;
        if (bl2) {
            hashMap = fontCache;
            synchronized (hashMap) {
                baseFont = (BaseFont)fontCache.get(string4);
            }
            if (baseFont != null) {
                return baseFont;
            }
        }
        if (bl5 || string.toLowerCase().endsWith(".afm") || string.toLowerCase().endsWith(".pfm")) {
            baseFont2 = new Type1Font(string, string2, bl, arrby, arrby2);
            baseFont2.fastWinansi = string2.equals("Cp1252");
        } else if (string3.toLowerCase().endsWith(".ttf") || string3.toLowerCase().endsWith(".otf") || string3.toLowerCase().indexOf(".ttc,") > 0) {
            if (string2.equals("Identity-H") || string2.equals("Identity-V")) {
                baseFont2 = new TrueTypeFontUnicode(string, string2, bl, arrby);
            } else {
                baseFont2 = new TrueTypeFont(string, string2, bl, arrby);
                baseFont2.fastWinansi = string2.equals("Cp1252");
            }
        } else if (bl4) {
            baseFont2 = new CJKFont(string, string2, bl);
        } else {
            if (bl3) {
                return null;
            }
            throw new DocumentException("Font '" + string + "' with '" + string2 + "' is not recognized.");
        }
        if (bl2) {
            hashMap = fontCache;
            synchronized (hashMap) {
                baseFont = (BaseFont)fontCache.get(string4);
                if (baseFont != null) {
                    return baseFont;
                }
                fontCache.put(string4, baseFont2);
            }
        }
        return baseFont2;
    }

    public static BaseFont createFont(PRIndirectReference pRIndirectReference) {
        return new DocumentFont(pRIndirectReference);
    }

    protected static String getBaseName(String string) {
        if (string.endsWith(",Bold")) {
            return string.substring(0, string.length() - 5);
        }
        if (string.endsWith(",Italic")) {
            return string.substring(0, string.length() - 7);
        }
        if (string.endsWith(",BoldItalic")) {
            return string.substring(0, string.length() - 11);
        }
        return string;
    }

    protected static String normalizeEncoding(String string) {
        if (string.equals("winansi") || string.equals("")) {
            return "Cp1252";
        }
        if (string.equals("macroman")) {
            return "MacRoman";
        }
        return string;
    }

    protected void createEncoding() {
        if (this.encoding.startsWith("#")) {
            reference var2_4;
            int n;
            String string;
            this.specialMap = new IntHashtable();
            StringTokenizer stringTokenizer = new StringTokenizer(this.encoding.substring(1), " ,\t\n\r\f");
            if (stringTokenizer.nextToken().equals("full")) {
                while (stringTokenizer.hasMoreTokens()) {
                    var2_4 = (reference)stringTokenizer.nextToken();
                    string = stringTokenizer.nextToken();
                    n = Integer.parseInt(stringTokenizer.nextToken(), 16);
                    int n2 = var2_4.startsWith("'") ? (int)var2_4.charAt(1) : Integer.parseInt(var2_4);
                    this.specialMap.put(n, n2 %= 256);
                    this.differences[n2] = string;
                    this.unicodeDifferences[n2] = n;
                    this.widths[n2] = this.getRawWidth(n, string);
                    this.charBBoxes[n2] = this.getRawCharBBox(n, string);
                }
            } else {
                void var2_7;
                boolean n3 = false;
                if (stringTokenizer.hasMoreTokens()) {
                    int string3 = Integer.parseInt(stringTokenizer.nextToken());
                }
                while (stringTokenizer.hasMoreTokens() && ++var2_7 < 256) {
                    string = stringTokenizer.nextToken();
                    n = Integer.parseInt(string, 16) % 65536;
                    String string2 = GlyphList.unicodeToName(n);
                    if (string2 == null) continue;
                    this.specialMap.put(n, (int)var2_7);
                    this.differences[var2_7] = string2;
                    this.unicodeDifferences[var2_7] = (char)n;
                    this.widths[var2_7] = this.getRawWidth(n, string2);
                    this.charBBoxes[var2_7] = this.getRawCharBBox(n, string2);
                }
            }
            for (var2_4 = false; var2_4 < 256; ++var2_4) {
                if (this.differences[var2_4] != null) continue;
                this.differences[var2_4] = ".notdef";
            }
        } else if (this.fontSpecific) {
            for (int i = 0; i < 256; ++i) {
                this.widths[i] = this.getRawWidth(i, null);
                this.charBBoxes[i] = this.getRawCharBBox(i, null);
            }
        } else {
            byte[] arrby = new byte[1];
            for (int i = 0; i < 256; ++i) {
                void var2_8;
                arrby[0] = (byte)i;
                String string = PdfEncodings.convertToString(arrby, this.encoding);
                int n = string.length() > 0 ? (int)string.charAt(0) : 63;
                String string3 = GlyphList.unicodeToName(n);
                if (string3 == null) {
                    String string4 = ".notdef";
                }
                this.differences[i] = var2_8;
                this.unicodeDifferences[i] = n;
                this.widths[i] = this.getRawWidth(n, (String)var2_8);
                this.charBBoxes[i] = this.getRawCharBBox(n, (String)var2_8);
            }
        }
    }

    abstract int getRawWidth(int var1, String var2);

    public abstract int getKerning(int var1, int var2);

    public abstract boolean setKerning(int var1, int var2, int var3);

    public int getWidth(int n) {
        if (this.fastWinansi) {
            if (n < 128 || n >= 160 && n <= 255) {
                return this.widths[n];
            }
            return this.widths[PdfEncodings.winansi.get(n)];
        }
        int n2 = 0;
        byte[] arrby = this.convertToBytes((char)n);
        for (int i = 0; i < arrby.length; ++i) {
            n2 += this.widths[255 & arrby[i]];
        }
        return n2;
    }

    public int getWidth(String string) {
        int n = 0;
        if (this.fastWinansi) {
            int n2 = string.length();
            for (int i = 0; i < n2; ++i) {
                char c = string.charAt(i);
                if (c < '' || c >= '\u00a0' && c <= '\u00ff') {
                    n += this.widths[c];
                    continue;
                }
                n += this.widths[PdfEncodings.winansi.get(c)];
            }
            return n;
        }
        byte[] arrby = this.convertToBytes(string);
        for (int i = 0; i < arrby.length; ++i) {
            n += this.widths[255 & arrby[i]];
        }
        return n;
    }

    public int getDescent(String string) {
        int n = 0;
        char[] arrc = string.toCharArray();
        for (int i = 0; i < arrc.length; ++i) {
            int[] arrn = this.getCharBBox(arrc[i]);
            if (arrn == null || arrn[1] >= n) continue;
            n = arrn[1];
        }
        return n;
    }

    public int getAscent(String string) {
        int n = 0;
        char[] arrc = string.toCharArray();
        for (int i = 0; i < arrc.length; ++i) {
            int[] arrn = this.getCharBBox(arrc[i]);
            if (arrn == null || arrn[3] <= n) continue;
            n = arrn[3];
        }
        return n;
    }

    public float getDescentPoint(String string, float f) {
        return (float)this.getDescent(string) * 0.001f * f;
    }

    public float getAscentPoint(String string, float f) {
        return (float)this.getAscent(string) * 0.001f * f;
    }

    public float getWidthPointKerned(String string, float f) {
        float f2 = (float)this.getWidth(string) * 0.001f * f;
        if (!this.hasKernPairs()) {
            return f2;
        }
        int n = string.length() - 1;
        int n2 = 0;
        char[] arrc = string.toCharArray();
        for (int i = 0; i < n; ++i) {
            n2 += this.getKerning(arrc[i], arrc[i + 1]);
        }
        return f2 + (float)n2 * 0.001f * f;
    }

    public float getWidthPoint(String string, float f) {
        return (float)this.getWidth(string) * 0.001f * f;
    }

    public float getWidthPoint(int n, float f) {
        return (float)this.getWidth(n) * 0.001f * f;
    }

    byte[] convertToBytes(String string) {
        if (this.directTextToByte) {
            return PdfEncodings.convertToBytes(string, null);
        }
        if (this.specialMap != null) {
            byte[] arrby = new byte[string.length()];
            int n = 0;
            int n2 = string.length();
            for (int i = 0; i < n2; ++i) {
                char c = string.charAt(i);
                if (!this.specialMap.containsKey(c)) continue;
                arrby[n++] = (byte)this.specialMap.get(c);
            }
            if (n < n2) {
                byte[] arrby2 = new byte[n];
                System.arraycopy(arrby, 0, arrby2, 0, n);
                return arrby2;
            }
            return arrby;
        }
        return PdfEncodings.convertToBytes(string, this.encoding);
    }

    byte[] convertToBytes(int n) {
        if (this.directTextToByte) {
            return PdfEncodings.convertToBytes((char)n, null);
        }
        if (this.specialMap != null) {
            if (this.specialMap.containsKey(n)) {
                return new byte[]{(byte)this.specialMap.get(n)};
            }
            return new byte[0];
        }
        return PdfEncodings.convertToBytes((char)n, this.encoding);
    }

    abstract void writeFont(PdfWriter var1, PdfIndirectReference var2, Object[] var3) throws DocumentException, IOException;

    abstract PdfStream getFullFontStream() throws IOException, DocumentException;

    public String getEncoding() {
        return this.encoding;
    }

    public abstract float getFontDescriptor(int var1, float var2);

    public int getFontType() {
        return this.fontType;
    }

    public boolean isEmbedded() {
        return this.embedded;
    }

    public boolean isFontSpecific() {
        return this.fontSpecific;
    }

    public static String createSubsetPrefix() {
        String string = "";
        for (int i = 0; i < 6; ++i) {
            string = string + (char)(Math.random() * 26.0 + 65.0);
        }
        return string + "+";
    }

    char getUnicodeDifferences(int n) {
        return this.unicodeDifferences[n];
    }

    public abstract String getPostscriptFontName();

    public abstract void setPostscriptFontName(String var1);

    public abstract String[][] getFullFontName();

    public abstract String[][] getAllNameEntries();

    public static String[][] getFullFontName(String string, String string2, byte[] arrby) throws DocumentException, IOException {
        String string3 = BaseFont.getBaseName(string);
        BaseFont baseFont = null;
        baseFont = string3.toLowerCase().endsWith(".ttf") || string3.toLowerCase().endsWith(".otf") || string3.toLowerCase().indexOf(".ttc,") > 0 ? new TrueTypeFont(string, "Cp1252", false, arrby, true) : BaseFont.createFont(string, string2, false, false, arrby, null);
        return baseFont.getFullFontName();
    }

    public static Object[] getAllFontNames(String string, String string2, byte[] arrby) throws DocumentException, IOException {
        String string3 = BaseFont.getBaseName(string);
        BaseFont baseFont = null;
        baseFont = string3.toLowerCase().endsWith(".ttf") || string3.toLowerCase().endsWith(".otf") || string3.toLowerCase().indexOf(".ttc,") > 0 ? new TrueTypeFont(string, "Cp1252", false, arrby, true) : BaseFont.createFont(string, string2, false, false, arrby, null);
        return new Object[]{baseFont.getPostscriptFontName(), baseFont.getFamilyFontName(), baseFont.getFullFontName()};
    }

    public static String[][] getAllNameEntries(String string, String string2, byte[] arrby) throws DocumentException, IOException {
        String string3 = BaseFont.getBaseName(string);
        BaseFont baseFont = null;
        baseFont = string3.toLowerCase().endsWith(".ttf") || string3.toLowerCase().endsWith(".otf") || string3.toLowerCase().indexOf(".ttc,") > 0 ? new TrueTypeFont(string, "Cp1252", false, arrby, true) : BaseFont.createFont(string, string2, false, false, arrby, null);
        return baseFont.getAllNameEntries();
    }

    public abstract String[][] getFamilyFontName();

    public String[] getCodePagesSupported() {
        return new String[0];
    }

    public static String[] enumerateTTCNames(String string) throws DocumentException, IOException {
        return new EnumerateTTC(string).getNames();
    }

    public static String[] enumerateTTCNames(byte[] arrby) throws DocumentException, IOException {
        return new EnumerateTTC(arrby).getNames();
    }

    public int[] getWidths() {
        return this.widths;
    }

    public String[] getDifferences() {
        return this.differences;
    }

    public char[] getUnicodeDifferences() {
        return this.unicodeDifferences;
    }

    public boolean isForceWidthsOutput() {
        return this.forceWidthsOutput;
    }

    public void setForceWidthsOutput(boolean bl) {
        this.forceWidthsOutput = bl;
    }

    public boolean isDirectTextToByte() {
        return this.directTextToByte;
    }

    public void setDirectTextToByte(boolean bl) {
        this.directTextToByte = bl;
    }

    public boolean isSubset() {
        return this.subset;
    }

    public void setSubset(boolean bl) {
        this.subset = bl;
    }

    public static InputStream getResourceStream(String string) {
        return BaseFont.getResourceStream(string, null);
    }

    public static InputStream getResourceStream(String string, ClassLoader classLoader) {
        if (string.startsWith("/")) {
            string = string.substring(1);
        }
        InputStream inputStream = null;
        if (classLoader != null && (inputStream = classLoader.getResourceAsStream(string)) != null) {
            return inputStream;
        }
        try {
            ClassLoader classLoader2 = Thread.currentThread().getContextClassLoader();
            if (classLoader2 != null) {
                inputStream = classLoader2.getResourceAsStream(string);
            }
        }
        catch (Throwable var3_4) {
            // empty catch block
        }
        if (inputStream == null) {
            Class class_ = BaseFont.class;
            inputStream = class_.getResourceAsStream("/" + string);
        }
        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(string);
        }
        return inputStream;
    }

    public int getUnicodeEquivalent(int n) {
        return n;
    }

    public int getCidCode(int n) {
        return n;
    }

    public abstract boolean hasKernPairs();

    public boolean charExists(int n) {
        byte[] arrby = this.convertToBytes(n);
        return arrby.length > 0;
    }

    public boolean setCharAdvance(int n, int n2) {
        byte[] arrby = this.convertToBytes(n);
        if (arrby.length == 0) {
            return false;
        }
        this.widths[255 & arrby[0]] = n2;
        return true;
    }

    private static void addFont(PRIndirectReference pRIndirectReference, IntHashtable intHashtable, ArrayList arrayList) {
        PdfObject pdfObject = PdfReader.getPdfObject(pRIndirectReference);
        if (pdfObject == null || !pdfObject.isDictionary()) {
            return;
        }
        PdfDictionary pdfDictionary = (PdfDictionary)pdfObject;
        PdfName pdfName = (PdfName)PdfReader.getPdfObject(pdfDictionary.get(PdfName.SUBTYPE));
        if (!PdfName.TYPE1.equals(pdfName) && !PdfName.TRUETYPE.equals(pdfName)) {
            return;
        }
        PdfName pdfName2 = (PdfName)PdfReader.getPdfObject(pdfDictionary.get(PdfName.BASEFONT));
        arrayList.add(new Object[]{PdfName.decodeName(pdfName2.toString()), pRIndirectReference});
        intHashtable.put(pRIndirectReference.getNumber(), 1);
    }

    private static void recourseFonts(PdfDictionary pdfDictionary, IntHashtable intHashtable, ArrayList arrayList, int n) {
        Object object;
        Object object2;
        if (++n > 50) {
            return;
        }
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.RESOURCES));
        if (pdfDictionary2 == null) {
            return;
        }
        PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.FONT));
        if (pdfDictionary3 != null) {
            object2 = pdfDictionary3.getKeys().iterator();
            while (object2.hasNext()) {
                int n2;
                object = pdfDictionary3.get((PdfName)object2.next());
                if (object == null || !object.isIndirect() || intHashtable.containsKey(n2 = ((PRIndirectReference)object).getNumber())) continue;
                BaseFont.addFont((PRIndirectReference)object, intHashtable, arrayList);
            }
        }
        if ((object2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.XOBJECT))) != null) {
            object = object2.getKeys().iterator();
            while (object.hasNext()) {
                BaseFont.recourseFonts((PdfDictionary)PdfReader.getPdfObject(object2.get((PdfName)object.next())), intHashtable, arrayList, n);
            }
        }
    }

    public static ArrayList getDocumentFonts(PdfReader pdfReader) {
        IntHashtable intHashtable = new IntHashtable();
        ArrayList<E> arrayList = new ArrayList<E>();
        int n = pdfReader.getNumberOfPages();
        for (int i = 1; i <= n; ++i) {
            BaseFont.recourseFonts(pdfReader.getPageN(i), intHashtable, arrayList, 1);
        }
        return arrayList;
    }

    public static ArrayList getDocumentFonts(PdfReader pdfReader, int n) {
        IntHashtable intHashtable = new IntHashtable();
        ArrayList<E> arrayList = new ArrayList<E>();
        BaseFont.recourseFonts(pdfReader.getPageN(n), intHashtable, arrayList, 1);
        return arrayList;
    }

    public int[] getCharBBox(int n) {
        byte[] arrby = this.convertToBytes(n);
        if (arrby.length == 0) {
            return null;
        }
        return this.charBBoxes[arrby[0] & 255];
    }

    protected abstract int[] getRawCharBBox(int var1, String var2);

    public void correctArabicAdvance() {
        int n;
        for (n = 1611; n <= 1624; n = (int)((char)(n + 1))) {
            this.setCharAdvance(n, 0);
        }
        this.setCharAdvance(1648, 0);
        for (n = 1750; n <= 1756; n = (int)((char)(n + 1))) {
            this.setCharAdvance(n, 0);
        }
        for (n = 1759; n <= 1764; n = (int)((char)(n + 1))) {
            this.setCharAdvance(n, 0);
        }
        for (n = 1767; n <= 1768; n = (int)((char)(n + 1))) {
            this.setCharAdvance(n, 0);
        }
        for (n = 1770; n <= 1773; n = (int)((char)(n + 1))) {
            this.setCharAdvance(n, 0);
        }
    }

    public void addSubsetRange(int[] arrn) {
        if (this.subsetRanges == null) {
            this.subsetRanges = new ArrayList<E>();
        }
        this.subsetRanges.add(arrn);
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int n) {
        this.compressionLevel = n < 0 || n > 9 ? -1 : n;
    }

    static {
        BuiltinFonts14.put("Courier", PdfName.COURIER);
        BuiltinFonts14.put("Courier-Bold", PdfName.COURIER_BOLD);
        BuiltinFonts14.put("Courier-BoldOblique", PdfName.COURIER_BOLDOBLIQUE);
        BuiltinFonts14.put("Courier-Oblique", PdfName.COURIER_OBLIQUE);
        BuiltinFonts14.put("Helvetica", PdfName.HELVETICA);
        BuiltinFonts14.put("Helvetica-Bold", PdfName.HELVETICA_BOLD);
        BuiltinFonts14.put("Helvetica-BoldOblique", PdfName.HELVETICA_BOLDOBLIQUE);
        BuiltinFonts14.put("Helvetica-Oblique", PdfName.HELVETICA_OBLIQUE);
        BuiltinFonts14.put("Symbol", PdfName.SYMBOL);
        BuiltinFonts14.put("Times-Roman", PdfName.TIMES_ROMAN);
        BuiltinFonts14.put("Times-Bold", PdfName.TIMES_BOLD);
        BuiltinFonts14.put("Times-BoldItalic", PdfName.TIMES_BOLDITALIC);
        BuiltinFonts14.put("Times-Italic", PdfName.TIMES_ITALIC);
        BuiltinFonts14.put("ZapfDingbats", PdfName.ZAPFDINGBATS);
    }

    static class StreamFont
    extends PdfStream {
        public StreamFont(byte[] arrby, int[] arrn, int n) throws DocumentException {
            try {
                this.bytes = arrby;
                this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                for (int i = 0; i < arrn.length; ++i) {
                    this.put(new PdfName("Length" + (i + 1)), new PdfNumber(arrn[i]));
                }
                this.flateCompress(n);
            }
            catch (Exception var4_5) {
                throw new DocumentException(var4_5);
            }
        }

        public StreamFont(byte[] arrby, String string, int n) throws DocumentException {
            try {
                this.bytes = arrby;
                this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                if (string != null) {
                    this.put(PdfName.SUBTYPE, new PdfName(string));
                }
                this.flateCompress(n);
            }
            catch (Exception var4_4) {
                throw new DocumentException(var4_4);
            }
        }
    }

}

