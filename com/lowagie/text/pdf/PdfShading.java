/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.ColorDetails;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFunction;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SpotColor;
import java.awt.Color;
import java.io.IOException;

public class PdfShading {
    protected PdfDictionary shading;
    protected PdfWriter writer;
    protected int shadingType;
    protected ColorDetails colorDetails;
    protected PdfName shadingName;
    protected PdfIndirectReference shadingReference;
    private Color cspace;
    protected float[] bBox;
    protected boolean antiAlias = false;

    protected PdfShading(PdfWriter pdfWriter) {
        this.writer = pdfWriter;
    }

    protected void setColorSpace(Color color) {
        this.cspace = color;
        int n = ExtendedColor.getType(color);
        PdfObject pdfObject = null;
        switch (n) {
            case 1: {
                pdfObject = PdfName.DEVICEGRAY;
                break;
            }
            case 2: {
                pdfObject = PdfName.DEVICECMYK;
                break;
            }
            case 3: {
                SpotColor spotColor = (SpotColor)color;
                this.colorDetails = this.writer.addSimple(spotColor.getPdfSpotColor());
                pdfObject = this.colorDetails.getIndirectReference();
                break;
            }
            case 4: 
            case 5: {
                PdfShading.throwColorSpaceError();
            }
            default: {
                pdfObject = PdfName.DEVICERGB;
            }
        }
        this.shading.put(PdfName.COLORSPACE, pdfObject);
    }

    public Color getColorSpace() {
        return this.cspace;
    }

    public static void throwColorSpaceError() {
        throw new IllegalArgumentException("A tiling or shading pattern cannot be used as a color space in a shading pattern");
    }

    public static void checkCompatibleColors(Color color, Color color2) {
        int n;
        int n2 = ExtendedColor.getType(color);
        if (n2 != (n = ExtendedColor.getType(color2))) {
            throw new IllegalArgumentException("Both colors must be of the same type.");
        }
        if (n2 == 3 && ((SpotColor)color).getPdfSpotColor() != ((SpotColor)color2).getPdfSpotColor()) {
            throw new IllegalArgumentException("The spot color must be the same, only the tint can vary.");
        }
        if (n2 == 4 || n2 == 5) {
            PdfShading.throwColorSpaceError();
        }
    }

