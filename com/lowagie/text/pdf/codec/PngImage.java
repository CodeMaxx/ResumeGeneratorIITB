/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgRaw;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class PngImage {
    public static final int[] PNGID = new int[]{137, 80, 78, 71, 13, 10, 26, 10};
    public static final String IHDR = "IHDR";
    public static final String PLTE = "PLTE";
    public static final String IDAT = "IDAT";
    public static final String IEND = "IEND";
    public static final String tRNS = "tRNS";
    public static final String pHYs = "pHYs";
    public static final String gAMA = "gAMA";
    public static final String cHRM = "cHRM";
    public static final String sRGB = "sRGB";
    public static final String iCCP = "iCCP";
    private static final int TRANSFERSIZE = 4096;
    private static final int PNG_FILTER_NONE = 0;
    private static final int PNG_FILTER_SUB = 1;
    private static final int PNG_FILTER_UP = 2;
    private static final int PNG_FILTER_AVERAGE = 3;
    private static final int PNG_FILTER_PAETH = 4;
    private static final PdfName[] intents = new PdfName[]{PdfName.PERCEPTUAL, PdfName.RELATIVECALORIMETRIC, PdfName.SATURATION, PdfName.ABSOLUTECALORIMETRIC};
    InputStream is;
    DataInputStream dataStream;
    int width;
    int height;
    int bitDepth;
    int colorType;
    int compressionMethod;
    int filterMethod;
    int interlaceMethod;
    PdfDictionary additional = new PdfDictionary();
    byte[] image;
    byte[] smask;
    byte[] trans;
    NewByteArrayOutputStream idat = new NewByteArrayOutputStream();
    int dpiX;
    int dpiY;
    float XYRatio;
    boolean genBWMask;
    boolean palShades;
    int transRedGray = -1;
    int transGreen = -1;
    int transBlue = -1;
    int inputBands;
    int bytesPerPixel;
    byte[] colorTable;
    float gamma = 1.0f;
    boolean hasCHRM = false;
    float xW;
    float yW;
    float xR;
    float yR;
    float xG;
    float yG;
    float xB;
    float yB;
    PdfName intent;
    ICC_Profile icc_profile;

    PngImage(InputStream inputStream) {
        this.is = inputStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Image getImage(URL uRL) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = uRL.openStream();
            Image image = PngImage.getImage(inputStream);
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
        PngImage pngImage = new PngImage(inputStream);
        return pngImage.getImage();
    }

    public static Image getImage(String string) throws IOException {
        return PngImage.getImage(Utilities.toURL(string));
    }

    public static Image getImage(byte[] arrby) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Image image = PngImage.getImage(byteArrayInputStream);
        image.setOriginalData(arrby);
        return image;
    }

    boolean checkMarker(String string) {
        if (string.length() != 4) {
            return false;
        }
        for (int i = 0; i < 4; ++i) {
            char c = string.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') continue;
            return false;
        }
        return true;
    }

    void readPng() throws IOException {
        for (int i = 0; i < PNGID.length; ++i) {
            if (PNGID[i] == this.is.read()) continue;
            throw new IOException("File is not a valid PNG.");
        }
        byte[] arrby = new byte[4096];
        do {
            int n;
            int n2;
            int n3;
            int n4;
            String string = PngImage.getString(this.is);
            if (n4 < 0 || !this.checkMarker(string)) {
                throw new IOException("Corrupted PNG file.");
            }
            if ("IDAT".equals(string)) {
                for (n4 = PngImage.getInt((InputStream)this.is); n4 != 0; n4 -= n3) {
                    n3 = this.is.read(arrby, 0, Math.min(n4, 4096));
                    if (n3 < 0) {
                        return;
                    }
                    this.idat.write(arrby, 0, n3);
                }
            } else if ("tRNS".equals(string)) {
                switch (this.colorType) {
                    case 0: {
                        if (n4 < 2) break;
                        n4 -= 2;
                        n3 = PngImage.getWord(this.is);
                        if (this.bitDepth == 16) {
                            this.transRedGray = n3;
                            break;
                        }
                        this.additional.put(PdfName.MASK, new PdfLiteral("[" + n3 + " " + n3 + "]"));
                        break;
                    }
                    case 2: {
                        if (n4 < 6) break;
                        n4 -= 6;
                        n3 = PngImage.getWord(this.is);
                        n = PngImage.getWord(this.is);
                        n2 = PngImage.getWord(this.is);
                        if (this.bitDepth == 16) {
                            this.transRedGray = n3;
                            this.transGreen = n;
                            this.transBlue = n2;
                            break;
                        }
                        this.additional.put(PdfName.MASK, new PdfLiteral("[" + n3 + " " + n3 + " " + n + " " + n + " " + n2 + " " + n2 + "]"));
                        break;
                    }
                    case 3: {
                        if (n4 <= 0) break;
                        this.trans = new byte[n4];
                        for (n3 = 0; n3 < n4; ++n3) {
                            this.trans[n3] = (byte)this.is.read();
                        }
                        n4 = 0;
                    }
                }
                Utilities.skip(this.is, n4);
            } else if ("IHDR".equals(string)) {
                this.width = PngImage.getInt(this.is);
                this.height = PngImage.getInt(this.is);
                this.bitDepth = this.is.read();
                this.colorType = this.is.read();
                this.compressionMethod = this.is.read();
                this.filterMethod = this.is.read();
                this.interlaceMethod = this.is.read();
            } else if ("PLTE".equals(string)) {
                if (this.colorType == 3) {
                    PdfArray pdfArray = new PdfArray();
                    pdfArray.add(PdfName.INDEXED);
                    pdfArray.add(this.getColorspace());
                    pdfArray.add(new PdfNumber(n4 / 3 - 1));
                    ByteBuffer byteBuffer = new ByteBuffer();
                    while (n4-- > 0) {
                        byteBuffer.append_i(this.is.read());
                    }
                    this.colorTable = byteBuffer.toByteArray();
                    pdfArray.add(new PdfString(this.colorTable));
                    this.additional.put(PdfName.COLORSPACE, pdfArray);
                } else {
                    Utilities.skip(this.is, n4);
                }
            } else if ("pHYs".equals(string)) {
                n3 = PngImage.getInt(this.is);
                n = PngImage.getInt(this.is);
                n2 = this.is.read();
                if (n2 == 1) {
                    this.dpiX = (int)((float)n3 * 0.0254f + 0.5f);
                    this.dpiY = (int)((float)n * 0.0254f + 0.5f);
                } else if (n != 0) {
                    this.XYRatio = (float)n3 / (float)n;
                }
            } else if ("cHRM".equals(string)) {
                this.xW = (float)PngImage.getInt(this.is) / 100000.0f;
                this.yW = (float)PngImage.getInt(this.is) / 100000.0f;
                this.xR = (float)PngImage.getInt(this.is) / 100000.0f;
                this.yR = (float)PngImage.getInt(this.is) / 100000.0f;
                this.xG = (float)PngImage.getInt(this.is) / 100000.0f;
                this.yG = (float)PngImage.getInt(this.is) / 100000.0f;
                this.xB = (float)PngImage.getInt(this.is) / 100000.0f;
                this.yB = (float)PngImage.getInt(this.is) / 100000.0f;
                this.hasCHRM = Math.abs(this.xW) >= 1.0E-4f && Math.abs(this.yW) >= 1.0E-4f && Math.abs(this.xR) >= 1.0E-4f && Math.abs(this.yR) >= 1.0E-4f && Math.abs(this.xG) >= 1.0E-4f && Math.abs(this.yG) >= 1.0E-4f && Math.abs(this.xB) >= 1.0E-4f && Math.abs(this.yB) >= 1.0E-4f;
            } else if ("sRGB".equals(string)) {
                n3 = this.is.read();
                this.intent = intents[n3];
                this.gamma = 2.2f;
                this.xW = 0.3127f;
                this.yW = 0.329f;
                this.xR = 0.64f;
                this.yR = 0.33f;
                this.xG = 0.3f;
                this.yG = 0.6f;
                this.xB = 0.15f;
                this.yB = 0.06f;
                this.hasCHRM = true;
            } else if ("gAMA".equals(string)) {
                n3 = PngImage.getInt(this.is);
                if (n3 != 0) {
                    this.gamma = 100000.0f / (float)n3;
                    if (!this.hasCHRM) {
                        this.xW = 0.3127f;
                        this.yW = 0.329f;
                        this.xR = 0.64f;
                        this.yR = 0.33f;
                        this.xG = 0.3f;
                        this.yG = 0.6f;
                        this.xB = 0.15f;
                        this.yB = 0.06f;
                        this.hasCHRM = true;
                    }
                }
            } else if ("iCCP".equals(string)) {
                do {
                    --n4;
                } while (this.is.read() != 0);
                this.is.read();
                byte[] arrby2 = new byte[--n4];
                n = 0;
                while (n4 > 0) {
                    n2 = this.is.read(arrby2, n, n4);
                    if (n2 < 0) {
                        throw new IOException("Premature end of file.");
                    }
                    n += n2;
                    n4 -= n2;
                }
                byte[] arrby3 = PdfReader.FlateDecode(arrby2, true);
                arrby2 = null;
                try {
                    this.icc_profile = ICC_Profile.getInstance(arrby3);
                }
                catch (RuntimeException var7_12) {
                    this.icc_profile = null;
                }
            } else {
                if ("IEND".equals(string)) break;
                Utilities.skip(this.is, n4);
            }
            Utilities.skip(this.is, 4);
        } while (true);
    }

    PdfObject getColorspace() {
        if (this.icc_profile != null) {
            if ((this.colorType & 2) == 0) {
                return PdfName.DEVICEGRAY;
            }
            return PdfName.DEVICERGB;
        }
        if (this.gamma == 1.0f && !this.hasCHRM) {
            if ((this.colorType & 2) == 0) {
                return PdfName.DEVICEGRAY;
            }
            return PdfName.DEVICERGB;
        }
        PdfArray pdfArray = new PdfArray();
        PdfDictionary pdfDictionary = new PdfDictionary();
        if ((this.colorType & 2) == 0) {
            if (this.gamma == 1.0f) {
                return PdfName.DEVICEGRAY;
            }
            pdfArray.add(PdfName.CALGRAY);
            pdfDictionary.put(PdfName.GAMMA, new PdfNumber(this.gamma));
            pdfDictionary.put(PdfName.WHITEPOINT, new PdfLiteral("[1 1 1]"));
            pdfArray.add(pdfDictionary);
        } else {
            PdfObject pdfObject = new PdfLiteral("[1 1 1]");
            pdfArray.add(PdfName.CALRGB);
            if (this.gamma != 1.0f) {
                PdfArray pdfArray2 = new PdfArray();
                PdfNumber pdfNumber = new PdfNumber(this.gamma);
                pdfArray2.add(pdfNumber);
                pdfArray2.add(pdfNumber);
                pdfArray2.add(pdfNumber);
                pdfDictionary.put(PdfName.GAMMA, pdfArray2);
            }
            if (this.hasCHRM) {
                float f = this.yW * ((this.xG - this.xB) * this.yR - (this.xR - this.xB) * this.yG + (this.xR - this.xG) * this.yB);
                float f2 = this.yR * ((this.xG - this.xB) * this.yW - (this.xW - this.xB) * this.yG + (this.xW - this.xG) * this.yB) / f;
                float f3 = f2 * this.xR / this.yR;
                float f4 = f2 * ((1.0f - this.xR) / this.yR - 1.0f);
                float f5 = (- this.yG) * ((this.xR - this.xB) * this.yW - (this.xW - this.xB) * this.yR + (this.xW - this.xR) * this.yB) / f;
                float f6 = f5 * this.xG / this.yG;
                float f7 = f5 * ((1.0f - this.xG) / this.yG - 1.0f);
                float f8 = this.yB * ((this.xR - this.xG) * this.yW - (this.xW - this.xG) * this.yW + (this.xW - this.xR) * this.yG) / f;
                float f9 = f8 * this.xB / this.yB;
                float f10 = f8 * ((1.0f - this.xB) / this.yB - 1.0f);
                float f11 = f3 + f6 + f9;
                float f12 = 1.0f;
                float f13 = f4 + f7 + f10;
                PdfArray pdfArray3 = new PdfArray();
                pdfArray3.add(new PdfNumber(f11));
                pdfArray3.add(new PdfNumber(f12));
                pdfArray3.add(new PdfNumber(f13));
                pdfObject = pdfArray3;
                PdfArray pdfArray4 = new PdfArray();
                pdfArray4.add(new PdfNumber(f3));
                pdfArray4.add(new PdfNumber(f2));
                pdfArray4.add(new PdfNumber(f4));
                pdfArray4.add(new PdfNumber(f6));
                pdfArray4.add(new PdfNumber(f5));
                pdfArray4.add(new PdfNumber(f7));
                pdfArray4.add(new PdfNumber(f9));
                pdfArray4.add(new PdfNumber(f8));
                pdfArray4.add(new PdfNumber(f10));
                pdfDictionary.put(PdfName.MATRIX, pdfArray4);
            }
            pdfDictionary.put(PdfName.WHITEPOINT, pdfObject);
            pdfArray.add(pdfDictionary);
        }
        return pdfArray;
    }

    Image getImage() throws IOException {
        this.readPng();
        try {
            int n;
            Image image;
            int n2;
            Object object;
            int n3;
            int n4 = 0;
            int n5 = 0;
            this.palShades = false;
            if (this.trans != null) {
                for (n = 0; n < this.trans.length; ++n) {
                    n3 = this.trans[n] & 255;
                    if (n3 == 0) {
                        ++n4;
                        n5 = n;
                    }
                    if (n3 == 0 || n3 == 255) continue;
                    this.palShades = true;
                    break;
                }
            }
            if ((this.colorType & 4) != 0) {
                this.palShades = true;
            }
            boolean bl = this.genBWMask = !this.palShades && (n4 > 1 || this.transRedGray >= 0);
            if (!this.palShades && !this.genBWMask && n4 == 1) {
                this.additional.put(PdfName.MASK, new PdfLiteral("[" + n5 + " " + n5 + "]"));
            }
            n = this.interlaceMethod == 1 || this.bitDepth == 16 || (this.colorType & 4) != 0 || this.palShades || this.genBWMask ? 1 : 0;
            switch (this.colorType) {
                case 0: {
                    this.inputBands = 1;
                    break;
                }
                case 2: {
                    this.inputBands = 3;
                    break;
                }
                case 3: {
                    this.inputBands = 1;
                    break;
                }
                case 4: {
                    this.inputBands = 2;
                    break;
                }
                case 6: {
                    this.inputBands = 4;
                }
            }
            if (n != 0) {
                this.decodeIdat();
            }
            n3 = this.inputBands;
            if ((this.colorType & 4) != 0) {
                --n3;
            }
            if ((n2 = this.bitDepth) == 16) {
                n2 = 8;
            }
            if (this.image != null) {
                image = this.colorType == 3 ? new ImgRaw(this.width, this.height, n3, n2, this.image) : Image.getInstance(this.width, this.height, n3, n2, this.image);
            } else {
                image = new ImgRaw(this.width, this.height, n3, n2, this.idat.toByteArray());
                image.setDeflated(true);
                object = new PdfDictionary();
                object.put(PdfName.BITSPERCOMPONENT, new PdfNumber(this.bitDepth));
                object.put(PdfName.PREDICTOR, new PdfNumber(15));
                object.put(PdfName.COLUMNS, new PdfNumber(this.width));
                object.put(PdfName.COLORS, new PdfNumber(this.colorType == 3 || (this.colorType & 2) == 0 ? 1 : 3));
                this.additional.put(PdfName.DECODEPARMS, (PdfObject)object);
            }
            if (this.additional.get(PdfName.COLORSPACE) == null) {
                this.additional.put(PdfName.COLORSPACE, this.getColorspace());
            }
            if (this.intent != null) {
                this.additional.put(PdfName.INTENT, this.intent);
            }
            if (this.additional.size() > 0) {
                image.setAdditional(this.additional);
            }
            if (this.icc_profile != null) {
                image.tagICC(this.icc_profile);
            }
            if (this.palShades) {
                object = Image.getInstance(this.width, this.height, 1, 8, this.smask);
                object.makeMask();
                image.setImageMask((Image)object);
            }
            if (this.genBWMask) {
                object = Image.getInstance(this.width, this.height, 1, 1, this.smask);
                object.makeMask();
                image.setImageMask((Image)object);
            }
            image.setDpi(this.dpiX, this.dpiY);
            image.setXYRatio(this.XYRatio);
            image.setOriginalType(2);
            return image;
        }
        catch (Exception var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    void decodeIdat() {
        int n = this.bitDepth;
        if (n == 16) {
            n = 8;
        }
        int n2 = -1;
        this.bytesPerPixel = this.bitDepth == 16 ? 2 : 1;
        switch (this.colorType) {
            case 0: {
                n2 = (n * this.width + 7) / 8 * this.height;
                break;
            }
            case 2: {
                n2 = this.width * 3 * this.height;
                this.bytesPerPixel *= 3;
                break;
            }
            case 3: {
                if (this.interlaceMethod == 1) {
                    n2 = (n * this.width + 7) / 8 * this.height;
                }
                this.bytesPerPixel = 1;
                break;
            }
            case 4: {
                n2 = this.width * this.height;
                this.bytesPerPixel *= 2;
                break;
            }
            case 6: {
                n2 = this.width * 3 * this.height;
                this.bytesPerPixel *= 4;
            }
        }
        if (n2 >= 0) {
            this.image = new byte[n2];
        }
        if (this.palShades) {
            this.smask = new byte[this.width * this.height];
        } else if (this.genBWMask) {
            this.smask = new byte[(this.width + 7) / 8 * this.height];
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.idat.getBuf(), 0, this.idat.size());
        InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream, new Inflater());
        this.dataStream = new DataInputStream(inflaterInputStream);
        if (this.interlaceMethod != 1) {
            this.decodePass(0, 0, 1, 1, this.width, this.height);
        } else {
            this.decodePass(0, 0, 8, 8, (this.width + 7) / 8, (this.height + 7) / 8);
            this.decodePass(4, 0, 8, 8, (this.width + 3) / 8, (this.height + 7) / 8);
            this.decodePass(0, 4, 4, 8, (this.width + 3) / 4, (this.height + 3) / 8);
            this.decodePass(2, 0, 4, 4, (this.width + 1) / 4, (this.height + 3) / 4);
            this.decodePass(0, 2, 2, 4, (this.width + 1) / 2, (this.height + 1) / 4);
            this.decodePass(1, 0, 2, 2, this.width / 2, (this.height + 1) / 2);
            this.decodePass(0, 1, 1, 2, this.width, this.height / 2);
        }
    }

    void decodePass(int n, int n2, int n3, int n4, int n5, int n6) {
        if (n5 == 0 || n6 == 0) {
            return;
        }
        int n7 = (this.inputBands * n5 * this.bitDepth + 7) / 8;
        byte[] arrby = new byte[n7];
        byte[] arrby2 = new byte[n7];
        int n8 = 0;
        int n9 = n2;
        while (n8 < n6) {
            int n10 = 0;
            try {
                n10 = this.dataStream.read();
                this.dataStream.readFully(arrby, 0, n7);
            }
            catch (Exception var13_14) {
                // empty catch block
            }
            switch (n10) {
                case 0: {
                    break;
                }
                case 1: {
                    PngImage.decodeSubFilter(arrby, n7, this.bytesPerPixel);
                    break;
                }
                case 2: {
                    PngImage.decodeUpFilter(arrby, arrby2, n7);
                    break;
                }
                case 3: {
                    PngImage.decodeAverageFilter(arrby, arrby2, n7, this.bytesPerPixel);
                    break;
                }
                case 4: {
                    PngImage.decodePaethFilter(arrby, arrby2, n7, this.bytesPerPixel);
                    break;
                }
                default: {
                    throw new RuntimeException("PNG filter unknown.");
                }
            }
            this.processPixels(arrby, n, n3, n9, n5);
            byte[] arrby3 = arrby2;
            arrby2 = arrby;
            arrby = arrby3;
            ++n8;
            n9 += n4;
        }
    }

    void processPixels(byte[] arrby, int n, int n2, int n3, int n4) {
        int n5;
        int n6;
        int n7;
        int[] arrn = this.getPixel(arrby);
        int n8 = 0;
        switch (this.colorType) {
            case 0: 
            case 3: 
            case 4: {
                n8 = 1;
                break;
            }
            case 2: 
            case 6: {
                n8 = 3;
            }
        }
        if (this.image != null) {
            n7 = n;
            n6 = (n8 * this.width * (this.bitDepth == 16 ? 8 : this.bitDepth) + 7) / 8;
            for (n5 = 0; n5 < n4; ++n5) {
                PngImage.setPixel(this.image, arrn, this.inputBands * n5, n8, n7, n3, this.bitDepth, n6);
                n7 += n2;
            }
        }
        if (this.palShades) {
            if ((this.colorType & 4) != 0) {
                if (this.bitDepth == 16) {
                    for (n6 = 0; n6 < n4; ++n6) {
                        int[] arrn2 = arrn;
                        int n9 = n6 * this.inputBands + n8;
                        arrn2[n9] = arrn2[n9] >>> 8;
                    }
                }
                n6 = this.width;
                n7 = n;
                for (n5 = 0; n5 < n4; ++n5) {
                    PngImage.setPixel(this.smask, arrn, this.inputBands * n5 + n8, 1, n7, n3, 8, n6);
                    n7 += n2;
                }
            } else {
                n6 = this.width;
                int[] arrn3 = new int[1];
                n7 = n;
                for (n5 = 0; n5 < n4; ++n5) {
                    int n10 = arrn[n5];
                    if (n10 < this.trans.length) {
                        arrn3[0] = this.trans[n10];
                    }
                    PngImage.setPixel(this.smask, arrn3, 0, 1, n7, n3, 8, n6);
                    n7 += n2;
                }
            }
        } else if (this.genBWMask) {
            switch (this.colorType) {
                case 3: {
                    n6 = (this.width + 7) / 8;
                    int[] arrn4 = new int[1];
                    n7 = n;
                    for (n5 = 0; n5 < n4; ++n5) {
                        int n11 = arrn[n5];
                        if (n11 < this.trans.length) {
                            arrn4[0] = this.trans[n11] == 0 ? 1 : 0;
                        }
                        PngImage.setPixel(this.smask, arrn4, 0, 1, n7, n3, 1, n6);
                        n7 += n2;
                    }
                    break;
                }
                case 0: {
                    n6 = (this.width + 7) / 8;
                    int[] arrn5 = new int[1];
                    n7 = n;
                    for (n5 = 0; n5 < n4; ++n5) {
                        int n12 = arrn[n5];
                        arrn5[0] = n12 == this.transRedGray ? 1 : 0;
                        PngImage.setPixel(this.smask, arrn5, 0, 1, n7, n3, 1, n6);
                        n7 += n2;
                    }
                    break;
                }
                case 2: {
                    n6 = (this.width + 7) / 8;
                    int[] arrn6 = new int[1];
                    n7 = n;
                    for (n5 = 0; n5 < n4; ++n5) {
                        int n13 = this.inputBands * n5;
                        arrn6[0] = arrn[n13] == this.transRedGray && arrn[n13 + 1] == this.transGreen && arrn[n13 + 2] == this.transBlue ? 1 : 0;
                        PngImage.setPixel(this.smask, arrn6, 0, 1, n7, n3, 1, n6);
                        n7 += n2;
                    }
                    break;
                }
            }
        }
    }

    static int getPixel(byte[] arrby, int n, int n2, int n3, int n4) {
        if (n3 == 8) {
            int n5 = n4 * n2 + n;
            return arrby[n5] & 255;
        }
        int n6 = n4 * n2 + n / (8 / n3);
        int n7 = arrby[n6] >> 8 - n3 * (n % (8 / n3)) - n3;
        return n7 & (1 << n3) - 1;
    }

    static void setPixel(byte[] arrby, int[] arrn, int n, int n2, int n3, int n4, int n5, int n6) {
        if (n5 == 8) {
            int n7 = n6 * n4 + n2 * n3;
            for (int i = 0; i < n2; ++i) {
                arrby[n7 + i] = (byte)arrn[i + n];
            }
        } else if (n5 == 16) {
            int n8 = n6 * n4 + n2 * n3;
            for (int i = 0; i < n2; ++i) {
                arrby[n8 + i] = (byte)(arrn[i + n] >>> 8);
            }
        } else {
            int n9 = n6 * n4 + n3 / (8 / n5);
            int n10 = arrn[n] << 8 - n5 * (n3 % (8 / n5)) - n5;
            byte[] arrby2 = arrby;
            int n11 = n9;
            arrby2[n11] = (byte)(arrby2[n11] | n10);
        }
    }

    int[] getPixel(byte[] arrby) {
        switch (this.bitDepth) {
            case 8: {
                int[] arrn = new int[arrby.length];
                for (int i = 0; i < arrn.length; ++i) {
                    arrn[i] = arrby[i] & 255;
                }
                return arrn;
            }
            case 16: {
                int[] arrn = new int[arrby.length / 2];
                for (int i = 0; i < arrn.length; ++i) {
                    arrn[i] = ((arrby[i * 2] & 255) << 8) + (arrby[i * 2 + 1] & 255);
                }
                return arrn;
            }
        }
        int[] arrn = new int[arrby.length * 8 / this.bitDepth];
        int n = 0;
        int n2 = 8 / this.bitDepth;
        int n3 = (1 << this.bitDepth) - 1;
        for (int i = 0; i < arrby.length; ++i) {
            for (int j = n2 - 1; j >= 0; --j) {
                arrn[n++] = arrby[i] >>> this.bitDepth * j & n3;
            }
        }
        return arrn;
    }

    private static void decodeSubFilter(byte[] arrby, int n, int n2) {
        for (int i = n2; i < n; ++i) {
            int n3 = arrby[i] & 255;
            arrby[i] = (byte)(n3 += arrby[i - n2] & 255);
        }
    }

    private static void decodeUpFilter(byte[] arrby, byte[] arrby2, int n) {
        for (int i = 0; i < n; ++i) {
            int n2 = arrby[i] & 255;
            int n3 = arrby2[i] & 255;
            arrby[i] = (byte)(n2 + n3);
        }
    }

    private static void decodeAverageFilter(byte[] arrby, byte[] arrby2, int n, int n2) {
        int n3;
        int n4;
        int n5;
        for (n4 = 0; n4 < n2; ++n4) {
            n5 = arrby[n4] & 255;
            n3 = arrby2[n4] & 255;
            arrby[n4] = (byte)(n5 + n3 / 2);
        }
        for (n4 = n2; n4 < n; ++n4) {
            n5 = arrby[n4] & 255;
            int n6 = arrby[n4 - n2] & 255;
            n3 = arrby2[n4] & 255;
            arrby[n4] = (byte)(n5 + (n6 + n3) / 2);
        }
    }

    private static int paethPredictor(int n, int n2, int n3) {
        int n4 = n + n2 - n3;
        int n5 = Math.abs(n4 - n);
        int n6 = Math.abs(n4 - n2);
        int n7 = Math.abs(n4 - n3);
        if (n5 <= n6 && n5 <= n7) {
            return n;
        }
        if (n6 <= n7) {
            return n2;
        }
        return n3;
    }

    private static void decodePaethFilter(byte[] arrby, byte[] arrby2, int n, int n2) {
        int n3;
        int n4;
        int n5;
        for (n3 = 0; n3 < n2; ++n3) {
            n5 = arrby[n3] & 255;
            n4 = arrby2[n3] & 255;
            arrby[n3] = (byte)(n5 + n4);
        }
        for (n3 = n2; n3 < n; ++n3) {
            n5 = arrby[n3] & 255;
            int n6 = arrby[n3 - n2] & 255;
            n4 = arrby2[n3] & 255;
            int n7 = arrby2[n3 - n2] & 255;
            arrby[n3] = (byte)(n5 + PngImage.paethPredictor(n6, n4, n7));
        }
    }

    public static final int getInt(InputStream inputStream) throws IOException {
        return (inputStream.read() << 24) + (inputStream.read() << 16) + (inputStream.read() << 8) + inputStream.read();
    }

    public static final int getWord(InputStream inputStream) throws IOException {
        return (inputStream.read() << 8) + inputStream.read();
    }

    public static final String getString(InputStream inputStream) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 4; ++i) {
            stringBuffer.append((char)inputStream.read());
        }
        return stringBuffer.toString();
    }

    static class NewByteArrayOutputStream
    extends ByteArrayOutputStream {
        NewByteArrayOutputStream() {
        }

        public byte[] getBuf() {
            return this.buf;
        }
    }

}

