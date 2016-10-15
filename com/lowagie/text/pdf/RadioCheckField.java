/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseField;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;

public class RadioCheckField
extends BaseField {
    public static final int TYPE_CHECK = 1;
    public static final int TYPE_CIRCLE = 2;
    public static final int TYPE_CROSS = 3;
    public static final int TYPE_DIAMOND = 4;
    public static final int TYPE_SQUARE = 5;
    public static final int TYPE_STAR = 6;
    private static String[] typeChars = new String[]{"4", "l", "8", "u", "n", "H"};
    private int checkType;
    private String onValue;
    private boolean checked;

    public RadioCheckField(PdfWriter pdfWriter, Rectangle rectangle, String string, String string2) {
        super(pdfWriter, rectangle, string);
        this.setOnValue(string2);
        this.setCheckType(2);
    }

    public int getCheckType() {
        return this.checkType;
    }

    public void setCheckType(int n) {
        if (n < 1 || n > 6) {
            n = 2;
        }
        this.checkType = n;
        this.setText(typeChars[n - 1]);
        try {
            this.setFont(BaseFont.createFont("ZapfDingbats", "Cp1252", false));
        }
        catch (Exception var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    public String getOnValue() {
        return this.onValue;
    }

    public void setOnValue(String string) {
        this.onValue = string;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean bl) {
        this.checked = bl;
    }

    public PdfAppearance getAppearance(boolean bl, boolean bl2) throws IOException, DocumentException {
        if (bl && this.checkType == 2) {
            return this.getAppearanceRadioCircle(bl2);
        }
        PdfAppearance pdfAppearance = this.getBorderAppearance();
        if (!bl2) {
            return pdfAppearance;
        }
        BaseFont baseFont = this.getRealFont();
        boolean bl3 = this.borderStyle == 2 || this.borderStyle == 3;
        float f = this.box.getHeight() - this.borderWidth * 2.0f;
        float f2 = this.borderWidth;
        if (bl3) {
            f -= this.borderWidth * 2.0f;
            f2 *= 2.0f;
        }
        float f3 = bl3 ? 2.0f * this.borderWidth : this.borderWidth;
        f3 = Math.max(f3, 1.0f);
        float f4 = Math.min(f2, f3);
        float f5 = this.box.getWidth() - 2.0f * f4;
        float f6 = this.box.getHeight() - 2.0f * f4;
        float f7 = this.fontSize;
        if (f7 == 0.0f) {
            float f8 = baseFont.getWidthPoint(this.text, 1.0f);
            f7 = f8 == 0.0f ? 12.0f : f5 / f8;
            float f9 = f / baseFont.getFontDescriptor(1, 1.0f);
            f7 = Math.min(f7, f9);
        }
        pdfAppearance.saveState();
        pdfAppearance.rectangle(f4, f4, f5, f6);
        pdfAppearance.clip();
        pdfAppearance.newPath();
        if (this.textColor == null) {
            pdfAppearance.resetGrayFill();
        } else {
            pdfAppearance.setColorFill(this.textColor);
        }
        pdfAppearance.beginText();
        pdfAppearance.setFontAndSize(baseFont, f7);
        pdfAppearance.setTextMatrix((this.box.getWidth() - baseFont.getWidthPoint(this.text, f7)) / 2.0f, (this.box.getHeight() - baseFont.getAscentPoint(this.text, f7)) / 2.0f);
        pdfAppearance.showText(this.text);
        pdfAppearance.endText();
        pdfAppearance.restoreState();
        return pdfAppearance;
    }

    public PdfAppearance getAppearanceRadioCircle(boolean bl) {
        PdfAppearance pdfAppearance = PdfAppearance.createAppearance(this.writer, this.box.getWidth(), this.box.getHeight());
        switch (this.rotation) {
            case 90: {
                pdfAppearance.setMatrix(0.0f, 1.0f, -1.0f, 0.0f, this.box.getHeight(), 0.0f);
                break;
            }
            case 180: {
                pdfAppearance.setMatrix(-1.0f, 0.0f, 0.0f, -1.0f, this.box.getWidth(), this.box.getHeight());
                break;
            }
            case 270: {
                pdfAppearance.setMatrix(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, this.box.getWidth());
            }
        }
        Rectangle rectangle = new Rectangle(pdfAppearance.getBoundingBox());
        float f = rectangle.getWidth() / 2.0f;
        float f2 = rectangle.getHeight() / 2.0f;
        float f3 = (Math.min(rectangle.getWidth(), rectangle.getHeight()) - this.borderWidth) / 2.0f;
        if (f3 <= 0.0f) {
            return pdfAppearance;
        }
        if (this.backgroundColor != null) {
            pdfAppearance.setColorFill(this.backgroundColor);
            pdfAppearance.circle(f, f2, f3 + this.borderWidth / 2.0f);
            pdfAppearance.fill();
        }
        if (this.borderWidth > 0.0f && this.borderColor != null) {
            pdfAppearance.setLineWidth(this.borderWidth);
            pdfAppearance.setColorStroke(this.borderColor);
            pdfAppearance.circle(f, f2, f3);
            pdfAppearance.stroke();
        }
        if (bl) {
            if (this.textColor == null) {
                pdfAppearance.resetGrayFill();
            } else {
                pdfAppearance.setColorFill(this.textColor);
            }
            pdfAppearance.circle(f, f2, f3 / 2.0f);
            pdfAppearance.fill();
        }
        return pdfAppearance;
    }

    public PdfFormField getRadioGroup(boolean bl, boolean bl2) {
        PdfFormField pdfFormField = PdfFormField.createRadioButton(this.writer, bl);
        if (bl2) {
            pdfFormField.setFieldFlags(33554432);
        }
        pdfFormField.setFieldName(this.fieldName);
        if ((this.options & 1) != 0) {
            pdfFormField.setFieldFlags(1);
        }
        if ((this.options & 2) != 0) {
            pdfFormField.setFieldFlags(2);
        }
        pdfFormField.setValueAsName(this.checked ? this.onValue : "Off");
        return pdfFormField;
    }

    public PdfFormField getRadioField() throws IOException, DocumentException {
        return this.getField(true);
    }

    public PdfFormField getCheckField() throws IOException, DocumentException {
        return this.getField(false);
    }

    protected PdfFormField getField(boolean bl) throws IOException, DocumentException {
        PdfFormField pdfFormField = null;
        pdfFormField = bl ? PdfFormField.createEmpty(this.writer) : PdfFormField.createCheckBox(this.writer);
        pdfFormField.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (!bl) {
            pdfFormField.setFieldName(this.fieldName);
            if ((this.options & 1) != 0) {
                pdfFormField.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                pdfFormField.setFieldFlags(2);
            }
            pdfFormField.setValueAsName(this.checked ? this.onValue : "Off");
        }
        if (this.text != null) {
            pdfFormField.setMKNormalCaption(this.text);
        }
        if (this.rotation != 0) {
            pdfFormField.setMKRotation(this.rotation);
        }
        pdfFormField.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        PdfAppearance pdfAppearance = this.getAppearance(bl, true);
        PdfAppearance pdfAppearance2 = this.getAppearance(bl, false);
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, this.onValue, pdfAppearance);
        pdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", pdfAppearance2);
        pdfFormField.setAppearanceState(this.checked ? this.onValue : "Off");
        PdfAppearance pdfAppearance3 = (PdfAppearance)pdfAppearance.getDuplicate();
        pdfAppearance3.setFontAndSize(this.getRealFont(), this.fontSize);
        if (this.textColor == null) {
            pdfAppearance3.setGrayFill(0.0f);
        } else {
            pdfAppearance3.setColorFill(this.textColor);
        }
        pdfFormField.setDefaultAppearanceString(pdfAppearance3);
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
        return pdfFormField;
    }
}

