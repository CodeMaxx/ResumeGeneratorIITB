/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import java.awt.color.ICC_Profile;

public class PdfICCBased
extends PdfStream {
    public PdfICCBased(ICC_Profile iCC_Profile) {
        this(iCC_Profile, -1);
    }

    public PdfICCBased(ICC_Profile iCC_Profile, int n) {
        try {
            int n2 = iCC_Profile.getNumComponents();
            switch (n2) {
                case 1: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICEGRAY);
                    break;
                }
                case 3: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICERGB);
                    break;
                }
                case 4: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICECMYK);
                    break;
                }
                default: {
                    throw new PdfException("" + n2 + " component(s) is not supported in PDF1.4");
                }
            }
            this.put(PdfName.N, new PdfNumber(n2));
            this.bytes = iCC_Profile.getData();
            this.flateCompress(n);
        }
        catch (Exception var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }
}

