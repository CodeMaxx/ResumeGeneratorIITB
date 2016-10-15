/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Jpeg;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.CCITTG4Encoder;
import com.lowagie.text.pdf.codec.TIFFDirectory;
import com.lowagie.text.pdf.codec.TIFFFaxDecoder;
import com.lowagie.text.pdf.codec.TIFFField;
import com.lowagie.text.pdf.codec.TIFFLZWDecoder;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

public class TiffImage {
    public static int getNumberOfPages(RandomAccessFileOrArray randomAccessFileOrArray) {
        try {
            return TIFFDirectory.getNumDirectories(randomAccessFileOrArray);
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    static int getDpi(TIFFField tIFFField, int n) {
        if (tIFFField == null) {
            return 0;
        }
        long[] arrl = tIFFField.getAsRational(0);
        float f = (float)arrl[0] / (float)arrl[1];
        int n2 = 0;
        switch (n) {
            case 1: 
            case 2: {
                n2 = (int)((double)f + 0.5);
                break;
            }
            case 3: {
                n2 = (int)((double)f * 2.54 + 0.5);
            }
        }
        return n2;
    }

    public static Image getTiffImage(RandomAccessFileOrArray randomAccessFileOrArray, int n) {
        return TiffImage.getTiffImage(randomAccessFileOrArray, n, false);
    }

    public static Image getTiffImage(RandomAccessFileOrArray randomAccessFileOrArray, int n, boolean bl) {
        if (n < 1) {
            throw new IllegalArgumentException("The page number must be >= 1.");
        }
        try {
            long l;
            Object object;
            Object object2;
            TIFFDirectory tIFFDirectory = new TIFFDirectory(randomAccessFileOrArray, n - 1);
            if (tIFFDirectory.isTagPresent(322)) {
                throw new IllegalArgumentException("Tiles are not supported.");
            }
            int n2 = (int)tIFFDirectory.getFieldAsLong(259);
            switch (n2) {
                case 2: 
                case 3: 
                case 4: 
                case 32771: {
                    break;
                }
                default: {
                    return TiffImage.getTiffImageColor(tIFFDirectory, randomAccessFileOrArray);
                }
            }
            float f = 0.0f;
            if (tIFFDirectory.isTagPresent(274)) {
                int n3 = (int)tIFFDirectory.getFieldAsLong(274);
                if (n3 == 3 || n3 == 4) {
                    f = 3.1415927f;
                } else if (n3 == 5 || n3 == 8) {
                    f = 1.5707964f;
                } else if (n3 == 6 || n3 == 7) {
                    f = -1.5707964f;
                }
            }
            Image image = null;
            long l2 = 0;
            long l3 = 0;
            int n4 = 1;
            int n5 = (int)tIFFDirectory.getFieldAsLong(257);
            int n6 = (int)tIFFDirectory.getFieldAsLong(256);
            int n7 = 0;
            int n8 = 0;
            float f2 = 0.0f;
            int n9 = 2;
            if (tIFFDirectory.isTagPresent(296)) {
                n9 = (int)tIFFDirectory.getFieldAsLong(296);
            }
            n7 = TiffImage.getDpi(tIFFDirectory.getField(282), n9);
            n8 = TiffImage.getDpi(tIFFDirectory.getField(283), n9);
            if (n9 == 1) {
                if (n8 != 0) {
                    f2 = (float)n7 / (float)n8;
                }
                n7 = 0;
                n8 = 0;
            }
            int n10 = n5;
            if (tIFFDirectory.isTagPresent(278)) {
                n10 = (int)tIFFDirectory.getFieldAsLong(278);
            }
            if (n10 <= 0 || n10 > n5) {
                n10 = n5;
            }
            long[] arrl = TiffImage.getArrayLongShort(tIFFDirectory, 273);
            long[] arrl2 = TiffImage.getArrayLongShort(tIFFDirectory, 279);
            if ((arrl2 == null || arrl2.length == 1 && (arrl2[0] == 0 || arrl2[0] + arrl[0] > (long)randomAccessFileOrArray.length())) && n5 == n10) {
                arrl2 = new long[]{randomAccessFileOrArray.length() - (int)arrl[0]};
            }
            boolean bl2 = false;
            TIFFField tIFFField = tIFFDirectory.getField(266);
            if (tIFFField != null) {
                n4 = tIFFField.getAsInt(0);
            }
            bl2 = n4 == 2;
            int n11 = 0;
            if (tIFFDirectory.isTagPresent(262) && (l = tIFFDirectory.getFieldAsLong(262)) == 1) {
                n11 |= 1;
            }
            int n12 = 0;
            switch (n2) {
                case 2: 
                case 32771: {
                    n12 = 257;
                    n11 |= 10;
                    break;
                }
                case 3: {
                    n12 = 257;
                    n11 |= 12;
                    object = tIFFDirectory.getField(292);
                    if (object == null) break;
                    l2 = object.getAsLong(0);
                    if ((l2 & 1) != 0) {
                        n12 = 258;
                    }
                    if ((l2 & 4) == 0) break;
                    n11 |= 2;
                    break;
                }
                case 4: {
                    n12 = 256;
                    object2 = tIFFDirectory.getField(293);
                    if (object2 == null) break;
                    l3 = object2.getAsLong(0);
                }
            }
            if (bl && n10 == n5) {
                object = new byte[(int)arrl2[0]];
                randomAccessFileOrArray.seek(arrl[0]);
                randomAccessFileOrArray.readFully((byte[])object);
                image = Image.getInstance(n6, n5, false, n12, n11, (byte[])object);
                image.setInverted(true);
            } else {
                int n13 = n5;
                object2 = new CCITTG4Encoder(n6);
                for (int i = 0; i < arrl.length; ++i) {
                    byte[] arrby = new byte[(int)arrl2[i]];
                    randomAccessFileOrArray.seek(arrl[i]);
                    randomAccessFileOrArray.readFully(arrby);
                    int n14 = Math.min(n10, n13);
                    TIFFFaxDecoder tIFFFaxDecoder = new TIFFFaxDecoder(n4, n6, n14);
                    byte[] arrby2 = new byte[(n6 + 7) / 8 * n14];
                    switch (n2) {
                        case 2: 
                        case 32771: {
                            tIFFFaxDecoder.decode1D(arrby2, arrby, 0, n14);
                            object2.fax4Encode(arrby2, n14);
                            break;
                        }
                        case 3: {
                            try {
                                tIFFFaxDecoder.decode2D(arrby2, arrby, 0, n14, l2);
                            }
                            catch (RuntimeException var32_36) {
                                l2 ^= 4;
                                try {
                                    tIFFFaxDecoder.decode2D(arrby2, arrby, 0, n14, l2);
                                }
                                catch (RuntimeException var33_37) {
                                    throw var32_36;
                                }
                            }
                            object2.fax4Encode(arrby2, n14);
                            break;
                        }
                        case 4: {
                            tIFFFaxDecoder.decodeT6(arrby2, arrby, 0, n14, l3);
                            object2.fax4Encode(arrby2, n14);
                        }
                    }
                    n13 -= n10;
                }
                byte[] arrby = object2.close();
                image = Image.getInstance(n6, n5, false, 256, n11 & 1, arrby);
            }
            image.setDpi(n7, n8);
            image.setXYRatio(f2);
            if (tIFFDirectory.isTagPresent(34675)) {
                try {
                    object = tIFFDirectory.getField(34675);
                    object2 = ICC_Profile.getInstance(object.getAsBytes());
                    if (object2.getNumComponents() == 1) {
                        image.tagICC((ICC_Profile)object2);
                    }
                }
                catch (RuntimeException var25_28) {
                    // empty catch block
                }
            }
            image.setOriginalType(5);
            if (f != 0.0f) {
                image.setInitialRotation(f);
            }
            return image;
        }
        catch (Exception var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    protected static Image getTiffImageColor(TIFFDirectory tIFFDirectory, RandomAccessFileOrArray randomAccessFileOrArray) {
        try {
            int n;
            int n2;
            byte[] arrby;
            int n3 = (int)tIFFDirectory.getFieldAsLong(259);
            int n4 = 1;
            TIFFLZWDecoder tIFFLZWDecoder = null;
            switch (n3) {
                case 1: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 32773: 
                case 32946: {
                    break;
                }
                default: {
                    throw new IllegalArgumentException("The compression " + n3 + " is not supported.");
                }
            }
            int n5 = (int)tIFFDirectory.getFieldAsLong(262);
            switch (n5) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 5: {
                    break;
                }
                default: {
                    if (n3 == 6 || n3 == 7) break;
                    throw new IllegalArgumentException("The photometric " + n5 + " is not supported.");
                }
            }
            float f = 0.0f;
            if (tIFFDirectory.isTagPresent(274)) {
                n = (int)tIFFDirectory.getFieldAsLong(274);
                if (n == 3 || n == 4) {
                    f = 3.1415927f;
                } else if (n == 5 || n == 8) {
                    f = 1.5707964f;
                } else if (n == 6 || n == 7) {
                    f = -1.5707964f;
                }
            }
            if (tIFFDirectory.isTagPresent(284) && tIFFDirectory.getFieldAsLong(284) == 2) {
                throw new IllegalArgumentException("Planar images are not supported.");
            }
            if (tIFFDirectory.isTagPresent(338)) {
                throw new IllegalArgumentException("Extra samples are not supported.");
            }
            n = 1;
            if (tIFFDirectory.isTagPresent(277)) {
                n = (int)tIFFDirectory.getFieldAsLong(277);
            }
            int n6 = 1;
            if (tIFFDirectory.isTagPresent(258)) {
                n6 = (int)tIFFDirectory.getFieldAsLong(258);
            }
            switch (n6) {
                case 1: 
                case 2: 
                case 4: 
                case 8: {
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Bits per sample " + n6 + " is not supported.");
                }
            }
            Image image = null;
            int n7 = (int)tIFFDirectory.getFieldAsLong(257);
            int n8 = (int)tIFFDirectory.getFieldAsLong(256);
            int n9 = 0;
            int n10 = 0;
            int n11 = 2;
            if (tIFFDirectory.isTagPresent(296)) {
                n11 = (int)tIFFDirectory.getFieldAsLong(296);
            }
            n9 = TiffImage.getDpi(tIFFDirectory.getField(282), n11);
            n10 = TiffImage.getDpi(tIFFDirectory.getField(283), n11);
            int n12 = 1;
            boolean bl = false;
            TIFFField tIFFField = tIFFDirectory.getField(266);
            if (tIFFField != null) {
                n12 = tIFFField.getAsInt(0);
            }
            bl = n12 == 2;
            int n13 = n7;
            if (tIFFDirectory.isTagPresent(278)) {
                n13 = (int)tIFFDirectory.getFieldAsLong(278);
            }
            if (n13 <= 0 || n13 > n7) {
                n13 = n7;
            }
            long[] arrl = TiffImage.getArrayLongShort(tIFFDirectory, 273);
            long[] arrl2 = TiffImage.getArrayLongShort(tIFFDirectory, 279);
            if ((arrl2 == null || arrl2.length == 1 && (arrl2[0] == 0 || arrl2[0] + arrl[0] > (long)randomAccessFileOrArray.length())) && n7 == n13) {
                arrl2 = new long[]{randomAccessFileOrArray.length() - (int)arrl[0]};
            }
            if (n3 == 5) {
                TIFFField tIFFField2 = tIFFDirectory.getField(317);
                if (tIFFField2 != null) {
                    n4 = tIFFField2.getAsInt(0);
                    if (n4 != 1 && n4 != 2) {
                        throw new RuntimeException("Illegal value for Predictor in TIFF file.");
                    }
                    if (n4 == 2 && n6 != 8) {
                        throw new RuntimeException("" + n6 + "-bit samples are not supported for Horizontal differencing Predictor.");
                    }
                }
                tIFFLZWDecoder = new TIFFLZWDecoder(n8, n4, n);
            }
            int n14 = n7;
            ByteArrayOutputStream byteArrayOutputStream = null;
            DeflaterOutputStream deflaterOutputStream = null;
            CCITTG4Encoder cCITTG4Encoder = null;
            if (n6 == 1 && n == 1) {
                cCITTG4Encoder = new CCITTG4Encoder(n8);
            } else {
                byteArrayOutputStream = new ByteArrayOutputStream();
                if (n3 != 6 && n3 != 7) {
                    deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
                }
            }
            if (n3 == 6) {
                if (!tIFFDirectory.isTagPresent(513)) {
                    throw new IOException("Missing tag(s) for OJPEG compression.");
                }
                int n15 = (int)tIFFDirectory.getFieldAsLong(513);
                int n16 = randomAccessFileOrArray.length() - n15;
                if (tIFFDirectory.isTagPresent(514)) {
                    n16 = (int)tIFFDirectory.getFieldAsLong(514) + (int)arrl2[0];
                }
                arrby = new byte[Math.min(n16, randomAccessFileOrArray.length() - n15)];
                n2 = randomAccessFileOrArray.getFilePointer();
                randomAccessFileOrArray.seek(n2 += n15);
                randomAccessFileOrArray.readFully(arrby);
                image = new Jpeg(arrby);
            } else if (n3 == 7) {
                if (arrl2.length > 1) {
                    throw new IOException("Compression JPEG is only supported with a single strip. This image has " + arrl2.length + " strips.");
                }
                byte[] arrby2 = new byte[(int)arrl2[0]];
                randomAccessFileOrArray.seek(arrl[0]);
                randomAccessFileOrArray.readFully(arrby2);
                image = new Jpeg(arrby2);
            } else {
                for (int i = 0; i < arrl.length; ++i) {
                    byte[] arrby3 = new byte[(int)arrl2[i]];
                    randomAccessFileOrArray.seek(arrl[i]);
                    randomAccessFileOrArray.readFully(arrby3);
                    int n17 = Math.min(n13, n14);
                    byte[] arrby4 = null;
                    if (n3 != 1) {
                        arrby4 = new byte[(n8 * n6 * n + 7) / 8 * n17];
                    }
                    if (bl) {
                        TIFFFaxDecoder.reverseBits(arrby3);
                    }
                    switch (n3) {
                        case 8: 
                        case 32946: {
                            TiffImage.inflate(arrby3, arrby4);
                            break;
                        }
                        case 1: {
                            arrby4 = arrby3;
                            break;
                        }
                        case 32773: {
                            TiffImage.decodePackbits(arrby3, arrby4);
                            break;
                        }
                        case 5: {
                            tIFFLZWDecoder.decode(arrby3, arrby4, n17);
                        }
                    }
                    if (n6 == 1 && n == 1) {
                        cCITTG4Encoder.fax4Encode(arrby4, n17);
                    } else {
                        deflaterOutputStream.write(arrby4);
                    }
                    n14 -= n13;
                }
                if (n6 == 1 && n == 1) {
                    image = Image.getInstance(n8, n7, false, 256, n5 == 1 ? 1 : 0, cCITTG4Encoder.close());
                } else {
                    deflaterOutputStream.close();
                    image = Image.getInstance(n8, n7, n, n6, byteArrayOutputStream.toByteArray());
                    image.setDeflated(true);
                }
            }
            image.setDpi(n9, n10);
            if (n3 != 6 && n3 != 7) {
                if (tIFFDirectory.isTagPresent(34675)) {
                    try {
                        TIFFField tIFFField3 = tIFFDirectory.getField(34675);
                        ICC_Profile iCC_Profile = ICC_Profile.getInstance(tIFFField3.getAsBytes());
                        if (n == iCC_Profile.getNumComponents()) {
                            image.tagICC(iCC_Profile);
                        }
                    }
                    catch (RuntimeException var25_31) {
                        // empty catch block
                    }
                }
                if (tIFFDirectory.isTagPresent(320)) {
                    TIFFField tIFFField4 = tIFFDirectory.getField(320);
                    char[] arrc = tIFFField4.getAsChars();
                    arrby = new byte[arrc.length];
                    n2 = arrc.length / 3;
                    int n18 = n2 * 2;
                    for (int i = 0; i < n2; ++i) {
                        arrby[i * 3] = (byte)(arrc[i] >>> 8);
                        arrby[i * 3 + 1] = (byte)(arrc[i + n2] >>> 8);
                        arrby[i * 3 + 2] = (byte)(arrc[i + n18] >>> 8);
                    }
                    PdfArray pdfArray = new PdfArray();
                    pdfArray.add(PdfName.INDEXED);
                    pdfArray.add(PdfName.DEVICERGB);
                    pdfArray.add(new PdfNumber(n2 - 1));
                    pdfArray.add(new PdfString(arrby));
                    PdfDictionary pdfDictionary = new PdfDictionary();
                    pdfDictionary.put(PdfName.COLORSPACE, pdfArray);
                    image.setAdditional(pdfDictionary);
                }
                image.setOriginalType(5);
            }
            if (n5 == 0) {
                image.setInverted(true);
            }
            if (f != 0.0f) {
                image.setInitialRotation(f);
            }
            return image;
        }
        catch (Exception var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    static long[] getArrayLongShort(TIFFDirectory tIFFDirectory, int n) {
        long[] arrl;
        TIFFField tIFFField = tIFFDirectory.getField(n);
        if (tIFFField == null) {
            return null;
        }
        if (tIFFField.getType() == 4) {
            arrl = tIFFField.getAsLongs();
        } else {
            char[] arrc = tIFFField.getAsChars();
            arrl = new long[arrc.length];
            for (int i = 0; i < arrc.length; ++i) {
                arrl[i] = arrc[i];
            }
        }
        return arrl;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static void decodePackbits(byte[] arrby, byte[] arrby2) {
        int n = 0;
        int n2 = 0;
        try {
            block2 : while (n2 < arrby2.length) {
                byte by;
                int n3;
                if ((by = arrby[n++]) >= 0 && by <= 127) {
                    n3 = 0;
                    do {
                        if (n3 >= by + 1) continue block2;
                        arrby2[n2++] = arrby[n++];
                        ++n3;
                    } while (true);
                }
                if (by <= -1 && by >= -127) {
                    byte by2 = arrby[n++];
                    n3 = 0;
                    do {
                        if (n3 >= - by + 1) continue block2;
                        arrby2[n2++] = by2;
                        ++n3;
                    } while (true);
                }
                ++n;
            }
            return;
        }
        catch (Exception var6_7) {
            // empty catch block
        }
    }

    public static void inflate(byte[] arrby, byte[] arrby2) {
        Inflater inflater = new Inflater();
        inflater.setInput(arrby);
        try {
            inflater.inflate(arrby2);
        }
        catch (DataFormatException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
    }
}

