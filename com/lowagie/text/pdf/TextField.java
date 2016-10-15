/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseField;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.FontSelector;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

public class TextField
extends BaseField {
    private String defaultText;
    private String[] choices;
    private String[] choiceExports;
    private int choiceSelection;
    private int topFirst;
    private float extraMarginLeft;
    private float extraMarginTop;
    private ArrayList substitutionFonts;
    private BaseFont extensionFont;

    public TextField(PdfWriter pdfWriter, Rectangle rectangle, String string) {
        super(pdfWriter, rectangle, string);
    }

    private static boolean checkRTL(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        char[] arrc = string.toCharArray();
        for (int i = 0; i < arrc.length; ++i) {
            char c = arrc[i];
            if (c < '\u0590' || c >= '\u0780') continue;
            return true;
        }
        return false;
    }

    private static void changeFontSize(Phrase phrase, float f) {
        for (int i = 0; i < phrase.size(); ++i) {
            ((Chunk)phrase.get(i)).getFont().setSize(f);
        }
    }

    private Phrase composePhrase(String string, BaseFont baseFont, Color color, float f) {
        Phrase phrase = null;
        if (this.extensionFont == null && (this.substitutionFonts == null || this.substitutionFonts.isEmpty())) {
            phrase = new Phrase(new Chunk(string, new Font(baseFont, f, 0, color)));
        } else {
            FontSelector fontSelector = new FontSelector();
            fontSelector.addFont(new Font(baseFont, f, 0, color));
            if (this.extensionFont != null) {
                fontSelector.addFont(new Font(this.extensionFont, f, 0, color));
            }
            if (this.substitutionFonts != null) {
                for (int i = 0; i < this.substitutionFonts.size(); ++i) {
                    fontSelector.addFont(new Font((BaseFont)this.substitutionFonts.get(i), f, 0, color));
                }
            }
            phrase = fontSelector.process(string);
        }
        return phrase;
    }

    private static String removeCRLF(String string) {
        if (string.indexOf(10) >= 0 || string.indexOf(13) >= 0) {
            char[] arrc = string.toCharArray();
            StringBuffer stringBuffer = new StringBuffer(arrc.length);
            for (int i = 0; i < arrc.length; ++i) {
                char c = arrc[i];
                if (c == '\n') {
                    stringBuffer.append(' ');
                    continue;
                }
                if (c == '\r') {
                    stringBuffer.append(' ');
                    if (i >= arrc.length - 1 || arrc[i + 1] != '\n') continue;
                    ++i;
                    continue;
                }
                stringBuffer.append(c);
            }
            return stringBuffer.toString();
        }
        return string;
    }

    public PdfAppearance getAppearance() throws IOException, DocumentException {
        int n;
        PdfAppearance pdfAppearance = this.getBorderAppearance();
        pdfAppearance.beginVariableText();
        if (this.text == null || this.text.length() == 0) {
            pdfAppearance.endVariableText();
            return pdfAppearance;
        }
        BaseFont baseFont = this.getRealFont();
        boolean bl = this.borderStyle == 2 || this.borderStyle == 3;
        float f = this.box.getHeight() - this.borderWidth * 2.0f;
        float f2 = this.borderWidth;
        if (bl) {
            f -= this.borderWidth * 2.0f;
            f2 *= 2.0f;
        }
        f -= this.extraMarginTop;
        float f3 = bl ? 2.0f * this.borderWidth : this.borderWidth;
        f3 = Math.max(f3, 1.0f);
        float f4 = Math.min(f2, f3);
        pdfAppearance.saveState();
        pdfAppearance.rectangle(f4, f4, this.box.getWidth() - 2.0f * f4, this.box.getHeight() - 2.0f * f4);
        pdfAppearance.clip();
        pdfAppearance.newPath();
        Color color = this.textColor == null ? GrayColor.GRAYBLACK : this.textColor;
        String string = this.text;
        if ((this.options & 8192) != 0) {
            char[] arrc = new char[this.text.length()];
            for (int i = 0; i < this.text.length(); ++i) {
                arrc[i] = 42;
            }
            string = new String(arrc);
        }
        int n2 = n = TextField.checkRTL(string) ? 2 : 1;
        if ((this.options & 4096) == 0) {
            string = TextField.removeCRLF(this.text);
        }
        Phrase phrase = this.composePhrase(string, baseFont, color, this.fontSize);
        if ((this.options & 4096) != 0) {
            float f5;
            float f6 = this.fontSize;
            float f7 = this.box.getWidth() - 4.0f * f3 - this.extraMarginLeft;
            float f8 = baseFont.getFontDescriptor(8, 1.0f) - baseFont.getFontDescriptor(6, 1.0f);
            ColumnText columnText = new ColumnText(null);
            if (f6 == 0.0f) {
                f6 = f / f8;
                if (f6 > 4.0f) {
                    if (f6 > 12.0f) {
                        f6 = 12.0f;
                    }
                    f5 = Math.max((f6 - 4.0f) / 10.0f, 0.2f);
                    columnText.setSimpleColumn(0.0f, - f, f7, 0.0f);
                    columnText.setAlignment(this.alignment);
                    columnText.setRunDirection(n);
                    while (f6 > 4.0f) {
                        columnText.setYLine(0.0f);
                        TextField.changeFontSize(phrase, f6);
                        columnText.setText(phrase);
                        columnText.setLeading(f8 * f6);
                        int n3 = columnText.go(true);
                        if ((n3 & 2) == 0) break;
                        f6 -= f5;
                    }
                }
                if (f6 < 4.0f) {
                    f6 = 4.0f;
                }
            }
            TextField.changeFontSize(phrase, f6);
            columnText.setCanvas(pdfAppearance);
            f5 = f6 * f8;
            float f9 = f3 + f - baseFont.getFontDescriptor(8, f6);
            columnText.setSimpleColumn(this.extraMarginLeft + 2.0f * f3, -20000.0f, this.box.getWidth() - 2.0f * f3, f9 + f5);
            columnText.setLeading(f5);
            columnText.setAlignment(this.alignment);
            columnText.setRunDirection(n);
            columnText.setText(phrase);
            columnText.go();
        } else {
            float f10;
            float f11;
            float f12 = this.fontSize;
            if (f12 == 0.0f) {
                f10 = f / (baseFont.getFontDescriptor(7, 1.0f) - baseFont.getFontDescriptor(6, 1.0f));
                TextField.changeFontSize(phrase, 1.0f);
                f11 = ColumnText.getWidth(phrase, n, 0);
                f12 = f11 == 0.0f ? f10 : (this.box.getWidth() - this.extraMarginLeft - 4.0f * f3) / f11;
                if (f12 > f10) {
                    f12 = f10;
                }
                if (f12 < 4.0f) {
                    f12 = 4.0f;
                }
            }
            TextField.changeFontSize(phrase, f12);
            f10 = f4 + (this.box.getHeight() - 2.0f * f4 - baseFont.getFontDescriptor(1, f12)) / 2.0f;
            if (f10 < f4) {
                f10 = f4;
            }
            if (f10 - f4 < - baseFont.getFontDescriptor(3, f12)) {
                f11 = - baseFont.getFontDescriptor(3, f12) + f4;
                float f13 = this.box.getHeight() - f4 - baseFont.getFontDescriptor(1, f12);
                f10 = Math.min(f11, Math.max(f10, f13));
            }
            if ((this.options & 16777216) != 0 && this.maxCharacterLength > 0) {
                int n4 = Math.min(this.maxCharacterLength, string.length());
                int n5 = 0;
                if (this.alignment == 2) {
                    n5 = this.maxCharacterLength - n4;
                } else if (this.alignment == 1) {
                    n5 = (this.maxCharacterLength - n4) / 2;
                }
                float f14 = (this.box.getWidth() - this.extraMarginLeft) / (float)this.maxCharacterLength;
                float f15 = f14 / 2.0f + (float)n5 * f14;
                if (this.textColor == null) {
                    pdfAppearance.setGrayFill(0.0f);
                } else {
                    pdfAppearance.setColorFill(this.textColor);
                }
                pdfAppearance.beginText();
                for (int i = 0; i < phrase.size(); ++i) {
                    Chunk chunk = (Chunk)phrase.get(i);
                    BaseFont baseFont2 = chunk.getFont().getBaseFont();
                    pdfAppearance.setFontAndSize(baseFont2, f12);
                    StringBuffer stringBuffer = chunk.append("");
                    for (int j = 0; j < stringBuffer.length(); ++j) {
                        String string2 = stringBuffer.substring(j, j + 1);
                        float f16 = baseFont2.getWidthPoint(string2, f12);
                        pdfAppearance.setTextMatrix(this.extraMarginLeft + f15 - f16 / 2.0f, f10 - this.extraMarginTop);
                        pdfAppearance.showText(string2);
                        f15 += f14;
                    }
                }
                pdfAppearance.endText();
            } else if (this.alignment == 2) {
                ColumnText.showTextAligned(pdfAppearance, 2, phrase, this.extraMarginLeft + this.box.getWidth() - 2.0f * f3, f10 - this.extraMarginTop, 0.0f, n, 0);
            } else if (this.alignment == 1) {
                ColumnText.showTextAligned(pdfAppearance, 1, phrase, this.extraMarginLeft + this.box.getWidth() / 2.0f, f10 - this.extraMarginTop, 0.0f, n, 0);
            } else {
                ColumnText.showTextAligned(pdfAppearance, 0, phrase, this.extraMarginLeft + 2.0f * f3, f10 - this.extraMarginTop, 0.0f, n, 0);
            }
        }
        pdfAppearance.restoreState();
        pdfAppearance.endVariableText();
        return pdfAppearance;
    }

    PdfAppearance getListAppearance() throws IOException, DocumentException {
        PdfAppearance pdfAppearance = this.getBorderAppearance();
        pdfAppearance.beginVariableText();
        if (this.choices == null || this.choices.length == 0) {
            pdfAppearance.endVariableText();
            return pdfAppearance;
        }
        int n = this.choiceSelection;
        if (n >= this.choices.length) {
            n = this.choices.length - 1;
        }
        if (n < 0) {
            n = 0;
        }
        BaseFont baseFont = this.getRealFont();
        float f = this.fontSize;
        if (f == 0.0f) {
            f = 12.0f;
        }
        boolean bl = this.borderStyle == 2 || this.borderStyle == 3;
        float f2 = this.box.getHeight() - this.borderWidth * 2.0f;
        if (bl) {
            f2 -= this.borderWidth * 2.0f;
        }
        float f3 = bl ? 2.0f * this.borderWidth : this.borderWidth;
        float f4 = baseFont.getFontDescriptor(8, f) - baseFont.getFontDescriptor(6, f);
        int n2 = (int)(f2 / f4) + 1;
        int n3 = 0;
        int n4 = 0;
        n4 = n + n2 / 2 + 1;
        n3 = n4 - n2;
        if (n3 < 0) {
            n4 += n3;
            n3 = 0;
        }
        if ((n4 = n3 + n2) > this.choices.length) {
            n4 = this.choices.length;
        }
        this.topFirst = n3;
        pdfAppearance.saveState();
        pdfAppearance.rectangle(f3, f3, this.box.getWidth() - 2.0f * f3, this.box.getHeight() - 2.0f * f3);
        pdfAppearance.clip();
        pdfAppearance.newPath();
        Color color = this.textColor == null ? GrayColor.GRAYBLACK : this.textColor;
        pdfAppearance.setColorFill(new Color(10, 36, 106));
        pdfAppearance.rectangle(f3, f3 + f2 - (float)(n - n3 + 1) * f4, this.box.getWidth() - 2.0f * f3, f4);
        pdfAppearance.fill();
        float f5 = f3 * 2.0f;
        float f6 = f3 + f2 - baseFont.getFontDescriptor(8, f);
        int n5 = n3;
        while (n5 < n4) {
            String string = this.choices[n5];
            int n6 = TextField.checkRTL(string) ? 2 : 1;
            string = TextField.removeCRLF(string);
            Phrase phrase = this.composePhrase(string, baseFont, n5 == n ? GrayColor.GRAYWHITE : color, f);
            ColumnText.showTextAligned(pdfAppearance, 0, phrase, f5, f6, 0.0f, n6, 0);
            ++n5;
            f6 -= f4;
        }
        pdfAppearance.restoreState();
        pdfAppearance.endVariableText();
        return pdfAppearance;
    }

    public PdfFormField getTextField() throws IOException, DocumentException {
        if (this.maxCharacterLength <= 0) {
            this.options &= -16777217;
        }
        if ((this.options & 16777216) != 0) {
            this.options &= -4097;
        }
        PdfFormField pdfFormField = PdfFormField.createTextField(this.writer, false, false, this.maxCharacterLength);
        pdfFormField.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        switch (this.alignment) {
            case 1: {
                pdfFormField.setQuadding(1);
                break;
            }
            case 2: {
                pdfFormField.setQuadding(2);
            }
        }
        if (this.rotation != 0) {
            pdfFormField.setMKRotation(this.rotation);
        }
        if (this.fieldName != null) {
            pdfFormField.setFieldName(this.fieldName);
            if ((this.options & 2) == 0 && !"".equals(this.text)) {
                pdfFormField.setValueAsString(this.text);
            }
            if (this.defaultText != null) {
                pdfFormField.setDefaultValueAsString(this.defaultText);
            }
            if ((this.options & 1) != 0) {
                pdfFormField.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                pdfFormField.setFieldFlags(2);
            }
            if ((this.options & 4096) != 0) {
                pdfFormField.setFieldFlags(4096);
            }
            if ((this.options & 8388608) != 0) {
                pdfFormField.setFieldFlags(8388608);
            }
            if ((this.options & 8192) != 0) {
                pdfFormField.setFieldFlags(8192);
            }
            if ((this.options & 1048576) != 0) {
                pdfFormField.setFieldFlags(1048576);
            }
            if ((this.options & 4194304) != 0) {
                pdfFormField.setFieldFlags(4194304);
            }
            if ((this.options & 16777216) != 0) {
                pdfFormField.setFieldFlags(16777216);
            }
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
        return pdfFormField;
    }

    public PdfFormField getComboField() throws IOException, DocumentException {
        return this.getChoiceField(false);
    }

    public PdfFormField getListField() throws IOException, DocumentException {
        return this.getChoiceField(true);
    }

    protected PdfFormField getChoiceField(boolean bl) throws IOException, DocumentException {
        int n;
        PdfAppearance pdfAppearance;
        this.options &= -16781313;
        String[] arrstring = this.choices;
        if (arrstring == null) {
            arrstring = new String[]{};
        }
        if ((n = this.choiceSelection) >= arrstring.length) {
            n = arrstring.length - 1;
        }
        if (this.text == null) {
            this.text = "";
        }
        if (n >= 0) {
            this.text = arrstring[n];
        }
        if (n < 0) {
            n = 0;
        }
        PdfFormField pdfFormField = null;
        String[][] arrstring2 = null;
        if (this.choiceExports == null) {
            pdfFormField = bl ? PdfFormField.createList(this.writer, arrstring, n) : PdfFormField.createCombo(this.writer, (this.options & 262144) != 0, arrstring, n);
        } else {
            int n2;
            arrstring2 = new String[arrstring.length][2];
            for (n2 = 0; n2 < arrstring2.length; ++n2) {
                String string = arrstring[n2];
                arrstring2[n2][1] = string;
                arrstring2[n2][0] = string;
            }
            n2 = Math.min(arrstring.length, this.choiceExports.length);
            for (int i = 0; i < n2; ++i) {
                if (this.choiceExports[i] == null) continue;
                arrstring2[i][0] = this.choiceExports[i];
            }
            pdfFormField = bl ? PdfFormField.createList(this.writer, arrstring2, n) : PdfFormField.createCombo(this.writer, (this.options & 262144) != 0, arrstring2, n);
        }
        pdfFormField.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (this.rotation != 0) {
            pdfFormField.setMKRotation(this.rotation);
        }
        if (this.fieldName != null) {
            pdfFormField.setFieldName(this.fieldName);
            if (arrstring.length > 0) {
                if (arrstring2 != null) {
                    pdfFormField.setValueAsString(arrstring2[n][0]);
                    pdfFormField.setDefaultValueAsString(arrstring2[n][0]);
                } else {
                    pdfFormField.setValueAsString(this.text);
                    pdfFormField.setDefaultValueAsString(this.text);
                }
            }
            if ((this.options & 1) != 0) {
                pdfFormField.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                pdfFormField.setFieldFlags(2);
            }
            if ((this.options & 4194304) != 0) {
                pdfFormField.setFieldFlags(4194304);
            }
        }
        pdfFormField.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        if (bl) {
            pdfAppearance = this.getListAppearance();
            if (this.topFirst > 0) {
                pdfFormField.put(PdfName.TI, new PdfNumber(this.topFirst));
            }
        } else {
            pdfAppearance = this.getAppearance();
        }
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
        return pdfFormField;
    }

    public String getDefaultText() {
        return this.defaultText;
    }

    public void setDefaultText(String string) {
        this.defaultText = string;
    }

    public String[] getChoices() {
        return this.choices;
    }

    public void setChoices(String[] arrstring) {
        this.choices = arrstring;
    }

    public String[] getChoiceExports() {
        return this.choiceExports;
    }

    public void setChoiceExports(String[] arrstring) {
        this.choiceExports = arrstring;
    }

    public int getChoiceSelection() {
        return this.choiceSelection;
    }

    public void setChoiceSelection(int n) {
        this.choiceSelection = n;
    }

    int getTopFirst() {
        return this.topFirst;
    }

    public void setExtraMargin(float f, float f2) {
        this.extraMarginLeft = f;
        this.extraMarginTop = f2;
    }

    public ArrayList getSubstitutionFonts() {
        return this.substitutionFonts;
    }

    public void setSubstitutionFonts(ArrayList arrayList) {
        this.substitutionFonts = arrayList;
    }

    public BaseFont getExtensionFont() {
        return this.extensionFont;
    }

    public void setExtensionFont(BaseFont baseFont) {
        this.extensionFont = baseFont;
    }
}

