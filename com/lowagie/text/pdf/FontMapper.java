/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BaseFont;
import java.awt.Font;

public interface FontMapper {
    public BaseFont awtToPdf(Font var1);

    public Font pdfToAwt(BaseFont var1, int var2);
}

