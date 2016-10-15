/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStructureTreeRoot;
import com.lowagie.text.pdf.PdfWriter;

public class PdfStructureElement
extends PdfDictionary {
    private PdfStructureElement parent;
    private PdfStructureTreeRoot top;
    private PdfIndirectReference reference;

    public PdfStructureElement(PdfStructureElement pdfStructureElement, PdfName pdfName) {
        this.top = pdfStructureElement.top;
        this.init(pdfStructureElement, pdfName);
        this.parent = pdfStructureElement;
        this.put(PdfName.P, pdfStructureElement.reference);
    }

    public PdfStructureElement(PdfStructureTreeRoot pdfStructureTreeRoot, PdfName pdfName) {
        this.top = pdfStructureTreeRoot;
        this.init(pdfStructureTreeRoot, pdfName);
        this.put(PdfName.P, pdfStructureTreeRoot.getReference());
    }

    private void init(PdfDictionary pdfDictionary, PdfName pdfName) {
        PdfObject pdfObject = pdfDictionary.get(PdfName.K);
        PdfArray pdfArray = null;
        if (pdfObject != null && !pdfObject.isArray()) {
            throw new IllegalArgumentException("The parent has already another function.");
        }
        if (pdfObject == null) {
            pdfArray = new PdfArray();
            pdfDictionary.put(PdfName.K, pdfArray);
        } else {
            pdfArray = (PdfArray)pdfObject;
        }
        pdfArray.add(this);
        this.put(PdfName.S, pdfName);
        this.reference = this.top.getWriter().getPdfIndirectReference();
    }

    public PdfDictionary getParent() {
        return this.parent;
    }

    void setPageMark(int n, int n2) {
        if (n2 >= 0) {
            this.put(PdfName.K, new PdfNumber(n2));
        }
        this.top.setPageMark(n, this.reference);
    }

    public PdfIndirectReference getReference() {
        return this.reference;
    }
}

