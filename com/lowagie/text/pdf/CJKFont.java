/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

class CJKFont
extends BaseFont {
    static final String CJK_ENCODING = "UnicodeBigUnmarked";
    private static final int FIRST = 0;
    private static final int BRACKET = 1;
    private static final int SERIAL = 2;
    private static final int V1Y = 880;
    static Properties cjkFonts = new Properties();
    static Properties cjkEncodings = new Properties();
    static Hashtable allCMaps = new Hashtable();
    static Hashtable allFonts = new Hashtable();
    private static boolean propertiesLoaded = false;
    private String fontName;
    private String style = "";
    private String CMap;
    private boolean cidDirect = false;
    private char[] translationMap;
    private IntHashtable vMetrics;
    private IntHashtable hMetrics;
    private HashMap fontDesc;
    private boolean vertical = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadProperties() {
        if (propertiesLoaded) {
            return;
        }
        Hashtable hashtable = allFonts;
        synchronized (hashtable) {
            if (propertiesLoaded) {
                return;
            }
            try {
                InputStream inputStream = CJKFont.getResourceStream("com/lowagie/text/pdf/fonts/cjkfonts.properties");
                cjkFonts.load(inputStream);
                inputStream.close();
                inputStream = CJKFont.getResourceStream("com/lowagie/text/pdf/fonts/cjkencodings.properties");
                cjkEncodings.load(inputStream);
                inputStream.close();
            }
            catch (Exception var1_2) {
                cjkFonts = new Properties();
                cjkEncodings = new Properties();
            }
            propertiesLoaded = true;
        }
    }

    CJKFont(String string, String string2, boolean bl) throws DocumentException {
        CJKFont.loadProperties();
        this.fontType = 2;
        String string3 = CJKFont.getBaseName(string);
        if (!CJKFont.isCJKFont(string3, string2)) {
            throw new DocumentException("Font '" + string + "' with '" + string2 + "' encoding is not a CJK font.");
        }
        if (string3.length() < string.length()) {
            this.style = string.substring(string3.length());
            string = string3;
        }
        this.fontName = string;
        this.encoding = "UnicodeBigUnmarked";
        this.vertical = string2.endsWith("V");
        this.CMap = string2;
        if (string2.startsWith("Identity-")) {
            this.cidDirect = true;
            String string4 = cjkFonts.getProperty(string);
            char[] arrc = (char[])allCMaps.get(string4 = string4.substring(0, string4.indexOf(95)));
            if (arrc == null) {
                arrc = CJKFont.readCMap(string4);
                if (arrc == null) {
                    throw new DocumentException("The cmap " + string4 + " does not exist as a resource.");
                }
                arrc[32767] = 10;
                allCMaps.put(string4, arrc);
            }
            this.translationMap = arrc;
        } else {
            char[] arrc = (char[])allCMaps.get(string2);
            if (arrc == null) {
                String string5 = cjkEncodings.getProperty(string2);
                if (string5 == null) {
                    throw new DocumentException("The resource cjkencodings.properties does not contain the encoding " + string2);
                }
                StringTokenizer stringTokenizer = new StringTokenizer(string5);
                String string6 = stringTokenizer.nextToken();
                arrc = (char[])allCMaps.get(string6);
                if (arrc == null) {
                    arrc = CJKFont.readCMap(string6);
                    allCMaps.put(string6, arrc);
                }
                if (stringTokenizer.hasMoreTokens()) {
                    String string7 = stringTokenizer.nextToken();
                    char[] arrc2 = CJKFont.readCMap(string7);
                    for (int i = 0; i < 65536; ++i) {
                        if (arrc2[i] != '\u0000') continue;
                        arrc2[i] = arrc[i];
                    }
                    allCMaps.put(string2, arrc2);
                    arrc = arrc2;
                }
            }
            this.translationMap = arrc;
        }
        this.fontDesc = (HashMap)allFonts.get(string);
        if (this.fontDesc == null) {
            this.fontDesc = CJKFont.readFontProperties(string);
            allFonts.put(string, this.fontDesc);
        }
        this.hMetrics = (IntHashtable)this.fontDesc.get("W");
        this.vMetrics = (IntHashtable)this.fontDesc.get("W2");
    }

    public static boolean isCJKFont(String string, String string2) {
        CJKFont.loadProperties();
        String string3 = cjkFonts.getProperty(string);
        return string3 != null && (string2.equals("Identity-H") || string2.equals("Identity-V") || string3.indexOf("_" + string2 + "_") >= 0);
    }

    public int getWidth(int n) {
        int n2 = n;
        if (!this.cidDirect) {
            n2 = this.translationMap[n2];
        }
        int n3 = this.vertical ? this.vMetrics.get(n2) : this.hMetrics.get(n2);
        if (n3 > 0) {
            return n3;
        }
        return 1000;
    }

    public int getWidth(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            int n2;
            char c = string.charAt(i);
            if (!this.cidDirect) {
                c = this.translationMap[c];
            }
            if ((n2 = this.vertical ? this.vMetrics.get(c) : this.hMetrics.get(c)) > 0) {
                n += n2;
                continue;
            }
            n += 1000;
        }
        return n;
    }

    int getRawWidth(int n, String string) {
        return 0;
    }

    public int getKerning(int n, int n2) {
        return 0;
    }

    private PdfDictionary getFontDescriptor() {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        pdfDictionary.put(PdfName.ASCENT, new PdfLiteral((String)this.fontDesc.get("Ascent")));
        pdfDictionary.put(PdfName.CAPHEIGHT, new PdfLiteral((String)this.fontDesc.get("CapHeight")));
        pdfDictionary.put(PdfName.DESCENT, new PdfLiteral((String)this.fontDesc.get("Descent")));
        pdfDictionary.put(PdfName.FLAGS, new PdfLiteral((String)this.fontDesc.get("Flags")));
        pdfDictionary.put(PdfName.FONTBBOX, new PdfLiteral((String)this.fontDesc.get("FontBBox")));
        pdfDictionary.put(PdfName.FONTNAME, new PdfName(this.fontName + this.style));
        pdfDictionary.put(PdfName.ITALICANGLE, new PdfLiteral((String)this.fontDesc.get("ItalicAngle")));
        pdfDictionary.put(PdfName.STEMV, new PdfLiteral((String)this.fontDesc.get("StemV")));
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.PANOSE, new PdfString((String)this.fontDesc.get("Panose"), null));
        pdfDictionary.put(PdfName.STYLE, pdfDictionary2);
        return pdfDictionary;
    }

    private PdfDictionary getCIDFont(PdfIndirectReference pdfIndirectReference, IntHashtable intHashtable) {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONT);
        pdfDictionary.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
        pdfDictionary.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
        pdfDictionary.put(PdfName.FONTDESCRIPTOR, pdfIndirectReference);
        int[] arrn = intHashtable.toOrderedKeys();
        String string = CJKFont.convertToHCIDMetrics(arrn, this.hMetrics);
        if (string != null) {
            pdfDictionary.put(PdfName.W, new PdfLiteral(string));
        }
        if (this.vertical) {
            string = CJKFont.convertToVCIDMetrics(arrn, this.vMetrics, this.hMetrics);
            if (string != null) {
                pdfDictionary.put(PdfName.W2, new PdfLiteral(string));
            }
        } else {
            pdfDictionary.put(PdfName.DW, new PdfNumber(1000));
        }
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.REGISTRY, new PdfString((String)this.fontDesc.get("Registry"), null));
        pdfDictionary2.put(PdfName.ORDERING, new PdfString((String)this.fontDesc.get("Ordering"), null));
        pdfDictionary2.put(PdfName.SUPPLEMENT, new PdfLiteral((String)this.fontDesc.get("Supplement")));
        pdfDictionary.put(PdfName.CIDSYSTEMINFO, pdfDictionary2);
        return pdfDictionary;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference pdfIndirectReference) {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONT);
        pdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE0);
        String string = this.fontName;
        if (this.style.length() > 0) {
            string = string + "-" + this.style.substring(1);
        }
        string = string + "-" + this.CMap;
        pdfDictionary.put(PdfName.BASEFONT, new PdfName(string));
        pdfDictionary.put(PdfName.ENCODING, new PdfName(this.CMap));
        pdfDictionary.put(PdfName.DESCENDANTFONTS, new PdfArray(pdfIndirectReference));
        return pdfDictionary;
    }

    void writeFont(PdfWriter pdfWriter, PdfIndirectReference pdfIndirectReference, Object[] arrobject) throws DocumentException, IOException {
        IntHashtable intHashtable = (IntHashtable)arrobject[0];
        PdfIndirectReference pdfIndirectReference2 = null;
        PdfDictionary pdfDictionary = null;
        PdfIndirectObject pdfIndirectObject = null;
        pdfDictionary = this.getFontDescriptor();
        if (pdfDictionary != null) {
            pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
            pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        }
        if ((pdfDictionary = this.getCIDFont(pdfIndirectReference2, intHashtable)) != null) {
            pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
            pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        }
        pdfDictionary = this.getFontBaseType(pdfIndirectReference2);
        pdfWriter.addToBody((PdfObject)pdfDictionary, pdfIndirectReference);
    }

    public PdfStream getFullFontStream() {
        return null;
    }

    private float getDescNumber(String string) {
        return Integer.parseInt((String)this.fontDesc.get(string));
    }

    private float getBBox(int n) {
        String string = (String)this.fontDesc.get("FontBBox");
        StringTokenizer stringTokenizer = new StringTokenizer(string, " []\r\n\t\f");
        String string2 = stringTokenizer.nextToken();
        for (int i = 0; i < n; ++i) {
            string2 = stringTokenizer.nextToken();
        }
        return Integer.parseInt(string2);
    }

    public float getFontDescriptor(int n, float f) {
        switch (n) {
            case 1: 
            case 9: {
                return this.getDescNumber("Ascent") * f / 1000.0f;
            }
            case 2: {
                return this.getDescNumber("CapHeight") * f / 1000.0f;
            }
            case 3: 
            case 10: {
                return this.getDescNumber("Descent") * f / 1000.0f;
            }
            case 4: {
                return this.getDescNumber("ItalicAngle");
            }
            case 5: {
                return f * this.getBBox(0) / 1000.0f;
            }
            case 6: {
                return f * this.getBBox(1) / 1000.0f;
            }
            case 7: {
                return f * this.getBBox(2) / 1000.0f;
            }
            case 8: {
                return f * this.getBBox(3) / 1000.0f;
            }
            case 11: {
                return 0.0f;
            }
            case 12: {
                return f * (this.getBBox(2) - this.getBBox(0)) / 1000.0f;
            }
        }
        return 0.0f;
    }

    public String getPostscriptFontName() {
        return this.fontName;
    }

    public String[][] getFullFontName() {
        return new String[][]{{"", "", "", this.fontName}};
    }

    public String[][] getAllNameEntries() {
        return new String[][]{{"4", "", "", "", this.fontName}};
    }

    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }

    static char[] readCMap(String string) {
        try {
            string = string + ".cmap";
            InputStream inputStream = CJKFont.getResourceStream("com/lowagie/text/pdf/fonts/" + string);
            char[] arrc = new char[65536];
            for (int i = 0; i < 65536; ++i) {
                arrc[i] = (char)((inputStream.read() << 8) + inputStream.read());
            }
            return arrc;
        }
        catch (Exception var1_2) {
            return null;
        }
    }

    static IntHashtable createMetric(String string) {
        IntHashtable intHashtable = new IntHashtable();
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        while (stringTokenizer.hasMoreTokens()) {
            int n = Integer.parseInt(stringTokenizer.nextToken());
            intHashtable.put(n, Integer.parseInt(stringTokenizer.nextToken()));
        }
        return intHashtable;
    }

    static String convertToHCIDMetrics(int[] arrn, IntHashtable intHashtable) {
        if (arrn.length == 0) {
            return null;
        }
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < arrn.length; ++i) {
            n = arrn[i];
            n2 = intHashtable.get(n);
            if (n2 == 0) continue;
            ++i;
            break;
        }
        if (n2 == 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        stringBuffer.append(n);
        int n3 = 0;
        for (int j = i; j < arrn.length; ++j) {
            int n4 = arrn[j];
            int n5 = intHashtable.get(n4);
            if (n5 == 0) continue;
            switch (n3) {
                case 0: {
                    if (n4 == n + 1 && n5 == n2) {
                        n3 = 2;
                        break;
                    }
                    if (n4 == n + 1) {
                        n3 = 1;
                        stringBuffer.append('[').append(n2);
                        break;
                    }
                    stringBuffer.append('[').append(n2).append(']').append(n4);
                    break;
                }
                case 1: {
                    if (n4 == n + 1 && n5 == n2) {
                        n3 = 2;
                        stringBuffer.append(']').append(n);
                        break;
                    }
                    if (n4 == n + 1) {
                        stringBuffer.append(' ').append(n2);
                        break;
                    }
                    n3 = 0;
                    stringBuffer.append(' ').append(n2).append(']').append(n4);
                    break;
                }
                case 2: {
                    if (n4 == n + 1 && n5 == n2) break;
                    stringBuffer.append(' ').append(n).append(' ').append(n2).append(' ').append(n4);
                    n3 = 0;
                }
            }
            n2 = n5;
            n = n4;
        }
        switch (n3) {
            case 0: {
                stringBuffer.append('[').append(n2).append("]]");
                break;
            }
            case 1: {
                stringBuffer.append(' ').append(n2).append("]]");
                break;
            }
            case 2: {
                stringBuffer.append(' ').append(n).append(' ').append(n2).append(']');
            }
        }
        return stringBuffer.toString();
    }

    static String convertToVCIDMetrics(int[] arrn, IntHashtable intHashtable, IntHashtable intHashtable2) {
        if (arrn.length == 0) {
            return null;
        }
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < arrn.length; ++i) {
            n = arrn[i];
            n2 = intHashtable.get(n);
            if (n2 != 0) {
                ++i;
                break;
            }
            n3 = intHashtable2.get(n);
        }
        if (n2 == 0) {
            return null;
        }
        if (n3 == 0) {
            n3 = 1000;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        stringBuffer.append(n);
        int n4 = 0;
        for (int j = i; j < arrn.length; ++j) {
            int n5 = arrn[j];
            int n6 = intHashtable.get(n5);
            if (n6 == 0) continue;
            int n7 = intHashtable2.get(n);
            if (n7 == 0) {
                n7 = 1000;
            }
            switch (n4) {
                case 0: {
                    if (n5 == n + 1 && n6 == n2 && n7 == n3) {
                        n4 = 2;
                        break;
                    }
                    stringBuffer.append(' ').append(n).append(' ').append(- n2).append(' ').append(n3 / 2).append(' ').append(880).append(' ').append(n5);
                    break;
                }
                case 2: {
                    if (n5 == n + 1 && n6 == n2 && n7 == n3) break;
                    stringBuffer.append(' ').append(n).append(' ').append(- n2).append(' ').append(n3 / 2).append(' ').append(880).append(' ').append(n5);
                    n4 = 0;
                }
            }
            n2 = n6;
            n = n5;
            n3 = n7;
        }
        stringBuffer.append(' ').append(n).append(' ').append(- n2).append(' ').append(n3 / 2).append(' ').append(880).append(" ]");
        return stringBuffer.toString();
    }

    static HashMap readFontProperties(String string) {
        try {
            string = string + ".properties";
            InputStream inputStream = CJKFont.getResourceStream("com/lowagie/text/pdf/fonts/" + string);
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            IntHashtable intHashtable = CJKFont.createMetric(properties.getProperty("W"));
            properties.remove("W");
            IntHashtable intHashtable2 = CJKFont.createMetric(properties.getProperty("W2"));
            properties.remove("W2");
            HashMap hashMap = new HashMap();
            Enumeration enumeration = properties.keys();
            while (enumeration.hasMoreElements()) {
                Object k = enumeration.nextElement();
                hashMap.put(k, properties.getProperty((String)k));
            }
            hashMap.put((String)"W", intHashtable);
            hashMap.put((String)"W2", intHashtable2);
            return hashMap;
        }
        catch (Exception var1_2) {
            return null;
        }
    }

    public int getUnicodeEquivalent(int n) {
        if (this.cidDirect) {
            return this.translationMap[n];
        }
        return n;
    }

    public int getCidCode(int n) {
        if (this.cidDirect) {
            return n;
        }
        return this.translationMap[n];
    }

    public boolean hasKernPairs() {
        return false;
    }

    public boolean charExists(int n) {
        return this.translationMap[n] != '\u0000';
    }

    public boolean setCharAdvance(int n, int n2) {
        return false;
    }

    public void setPostscriptFontName(String string) {
        this.fontName = string;
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

