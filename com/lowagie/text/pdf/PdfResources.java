/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;

class PdfResources
extends PdfDictionary {
    PdfResources() {
    }

    void add(PdfName pdfName, PdfDictionary pdfDictionary) {
        if (pdfDictionary.size() == 0) {
            return;
        }
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(this.get(pdfName));
        if (pdfDictionary2 == null) {
            this.put(pdfName, pdfDictionary);
        } else {
            pdfDictionary2.putAll(pdfDictionary);
        }
    }
}

