/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEFStream;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.collection.PdfCollectionItem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PdfFileSpecification
extends PdfDictionary {
    protected PdfWriter writer;
    protected PdfIndirectReference ref;

    public PdfFileSpecification() {
        super(PdfName.FILESPEC);
    }

    public static PdfFileSpecification url(PdfWriter pdfWriter, String string) {
        PdfFileSpecification pdfFileSpecification = new PdfFileSpecification();
        pdfFileSpecification.writer = pdfWriter;
        pdfFileSpecification.put(PdfName.FS, PdfName.URL);
        pdfFileSpecification.put(PdfName.F, new PdfString(string));
        return pdfFileSpecification;
    }

    public static PdfFileSpecification fileEmbedded(PdfWriter pdfWriter, String string, String string2, byte[] arrby) throws IOException {
        return PdfFileSpecification.fileEmbedded(pdfWriter, string, string2, arrby, 9);
    }

    public static PdfFileSpecification fileEmbedded(PdfWriter pdfWriter, String string, String string2, byte[] arrby, int n) throws IOException {
        return PdfFileSpecification.fileEmbedded(pdfWriter, string, string2, arrby, null, null, n);
    }

    public static PdfFileSpecification fileEmbedded(PdfWriter pdfWriter, String string, String string2, byte[] arrby, boolean bl) throws IOException {
        return PdfFileSpecification.fileEmbedded(pdfWriter, string, string2, arrby, null, null, bl ? 9 : 0);
    }

    public static PdfFileSpecification fileEmbedded(PdfWriter pdfWriter, String string, String string2, byte[] arrby, boolean bl, String string3, PdfDictionary pdfDictionary) throws IOException {
        return PdfFileSpecification.fileEmbedded(pdfWriter, string, string2, arrby, null, null, bl ? 9 : 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static PdfFileSpecification fileEmbedded(PdfWriter var0, String var1_1, String var2_2, byte[] var3_3, String var4_4, PdfDictionary var5_5, int var6_6) throws IOException {
        var7_7 = new PdfFileSpecification();
        var7_7.writer = var0;
        var7_7.put(PdfName.F, new PdfString(var2_2));
        var7_7.setUnicodeFileName(var2_2, false);
        var9_8 = null;
        try {
            var11_9 = var0.getPdfIndirectReference();
            if (var3_3 == null) {
                var12_10 = new File(var1_1);
                if (var12_10.canRead()) {
                    var9_8 = new FileInputStream(var1_1);
                } else if (var1_1.startsWith("file:/") || var1_1.startsWith("http://") || var1_1.startsWith("https://") || var1_1.startsWith("jar:")) {
                    var9_8 = new URL(var1_1).openStream();
                } else {
                    var9_8 = BaseFont.getResourceStream(var1_1);
                    if (var9_8 == null) {
                        throw new IOException(var1_1 + " not found as file or resource.");
                    }
                }
                var8_11 = new PdfEFStream(var9_8, var0);
            } else {
                var8_11 = new PdfEFStream(var3_3);
            }
            var8_11.put(PdfName.TYPE, PdfName.EMBEDDEDFILE);
            var8_11.flateCompress(var6_6);
            var8_11.put(PdfName.PARAMS, var11_9);
            if (var4_4 != null) {
                var8_11.put(PdfName.SUBTYPE, new PdfName(var4_4));
            }
            var10_12 = var0.addToBody(var8_11).getIndirectReference();
            if (var3_3 == null) {
                var8_11.writeLength();
            }
            var12_10 = new PdfDictionary();
            if (var5_5 != null) {
                var12_10.merge(var5_5);
            }
            var12_10.put(PdfName.SIZE, new PdfNumber(var8_11.getRawLength()));
            var0.addToBody((PdfObject)var12_10, var11_9);
            var14_13 = null;
            if (var9_8 != null) {
                try {
                    var9_8.close();
                }
                catch (Exception var15_15) {}
            }
        }
        catch (Throwable var13_17) {
            var14_14 = null;
            if (var9_8 == null) throw var13_17;
            ** try [egrp 1[TRYBLOCK] [2 : 338->346)] { 
lbl48: // 1 sources:
            var9_8.close();
            throw var13_17;
lbl50: // 1 sources:
            catch (Exception var15_16) {
                // empty catch block
            }
            throw var13_17;
        }
        var12_10 = new PdfDictionary();
        var12_10.put(PdfName.F, var10_12);
        var12_10.put(PdfName.UF, var10_12);
        var7_7.put(PdfName.EF, (PdfObject)var12_10);
        return var7_7;
    }

    public static PdfFileSpecification fileExtern(PdfWriter pdfWriter, String string) {
        PdfFileSpecification pdfFileSpecification = new PdfFileSpecification();
        pdfFileSpecification.writer = pdfWriter;
        pdfFileSpecification.put(PdfName.F, new PdfString(string));
        pdfFileSpecification.setUnicodeFileName(string, false);
        return pdfFileSpecification;
    }

    public PdfIndirectReference getReference() throws IOException {
        if (this.ref != null) {
            return this.ref;
        }
        this.ref = this.writer.addToBody(this).getIndirectReference();
        return this.ref;
    }

    public void setMultiByteFileName(byte[] arrby) {
        this.put(PdfName.F, new PdfString(arrby).setHexWriting(true));
    }

    public void setUnicodeFileName(String string, boolean bl) {
        this.put(PdfName.UF, new PdfString(string, bl ? "UnicodeBig" : "PDF"));
    }

    public void setVolatile(boolean bl) {
        this.put(PdfName.V, new PdfBoolean(bl));
    }

    public void addDescription(String string, boolean bl) {
        this.put(PdfName.DESC, new PdfString(string, bl ? "UnicodeBig" : "PDF"));
    }

    public void addCollectionItem(PdfCollectionItem pdfCollectionItem) {
        this.put(PdfName.CI, pdfCollectionItem);
    }
}

