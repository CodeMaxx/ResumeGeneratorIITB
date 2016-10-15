/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

class PdfReaderInstance {
    static final PdfLiteral IDENTITYMATRIX = new PdfLiteral("[1 0 0 1 0 0]");
    static final PdfNumber ONE = new PdfNumber(1);
    int[] myXref;
    PdfReader reader;
    RandomAccessFileOrArray file;
    HashMap importedPages = new HashMap();
    PdfWriter writer;
    HashMap visited = new HashMap();
    ArrayList nextRound = new ArrayList();

    PdfReaderInstance(PdfReader pdfReader, PdfWriter pdfWriter) {
        this.reader = pdfReader;
        this.writer = pdfWriter;
        this.file = pdfReader.getSafeFile();
        this.myXref = new int[pdfReader.getXrefSize()];
    }

    PdfReader getReader() {
        return this.reader;
    }

    PdfImportedPage getImportedPage(int n) {
        if (!this.reader.isOpenedWithFullPermissions()) {
            throw new IllegalArgumentException("PdfReader not opened with owner password");
        }
        if (n < 1 || n > this.reader.getNumberOfPages()) {
            throw new IllegalArgumentException("Invalid page number: " + n);
        }
        Integer n2 = new Integer(n);
        PdfImportedPage pdfImportedPage = (PdfImportedPage)this.importedPages.get(n2);
        if (pdfImportedPage == null) {
            pdfImportedPage = new PdfImportedPage(this, this.writer, n);
            this.importedPages.put(n2, pdfImportedPage);
        }
        return pdfImportedPage;
    }

    int getNewObjectNumber(int n, int n2) {
        if (this.myXref[n] == 0) {
            this.myXref[n] = this.writer.getIndirectReferenceNumber();
            this.nextRound.add(new Integer(n));
        }
        return this.myXref[n];
    }

    RandomAccessFileOrArray getReaderFile() {
        return this.file;
    }

    PdfObject getResources(int n) {
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(this.reader.getPageNRelease(n).get(PdfName.RESOURCES));
        return pdfObject;
    }

    PdfStream getFormXObject(int n, int n2) throws IOException {
        PRStream pRStream;
        PdfDictionary pdfDictionary = this.reader.getPageNRelease(n);
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.CONTENTS));
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        byte[] arrby = null;
        if (pdfObject != null) {
            if (pdfObject.isStream()) {
                pdfDictionary2.putAll((PRStream)pdfObject);
            } else {
                arrby = this.reader.getPageContent(n, this.file);
            }
        } else {
            arrby = new byte[]{};
        }
        pdfDictionary2.put(PdfName.RESOURCES, PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.RESOURCES)));
        pdfDictionary2.put(PdfName.TYPE, PdfName.XOBJECT);
        pdfDictionary2.put(PdfName.SUBTYPE, PdfName.FORM);
        PdfImportedPage pdfImportedPage = (PdfImportedPage)this.importedPages.get(new Integer(n));
        pdfDictionary2.put(PdfName.BBOX, new PdfRectangle(pdfImportedPage.getBoundingBox()));
        PdfArray pdfArray = pdfImportedPage.getMatrix();
        if (pdfArray == null) {
            pdfDictionary2.put(PdfName.MATRIX, IDENTITYMATRIX);
        } else {
            pdfDictionary2.put(PdfName.MATRIX, pdfArray);
        }
        pdfDictionary2.put(PdfName.FORMTYPE, ONE);
        if (arrby == null) {
            pRStream = new PRStream((PRStream)pdfObject, pdfDictionary2);
        } else {
            pRStream = new PRStream(this.reader, arrby, n2);
            pRStream.putAll(pdfDictionary2);
        }
        return pRStream;
    }

    void writeAllVisited() throws IOException {
        while (!this.nextRound.isEmpty()) {
            ArrayList arrayList = this.nextRound;
            this.nextRound = new ArrayList();
            for (int i = 0; i < arrayList.size(); ++i) {
                Integer n = (Integer)arrayList.get(i);
                if (this.visited.containsKey(n)) continue;
                this.visited.put(n, null);
                int n2 = n;
                this.writer.addToBody(this.reader.getPdfObjectRelease(n2), this.myXref[n2]);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writeAllPages() throws IOException {
        try {
            this.file.reOpen();
            Iterator iterator = this.importedPages.values().iterator();
            while (iterator.hasNext()) {
                PdfImportedPage pdfImportedPage = (PdfImportedPage)iterator.next();
                this.writer.addToBody((PdfObject)pdfImportedPage.getFormXObject(this.writer.getCompressionLevel()), pdfImportedPage.getIndirectReference());
            }
            this.writeAllVisited();
        }
        finally {
            try {
                this.reader.close();
                this.file.close();
            }
            catch (Exception var1_2) {}
        }
    }
}

