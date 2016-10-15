/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;

public class PdfDestination
extends PdfArray {
    public static final int XYZ = 0;
    public static final int FIT = 1;
    public static final int FITH = 2;
    public static final int FITV = 3;
    public static final int FITR = 4;
    public static final int FITB = 5;
    public static final int FITBH = 6;
    public static final int FITBV = 7;
    private boolean status = false;

    public PdfDestination(int n) {
        if (n == 5) {
            this.add(PdfName.FITB);
        } else {
            this.add(PdfName.FIT);
        }
    }

    public PdfDestination(int n, float f) {
        super(new PdfNumber(f));
        switch (n) {
            default: {
                this.addFirst(PdfName.FITH);
                break;
            }
            case 3: {
                this.addFirst(PdfName.FITV);
                break;
            }
            case 6: {
                this.addFirst(PdfName.FITBH);
                break;
            }
            case 7: {
                this.addFirst(PdfName.FITBV);
            }
        }
    }

    public PdfDestination(int n, float f, float f2, float f3) {
        super(PdfName.XYZ);
        if (f < 0.0f) {
            this.add(PdfNull.PDFNULL);
        } else {
            this.add(new PdfNumber(f));
        }
        if (f2 < 0.0f) {
            this.add(PdfNull.PDFNULL);
        } else {
            this.add(new PdfNumber(f2));
        }
        this.add(new PdfNumber(f3));
    }

    public PdfDestination(int n, float f, float f2, float f3, float f4) {
        super(PdfName.FITR);
        this.add(new PdfNumber(f));
        this.add(new PdfNumber(f2));
        this.add(new PdfNumber(f3));
        this.add(new PdfNumber(f4));
    }

    public boolean hasPage() {
        return this.status;
    }

    public boolean addPage(PdfIndirectReference pdfIndirectReference) {
        if (!this.status) {
            this.addFirst(pdfIndirectReference);
            this.status = true;
            return true;
        }
        return false;
    }
}

