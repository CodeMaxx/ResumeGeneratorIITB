/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTransparencyGroup;
import com.lowagie.text.pdf.PdfWriter;

public class PdfFormXObject
extends PdfStream {
    public static final PdfNumber ZERO = new PdfNumber(0);
    public static final PdfNumber ONE = new PdfNumber(1);
    public static final PdfLiteral MATRIX = new PdfLiteral("[1 0 0 1 0 0]");

    PdfFormXObject(PdfTemplate pdfTemplate, int n) {
        PdfArray pdfArray;
        this.put(PdfName.TYPE, PdfName.XOBJECT);
        this.put(PdfName.SUBTYPE, PdfName.FORM);
        this.put(PdfName.RESOURCES, pdfTemplate.getResources());
        this.put(PdfName.BBOX, new PdfRectangle(pdfTemplate.getBoundingBox()));
        this.put(PdfName.FORMTYPE, ONE);
        if (pdfTemplate.getLayer() != null) {
            this.put(PdfName.OC, pdfTemplate.getLayer().getRef());
        }
        if (pdfTemplate.getGroup() != null) {
            this.put(PdfName.GROUP, pdfTemplate.getGroup());
        }
        if ((pdfArray = pdfTemplate.getMatrix()) == null) {
            this.put(PdfName.MATRIX, MATRIX);
        } else {
            this.put(PdfName.MATRIX, pdfArray);
        }
        this.bytes = pdfTemplate.toPdf(null);
        this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        this.flateCompress(n);
    }
}

