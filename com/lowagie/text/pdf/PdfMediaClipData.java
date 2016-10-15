/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;

public class PdfMediaClipData
extends PdfDictionary {
    PdfMediaClipData(String string, PdfFileSpecification pdfFileSpecification, String string2) throws IOException {
        this.put(PdfName.TYPE, new PdfName("MediaClip"));
        this.put(PdfName.S, new PdfName("MCD"));
        this.put(PdfName.N, new PdfString("Media clip for " + string));
        this.put(new PdfName("CT"), new PdfString(string2));
        PdfDictionary pdfDictionary = new PdfDictionary();
        pdfDictionary.put(new PdfName("TF"), new PdfString("TEMPACCESS"));
        this.put(new PdfName("P"), pdfDictionary);
        this.put(PdfName.D, pdfFileSpecification.getReference());
    }
}

