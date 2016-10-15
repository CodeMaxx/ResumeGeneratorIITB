/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;

public class BarcodeInter25
extends Barcode {
    private static final byte[][] BARS = new byte[][]{{0, 0, 1, 1, 0}, {1, 0, 0, 0, 1}, {0, 1, 0, 0, 1}, {1, 1, 0, 0, 0}, {0, 0, 1, 0, 1}, {1, 0, 1, 0, 0}, {0, 1, 1, 0, 0}, {0, 0, 0, 1, 1}, {1, 0, 0, 1, 0}, {0, 1, 0, 1, 0}};

    public BarcodeInter25() {
        try {
            this.x = 0.8f;
            this.n = 2.0f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.baseline = this.size = 8.0f;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.generateChecksum = false;
            this.checksumText = false;
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public static String keepNumbers(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c < '0' || c > '9') continue;
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public static char getChecksum(String string) {
        int n = 3;
        int n2 = 0;
        for (int i = string.length() - 1; i >= 0; --i) {
            int n3 = string.charAt(i) - 48;
            n2 += n * n3;
            n ^= 2;
        }
        return (char)((10 - n2 % 10) % 10 + 48);
    }

    public static byte[] getBarsInter25(String string) {
        if (((string = BarcodeInter25.keepNumbers(string)).length() & 1) != 0) {
            throw new IllegalArgumentException("The text length must be even.");
        }
        byte[] arrby = new byte[string.length() * 5 + 7];
        int n = 0;
        arrby[n++] = 0;
        arrby[n++] = 0;
        arrby[n++] = 0;
        arrby[n++] = 0;
        int n2 = string.length() / 2;
        for (int i = 0; i < n2; ++i) {
            int n3 = string.charAt(i * 2) - 48;
            int n4 = string.charAt(i * 2 + 1) - 48;
            byte[] arrby2 = BARS[n3];
            byte[] arrby3 = BARS[n4];
            for (int j = 0; j < 5; ++j) {
                arrby[n++] = arrby2[j];
                arrby[n++] = arrby3[j];
            }
        }
        arrby[n++] = 1;
        arrby[n++] = 0;
        arrby[n++] = 0;
        return arrby;
    }

    public Rectangle getBarcodeSize() {
        String string;
        float f = 0.0f;
        float f2 = 0.0f;
        if (this.font != null) {
            f2 = this.baseline > 0.0f ? this.baseline - this.font.getFontDescriptor(3, this.size) : - this.baseline + this.size;
            string = this.code;
            if (this.generateChecksum && this.checksumText) {
                string = string + BarcodeInter25.getChecksum(string);
            }
            f = this.font.getWidthPoint(this.altText != null ? this.altText : string, this.size);
        }
        string = BarcodeInter25.keepNumbers(this.code);
        int n = string.length();
        if (this.generateChecksum) {
            ++n;
        }
        float f3 = (float)n * (3.0f * this.x + 2.0f * this.x * this.n) + (6.0f + this.n) * this.x;
        f3 = Math.max(f3, f);
        float f4 = this.barHeight + f2;
        return new Rectangle(f3, f4);
    }

    public Rectangle placeBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        String string = this.code;
        float f = 0.0f;
        if (this.font != null) {
            if (this.generateChecksum && this.checksumText) {
                string = string + BarcodeInter25.getChecksum(string);
            }
            string = this.altText != null ? this.altText : string;
            f = this.font.getWidthPoint(string, this.size);
        }
        String string2 = BarcodeInter25.keepNumbers(this.code);
        if (this.generateChecksum) {
            string2 = string2 + BarcodeInter25.getChecksum(string2);
        }
        int n = string2.length();
        float f2 = (float)n * (3.0f * this.x + 2.0f * this.x * this.n) + (6.0f + this.n) * this.x;
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
        byte[] arrby = BarcodeInter25.getBarsInter25(string2);
        boolean bl = true;
        if (color != null) {
            pdfContentByte.setColorFill(color);
        }
        for (int i = 0; i < arrby.length; ++i) {
            float f7;
            float f8 = f7 = arrby[i] == 0 ? this.x : this.x * this.n;
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
        int n2 = color.getRGB();
        int n3 = color2.getRGB();
        Canvas canvas = new Canvas();
        String string = BarcodeInter25.keepNumbers(this.code);
        if (this.generateChecksum) {
            string = string + BarcodeInter25.getChecksum(string);
        }
        int n4 = string.length();
        int n5 = (int)this.n;
        int n6 = n4 * (3 + 2 * n5) + (6 + n5);
        byte[] arrby = BarcodeInter25.getBarsInter25(string);
        boolean bl = true;
        int n7 = 0;
        int n8 = (int)this.barHeight;
        int[] arrn = new int[n6 * n8];
        for (n = 0; n < arrby.length; ++n) {
            int n9 = arrby[n] == 0 ? 1 : n5;
            int n10 = n3;
            if (bl) {
                n10 = n2;
            }
            bl = !bl;
            for (int i = 0; i < n9; ++i) {
                arrn[n7++] = n10;
            }
        }
        for (n = n6; n < arrn.length; n += n6) {
            System.arraycopy(arrn, 0, arrn, n, n6);
        }
        Image image = canvas.createImage(new MemoryImageSource(n6, n8, arrn, 0, n6));
        return image;
    }
}

