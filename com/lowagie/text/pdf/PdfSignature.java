/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;

public class PdfSignature
extends PdfDictionary {
    public PdfSignature(PdfName pdfName, PdfName pdfName2) {
        super(PdfName.SIG);
        this.put(PdfName.FILTER, pdfName);
        this.put(PdfName.SUBFILTER, pdfName2);
    }

    public void setByteRange(int[] arrn) {
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrn.length; ++i) {
            pdfArray.add(new PdfNumber(arrn[i]));
        }
        this.put(PdfName.BYTERANGE, pdfArray);
    }

    public void setContents(byte[] arrby) {
        this.put(PdfName.CONTENTS, new PdfString(arrby).setHexWriting(true));
    }

    public void setCert(byte[] arrby) {
        this.put(PdfName.CERT, new PdfString(arrby));
    }

    public void setName(String string) {
        this.put(PdfName.NAME, new PdfString(string, "UnicodeBig"));
    }

    public void setDate(PdfDate pdfDate) {
        this.put(PdfName.M, pdfDate);
    }

    public void setLocation(String string) {
        this.put(PdfName.LOCATION, new PdfString(string, "UnicodeBig"));
    }

    public void setReason(String string) {
        this.put(PdfName.REASON, new PdfString(string, "UnicodeBig"));
    }

    public void setContact(String string) {
        this.put(PdfName.CONTACTINFO, new PdfString(string, "UnicodeBig"));
    }
}

