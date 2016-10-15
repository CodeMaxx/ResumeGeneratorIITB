/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseField;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

public class PushbuttonField
extends BaseField {
    public static final int LAYOUT_LABEL_ONLY = 1;
    public static final int LAYOUT_ICON_ONLY = 2;
    public static final int LAYOUT_ICON_TOP_LABEL_BOTTOM = 3;
    public static final int LAYOUT_LABEL_TOP_ICON_BOTTOM = 4;
    public static final int LAYOUT_ICON_LEFT_LABEL_RIGHT = 5;
    public static final int LAYOUT_LABEL_LEFT_ICON_RIGHT = 6;
    public static final int LAYOUT_LABEL_OVER_ICON = 7;
    public static final int SCALE_ICON_ALWAYS = 1;
    public static final int SCALE_ICON_NEVER = 2;
    public static final int SCALE_ICON_IS_TOO_BIG = 3;
    public static final int SCALE_ICON_IS_TOO_SMALL = 4;
    private int layout = 1;
    private Image image;
    private PdfTemplate template;
    private int scaleIcon = 1;
    private boolean proportionalIcon = true;
    private float iconVerticalAdjustment = 0.5f;
    private float iconHorizontalAdjustment = 0.5f;
    private boolean iconFitToBounds;
    private PdfTemplate tp;
    private PRIndirectReference iconReference;

    public PushbuttonField(PdfWriter pdfWriter, Rectangle rectangle, String string) {
        super(pdfWriter, rectangle, string);
    }

    public int getLayout() {
        return this.layout;
    }

    public void setLayout(int n) {
        if (n < 1 || n > 7) {
            throw new IllegalArgumentException("Layout out of bounds.");
        }
        this.layout = n;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
        this.template = null;
    }

    public PdfTemplate getTemplate() {
        return this.template;
    }

    public void setTemplate(PdfTemplate pdfTemplate) {
        this.template = pdfTemplate;
        this.image = null;
    }

    public int getScaleIcon() {
        return this.scaleIcon;
    }

    public void setScaleIcon(int n) {
        if (n < 1 || n > 4) {
            n = 1;
        }
        this.scaleIcon = n;
    }

    public boolean isProportionalIcon() {
        return this.proportionalIcon;
    }

    public void setProportionalIcon(boolean bl) {
        this.proportionalIcon = bl;
    }

    public float getIconVerticalAdjustment() {
        return this.iconVerticalAdjustment;
    }

    public void setIconVerticalAdjustment(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        this.iconVerticalAdjustment = f;
    }

    public float getIconHorizontalAdjustment() {
        return this.iconHorizontalAdjustment;
    }

    public void setIconHorizontalAdjustment(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        this.iconHorizontalAdjustment = f;
    }

    private float calculateFontSize(float f, float f2) throws IOException, DocumentException {
        BaseFont baseFont = this.getRealFont();
        float f3 = this.fontSize;
        if (f3 == 0.0f) {
            float f4 = baseFont.getWidthPoint(this.text, 1.0f);
            f3 = f4 == 0.0f ? 12.0f : f / f4;
            float f5 = f2 / (1.0f - baseFont.getFontDescriptor(3, 1.0f));
            if ((f3 = Math.min(f3, f5)) < 4.0f) {
                f3 = 4.0f;
            }
        }
        return f3;
    }

    public PdfAppearance getAppearance() throws IOException, DocumentException {
        float f;
        PdfAppearance pdfAppearance = this.getBorderAppearance();
        Rectangle rectangle = new Rectangle(pdfAppearance.getBoundingBox());
        if ((this.text == null || this.text.length() == 0) && (this.layout == 1 || this.image == null && this.template == null && this.iconReference == null)) {
            return pdfAppearance;
        }
        if (this.layout == 2 && this.image == null && this.template == null && this.iconReference == null) {
            return pdfAppearance;
        }
        BaseFont baseFont = this.getRealFont();
        boolean bl = this.borderStyle == 2 || this.borderStyle == 3;
        float f2 = rectangle.getHeight() - this.borderWidth * 2.0f;
        float f3 = this.borderWidth;
        if (bl) {
            f2 -= this.borderWidth * 2.0f;
            f3 *= 2.0f;
        }
        float f4 = bl ? 2.0f * this.borderWidth : this.borderWidth;
        f4 = Math.max(f4, 1.0f);
        float f5 = Math.min(f3, f4);
        this.tp = null;
        float f6 = Float.NaN;
        float f7 = 0.0f;
        float f8 = this.fontSize;
        float f9 = rectangle.getWidth() - 2.0f * f5 - 2.0f;
        float f10 = rectangle.getHeight() - 2.0f * f5;
        float f11 = this.iconFitToBounds ? 0.0f : f5 + 1.0f;
        int n = this.layout;
        if (this.image == null && this.template == null && this.iconReference == null) {
            n = 1;
        }
        Rectangle rectangle2 = null;
        block18 : do {
            switch (n) {
                float f12;
                case 1: 
                case 7: {
                    if (this.text != null && this.text.length() > 0 && f9 > 0.0f && f10 > 0.0f) {
                        f8 = this.calculateFontSize(f9, f10);
                        f6 = (rectangle.getWidth() - baseFont.getWidthPoint(this.text, f8)) / 2.0f;
                        f7 = (rectangle.getHeight() - baseFont.getFontDescriptor(1, f8)) / 2.0f;
                    }
                }
                case 2: {
                    if (n != 7 && n != 2) break block18;
                    rectangle2 = new Rectangle(rectangle.getLeft() + f11, rectangle.getBottom() + f11, rectangle.getRight() - f11, rectangle.getTop() - f11);
                    break block18;
                }
                case 3: {
                    if (this.text == null || this.text.length() == 0 || f9 <= 0.0f || f10 <= 0.0f) {
                        n = 2;
                        continue block18;
                    }
                    f12 = rectangle.getHeight() * 0.35f - f5;
                    f8 = f12 > 0.0f ? this.calculateFontSize(f9, f12) : 4.0f;
                    f6 = (rectangle.getWidth() - baseFont.getWidthPoint(this.text, f8)) / 2.0f;
                    f7 = f5 - baseFont.getFontDescriptor(3, f8);
                    rectangle2 = new Rectangle(rectangle.getLeft() + f11, f7 + f8, rectangle.getRight() - f11, rectangle.getTop() - f11);
                    break block18;
                }
                case 4: {
                    if (this.text == null || this.text.length() == 0 || f9 <= 0.0f || f10 <= 0.0f) {
                        n = 2;
                        continue block18;
                    }
                    f12 = rectangle.getHeight() * 0.35f - f5;
                    f8 = f12 > 0.0f ? this.calculateFontSize(f9, f12) : 4.0f;
                    f6 = (rectangle.getWidth() - baseFont.getWidthPoint(this.text, f8)) / 2.0f;
                    f7 = rectangle.getHeight() - f5 - f8;
                    if (f7 < f5) {
                        f7 = f5;
                    }
                    rectangle2 = new Rectangle(rectangle.getLeft() + f11, rectangle.getBottom() + f11, rectangle.getRight() - f11, f7 + baseFont.getFontDescriptor(3, f8));
                    break block18;
                }
                case 6: {
                    if (this.text == null || this.text.length() == 0 || f9 <= 0.0f || f10 <= 0.0f) {
                        n = 2;
                        continue block18;
                    }
                    f = rectangle.getWidth() * 0.35f - f5;
                    f8 = f > 0.0f ? this.calculateFontSize(f9, f) : 4.0f;
                    if (baseFont.getWidthPoint(this.text, f8) >= f9) {
                        n = 1;
                        f8 = this.fontSize;
                        continue block18;
                    }
                    f6 = f5 + 1.0f;
                    f7 = (rectangle.getHeight() - baseFont.getFontDescriptor(1, f8)) / 2.0f;
                    rectangle2 = new Rectangle(f6 + baseFont.getWidthPoint(this.text, f8), rectangle.getBottom() + f11, rectangle.getRight() - f11, rectangle.getTop() - f11);
                    break block18;
                }
                case 5: {
                    if (this.text == null || this.text.length() == 0 || f9 <= 0.0f || f10 <= 0.0f) {
                        n = 2;
                        continue block18;
                    }
                    f = rectangle.getWidth() * 0.35f - f5;
                    f8 = f > 0.0f ? this.calculateFontSize(f9, f) : 4.0f;
                    if (baseFont.getWidthPoint(this.text, f8) >= f9) {
                        n = 1;
                        f8 = this.fontSize;
                        continue block18;
                    }
                    f6 = rectangle.getWidth() - baseFont.getWidthPoint(this.text, f8) - f5 - 1.0f;
                    f7 = (rectangle.getHeight() - baseFont.getFontDescriptor(1, f8)) / 2.0f;
                    rectangle2 = new Rectangle(rectangle.getLeft() + f11, rectangle.getBottom() + f11, f6 - 1.0f, rectangle.getTop() - f11);
                }
            }
            break;
        } while (true);
        if (f7 < rectangle.getBottom() + f5) {
            f7 = rectangle.getBottom() + f5;
        }
        if (rectangle2 != null && (rectangle2.getWidth() <= 0.0f || rectangle2.getHeight() <= 0.0f)) {
            rectangle2 = null;
        }
        boolean bl2 = false;
        f = 0.0f;
        float f13 = 0.0f;
        PdfArray pdfArray = null;
        if (rectangle2 != null) {
            PdfDictionary pdfDictionary;
            if (this.image != null) {
                this.tp = new PdfTemplate(this.writer);
                this.tp.setBoundingBox(new Rectangle(this.image));
                this.writer.addDirectTemplateSimple(this.tp, PdfName.FRM);
                this.tp.addImage(this.image, this.image.getWidth(), 0.0f, 0.0f, this.image.getHeight(), 0.0f, 0.0f);
                bl2 = true;
                f = this.tp.getBoundingBox().getWidth();
                f13 = this.tp.getBoundingBox().getHeight();
            } else if (this.template != null) {
                this.tp = new PdfTemplate(this.writer);
                this.tp.setBoundingBox(new Rectangle(this.template.getWidth(), this.template.getHeight()));
                this.writer.addDirectTemplateSimple(this.tp, PdfName.FRM);
                this.tp.addTemplate(this.template, this.template.getBoundingBox().getLeft(), this.template.getBoundingBox().getBottom());
                bl2 = true;
                f = this.tp.getBoundingBox().getWidth();
                f13 = this.tp.getBoundingBox().getHeight();
            } else if (this.iconReference != null && (pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.iconReference)) != null) {
                Rectangle rectangle3 = PdfReader.getNormalizedRectangle((PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.BBOX)));
                pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.MATRIX));
                bl2 = true;
                f = rectangle3.getWidth();
                f13 = rectangle3.getHeight();
            }
        }
        if (bl2) {
            float f14 = rectangle2.getWidth() / f;
            float f15 = rectangle2.getHeight() / f13;
            if (this.proportionalIcon) {
                switch (this.scaleIcon) {
                    case 3: {
                        f14 = Math.min(f14, f15);
                        f14 = Math.min(f14, 1.0f);
                        break;
                    }
                    case 4: {
                        f14 = Math.min(f14, f15);
                        f14 = Math.max(f14, 1.0f);
                        break;
                    }
                    case 2: {
                        f14 = 1.0f;
                        break;
                    }
                    default: {
                        f14 = Math.min(f14, f15);
                    }
                }
                f15 = f14;
            } else {
                switch (this.scaleIcon) {
                    case 3: {
                        f14 = Math.min(f14, 1.0f);
                        f15 = Math.min(f15, 1.0f);
                        break;
                    }
                    case 4: {
                        f14 = Math.max(f14, 1.0f);
                        f15 = Math.max(f15, 1.0f);
                        break;
                    }
                    case 2: {
                        f15 = 1.0f;
                        f14 = 1.0f;
                        break;
                    }
                }
            }
            float f16 = rectangle2.getLeft() + (rectangle2.getWidth() - f * f14) * this.iconHorizontalAdjustment;
            float f17 = rectangle2.getBottom() + (rectangle2.getHeight() - f13 * f15) * this.iconVerticalAdjustment;
            pdfAppearance.saveState();
            pdfAppearance.rectangle(rectangle2.getLeft(), rectangle2.getBottom(), rectangle2.getWidth(), rectangle2.getHeight());
            pdfAppearance.clip();
            pdfAppearance.newPath();
            if (this.tp != null) {
                pdfAppearance.addTemplate(this.tp, f14, 0.0f, 0.0f, f15, f16, f17);
            } else {
                float f18 = 0.0f;
                float f19 = 0.0f;
                if (pdfArray != null && pdfArray.size() == 6) {
                    PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject((PdfObject)pdfArray.getArrayList().get(4));
                    if (pdfNumber != null) {
                        f18 = pdfNumber.floatValue();
                    }
                    if ((pdfNumber = (PdfNumber)PdfReader.getPdfObject((PdfObject)pdfArray.getArrayList().get(5))) != null) {
                        f19 = pdfNumber.floatValue();
                    }
                }
                pdfAppearance.addTemplateReference(this.iconReference, PdfName.FRM, f14, 0.0f, 0.0f, f15, f16 - f18 * f14, f17 - f19 * f15);
            }
            pdfAppearance.restoreState();
        }
        if (!Float.isNaN(f6)) {
            pdfAppearance.saveState();
            pdfAppearance.rectangle(f5, f5, rectangle.getWidth() - 2.0f * f5, rectangle.getHeight() - 2.0f * f5);
            pdfAppearance.clip();
            pdfAppearance.newPath();
            if (this.textColor == null) {
                pdfAppearance.resetGrayFill();
            } else {
                pdfAppearance.setColorFill(this.textColor);
            }
            pdfAppearance.beginText();
            pdfAppearance.setFontAndSize(baseFont, f8);
            pdfAppearance.setTextMatrix(f6, f7);
            pdfAppearance.showText(this.text);
            pdfAppearance.endText();
            pdfAppearance.restoreState();
        }
        return pdfAppearance;
    }

    public PdfFormField getField() throws IOException, DocumentException {
        PdfFormField pdfFormField = PdfFormField.createPushButton(this.writer);
        pdfFormField.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (this.fieldName != null) {
            pdfFormField.setFieldName(this.fieldName);
            if ((this.options & 1) != 0) {
                pdfFormField.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                pdfFormField.setFieldFlags(2);
            }
        }
        if (this.text != null) {
            pdfFormField.setMKNormalCaption(this.text);
        }
        if (this.rotation != 0) {
            pdfFormField.setMKRotation(this.rotation);
        }
        pdfFormField.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        PdfAppearance pdfAppearance = this.getAppearance();
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pdfAppearance);
        PdfAppearance pdfAppearance2 = (PdfAppearance)pdfAppearance.getDuplicate();
        pdfAppearance2.setFontAndSize(this.getRealFont(), this.fontSize);
        if (this.textColor == null) {
            pdfAppearance2.setGrayFill(0.0f);
        } else {
            pdfAppearance2.setColorFill(this.textColor);
        }
        pdfFormField.setDefaultAppearanceString(pdfAppearance2);
        if (this.borderColor != null) {
            pdfFormField.setMKBorderColor(this.borderColor);
        }
        if (this.backgroundColor != null) {
            pdfFormField.setMKBackgroundColor(this.backgroundColor);
        }
        switch (this.visibility) {
            case 1: {
                pdfFormField.setFlags(6);
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                pdfFormField.setFlags(36);
                break;
            }
            default: {
                pdfFormField.setFlags(4);
            }
        }
        if (this.tp != null) {
            pdfFormField.setMKNormalIcon(this.tp);
        }
        pdfFormField.setMKTextPosition(this.layout - 1);
        PdfName pdfName = PdfName.A;
        if (this.scaleIcon == 3) {
            pdfName = PdfName.B;
        } else if (this.scaleIcon == 4) {
            pdfName = PdfName.S;
        } else if (this.scaleIcon == 2) {
            pdfName = PdfName.N;
        }
        pdfFormField.setMKIconFit(pdfName, this.proportionalIcon ? PdfName.P : PdfName.A, this.iconHorizontalAdjustment, this.iconVerticalAdjustment, this.iconFitToBounds);
        return pdfFormField;
    }

    public boolean isIconFitToBounds() {
        return this.iconFitToBounds;
    }

    public void setIconFitToBounds(boolean bl) {
        this.iconFitToBounds = bl;
    }

    public PRIndirectReference getIconReference() {
        return this.iconReference;
    }

    public void setIconReference(PRIndirectReference pRIndirectReference) {
        this.iconReference = pRIndirectReference;
    }
}

