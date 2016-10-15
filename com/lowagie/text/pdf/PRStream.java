/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PRStream
extends PdfStream {
    protected PdfReader reader;
    protected int offset;
    protected int length;
    protected int objNum = 0;
    protected int objGen = 0;

    public PRStream(PRStream pRStream, PdfDictionary pdfDictionary) {
        this.reader = pRStream.reader;
        this.offset = pRStream.offset;
        this.length = pRStream.length;
        this.compressed = pRStream.compressed;
        this.compressionLevel = pRStream.compressionLevel;
        this.streamBytes = pRStream.streamBytes;
        this.bytes = pRStream.bytes;
        this.objNum = pRStream.objNum;
        this.objGen = pRStream.objGen;
        if (pdfDictionary != null) {
            this.putAll(pdfDictionary);
        } else {
            this.hashMap.putAll(pRStream.hashMap);
        }
    }

    public PRStream(PRStream pRStream, PdfDictionary pdfDictionary, PdfReader pdfReader) {
        this(pRStream, pdfDictionary);
        this.reader = pdfReader;
    }

    public PRStream(PdfReader pdfReader, int n) {
        this.reader = pdfReader;
        this.offset = n;
    }

    public PRStream(PdfReader pdfReader, byte[] arrby) {
        this(pdfReader, arrby, -1);
    }

    public PRStream(PdfReader pdfReader, byte[] arrby, int n) {
        this.reader = pdfReader;
        this.offset = -1;
        if (Document.compress) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream((OutputStream)byteArrayOutputStream, new Deflater(n));
                deflaterOutputStream.write(arrby);
                deflaterOutputStream.close();
                this.bytes = byteArrayOutputStream.toByteArray();
            }
            catch (IOException var4_5) {
                throw new ExceptionConverter(var4_5);
            }
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        } else {
            this.bytes = arrby;
        }
        this.setLength(this.bytes.length);
    }

    public void setData(byte[] arrby, boolean bl) {
        this.setData(arrby, bl, -1);
    }

    public void setData(byte[] arrby, boolean bl, int n) {
        this.remove(PdfName.FILTER);
        this.offset = -1;
        if (Document.compress && bl) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream((OutputStream)byteArrayOutputStream, new Deflater(n));
                deflaterOutputStream.write(arrby);
                deflaterOutputStream.close();
                this.bytes = byteArrayOutputStream.toByteArray();
                this.compressionLevel = n;
            }
            catch (IOException var4_5) {
                throw new ExceptionConverter(var4_5);
            }
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        } else {
            this.bytes = arrby;
        }
        this.setLength(this.bytes.length);
    }

    public void setData(byte[] arrby) {
        this.setData(arrby, true);
    }

    public void setLength(int n) {
        this.length = n;
        this.put(PdfName.LENGTH, new PdfNumber(n));
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public PdfReader getReader() {
        return this.reader;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setObjNum(int n, int n2) {
        this.objNum = n;
        this.objGen = n2;
    }

    int getObjNum() {
        return this.objNum;
    }

    int getObjGen() {
        return this.objGen;
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        byte[] arrby = PdfReader.getStreamBytesRaw(this);
        PdfEncryption pdfEncryption = null;
        if (pdfWriter != null) {
            pdfEncryption = pdfWriter.getEncryption();
        }
        PdfObject pdfObject = this.get(PdfName.LENGTH);
        int n = arrby.length;
        if (pdfEncryption != null) {
            n = pdfEncryption.calculateStreamSize(n);
        }
        this.put(PdfName.LENGTH, new PdfNumber(n));
        this.superToPdf(pdfWriter, outputStream);
        this.put(PdfName.LENGTH, pdfObject);
        outputStream.write(STARTSTREAM);
        if (this.length > 0) {
            if (pdfEncryption != null && !pdfEncryption.isEmbeddedFilesOnly()) {
                arrby = pdfEncryption.encryptByteArray(arrby);
            }
            outputStream.write(arrby);
        }
        outputStream.write(ENDSTREAM);
    }
}

