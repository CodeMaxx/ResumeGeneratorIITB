/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfFunction;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

public class PdfSpotColor {
    protected float tint;
    public PdfName name;
    public Color altcs;

    public PdfSpotColor(String string, float f, Color color) {
        this.name = new PdfName(string);
        this.tint = f;
        this.altcs = color;
    }

    public float getTint() {
        return this.tint;
    }

    public Color getAlternativeCS() {
        return this.altcs;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    protected PdfObject getSpotObject(PdfWriter var1_1) {
        var2_2 = new PdfArray(PdfName.SEPARATION);
        var2_2.add(this.name);
        var3_3 = null;
        if (!(this.altcs instanceof ExtendedColor)) ** GOTO lbl18
        var4_4 = ((ExtendedColor)this.altcs).type;
        switch (var4_4) {
            case 1: {
                var2_2.add(PdfName.DEVICEGRAY);
                var3_3 = PdfFunction.type2(var1_1, new float[]{0.0f, 1.0f}, null, new float[]{0.0f}, new float[]{((GrayColor)this.altcs).getGray()}, 1.0f);
                ** GOTO lbl20
            }
            case 2: {
                var2_2.add(PdfName.DEVICECMYK);
                var5_5 = (CMYKColor)this.altcs;
                var3_3 = PdfFunction.type2(var1_1, new float[]{0.0f, 1.0f}, null, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{var5_5.getCyan(), var5_5.getMagenta(), var5_5.getYellow(), var5_5.getBlack()}, 1.0f);
                ** GOTO lbl20
            }
            default: {
                throw new RuntimeException("Only RGB, Gray and CMYK are supported as alternative color spaces.");
            }
        }
lbl18: // 1 sources:
        var2_2.add(PdfName.DEVICERGB);
        var3_3 = PdfFunction.type2(var1_1, new float[]{0.0f, 1.0f}, null, new float[]{1.0f, 1.0f, 1.0f}, new float[]{(float)this.altcs.getRed() / 255.0f, (float)this.altcs.getGreen() / 255.0f, (float)this.altcs.getBlue() / 255.0f}, 1.0f);
lbl20: // 3 sources:
        var2_2.add(var3_3.getReference());
        return var2_2;
    }
}

