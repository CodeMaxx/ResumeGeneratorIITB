/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

public class PdfImage
extends PdfStream {
    static final int TRANSFERSIZE = 4096;
    protected PdfName name = null;

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public PdfImage(Image var1_1, String var2_2, PdfIndirectReference var3_3) throws BadPdfFormatException {
        super();
        this.name = new PdfName(var2_2);
        this.put(PdfName.TYPE, PdfName.XOBJECT);
        this.put(PdfName.SUBTYPE, PdfName.IMAGE);
        this.put(PdfName.WIDTH, new PdfNumber(var1_1.getWidth()));
        this.put(PdfName.HEIGHT, new PdfNumber(var1_1.getHeight()));
        if (var1_1.getLayer() != null) {
            this.put(PdfName.OC, var1_1.getLayer().getRef());
        }
        if (var1_1.isMask() && (var1_1.getBpc() == 1 || var1_1.getBpc() > 255)) {
            this.put(PdfName.IMAGEMASK, PdfBoolean.PDFTRUE);
        }
        if (var3_3 != null) {
            if (var1_1.isSmask()) {
                this.put(PdfName.SMASK, var3_3);
            } else {
                this.put(PdfName.MASK, var3_3);
            }
        }
        if (var1_1.isMask() && var1_1.isInverted()) {
            this.put(PdfName.DECODE, new PdfLiteral("[1 0]"));
        }
        if (var1_1.isInterpolation()) {
            this.put(PdfName.INTERPOLATE, PdfBoolean.PDFTRUE);
        }
        var4_4 = null;
        try {
            try {
                if (var1_1.isImgRaw()) {
                    var5_5 = var1_1.getColorspace();
                    var6_8 = var1_1.getTransparency();
                    if (var6_8 != null && !var1_1.isMask() && var3_3 == null) {
                        var7_9 = "[";
                        for (var8_11 = 0; var8_11 < var6_8.length; ++var8_11) {
                            var7_9 = var7_9 + var6_8[var8_11] + " ";
                        }
                        var7_9 = var7_9 + "]";
                        this.put(PdfName.MASK, new PdfLiteral(var7_9));
                    }
                    this.bytes = var1_1.getRawData();
                    this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                    var7_10 = var1_1.getBpc();
                    if (var7_10 > 255) {
                        if (!var1_1.isMask()) {
                            this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                        }
                        this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(1));
                        this.put(PdfName.FILTER, PdfName.CCITTFAXDECODE);
                        var8_11 = var7_10 - 257;
                        var9_13 = new PdfDictionary();
                        if (var8_11 != 0) {
                            var9_13.put(PdfName.K, new PdfNumber(var8_11));
                        }
                        if ((var5_5 & 1) != 0) {
                            var9_13.put(PdfName.BLACKIS1, PdfBoolean.PDFTRUE);
                        }
                        if ((var5_5 & 2) != 0) {
                            var9_13.put(PdfName.ENCODEDBYTEALIGN, PdfBoolean.PDFTRUE);
                        }
                        if ((var5_5 & 4) != 0) {
                            var9_13.put(PdfName.ENDOFLINE, PdfBoolean.PDFTRUE);
                        }
                        if ((var5_5 & 8) != 0) {
                            var9_13.put(PdfName.ENDOFBLOCK, PdfBoolean.PDFFALSE);
                        }
                        var9_13.put(PdfName.COLUMNS, new PdfNumber(var1_1.getWidth()));
                        var9_13.put(PdfName.ROWS, new PdfNumber(var1_1.getHeight()));
                        this.put(PdfName.DECODEPARMS, var9_13);
                    } else {
                        switch (var5_5) {
                            case 1: {
                                this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                                if (!var1_1.isInverted()) break;
                                this.put(PdfName.DECODE, new PdfLiteral("[1 0]"));
                                break;
                            }
                            case 3: {
                                this.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                                if (!var1_1.isInverted()) break;
                                this.put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0]"));
                                break;
                            }
                            default: {
                                this.put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
                                if (!var1_1.isInverted()) break;
                                this.put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0 1 0]"));
                            }
                        }
                        if ((var8_12 = var1_1.getAdditional()) != null) {
                            this.putAll(var8_12);
                        }
                        if (var1_1.isMask() && (var1_1.getBpc() == 1 || var1_1.getBpc() > 8)) {
                            this.remove(PdfName.COLORSPACE);
                        }
                        this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(var1_1.getBpc()));
                        if (var1_1.isDeflated()) {
                            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
                        } else {
                            this.flateCompress(var1_1.getCompressionLevel());
                        }
                    }
                    var11_14 = null;
                    if (var4_4 == null) return;
                    try {
                        var4_4.close();
                        return;
                    }
                    catch (Exception var12_19) {
                        // empty catch block
                    }
                    return;
                }
                if (var1_1.getRawData() == null) {
                    var4_4 = var1_1.getUrl().openStream();
                    var5_6 = var1_1.getUrl().toString();
                } else {
                    var4_4 = new ByteArrayInputStream(var1_1.getRawData());
                    var5_6 = "Byte array";
                }
                switch (var1_1.type()) {
                    case 32: {
                        this.put(PdfName.FILTER, PdfName.DCTDECODE);
                        switch (var1_1.getColorspace()) {
                            case 1: {
                                this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                                ** break;
                            }
                            case 3: {
                                this.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                                ** break;
                            }
                        }
                        this.put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
                        if (var1_1.isInverted()) {
                            this.put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0 1 0]"));
                        }
lbl110: // 5 sources:
                        this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                        if (var1_1.getRawData() != null) {
                            this.bytes = var1_1.getRawData();
                            this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                            var11_15 = null;
                            if (var4_4 == null) return;
                            ** try [egrp 2[TRYBLOCK] [9 : 1321->1329)] { 
lbl118: // 1 sources:
                            var4_4.close();
                            return;
lbl120: // 1 sources:
                            catch (Exception var12_20) {
                                // empty catch block
                            }
                            return;
                        }
                        this.streamBytes = new ByteArrayOutputStream();
                        PdfImage.transferBytes(var4_4, this.streamBytes, -1);
                        ** break;
                    }
                    case 33: {
                        this.put(PdfName.FILTER, PdfName.JPXDECODE);
                        if (var1_1.getColorspace() > 0) {
                            switch (var1_1.getColorspace()) {
                                case 1: {
                                    this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                                    ** break;
                                }
                                case 3: {
                                    this.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                                    ** break;
                                }
                            }
                            this.put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
lbl137: // 3 sources:
                            this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(var1_1.getBpc()));
                        }
                        if (var1_1.getRawData() != null) {
                            this.bytes = var1_1.getRawData();
                            this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                            var11_16 = null;
                            if (var4_4 == null) return;
                            ** try [egrp 2[TRYBLOCK] [9 : 1321->1329)] { 
lbl145: // 1 sources:
                            var4_4.close();
                            return;
lbl147: // 1 sources:
                            catch (Exception var12_21) {
                                // empty catch block
                            }
                            return;
                        }
                        this.streamBytes = new ByteArrayOutputStream();
                        PdfImage.transferBytes(var4_4, this.streamBytes, -1);
                        ** break;
                    }
                }
                throw new BadPdfFormatException(var5_6 + " is an unknown Image format.");
