/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import java.util.HashMap;

public class PdfPage
extends PdfDictionary {
    private static final String[] boxStrings = new String[]{"crop", "trim", "art", "bleed"};
    private static final PdfName[] boxNames = new PdfName[]{PdfName.CROPBOX, PdfName.TRIMBOX, PdfName.ARTBOX, PdfName.BLEEDBOX};
    public static final PdfNumber PORTRAIT = new PdfNumber(0);
    public static final PdfNumber LANDSCAPE = new PdfNumber(90);
    public static final PdfNumber INVERTEDPORTRAIT = new PdfNumber(180);
    public static final PdfNumber SEASCAPE = new PdfNumber(270);
    PdfRectangle mediaBox;

    PdfPage(PdfRectangle pdfRectangle, HashMap hashMap, PdfDictionary pdfDictionary, int n) {
        super(PAGE);
        this.mediaBox = pdfRectangle;
        this.put(PdfName.MEDIABOX, pdfRectangle);
        this.put(PdfName.RESOURCES, pdfDictionary);
        if (n != 0) {
            this.put(PdfName.ROTATE, new PdfNumber(n));
        }
        for (int i = 0; i < boxStrings.length; ++i) {
            PdfObject pdfObject = (PdfObject)hashMap.get(boxStrings[i]);
            if (pdfObject == null) continue;
            this.put(boxNames[i], pdfObject);
        }
    }

    PdfPage(PdfRectangle pdfRectangle, HashMap hashMap, PdfDictionary pdfDictionary) {
        this(pdfRectangle, hashMap, pdfDictionary, 0);
    }

    public boolean isParent() {
        return false;
    }

    void add(PdfIndirectReference pdfIndirectReference) {
        this.put(PdfName.CONTENTS, pdfIndirectReference);
    }

    PdfRectangle rotateMediaBox() {
        this.mediaBox = this.mediaBox.rotate();
        this.put(PdfName.MEDIABOX, this.mediaBox);
        return this.mediaBox;
    }

    PdfRectangle getMediaBox() {
        return this.mediaBox;
    }
}

