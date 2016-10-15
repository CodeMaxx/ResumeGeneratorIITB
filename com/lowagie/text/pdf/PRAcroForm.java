/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PRAcroForm
extends PdfDictionary {
    ArrayList fields;
    ArrayList stack;
    HashMap fieldByName;
    PdfReader reader;

    public PRAcroForm(PdfReader pdfReader) {
        this.reader = pdfReader;
        this.fields = new ArrayList();
        this.fieldByName = new HashMap();
        this.stack = new ArrayList();
    }

    public int size() {
        return this.fields.size();
    }

    public ArrayList getFields() {
        return this.fields;
    }

    public FieldInformation getField(String string) {
        return (FieldInformation)this.fieldByName.get(string);
    }

    public PRIndirectReference getRefByName(String string) {
        FieldInformation fieldInformation = (FieldInformation)this.fieldByName.get(string);
        if (fieldInformation == null) {
            return null;
        }
        return fieldInformation.getRef();
    }

    public void readAcroForm(PdfDictionary pdfDictionary) {
        if (pdfDictionary == null) {
            return;
        }
        this.hashMap = pdfDictionary.hashMap;
        this.pushAttrib(pdfDictionary);
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.FIELDS));
        this.iterateFields(pdfArray, null, null);
    }

    protected void iterateFields(PdfArray pdfArray, PRIndirectReference pRIndirectReference, String string) {
        Iterator iterator = pdfArray.getArrayList().iterator();
        while (iterator.hasNext()) {
            PdfArray pdfArray2;
            boolean bl;
            PRIndirectReference pRIndirectReference2 = (PRIndirectReference)iterator.next();
            PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pRIndirectReference2);
            PRIndirectReference pRIndirectReference3 = pRIndirectReference;
            String string2 = string;
            PdfString pdfString = (PdfString)pdfDictionary.get(PdfName.T);
            boolean bl2 = bl = pdfString != null;
            if (bl) {
                pRIndirectReference3 = pRIndirectReference2;
                string2 = string == null ? pdfString.toString() : string + '.' + pdfString.toString();
            }
            if ((pdfArray2 = (PdfArray)pdfDictionary.get(PdfName.KIDS)) != null) {
                this.pushAttrib(pdfDictionary);
                this.iterateFields(pdfArray2, pRIndirectReference3, string2);
                this.stack.remove(this.stack.size() - 1);
                continue;
            }
            if (pRIndirectReference3 == null) continue;
            PdfDictionary pdfDictionary2 = (PdfDictionary)this.stack.get(this.stack.size() - 1);
            if (bl) {
                pdfDictionary2 = this.mergeAttrib(pdfDictionary2, pdfDictionary);
            }
            pdfDictionary2.put(PdfName.T, new PdfString(string2));
            FieldInformation fieldInformation = new FieldInformation(string2, pdfDictionary2, pRIndirectReference3);
            this.fields.add(fieldInformation);
            this.fieldByName.put(string2, fieldInformation);
        }
    }

    protected PdfDictionary mergeAttrib(PdfDictionary pdfDictionary, PdfDictionary pdfDictionary2) {
        PdfDictionary pdfDictionary3 = new PdfDictionary();
        if (pdfDictionary != null) {
            pdfDictionary3.putAll(pdfDictionary);
        }
        Iterator iterator = pdfDictionary2.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            if (!pdfName.equals(PdfName.DR) && !pdfName.equals(PdfName.DA) && !pdfName.equals(PdfName.Q) && !pdfName.equals(PdfName.FF) && !pdfName.equals(PdfName.DV) && !pdfName.equals(PdfName.V) && !pdfName.equals(PdfName.FT) && !pdfName.equals(PdfName.F)) continue;
            pdfDictionary3.put(pdfName, pdfDictionary2.get(pdfName));
        }
        return pdfDictionary3;
    }

    protected void pushAttrib(PdfDictionary pdfDictionary) {
        PdfDictionary pdfDictionary2 = null;
        if (!this.stack.isEmpty()) {
            pdfDictionary2 = (PdfDictionary)this.stack.get(this.stack.size() - 1);
        }
        pdfDictionary2 = this.mergeAttrib(pdfDictionary2, pdfDictionary);
        this.stack.add(pdfDictionary2);
    }

    public static class FieldInformation {
        String name;
        PdfDictionary info;
        PRIndirectReference ref;

        FieldInformation(String string, PdfDictionary pdfDictionary, PRIndirectReference pRIndirectReference) {
            this.name = string;
            this.info = pdfDictionary;
            this.ref = pRIndirectReference;
        }

        public String getName() {
            return this.name;
        }

        public PdfDictionary getInfo() {
            return this.info;
        }

        public PRIndirectReference getRef() {
            return this.ref;
        }
    }

}

