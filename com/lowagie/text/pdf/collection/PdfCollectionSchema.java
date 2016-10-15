/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.collection.PdfCollectionField;

public class PdfCollectionSchema
extends PdfDictionary {
    public PdfCollectionSchema() {
        super(PdfName.COLLECTIONSCHEMA);
    }

    public void addField(String string, PdfCollectionField pdfCollectionField) {
        this.put(new PdfName(string), pdfCollectionField);
    }
}

