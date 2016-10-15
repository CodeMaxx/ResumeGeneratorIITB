/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.FdfReader;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSigGenericPKCS;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamperImp;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfStamper
implements PdfViewerPreferences,
PdfEncryptionSettings {
    protected PdfStamperImp stamper;
    private HashMap moreInfo;
    private boolean hasSignature;
    private PdfSignatureAppearance sigApp;

    public PdfStamper(PdfReader pdfReader, OutputStream outputStream) throws DocumentException, IOException {
        this.stamper = new PdfStamperImp(pdfReader, outputStream, '\u0000', false);
    }

    public PdfStamper(PdfReader pdfReader, OutputStream outputStream, char c) throws DocumentException, IOException {
        this.stamper = new PdfStamperImp(pdfReader, outputStream, c, false);
    }

    public PdfStamper(PdfReader pdfReader, OutputStream outputStream, char c, boolean bl) throws DocumentException, IOException {
        this.stamper = new PdfStamperImp(pdfReader, outputStream, c, bl);
    }

    public HashMap getMoreInfo() {
        return this.moreInfo;
    }

    public void setMoreInfo(HashMap hashMap) {
        this.moreInfo = hashMap;
    }

    public void replacePage(PdfReader pdfReader, int n, int n2) {
        this.stamper.replacePage(pdfReader, n, n2);
    }

    public void insertPage(int n, Rectangle rectangle) {
        this.stamper.insertPage(n, rectangle);
    }

    public PdfSignatureAppearance getSignatureAppearance() {
        return this.sigApp;
    }

    public void close() throws DocumentException, IOException {
        if (!this.hasSignature) {
            this.stamper.close(this.moreInfo);
            return;
        }
        this.sigApp.preClose();
        PdfSigGenericPKCS pdfSigGenericPKCS = this.sigApp.getSigStandard();
        PdfLiteral pdfLiteral = (PdfLiteral)pdfSigGenericPKCS.get(PdfName.CONTENTS);
        int n = (pdfLiteral.getPosLength() - 2) / 2;
        byte[] arrby = new byte[8192];
        InputStream inputStream = this.sigApp.getRangeStream();
        try {
            int n2;
            while ((n2 = inputStream.read(arrby)) > 0) {
                pdfSigGenericPKCS.getSigner().update(arrby, 0, n2);
            }
        }
        catch (SignatureException var7_7) {
            throw new ExceptionConverter(var7_7);
        }
        arrby = new byte[n];
        byte[] arrby2 = pdfSigGenericPKCS.getSignerContents();
        System.arraycopy(arrby2, 0, arrby, 0, arrby2.length);
        PdfString pdfString = new PdfString(arrby);
        pdfString.setHexWriting(true);
        PdfDictionary pdfDictionary = new PdfDictionary();
        pdfDictionary.put(PdfName.CONTENTS, pdfString);
        this.sigApp.close(pdfDictionary);
        this.stamper.reader.close();
    }

    public PdfContentByte getUnderContent(int n) {
        return this.stamper.getUnderContent(n);
    }

    public PdfContentByte getOverContent(int n) {
        return this.stamper.getOverContent(n);
    }

    public boolean isRotateContents() {
        return this.stamper.isRotateContents();
    }

    public void setRotateContents(boolean bl) {
        this.stamper.setRotateContents(bl);
    }

    public void setEncryption(byte[] arrby, byte[] arrby2, int n, boolean bl) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException("Append mode does not support changing the encryption status.");
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException("Content was already written to the output.");
        }
        this.stamper.setEncryption(arrby, arrby2, n, bl ? 1 : 0);
    }

    public void setEncryption(byte[] arrby, byte[] arrby2, int n, int n2) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException("Append mode does not support changing the encryption status.");
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException("Content was already written to the output.");
        }
        this.stamper.setEncryption(arrby, arrby2, n, n2);
    }

    public void setEncryption(boolean bl, String string, String string2, int n) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(string), DocWriter.getISOBytes(string2), n, bl);
    }

    public void setEncryption(int n, String string, String string2, int n2) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(string), DocWriter.getISOBytes(string2), n2, n);
    }

    public void setEncryption(Certificate[] arrcertificate, int[] arrn, int n) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException("Append mode does not support changing the encryption status.");
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException("Content was already written to the output.");
        }
        this.stamper.setEncryption(arrcertificate, arrn, n);
    }

    public PdfImportedPage getImportedPage(PdfReader pdfReader, int n) {
        return this.stamper.getImportedPage(pdfReader, n);
    }

    public PdfWriter getWriter() {
        return this.stamper;
    }

    public PdfReader getReader() {
        return this.stamper.reader;
    }

    public AcroFields getAcroFields() {
        return this.stamper.getAcroFields();
    }

    public void setFormFlattening(boolean bl) {
        this.stamper.setFormFlattening(bl);
    }

    public void setFreeTextFlattening(boolean bl) {
        this.stamper.setFreeTextFlattening(bl);
    }

    public void addAnnotation(PdfAnnotation pdfAnnotation, int n) {
        this.stamper.addAnnotation(pdfAnnotation, n);
    }

    public void addComments(FdfReader fdfReader) throws IOException {
        this.stamper.addComments(fdfReader);
    }

    public void setOutlines(List list) {
        this.stamper.setOutlines(list);
    }

    public void setThumbnail(Image image, int n) throws PdfException, DocumentException {
        this.stamper.setThumbnail(image, n);
    }

    public boolean partialFormFlattening(String string) {
        return this.stamper.partialFormFlattening(string);
    }

    public void addJavaScript(String string) {
        this.stamper.addJavaScript(string, !PdfEncodings.isPdfDocEncoding(string));
    }

    public void addFileAttachment(String string, byte[] arrby, String string2, String string3) throws IOException {
        this.addFileAttachment(string, PdfFileSpecification.fileEmbedded(this.stamper, string2, string3, arrby));
    }

    public void addFileAttachment(String string, PdfFileSpecification pdfFileSpecification) throws IOException {
        this.stamper.addFileAttachment(string, pdfFileSpecification);
    }

    public void makePackage(PdfName pdfName) {
        PdfCollection pdfCollection = new PdfCollection(0);
        pdfCollection.put(PdfName.VIEW, pdfName);
        this.stamper.makePackage(pdfCollection);
    }

    public void makePackage(PdfCollection pdfCollection) {
        this.stamper.makePackage(pdfCollection);
    }

    public void setViewerPreferences(int n) {
        this.stamper.setViewerPreferences(n);
    }

    public void addViewerPreference(PdfName pdfName, PdfObject pdfObject) {
        this.stamper.addViewerPreference(pdfName, pdfObject);
    }

    public void setXmpMetadata(byte[] arrby) {
        this.stamper.setXmpMetadata(arrby);
    }

    public boolean isFullCompression() {
        return this.stamper.isFullCompression();
    }

    public void setFullCompression() {
        if (this.stamper.isAppend()) {
            return;
        }
        this.stamper.setFullCompression();
    }

    public void setPageAction(PdfName pdfName, PdfAction pdfAction, int n) throws PdfException {
        this.stamper.setPageAction(pdfName, pdfAction, n);
    }

    public void setDuration(int n, int n2) {
        this.stamper.setDuration(n, n2);
    }

    public void setTransition(PdfTransition pdfTransition, int n) {
        this.stamper.setTransition(pdfTransition, n);
    }

    public static PdfStamper createSignature(PdfReader pdfReader, OutputStream outputStream, char c, File file, boolean bl) throws DocumentException, IOException {
        PdfStamper pdfStamper;
        Object object;
        if (file == null) {
            object = new ByteBuffer();
            pdfStamper = new PdfStamper(pdfReader, (OutputStream)object, c, bl);
            pdfStamper.sigApp = new PdfSignatureAppearance(pdfStamper.stamper);
            pdfStamper.sigApp.setSigout((ByteBuffer)object);
        } else {
            if (file.isDirectory()) {
                file = File.createTempFile("pdf", null, file);
            }
            object = new FileOutputStream(file);
            pdfStamper = new PdfStamper(pdfReader, (OutputStream)object, c, bl);
            pdfStamper.sigApp = new PdfSignatureAppearance(pdfStamper.stamper);
            pdfStamper.sigApp.setTempFile(file);
        }
        pdfStamper.sigApp.setOriginalout(outputStream);
        pdfStamper.sigApp.setStamper(pdfStamper);
        pdfStamper.hasSignature = true;
        object = pdfReader.getCatalog();
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(object.get(PdfName.ACROFORM), (PdfObject)object);
        if (pdfDictionary != null) {
            pdfDictionary.remove(PdfName.NEEDAPPEARANCES);
            pdfStamper.stamper.markUsed(pdfDictionary);
        }
        return pdfStamper;
    }

    public static PdfStamper createSignature(PdfReader pdfReader, OutputStream outputStream, char c) throws DocumentException, IOException {
        return PdfStamper.createSignature(pdfReader, outputStream, c, null, false);
    }

    public static PdfStamper createSignature(PdfReader pdfReader, OutputStream outputStream, char c, File file) throws DocumentException, IOException {
        return PdfStamper.createSignature(pdfReader, outputStream, c, file, false);
    }

    public Map getPdfLayers() {
        return this.stamper.getPdfLayers();
    }
}

