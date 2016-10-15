/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.FdfReader;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfContents;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfOCProperties;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.StampContent;
import com.lowagie.text.pdf.XfaForm;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.internal.PdfVersionImp;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import com.lowagie.text.xml.xmp.XmpReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

class PdfStamperImp
extends PdfWriter {
    HashMap readers2intrefs = new HashMap();
    HashMap readers2file = new HashMap();
    RandomAccessFileOrArray file;
    PdfReader reader;
    IntHashtable myXref = new IntHashtable();
    HashMap pagesToContent = new HashMap();
    boolean closed = false;
    private boolean rotateContents = true;
    protected AcroFields acroFields;
    protected boolean flat = false;
    protected boolean flatFreeText = false;
    protected int[] namePtr = new int[]{0};
    protected HashSet partialFlattening = new HashSet();
    protected boolean useVp = false;
    protected PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
    protected HashMap fieldTemplates = new HashMap();
    protected boolean fieldsAdded = false;
    protected int sigFlags = 0;
    protected boolean append;
    protected IntHashtable marked;
    protected int initialXrefSize;
    protected PdfAction openAction;

    PdfStamperImp(PdfReader pdfReader, OutputStream outputStream, char c, boolean bl) throws DocumentException, IOException {
        super(new PdfDocument(), outputStream);
        if (!pdfReader.isOpenedWithFullPermissions()) {
            throw new IllegalArgumentException("PdfReader not opened with owner password");
        }
        if (pdfReader.isTampered()) {
            throw new DocumentException("The original document was reused. Read it again from file.");
        }
        pdfReader.setTampered(true);
        this.reader = pdfReader;
        this.file = pdfReader.getSafeFile();
        this.append = bl;
        if (bl) {
            int n;
            if (pdfReader.isRebuilt()) {
                throw new DocumentException("Append mode requires a document without errors even if recovery was possible.");
            }
            if (pdfReader.isEncrypted()) {
                this.crypto = new PdfEncryption(pdfReader.getDecrypt());
            }
            this.pdf_version.setAppendmode(true);
            this.file.reOpen();
            byte[] arrby = new byte[8192];
            while ((n = this.file.read(arrby)) > 0) {
                this.os.write(arrby, 0, n);
            }
            this.file.close();
            this.prevxref = pdfReader.getLastXref();
            pdfReader.setAppendable(true);
        } else if (c == '\u0000') {
            super.setPdfVersion(pdfReader.getPdfVersion());
        } else {
            super.setPdfVersion(c);
        }
        super.open();
        this.pdf.addWriter(this);
        if (bl) {
            this.body.setRefnum(pdfReader.getXrefSize());
            this.marked = new IntHashtable();
            if (pdfReader.isNewXrefType()) {
                this.fullCompression = true;
            }
            if (pdfReader.isHybridXref()) {
                this.fullCompression = false;
            }
        }
        this.initialXrefSize = pdfReader.getXrefSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void close(HashMap hashMap) throws IOException {
        Object object;
        PdfDate pdfDate;
        Object object5;
        void var8_18;
        Object object2;
        PdfObject pdfObject;
        Object object3;
        Object object4;
        if (this.closed) {
            return;
        }
        if (this.useVp) {
            this.reader.setViewerPreferences(this.viewerPreferences);
            this.markUsed(this.reader.getTrailer().get(PdfName.ROOT));
        }
        if (this.flat) {
            this.flatFields();
        }
        if (this.flatFreeText) {
            this.flatFreeTextFields();
        }
        this.addFieldResources();
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM), this.reader.getCatalog());
        if (this.acroFields != null && this.acroFields.getXfa().isChanged()) {
            this.markUsed(pdfDictionary);
            if (!this.flat) {
                this.acroFields.getXfa().setXfa(this);
            }
        }
        if (this.sigFlags != 0 && pdfDictionary != null) {
            pdfDictionary.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
            this.markUsed(pdfDictionary);
        }
        this.closed = true;
        this.addSharedObjectsToBody();
        this.setOutlines();
        this.setJavaScript();
        this.addFileAttachments();
        PdfDictionary pdfDictionary2 = this.reader.getCatalog();
        if (this.openAction != null) {
            pdfDictionary2.put(PdfName.OPENACTION, this.openAction);
        }
        if (this.pdf.pageLabels != null) {
            pdfDictionary2.put(PdfName.PAGELABELS, this.pdf.pageLabels.getDictionary(this));
        }
        byte[] arrby = null;
        PdfObject pdfObject2 = PdfReader.getPdfObject(pdfDictionary2.get(PdfName.METADATA));
        if (pdfObject2 != null && pdfObject2.isStream()) {
            arrby = PdfReader.getStreamBytesRaw((PRStream)pdfObject2);
            PdfReader.killIndirect(pdfDictionary2.get(PdfName.METADATA));
        }
        if (this.xmpMetadata != null) {
            arrby = this.xmpMetadata;
        }
        pdfDate = new PdfDate();
        if (arrby != null) {
            object5 = new XmpReader(arrby);
            object5.replace("http://ns.adobe.com/xap/1.0/", "ModifyDate", pdfDate.toString());
            object5.replace("http://ns.adobe.com/xap/1.0/", "MetadataDate", pdfDate.toString());
            PdfStream pdfStream = new PdfStream(object5.serializeDoc());
            pdfStream.put(PdfName.TYPE, PdfName.METADATA);
            pdfStream.put(PdfName.SUBTYPE, PdfName.XML);
            if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
                PdfArray pdfArray = new PdfArray();
                pdfArray.add(PdfName.CRYPT);
                pdfStream.put(PdfName.FILTER, pdfArray);
            }
            if (this.append && pdfObject2 != null) {
                this.body.add((PdfObject)pdfStream, pdfObject2.getIndRef());
            } else {
                pdfDictionary2.put(PdfName.METADATA, this.body.add(pdfStream).getIndirectReference());
                this.markUsed(pdfDictionary2);
            }
        }
        if (!this.documentOCG.isEmpty()) {
            this.fillOCProperties(false);
            object5 = pdfDictionary2.getAsDict(PdfName.OCPROPERTIES);
            if (object5 == null) {
                this.reader.getCatalog().put(PdfName.OCPROPERTIES, this.OCProperties);
            } else {
                object5.put(PdfName.OCGS, this.OCProperties.get(PdfName.OCGS));
                PdfDictionary n = object5.getAsDict(PdfName.D);
                n.put(PdfName.ORDER, this.OCProperties.getAsDict(PdfName.D).get(PdfName.ORDER));
                n.put(PdfName.RBGROUPS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.RBGROUPS));
                n.put(PdfName.OFF, this.OCProperties.getAsDict(PdfName.D).get(PdfName.OFF));
                n.put(PdfName.AS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.AS));
            }
        }
        object5 = null;
        try {
            int n;
            this.file.reOpen();
            this.alterContents();
            object5 = (PRIndirectReference)this.reader.trailer.get(PdfName.INFO);
            int n2 = -1;
            if (object5 != null) {
                n = object5.getNumber();
            }
            int n3 = ((PRIndirectReference)this.reader.trailer.get(PdfName.ROOT)).getNumber();
            if (this.append) {
                int n4;
                object2 = this.marked.getKeys();
                for (n4 = 0; n4 < object2.length; ++n4) {
                    int n5 = object2[n4];
                    pdfObject = this.reader.getPdfObjectRelease(n5);
                    if (pdfObject == null || n == n5 || n5 >= this.initialXrefSize) continue;
                    this.addToBody(pdfObject, n5, n5 != n3);
                }
                for (n4 = this.initialXrefSize; n4 < this.reader.getXrefSize(); ++n4) {
                    PdfObject pdfObject3 = this.reader.getPdfObject(n4);
                    if (pdfObject3 == null) continue;
                    this.addToBody(pdfObject3, this.getNewObjectNumber(this.reader, n4, 0));
                }
            } else {
                for (int i = 1; i < this.reader.getXrefSize(); ++i) {
                    PdfObject pdfObject4 = this.reader.getPdfObjectRelease(i);
                    if (pdfObject4 == null || n == i) continue;
                    this.addToBody(pdfObject4, this.getNewObjectNumber(this.reader, i, 0), i != n3);
                }
            }
        }
        finally {
            try {
                this.file.close();
            }
            catch (Exception var8_13) {}
        }
        Object var8_14 = null;
        PdfObject pdfObject5 = null;
        if (this.crypto != null) {
            if (this.append) {
                PdfIndirectReference pdfIndirectReference = this.reader.getCryptoRef();
            } else {
                object2 = this.addToBody((PdfObject)this.crypto.getEncryptionDictionary(), false);
                PdfIndirectReference pdfIndirectReference = object2.getIndirectReference();
            }
            pdfObject5 = this.crypto.getFileID();
        } else {
            pdfObject5 = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
        }
        object2 = (PRIndirectReference)this.reader.trailer.get(PdfName.ROOT);
        PdfIndirectReference pdfIndirectReference = new PdfIndirectReference(0, this.getNewObjectNumber(this.reader, object2.getNumber(), 0));
        PdfIndirectReference pdfIndirectReference2 = null;
        pdfObject = (PdfDictionary)PdfReader.getPdfObject((PdfObject)object5);
        PdfDictionary pdfDictionary = new PdfDictionary();
        if (pdfObject != null) {
            object3 = pdfObject.getKeys().iterator();
            while (object3.hasNext()) {
                object = (PdfName)object3.next();
                object4 = PdfReader.getPdfObject(pdfObject.get((PdfName)object));
                pdfDictionary.put((PdfName)object, (PdfObject)object4);
            }
        }
        if (hashMap != null) {
            object3 = hashMap.entrySet().iterator();
            while (object3.hasNext()) {
                object = (Map.Entry)object3.next();
                object4 = (String)object.getKey();
                PdfName pdfName = new PdfName((String)object4);
                String string = (String)object.getValue();
                if (string == null) {
                    pdfDictionary.remove(pdfName);
                    continue;
                }
                pdfDictionary.put(pdfName, new PdfString(string, "UnicodeBig"));
            }
        }
        pdfDictionary.put(PdfName.MODDATE, pdfDate);
        pdfIndirectReference2 = this.append ? (object5 == null ? this.addToBody((PdfObject)pdfDictionary, false).getIndirectReference() : this.addToBody((PdfObject)pdfDictionary, object5.getNumber(), false).getIndirectReference()) : this.addToBody((PdfObject)pdfDictionary, false).getIndirectReference();
        this.body.writeCrossReferenceTable(this.os, pdfIndirectReference, pdfIndirectReference2, (PdfIndirectReference)var8_18, pdfObject5, this.prevxref);
        if (this.fullCompression) {
            this.os.write(PdfStamperImp.getISOBytes("startxref\n"));
            this.os.write(PdfStamperImp.getISOBytes(String.valueOf(this.body.offset())));
            this.os.write(PdfStamperImp.getISOBytes("\n%%EOF\n"));
        } else {
            object3 = new PdfWriter.PdfTrailer(this.body.size(), this.body.offset(), pdfIndirectReference, pdfIndirectReference2, (PdfIndirectReference)var8_18, pdfObject5, this.prevxref);
            object3.toPdf(this, this.os);
        }
        this.os.flush();
        if (this.isCloseStream()) {
            this.os.close();
        }
        this.reader.close();
    }

    void applyRotation(PdfDictionary pdfDictionary, ByteBuffer byteBuffer) {
        if (!this.rotateContents) {
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

    void alterContents() throws IOException {
        Iterator iterator = this.pagesToContent.values().iterator();
        while (iterator.hasNext()) {
            PageStamp pageStamp = (PageStamp)iterator.next();
            PdfDictionary pdfDictionary = pageStamp.pageN;
            this.markUsed(pdfDictionary);
            PdfArray pdfArray = null;
            PdfObject pdfObject = PdfReader.getPdfObject(pdfDictionary.get(PdfName.CONTENTS), pdfDictionary);
            if (pdfObject == null) {
                pdfArray = new PdfArray();
                pdfDictionary.put(PdfName.CONTENTS, pdfArray);
            } else if (pdfObject.isArray()) {
                pdfArray = (PdfArray)pdfObject;
                this.markUsed(pdfArray);
            } else if (pdfObject.isStream()) {
                pdfArray = new PdfArray();
                pdfArray.add(pdfDictionary.get(PdfName.CONTENTS));
                pdfDictionary.put(PdfName.CONTENTS, pdfArray);
            } else {
                pdfArray = new PdfArray();
                pdfDictionary.put(PdfName.CONTENTS, pdfArray);
            }
            ByteBuffer byteBuffer = new ByteBuffer();
            if (pageStamp.under != null) {
                byteBuffer.append(PdfContents.SAVESTATE);
                this.applyRotation(pdfDictionary, byteBuffer);
                byteBuffer.append(pageStamp.under.getInternalBuffer());
                byteBuffer.append(PdfContents.RESTORESTATE);
            }
            if (pageStamp.over != null) {
                byteBuffer.append(PdfContents.SAVESTATE);
            }
            PdfStream pdfStream = new PdfStream(byteBuffer.toByteArray());
            pdfStream.flateCompress(this.compressionLevel);
            pdfArray.addFirst(this.addToBody(pdfStream).getIndirectReference());
            byteBuffer.reset();
            if (pageStamp.over != null) {
                byteBuffer.append(' ');
                byteBuffer.append(PdfContents.RESTORESTATE);
                ByteBuffer byteBuffer2 = pageStamp.over.getInternalBuffer();
                byteBuffer.append(byteBuffer2.getBuffer(), 0, pageStamp.replacePoint);
                byteBuffer.append(PdfContents.SAVESTATE);
                this.applyRotation(pdfDictionary, byteBuffer);
                byteBuffer.append(byteBuffer2.getBuffer(), pageStamp.replacePoint, byteBuffer2.size() - pageStamp.replacePoint);
                byteBuffer.append(PdfContents.RESTORESTATE);
                pdfStream = new PdfStream(byteBuffer.toByteArray());
                pdfStream.flateCompress(this.compressionLevel);
                pdfArray.add(this.addToBody(pdfStream).getIndirectReference());
            }
            this.alterResources(pageStamp);
        }
    }

    void alterResources(PageStamp pageStamp) {
        pageStamp.pageN.put(PdfName.RESOURCES, pageStamp.pageResources.getResources());
    }

    protected int getNewObjectNumber(PdfReader pdfReader, int n, int n2) {
        IntHashtable intHashtable = (IntHashtable)this.readers2intrefs.get(pdfReader);
        if (intHashtable != null) {
            int n3 = intHashtable.get(n);
            if (n3 == 0) {
                n3 = this.getIndirectReferenceNumber();
                intHashtable.put(n, n3);
            }
            return n3;
        }
        if (this.currentPdfReaderInstance == null) {
            if (this.append && n < this.initialXrefSize) {
                return n;
            }
            int n4 = this.myXref.get(n);
            if (n4 == 0) {
                n4 = this.getIndirectReferenceNumber();
                this.myXref.put(n, n4);
            }
            return n4;
        }
        return this.currentPdfReaderInstance.getNewObjectNumber(n, n2);
    }

    RandomAccessFileOrArray getReaderFile(PdfReader pdfReader) {
        if (this.readers2intrefs.containsKey(pdfReader)) {
            RandomAccessFileOrArray randomAccessFileOrArray = (RandomAccessFileOrArray)this.readers2file.get(pdfReader);
            if (randomAccessFileOrArray != null) {
                return randomAccessFileOrArray;
            }
            return pdfReader.getSafeFile();
        }
        if (this.currentPdfReaderInstance == null) {
            return this.file;
        }
        return this.currentPdfReaderInstance.getReaderFile();
    }

    public void registerReader(PdfReader pdfReader, boolean bl) throws IOException {
        if (this.readers2intrefs.containsKey(pdfReader)) {
            return;
        }
        this.readers2intrefs.put(pdfReader, new IntHashtable());
        if (bl) {
            RandomAccessFileOrArray randomAccessFileOrArray = pdfReader.getSafeFile();
            this.readers2file.put(pdfReader, randomAccessFileOrArray);
            randomAccessFileOrArray.reOpen();
        }
    }

    public void unRegisterReader(PdfReader pdfReader) {
        if (!this.readers2intrefs.containsKey(pdfReader)) {
            return;
        }
        this.readers2intrefs.remove(pdfReader);
        RandomAccessFileOrArray randomAccessFileOrArray = (RandomAccessFileOrArray)this.readers2file.get(pdfReader);
        if (randomAccessFileOrArray == null) {
            return;
        }
        this.readers2file.remove(pdfReader);
        try {
            randomAccessFileOrArray.close();
        }
        catch (Exception var3_3) {
            // empty catch block
        }
    }

    static void findAllObjects(PdfReader pdfReader, PdfObject pdfObject, IntHashtable intHashtable) {
        if (pdfObject == null) {
            return;
        }
        switch (pdfObject.type()) {
            case 10: {
                PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfObject;
                if (pdfReader != pRIndirectReference.getReader()) {
                    return;
                }
                if (intHashtable.containsKey(pRIndirectReference.getNumber())) {
                    return;
                }
                intHashtable.put(pRIndirectReference.getNumber(), 1);
                PdfStamperImp.findAllObjects(pdfReader, PdfReader.getPdfObject(pdfObject), intHashtable);
                return;
            }
            case 5: {
                ArrayList arrayList = ((PdfArray)pdfObject).getArrayList();
                for (int i = 0; i < arrayList.size(); ++i) {
                    PdfStamperImp.findAllObjects(pdfReader, (PdfObject)arrayList.get(i), intHashtable);
                }
                return;
            }
            case 6: 
            case 7: {
                PdfDictionary pdfDictionary = (PdfDictionary)pdfObject;
                Iterator iterator = pdfDictionary.getKeys().iterator();
                while (iterator.hasNext()) {
                    PdfName pdfName = (PdfName)iterator.next();
                    PdfStamperImp.findAllObjects(pdfReader, pdfDictionary.get(pdfName), intHashtable);
                }
                return;
            }
        }
    }

    public void addComments(FdfReader fdfReader) throws IOException {
        PdfDictionary pdfDictionary;
        PdfObject pdfObject;
        int n;
        if (this.readers2intrefs.containsKey(fdfReader)) {
            return;
        }
        PdfDictionary pdfDictionary4 = fdfReader.getCatalog();
        if ((pdfDictionary4 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary4.get(PdfName.FDF))) == null) {
            return;
        }
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary4.get(PdfName.ANNOTS));
        if (pdfArray == null || pdfArray.size() == 0) {
            return;
        }
        this.registerReader(fdfReader, false);
        IntHashtable intHashtable = new IntHashtable();
        HashMap<String, PdfObject> hashMap = new HashMap<String, PdfObject>();
        ArrayList<PdfObject> arrayList = new ArrayList<PdfObject>();
        ArrayList arrayList2 = pdfArray.getArrayList();
        for (int i = 0; i < arrayList2.size(); ++i) {
            PdfObject pdfObject2 = (PdfObject)arrayList2.get(i);
            PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pdfObject2);
            PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.PAGE));
            if (pdfNumber == null || pdfNumber.intValue() >= this.reader.getNumberOfPages()) continue;
            PdfStamperImp.findAllObjects(fdfReader, pdfObject2, intHashtable);
            arrayList.add(pdfObject2);
            if (pdfObject2.type() != 10 || (pdfObject = PdfReader.getPdfObject(pdfDictionary3.get(PdfName.NM))) == null || pdfObject.type() != 3) continue;
            hashMap.put(pdfObject.toString(), pdfObject2);
        }
        int[] arrn = intHashtable.getKeys();
        for (n = 0; n < arrn.length; ++n) {
            void var11_20;
            int n2 = arrn[n];
            PdfObject pdfObject2 = fdfReader.getPdfObject(n2);
            if (pdfObject2.type() == 6 && (pdfObject = PdfReader.getPdfObject(((PdfDictionary)pdfObject2).get(PdfName.IRT))) != null && pdfObject.type() == 3 && (pdfDictionary = (PdfObject)hashMap.get(pdfObject.toString())) != null) {
                PdfDictionary pdfDictionary2 = new PdfDictionary();
                pdfDictionary2.merge((PdfDictionary)pdfObject2);
                pdfDictionary2.put(PdfName.IRT, pdfDictionary);
                PdfDictionary pdfDictionary3 = pdfDictionary2;
            }
            this.addToBody((PdfObject)var11_20, this.getNewObjectNumber(fdfReader, n2, 0));
        }
        for (n = 0; n < arrayList.size(); ++n) {
            void var14_29;
            PdfObject pdfObject3 = (PdfObject)arrayList.get(n);
            PdfDictionary pdfDictionary5 = (PdfDictionary)PdfReader.getPdfObject(pdfObject3);
            pdfObject = (PdfNumber)PdfReader.getPdfObject(pdfDictionary5.get(PdfName.PAGE));
            pdfDictionary = this.reader.getPageN(pdfObject.intValue() + 1);
            PdfArray pdfArray2 = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ANNOTS), pdfDictionary);
            if (pdfArray2 == null) {
                PdfArray pdfArray3 = new PdfArray();
                pdfDictionary.put(PdfName.ANNOTS, pdfArray3);
                this.markUsed(pdfDictionary);
            }
            this.markUsed((PdfObject)var14_29);
            var14_29.add(pdfObject3);
        }
    }

    PageStamp getPageStamp(int n) {
        PdfDictionary pdfDictionary = this.reader.getPageN(n);
        PageStamp pageStamp = (PageStamp)this.pagesToContent.get(pdfDictionary);
        if (pageStamp == null) {
            pageStamp = new PageStamp(this, this.reader, pdfDictionary);
            this.pagesToContent.put(pdfDictionary, pageStamp);
        }
        return pageStamp;
    }

    PdfContentByte getUnderContent(int n) {
        if (n < 1 || n > this.reader.getNumberOfPages()) {
            return null;
        }
        PageStamp pageStamp = this.getPageStamp(n);
        if (pageStamp.under == null) {
            pageStamp.under = new StampContent(this, pageStamp);
        }
        return pageStamp.under;
    }

    PdfContentByte getOverContent(int n) {
        if (n < 1 || n > this.reader.getNumberOfPages()) {
            return null;
        }
        PageStamp pageStamp = this.getPageStamp(n);
        if (pageStamp.over == null) {
            pageStamp.over = new StampContent(this, pageStamp);
        }
        return pageStamp.over;
    }

    void correctAcroFieldPages(int n) {
        if (this.acroFields == null) {
            return;
        }
        if (n > this.reader.getNumberOfPages()) {
            return;
        }
        HashMap hashMap = this.acroFields.getFields();
        Iterator iterator = hashMap.values().iterator();
        while (iterator.hasNext()) {
            AcroFields.Item item = (AcroFields.Item)iterator.next();
            ArrayList arrayList = item.page;
            for (int i = 0; i < arrayList.size(); ++i) {
                int n2 = (Integer)arrayList.get(i);
                if (n2 < n) continue;
                arrayList.set(i, new Integer(n2 + 1));
            }
        }
    }

    private static void moveRectangle(PdfDictionary pdfDictionary, PdfReader pdfReader, int n, PdfName pdfName, String string) {
        Rectangle rectangle = pdfReader.getBoxSize(n, string);
        if (rectangle == null) {
            pdfDictionary.remove(pdfName);
        } else {
            pdfDictionary.put(pdfName, new PdfRectangle(rectangle));
        }
    }

    void replacePage(PdfReader pdfReader, int n, int n2) {
        PdfDictionary pdfDictionary = this.reader.getPageN(n2);
        if (this.pagesToContent.containsKey(pdfDictionary)) {
            throw new IllegalStateException("This page cannot be replaced: new content was already added");
        }
        PdfImportedPage pdfImportedPage = this.getImportedPage(pdfReader, n);
        PdfDictionary pdfDictionary2 = this.reader.getPageNRelease(n2);
        pdfDictionary2.remove(PdfName.RESOURCES);
        pdfDictionary2.remove(PdfName.CONTENTS);
        PdfStamperImp.moveRectangle(pdfDictionary2, pdfReader, n, PdfName.MEDIABOX, "media");
        PdfStamperImp.moveRectangle(pdfDictionary2, pdfReader, n, PdfName.CROPBOX, "crop");
        PdfStamperImp.moveRectangle(pdfDictionary2, pdfReader, n, PdfName.TRIMBOX, "trim");
        PdfStamperImp.moveRectangle(pdfDictionary2, pdfReader, n, PdfName.ARTBOX, "art");
        PdfStamperImp.moveRectangle(pdfDictionary2, pdfReader, n, PdfName.BLEEDBOX, "bleed");
        pdfDictionary2.put(PdfName.ROTATE, new PdfNumber(pdfReader.getPageRotation(n)));
        PdfContentByte pdfContentByte = this.getOverContent(n2);
        pdfContentByte.addTemplate(pdfImportedPage, 0.0f, 0.0f);
        PageStamp pageStamp = (PageStamp)this.pagesToContent.get(pdfDictionary);
        pageStamp.replacePoint = pageStamp.over.getInternalBuffer().size();
    }

    void insertPage(int n, Rectangle rectangle) {
        PdfIndirectReference pdfIndirectReference;
        PdfDictionary pdfDictionary;
        PRIndirectReference pRIndirectReference;
        Rectangle rectangle2 = new Rectangle(rectangle);
        int n2 = rectangle2.getRotation() % 360;
        PdfDictionary pdfDictionary3 = new PdfDictionary(PdfName.PAGE);
        PdfDictionary pdfDictionary4 = new PdfDictionary();
        PdfArray pdfArray = new PdfArray();
        pdfArray.add(PdfName.PDF);
        pdfArray.add(PdfName.TEXT);
        pdfArray.add(PdfName.IMAGEB);
        pdfArray.add(PdfName.IMAGEC);
        pdfArray.add(PdfName.IMAGEI);
        pdfDictionary4.put(PdfName.PROCSET, pdfArray);
        pdfDictionary3.put(PdfName.RESOURCES, pdfDictionary4);
        pdfDictionary3.put(PdfName.ROTATE, new PdfNumber(n2));
        pdfDictionary3.put(PdfName.MEDIABOX, new PdfRectangle(rectangle2, n2));
        PRIndirectReference pRIndirectReference2 = this.reader.addPdfObject(pdfDictionary3);
        if (n > this.reader.getNumberOfPages()) {
            PdfDictionary pdfDictionary2 = this.reader.getPageNRelease(this.reader.getNumberOfPages());
            pRIndirectReference = (PRIndirectReference)pdfDictionary2.get(PdfName.PARENT);
            pRIndirectReference = new PRIndirectReference(this.reader, pRIndirectReference.getNumber());
            pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pRIndirectReference);
            pdfIndirectReference = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.KIDS), pdfDictionary);
            pdfIndirectReference.add(pRIndirectReference2);
            this.markUsed(pdfIndirectReference);
            this.reader.pageRefs.insertPage(n, pRIndirectReference2);
        } else {
            if (n < 1) {
                n = 1;
            }
            PdfDictionary pdfDictionary5 = this.reader.getPageN(n);
            pdfIndirectReference = this.reader.getPageOrigRef(n);
            this.reader.releasePage(n);
            pRIndirectReference = (PRIndirectReference)pdfDictionary5.get(PdfName.PARENT);
            pRIndirectReference = new PRIndirectReference(this.reader, pRIndirectReference.getNumber());
            pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pRIndirectReference);
            PdfArray pdfArray2 = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.KIDS), pdfDictionary);
            ArrayList arrayList = pdfArray2.getArrayList();
            int n3 = arrayList.size();
            int n4 = pdfIndirectReference.getNumber();
            for (int i = 0; i < n3; ++i) {
                PRIndirectReference pRIndirectReference3 = (PRIndirectReference)arrayList.get(i);
                if (n4 != pRIndirectReference3.getNumber()) continue;
                arrayList.add(i, pRIndirectReference2);
                break;
            }
            if (n3 == arrayList.size()) {
                throw new RuntimeException("Internal inconsistence.");
            }
            this.markUsed(pdfArray2);
            this.reader.pageRefs.insertPage(n, pRIndirectReference2);
            this.correctAcroFieldPages(n);
        }
        pdfDictionary3.put(PdfName.PARENT, pRIndirectReference);
        while (pdfDictionary != null) {
            this.markUsed(pdfDictionary);
            PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.COUNT));
            pdfDictionary.put(PdfName.COUNT, new PdfNumber(pdfNumber.intValue() + 1));
            pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.PARENT));
        }
    }

    boolean isRotateContents() {
        return this.rotateContents;
    }

    void setRotateContents(boolean bl) {
        this.rotateContents = bl;
    }

    boolean isContentWritten() {
        return this.body.size() > 1;
    }

    AcroFields getAcroFields() {
        if (this.acroFields == null) {
            this.acroFields = new AcroFields(this.reader, this);
        }
        return this.acroFields;
    }

    void setFormFlattening(boolean bl) {
        this.flat = bl;
    }

    void setFreeTextFlattening(boolean bl) {
        this.flatFreeText = bl;
    }

    boolean partialFormFlattening(String string) {
        this.getAcroFields();
        if (this.acroFields.getXfa().isXfaPresent()) {
            throw new UnsupportedOperationException("Partial form flattening is not supported with XFA forms.");
        }
        if (!this.acroFields.getFields().containsKey(string)) {
            return false;
        }
        this.partialFlattening.add(string);
        return true;
    }

    void flatFields() {
        Object object4;
        PdfObject pdfObject;
        Object object3;
        Object object2;
        Object object5;
        Object object;
        int n;
        if (this.append) {
            throw new IllegalArgumentException("Field flattening is not supported in append mode.");
        }
        this.getAcroFields();
        HashMap hashMap = this.acroFields.getFields();
        if (this.fieldsAdded && this.partialFlattening.isEmpty()) {
            object = hashMap.keySet().iterator();
            while (object.hasNext()) {
                this.partialFlattening.add(object.next());
            }
        }
        object = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM));
        ArrayList arrayList = null;
        if (object != null && (object5 = (PdfArray)PdfReader.getPdfObject(object.get(PdfName.FIELDS), (PdfObject)object)) != null) {
            arrayList = object5.getArrayList();
        }
        object5 = hashMap.entrySet().iterator();
        while (object5.hasNext()) {
            object2 = (Map.Entry)object5.next();
            object3 = (String)object2.getKey();
            if (!this.partialFlattening.isEmpty() && !this.partialFlattening.contains(object3)) continue;
            object4 = (AcroFields.Item)object2.getValue();
            for (n = 0; n < object4.merged.size(); ++n) {
                PdfObject pdfObject3;
                PdfObject pdfObject2;
                Object object6;
                Object object7;
                pdfObject = (PdfDictionary)object4.merged.get(n);
                PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfObject.get(PdfName.F));
                int n2 = 0;
                if (pdfNumber != null) {
                    n2 = pdfNumber.intValue();
                }
                int n3 = (Integer)object4.page.get(n);
                PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfObject.get(PdfName.AP));
                if (pdfDictionary != null && (n2 & 4) != 0 && (n2 & 2) == 0) {
                    Object object8;
                    pdfObject2 = pdfDictionary.get(PdfName.N);
                    object7 = null;
                    if (pdfObject2 != null) {
                        object6 = PdfReader.getPdfObject(pdfObject2);
                        if (pdfObject2 instanceof PdfIndirectReference && !pdfObject2.isIndirect()) {
                            object7 = new PdfAppearance((PdfIndirectReference)pdfObject2);
                        } else if (object6 instanceof PdfStream) {
                            ((PdfDictionary)object6).put(PdfName.SUBTYPE, PdfName.FORM);
                            object7 = new PdfAppearance((PdfIndirectReference)pdfObject2);
                        } else if (object6 != null && object6.isDictionary() && (object8 = (PdfName)PdfReader.getPdfObject(pdfObject.get(PdfName.AS))) != null && (pdfObject3 = (PdfIndirectReference)((PdfDictionary)object6).get((PdfName)object8)) != null) {
                            object7 = new PdfAppearance((PdfIndirectReference)pdfObject3);
                            if (pdfObject3.isIndirect()) {
                                object6 = PdfReader.getPdfObject(pdfObject3);
                                ((PdfDictionary)object6).put(PdfName.SUBTYPE, PdfName.FORM);
                            }
                        }
                    }
                    if (object7 != null) {
                        object6 = PdfReader.getNormalizedRectangle((PdfArray)PdfReader.getPdfObject(pdfObject.get(PdfName.RECT)));
                        object8 = this.getOverContent(n3);
                        object8.setLiteral("Q ");
                        object8.addTemplate((PdfTemplate)object7, object6.getLeft(), object6.getBottom());
                        object8.setLiteral("q ");
                    }
                }
                if (this.partialFlattening.isEmpty() || (object7 = (PdfArray)PdfReader.getPdfObject((pdfObject2 = this.reader.getPageN(n3)).get(PdfName.ANNOTS))) == null) continue;
                object6 = object7.getArrayList();
                block3 : for (int i = 0; i < object6.size(); ++i) {
                    PdfObject pdfObject4;
                    pdfObject3 = (PdfObject)object6.get(i);
                    if (!pdfObject3.isIndirect() || !(pdfObject4 = (PdfObject)object4.widget_refs.get(n)).isIndirect() || ((PRIndirectReference)pdfObject3).getNumber() != ((PRIndirectReference)pdfObject4).getNumber()) continue;
                    object6.remove(i--);
                    PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfObject4;
                    do {
                        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pRIndirectReference);
                        PRIndirectReference pRIndirectReference2 = (PRIndirectReference)pdfDictionary2.get(PdfName.PARENT);
                        PdfReader.killIndirect(pRIndirectReference);
                        if (pRIndirectReference2 == null) {
                            for (int j = 0; j < arrayList.size(); ++j) {
                                PdfObject pdfObject5 = (PdfObject)arrayList.get(j);
                                if (!pdfObject5.isIndirect() || ((PRIndirectReference)pdfObject5).getNumber() != pRIndirectReference.getNumber()) continue;
                                arrayList.remove(j);
                                --j;
                            }
                            continue block3;
                        }
                        PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pRIndirectReference2);
                        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.KIDS));
                        ArrayList arrayList2 = pdfArray.getArrayList();
                        for (int j = 0; j < arrayList2.size(); ++j) {
                            PdfObject pdfObject6 = (PdfObject)arrayList2.get(j);
                            if (!pdfObject6.isIndirect() || ((PRIndirectReference)pdfObject6).getNumber() != pRIndirectReference.getNumber()) continue;
                            arrayList2.remove(j);
                            --j;
                        }
                        if (!arrayList2.isEmpty()) continue block3;
                        pRIndirectReference = pRIndirectReference2;
                    } while (true);
                }
                if (!object6.isEmpty()) continue;
                PdfReader.killIndirect(pdfObject2.get(PdfName.ANNOTS));
                pdfObject2.remove(PdfName.ANNOTS);
            }
        }
        if (!this.fieldsAdded && this.partialFlattening.isEmpty()) {
            for (int i = 1; i <= this.reader.getNumberOfPages(); ++i) {
                object2 = this.reader.getPageN(i);
                object3 = (PdfArray)PdfReader.getPdfObject(object2.get(PdfName.ANNOTS));
                if (object3 == null) continue;
                object4 = object3.getArrayList();
                for (n = 0; n < object4.size(); ++n) {
                    pdfObject = PdfReader.getPdfObject((PdfObject)object4.get(n));
                    if (pdfObject instanceof PdfIndirectReference && !pdfObject.isIndirect() || pdfObject.isDictionary() && !PdfName.WIDGET.equals(((PdfDictionary)pdfObject).get(PdfName.SUBTYPE))) continue;
                    object4.remove(n);
                    --n;
                }
                if (!object4.isEmpty()) continue;
                PdfReader.killIndirect(object2.get(PdfName.ANNOTS));
                object2.remove(PdfName.ANNOTS);
            }
            this.eliminateAcroformObjects();
        }
    }

    void eliminateAcroformObjects() {
        PdfObject pdfObject = this.reader.getCatalog().get(PdfName.ACROFORM);
        if (pdfObject == null) {
            return;
        }
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfObject);
        this.reader.killXref(pdfDictionary.get(PdfName.XFA));
        pdfDictionary.remove(PdfName.XFA);
        PdfObject pdfObject2 = pdfDictionary.get(PdfName.FIELDS);
        if (pdfObject2 != null) {
            PdfDictionary pdfDictionary2 = new PdfDictionary();
            pdfDictionary2.put(PdfName.KIDS, pdfObject2);
            this.sweepKids(pdfDictionary2);
            PdfReader.killIndirect(pdfObject2);
            pdfDictionary.put(PdfName.FIELDS, new PdfArray());
        }
    }

    void sweepKids(PdfObject pdfObject) {
        PdfObject pdfObject2 = PdfReader.killIndirect(pdfObject);
        if (pdfObject2 == null || !pdfObject2.isDictionary()) {
            return;
        }
        PdfDictionary pdfDictionary = (PdfDictionary)pdfObject2;
        PdfArray pdfArray = (PdfArray)PdfReader.killIndirect(pdfDictionary.get(PdfName.KIDS));
        if (pdfArray == null) {
            return;
        }
        ArrayList arrayList = pdfArray.getArrayList();
        for (int i = 0; i < arrayList.size(); ++i) {
            this.sweepKids((PdfObject)arrayList.get(i));
        }
    }

    private void flatFreeTextFields() {
        if (this.append) {
            throw new IllegalArgumentException("FreeText flattening is not supported in append mode.");
        }
        for (int i = 1; i <= this.reader.getNumberOfPages(); ++i) {
            PdfObject pdfObject;
            int n;
            PdfDictionary pdfDictionary;
            PdfDictionary pdfDictionary2 = this.reader.getPageN(i);
            PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.ANNOTS));
            if (pdfArray == null) continue;
            ArrayList arrayList = pdfArray.getArrayList();
            for (n = 0; n < arrayList.size(); ++n) {
                Object object;
                int n2;
                Object object2;
                PdfObject pdfObject2;
                pdfObject = PdfReader.getPdfObject((PdfObject)arrayList.get(n));
                if (pdfObject instanceof PdfIndirectReference && !pdfObject.isIndirect() || !((PdfName)(pdfDictionary = (PdfDictionary)pdfObject).get(PdfName.SUBTYPE)).equals(PdfName.FREETEXT)) continue;
                PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.F));
                int n3 = n2 = pdfNumber != null ? pdfNumber.intValue() : 0;
                if ((n2 & 4) == 0 || (n2 & 2) != 0 || (pdfObject2 = pdfDictionary.get(PdfName.AP)) == null) continue;
                PdfDictionary pdfDictionary3 = pdfObject2 instanceof PdfIndirectReference ? (PdfDictionary)PdfReader.getPdfObject(pdfObject2) : (PdfDictionary)pdfObject2;
                PdfObject pdfObject3 = pdfDictionary3.get(PdfName.N);
                PdfAppearance pdfAppearance = null;
                PdfObject pdfObject4 = PdfReader.getPdfObject(pdfObject3);
                if (pdfObject3 instanceof PdfIndirectReference && !pdfObject3.isIndirect()) {
                    pdfAppearance = new PdfAppearance((PdfIndirectReference)pdfObject3);
                } else if (pdfObject4 instanceof PdfStream) {
                    ((PdfDictionary)pdfObject4).put(PdfName.SUBTYPE, PdfName.FORM);
                    pdfAppearance = new PdfAppearance((PdfIndirectReference)pdfObject3);
                } else if (pdfObject4.isDictionary() && (object2 = (PdfName)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.AS))) != null && (object = (PdfIndirectReference)((PdfDictionary)pdfObject4).get((PdfName)object2)) != null) {
                    pdfAppearance = new PdfAppearance((PdfIndirectReference)object);
                    if (object.isIndirect()) {
                        pdfObject4 = PdfReader.getPdfObject((PdfObject)object);
                        ((PdfDictionary)pdfObject4).put(PdfName.SUBTYPE, PdfName.FORM);
                    }
                }
                if (pdfAppearance == null) continue;
                object2 = PdfReader.getNormalizedRectangle((PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.RECT)));
                object = this.getOverContent(i);
                object.setLiteral("Q ");
                object.addTemplate(pdfAppearance, object2.getLeft(), object2.getBottom());
                object.setLiteral("q ");
            }
            for (n = 0; n < arrayList.size(); ++n) {
                pdfObject = PdfReader.getPdfObject((PdfObject)arrayList.get(n));
                if (pdfObject == null || !pdfObject.isDictionary() || !PdfName.FREETEXT.equals((pdfDictionary = (PdfDictionary)pdfObject).get(PdfName.SUBTYPE))) continue;
                arrayList.remove(n);
                --n;
            }
            if (!arrayList.isEmpty()) continue;
            PdfReader.killIndirect(pdfDictionary2.get(PdfName.ANNOTS));
            pdfDictionary2.remove(PdfName.ANNOTS);
        }
    }

    public PdfIndirectReference getPageReference(int n) {
        PRIndirectReference pRIndirectReference = this.reader.getPageOrigRef(n);
        if (pRIndirectReference == null) {
            throw new IllegalArgumentException("Invalid page number " + n);
        }
        return pRIndirectReference;
    }

    public void addAnnotation(PdfAnnotation pdfAnnotation) {
        throw new RuntimeException("Unsupported in this context. Use PdfStamper.addAnnotation()");
    }

    void addDocumentField(PdfIndirectReference pdfIndirectReference) {
        PdfArray pdfArray;
        PdfDictionary pdfDictionary = this.reader.getCatalog();
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ACROFORM), pdfDictionary);
        if (pdfDictionary2 == null) {
            pdfDictionary2 = new PdfDictionary();
            pdfDictionary.put(PdfName.ACROFORM, pdfDictionary2);
            this.markUsed(pdfDictionary);
        }
        if ((pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.FIELDS), pdfDictionary2)) == null) {
            pdfArray = new PdfArray();
            pdfDictionary2.put(PdfName.FIELDS, pdfArray);
            this.markUsed(pdfDictionary2);
        }
        if (!pdfDictionary2.contains(PdfName.DA)) {
            pdfDictionary2.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
            this.markUsed(pdfDictionary2);
        }
        pdfArray.add(pdfIndirectReference);
        this.markUsed(pdfArray);
    }

    void addFieldResources() throws IOException {
        PdfDictionary pdfDictionary;
        Object object;
        if (this.fieldTemplates.isEmpty()) {
            return;
        }
        PdfDictionary pdfDictionary2 = this.reader.getCatalog();
        PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.ACROFORM), pdfDictionary2);
        if (pdfDictionary3 == null) {
            pdfDictionary3 = new PdfDictionary();
            pdfDictionary2.put(PdfName.ACROFORM, pdfDictionary3);
            this.markUsed(pdfDictionary2);
        }
        if ((pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.DR), pdfDictionary3)) == null) {
            pdfDictionary = new PdfDictionary();
            pdfDictionary3.put(PdfName.DR, pdfDictionary);
            this.markUsed(pdfDictionary3);
        }
        this.markUsed(pdfDictionary);
        Object object2 = this.fieldTemplates.keySet().iterator();
        while (object2.hasNext()) {
            object = (PdfTemplate)object2.next();
            PdfFormField.mergeResources(pdfDictionary, (PdfDictionary)object.getResources(), this);
        }
        if (pdfDictionary.get(PdfName.ENCODING) == null) {
            pdfDictionary.put(PdfName.ENCODING, PdfName.WIN_ANSI_ENCODING);
        }
        if ((object2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.FONT))) == null) {
            object2 = new PdfDictionary();
            pdfDictionary.put(PdfName.FONT, (PdfObject)object2);
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
        if (pdfDictionary3.get(PdfName.DA) == null) {
            pdfDictionary3.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
            this.markUsed(pdfDictionary3);
        }
    }

    void expandFields(PdfFormField pdfFormField, ArrayList arrayList) {
        arrayList.add(pdfFormField);
        ArrayList arrayList2 = pdfFormField.getKids();
        if (arrayList2 != null) {
            for (int i = 0; i < arrayList2.size(); ++i) {
                this.expandFields((PdfFormField)arrayList2.get(i), arrayList);
            }
        }
    }

    void addAnnotation(PdfAnnotation pdfAnnotation, PdfDictionary pdfDictionary) {
        try {
            ArrayList<PdfAnnotation> arrayList = new ArrayList<PdfAnnotation>();
            if (pdfAnnotation.isForm()) {
                this.fieldsAdded = true;
                this.getAcroFields();
                PdfFormField pdfFormField = (PdfFormField)pdfAnnotation;
                if (pdfFormField.getParent() != null) {
                    return;
                }
                this.expandFields(pdfFormField, arrayList);
            } else {
                arrayList.add(pdfAnnotation);
            }
            for (int i = 0; i < arrayList.size(); ++i) {
                Object object;
                pdfAnnotation = (PdfAnnotation)arrayList.get(i);
                if (pdfAnnotation.getPlaceInPage() > 0) {
                    pdfDictionary = this.reader.getPageN(pdfAnnotation.getPlaceInPage());
                }
                if (pdfAnnotation.isForm()) {
                    if (!pdfAnnotation.isUsed() && (object = pdfAnnotation.getTemplates()) != null) {
                        this.fieldTemplates.putAll(object);
                    }
                    if ((object = (PdfFormField)pdfAnnotation).getParent() == null) {
                        this.addDocumentField(object.getIndirectReference());
                    }
                }
                if (pdfAnnotation.isAnnotation()) {
                    PdfRectangle pdfRectangle;
                    object = PdfReader.getPdfObject(pdfDictionary.get(PdfName.ANNOTS), pdfDictionary);
                    PdfArray pdfArray = null;
                    if (object == null || !object.isArray()) {
                        pdfArray = new PdfArray();
                        pdfDictionary.put(PdfName.ANNOTS, pdfArray);
                        this.markUsed(pdfDictionary);
                    } else {
                        pdfArray = (PdfArray)object;
                    }
                    pdfArray.add(pdfAnnotation.getIndirectReference());
                    this.markUsed(pdfArray);
                    if (!(pdfAnnotation.isUsed() || (pdfRectangle = (PdfRectangle)pdfAnnotation.get(PdfName.RECT)) == null || pdfRectangle.left() == 0.0f && pdfRectangle.right() == 0.0f && pdfRectangle.top() == 0.0f && pdfRectangle.bottom() == 0.0f)) {
                        int n = this.reader.getPageRotation(pdfDictionary);
                        Rectangle rectangle = this.reader.getPageSizeWithRotation(pdfDictionary);
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
                this.addToBody((PdfObject)pdfAnnotation, pdfAnnotation.getIndirectReference());
            }
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    void addAnnotation(PdfAnnotation pdfAnnotation, int n) {
        this.addAnnotation(pdfAnnotation, this.reader.getPageN(n));
    }

    private void outlineTravel(PRIndirectReference pRIndirectReference) {
        while (pRIndirectReference != null) {
            PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pRIndirectReference);
            PRIndirectReference pRIndirectReference2 = (PRIndirectReference)pdfDictionary.get(PdfName.FIRST);
            if (pRIndirectReference2 != null) {
                this.outlineTravel(pRIndirectReference2);
            }
            PdfReader.killIndirect(pdfDictionary.get(PdfName.DEST));
            PdfReader.killIndirect(pdfDictionary.get(PdfName.A));
            PdfReader.killIndirect(pRIndirectReference);
            pRIndirectReference = (PRIndirectReference)pdfDictionary.get(PdfName.NEXT);
        }
    }

    void deleteOutlines() {
        PdfDictionary pdfDictionary = this.reader.getCatalog();
        PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfDictionary.get(PdfName.OUTLINES);
        if (pRIndirectReference == null) {
            return;
        }
        this.outlineTravel(pRIndirectReference);
        PdfReader.killIndirect(pRIndirectReference);
        pdfDictionary.remove(PdfName.OUTLINES);
        this.markUsed(pdfDictionary);
    }

    void setJavaScript() throws IOException {
        HashMap hashMap = this.pdf.getDocumentLevelJS();
        if (hashMap.isEmpty()) {
            return;
        }
        PdfDictionary pdfDictionary = this.reader.getCatalog();
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.NAMES), pdfDictionary);
        if (pdfDictionary2 == null) {
            pdfDictionary2 = new PdfDictionary();
            pdfDictionary.put(PdfName.NAMES, pdfDictionary2);
            this.markUsed(pdfDictionary);
        }
        this.markUsed(pdfDictionary2);
        PdfDictionary pdfDictionary3 = PdfNameTree.writeTree(hashMap, this);
        pdfDictionary2.put(PdfName.JAVASCRIPT, this.addToBody(pdfDictionary3).getIndirectReference());
    }

    void addFileAttachments() throws IOException {
        HashMap hashMap = this.pdf.getDocumentFileAttachment();
        if (hashMap.isEmpty()) {
            return;
        }
        PdfDictionary pdfDictionary = this.reader.getCatalog();
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.NAMES), pdfDictionary);
        if (pdfDictionary2 == null) {
            pdfDictionary2 = new PdfDictionary();
            pdfDictionary.put(PdfName.NAMES, pdfDictionary2);
            this.markUsed(pdfDictionary);
        }
        this.markUsed(pdfDictionary2);
        HashMap hashMap2 = PdfNameTree.readTree((PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.EMBEDDEDFILES)));
        Object object = hashMap.entrySet().iterator();
        while (object.hasNext()) {
            Map.Entry entry = object.next();
            String string = (String)entry.getKey();
            int n = 0;
            String string2 = string;
            while (hashMap2.containsKey(string2)) {
                string2 = string2 + " " + ++n;
            }
            hashMap2.put(string2, entry.getValue());
        }
        object = PdfNameTree.writeTree(hashMap2, this);
        pdfDictionary2.put(PdfName.EMBEDDEDFILES, this.addToBody((PdfObject)object).getIndirectReference());
    }

    void makePackage(PdfCollection pdfCollection) {
        PdfDictionary pdfDictionary = this.reader.getCatalog();
        pdfDictionary.put(PdfName.COLLECTION, pdfCollection);
    }

    void setOutlines() throws IOException {
        if (this.newBookmarks == null) {
            return;
        }
        this.deleteOutlines();
        if (this.newBookmarks.isEmpty()) {
            return;
        }
        PdfDictionary pdfDictionary = this.reader.getCatalog();
        boolean bl = pdfDictionary.get(PdfName.DESTS) != null;
        this.writeOutlines(pdfDictionary, bl);
        this.markUsed(pdfDictionary);
    }

    public void setViewerPreferences(int n) {
        this.useVp = true;
        this.viewerPreferences.setViewerPreferences(n);
    }

    public void addViewerPreference(PdfName pdfName, PdfObject pdfObject) {
        this.useVp = true;
        this.viewerPreferences.addViewerPreference(pdfName, pdfObject);
    }

    public void setSigFlags(int n) {
        this.sigFlags |= n;
    }

    public void setPageAction(PdfName pdfName, PdfAction pdfAction) throws PdfException {
        throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
    }

    void setPageAction(PdfName pdfName, PdfAction pdfAction, int n) throws PdfException {
        if (!pdfName.equals(PAGE_OPEN) && !pdfName.equals(PAGE_CLOSE)) {
            throw new PdfException("Invalid page additional action type: " + pdfName.toString());
        }
        PdfDictionary pdfDictionary = this.reader.getPageN(n);
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.AA), pdfDictionary);
        if (pdfDictionary2 == null) {
            pdfDictionary2 = new PdfDictionary();
            pdfDictionary.put(PdfName.AA, pdfDictionary2);
            this.markUsed(pdfDictionary);
        }
        pdfDictionary2.put(pdfName, pdfAction);
        this.markUsed(pdfDictionary2);
    }

    public void setDuration(int n) {
        throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
    }

    public void setTransition(PdfTransition pdfTransition) {
        throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
    }

    void setDuration(int n, int n2) {
        PdfDictionary pdfDictionary = this.reader.getPageN(n2);
        if (n < 0) {
            pdfDictionary.remove(PdfName.DUR);
        } else {
            pdfDictionary.put(PdfName.DUR, new PdfNumber(n));
        }
        this.markUsed(pdfDictionary);
    }

    void setTransition(PdfTransition pdfTransition, int n) {
        PdfDictionary pdfDictionary = this.reader.getPageN(n);
        if (pdfTransition == null) {
            pdfDictionary.remove(PdfName.TRANS);
        } else {
            pdfDictionary.put(PdfName.TRANS, pdfTransition.getTransitionDictionary());
        }
        this.markUsed(pdfDictionary);
    }

    protected void markUsed(PdfObject pdfObject) {
        if (this.append && pdfObject != null) {
            PRIndirectReference pRIndirectReference = null;
            pRIndirectReference = pdfObject.type() == 10 ? (PRIndirectReference)pdfObject : pdfObject.getIndRef();
            if (pRIndirectReference != null) {
                this.marked.put(pRIndirectReference.getNumber(), 1);
            }
        }
    }

    protected void markUsed(int n) {
        if (this.append) {
            this.marked.put(n, 1);
        }
    }

    boolean isAppend() {
        return this.append;
    }

    public void setAdditionalAction(PdfName pdfName, PdfAction pdfAction) throws PdfException {
        if (!(pdfName.equals(DOCUMENT_CLOSE) || pdfName.equals(WILL_SAVE) || pdfName.equals(DID_SAVE) || pdfName.equals(WILL_PRINT) || pdfName.equals(DID_PRINT))) {
            throw new PdfException("Invalid additional action type: " + pdfName.toString());
        }
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.AA));
        if (pdfDictionary == null) {
            if (pdfAction == null) {
                return;
            }
            pdfDictionary = new PdfDictionary();
            this.reader.getCatalog().put(PdfName.AA, pdfDictionary);
        }
        this.markUsed(pdfDictionary);
        if (pdfAction == null) {
            pdfDictionary.remove(pdfName);
        } else {
            pdfDictionary.put(pdfName, pdfAction);
        }
    }

    public void setOpenAction(PdfAction pdfAction) {
        this.openAction = pdfAction;
    }

    public void setOpenAction(String string) {
        throw new UnsupportedOperationException("Open actions by name are not supported.");
    }

    public void setThumbnail(Image image) {
        throw new UnsupportedOperationException("Use PdfStamper.setThumbnail().");
    }

    void setThumbnail(Image image, int n) throws PdfException, DocumentException {
        PdfIndirectReference pdfIndirectReference = this.getImageReference(this.addDirectImageSimple(image));
        this.reader.resetReleasePage();
        PdfDictionary pdfDictionary = this.reader.getPageN(n);
        pdfDictionary.put(PdfName.THUMB, pdfIndirectReference);
        this.reader.resetReleasePage();
    }

    public PdfContentByte getDirectContentUnder() {
        throw new UnsupportedOperationException("Use PdfStamper.getUnderContent() or PdfStamper.getOverContent()");
    }

    public PdfContentByte getDirectContent() {
        throw new UnsupportedOperationException("Use PdfStamper.getUnderContent() or PdfStamper.getOverContent()");
    }

    protected void readOCProperties() {
        PdfLayer pdfLayer;
        PdfIndirectReference pdfIndirectReference;
        Object object;
        if (!this.documentOCG.isEmpty()) {
            return;
        }
        PdfDictionary pdfDictionary = this.reader.getCatalog().getAsDict(PdfName.OCPROPERTIES);
        if (pdfDictionary == null) {
            return;
        }
        PdfArray pdfArray = pdfDictionary.getAsArray(PdfName.OCGS);
        HashMap<String, PdfLayer> hashMap = new HashMap<String, PdfLayer>();
        Object object2 = pdfArray.listIterator();
        while (object2.hasNext()) {
            pdfIndirectReference = (PdfIndirectReference)object2.next();
            pdfLayer = new PdfLayer(null);
            pdfLayer.setRef(pdfIndirectReference);
            pdfLayer.setOnPanel(false);
            pdfLayer.merge((PdfDictionary)PdfReader.getPdfObject(pdfIndirectReference));
            hashMap.put(pdfIndirectReference.toString(), pdfLayer);
        }
        object2 = pdfDictionary.getAsDict(PdfName.D);
        PdfArray pdfArray2 = object2.getAsArray(PdfName.OFF);
        if (pdfArray2 != null) {
            object = pdfArray2.listIterator();
            while (object.hasNext()) {
                pdfIndirectReference = (PdfIndirectReference)object.next();
                pdfLayer = (PdfLayer)hashMap.get(pdfIndirectReference.toString());
                pdfLayer.setOn(false);
            }
        }
        if ((object = object2.getAsArray(PdfName.ORDER)) != null) {
            this.addOrder(null, (PdfArray)object, hashMap);
        }
        this.documentOCG.addAll(hashMap.values());
        this.OCGRadioGroup = object2.getAsArray(PdfName.RBGROUPS);
        this.OCGLocked = object2.getAsArray(PdfName.LOCKED);
    }

    private void addOrder(PdfLayer pdfLayer, PdfArray pdfArray, Map map) {
        for (int i = 0; i < pdfArray.size(); ++i) {
            PdfLayer pdfLayer2;
            PdfObject pdfObject = pdfArray.getPdfObject(i);
            if (pdfObject.isIndirect()) {
                pdfLayer2 = (PdfLayer)map.get(pdfObject.toString());
                pdfLayer2.setOnPanel(true);
                this.registerLayer(pdfLayer2);
                if (pdfLayer != null) {
                    pdfLayer.addChild(pdfLayer2);
                }
                if (pdfArray.size() <= i + 1 || !pdfArray.getPdfObject(i + 1).isArray()) continue;
                this.addOrder(pdfLayer2, (PdfArray)pdfArray.getPdfObject(++i), map);
                continue;
            }
            if (!pdfObject.isArray()) continue;
            ArrayList arrayList = ((PdfArray)pdfObject).getArrayList();
            if (arrayList.isEmpty()) {
                return;
            }
            pdfObject = (PdfObject)arrayList.get(0);
            if (pdfObject.isString()) {
                pdfLayer2 = new PdfLayer(arrayList.get(0).toString());
                pdfLayer2.setOnPanel(true);
                this.registerLayer(pdfLayer2);
                if (pdfLayer != null) {
                    pdfLayer.addChild(pdfLayer2);
                }
                PdfArray pdfArray2 = new PdfArray();
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext()) {
                    pdfArray2.add((PdfObject)iterator.next());
                }
                this.addOrder(pdfLayer2, pdfArray2, map);
                continue;
            }
            this.addOrder(pdfLayer, (PdfArray)pdfObject, map);
        }
    }

    public Map getPdfLayers() {
        if (this.documentOCG.isEmpty()) {
            this.readOCProperties();
        }
        HashMap<String, PdfLayer> hashMap = new HashMap<String, PdfLayer>();
        Iterator iterator = this.documentOCG.iterator();
        while (iterator.hasNext()) {
            PdfLayer pdfLayer = (PdfLayer)iterator.next();
            String string = pdfLayer.getTitle() == null ? pdfLayer.getAsString(PdfName.NAME).toString() : pdfLayer.getTitle();
            if (hashMap.containsKey(string)) {
                int n = 2;
                String string2 = string + "(" + n + ")";
                while (hashMap.containsKey(string2)) {
                    string2 = string + "(" + ++n + ")";
                }
                string = string2;
            }
            hashMap.put(string, pdfLayer);
        }
        return hashMap;
    }

    static class PageStamp {
        PdfDictionary pageN;
        StampContent under;
        StampContent over;
        PageResources pageResources;
        int replacePoint = 0;

        PageStamp(PdfStamperImp pdfStamperImp, PdfReader pdfReader, PdfDictionary pdfDictionary) {
            this.pageN = pdfDictionary;
            this.pageResources = new PageResources();
            PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.RESOURCES));
            this.pageResources.setOriginalResources(pdfDictionary2, pdfStamperImp.namePtr);
        }
    }

}

