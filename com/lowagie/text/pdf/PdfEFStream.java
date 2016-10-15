/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.OutputStreamEncryption;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PdfEFStream
extends PdfStream {
    public PdfEFStream(InputStream inputStream, PdfWriter pdfWriter) {
        super(inputStream, pdfWriter);
    }

    public PdfEFStream(byte[] arrby) {
        super(arrby);
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        Object object;
        PdfObject pdfObject;
        Object object2;
        if (this.inputStream != null && this.compressed) {
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        PdfEncryption pdfEncryption = null;
        if (pdfWriter != null) {
            pdfEncryption = pdfWriter.getEncryption();
        }
        if (pdfEncryption != null && (pdfObject = this.get(PdfName.FILTER)) != null) {
            if (PdfName.CRYPT.equals(pdfObject)) {
                pdfEncryption = null;
            } else if (pdfObject.isArray() && !(object2 = ((PdfArray)pdfObject).getArrayList()).isEmpty() && PdfName.CRYPT.equals(object2.get(0))) {
                pdfEncryption = null;
            }
        }
        if (pdfEncryption != null && pdfEncryption.isEmbeddedFilesOnly()) {
            pdfObject = new PdfArray();
            object2 = new PdfArray();
            object = new PdfDictionary();
            object.put(PdfName.NAME, PdfName.STDCF);
            pdfObject.add(PdfName.CRYPT);
            object2.add((PdfObject)object);
            if (this.compressed) {
                pdfObject.add(PdfName.FLATEDECODE);
                object2.add(new PdfNull());
            }
            this.put(PdfName.FILTER, pdfObject);
            this.put(PdfName.DECODEPARMS, (PdfObject)object2);
        }
        pdfObject = this.get(PdfName.LENGTH);
        if (pdfEncryption != null && pdfObject != null && pdfObject.isNumber()) {
            int n = ((PdfNumber)pdfObject).intValue();
            this.put(PdfName.LENGTH, new PdfNumber(pdfEncryption.calculateStreamSize(n)));
            this.superToPdf(pdfWriter, outputStream);
            this.put(PdfName.LENGTH, pdfObject);
        } else {
            this.superToPdf(pdfWriter, outputStream);
        }
        outputStream.write(STARTSTREAM);
        if (this.inputStream != null) {
            int n;
            this.rawLength = 0;
            DeflaterOutputStream deflaterOutputStream = null;
            object = new OutputStreamCounter(outputStream);
            OutputStreamEncryption outputStreamEncryption = null;
            Object object3 = object;
            if (pdfEncryption != null) {
                object3 = outputStreamEncryption = pdfEncryption.getEncryptionStream((OutputStream)object3);
            }
            if (this.compressed) {
                object3 = deflaterOutputStream = new DeflaterOutputStream((OutputStream)object3, new Deflater(this.compressionLevel), 32768);
            }
            byte[] arrby = new byte[4192];
            while ((n = this.inputStream.read(arrby)) > 0) {
                object3.write(arrby, 0, n);
                this.rawLength += n;
            }
            if (deflaterOutputStream != null) {
                deflaterOutputStream.finish();
            }
            if (outputStreamEncryption != null) {
                outputStreamEncryption.finish();
            }
            this.inputStreamLength = object.getCounter();
        } else if (pdfEncryption == null) {
            if (this.streamBytes != null) {
                this.streamBytes.writeTo(outputStream);
            } else {
                outputStream.write(this.bytes);
            }
        } else {
            byte[] arrby = this.streamBytes != null ? pdfEncryption.encryptByteArray(this.streamBytes.toByteArray()) : pdfEncryption.encryptByteArray(this.bytes);
            outputStream.write(arrby);
        }
        outputStream.write(ENDSTREAM);
    }
}

