/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfPattern;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

public final class PdfPatternPainter
extends PdfTemplate {
    float xstep;
    float ystep;
    boolean stencil = false;
    Color defaultColor;

    private PdfPatternPainter() {
        this.type = 3;
    }

    PdfPatternPainter(PdfWriter pdfWriter) {
        super(pdfWriter);
        this.type = 3;
    }

    PdfPatternPainter(PdfWriter pdfWriter, Color color) {
        this(pdfWriter);
        this.stencil = true;
        this.defaultColor = color == null ? Color.gray : color;
    }

    public void setXStep(float f) {
        this.xstep = f;
    }

    public void setYStep(float f) {
        this.ystep = f;
    }

    public float getXStep() {
        return this.xstep;
    }

    public float getYStep() {
        return this.ystep;
    }

    public boolean isStencil() {
        return this.stencil;
    }

    public void setPatternMatrix(float f, float f2, float f3, float f4, float f5, float f6) {
        this.setMatrix(f, f2, f3, f4, f5, f6);
    }

    PdfPattern getPattern() {
        return new PdfPattern(this);
    }

    PdfPattern getPattern(int n) {
        return new PdfPattern(this, n);
    }

    public PdfContentByte getDuplicate() {
        PdfPatternPainter pdfPatternPainter = new PdfPatternPainter();
        pdfPatternPainter.writer = this.writer;
        pdfPatternPainter.pdf = this.pdf;
        pdfPatternPainter.thisReference = this.thisReference;
        pdfPatternPainter.pageResources = this.pageResources;
        pdfPatternPainter.bBox = new Rectangle(this.bBox);
        pdfPatternPainter.xstep = this.xstep;
        pdfPatternPainter.ystep = this.ystep;
        pdfPatternPainter.matrix = this.matrix;
        pdfPatternPainter.stencil = this.stencil;
        pdfPatternPainter.defaultColor = this.defaultColor;
        return pdfPatternPainter;
    }

    public Color getDefaultColor() {
        return this.defaultColor;
    }

    public void setGrayFill(float f) {
        this.checkNoColor();
        super.setGrayFill(f);
    }

    public void resetGrayFill() {
        this.checkNoColor();
        super.resetGrayFill();
    }

    public void setGrayStroke(float f) {
        this.checkNoColor();
        super.setGrayStroke(f);
    }

    public void resetGrayStroke() {
        this.checkNoColor();
        super.resetGrayStroke();
    }

    public void setRGBColorFillF(float f, float f2, float f3) {
        this.checkNoColor();
        super.setRGBColorFillF(f, f2, f3);
    }

    public void resetRGBColorFill() {
        this.checkNoColor();
        super.resetRGBColorFill();
    }

    public void setRGBColorStrokeF(float f, float f2, float f3) {
        this.checkNoColor();
        super.setRGBColorStrokeF(f, f2, f3);
    }

    public void resetRGBColorStroke() {
        this.checkNoColor();
        super.resetRGBColorStroke();
    }

    public void setCMYKColorFillF(float f, float f2, float f3, float f4) {
        this.checkNoColor();
        super.setCMYKColorFillF(f, f2, f3, f4);
    }

    public void resetCMYKColorFill() {
        this.checkNoColor();
        super.resetCMYKColorFill();
    }

    public void setCMYKColorStrokeF(float f, float f2, float f3, float f4) {
        this.checkNoColor();
        super.setCMYKColorStrokeF(f, f2, f3, f4);
    }

    public void resetCMYKColorStroke() {
        this.checkNoColor();
        super.resetCMYKColorStroke();
    }

    public void addImage(Image image, float f, float f2, float f3, float f4, float f5, float f6) throws DocumentException {
        if (this.stencil && !image.isMask()) {
            this.checkNoColor();
        }
        super.addImage(image, f, f2, f3, f4, f5, f6);
    }

    public void setCMYKColorFill(int n, int n2, int n3, int n4) {
        this.checkNoColor();
        super.setCMYKColorFill(n, n2, n3, n4);
    }

    public void setCMYKColorStroke(int n, int n2, int n3, int n4) {
        this.checkNoColor();
        super.setCMYKColorStroke(n, n2, n3, n4);
    }

    public void setRGBColorFill(int n, int n2, int n3) {
        this.checkNoColor();
        super.setRGBColorFill(n, n2, n3);
    }

    public void setRGBColorStroke(int n, int n2, int n3) {
        this.checkNoColor();
        super.setRGBColorStroke(n, n2, n3);
    }

    public void setColorStroke(Color color) {
        this.checkNoColor();
        super.setColorStroke(color);
    }

    public void setColorFill(Color color) {
        this.checkNoColor();
        super.setColorFill(color);
    }

    public void setColorFill(PdfSpotColor pdfSpotColor, float f) {
        this.checkNoColor();
        super.setColorFill(pdfSpotColor, f);
    }

    public void setColorStroke(PdfSpotColor pdfSpotColor, float f) {
        this.checkNoColor();
        super.setColorStroke(pdfSpotColor, f);
    }

    public void setPatternFill(PdfPatternPainter pdfPatternPainter) {
        this.checkNoColor();
        super.setPatternFill(pdfPatternPainter);
    }

    public void setPatternFill(PdfPatternPainter pdfPatternPainter, Color color, float f) {
        this.checkNoColor();
        super.setPatternFill(pdfPatternPainter, color, f);
    }

    public void setPatternStroke(PdfPatternPainter pdfPatternPainter, Color color, float f) {
        this.checkNoColor();
        super.setPatternStroke(pdfPatternPainter, color, f);
    }

    public void setPatternStroke(PdfPatternPainter pdfPatternPainter) {
        this.checkNoColor();
        super.setPatternStroke(pdfPatternPainter);
    }

    void checkNoColor() {
        if (this.stencil) {
            throw new RuntimeException("Colors are not allowed in uncolored tile patterns.");
        }
    }
}

