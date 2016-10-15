/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

public class PdfCollectionSort
extends PdfDictionary {
    public PdfCollectionSort(String string) {
        super(PdfName.COLLECTIONSORT);
        this.put(PdfName.S, new PdfName(string));
    }

    public PdfCollectionSort(String[] arrstring) {
        super(PdfName.COLLECTIONSORT);
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrstring.length; ++i) {
            pdfArray.add(new PdfName(arrstring[i]));
        }
        this.put(PdfName.S, pdfArray);
    }

    public void setSortOrder(boolean bl) {
        PdfObject pdfObject = this.get(PdfName.S);
        if (!(pdfObject instanceof PdfName)) {
            throw new IllegalArgumentException("You have to define a boolean array for this collection sort dictionary.");
        }
        this.put(PdfName.A, new PdfBoolean(bl));
    }

    public void setSortOrder(boolean[] arrbl) {
        PdfArray pdfArray;
        PdfObject pdfObject = this.get(PdfName.S);
        if (pdfObject instanceof PdfArray) {
            if (((PdfArray)pdfObject).size() != arrbl.length) {
                throw new IllegalArgumentException("The number of booleans in this array doesn't correspond with the number of fields.");
            }
            pdfArray = new PdfArray();
            for (int i = 0; i < arrbl.length; ++i) {
                pdfArray.add(new PdfBoolean(arrbl[i]));
            }
        } else {
            throw new IllegalArgumentException("You need a single boolean for this collection sort dictionary.");
        }
        this.put(PdfName.A, pdfArray);
    }
}

