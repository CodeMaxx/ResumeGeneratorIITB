/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

public class PdfArray
extends PdfObject {
    protected ArrayList arrayList;

    public PdfArray() {
        super(5);
        this.arrayList = new ArrayList();
    }

    public PdfArray(PdfObject pdfObject) {
        super(5);
        this.arrayList = new ArrayList();
        this.arrayList.add(pdfObject);
    }

    public PdfArray(float[] arrf) {
        super(5);
        this.arrayList = new ArrayList();
        this.add(arrf);
    }

    public PdfArray(int[] arrn) {
        super(5);
        this.arrayList = new ArrayList();
        this.add(arrn);
    }

    public PdfArray(ArrayList arrayList) {
        this();
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            this.add((PdfObject)iterator.next());
        }
    }

    public PdfArray(PdfArray pdfArray) {
        super(5);
        this.arrayList = new ArrayList(pdfArray.getArrayList());
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        PdfObject pdfObject;
        outputStream.write(91);
        Iterator iterator = this.arrayList.iterator();
        int n = 0;
        if (iterator.hasNext()) {
            pdfObject = (PdfObject)iterator.next();
            if (pdfObject == null) {
                pdfObject = PdfNull.PDFNULL;
            }
            pdfObject.toPdf(pdfWriter, outputStream);
        }
        while (iterator.hasNext()) {
            pdfObject = (PdfObject)iterator.next();
            if (pdfObject == null) {
                pdfObject = PdfNull.PDFNULL;
            }
            if ((n = pdfObject.type()) != 5 && n != 6 && n != 4 && n != 3) {
                outputStream.write(32);
            }
            pdfObject.toPdf(pdfWriter, outputStream);
        }
        outputStream.write(93);
    }

    public ArrayList getArrayList() {
        return this.arrayList;
    }

    public int size() {
        return this.arrayList.size();
    }

    public boolean add(PdfObject pdfObject) {
        return this.arrayList.add(pdfObject);
    }

    public boolean add(float[] arrf) {
        for (int i = 0; i < arrf.length; ++i) {
            this.arrayList.add(new PdfNumber(arrf[i]));
        }
        return true;
    }

    public boolean add(int[] arrn) {
        for (int i = 0; i < arrn.length; ++i) {
            this.arrayList.add(new PdfNumber(arrn[i]));
        }
        return true;
    }

    public void addFirst(PdfObject pdfObject) {
        this.arrayList.add(0, pdfObject);
    }

    public boolean contains(PdfObject pdfObject) {
        return this.arrayList.contains(pdfObject);
    }

    public ListIterator listIterator() {
        return this.arrayList.listIterator();
    }

    public String toString() {
        return this.arrayList.toString();
    }

    public PdfObject getPdfObject(int n) {
        return (PdfObject)this.arrayList.get(n);
    }

    public PdfObject getDirectObject(int n) {
        return PdfReader.getPdfObject(this.getPdfObject(n));
    }

    public PdfDictionary getAsDict(int n) {
        PdfDictionary pdfDictionary = null;
        PdfObject pdfObject = this.getDirectObject(n);
        if (pdfObject != null && pdfObject.isDictionary()) {
            pdfDictionary = (PdfDictionary)pdfObject;
        }
        return pdfDictionary;
    }

    public PdfArray getAsArray(int n) {
        PdfArray pdfArray = null;
        PdfObject pdfObject = this.getDirectObject(n);
        if (pdfObject != null && pdfObject.isArray()) {
            pdfArray = (PdfArray)pdfObject;
        }
        return pdfArray;
    }

    public PdfStream getAsStream(int n) {
        PdfStream pdfStream = null;
        PdfObject pdfObject = this.getDirectObject(n);
        if (pdfObject != null && pdfObject.isStream()) {
            pdfStream = (PdfStream)pdfObject;
        }
        return pdfStream;
    }

    public PdfString getAsString(int n) {
        PdfString pdfString = null;
        PdfObject pdfObject = this.getDirectObject(n);
        if (pdfObject != null && pdfObject.isString()) {
            pdfString = (PdfString)pdfObject;
        }
        return pdfString;
    }

    public PdfNumber getAsNumber(int n) {
        PdfNumber pdfNumber = null;
        PdfObject pdfObject = this.getDirectObject(n);
        if (pdfObject != null && pdfObject.isNumber()) {
            pdfNumber = (PdfNumber)pdfObject;
        }
        return pdfNumber;
    }

    public PdfName getAsName(int n) {
        PdfName pdfName = null;
        PdfObject pdfObject = this.getDirectObject(n);
        if (pdfObject != null && pdfObject.isName()) {
            pdfName = (PdfName)pdfObject;
        }
        return pdfName;
    }

    public PdfBoolean getAsBoolean(int n) {
        PdfBoolean pdfBoolean = null;
        PdfObject pdfObject = this.getDirectObject(n);
        if (pdfObject != null && pdfObject.isBoolean()) {
            pdfBoolean = (PdfBoolean)pdfObject;
        }
        return pdfBoolean;
    }

    public PdfIndirectReference getAsIndirectObject(int n) {
        PdfIndirectReference pdfIndirectReference = null;
        PdfObject pdfObject = this.getPdfObject(n);
        if (pdfObject != null && pdfObject.isIndirect()) {
            pdfIndirectReference = (PdfIndirectReference)pdfObject;
        }
        return pdfIndirectReference;
    }
}

