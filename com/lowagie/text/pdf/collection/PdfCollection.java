/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.collection.PdfCollectionSchema;
import com.lowagie.text.pdf.collection.PdfCollectionSort;

public class PdfCollection
extends PdfDictionary {
    public static final int DETAILS = 0;
    public static final int TILE = 1;
    public static final int HIDDEN = 2;

    public PdfCollection(int n) {
        super(PdfName.COLLECTION);
        switch (n) {
            case 1: {
                this.put(PdfName.VIEW, PdfName.T);
                break;
            }
            case 2: {
                this.put(PdfName.VIEW, PdfName.H);
                break;
            }
            default: {
                this.put(PdfName.VIEW, PdfName.D);
            }
        }
    }

    public void setInitialDocument(String string) {
        this.put(PdfName.D, new PdfString(string, null));
    }

    public void setSchema(PdfCollectionSchema pdfCollectionSchema) {
        this.put(PdfName.SCHEMA, pdfCollectionSchema);
    }

    public PdfCollectionSchema getSchema() {
        return (PdfCollectionSchema)this.get(PdfName.SCHEMA);
    }

    public void setSort(PdfCollectionSort pdfCollectionSort) {
        this.put(PdfName.SORT, pdfCollectionSort);
    }
}

