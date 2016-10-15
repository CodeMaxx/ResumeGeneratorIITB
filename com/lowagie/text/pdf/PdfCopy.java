/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocListener;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfContents;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfPages;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PdfCopy
extends PdfWriter {
    protected HashMap indirects;
    protected HashMap indirectMap;
    protected int currentObjectNum = 1;
    protected PdfReader reader;
    protected PdfIndirectReference acroForm;
    protected int[] namePtr = new int[]{0};
    private boolean rotateContents = true;
    protected PdfArray fieldArray;
    protected HashMap fieldTemplates;

    public PdfCopy(Document document, OutputStream outputStream) throws DocumentException {
        super(new PdfDocument(), outputStream);
        document.addDocListener(this.pdf);
        this.pdf.addWriter(this);
        this.indirectMap = new HashMap();
    }

    public boolean isRotateContents() {
        return this.rotateContents;
    }

    public void setRotateContents(boolean bl) {
        this.rotateContents = bl;
    }

    public PdfImportedPage getImportedPage(PdfReader pdfReader, int n) {
        if (this.currentPdfReaderInstance != null) {
            if (this.currentPdfReaderInstance.getReader() != pdfReader) {
                try {
                    this.currentPdfReaderInstance.getReader().close();
                    this.currentPdfReaderInstance.getReaderFile().close();
                }
                catch (IOException var3_3) {
                    // empty catch block
                }
                this.currentPdfReaderInstance = pdfReader.getPdfReaderInstance(this);
            }
        } else {
            this.currentPdfReaderInstance = pdfReader.getPdfReaderInstance(this);
        }
        return this.currentPdfReaderInstance.getImportedPage(n);
    }

    protected PdfIndirectReference copyIndirect(PRIndirectReference pRIndirectReference) throws IOException, BadPdfFormatException {
        PdfObject pdfObject;
        PdfObject pdfObject2;
        PdfIndirectReference pdfIndirectReference;
        RefKey refKey = new RefKey(pRIndirectReference);
        IndirectReferences indirectReferences = (IndirectReferences)this.indirects.get(refKey);
        if (indirectReferences != null) {
            pdfIndirectReference = indirectReferences.getRef();
            if (indirectReferences.getCopied()) {
                return pdfIndirectReference;
            }
        } else {
            pdfIndirectReference = this.body.getPdfIndirectReference();
            indirectReferences = new IndirectReferences(pdfIndirectReference);
            this.indirects.put(refKey, indirectReferences);
        }
        if ((pdfObject2 = PdfReader.getPdfObjectRelease(pRIndirectReference)) != null && pdfObject2.isDictionary() && (pdfObject = PdfReader.getPdfObjectRelease(((PdfDictionary)pdfObject2).get(PdfName.TYPE))) != null && PdfName.PAGE.equals(pdfObject)) {
            return pdfIndirectReference;
        }
        indirectReferences.setCopied();
        pdfObject2 = this.copyObject(pdfObject2);
        this.addToBody(pdfObject2, pdfIndirectReference);
        return pdfIndirectReference;
    }

    protected PdfDictionary copyDictionary(PdfDictionary pdfDictionary) throws IOException, BadPdfFormatException {
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.TYPE));
        Iterator iterator = pdfDictionary.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            PdfObject pdfObject2 = pdfDictionary.get(pdfName);
            if (pdfObject != null && PdfName.PAGE.equals(pdfObject)) {
                if (pdfName.equals(PdfName.B) || pdfName.equals(PdfName.PARENT)) continue;
                pdfDictionary2.put(pdfName, this.copyObject(pdfObject2));
                continue;
            }
            pdfDictionary2.put(pdfName, this.copyObject(pdfObject2));
        }
        return pdfDictionary2;
    }

    protected PdfStream copyStream(PRStream pRStream) throws IOException, BadPdfFormatException {
        PRStream pRStream2 = new PRStream(pRStream, null);
        Iterator iterator = pRStream.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            PdfObject pdfObject = pRStream.get(pdfName);
            pRStream2.put(pdfName, this.copyObject(pdfObject));
        }
        return pRStream2;
    }

    protected PdfArray copyArray(PdfArray pdfArray) throws IOException, BadPdfFormatException {
        PdfArray pdfArray2 = new PdfArray();
        Iterator iterator = pdfArray.getArrayList().iterator();
        while (iterator.hasNext()) {
            PdfObject pdfObject = (PdfObject)iterator.next();
            pdfArray2.add(this.copyObject(pdfObject));
        }
        return pdfArray2;
    }

    protected PdfObject copyObject(PdfObject pdfObject) throws IOException, BadPdfFormatException {
        if (pdfObject == null) {
            return PdfNull.PDFNULL;
        }
        switch (pdfObject.type) {
            case 6: {
                return this.copyDictionary((PdfDictionary)pdfObject);
            }
            case 10: {
                return this.copyIndirect((PRIndirectReference)pdfObject);
            }
            case 5: {
                return this.copyArray((PdfArray)pdfObject);
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 8: {
                return pdfObject;
            }
            case 7: {
                return this.copyStream((PRStream)pdfObject);
            }
        }
        if (pdfObject.type < 0) {
            String string = ((PdfLiteral)pdfObject).toString();
            if (string.equals("true") || string.equals("false")) {
                return new PdfBoolean(string);
            }
            return new PdfLiteral(string);
        }
        System.out.println("CANNOT COPY type " + pdfObject.type);
        return null;
    }

    protected int setFromIPage(PdfImportedPage pdfImportedPage) {
        int n = pdfImportedPage.getPageNumber();
        PdfReaderInstance pdfReaderInstance = this.currentPdfReaderInstance = pdfImportedPage.getPdfReaderInstance();
        this.reader = pdfReaderInstance.getReader();
        this.setFromReader(this.reader);
        return n;
    }

    protected void setFromReader(PdfReader pdfReader) {
        this.reader = pdfReader;
        this.indirects = (HashMap)this.indirectMap.get(pdfReader);
        if (this.indirects == null) {
            this.indirects = new HashMap();
            this.indirectMap.put(pdfReader, this.indirects);
            PdfDictionary pdfDictionary = pdfReader.getCatalog();
            PRIndirectReference pRIndirectReference = null;
            PdfObject pdfObject = pdfDictionary.get(PdfName.ACROFORM);
            if (pdfObject == null || pdfObject.type() != 10) {
                return;
            }
            pRIndirectReference = (PRIndirectReference)pdfObject;
            if (this.acroForm == null) {
                this.acroForm = this.body.getPdfIndirectReference();
            }
            this.indirects.put(new RefKey(pRIndirectReference), new IndirectReferences(this.acroForm));
        }
    }

    public void addPage(PdfImportedPage pdfImportedPage) throws IOException, BadPdfFormatException {
        int n = this.setFromIPage(pdfImportedPage);
        PdfDictionary pdfDictionary = this.reader.getPageN(n);
        PRIndirectReference pRIndirectReference = this.reader.getPageOrigRef(n);
        this.reader.releasePage(n);
        RefKey refKey = new RefKey(pRIndirectReference);
        IndirectReferences indirectReferences = (IndirectReferences)this.indirects.get(refKey);
        if (indirectReferences != null && !indirectReferences.getCopied()) {
            this.pageReferences.add(indirectReferences.getRef());
            indirectReferences.setCopied();
        }
        PdfIndirectReference pdfIndirectReference = this.getCurrentPage();
        if (indirectReferences == null) {
            indirectReferences = new IndirectReferences(pdfIndirectReference);
            this.indirects.put(refKey, indirectReferences);
        }
        indirectReferences.setCopied();
        PdfDictionary pdfDictionary2 = this.copyDictionary(pdfDictionary);
        this.root.addPage(pdfDictionary2);
        ++this.currentPageNumber;
    }

    public void copyAcroForm(PdfReader pdfReader) throws IOException, BadPdfFormatException {
        PdfIndirectReference pdfIndirectReference;
        this.setFromReader(pdfReader);
        PdfDictionary pdfDictionary = pdfReader.getCatalog();
        PRIndirectReference pRIndirectReference = null;
        PdfObject pdfObject = pdfDictionary.get(PdfName.ACROFORM);
        if (pdfObject != null && pdfObject.type() == 10) {
            pRIndirectReference = (PRIndirectReference)pdfObject;
        }
        if (pRIndirectReference == null) {
            return;
        }
        RefKey refKey = new RefKey(pRIndirectReference);
        IndirectReferences indirectReferences = (IndirectReferences)this.indirects.get(refKey);
        if (indirectReferences != null) {
            this.acroForm = pdfIndirectReference = indirectReferences.getRef();
        } else {
            this.acroForm = pdfIndirectReference = this.body.getPdfIndirectReference();
            indirectReferences = new IndirectReferences(pdfIndirectReference);
            this.indirects.put(refKey, indirectReferences);
        }
        if (!indirectReferences.getCopied()) {
            indirectReferences.setCopied();
            PdfDictionary pdfDictionary2 = this.copyDictionary((PdfDictionary)PdfReader.getPdfObject(pRIndirectReference));
            this.addToBody((PdfObject)pdfDictionary2, pdfIndirectReference);
        }
    }

    protected PdfDictionary getCatalog(PdfIndirectReference pdfIndirectReference) {
        try {
            PdfDocument.PdfCatalog pdfCatalog = this.pdf.getCatalog(pdfIndirectReference);
            if (this.fieldArray == null) {
                if (this.acroForm != null) {
                    pdfCatalog.put(PdfName.ACROFORM, this.acroForm);
                }
            } else {
                this.addFieldResources(pdfCatalog);
            }
            this.writeOutlines(pdfCatalog, false);
            return pdfCatalog;
        }
        catch (IOException var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    private void addFieldResources(PdfDictionary pdfDictionary) throws IOException {
        Object object;
        if (this.fieldArray == null) {
            return;
        }
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary.put(PdfName.ACROFORM, pdfDictionary2);
        pdfDictionary2.put(PdfName.FIELDS, this.fieldArray);
        pdfDictionary2.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        if (this.fieldTemplates.isEmpty()) {
            return;
        }
        PdfDictionary pdfDictionary3 = new PdfDictionary();
        pdfDictionary2.put(PdfName.DR, pdfDictionary3);
        Object object2 = this.fieldTemplates.keySet().iterator();
        while (object2.hasNext()) {
            object = (PdfTemplate)object2.next();
            PdfFormField.mergeResources(pdfDictionary3, (PdfDictionary)object.getResources());
        }
        if (pdfDictionary3.get(PdfName.ENCODING) == null) {
            pdfDictionary3.put(PdfName.ENCODING, PdfName.WIN_ANSI_ENCODING);
        }
        if ((object2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.FONT))) == null) {
            object2 = new PdfDictionary();
            pdfDictionary3.put(PdfName.FONT, (PdfObject)object2);
        }
        if (!object2.contains(PdfName.HELV)) {
            object = new PdfDictionary(PdfName.FONT);
            object.put(PdfName.BASEFONT, PdfName.HELVETICA);
            object.put(PdfName.ENCODING, PdfName.WIN_ANSI_ENCODING);
            object.put(PdfName.NAME, PdfName.HELV);
            object.put(PdfName.SUBTYPE, PdfName.TYPE1);
            object2.put(PdfName.HELV, this.addToBody((PdfObject)object).getIndirectReference());
        }
        if (!object2.contains(PdfName.ZADB)) {
            object = new PdfDictionary(PdfName.FONT);
            object.put(PdfName.BASEFONT, PdfName.ZAPFDINGBATS);
            object.put(PdfName.NAME, PdfName.ZADB);
            object.put(PdfName.SUBTYPE, PdfName.TYPE1);
            object2.put(PdfName.ZADB, this.addToBody((PdfObject)object).getIndirectReference());
        }
    }

    public void close() {
        if (this.open) {
            PdfReaderInstance pdfReaderInstance = this.currentPdfReaderInstance;
            this.pdf.close();
            super.close();
            if (pdfReaderInstance != null) {
                try {
                    pdfReaderInstance.getReader().close();
                    pdfReaderInstance.getReaderFile().close();
                }
                catch (IOException var2_2) {
                    // empty catch block
                }
            }
        }
    }

    public PdfIndirectReference add(PdfOutline pdfOutline) {
        return null;
    }

    public void addAnnotation(PdfAnnotation pdfAnnotation) {
    }

    PdfIndirectReference add(PdfPage pdfPage, PdfContents pdfContents) throws PdfException {
        return null;
    }

    public void freeReader(PdfReader pdfReader) throws IOException {
        this.indirectMap.remove(pdfReader);
        if (this.currentPdfReaderInstance != null && this.currentPdfReaderInstance.getReader() == pdfReader) {
            try {
                this.currentPdfReaderInstance.getReader().close();
                this.currentPdfReaderInstance.getReaderFile().close();
            }
            catch (IOException var2_2) {
                // empty catch block
            }
            this.currentPdfReaderInstance = null;
        }
    }

    public PageStamp createPageStamp(PdfImportedPage pdfImportedPage) {
        int n = pdfImportedPage.getPageNumber();
        PdfReader pdfReader = pdfImportedPage.getPdfReaderInstance().getReader();
        PdfDictionary pdfDictionary = pdfReader.getPageN(n);
        return new PageStamp(pdfReader, pdfDictionary, this);
    }

    public static class StampContent
    extends PdfContentByte {
        PageResources pageResources;

        StampContent(PdfWriter pdfWriter, PageResources pageResources) {
            super(pdfWriter);
            this.pageResources = pageResources;
        }

        public PdfContentByte getDuplicate() {
            return new StampContent(this.writer, this.pageResources);
        }

        PageResources getPageResources() {
            return this.pageResources;
        }
    }

    public static class PageStamp {
        PdfDictionary pageN;
        StampContent under;
        StampContent over;
        PageResources pageResources;
        PdfReader reader;
        PdfCopy cstp;

        PageStamp(PdfReader pdfReader, PdfDictionary pdfDictionary, PdfCopy pdfCopy) {
            this.pageN = pdfDictionary;
            this.reader = pdfReader;
            this.cstp = pdfCopy;
        }

        public PdfContentByte getUnderContent() {
            if (this.under == null) {
                if (this.pageResources == null) {
                    this.pageResources = new PageResources();
                    PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.pageN.get(PdfName.RESOURCES));
                    this.pageResources.setOriginalResources(pdfDictionary, this.cstp.namePtr);
                }
                this.under = new StampContent(this.cstp, this.pageResources);
            }
            return this.under;
        }

        public PdfContentByte getOverContent() {
            if (this.over == null) {
                if (this.pageResources == null) {
                    this.pageResources = new PageResources();
                    PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.pageN.get(PdfName.RESOURCES));
                    this.pageResources.setOriginalResources(pdfDictionary, this.cstp.namePtr);
                }
                this.over = new StampContent(this.cstp, this.pageResources);
            }
            return this.over;
        }

        public void alterContents() throws IOException {
            if (this.over == null && this.under == null) {
                return;
            }
            PdfArray pdfArray = null;
            PdfObject pdfObject = PdfReader.getPdfObject(this.pageN.get(PdfName.CONTENTS), this.pageN);
            if (pdfObject == null) {
                pdfArray = new PdfArray();
                this.pageN.put(PdfName.CONTENTS, pdfArray);
            } else if (pdfObject.isArray()) {
                pdfArray = (PdfArray)pdfObject;
            } else if (pdfObject.isStream()) {
                pdfArray = new PdfArray();
                pdfArray.add(this.pageN.get(PdfName.CONTENTS));
                this.pageN.put(PdfName.CONTENTS, pdfArray);
            } else {
                pdfArray = new PdfArray();
                this.pageN.put(PdfName.CONTENTS, pdfArray);
            }
            ByteBuffer byteBuffer = new ByteBuffer();
            if (this.under != null) {
                byteBuffer.append(PdfContents.SAVESTATE);
                this.applyRotation(this.pageN, byteBuffer);
                byteBuffer.append(this.under.getInternalBuffer());
                byteBuffer.append(PdfContents.RESTORESTATE);
            }
            if (this.over != null) {
                byteBuffer.append(PdfContents.SAVESTATE);
            }
            PdfStream pdfStream = new PdfStream(byteBuffer.toByteArray());
            pdfStream.flateCompress(this.cstp.getCompressionLevel());
            PdfIndirectReference pdfIndirectReference = this.cstp.addToBody(pdfStream).getIndirectReference();
            pdfArray.addFirst(pdfIndirectReference);
            byteBuffer.reset();
            if (this.over != null) {
                byteBuffer.append(' ');
                byteBuffer.append(PdfContents.RESTORESTATE);
                byteBuffer.append(PdfContents.SAVESTATE);
                this.applyRotation(this.pageN, byteBuffer);
                byteBuffer.append(this.over.getInternalBuffer());
                byteBuffer.append(PdfContents.RESTORESTATE);
                pdfStream = new PdfStream(byteBuffer.toByteArray());
                pdfStream.flateCompress(this.cstp.getCompressionLevel());
                pdfArray.add(this.cstp.addToBody(pdfStream).getIndirectReference());
            }
            this.pageN.put(PdfName.RESOURCES, this.pageResources.getResources());
        }

        void applyRotation(PdfDictionary pdfDictionary, ByteBuffer byteBuffer) {
            if (!this.cstp.rotateContents) {
                return;
            }
            Rectangle rectangle = this.reader.getPageSizeWithRotation(pdfDictionary);
            int n = rectangle.getRotation();
            switch (n) {
                case 90: {
                    byteBuffer.append(PdfContents.ROTATE90);
                    byteBuffer.append(rectangle.getTop());
                    byteBuffer.append(' ').append('0').append(PdfContents.ROTATEFINAL);
                    break;
                }
                case 180: {
                    byteBuffer.append(PdfContents.ROTATE180);
                    byteBuffer.append(rectangle.getRight());
                    byteBuffer.append(' ');
                    byteBuffer.append(rectangle.getTop());
                    byteBuffer.append(PdfContents.ROTATEFINAL);
                    break;
                }
                case 270: {
                    byteBuffer.append(PdfContents.ROTATE270);
                    byteBuffer.append('0').append(' ');
                    byteBuffer.append(rectangle.getRight());
                    byteBuffer.append(PdfContents.ROTATEFINAL);
                }
            }
        }

        private void addDocumentField(PdfIndirectReference pdfIndirectReference) {
            if (this.cstp.fieldArray == null) {
                this.cstp.fieldArray = new PdfArray();
            }
            this.cstp.fieldArray.add(pdfIndirectReference);
        }

        private void expandFields(PdfFormField pdfFormField, ArrayList arrayList) {
            arrayList.add(pdfFormField);
            ArrayList arrayList2 = pdfFormField.getKids();
            if (arrayList2 != null) {
                for (int i = 0; i < arrayList2.size(); ++i) {
                    this.expandFields((PdfFormField)arrayList2.get(i), arrayList);
                }
            }
        }

        public void addAnnotation(PdfAnnotation pdfAnnotation) {
            try {
                ArrayList<PdfAnnotation> arrayList = new ArrayList<PdfAnnotation>();
                if (pdfAnnotation.isForm()) {
                    PdfFormField pdfFormField = (PdfFormField)pdfAnnotation;
                    if (pdfFormField.getParent() != null) {
                        return;
                    }
                    this.expandFields(pdfFormField, arrayList);
                    if (this.cstp.fieldTemplates == null) {
                        this.cstp.fieldTemplates = new HashMap();
                    }
                } else {
                    arrayList.add(pdfAnnotation);
                }
                for (int i = 0; i < arrayList.size(); ++i) {
                    Object object;
                    pdfAnnotation = (PdfAnnotation)arrayList.get(i);
                    if (pdfAnnotation.isForm()) {
                        if (!pdfAnnotation.isUsed() && (object = pdfAnnotation.getTemplates()) != null) {
                            this.cstp.fieldTemplates.putAll(object);
                        }
                        if ((object = (PdfFormField)pdfAnnotation).getParent() == null) {
                            this.addDocumentField(object.getIndirectReference());
                        }
                    }
                    if (pdfAnnotation.isAnnotation()) {
                        PdfRectangle pdfRectangle;
                        object = PdfReader.getPdfObject(this.pageN.get(PdfName.ANNOTS), this.pageN);
                        PdfArray pdfArray = null;
                        if (object == null || !object.isArray()) {
                            pdfArray = new PdfArray();
                            this.pageN.put(PdfName.ANNOTS, pdfArray);
                        } else {
                            pdfArray = (PdfArray)object;
                        }
                        pdfArray.add(pdfAnnotation.getIndirectReference());
                        if (!(pdfAnnotation.isUsed() || (pdfRectangle = (PdfRectangle)pdfAnnotation.get(PdfName.RECT)) == null || pdfRectangle.left() == 0.0f && pdfRectangle.right() == 0.0f && pdfRectangle.top() == 0.0f && pdfRectangle.bottom() == 0.0f)) {
                            int n = this.reader.getPageRotation(this.pageN);
                            Rectangle rectangle = this.reader.getPageSizeWithRotation(this.pageN);
                            switch (n) {
                                case 90: {
                                    pdfAnnotation.put(PdfName.RECT, new PdfRectangle(rectangle.getTop() - pdfRectangle.bottom(), pdfRectangle.left(), rectangle.getTop() - pdfRectangle.top(), pdfRectangle.right()));
                                    break;
                                }
                                case 180: {
                                    pdfAnnotation.put(PdfName.RECT, new PdfRectangle(rectangle.getRight() - pdfRectangle.left(), rectangle.getTop() - pdfRectangle.bottom(), rectangle.getRight() - pdfRectangle.right(), rectangle.getTop() - pdfRectangle.top()));
                                    break;
                                }
                                case 270: {
                                    pdfAnnotation.put(PdfName.RECT, new PdfRectangle(pdfRectangle.bottom(), rectangle.getRight() - pdfRectangle.left(), pdfRectangle.top(), rectangle.getRight() - pdfRectangle.right()));
                                }
                            }
                        }
                    }
                    if (pdfAnnotation.isUsed()) continue;
                    pdfAnnotation.setUsed();
                    this.cstp.addToBody((PdfObject)pdfAnnotation, pdfAnnotation.getIndirectReference());
                }
            }
            catch (IOException var2_3) {
                throw new ExceptionConverter(var2_3);
            }
        }
    }

    protected static class RefKey {
        int num;
        int gen;

        RefKey(int n, int n2) {
            this.num = n;
            this.gen = n2;
        }

        RefKey(PdfIndirectReference pdfIndirectReference) {
            this.num = pdfIndirectReference.getNumber();
            this.gen = pdfIndirectReference.getGeneration();
        }

        RefKey(PRIndirectReference pRIndirectReference) {
            this.num = pRIndirectReference.getNumber();
            this.gen = pRIndirectReference.getGeneration();
        }

        public int hashCode() {
            return (this.gen << 16) + this.num;
        }

        public boolean equals(Object object) {
            if (!(object instanceof RefKey)) {
                return false;
            }
            RefKey refKey = (RefKey)object;
            return this.gen == refKey.gen && this.num == refKey.num;
        }

        public String toString() {
            return Integer.toString(this.num) + ' ' + this.gen;
        }
    }

    static class IndirectReferences {
        PdfIndirectReference theRef;
        boolean hasCopied;

        IndirectReferences(PdfIndirectReference pdfIndirectReference) {
            this.theRef = pdfIndirectReference;
            this.hasCopied = false;
        }

        void setCopied() {
            this.hasCopied = true;
        }

        boolean getCopied() {
            return this.hasCopied;
        }

        PdfIndirectReference getRef() {
            return this.theRef;
        }
    }

}

