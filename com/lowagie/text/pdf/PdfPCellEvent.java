/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;

public interface PdfPCellEvent {
    public void cellLayout(PdfPCell var1, Rectangle var2, PdfContentByte[] var3);
}

