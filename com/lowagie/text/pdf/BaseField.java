/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfCopyFieldsImp;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class BaseField {
    public static final float BORDER_WIDTH_THIN = 1.0f;
    public static final float BORDER_WIDTH_MEDIUM = 2.0f;
    public static final float BORDER_WIDTH_THICK = 3.0f;
    public static final int VISIBLE = 0;
    public static final int HIDDEN = 1;
    public static final int VISIBLE_BUT_DOES_NOT_PRINT = 2;
    public static final int HIDDEN_BUT_PRINTABLE = 3;
    public static final int READ_ONLY = 1;
    public static final int REQUIRED = 2;
    public static final int MULTILINE = 4096;
    public static final int DO_NOT_SCROLL = 8388608;
    public static final int PASSWORD = 8192;
    public static final int FILE_SELECTION = 1048576;
    public static final int DO_NOT_SPELL_CHECK = 4194304;
    public static final int EDIT = 262144;
    public static final int COMB = 16777216;
    protected float borderWidth = 1.0f;
    protected int borderStyle = 0;
    protected Color borderColor;
    protected Color backgroundColor;
    protected Color textColor;
    protected BaseFont font;
    protected float fontSize = 0.0f;
    protected int alignment = 0;
    protected PdfWriter writer;
    protected String text;
    protected Rectangle box;
    protected int rotation = 0;
    protected int visibility;
    protected String fieldName;
    protected int options;
    protected int maxCharacterLength;
    private static final HashMap fieldKeys = new HashMap();

    public BaseField(PdfWriter pdfWriter, Rectangle rectangle, String string) {
        this.writer = pdfWriter;
        this.setBox(rectangle);
        this.fieldName = string;
    }

    protected BaseFont getRealFont() throws IOException, DocumentException {
        if (this.font == null) {
            return BaseFont.createFont("Helvetica", "Cp1252", false);
        }
        return this.font;
    }

    protected PdfAppearance getBorderAppearance() {
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
        pdfAppearance.saveState();
        if (this.backgroundColor != null) {
            pdfAppearance.setColorFill(this.backgroundColor);
            pdfAppearance.rectangle(0.0f, 0.0f, this.box.getWidth(), this.box.getHeight());
            pdfAppearance.fill();
        }
        if (this.borderStyle == 4) {
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                pdfAppearance.setColorStroke(this.borderColor);
                pdfAppearance.setLineWidth(this.borderWidth);
                pdfAppearance.moveTo(0.0f, this.borderWidth / 2.0f);
                pdfAppearance.lineTo(this.box.getWidth(), this.borderWidth / 2.0f);
                pdfAppearance.stroke();
            }
        } else if (this.borderStyle == 2) {
            Color color;
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                pdfAppearance.setColorStroke(this.borderColor);
                pdfAppearance.setLineWidth(this.borderWidth);
                pdfAppearance.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
                pdfAppearance.stroke();
            }
            if ((color = this.backgroundColor) == null) {
                color = Color.white;
            }
            pdfAppearance.setGrayFill(1.0f);
            this.drawTopFrame(pdfAppearance);
            pdfAppearance.setColorFill(color.darker());
            this.drawBottomFrame(pdfAppearance);
        } else if (this.borderStyle == 3) {
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                pdfAppearance.setColorStroke(this.borderColor);
                pdfAppearance.setLineWidth(this.borderWidth);
                pdfAppearance.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
                pdfAppearance.stroke();
            }
            pdfAppearance.setGrayFill(0.5f);
            this.drawTopFrame(pdfAppearance);
            pdfAppearance.setGrayFill(0.75f);
            this.drawBottomFrame(pdfAppearance);
        } else if (this.borderWidth != 0.0f && this.borderColor != null) {
            if (this.borderStyle == 1) {
                pdfAppearance.setLineDash(3.0f, 0.0f);
            }
            pdfAppearance.setColorStroke(this.borderColor);
            pdfAppearance.setLineWidth(this.borderWidth);
            pdfAppearance.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
            pdfAppearance.stroke();
            if ((this.options & 16777216) != 0 && this.maxCharacterLength > 1) {
                float f = this.box.getWidth() / (float)this.maxCharacterLength;
                float f2 = this.borderWidth / 2.0f;
                float f3 = this.box.getHeight() - this.borderWidth / 2.0f;
                for (int i = 1; i < this.maxCharacterLength; ++i) {
                    float f4 = f * (float)i;
                    pdfAppearance.moveTo(f4, f2);
                    pdfAppearance.lineTo(f4, f3);
                }
                pdfAppearance.stroke();
            }
        }
        pdfAppearance.restoreState();
        return pdfAppearance;
    }

    protected static ArrayList getHardBreaks(String string) {
        ArrayList<String> arrayList = new ArrayList<String>();
        char[] arrc = string.toCharArray();
        int n = arrc.length;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < n; ++i) {
            char c = arrc[i];
            if (c == '\r') {
                if (i + 1 < n && arrc[i + 1] == '\n') {
                    ++i;
                }
                arrayList.add(stringBuffer.toString());
                stringBuffer = new StringBuffer();
                continue;
            }
            if (c == '\n') {
                arrayList.add(stringBuffer.toString());
                stringBuffer = new StringBuffer();
                continue;
            }
            stringBuffer.append(c);
        }
        arrayList.add(stringBuffer.toString());
        return arrayList;
    }

    protected static void trimRight(StringBuffer stringBuffer) {
        int n = stringBuffer.length();
        while (n != 0) {
            if (stringBuffer.charAt(--n) != ' ') {
                return;
            }
            stringBuffer.setLength(n);
        }
        return;
    }

    protected static ArrayList breakLines(ArrayList arrayList, BaseFont baseFont, float f, float f2) {
        ArrayList<String> arrayList2 = new ArrayList<String>();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayList.size(); ++i) {
            stringBuffer.setLength(0);
            float f3 = 0.0f;
            char[] arrc = ((String)arrayList.get(i)).toCharArray();
            int n = arrc.length;
            int n2 = 0;
            int n3 = -1;
            char c = '\u0000';
            int n4 = 0;
            block6 : for (int j = 0; j < n; ++j) {
                c = arrc[j];
                switch (n2) {
                    case 0: {
                        stringBuffer.append(c);
                        if ((f3 += baseFont.getWidthPoint(c, f)) > f2) {
                            f3 = 0.0f;
                            if (stringBuffer.length() > 1) {
                                --j;
                                stringBuffer.setLength(stringBuffer.length() - 1);
                            }
                            arrayList2.add(stringBuffer.toString());
                            stringBuffer.setLength(0);
                            n4 = j;
                            if (c == ' ') {
                                n2 = 2;
                                continue block6;
                            }
                            n2 = 1;
                            continue block6;
                        }
                        if (c == ' ') continue block6;
                        n2 = 1;
                        continue block6;
                    }
                    case 1: {
                        f3 += baseFont.getWidthPoint(c, f);
                        stringBuffer.append(c);
                        if (c == ' ') {
                            n3 = j;
                        }
                        if (f3 <= f2) continue block6;
                        f3 = 0.0f;
                        if (n3 >= 0) {
                            j = n3;
                            stringBuffer.setLength(n3 - n4);
                            BaseField.trimRight(stringBuffer);
                            arrayList2.add(stringBuffer.toString());
                            stringBuffer.setLength(0);
                            n4 = j;
                            n3 = -1;
                            n2 = 2;
                            continue block6;
                        }
                        if (stringBuffer.length() > 1) {
                            --j;
                            stringBuffer.setLength(stringBuffer.length() - 1);
                        }
                        arrayList2.add(stringBuffer.toString());
                        stringBuffer.setLength(0);
                        n4 = j;
                        if (c != ' ') continue block6;
                        n2 = 2;
                        continue block6;
                    }
                    case 2: {
                        if (c == ' ') continue block6;
                        f3 = 0.0f;
                        --j;
                        n2 = 1;
                    }
                }
            }
            BaseField.trimRight(stringBuffer);
            arrayList2.add(stringBuffer.toString());
        }
        return arrayList2;
    }

    private void drawTopFrame(PdfAppearance pdfAppearance) {
        pdfAppearance.moveTo(this.borderWidth, this.borderWidth);
        pdfAppearance.lineTo(this.borderWidth, this.box.getHeight() - this.borderWidth);
        pdfAppearance.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        pdfAppearance.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        pdfAppearance.lineTo(2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        pdfAppearance.lineTo(2.0f * this.borderWidth, 2.0f * this.borderWidth);
        pdfAppearance.lineTo(this.borderWidth, this.borderWidth);
        pdfAppearance.fill();
    }

    private void drawBottomFrame(PdfAppearance pdfAppearance) {
        pdfAppearance.moveTo(this.borderWidth, this.borderWidth);
        pdfAppearance.lineTo(this.box.getWidth() - this.borderWidth, this.borderWidth);
        pdfAppearance.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        pdfAppearance.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        pdfAppearance.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, 2.0f * this.borderWidth);
        pdfAppearance.lineTo(2.0f * this.borderWidth, 2.0f * this.borderWidth);
        pdfAppearance.lineTo(this.borderWidth, this.borderWidth);
        pdfAppearance.fill();
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(float f) {
        this.borderWidth = f;
    }

    public int getBorderStyle() {
        return this.borderStyle;
    }

    public void setBorderStyle(int n) {
        this.borderStyle = n;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public Color getTextColor() {
        return this.textColor;
    }

    public void setTextColor(Color color) {
        this.textColor = color;
    }

    public BaseFont getFont() {
        return this.font;
    }

    public void setFont(BaseFont baseFont) {
        this.font = baseFont;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float f) {
        this.fontSize = f;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int n) {
        this.alignment = n;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String string) {
        this.text = string;
    }

    public Rectangle getBox() {
        return this.box;
    }

    public void setBox(Rectangle rectangle) {
        if (rectangle == null) {
            this.box = null;
        } else {
            this.box = new Rectangle(rectangle);
            this.box.normalize();
        }
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int n) {
        if (n % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be a multiple of 90.");
        }
        if ((n %= 360) < 0) {
            n += 360;
        }
        this.rotation = n;
    }

    public void setRotationFromPage(Rectangle rectangle) {
        this.setRotation(rectangle.getRotation());
    }

    public int getVisibility() {
        return this.visibility;
    }

    public void setVisibility(int n) {
        this.visibility = n;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String string) {
        this.fieldName = string;
    }

    public int getOptions() {
        return this.options;
    }

    public void setOptions(int n) {
        this.options = n;
    }

    public int getMaxCharacterLength() {
        return this.maxCharacterLength;
    }

    public void setMaxCharacterLength(int n) {
        this.maxCharacterLength = n;
    }

    public PdfWriter getWriter() {
        return this.writer;
    }

    public void setWriter(PdfWriter pdfWriter) {
        this.writer = pdfWriter;
    }

    public static void moveFields(PdfDictionary pdfDictionary, PdfDictionary pdfDictionary2) {
        Iterator iterator = pdfDictionary.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            if (!fieldKeys.containsKey(pdfName)) continue;
            if (pdfDictionary2 != null) {
                pdfDictionary2.put(pdfName, pdfDictionary.get(pdfName));
            }
            iterator.remove();
        }
    }

    static {
        fieldKeys.putAll(PdfCopyFieldsImp.fieldKeys);
        fieldKeys.put(PdfName.T, new Integer(1));
    }
}

