/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.Utilities;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Jpeg
extends Image {
    public static final int NOT_A_MARKER = -1;
    public static final int VALID_MARKER = 0;
    public static final int[] VALID_MARKERS = new int[]{192, 193, 194};
    public static final int UNSUPPORTED_MARKER = 1;
    public static final int[] UNSUPPORTED_MARKERS = new int[]{195, 197, 198, 199, 200, 201, 202, 203, 205, 206, 207};
    public static final int NOPARAM_MARKER = 2;
    public static final int[] NOPARAM_MARKERS = new int[]{208, 209, 210, 211, 212, 213, 214, 215, 216, 1};
    public static final int M_APP0 = 224;
    public static final int M_APP2 = 226;
    public static final int M_APPE = 238;
    public static final byte[] JFIF_ID = new byte[]{74, 70, 73, 70, 0};
    private byte[][] icc;

    Jpeg(Image image) {
        super(image);
    }

    public Jpeg(URL uRL) throws BadElementException, IOException {
        super(uRL);
        this.processParameters();
    }

    public Jpeg(byte[] arrby) throws BadElementException, IOException {
        super((URL)null);
        this.rawData = arrby;
        this.originalData = arrby;
        this.processParameters();
    }

    public Jpeg(byte[] arrby, float f, float f2) throws BadElementException, IOException {
        this(arrby);
        this.scaledWidth = f;
        this.scaledHeight = f2;
    }

    private static final int getShort(InputStream inputStream) throws IOException {
        return (inputStream.read() << 8) + inputStream.read();
    }

    private static final int marker(int n) {
        int n2;
        for (n2 = 0; n2 < VALID_MARKERS.length; ++n2) {
            if (n != VALID_MARKERS[n2]) continue;
            return 0;
        }
        for (n2 = 0; n2 < NOPARAM_MARKERS.length; ++n2) {
            if (n != NOPARAM_MARKERS[n2]) continue;
            return 2;
        }
        for (n2 = 0; n2 < UNSUPPORTED_MARKERS.length; ++n2) {
            if (n != UNSUPPORTED_MARKERS[n2]) continue;
            return 1;
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processParameters() throws BadElementException, IOException {
        int n;
        int n2;
        this.type = 32;
        this.originalType = 1;
        InputStream inputStream = null;
        try {
            String string;
            if (this.rawData == null) {
                inputStream = this.url.openStream();
                string = this.url.toString();
            } else {
                inputStream = new ByteArrayInputStream(this.rawData);
                string = "Byte array";
            }
            if (inputStream.read() != 255 || inputStream.read() != 216) {
                throw new BadElementException(string + " is not a valid JPEG-file.");
            }
            n = 1;
            do {
                int n3;
                int n4;
                int n5;
                int n6;
                byte[] arrby;
                if ((n5 = inputStream.read()) < 0) {
                    throw new IOException("Premature EOF while reading JPG.");
                }
                if (n5 != 255) continue;
                int n7 = inputStream.read();
                if (n != 0 && n7 == 224) {
                    n = 0;
                    n2 = Jpeg.getShort(inputStream);
                    if (n2 < 16) {
                        Utilities.skip(inputStream, n2 - 2);
                        continue;
                    }
                    arrby = new byte[JFIF_ID.length];
                    n6 = inputStream.read(arrby);
                    if (n6 != arrby.length) {
                        throw new BadElementException(string + " corrupted JFIF marker.");
                    }
                    n4 = 1;
                    for (n3 = 0; n3 < arrby.length; ++n3) {
                        if (arrby[n3] == JFIF_ID[n3]) continue;
                        n4 = 0;
                        break;
                    }
                    if (n4 == 0) {
                        Utilities.skip(inputStream, n2 - 2 - arrby.length);
                        continue;
                    }
                    Utilities.skip(inputStream, 2);
                    n3 = inputStream.read();
                    int n8 = Jpeg.getShort(inputStream);
                    int n9 = Jpeg.getShort(inputStream);
                    if (n3 == 1) {
                        this.dpiX = n8;
                        this.dpiY = n9;
                    } else if (n3 == 2) {
                        this.dpiX = (int)((float)n8 * 2.54f + 0.5f);
                        this.dpiY = (int)((float)n9 * 2.54f + 0.5f);
                    }
                    Utilities.skip(inputStream, n2 - 2 - arrby.length - 7);
                    continue;
                }
                if (n7 == 238) {
                    String string2;
                    n2 = Jpeg.getShort(inputStream) - 2;
                    arrby = new byte[n2];
                    for (n6 = 0; n6 < n2; ++n6) {
                        arrby[n6] = (byte)inputStream.read();
                    }
                    if (arrby.length < 12 || !(string2 = new String(arrby, 0, 5, "ISO-8859-1")).equals("Adobe")) continue;
                    this.invert = true;
                    continue;
                }
                if (n7 == 226) {
                    String string3;
                    n2 = Jpeg.getShort(inputStream) - 2;
                    arrby = new byte[n2];
                    for (n6 = 0; n6 < n2; ++n6) {
                        arrby[n6] = (byte)inputStream.read();
                    }
                    if (arrby.length < 14 || !(string3 = new String(arrby, 0, 11, "ISO-8859-1")).equals("ICC_PROFILE")) continue;
                    n4 = arrby[12] & 255;
                    n3 = arrby[13] & 255;
                    if (this.icc == null) {
                        this.icc = new byte[n3][];
                    }
                    this.icc[n4 - 1] = arrby;
                    continue;
                }
                n = 0;
                int n10 = Jpeg.marker(n7);
                if (n10 == 0) {
                    Utilities.skip(inputStream, 2);
                    if (inputStream.read() != 8) {
                        throw new BadElementException(string + " must have 8 bits per component.");
                    }
                    this.scaledHeight = Jpeg.getShort(inputStream);
                    this.setTop(this.scaledHeight);
                    this.scaledWidth = Jpeg.getShort(inputStream);
                    this.setRight(this.scaledWidth);
                    this.colorspace = inputStream.read();
                    this.bpc = 8;
                    break;
                }
                if (n10 == 1) {
                    throw new BadElementException(string + ": unsupported JPEG marker: " + n7);
                }
                if (n10 == 2) continue;
                Utilities.skip(inputStream, Jpeg.getShort(inputStream) - 2);
            } while (true);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
        if (this.icc != null) {
            int n11 = 0;
            for (n = 0; n < this.icc.length; ++n) {
                if (this.icc[n] == null) {
                    this.icc = null;
                    return;
                }
                n11 += this.icc[n].length - 14;
            }
            byte[] arrby = new byte[n11];
            n11 = 0;
            for (n2 = 0; n2 < this.icc.length; ++n2) {
                System.arraycopy(this.icc[n2], 14, arrby, n11, this.icc[n2].length - 14);
                n11 += this.icc[n2].length - 14;
            }
            ICC_Profile iCC_Profile = ICC_Profile.getInstance(arrby);
            this.tagICC(iCC_Profile);
            this.icc = null;
        }
    }
}

