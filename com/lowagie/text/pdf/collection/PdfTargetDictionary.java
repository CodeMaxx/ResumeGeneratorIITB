/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;

public class PdfTargetDictionary
extends PdfDictionary {
    public PdfTargetDictionary(PdfTargetDictionary pdfTargetDictionary) {
        this.put(PdfName.R, PdfName.P);
        if (pdfTargetDictionary != null) {
            this.setAdditionalPath(pdfTargetDictionary);
        }
    }

    public PdfTargetDictionary(boolean bl) {
        if (bl) {
            this.put(PdfName.R, PdfName.C);
        } else {
            this.put(PdfName.R, PdfName.P);
        }
    }

    public void setEmbeddedFileName(String string) {
        this.put(PdfName.N, new PdfString(string, null));
    }

    public void setFileAttachmentPagename(String string) {
        this.put(PdfName.P, new PdfString(string, null));
    }

    public void setFileAttachmentPage(int n) {
        this.put(PdfName.P, new PdfNumber(n));
    }

    public void setFileAttachmentName(String string) {
        this.put(PdfName.A, new PdfString(string, "UnicodeBig"));
    }

    public void setFileAttachmentIndex(int n) {
        this.put(PdfName.A, new PdfNumber(n));
    }

    public void setAdditionalPath(PdfTargetDictionary pdfTargetDictionary) {
        this.put(PdfName.T, pdfTargetDictionary);
    }
}

