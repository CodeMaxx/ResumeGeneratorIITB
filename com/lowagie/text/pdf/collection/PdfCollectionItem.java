/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.collection.PdfCollectionField;
import com.lowagie.text.pdf.collection.PdfCollectionSchema;
import java.util.Calendar;

public class PdfCollectionItem
extends PdfDictionary {
    PdfCollectionSchema schema;

    public PdfCollectionItem(PdfCollectionSchema pdfCollectionSchema) {
        super(PdfName.COLLECTIONITEM);
        this.schema = pdfCollectionSchema;
    }

    public void addItem(String string, String string2) {
        PdfName pdfName = new PdfName(string);
        PdfCollectionField pdfCollectionField = (PdfCollectionField)this.schema.get(pdfName);
        this.put(pdfName, pdfCollectionField.getValue(string2));
    }

    public void addItem(String string, PdfString pdfString) {
        PdfName pdfName = new PdfName(string);
        PdfCollectionField pdfCollectionField = (PdfCollectionField)this.schema.get(pdfName);
        if (pdfCollectionField.fieldType == 0) {
            this.put(pdfName, pdfString);
        }
    }

    public void addItem(String string, PdfDate pdfDate) {
        PdfName pdfName = new PdfName(string);
        PdfCollectionField pdfCollectionField = (PdfCollectionField)this.schema.get(pdfName);
        if (pdfCollectionField.fieldType == 1) {
            this.put(pdfName, pdfDate);
        }
    }

    public void addItem(String string, PdfNumber pdfNumber) {
        PdfName pdfName = new PdfName(string);
        PdfCollectionField pdfCollectionField = (PdfCollectionField)this.schema.get(pdfName);
        if (pdfCollectionField.fieldType == 2) {
            this.put(pdfName, pdfNumber);
        }
    }

    public void addItem(String string, Calendar calendar) {
        this.addItem(string, new PdfDate(calendar));
    }

    public void addItem(String string, int n) {
        this.addItem(string, new PdfNumber(n));
    }

    public void addItem(String string, float f) {
        this.addItem(string, new PdfNumber(f));
    }

    public void addItem(String string, double d) {
        this.addItem(string, new PdfNumber(d));
    }

    public void setPrefix(String string, String string2) {
        PdfName pdfName = new PdfName(string);
        PdfObject pdfObject = this.get(pdfName);
        if (pdfObject == null) {
            throw new IllegalArgumentException("You must set a value before adding a prefix.");
        }
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.COLLECTIONSUBITEM);
        pdfDictionary.put(PdfName.D, pdfObject);
        pdfDictionary.put(PdfName.P, new PdfString(string2, "UnicodeBig"));
        this.put(pdfName, pdfDictionary);
    }
}

