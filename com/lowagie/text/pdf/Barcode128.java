/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;

public class Barcode128
extends Barcode {
    private static final byte[][] BARS;
    private static final byte[] BARS_STOP;
    public static final char CODE_AB_TO_C = 'c';
    public static final char CODE_AC_TO_B = 'd';
    public static final char CODE_BC_TO_A = 'e';
    public static final char FNC1_INDEX = 'f';
    public static final char START_A = 'g';
    public static final char START_B = 'h';
    public static final char START_C = 'i';
    public static final char FNC1 = '\u00ca';
    public static final char DEL = '\u00c3';
    public static final char FNC3 = '\u00c4';
    public static final char FNC2 = '\u00c5';
    public static final char SHIFT = '\u00c6';
    public static final char CODE_C = '\u00c7';
    public static final char CODE_A = '\u00c8';
    public static final char FNC4 = '\u00c8';
    public static final char STARTA = '\u00cb';
    public static final char STARTB = '\u00cc';
    public static final char STARTC = '\u00cd';
    private static final IntHashtable ais;

    public Barcode128() {
        try {
            this.x = 0.8f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.baseline = this.size = 8.0f;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.codeType = 9;
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public static String removeFNC1(String string) {
        int n = string.length();
        StringBuffer stringBuffer = new StringBuffer(n);
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c < ' ' || c > '~') continue;
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public static String getHumanReadableUCCEAN(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        String string2 = String.valueOf('\u00ca');
        try {
            do {
                int n;
                if (string.startsWith(string2)) {
                    string = string.substring(1);
                    continue;
                }
                int n2 = 0;
                int n3 = 0;
                for (n = 2; n < 5 && string.length() >= n; ++n) {
                    n2 = ais.get(Integer.parseInt(string.substring(0, n)));
                    if (n2 == 0) continue;
                    n3 = n;
                    break;
                }
                if (n3 != 0) {
                    stringBuffer.append('(').append(string.substring(0, n3)).append(')');
                    string = string.substring(n3);
                    if (n2 > 0) {
                        if (string.length() > (n2 -= n3)) {
                            stringBuffer.append(Barcode128.removeFNC1(string.substring(0, n2)));
                            string = string.substring(n2);
                            continue;
                        }
                    } else {
                        n = string.indexOf(202);
                        if (n >= 0) {
                            stringBuffer.append(string.substring(0, n));
                            string = string.substring(n + 1);
                            continue;
                        }
                    }
                }
                break;
            } while (true);
        }
        catch (Exception var3_4) {
            // empty catch block
        }
        stringBuffer.append(Barcode128.removeFNC1(string));
        return stringBuffer.toString();
    }

    static boolean isNextDigits(String string, int n, int n2) {
        int n3 = string.length();
        while (n < n3 && n2 > 0) {
            if (string.charAt(n) == '\u00ca') {
                ++n;
                continue;
            }
            int n4 = Math.min(2, n2);
            if (n + n4 > n3) {
                return false;
            }
            while (n4-- > 0) {
                char c;
                if ((c = string.charAt(n++)) < '0' || c > '9') {
                    return false;
                }
                --n2;
            }
        }
        return n2 == 0;
    }

    static String getPackedRawDigits(String string, int n, int n2) {
        String string2 = "";
        int n3 = n;
        while (n2 > 0) {
            if (string.charAt(n) == '\u00ca') {
                string2 = string2 + 'f';
                ++n;
                continue;
            }
            n2 -= 2;
            int n4 = string.charAt(n++) - 48;
            int n5 = string.charAt(n++) - 48;
            string2 = string2 + (char)(n4 * 10 + n5);
        }
        return "" + (char)(n - n3) + string2;
    }

    public static String getRawText(String string, boolean bl) {
        String string2;
        int n;
        String string3 = "";
        int n2 = string.length();
        if (n2 == 0) {
            string3 = string3 + 'h';
            if (bl) {
                string3 = string3 + 'f';
            }
            return string3;
        }
        char c = '\u0000';
        for (n = 0; n < n2; ++n) {
            c = string.charAt(n);
            if (c <= '' || c == '\u00ca') continue;
            throw new RuntimeException("There are illegal characters for barcode 128 in '" + string + "'.");
        }
        c = string.charAt(0);
        n = 104;
        int n3 = 0;
        if (Barcode128.isNextDigits(string, n3, 2)) {
            n = 105;
            string3 = string3 + (char)n;
            if (bl) {
                string3 = string3 + 'f';
            }
            string2 = Barcode128.getPackedRawDigits(string, n3, 2);
            n3 += string2.charAt(0);
            string3 = string3 + string2.substring(1);
        } else if (c < ' ') {
            n = 103;
            string3 = string3 + (char)n;
            if (bl) {
                string3 = string3 + 'f';
            }
            string3 = string3 + (char)(c + 64);
            ++n3;
        } else {
            string3 = string3 + (char)n;
            if (bl) {
                string3 = string3 + 'f';
            }
            string3 = c == '\u00ca' ? string3 + 'f' : string3 + (char)(c - 32);
            ++n3;
        }
        while (n3 < n2) {
            switch (n) {
                case 103: {
                    if (Barcode128.isNextDigits(string, n3, 4)) {
                        n = 105;
                        string3 = string3 + 'c';
                        string2 = Barcode128.getPackedRawDigits(string, n3, 4);
                        n3 += string2.charAt(0);
                        string3 = string3 + string2.substring(1);
                        break;
                    }
                    if ((c = string.charAt(n3++)) == '\u00ca') {
                        string3 = string3 + 'f';
                        break;
                    }
                    if (c > '_') {
                        n = 104;
                        string3 = string3 + 'd';
                        string3 = string3 + (char)(c - 32);
                        break;
                    }
                    if (c < ' ') {
                        string3 = string3 + (char)(c + 64);
                        break;
                    }
                    string3 = string3 + (char)(c - 32);
                    break;
                }
                case 104: {
                    if (Barcode128.isNextDigits(string, n3, 4)) {
                        n = 105;
                        string3 = string3 + 'c';
                        string2 = Barcode128.getPackedRawDigits(string, n3, 4);
                        n3 += string2.charAt(0);
                        string3 = string3 + string2.substring(1);
                        break;
                    }
                    if ((c = string.charAt(n3++)) == '\u00ca') {
                        string3 = string3 + 'f';
                        break;
                    }
                    if (c < ' ') {
                        n = 103;
                        string3 = string3 + 'e';
                        string3 = string3 + (char)(c + 64);
                        break;
                    }
                    string3 = string3 + (char)(c - 32);
                    break;
                }
                case 105: {
                    if (Barcode128.isNextDigits(string, n3, 2)) {
                        string2 = Barcode128.getPackedRawDigits(string, n3, 2);
                        n3 += string2.charAt(0);
                        string3 = string3 + string2.substring(1);
                        break;
                    }
                    if ((c = string.charAt(n3++)) == '\u00ca') {
                        string3 = string3 + 'f';
                        break;
                    }
                    if (c < ' ') {
                        n = 103;
                        string3 = string3 + 'e';
                        string3 = string3 + (char)(c + 64);
                        break;
                    }
                    n = 104;
                    string3 = string3 + 'd';
                    string3 = string3 + (char)(c - 32);
                }
            }
        }
        return string3;
    }

    public static byte[] getBarsCode128Raw(String string) {
        int n;
        int n2 = string.indexOf(65535);
        if (n2 >= 0) {
            string = string.substring(0, n2);
        }
        int n3 = string.charAt(0);
        for (int i = 1; i < string.length(); ++i) {
            n3 += i * string.charAt(i);
        }
        string = string + (char)(n3 %= 103);
        byte[] arrby = new byte[(string.length() + 1) * 6 + 7];
        for (n = 0; n < string.length(); ++n) {
            System.arraycopy(BARS[string.charAt(n)], 0, arrby, n * 6, 6);
        }
        System.arraycopy(BARS_STOP, 0, arrby, n * 6, 7);
        return arrby;
    }

    public Rectangle getBarcodeSize() {
        String string;
        int n;
        float f = 0.0f;
        float f2 = 0.0f;
        if (this.font != null) {
            f2 = this.baseline > 0.0f ? this.baseline - this.font.getFontDescriptor(3, this.size) : - this.baseline + this.size;
            string = this.codeType == 11 ? ((n = this.code.indexOf(65535)) < 0 ? "" : this.code.substring(n + 1)) : (this.codeType == 10 ? Barcode128.getHumanReadableUCCEAN(this.code) : Barcode128.removeFNC1(this.code));
            f = this.font.getWidthPoint(this.altText != null ? this.altText : string, this.size);
        }
        string = this.codeType == 11 ? ((n = this.code.indexOf(65535)) >= 0 ? this.code.substring(0, n) : this.code) : Barcode128.getRawText(this.code, this.codeType == 10);
        n = string.length();
        float f3 = (float)((n + 2) * 11) * this.x + 2.0f * this.x;
        f3 = Math.max(f3, f);
        float f4 = this.barHeight + f2;
        return new Rectangle(f3, f4);
    }

    public Rectangle placeBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        int n;
        int n2;
        String string = this.codeType == 11 ? ((n2 = this.code.indexOf(65535)) < 0 ? "" : this.code.substring(n2 + 1)) : (this.codeType == 10 ? Barcode128.getHumanReadableUCCEAN(this.code) : Barcode128.removeFNC1(this.code));
        float f = 0.0f;
        if (this.font != null) {
            string = this.altText != null ? this.altText : string;
            f = this.font.getWidthPoint(string, this.size);
        }
        String string2 = this.codeType == 11 ? ((n = this.code.indexOf(65535)) >= 0 ? this.code.substring(0, n) : this.code) : Barcode128.getRawText(this.code, this.codeType == 10);
        n = string2.length();
        float f2 = (float)((n + 2) * 11) * this.x + 2.0f * this.x;
        float f3 = 0.0f;
        float f4 = 0.0f;
        switch (this.textAlignment) {
            case 0: {
                break;
            }
            case 2: {
                if (f > f2) {
                    f3 = f - f2;
                    break;
                }
                f4 = f2 - f;
                break;
            }
            default: {
                if (f > f2) {
                    f3 = (f - f2) / 2.0f;
                    break;
                }
                f4 = (f2 - f) / 2.0f;
            }
        }
        float f5 = 0.0f;
        float f6 = 0.0f;
        if (this.font != null) {
            if (this.baseline <= 0.0f) {
                f6 = this.barHeight - this.baseline;
            } else {
                f6 = - this.font.getFontDescriptor(3, this.size);
                f5 = f6 + this.baseline;
            }
        }
        byte[] arrby = Barcode128.getBarsCode128Raw(string2);
        boolean bl = true;
        if (color != null) {
            pdfContentByte.setColorFill(color);
        }
        for (int i = 0; i < arrby.length; ++i) {
            float f7 = (float)arrby[i] * this.x;
            if (bl) {
                pdfContentByte.rectangle(f3, f5, f7 - this.inkSpreading, this.barHeight);
            }
            bl = !bl;
            f3 += f7;
        }
        pdfContentByte.fill();
        if (this.font != null) {
            if (color2 != null) {
                pdfContentByte.setColorFill(color2);
            }
            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(this.font, this.size);
            pdfContentByte.setTextMatrix(f4, f6);
            pdfContentByte.showText(string);
            pdfContentByte.endText();
        }
        return this.getBarcodeSize();
    }

    public Image createAwtImage(Color color, Color color2) {
        int n;
        int n2;
        int n3 = color.getRGB();
        int n4 = color2.getRGB();
        Canvas canvas = new Canvas();
        String string = this.codeType == 11 ? ((n = this.code.indexOf(65535)) >= 0 ? this.code.substring(0, n) : this.code) : Barcode128.getRawText(this.code, this.codeType == 10);
        n = string.length();
        int n5 = (n + 2) * 11 + 2;
        byte[] arrby = Barcode128.getBarsCode128Raw(string);
        boolean bl = true;
        int n6 = 0;
        int n7 = (int)this.barHeight;
        int[] arrn = new int[n5 * n7];
        for (n2 = 0; n2 < arrby.length; ++n2) {
            int n8 = arrby[n2];
            int n9 = n4;
            if (bl) {
                n9 = n3;
            }
            bl = !bl;
            for (int i = 0; i < n8; ++i) {
                arrn[n6++] = n9;
            }
        }
        for (n2 = n5; n2 < arrn.length; n2 += n5) {
            System.arraycopy(arrn, 0, arrn, n2, n5);
        }
        Image image = canvas.createImage(new MemoryImageSource(n5, n7, arrn, 0, n5));
        return image;
    }

    public void setCode(String string) {
        if (this.getCodeType() == 10 && string.startsWith("(")) {
            int n = 0;
            String string2 = "";
            while (n >= 0) {
                int n2 = string.indexOf(41, n);
                if (n2 < 0) {
                    throw new IllegalArgumentException("Badly formed UCC string: " + string);
                }
                String string3 = string.substring(n + 1, n2);
                if (string3.length() < 2) {
                    throw new IllegalArgumentException("AI too short: (" + string3 + ")");
                }
                int n3 = Integer.parseInt(string3);
                int n4 = ais.get(n3);
                if (n4 == 0) {
                    throw new IllegalArgumentException("AI not found: (" + string3 + ")");
                }
                string3 = String.valueOf(n3);
                if (string3.length() == 1) {
                    string3 = "0" + string3;
                }
                int n5 = (n = string.indexOf(40, n2)) < 0 ? string.length() : n;
                string2 = string2 + string3 + string.substring(n2 + 1, n5);
                if (n4 < 0) {
                    if (n < 0) continue;
                    string2 = string2 + '\u00ca';
                    continue;
                }
                if (n5 - n2 - 1 + string3.length() == n4) continue;
                throw new IllegalArgumentException("Invalid AI length: (" + string3 + ")");
            }
            super.setCode(string2);
        } else {
            super.setCode(string);
        }
    }

    static {
        int n;
        BARS = new byte[][]{{2, 1, 2, 2, 2, 2}, {2, 2, 2, 1, 2, 2}, {2, 2, 2, 2, 2, 1}, {1, 2, 1, 2, 2, 3}, {1, 2, 1, 3, 2, 2}, {1, 3, 1, 2, 2, 2}, {1, 2, 2, 2, 1, 3}, {1, 2, 2, 3, 1, 2}, {1, 3, 2, 2, 1, 2}, {2, 2, 1, 2, 1, 3}, {2, 2, 1, 3, 1, 2}, {2, 3, 1, 2, 1, 2}, {1, 1, 2, 2, 3, 2}, {1, 2, 2, 1, 3, 2}, {1, 2, 2, 2, 3, 1}, {1, 1, 3, 2, 2, 2}, {1, 2, 3, 1, 2, 2}, {1, 2, 3, 2, 2, 1}, {2, 2, 3, 2, 1, 1}, {2, 2, 1, 1, 3, 2}, {2, 2, 1, 2, 3, 1}, {2, 1, 3, 2, 1, 2}, {2, 2, 3, 1, 1, 2}, {3, 1, 2, 1, 3, 1}, {3, 1, 1, 2, 2, 2}, {3, 2, 1, 1, 2, 2}, {3, 2, 1, 2, 2, 1}, {3, 1, 2, 2, 1, 2}, {3, 2, 2, 1, 1, 2}, {3, 2, 2, 2, 1, 1}, {2, 1, 2, 1, 2, 3}, {2, 1, 2, 3, 2, 1}, {2, 3, 2, 1, 2, 1}, {1, 1, 1, 3, 2, 3}, {1, 3, 1, 1, 2, 3}, {1, 3, 1, 3, 2, 1}, {1, 1, 2, 3, 1, 3}, {1, 3, 2, 1, 1, 3}, {1, 3, 2, 3, 1, 1}, {2, 1, 1, 3, 1, 3}, {2, 3, 1, 1, 1, 3}, {2, 3, 1, 3, 1, 1}, {1, 1, 2, 1, 3, 3}, {1, 1, 2, 3, 3, 1}, {1, 3, 2, 1, 3, 1}, {1, 1, 3, 1, 2, 3}, {1, 1, 3, 3, 2, 1}, {1, 3, 3, 1, 2, 1}, {3, 1, 3, 1, 2, 1}, {2, 1, 1, 3, 3, 1}, {2, 3, 1, 1, 3, 1}, {2, 1, 3, 1, 1, 3}, {2, 1, 3, 3, 1, 1}, {2, 1, 3, 1, 3, 1}, {3, 1, 1, 1, 2, 3}, {3, 1, 1, 3, 2, 1}, {3, 3, 1, 1, 2, 1}, {3, 1, 2, 1, 1, 3}, {3, 1, 2, 3, 1, 1}, {3, 3, 2, 1, 1, 1}, {3, 1, 4, 1, 1, 1}, {2, 2, 1, 4, 1, 1}, {4, 3, 1, 1, 1, 1}, {1, 1, 1, 2, 2, 4}, {1, 1, 1, 4, 2, 2}, {1, 2, 1, 1, 2, 4}, {1, 2, 1, 4, 2, 1}, {1, 4, 1, 1, 2, 2}, {1, 4, 1, 2, 2, 1}, {1, 1, 2, 2, 1, 4}, {1, 1, 2, 4, 1, 2}, {1, 2, 2, 1, 1, 4}, {1, 2, 2, 4, 1, 1}, {1, 4, 2, 1, 1, 2}, {1, 4, 2, 2, 1, 1}, {2, 4, 1, 2, 1, 1}, {2, 2, 1, 1, 1, 4}, {4, 1, 3, 1, 1, 1}, {2, 4, 1, 1, 1, 2}, {1, 3, 4, 1, 1, 1}, {1, 1, 1, 2, 4, 2}, {1, 2, 1, 1, 4, 2}, {1, 2, 1, 2, 4, 1}, {1, 1, 4, 2, 1, 2}, {1, 2, 4, 1, 1, 2}, {1, 2, 4, 2, 1, 1}, {4, 1, 1, 2, 1, 2}, {4, 2, 1, 1, 1, 2}, {4, 2, 1, 2, 1, 1}, {2, 1, 2, 1, 4, 1}, {2, 1, 4, 1, 2, 1}, {4, 1, 2, 1, 2, 1}, {1, 1, 1, 1, 4, 3}, {1, 1, 1, 3, 4, 1}, {1, 3, 1, 1, 4, 1}, {1, 1, 4, 1, 1, 3}, {1, 1, 4, 3, 1, 1}, {4, 1, 1, 1, 1, 3}, {4, 1, 1, 3, 1, 1}, {1, 1, 3, 1, 4, 1}, {1, 1, 4, 1, 3, 1}, {3, 1, 1, 1, 4, 1}, {4, 1, 1, 1, 3, 1}, {2, 1, 1, 4, 1, 2}, {2, 1, 1, 2, 1, 4}, {2, 1, 1, 2, 3, 2}};
        BARS_STOP = new byte[]{2, 3, 3, 1, 1, 1, 2};
        ais = new IntHashtable();
        ais.put(0, 20);
        ais.put(1, 16);
        ais.put(2, 16);
        ais.put(10, -1);
        ais.put(11, 9);
        ais.put(12, 8);
        ais.put(13, 8);
        ais.put(15, 8);
        ais.put(17, 8);
        ais.put(20, 4);
        ais.put(21, -1);
        ais.put(22, -1);
        ais.put(23, -1);
        ais.put(240, -1);
        ais.put(241, -1);
        ais.put(250, -1);
        ais.put(251, -1);
        ais.put(252, -1);
        ais.put(30, -1);
        for (n = 3100; n < 3700; ++n) {
            ais.put(n, 10);
        }
        ais.put(37, -1);
        for (n = 3900; n < 3940; ++n) {
            ais.put(n, -1);
        }
        ais.put(400, -1);
        ais.put(401, -1);
        ais.put(402, 20);
        ais.put(403, -1);
        for (n = 410; n < 416; ++n) {
            ais.put(n, 16);
        }
        ais.put(420, -1);
        ais.put(421, -1);
        ais.put(422, 6);
        ais.put(423, -1);
        ais.put(424, 6);
        ais.put(425, 6);
        ais.put(426, 6);
        ais.put(7001, 17);
        ais.put(7002, -1);
        for (n = 7030; n < 7040; ++n) {
            ais.put(n, -1);
        }
        ais.put(8001, 18);
        ais.put(8002, -1);
        ais.put(8003, -1);
        ais.put(8004, -1);
        ais.put(8005, 10);
        ais.put(8006, 22);
        ais.put(8007, -1);
        ais.put(8008, -1);
        ais.put(8018, 22);
        ais.put(8020, -1);
        ais.put(8100, 10);
        ais.put(8101, 14);
        ais.put(8102, 6);
        for (n = 90; n < 100; ++n) {
            ais.put(n, -1);
        }
    }
}

