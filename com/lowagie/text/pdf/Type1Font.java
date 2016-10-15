/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
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
import com.lowagie.text.pdf.Pfm2afm;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.fonts.FontsResourceAnchor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.StringTokenizer;

class Type1Font
extends BaseFont {
    private static FontsResourceAnchor resourceAnchor;
    protected byte[] pfb;
    private String FontName;
    private String FullName;
    private String FamilyName;
    private String Weight = "";
    private float ItalicAngle = 0.0f;
    private boolean IsFixedPitch = false;
    private String CharacterSet;
    private int llx = -50;
    private int lly = -200;
    private int urx = 1000;
    private int ury = 900;
    private int UnderlinePosition = -100;
    private int UnderlineThickness = 50;
    private String EncodingScheme = "FontSpecific";
    private int CapHeight = 700;
    private int XHeight = 480;
    private int Ascender = 800;
    private int Descender = -200;
    private int StdHW;
    private int StdVW = 80;
    private HashMap CharMetrics = new HashMap<K, V>();
    private HashMap KernPairs = new HashMap<K, V>();
    private String fileName;
    private boolean builtinFont = false;
    private static final int[] PFB_TYPES;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    Type1Font(String var1_1, String var2_2, boolean var3_3, byte[] var4_4, byte[] var5_5) throws DocumentException, IOException {
        super();
        if (var3_3 && var4_4 != null && var5_5 == null) {
            throw new DocumentException("Two byte arrays are needed if the Type1 font is embedded.");
        }
        if (var3_3 && var4_4 != null) {
            this.pfb = var5_5;
        }
        this.encoding = var2_2;
        this.embedded = var3_3;
        this.fileName = var1_1;
        this.fontType = 0;
        var6_6 = null;
        var7_7 = null;
        if (Type1Font.BuiltinFonts14.containsKey(var1_1)) {
            block34 : {
                this.embedded = false;
                this.builtinFont = true;
                var8_8 = new byte[1024];
                try {
                    if (Type1Font.resourceAnchor == null) {
                        Type1Font.resourceAnchor = new FontsResourceAnchor();
                    }
                    if ((var7_7 = Type1Font.getResourceStream("com/lowagie/text/pdf/fonts/" + var1_1 + ".afm", Type1Font.resourceAnchor.getClass().getClassLoader())) == null) {
                        var9_10 = var1_1 + " not found as resource. (The *.afm files must exist as resources in the package com.lowagie.text.pdf.fonts)";
                        System.err.println(var9_10);
                        throw new DocumentException(var9_10);
                    }
                    var9_11 = new ByteArrayOutputStream();
                    do {
                        if ((var10_12 = var7_7.read(var8_8)) < 0) {
                            var8_8 = var9_11.toByteArray();
                            var12_13 = null;
                            if (var7_7 != null) {
                                break;
                            }
                            break block34;
                        }
                        var9_11.write(var8_8, 0, var10_12);
                    } while (true);
                    try {
                        var7_7.close();
                    }
                    catch (Exception var13_15) {}
                }
                catch (Throwable var11_17) {
                    var12_14 = null;
                    if (var7_7 == null) throw var11_17;
                    ** try [egrp 1[TRYBLOCK] [2 : 383->391)] { 
lbl62: // 1 sources:
                    var7_7.close();
                    throw var11_17;
lbl64: // 1 sources:
                    catch (Exception var13_16) {
                        // empty catch block
                    }
                    throw var11_17;
                }
            }
            try {
                var6_6 = new RandomAccessFileOrArray(var8_8);
                this.process(var6_6);
                var15_18 = null;
                if (var6_6 == null) ** GOTO lbl137
                try {
                    var6_6.close();
                }
                catch (Exception var16_20) {}
            }
            catch (Throwable var14_22) {
                var15_19 = null;
                if (var6_6 == null) throw var14_22;
                ** try [egrp 3[TRYBLOCK] [5 : 433->441)] { 
lbl84: // 1 sources:
                var6_6.close();
                throw var14_22;
lbl86: // 1 sources:
                catch (Exception var16_21) {
                    // empty catch block
                }
                throw var14_22;
            }
        } else if (var1_1.toLowerCase().endsWith(".afm")) {
            try {
                var6_6 = var4_4 == null ? new RandomAccessFileOrArray(var1_1) : new RandomAccessFileOrArray(var4_4);
                this.process(var6_6);
                var18_23 = null;
                if (var6_6 == null) ** GOTO lbl137
                try {
                    var6_6.close();
                }
                catch (Exception var19_25) {}
            }
            catch (Throwable var17_27) {
                var18_24 = null;
                if (var6_6 == null) throw var17_27;
                ** try [egrp 5[TRYBLOCK] [8 : 513->521)] { 
lbl106: // 1 sources:
                var6_6.close();
                throw var17_27;
lbl108: // 1 sources:
                catch (Exception var19_26) {
                    // empty catch block
                }
                throw var17_27;
            }
        } else {
            if (var1_1.toLowerCase().endsWith(".pfm") == false) throw new DocumentException(var1_1 + " is not an AFM or PFM font file.");
            try {
                var8_9 = new ByteArrayOutputStream();
                var6_6 = var4_4 == null ? new RandomAccessFileOrArray(var1_1) : new RandomAccessFileOrArray(var4_4);
                Pfm2afm.convert(var6_6, var8_9);
                var6_6.close();
                var6_6 = new RandomAccessFileOrArray(var8_9.toByteArray());
                this.process(var6_6);
                var21_28 = null;
                if (var6_6 != null) {
                    try {
                        var6_6.close();
                    }
                    catch (Exception var22_30) {}
                }
            }
            catch (Throwable var20_32) {
                var21_29 = null;
                if (var6_6 == null) throw var20_32;
                ** try [egrp 7[TRYBLOCK] [11 : 628->636)] { 
lbl132: // 1 sources:
                var6_6.close();
                throw var20_32;
lbl134: // 1 sources:
                catch (Exception var22_31) {
                    // empty catch block
                }
                throw var20_32;
            }
        }
lbl137: // 8 sources:
        this.EncodingScheme = this.EncodingScheme.trim();
        if (this.EncodingScheme.equals("AdobeStandardEncoding") || this.EncodingScheme.equals("StandardEncoding")) {
            this.fontSpecific = false;
        }
        if (!this.encoding.startsWith("#")) {
            PdfEncodings.convertToBytes(" ", var2_2);
        }
        this.createEncoding();
    }

    int getRawWidth(int n, String string) {
        Object[] arrobject;
        if (string == null) {
            arrobject = (Object[])this.CharMetrics.get(new Integer(n));
        } else {
            if (string.equals(".notdef")) {
                return 0;
            }
            arrobject = (Object[])this.CharMetrics.get(string);
        }
        if (arrobject != null) {
            return (Integer)arrobject[1];
        }
        return 0;
    }

    public int getKerning(int n, int n2) {
        String string = GlyphList.unicodeToName(n);
        if (string == null) {
            return 0;
        }
        String string2 = GlyphList.unicodeToName(n2);
        if (string2 == null) {
            return 0;
        }
        Object[] arrobject = (Object[])this.KernPairs.get(string);
        if (arrobject == null) {
            return 0;
        }
        for (int i = 0; i < arrobject.length; i += 2) {
            if (!string2.equals(arrobject[i])) continue;
            return (Integer)arrobject[i + 1];
        }
        return 0;
    }

    public void process(RandomAccessFileOrArray randomAccessFileOrArray) throws DocumentException, IOException {
        Object object;
        Object object2;
        Object[] arrobject;
        Object object3;
        Object object4;
        String string;
        String string2;
        boolean bl = false;
        while ((string2 = randomAccessFileOrArray.readLine()) != null) {
            object = new Object[](string2, " ,\n\r\t\f");
            if (!object.hasMoreTokens()) continue;
            string = object.nextToken();
            if (string.equals("FontName")) {
                this.FontName = object.nextToken("\u00ff").substring(1);
                continue;
            }
            if (string.equals("FullName")) {
                this.FullName = object.nextToken("\u00ff").substring(1);
                continue;
            }
            if (string.equals("FamilyName")) {
                this.FamilyName = object.nextToken("\u00ff").substring(1);
                continue;
            }
            if (string.equals("Weight")) {
                this.Weight = object.nextToken("\u00ff").substring(1);
                continue;
            }
            if (string.equals("ItalicAngle")) {
                this.ItalicAngle = Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("IsFixedPitch")) {
                this.IsFixedPitch = object.nextToken().equals("true");
                continue;
            }
            if (string.equals("CharacterSet")) {
                this.CharacterSet = object.nextToken("\u00ff").substring(1);
                continue;
            }
            if (string.equals("FontBBox")) {
                this.llx = (int)Float.parseFloat(object.nextToken());
                this.lly = (int)Float.parseFloat(object.nextToken());
                this.urx = (int)Float.parseFloat(object.nextToken());
                this.ury = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("UnderlinePosition")) {
                this.UnderlinePosition = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("UnderlineThickness")) {
                this.UnderlineThickness = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("EncodingScheme")) {
                this.EncodingScheme = object.nextToken("\u00ff").substring(1);
                continue;
            }
            if (string.equals("CapHeight")) {
                this.CapHeight = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("XHeight")) {
                this.XHeight = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("Ascender")) {
                this.Ascender = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("Descender")) {
                this.Descender = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("StdHW")) {
                this.StdHW = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (string.equals("StdVW")) {
                this.StdVW = (int)Float.parseFloat(object.nextToken());
                continue;
            }
            if (!string.equals("StartCharMetrics")) continue;
            bl = true;
            break;
        }
        if (!bl) {
            throw new DocumentException("Missing StartCharMetrics in " + this.fileName);
        }
        while ((string2 = randomAccessFileOrArray.readLine()) != null) {
            Object[] arrobject2;
            object = new StringTokenizer(string2);
            if (!object.hasMoreTokens()) continue;
            string = object.nextToken();
            if (string.equals("EndCharMetrics")) {
                bl = false;
                break;
            }
            object3 = new Integer(-1);
            object2 = new Integer(250);
            object4 = "";
            arrobject = null;
            object = new StringTokenizer(string2, ";");
            while (object.hasMoreTokens()) {
                arrobject2 = new Object[](object.nextToken());
                if (!arrobject2.hasMoreTokens()) continue;
                string = arrobject2.nextToken();
                if (string.equals("C")) {
                    object3 = Integer.valueOf(arrobject2.nextToken());
                    continue;
                }
                if (string.equals("WX")) {
                    object2 = new Integer((int)Float.parseFloat(arrobject2.nextToken()));
                    continue;
                }
                if (string.equals("N")) {
                    object4 = arrobject2.nextToken();
                    continue;
                }
                if (!string.equals("B")) continue;
                arrobject = new int[]{Integer.parseInt(arrobject2.nextToken()), Integer.parseInt(arrobject2.nextToken()), Integer.parseInt(arrobject2.nextToken()), Integer.parseInt(arrobject2.nextToken())};
            }
            arrobject2 = new Object[]{object3, object2, object4, arrobject};
            if (object3.intValue() >= 0) {
                this.CharMetrics.put(object3, arrobject2);
            }
            this.CharMetrics.put(object4, arrobject2);
        }
        if (bl) {
            throw new DocumentException("Missing EndCharMetrics in " + this.fileName);
        }
        if (!this.CharMetrics.containsKey("nonbreakingspace") && (object = (Object[])this.CharMetrics.get("space")) != null) {
            this.CharMetrics.put("nonbreakingspace", object);
        }
        while ((string2 = randomAccessFileOrArray.readLine()) != null) {
            object = new StringTokenizer(string2);
            if (!object.hasMoreTokens()) continue;
            string = object.nextToken();
            if (string.equals("EndFontMetrics")) {
                return;
            }
            if (!string.equals("StartKernPairs")) continue;
            bl = true;
            break;
        }
        if (!bl) {
            throw new DocumentException("Missing EndFontMetrics in " + this.fileName);
        }
        while ((string2 = randomAccessFileOrArray.readLine()) != null) {
            object = new StringTokenizer(string2);
            if (!object.hasMoreTokens()) continue;
            string = object.nextToken();
            if (string.equals("KPX")) {
                object3 = object.nextToken();
                object2 = object.nextToken();
                object4 = new Integer((int)Float.parseFloat(object.nextToken()));
                arrobject = (Object[])this.KernPairs.get(object3);
                if (arrobject == null) {
                    this.KernPairs.put(object3, new Object[]{object2, object4});
                    continue;
                }
                int n = arrobject.length;
                Object[] arrobject3 = new Object[n + 2];
                System.arraycopy(arrobject, 0, arrobject3, 0, n);
                arrobject3[n] = object2;
                arrobject3[n + 1] = object4;
                this.KernPairs.put(object3, arrobject3);
                continue;
            }
            if (!string.equals("EndKernPairs")) continue;
            bl = false;
            break;
        }
        if (bl) {
            throw new DocumentException("Missing EndKernPairs in " + this.fileName);
        }
        randomAccessFileOrArray.close();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public PdfStream getFullFontStream() throws DocumentException {
        block14 : {
            if (this.builtinFont != false) return null;
            if (!this.embedded) {
                return null;
            }
            randomAccessFileOrArray = null;
            try {
                try {
                    string = this.fileName.substring(0, this.fileName.length() - 3) + "pfb";
                    randomAccessFileOrArray = this.pfb == null ? new RandomAccessFileOrArray(string) : new RandomAccessFileOrArray(this.pfb);
                    n = randomAccessFileOrArray.length();
                    arrby = new byte[n - 18];
                    arrn = new int[3];
                    n2 = 0;
                    i = 0;
lbl14: // 2 sources:
                    if (i < 3) {
                        if (randomAccessFileOrArray.read() != 128) {
                            throw new DocumentException("Start marker missing in " + string);
                        }
                        if (randomAccessFileOrArray.read() != Type1Font.PFB_TYPES[i]) {
                            throw new DocumentException("Incorrect segment type in " + string);
                        }
                        n3 = randomAccessFileOrArray.read();
                        n3 += randomAccessFileOrArray.read() << 8;
                        n3 += randomAccessFileOrArray.read() << 16;
                        arrn[i] = n3 += randomAccessFileOrArray.read() << 24;
                        break block14;
                    }
                    streamFont = new BaseFont.StreamFont(arrby, arrn, this.compressionLevel);
                    var11_12 = null;
                    if (randomAccessFileOrArray == null) return streamFont;
                }
                catch (Exception exception) {
                    throw new DocumentException(exception);
                }
                try {
                    randomAccessFileOrArray.close();
                    return streamFont;
                }
                catch (Exception var12_14) {
                    // empty catch block
                }
                return streamFont;
            }
            catch (Throwable var10_16) {
                var11_13 = null;
                if (randomAccessFileOrArray == null) throw var10_16;
                ** try [egrp 2[TRYBLOCK] [3 : 360->367)] { 
lbl41: // 1 sources:
                randomAccessFileOrArray.close();
                throw var10_16;
lbl43: // 1 sources:
                catch (Exception var12_15) {
                    // empty catch block
                }
                throw var10_16;
            }
        }
        while (n3 != 0) {
            n4 = randomAccessFileOrArray.read(arrby, n2, n3);
            if (n4 < 0) {
                throw new DocumentException("Premature end in " + string);
            }
            n2 += n4;
            n3 -= n4;
        }
        ++i;
        ** GOTO lbl14
    }

    private PdfDictionary getFontDescriptor(PdfIndirectReference pdfIndirectReference) {
        if (this.builtinFont) {
            return null;
        }
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        pdfDictionary.put(PdfName.ASCENT, new PdfNumber(this.Ascender));
        pdfDictionary.put(PdfName.CAPHEIGHT, new PdfNumber(this.CapHeight));
        pdfDictionary.put(PdfName.DESCENT, new PdfNumber(this.Descender));
        pdfDictionary.put(PdfName.FONTBBOX, new PdfRectangle(this.llx, this.lly, this.urx, this.ury));
        pdfDictionary.put(PdfName.FONTNAME, new PdfName(this.FontName));
        pdfDictionary.put(PdfName.ITALICANGLE, new PdfNumber(this.ItalicAngle));
        pdfDictionary.put(PdfName.STEMV, new PdfNumber(this.StdVW));
        if (pdfIndirectReference != null) {
            pdfDictionary.put(PdfName.FONTFILE, pdfIndirectReference);
        }
        int n = 0;
        if (this.IsFixedPitch) {
            n |= true;
        }
        n |= this.fontSpecific ? 4 : 32;
        if (this.ItalicAngle < 0.0f) {
            n |= 64;
        }
        if (this.FontName.indexOf("Caps") >= 0 || this.FontName.endsWith("SC")) {
            n |= 131072;
        }
        if (this.Weight.equals("Bold")) {
            n |= 262144;
        }
        pdfDictionary.put(PdfName.FLAGS, new PdfNumber(n));
        return pdfDictionary;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference pdfIndirectReference, int n, int n2, byte[] arrby) {
        boolean bl;
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONT);
        pdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE1);
        pdfDictionary.put(PdfName.BASEFONT, new PdfName(this.FontName));
        boolean bl2 = bl = this.encoding.equals("Cp1252") || this.encoding.equals("MacRoman");
        if (!this.fontSpecific || this.specialMap != null) {
            for (int i = n; i <= n2; ++i) {
                if (this.differences[i].equals(".notdef")) continue;
                n = i;
                break;
            }
            if (bl) {
                pdfDictionary.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
            } else {
                PdfDictionary pdfDictionary2 = new PdfDictionary(PdfName.ENCODING);
                PdfArray pdfArray = new PdfArray();
                boolean bl3 = true;
                for (int j = n; j <= n2; ++j) {
                    if (arrby[j] != 0) {
                        if (bl3) {
                            pdfArray.add(new PdfNumber(j));
                            bl3 = false;
                        }
                        pdfArray.add(new PdfName(this.differences[j]));
                        continue;
                    }
                    bl3 = true;
                }
                pdfDictionary2.put(PdfName.DIFFERENCES, pdfArray);
                pdfDictionary.put(PdfName.ENCODING, pdfDictionary2);
            }
        }
        if (this.specialMap != null || this.forceWidthsOutput || !this.builtinFont || !this.fontSpecific && !bl) {
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
        }
        if (!this.builtinFont && pdfIndirectReference != null) {
            pdfDictionary.put(PdfName.FONTDESCRIPTOR, pdfIndirectReference);
        }
        return pdfDictionary;
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
        pdfDictionary = this.getFullFontStream();
        if (pdfDictionary != null) {
            pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
            pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        }
        if ((pdfDictionary = this.getFontDescriptor(pdfIndirectReference2)) != null) {
            pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
            pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        }
        pdfDictionary = this.getFontBaseType(pdfIndirectReference2, n, n2, arrby);
        pdfWriter.addToBody((PdfObject)pdfDictionary, pdfIndirectReference);
    }

    public float getFontDescriptor(int n, float f) {
        switch (n) {
            case 1: 
            case 9: {
                return (float)this.Ascender * f / 1000.0f;
            }
            case 2: {
                return (float)this.CapHeight * f / 1000.0f;
            }
            case 3: 
            case 10: {
                return (float)this.Descender * f / 1000.0f;
            }
            case 4: {
                return this.ItalicAngle;
            }
            case 5: {
                return (float)this.llx * f / 1000.0f;
            }
            case 6: {
                return (float)this.lly * f / 1000.0f;
            }
            case 7: {
                return (float)this.urx * f / 1000.0f;
            }
            case 8: {
                return (float)this.ury * f / 1000.0f;
            }
            case 11: {
                return 0.0f;
            }
            case 12: {
                return (float)(this.urx - this.llx) * f / 1000.0f;
            }
            case 13: {
                return (float)this.UnderlinePosition * f / 1000.0f;
            }
            case 14: {
                return (float)this.UnderlineThickness * f / 1000.0f;
            }
        }
        return 0.0f;
    }

    public String getPostscriptFontName() {
        return this.FontName;
    }

    public String[][] getFullFontName() {
        return new String[][]{{"", "", "", this.FullName}};
    }

    public String[][] getAllNameEntries() {
        return new String[][]{{"4", "", "", "", this.FullName}};
    }

    public String[][] getFamilyFontName() {
        return new String[][]{{"", "", "", this.FamilyName}};
    }

    public boolean hasKernPairs() {
        return !this.KernPairs.isEmpty();
    }

    public void setPostscriptFontName(String string) {
        this.FontName = string;
    }

    public boolean setKerning(int n, int n2, int n3) {
        int n4;
        String string = GlyphList.unicodeToName(n);
        if (string == null) {
            return false;
        }
        String string2 = GlyphList.unicodeToName(n2);
        if (string2 == null) {
            return false;
        }
        Object[] arrobject = (Object[])this.KernPairs.get(string);
        if (arrobject == null) {
            arrobject = new Object[]{string2, new Integer(n3)};
            this.KernPairs.put(string, arrobject);
            return true;
        }
        for (n4 = 0; n4 < arrobject.length; n4 += 2) {
            if (!string2.equals(arrobject[n4])) continue;
            arrobject[n4 + 1] = new Integer(n3);
            return true;
        }
        n4 = arrobject.length;
        Object[] arrobject2 = new Object[n4 + 2];
        System.arraycopy(arrobject, 0, arrobject2, 0, n4);
        arrobject2[n4] = string2;
        arrobject2[n4 + 1] = new Integer(n3);
        this.KernPairs.put(string, arrobject2);
        return true;
    }

    protected int[] getRawCharBBox(int n, String string) {
        Object[] arrobject;
        if (string == null) {
            arrobject = (Object[])this.CharMetrics.get(new Integer(n));
        } else {
            if (string.equals(".notdef")) {
                return null;
            }
            arrobject = (Object[])this.CharMetrics.get(string);
        }
        if (arrobject != null) {
            return (int[])arrobject[3];
        }
        return null;
    }

    static {
        PFB_TYPES = new int[]{1, 2, 1};
    }
}