    public static float[] getColorArray(Color color) {
        int n = ExtendedColor.getType(color);
        switch (n) {
            case 1: {
                return new float[]{((GrayColor)color).getGray()};
            }
            case 2: {
                CMYKColor cMYKColor = (CMYKColor)color;
                return new float[]{cMYKColor.getCyan(), cMYKColor.getMagenta(), cMYKColor.getYellow(), cMYKColor.getBlack()};
            }
            case 3: {
                return new float[]{((SpotColor)color).getTint()};
            }
            case 0: {
                return new float[]{(float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f};
            }
        }
        PdfShading.throwColorSpaceError();
        return null;
    }

    public static PdfShading type1(PdfWriter pdfWriter, Color color, float[] arrf, float[] arrf2, PdfFunction pdfFunction) {
        PdfShading pdfShading = new PdfShading(pdfWriter);
        pdfShading.shading = new PdfDictionary();
        pdfShading.shadingType = 1;
        pdfShading.shading.put(PdfName.SHADINGTYPE, new PdfNumber(pdfShading.shadingType));
        pdfShading.setColorSpace(color);
        if (arrf != null) {
            pdfShading.shading.put(PdfName.DOMAIN, new PdfArray(arrf));
        }
        if (arrf2 != null) {
            pdfShading.shading.put(PdfName.MATRIX, new PdfArray(arrf2));
        }
        pdfShading.shading.put(PdfName.FUNCTION, pdfFunction.getReference());
        return pdfShading;
    }

    public static PdfShading type2(PdfWriter pdfWriter, Color color, float[] arrf, float[] arrf2, PdfFunction pdfFunction, boolean[] arrbl) {
        PdfShading pdfShading = new PdfShading(pdfWriter);
        pdfShading.shading = new PdfDictionary();
        pdfShading.shadingType = 2;
        pdfShading.shading.put(PdfName.SHADINGTYPE, new PdfNumber(pdfShading.shadingType));
        pdfShading.setColorSpace(color);
        pdfShading.shading.put(PdfName.COORDS, new PdfArray(arrf));
        if (arrf2 != null) {
            pdfShading.shading.put(PdfName.DOMAIN, new PdfArray(arrf2));
        }
        pdfShading.shading.put(PdfName.FUNCTION, pdfFunction.getReference());
        if (arrbl != null && (arrbl[0] || arrbl[1])) {
            PdfArray pdfArray = new PdfArray(arrbl[0] ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
            pdfArray.add(arrbl[1] ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
            pdfShading.shading.put(PdfName.EXTEND, pdfArray);
        }
        return pdfShading;
    }

    public static PdfShading type3(PdfWriter pdfWriter, Color color, float[] arrf, float[] arrf2, PdfFunction pdfFunction, boolean[] arrbl) {
        PdfShading pdfShading = PdfShading.type2(pdfWriter, color, arrf, arrf2, pdfFunction, arrbl);
        pdfShading.shadingType = 3;
        pdfShading.shading.put(PdfName.SHADINGTYPE, new PdfNumber(pdfShading.shadingType));
        return pdfShading;
    }

    public static PdfShading simpleAxial(PdfWriter pdfWriter, float f, float f2, float f3, float f4, Color color, Color color2, boolean bl, boolean bl2) {
        PdfShading.checkCompatibleColors(color, color2);
        PdfFunction pdfFunction = PdfFunction.type2(pdfWriter, new float[]{0.0f, 1.0f}, null, PdfShading.getColorArray(color), PdfShading.getColorArray(color2), 1.0f);
        return PdfShading.type2(pdfWriter, color, new float[]{f, f2, f3, f4}, null, pdfFunction, new boolean[]{bl, bl2});
    }

    public static PdfShading simpleAxial(PdfWriter pdfWriter, float f, float f2, float f3, float f4, Color color, Color color2) {
        return PdfShading.simpleAxial(pdfWriter, f, f2, f3, f4, color, color2, true, true);
    }

    public static PdfShading simpleRadial(PdfWriter pdfWriter, float f, float f2, float f3, float f4, float f5, float f6, Color color, Color color2, boolean bl, boolean bl2) {
        PdfShading.checkCompatibleColors(color, color2);
        PdfFunction pdfFunction = PdfFunction.type2(pdfWriter, new float[]{0.0f, 1.0f}, null, PdfShading.getColorArray(color), PdfShading.getColorArray(color2), 1.0f);
        return PdfShading.type3(pdfWriter, color, new float[]{f, f2, f3, f4, f5, f6}, null, pdfFunction, new boolean[]{bl, bl2});
    }

    public static PdfShading simpleRadial(PdfWriter pdfWriter, float f, float f2, float f3, float f4, float f5, float f6, Color color, Color color2) {
        return PdfShading.simpleRadial(pdfWriter, f, f2, f3, f4, f5, f6, color, color2, true, true);
    }

    PdfName getShadingName() {
        return this.shadingName;
    }

    PdfIndirectReference getShadingReference() {
        if (this.shadingReference == null) {
            this.shadingReference = this.writer.getPdfIndirectReference();
        }
        return this.shadingReference;
    }

    void setName(int n) {
        this.shadingName = new PdfName("Sh" + n);
    }

    void addToBody() throws IOException {
        if (this.bBox != null) {
            this.shading.put(PdfName.BBOX, new PdfArray(this.bBox));
        }
        if (this.antiAlias) {
            this.shading.put(PdfName.ANTIALIAS, PdfBoolean.PDFTRUE);
        }
        this.writer.addToBody((PdfObject)this.shading, this.getShadingReference());
    }

    PdfWriter getWriter() {
        return this.writer;
    }

    ColorDetails getColorDetails() {
        return this.colorDetails;
    }

    public float[] getBBox() {
        return this.bBox;
    }

    public void setBBox(float[] arrf) {
        if (arrf.length != 4) {
            throw new IllegalArgumentException("BBox must be a 4 element array.");
        }
        this.bBox = arrf;
    }

    public boolean isAntiAlias() {
        return this.antiAlias;
    }

    public void setAntiAlias(boolean bl) {
        this.antiAlias = bl;
    }
}

