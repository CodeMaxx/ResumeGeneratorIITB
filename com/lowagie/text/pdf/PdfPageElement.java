/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfIndirectReference;

interface PdfPageElement {
    public void setParent(PdfIndirectReference var1);

    public boolean isParent();
}

