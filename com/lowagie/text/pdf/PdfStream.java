/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.OutputStreamEncryption;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PdfStream
extends PdfDictionary {
    public static final int DEFAULT_COMPRESSION = -1;
    public static final int NO_COMPRESSION = 0;
    public static final int BEST_SPEED = 1;
    public static final int BEST_COMPRESSION = 9;
    protected boolean compressed = false;
    protected int compressionLevel = 0;
    protected ByteArrayOutputStream streamBytes = null;
    protected InputStream inputStream;
    protected PdfIndirectReference ref;
    protected int inputStreamLength = -1;
    protected PdfWriter writer;
    protected int rawLength;
    static final byte[] STARTSTREAM = DocWriter.getISOBytes("stream\n");
    static final byte[] ENDSTREAM = DocWriter.getISOBytes("\nendstream");
    static final int SIZESTREAM = STARTSTREAM.length + ENDSTREAM.length;

    public PdfStream(byte[] arrby) {
        this.type = 7;
        this.bytes = arrby;
        this.rawLength = arrby.length;
        this.put(PdfName.LENGTH, new PdfNumber(arrby.length));
    }

    public PdfStream(InputStream inputStream, PdfWriter pdfWriter) {
        this.type = 7;
        this.inputStream = inputStream;
        this.writer = pdfWriter;
        this.ref = pdfWriter.getPdfIndirectReference();
        this.put(PdfName.LENGTH, this.ref);
    }

    protected PdfStream() {
        this.type = 7;
    }

    public void writeLength() throws IOException {
        if (this.inputStream == null) {
            throw new UnsupportedOperationException("writeLength() can only be called in a contructed PdfStream(InputStream,PdfWriter).");
        }
        if (this.inputStreamLength == -1) {
            throw new IOException("writeLength() can only be called after output of the stream body.");
        }
        this.writer.addToBody((PdfObject)new PdfNumber(this.inputStreamLength), this.ref, false);
    }

    public int getRawLength() {
        return this.rawLength;
    }

    public void flateCompress() {
        this.flateCompress(-1);
    }

    public void flateCompress(int n) {
        if (!Document.compress) {
            return;
        }
        if (this.compressed) {
            return;
        }
        this.compressionLevel = n;
        if (this.inputStream != null) {
            this.compressed = true;
            return;
        }
        PdfObject pdfObject = PdfReader.getPdfObject(this.get(PdfName.FILTER));
        if (pdfObject != null) {
            if (pdfObject.isName()) {
                if (PdfName.FLATEDECODE.equals(pdfObject)) {
                    return;
                }
            } else if (pdfObject.isArray()) {
                if (((PdfArray)pdfObject).contains(PdfName.FLATEDECODE)) {
                    return;
                }
            } else {
                throw new RuntimeException("Stream could not be compressed: filter is not a name or array.");
            }
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream((OutputStream)byteArrayOutputStream, new Deflater(n));
            if (this.streamBytes != null) {
                this.streamBytes.writeTo(deflaterOutputStream);
            } else {
                deflaterOutputStream.write(this.bytes);
            }
            deflaterOutputStream.close();
            this.streamBytes = byteArrayOutputStream;
            this.bytes = null;
            this.put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
            if (pdfObject == null) {
                this.put(PdfName.FILTER, PdfName.FLATEDECODE);
            } else {
                PdfArray pdfArray = new PdfArray(pdfObject);
                pdfArray.add(PdfName.FLATEDECODE);
                this.put(PdfName.FILTER, pdfArray);
            }
            this.compressed = true;
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    protected void superToPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        super.toPdf(pdfWriter, outputStream);
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        PdfObject pdfObject;
        if (this.inputStream != null && this.compressed) {
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        PdfEncryption pdfEncryption = null;
        if (pdfWriter != null) {
            pdfEncryption = pdfWriter.getEncryption();
        }
        if (pdfEncryption != null && (pdfObject = this.get(PdfName.FILTER)) != null) {
            ArrayList arrayList;
            if (PdfName.CRYPT.equals(pdfObject)) {
                pdfEncryption = null;
            } else if (pdfObject.isArray() && !(arrayList = ((PdfArray)pdfObject).getArrayList()).isEmpty() && PdfName.CRYPT.equals(arrayList.get(0))) {
                pdfEncryption = null;
            }
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
            OutputStreamCounter outputStreamCounter = new OutputStreamCounter(outputStream);
            OutputStreamEncryption outputStreamEncryption = null;
            OutputStream outputStream2 = outputStreamCounter;
            if (pdfEncryption != null && !pdfEncryption.isEmbeddedFilesOnly()) {
                outputStream2 = outputStreamEncryption = pdfEncryption.getEncryptionStream(outputStream2);
            }
            if (this.compressed) {
                outputStream2 = deflaterOutputStream = new DeflaterOutputStream(outputStream2, new Deflater(this.compressionLevel), 32768);
            }
            byte[] arrby = new byte[4192];
            while ((n = this.inputStream.read(arrby)) > 0) {
                outputStream2.write(arrby, 0, n);
                this.rawLength += n;
            }
            if (deflaterOutputStream != null) {
                deflaterOutputStream.finish();
            }
            if (outputStreamEncryption != null) {
                outputStreamEncryption.finish();
            }
            this.inputStreamLength = outputStreamCounter.getCounter();
        } else if (pdfEncryption != null && !pdfEncryption.isEmbeddedFilesOnly()) {
            byte[] arrby = this.streamBytes != null ? pdfEncryption.encryptByteArray(this.streamBytes.toByteArray()) : pdfEncryption.encryptByteArray(this.bytes);
            outputStream.write(arrby);
        } else if (this.streamBytes != null) {
            this.streamBytes.writeTo(outputStream);
        } else {
            outputStream.write(this.bytes);
        }
        outputStream.write(ENDSTREAM);
    }

    public void writeContent(OutputStream outputStream) throws IOException {
        if (this.streamBytes != null) {
            this.streamBytes.writeTo(outputStream);
        } else if (this.bytes != null) {
            outputStream.write(this.bytes);
        }
    }

    public String toString() {
        if (this.get(PdfName.TYPE) == null) {
            return "Stream";
        }
        return "Stream of type: " + this.get(PdfName.TYPE);
    }
}

