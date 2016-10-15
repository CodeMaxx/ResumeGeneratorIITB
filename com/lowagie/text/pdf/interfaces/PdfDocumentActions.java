/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfName;

public interface PdfDocumentActions {
    public void setOpenAction(String var1);

    public void setOpenAction(PdfAction var1);

    public void setAdditionalAction(PdfName var1, PdfAction var2) throws DocumentException;
}

