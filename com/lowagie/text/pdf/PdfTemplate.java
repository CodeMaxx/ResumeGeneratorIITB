/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfFormXObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTransparencyGroup;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class PdfTemplate
extends PdfContentByte {
    public static final int TYPE_TEMPLATE = 1;
    public static final int TYPE_IMPORTED = 2;
    public static final int TYPE_PATTERN = 3;
    protected int type = 1;
    protected PdfIndirectReference thisReference;
    protected PageResources pageResources;
    protected Rectangle bBox = new Rectangle(0.0f, 0.0f);
    protected PdfArray matrix;
    protected PdfTransparencyGroup group;
    protected PdfOCG layer;

    protected PdfTemplate() {
        super(null);
    }

    PdfTemplate(PdfWriter pdfWriter) {
        super(pdfWriter);
        this.pageResources = new PageResources();
        this.pageResources.addDefaultColor(pdfWriter.getDefaultColorspace());
        this.thisReference = this.writer.getPdfIndirectReference();
    }

    public static PdfTemplate createTemplate(PdfWriter pdfWriter, float f, float f2) {
        return PdfTemplate.createTemplate(pdfWriter, f, f2, null);
    }

    static PdfTemplate createTemplate(PdfWriter pdfWriter, float f, float f2, PdfName pdfName) {
        PdfTemplate pdfTemplate = new PdfTemplate(pdfWriter);
        pdfTemplate.setWidth(f);
        pdfTemplate.setHeight(f2);
        pdfWriter.addDirectTemplateSimple(pdfTemplate, pdfName);
        return pdfTemplate;
    }

    public void setWidth(float f) {
        this.bBox.setLeft(0.0f);
        this.bBox.setRight(f);
    }

    public void setHeight(float f) {
        this.bBox.setBottom(0.0f);
        this.bBox.setTop(f);
    }

    public float getWidth() {
        return this.bBox.getWidth();
    }

    public float getHeight() {
        return this.bBox.getHeight();
    }

    public Rectangle getBoundingBox() {
        return this.bBox;
    }

    public void setBoundingBox(Rectangle rectangle) {
        this.bBox = rectangle;
    }

    public void setLayer(PdfOCG pdfOCG) {
        this.layer = pdfOCG;
    }

    public PdfOCG getLayer() {
        return this.layer;
    }

    public void setMatrix(float f, float f2, float f3, float f4, float f5, float f6) {
        this.matrix = new PdfArray();
        this.matrix.add(new PdfNumber(f));
        this.matrix.add(new PdfNumber(f2));
        this.matrix.add(new PdfNumber(f3));
        this.matrix.add(new PdfNumber(f4));
        this.matrix.add(new PdfNumber(f5));
        this.matrix.add(new PdfNumber(f6));
    }

    PdfArray getMatrix() {
        return this.matrix;
    }

    public PdfIndirectReference getIndirectReference() {
        return this.thisReference;
    }

    public void beginVariableText() {
        this.content.append("/Tx BMC ");
    }

    public void endVariableText() {
        this.content.append("EMC ");
    }

    PdfObject getResources() {
        return this.getPageResources().getResources();
    }

    PdfStream getFormXObject(int n) throws IOException {
        return new PdfFormXObject(this, n);
    }

    public PdfContentByte getDuplicate() {
        PdfTemplate pdfTemplate = new PdfTemplate();
        pdfTemplate.writer = this.writer;
        pdfTemplate.pdf = this.pdf;
        pdfTemplate.thisReference = this.thisReference;
        pdfTemplate.pageResources = this.pageResources;
        pdfTemplate.bBox = new Rectangle(this.bBox);
        pdfTemplate.group = this.group;
        pdfTemplate.layer = this.layer;
        if (this.matrix != null) {
            pdfTemplate.matrix = new PdfArray(this.matrix);
        }
        pdfTemplate.separator = this.separator;
        return pdfTemplate;
    }

    public int getType() {
        return this.type;
    }

    PageResources getPageResources() {
        return this.pageResources;
    }

    public PdfTransparencyGroup getGroup() {
        return this.group;
    }

    public void setGroup(PdfTransparencyGroup pdfTransparencyGroup) {
        this.group = pdfTransparencyGroup;
    }
}

