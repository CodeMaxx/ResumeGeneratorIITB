/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;

public class BarcodePostnet
extends Barcode {
    private static final byte[][] BARS = new byte[][]{{1, 1, 0, 0, 0}, {0, 0, 0, 1, 1}, {0, 0, 1, 0, 1}, {0, 0, 1, 1, 0}, {0, 1, 0, 0, 1}, {0, 1, 0, 1, 0}, {0, 1, 1, 0, 0}, {1, 0, 0, 0, 1}, {1, 0, 0, 1, 0}, {1, 0, 1, 0, 0}};

    public BarcodePostnet() {
        this.n = 3.2727273f;
        this.x = 1.4399999f;
        this.barHeight = 9.0f;
        this.size = 3.6000001f;
        this.codeType = 7;
    }

    public static byte[] getBarsPostnet(String string) {
        int n;
        int n2 = 0;
        for (int i = string.length() - 1; i >= 0; --i) {
            n = string.charAt(i) - 48;
            n2 += n;
        }
        string = string + (char)((10 - n2 % 10) % 10 + 48);
        byte[] arrby = new byte[string.length() * 5 + 2];
        arrby[0] = 1;
        arrby[arrby.length - 1] = 1;
        for (n = 0; n < string.length(); ++n) {
            int n3 = string.charAt(n) - 48;
            System.arraycopy(BARS[n3], 0, arrby, n * 5 + 1, 5);
        }
        return arrby;
    }

    public Rectangle getBarcodeSize() {
        float f = (float)((this.code.length() + 1) * 5 + 1) * this.n + this.x;
        return new Rectangle(f, this.barHeight);
    }

    public Rectangle placeBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        if (color != null) {
            pdfContentByte.setColorFill(color);
        }
        byte[] arrby = BarcodePostnet.getBarsPostnet(this.code);
        byte by = 1;
        if (this.codeType == 8) {
            by = 0;
            arrby[0] = 0;
            arrby[arrby.length - 1] = 0;
        }
        float f = 0.0f;
        for (int i = 0; i < arrby.length; ++i) {
            pdfContentByte.rectangle(f, 0.0f, this.x - this.inkSpreading, arrby[i] == by ? this.barHeight : this.size);
            f += this.n;
        }
        pdfContentByte.fill();
        return this.getBarcodeSize();
    }

    public Image createAwtImage(Color color, Color color2) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7 = color.getRGB();
        int n8 = color2.getRGB();
        Canvas canvas = new Canvas();
        int n9 = (int)this.x;
        if (n9 <= 0) {
            n9 = 1;
        }
        if ((n = (int)this.n) <= n9) {
            n = n9 + 1;
        }
        if ((n2 = (int)this.size) <= 0) {
            n2 = 1;
        }
        if ((n5 = (int)this.barHeight) <= n2) {
            n5 = n2 + 1;
        }
        int n10 = ((this.code.length() + 1) * 5 + 1) * n + n9;
        int[] arrn = new int[n10 * n5];
        byte[] arrby = BarcodePostnet.getBarsPostnet(this.code);
        byte by = 1;
        if (this.codeType == 8) {
            by = 0;
            arrby[0] = 0;
            arrby[arrby.length - 1] = 0;
        }
        int n11 = 0;
        for (n4 = 0; n4 < arrby.length; ++n4) {
            n3 = arrby[n4] == by ? 1 : 0;
            for (n6 = 0; n6 < n; ++n6) {
                arrn[n11 + n6] = n3 != 0 && n6 < n9 ? n7 : n8;
            }
            n11 += n;
        }
        n4 = n10 * (n5 - n2);
        for (n3 = n10; n3 < n4; n3 += n10) {
            System.arraycopy(arrn, 0, arrn, n3, n10);
        }
        n11 = n4;
        for (n3 = 0; n3 < arrby.length; ++n3) {
            for (n6 = 0; n6 < n; ++n6) {
                arrn[n11 + n6] = n6 < n9 ? n7 : n8;
            }
            n11 += n;
        }
        for (n3 = n4 + n10; n3 < arrn.length; n3 += n10) {
            System.arraycopy(arrn, n4, arrn, n3, n10);
        }
        Image image = canvas.createImage(new MemoryImageSource(n10, n5, arrn, 0, n10));
        return image;
    }
}

