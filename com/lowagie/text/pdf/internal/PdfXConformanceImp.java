/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.PatternColor;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfXConformanceException;
import com.lowagie.text.pdf.ShadingColor;
import com.lowagie.text.pdf.SpotColor;
import com.lowagie.text.pdf.interfaces.PdfXConformance;
import java.awt.Color;
import java.util.ArrayList;

public class PdfXConformanceImp
implements PdfXConformance {
    public static final int PDFXKEY_COLOR = 1;
    public static final int PDFXKEY_CMYK = 2;
    public static final int PDFXKEY_RGB = 3;
    public static final int PDFXKEY_FONT = 4;
    public static final int PDFXKEY_IMAGE = 5;
    public static final int PDFXKEY_GSTATE = 6;
    public static final int PDFXKEY_LAYER = 7;
    protected int pdfxConformance = 0;

    public void setPDFXConformance(int n) {
        this.pdfxConformance = n;
    }

    public int getPDFXConformance() {
        return this.pdfxConformance;
    }

    public boolean isPdfX() {
        return this.pdfxConformance != 0;
    }

    public boolean isPdfX1A2001() {
        return this.pdfxConformance == 1;
    }

    public boolean isPdfX32002() {
        return this.pdfxConformance == 2;
    }

    public boolean isPdfA1() {
        return this.pdfxConformance == 3 || this.pdfxConformance == 4;
    }

    public boolean isPdfA1A() {
        return this.pdfxConformance == 3;
    }

    public void completeInfoDictionary(PdfDictionary pdfDictionary) {
        if (this.isPdfX() && !this.isPdfA1()) {
            if (pdfDictionary.get(PdfName.GTS_PDFXVERSION) == null) {
                if (this.isPdfX1A2001()) {
                    pdfDictionary.put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-1:2001"));
                    pdfDictionary.put(new PdfName("GTS_PDFXConformance"), new PdfString("PDF/X-1a:2001"));
                } else if (this.isPdfX32002()) {
                    pdfDictionary.put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-3:2002"));
                }
            }
            if (pdfDictionary.get(PdfName.TITLE) == null) {
                pdfDictionary.put(PdfName.TITLE, new PdfString("Pdf document"));
            }
            if (pdfDictionary.get(PdfName.CREATOR) == null) {
                pdfDictionary.put(PdfName.CREATOR, new PdfString("Unknown"));
            }
            if (pdfDictionary.get(PdfName.TRAPPED) == null) {
                pdfDictionary.put(PdfName.TRAPPED, new PdfName("False"));
            }
        }
    }

    public void completeExtraCatalog(PdfDictionary pdfDictionary) {
        if (this.isPdfX() && !this.isPdfA1() && pdfDictionary.get(PdfName.OUTPUTINTENTS) == null) {
            PdfDictionary pdfDictionary2 = new PdfDictionary(PdfName.OUTPUTINTENT);
            pdfDictionary2.put(PdfName.OUTPUTCONDITION, new PdfString("SWOP CGATS TR 001-1995"));
            pdfDictionary2.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString("CGATS TR 001"));
            pdfDictionary2.put(PdfName.REGISTRYNAME, new PdfString("http://www.color.org"));
            pdfDictionary2.put(PdfName.INFO, new PdfString(""));
            pdfDictionary2.put(PdfName.S, PdfName.GTS_PDFX);
            pdfDictionary.put(PdfName.OUTPUTINTENTS, new PdfArray(pdfDictionary2));
        }
    }

    public static void checkPDFXConformance(PdfWriter pdfWriter, int n, Object object) {
        if (pdfWriter == null || !pdfWriter.isPdfX()) {
            return;
        }
        int n2 = pdfWriter.getPDFXConformance();
        block0 : switch (n) {
            case 1: {
                switch (n2) {
                    case 1: {
                        if (object instanceof ExtendedColor) {
                            ExtendedColor extendedColor = (ExtendedColor)object;
                            switch (extendedColor.getType()) {
                                case 1: 
                                case 2: {
                                    return;
                                }
                                case 0: {
                                    throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                                }
                                case 3: {
                                    SpotColor spotColor = (SpotColor)extendedColor;
                                    PdfXConformanceImp.checkPDFXConformance(pdfWriter, 1, spotColor.getPdfSpotColor().getAlternativeCS());
                                    break block0;
                                }
                                case 5: {
                                    ShadingColor shadingColor = (ShadingColor)extendedColor;
                                    PdfXConformanceImp.checkPDFXConformance(pdfWriter, 1, shadingColor.getPdfShadingPattern().getShading().getColorSpace());
                                    break block0;
                                }
                                case 4: {
                                    PatternColor patternColor = (PatternColor)extendedColor;
                                    PdfXConformanceImp.checkPDFXConformance(pdfWriter, 1, patternColor.getPainter().getDefaultColor());
                                }
                            }
                            break block0;
                        }
                        if (!(object instanceof Color)) break;
                        throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                    }
                }
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                if (n2 != 1) break;
                throw new PdfXConformanceException("Colorspace RGB is not allowed.");
            }
            case 4: {
                if (((BaseFont)object).isEmbedded()) break;
                throw new PdfXConformanceException("All the fonts must be embedded.");
            }
            case 5: {
                PdfImage pdfImage = (PdfImage)object;
                if (pdfImage.get(PdfName.SMASK) != null) {
                    throw new PdfXConformanceException("The /SMask key is not allowed in images.");
                }
                switch (n2) {
                    case 1: {
                        PdfObject pdfObject = pdfImage.get(PdfName.COLORSPACE);
                        if (pdfObject == null) {
                            return;
                        }
                        if (pdfObject.isName()) {
                            if (!PdfName.DEVICERGB.equals(pdfObject)) break;
                            throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                        }
                        if (!pdfObject.isArray() || !PdfName.CALRGB.equals(((PdfArray)pdfObject).getArrayList().get(0))) break;
                        throw new PdfXConformanceException("Colorspace CalRGB is not allowed.");
                    }
                }
                break;
            }
            case 6: {
                PdfDictionary pdfDictionary = (PdfDictionary)object;
                PdfObject pdfObject = pdfDictionary.get(PdfName.BM);
                if (pdfObject != null && !PdfGState.BM_NORMAL.equals(pdfObject) && !PdfGState.BM_COMPATIBLE.equals(pdfObject)) {
                    throw new PdfXConformanceException("Blend mode " + pdfObject.toString() + " not allowed.");
                }
                pdfObject = pdfDictionary.get(PdfName.CA);
                double d = 0.0;
                if (pdfObject != null && (d = ((PdfNumber)pdfObject).doubleValue()) != 1.0) {
                    throw new PdfXConformanceException("Transparency is not allowed: /CA = " + d);
                }
                pdfObject = pdfDictionary.get(PdfName.ca);
                d = 0.0;
                if (pdfObject == null || (d = ((PdfNumber)pdfObject).doubleValue()) == 1.0) break;
                throw new PdfXConformanceException("Transparency is not allowed: /ca = " + d);
            }
            case 7: {
                throw new PdfXConformanceException("Layers are not allowed.");
            }
        }
    }
}

