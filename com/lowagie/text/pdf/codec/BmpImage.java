/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

import com.lowagie.text.BadElementException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgRaw;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class BmpImage {
    private InputStream inputStream;
    private long bitmapFileSize;
    private long bitmapOffset;
    private long compression;
    private long imageSize;
    private byte[] palette;
    private int imageType;
    private int numBands;
    private boolean isBottomUp;
    private int bitsPerPixel;
    private int redMask;
    private int greenMask;
    private int blueMask;
    private int alphaMask;
    public HashMap properties = new HashMap();
    private long xPelsPerMeter;
    private long yPelsPerMeter;
    private static final int VERSION_2_1_BIT = 0;
    private static final int VERSION_2_4_BIT = 1;
    private static final int VERSION_2_8_BIT = 2;
    private static final int VERSION_2_24_BIT = 3;
    private static final int VERSION_3_1_BIT = 4;
    private static final int VERSION_3_4_BIT = 5;
    private static final int VERSION_3_8_BIT = 6;
    private static final int VERSION_3_24_BIT = 7;
    private static final int VERSION_3_NT_16_BIT = 8;
    private static final int VERSION_3_NT_32_BIT = 9;
    private static final int VERSION_4_1_BIT = 10;
    private static final int VERSION_4_4_BIT = 11;
    private static final int VERSION_4_8_BIT = 12;
    private static final int VERSION_4_16_BIT = 13;
    private static final int VERSION_4_24_BIT = 14;
    private static final int VERSION_4_32_BIT = 15;
    private static final int LCS_CALIBRATED_RGB = 0;
    private static final int LCS_sRGB = 1;
    private static final int LCS_CMYK = 2;
    private static final int BI_RGB = 0;
    private static final int BI_RLE8 = 1;
    private static final int BI_RLE4 = 2;
    private static final int BI_BITFIELDS = 3;
    int width;
    int height;

    BmpImage(InputStream inputStream, boolean bl, int n) throws IOException {
        this.bitmapFileSize = n;
        this.bitmapOffset = 0;
        this.process(inputStream, bl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Image getImage(URL uRL) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = uRL.openStream();
            Image image = BmpImage.getImage(inputStream);
            image.setUrl(uRL);
            Image image2 = image;
            return image2;
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static Image getImage(InputStream inputStream) throws IOException {
        return BmpImage.getImage(inputStream, false, 0);
    }

    public static Image getImage(InputStream inputStream, boolean bl, int n) throws IOException {
        BmpImage bmpImage = new BmpImage(inputStream, bl, n);
        try {
            Image image = bmpImage.getImage();
            image.setDpi((int)((double)bmpImage.xPelsPerMeter * 0.0254 + 0.5), (int)((double)bmpImage.yPelsPerMeter * 0.0254 + 0.5));
            image.setOriginalType(4);
            return image;
        }
        catch (BadElementException var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    public static Image getImage(String string) throws IOException {
        return BmpImage.getImage(Utilities.toURL(string));
    }

    public static Image getImage(byte[] arrby) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Image image = BmpImage.getImage(byteArrayInputStream);
        image.setOriginalData(arrby);
        return image;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    protected void process(InputStream var1_1, boolean var2_2) throws IOException {
        this.inputStream = var2_2 != false || var1_1 instanceof BufferedInputStream != false ? var1_1 : new BufferedInputStream(var1_1);
        if (!var2_2) {
            if (this.readUnsignedByte(this.inputStream) != 66) throw new RuntimeException("Invalid magic value for BMP file.");
            if (this.readUnsignedByte(this.inputStream) != 77) {
                throw new RuntimeException("Invalid magic value for BMP file.");
            }
            this.bitmapFileSize = this.readDWord(this.inputStream);
            this.readWord(this.inputStream);
            this.readWord(this.inputStream);
            this.bitmapOffset = this.readDWord(this.inputStream);
        }
        if ((var3_3 = this.readDWord(this.inputStream)) == 12) {
            this.width = this.readWord(this.inputStream);
            this.height = this.readWord(this.inputStream);
        } else {
            this.width = this.readLong(this.inputStream);
            this.height = this.readLong(this.inputStream);
        }
        var5_4 = this.readWord(this.inputStream);
        this.bitsPerPixel = this.readWord(this.inputStream);
        this.properties.put("color_planes", new Integer(var5_4));
        this.properties.put("bits_per_pixel", new Integer(this.bitsPerPixel));
        this.numBands = 3;
        if (this.bitmapOffset == 0) {
            this.bitmapOffset = var3_3;
        }
        if (var3_3 != 12) ** GOTO lbl54
        this.properties.put("bmp_version", "BMP v. 2.x");
        if (this.bitsPerPixel == 1) {
            this.imageType = 0;
        } else if (this.bitsPerPixel == 4) {
            this.imageType = 1;
        } else if (this.bitsPerPixel == 8) {
            this.imageType = 2;
        } else if (this.bitsPerPixel == 24) {
            this.imageType = 3;
        }
        var6_5 = (int)((this.bitmapOffset - 14 - var3_3) / 3);
        var7_9 = var6_5 * 3;
        if (this.bitmapOffset == var3_3) {
            switch (this.imageType) {
                case 0: {
                    var7_9 = 6;
                    break;
                }
                case 1: {
                    var7_9 = 48;
                    break;
                }
                case 2: {
                    var7_9 = 768;
                    break;
                }
                case 3: {
                    var7_9 = 0;
                }
            }
            this.bitmapOffset = var3_3 + (long)var7_9;
        }
        this.readPalette(var7_9);
        ** GOTO lbl232
lbl54: // 1 sources:
        this.compression = this.readDWord(this.inputStream);
        this.imageSize = this.readDWord(this.inputStream);
        this.xPelsPerMeter = this.readLong(this.inputStream);
        this.yPelsPerMeter = this.readLong(this.inputStream);
        var6_6 = this.readDWord(this.inputStream);
        var8_12 = this.readDWord(this.inputStream);
        switch ((int)this.compression) {
            case 0: {
                this.properties.put("compression", "BI_RGB");
                break;
            }
            case 1: {
                this.properties.put("compression", "BI_RLE8");
                break;
            }
            case 2: {
                this.properties.put("compression", "BI_RLE4");
                break;
            }
            case 3: {
                this.properties.put("compression", "BI_BITFIELDS");
            }
        }
        this.properties.put("x_pixels_per_meter", new Long(this.xPelsPerMeter));
        this.properties.put("y_pixels_per_meter", new Long(this.yPelsPerMeter));
        this.properties.put("colors_used", new Long(var6_6));
        this.properties.put("colors_important", new Long(var8_12));
        if (var3_3 != 40) ** GOTO lbl144
        switch ((int)this.compression) {
            case 0: 
            case 1: 
            case 2: {
                if (this.bitsPerPixel == 1) {
                    this.imageType = 4;
                } else if (this.bitsPerPixel == 4) {
                    this.imageType = 5;
                } else if (this.bitsPerPixel == 8) {
                    this.imageType = 6;
                } else if (this.bitsPerPixel == 24) {
                    this.imageType = 7;
                } else if (this.bitsPerPixel == 16) {
                    this.imageType = 8;
                    this.redMask = 31744;
                    this.greenMask = 992;
                    this.blueMask = 31;
                    this.properties.put("red_mask", new Integer(this.redMask));
                    this.properties.put("green_mask", new Integer(this.greenMask));
                    this.properties.put("blue_mask", new Integer(this.blueMask));
                } else if (this.bitsPerPixel == 32) {
                    this.imageType = 9;
                    this.redMask = 16711680;
                    this.greenMask = 65280;
                    this.blueMask = 255;
                    this.properties.put("red_mask", new Integer(this.redMask));
                    this.properties.put("green_mask", new Integer(this.greenMask));
                    this.properties.put("blue_mask", new Integer(this.blueMask));
                }
                var10_15 = (int)((this.bitmapOffset - 14 - var3_3) / 4);
                var11_17 = var10_15 * 4;
                if (this.bitmapOffset == var3_3) {
                    switch (this.imageType) {
                        case 4: {
                            var11_17 = (int)(var6_6 == 0 ? 2 : var6_6) * 4;
                            ** break;
                        }
                        case 5: {
                            var11_17 = (int)(var6_6 == 0 ? 16 : var6_6) * 4;
                            ** break;
                        }
                        case 6: {
                            var11_17 = (int)(var6_6 == 0 ? 256 : var6_6) * 4;
                            ** break;
                        }
                    }
                    var11_17 = 0;
lbl122: // 4 sources:
                    this.bitmapOffset = var3_3 + (long)var11_17;
                }
                this.readPalette(var11_17);
                this.properties.put("bmp_version", "BMP v. 3.x");
                ** GOTO lbl232
            }
            case 3: {
                if (this.bitsPerPixel == 16) {
                    this.imageType = 8;
                } else if (this.bitsPerPixel == 32) {
                    this.imageType = 9;
                }
                this.redMask = (int)this.readDWord(this.inputStream);
                this.greenMask = (int)this.readDWord(this.inputStream);
                this.blueMask = (int)this.readDWord(this.inputStream);
                this.properties.put("red_mask", new Integer(this.redMask));
                this.properties.put("green_mask", new Integer(this.greenMask));
                this.properties.put("blue_mask", new Integer(this.blueMask));
                if (var6_6 != 0) {
                    var11_17 = (int)var6_6 * 4;
                    this.readPalette(var11_17);
                }
                this.properties.put("bmp_version", "BMP v. 3.x NT");
                ** GOTO lbl232
            }
        }
        throw new RuntimeException("Invalid compression specified in BMP file.");
lbl144: // 1 sources:
        if (var3_3 != 108) {
            this.properties.put("bmp_version", "BMP v. 5.x");
            throw new RuntimeException("BMP version 5 not implemented yet.");
        }
        this.properties.put("bmp_version", "BMP v. 4.x");
        this.redMask = (int)this.readDWord(this.inputStream);
        this.greenMask = (int)this.readDWord(this.inputStream);
        this.blueMask = (int)this.readDWord(this.inputStream);
        this.alphaMask = (int)this.readDWord(this.inputStream);
        var10_16 = this.readDWord(this.inputStream);
        var12_18 = this.readLong(this.inputStream);
        var13_19 = this.readLong(this.inputStream);
        var14_20 = this.readLong(this.inputStream);
        var15_21 = this.readLong(this.inputStream);
        var16_22 = this.readLong(this.inputStream);
        var17_23 = this.readLong(this.inputStream);
        var18_24 = this.readLong(this.inputStream);
        var19_25 = this.readLong(this.inputStream);
        var20_26 = this.readLong(this.inputStream);
        var21_27 = this.readDWord(this.inputStream);
        var23_28 = this.readDWord(this.inputStream);
        var25_29 = this.readDWord(this.inputStream);
        if (this.bitsPerPixel == 1) {
            this.imageType = 10;
        } else if (this.bitsPerPixel == 4) {
            this.imageType = 11;
        } else if (this.bitsPerPixel == 8) {
            this.imageType = 12;
        } else if (this.bitsPerPixel == 16) {
            this.imageType = 13;
            if ((int)this.compression == 0) {
                this.redMask = 31744;
                this.greenMask = 992;
                this.blueMask = 31;
            }
        } else if (this.bitsPerPixel == 24) {
            this.imageType = 14;
        } else if (this.bitsPerPixel == 32) {
            this.imageType = 15;
            if ((int)this.compression == 0) {
                this.redMask = 16711680;
                this.greenMask = 65280;
                this.blueMask = 255;
            }
        }
        this.properties.put("red_mask", new Integer(this.redMask));
        this.properties.put("green_mask", new Integer(this.greenMask));
        this.properties.put("blue_mask", new Integer(this.blueMask));
        this.properties.put("alpha_mask", new Integer(this.alphaMask));
        var27_30 = (int)((this.bitmapOffset - 14 - var3_3) / 4);
        var28_31 = var27_30 * 4;
        if (this.bitmapOffset == var3_3) {
            switch (this.imageType) {
                case 10: {
                    var28_31 = (int)(var6_6 == 0 ? 2 : var6_6) * 4;
                    ** break;
                }
                case 11: {
                    var28_31 = (int)(var6_6 == 0 ? 16 : var6_6) * 4;
                    ** break;
                }
                case 12: {
                    var28_31 = (int)(var6_6 == 0 ? 256 : var6_6) * 4;
                    ** break;
                }
            }
            var28_31 = 0;
lbl208: // 4 sources:
            this.bitmapOffset = var3_3 + (long)var28_31;
        }
        this.readPalette(var28_31);
        switch ((int)var10_16) {
            case 0: {
                this.properties.put("color_space", "LCS_CALIBRATED_RGB");
                this.properties.put("redX", new Integer(var12_18));
                this.properties.put("redY", new Integer(var13_19));
                this.properties.put("redZ", new Integer(var14_20));
                this.properties.put("greenX", new Integer(var15_21));
                this.properties.put("greenY", new Integer(var16_22));
                this.properties.put("greenZ", new Integer(var17_23));
                this.properties.put("blueX", new Integer(var18_24));
                this.properties.put("blueY", new Integer(var19_25));
                this.properties.put("blueZ", new Integer(var20_26));
                this.properties.put("gamma_red", new Long(var21_27));
                this.properties.put("gamma_green", new Long(var23_28));
                this.properties.put("gamma_blue", new Long(var25_29));
                throw new RuntimeException("Not implemented yet.");
            }
            case 1: {
                this.properties.put("color_space", "LCS_sRGB");
                break;
            }
            case 2: {
                this.properties.put("color_space", "LCS_CMYK");
                throw new RuntimeException("Not implemented yet.");
            }
        }
lbl232: // 5 sources:
        if (this.height > 0) {
            this.isBottomUp = true;
        } else {
            this.isBottomUp = false;
            this.height = Math.abs(this.height);
        }
        if (this.bitsPerPixel == 1 || this.bitsPerPixel == 4 || this.bitsPerPixel == 8) {
            this.numBands = 1;
            if (this.imageType == 0 || this.imageType == 1 || this.imageType == 2) {
                var9_32 = this.palette.length / 3;
                if (var9_32 > 256) {
                    var9_32 = 256;
                }
                var6_7 = new byte[var9_32];
                var7_10 = new byte[var9_32];
                var8_13 = new byte[var9_32];
                var11_17 = 0;
                while (var11_17 < var9_32) {
                    var10_15 = 3 * var11_17;
                    var8_13[var11_17] = this.palette[var10_15];
                    var7_10[var11_17] = this.palette[var10_15 + 1];
                    var6_7[var11_17] = this.palette[var10_15 + 2];
                    ++var11_17;
                }
                return;
            }
            var9_33 = this.palette.length / 4;
            if (var9_33 > 256) {
                var9_33 = 256;
            }
            var6_8 = new byte[var9_33];
            var7_11 = new byte[var9_33];
            var8_14 = new byte[var9_33];
            var11_17 = 0;
            while (var11_17 < var9_33) {
                var10_15 = 4 * var11_17;
                var8_14[var11_17] = this.palette[var10_15];
                var7_11[var11_17] = this.palette[var10_15 + 1];
                var6_8[var11_17] = this.palette[var10_15 + 2];
                ++var11_17;
            }
            return;
        }
        if (this.bitsPerPixel == 16) {
            this.numBands = 3;
            return;
        }
        if (this.bitsPerPixel != 32) {
            this.numBands = 3;
            return;
        }
        this.numBands = this.alphaMask == 0 ? 3 : 4;
    }

    private byte[] getPalette(int n) {
        if (this.palette == null) {
            return null;
        }
        byte[] arrby = new byte[this.palette.length / n * 3];
        int n2 = this.palette.length / n;
        for (int i = 0; i < n2; ++i) {
            int n3 = i * n;
            int n4 = i * 3;
            arrby[n4 + 2] = this.palette[n3++];
            arrby[n4 + 1] = this.palette[n3++];
            arrby[n4] = this.palette[n3];
        }
        return arrby;
    }

    private Image getImage() throws IOException, BadElementException {
        byte[] arrby = null;
        switch (this.imageType) {
            case 0: {
                return this.read1Bit(3);
            }
            case 1: {
                return this.read4Bit(3);
            }
            case 2: {
                return this.read8Bit(3);
            }
            case 3: {
                arrby = new byte[this.width * this.height * 3];
                this.read24Bit(arrby);
                return new ImgRaw(this.width, this.height, 3, 8, arrby);
            }
            case 4: {
                return this.read1Bit(4);
            }
            case 5: {
                switch ((int)this.compression) {
                    case 0: {
                        return this.read4Bit(4);
                    }
                    case 2: {
                        return this.readRLE4();
                    }
                }
                throw new RuntimeException("Invalid compression specified for BMP file.");
            }
            case 6: {
                switch ((int)this.compression) {
                    case 0: {
                        return this.read8Bit(4);
                    }
                    case 1: {
                        return this.readRLE8();
                    }
                }
                throw new RuntimeException("Invalid compression specified for BMP file.");
            }
            case 7: {
                arrby = new byte[this.width * this.height * 3];
                this.read24Bit(arrby);
                return new ImgRaw(this.width, this.height, 3, 8, arrby);
            }
            case 8: {
                return this.read1632Bit(false);
            }
            case 9: {
                return this.read1632Bit(true);
            }
            case 10: {
                return this.read1Bit(4);
            }
            case 11: {
                switch ((int)this.compression) {
                    case 0: {
                        return this.read4Bit(4);
                    }
                    case 2: {
                        return this.readRLE4();
                    }
                }
                throw new RuntimeException("Invalid compression specified for BMP file.");
            }
            case 12: {
                switch ((int)this.compression) {
                    case 0: {
                        return this.read8Bit(4);
                    }
                    case 1: {
                        return this.readRLE8();
                    }
                }
                throw new RuntimeException("Invalid compression specified for BMP file.");
            }
            case 13: {
                return this.read1632Bit(false);
            }
            case 14: {
                arrby = new byte[this.width * this.height * 3];
                this.read24Bit(arrby);
                return new ImgRaw(this.width, this.height, 3, 8, arrby);
            }
            case 15: {
                return this.read1632Bit(true);
            }
        }
        return null;
    }

    private Image indexedModel(byte[] arrby, int n, int n2) throws BadElementException {
        ImgRaw imgRaw = new ImgRaw(this.width, this.height, 1, n, arrby);
        PdfArray pdfArray = new PdfArray();
        pdfArray.add(PdfName.INDEXED);
        pdfArray.add(PdfName.DEVICERGB);
        byte[] arrby2 = this.getPalette(n2);
        int n3 = arrby2.length;
        pdfArray.add(new PdfNumber(n3 / 3 - 1));
        pdfArray.add(new PdfString(arrby2));
        PdfDictionary pdfDictionary = new PdfDictionary();
        pdfDictionary.put(PdfName.COLORSPACE, pdfArray);
        imgRaw.setAdditional(pdfDictionary);
        return imgRaw;
    }

    private void readPalette(int n) throws IOException {
        int n2;
        if (n == 0) {
            return;
        }
        this.palette = new byte[n];
        for (int i = 0; i < n; i += n2) {
            n2 = this.inputStream.read(this.palette, i, n - i);
            if (n2 >= 0) continue;
            throw new RuntimeException("incomplete palette");
        }
        this.properties.put("palette", this.palette);
    }

    private Image read1Bit(int n) throws IOException, BadElementException {
        byte[] arrby = new byte[(this.width + 7) / 8 * this.height];
        int n2 = 0;
        int n3 = (int)Math.ceil((double)this.width / 8.0);
        int n4 = n3 % 4;
        if (n4 != 0) {
            n2 = 4 - n4;
        }
        int n5 = (n3 + n2) * this.height;
        byte[] arrby2 = new byte[n5];
        for (int i = 0; i < n5; i += this.inputStream.read((byte[])arrby2, (int)i, (int)(n5 - i))) {
        }
        if (this.isBottomUp) {
            for (int j = 0; j < this.height; ++j) {
                System.arraycopy(arrby2, n5 - (j + 1) * (n3 + n2), arrby, j * n3, n3);
            }
        } else {
            for (int j = 0; j < this.height; ++j) {
                System.arraycopy(arrby2, j * (n3 + n2), arrby, j * n3, n3);
            }
        }
        return this.indexedModel(arrby, 1, n);
    }

    private Image read4Bit(int n) throws IOException, BadElementException {
        byte[] arrby = new byte[(this.width + 1) / 2 * this.height];
        int n2 = 0;
        int n3 = (int)Math.ceil((double)this.width / 2.0);
        int n4 = n3 % 4;
        if (n4 != 0) {
            n2 = 4 - n4;
        }
        int n5 = (n3 + n2) * this.height;
        byte[] arrby2 = new byte[n5];
        for (int i = 0; i < n5; i += this.inputStream.read((byte[])arrby2, (int)i, (int)(n5 - i))) {
        }
        if (this.isBottomUp) {
            for (int j = 0; j < this.height; ++j) {
                System.arraycopy(arrby2, n5 - (j + 1) * (n3 + n2), arrby, j * n3, n3);
            }
        } else {
            for (int j = 0; j < this.height; ++j) {
                System.arraycopy(arrby2, j * (n3 + n2), arrby, j * n3, n3);
            }
        }
        return this.indexedModel(arrby, 4, n);
    }

    private Image read8Bit(int n) throws IOException, BadElementException {
        byte[] arrby = new byte[this.width * this.height];
        int n2 = 0;
        int n3 = this.width * 8;
        if (n3 % 32 != 0) {
            n2 = (n3 / 32 + 1) * 32 - n3;
            n2 = (int)Math.ceil((double)n2 / 8.0);
        }
        int n4 = (this.width + n2) * this.height;
        byte[] arrby2 = new byte[n4];
        for (int i = 0; i < n4; i += this.inputStream.read((byte[])arrby2, (int)i, (int)(n4 - i))) {
        }
        if (this.isBottomUp) {
            for (int j = 0; j < this.height; ++j) {
                System.arraycopy(arrby2, n4 - (j + 1) * (this.width + n2), arrby, j * this.width, this.width);
            }
        } else {
            for (int j = 0; j < this.height; ++j) {
                System.arraycopy(arrby2, j * (this.width + n2), arrby, j * this.width, this.width);
            }
        }
        return this.indexedModel(arrby, 8, n);
    }

    private void read24Bit(byte[] arrby) {
        int n;
        int n2;
        int n3 = 0;
        int n4 = this.width * 24;
        if (n4 % 32 != 0) {
            n3 = (n4 / 32 + 1) * 32 - n4;
            n3 = (int)Math.ceil((double)n3 / 8.0);
        }
        int n5 = (this.width * 3 + 3) / 4 * 4 * this.height;
        byte[] arrby2 = new byte[n5];
        try {
            for (n = 0; n < n5 && (n2 = this.inputStream.read(arrby2, n, n5 - n)) >= 0; n += n2) {
            }
        }
        catch (IOException var6_7) {
            throw new ExceptionConverter(var6_7);
        }
        n = 0;
        if (this.isBottomUp) {
            int n6 = this.width * this.height * 3 - 1;
            n2 = - n3;
            for (int i = 0; i < this.height; ++i) {
                n = n6 - (i + 1) * this.width * 3 + 1;
                n2 += n3;
                for (int j = 0; j < this.width; ++j) {
                    arrby[n + 2] = arrby2[n2++];
                    arrby[n + 1] = arrby2[n2++];
                    arrby[n] = arrby2[n2++];
                    n += 3;
                }
            }
        } else {
            n2 = - n3;
            for (int i = 0; i < this.height; ++i) {
                n2 += n3;
                for (int j = 0; j < this.width; ++j) {
                    arrby[n + 2] = arrby2[n2++];
                    arrby[n + 1] = arrby2[n2++];
                    arrby[n] = arrby2[n2++];
                    n += 3;
                }
            }
        }
    }

    private int findMask(int n) {
        for (int i = 0; i < 32 && (n & 1) != 1; ++i) {
            n >>>= 1;
        }
        return n;
    }

    private int findShift(int n) {
        int n2;
        for (n2 = 0; n2 < 32 && (n & 1) != 1; ++n2) {
            n >>>= 1;
        }
        return n2;
    }

    private Image read1632Bit(boolean bl) throws IOException, BadElementException {
        int n;
        int n2 = this.findMask(this.redMask);
        int n3 = this.findShift(this.redMask);
        int n4 = n2 + 1;
        int n5 = this.findMask(this.greenMask);
        int n6 = this.findShift(this.greenMask);
        int n7 = n5 + 1;
        int n8 = this.findMask(this.blueMask);
        int n9 = this.findShift(this.blueMask);
        int n10 = n8 + 1;
        byte[] arrby = new byte[this.width * this.height * 3];
        int n11 = 0;
        if (!bl && (n = this.width * 16) % 32 != 0) {
            n11 = (n / 32 + 1) * 32 - n;
            n11 = (int)Math.ceil((double)n11 / 8.0);
        }
        if ((n = (int)this.imageSize) == 0) {
            n = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        int n12 = 0;
        if (this.isBottomUp) {
            for (int i = this.height - 1; i >= 0; --i) {
                int n13;
                n12 = this.width * 3 * i;
                for (n13 = 0; n13 < this.width; ++n13) {
                    int n14 = bl ? (int)this.readDWord(this.inputStream) : this.readWord(this.inputStream);
                    arrby[n12++] = (byte)((n14 >>> n3 & n2) * 256 / n4);
                    arrby[n12++] = (byte)((n14 >>> n6 & n5) * 256 / n7);
                    arrby[n12++] = (byte)((n14 >>> n9 & n8) * 256 / n10);
                }
                for (n13 = 0; n13 < n11; ++n13) {
                    this.inputStream.read();
                }
            }
        } else {
            for (int i = 0; i < this.height; ++i) {
                int n15;
                for (n15 = 0; n15 < this.width; ++n15) {
                    int n16 = bl ? (int)this.readDWord(this.inputStream) : this.readWord(this.inputStream);
                    arrby[n12++] = (byte)((n16 >>> n3 & n2) * 256 / n4);
                    arrby[n12++] = (byte)((n16 >>> n6 & n5) * 256 / n7);
                    arrby[n12++] = (byte)((n16 >>> n9 & n8) * 256 / n10);
                }
                for (n15 = 0; n15 < n11; ++n15) {
                    this.inputStream.read();
                }
            }
        }
        return new ImgRaw(this.width, this.height, 3, 8, arrby);
    }

    private Image readRLE8() throws IOException, BadElementException {
        int n = (int)this.imageSize;
        if (n == 0) {
            n = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        byte[] arrby = new byte[n];
        for (int i = 0; i < n; i += this.inputStream.read((byte[])arrby, (int)i, (int)(n - i))) {
        }
        byte[] arrby2 = this.decodeRLE(true, arrby);
        n = this.width * this.height;
        if (this.isBottomUp) {
            byte[] arrby3 = new byte[arrby2.length];
            int n2 = this.width;
            for (int j = 0; j < this.height; ++j) {
                System.arraycopy(arrby2, n - (j + 1) * n2, arrby3, j * n2, n2);
            }
            arrby2 = arrby3;
        }
        return this.indexedModel(arrby2, 8, 4);
    }

    private Image readRLE4() throws IOException, BadElementException {
        int n;
        int n2;
        int n3;
        int n4 = (int)this.imageSize;
        if (n4 == 0) {
            n4 = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        byte[] arrby = new byte[n4];
        for (int i = 0; i < n4; i += this.inputStream.read((byte[])arrby, (int)i, (int)(n4 - i))) {
        }
        byte[] arrby2 = this.decodeRLE(false, arrby);
        if (this.isBottomUp) {
            byte[] arrby3 = arrby2;
            arrby2 = new byte[this.width * this.height];
            int n5 = 0;
            for (n2 = this.height - 1; n2 >= 0; --n2) {
                n = n2 * this.width;
                n3 = n5 + this.width;
                while (n5 != n3) {
                    arrby2[n5++] = arrby3[n++];
                }
            }
        }
        int n6 = (this.width + 1) / 2;
        byte[] arrby4 = new byte[n6 * this.height];
        n = 0;
        n3 = 0;
        for (n2 = 0; n2 < this.height; ++n2) {
            for (int j = 0; j < this.width; ++j) {
                if ((j & 1) == 0) {
                    arrby4[n3 + j / 2] = (byte)(arrby2[n++] << 4);
                    continue;
                }
                byte[] arrby5 = arrby4;
                int n7 = n3 + j / 2;
                arrby5[n7] = (byte)(arrby5[n7] | (byte)(arrby2[n++] & 15));
            }
            n3 += n6;
        }
        return this.indexedModel(arrby4, 4, 4);
    }

    private byte[] decodeRLE(boolean bl, byte[] arrby) {
        byte[] arrby2;
        arrby2 = new byte[this.width * this.height];
        try {
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            int n4 = 0;
            block6 : while (n4 < this.height && n < arrby.length) {
                int n5;
                int n6;
                int n7;
                if ((n7 = arrby[n++] & 255) != 0) {
                    n6 = arrby[n++] & 255;
                    if (bl) {
                        for (n5 = n7; n5 != 0; --n5) {
                            arrby2[n3++] = (byte)n6;
                        }
                    } else {
                        for (n5 = 0; n5 < n7; ++n5) {
                            arrby2[n3++] = (byte)((n5 & 1) == 1 ? n6 & 15 : n6 >>> 4 & 15);
                        }
                    }
                    n2 += n7;
                    continue;
                }
                if ((n7 = arrby[n++] & 255) == 1) break;
                switch (n7) {
                    case 0: {
                        n2 = 0;
                        n3 = ++n4 * this.width;
                        continue block6;
                    }
                    case 2: {
                        n3 = (n4 += arrby[n++] & 255) * this.width + (n2 += arrby[n++] & 255);
                        continue block6;
                    }
                }
                if (bl) {
                    for (n6 = n7; n6 != 0; --n6) {
                        arrby2[n3++] = (byte)(arrby[n++] & 255);
                    }
                } else {
                    n6 = 0;
                    for (n5 = 0; n5 < n7; ++n5) {
                        if ((n5 & 1) == 0) {
                            n6 = arrby[n++] & 255;
                        }
                        arrby2[n3++] = (byte)((n5 & 1) == 1 ? n6 & 15 : n6 >>> 4 & 15);
                    }
                }
                n2 += n7;
                if (bl) {
                    if ((n7 & 1) != 1) continue;
                    ++n;
                    continue;
                }
                if ((n7 & 3) != 1 && (n7 & 3) != 2) continue;
                ++n;
            }
        }
        catch (RuntimeException var4_5) {
            // empty catch block
        }
        return arrby2;
    }

    private int readUnsignedByte(InputStream inputStream) throws IOException {
        return inputStream.read() & 255;
    }

    private int readUnsignedShort(InputStream inputStream) throws IOException {
        int n = this.readUnsignedByte(inputStream);
        int n2 = this.readUnsignedByte(inputStream);
        return (n2 << 8 | n) & 65535;
    }

    private int readShort(InputStream inputStream) throws IOException {
        int n = this.readUnsignedByte(inputStream);
        int n2 = this.readUnsignedByte(inputStream);
        return n2 << 8 | n;
    }

    private int readWord(InputStream inputStream) throws IOException {
        return this.readUnsignedShort(inputStream);
    }

    private long readUnsignedInt(InputStream inputStream) throws IOException {
        int n = this.readUnsignedByte(inputStream);
        int n2 = this.readUnsignedByte(inputStream);
        int n3 = this.readUnsignedByte(inputStream);
        int n4 = this.readUnsignedByte(inputStream);
        long l = n4 << 24 | n3 << 16 | n2 << 8 | n;
        return l & -1;
    }

    private int readInt(InputStream inputStream) throws IOException {
        int n = this.readUnsignedByte(inputStream);
        int n2 = this.readUnsignedByte(inputStream);
        int n3 = this.readUnsignedByte(inputStream);
        int n4 = this.readUnsignedByte(inputStream);
        return n4 << 24 | n3 << 16 | n2 << 8 | n;
    }

    private long readDWord(InputStream inputStream) throws IOException {
        return this.readUnsignedInt(inputStream);
    }

    private int readLong(InputStream inputStream) throws IOException {
        return this.readInt(inputStream);
    }
}

