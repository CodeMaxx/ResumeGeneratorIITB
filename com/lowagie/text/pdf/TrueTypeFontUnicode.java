/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.CFFFontSubset;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.TrueTypeFont;
import com.lowagie.text.pdf.TrueTypeFontSubSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

class TrueTypeFontUnicode
extends TrueTypeFont
implements Comparator {
    boolean vertical = false;
    private static final byte[] rotbits = new byte[]{-128, 64, 32, 16, 8, 4, 2, 1};

    TrueTypeFontUnicode(String string, String string2, boolean bl, byte[] arrby) throws DocumentException, IOException {
        String string3 = TrueTypeFontUnicode.getBaseName(string);
        String string4 = TrueTypeFontUnicode.getTTCName(string3);
        if (string3.length() < string.length()) {
            this.style = string.substring(string3.length());
        }
        this.encoding = string2;
        this.embedded = bl;
        this.fileName = string4;
        this.ttcIndex = "";
        if (string4.length() < string3.length()) {
            this.ttcIndex = string3.substring(string4.length() + 1);
        }
        this.fontType = 3;
        if ((this.fileName.toLowerCase().endsWith(".ttf") || this.fileName.toLowerCase().endsWith(".otf") || this.fileName.toLowerCase().endsWith(".ttc")) && (string2.equals("Identity-H") || string2.equals("Identity-V")) && bl) {
            this.process(arrby);
            if (this.os_2.fsType == 2) {
                throw new DocumentException(this.fileName + this.style + " cannot be embedded due to licensing restrictions.");
            }
            if (this.cmap31 == null && !this.fontSpecific || this.cmap10 == null && this.fontSpecific) {
                this.directTextToByte = true;
            }
            if (this.fontSpecific) {
                this.fontSpecific = false;
                String string5 = this.encoding;
                this.encoding = "";
                this.createEncoding();
                this.encoding = string5;
                this.fontSpecific = true;
            }
        } else {
            throw new DocumentException(this.fileName + " " + this.style + " is not a TTF font file.");
        }
        this.vertical = string2.endsWith("V");
    }

    public int getWidth(int n) {
        if (this.vertical) {
            return 1000;
        }
        if (this.fontSpecific) {
            if ((n & 65280) == 0 || (n & 65280) == 61440) {
                return this.getRawWidth(n & 255, null);
            }
            return 0;
        }
        return this.getRawWidth(n, this.encoding);
    }

    public int getWidth(String string) {
        if (this.vertical) {
            return string.length() * 1000;
        }
        int n = 0;
        if (this.fontSpecific) {
            char[] arrc = string.toCharArray();
            int n2 = arrc.length;
            for (int i = 0; i < n2; ++i) {
                char c = arrc[i];
                if ((c & 65280) != 0 && (c & 65280) != 61440) continue;
                n += this.getRawWidth(c & 255, null);
            }
        } else {
            int n3 = string.length();
            for (int i = 0; i < n3; ++i) {
                if (Utilities.isSurrogatePair(string, i)) {
                    n += this.getRawWidth(Utilities.convertToUtf32(string, i), this.encoding);
                    ++i;
                    continue;
                }
                n += this.getRawWidth(string.charAt(i), this.encoding);
            }
        }
        return n;
    }

    private PdfStream getToUnicode(Object[] arrobject) {
        Object object;
        if (arrobject.length == 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer("/CIDInit /ProcSet findresource begin\n12 dict begin\nbegincmap\n/CIDSystemInfo\n<< /Registry (TTX+0)\n/Ordering (T42UV)\n/Supplement 0\n>> def\n/CMapName /TTX+0 def\n/CMapType 2 def\n1 begincodespacerange\n<0000><FFFF>\nendcodespacerange\n");
        int n = 0;
        for (int i = 0; i < arrobject.length; ++i) {
            if (n == 0) {
                if (i != 0) {
                    stringBuffer.append("endbfrange\n");
                }
                n = Math.min(100, arrobject.length - i);
                stringBuffer.append(n).append(" beginbfrange\n");
            }
            --n;
            object = (int[])arrobject[i];
            String string = TrueTypeFontUnicode.toHex(object[0]);
            stringBuffer.append(string).append(string).append(TrueTypeFontUnicode.toHex((int)object[2])).append('\n');
        }
        stringBuffer.append("endbfrange\nendcmap\nCMapName currentdict /CMap defineresource pop\nend end\n");
        String string = stringBuffer.toString();
        object = new PdfStream(PdfEncodings.convertToBytes(string, null));
        object.flateCompress(this.compressionLevel);
        return object;
    }

    private static String toHex4(int n) {
        String string = "0000" + Integer.toHexString(n);
        return string.substring(string.length() - 4);
    }

    static String toHex(int n) {
        if (n < 65536) {
            return "<" + TrueTypeFontUnicode.toHex4(n) + ">";
        }
        int n2 = (n -= 65536) / 1024 + 55296;
        int n3 = n % 1024 + 56320;
        return "[<" + TrueTypeFontUnicode.toHex4(n2) + TrueTypeFontUnicode.toHex4(n3) + ">]";
    }

    private PdfDictionary getCIDFontType2(PdfIndirectReference pdfIndirectReference, String string, Object[] arrobject) {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONT);
        if (this.cff) {
            pdfDictionary.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
            pdfDictionary.put(PdfName.BASEFONT, new PdfName(string + this.fontName + "-" + this.encoding));
        } else {
            pdfDictionary.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE2);
            pdfDictionary.put(PdfName.BASEFONT, new PdfName(string + this.fontName));
        }
        pdfDictionary.put(PdfName.FONTDESCRIPTOR, pdfIndirectReference);
        if (!this.cff) {
            pdfDictionary.put(PdfName.CIDTOGIDMAP, PdfName.IDENTITY);
        }
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.REGISTRY, new PdfString("Adobe"));
        pdfDictionary2.put(PdfName.ORDERING, new PdfString("Identity"));
        pdfDictionary2.put(PdfName.SUPPLEMENT, new PdfNumber(0));
        pdfDictionary.put(PdfName.CIDSYSTEMINFO, pdfDictionary2);
        if (!this.vertical) {
            pdfDictionary.put(PdfName.DW, new PdfNumber(1000));
            StringBuffer stringBuffer = new StringBuffer("[");
            int n = -10;
            boolean bl = true;
            for (int i = 0; i < arrobject.length; ++i) {
                int[] arrn = (int[])arrobject[i];
                if (arrn[1] == 1000) continue;
                int n2 = arrn[0];
                if (n2 == n + 1) {
                    stringBuffer.append(' ').append(arrn[1]);
                } else {
                    if (!bl) {
                        stringBuffer.append(']');
                    }
                    bl = false;
                    stringBuffer.append(n2).append('[').append(arrn[1]);
                }
                n = n2;
            }
            if (stringBuffer.length() > 1) {
                stringBuffer.append("]]");
                pdfDictionary.put(PdfName.W, new PdfLiteral(stringBuffer.toString()));
            }
        }
        return pdfDictionary;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference pdfIndirectReference, String string, PdfIndirectReference pdfIndirectReference2) {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONT);
        pdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE0);
        if (this.cff) {
            pdfDictionary.put(PdfName.BASEFONT, new PdfName(string + this.fontName + "-" + this.encoding));
        } else {
            pdfDictionary.put(PdfName.BASEFONT, new PdfName(string + this.fontName));
        }
        pdfDictionary.put(PdfName.ENCODING, new PdfName(this.encoding));
        pdfDictionary.put(PdfName.DESCENDANTFONTS, new PdfArray(pdfIndirectReference));
        if (pdfIndirectReference2 != null) {
            pdfDictionary.put(PdfName.TOUNICODE, pdfIndirectReference2);
        }
        return pdfDictionary;
    }

    public int compare(Object object, Object object2) {
        int n = ((int[])object)[0];
        int n2 = ((int[])object2)[0];
        if (n < n2) {
            return -1;
        }
        if (n == n2) {
            return 0;
        }
        return 1;
    }

    void writeFont(PdfWriter pdfWriter, PdfIndirectReference pdfIndirectReference, Object[] arrobject) throws DocumentException, IOException {
        Object object;
        Object object2;
        Object object3;
        HashMap hashMap = (HashMap)arrobject[0];
        this.addRangeUni(hashMap, true, this.subset);
        Object[] arrobject2 = hashMap.values().toArray();
        Arrays.sort(arrobject2, this);
        PdfIndirectReference pdfIndirectReference2 = null;
        PdfDictionary pdfDictionary = null;
        PdfIndirectObject pdfIndirectObject = null;
        PdfIndirectReference pdfIndirectReference3 = null;
        if (pdfWriter.getPDFXConformance() == 3 || pdfWriter.getPDFXConformance() == 4) {
            if (arrobject2.length == 0) {
                object3 = new PdfStream(new byte[]{-128});
            } else {
                object2 = ((int[])arrobject2[arrobject2.length - 1])[0];
                object = new byte[object2 / 8 + 1];
                for (int i = 0; i < arrobject2.length; ++i) {
                    int n = ((int[])arrobject2[i])[0];
                    byte[] arrby = object;
                    int n2 = n / 8;
                    arrby[n2] = (byte)(arrby[n2] | rotbits[n % 8]);
                }
                object3 = new PdfStream((byte[])object);
                object3.flateCompress(this.compressionLevel);
            }
            pdfIndirectReference3 = pdfWriter.addToBody((PdfObject)object3).getIndirectReference();
        }
        if (this.cff) {
            object3 = this.readCffFont();
            if (this.subset || this.subsetRanges != null) {
                CFFFontSubset cFFFontSubset = new CFFFontSubset(new RandomAccessFileOrArray((byte[])object3), hashMap);
                object3 = cFFFontSubset.Process(cFFFontSubset.getNames()[0]);
            }
            pdfDictionary = new BaseFont.StreamFont((byte[])object3, "CIDFontType0C", this.compressionLevel);
            pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
            pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        } else {
            if (this.subset || this.directoryOffset != 0) {
                TrueTypeFontSubSet trueTypeFontSubSet = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), hashMap, this.directoryOffset, false, false);
                object3 = trueTypeFontSubSet.process();
            } else {
                object3 = this.getFullFont();
            }
            object2 = new int[]{object3.length};
            pdfDictionary = new BaseFont.StreamFont((byte[])object3, (int[])object2, this.compressionLevel);
            pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
            pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        }
        object3 = "";
        if (this.subset) {
            object3 = TrueTypeFontUnicode.createSubsetPrefix();
        }
        PdfDictionary pdfDictionary2 = this.getFontDescriptor(pdfIndirectReference2, (String)object3, pdfIndirectReference3);
        pdfIndirectObject = pdfWriter.addToBody(pdfDictionary2);
        pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        pdfDictionary = this.getCIDFontType2(pdfIndirectReference2, (String)object3, arrobject2);
        pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
        pdfIndirectReference2 = pdfIndirectObject.getIndirectReference();
        pdfDictionary = this.getToUnicode(arrobject2);
        object = null;
        if (pdfDictionary != null) {
            pdfIndirectObject = pdfWriter.addToBody(pdfDictionary);
            object = pdfIndirectObject.getIndirectReference();
        }
        pdfDictionary = this.getFontBaseType(pdfIndirectReference2, (String)object3, (PdfIndirectReference)object);
        pdfWriter.addToBody((PdfObject)pdfDictionary, pdfIndirectReference);
    }

    public PdfStream getFullFontStream() throws IOException, DocumentException {
        if (this.cff) {
            return new BaseFont.StreamFont(this.readCffFont(), "CIDFontType0C", this.compressionLevel);
        }
        return super.getFullFontStream();
    }

    byte[] convertToBytes(String string) {
        return null;
    }

    byte[] convertToBytes(int n) {
        return null;
    }

    public int[] getMetricsTT(int n) {
        if (this.cmapExt != null) {
            return (int[])this.cmapExt.get(new Integer(n));
        }
        HashMap hashMap = null;
        hashMap = this.fontSpecific ? this.cmap10 : this.cmap31;
        if (hashMap == null) {
            return null;
        }
        if (this.fontSpecific) {
            if ((n & -256) == 0 || (n & -256) == 61440) {
                return (int[])hashMap.get(new Integer(n & 255));
            }
            return null;
        }
        return (int[])hashMap.get(new Integer(n));
    }

    public boolean charExists(int n) {
        return this.getMetricsTT(n) != null;
    }

    public boolean setCharAdvance(int n, int n2) {
        int[] arrn = this.getMetricsTT(n);
        if (arrn == null) {
            return false;
        }
        arrn[1] = n2;
        return true;
    }

    public int[] getCharBBox(int n) {
        if (this.bboxes == null) {
            return null;
        }
        int[] arrn = this.getMetricsTT(n);
        if (arrn == null) {
            return null;
        }
        return this.bboxes[arrn[0]];
    }
}

