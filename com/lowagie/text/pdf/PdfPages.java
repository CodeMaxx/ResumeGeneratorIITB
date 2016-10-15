/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PdfPages {
    private ArrayList pages = new ArrayList();
    private ArrayList parents = new ArrayList();
    private int leafSize = 10;
    private PdfWriter writer;
    private PdfIndirectReference topParent;

    PdfPages(PdfWriter pdfWriter) {
        this.writer = pdfWriter;
    }

    void addPage(PdfDictionary pdfDictionary) {
        try {
            if (this.pages.size() % this.leafSize == 0) {
                this.parents.add(this.writer.getPdfIndirectReference());
            }
            PdfIndirectReference pdfIndirectReference = (PdfIndirectReference)this.parents.get(this.parents.size() - 1);
            pdfDictionary.put(PdfName.PARENT, pdfIndirectReference);
            PdfIndirectReference pdfIndirectReference2 = this.writer.getCurrentPage();
            this.writer.addToBody((PdfObject)pdfDictionary, pdfIndirectReference2);
            this.pages.add(pdfIndirectReference2);
        }
        catch (Exception var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    PdfIndirectReference addPageRef(PdfIndirectReference pdfIndirectReference) {
        try {
            if (this.pages.size() % this.leafSize == 0) {
                this.parents.add(this.writer.getPdfIndirectReference());
            }
            this.pages.add(pdfIndirectReference);
            return (PdfIndirectReference)this.parents.get(this.parents.size() - 1);
        }
        catch (Exception var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    PdfIndirectReference writePageTree() throws IOException {
        if (this.pages.isEmpty()) {
            throw new IOException("The document has no pages.");
        }
        int n = 1;
        ArrayList<PdfIndirectReference> arrayList = this.parents;
        ArrayList arrayList2 = this.pages;
        ArrayList<PdfIndirectReference> arrayList3 = new ArrayList<PdfIndirectReference>();
        do {
            n *= this.leafSize;
            int n2 = this.leafSize;
            int n3 = arrayList2.size() % this.leafSize;
            if (n3 == 0) {
                n3 = this.leafSize;
            }
            for (int i = 0; i < arrayList.size(); ++i) {
                int n4;
                int n5 = n;
                if (i == arrayList.size() - 1) {
                    n4 = n3;
                    n5 = this.pages.size() % n;
                    if (n5 == 0) {
                        n5 = n;
                    }
                } else {
                    n4 = n2;
                }
                PdfDictionary pdfDictionary = new PdfDictionary(PdfName.PAGES);
                pdfDictionary.put(PdfName.COUNT, new PdfNumber(n5));
                PdfArray pdfArray = new PdfArray();
                ArrayList arrayList4 = pdfArray.getArrayList();
                arrayList4.addAll(arrayList2.subList(i * n2, i * n2 + n4));
                pdfDictionary.put(PdfName.KIDS, pdfArray);
                if (arrayList.size() > 1) {
                    if (i % this.leafSize == 0) {
                        arrayList3.add(this.writer.getPdfIndirectReference());
                    }
                    pdfDictionary.put(PdfName.PARENT, (PdfIndirectReference)arrayList3.get(i / this.leafSize));
                }
                this.writer.addToBody((PdfObject)pdfDictionary, (PdfIndirectReference)arrayList.get(i));
            }
            if (arrayList.size() == 1) {
                this.topParent = (PdfIndirectReference)arrayList.get(0);
                return this.topParent;
            }
            arrayList2 = arrayList;
            arrayList = arrayList3;
            arrayList3 = new ArrayList();
        } while (true);
    }

    PdfIndirectReference getTopParent() {
        return this.topParent;
    }

    void setLinearMode(PdfIndirectReference pdfIndirectReference) {
        if (this.parents.size() > 1) {
            throw new RuntimeException("Linear page mode can only be called with a single parent.");
        }
        if (pdfIndirectReference != null) {
            this.topParent = pdfIndirectReference;
            this.parents.clear();
            this.parents.add(pdfIndirectReference);
        }
        this.leafSize = 10000000;
    }

    void addPage(PdfIndirectReference pdfIndirectReference) {
        this.pages.add(pdfIndirectReference);
    }

    int reorderPages(int[] arrn) throws DocumentException {
        int n;
        if (arrn == null) {
            return this.pages.size();
        }
        if (this.parents.size() > 1) {
            throw new DocumentException("Page reordering requires a single parent in the page tree. Call PdfWriter.setLinearMode() after open.");
        }
        if (arrn.length != this.pages.size()) {
            throw new DocumentException("Page reordering requires an array with the same size as the number of pages.");
        }
        int n2 = this.pages.size();
        boolean[] arrbl = new boolean[n2];
        for (int i = 0; i < n2; ++i) {
            n = arrn[i];
            if (n < 1 || n > n2) {
                throw new DocumentException("Page reordering requires pages between 1 and " + n2 + ". Found " + n + ".");
            }
            if (arrbl[n - 1]) {
                throw new DocumentException("Page reordering requires no page repetition. Page " + n + " is repeated.");
            }
            arrbl[n - 1] = true;
        }
        Object[] arrobject = this.pages.toArray();
        for (n = 0; n < n2; ++n) {
            this.pages.set(n, arrobject[arrn[n] - 1]);
        }
        return n2;
    }
}

