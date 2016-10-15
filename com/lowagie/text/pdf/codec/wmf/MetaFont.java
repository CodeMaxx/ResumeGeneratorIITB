/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.codec.wmf.InputMeta;
import com.lowagie.text.pdf.codec.wmf.MetaObject;
import com.lowagie.text.pdf.codec.wmf.MetaState;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MetaFont
extends MetaObject {
    static final String[] fontNames = new String[]{"Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique", "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique", "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic", "Symbol", "ZapfDingbats"};
    static final int MARKER_BOLD = 1;
    static final int MARKER_ITALIC = 2;
    static final int MARKER_COURIER = 0;
    static final int MARKER_HELVETICA = 4;
    static final int MARKER_TIMES = 8;
    static final int MARKER_SYMBOL = 12;
    static final int DEFAULT_PITCH = 0;
    static final int FIXED_PITCH = 1;
    static final int VARIABLE_PITCH = 2;
    static final int FF_DONTCARE = 0;
    static final int FF_ROMAN = 1;
    static final int FF_SWISS = 2;
    static final int FF_MODERN = 3;
    static final int FF_SCRIPT = 4;
    static final int FF_DECORATIVE = 5;
    static final int BOLDTHRESHOLD = 600;
    static final int nameSize = 32;
    static final int ETO_OPAQUE = 2;
    static final int ETO_CLIPPED = 4;
    int height;
    float angle;
    int bold;
    int italic;
    boolean underline;
    boolean strikeout;
    int charset;
    int pitchAndFamily;
    String faceName = "arial";
    BaseFont font = null;

    public MetaFont() {
        this.type = 3;
    }

    public void init(InputMeta inputMeta) throws IOException {
        int n;
        int n2;
        this.height = Math.abs(inputMeta.readShort());
        inputMeta.skip(2);
        this.angle = (float)((double)inputMeta.readShort() / 1800.0 * 3.141592653589793);
        inputMeta.skip(2);
        this.bold = inputMeta.readShort() >= 600 ? 1 : 0;
        this.italic = inputMeta.readByte() != 0 ? 2 : 0;
        this.underline = inputMeta.readByte() != 0;
        this.strikeout = inputMeta.readByte() != 0;
        this.charset = inputMeta.readByte();
        inputMeta.skip(3);
        this.pitchAndFamily = inputMeta.readByte();
        byte[] arrby = new byte[32];
        for (n = 0; n < 32 && (n2 = inputMeta.readByte()) != 0; ++n) {
            arrby[n] = (byte)n2;
        }
        try {
            this.faceName = new String(arrby, 0, n, "Cp1252");
        }
        catch (UnsupportedEncodingException var4_5) {
            this.faceName = new String(arrby, 0, n);
        }
        this.faceName = this.faceName.toLowerCase();
    }

    public BaseFont getFont() {
        String string;
        if (this.font != null) {
            return this.font;
        }
        Font font = FontFactory.getFont(this.faceName, "Cp1252", true, 10.0f, (this.italic != 0 ? 2 : 0) | (this.bold != 0 ? 1 : 0));
        this.font = font.getBaseFont();
        if (this.font != null) {
            return this.font;
        }
        if (this.faceName.indexOf("courier") != -1 || this.faceName.indexOf("terminal") != -1 || this.faceName.indexOf("fixedsys") != -1) {
            string = fontNames[0 + this.italic + this.bold];
        } else if (this.faceName.indexOf("ms sans serif") != -1 || this.faceName.indexOf("arial") != -1 || this.faceName.indexOf("system") != -1) {
            string = fontNames[4 + this.italic + this.bold];
        } else if (this.faceName.indexOf("arial black") != -1) {
            string = fontNames[4 + this.italic + 1];
        } else if (this.faceName.indexOf("times") != -1 || this.faceName.indexOf("ms serif") != -1 || this.faceName.indexOf("roman") != -1) {
            string = fontNames[8 + this.italic + this.bold];
        } else if (this.faceName.indexOf("symbol") != -1) {
            string = fontNames[12];
        } else {
            int n = this.pitchAndFamily & 3;
            int n2 = this.pitchAndFamily >> 4 & 7;
            block1 : switch (n2) {
                case 3: {
                    string = fontNames[0 + this.italic + this.bold];
                    break;
                }
                case 1: {
                    string = fontNames[8 + this.italic + this.bold];
                    break;
                }
                case 2: 
                case 4: 
                case 5: {
                    string = fontNames[4 + this.italic + this.bold];
                    break;
                }
                default: {
                    switch (n) {
                        case 1: {
                            string = fontNames[0 + this.italic + this.bold];
                            break block1;
                        }
                    }
                    string = fontNames[4 + this.italic + this.bold];
                }
            }
        }
        try {
            this.font = BaseFont.createFont(string, "Cp1252", false);
        }
        catch (Exception var3_4) {
            throw new ExceptionConverter(var3_4);
        }
        return this.font;
    }

    public float getAngle() {
        return this.angle;
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public boolean isStrikeout() {
        return this.strikeout;
    }

    public float getFontSize(MetaState metaState) {
        return Math.abs(metaState.transformY(this.height) - metaState.transformY(0)) * Document.wmfFontCorrection;
    }
}

