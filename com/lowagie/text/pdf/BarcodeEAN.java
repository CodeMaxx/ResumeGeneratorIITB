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
import java.util.Arrays;

public class BarcodeEAN
extends Barcode {
    private static final int[] GUARD_EMPTY = new int[0];
    private static final int[] GUARD_UPCA = new int[]{0, 2, 4, 6, 28, 30, 52, 54, 56, 58};
    private static final int[] GUARD_EAN13 = new int[]{0, 2, 28, 30, 56, 58};
    private static final int[] GUARD_EAN8 = new int[]{0, 2, 20, 22, 40, 42};
    private static final int[] GUARD_UPCE = new int[]{0, 2, 28, 30, 32};
    private static final float[] TEXTPOS_EAN13 = new float[]{6.5f, 13.5f, 20.5f, 27.5f, 34.5f, 41.5f, 53.5f, 60.5f, 67.5f, 74.5f, 81.5f, 88.5f};
    private static final float[] TEXTPOS_EAN8 = new float[]{6.5f, 13.5f, 20.5f, 27.5f, 39.5f, 46.5f, 53.5f, 60.5f};
    private static final byte[][] BARS = new byte[][]{{3, 2, 1, 1}, {2, 2, 2, 1}, {2, 1, 2, 2}, {1, 4, 1, 1}, {1, 1, 3, 2}, {1, 2, 3, 1}, {1, 1, 1, 4}, {1, 3, 1, 2}, {1, 2, 1, 3}, {3, 1, 1, 2}};
    private static final int TOTALBARS_EAN13 = 59;
    private static final int TOTALBARS_EAN8 = 43;
    private static final int TOTALBARS_UPCE = 33;
    private static final int TOTALBARS_SUPP2 = 13;
    private static final int TOTALBARS_SUPP5 = 31;
    private static final int ODD = 0;
    private static final int EVEN = 1;
    private static final byte[][] PARITY13 = new byte[][]{{0, 0, 0, 0, 0, 0}, {0, 0, 1, 0, 1, 1}, {0, 0, 1, 1, 0, 1}, {0, 0, 1, 1, 1, 0}, {0, 1, 0, 0, 1, 1}, {0, 1, 1, 0, 0, 1}, {0, 1, 1, 1, 0, 0}, {0, 1, 0, 1, 0, 1}, {0, 1, 0, 1, 1, 0}, {0, 1, 1, 0, 1, 0}};
    private static final byte[][] PARITY2 = new byte[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
    private static final byte[][] PARITY5 = new byte[][]{{1, 1, 0, 0, 0}, {1, 0, 1, 0, 0}, {1, 0, 0, 1, 0}, {1, 0, 0, 0, 1}, {0, 1, 1, 0, 0}, {0, 0, 1, 1, 0}, {0, 0, 0, 1, 1}, {0, 1, 0, 1, 0}, {0, 1, 0, 0, 1}, {0, 0, 1, 0, 1}};
    private static final byte[][] PARITYE = new byte[][]{{1, 1, 1, 0, 0, 0}, {1, 1, 0, 1, 0, 0}, {1, 1, 0, 0, 1, 0}, {1, 1, 0, 0, 0, 1}, {1, 0, 1, 1, 0, 0}, {1, 0, 0, 1, 1, 0}, {1, 0, 0, 0, 1, 1}, {1, 0, 1, 0, 1, 0}, {1, 0, 1, 0, 0, 1}, {1, 0, 0, 1, 0, 1}};

    public BarcodeEAN() {
        try {
            this.x = 0.8f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.baseline = this.size = 8.0f;
            this.barHeight = this.size * 3.0f;
            this.guardBars = true;
            this.codeType = 1;
            this.code = "";
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public static int calculateEANParity(String string) {
        int n = 3;
        int n2 = 0;
        for (int i = string.length() - 1; i >= 0; --i) {
            int n3 = string.charAt(i) - 48;
            n2 += n * n3;
            n ^= 2;
        }
        return (10 - n2 % 10) % 10;
    }

    public static String convertUPCAtoUPCE(String string) {
        if (string.length() != 12 || !string.startsWith("0") && !string.startsWith("1")) {
            return null;
        }
        if (string.substring(3, 6).equals("000") || string.substring(3, 6).equals("100") || string.substring(3, 6).equals("200")) {
            if (string.substring(6, 8).equals("00")) {
                return string.substring(0, 1) + string.substring(1, 3) + string.substring(8, 11) + string.substring(3, 4) + string.substring(11);
            }
        } else if (string.substring(4, 6).equals("00")) {
            if (string.substring(6, 9).equals("000")) {
                return string.substring(0, 1) + string.substring(1, 4) + string.substring(9, 11) + "3" + string.substring(11);
            }
        } else if (string.substring(5, 6).equals("0")) {
            if (string.substring(6, 10).equals("0000")) {
                return string.substring(0, 1) + string.substring(1, 5) + string.substring(10, 11) + "4" + string.substring(11);
            }
        } else if (string.charAt(10) >= '5' && string.substring(6, 10).equals("0000")) {
            return string.substring(0, 1) + string.substring(1, 6) + string.substring(10, 11) + string.substring(11);
        }
        return null;
    }

    public static byte[] getBarsEAN13(String string) {
        int n;
        int n2;
        byte[] arrby;
        int[] arrn = new int[string.length()];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = string.charAt(i) - 48;
        }
        byte[] arrby2 = new byte[59];
        int n3 = 0;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        byte[] arrby3 = PARITY13[arrn[0]];
        for (n2 = 0; n2 < arrby3.length; ++n2) {
            n = arrn[n2 + 1];
            arrby = BARS[n];
            if (arrby3[n2] == 0) {
                arrby2[n3++] = arrby[0];
                arrby2[n3++] = arrby[1];
                arrby2[n3++] = arrby[2];
                arrby2[n3++] = arrby[3];
                continue;
            }
            arrby2[n3++] = arrby[3];
            arrby2[n3++] = arrby[2];
            arrby2[n3++] = arrby[1];
            arrby2[n3++] = arrby[0];
        }
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        for (n2 = 7; n2 < 13; ++n2) {
            n = arrn[n2];
            arrby = BARS[n];
            arrby2[n3++] = arrby[0];
            arrby2[n3++] = arrby[1];
            arrby2[n3++] = arrby[2];
            arrby2[n3++] = arrby[3];
        }
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        return arrby2;
    }

    public static byte[] getBarsEAN8(String string) {
        byte[] arrby;
        int n;
        int n2;
        int[] arrn = new int[string.length()];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = string.charAt(i) - 48;
        }
        byte[] arrby2 = new byte[43];
        int n3 = 0;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        for (n2 = 0; n2 < 4; ++n2) {
            n = arrn[n2];
            arrby = BARS[n];
            arrby2[n3++] = arrby[0];
            arrby2[n3++] = arrby[1];
            arrby2[n3++] = arrby[2];
            arrby2[n3++] = arrby[3];
        }
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        for (n2 = 4; n2 < 8; ++n2) {
            n = arrn[n2];
            arrby = BARS[n];
            arrby2[n3++] = arrby[0];
            arrby2[n3++] = arrby[1];
            arrby2[n3++] = arrby[2];
            arrby2[n3++] = arrby[3];
        }
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        arrby2[n3++] = 1;
        return arrby2;
    }

    public static byte[] getBarsUPCE(String string) {
        int[] arrn = new int[string.length()];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = string.charAt(i) - 48;
        }
        byte[] arrby = new byte[33];
        boolean bl = arrn[0] != 0;
        int n = 0;
        arrby[n++] = 1;
        arrby[n++] = 1;
        arrby[n++] = 1;
        byte[] arrby2 = PARITYE[arrn[arrn.length - 1]];
        for (int j = 1; j < arrn.length - 1; ++j) {
            int n2 = arrn[j];
            byte[] arrby3 = BARS[n2];
            if (arrby2[j - 1] == (bl ? 1 : 0)) {
                arrby[n++] = arrby3[0];
                arrby[n++] = arrby3[1];
                arrby[n++] = arrby3[2];
                arrby[n++] = arrby3[3];
                continue;
            }
            arrby[n++] = arrby3[3];
            arrby[n++] = arrby3[2];
            arrby[n++] = arrby3[1];
            arrby[n++] = arrby3[0];
        }
        arrby[n++] = 1;
        arrby[n++] = 1;
        arrby[n++] = 1;
        arrby[n++] = 1;
        arrby[n++] = 1;
        arrby[n++] = 1;
        return arrby;
    }

    public static byte[] getBarsSupplemental2(String string) {
        int[] arrn = new int[2];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = string.charAt(i) - 48;
        }
        byte[] arrby = new byte[13];
        int n = 0;
        int n2 = (arrn[0] * 10 + arrn[1]) % 4;
        arrby[n++] = 1;
        arrby[n++] = 1;
        arrby[n++] = 2;
        byte[] arrby2 = PARITY2[n2];
        for (int j = 0; j < arrby2.length; ++j) {
            if (j == 1) {
                arrby[n++] = 1;
                arrby[n++] = 1;
            }
            int n3 = arrn[j];
            byte[] arrby3 = BARS[n3];
            if (arrby2[j] == 0) {
                arrby[n++] = arrby3[0];
                arrby[n++] = arrby3[1];
                arrby[n++] = arrby3[2];
                arrby[n++] = arrby3[3];
                continue;
            }
            arrby[n++] = arrby3[3];
            arrby[n++] = arrby3[2];
            arrby[n++] = arrby3[1];
            arrby[n++] = arrby3[0];
        }
        return arrby;
    }

    public static byte[] getBarsSupplemental5(String string) {
        int[] arrn = new int[5];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = string.charAt(i) - 48;
        }
        byte[] arrby = new byte[31];
        int n = 0;
        int n2 = ((arrn[0] + arrn[2] + arrn[4]) * 3 + (arrn[1] + arrn[3]) * 9) % 10;
        arrby[n++] = 1;
        arrby[n++] = 1;
        arrby[n++] = 2;
        byte[] arrby2 = PARITY5[n2];
        for (int j = 0; j < arrby2.length; ++j) {
            if (j != 0) {
                arrby[n++] = 1;
                arrby[n++] = 1;
            }
            int n3 = arrn[j];
            byte[] arrby3 = BARS[n3];
            if (arrby2[j] == 0) {
                arrby[n++] = arrby3[0];
                arrby[n++] = arrby3[1];
                arrby[n++] = arrby3[2];
                arrby[n++] = arrby3[3];
                continue;
            }
            arrby[n++] = arrby3[3];
            arrby[n++] = arrby3[2];
            arrby[n++] = arrby3[1];
            arrby[n++] = arrby3[0];
        }
        return arrby;
    }

    public Rectangle getBarcodeSize() {
        float f = 0.0f;
        float f2 = this.barHeight;
        if (this.font != null) {
            f2 = this.baseline <= 0.0f ? (f2 += - this.baseline + this.size) : (f2 += this.baseline - this.font.getFontDescriptor(3, this.size));
        }
        switch (this.codeType) {
            case 1: {
                f = this.x * 95.0f;
                if (this.font == null) break;
                f += this.font.getWidthPoint(this.code.charAt(0), this.size);
                break;
            }
            case 2: {
                f = this.x * 67.0f;
                break;
            }
            case 3: {
                f = this.x * 95.0f;
                if (this.font == null) break;
                f += this.font.getWidthPoint(this.code.charAt(0), this.size) + this.font.getWidthPoint(this.code.charAt(11), this.size);
                break;
            }
            case 4: {
                f = this.x * 51.0f;
                if (this.font == null) break;
                f += this.font.getWidthPoint(this.code.charAt(0), this.size) + this.font.getWidthPoint(this.code.charAt(7), this.size);
                break;
            }
            case 5: {
                f = this.x * 20.0f;
                break;
            }
            case 6: {
                f = this.x * 47.0f;
                break;
            }
            default: {
                throw new RuntimeException("Invalid code type.");
            }
        }
        return new Rectangle(f, f2);
    }

    public Rectangle placeBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        int n;
        Rectangle rectangle = this.getBarcodeSize();
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        if (this.font != null) {
            if (this.baseline <= 0.0f) {
                f3 = this.barHeight - this.baseline;
            } else {
                f3 = - this.font.getFontDescriptor(3, this.size);
                f2 = f3 + this.baseline;
            }
        }
        switch (this.codeType) {
            case 1: 
            case 3: 
            case 4: {
                if (this.font == null) break;
                f += this.font.getWidthPoint(this.code.charAt(0), this.size);
            }
        }
        byte[] arrby = null;
        int[] arrn = GUARD_EMPTY;
        switch (this.codeType) {
            case 1: {
                arrby = BarcodeEAN.getBarsEAN13(this.code);
                arrn = GUARD_EAN13;
                break;
            }
            case 2: {
                arrby = BarcodeEAN.getBarsEAN8(this.code);
                arrn = GUARD_EAN8;
                break;
            }
            case 3: {
                arrby = BarcodeEAN.getBarsEAN13("0" + this.code);
                arrn = GUARD_UPCA;
                break;
            }
            case 4: {
                arrby = BarcodeEAN.getBarsUPCE(this.code);
                arrn = GUARD_UPCE;
                break;
            }
            case 5: {
                arrby = BarcodeEAN.getBarsSupplemental2(this.code);
                break;
            }
            case 6: {
                arrby = BarcodeEAN.getBarsSupplemental5(this.code);
            }
        }
        float f4 = f;
        boolean bl = true;
        float f5 = 0.0f;
        if (this.font != null && this.baseline > 0.0f && this.guardBars) {
            f5 = this.baseline / 2.0f;
        }
        if (color != null) {
            pdfContentByte.setColorFill(color);
        }
        for (n = 0; n < arrby.length; ++n) {
            float f6 = (float)arrby[n] * this.x;
            if (bl) {
                if (Arrays.binarySearch(arrn, n) >= 0) {
                    pdfContentByte.rectangle(f, f2 - f5, f6 - this.inkSpreading, this.barHeight + f5);
                } else {
                    pdfContentByte.rectangle(f, f2, f6 - this.inkSpreading, this.barHeight);
                }
            }
            bl = !bl;
            f += f6;
        }
        pdfContentByte.fill();
        if (this.font != null) {
            if (color2 != null) {
                pdfContentByte.setColorFill(color2);
            }
            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(this.font, this.size);
            switch (this.codeType) {
                case 1: {
                    pdfContentByte.setTextMatrix(0.0f, f3);
                    pdfContentByte.showText(this.code.substring(0, 1));
                    for (n = 1; n < 13; ++n) {
                        String string = this.code.substring(n, n + 1);
                        float f7 = this.font.getWidthPoint(string, this.size);
                        float f8 = f4 + TEXTPOS_EAN13[n - 1] * this.x - f7 / 2.0f;
                        pdfContentByte.setTextMatrix(f8, f3);
                        pdfContentByte.showText(string);
                    }
                    break;
                }
                case 2: {
                    for (n = 0; n < 8; ++n) {
                        String string = this.code.substring(n, n + 1);
                        float f9 = this.font.getWidthPoint(string, this.size);
                        float f10 = TEXTPOS_EAN8[n] * this.x - f9 / 2.0f;
                        pdfContentByte.setTextMatrix(f10, f3);
                        pdfContentByte.showText(string);
                    }
                    break;
                }
                case 3: {
                    pdfContentByte.setTextMatrix(0.0f, f3);
                    pdfContentByte.showText(this.code.substring(0, 1));
                    for (n = 1; n < 11; ++n) {
                        String string = this.code.substring(n, n + 1);
                        float f11 = this.font.getWidthPoint(string, this.size);
                        float f12 = f4 + TEXTPOS_EAN13[n] * this.x - f11 / 2.0f;
                        pdfContentByte.setTextMatrix(f12, f3);
                        pdfContentByte.showText(string);
                    }
                    pdfContentByte.setTextMatrix(f4 + this.x * 95.0f, f3);
                    pdfContentByte.showText(this.code.substring(11, 12));
                    break;
                }
                case 4: {
                    pdfContentByte.setTextMatrix(0.0f, f3);
                    pdfContentByte.showText(this.code.substring(0, 1));
                    for (n = 1; n < 7; ++n) {
                        String string = this.code.substring(n, n + 1);
                        float f13 = this.font.getWidthPoint(string, this.size);
                        float f14 = f4 + TEXTPOS_EAN13[n - 1] * this.x - f13 / 2.0f;
                        pdfContentByte.setTextMatrix(f14, f3);
                        pdfContentByte.showText(string);
                    }
                    pdfContentByte.setTextMatrix(f4 + this.x * 51.0f, f3);
                    pdfContentByte.showText(this.code.substring(7, 8));
                    break;
                }
                case 5: 
                case 6: {
                    for (n = 0; n < this.code.length(); ++n) {
                        String string = this.code.substring(n, n + 1);
                        float f15 = this.font.getWidthPoint(string, this.size);
                        float f16 = (7.5f + (float)(9 * n)) * this.x - f15 / 2.0f;
                        pdfContentByte.setTextMatrix(f16, f3);
                        pdfContentByte.showText(string);
                    }
                    break;
                }
            }
            pdfContentByte.endText();
        }
        return rectangle;
    }

    public Image createAwtImage(Color color, Color color2) {
        int n;
        int n2 = color.getRGB();
        int n3 = color2.getRGB();
        Canvas canvas = new Canvas();
        int n4 = 0;
        byte[] arrby = null;
        switch (this.codeType) {
            case 1: {
                arrby = BarcodeEAN.getBarsEAN13(this.code);
                n4 = 95;
                break;
            }
            case 2: {
                arrby = BarcodeEAN.getBarsEAN8(this.code);
                n4 = 67;
                break;
            }
            case 3: {
                arrby = BarcodeEAN.getBarsEAN13("0" + this.code);
                n4 = 95;
                break;
            }
            case 4: {
                arrby = BarcodeEAN.getBarsUPCE(this.code);
                n4 = 51;
                break;
            }
            case 5: {
                arrby = BarcodeEAN.getBarsSupplemental2(this.code);
                n4 = 20;
                break;
            }
            case 6: {
                arrby = BarcodeEAN.getBarsSupplemental5(this.code);
                n4 = 47;
                break;
            }
            default: {
                throw new RuntimeException("Invalid code type.");
            }
        }
        boolean bl = true;
        int n5 = 0;
        int n6 = (int)this.barHeight;
        int[] arrn = new int[n4 * n6];
        for (n = 0; n < arrby.length; ++n) {
            int n7 = arrby[n];
            int n8 = n3;
            if (bl) {
                n8 = n2;
            }
            bl = !bl;
            for (int i = 0; i < n7; ++i) {
                arrn[n5++] = n8;
            }
        }
        for (n = n4; n < arrn.length; n += n4) {
            System.arraycopy(arrn, 0, arrn, n, n4);
        }
        Image image = canvas.createImage(new MemoryImageSource(n4, n6, arrn, 0, n4));
        return image;
    }
}