lbl154: // 2 sources:
                this.put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
            }
            catch (IOException var5_7) {
                throw new BadPdfFormatException(var5_7.getMessage());
            }
            var11_17 = null;
            if (var4_4 == null) return;
            ** try [egrp 2[TRYBLOCK] [9 : 1321->1329)] { 
lbl161: // 1 sources:
            catch (Exception var12_22) {
                return;
            }
lbl163: // 1 sources:
            var4_4.close();
            return;
        }
        catch (Throwable var10_24) {
            var11_18 = null;
            if (var4_4 == null) throw var10_24;
            ** try [egrp 2[TRYBLOCK] [9 : 1321->1329)] { 
lbl170: // 1 sources:
            var4_4.close();
            throw var10_24;
lbl172: // 1 sources:
            catch (Exception var12_23) {
                // empty catch block
            }
            throw var10_24;
        }
    }

    public PdfName name() {
        return this.name;
    }

    static void transferBytes(InputStream inputStream, OutputStream outputStream, int n) throws IOException {
        byte[] arrby = new byte[4096];
        if (n < 0) {
            n = 134217727;
        }
        while (n != 0) {
            int n2 = inputStream.read(arrby, 0, Math.min(n, 4096));
            if (n2 < 0) {
                return;
            }
            outputStream.write(arrby, 0, n2);
            n -= n2;
        }
    }

    protected void importAll(PdfImage pdfImage) {
        this.name = pdfImage.name;
        this.compressed = pdfImage.compressed;
        this.compressionLevel = pdfImage.compressionLevel;
        this.streamBytes = pdfImage.streamBytes;
        this.bytes = pdfImage.bytes;
        this.hashMap = pdfImage.hashMap;
    }
}

