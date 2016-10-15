/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.cms.CMSEnvelopedData
 *  org.bouncycastle.cms.RecipientId
 *  org.bouncycastle.cms.RecipientInformation
 *  org.bouncycastle.cms.RecipientInformationStore
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BadPasswordException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.LZWDecoder;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SequenceList;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.zip.InflaterInputStream;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class PdfReader
implements PdfViewerPreferences {
    static final PdfName[] pageInhCandidates = new PdfName[]{PdfName.MEDIABOX, PdfName.ROTATE, PdfName.RESOURCES, PdfName.CROPBOX};
    static final byte[] endstream = PdfEncodings.convertToBytes("endstream", null);
    static final byte[] endobj = PdfEncodings.convertToBytes("endobj", null);
    protected PRTokeniser tokens;
    protected int[] xref;
    protected HashMap objStmMark;
    protected IntHashtable objStmToOffset;
    protected boolean newXrefType;
    private ArrayList xrefObj;
    PdfDictionary rootPages;
    protected PdfDictionary trailer;
    protected PdfDictionary catalog;
    protected PageRefs pageRefs;
    protected PRAcroForm acroForm = null;
    protected boolean acroFormParsed = false;
    protected boolean encrypted = false;
    protected boolean rebuilt = false;
    protected int freeXref;
    protected boolean tampered = false;
    protected int lastXref;
    protected int eofPos;
    protected char pdfVersion;
    protected PdfEncryption decrypt;
    protected byte[] password = null;
    protected Key certificateKey = null;
    protected Certificate certificate = null;
    protected String certificateKeyProvider = null;
    private boolean ownerPasswordUsed;
    protected ArrayList strings = new ArrayList();
    protected boolean sharedStreams = true;
    protected boolean consolidateNamedDestinations = false;
    protected int rValue;
    protected int pValue;
    private int objNum;
    private int objGen;
    private int fileLength;
    private boolean hybridXref;
    private int lastXrefPartial = -1;
    private boolean partial;
    private PRIndirectReference cryptoRef;
    private PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
    private boolean encryptionError;
    private boolean appendable;

    protected PdfReader() {
    }

    public PdfReader(String string) throws IOException {
        this(string, null);
    }

    public PdfReader(String string, byte[] arrby) throws IOException {
        this.password = arrby;
        this.tokens = new PRTokeniser(string);
        this.readPdf();
    }

    public PdfReader(byte[] arrby) throws IOException {
        this(arrby, null);
    }

    public PdfReader(byte[] arrby, byte[] arrby2) throws IOException {
        this.password = arrby2;
        this.tokens = new PRTokeniser(arrby);
        this.readPdf();
    }

    public PdfReader(String string, Certificate certificate, Key key, String string2) throws IOException {
        this.certificate = certificate;
        this.certificateKey = key;
        this.certificateKeyProvider = string2;
        this.tokens = new PRTokeniser(string);
        this.readPdf();
    }

    public PdfReader(URL uRL) throws IOException {
        this(uRL, null);
    }

    public PdfReader(URL uRL, byte[] arrby) throws IOException {
        this.password = arrby;
        this.tokens = new PRTokeniser(new RandomAccessFileOrArray(uRL));
        this.readPdf();
    }

    public PdfReader(InputStream inputStream, byte[] arrby) throws IOException {
        this.password = arrby;
        this.tokens = new PRTokeniser(new RandomAccessFileOrArray(inputStream));
        this.readPdf();
    }

    public PdfReader(InputStream inputStream) throws IOException {
        this(inputStream, null);
    }

    public PdfReader(RandomAccessFileOrArray randomAccessFileOrArray, byte[] arrby) throws IOException {
        this.password = arrby;
        this.partial = true;
        this.tokens = new PRTokeniser(randomAccessFileOrArray);
        this.readPdfPartial();
    }

    public PdfReader(PdfReader pdfReader) {
        this.appendable = pdfReader.appendable;
        this.consolidateNamedDestinations = pdfReader.consolidateNamedDestinations;
        this.encrypted = pdfReader.encrypted;
        this.rebuilt = pdfReader.rebuilt;
        this.sharedStreams = pdfReader.sharedStreams;
        this.tampered = pdfReader.tampered;
        this.password = pdfReader.password;
        this.pdfVersion = pdfReader.pdfVersion;
        this.eofPos = pdfReader.eofPos;
        this.freeXref = pdfReader.freeXref;
        this.lastXref = pdfReader.lastXref;
        this.tokens = new PRTokeniser(pdfReader.tokens.getSafeFile());
        if (pdfReader.decrypt != null) {
            this.decrypt = new PdfEncryption(pdfReader.decrypt);
        }
        this.pValue = pdfReader.pValue;
        this.rValue = pdfReader.rValue;
        this.xrefObj = new ArrayList(pdfReader.xrefObj);
        for (int i = 0; i < pdfReader.xrefObj.size(); ++i) {
            this.xrefObj.set(i, PdfReader.duplicatePdfObject((PdfObject)pdfReader.xrefObj.get(i), this));
        }
        this.pageRefs = new PageRefs(pdfReader.pageRefs, this);
        this.trailer = (PdfDictionary)PdfReader.duplicatePdfObject(pdfReader.trailer, this);
        this.catalog = (PdfDictionary)PdfReader.getPdfObject(this.trailer.get(PdfName.ROOT));
        this.rootPages = (PdfDictionary)PdfReader.getPdfObject(this.catalog.get(PdfName.PAGES));
        this.fileLength = pdfReader.fileLength;
        this.partial = pdfReader.partial;
        this.hybridXref = pdfReader.hybridXref;
        this.objStmToOffset = pdfReader.objStmToOffset;
        this.xref = pdfReader.xref;
        this.cryptoRef = (PRIndirectReference)PdfReader.duplicatePdfObject(pdfReader.cryptoRef, this);
        this.ownerPasswordUsed = pdfReader.ownerPasswordUsed;
    }

    public RandomAccessFileOrArray getSafeFile() {
        return this.tokens.getSafeFile();
    }

    protected PdfReaderInstance getPdfReaderInstance(PdfWriter pdfWriter) {
        return new PdfReaderInstance(this, pdfWriter);
    }

    public int getNumberOfPages() {
        return this.pageRefs.size();
    }

    public PdfDictionary getCatalog() {
        return this.catalog;
    }

    public PRAcroForm getAcroForm() {
        if (!this.acroFormParsed) {
            this.acroFormParsed = true;
            PdfObject pdfObject = this.catalog.get(PdfName.ACROFORM);
            if (pdfObject != null) {
                try {
                    this.acroForm = new PRAcroForm(this);
                    this.acroForm.readAcroForm((PdfDictionary)PdfReader.getPdfObject(pdfObject));
                }
                catch (Exception var2_2) {
                    this.acroForm = null;
                }
            }
        }
        return this.acroForm;
    }

    public int getPageRotation(int n) {
        return this.getPageRotation(this.pageRefs.getPageNRelease(n));
    }

    int getPageRotation(PdfDictionary pdfDictionary) {
        PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ROTATE));
        if (pdfNumber == null) {
            return 0;
        }
        int n = pdfNumber.intValue();
        return (n %= 360) < 0 ? n + 360 : n;
    }

    public Rectangle getPageSizeWithRotation(int n) {
        return this.getPageSizeWithRotation(this.pageRefs.getPageNRelease(n));
    }

    public Rectangle getPageSizeWithRotation(PdfDictionary pdfDictionary) {
        Rectangle rectangle = this.getPageSize(pdfDictionary);
        for (int i = this.getPageRotation((PdfDictionary)pdfDictionary); i > 0; i -= 90) {
            rectangle = rectangle.rotate();
        }
        return rectangle;
    }

    public Rectangle getPageSize(int n) {
        return this.getPageSize(this.pageRefs.getPageNRelease(n));
    }

    public Rectangle getPageSize(PdfDictionary pdfDictionary) {
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.MEDIABOX));
        return PdfReader.getNormalizedRectangle(pdfArray);
    }

    public Rectangle getCropBox(int n) {
        PdfDictionary pdfDictionary = this.pageRefs.getPageNRelease(n);
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.CROPBOX));
        if (pdfArray == null) {
            return this.getPageSize(pdfDictionary);
        }
        return PdfReader.getNormalizedRectangle(pdfArray);
    }

    public Rectangle getBoxSize(int n, String string) {
        PdfDictionary pdfDictionary = this.pageRefs.getPageNRelease(n);
        PdfArray pdfArray = null;
        if (string.equals("trim")) {
            pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.TRIMBOX));
        } else if (string.equals("art")) {
            pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.ARTBOX));
        } else if (string.equals("bleed")) {
            pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.BLEEDBOX));
        } else if (string.equals("crop")) {
            pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.CROPBOX));
        } else if (string.equals("media")) {
            pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.MEDIABOX));
        }
        if (pdfArray == null) {
            return null;
        }
        return PdfReader.getNormalizedRectangle(pdfArray);
    }

    public HashMap getInfo() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.trailer.get(PdfName.INFO));
        if (pdfDictionary == null) {
            return hashMap;
        }
        Iterator iterator = pdfDictionary.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            PdfObject pdfObject = PdfReader.getPdfObject(pdfDictionary.get(pdfName));
            if (pdfObject == null) continue;
            String string = pdfObject.toString();
            switch (pdfObject.type()) {
                case 3: {
                    string = ((PdfString)pdfObject).toUnicodeString();
                    break;
                }
                case 4: {
                    string = PdfName.decodeName(string);
                }
            }
            hashMap.put(PdfName.decodeName(pdfName.toString()), string);
        }
        return hashMap;
    }

    public static Rectangle getNormalizedRectangle(PdfArray pdfArray) {
        ArrayList arrayList = pdfArray.getArrayList();
        float f = ((PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(0))).floatValue();
        float f2 = ((PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(1))).floatValue();
        float f3 = ((PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(2))).floatValue();
        float f4 = ((PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(3))).floatValue();
        return new Rectangle(Math.min(f, f3), Math.min(f2, f4), Math.max(f, f3), Math.max(f2, f4));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void readPdf() throws IOException {
        try {
            this.fileLength = this.tokens.getFile().length();
            this.pdfVersion = this.tokens.checkPdfHeader();
            try {
                this.readXref();
            }
            catch (Exception var1_1) {
                try {
                    this.rebuilt = true;
                    this.rebuildXref();
                    this.lastXref = -1;
                }
                catch (Exception var2_4) {
                    throw new IOException("Rebuild failed: " + var2_4.getMessage() + "; Original message: " + var1_1.getMessage());
                }
            }
            try {
                this.readDocObj();
            }
            catch (Exception var1_2) {
                if (this.rebuilt || this.encryptionError) {
                    throw new IOException(var1_2.getMessage());
                }
                this.rebuilt = true;
                this.encrypted = false;
                this.rebuildXref();
                this.lastXref = -1;
                this.readDocObj();
            }
            this.strings.clear();
            this.readPages();
            this.eliminateSharedStreams();
            this.removeUnusedObjects();
        }
        finally {
            try {
                this.tokens.close();
            }
            catch (Exception var1_3) {}
        }
    }

    protected void readPdfPartial() throws IOException {
        try {
            this.fileLength = this.tokens.getFile().length();
            this.pdfVersion = this.tokens.checkPdfHeader();
            try {
                this.readXref();
            }
            catch (Exception var1_1) {
                try {
                    this.rebuilt = true;
                    this.rebuildXref();
                    this.lastXref = -1;
                }
                catch (Exception var2_3) {
                    throw new IOException("Rebuild failed: " + var2_3.getMessage() + "; Original message: " + var1_1.getMessage());
                }
            }
            this.readDocObjPartial();
            this.readPages();
        }
        catch (IOException var1_2) {
            try {
                this.tokens.close();
            }
            catch (Exception var2_4) {
                // empty catch block
            }
            throw var1_2;
        }
    }

    private boolean equalsArray(byte[] arrby, byte[] arrby2, int n) {
        for (int i = 0; i < n; ++i) {
            if (arrby[i] == arrby2[i]) continue;
            return false;
        }
        return true;
    }

    private void readDecryptedDocObj() throws IOException {
        PdfObject pdfObject;
        Object object;
        String string;
        if (this.encrypted) {
            return;
        }
        PdfObject pdfObject2 = this.trailer.get(PdfName.ENCRYPT);
        if (pdfObject2 == null || pdfObject2.toString().equals("null")) {
            return;
        }
        this.encryptionError = true;
        byte[] arrby = null;
        this.encrypted = true;
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfObject2);
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(this.trailer.get(PdfName.ID));
        byte[] arrby2 = null;
        if (pdfArray != null) {
            pdfObject = (PdfObject)pdfArray.getArrayList().get(0);
            this.strings.remove(pdfObject);
            string = pdfObject.toString();
            arrby2 = DocWriter.getISOBytes(string);
            if (pdfArray.size() > 1) {
                this.strings.remove(pdfArray.getArrayList().get(1));
            }
        }
        if (arrby2 == null) {
            arrby2 = new byte[]{};
        }
        byte[] arrby3 = null;
        byte[] arrby4 = null;
        int n = 0;
        int n2 = 0;
        PdfObject pdfObject3 = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.FILTER));
        if (pdfObject3.equals(PdfName.STANDARD)) {
            string = pdfDictionary.get(PdfName.U).toString();
            this.strings.remove(pdfDictionary.get(PdfName.U));
            arrby3 = DocWriter.getISOBytes(string);
            string = pdfDictionary.get(PdfName.O).toString();
            this.strings.remove(pdfDictionary.get(PdfName.O));
            arrby4 = DocWriter.getISOBytes(string);
            pdfObject = pdfDictionary.get(PdfName.R);
            if (!pdfObject.isNumber()) {
                throw new IOException("Illegal R value.");
            }
            this.rValue = ((PdfNumber)pdfObject).intValue();
            if (this.rValue != 2 && this.rValue != 3 && this.rValue != 4) {
                throw new IOException("Unknown encryption type (" + this.rValue + ")");
            }
            pdfObject = pdfDictionary.get(PdfName.P);
            if (!pdfObject.isNumber()) {
                throw new IOException("Illegal P value.");
            }
            this.pValue = ((PdfNumber)pdfObject).intValue();
            if (this.rValue == 3) {
                pdfObject = pdfDictionary.get(PdfName.LENGTH);
                if (!pdfObject.isNumber()) {
                    throw new IOException("Illegal Length value.");
                }
                n2 = ((PdfNumber)pdfObject).intValue();
                if (n2 > 128 || n2 < 40 || n2 % 8 != 0) {
                    throw new IOException("Illegal Length value.");
                }
                n = 1;
            } else if (this.rValue == 4) {
                PdfDictionary pdfDictionary2 = (PdfDictionary)pdfDictionary.get(PdfName.CF);
                if (pdfDictionary2 == null) {
                    throw new IOException("/CF not found (encryption)");
                }
                if ((pdfDictionary2 = (PdfDictionary)pdfDictionary2.get(PdfName.STDCF)) == null) {
                    throw new IOException("/StdCF not found (encryption)");
                }
                if (PdfName.V2.equals(pdfDictionary2.get(PdfName.CFM))) {
                    n = 1;
                } else if (PdfName.AESV2.equals(pdfDictionary2.get(PdfName.CFM))) {
                    n = 2;
                } else {
                    throw new IOException("No compatible encryption found");
                }
                object = pdfDictionary.get(PdfName.ENCRYPTMETADATA);
                if (object != null && object.toString().equals("false")) {
                    n |= 8;
                }
            } else {
                n = 0;
            }
        } else if (pdfObject3.equals(PdfName.PUBSEC)) {
            PdfObject pdfObject4;
            byte[] arrby5;
            boolean bl = false;
            object = null;
            PdfArray pdfArray2 = null;
            pdfObject = pdfDictionary.get(PdfName.V);
            if (!pdfObject.isNumber()) {
                throw new IOException("Illegal V value.");
            }
            int n3 = ((PdfNumber)pdfObject).intValue();
            if (n3 != 1 && n3 != 2 && n3 != 4) {
                throw new IOException("Unknown encryption type V = " + this.rValue);
            }
            if (n3 == 2) {
                pdfObject = pdfDictionary.get(PdfName.LENGTH);
                if (!pdfObject.isNumber()) {
                    throw new IOException("Illegal Length value.");
                }
                n2 = ((PdfNumber)pdfObject).intValue();
                if (n2 > 128 || n2 < 40 || n2 % 8 != 0) {
                    throw new IOException("Illegal Length value.");
                }
                n = 1;
                pdfArray2 = (PdfArray)pdfDictionary.get(PdfName.RECIPIENTS);
            } else if (n3 == 4) {
                PdfDictionary pdfDictionary3 = (PdfDictionary)pdfDictionary.get(PdfName.CF);
                if (pdfDictionary3 == null) {
                    throw new IOException("/CF not found (encryption)");
                }
                if ((pdfDictionary3 = (PdfDictionary)pdfDictionary3.get(PdfName.DEFAULTCRYPTFILER)) == null) {
                    throw new IOException("/DefaultCryptFilter not found (encryption)");
                }
                if (PdfName.V2.equals(pdfDictionary3.get(PdfName.CFM))) {
                    n = 1;
                    n2 = 128;
                } else if (PdfName.AESV2.equals(pdfDictionary3.get(PdfName.CFM))) {
                    n = 2;
                    n2 = 128;
                } else {
                    throw new IOException("No compatible encryption found");
                }
                pdfObject4 = pdfDictionary3.get(PdfName.ENCRYPTMETADATA);
                if (pdfObject4 != null && pdfObject4.toString().equals("false")) {
                    n |= 8;
                }
                pdfArray2 = (PdfArray)pdfDictionary3.get(PdfName.RECIPIENTS);
            } else {
                n = 0;
                n2 = 40;
                pdfArray2 = (PdfArray)pdfDictionary.get(PdfName.RECIPIENTS);
            }
            for (int i = 0; i < pdfArray2.size(); ++i) {
                pdfObject4 = (PdfObject)pdfArray2.getArrayList().get(i);
                this.strings.remove(pdfObject4);
                arrby5 = null;
                try {
                    arrby5 = new byte[](pdfObject4.getBytes());
                    Iterator iterator = arrby5.getRecipientInfos().getRecipients().iterator();
                    while (iterator.hasNext()) {
                        RecipientInformation recipientInformation = (RecipientInformation)iterator.next();
                        if (!recipientInformation.getRID().match(this.certificate) || bl) continue;
                        object = recipientInformation.getContent(this.certificateKey, this.certificateKeyProvider);
                        bl = true;
                    }
                    continue;
                }
                catch (Exception var20_27) {
                    throw new ExceptionConverter(var20_27);
                }
            }
            if (!bl || object == null) {
                throw new IOException("Bad certificate and key.");
            }
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("SHA-1");
                messageDigest.update((byte[])object, 0, 20);
                for (int j = 0; j < pdfArray2.size(); ++j) {
                    arrby5 = ((PdfObject)pdfArray2.getArrayList().get(j)).getBytes();
                    messageDigest.update(arrby5);
                }
                if ((n & 8) != 0) {
                    messageDigest.update(new byte[]{-1, -1, -1, -1});
                }
                arrby = messageDigest.digest();
            }
            catch (Exception var18_24) {
                throw new ExceptionConverter(var18_24);
            }
        }
        this.decrypt = new PdfEncryption();
        this.decrypt.setCryptoMode(n, n2);
        if (pdfObject3.equals(PdfName.STANDARD)) {
            this.decrypt.setupByOwnerPassword(arrby2, this.password, arrby3, arrby4, this.pValue);
            if (!this.equalsArray(arrby3, this.decrypt.userKey, this.rValue == 3 || this.rValue == 4 ? 16 : 32)) {
                this.decrypt.setupByUserPassword(arrby2, this.password, arrby4, this.pValue);
                if (!this.equalsArray(arrby3, this.decrypt.userKey, this.rValue == 3 || this.rValue == 4 ? 16 : 32)) {
                    throw new BadPasswordException();
                }
            } else {
                this.ownerPasswordUsed = true;
            }
        } else if (pdfObject3.equals(PdfName.PUBSEC)) {
            this.decrypt.setupByEncryptionKey(arrby, n2);
            this.ownerPasswordUsed = true;
        }
        for (int i = 0; i < this.strings.size(); ++i) {
            object = (byte[])this.strings.get(i);
            object.decrypt(this);
        }
        if (pdfObject2.isIndirect()) {
            this.cryptoRef = (PRIndirectReference)pdfObject2;
            this.xrefObj.set(this.cryptoRef.getNumber(), null);
        }
        this.encryptionError = false;
    }

    public static PdfObject getPdfObjectRelease(PdfObject pdfObject) {
        PdfObject pdfObject2 = PdfReader.getPdfObject(pdfObject);
        PdfReader.releaseLastXrefPartial(pdfObject);
        return pdfObject2;
    }

    public static PdfObject getPdfObject(PdfObject pdfObject) {
        if (pdfObject == null) {
            return null;
        }
        if (!pdfObject.isIndirect()) {
            return pdfObject;
        }
        try {
            PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfObject;
            int n = pRIndirectReference.getNumber();
            boolean bl = pRIndirectReference.getReader().appendable;
            pdfObject = pRIndirectReference.getReader().getPdfObject(n);
            if (pdfObject == null) {
                return null;
            }
            if (bl) {
                switch (pdfObject.type()) {
                    case 8: {
                        pdfObject = new PdfNull();
                        break;
                    }
                    case 1: {
                        pdfObject = new PdfBoolean(((PdfBoolean)pdfObject).booleanValue());
                        break;
                    }
                    case 4: {
                        pdfObject = new PdfName(pdfObject.getBytes());
                    }
                }
                pdfObject.setIndRef(pRIndirectReference);
            }
            return pdfObject;
        }
        catch (Exception var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public static PdfObject getPdfObjectRelease(PdfObject pdfObject, PdfObject pdfObject2) {
        PdfObject pdfObject3 = PdfReader.getPdfObject(pdfObject, pdfObject2);
        PdfReader.releaseLastXrefPartial(pdfObject);
        return pdfObject3;
    }

    public static PdfObject getPdfObject(PdfObject pdfObject, PdfObject pdfObject2) {
        if (pdfObject == null) {
            return null;
        }
        if (!pdfObject.isIndirect()) {
            PRIndirectReference pRIndirectReference = null;
            if (pdfObject2 != null && (pRIndirectReference = pdfObject2.getIndRef()) != null && pRIndirectReference.getReader().isAppendable()) {
                switch (pdfObject.type()) {
                    case 8: {
                        pdfObject = new PdfNull();
                        break;
                    }
                    case 1: {
                        pdfObject = new PdfBoolean(((PdfBoolean)pdfObject).booleanValue());
                        break;
                    }
                    case 4: {
                        pdfObject = new PdfName(pdfObject.getBytes());
                    }
                }
                pdfObject.setIndRef(pRIndirectReference);
            }
            return pdfObject;
        }
        return PdfReader.getPdfObject(pdfObject);
    }

    public PdfObject getPdfObjectRelease(int n) {
        PdfObject pdfObject = this.getPdfObject(n);
        this.releaseLastXrefPartial();
        return pdfObject;
    }

    public PdfObject getPdfObject(int n) {
        try {
            this.lastXrefPartial = -1;
            if (n < 0 || n >= this.xrefObj.size()) {
                return null;
            }
            PdfObject pdfObject = (PdfObject)this.xrefObj.get(n);
            if (!this.partial || pdfObject != null) {
                return pdfObject;
            }
            if (n * 2 >= this.xref.length) {
                return null;
            }
            pdfObject = this.readSingleObject(n);
            this.lastXrefPartial = -1;
            if (pdfObject != null) {
                this.lastXrefPartial = n;
            }
            return pdfObject;
        }
        catch (Exception var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    public void resetLastXrefPartial() {
        this.lastXrefPartial = -1;
    }

    public void releaseLastXrefPartial() {
        if (this.partial && this.lastXrefPartial != -1) {
            this.xrefObj.set(this.lastXrefPartial, null);
            this.lastXrefPartial = -1;
        }
    }

    public static void releaseLastXrefPartial(PdfObject pdfObject) {
        if (pdfObject == null) {
            return;
        }
        if (!pdfObject.isIndirect()) {
            return;
        }
        PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfObject;
        PdfReader pdfReader = pRIndirectReference.getReader();
        if (pdfReader.partial && pdfReader.lastXrefPartial != -1 && pdfReader.lastXrefPartial == pRIndirectReference.getNumber()) {
            pdfReader.xrefObj.set(pdfReader.lastXrefPartial, null);
        }
        pdfReader.lastXrefPartial = -1;
    }

    private void setXrefPartialObject(int n, PdfObject pdfObject) {
        if (!this.partial || n < 0) {
            return;
        }
        this.xrefObj.set(n, pdfObject);
    }

    public PRIndirectReference addPdfObject(PdfObject pdfObject) {
        this.xrefObj.add(pdfObject);
        return new PRIndirectReference(this, this.xrefObj.size() - 1);
    }

    protected void readPages() throws IOException {
        this.catalog = (PdfDictionary)PdfReader.getPdfObject(this.trailer.get(PdfName.ROOT));
        this.rootPages = (PdfDictionary)PdfReader.getPdfObject(this.catalog.get(PdfName.PAGES));
        this.pageRefs = new PageRefs(this);
    }

    protected void readDocObjPartial() throws IOException {
        this.xrefObj = new ArrayList(this.xref.length / 2);
        this.xrefObj.addAll(Collections.nCopies(this.xref.length / 2, null));
        this.readDecryptedDocObj();
        if (this.objStmToOffset != null) {
            int[] arrn = this.objStmToOffset.getKeys();
            for (int i = 0; i < arrn.length; ++i) {
                int n = arrn[i];
                this.objStmToOffset.put(n, this.xref[n * 2]);
                this.xref[n * 2] = -1;
            }
        }
    }

    protected PdfObject readSingleObject(int n) throws IOException {
        PdfObject pdfObject;
        this.strings.clear();
        int n2 = n * 2;
        int n3 = this.xref[n2];
        if (n3 < 0) {
            return null;
        }
        if (this.xref[n2 + 1] > 0) {
            n3 = this.objStmToOffset.get(this.xref[n2 + 1]);
        }
        if (n3 == 0) {
            return null;
        }
        this.tokens.seek(n3);
        this.tokens.nextValidToken();
        if (this.tokens.getTokenType() != 1) {
            this.tokens.throwError("Invalid object number.");
        }
        this.objNum = this.tokens.intValue();
        this.tokens.nextValidToken();
        if (this.tokens.getTokenType() != 1) {
            this.tokens.throwError("Invalid generation number.");
        }
        this.objGen = this.tokens.intValue();
        this.tokens.nextValidToken();
        if (!this.tokens.getStringValue().equals("obj")) {
            this.tokens.throwError("Token 'obj' expected.");
        }
        try {
            pdfObject = this.readPRObject();
            for (int i = 0; i < this.strings.size(); ++i) {
                PdfString pdfString = (PdfString)this.strings.get(i);
                pdfString.decrypt(this);
            }
            if (pdfObject.isStream()) {
                this.checkPRStreamLength((PRStream)pdfObject);
            }
        }
        catch (Exception var5_6) {
            pdfObject = null;
        }
        if (this.xref[n2 + 1] > 0) {
            pdfObject = this.readOneObjStm((PRStream)pdfObject, this.xref[n2]);
        }
        this.xrefObj.set(n, pdfObject);
        return pdfObject;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected PdfObject readOneObjStm(PRStream pRStream, int n) throws IOException {
        int n2 = ((PdfNumber)PdfReader.getPdfObject(pRStream.get(PdfName.FIRST))).intValue();
        byte[] arrby = PdfReader.getStreamBytes(pRStream, this.tokens.getFile());
        PRTokeniser pRTokeniser = this.tokens;
        this.tokens = new PRTokeniser(arrby);
        try {
            int n3 = 0;
            boolean bl = true;
            for (int i = 0; i < ++n && (bl = this.tokens.nextToken()); ++i) {
                if (this.tokens.getTokenType() != 1) {
                    bl = false;
                    break;
                }
                bl = this.tokens.nextToken();
                if (!bl) break;
                if (this.tokens.getTokenType() != 1) {
                    bl = false;
                    break;
                }
                n3 = this.tokens.intValue() + n2;
            }
            if (!bl) {
                throw new IOException("Error reading ObjStm");
            }
            this.tokens.seek(n3);
            PdfObject pdfObject = this.readPRObject();
            return pdfObject;
        }
        finally {
            this.tokens = pRTokeniser;
        }
    }

    public double dumpPerc() {
        int n = 0;
        for (int i = 0; i < this.xrefObj.size(); ++i) {
            if (this.xrefObj.get(i) == null) continue;
            ++n;
        }
        return (double)n * 100.0 / (double)this.xrefObj.size();
    }

    protected void readDocObj() throws IOException {
        int n;
        ArrayList<PdfObject> arrayList = new ArrayList<PdfObject>();
        this.xrefObj = new ArrayList(this.xref.length / 2);
        this.xrefObj.addAll(Collections.nCopies(this.xref.length / 2, null));
        for (n = 2; n < this.xref.length; n += 2) {
            PdfObject pdfObject;
            int n2 = this.xref[n];
            if (n2 <= 0 || this.xref[n + 1] > 0) continue;
            this.tokens.seek(n2);
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError("Invalid object number.");
            }
            this.objNum = this.tokens.intValue();
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError("Invalid generation number.");
            }
            this.objGen = this.tokens.intValue();
            this.tokens.nextValidToken();
            if (!this.tokens.getStringValue().equals("obj")) {
                this.tokens.throwError("Token 'obj' expected.");
            }
            try {
                pdfObject = this.readPRObject();
                if (pdfObject.isStream()) {
                    arrayList.add(pdfObject);
                }
            }
            catch (Exception var5_9) {
                pdfObject = null;
            }
            this.xrefObj.set(n / 2, pdfObject);
        }
        for (n = 0; n < arrayList.size(); ++n) {
            this.checkPRStreamLength((PRStream)arrayList.get(n));
        }
        this.readDecryptedDocObj();
        if (this.objStmMark != null) {
            Iterator iterator = this.objStmMark.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                int n3 = (Integer)entry.getKey();
                IntHashtable intHashtable = (IntHashtable)entry.getValue();
                this.readObjStm((PRStream)this.xrefObj.get(n3), intHashtable);
                this.xrefObj.set(n3, null);
            }
            this.objStmMark = null;
        }
        this.xref = null;
    }

    private void checkPRStreamLength(PRStream pRStream) throws IOException {
        int n;
        block8 : {
            Object object;
            int n2 = this.tokens.length();
            int n3 = pRStream.getOffset();
            boolean bl = false;
            n = 0;
            PdfObject pdfObject = PdfReader.getPdfObjectRelease(pRStream.get(PdfName.LENGTH));
            if (pdfObject != null && pdfObject.type() == 2) {
                n = ((PdfNumber)pdfObject).intValue();
                if (n + n3 > n2 - 20) {
                    bl = true;
                } else {
                    this.tokens.seek(n3 + n);
                    object = this.tokens.readString(20);
                    if (!(object.startsWith("\nendstream") || object.startsWith("\r\nendstream") || object.startsWith("\rendstream") || object.startsWith("endstream"))) {
                        bl = true;
                    }
                }
            } else {
                bl = true;
            }
            if (bl) {
                int n4;
                object = new byte[16];
                this.tokens.seek(n3);
                do {
                    n4 = this.tokens.getFilePointer();
                    if (!this.tokens.readLineSegment((byte[])object)) break block8;
                    if (!PdfReader.equalsn((byte[])object, endstream)) continue;
                    n = n4 - n3;
                    break block8;
                } while (!PdfReader.equalsn((byte[])object, endobj));
                this.tokens.seek(n4 - 16);
                String string = this.tokens.readString(16);
                int n5 = string.indexOf("endstream");
                if (n5 >= 0) {
                    n4 = n4 - 16 + n5;
                }
                n = n4 - n3;
            }
        }
        pRStream.setLength(n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void readObjStm(PRStream pRStream, IntHashtable intHashtable) throws IOException {
        int n = ((PdfNumber)PdfReader.getPdfObject(pRStream.get(PdfName.FIRST))).intValue();
        int n2 = ((PdfNumber)PdfReader.getPdfObject(pRStream.get(PdfName.N))).intValue();
        byte[] arrby = PdfReader.getStreamBytes(pRStream, this.tokens.getFile());
        PRTokeniser pRTokeniser = this.tokens;
        this.tokens = new PRTokeniser(arrby);
        try {
            int n3;
            int[] arrn = new int[n2];
            int[] arrn2 = new int[n2];
            boolean bl = true;
            for (n3 = 0; n3 < n2 && (bl = this.tokens.nextToken()); ++n3) {
                if (this.tokens.getTokenType() != 1) {
                    bl = false;
                    break;
                }
                arrn2[n3] = this.tokens.intValue();
                bl = this.tokens.nextToken();
                if (!bl) break;
                if (this.tokens.getTokenType() != 1) {
                    bl = false;
                    break;
                }
                arrn[n3] = this.tokens.intValue() + n;
            }
            if (!bl) {
                throw new IOException("Error reading ObjStm");
            }
            for (n3 = 0; n3 < n2; ++n3) {
                if (!intHashtable.containsKey(n3)) continue;
                this.tokens.seek(arrn[n3]);
                PdfObject pdfObject = this.readPRObject();
                this.xrefObj.set(arrn2[n3], pdfObject);
            }
        }
        finally {
            this.tokens = pRTokeniser;
        }
    }

    public static PdfObject killIndirect(PdfObject pdfObject) {
        if (pdfObject == null || pdfObject.isNull()) {
            return null;
        }
        PdfObject pdfObject2 = PdfReader.getPdfObjectRelease(pdfObject);
        if (pdfObject.isIndirect()) {
            PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfObject;
            PdfReader pdfReader = pRIndirectReference.getReader();
            int n = pRIndirectReference.getNumber();
            pdfReader.xrefObj.set(n, null);
            if (pdfReader.partial) {
                pdfReader.xref[n * 2] = -1;
            }
        }
        return pdfObject2;
    }

    private void ensureXrefSize(int n) {
        if (n == 0) {
            return;
        }
        if (this.xref == null) {
            this.xref = new int[n];
        } else if (this.xref.length < n) {
            int[] arrn = new int[n];
            System.arraycopy(this.xref, 0, arrn, 0, this.xref.length);
            this.xref = arrn;
        }
    }

    protected void readXref() throws IOException {
        PdfNumber pdfNumber;
        int n;
        this.hybridXref = false;
        this.newXrefType = false;
        this.tokens.seek(this.tokens.getStartxref());
        this.tokens.nextToken();
        if (!this.tokens.getStringValue().equals("startxref")) {
            throw new IOException("startxref not found.");
        }
        this.tokens.nextToken();
        if (this.tokens.getTokenType() != 1) {
            throw new IOException("startxref is not followed by a number.");
        }
        this.lastXref = n = this.tokens.intValue();
        this.eofPos = this.tokens.getFilePointer();
        try {
            if (this.readXRefStream(n)) {
                this.newXrefType = true;
                return;
            }
        }
        catch (Exception var2_2) {
            // empty catch block
        }
        this.xref = null;
        this.tokens.seek(n);
        PdfDictionary pdfDictionary = this.trailer = this.readXrefSection();
        while ((pdfNumber = (PdfNumber)pdfDictionary.get(PdfName.PREV)) != null) {
            this.tokens.seek(pdfNumber.intValue());
            pdfDictionary = this.readXrefSection();
        }
    }

    protected PdfDictionary readXrefSection() throws IOException {
        this.tokens.nextValidToken();
        if (!this.tokens.getStringValue().equals("xref")) {
            this.tokens.throwError("xref subsection not found");
        }
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        block2 : do {
            int n5;
            this.tokens.nextValidToken();
            if (this.tokens.getStringValue().equals("trailer")) break;
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError("Object number of the first object in this xref subsection not found");
            }
            n = this.tokens.intValue();
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError("Number of entries in this xref subsection not found");
            }
            n2 = this.tokens.intValue() + n;
            if (n == 1) {
                n5 = this.tokens.getFilePointer();
                this.tokens.nextValidToken();
                n3 = this.tokens.intValue();
                this.tokens.nextValidToken();
                n4 = this.tokens.intValue();
                if (n3 == 0 && n4 == 65535) {
                    --n;
                    --n2;
                }
                this.tokens.seek(n5);
            }
            this.ensureXrefSize(n2 * 2);
            n5 = n;
            do {
                if (n5 >= n2) continue block2;
                this.tokens.nextValidToken();
                n3 = this.tokens.intValue();
                this.tokens.nextValidToken();
                n4 = this.tokens.intValue();
                this.tokens.nextValidToken();
                int n6 = n5 * 2;
                if (this.tokens.getStringValue().equals("n")) {
                    if (this.xref[n6] == 0 && this.xref[n6 + 1] == 0) {
                        this.xref[n6] = n3;
                    }
                } else if (this.tokens.getStringValue().equals("f")) {
                    if (this.xref[n6] == 0 && this.xref[n6 + 1] == 0) {
                        this.xref[n6] = -1;
                    }
                } else {
                    this.tokens.throwError("Invalid cross-reference entry in this xref subsection");
                }
                ++n5;
            } while (true);
            break;
        } while (true);
        PdfDictionary pdfDictionary = (PdfDictionary)this.readPRObject();
        PdfNumber pdfNumber = (PdfNumber)pdfDictionary.get(PdfName.SIZE);
        this.ensureXrefSize(pdfNumber.intValue() * 2);
        PdfObject pdfObject = pdfDictionary.get(PdfName.XREFSTM);
        if (pdfObject != null && pdfObject.isNumber()) {
            int n7 = ((PdfNumber)pdfObject).intValue();
            try {
                this.readXRefStream(n7);
                this.newXrefType = true;
                this.hybridXref = true;
            }
            catch (IOException var9_11) {
                this.xref = null;
                throw var9_11;
            }
        }
        return pdfDictionary;
    }

    protected boolean readXRefStream(int n) throws IOException {
        PdfArray pdfArray;
        this.tokens.seek(n);
        int n2 = 0;
        if (!this.tokens.nextToken()) {
            return false;
        }
        if (this.tokens.getTokenType() != 1) {
            return false;
        }
        n2 = this.tokens.intValue();
        if (!this.tokens.nextToken() || this.tokens.getTokenType() != 1) {
            return false;
        }
        if (!this.tokens.nextToken() || !this.tokens.getStringValue().equals("obj")) {
            return false;
        }
        PdfObject pdfObject = this.readPRObject();
        PRStream pRStream = null;
        if (pdfObject.isStream()) {
            pRStream = (PRStream)pdfObject;
            if (!PdfName.XREF.equals(pRStream.get(PdfName.TYPE))) {
                return false;
            }
        } else {
            return false;
        }
        if (this.trailer == null) {
            this.trailer = new PdfDictionary();
            this.trailer.putAll(pRStream);
        }
        pRStream.setLength(((PdfNumber)pRStream.get(PdfName.LENGTH)).intValue());
        int n3 = ((PdfNumber)pRStream.get(PdfName.SIZE)).intValue();
        PdfObject pdfObject2 = pRStream.get(PdfName.INDEX);
        if (pdfObject2 == null) {
            pdfArray = new PdfArray();
            pdfArray.add(new int[]{0, n3});
        } else {
            pdfArray = (PdfArray)pdfObject2;
        }
        PdfArray pdfArray2 = (PdfArray)pRStream.get(PdfName.W);
        int n4 = -1;
        pdfObject2 = pRStream.get(PdfName.PREV);
        if (pdfObject2 != null) {
            n4 = ((PdfNumber)pdfObject2).intValue();
        }
        this.ensureXrefSize(n3 * 2);
        if (this.objStmMark == null && !this.partial) {
            this.objStmMark = new HashMap();
        }
        if (this.objStmToOffset == null && this.partial) {
            this.objStmToOffset = new IntHashtable();
        }
        byte[] arrby = PdfReader.getStreamBytes(pRStream, this.tokens.getFile());
        int n5 = 0;
        ArrayList arrayList = pdfArray2.getArrayList();
        int[] arrn = new int[3];
        for (int i = 0; i < 3; ++i) {
            arrn[i] = ((PdfNumber)arrayList.get(i)).intValue();
        }
        ArrayList arrayList2 = pdfArray.getArrayList();
        for (int j = 0; j < arrayList2.size(); j += 2) {
            int n6 = ((PdfNumber)arrayList2.get(j)).intValue();
            int n7 = ((PdfNumber)arrayList2.get(j + 1)).intValue();
            this.ensureXrefSize((n6 + n7) * 2);
            while (n7-- > 0) {
                int n8;
                int n9;
                int n10;
                int n11 = 1;
                if (arrn[0] > 0) {
                    n11 = 0;
                    for (n9 = 0; n9 < arrn[0]; ++n9) {
                        n11 = (n11 << 8) + (arrby[n5++] & 255);
                    }
                }
                n9 = 0;
                for (n10 = 0; n10 < arrn[1]; ++n10) {
                    n9 = (n9 << 8) + (arrby[n5++] & 255);
                }
                n10 = 0;
                for (n8 = 0; n8 < arrn[2]; ++n8) {
                    n10 = (n10 << 8) + (arrby[n5++] & 255);
                }
                n8 = n6 * 2;
                if (this.xref[n8] == 0 && this.xref[n8 + 1] == 0) {
                    switch (n11) {
                        case 0: {
                            this.xref[n8] = -1;
                            break;
                        }
                        case 1: {
                            this.xref[n8] = n9;
                            break;
                        }
                        case 2: {
                            this.xref[n8] = n10;
                            this.xref[n8 + 1] = n9;
                            if (this.partial) {
                                this.objStmToOffset.put(n9, 0);
                                break;
                            }
                            Integer n12 = new Integer(n9);
                            IntHashtable intHashtable = (IntHashtable)this.objStmMark.get(n12);
                            if (intHashtable == null) {
                                intHashtable = new IntHashtable();
                                intHashtable.put(n10, 1);
                                this.objStmMark.put(n12, intHashtable);
                                break;
                            }
                            intHashtable.put(n10, 1);
                        }
                    }
                }
                ++n6;
            }
        }
        if ((n2 *= 2) < this.xref.length) {
            this.xref[n2] = -1;
        }
        if (n4 == -1) {
            return true;
        }
        return this.readXRefStream(n4);
    }

    protected void rebuildXref() throws IOException {
        int n;
        int[] arrn;
        this.hybridXref = false;
        this.newXrefType = false;
        this.tokens.seek(0);
        int[][] arrarrn = new int[1024][];
        int n2 = 0;
        this.trailer = null;
        byte[] arrby = new byte[64];
        do {
            n = this.tokens.getFilePointer();
            if (!this.tokens.readLineSegment(arrby)) break;
            if (arrby[0] == 116) {
                if (!PdfEncodings.convertToString(arrby, null).startsWith("trailer")) continue;
                this.tokens.seek(n);
                this.tokens.nextToken();
                n = this.tokens.getFilePointer();
                try {
                    arrn = (PdfDictionary)this.readPRObject();
                    if (arrn.get(PdfName.ROOT) != null) {
                        this.trailer = arrn;
                        continue;
                    }
                    this.tokens.seek(n);
                }
                catch (Exception var5_6) {
                    this.tokens.seek(n);
                }
                continue;
            }
            if (arrby[0] < 48 || arrby[0] > 57 || (arrn = PRTokeniser.checkObjectStart(arrby)) == null) continue;
            int n3 = arrn[0];
            int n4 = arrn[1];
            if (n3 >= arrarrn.length) {
                int n5 = n3 * 2;
                int[][] arrarrn2 = new int[n5][];
                System.arraycopy(arrarrn, 0, arrarrn2, 0, n2);
                arrarrn = arrarrn2;
            }
            if (n3 >= n2) {
                n2 = n3 + 1;
            }
            if (arrarrn[n3] != null && n4 < arrarrn[n3][1]) continue;
            arrn[0] = n;
            arrarrn[n3] = arrn;
        } while (true);
        if (this.trailer == null) {
            throw new IOException("trailer not found.");
        }
        this.xref = new int[n2 * 2];
        for (n = 0; n < n2; ++n) {
            arrn = arrarrn[n];
            if (arrn == null) continue;
            this.xref[n * 2] = arrn[0];
        }
    }

    protected PdfDictionary readDictionary() throws IOException {
        PdfDictionary pdfDictionary = new PdfDictionary();
        do {
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() == 8) break;
            if (this.tokens.getTokenType() != 3) {
                this.tokens.throwError("Dictionary key is not a name.");
            }
            PdfName pdfName = new PdfName(this.tokens.getStringValue(), false);
            PdfObject pdfObject = this.readPRObject();
            int n = pdfObject.type();
            if (- n == 8) {
                this.tokens.throwError("Unexpected '>>'");
            }
            if (- n == 6) {
                this.tokens.throwError("Unexpected ']'");
            }
            pdfDictionary.put(pdfName, pdfObject);
        } while (true);
        return pdfDictionary;
    }

    protected PdfArray readArray() throws IOException {
        PdfObject pdfObject;
        int n;
        PdfArray pdfArray = new PdfArray();
        while (- (n = (pdfObject = this.readPRObject()).type()) != 6) {
            if (- n == 8) {
                this.tokens.throwError("Unexpected '>>'");
            }
            pdfArray.add(pdfObject);
        }
        return pdfArray;
    }

    protected PdfObject readPRObject() throws IOException {
        this.tokens.nextValidToken();
        int n = this.tokens.getTokenType();
        switch (n) {
            case 7: {
                PdfDictionary pdfDictionary = this.readDictionary();
                int n2 = this.tokens.getFilePointer();
                if (this.tokens.nextToken() && this.tokens.getStringValue().equals("stream")) {
                    int n3 = this.tokens.read();
                    if (n3 != 10) {
                        n3 = this.tokens.read();
                    }
                    if (n3 != 10) {
                        this.tokens.backOnePosition(n3);
                    }
                    PRStream pRStream = new PRStream(this, this.tokens.getFilePointer());
                    pRStream.putAll(pdfDictionary);
                    pRStream.setObjNum(this.objNum, this.objGen);
                    return pRStream;
                }
                this.tokens.seek(n2);
                return pdfDictionary;
            }
            case 5: {
                return this.readArray();
            }
            case 1: {
                return new PdfNumber(this.tokens.getStringValue());
            }
            case 2: {
                PdfString pdfString = new PdfString(this.tokens.getStringValue(), null).setHexWriting(this.tokens.isHexString());
                pdfString.setObjNum(this.objNum, this.objGen);
                if (this.strings != null) {
                    this.strings.add(pdfString);
                }
                return pdfString;
            }
            case 3: {
                return new PdfName(this.tokens.getStringValue(), false);
            }
            case 9: {
                int n4 = this.tokens.getReference();
                PRIndirectReference pRIndirectReference = new PRIndirectReference(this, n4, this.tokens.getGeneration());
                return pRIndirectReference;
            }
        }
        String string = this.tokens.getStringValue();
        if ("null".equals(string)) {
            return PdfNull.PDFNULL;
        }
        if ("true".equals(string)) {
            return PdfBoolean.PDFTRUE;
        }
        if ("false".equals(string)) {
            return PdfBoolean.PDFFALSE;
        }
        return new PdfLiteral(- n, this.tokens.getStringValue());
    }

    public static byte[] FlateDecode(byte[] arrby) {
        byte[] arrby2 = PdfReader.FlateDecode(arrby, true);
        if (arrby2 == null) {
            return PdfReader.FlateDecode(arrby, false);
        }
        return arrby2;
    }

    public static byte[] decodePredictor(byte[] arrby, PdfObject pdfObject) {
        if (pdfObject == null || !pdfObject.isDictionary()) {
            return arrby;
        }
        PdfDictionary pdfDictionary = (PdfDictionary)pdfObject;
        PdfObject pdfObject2 = PdfReader.getPdfObject(pdfDictionary.get(PdfName.PREDICTOR));
        if (pdfObject2 == null || !pdfObject2.isNumber()) {
            return arrby;
        }
        int n = ((PdfNumber)pdfObject2).intValue();
        if (n < 10) {
            return arrby;
        }
        int n2 = 1;
        pdfObject2 = PdfReader.getPdfObject(pdfDictionary.get(PdfName.COLUMNS));
        if (pdfObject2 != null && pdfObject2.isNumber()) {
            n2 = ((PdfNumber)pdfObject2).intValue();
        }
        int n3 = 1;
        pdfObject2 = PdfReader.getPdfObject(pdfDictionary.get(PdfName.COLORS));
        if (pdfObject2 != null && pdfObject2.isNumber()) {
            n3 = ((PdfNumber)pdfObject2).intValue();
        }
        int n4 = 8;
        pdfObject2 = PdfReader.getPdfObject(pdfDictionary.get(PdfName.BITSPERCOMPONENT));
        if (pdfObject2 != null && pdfObject2.isNumber()) {
            n4 = ((PdfNumber)pdfObject2).intValue();
        }
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(arrby));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(arrby.length);
        int n5 = n3 * n4 / 8;
        int n6 = (n3 * n2 * n4 + 7) / 8;
        byte[] arrby2 = new byte[n6];
        byte[] arrby3 = new byte[n6];
        do {
            int n7 = 0;
            try {
                n7 = dataInputStream.read();
                if (n7 < 0) {
                    return byteArrayOutputStream.toByteArray();
                }
                dataInputStream.readFully(arrby2, 0, n6);
            }
            catch (Exception var15_16) {
                return byteArrayOutputStream.toByteArray();
            }
            switch (n7) {
                case 0: {
                    break;
                }
                case 1: {
                    for (int i = n5; i < n6; ++i) {
                        byte[] arrby4 = arrby2;
                        int n8 = i;
                        arrby4[n8] = (byte)(arrby4[n8] + arrby2[i - n5]);
                    }
                    break;
                }
                case 2: {
                    for (int i = 0; i < n6; ++i) {
                        byte[] arrby5 = arrby2;
                        int n9 = i;
                        arrby5[n9] = (byte)(arrby5[n9] + arrby3[i]);
                    }
                    break;
                }
                case 3: {
                    int n10;
                    for (n10 = 0; n10 < n5; ++n10) {
                        byte[] arrby6 = arrby2;
                        int n11 = n10;
                        arrby6[n11] = (byte)(arrby6[n11] + arrby3[n10] / 2);
                    }
                    for (n10 = n5; n10 < n6; ++n10) {
                        byte[] arrby7 = arrby2;
                        int n12 = n10;
                        arrby7[n12] = (byte)(arrby7[n12] + ((arrby2[n10 - n5] & 255) + (arrby3[n10] & 255)) / 2);
                    }
                    break;
                }
                case 4: {
                    int n13;
                    for (n13 = 0; n13 < n5; ++n13) {
                        byte[] arrby8 = arrby2;
                        int n14 = n13;
                        arrby8[n14] = (byte)(arrby8[n14] + arrby3[n13]);
                    }
                    n13 = n5;
                    while (n13 < n6) {
                        int n15 = arrby2[n13 - n5] & 255;
                        int n16 = arrby3[n13] & 255;
                        int n17 = arrby3[n13 - n5] & 255;
                        int n18 = n15 + n16 - n17;
                        int n19 = Math.abs(n18 - n15);
                        int n20 = Math.abs(n18 - n16);
                        int n21 = Math.abs(n18 - n17);
                        int n22 = n19 <= n20 && n19 <= n21 ? n15 : (n20 <= n21 ? n16 : n17);
                        byte[] arrby9 = arrby2;
                        int n23 = n13++;
                        arrby9[n23] = (byte)(arrby9[n23] + (byte)n22);
                    }
                    break;
                }
                default: {
                    throw new RuntimeException("PNG filter unknown.");
                }
            }
            try {
                byteArrayOutputStream.write(arrby2);
            }
            catch (IOException var15_21) {
                // empty catch block
            }
            byte[] arrby10 = arrby3;
            arrby3 = arrby2;
            arrby2 = arrby10;
        } while (true);
    }

    public static byte[] FlateDecode(byte[] arrby, boolean bl) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrby2 = new byte[bl ? 4092 : 1];
        try {
            int n;
            while ((n = inflaterInputStream.read(arrby2)) >= 0) {
                byteArrayOutputStream.write(arrby2, 0, n);
            }
            inflaterInputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (Exception var6_7) {
            if (bl) {
                return null;
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static byte[] ASCIIHexDecode(byte[] arrby) {
        int n;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean bl = true;
        int n2 = 0;
        for (int i = 0; i < arrby.length && (n = arrby[i] & 255) != 62; ++i) {
            if (PRTokeniser.isWhitespace(n)) continue;
            int n3 = PRTokeniser.getHex(n);
            if (n3 == -1) {
                throw new RuntimeException("Illegal character in ASCIIHexDecode.");
            }
            if (bl) {
                n2 = n3;
            } else {
                byteArrayOutputStream.write((byte)((n2 << 4) + n3));
            }
            bl = !bl;
        }
        if (!bl) {
            byteArrayOutputStream.write((byte)(n2 << 4));
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] ASCII85Decode(byte[] arrby) {
        int n;
        int n2;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int n3 = 0;
        int[] arrn = new int[5];
        for (n = 0; n < arrby.length && (n2 = arrby[n] & 255) != 126; ++n) {
            if (PRTokeniser.isWhitespace(n2)) continue;
            if (n2 == 122 && n3 == 0) {
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(0);
                continue;
            }
            if (n2 < 33 || n2 > 117) {
                throw new RuntimeException("Illegal character in ASCII85Decode.");
            }
            arrn[n3] = n2 - 33;
            if (++n3 != 5) continue;
            n3 = 0;
            int n4 = 0;
            for (int i = 0; i < 5; ++i) {
                n4 = n4 * 85 + arrn[i];
            }
            byteArrayOutputStream.write((byte)(n4 >> 24));
            byteArrayOutputStream.write((byte)(n4 >> 16));
            byteArrayOutputStream.write((byte)(n4 >> 8));
            byteArrayOutputStream.write((byte)n4);
        }
        n = 0;
        if (n3 == 2) {
            n = arrn[0] * 85 * 85 * 85 * 85 + arrn[1] * 85 * 85 * 85 + 614125 + 7225 + 85;
            byteArrayOutputStream.write((byte)(n >> 24));
        } else if (n3 == 3) {
            n = arrn[0] * 85 * 85 * 85 * 85 + arrn[1] * 85 * 85 * 85 + arrn[2] * 85 * 85 + 7225 + 85;
            byteArrayOutputStream.write((byte)(n >> 24));
            byteArrayOutputStream.write((byte)(n >> 16));
        } else if (n3 == 4) {
            n = arrn[0] * 85 * 85 * 85 * 85 + arrn[1] * 85 * 85 * 85 + arrn[2] * 85 * 85 + arrn[3] * 85 + 85;
            byteArrayOutputStream.write((byte)(n >> 24));
            byteArrayOutputStream.write((byte)(n >> 16));
            byteArrayOutputStream.write((byte)(n >> 8));
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] LZWDecode(byte[] arrby) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        LZWDecoder lZWDecoder = new LZWDecoder();
        lZWDecoder.decode(arrby, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public boolean isRebuilt() {
        return this.rebuilt;
    }

    public PdfDictionary getPageN(int n) {
        PdfDictionary pdfDictionary = this.pageRefs.getPageN(n);
        if (pdfDictionary == null) {
            return null;
        }
        if (this.appendable) {
            pdfDictionary.setIndRef(this.pageRefs.getPageOrigRef(n));
        }
        return pdfDictionary;
    }

    public PdfDictionary getPageNRelease(int n) {
        PdfDictionary pdfDictionary = this.getPageN(n);
        this.pageRefs.releasePage(n);
        return pdfDictionary;
    }

    public void releasePage(int n) {
        this.pageRefs.releasePage(n);
    }

    public void resetReleasePage() {
        this.pageRefs.resetReleasePage();
    }

    public PRIndirectReference getPageOrigRef(int n) {
        return this.pageRefs.getPageOrigRef(n);
    }

    public byte[] getPageContent(int n, RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        PdfDictionary pdfDictionary = this.getPageNRelease(n);
        if (pdfDictionary == null) {
            return null;
        }
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.CONTENTS));
        if (pdfObject == null) {
            return new byte[0];
        }
        ByteArrayOutputStream byteArrayOutputStream = null;
        if (pdfObject.isStream()) {
            return PdfReader.getStreamBytes((PRStream)pdfObject, randomAccessFileOrArray);
        }
        if (pdfObject.isArray()) {
            PdfArray pdfArray = (PdfArray)pdfObject;
            ArrayList arrayList = pdfArray.getArrayList();
            byteArrayOutputStream = new ByteArrayOutputStream();
            for (int i = 0; i < arrayList.size(); ++i) {
                PdfObject pdfObject2 = PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(i));
                if (pdfObject2 == null || !pdfObject2.isStream()) continue;
                byte[] arrby = PdfReader.getStreamBytes((PRStream)pdfObject2, randomAccessFileOrArray);
                byteArrayOutputStream.write(arrby);
                if (i == arrayList.size() - 1) continue;
                byteArrayOutputStream.write(10);
            }
            return byteArrayOutputStream.toByteArray();
        }
        return new byte[0];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getPageContent(int n) throws IOException {
        RandomAccessFileOrArray randomAccessFileOrArray = this.getSafeFile();
        try {
            randomAccessFileOrArray.reOpen();
            byte[] arrby = this.getPageContent(n, randomAccessFileOrArray);
            return arrby;
        }
        finally {
            try {
                randomAccessFileOrArray.close();
            }
            catch (Exception var4_4) {}
        }
    }

    protected void killXref(PdfObject pdfObject) {
        if (pdfObject == null) {
            return;
        }
        if (pdfObject instanceof PdfIndirectReference && !pdfObject.isIndirect()) {
            return;
        }
        switch (pdfObject.type()) {
            case 10: {
                int n = ((PRIndirectReference)pdfObject).getNumber();
                pdfObject = (PdfObject)this.xrefObj.get(n);
                this.xrefObj.set(n, null);
                this.freeXref = n;
                this.killXref(pdfObject);
                break;
            }
            case 5: {
                ArrayList arrayList = ((PdfArray)pdfObject).getArrayList();
                for (int i = 0; i < arrayList.size(); ++i) {
                    this.killXref((PdfObject)arrayList.get(i));
                }
                break;
            }
            case 6: 
            case 7: {
                PdfDictionary pdfDictionary = (PdfDictionary)pdfObject;
                Iterator iterator = pdfDictionary.getKeys().iterator();
                while (iterator.hasNext()) {
                    this.killXref(pdfDictionary.get((PdfName)iterator.next()));
                }
                break;
            }
        }
    }

    public void setPageContent(int n, byte[] arrby) {
        this.setPageContent(n, arrby, -1);
    }

    public void setPageContent(int n, byte[] arrby, int n2) {
        PdfDictionary pdfDictionary = this.getPageN(n);
        if (pdfDictionary == null) {
            return;
        }
        PdfObject pdfObject = pdfDictionary.get(PdfName.CONTENTS);
        this.freeXref = -1;
        this.killXref(pdfObject);
        if (this.freeXref == -1) {
            this.xrefObj.add(null);
            this.freeXref = this.xrefObj.size() - 1;
        }
        pdfDictionary.put(PdfName.CONTENTS, new PRIndirectReference(this, this.freeXref));
        this.xrefObj.set(this.freeXref, new PRStream(this, arrby, n2));
    }

    public static byte[] getStreamBytes(PRStream pRStream, RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(pRStream.get(PdfName.FILTER));
        byte[] arrby = PdfReader.getStreamBytesRaw(pRStream, randomAccessFileOrArray);
        ArrayList arrayList = new ArrayList();
        if (pdfObject != null) {
            if (pdfObject.isName()) {
                arrayList.add((PdfObject)pdfObject);
            } else if (pdfObject.isArray()) {
                arrayList = ((PdfArray)pdfObject).getArrayList();
            }
        }
        ArrayList arrayList2 = new ArrayList();
        PdfObject pdfObject2 = PdfReader.getPdfObjectRelease(pRStream.get(PdfName.DECODEPARMS));
        if (pdfObject2 == null || !pdfObject2.isDictionary() && !pdfObject2.isArray()) {
            pdfObject2 = PdfReader.getPdfObjectRelease(pRStream.get(PdfName.DP));
        }
        if (pdfObject2 != null) {
            if (pdfObject2.isDictionary()) {
                arrayList2.add((PdfObject)pdfObject2);
            } else if (pdfObject2.isArray()) {
                arrayList2 = ((PdfArray)pdfObject2).getArrayList();
            }
        }
        for (int i = 0; i < arrayList.size(); ++i) {
            PdfObject pdfObject3;
            String string = ((PdfName)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(i))).toString();
            if (string.equals("/FlateDecode") || string.equals("/Fl")) {
                arrby = PdfReader.FlateDecode(arrby);
                pdfObject3 = null;
                if (i >= arrayList2.size()) continue;
                pdfObject3 = (PdfObject)arrayList2.get(i);
                arrby = PdfReader.decodePredictor(arrby, pdfObject3);
                continue;
            }
            if (string.equals("/ASCIIHexDecode") || string.equals("/AHx")) {
                arrby = PdfReader.ASCIIHexDecode(arrby);
                continue;
            }
            if (string.equals("/ASCII85Decode") || string.equals("/A85")) {
                arrby = PdfReader.ASCII85Decode(arrby);
                continue;
            }
            if (string.equals("/LZWDecode")) {
                arrby = PdfReader.LZWDecode(arrby);
                pdfObject3 = null;
                if (i >= arrayList2.size()) continue;
                pdfObject3 = (PdfObject)arrayList2.get(i);
                arrby = PdfReader.decodePredictor(arrby, pdfObject3);
                continue;
            }
            if (string.equals("/Crypt")) continue;
            throw new IOException("The filter " + string + " is not supported.");
        }
        return arrby;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getStreamBytes(PRStream pRStream) throws IOException {
        RandomAccessFileOrArray randomAccessFileOrArray = pRStream.getReader().getSafeFile();
        try {
            randomAccessFileOrArray.reOpen();
            byte[] arrby = PdfReader.getStreamBytes(pRStream, randomAccessFileOrArray);
            return arrby;
        }
        finally {
            try {
                randomAccessFileOrArray.close();
            }
            catch (Exception var3_3) {}
        }
    }

    public static byte[] getStreamBytesRaw(PRStream pRStream, RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        byte[] arrby;
        PdfReader pdfReader = pRStream.getReader();
        if (pRStream.getOffset() < 0) {
            arrby = pRStream.getBytes();
        } else {
            arrby = new byte[pRStream.getLength()];
            randomAccessFileOrArray.seek(pRStream.getOffset());
            randomAccessFileOrArray.readFully(arrby);
            PdfEncryption pdfEncryption = pdfReader.getDecrypt();
            if (pdfEncryption != null) {
                PdfObject pdfObject = PdfReader.getPdfObjectRelease(pRStream.get(PdfName.FILTER));
                ArrayList arrayList = new ArrayList();
                if (pdfObject != null) {
                    if (pdfObject.isName()) {
                        arrayList.add((PdfObject)pdfObject);
                    } else if (pdfObject.isArray()) {
                        arrayList = ((PdfArray)pdfObject).getArrayList();
                    }
                }
                boolean bl = false;
                for (int i = 0; i < arrayList.size(); ++i) {
                    PdfObject pdfObject2 = PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(i));
                    if (pdfObject2 == null || !pdfObject2.toString().equals("/Crypt")) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    pdfEncryption.setHashKey(pRStream.getObjNum(), pRStream.getObjGen());
                    arrby = pdfEncryption.decryptByteArray(arrby);
                }
            }
        }
        return arrby;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getStreamBytesRaw(PRStream pRStream) throws IOException {
        RandomAccessFileOrArray randomAccessFileOrArray = pRStream.getReader().getSafeFile();
        try {
            randomAccessFileOrArray.reOpen();
            byte[] arrby = PdfReader.getStreamBytesRaw(pRStream, randomAccessFileOrArray);
            return arrby;
        }
        finally {
            try {
                randomAccessFileOrArray.close();
            }
            catch (Exception var3_3) {}
        }
    }

    public void eliminateSharedStreams() {
        int n;
        if (!this.sharedStreams) {
            return;
        }
        this.sharedStreams = false;
        if (this.pageRefs.size() == 1) {
            return;
        }
        ArrayList<PdfIndirectReference> arrayList = new ArrayList<PdfIndirectReference>();
        ArrayList<PRStream> arrayList2 = new ArrayList<PRStream>();
        IntHashtable intHashtable = new IntHashtable();
        for (n = 1; n <= this.pageRefs.size(); ++n) {
            PdfObject pdfObject;
            PdfIndirectReference pdfIndirectReference;
            PdfDictionary pdfDictionary = this.pageRefs.getPageN(n);
            if (pdfDictionary == null || (pdfObject = PdfReader.getPdfObject(pdfDictionary.get(PdfName.CONTENTS))) == null) continue;
            if (pdfObject.isStream()) {
                pdfIndirectReference = (PRIndirectReference)pdfDictionary.get(PdfName.CONTENTS);
                if (intHashtable.containsKey(pdfIndirectReference.getNumber())) {
                    arrayList.add(pdfIndirectReference);
                    arrayList2.add(new PRStream((PRStream)pdfObject, null));
                    continue;
                }
                intHashtable.put(pdfIndirectReference.getNumber(), 1);
                continue;
            }
            if (!pdfObject.isArray()) continue;
            pdfIndirectReference = (PdfArray)pdfObject;
            ArrayList arrayList3 = pdfIndirectReference.getArrayList();
            for (int i = 0; i < arrayList3.size(); ++i) {
                PRIndirectReference pRIndirectReference = (PRIndirectReference)arrayList3.get(i);
                if (intHashtable.containsKey(pRIndirectReference.getNumber())) {
                    arrayList.add(pRIndirectReference);
                    arrayList2.add(new PRStream((PRStream)PdfReader.getPdfObject(pRIndirectReference), null));
                    continue;
                }
                intHashtable.put(pRIndirectReference.getNumber(), 1);
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        for (n = 0; n < arrayList2.size(); ++n) {
            this.xrefObj.add(arrayList2.get(n));
            PRIndirectReference pRIndirectReference = (PRIndirectReference)arrayList.get(n);
            pRIndirectReference.setNumber(this.xrefObj.size() - 1, 0);
        }
    }

    public boolean isTampered() {
        return this.tampered;
    }

    public void setTampered(boolean bl) {
        this.tampered = bl;
        this.pageRefs.keepPages();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getMetadata() throws IOException {
        PdfObject pdfObject = PdfReader.getPdfObject(this.catalog.get(PdfName.METADATA));
        if (!(pdfObject instanceof PRStream)) {
            return null;
        }
        RandomAccessFileOrArray randomAccessFileOrArray = this.getSafeFile();
        byte[] arrby = null;
        try {
            randomAccessFileOrArray.reOpen();
            arrby = PdfReader.getStreamBytes((PRStream)pdfObject, randomAccessFileOrArray);
        }
        finally {
            try {
                randomAccessFileOrArray.close();
            }
            catch (Exception var4_4) {}
        }
        return arrby;
    }

    public int getLastXref() {
        return this.lastXref;
    }

    public int getXrefSize() {
        return this.xrefObj.size();
    }

    public int getEofPos() {
        return this.eofPos;
    }

    public char getPdfVersion() {
        return this.pdfVersion;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public int getPermissions() {
        return this.pValue;
    }

    public boolean is128Key() {
        return this.rValue == 3;
    }

    public PdfDictionary getTrailer() {
        return this.trailer;
    }

    PdfEncryption getDecrypt() {
        return this.decrypt;
    }

    static boolean equalsn(byte[] arrby, byte[] arrby2) {
        int n = arrby2.length;
        for (int i = 0; i < n; ++i) {
            if (arrby[i] == arrby2[i]) continue;
            return false;
        }
        return true;
    }

    static boolean existsName(PdfDictionary pdfDictionary, PdfName pdfName, PdfName pdfName2) {
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(pdfName));
        if (pdfObject == null || !pdfObject.isName()) {
            return false;
        }
        PdfName pdfName3 = (PdfName)pdfObject;
        return pdfName3.equals(pdfName2);
    }

    static String getFontName(PdfDictionary pdfDictionary) {
        if (pdfDictionary == null) {
            return null;
        }
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.BASEFONT));
        if (pdfObject == null || !pdfObject.isName()) {
            return null;
        }
        return PdfName.decodeName(pdfObject.toString());
    }

    static String getSubsetPrefix(PdfDictionary pdfDictionary) {
        if (pdfDictionary == null) {
            return null;
        }
        String string = PdfReader.getFontName(pdfDictionary);
        if (string == null) {
            return null;
        }
        if (string.length() < 8 || string.charAt(6) != '+') {
            return null;
        }
        for (int i = 0; i < 6; ++i) {
            char c = string.charAt(i);
            if (c >= 'A' && c <= 'Z') continue;
            return null;
        }
        return string;
    }

    public int shuffleSubsetNames() {
        int n = 0;
        for (int i = 1; i < this.xrefObj.size(); ++i) {
            String string;
            PdfDictionary pdfDictionary;
            Object object;
            Object object2;
            PdfDictionary pdfDictionary2;
            String string2;
            PdfObject pdfObject = this.getPdfObjectRelease(i);
            if (pdfObject == null || !pdfObject.isDictionary() || !PdfReader.existsName(pdfDictionary2 = (PdfDictionary)pdfObject, PdfName.TYPE, PdfName.FONT)) continue;
            if (PdfReader.existsName(pdfDictionary2, PdfName.SUBTYPE, PdfName.TYPE1) || PdfReader.existsName(pdfDictionary2, PdfName.SUBTYPE, PdfName.MMTYPE1) || PdfReader.existsName(pdfDictionary2, PdfName.SUBTYPE, PdfName.TRUETYPE)) {
                string2 = PdfReader.getSubsetPrefix(pdfDictionary2);
                if (string2 == null) continue;
                object2 = BaseFont.createSubsetPrefix() + string2.substring(7);
                object = new PdfName((String)object2);
                pdfDictionary2.put(PdfName.BASEFONT, (PdfObject)object);
                this.setXrefPartialObject(i, pdfDictionary2);
                ++n;
                pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.FONTDESCRIPTOR));
                if (pdfDictionary == null) continue;
                pdfDictionary.put(PdfName.FONTNAME, (PdfObject)object);
                continue;
            }
            if (!PdfReader.existsName(pdfDictionary2, PdfName.SUBTYPE, PdfName.TYPE0)) continue;
            string2 = PdfReader.getSubsetPrefix(pdfDictionary2);
            object2 = (PdfArray)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.DESCENDANTFONTS));
            if (object2 == null || (object = object2.getArrayList()).isEmpty() || (string = PdfReader.getSubsetPrefix(pdfDictionary = (PdfDictionary)PdfReader.getPdfObject((PdfObject)object.get(0)))) == null) continue;
            String string3 = BaseFont.createSubsetPrefix();
            if (string2 != null) {
                pdfDictionary2.put(PdfName.BASEFONT, new PdfName(string3 + string2.substring(7)));
            }
            this.setXrefPartialObject(i, pdfDictionary2);
            PdfName pdfName = new PdfName(string3 + string.substring(7));
            pdfDictionary.put(PdfName.BASEFONT, pdfName);
            ++n;
            PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.FONTDESCRIPTOR));
            if (pdfDictionary3 == null) continue;
            pdfDictionary3.put(PdfName.FONTNAME, pdfName);
        }
        return n;
    }

    public int createFakeFontSubsets() {
        int n = 0;
        for (int i = 1; i < this.xrefObj.size(); ++i) {
            PdfDictionary pdfDictionary;
            String string;
            PdfObject pdfObject = this.getPdfObjectRelease(i);
            if (pdfObject == null || !pdfObject.isDictionary() || !PdfReader.existsName(pdfDictionary = (PdfDictionary)pdfObject, PdfName.TYPE, PdfName.FONT) || !PdfReader.existsName(pdfDictionary, PdfName.SUBTYPE, PdfName.TYPE1) && !PdfReader.existsName(pdfDictionary, PdfName.SUBTYPE, PdfName.MMTYPE1) && !PdfReader.existsName(pdfDictionary, PdfName.SUBTYPE, PdfName.TRUETYPE) || (string = PdfReader.getSubsetPrefix(pdfDictionary)) != null || (string = PdfReader.getFontName(pdfDictionary)) == null) continue;
            String string2 = BaseFont.createSubsetPrefix() + string;
            PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.FONTDESCRIPTOR));
            if (pdfDictionary2 == null || pdfDictionary2.get(PdfName.FONTFILE) == null && pdfDictionary2.get(PdfName.FONTFILE2) == null && pdfDictionary2.get(PdfName.FONTFILE3) == null) continue;
            pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.FONTDESCRIPTOR));
            PdfName pdfName = new PdfName(string2);
            pdfDictionary.put(PdfName.BASEFONT, pdfName);
            pdfDictionary2.put(PdfName.FONTNAME, pdfName);
            this.setXrefPartialObject(i, pdfDictionary);
            ++n;
        }
        return n;
    }

    private static PdfArray getNameArray(PdfObject pdfObject) {
        PdfObject pdfObject2;
        if (pdfObject == null) {
            return null;
        }
        if ((pdfObject = PdfReader.getPdfObjectRelease(pdfObject)) == null) {
            return null;
        }
        if (pdfObject.isArray()) {
            return (PdfArray)pdfObject;
        }
        if (pdfObject.isDictionary() && (pdfObject2 = PdfReader.getPdfObjectRelease(((PdfDictionary)pdfObject).get(PdfName.D))) != null && pdfObject2.isArray()) {
            return (PdfArray)pdfObject2;
        }
        return null;
    }

    public HashMap getNamedDestination() {
        HashMap hashMap = this.getNamedDestinationFromNames();
        hashMap.putAll(this.getNamedDestinationFromStrings());
        return hashMap;
    }

    public HashMap getNamedDestinationFromNames() {
        HashMap<String, PdfArray> hashMap = new HashMap<String, PdfArray>();
        if (this.catalog.get(PdfName.DESTS) != null) {
            PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(this.catalog.get(PdfName.DESTS));
            if (pdfDictionary == null) {
                return hashMap;
            }
            Set set = pdfDictionary.getKeys();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                PdfName pdfName = (PdfName)iterator.next();
                String string = PdfName.decodeName(pdfName.toString());
                PdfArray pdfArray = PdfReader.getNameArray(pdfDictionary.get(pdfName));
                if (pdfArray == null) continue;
                hashMap.put(string, pdfArray);
            }
        }
        return hashMap;
    }

    public HashMap getNamedDestinationFromStrings() {
        PdfDictionary pdfDictionary;
        if (this.catalog.get(PdfName.NAMES) != null && (pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(this.catalog.get(PdfName.NAMES))) != null && (pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.DESTS))) != null) {
            HashMap hashMap = PdfNameTree.readTree(pdfDictionary);
            Iterator iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                PdfArray pdfArray = PdfReader.getNameArray((PdfObject)entry.getValue());
                if (pdfArray != null) {
                    entry.setValue(pdfArray);
                    continue;
                }
                iterator.remove();
            }
            return hashMap;
        }
        return new HashMap();
    }

    private boolean replaceNamedDestination(PdfObject pdfObject, HashMap hashMap) {
        pdfObject = PdfReader.getPdfObject(pdfObject);
        int n = this.lastXrefPartial;
        this.releaseLastXrefPartial();
        if (pdfObject != null && pdfObject.isDictionary()) {
            PdfObject pdfObject2 = PdfReader.getPdfObjectRelease(((PdfDictionary)pdfObject).get(PdfName.DEST));
            String string = null;
            if (pdfObject2 != null) {
                if (pdfObject2.isName()) {
                    string = PdfName.decodeName(pdfObject2.toString());
                } else if (pdfObject2.isString()) {
                    string = pdfObject2.toString();
                }
                PdfArray pdfArray = (PdfArray)hashMap.get(string);
                if (pdfArray != null) {
                    ((PdfDictionary)pdfObject).put(PdfName.DEST, pdfArray);
                    this.setXrefPartialObject(n, pdfObject);
                    return true;
                }
            } else {
                pdfObject2 = PdfReader.getPdfObject(((PdfDictionary)pdfObject).get(PdfName.A));
                if (pdfObject2 != null) {
                    int n2 = this.lastXrefPartial;
                    this.releaseLastXrefPartial();
                    PdfDictionary pdfDictionary = (PdfDictionary)pdfObject2;
                    PdfName pdfName = (PdfName)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.S));
                    if (PdfName.GOTO.equals(pdfName)) {
                        PdfArray pdfArray;
                        PdfObject pdfObject3 = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.D));
                        if (pdfObject3 != null) {
                            if (pdfObject3.isName()) {
                                string = PdfName.decodeName(pdfObject3.toString());
                            } else if (pdfObject3.isString()) {
                                string = pdfObject3.toString();
                            }
                        }
                        if ((pdfArray = (PdfArray)hashMap.get(string)) != null) {
                            pdfDictionary.put(PdfName.D, pdfArray);
                            this.setXrefPartialObject(n2, pdfObject2);
                            this.setXrefPartialObject(n, pdfObject);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void removeFields() {
        this.pageRefs.resetReleasePage();
        for (int i = 1; i <= this.pageRefs.size(); ++i) {
            PdfDictionary pdfDictionary = this.pageRefs.getPageN(i);
            PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ANNOTS));
            if (pdfArray == null) {
                this.pageRefs.releasePage(i);
                continue;
            }
            ArrayList arrayList = pdfArray.getArrayList();
            for (int j = 0; j < arrayList.size(); ++j) {
                PdfDictionary pdfDictionary2;
                PdfObject pdfObject = PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(j));
                if (pdfObject == null || !pdfObject.isDictionary() || !PdfName.WIDGET.equals((pdfDictionary2 = (PdfDictionary)pdfObject).get(PdfName.SUBTYPE))) continue;
                arrayList.remove(j--);
            }
            if (arrayList.isEmpty()) {
                pdfDictionary.remove(PdfName.ANNOTS);
                continue;
            }
            this.pageRefs.releasePage(i);
        }
        this.catalog.remove(PdfName.ACROFORM);
        this.pageRefs.resetReleasePage();
    }

    public void removeAnnotations() {
        this.pageRefs.resetReleasePage();
        for (int i = 1; i <= this.pageRefs.size(); ++i) {
            PdfDictionary pdfDictionary = this.pageRefs.getPageN(i);
            if (pdfDictionary.get(PdfName.ANNOTS) == null) {
                this.pageRefs.releasePage(i);
                continue;
            }
            pdfDictionary.remove(PdfName.ANNOTS);
        }
        this.catalog.remove(PdfName.ACROFORM);
        this.pageRefs.resetReleasePage();
    }

    public ArrayList getLinks(int n) {
        this.pageRefs.resetReleasePage();
        ArrayList<PdfAnnotation.PdfImportedLink> arrayList = new ArrayList<PdfAnnotation.PdfImportedLink>();
        PdfDictionary pdfDictionary = this.pageRefs.getPageN(n);
        if (pdfDictionary.get(PdfName.ANNOTS) != null) {
            PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ANNOTS));
            ArrayList arrayList2 = pdfArray.getArrayList();
            for (int i = 0; i < arrayList2.size(); ++i) {
                PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)arrayList2.get(i));
                if (!PdfName.LINK.equals(pdfDictionary2.get(PdfName.SUBTYPE))) continue;
                arrayList.add(new PdfAnnotation.PdfImportedLink(pdfDictionary2));
            }
        }
        this.pageRefs.releasePage(n);
        this.pageRefs.resetReleasePage();
        return arrayList;
    }

    private void iterateBookmarks(PdfObject pdfObject, HashMap hashMap) {
        while (pdfObject != null) {
            this.replaceNamedDestination(pdfObject, hashMap);
            PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfObject);
            PdfObject pdfObject2 = pdfDictionary.get(PdfName.FIRST);
            if (pdfObject2 != null) {
                this.iterateBookmarks(pdfObject2, hashMap);
            }
            pdfObject = pdfDictionary.get(PdfName.NEXT);
        }
    }

    public void consolidateNamedDestinations() {
        if (this.consolidateNamedDestinations) {
            return;
        }
        this.consolidateNamedDestinations = true;
        HashMap hashMap = this.getNamedDestination();
        if (hashMap.isEmpty()) {
            return;
        }
        for (int i = 1; i <= this.pageRefs.size(); ++i) {
            PdfDictionary pdfDictionary = this.pageRefs.getPageN(i);
            PdfObject pdfObject = pdfDictionary.get(PdfName.ANNOTS);
            PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfObject);
            int n = this.lastXrefPartial;
            this.releaseLastXrefPartial();
            if (pdfArray == null) {
                this.pageRefs.releasePage(i);
                continue;
            }
            ArrayList arrayList = pdfArray.getArrayList();
            boolean bl = false;
            for (int j = 0; j < arrayList.size(); ++j) {
                PdfObject pdfObject2 = (PdfObject)arrayList.get(j);
                if (!this.replaceNamedDestination(pdfObject2, hashMap) || pdfObject2.isIndirect()) continue;
                bl = true;
            }
            if (bl) {
                this.setXrefPartialObject(n, pdfArray);
            }
            if (bl && !pdfObject.isIndirect()) continue;
            this.pageRefs.releasePage(i);
        }
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(this.catalog.get(PdfName.OUTLINES));
        if (pdfDictionary == null) {
            return;
        }
        this.iterateBookmarks(pdfDictionary.get(PdfName.FIRST), hashMap);
    }

    protected static PdfDictionary duplicatePdfDictionary(PdfDictionary pdfDictionary, PdfDictionary pdfDictionary2, PdfReader pdfReader) {
        if (pdfDictionary2 == null) {
            pdfDictionary2 = new PdfDictionary();
        }
        Iterator iterator = pdfDictionary.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            pdfDictionary2.put(pdfName, PdfReader.duplicatePdfObject(pdfDictionary.get(pdfName), pdfReader));
        }
        return pdfDictionary2;
    }

    protected static PdfObject duplicatePdfObject(PdfObject pdfObject, PdfReader pdfReader) {
        if (pdfObject == null) {
            return null;
        }
        switch (pdfObject.type()) {
            case 6: {
                return PdfReader.duplicatePdfDictionary((PdfDictionary)pdfObject, null, pdfReader);
            }
            case 7: {
                PRStream pRStream = (PRStream)pdfObject;
                PRStream pRStream2 = new PRStream(pRStream, null, pdfReader);
                PdfReader.duplicatePdfDictionary(pRStream, pRStream2, pdfReader);
                return pRStream2;
            }
            case 5: {
                ArrayList arrayList = ((PdfArray)pdfObject).getArrayList();
                PdfArray pdfArray = new PdfArray();
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext()) {
                    pdfArray.add(PdfReader.duplicatePdfObject((PdfObject)iterator.next(), pdfReader));
                }
                return pdfArray;
            }
            case 10: {
                PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfObject;
                return new PRIndirectReference(pdfReader, pRIndirectReference.getNumber(), pRIndirectReference.getGeneration());
            }
        }
        return pdfObject;
    }

    public void close() {
        if (!this.partial) {
            return;
        }
        try {
            this.tokens.close();
        }
        catch (IOException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    protected void removeUnusedNode(PdfObject var1_1, boolean[] var2_2) {
        var3_3 = new Stack<PdfObject>();
        var3_3.push(var1_1);
        block5 : while (var3_3.empty() == false) {
            var4_4 = var3_3.pop();
            if (var4_4 == null) continue;
            var5_5 = null;
            var6_6 = null;
            var7_7 = null;
            var8_8 = null;
            var9_9 = 0;
            if (!(var4_4 instanceof PdfObject)) ** GOTO lbl33
            var1_1 = (PdfObject)var4_4;
            switch (var1_1.type()) {
                case 6: 
                case 7: {
                    var6_6 = (PdfDictionary)var1_1;
                    var7_7 = new PdfName[var6_6.size()];
                    var6_6.getKeys().toArray(var7_7);
                    ** GOTO lbl41
                }
                case 5: {
                    var5_5 = ((PdfArray)var1_1).getArrayList();
                    ** GOTO lbl41
                }
                case 10: {
                    var10_10 = (PRIndirectReference)var1_1;
                    var11_13 = var10_10.getNumber();
                    if (var2_2[var11_13]) break;
                    var2_2[var11_13] = true;
                    var3_3.push(PdfReader.getPdfObjectRelease(var10_10));
                    ** break;
                }
                default: {
                    ** break;
lbl31: // 2 sources:
                    break;
                }
            }
            continue;
lbl33: // 1 sources:
            var8_8 = (Object[])var4_4;
            if (var8_8[0] instanceof ArrayList) {
                var5_5 = (ArrayList)var8_8[0];
                var9_9 = (Integer)var8_8[1];
            } else {
                var7_7 = (PdfName[])var8_8[0];
                var6_6 = (PdfDictionary)var8_8[1];
                var9_9 = (Integer)var8_8[2];
            }
lbl41: // 4 sources:
            if (var5_5 != null) {
                var10_11 = var9_9;
                do {
                    if (var10_11 >= var5_5.size()) continue block5;
                    var11_14 = (PdfObject)var5_5.get(var10_11);
                    if (!var11_14.isIndirect() || (var12_16 = ((PRIndirectReference)var11_14).getNumber()) < this.xrefObj.size() && (this.partial || this.xrefObj.get(var12_16) != null)) break;
                    var5_5.set(var10_11, PdfNull.PDFNULL);
                    ++var10_11;
                } while (true);
                if (var8_8 == null) {
                    var3_3.push((PdfObject)new Object[]{var5_5, new Integer(var10_11 + 1)});
                } else {
                    var8_8[1] = new Integer(var10_11 + 1);
                    var3_3.push((PdfObject)var8_8);
                }
                var3_3.push(var11_14);
                continue;
            }
            var10_12 = var9_9;
            do {
                if (var10_12 >= var7_7.length) continue block5;
                var11_15 = var7_7[var10_12];
                var12_17 = var6_6.get(var11_15);
                if (!var12_17.isIndirect() || (var13_18 = ((PRIndirectReference)var12_17).getNumber()) < this.xrefObj.size() && (this.partial || this.xrefObj.get(var13_18) != null)) break;
                var6_6.put(var11_15, PdfNull.PDFNULL);
                ++var10_12;
            } while (true);
            if (var8_8 == null) {
                var3_3.push((PdfObject)new Object[]{var7_7, var6_6, new Integer(var10_12 + 1)});
            } else {
                var8_8[2] = new Integer(var10_12 + 1);
                var3_3.push((PdfObject)var8_8);
            }
            var3_3.push(var12_17);
        }
    }

    public int removeUnusedObjects() {
        boolean[] arrbl = new boolean[this.xrefObj.size()];
        this.removeUnusedNode(this.trailer, arrbl);
        int n = 0;
        if (this.partial) {
            for (int i = 1; i < arrbl.length; ++i) {
                if (arrbl[i]) continue;
                this.xref[i * 2] = -1;
                this.xref[i * 2 + 1] = 0;
                this.xrefObj.set(i, null);
                ++n;
            }
        } else {
            for (int i = 1; i < arrbl.length; ++i) {
                if (arrbl[i]) continue;
                this.xrefObj.set(i, null);
                ++n;
            }
        }
        return n;
    }

    public AcroFields getAcroFields() {
        return new AcroFields(this, null);
    }

    public String getJavaScript(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(this.catalog.get(PdfName.NAMES));
        if (pdfDictionary == null) {
            return null;
        }
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.JAVASCRIPT));
        if (pdfDictionary2 == null) {
            return null;
        }
        HashMap hashMap = PdfNameTree.readTree(pdfDictionary2);
        Object[] arrobject = new String[hashMap.size()];
        arrobject = hashMap.keySet().toArray(arrobject);
        Arrays.sort(arrobject);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrobject.length; ++i) {
            PdfObject pdfObject;
            PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfIndirectReference)hashMap.get(arrobject[i]));
            if (pdfDictionary3 == null || (pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary3.get(PdfName.JS))) == null) continue;
            if (pdfObject.isString()) {
                stringBuffer.append(((PdfString)pdfObject).toUnicodeString()).append('\n');
                continue;
            }
            if (!pdfObject.isStream()) continue;
            byte[] arrby = PdfReader.getStreamBytes((PRStream)pdfObject, randomAccessFileOrArray);
            if (arrby.length >= 2 && arrby[0] == -2 && arrby[1] == -1) {
                stringBuffer.append(PdfEncodings.convertToString(arrby, "UnicodeBig"));
            } else {
                stringBuffer.append(PdfEncodings.convertToString(arrby, "PDF"));
            }
            stringBuffer.append('\n');
        }
        return stringBuffer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getJavaScript() throws IOException {
        RandomAccessFileOrArray randomAccessFileOrArray = this.getSafeFile();
        try {
            randomAccessFileOrArray.reOpen();
            String string = this.getJavaScript(randomAccessFileOrArray);
            return string;
        }
        finally {
            try {
                randomAccessFileOrArray.close();
            }
            catch (Exception var3_3) {}
        }
    }

    public void selectPages(String string) {
        this.selectPages(SequenceList.expand(string, this.getNumberOfPages()));
    }

    public void selectPages(List list) {
        this.pageRefs.selectPages(list);
        this.removeUnusedObjects();
    }

    public void setViewerPreferences(int n) {
        this.viewerPreferences.setViewerPreferences(n);
        this.setViewerPreferences(this.viewerPreferences);
    }

    public void addViewerPreference(PdfName pdfName, PdfObject pdfObject) {
        this.viewerPreferences.addViewerPreference(pdfName, pdfObject);
        this.setViewerPreferences(this.viewerPreferences);
    }

    void setViewerPreferences(PdfViewerPreferencesImp pdfViewerPreferencesImp) {
        pdfViewerPreferencesImp.addToCatalog(this.catalog);
    }

    public int getSimpleViewerPreferences() {
        return PdfViewerPreferencesImp.getViewerPreferences(this.catalog).getPageLayoutAndMode();
    }

    public boolean isAppendable() {
        return this.appendable;
    }

    public void setAppendable(boolean bl) {
        this.appendable = bl;
        if (bl) {
            PdfReader.getPdfObject(this.trailer.get(PdfName.ROOT));
        }
    }

    public boolean isNewXrefType() {
        return this.newXrefType;
    }

    public int getFileLength() {
        return this.fileLength;
    }

    public boolean isHybridXref() {
        return this.hybridXref;
    }

    PdfIndirectReference getCryptoRef() {
        if (this.cryptoRef == null) {
            return null;
        }
        return new PdfIndirectReference(0, this.cryptoRef.getNumber(), this.cryptoRef.getGeneration());
    }

    public void removeUsageRights() {
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.catalog.get(PdfName.PERMS));
        if (pdfDictionary == null) {
            return;
        }
        pdfDictionary.remove(PdfName.UR);
        pdfDictionary.remove(PdfName.UR3);
        if (pdfDictionary.size() == 0) {
            this.catalog.remove(PdfName.PERMS);
        }
    }

    public int getCertificationLevel() {
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.catalog.get(PdfName.PERMS));
        if (pdfDictionary == null) {
            return 0;
        }
        if ((pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.DOCMDP))) == null) {
            return 0;
        }
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.REFERENCE));
        if (pdfArray == null || pdfArray.size() == 0) {
            return 0;
        }
        pdfDictionary = (PdfDictionary)PdfReader.getPdfObject((PdfObject)pdfArray.getArrayList().get(0));
        if (pdfDictionary == null) {
            return 0;
        }
        if ((pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.TRANSFORMPARAMS))) == null) {
            return 0;
        }
        PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.P));
        if (pdfNumber == null) {
            return 0;
        }
        return pdfNumber.intValue();
    }

    public final boolean isOpenedWithFullPermissions() {
        return !this.encrypted || this.ownerPasswordUsed;
    }

    public int getCryptoMode() {
        if (this.decrypt == null) {
            return -1;
        }
        return this.decrypt.getCryptoMode();
    }

    public boolean isMetadataEncrypted() {
        if (this.decrypt == null) {
            return false;
        }
        return this.decrypt.isMetadataEncrypted();
    }

    public byte[] computeUserPassword() {
        if (!this.encrypted || !this.ownerPasswordUsed) {
            return null;
        }
        return this.decrypt.computeUserPassword(this.password);
    }

    static class PageRefs {
        private PdfReader reader;
        private IntHashtable refsp;
        private ArrayList refsn;
        private ArrayList pageInh;
        private int lastPageRead = -1;
        private int sizep;
        private boolean keepPages;

        private PageRefs(PdfReader pdfReader) throws IOException {
            this.reader = pdfReader;
            if (pdfReader.partial) {
                this.refsp = new IntHashtable();
                PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObjectRelease(pdfReader.rootPages.get(PdfName.COUNT));
                this.sizep = pdfNumber.intValue();
            } else {
                this.readPages();
            }
        }

        PageRefs(PageRefs pageRefs, PdfReader pdfReader) {
            this.reader = pdfReader;
            this.sizep = pageRefs.sizep;
            if (pageRefs.refsn != null) {
                this.refsn = new ArrayList(pageRefs.refsn);
                for (int i = 0; i < this.refsn.size(); ++i) {
                    this.refsn.set(i, PdfReader.duplicatePdfObject((PdfObject)this.refsn.get(i), pdfReader));
                }
            } else {
                this.refsp = (IntHashtable)pageRefs.refsp.clone();
            }
        }

        int size() {
            if (this.refsn != null) {
                return this.refsn.size();
            }
            return this.sizep;
        }

        void readPages() throws IOException {
            if (this.refsn != null) {
                return;
            }
            this.refsp = null;
            this.refsn = new ArrayList();
            this.pageInh = new ArrayList();
            this.iteratePages((PRIndirectReference)this.reader.catalog.get(PdfName.PAGES));
            this.pageInh = null;
            this.reader.rootPages.put(PdfName.COUNT, new PdfNumber(this.refsn.size()));
        }

        void reReadPages() throws IOException {
            this.refsn = null;
            this.readPages();
        }

        public PdfDictionary getPageN(int n) {
            PRIndirectReference pRIndirectReference = this.getPageOrigRef(n);
            return (PdfDictionary)PdfReader.getPdfObject(pRIndirectReference);
        }

        public PdfDictionary getPageNRelease(int n) {
            PdfDictionary pdfDictionary = this.getPageN(n);
            this.releasePage(n);
            return pdfDictionary;
        }

        public PRIndirectReference getPageOrigRefRelease(int n) {
            PRIndirectReference pRIndirectReference = this.getPageOrigRef(n);
            this.releasePage(n);
            return pRIndirectReference;
        }

        public PRIndirectReference getPageOrigRef(int n) {
            try {
                if (--n < 0 || n >= this.size()) {
                    return null;
                }
                if (this.refsn != null) {
                    return (PRIndirectReference)this.refsn.get(n);
                }
                int n2 = this.refsp.get(n);
                if (n2 == 0) {
                    PRIndirectReference pRIndirectReference = this.getSinglePage(n);
                    this.lastPageRead = this.reader.lastXrefPartial == -1 ? -1 : n;
                    this.reader.lastXrefPartial = -1;
                    this.refsp.put(n, pRIndirectReference.getNumber());
                    if (this.keepPages) {
                        this.lastPageRead = -1;
                    }
                    return pRIndirectReference;
                }
                if (this.lastPageRead != n) {
                    this.lastPageRead = -1;
                }
                if (this.keepPages) {
                    this.lastPageRead = -1;
                }
                return new PRIndirectReference(this.reader, n2);
            }
            catch (Exception var2_3) {
                throw new ExceptionConverter(var2_3);
            }
        }

        void keepPages() {
            if (this.refsp == null || this.keepPages) {
                return;
            }
            this.keepPages = true;
            this.refsp.clear();
        }

        public void releasePage(int n) {
            if (this.refsp == null) {
                return;
            }
            if (--n < 0 || n >= this.size()) {
                return;
            }
            if (n != this.lastPageRead) {
                return;
            }
            this.lastPageRead = -1;
            this.reader.lastXrefPartial = this.refsp.get(n);
            this.reader.releaseLastXrefPartial();
            this.refsp.remove(n);
        }

        public void resetReleasePage() {
            if (this.refsp == null) {
                return;
            }
            this.lastPageRead = -1;
        }

        void insertPage(int n, PRIndirectReference pRIndirectReference) {
            --n;
            if (this.refsn != null) {
                if (n >= this.refsn.size()) {
                    this.refsn.add(pRIndirectReference);
                } else {
                    this.refsn.add(n, pRIndirectReference);
                }
            } else {
                ++this.sizep;
                this.lastPageRead = -1;
                if (n >= this.size()) {
                    this.refsp.put(this.size(), pRIndirectReference.getNumber());
                } else {
                    IntHashtable intHashtable = new IntHashtable((this.refsp.size() + 1) * 2);
                    Iterator iterator = this.refsp.getEntryIterator();
                    while (iterator.hasNext()) {
                        IntHashtable.Entry entry = (IntHashtable.Entry)iterator.next();
                        int n2 = entry.getKey();
                        intHashtable.put(n2 >= n ? n2 + 1 : n2, entry.getValue());
                    }
                    intHashtable.put(n, pRIndirectReference.getNumber());
                    this.refsp = intHashtable;
                }
            }
        }

        private void pushPageAttributes(PdfDictionary pdfDictionary) {
            PdfDictionary pdfDictionary2 = new PdfDictionary();
            if (!this.pageInh.isEmpty()) {
                pdfDictionary2.putAll((PdfDictionary)this.pageInh.get(this.pageInh.size() - 1));
            }
            for (int i = 0; i < PdfReader.pageInhCandidates.length; ++i) {
                PdfObject pdfObject = pdfDictionary.get(PdfReader.pageInhCandidates[i]);
                if (pdfObject == null) continue;
                pdfDictionary2.put(PdfReader.pageInhCandidates[i], pdfObject);
            }
            this.pageInh.add(pdfDictionary2);
        }

        private void popPageAttributes() {
            this.pageInh.remove(this.pageInh.size() - 1);
        }

        private void iteratePages(PRIndirectReference pRIndirectReference) throws IOException {
            PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pRIndirectReference);
            PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.KIDS));
            if (pdfArray == null) {
                pdfDictionary.put(PdfName.TYPE, PdfName.PAGE);
                PdfDictionary pdfDictionary2 = (PdfDictionary)this.pageInh.get(this.pageInh.size() - 1);
                Object object = pdfDictionary2.getKeys().iterator();
                while (object.hasNext()) {
                    PdfName pdfName = (PdfName)object.next();
                    if (pdfDictionary.get(pdfName) != null) continue;
                    pdfDictionary.put(pdfName, pdfDictionary2.get(pdfName));
                }
                if (pdfDictionary.get(PdfName.MEDIABOX) == null) {
                    object = new PdfArray(new float[]{0.0f, 0.0f, PageSize.LETTER.getRight(), PageSize.LETTER.getTop()});
                    pdfDictionary.put(PdfName.MEDIABOX, (PdfObject)object);
                }
                this.refsn.add(pRIndirectReference);
            } else {
                pdfDictionary.put(PdfName.TYPE, PdfName.PAGES);
                this.pushPageAttributes(pdfDictionary);
                ArrayList arrayList = pdfArray.getArrayList();
                for (int i = 0; i < arrayList.size(); ++i) {
                    PdfObject pdfObject = (PdfObject)arrayList.get(i);
                    if (!pdfObject.isIndirect()) {
                        while (i < arrayList.size()) {
                            arrayList.remove(i);
                        }
                        break;
                    }
                    this.iteratePages((PRIndirectReference)pdfObject);
                }
                this.popPageAttributes();
            }
        }

        /*
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Lifted jumps to return sites
         */
        protected PRIndirectReference getSinglePage(int var1_1) {
            var2_2 = new PdfDictionary();
            var3_3 = this.reader.rootPages;
            var4_4 = 0;
            do lbl-1000: // 5 sources:
            {
                for (var5_6 = 0; var5_6 < PdfReader.pageInhCandidates.length; ++var5_6) {
                    var6_7 = var3_3.get(PdfReader.pageInhCandidates[var5_6]);
                    if (var6_7 == null) continue;
                    var2_2.put(PdfReader.pageInhCandidates[var5_6], (PdfObject)var6_7);
                }
                var5_5 = (PdfArray)PdfReader.getPdfObjectRelease(var3_3.get(PdfName.KIDS));
                var6_7 = var5_5.listIterator();
                do {
                    if (!var6_7.hasNext()) ** continue;
                    var7_8 = (PRIndirectReference)var6_7.next();
                    var8_9 = (PdfDictionary)PdfReader.getPdfObject(var7_8);
                    var9_10 = PdfReader.access$300(this.reader);
                    var10_11 = PdfReader.getPdfObjectRelease(var8_9.get(PdfName.COUNT));
                    PdfReader.access$302(this.reader, var9_10);
                    var11_12 = 1;
                    if (var10_11 != null && var10_11.type() == 2) {
                        var11_12 = ((PdfNumber)var10_11).intValue();
                    }
                    if (var1_1 < var4_4 + var11_12) {
                        if (var10_11 == null) {
                            var8_9.mergeDifferent(var2_2);
                            return var7_8;
                        }
                        this.reader.releaseLastXrefPartial();
                        var3_3 = var8_9;
                        ** continue;
                    }
                    this.reader.releaseLastXrefPartial();
                    var4_4 += var11_12;
                } while (true);
                break;
            } while (true);
        }

        private void selectPages(List list) {
            int n;
            Object object;
            IntHashtable intHashtable = new IntHashtable();
            ArrayList<Object> arrayList = new ArrayList<Object>();
            int n2 = this.size();
            Object object2 = list.iterator();
            while (object2.hasNext()) {
                object = (Integer)object2.next();
                int n3 = object.intValue();
                if (n3 < 1 || n3 > n2 || intHashtable.put(n3, 1) != 0) continue;
                arrayList.add(object);
            }
            if (this.reader.partial) {
                for (int i = 1; i <= n2; ++i) {
                    this.getPageOrigRef(i);
                    this.resetReleasePage();
                }
            }
            object2 = (PRIndirectReference)this.reader.catalog.get(PdfName.PAGES);
            object = (PdfDictionary)PdfReader.getPdfObject((PdfObject)object2);
            ArrayList<PRIndirectReference> arrayList2 = new ArrayList<PRIndirectReference>(arrayList.size());
            PdfArray pdfArray = new PdfArray();
            for (int i = 0; i < arrayList.size(); ++i) {
                n = (Integer)arrayList.get(i);
                PRIndirectReference pRIndirectReference = this.getPageOrigRef(n);
                this.resetReleasePage();
                pdfArray.add(pRIndirectReference);
                arrayList2.add(pRIndirectReference);
                this.getPageN(n).put(PdfName.PARENT, (PdfObject)object2);
            }
            AcroFields acroFields = this.reader.getAcroFields();
            n = acroFields.getFields().size() > 0 ? 1 : 0;
            for (int j = 1; j <= n2; ++j) {
                if (intHashtable.containsKey(j)) continue;
                if (n != 0) {
                    acroFields.removeFieldsFromPage(j);
                }
                PRIndirectReference pRIndirectReference = this.getPageOrigRef(j);
                int n4 = pRIndirectReference.getNumber();
                this.reader.xrefObj.set(n4, null);
                if (!this.reader.partial) continue;
                this.reader.xref[n4 * 2] = -1;
                this.reader.xref[n4 * 2 + 1] = 0;
            }
            object.put(PdfName.COUNT, new PdfNumber(arrayList.size()));
            object.put(PdfName.KIDS, pdfArray);
            this.refsp = null;
            this.refsn = arrayList2;
        }
    }

}

