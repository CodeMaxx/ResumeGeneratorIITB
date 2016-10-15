/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PdfIndirectObject {
    protected int number;
    protected int generation = 0;
    static final byte[] STARTOBJ = DocWriter.getISOBytes(" obj");
    static final byte[] ENDOBJ = DocWriter.getISOBytes("\nendobj\n");
    static final int SIZEOBJ = STARTOBJ.length + ENDOBJ.length;
    PdfObject object;
    PdfWriter writer;

    PdfIndirectObject(int n, PdfObject pdfObject, PdfWriter pdfWriter) {
        this(n, 0, pdfObject, pdfWriter);
    }

    PdfIndirectObject(PdfIndirectReference pdfIndirectReference, PdfObject pdfObject, PdfWriter pdfWriter) {
        this(pdfIndirectReference.getNumber(), pdfIndirectReference.getGeneration(), pdfObject, pdfWriter);
    }

    PdfIndirectObject(int n, int n2, PdfObject pdfObject, PdfWriter pdfWriter) {
        this.writer = pdfWriter;
        this.number = n;
        this.generation = n2;
        this.object = pdfObject;
        PdfEncryption pdfEncryption = null;
        if (pdfWriter != null) {
            pdfEncryption = pdfWriter.getEncryption();
        }
        if (pdfEncryption != null) {
            pdfEncryption.setHashKey(n, n2);
        }
    }

    public PdfIndirectReference getIndirectReference() {
        return new PdfIndirectReference(this.object.type(), this.number, this.generation);
    }

    void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(DocWriter.getISOBytes(String.valueOf(this.number)));
        outputStream.write(32);
        outputStream.write(DocWriter.getISOBytes(String.valueOf(this.generation)));
        outputStream.write(STARTOBJ);
        int n = this.object.type();
        if (n != 5 && n != 6 && n != 4 && n != 3) {
            outputStream.write(32);
        }
        this.object.toPdf(this.writer, outputStream);
        outputStream.write(ENDOBJ);
    }
}

