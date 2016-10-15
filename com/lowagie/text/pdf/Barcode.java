/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import java.awt.Color;

public abstract class Barcode {
    public static final int EAN13 = 1;
    public static final int EAN8 = 2;
    public static final int UPCA = 3;
    public static final int UPCE = 4;
    public static final int SUPP2 = 5;
    public static final int SUPP5 = 6;
    public static final int POSTNET = 7;
    public static final int PLANET = 8;
    public static final int CODE128 = 9;
    public static final int CODE128_UCC = 10;
    public static final int CODE128_RAW = 11;
    public static final int CODABAR = 12;
    protected float x;
    protected float n;
    protected BaseFont font;
    protected float size;
    protected float baseline;
    protected float barHeight;
    protected int textAlignment;
    protected boolean generateChecksum;
    protected boolean checksumText;
    protected boolean startStopText;
    protected boolean extended;
    protected String code = "";
    protected boolean guardBars;
    protected int codeType;
    protected float inkSpreading = 0.0f;
    protected String altText;

    public float getX() {
        return this.x;
    }

    public void setX(float f) {
        this.x = f;
    }

    public float getN() {
        return this.n;
    }

    public void setN(float f) {
        this.n = f;
    }

    public BaseFont getFont() {
        return this.font;
    }

    public void setFont(BaseFont baseFont) {
        this.font = baseFont;
    }

    public float getSize() {
        return this.size;
    }

    public void setSize(float f) {
        this.size = f;
    }

    public float getBaseline() {
        return this.baseline;
    }

    public void setBaseline(float f) {
        this.baseline = f;
    }

    public float getBarHeight() {
        return this.barHeight;
    }

    public void setBarHeight(float f) {
        this.barHeight = f;
    }

    public int getTextAlignment() {
        return this.textAlignment;
    }

    public void setTextAlignment(int n) {
        this.textAlignment = n;
    }

    public boolean isGenerateChecksum() {
        return this.generateChecksum;
    }

    public void setGenerateChecksum(boolean bl) {
        this.generateChecksum = bl;
    }

    public boolean isChecksumText() {
        return this.checksumText;
    }

    public void setChecksumText(boolean bl) {
        this.checksumText = bl;
    }

    public boolean isStartStopText() {
        return this.startStopText;
    }

    public void setStartStopText(boolean bl) {
        this.startStopText = bl;
    }

    public boolean isExtended() {
        return this.extended;
    }

    public void setExtended(boolean bl) {
        this.extended = bl;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String string) {
        this.code = string;
    }

    public boolean isGuardBars() {
        return this.guardBars;
    }

    public void setGuardBars(boolean bl) {
        this.guardBars = bl;
    }

    public int getCodeType() {
        return this.codeType;
    }

    public void setCodeType(int n) {
        this.codeType = n;
    }

    public abstract Rectangle getBarcodeSize();

    public abstract Rectangle placeBarcode(PdfContentByte var1, Color var2, Color var3);

    public PdfTemplate createTemplateWithBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        PdfTemplate pdfTemplate = pdfContentByte.createTemplate(0.0f, 0.0f);
        Rectangle rectangle = this.placeBarcode(pdfTemplate, color, color2);
        pdfTemplate.setBoundingBox(rectangle);
        return pdfTemplate;
    }

    public Image createImageWithBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        try {
            return Image.getInstance(this.createTemplateWithBarcode(pdfContentByte, color, color2));
        }
        catch (Exception var4_4) {
            throw new ExceptionConverter(var4_4);
        }
    }

    public abstract java.awt.Image createAwtImage(Color var1, Color var2);

    public float getInkSpreading() {
        return this.inkSpreading;
    }

    public void setInkSpreading(float f) {
        this.inkSpreading = f;
    }

    public String getAltText() {
        return this.altText;
    }

    public void setAltText(String string) {
        this.altText = string;
    }
}

