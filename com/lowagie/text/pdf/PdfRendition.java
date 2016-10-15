/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfMediaClipData;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;

public class PdfRendition
extends PdfDictionary {
    PdfRendition(String string, PdfFileSpecification pdfFileSpecification, String string2) throws IOException {
        this.put(PdfName.S, new PdfName("MR"));
        this.put(PdfName.N, new PdfString("Rendition for " + string));
        this.put(PdfName.C, new PdfMediaClipData(string, pdfFileSpecification, string2));
    }
}

