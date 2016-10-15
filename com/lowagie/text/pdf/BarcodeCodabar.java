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

public class BarcodeCodabar
extends Barcode {
    private static final byte[][] BARS = new byte[][]{{0, 0, 0, 0, 0, 1, 1}, {0, 0, 0, 0, 1, 1, 0}, {0, 0, 0, 1, 0, 0, 1}, {1, 1, 0, 0, 0, 0, 0}, {0, 0, 1, 0, 0, 1, 0}, {1, 0, 0, 0, 0, 1, 0}, {0, 1, 0, 0, 0, 0, 1}, {0, 1, 0, 0, 1, 0, 0}, {0, 1, 1, 0, 0, 0, 0}, {1, 0, 0, 1, 0, 0, 0}, {0, 0, 0, 1, 1, 0, 0}, {0, 0, 1, 1, 0, 0, 0}, {1, 0, 0, 0, 1, 0, 1}, {1, 0, 1, 0, 0, 0, 1}, {1, 0, 1, 0, 1, 0, 0}, {0, 0, 1, 0, 1, 0, 1}, {0, 0, 1, 1, 0, 1, 0}, {0, 1, 0, 1, 0, 0, 1}, {0, 0, 0, 1, 0, 1, 1}, {0, 0, 0, 1, 1, 1, 0}};
    private static final String CHARS = "0123456789-$:/.+ABCD";
    private static final int START_STOP_IDX = 16;

    public BarcodeCodabar() {
        try {
            this.x = 0.8f;
            this.n = 2.0f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.baseline = this.size = 8.0f;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.generateChecksum = false;
            this.checksumText = false;
            this.startStopText = false;
            this.codeType = 12;
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public static byte[] getBarsCodabar(String string) {
        int n = (string = string.toUpperCase()).length();
        if (n < 2) {
            throw new IllegalArgumentException("Codabar must have at least a start and stop character.");
        }
        if ("0123456789-$:/.+ABCD".indexOf(string.charAt(0)) < 16 || "0123456789-$:/.+ABCD".indexOf(string.charAt(n - 1)) < 16) {
            throw new IllegalArgumentException("Codabar must have one of 'ABCD' as start/stop character.");
        }
        byte[] arrby = new byte[string.length() * 8 - 1];
        for (int i = 0; i < n; ++i) {
            int n2 = "0123456789-$:/.+ABCD".indexOf(string.charAt(i));
            if (n2 >= 16 && i > 0 && i < n - 1) {
                throw new IllegalArgumentException("In codabar, start/stop characters are only allowed at the extremes.");
            }
            if (n2 < 0) {
                throw new IllegalArgumentException("The character '" + string.charAt(i) + "' is illegal in codabar.");
            }
            System.arraycopy(BARS[n2], 0, arrby, i * 8, 7);
        }
        return arrby;
    }

    public static String calculateChecksum(String string) {
        if (string.length() < 2) {
            return string;
        }
        String string2 = string.toUpperCase();
        int n = 0;
        int n2 = string2.length();
        for (int i = 0; i < n2; ++i) {
            n += "0123456789-$:/.+ABCD".indexOf(string2.charAt(i));
        }
        n = (n + 15) / 16 * 16 - n;
        return string.substring(0, n2 - 1) + "0123456789-$:/.+ABCD".charAt(n) + string.substring(n2 - 1);
    }

    public Rectangle getBarcodeSize() {
        int n;
        float f = 0.0f;
        float f2 = 0.0f;
        String string = this.code;
        if (this.generateChecksum && this.checksumText) {
            string = BarcodeCodabar.calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            string = string.substring(1, string.length() - 1);
        }
        if (this.font != null) {
            f2 = this.baseline > 0.0f ? this.baseline - this.font.getFontDescriptor(3, this.size) : - this.baseline + this.size;
            f = this.font.getWidthPoint(this.altText != null ? this.altText : string, this.size);
        }
        string = this.code;
        if (this.generateChecksum) {
            string = BarcodeCodabar.calculateChecksum(this.code);
        }
        byte[] arrby = BarcodeCodabar.getBarsCodabar(string);
        int n2 = 0;
        for (n = 0; n < arrby.length; ++n) {
            n2 += arrby[n];
        }
        n = arrby.length - n2;
        float f3 = this.x * ((float)n + (float)n2 * this.n);
        f3 = Math.max(f3, f);
        float f4 = this.barHeight + f2;
        return new Rectangle(f3, f4);
    }

    public Rectangle placeBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        int n;
        String string = this.code;
        if (this.generateChecksum && this.checksumText) {
            string = BarcodeCodabar.calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            string = string.substring(1, string.length() - 1);
        }
        float f = 0.0f;
        if (this.font != null) {
            string = this.altText != null ? this.altText : string;
            f = this.font.getWidthPoint(string, this.size);
        }
        byte[] arrby = BarcodeCodabar.getBarsCodabar(this.generateChecksum ? BarcodeCodabar.calculateChecksum(this.code) : this.code);
        int n2 = 0;
        for (n = 0; n < arrby.length; ++n) {
            n2 += arrby[n];
        }
        n = arrby.length - n2;
        float f2 = this.x * ((float)n + (float)n2 * this.n);
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
        int n2;
        int n3 = color.getRGB();
        int n4 = color2.getRGB();
        Canvas canvas = new Canvas();
        String string = this.code;
        if (this.generateChecksum && this.checksumText) {
            string = BarcodeCodabar.calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            string = string.substring(1, string.length() - 1);
        }
        byte[] arrby = BarcodeCodabar.getBarsCodabar(this.generateChecksum ? BarcodeCodabar.calculateChecksum(this.code) : this.code);
        int n5 = 0;
        for (n2 = 0; n2 < arrby.length; ++n2) {
            n5 += arrby[n2];
        }
        n2 = arrby.length - n5;
        int n6 = n2 + n5 * (int)this.n;
        boolean bl = true;
        int n7 = 0;
        int n8 = (int)this.barHeight;
        int[] arrn = new int[n6 * n8];
        for (n = 0; n < arrby.length; ++n) {
            int n9 = arrby[n] == 0 ? 1 : (int)this.n;
            int n10 = n4;
            if (bl) {
                n10 = n3;
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

