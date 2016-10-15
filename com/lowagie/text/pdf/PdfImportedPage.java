/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class PdfImportedPage
extends PdfTemplate {
    PdfReaderInstance readerInstance;
    int pageNumber;

    PdfImportedPage(PdfReaderInstance pdfReaderInstance, PdfWriter pdfWriter, int n) {
        this.readerInstance = pdfReaderInstance;
        this.pageNumber = n;
        this.thisReference = pdfWriter.getPdfIndirectReference();
        this.bBox = pdfReaderInstance.getReader().getPageSize(n);
        this.setMatrix(1.0f, 0.0f, 0.0f, 1.0f, - this.bBox.getLeft(), - this.bBox.getBottom());
        this.type = 2;
    }

    public PdfImportedPage getFromReader() {
        return this;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void addImage(Image image, float f, float f2, float f3, float f4, float f5, float f6) throws DocumentException {
        this.throwError();
    }

    public void addTemplate(PdfTemplate pdfTemplate, float f, float f2, float f3, float f4, float f5, float f6) {
        this.throwError();
    }

    public PdfContentByte getDuplicate() {
        this.throwError();
        return null;
    }

    PdfStream getFormXObject(int n) throws IOException {
        return this.readerInstance.getFormXObject(this.pageNumber, n);
    }

    public void setColorFill(PdfSpotColor pdfSpotColor, float f) {
        this.throwError();
    }

    public void setColorStroke(PdfSpotColor pdfSpotColor, float f) {
        this.throwError();
    }

    PdfObject getResources() {
        return this.readerInstance.getResources(this.pageNumber);
    }

    public void setFontAndSize(BaseFont baseFont, float f) {
        this.throwError();
    }

    void throwError() {
        throw new RuntimeException("Content can not be added to a PdfImportedPage.");
    }

    PdfReaderInstance getPdfReaderInstance() {
        return this.readerInstance;
    }
}

