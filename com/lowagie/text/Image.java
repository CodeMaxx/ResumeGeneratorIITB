/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.ImgCCITT;
import com.lowagie.text.ImgRaw;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.ImgWMF;
import com.lowagie.text.Jpeg;
import com.lowagie.text.Jpeg2000;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.BmpImage;
import com.lowagie.text.pdf.codec.CCITTG4Encoder;
import com.lowagie.text.pdf.codec.GifImage;
import com.lowagie.text.pdf.codec.PngImage;
import com.lowagie.text.pdf.codec.TiffImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public abstract class Image
extends Rectangle {
    public static final int DEFAULT = 0;
    public static final int RIGHT = 2;
    public static final int LEFT = 0;
    public static final int MIDDLE = 1;
    public static final int TEXTWRAP = 4;
    public static final int UNDERLYING = 8;
    public static final int AX = 0;
    public static final int AY = 1;
    public static final int BX = 2;
    public static final int BY = 3;
    public static final int CX = 4;
    public static final int CY = 5;
    public static final int DX = 6;
    public static final int DY = 7;
    public static final int ORIGINAL_NONE = 0;
    public static final int ORIGINAL_JPEG = 1;
    public static final int ORIGINAL_PNG = 2;
    public static final int ORIGINAL_GIF = 3;
    public static final int ORIGINAL_BMP = 4;
    public static final int ORIGINAL_TIFF = 5;
    public static final int ORIGINAL_WMF = 6;
    public static final int ORIGINAL_PS = 7;
    public static final int ORIGINAL_JPEG2000 = 8;
    protected int type;
    protected URL url;
    protected byte[] rawData;
    protected int bpc = 1;
    protected PdfTemplate[] template = new PdfTemplate[1];
    protected int alignment;
    protected String alt;
    protected float absoluteX = Float.NaN;
    protected float absoluteY = Float.NaN;
    protected float plainWidth;
    protected float plainHeight;
    protected float scaledWidth;
    protected float scaledHeight;
    protected int compressionLevel = -1;
    protected Long mySerialId = Image.getSerialId();
    private PdfIndirectReference directReference;
    static long serialId = 0;
    protected float rotationRadians;
    private float initialRotation;
    protected float indentationLeft = 0.0f;
    protected float indentationRight = 0.0f;
    protected float spacingBefore;
    protected float spacingAfter;
    private float widthPercentage = 100.0f;
    protected Annotation annotation = null;
    protected PdfOCG layer;
    protected boolean interpolation;
    protected int originalType = 0;
    protected byte[] originalData;
    protected boolean deflated = false;
    protected int dpiX = 0;
    protected int dpiY = 0;
    private float XYRatio = 0.0f;
    protected int colorspace = -1;
    protected boolean invert = false;
    protected ICC_Profile profile = null;
    private PdfDictionary additional = null;
    protected boolean mask = false;
    protected Image imageMask;
    private boolean smask;
    protected int[] transparency;

    public Image(URL uRL) {
        super(0.0f, 0.0f);
        this.url = uRL;
        this.alignment = 0;
        this.rotationRadians = 0.0f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Image getInstance(URL uRL) throws BadElementException, MalformedURLException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = uRL.openStream();
            int n = inputStream.read();
            int n2 = inputStream.read();
            int n3 = inputStream.read();
            int n4 = inputStream.read();
            inputStream.close();
            inputStream = null;
            if (n == 71 && n2 == 73 && n3 == 70) {
                Image image;
                GifImage gifImage = new GifImage(uRL);
                Image image2 = image = gifImage.getImage(1);
                return image2;
            }
            if (n == 255 && n2 == 216) {
                Jpeg jpeg = new Jpeg(uRL);
                return jpeg;
            }
            if (n == 0 && n2 == 0 && n3 == 0 && n4 == 12) {
                Jpeg2000 jpeg2000 = new Jpeg2000(uRL);
                return jpeg2000;
            }
            if (n == 255 && n2 == 79 && n3 == 255 && n4 == 81) {
                Jpeg2000 jpeg2000 = new Jpeg2000(uRL);
                return jpeg2000;
            }
            if (n == PngImage.PNGID[0] && n2 == PngImage.PNGID[1] && n3 == PngImage.PNGID[2] && n4 == PngImage.PNGID[3]) {
                Image image = PngImage.getImage(uRL);
                return image;
            }
            if (n == 215 && n2 == 205) {
                ImgWMF imgWMF = new ImgWMF(uRL);
                return imgWMF;
            }
            if (n == 66 && n2 == 77) {
                Image image = BmpImage.getImage(uRL);
                return image;
            }
            if (n == 77 && n2 == 77 && n3 == 0 && n4 == 42 || n == 73 && n2 == 73 && n3 == 42 && n4 == 0) {
                RandomAccessFileOrArray randomAccessFileOrArray = null;
                try {
                    Object object;
                    if (uRL.getProtocol().equals("file")) {
                        object = uRL.getFile();
                        object = Utilities.unEscapeURL((String)object);
                        randomAccessFileOrArray = new RandomAccessFileOrArray((String)object);
                    } else {
                        randomAccessFileOrArray = new RandomAccessFileOrArray(uRL);
                    }
                    object = TiffImage.getTiffImage(randomAccessFileOrArray, 1);
                    object.url = uRL;
                    Object object2 = object;
                    return object2;
                }
                finally {
                    if (randomAccessFileOrArray != null) {
                        randomAccessFileOrArray.close();
                    }
                }
            }
            throw new IOException(uRL.toString() + " is not a recognized imageformat.");
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static Image getInstance(String string) throws BadElementException, MalformedURLException, IOException {
        return Image.getInstance(Utilities.toURL(string));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Image getInstance(byte[] arrby) throws BadElementException, MalformedURLException, IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(arrby);
            int n = byteArrayInputStream.read();
            int n2 = byteArrayInputStream.read();
            int n3 = byteArrayInputStream.read();
            int n4 = byteArrayInputStream.read();
            byteArrayInputStream.close();
            byteArrayInputStream = null;
            if (n == 71 && n2 == 73 && n3 == 70) {
                GifImage gifImage = new GifImage(arrby);
                Image image = gifImage.getImage(1);
                return image;
            }
            if (n == 255 && n2 == 216) {
                Jpeg jpeg = new Jpeg(arrby);
                return jpeg;
            }
            if (n == 0 && n2 == 0 && n3 == 0 && n4 == 12) {
                Jpeg2000 jpeg2000 = new Jpeg2000(arrby);
                return jpeg2000;
            }
            if (n == 255 && n2 == 79 && n3 == 255 && n4 == 81) {
                Jpeg2000 jpeg2000 = new Jpeg2000(arrby);
                return jpeg2000;
            }
            if (n == PngImage.PNGID[0] && n2 == PngImage.PNGID[1] && n3 == PngImage.PNGID[2] && n4 == PngImage.PNGID[3]) {
                Image image = PngImage.getImage(arrby);
                return image;
            }
            if (n == 215 && n2 == 205) {
                ImgWMF imgWMF = new ImgWMF(arrby);
                return imgWMF;
            }
            if (n == 66 && n2 == 77) {
                Image image = BmpImage.getImage(arrby);
                return image;
            }
            if (n == 77 && n2 == 77 && n3 == 0 && n4 == 42 || n == 73 && n2 == 73 && n3 == 42 && n4 == 0) {
                RandomAccessFileOrArray randomAccessFileOrArray = null;
                try {
                    randomAccessFileOrArray = new RandomAccessFileOrArray(arrby);
                    Image image = TiffImage.getTiffImage(randomAccessFileOrArray, 1);
                    if (image.getOriginalData() == null) {
                        image.setOriginalData(arrby);
                    }
                    Image image2 = image;
                    return image2;
                }
                finally {
                    if (randomAccessFileOrArray != null) {
                        randomAccessFileOrArray.close();
                    }
                }
            }
            throw new IOException("The byte array is not a recognized imageformat.");
        }
        finally {
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        }
    }

    public static Image getInstance(int n, int n2, int n3, int n4, byte[] arrby) throws BadElementException {
        return Image.getInstance(n, n2, n3, n4, arrby, null);
    }

    public static Image getInstance(int n, int n2, boolean bl, int n3, int n4, byte[] arrby) throws BadElementException {
        return Image.getInstance(n, n2, bl, n3, n4, arrby, null);
    }

    public static Image getInstance(int n, int n2, boolean bl, int n3, int n4, byte[] arrby, int[] arrn) throws BadElementException {
        if (arrn != null && arrn.length != 2) {
            throw new BadElementException("Transparency length must be equal to 2 with CCITT images");
        }
        ImgCCITT imgCCITT = new ImgCCITT(n, n2, bl, n3, n4, arrby);
        imgCCITT.transparency = arrn;
        return imgCCITT;
    }

    public static Image getInstance(int n, int n2, int n3, int n4, byte[] arrby, int[] arrn) throws BadElementException {
        if (arrn != null && arrn.length != n3 * 2) {
            throw new BadElementException("Transparency length must be equal to (componentes * 2)");
        }
        if (n3 == 1 && n4 == 1) {
            byte[] arrby2 = CCITTG4Encoder.compress(arrby, n, n2);
            return Image.getInstance(n, n2, false, 256, 1, arrby2, arrn);
        }
        ImgRaw imgRaw = new ImgRaw(n, n2, n3, n4, arrby);
        imgRaw.transparency = arrn;
        return imgRaw;
    }

    public static Image getInstance(PdfTemplate pdfTemplate) throws BadElementException {
        return new ImgTemplate(pdfTemplate);
    }

    public static Image getInstance(java.awt.Image image, Color color, boolean bl) throws BadElementException, IOException {
        int n;
        int n2;
        Object object;
        if (image instanceof BufferedImage && (object = (BufferedImage)image).getType() == 12) {
            bl = true;
        }
        object = new PixelGrabber(image, 0, 0, -1, -1, true);
        try {
            object.grabPixels();
        }
        catch (InterruptedException var4_4) {
            throw new IOException("java.awt.Image Interrupted waiting for pixels!");
        }
        if ((object.getStatus() & 128) != 0) {
            throw new IOException("java.awt.Image fetch aborted or errored");
        }
        int n3 = object.getWidth();
        int n4 = object.getHeight();
        int[] arrn = (int[])object.getPixels();
        if (bl) {
            int n5 = n3 / 8 + ((n3 & 7) != 0 ? 1 : 0);
            byte[] arrby = new byte[n5 * n4];
            int n6 = 0;
            int n7 = n4 * n3;
            boolean bl2 = true;
            if (color != null) {
                bl2 = color.getRed() + color.getGreen() + color.getBlue() >= 384;
            }
            int[] arrn2 = null;
            int n8 = 128;
            int n9 = 0;
            int n10 = 0;
            if (color != null) {
                for (int i = 0; i < n7; ++i) {
                    int n11 = arrn[i] >> 24 & 255;
                    if (n11 < 250) {
                        if (bl2) {
                            n10 |= n8;
                        }
                    } else if ((arrn[i] & 2184) != 0) {
                        n10 |= n8;
                    }
                    if ((n8 >>= 1) == 0 || n9 + 1 >= n3) {
                        arrby[n6++] = (byte)n10;
                        n8 = 128;
                        n10 = 0;
                    }
                    if (++n9 < n3) continue;
                    n9 = 0;
                }
            } else {
                for (int i = 0; i < n7; ++i) {
                    int n12;
                    if (arrn2 == null && (n12 = arrn[i] >> 24 & 255) == 0) {
                        arrn2 = new int[2];
                        arrn2[1] = (arrn[i] & 2184) != 0 ? 1 : 0;
                        arrn2[0] = arrn2[1];
                    }
                    if ((arrn[i] & 2184) != 0) {
                        n10 |= n8;
                    }
                    if ((n8 >>= 1) == 0 || n9 + 1 >= n3) {
                        arrby[n6++] = (byte)n10;
                        n8 = 128;
                        n10 = 0;
                    }
                    if (++n9 < n3) continue;
                    n9 = 0;
                }
            }
            return Image.getInstance(n3, n4, 1, 1, arrby, arrn2);
        }
        byte[] arrby = new byte[n3 * n4 * 3];
        byte[] arrby2 = null;
        int n13 = 0;
        int n14 = n4 * n3;
        int n15 = 255;
        int n16 = 255;
        int n17 = 255;
        if (color != null) {
            n15 = color.getRed();
            n16 = color.getGreen();
            n17 = color.getBlue();
        }
        int[] arrn3 = null;
        if (color != null) {
            for (n = 0; n < n14; ++n) {
                n2 = arrn[n] >> 24 & 255;
                if (n2 < 250) {
                    arrby[n13++] = (byte)n15;
                    arrby[n13++] = (byte)n16;
                    arrby[n13++] = (byte)n17;
                    continue;
                }
                arrby[n13++] = (byte)(arrn[n] >> 16 & 255);
                arrby[n13++] = (byte)(arrn[n] >> 8 & 255);
                arrby[n13++] = (byte)(arrn[n] & 255);
            }
        } else {
            n = 0;
            arrby2 = new byte[n3 * n4];
            n2 = 0;
            for (int i = 0; i < n14; ++i) {
                byte by = arrby2[i] = (byte)(arrn[i] >> 24 & 255);
                if (n2 == 0) {
                    if (by != 0 && by != -1) {
                        n2 = 1;
                    } else if (arrn3 == null) {
                        if (by == 0) {
                            n = arrn[i] & 16777215;
                            arrn3 = new int[6];
                            arrn3[0] = arrn3[1] = n >> 16 & 255;
                            arrn3[2] = arrn3[3] = n >> 8 & 255;
                            arrn3[4] = arrn3[5] = n & 255;
                        }
                    } else if ((arrn[i] & 16777215) != n) {
                        n2 = 1;
                    }
                }
                arrby[n13++] = (byte)(arrn[i] >> 16 & 255);
                arrby[n13++] = (byte)(arrn[i] >> 8 & 255);
                arrby[n13++] = (byte)(arrn[i] & 255);
            }
            if (n2 != 0) {
                arrn3 = null;
            } else {
                arrby2 = null;
            }
        }
        Image image2 = Image.getInstance(n3, n4, 3, 8, arrby, arrn3);
        if (arrby2 != null) {
            Image image3 = Image.getInstance(n3, n4, 1, 8, arrby2);
            try {
                image3.makeMask();
                image2.setImageMask(image3);
            }
            catch (DocumentException var17_34) {
                throw new ExceptionConverter(var17_34);
            }
        }
        return image2;
    }

    public static Image getInstance(java.awt.Image image, Color color) throws BadElementException, IOException {
        return Image.getInstance(image, color, false);
    }

    public static Image getInstance(PdfWriter pdfWriter, java.awt.Image image, float f) throws BadElementException, IOException {
        return Image.getInstance(new PdfContentByte(pdfWriter), image, f);
    }

    public static Image getInstance(PdfContentByte pdfContentByte, java.awt.Image image, float f) throws BadElementException, IOException {
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, -1, -1, true);
        try {
            pixelGrabber.grabPixels();
        }
        catch (InterruptedException var4_4) {
            throw new IOException("java.awt.Image Interrupted waiting for pixels!");
        }
        if ((pixelGrabber.getStatus() & 128) != 0) {
            throw new IOException("java.awt.Image fetch aborted or errored");
        }
        int n = pixelGrabber.getWidth();
        int n2 = pixelGrabber.getHeight();
        PdfTemplate pdfTemplate = pdfContentByte.createTemplate(n, n2);
        Graphics2D graphics2D = pdfTemplate.createGraphics(n, n2, true, f);
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        return Image.getInstance(pdfTemplate);
    }

    public PdfIndirectReference getDirectReference() {
        return this.directReference;
    }

    public void setDirectReference(PdfIndirectReference pdfIndirectReference) {
        this.directReference = pdfIndirectReference;
    }

    public static Image getInstance(PRIndirectReference pRIndirectReference) throws BadElementException {
        Object object;
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pRIndirectReference);
        int n = ((PdfNumber)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.WIDTH))).intValue();
        int n2 = ((PdfNumber)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.HEIGHT))).intValue();
        Image image = null;
        PdfObject pdfObject = pdfDictionary.get(PdfName.SMASK);
        if (pdfObject != null && pdfObject.isIndirect()) {
            image = Image.getInstance((PRIndirectReference)pdfObject);
        } else {
            pdfObject = pdfDictionary.get(PdfName.MASK);
            if (pdfObject != null && pdfObject.isIndirect() && (object = PdfReader.getPdfObjectRelease(pdfObject)) instanceof PdfDictionary) {
                image = Image.getInstance((PRIndirectReference)pdfObject);
            }
        }
        object = new ImgRaw(n, n2, 1, 1, null);
        object.imageMask = image;
        object.directReference = pRIndirectReference;
        return object;
    }

    protected Image(Image image) {
        super(image);
        this.type = image.type;
        this.url = image.url;
        this.rawData = image.rawData;
        this.bpc = image.bpc;
        this.template = image.template;
        this.alignment = image.alignment;
        this.alt = image.alt;
        this.absoluteX = image.absoluteX;
        this.absoluteY = image.absoluteY;
        this.plainWidth = image.plainWidth;
        this.plainHeight = image.plainHeight;
        this.scaledWidth = image.scaledWidth;
        this.scaledHeight = image.scaledHeight;
        this.mySerialId = image.mySerialId;
        this.directReference = image.directReference;
        this.rotationRadians = image.rotationRadians;
        this.initialRotation = image.initialRotation;
        this.indentationLeft = image.indentationLeft;
        this.indentationRight = image.indentationRight;
        this.spacingBefore = image.spacingBefore;
        this.spacingAfter = image.spacingAfter;
        this.widthPercentage = image.widthPercentage;
        this.annotation = image.annotation;
        this.layer = image.layer;
        this.interpolation = image.interpolation;
        this.originalType = image.originalType;
        this.originalData = image.originalData;
        this.deflated = image.deflated;
        this.dpiX = image.dpiX;
        this.dpiY = image.dpiY;
        this.XYRatio = image.XYRatio;
        this.colorspace = image.colorspace;
        this.invert = image.invert;
        this.profile = image.profile;
        this.additional = image.additional;
        this.mask = image.mask;
        this.imageMask = image.imageMask;
        this.smask = image.smask;
        this.transparency = image.transparency;
    }

    public static Image getInstance(Image image) {
        if (image == null) {
            return null;
        }
        try {
            Class class_ = image.getClass();
            Class[] arrclass = new Class[1];
            Class class_2 = Image.class;
            arrclass[0] = class_2;
            Constructor constructor = class_.getDeclaredConstructor(arrclass);
            return (Image)constructor.newInstance(image);
        }
        catch (Exception var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public int type() {
        return this.type;
    }

    public boolean isNestable() {
        return true;
    }

    public boolean isJpeg() {
        return this.type == 32;
    }

    public boolean isImgRaw() {
        return this.type == 34;
    }

    public boolean isImgTemplate() {
        return this.type == 35;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(URL uRL) {
        this.url = uRL;
    }

    public byte[] getRawData() {
        return this.rawData;
    }

    public int getBpc() {
        return this.bpc;
    }

    public PdfTemplate getTemplateData() {
        return this.template[0];
    }

    public void setTemplateData(PdfTemplate pdfTemplate) {
        this.template[0] = pdfTemplate;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int n) {
        this.alignment = n;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String string) {
        this.alt = string;
    }

    public void setAbsolutePosition(float f, float f2) {
        this.absoluteX = f;
        this.absoluteY = f2;
    }

    public boolean hasAbsoluteX() {
        return !Float.isNaN(this.absoluteX);
    }

    public float getAbsoluteX() {
        return this.absoluteX;
    }

    public boolean hasAbsoluteY() {
        return !Float.isNaN(this.absoluteY);
    }

    public float getAbsoluteY() {
        return this.absoluteY;
    }

    public float getScaledWidth() {
        return this.scaledWidth;
    }

    public float getScaledHeight() {
        return this.scaledHeight;
    }

    public float getPlainWidth() {
        return this.plainWidth;
    }

    public float getPlainHeight() {
        return this.plainHeight;
    }

    public void scaleAbsolute(float f, float f2) {
        this.plainWidth = f;
        this.plainHeight = f2;
        float[] arrf = this.matrix();
        this.scaledWidth = arrf[6] - arrf[4];
        this.scaledHeight = arrf[7] - arrf[5];
        this.setWidthPercentage(0.0f);
    }

    public void scaleAbsoluteWidth(float f) {
        this.plainWidth = f;
        float[] arrf = this.matrix();
        this.scaledWidth = arrf[6] - arrf[4];
        this.scaledHeight = arrf[7] - arrf[5];
        this.setWidthPercentage(0.0f);
    }

    public void scaleAbsoluteHeight(float f) {
        this.plainHeight = f;
        float[] arrf = this.matrix();
        this.scaledWidth = arrf[6] - arrf[4];
        this.scaledHeight = arrf[7] - arrf[5];
        this.setWidthPercentage(0.0f);
    }

    public void scalePercent(float f) {
        this.scalePercent(f, f);
    }

    public void scalePercent(float f, float f2) {
        this.plainWidth = this.getWidth() * f / 100.0f;
        this.plainHeight = this.getHeight() * f2 / 100.0f;
        float[] arrf = this.matrix();
        this.scaledWidth = arrf[6] - arrf[4];
        this.scaledHeight = arrf[7] - arrf[5];
        this.setWidthPercentage(0.0f);
    }

    public void scaleToFit(float f, float f2) {
        this.scalePercent(100.0f);
        float f3 = f * 100.0f / this.getScaledWidth();
        float f4 = f2 * 100.0f / this.getScaledHeight();
        this.scalePercent(f3 < f4 ? f3 : f4);
        this.setWidthPercentage(0.0f);
    }

    public float[] matrix() {
        float[] arrf = new float[8];
        float f = (float)Math.cos(this.rotationRadians);
        float f2 = (float)Math.sin(this.rotationRadians);
        arrf[0] = this.plainWidth * f;
        arrf[1] = this.plainWidth * f2;
        arrf[2] = (- this.plainHeight) * f2;
        arrf[3] = this.plainHeight * f;
        if ((double)this.rotationRadians < 1.5707963267948966) {
            arrf[4] = arrf[2];
            arrf[5] = 0.0f;
            arrf[6] = arrf[0];
            arrf[7] = arrf[1] + arrf[3];
        } else if ((double)this.rotationRadians < 3.141592653589793) {
            arrf[4] = arrf[0] + arrf[2];
            arrf[5] = arrf[3];
            arrf[6] = 0.0f;
            arrf[7] = arrf[1];
        } else if ((double)this.rotationRadians < 4.71238898038469) {
            arrf[4] = arrf[0];
            arrf[5] = arrf[1] + arrf[3];
            arrf[6] = arrf[2];
            arrf[7] = 0.0f;
        } else {
            arrf[4] = 0.0f;
            arrf[5] = arrf[1];
            arrf[6] = arrf[0] + arrf[2];
            arrf[7] = arrf[3];
        }
        return arrf;
    }

    protected static synchronized Long getSerialId() {
        return new Long(++serialId);
    }

    public Long getMySerialId() {
        return this.mySerialId;
    }

    public float getImageRotation() {
        double d = 6.283185307179586;
        float f = (float)((double)(this.rotationRadians - this.initialRotation) % d);
        if (f < 0.0f) {
            f = (float)((double)f + d);
        }
        return f;
    }

    public void setRotation(float f) {
        double d = 6.283185307179586;
        this.rotationRadians = (float)((double)(f + this.initialRotation) % d);
        if (this.rotationRadians < 0.0f) {
            this.rotationRadians = (float)((double)this.rotationRadians + d);
        }
        float[] arrf = this.matrix();
        this.scaledWidth = arrf[6] - arrf[4];
        this.scaledHeight = arrf[7] - arrf[5];
    }

    public void setRotationDegrees(float f) {
        double d = 3.141592653589793;
        this.setRotation(f / 180.0f * (float)d);
    }

    public float getInitialRotation() {
        return this.initialRotation;
    }

    public void setInitialRotation(float f) {
        float f2 = this.rotationRadians - this.initialRotation;
        this.initialRotation = f;
        this.setRotation(f2);
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public void setIndentationLeft(float f) {
        this.indentationLeft = f;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public void setIndentationRight(float f) {
        this.indentationRight = f;
    }

    public float getSpacingBefore() {
        return this.spacingBefore;
    }

    public void setSpacingBefore(float f) {
        this.spacingBefore = f;
    }

    public float getSpacingAfter() {
        return this.spacingAfter;
    }

    public void setSpacingAfter(float f) {
        this.spacingAfter = f;
    }

    public float getWidthPercentage() {
        return this.widthPercentage;
    }

    public void setWidthPercentage(float f) {
        this.widthPercentage = f;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public PdfOCG getLayer() {
        return this.layer;
    }

    public void setLayer(PdfOCG pdfOCG) {
        this.layer = pdfOCG;
    }

    public boolean isInterpolation() {
        return this.interpolation;
    }

    public void setInterpolation(boolean bl) {
        this.interpolation = bl;
    }

    public int getOriginalType() {
        return this.originalType;
    }

    public void setOriginalType(int n) {
        this.originalType = n;
    }

    public byte[] getOriginalData() {
        return this.originalData;
    }

    public void setOriginalData(byte[] arrby) {
        this.originalData = arrby;
    }

    public boolean isDeflated() {
        return this.deflated;
    }

    public void setDeflated(boolean bl) {
        this.deflated = bl;
    }

    public int getDpiX() {
        return this.dpiX;
    }

    public int getDpiY() {
        return this.dpiY;
    }

    public void setDpi(int n, int n2) {
        this.dpiX = n;
        this.dpiY = n2;
    }

    public float getXYRatio() {
        return this.XYRatio;
    }

    public void setXYRatio(float f) {
        this.XYRatio = f;
    }

    public int getColorspace() {
        return this.colorspace;
    }

    public boolean isInverted() {
        return this.invert;
    }

    public void setInverted(boolean bl) {
        this.invert = bl;
    }

    public void tagICC(ICC_Profile iCC_Profile) {
        this.profile = iCC_Profile;
    }

    public boolean hasICCProfile() {
        return this.profile != null;
    }

    public ICC_Profile getICCProfile() {
        return this.profile;
    }

    public PdfDictionary getAdditional() {
        return this.additional;
    }

    public void setAdditional(PdfDictionary pdfDictionary) {
        this.additional = pdfDictionary;
    }

    public void simplifyColorspace() {
        if (this.additional == null) {
            return;
        }
        PdfObject pdfObject = this.additional.get(PdfName.COLORSPACE);
        if (pdfObject == null || !pdfObject.isArray()) {
            return;
        }
        PdfObject pdfObject2 = this.simplifyColorspace(pdfObject);
        if (pdfObject2.isName()) {
            pdfObject = pdfObject2;
        } else {
            ArrayList arrayList;
            PdfObject pdfObject3 = (PdfObject)((PdfArray)pdfObject).getArrayList().get(0);
            if (PdfName.INDEXED.equals(pdfObject3) && (arrayList = ((PdfArray)pdfObject).getArrayList()).size() >= 2 && ((PdfObject)arrayList.get(1)).isArray()) {
                arrayList.set(1, this.simplifyColorspace((PdfObject)arrayList.get(1)));
            }
        }
        this.additional.put(PdfName.COLORSPACE, pdfObject);
    }

    private PdfObject simplifyColorspace(PdfObject pdfObject) {
        if (pdfObject == null || !pdfObject.isArray()) {
            return pdfObject;
        }
        PdfObject pdfObject2 = (PdfObject)((PdfArray)pdfObject).getArrayList().get(0);
        if (PdfName.CALGRAY.equals(pdfObject2)) {
            return PdfName.DEVICEGRAY;
        }
        if (PdfName.CALRGB.equals(pdfObject2)) {
            return PdfName.DEVICERGB;
        }
        return pdfObject;
    }

    public boolean isMask() {
        return this.mask;
    }

    public void makeMask() throws DocumentException {
        if (!this.isMaskCandidate()) {
            throw new DocumentException("This image can not be an image mask.");
        }
        this.mask = true;
    }

    public boolean isMaskCandidate() {
        if (this.type == 34 && this.bpc > 255) {
            return true;
        }
        return this.colorspace == 1;
    }

    public Image getImageMask() {
        return this.imageMask;
    }

    public void setImageMask(Image image) throws DocumentException {
        if (this.mask) {
            throw new DocumentException("An image mask cannot contain another image mask.");
        }
        if (!image.mask) {
            throw new DocumentException("The image mask is not a mask. Did you do makeMask()?");
        }
        this.imageMask = image;
        this.smask = image.bpc > 1 && image.bpc <= 8;
    }

    public boolean isSmask() {
        return this.smask;
    }

    public void setSmask(boolean bl) {
        this.smask = bl;
    }

    public int[] getTransparency() {
        return this.transparency;
    }

    public void setTransparency(int[] arrn) {
        this.transparency = arrn;
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int n) {
        this.compressionLevel = n < 0 || n > 9 ? -1 : n;
    }
}

