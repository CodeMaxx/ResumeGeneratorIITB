/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.Utilities;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Jpeg2000
extends Image {
    public static final int JP2_JP = 1783636000;
    public static final int JP2_IHDR = 1768449138;
    public static final int JPIP_JPIP = 1785751920;
    public static final int JP2_FTYP = 1718909296;
    public static final int JP2_JP2H = 1785737832;
    public static final int JP2_COLR = 1668246642;
    public static final int JP2_JP2C = 1785737827;
    public static final int JP2_URL = 1970433056;
    public static final int JP2_DBTL = 1685348972;
    public static final int JP2_BPCC = 1651532643;
    public static final int JP2_JP2 = 1785737760;
    InputStream inp;
    int boxLength;
    int boxType;

    Jpeg2000(Image image) {
        super(image);
    }

    public Jpeg2000(URL uRL) throws BadElementException, IOException {
        super(uRL);
        this.processParameters();
    }

    public Jpeg2000(byte[] arrby) throws BadElementException, IOException {
        super((URL)null);
        this.rawData = arrby;
        this.originalData = arrby;
        this.processParameters();
    }

    public Jpeg2000(byte[] arrby, float f, float f2) throws BadElementException, IOException {
        this(arrby);
        this.scaledWidth = f;
        this.scaledHeight = f2;
    }

    private int cio_read(int n) throws IOException {
        int n2 = 0;
        for (int i = n - 1; i >= 0; --i) {
            n2 += this.inp.read() << (i << 3);
        }
        return n2;
    }

    public void jp2_read_boxhdr() throws IOException {
        this.boxLength = this.cio_read(4);
        this.boxType = this.cio_read(4);
        if (this.boxLength == 1) {
            if (this.cio_read(4) != 0) {
                throw new IOException("Cannot handle box sizes higher than 2^32");
            }
            this.boxLength = this.cio_read(4);
            if (this.boxLength == 0) {
                throw new IOException("Unsupported box size == 0");
            }
        } else if (this.boxLength == 0) {
            throw new IOException("Unsupported box size == 0");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processParameters() throws IOException {
        block18 : {
            this.type = 33;
            this.originalType = 8;
            this.inp = null;
            try {
                String string;
                if (this.rawData == null) {
                    this.inp = this.url.openStream();
                    string = this.url.toString();
                } else {
                    this.inp = new ByteArrayInputStream(this.rawData);
                    string = "Byte array";
                }
                this.boxLength = this.cio_read(4);
                if (this.boxLength == 12) {
                    this.boxType = this.cio_read(4);
                    if (1783636000 != this.boxType) {
                        throw new IOException("Expected JP Marker");
                    }
                    if (218793738 != this.cio_read(4)) {
                        throw new IOException("Error with JP Marker");
                    }
                    this.jp2_read_boxhdr();
                    if (1718909296 != this.boxType) {
                        throw new IOException("Expected FTYP Marker");
                    }
                    Utilities.skip(this.inp, this.boxLength - 8);
                    this.jp2_read_boxhdr();
                    do {
                        if (1785737832 == this.boxType) continue;
                        if (this.boxType == 1785737827) {
                            throw new IOException("Expected JP2H Marker");
                        }
                        Utilities.skip(this.inp, this.boxLength - 8);
                        this.jp2_read_boxhdr();
                    } while (1785737832 != this.boxType);
                    this.jp2_read_boxhdr();
                    if (1768449138 != this.boxType) {
                        throw new IOException("Expected IHDR Marker");
                    }
                    this.scaledHeight = this.cio_read(4);
                    this.setTop(this.scaledHeight);
                    this.scaledWidth = this.cio_read(4);
                    this.setRight(this.scaledWidth);
                    this.bpc = -1;
                } else if (this.boxLength == -11534511) {
                    Utilities.skip(this.inp, 4);
                    int n = this.cio_read(4);
                    int n2 = this.cio_read(4);
                    int n3 = this.cio_read(4);
                    int n4 = this.cio_read(4);
                    Utilities.skip(this.inp, 16);
                    this.colorspace = this.cio_read(2);
                    this.bpc = 8;
                    this.scaledHeight = n2 - n4;
                    this.setTop(this.scaledHeight);
                    this.scaledWidth = n - n3;
                    this.setRight(this.scaledWidth);
                } else {
                    throw new IOException("Not a valid Jpeg2000 file");
                }
                java.lang.Object var7_6 = null;
                if (this.inp == null) break block18;
            }
            catch (Throwable var6_10) {
                java.lang.Object var7_7 = null;
                if (this.inp != null) {
                    try {
                        this.inp.close();
                    }
                    catch (Exception var8_9) {
                        // empty catch block
                    }
                    this.inp = null;
                }
                throw var6_10;
            }
            try {
                this.inp.close();
            }
            catch (Exception var8_8) {
                // empty catch block
            }
            this.inp = null;
            {
            }
        }
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}

