/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfResources;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

class PageResources {
    protected PdfDictionary fontDictionary = new PdfDictionary();
    protected PdfDictionary xObjectDictionary = new PdfDictionary();
    protected PdfDictionary colorDictionary = new PdfDictionary();
    protected PdfDictionary patternDictionary = new PdfDictionary();
    protected PdfDictionary shadingDictionary = new PdfDictionary();
    protected PdfDictionary extGStateDictionary = new PdfDictionary();
    protected PdfDictionary propertyDictionary = new PdfDictionary();
    protected HashMap forbiddenNames;
    protected PdfDictionary originalResources;
    protected int[] namePtr = new int[]{0};
    protected HashMap usedNames;

    PageResources() {
    }

    void setOriginalResources(PdfDictionary pdfDictionary, int[] arrn) {
        if (arrn != null) {
            this.namePtr = arrn;
        }
        this.forbiddenNames = new HashMap();
        this.usedNames = new HashMap();
        if (pdfDictionary == null) {
            return;
        }
        this.originalResources = new PdfDictionary();
        this.originalResources.merge(pdfDictionary);
        Iterator iterator = pdfDictionary.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            PdfObject pdfObject = PdfReader.getPdfObject(pdfDictionary.get(pdfName));
            if (pdfObject == null || !pdfObject.isDictionary()) continue;
            PdfDictionary pdfDictionary2 = (PdfDictionary)pdfObject;
            Object object = pdfDictionary2.getKeys().iterator();
            while (object.hasNext()) {
                this.forbiddenNames.put(object.next(), null);
            }
            object = new PdfDictionary();
            object.merge(pdfDictionary2);
            this.originalResources.put(pdfName, (PdfObject)object);
        }
    }

    PdfName translateName(PdfName pdfName) {
        PdfName pdfName2 = pdfName;
        if (this.forbiddenNames != null && (pdfName2 = (PdfName)this.usedNames.get(pdfName)) == null) {
            int n;
            do {
                int[] arrn = this.namePtr;
                n = arrn[0];
                arrn[0] = n + 1;
            } while (this.forbiddenNames.containsKey(pdfName2 = new PdfName("Xi" + n)));
            this.usedNames.put(pdfName, pdfName2);
        }
        return pdfName2;
    }

    PdfName addFont(PdfName pdfName, PdfIndirectReference pdfIndirectReference) {
        pdfName = this.translateName(pdfName);
        this.fontDictionary.put(pdfName, pdfIndirectReference);
        return pdfName;
    }

    PdfName addXObject(PdfName pdfName, PdfIndirectReference pdfIndirectReference) {
        pdfName = this.translateName(pdfName);
        this.xObjectDictionary.put(pdfName, pdfIndirectReference);
        return pdfName;
    }

    PdfName addColor(PdfName pdfName, PdfIndirectReference pdfIndirectReference) {
        pdfName = this.translateName(pdfName);
        this.colorDictionary.put(pdfName, pdfIndirectReference);
        return pdfName;
    }

    void addDefaultColor(PdfName pdfName, PdfObject pdfObject) {
        if (pdfObject == null || pdfObject.isNull()) {
            this.colorDictionary.remove(pdfName);
        } else {
            this.colorDictionary.put(pdfName, pdfObject);
        }
    }

    void addDefaultColor(PdfDictionary pdfDictionary) {
        this.colorDictionary.merge(pdfDictionary);
    }

    void addDefaultColorDiff(PdfDictionary pdfDictionary) {
        this.colorDictionary.mergeDifferent(pdfDictionary);
    }

    PdfName addShading(PdfName pdfName, PdfIndirectReference pdfIndirectReference) {
        pdfName = this.translateName(pdfName);
        this.shadingDictionary.put(pdfName, pdfIndirectReference);
        return pdfName;
    }

    PdfName addPattern(PdfName pdfName, PdfIndirectReference pdfIndirectReference) {
        pdfName = this.translateName(pdfName);
        this.patternDictionary.put(pdfName, pdfIndirectReference);
        return pdfName;
    }

    PdfName addExtGState(PdfName pdfName, PdfIndirectReference pdfIndirectReference) {
        pdfName = this.translateName(pdfName);
        this.extGStateDictionary.put(pdfName, pdfIndirectReference);
        return pdfName;
    }

    PdfName addProperty(PdfName pdfName, PdfIndirectReference pdfIndirectReference) {
        pdfName = this.translateName(pdfName);
        this.propertyDictionary.put(pdfName, pdfIndirectReference);
        return pdfName;
    }

    PdfDictionary getResources() {
        PdfResources pdfResources = new PdfResources();
        if (this.originalResources != null) {
            pdfResources.putAll(this.originalResources);
        }
        pdfResources.put(PdfName.PROCSET, new PdfLiteral("[/PDF /Text /ImageB /ImageC /ImageI]"));
        pdfResources.add(PdfName.FONT, this.fontDictionary);
        pdfResources.add(PdfName.XOBJECT, this.xObjectDictionary);
        pdfResources.add(PdfName.COLORSPACE, this.colorDictionary);
        pdfResources.add(PdfName.PATTERN, this.patternDictionary);
        pdfResources.add(PdfName.SHADING, this.shadingDictionary);
        pdfResources.add(PdfName.EXTGSTATE, this.extGStateDictionary);
        pdfResources.add(PdfName.PROPERTIES, this.propertyDictionary);
        return pdfResources;
    }

    boolean hasResources() {
        return this.fontDictionary.size() > 0 || this.xObjectDictionary.size() > 0 || this.colorDictionary.size() > 0 || this.patternDictionary.size() > 0 || this.shadingDictionary.size() > 0 || this.extGStateDictionary.size() > 0 || this.propertyDictionary.size() > 0;
    }
}

