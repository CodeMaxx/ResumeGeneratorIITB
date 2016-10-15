/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfStamperImp;
import com.lowagie.text.pdf.PdfWriter;

public class StampContent
extends PdfContentByte {
    PdfStamperImp.PageStamp ps;
    PageResources pageResources;

    StampContent(PdfStamperImp pdfStamperImp, PdfStamperImp.PageStamp pageStamp) {
        super(pdfStamperImp);
        this.ps = pageStamp;
        this.pageResources = pageStamp.pageResources;
    }

    public void setAction(PdfAction pdfAction, float f, float f2, float f3, float f4) {
        ((PdfStamperImp)this.writer).addAnnotation(new PdfAnnotation(this.writer, f, f2, f3, f4, pdfAction), this.ps.pageN);
    }

    public PdfContentByte getDuplicate() {
        return new StampContent((PdfStamperImp)this.writer, this.ps);
    }

    PageResources getPageResources() {
        return this.pageResources;
    }

    void addAnnotation(PdfAnnotation pdfAnnotation) {
        ((PdfStamperImp)this.writer).addAnnotation(pdfAnnotation, this.ps.pageN);
    }
}

