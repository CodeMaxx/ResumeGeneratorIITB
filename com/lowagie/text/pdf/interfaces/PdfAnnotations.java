/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfFormField;

public interface PdfAnnotations {
    public PdfAcroForm getAcroForm();

    public void addAnnotation(PdfAnnotation var1);

    public void addCalculationOrder(PdfFormField var1);

    public void setSigFlags(int var1);
}

