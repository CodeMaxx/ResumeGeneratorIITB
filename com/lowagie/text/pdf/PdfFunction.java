/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class PdfFunction {
    protected PdfWriter writer;
    protected PdfIndirectReference reference;
    protected PdfDictionary dictionary;

    protected PdfFunction(PdfWriter pdfWriter) {
        this.writer = pdfWriter;
    }

    PdfIndirectReference getReference() {
        try {
            if (this.reference == null) {
                this.reference = this.writer.addToBody(this.dictionary).getIndirectReference();
            }
        }
        catch (IOException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
        return this.reference;
    }

    public static PdfFunction type0(PdfWriter pdfWriter, float[] arrf, float[] arrf2, int[] arrn, int n, int n2, float[] arrf3, float[] arrf4, byte[] arrby) {
        PdfFunction pdfFunction = new PdfFunction(pdfWriter);
        pdfFunction.dictionary = new PdfStream(arrby);
        ((PdfStream)pdfFunction.dictionary).flateCompress(pdfWriter.getCompressionLevel());
        pdfFunction.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(0));
        pdfFunction.dictionary.put(PdfName.DOMAIN, new PdfArray(arrf));
        pdfFunction.dictionary.put(PdfName.RANGE, new PdfArray(arrf2));
        pdfFunction.dictionary.put(PdfName.SIZE, new PdfArray(arrn));
        pdfFunction.dictionary.put(PdfName.BITSPERSAMPLE, new PdfNumber(n));
        if (n2 != 1) {
            pdfFunction.dictionary.put(PdfName.ORDER, new PdfNumber(n2));
        }
        if (arrf3 != null) {
            pdfFunction.dictionary.put(PdfName.ENCODE, new PdfArray(arrf3));
        }
        if (arrf4 != null) {
            pdfFunction.dictionary.put(PdfName.DECODE, new PdfArray(arrf4));
        }
        return pdfFunction;
    }

    public static PdfFunction type2(PdfWriter pdfWriter, float[] arrf, float[] arrf2, float[] arrf3, float[] arrf4, float f) {
        PdfFunction pdfFunction = new PdfFunction(pdfWriter);
        pdfFunction.dictionary = new PdfDictionary();
        pdfFunction.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(2));
        pdfFunction.dictionary.put(PdfName.DOMAIN, new PdfArray(arrf));
        if (arrf2 != null) {
            pdfFunction.dictionary.put(PdfName.RANGE, new PdfArray(arrf2));
        }
        if (arrf3 != null) {
            pdfFunction.dictionary.put(PdfName.C0, new PdfArray(arrf3));
        }
        if (arrf4 != null) {
            pdfFunction.dictionary.put(PdfName.C1, new PdfArray(arrf4));
        }
        pdfFunction.dictionary.put(PdfName.N, new PdfNumber(f));
        return pdfFunction;
    }

    public static PdfFunction type3(PdfWriter pdfWriter, float[] arrf, float[] arrf2, PdfFunction[] arrpdfFunction, float[] arrf3, float[] arrf4) {
        PdfFunction pdfFunction = new PdfFunction(pdfWriter);
        pdfFunction.dictionary = new PdfDictionary();
        pdfFunction.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(3));
        pdfFunction.dictionary.put(PdfName.DOMAIN, new PdfArray(arrf));
        if (arrf2 != null) {
            pdfFunction.dictionary.put(PdfName.RANGE, new PdfArray(arrf2));
        }
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrpdfFunction.length; ++i) {
            pdfArray.add(arrpdfFunction[i].getReference());
        }
        pdfFunction.dictionary.put(PdfName.FUNCTIONS, pdfArray);
        pdfFunction.dictionary.put(PdfName.BOUNDS, new PdfArray(arrf3));
        pdfFunction.dictionary.put(PdfName.ENCODE, new PdfArray(arrf4));
        return pdfFunction;
    }

    public static PdfFunction type4(PdfWriter pdfWriter, float[] arrf, float[] arrf2, String string) {
        byte[] arrby = new byte[string.length()];
        for (int i = 0; i < arrby.length; ++i) {
            arrby[i] = (byte)string.charAt(i);
        }
        PdfFunction pdfFunction = new PdfFunction(pdfWriter);
        pdfFunction.dictionary = new PdfStream(arrby);
        ((PdfStream)pdfFunction.dictionary).flateCompress(pdfWriter.getCompressionLevel());
        pdfFunction.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(4));
        pdfFunction.dictionary.put(PdfName.DOMAIN, new PdfArray(arrf));
        pdfFunction.dictionary.put(PdfName.RANGE, new PdfArray(arrf2));
        return pdfFunction;
    }
}

