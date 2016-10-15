/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PdfSmartCopy
extends PdfCopy {
    private HashMap streamMap = new HashMap();

    public PdfSmartCopy(Document document, OutputStream outputStream) throws DocumentException {
        super(document, outputStream);
    }

    protected PdfIndirectReference copyIndirect(PRIndirectReference pRIndirectReference) throws IOException, BadPdfFormatException {
        PdfCopy.IndirectReferences indirectReferences;
        PdfCopy.RefKey refKey;
        PdfObject pdfObject;
        PdfIndirectReference pdfIndirectReference;
        PdfObject pdfObject2 = PdfReader.getPdfObjectRelease(pRIndirectReference);
        ByteStore byteStore = null;
        boolean bl = false;
        if (pdfObject2.isStream()) {
            byteStore = new ByteStore((PRStream)pdfObject2);
            bl = true;
            pdfIndirectReference = (PdfIndirectReference)this.streamMap.get(byteStore);
            if (pdfIndirectReference != null) {
                return pdfIndirectReference;
            }
        }
        if ((indirectReferences = (PdfCopy.IndirectReferences)this.indirects.get(refKey = new PdfCopy.RefKey(pRIndirectReference))) != null) {
            pdfIndirectReference = indirectReferences.getRef();
            if (indirectReferences.getCopied()) {
                return pdfIndirectReference;
            }
        } else {
            pdfIndirectReference = this.body.getPdfIndirectReference();
            indirectReferences = new PdfCopy.IndirectReferences(pdfIndirectReference);
            this.indirects.put(refKey, indirectReferences);
        }
        if (pdfObject2.isDictionary() && (pdfObject = PdfReader.getPdfObjectRelease(((PdfDictionary)pdfObject2).get(PdfName.TYPE))) != null && PdfName.PAGE.equals(pdfObject)) {
            return pdfIndirectReference;
        }
        indirectReferences.setCopied();
        if (bl) {
            this.streamMap.put(byteStore, pdfIndirectReference);
        }
        pdfObject = this.copyObject(pdfObject2);
        this.addToBody(pdfObject, pdfIndirectReference);
        return pdfIndirectReference;
    }

    static class ByteStore {
        private byte[] b;
        private int hash;
        private MessageDigest md5;

        private void serObject(PdfObject pdfObject, int n, ByteBuffer byteBuffer) throws IOException {
            if (n <= 0) {
                return;
            }
            if (pdfObject == null) {
                byteBuffer.append("$Lnull");
                return;
            }
            if ((pdfObject = PdfReader.getPdfObject(pdfObject)).isStream()) {
                byteBuffer.append("$B");
                this.serDic((PdfDictionary)pdfObject, n - 1, byteBuffer);
                if (n > 0) {
                    this.md5.reset();
                    byteBuffer.append(this.md5.digest(PdfReader.getStreamBytesRaw((PRStream)pdfObject)));
                }
            } else if (pdfObject.isDictionary()) {
                this.serDic((PdfDictionary)pdfObject, n - 1, byteBuffer);
            } else if (pdfObject.isArray()) {
                this.serArray((PdfArray)pdfObject, n - 1, byteBuffer);
            } else if (pdfObject.isString()) {
                byteBuffer.append("$S").append(pdfObject.toString());
            } else if (pdfObject.isName()) {
                byteBuffer.append("$N").append(pdfObject.toString());
            } else {
                byteBuffer.append("$L").append(pdfObject.toString());
            }
        }

        private void serDic(PdfDictionary pdfDictionary, int n, ByteBuffer byteBuffer) throws IOException {
            byteBuffer.append("$D");
            if (n <= 0) {
                return;
            }
            Object[] arrobject = pdfDictionary.getKeys().toArray();
            Arrays.sort(arrobject);
            for (int i = 0; i < arrobject.length; ++i) {
                this.serObject((PdfObject)arrobject[i], n, byteBuffer);
                this.serObject(pdfDictionary.get((PdfName)arrobject[i]), n, byteBuffer);
            }
        }

        private void serArray(PdfArray pdfArray, int n, ByteBuffer byteBuffer) throws IOException {
            byteBuffer.append("$A");
            if (n <= 0) {
                return;
            }
            ArrayList arrayList = pdfArray.getArrayList();
            for (int i = 0; i < arrayList.size(); ++i) {
                this.serObject((PdfObject)arrayList.get(i), n, byteBuffer);
            }
        }

        ByteStore(PRStream pRStream) throws IOException {
            try {
                this.md5 = MessageDigest.getInstance("MD5");
            }
            catch (Exception var2_2) {
                throw new ExceptionConverter(var2_2);
            }
            ByteBuffer byteBuffer = new ByteBuffer();
            int n = 10;
            this.serObject(pRStream, n, byteBuffer);
            this.b = byteBuffer.toByteArray();
            this.md5 = null;
        }

        public boolean equals(Object object) {
            if (!(object instanceof ByteStore)) {
                return false;
            }
            if (this.hashCode() != object.hashCode()) {
                return false;
            }
            return Arrays.equals(this.b, ((ByteStore)object).b);
        }

        public int hashCode() {
            if (this.hash == 0) {
                int n = this.b.length;
                for (int i = 0; i < n; ++i) {
                    this.hash = this.hash * 31 + (this.b[i] & 255);
                }
            }
            return this.hash;
        }
    }

}

