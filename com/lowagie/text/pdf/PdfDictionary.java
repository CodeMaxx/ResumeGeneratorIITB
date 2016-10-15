/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PdfDictionary
extends PdfObject {
    public static final PdfName FONT = PdfName.FONT;
    public static final PdfName OUTLINES = PdfName.OUTLINES;
    public static final PdfName PAGE = PdfName.PAGE;
    public static final PdfName PAGES = PdfName.PAGES;
    public static final PdfName CATALOG = PdfName.CATALOG;
    private PdfName dictionaryType = null;
    protected HashMap hashMap = new HashMap();

    public PdfDictionary() {
        super(6);
    }

    public PdfDictionary(PdfName pdfName) {
        this();
        this.dictionaryType = pdfName;
        this.put(PdfName.TYPE, this.dictionaryType);
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        outputStream.write(60);
        outputStream.write(60);
        int n = 0;
        Iterator iterator = this.hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            PdfObject pdfObject = (PdfObject)this.hashMap.get(pdfName);
            pdfName.toPdf(pdfWriter, outputStream);
            n = pdfObject.type();
            if (n != 5 && n != 6 && n != 4 && n != 3) {
                outputStream.write(32);
            }
            pdfObject.toPdf(pdfWriter, outputStream);
        }
        outputStream.write(62);
        outputStream.write(62);
    }

    public void put(PdfName pdfName, PdfObject pdfObject) {
        if (pdfObject == null || pdfObject.isNull()) {
            this.hashMap.remove(pdfName);
        } else {
            this.hashMap.put(pdfName, pdfObject);
        }
    }

    public void putEx(PdfName pdfName, PdfObject pdfObject) {
        if (pdfObject == null) {
            return;
        }
        this.put(pdfName, pdfObject);
    }

    public void remove(PdfName pdfName) {
        this.hashMap.remove(pdfName);
    }

    public PdfObject get(PdfName pdfName) {
        return (PdfObject)this.hashMap.get(pdfName);
    }

    public boolean isFont() {
        return FONT.equals(this.dictionaryType);
    }

    public boolean isPage() {
        return PAGE.equals(this.dictionaryType);
    }

    public boolean isPages() {
        return PAGES.equals(this.dictionaryType);
    }

    public boolean isCatalog() {
        return CATALOG.equals(this.dictionaryType);
    }

    public boolean isOutlineTree() {
        return OUTLINES.equals(this.dictionaryType);
    }

    public void merge(PdfDictionary pdfDictionary) {
        this.hashMap.putAll(pdfDictionary.hashMap);
    }

    public void mergeDifferent(PdfDictionary pdfDictionary) {
        Iterator iterator = pdfDictionary.hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            Object k = iterator.next();
            if (this.hashMap.containsKey(k)) continue;
            this.hashMap.put(k, pdfDictionary.hashMap.get(k));
        }
    }

    public Set getKeys() {
        return this.hashMap.keySet();
    }

    public void putAll(PdfDictionary pdfDictionary) {
        this.hashMap.putAll(pdfDictionary.hashMap);
    }

    public int size() {
        return this.hashMap.size();
    }

    public boolean contains(PdfName pdfName) {
        return this.hashMap.containsKey(pdfName);
    }

    public String toString() {
        if (this.get(PdfName.TYPE) == null) {
            return "Dictionary";
        }
        return "Dictionary of type: " + this.get(PdfName.TYPE);
    }

    public PdfObject getDirectObject(PdfName pdfName) {
        return PdfReader.getPdfObject(this.get(pdfName));
    }

    public PdfDictionary getAsDict(PdfName pdfName) {
        PdfDictionary pdfDictionary = null;
        PdfObject pdfObject = this.getDirectObject(pdfName);
        if (pdfObject != null && pdfObject.isDictionary()) {
            pdfDictionary = (PdfDictionary)pdfObject;
        }
        return pdfDictionary;
    }

    public PdfArray getAsArray(PdfName pdfName) {
        PdfArray pdfArray = null;
        PdfObject pdfObject = this.getDirectObject(pdfName);
        if (pdfObject != null && pdfObject.isArray()) {
            pdfArray = (PdfArray)pdfObject;
        }
        return pdfArray;
    }

    public PdfStream getAsStream(PdfName pdfName) {
        PdfStream pdfStream = null;
        PdfObject pdfObject = this.getDirectObject(pdfName);
        if (pdfObject != null && pdfObject.isStream()) {
            pdfStream = (PdfStream)pdfObject;
        }
        return pdfStream;
    }

    public PdfString getAsString(PdfName pdfName) {
        PdfString pdfString = null;
        PdfObject pdfObject = this.getDirectObject(pdfName);
        if (pdfObject != null && pdfObject.isString()) {
            pdfString = (PdfString)pdfObject;
        }
        return pdfString;
    }

    public PdfNumber getAsNumber(PdfName pdfName) {
        PdfNumber pdfNumber = null;
        PdfObject pdfObject = this.getDirectObject(pdfName);
        if (pdfObject != null && pdfObject.isNumber()) {
            pdfNumber = (PdfNumber)pdfObject;
        }
        return pdfNumber;
    }

    public PdfName getAsName(PdfName pdfName) {
        PdfName pdfName2 = null;
        PdfObject pdfObject = this.getDirectObject(pdfName);
        if (pdfObject != null && pdfObject.isName()) {
            pdfName2 = (PdfName)pdfObject;
        }
        return pdfName2;
    }

    public PdfBoolean getAsBoolean(PdfName pdfName) {
        PdfBoolean pdfBoolean = null;
        PdfObject pdfObject = this.getDirectObject(pdfName);
        if (pdfObject != null && pdfObject.isBoolean()) {
            pdfBoolean = (PdfBoolean)pdfObject;
        }
        return pdfBoolean;
    }

    public PdfIndirectReference getAsIndirectObject(PdfName pdfName) {
        PdfIndirectReference pdfIndirectReference = null;
        PdfObject pdfObject = this.get(pdfName);
        if (pdfObject != null && pdfObject.isIndirect()) {
            pdfIndirectReference = (PdfIndirectReference)pdfObject;
        }
        return pdfIndirectReference;
    }
}

