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

public class Barcode39
extends Barcode {
    private static final byte[][] BARS = new byte[][]{{0, 0, 0, 1, 1, 0, 1, 0, 0}, {1, 0, 0, 1, 0, 0, 0, 0, 1}, {0, 0, 1, 1, 0, 0, 0, 0, 1}, {1, 0, 1, 1, 0, 0, 0, 0, 0}, {0, 0, 0, 1, 1, 0, 0, 0, 1}, {1, 0, 0, 1, 1, 0, 0, 0, 0}, {0, 0, 1, 1, 1, 0, 0, 0, 0}, {0, 0, 0, 1, 0, 0, 1, 0, 1}, {1, 0, 0, 1, 0, 0, 1, 0, 0}, {0, 0, 1, 1, 0, 0, 1, 0, 0}, {1, 0, 0, 0, 0, 1, 0, 0, 1}, {0, 0, 1, 0, 0, 1, 0, 0, 1}, {1, 0, 1, 0, 0, 1, 0, 0, 0}, {0, 0, 0, 0, 1, 1, 0, 0, 1}, {1, 0, 0, 0, 1, 1, 0, 0, 0}, {0, 0, 1, 0, 1, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 1, 1, 0, 1}, {1, 0, 0, 0, 0, 1, 1, 0, 0}, {0, 0, 1, 0, 0, 1, 1, 0, 0}, {0, 0, 0, 0, 1, 1, 1, 0, 0}, {1, 0, 0, 0, 0, 0, 0, 1, 1}, {0, 0, 1, 0, 0, 0, 0, 1, 1}, {1, 0, 1, 0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 1, 0, 0, 1, 1}, {1, 0, 0, 0, 1, 0, 0, 1, 0}, {0, 0, 1, 0, 1, 0, 0, 1, 0}, {0, 0, 0, 0, 0, 0, 1, 1, 1}, {1, 0, 0, 0, 0, 0, 1, 1, 0}, {0, 0, 1, 0, 0, 0, 1, 1, 0}, {0, 0, 0, 0, 1, 0, 1, 1, 0}, {1, 1, 0, 0, 0, 0, 0, 0, 1}, {0, 1, 1, 0, 0, 0, 0, 0, 1}, {1, 1, 1, 0, 0, 0, 0, 0, 0}, {0, 1, 0, 0, 1, 0, 0, 0, 1}, {1, 1, 0, 0, 1, 0, 0, 0, 0}, {0, 1, 1, 0, 1, 0, 0, 0, 0}, {0, 1, 0, 0, 0, 0, 1, 0, 1}, {1, 1, 0, 0, 0, 0, 1, 0, 0}, {0, 1, 1, 0, 0, 0, 1, 0, 0}, {0, 1, 0, 1, 0, 1, 0, 0, 0}, {0, 1, 0, 1, 0, 0, 0, 1, 0}, {0, 1, 0, 0, 0, 1, 0, 1, 0}, {0, 0, 0, 1, 0, 1, 0, 1, 0}, {0, 1, 0, 0, 1, 0, 1, 0, 0}};
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*";
    private static final String EXTENDED = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T";

    public Barcode39() {
        try {
            this.x = 0.8f;
            this.n = 2.0f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.baseline = this.size = 8.0f;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.generateChecksum = false;
            this.checksumText = false;
            this.startStopText = true;
            this.extended = false;
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public static byte[] getBarsCode39(String string) {
        string = "*" + string + "*";
        byte[] arrby = new byte[string.length() * 10 - 1];
        for (int i = 0; i < string.length(); ++i) {
            int n = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".indexOf(string.charAt(i));
            if (n < 0) {
                throw new IllegalArgumentException("The character '" + string.charAt(i) + "' is illegal in code 39.");
            }
            System.arraycopy(BARS[n], 0, arrby, i * 10, 9);
        }
        return arrby;
    }

    public static String getCode39Ex(String string) {
        String string2 = "";
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c > '') {
                throw new IllegalArgumentException("The character '" + c + "' is illegal in code 39 extended.");
            }
            char c2 = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T".charAt(c * 2);
            char c3 = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T".charAt(c * 2 + 1);
            if (c2 != ' ') {
                string2 = string2 + c2;
            }
            string2 = string2 + c3;
        }
        return string2;
    }

    static char getChecksum(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            int n2 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".indexOf(string.charAt(i));
            if (n2 < 0) {
                throw new IllegalArgumentException("The character '" + string.charAt(i) + "' is illegal in code 39.");
            }
            n += n2;
        }
        return "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".charAt(n % 43);
    }

    public Rectangle getBarcodeSize() {
        float f = 0.0f;
        float f2 = 0.0f;
        String string = this.code;
        if (this.extended) {
            string = Barcode39.getCode39Ex(this.code);
        }
        if (this.font != null) {
            f2 = this.baseline > 0.0f ? this.baseline - this.font.getFontDescriptor(3, this.size) : - this.baseline + this.size;
            String string2 = this.code;
            if (this.generateChecksum && this.checksumText) {
                string2 = string2 + Barcode39.getChecksum(string);
            }
            if (this.startStopText) {
                string2 = "*" + string2 + "*";
            }
            f = this.font.getWidthPoint(this.altText != null ? this.altText : string2, this.size);
        }
        int n = string.length() + 2;
        if (this.generateChecksum) {
            ++n;
        }
        float f3 = (float)n * (6.0f * this.x + 3.0f * this.x * this.n) + (float)(n - 1) * this.x;
        f3 = Math.max(f3, f);
        float f4 = this.barHeight + f2;
        return new Rectangle(f3, f4);
    }

    public Rectangle placeBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        String string = this.code;
        float f = 0.0f;
        String string2 = this.code;
        if (this.extended) {
            string2 = Barcode39.getCode39Ex(this.code);
        }
        if (this.font != null) {
            if (this.generateChecksum && this.checksumText) {
                string = string + Barcode39.getChecksum(string2);
            }
            if (this.startStopText) {
                string = "*" + string + "*";
            }
            string = this.altText != null ? this.altText : string;
            f = this.font.getWidthPoint(string, this.size);
        }
        if (this.generateChecksum) {
            string2 = string2 + Barcode39.getChecksum(string2);
        }
        int n = string2.length() + 2;
        float f2 = (float)n * (6.0f * this.x + 3.0f * this.x * this.n) + (float)(n - 1) * this.x;
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
        byte[] arrby = Barcode39.getBarsCode39(string2);
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
        String string = this.code;
        if (this.extended) {
            string = Barcode39.getCode39Ex(this.code);
        }
        if (this.generateChecksum) {
            string = string + Barcode39.getChecksum(string);
        }
        int n4 = string.length() + 2;
        int n5 = (int)this.n;
        int n6 = n4 * (6 + 3 * n5) + (n4 - 1);
        byte[] arrby = Barcode39.getBarsCode39(string);
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

