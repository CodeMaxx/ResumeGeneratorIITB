/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.util.Collection;
import java.util.HashSet;

public class PdfLayerMembership
extends PdfDictionary
implements PdfOCG {
    public static final PdfName ALLON = new PdfName("AllOn");
    public static final PdfName ANYON = new PdfName("AnyOn");
    public static final PdfName ANYOFF = new PdfName("AnyOff");
    public static final PdfName ALLOFF = new PdfName("AllOff");
    PdfIndirectReference ref;
    PdfArray members = new PdfArray();
    HashSet layers = new HashSet();

    public PdfLayerMembership(PdfWriter pdfWriter) {
        super(PdfName.OCMD);
        this.put(PdfName.OCGS, this.members);
        this.ref = pdfWriter.getPdfIndirectReference();
    }

    public PdfIndirectReference getRef() {
        return this.ref;
    }

    public void addMember(PdfLayer pdfLayer) {
        if (!this.layers.contains(pdfLayer)) {
            this.members.add(pdfLayer.getRef());
            this.layers.add(pdfLayer);
        }
    }

    public Collection getLayers() {
        return this.layers;
    }

    public void setVisibilityPolicy(PdfName pdfName) {
        this.put(PdfName.P, pdfName);
    }

    public PdfObject getPdfObject() {
        return this;
    }
}

