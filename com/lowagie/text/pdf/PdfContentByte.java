/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Annotation;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.ColorDetails;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.FontDetails;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PatternColor;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfLayerMembership;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPSXObject;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfPrinterGraphics2D;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTextArray;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.ShadingColor;
import com.lowagie.text.pdf.SpotColor;
import com.lowagie.text.pdf.internal.PdfAnnotationsImp;
import com.lowagie.text.pdf.internal.PdfXConformanceImp;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.print.PrinterJob;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PdfContentByte {
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 2;
    public static final int LINE_CAP_BUTT = 0;
    public static final int LINE_CAP_ROUND = 1;
    public static final int LINE_CAP_PROJECTING_SQUARE = 2;
    public static final int LINE_JOIN_MITER = 0;
    public static final int LINE_JOIN_ROUND = 1;
    public static final int LINE_JOIN_BEVEL = 2;
    public static final int TEXT_RENDER_MODE_FILL = 0;
    public static final int TEXT_RENDER_MODE_STROKE = 1;
    public static final int TEXT_RENDER_MODE_FILL_STROKE = 2;
    public static final int TEXT_RENDER_MODE_INVISIBLE = 3;
    public static final int TEXT_RENDER_MODE_FILL_CLIP = 4;
    public static final int TEXT_RENDER_MODE_STROKE_CLIP = 5;
    public static final int TEXT_RENDER_MODE_FILL_STROKE_CLIP = 6;
    public static final int TEXT_RENDER_MODE_CLIP = 7;
    private static final float[] unitRect = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
    protected ByteBuffer content = new ByteBuffer();
    protected PdfWriter writer;
    protected PdfDocument pdf;
    protected GraphicState state = new GraphicState();
    protected ArrayList stateList = new ArrayList();
    protected ArrayList layerDepth;
    protected int separator = 10;
    private static HashMap abrev = new HashMap();

    public PdfContentByte(PdfWriter pdfWriter) {
        if (pdfWriter != null) {
            this.writer = pdfWriter;
            this.pdf = this.writer.getPdfDocument();
        }
    }

    public String toString() {
        return this.content.toString();
    }

    public ByteBuffer getInternalBuffer() {
        return this.content;
    }

    public byte[] toPdf(PdfWriter pdfWriter) {
        return this.content.toByteArray();
    }

    public void add(PdfContentByte pdfContentByte) {
        if (pdfContentByte.writer != null && this.writer != pdfContentByte.writer) {
            throw new RuntimeException("Inconsistent writers. Are you mixing two documents?");
        }
        this.content.append(pdfContentByte.content);
    }

    public float getXTLM() {
        return this.state.xTLM;
    }

    public float getYTLM() {
        return this.state.yTLM;
    }

    public float getLeading() {
        return this.state.leading;
    }

    public float getCharacterSpacing() {
        return this.state.charSpace;
    }

    public float getWordSpacing() {
        return this.state.wordSpace;
    }

    public float getHorizontalScaling() {
        return this.state.scale;
    }

    public void setFlatness(float f) {
        if (f >= 0.0f && f <= 100.0f) {
            this.content.append(f).append(" i").append_i(this.separator);
        }
    }

    public void setLineCap(int n) {
        if (n >= 0 && n <= 2) {
            this.content.append(n).append(" J").append_i(this.separator);
        }
    }

    public void setLineDash(float f) {
        this.content.append("[] ").append(f).append(" d").append_i(this.separator);
    }

    public void setLineDash(float f, float f2) {
        this.content.append("[").append(f).append("] ").append(f2).append(" d").append_i(this.separator);
    }

    public void setLineDash(float f, float f2, float f3) {
        this.content.append("[").append(f).append(' ').append(f2).append("] ").append(f3).append(" d").append_i(this.separator);
    }

    public final void setLineDash(float[] arrf, float f) {
        this.content.append("[");
        for (int i = 0; i < arrf.length; ++i) {
            this.content.append(arrf[i]);
            if (i >= arrf.length - 1) continue;
            this.content.append(' ');
        }
        this.content.append("] ").append(f).append(" d").append_i(this.separator);
    }

    public void setLineJoin(int n) {
        if (n >= 0 && n <= 2) {
            this.content.append(n).append(" j").append_i(this.separator);
        }
    }

    public void setLineWidth(float f) {
        this.content.append(f).append(" w").append_i(this.separator);
    }

    public void setMiterLimit(float f) {
        if (f > 1.0f) {
            this.content.append(f).append(" M").append_i(this.separator);
        }
    }

    public void clip() {
        this.content.append("W").append_i(this.separator);
    }

    public void eoClip() {
        this.content.append("W*").append_i(this.separator);
    }

    public void setGrayFill(float f) {
        this.content.append(f).append(" g").append_i(this.separator);
    }

    public void resetGrayFill() {
        this.content.append("0 g").append_i(this.separator);
    }

    public void setGrayStroke(float f) {
        this.content.append(f).append(" G").append_i(this.separator);
    }

    public void resetGrayStroke() {
        this.content.append("0 G").append_i(this.separator);
    }

    private void HelperRGB(float f, float f2, float f3) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 3, null);
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        if (f2 < 0.0f) {
            f2 = 0.0f;
        } else if (f2 > 1.0f) {
            f2 = 1.0f;
        }
        if (f3 < 0.0f) {
            f3 = 0.0f;
        } else if (f3 > 1.0f) {
            f3 = 1.0f;
        }
        this.content.append(f).append(' ').append(f2).append(' ').append(f3);
    }

    public void setRGBColorFillF(float f, float f2, float f3) {
        this.HelperRGB(f, f2, f3);
        this.content.append(" rg").append_i(this.separator);
    }

    public void resetRGBColorFill() {
        this.content.append("0 g").append_i(this.separator);
    }

    public void setRGBColorStrokeF(float f, float f2, float f3) {
        this.HelperRGB(f, f2, f3);
        this.content.append(" RG").append_i(this.separator);
    }

    public void resetRGBColorStroke() {
        this.content.append("0 G").append_i(this.separator);
    }

    private void HelperCMYK(float f, float f2, float f3, float f4) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        if (f2 < 0.0f) {
            f2 = 0.0f;
        } else if (f2 > 1.0f) {
            f2 = 1.0f;
        }
        if (f3 < 0.0f) {
            f3 = 0.0f;
        } else if (f3 > 1.0f) {
            f3 = 1.0f;
        }
        if (f4 < 0.0f) {
            f4 = 0.0f;
        } else if (f4 > 1.0f) {
            f4 = 1.0f;
        }
        this.content.append(f).append(' ').append(f2).append(' ').append(f3).append(' ').append(f4);
    }

    public void setCMYKColorFillF(float f, float f2, float f3, float f4) {
        this.HelperCMYK(f, f2, f3, f4);
        this.content.append(" k").append_i(this.separator);
    }

    public void resetCMYKColorFill() {
        this.content.append("0 0 0 1 k").append_i(this.separator);
    }

    public void setCMYKColorStrokeF(float f, float f2, float f3, float f4) {
        this.HelperCMYK(f, f2, f3, f4);
        this.content.append(" K").append_i(this.separator);
    }

    public void resetCMYKColorStroke() {
        this.content.append("0 0 0 1 K").append_i(this.separator);
    }

    public void moveTo(float f, float f2) {
        this.content.append(f).append(' ').append(f2).append(" m").append_i(this.separator);
    }

    public void lineTo(float f, float f2) {
        this.content.append(f).append(' ').append(f2).append(" l").append_i(this.separator);
    }

    public void curveTo(float f, float f2, float f3, float f4, float f5, float f6) {
        this.content.append(f).append(' ').append(f2).append(' ').append(f3).append(' ').append(f4).append(' ').append(f5).append(' ').append(f6).append(" c").append_i(this.separator);
    }

    public void curveTo(float f, float f2, float f3, float f4) {
        this.content.append(f).append(' ').append(f2).append(' ').append(f3).append(' ').append(f4).append(" v").append_i(this.separator);
    }

    public void curveFromTo(float f, float f2, float f3, float f4) {
        this.content.append(f).append(' ').append(f2).append(' ').append(f3).append(' ').append(f4).append(" y").append_i(this.separator);
    }

    public void circle(float f, float f2, float f3) {
        float f4 = 0.5523f;
        this.moveTo(f + f3, f2);
        this.curveTo(f + f3, f2 + f3 * f4, f + f3 * f4, f2 + f3, f, f2 + f3);
        this.curveTo(f - f3 * f4, f2 + f3, f - f3, f2 + f3 * f4, f - f3, f2);
        this.curveTo(f - f3, f2 - f3 * f4, f - f3 * f4, f2 - f3, f, f2 - f3);
        this.curveTo(f + f3 * f4, f2 - f3, f + f3, f2 - f3 * f4, f + f3, f2);
    }

    public void rectangle(float f, float f2, float f3, float f4) {
        this.content.append(f).append(' ').append(f2).append(' ').append(f3).append(' ').append(f4).append(" re").append_i(this.separator);
    }

    private boolean compareColors(Color color, Color color2) {
        if (color == null && color2 == null) {
            return true;
        }
        if (color == null || color2 == null) {
            return false;
        }
        if (color instanceof ExtendedColor) {
            return color.equals(color2);
        }
        return color2.equals(color);
    }

    public void variableRectangle(Rectangle rectangle) {
        boolean bl;
        boolean bl2;
        float f = rectangle.getTop();
        float f2 = rectangle.getBottom();
        float f3 = rectangle.getRight();
        float f4 = rectangle.getLeft();
        float f5 = rectangle.getBorderWidthTop();
        float f6 = rectangle.getBorderWidthBottom();
        float f7 = rectangle.getBorderWidthRight();
        float f8 = rectangle.getBorderWidthLeft();
        Color color = rectangle.getBorderColorTop();
        Color color2 = rectangle.getBorderColorBottom();
        Color color3 = rectangle.getBorderColorRight();
        Color color4 = rectangle.getBorderColorLeft();
        this.saveState();
        this.setLineCap(0);
        this.setLineJoin(0);
        float f9 = 0.0f;
        boolean bl3 = false;
        Color color5 = null;
        boolean bl4 = false;
        Color color6 = null;
        if (f5 > 0.0f) {
            f9 = f5;
            this.setLineWidth(f9);
            bl3 = true;
            if (color == null) {
                this.resetRGBColorStroke();
            } else {
                this.setColorStroke(color);
            }
            color5 = color;
            this.moveTo(f4, f - f5 / 2.0f);
            this.lineTo(f3, f - f5 / 2.0f);
            this.stroke();
        }
        if (f6 > 0.0f) {
            if (f6 != f9) {
                f9 = f6;
                this.setLineWidth(f9);
            }
            if (!bl3 || !this.compareColors(color5, color2)) {
                bl3 = true;
                if (color2 == null) {
                    this.resetRGBColorStroke();
                } else {
                    this.setColorStroke(color2);
                }
                color5 = color2;
            }
            this.moveTo(f3, f2 + f6 / 2.0f);
            this.lineTo(f4, f2 + f6 / 2.0f);
            this.stroke();
        }
        if (f7 > 0.0f) {
            if (f7 != f9) {
                f9 = f7;
                this.setLineWidth(f9);
            }
            if (!bl3 || !this.compareColors(color5, color3)) {
                bl3 = true;
                if (color3 == null) {
                    this.resetRGBColorStroke();
                } else {
                    this.setColorStroke(color3);
                }
                color5 = color3;
            }
            bl2 = this.compareColors(color, color3);
            bl = this.compareColors(color2, color3);
            this.moveTo(f3 - f7 / 2.0f, bl2 ? f : f - f5);
            this.lineTo(f3 - f7 / 2.0f, bl ? f2 : f2 + f6);
            this.stroke();
            if (!bl2 || !bl) {
                bl4 = true;
                if (color3 == null) {
                    this.resetRGBColorFill();
                } else {
                    this.setColorFill(color3);
                }
                color6 = color3;
                if (!bl2) {
                    this.moveTo(f3, f);
                    this.lineTo(f3, f - f5);
                    this.lineTo(f3 - f7, f - f5);
                    this.fill();
                }
                if (!bl) {
                    this.moveTo(f3, f2);
                    this.lineTo(f3, f2 + f6);
                    this.lineTo(f3 - f7, f2 + f6);
                    this.fill();
                }
            }
        }
        if (f8 > 0.0f) {
            if (f8 != f9) {
                this.setLineWidth(f8);
            }
            if (!bl3 || !this.compareColors(color5, color4)) {
                if (color4 == null) {
                    this.resetRGBColorStroke();
                } else {
                    this.setColorStroke(color4);
                }
            }
            bl2 = this.compareColors(color, color4);
            bl = this.compareColors(color2, color4);
            this.moveTo(f4 + f8 / 2.0f, bl2 ? f : f - f5);
            this.lineTo(f4 + f8 / 2.0f, bl ? f2 : f2 + f6);
            this.stroke();
            if (!bl2 || !bl) {
                if (!bl4 || !this.compareColors(color6, color4)) {
                    if (color4 == null) {
                        this.resetRGBColorFill();
                    } else {
                        this.setColorFill(color4);
                    }
                }
                if (!bl2) {
                    this.moveTo(f4, f);
                    this.lineTo(f4, f - f5);
                    this.lineTo(f4 + f8, f - f5);
                    this.fill();
                }
                if (!bl) {
                    this.moveTo(f4, f2);
                    this.lineTo(f4, f2 + f6);
                    this.lineTo(f4 + f8, f2 + f6);
                    this.fill();
                }
            }
        }
        this.restoreState();
    }

    public void rectangle(Rectangle rectangle) {
        float f = rectangle.getLeft();
        float f2 = rectangle.getBottom();
        float f3 = rectangle.getRight();
        float f4 = rectangle.getTop();
        Color color = rectangle.getBackgroundColor();
        if (color != null) {
            this.setColorFill(color);
            this.rectangle(f, f2, f3 - f, f4 - f2);
            this.fill();
            this.resetRGBColorFill();
        }
        if (!rectangle.hasBorders()) {
            return;
        }
        if (rectangle.isUseVariableBorders()) {
            this.variableRectangle(rectangle);
        } else {
            Color color2;
            if (rectangle.getBorderWidth() != -1.0f) {
                this.setLineWidth(rectangle.getBorderWidth());
            }
            if ((color2 = rectangle.getBorderColor()) != null) {
                this.setColorStroke(color2);
            }
            if (rectangle.hasBorder(15)) {
                this.rectangle(f, f2, f3 - f, f4 - f2);
            } else {
                if (rectangle.hasBorder(8)) {
                    this.moveTo(f3, f2);
                    this.lineTo(f3, f4);
                }
                if (rectangle.hasBorder(4)) {
                    this.moveTo(f, f2);
                    this.lineTo(f, f4);
                }
                if (rectangle.hasBorder(2)) {
                    this.moveTo(f, f2);
                    this.lineTo(f3, f2);
                }
                if (rectangle.hasBorder(1)) {
                    this.moveTo(f, f4);
                    this.lineTo(f3, f4);
                }
            }
            this.stroke();
            if (color2 != null) {
                this.resetRGBColorStroke();
            }
        }
    }

    public void closePath() {
        this.content.append("h").append_i(this.separator);
    }

    public void newPath() {
        this.content.append("n").append_i(this.separator);
    }

    public void stroke() {
        this.content.append("S").append_i(this.separator);
    }

    public void closePathStroke() {
        this.content.append("s").append_i(this.separator);
    }

    public void fill() {
        this.content.append("f").append_i(this.separator);
    }

    public void eoFill() {
        this.content.append("f*").append_i(this.separator);
    }

    public void fillStroke() {
        this.content.append("B").append_i(this.separator);
    }

    public void closePathFillStroke() {
        this.content.append("b").append_i(this.separator);
    }

    public void eoFillStroke() {
        this.content.append("B*").append_i(this.separator);
    }

    public void closePathEoFillStroke() {
        this.content.append("b*").append_i(this.separator);
    }

    public void addImage(Image image) throws DocumentException {
        this.addImage(image, false);
    }

    public void addImage(Image image, boolean bl) throws DocumentException {
        if (!image.hasAbsoluteY()) {
            throw new DocumentException("The image must have absolute positioning.");
        }
        float[] arrf = image.matrix();
        arrf[4] = image.getAbsoluteX() - arrf[4];
        arrf[5] = image.getAbsoluteY() - arrf[5];
        this.addImage(image, arrf[0], arrf[1], arrf[2], arrf[3], arrf[4], arrf[5], bl);
    }

    public void addImage(Image image, float f, float f2, float f3, float f4, float f5, float f6) throws DocumentException {
        this.addImage(image, f, f2, f3, f4, f5, f6, false);
    }

    public void addImage(Image image, float f, float f2, float f3, float f4, float f5, float f6, boolean bl) throws DocumentException {
        try {
            float f7;
            Object object;
            if (image.getLayer() != null) {
                this.beginLayer(image.getLayer());
            }
            if (image.isImgTemplate()) {
                this.writer.addDirectImageSimple(image);
                object = image.getTemplateData();
                f7 = object.getWidth();
                float f8 = object.getHeight();
                this.addTemplate((PdfTemplate)object, f / f7, f2 / f7, f3 / f8, f4 / f8, f5, f6);
            } else {
                this.content.append("q ");
                this.content.append(f).append(' ');
                this.content.append(f2).append(' ');
                this.content.append(f3).append(' ');
                this.content.append(f4).append(' ');
                this.content.append(f5).append(' ');
                this.content.append(f6).append(" cm");
                if (bl) {
                    this.content.append("\nBI\n");
                    object = new PdfImage(image, "", null);
                    Iterator iterator = object.getKeys().iterator();
                    while (iterator.hasNext()) {
                        Object object2;
                        PdfName pdfName = (PdfName)iterator.next();
                        Object object3 = object.get(pdfName);
                        String string = (String)abrev.get(pdfName);
                        if (string == null) continue;
                        this.content.append(string);
                        boolean bl2 = true;
                        if (pdfName.equals(PdfName.COLORSPACE) && object3.isArray() && (object2 = ((PdfArray)object3).getArrayList()).size() == 4 && PdfName.INDEXED.equals(object2.get(0)) && ((PdfObject)object2.get(1)).isName() && ((PdfObject)object2.get(2)).isNumber() && ((PdfObject)object2.get(3)).isString()) {
                            bl2 = false;
                        }
                        if (bl2 && pdfName.equals(PdfName.COLORSPACE) && !object3.isName()) {
                            object2 = this.writer.getColorspaceName();
                            PageResources pageResources = this.getPageResources();
                            pageResources.addColor((PdfName)object2, this.writer.addToBody((PdfObject)object3).getIndirectReference());
                            object3 = object2;
                        }
                        object3.toPdf(null, this.content);
                        this.content.append('\n');
                    }
                    this.content.append("ID\n");
                    object.writeContent(this.content);
                    this.content.append("\nEI\nQ").append_i(this.separator);
                } else {
                    PageResources pageResources = this.getPageResources();
                    Image image2 = image.getImageMask();
                    if (image2 != null) {
                        object = this.writer.addDirectImageSimple(image2);
                        pageResources.addXObject((PdfName)object, this.writer.getImageReference((PdfName)object));
                    }
                    object = this.writer.addDirectImageSimple(image);
                    object = pageResources.addXObject((PdfName)object, this.writer.getImageReference((PdfName)object));
                    this.content.append(' ').append(object.getBytes()).append(" Do Q").append_i(this.separator);
                }
            }
            if (image.hasBorders()) {
                this.saveState();
                float f9 = image.getWidth();
                f7 = image.getHeight();
                this.concatCTM(f / f9, f2 / f9, f3 / f7, f4 / f7, f5, f6);
                this.rectangle(image);
                this.restoreState();
            }
            if (image.getLayer() != null) {
                this.endLayer();
            }
            if ((object = image.getAnnotation()) == null) {
                return;
            }
            float[] arrf = new float[unitRect.length];
            for (int i = 0; i < unitRect.length; i += 2) {
                arrf[i] = f * unitRect[i] + f3 * unitRect[i + 1] + f5;
                arrf[i + 1] = f2 * unitRect[i] + f4 * unitRect[i + 1] + f6;
            }
            float f10 = arrf[0];
            float f11 = arrf[1];
            float f12 = f10;
            float f13 = f11;
            for (int j = 2; j < arrf.length; j += 2) {
                f10 = Math.min(f10, arrf[j]);
                f11 = Math.min(f11, arrf[j + 1]);
                f12 = Math.max(f12, arrf[j]);
                f13 = Math.max(f13, arrf[j + 1]);
            }
            object = new Annotation((Annotation)object);
            object.setDimensions(f10, f11, f12, f13);
            PdfAnnotation pdfAnnotation = PdfAnnotationsImp.convertAnnotation(this.writer, (Annotation)object, new Rectangle(f10, f11, f12, f13));
            if (pdfAnnotation == null) {
                return;
            }
            this.addAnnotation(pdfAnnotation);
        }
        catch (Exception var9_11) {
            throw new DocumentException(var9_11);
        }
    }

    public void reset() {
        this.content.reset();
        this.stateList.clear();
        this.state = new GraphicState();
    }

    public void beginText() {
        this.state.xTLM = 0.0f;
        this.state.yTLM = 0.0f;
        this.content.append("BT").append_i(this.separator);
    }

    public void endText() {
        this.content.append("ET").append_i(this.separator);
    }

    public void saveState() {
        this.content.append("q").append_i(this.separator);
        this.stateList.add(new GraphicState(this.state));
    }

    public void restoreState() {
        this.content.append("Q").append_i(this.separator);
        int n = this.stateList.size() - 1;
        if (n < 0) {
            throw new RuntimeException("Unbalanced save/restore state operators.");
        }
        this.state = (GraphicState)this.stateList.get(n);
        this.stateList.remove(n);
    }

    public void setCharacterSpacing(float f) {
        this.state.charSpace = f;
        this.content.append(f).append(" Tc").append_i(this.separator);
    }

    public void setWordSpacing(float f) {
        this.state.wordSpace = f;
        this.content.append(f).append(" Tw").append_i(this.separator);
    }

    public void setHorizontalScaling(float f) {
        this.state.scale = f;
        this.content.append(f).append(" Tz").append_i(this.separator);
    }

    public void setLeading(float f) {
        this.state.leading = f;
        this.content.append(f).append(" TL").append_i(this.separator);
    }

    public void setFontAndSize(BaseFont baseFont, float f) {
        this.checkWriter();
        if (f < 1.0E-4f && f > -1.0E-4f) {
            throw new IllegalArgumentException("Font size too small: " + f);
        }
        this.state.size = f;
        this.state.fontDetails = this.writer.addSimple(baseFont);
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = this.state.fontDetails.getFontName();
        pdfName = pageResources.addFont(pdfName, this.state.fontDetails.getIndirectReference());
        this.content.append(pdfName.getBytes()).append(' ').append(f).append(" Tf").append_i(this.separator);
    }

    public void setTextRenderingMode(int n) {
        this.content.append(n).append(" Tr").append_i(this.separator);
    }

    public void setTextRise(float f) {
        this.content.append(f).append(" Ts").append_i(this.separator);
    }

    private void showText2(String string) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException("Font and size must be set before writing any text");
        }
        byte[] arrby = this.state.fontDetails.convertToBytes(string);
        PdfContentByte.escapeString(arrby, this.content);
    }

    public void showText(String string) {
        this.showText2(string);
        this.content.append("Tj").append_i(this.separator);
    }

    public static PdfTextArray getKernArray(String string, BaseFont baseFont) {
        PdfTextArray pdfTextArray = new PdfTextArray();
        StringBuffer stringBuffer = new StringBuffer();
        int n = string.length() - 1;
        char[] arrc = string.toCharArray();
        if (n >= 0) {
            stringBuffer.append(arrc, 0, 1);
        }
        for (int i = 0; i < n; ++i) {
            char c = arrc[i + 1];
            int n2 = baseFont.getKerning(arrc[i], c);
            if (n2 == 0) {
                stringBuffer.append(c);
                continue;
            }
            pdfTextArray.add(stringBuffer.toString());
            stringBuffer.setLength(0);
            stringBuffer.append(arrc, i + 1, 1);
            pdfTextArray.add(- n2);
        }
        pdfTextArray.add(stringBuffer.toString());
        return pdfTextArray;
    }

    public void showTextKerned(String string) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException("Font and size must be set before writing any text");
        }
        BaseFont baseFont = this.state.fontDetails.getBaseFont();
        if (baseFont.hasKernPairs()) {
            this.showText(PdfContentByte.getKernArray(string, baseFont));
        } else {
            this.showText(string);
        }
    }

    public void newlineShowText(String string) {
        this.state.yTLM -= this.state.leading;
        this.showText2(string);
        this.content.append("'").append_i(this.separator);
    }

    public void newlineShowText(float f, float f2, String string) {
        this.state.yTLM -= this.state.leading;
        this.content.append(f).append(' ').append(f2);
        this.showText2(string);
        this.content.append("\"").append_i(this.separator);
        this.state.charSpace = f2;
        this.state.wordSpace = f;
    }

    public void setTextMatrix(float f, float f2, float f3, float f4, float f5, float f6) {
        this.state.xTLM = f5;
        this.state.yTLM = f6;
        this.content.append(f).append(' ').append(f2).append_i(32).append(f3).append_i(32).append(f4).append_i(32).append(f5).append_i(32).append(f6).append(" Tm").append_i(this.separator);
    }

    public void setTextMatrix(float f, float f2) {
        this.setTextMatrix(1.0f, 0.0f, 0.0f, 1.0f, f, f2);
    }

    public void moveText(float f, float f2) {
        this.state.xTLM += f;
        this.state.yTLM += f2;
        this.content.append(f).append(' ').append(f2).append(" Td").append_i(this.separator);
    }

    public void moveTextWithLeading(float f, float f2) {
        this.state.xTLM += f;
        this.state.yTLM += f2;
        this.state.leading = - f2;
        this.content.append(f).append(' ').append(f2).append(" TD").append_i(this.separator);
    }

    public void newlineText() {
        this.state.yTLM -= this.state.leading;
        this.content.append("T*").append_i(this.separator);
    }

    int size() {
        return this.content.size();
    }

    static byte[] escapeString(byte[] arrby) {
        ByteBuffer byteBuffer = new ByteBuffer();
        PdfContentByte.escapeString(arrby, byteBuffer);
        return byteBuffer.toByteArray();
    }

    static void escapeString(byte[] arrby, ByteBuffer byteBuffer) {
        byteBuffer.append_i(40);
        block8 : for (int i = 0; i < arrby.length; ++i) {
            byte by = arrby[i];
            switch (by) {
                case 13: {
                    byteBuffer.append("\\r");
                    continue block8;
                }
                case 10: {
                    byteBuffer.append("\\n");
                    continue block8;
                }
                case 9: {
                    byteBuffer.append("\\t");
                    continue block8;
                }
                case 8: {
                    byteBuffer.append("\\b");
                    continue block8;
                }
                case 12: {
                    byteBuffer.append("\\f");
                    continue block8;
                }
                case 40: 
                case 41: 
                case 92: {
                    byteBuffer.append_i(92).append_i(by);
                    continue block8;
                }
                default: {
                    byteBuffer.append_i(by);
                }
            }
        }
        byteBuffer.append(")");
    }

    public void addOutline(PdfOutline pdfOutline, String string) {
        this.checkWriter();
        this.pdf.addOutline(pdfOutline, string);
    }

    public PdfOutline getRootOutline() {
        this.checkWriter();
        return this.pdf.getRootOutline();
    }

    public float getEffectiveStringWidth(String string, boolean bl) {
        BaseFont baseFont = this.state.fontDetails.getBaseFont();
        float f = bl ? baseFont.getWidthPointKerned(string, this.state.size) : baseFont.getWidthPoint(string, this.state.size);
        if (this.state.charSpace != 0.0f && string.length() > 1) {
            f += this.state.charSpace * (float)(string.length() - 1);
        }
        int n = baseFont.getFontType();
        if (this.state.wordSpace != 0.0f && (n == 0 || n == 1 || n == 5)) {
            for (int i = 0; i < string.length() - 1; ++i) {
                if (string.charAt(i) != ' ') continue;
                f += this.state.wordSpace;
            }
        }
        if ((double)this.state.scale != 100.0) {
            f = f * this.state.scale / 100.0f;
        }
        return f;
    }

    public void showTextAligned(int n, String string, float f, float f2, float f3) {
        this.showTextAligned(n, string, f, f2, f3, false);
    }

    private void showTextAligned(int n, String string, float f, float f2, float f3, boolean bl) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException("Font and size must be set before writing any text");
        }
        if (f3 == 0.0f) {
            switch (n) {
                case 1: {
                    f -= this.getEffectiveStringWidth(string, bl) / 2.0f;
                    break;
                }
                case 2: {
                    f -= this.getEffectiveStringWidth(string, bl);
                }
            }
            this.setTextMatrix(f, f2);
            if (bl) {
                this.showTextKerned(string);
            } else {
                this.showText(string);
            }
        } else {
            double d = (double)f3 * 3.141592653589793 / 180.0;
            float f4 = (float)Math.cos(d);
            float f5 = (float)Math.sin(d);
            switch (n) {
                case 1: {
                    float f6 = this.getEffectiveStringWidth(string, bl) / 2.0f;
                    f -= f6 * f4;
                    f2 -= f6 * f5;
                    break;
                }
                case 2: {
                    float f7 = this.getEffectiveStringWidth(string, bl);
                    f -= f7 * f4;
                    f2 -= f7 * f5;
                }
            }
            this.setTextMatrix(f4, f5, - f5, f4, f, f2);
            if (bl) {
                this.showTextKerned(string);
            } else {
                this.showText(string);
            }
            this.setTextMatrix(0.0f, 0.0f);
        }
    }

    public void showTextAlignedKerned(int n, String string, float f, float f2, float f3) {
        this.showTextAligned(n, string, f, f2, f3, true);
    }

    public void concatCTM(float f, float f2, float f3, float f4, float f5, float f6) {
        this.content.append(f).append(' ').append(f2).append(' ').append(f3).append(' ');
        this.content.append(f4).append(' ').append(f5).append(' ').append(f6).append(" cm").append_i(this.separator);
    }

    public static ArrayList bezierArc(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7;
        int n;
        float f8;
        if (f > f3) {
            f7 = f;
            f = f3;
            f3 = f7;
        }
        if (f4 > f2) {
            f7 = f2;
            f2 = f4;
            f4 = f7;
        }
        if (Math.abs(f6) <= 90.0f) {
            f8 = f6;
            n = 1;
        } else {
            n = (int)Math.ceil(Math.abs(f6) / 90.0f);
            f8 = f6 / (float)n;
        }
        float f9 = (f + f3) / 2.0f;
        float f10 = (f2 + f4) / 2.0f;
        float f11 = (f3 - f) / 2.0f;
        float f12 = (f4 - f2) / 2.0f;
        float f13 = (float)((double)f8 * 3.141592653589793 / 360.0);
        float f14 = (float)Math.abs(1.3333333333333333 * (1.0 - Math.cos(f13)) / Math.sin(f13));
        ArrayList<float[]> arrayList = new ArrayList<float[]>();
        for (int i = 0; i < n; ++i) {
            float f15 = (float)((double)(f5 + (float)i * f8) * 3.141592653589793 / 180.0);
            float f16 = (float)((double)(f5 + (float)(i + 1) * f8) * 3.141592653589793 / 180.0);
            float f17 = (float)Math.cos(f15);
            float f18 = (float)Math.cos(f16);
            float f19 = (float)Math.sin(f15);
            float f20 = (float)Math.sin(f16);
            if (f8 > 0.0f) {
                arrayList.add(new float[]{f9 + f11 * f17, f10 - f12 * f19, f9 + f11 * (f17 - f14 * f19), f10 - f12 * (f19 + f14 * f17), f9 + f11 * (f18 + f14 * f20), f10 - f12 * (f20 - f14 * f18), f9 + f11 * f18, f10 - f12 * f20});
                continue;
            }
            arrayList.add(new float[]{f9 + f11 * f17, f10 - f12 * f19, f9 + f11 * (f17 + f14 * f19), f10 - f12 * (f19 - f14 * f17), f9 + f11 * (f18 - f14 * f20), f10 - f12 * (f20 + f14 * f18), f9 + f11 * f18, f10 - f12 * f20});
        }
        return arrayList;
    }

    public void arc(float f, float f2, float f3, float f4, float f5, float f6) {
        ArrayList arrayList = PdfContentByte.bezierArc(f, f2, f3, f4, f5, f6);
        if (arrayList.isEmpty()) {
            return;
        }
        float[] arrf = (float[])arrayList.get(0);
        this.moveTo(arrf[0], arrf[1]);
        for (int i = 0; i < arrayList.size(); ++i) {
            arrf = (float[])arrayList.get(i);
            this.curveTo(arrf[2], arrf[3], arrf[4], arrf[5], arrf[6], arrf[7]);
        }
    }

    public void ellipse(float f, float f2, float f3, float f4) {
        this.arc(f, f2, f3, f4, 0.0f, 360.0f);
    }

    public PdfPatternPainter createPattern(float f, float f2, float f3, float f4) {
        this.checkWriter();
        if (f3 == 0.0f || f4 == 0.0f) {
            throw new RuntimeException("XStep or YStep can not be ZERO.");
        }
        PdfPatternPainter pdfPatternPainter = new PdfPatternPainter(this.writer);
        pdfPatternPainter.setWidth(f);
        pdfPatternPainter.setHeight(f2);
        pdfPatternPainter.setXStep(f3);
        pdfPatternPainter.setYStep(f4);
        this.writer.addSimplePattern(pdfPatternPainter);
        return pdfPatternPainter;
    }

    public PdfPatternPainter createPattern(float f, float f2) {
        return this.createPattern(f, f2, f, f2);
    }

    public PdfPatternPainter createPattern(float f, float f2, float f3, float f4, Color color) {
        this.checkWriter();
        if (f3 == 0.0f || f4 == 0.0f) {
            throw new RuntimeException("XStep or YStep can not be ZERO.");
        }
        PdfPatternPainter pdfPatternPainter = new PdfPatternPainter(this.writer, color);
        pdfPatternPainter.setWidth(f);
        pdfPatternPainter.setHeight(f2);
        pdfPatternPainter.setXStep(f3);
        pdfPatternPainter.setYStep(f4);
        this.writer.addSimplePattern(pdfPatternPainter);
        return pdfPatternPainter;
    }

    public PdfPatternPainter createPattern(float f, float f2, Color color) {
        return this.createPattern(f, f2, f, f2, color);
    }

    public PdfTemplate createTemplate(float f, float f2) {
        return this.createTemplate(f, f2, null);
    }

    PdfTemplate createTemplate(float f, float f2, PdfName pdfName) {
        this.checkWriter();
        PdfTemplate pdfTemplate = new PdfTemplate(this.writer);
        pdfTemplate.setWidth(f);
        pdfTemplate.setHeight(f2);
        this.writer.addDirectTemplateSimple(pdfTemplate, pdfName);
        return pdfTemplate;
    }

    public PdfAppearance createAppearance(float f, float f2) {
        return this.createAppearance(f, f2, null);
    }

    PdfAppearance createAppearance(float f, float f2, PdfName pdfName) {
        this.checkWriter();
        PdfAppearance pdfAppearance = new PdfAppearance(this.writer);
        pdfAppearance.setWidth(f);
        pdfAppearance.setHeight(f2);
        this.writer.addDirectTemplateSimple(pdfAppearance, pdfName);
        return pdfAppearance;
    }

    public void addPSXObject(PdfPSXObject pdfPSXObject) {
        this.checkWriter();
        PdfName pdfName = this.writer.addDirectTemplateSimple(pdfPSXObject, null);
        PageResources pageResources = this.getPageResources();
        pdfName = pageResources.addXObject(pdfName, pdfPSXObject.getIndirectReference());
        this.content.append(pdfName.getBytes()).append(" Do").append_i(this.separator);
    }

    public void addTemplate(PdfTemplate pdfTemplate, float f, float f2, float f3, float f4, float f5, float f6) {
        this.checkWriter();
        this.checkNoPattern(pdfTemplate);
        PdfName pdfName = this.writer.addDirectTemplateSimple(pdfTemplate, null);
        PageResources pageResources = this.getPageResources();
        pdfName = pageResources.addXObject(pdfName, pdfTemplate.getIndirectReference());
        this.content.append("q ");
        this.content.append(f).append(' ');
        this.content.append(f2).append(' ');
        this.content.append(f3).append(' ');
        this.content.append(f4).append(' ');
        this.content.append(f5).append(' ');
        this.content.append(f6).append(" cm ");
        this.content.append(pdfName.getBytes()).append(" Do Q").append_i(this.separator);
    }

    void addTemplateReference(PdfIndirectReference pdfIndirectReference, PdfName pdfName, float f, float f2, float f3, float f4, float f5, float f6) {
        this.checkWriter();
        PageResources pageResources = this.getPageResources();
        pdfName = pageResources.addXObject(pdfName, pdfIndirectReference);
        this.content.append("q ");
        this.content.append(f).append(' ');
        this.content.append(f2).append(' ');
        this.content.append(f3).append(' ');
        this.content.append(f4).append(' ');
        this.content.append(f5).append(' ');
        this.content.append(f6).append(" cm ");
        this.content.append(pdfName.getBytes()).append(" Do Q").append_i(this.separator);
    }

    public void addTemplate(PdfTemplate pdfTemplate, float f, float f2) {
        this.addTemplate(pdfTemplate, 1.0f, 0.0f, 0.0f, 1.0f, f, f2);
    }

    public void setCMYKColorFill(int n, int n2, int n3, int n4) {
        this.content.append((float)(n & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(n2 & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(n3 & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(n4 & 255) / 255.0f);
        this.content.append(" k").append_i(this.separator);
    }

    public void setCMYKColorStroke(int n, int n2, int n3, int n4) {
        this.content.append((float)(n & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(n2 & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(n3 & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((float)(n4 & 255) / 255.0f);
        this.content.append(" K").append_i(this.separator);
    }

    public void setRGBColorFill(int n, int n2, int n3) {
        this.HelperRGB((float)(n & 255) / 255.0f, (float)(n2 & 255) / 255.0f, (float)(n3 & 255) / 255.0f);
        this.content.append(" rg").append_i(this.separator);
    }

    public void setRGBColorStroke(int n, int n2, int n3) {
        this.HelperRGB((float)(n & 255) / 255.0f, (float)(n2 & 255) / 255.0f, (float)(n3 & 255) / 255.0f);
        this.content.append(" RG").append_i(this.separator);
    }

    public void setColorStroke(Color color) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int n = ExtendedColor.getType(color);
        switch (n) {
            case 1: {
                this.setGrayStroke(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                CMYKColor cMYKColor = (CMYKColor)color;
                this.setCMYKColorStrokeF(cMYKColor.getCyan(), cMYKColor.getMagenta(), cMYKColor.getYellow(), cMYKColor.getBlack());
                break;
            }
            case 3: {
                SpotColor spotColor = (SpotColor)color;
                this.setColorStroke(spotColor.getPdfSpotColor(), spotColor.getTint());
                break;
            }
            case 4: {
                PatternColor patternColor = (PatternColor)color;
                this.setPatternStroke(patternColor.getPainter());
                break;
            }
            case 5: {
                ShadingColor shadingColor = (ShadingColor)color;
                this.setShadingStroke(shadingColor.getPdfShadingPattern());
                break;
            }
            default: {
                this.setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
            }
        }
    }

    public void setColorFill(Color color) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int n = ExtendedColor.getType(color);
        switch (n) {
            case 1: {
                this.setGrayFill(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                CMYKColor cMYKColor = (CMYKColor)color;
                this.setCMYKColorFillF(cMYKColor.getCyan(), cMYKColor.getMagenta(), cMYKColor.getYellow(), cMYKColor.getBlack());
                break;
            }
            case 3: {
                SpotColor spotColor = (SpotColor)color;
                this.setColorFill(spotColor.getPdfSpotColor(), spotColor.getTint());
                break;
            }
            case 4: {
                PatternColor patternColor = (PatternColor)color;
                this.setPatternFill(patternColor.getPainter());
                break;
            }
            case 5: {
                ShadingColor shadingColor = (ShadingColor)color;
                this.setShadingFill(shadingColor.getPdfShadingPattern());
                break;
            }
            default: {
                this.setRGBColorFill(color.getRed(), color.getGreen(), color.getBlue());
            }
        }
    }

    public void setColorFill(PdfSpotColor pdfSpotColor, float f) {
        this.checkWriter();
        this.state.colorDetails = this.writer.addSimple(pdfSpotColor);
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = this.state.colorDetails.getColorName();
        pdfName = pageResources.addColor(pdfName, this.state.colorDetails.getIndirectReference());
        this.content.append(pdfName.getBytes()).append(" cs ").append(f).append(" scn").append_i(this.separator);
    }

    public void setColorStroke(PdfSpotColor pdfSpotColor, float f) {
        this.checkWriter();
        this.state.colorDetails = this.writer.addSimple(pdfSpotColor);
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = this.state.colorDetails.getColorName();
        pdfName = pageResources.addColor(pdfName, this.state.colorDetails.getIndirectReference());
        this.content.append(pdfName.getBytes()).append(" CS ").append(f).append(" SCN").append_i(this.separator);
    }

    public void setPatternFill(PdfPatternPainter pdfPatternPainter) {
        if (pdfPatternPainter.isStencil()) {
            this.setPatternFill(pdfPatternPainter, pdfPatternPainter.getDefaultColor());
            return;
        }
        this.checkWriter();
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = this.writer.addSimplePattern(pdfPatternPainter);
        pdfName = pageResources.addPattern(pdfName, pdfPatternPainter.getIndirectReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(pdfName.getBytes()).append(" scn").append_i(this.separator);
    }

    void outputColorNumbers(Color color, float f) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int n = ExtendedColor.getType(color);
        switch (n) {
            case 0: {
                this.content.append((float)color.getRed() / 255.0f);
                this.content.append(' ');
                this.content.append((float)color.getGreen() / 255.0f);
                this.content.append(' ');
                this.content.append((float)color.getBlue() / 255.0f);
                break;
            }
            case 1: {
                this.content.append(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                CMYKColor cMYKColor = (CMYKColor)color;
                this.content.append(cMYKColor.getCyan()).append(' ').append(cMYKColor.getMagenta());
                this.content.append(' ').append(cMYKColor.getYellow()).append(' ').append(cMYKColor.getBlack());
                break;
            }
            case 3: {
                this.content.append(f);
                break;
            }
            default: {
                throw new RuntimeException("Invalid color type.");
            }
        }
    }

    public void setPatternFill(PdfPatternPainter pdfPatternPainter, Color color) {
        if (ExtendedColor.getType(color) == 3) {
            this.setPatternFill(pdfPatternPainter, color, ((SpotColor)color).getTint());
        } else {
            this.setPatternFill(pdfPatternPainter, color, 0.0f);
        }
    }

    public void setPatternFill(PdfPatternPainter pdfPatternPainter, Color color, float f) {
        this.checkWriter();
        if (!pdfPatternPainter.isStencil()) {
            throw new RuntimeException("An uncolored pattern was expected.");
        }
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = this.writer.addSimplePattern(pdfPatternPainter);
        pdfName = pageResources.addPattern(pdfName, pdfPatternPainter.getIndirectReference());
        ColorDetails colorDetails = this.writer.addSimplePatternColorspace(color);
        PdfName pdfName2 = pageResources.addColor(colorDetails.getColorName(), colorDetails.getIndirectReference());
        this.content.append(pdfName2.getBytes()).append(" cs").append_i(this.separator);
        this.outputColorNumbers(color, f);
        this.content.append(' ').append(pdfName.getBytes()).append(" scn").append_i(this.separator);
    }

    public void setPatternStroke(PdfPatternPainter pdfPatternPainter, Color color) {
        if (ExtendedColor.getType(color) == 3) {
            this.setPatternStroke(pdfPatternPainter, color, ((SpotColor)color).getTint());
        } else {
            this.setPatternStroke(pdfPatternPainter, color, 0.0f);
        }
    }

    public void setPatternStroke(PdfPatternPainter pdfPatternPainter, Color color, float f) {
        this.checkWriter();
        if (!pdfPatternPainter.isStencil()) {
            throw new RuntimeException("An uncolored pattern was expected.");
        }
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = this.writer.addSimplePattern(pdfPatternPainter);
        pdfName = pageResources.addPattern(pdfName, pdfPatternPainter.getIndirectReference());
        ColorDetails colorDetails = this.writer.addSimplePatternColorspace(color);
        PdfName pdfName2 = pageResources.addColor(colorDetails.getColorName(), colorDetails.getIndirectReference());
        this.content.append(pdfName2.getBytes()).append(" CS").append_i(this.separator);
        this.outputColorNumbers(color, f);
        this.content.append(' ').append(pdfName.getBytes()).append(" SCN").append_i(this.separator);
    }

    public void setPatternStroke(PdfPatternPainter pdfPatternPainter) {
        if (pdfPatternPainter.isStencil()) {
            this.setPatternStroke(pdfPatternPainter, pdfPatternPainter.getDefaultColor());
            return;
        }
        this.checkWriter();
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = this.writer.addSimplePattern(pdfPatternPainter);
        pdfName = pageResources.addPattern(pdfName, pdfPatternPainter.getIndirectReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(pdfName.getBytes()).append(" SCN").append_i(this.separator);
    }

    public void paintShading(PdfShading pdfShading) {
        this.writer.addSimpleShading(pdfShading);
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = pageResources.addShading(pdfShading.getShadingName(), pdfShading.getShadingReference());
        this.content.append(pdfName.getBytes()).append(" sh").append_i(this.separator);
        ColorDetails colorDetails = pdfShading.getColorDetails();
        if (colorDetails != null) {
            pageResources.addColor(colorDetails.getColorName(), colorDetails.getIndirectReference());
        }
    }

    public void paintShading(PdfShadingPattern pdfShadingPattern) {
        this.paintShading(pdfShadingPattern.getShading());
    }

    public void setShadingFill(PdfShadingPattern pdfShadingPattern) {
        this.writer.addSimpleShadingPattern(pdfShadingPattern);
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = pageResources.addPattern(pdfShadingPattern.getPatternName(), pdfShadingPattern.getPatternReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(pdfName.getBytes()).append(" scn").append_i(this.separator);
        ColorDetails colorDetails = pdfShadingPattern.getColorDetails();
        if (colorDetails != null) {
            pageResources.addColor(colorDetails.getColorName(), colorDetails.getIndirectReference());
        }
    }

    public void setShadingStroke(PdfShadingPattern pdfShadingPattern) {
        this.writer.addSimpleShadingPattern(pdfShadingPattern);
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = pageResources.addPattern(pdfShadingPattern.getPatternName(), pdfShadingPattern.getPatternReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(pdfName.getBytes()).append(" SCN").append_i(this.separator);
        ColorDetails colorDetails = pdfShadingPattern.getColorDetails();
        if (colorDetails != null) {
            pageResources.addColor(colorDetails.getColorName(), colorDetails.getIndirectReference());
        }
    }

    protected void checkWriter() {
        if (this.writer == null) {
            throw new NullPointerException("The writer in PdfContentByte is null.");
        }
    }

    public void showText(PdfTextArray pdfTextArray) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException("Font and size must be set before writing any text");
        }
        this.content.append("[");
        ArrayList arrayList = pdfTextArray.getArrayList();
        boolean bl = false;
        for (int i = 0; i < arrayList.size(); ++i) {
            Object e = arrayList.get(i);
            if (e instanceof String) {
                this.showText2((String)e);
                bl = false;
                continue;
            }
            if (bl) {
                this.content.append(' ');
            } else {
                bl = true;
            }
            this.content.append(((Float)e).floatValue());
        }
        this.content.append("]TJ").append_i(this.separator);
    }

    public PdfWriter getPdfWriter() {
        return this.writer;
    }

    public PdfDocument getPdfDocument() {
        return this.pdf;
    }

    public void localGoto(String string, float f, float f2, float f3, float f4) {
        this.pdf.localGoto(string, f, f2, f3, f4);
    }

    public boolean localDestination(String string, PdfDestination pdfDestination) {
        return this.pdf.localDestination(string, pdfDestination);
    }

    public PdfContentByte getDuplicate() {
        return new PdfContentByte(this.writer);
    }

    public void remoteGoto(String string, String string2, float f, float f2, float f3, float f4) {
        this.pdf.remoteGoto(string, string2, f, f2, f3, f4);
    }

    public void remoteGoto(String string, int n, float f, float f2, float f3, float f4) {
        this.pdf.remoteGoto(string, n, f, f2, f3, f4);
    }

    public void roundRectangle(float f, float f2, float f3, float f4, float f5) {
        if (f3 < 0.0f) {
            f += f3;
            f3 = - f3;
        }
        if (f4 < 0.0f) {
            f2 += f4;
            f4 = - f4;
        }
        if (f5 < 0.0f) {
            f5 = - f5;
        }
        float f6 = 0.4477f;
        this.moveTo(f + f5, f2);
        this.lineTo(f + f3 - f5, f2);
        this.curveTo(f + f3 - f5 * f6, f2, f + f3, f2 + f5 * f6, f + f3, f2 + f5);
        this.lineTo(f + f3, f2 + f4 - f5);
        this.curveTo(f + f3, f2 + f4 - f5 * f6, f + f3 - f5 * f6, f2 + f4, f + f3 - f5, f2 + f4);
        this.lineTo(f + f5, f2 + f4);
        this.curveTo(f + f5 * f6, f2 + f4, f, f2 + f4 - f5 * f6, f, f2 + f4 - f5);
        this.lineTo(f, f2 + f5);
        this.curveTo(f, f2 + f5 * f6, f + f5 * f6, f2, f + f5, f2);
    }

    public void setAction(PdfAction pdfAction, float f, float f2, float f3, float f4) {
        this.pdf.setAction(pdfAction, f, f2, f3, f4);
    }

    public void setLiteral(String string) {
        this.content.append(string);
    }

    public void setLiteral(char c) {
        this.content.append(c);
    }

    public void setLiteral(float f) {
        this.content.append(f);
    }

    void checkNoPattern(PdfTemplate pdfTemplate) {
        if (pdfTemplate.getType() == 3) {
            throw new RuntimeException("Invalid use of a pattern. A template was expected.");
        }
    }

    public void drawRadioField(float f, float f2, float f3, float f4, boolean bl) {
        float f5;
        if (f > f3) {
            f5 = f;
            f = f3;
            f3 = f5;
        }
        if (f2 > f4) {
            f5 = f2;
            f2 = f4;
            f4 = f5;
        }
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(192, 192, 192));
        this.arc(f + 1.0f, f2 + 1.0f, f3 - 1.0f, f4 - 1.0f, 0.0f, 360.0f);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(160, 160, 160));
        this.arc(f + 0.5f, f2 + 0.5f, f3 - 0.5f, f4 - 0.5f, 45.0f, 180.0f);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(0, 0, 0));
        this.arc(f + 1.5f, f2 + 1.5f, f3 - 1.5f, f4 - 1.5f, 45.0f, 180.0f);
        this.stroke();
        if (bl) {
            this.setLineWidth(1.0f);
            this.setLineCap(1);
            this.setColorFill(new Color(0, 0, 0));
            this.arc(f + 4.0f, f2 + 4.0f, f3 - 4.0f, f4 - 4.0f, 0.0f, 360.0f);
            this.fill();
        }
    }

    public void drawTextField(float f, float f2, float f3, float f4) {
        float f5;
        if (f > f3) {
            f5 = f;
            f = f3;
            f3 = f5;
        }
        if (f2 > f4) {
            f5 = f2;
            f2 = f4;
            f4 = f5;
        }
        this.setColorStroke(new Color(192, 192, 192));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.rectangle(f, f2, f3 - f, f4 - f2);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.setColorFill(new Color(255, 255, 255));
        this.rectangle(f + 0.5f, f2 + 0.5f, f3 - f - 1.0f, f4 - f2 - 1.0f);
        this.fill();
        this.setColorStroke(new Color(192, 192, 192));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(f + 1.0f, f2 + 1.5f);
        this.lineTo(f3 - 1.5f, f2 + 1.5f);
        this.lineTo(f3 - 1.5f, f4 - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(160, 160, 160));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(f + 1.0f, f2 + 1.0f);
        this.lineTo(f + 1.0f, f4 - 1.0f);
        this.lineTo(f3 - 1.0f, f4 - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(0, 0, 0));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(f + 2.0f, f2 + 2.0f);
        this.lineTo(f + 2.0f, f4 - 2.0f);
        this.lineTo(f3 - 2.0f, f4 - 2.0f);
        this.stroke();
    }

    public void drawButton(float f, float f2, float f3, float f4, String string, BaseFont baseFont, float f5) {
        float f6;
        if (f > f3) {
            f6 = f;
            f = f3;
            f3 = f6;
        }
        if (f2 > f4) {
            f6 = f2;
            f2 = f4;
            f4 = f6;
        }
        this.setColorStroke(new Color(0, 0, 0));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.rectangle(f, f2, f3 - f, f4 - f2);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.setColorFill(new Color(192, 192, 192));
        this.rectangle(f + 0.5f, f2 + 0.5f, f3 - f - 1.0f, f4 - f2 - 1.0f);
        this.fill();
        this.setColorStroke(new Color(255, 255, 255));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(f + 1.0f, f2 + 1.0f);
        this.lineTo(f + 1.0f, f4 - 1.0f);
        this.lineTo(f3 - 1.0f, f4 - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(160, 160, 160));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(f + 1.0f, f2 + 1.0f);
        this.lineTo(f3 - 1.0f, f2 + 1.0f);
        this.lineTo(f3 - 1.0f, f4 - 1.0f);
        this.stroke();
        this.resetRGBColorFill();
        this.beginText();
        this.setFontAndSize(baseFont, f5);
        this.showTextAligned(1, string, f + (f3 - f) / 2.0f, f2 + (f4 - f2 - f5) / 2.0f, 0.0f);
        this.endText();
    }

    public Graphics2D createGraphicsShapes(float f, float f2) {
        return new PdfGraphics2D(this, f, f2, null, true, false, 0.0f);
    }

    public Graphics2D createPrinterGraphicsShapes(float f, float f2, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, f, f2, null, true, false, 0.0f, printerJob);
    }

    public Graphics2D createGraphics(float f, float f2) {
        return new PdfGraphics2D(this, f, f2, null, false, false, 0.0f);
    }

    public Graphics2D createPrinterGraphics(float f, float f2, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, f, f2, null, false, false, 0.0f, printerJob);
    }

    public Graphics2D createGraphics(float f, float f2, boolean bl, float f3) {
        return new PdfGraphics2D(this, f, f2, null, false, bl, f3);
    }

    public Graphics2D createPrinterGraphics(float f, float f2, boolean bl, float f3, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, f, f2, null, false, bl, f3, printerJob);
    }

    public Graphics2D createGraphicsShapes(float f, float f2, boolean bl, float f3) {
        return new PdfGraphics2D(this, f, f2, null, true, bl, f3);
    }

    public Graphics2D createPrinterGraphicsShapes(float f, float f2, boolean bl, float f3, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, f, f2, null, true, bl, f3, printerJob);
    }

    public Graphics2D createGraphics(float f, float f2, FontMapper fontMapper) {
        return new PdfGraphics2D(this, f, f2, fontMapper, false, false, 0.0f);
    }

    public Graphics2D createPrinterGraphics(float f, float f2, FontMapper fontMapper, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, f, f2, fontMapper, false, false, 0.0f, printerJob);
    }

    public Graphics2D createGraphics(float f, float f2, FontMapper fontMapper, boolean bl, float f3) {
        return new PdfGraphics2D(this, f, f2, fontMapper, false, bl, f3);
    }

    public Graphics2D createPrinterGraphics(float f, float f2, FontMapper fontMapper, boolean bl, float f3, PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, f, f2, fontMapper, false, bl, f3, printerJob);
    }

    PageResources getPageResources() {
        return this.pdf.getPageResources();
    }

    public void setGState(PdfGState pdfGState) {
        PdfObject[] arrpdfObject = this.writer.addSimpleExtGState(pdfGState);
        PageResources pageResources = this.getPageResources();
        PdfName pdfName = pageResources.addExtGState((PdfName)arrpdfObject[0], (PdfIndirectReference)arrpdfObject[1]);
        this.content.append(pdfName.getBytes()).append(" gs").append_i(this.separator);
    }

    public void beginLayer(PdfOCG pdfOCG) {
        if (pdfOCG instanceof PdfLayer && ((PdfLayer)pdfOCG).getTitle() != null) {
            throw new IllegalArgumentException("A title is not a layer");
        }
        if (this.layerDepth == null) {
            this.layerDepth = new ArrayList();
        }
        if (pdfOCG instanceof PdfLayerMembership) {
            this.layerDepth.add(new Integer(1));
            this.beginLayer2(pdfOCG);
            return;
        }
        int n = 0;
        for (PdfLayer pdfLayer = (PdfLayer)pdfOCG; pdfLayer != null; pdfLayer = pdfLayer.getParent()) {
            if (pdfLayer.getTitle() != null) continue;
            this.beginLayer2(pdfLayer);
            ++n;
        }
        this.layerDepth.add(new Integer(n));
    }

    private void beginLayer2(PdfOCG pdfOCG) {
        PdfName pdfName = (PdfName)this.writer.addSimpleProperty(pdfOCG, pdfOCG.getRef())[0];
        PageResources pageResources = this.getPageResources();
        pdfName = pageResources.addProperty(pdfName, pdfOCG.getRef());
        this.content.append("/OC ").append(pdfName.getBytes()).append(" BDC").append_i(this.separator);
    }

    public void endLayer() {
        int n = 1;
        if (this.layerDepth != null && !this.layerDepth.isEmpty()) {
            n = (Integer)this.layerDepth.get(this.layerDepth.size() - 1);
            this.layerDepth.remove(this.layerDepth.size() - 1);
        }
        while (n-- > 0) {
            this.content.append("EMC").append_i(this.separator);
        }
    }

    public void transform(AffineTransform affineTransform) {
        double[] arrd = new double[6];
        affineTransform.getMatrix(arrd);
        this.content.append(arrd[0]).append(' ').append(arrd[1]).append(' ').append(arrd[2]).append(' ');
        this.content.append(arrd[3]).append(' ').append(arrd[4]).append(' ').append(arrd[5]).append(" cm").append_i(this.separator);
    }

    void addAnnotation(PdfAnnotation pdfAnnotation) {
        this.writer.addAnnotation(pdfAnnotation);
    }

    public void setDefaultColorspace(PdfName pdfName, PdfObject pdfObject) {
        PageResources pageResources = this.getPageResources();
        pageResources.addDefaultColor(pdfName, pdfObject);
    }

    public void beginMarkedContentSequence(PdfStructureElement pdfStructureElement) {
        PdfObject pdfObject = pdfStructureElement.get(PdfName.K);
        int n = this.pdf.getMarkPoint();
        if (pdfObject != null) {
            PdfArray pdfArray = null;
            if (pdfObject.isNumber()) {
                pdfArray = new PdfArray();
                pdfArray.add(pdfObject);
                pdfStructureElement.put(PdfName.K, pdfArray);
            } else if (pdfObject.isArray()) {
                pdfArray = (PdfArray)pdfObject;
                if (!((PdfObject)pdfArray.getArrayList().get(0)).isNumber()) {
                    throw new IllegalArgumentException("The structure has kids.");
                }
            } else {
                throw new IllegalArgumentException("Unknown object at /K " + pdfObject.getClass().toString());
            }
            PdfDictionary pdfDictionary = new PdfDictionary(PdfName.MCR);
            pdfDictionary.put(PdfName.PG, this.writer.getCurrentPage());
            pdfDictionary.put(PdfName.MCID, new PdfNumber(n));
            pdfArray.add(pdfDictionary);
            pdfStructureElement.setPageMark(this.writer.getPageNumber() - 1, -1);
        } else {
            pdfStructureElement.setPageMark(this.writer.getPageNumber() - 1, n);
            pdfStructureElement.put(PdfName.PG, this.writer.getCurrentPage());
        }
        this.pdf.incMarkPoint();
        this.content.append(pdfStructureElement.get(PdfName.S).getBytes()).append(" <</MCID ").append(n).append(">> BDC").append_i(this.separator);
    }

    public void endMarkedContentSequence() {
        this.content.append("EMC").append_i(this.separator);
    }

    public void beginMarkedContentSequence(PdfName pdfName, PdfDictionary pdfDictionary, boolean bl) {
        if (pdfDictionary == null) {
            this.content.append(pdfName.getBytes()).append(" BMC").append_i(this.separator);
            return;
        }
        this.content.append(pdfName.getBytes()).append(' ');
        if (bl) {
            try {
                pdfDictionary.toPdf(this.writer, this.content);
            }
            catch (Exception var4_4) {
                throw new ExceptionConverter(var4_4);
            }
        } else {
            PdfObject[] arrpdfObject = this.writer.propertyExists(pdfDictionary) ? this.writer.addSimpleProperty(pdfDictionary, null) : this.writer.addSimpleProperty(pdfDictionary, this.writer.getPdfIndirectReference());
            PdfName pdfName2 = (PdfName)arrpdfObject[0];
            PageResources pageResources = this.getPageResources();
            pdfName2 = pageResources.addProperty(pdfName2, (PdfIndirectReference)arrpdfObject[1]);
            this.content.append(pdfName2.getBytes());
        }
        this.content.append(" BDC").append_i(this.separator);
    }

    public void beginMarkedContentSequence(PdfName pdfName) {
        this.beginMarkedContentSequence(pdfName, null, false);
    }

    static {
        abrev.put(PdfName.BITSPERCOMPONENT, "/BPC ");
        abrev.put(PdfName.COLORSPACE, "/CS ");
        abrev.put(PdfName.DECODE, "/D ");
        abrev.put(PdfName.DECODEPARMS, "/DP ");
        abrev.put(PdfName.FILTER, "/F ");
        abrev.put(PdfName.HEIGHT, "/H ");
        abrev.put(PdfName.IMAGEMASK, "/IM ");
        abrev.put(PdfName.INTENT, "/Intent ");
        abrev.put(PdfName.INTERPOLATE, "/I ");
        abrev.put(PdfName.WIDTH, "/W ");
    }

    static class GraphicState {
        FontDetails fontDetails;
        ColorDetails colorDetails;
        float size;
        protected float xTLM = 0.0f;
        protected float yTLM = 0.0f;
        protected float leading = 0.0f;
        protected float scale = 100.0f;
        protected float charSpace = 0.0f;
        protected float wordSpace = 0.0f;

        GraphicState() {
        }

        GraphicState(GraphicState graphicState) {
            this.fontDetails = graphicState.fontDetails;
            this.colorDetails = graphicState.colorDetails;
            this.size = graphicState.size;
            this.xTLM = graphicState.xTLM;
            this.yTLM = graphicState.yTLM;
            this.leading = graphicState.leading;
            this.scale = graphicState.scale;
            this.charSpace = graphicState.charSpace;
            this.wordSpace = graphicState.wordSpace;
        }
    }

}

