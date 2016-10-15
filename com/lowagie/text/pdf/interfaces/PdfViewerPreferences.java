/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

public interface PdfViewerPreferences {
    public void setViewerPreferences(int var1);

    public void addViewerPreference(PdfName var1, PdfObject var2);
}

