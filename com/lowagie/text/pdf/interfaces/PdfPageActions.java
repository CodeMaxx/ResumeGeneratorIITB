/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfTransition;

public interface PdfPageActions {
    public void setPageAction(PdfName var1, PdfAction var2) throws DocumentException;

    public void setDuration(int var1);

    public void setTransition(PdfTransition var1);
}

