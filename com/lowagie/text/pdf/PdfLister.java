/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfReaderInstance;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class PdfLister {
    PrintStream out;

    public PdfLister(PrintStream printStream) {
        this.out = printStream;
    }

    public void listAnyObject(PdfObject pdfObject) {
        switch (pdfObject.type()) {
            case 5: {
                this.listArray((PdfArray)pdfObject);
                break;
            }
            case 6: {
                this.listDict((PdfDictionary)pdfObject);
                break;
            }
            case 3: {
                this.out.println("(" + pdfObject.toString() + ")");
                break;
            }
            default: {
                this.out.println(pdfObject.toString());
            }
        }
    }

    public void listDict(PdfDictionary pdfDictionary) {
        this.out.println("<<");
        Iterator iterator = pdfDictionary.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            PdfObject pdfObject = pdfDictionary.get(pdfName);
            this.out.print(pdfName.toString());
            this.out.print(' ');
            this.listAnyObject(pdfObject);
        }
        this.out.println(">>");
    }

    public void listArray(PdfArray pdfArray) {
        this.out.println('[');
        Iterator iterator = pdfArray.getArrayList().iterator();
        while (iterator.hasNext()) {
            PdfObject pdfObject = (PdfObject)iterator.next();
            this.listAnyObject(pdfObject);
        }
        this.out.println(']');
    }

    public void listStream(PRStream pRStream, PdfReaderInstance pdfReaderInstance) {
        try {
            this.listDict(pRStream);
            this.out.println("startstream");
            byte[] arrby = PdfReader.getStreamBytes(pRStream);
            int n = arrby.length - 1;
            for (int i = 0; i < n; ++i) {
                if (arrby[i] != 13 || arrby[i + 1] == 10) continue;
                arrby[i] = 10;
            }
            this.out.println(new String(arrby));
            this.out.println("endstream");
        }
        catch (IOException var3_4) {
            System.err.println("I/O exception: " + var3_4);
        }
    }

    public void listPage(PdfImportedPage pdfImportedPage) {
        int n = pdfImportedPage.getPageNumber();
        PdfReaderInstance pdfReaderInstance = pdfImportedPage.getPdfReaderInstance();
        PdfReader pdfReader = pdfReaderInstance.getReader();
        PdfDictionary pdfDictionary = pdfReader.getPageN(n);
        this.listDict(pdfDictionary);
        PdfObject pdfObject = PdfReader.getPdfObject(pdfDictionary.get(PdfName.CONTENTS));
        if (pdfObject == null) {
            return;
        }
        switch (pdfObject.type) {
            case 7: {
                this.listStream((PRStream)pdfObject, pdfReaderInstance);
                break;
            }
            case 5: {
                Iterator iterator = ((PdfArray)pdfObject).getArrayList().iterator();
                while (iterator.hasNext()) {
                    PdfObject pdfObject2 = PdfReader.getPdfObject((PdfObject)iterator.next());
                    this.listStream((PRStream)pdfObject2, pdfReaderInstance);
                    this.out.println("-----------");
                }
                break;
            }
        }
    }
}

