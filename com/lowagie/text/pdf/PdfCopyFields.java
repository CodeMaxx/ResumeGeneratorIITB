/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFieldsImp;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SequenceList;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.List;

public class PdfCopyFields
implements PdfViewerPreferences,
PdfEncryptionSettings {
    private PdfCopyFieldsImp fc;

    public PdfCopyFields(OutputStream outputStream) throws DocumentException {
        this.fc = new PdfCopyFieldsImp(outputStream);
    }

    public PdfCopyFields(OutputStream outputStream, char c) throws DocumentException {
        this.fc = new PdfCopyFieldsImp(outputStream, c);
    }

    public void addDocument(PdfReader pdfReader) throws DocumentException {
        this.fc.addDocument(pdfReader);
    }

    public void addDocument(PdfReader pdfReader, List list) throws DocumentException {
        this.fc.addDocument(pdfReader, list);
    }

    public void addDocument(PdfReader pdfReader, String string) throws DocumentException {
        this.fc.addDocument(pdfReader, SequenceList.expand(string, pdfReader.getNumberOfPages()));
    }

    public void setEncryption(byte[] arrby, byte[] arrby2, int n, boolean bl) throws DocumentException {
        this.fc.setEncryption(arrby, arrby2, n, bl ? 1 : 0);
    }

    public void setEncryption(boolean bl, String string, String string2, int n) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(string), DocWriter.getISOBytes(string2), n, bl);
    }

    public void close() {
        this.fc.close();
    }

    public void open() {
        this.fc.openDoc();
    }

    public void addJavaScript(String string) {
        this.fc.addJavaScript(string, !PdfEncodings.isPdfDocEncoding(string));
    }

    public void setOutlines(List list) {
        this.fc.setOutlines(list);
    }

    public PdfWriter getWriter() {
        return this.fc;
    }

    public boolean isFullCompression() {
        return this.fc.isFullCompression();
    }

    public void setFullCompression() {
        this.fc.setFullCompression();
    }

    public void setEncryption(byte[] arrby, byte[] arrby2, int n, int n2) throws DocumentException {
        this.fc.setEncryption(arrby, arrby2, n, n2);
    }

    public void addViewerPreference(PdfName pdfName, PdfObject pdfObject) {
        this.fc.addViewerPreference(pdfName, pdfObject);
    }

    public void setViewerPreferences(int n) {
        this.fc.setViewerPreferences(n);
    }

    public void setEncryption(Certificate[] arrcertificate, int[] arrn, int n) throws DocumentException {
        this.fc.setEncryption(arrcertificate, arrn, n);
    }
}

