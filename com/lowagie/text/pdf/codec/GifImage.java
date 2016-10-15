/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class GifImage {
    protected DataInputStream in;
    protected int width;
    protected int height;
    protected boolean gctFlag;
    protected int bgIndex;
    protected int bgColor;
    protected int pixelAspect;
    protected boolean lctFlag;
    protected boolean interlace;
    protected int lctSize;
    protected int ix;
    protected int iy;
    protected int iw;
    protected int ih;
    protected byte[] block = new byte[256];
    protected int blockSize = 0;
    protected int dispose = 0;
    protected boolean transparency = false;
    protected int delay = 0;
    protected int transIndex;
    protected static final int MaxStackSize = 4096;
    protected short[] prefix;
    protected byte[] suffix;
    protected byte[] pixelStack;
    protected byte[] pixels;
    protected byte[] m_out;
    protected int m_bpc;
    protected int m_gbpc;
    protected byte[] m_global_table;
    protected byte[] m_local_table;
    protected byte[] m_curr_table;
    protected int m_line_stride;
    protected byte[] fromData;
    protected URL fromUrl;
    protected ArrayList frames = new ArrayList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GifImage(URL uRL) throws IOException {
        this.fromUrl = uRL;
        InputStream inputStream = null;
        try {
            inputStream = uRL.openStream();
            this.process(inputStream);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public GifImage(String string) throws IOException {
        this(Utilities.toURL(string));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GifImage(byte[] arrby) throws IOException {
        this.fromData = arrby;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(arrby);
            this.process(byteArrayInputStream);
        }
        finally {
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        }
    }

    public GifImage(InputStream inputStream) throws IOException {
        this.process(inputStream);
    }

    public int getFrameCount() {
        return this.frames.size();
    }

    public Image getImage(int n) {
        GifFrame gifFrame = (GifFrame)this.frames.get(n - 1);
        return gifFrame.image;
    }

    public int[] getFramePosition(int n) {
        GifFrame gifFrame = (GifFrame)this.frames.get(n - 1);
        return new int[]{gifFrame.ix, gifFrame.iy};
    }

    public int[] getLogicalScreen() {
        return new int[]{this.width, this.height};
    }

    void process(InputStream inputStream) throws IOException {
        this.in = new DataInputStream(new BufferedInputStream(inputStream));
        this.readHeader();
        this.readContents();
        if (this.frames.isEmpty()) {
            throw new IOException("The file does not contain any valid image.");
        }
    }

    protected void readHeader() throws IOException {
        String string = "";
        for (int i = 0; i < 6; ++i) {
            string = string + (char)this.in.read();
        }
        if (!string.startsWith("GIF8")) {
            throw new IOException("Gif signature nor found.");
        }
        this.readLSD();
        if (this.gctFlag) {
            this.m_global_table = this.readColorTable(this.m_gbpc);
        }
    }

    protected void readLSD() throws IOException {
        this.width = this.readShort();
        this.height = this.readShort();
        int n = this.in.read();
        this.gctFlag = (n & 128) != 0;
        this.m_gbpc = (n & 7) + 1;
        this.bgIndex = this.in.read();
        this.pixelAspect = this.in.read();
    }

    protected int readShort() throws IOException {
        return this.in.read() | this.in.read() << 8;
    }

    protected int readBlock() throws IOException {
        this.blockSize = this.in.read();
        if (this.blockSize <= 0) {
            this.blockSize = 0;
            return 0;
        }
        for (int i = 0; i < this.blockSize; ++i) {
            int n = this.in.read();
            if (n < 0) {
                this.blockSize = i;
                return this.blockSize;
            }
            this.block[i] = (byte)n;
        }
        return this.blockSize;
    }

    protected byte[] readColorTable(int n) throws IOException {
        int n2 = 1 << n;
        int n3 = 3 * n2;
        n = GifImage.newBpc(n);
        byte[] arrby = new byte[(1 << n) * 3];
        this.in.readFully(arrby, 0, n3);
        return arrby;
    }

    protected static int newBpc(int n) {
        switch (n) {
            case 1: 
            case 2: 
            case 4: {
                break;
            }
            case 3: {
                return 4;
            }
            default: {
                return 8;
            }
        }
        return n;
    }

    protected void readContents() throws IOException {
        boolean bl = false;
        block8 : while (!bl) {
            int n = this.in.read();
            switch (n) {
                case 44: {
                    this.readImage();
                    continue block8;
                }
                case 33: {
                    n = this.in.read();
                    switch (n) {
                        case 249: {
                            this.readGraphicControlExt();
                            continue block8;
                        }
                        case 255: {
                            this.readBlock();
                            this.skip();
                            continue block8;
                        }
                    }
                    this.skip();
                    continue block8;
                }
            }
            bl = true;
        }
    }

    protected void readImage() throws IOException {
        Object object;
        boolean bl;
        this.ix = this.readShort();
        this.iy = this.readShort();
        this.iw = this.readShort();
        this.ih = this.readShort();
        int n = this.in.read();
        this.lctFlag = (n & 128) != 0;
        this.interlace = (n & 64) != 0;
        this.lctSize = 2 << (n & 7);
        this.m_bpc = GifImage.newBpc(this.m_gbpc);
        if (this.lctFlag) {
            this.m_curr_table = this.readColorTable((n & 7) + 1);
            this.m_bpc = GifImage.newBpc((n & 7) + 1);
        } else {
            this.m_curr_table = this.m_global_table;
        }
        if (this.transparency && this.transIndex >= this.m_curr_table.length / 3) {
            this.transparency = false;
        }
        if (this.transparency && this.m_bpc == 1) {
            byte[] arrby = new byte[12];
            System.arraycopy(this.m_curr_table, 0, arrby, 0, 6);
            this.m_curr_table = arrby;
            this.m_bpc = 2;
        }
        if (!(bl = this.decodeImageData())) {
            this.skip();
        }
        ImgRaw imgRaw = null;
        try {
            imgRaw = new ImgRaw(this.iw, this.ih, 1, this.m_bpc, this.m_out);
            object = new PdfArray();
            object.add(PdfName.INDEXED);
            object.add(PdfName.DEVICERGB);
            int n2 = this.m_curr_table.length;
            object.add(new PdfNumber(n2 / 3 - 1));
            object.add(new PdfString(this.m_curr_table));
            PdfDictionary pdfDictionary = new PdfDictionary();
            pdfDictionary.put(PdfName.COLORSPACE, (PdfObject)object);
            imgRaw.setAdditional(pdfDictionary);
            if (this.transparency) {
                imgRaw.setTransparency(new int[]{this.transIndex, this.transIndex});
            }
        }
        catch (Exception var4_6) {
            throw new ExceptionConverter(var4_6);
        }
        imgRaw.setOriginalType(3);
        imgRaw.setOriginalData(this.fromData);
        imgRaw.setUrl(this.fromUrl);
        object = new GifFrame();
        object.image = imgRaw;
        object.ix = this.ix;
        object.iy = this.iy;
        this.frames.add(object);
    }

    protected boolean decodeImageData() throws IOException {
        int n;
        int n2 = -1;
        int n3 = this.iw * this.ih;
        boolean bl = false;
        if (this.prefix == null) {
            this.prefix = new short[4096];
        }
        if (this.suffix == null) {
            this.suffix = new byte[4096];
        }
        if (this.pixelStack == null) {
            this.pixelStack = new byte[4097];
        }
        this.m_line_stride = (this.iw * this.m_bpc + 7) / 8;
        this.m_out = new byte[this.m_line_stride * this.ih];
        int n4 = 1;
        int n5 = this.interlace ? 8 : 1;
        int n6 = 0;
        int n7 = 0;
        int n8 = this.in.read();
        int n9 = 1 << n8;
        int n10 = n9 + 1;
        int n11 = n9 + 2;
        int n12 = n2;
        int n13 = n8 + 1;
        int n14 = (1 << n13) - 1;
        for (n = 0; n < n9; ++n) {
            this.prefix[n] = 0;
            this.suffix[n] = (byte)n;
        }
        int n15 = 0;
        int n16 = 0;
        int n17 = 0;
        int n18 = 0;
        int n19 = 0;
        int n20 = 0;
        int n21 = 0;
        while (n21 < n3) {
            if (n16 == 0) {
                if (n19 < n13) {
                    if (n18 == 0) {
                        n18 = this.readBlock();
                        if (n18 <= 0) {
                            bl = true;
                            break;
                        }
                        n15 = 0;
                    }
                    n20 += (this.block[n15] & 255) << n19;
                    n19 += 8;
                    ++n15;
                    --n18;
                    continue;
                }
                n = n20 & n14;
                n20 >>= n13;
                n19 -= n13;
                if (n > n11 || n == n10) break;
                if (n == n9) {
                    n13 = n8 + 1;
                    n14 = (1 << n13) - 1;
                    n11 = n9 + 2;
                    n12 = n2;
                    continue;
                }
                if (n12 == n2) {
                    this.pixelStack[n16++] = this.suffix[n];
                    n12 = n;
                    n17 = n;
                    continue;
                }
                int n22 = n;
                if (n == n11) {
                    this.pixelStack[n16++] = (byte)n17;
                    n = n12;
                }
                while (n > n9) {
                    this.pixelStack[n16++] = this.suffix[n];
                    n = this.prefix[n];
                }
                n17 = this.suffix[n] & 255;
                if (n11 >= 4096) break;
                this.pixelStack[n16++] = (byte)n17;
                this.prefix[n11] = (short)n12;
                this.suffix[n11] = (byte)n17;
                if ((++n11 & n14) == 0 && n11 < 4096) {
                    ++n13;
                    n14 += n11;
                }
                n12 = n22;
            }
            ++n21;
            this.setPixel(n7, n6, this.pixelStack[--n16]);
            if (++n7 < this.iw) continue;
            n7 = 0;
            if ((n6 += n5) < this.ih) continue;
            if (this.interlace) {
                do {
                    switch (++n4) {
                        case 2: {
                            n6 = 4;
                            break;
                        }
                        case 3: {
                            n6 = 2;
                            n5 = 4;
                            break;
                        }
                        case 4: {
                            n6 = 1;
                            n5 = 2;
                            break;
                        }
                        default: {
                            n6 = this.ih - 1;
                            n5 = 0;
                        }
                    }
                } while (n6 >= this.ih);
                continue;
            }
            n6 = this.ih - 1;
            n5 = 0;
        }
        return bl;
    }

    protected void setPixel(int n, int n2, int n3) {
        if (this.m_bpc == 8) {
            int n4 = n + this.iw * n2;
            this.m_out[n4] = (byte)n3;
        } else {
            int n5 = this.m_line_stride * n2 + n / (8 / this.m_bpc);
            int n6 = n3 << 8 - this.m_bpc * (n % (8 / this.m_bpc)) - this.m_bpc;
            byte[] arrby = this.m_out;
            int n7 = n5;
            arrby[n7] = (byte)(arrby[n7] | n6);
        }
    }

    protected void resetFrame() {
    }

    protected void readGraphicControlExt() throws IOException {
        this.in.read();
        int n = this.in.read();
        this.dispose = (n & 28) >> 2;
        if (this.dispose == 0) {
            this.dispose = 1;
        }
        this.transparency = (n & 1) != 0;
        this.delay = this.readShort() * 10;
        this.transIndex = this.in.read();
        this.in.read();
    }

    protected void skip() throws IOException {
        do {
            this.readBlock();
        } while (this.blockSize > 0);
    }

    static class GifFrame {
        Image image;
        int ix;
        int iy;

        GifFrame() {
        }
    }

}

