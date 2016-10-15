/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumberTree;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PdfStructureTreeRoot
extends PdfDictionary {
    private HashMap parentTree = new HashMap();
    private PdfIndirectReference reference;
    private PdfWriter writer;

    PdfStructureTreeRoot(PdfWriter pdfWriter) {
        super(PdfName.STRUCTTREEROOT);
        this.writer = pdfWriter;
        this.reference = pdfWriter.getPdfIndirectReference();
    }

    public void mapRole(PdfName pdfName, PdfName pdfName2) {
        PdfDictionary pdfDictionary = (PdfDictionary)this.get(PdfName.ROLEMAP);
        if (pdfDictionary == null) {
            pdfDictionary = new PdfDictionary();
            this.put(PdfName.ROLEMAP, pdfDictionary);
        }
        pdfDictionary.put(pdfName, pdfName2);
    }

    public PdfWriter getWriter() {
        return this.writer;
    }

    public PdfIndirectReference getReference() {
        return this.reference;
    }

    void setPageMark(int n, PdfIndirectReference pdfIndirectReference) {
        Integer n2 = new Integer(n);
        PdfArray pdfArray = (PdfArray)this.parentTree.get(n2);
        if (pdfArray == null) {
            pdfArray = new PdfArray();
            this.parentTree.put(n2, pdfArray);
        }
        pdfArray.add(pdfIndirectReference);
    }

    private void nodeProcess(PdfDictionary pdfDictionary, PdfIndirectReference pdfIndirectReference) throws IOException {
        PdfObject pdfObject = pdfDictionary.get(PdfName.K);
        if (pdfObject != null && pdfObject.isArray() && !((PdfObject)((PdfArray)pdfObject).getArrayList().get(0)).isNumber()) {
            PdfArray pdfArray = (PdfArray)pdfObject;
            ArrayList arrayList = pdfArray.getArrayList();
            for (int i = 0; i < arrayList.size(); ++i) {
                PdfStructureElement pdfStructureElement = (PdfStructureElement)arrayList.get(i);
                arrayList.set(i, pdfStructureElement.getReference());
                this.nodeProcess(pdfStructureElement, pdfStructureElement.getReference());
            }
        }
        if (pdfIndirectReference != null) {
            this.writer.addToBody((PdfObject)pdfDictionary, pdfIndirectReference);
        }
    }

    void buildTree() throws IOException {
        HashMap<Integer, PdfIndirectReference> hashMap = new HashMap<Integer, PdfIndirectReference>();
        Object object = this.parentTree.keySet().iterator();
        while (object.hasNext()) {
            Integer n = (Integer)object.next();
            PdfArray pdfArray = (PdfArray)this.parentTree.get(n);
            hashMap.put(n, this.writer.addToBody(pdfArray).getIndirectReference());
        }
        object = PdfNumberTree.writeTree(hashMap, this.writer);
        if (object != null) {
            this.put(PdfName.PARENTTREE, this.writer.addToBody((PdfObject)object).getIndirectReference());
        }
        this.nodeProcess(this, this.reference);
    }
}

