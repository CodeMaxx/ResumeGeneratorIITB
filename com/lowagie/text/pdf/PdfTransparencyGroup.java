/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

public class PdfTransparencyGroup
extends PdfDictionary {
    public PdfTransparencyGroup() {
        this.put(PdfName.S, PdfName.TRANSPARENCY);
    }

    public void setIsolated(boolean bl) {
        if (bl) {
            this.put(PdfName.I, PdfBoolean.PDFTRUE);
        } else {
            this.remove(PdfName.I);
        }
    }

    public void setKnockout(boolean bl) {
        if (bl) {
            this.put(PdfName.K, PdfBoolean.PDFTRUE);
        } else {
            this.remove(PdfName.K);
        }
    }
}

