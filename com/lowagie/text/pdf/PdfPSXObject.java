/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class PdfPSXObject
extends PdfTemplate {
    protected PdfPSXObject() {
    }

    public PdfPSXObject(PdfWriter pdfWriter) {
        super(pdfWriter);
    }

    PdfStream getFormXObject(int n) throws IOException {
        PdfStream pdfStream = new PdfStream(this.content.toByteArray());
        pdfStream.put(PdfName.TYPE, PdfName.XOBJECT);
        pdfStream.put(PdfName.SUBTYPE, PdfName.PS);
        pdfStream.flateCompress(n);
        return pdfStream;
    }

    public PdfContentByte getDuplicate() {
        PdfPSXObject pdfPSXObject = new PdfPSXObject();
        pdfPSXObject.writer = this.writer;
        pdfPSXObject.pdf = this.pdf;
        pdfPSXObject.thisReference = this.thisReference;
        pdfPSXObject.pageResources = this.pageResources;
        pdfPSXObject.separator = this.separator;
        return pdfPSXObject;
    }
}

