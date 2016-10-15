/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import java.util.Calendar;

public class PdfCollectionField
extends PdfDictionary {
    public static final int TEXT = 0;
    public static final int DATE = 1;
    public static final int NUMBER = 2;
    public static final int FILENAME = 3;
    public static final int DESC = 4;
    public static final int MODDATE = 5;
    public static final int CREATIONDATE = 6;
    public static final int SIZE = 7;
    protected int fieldType;

    public PdfCollectionField(String string, int n) {
        super(PdfName.COLLECTIONFIELD);
        this.put(PdfName.N, new PdfString(string, "UnicodeBig"));
        this.fieldType = n;
        switch (n) {
            default: {
                this.put(PdfName.SUBTYPE, PdfName.S);
                break;
            }
            case 1: {
                this.put(PdfName.SUBTYPE, PdfName.D);
                break;
            }
            case 2: {
                this.put(PdfName.SUBTYPE, PdfName.N);
                break;
            }
            case 3: {
                this.put(PdfName.SUBTYPE, PdfName.F);
                break;
            }
            case 4: {
                this.put(PdfName.SUBTYPE, PdfName.DESC);
                break;
            }
            case 5: {
                this.put(PdfName.SUBTYPE, PdfName.MODDATE);
                break;
            }
            case 6: {
                this.put(PdfName.SUBTYPE, PdfName.CREATIONDATE);
                break;
            }
            case 7: {
                this.put(PdfName.SUBTYPE, PdfName.SIZE);
            }
        }
    }

    public void setOrder(int n) {
        this.put(PdfName.O, new PdfNumber(n));
    }

    public void setVisible(boolean bl) {
        this.put(PdfName.V, new PdfBoolean(bl));
    }

    public void setEditable(boolean bl) {
        this.put(PdfName.E, new PdfBoolean(bl));
    }

    public boolean isCollectionItem() {
        switch (this.fieldType) {
            case 0: 
            case 1: 
            case 2: {
                return true;
            }
        }
        return false;
    }

    public PdfObject getValue(String string) {
        switch (this.fieldType) {
            case 0: {
                return new PdfString(string, "UnicodeBig");
            }
            case 1: {
                return new PdfDate(PdfDate.decode(string));
            }
            case 2: {
                return new PdfNumber(string);
            }
        }
        throw new IllegalArgumentException(string + " is not an acceptable value for the field " + this.get(PdfName.N).toString());
    }
}

