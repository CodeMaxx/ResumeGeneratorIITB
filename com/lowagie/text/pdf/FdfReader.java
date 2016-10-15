/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class FdfReader
extends PdfReader {
    HashMap fields;
    String fileSpec;
    PdfName encoding;

    public FdfReader(String string) throws IOException {
        super(string);
    }

    public FdfReader(byte[] arrby) throws IOException {
        super(arrby);
    }

    public FdfReader(URL uRL) throws IOException {
        super(uRL);
    }

    public FdfReader(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void readPdf() throws IOException {
        this.fields = new HashMap();
        try {
            this.tokens.checkFdfHeader();
            this.rebuildXref();
            this.readDocObj();
        }
        finally {
            try {
                this.tokens.close();
            }
            catch (Exception var1_1) {}
        }
        this.readFields();
    }

    protected void kidNode(PdfDictionary pdfDictionary, String string) {
        PdfArray pdfArray = (PdfArray)FdfReader.getPdfObject(pdfDictionary.get(PdfName.KIDS));
        if (pdfArray == null || pdfArray.getArrayList().size() == 0) {
            if (string.length() > 0) {
                string = string.substring(1);
            }
            this.fields.put(string, pdfDictionary);
        } else {
            pdfDictionary.remove(PdfName.KIDS);
            ArrayList arrayList = pdfArray.getArrayList();
            for (int i = 0; i < arrayList.size(); ++i) {
                PdfDictionary pdfDictionary2 = new PdfDictionary();
                pdfDictionary2.merge(pdfDictionary);
                PdfDictionary pdfDictionary3 = (PdfDictionary)FdfReader.getPdfObject((PdfObject)arrayList.get(i));
                PdfString pdfString = (PdfString)FdfReader.getPdfObject(pdfDictionary3.get(PdfName.T));
                String string2 = string;
                if (pdfString != null) {
                    string2 = string2 + "." + pdfString.toUnicodeString();
                }
                pdfDictionary2.merge(pdfDictionary3);
                pdfDictionary2.remove(PdfName.T);
                this.kidNode(pdfDictionary2, string2);
            }
        }
    }

    protected void readFields() {
        PdfArray pdfArray;
        this.catalog = (PdfDictionary)FdfReader.getPdfObject(this.trailer.get(PdfName.ROOT));
        PdfDictionary pdfDictionary = (PdfDictionary)FdfReader.getPdfObject(this.catalog.get(PdfName.FDF));
        if (pdfDictionary == null) {
            return;
        }
        PdfString pdfString = (PdfString)FdfReader.getPdfObject(pdfDictionary.get(PdfName.F));
        if (pdfString != null) {
            this.fileSpec = pdfString.toUnicodeString();
        }
        if ((pdfArray = (PdfArray)FdfReader.getPdfObject(pdfDictionary.get(PdfName.FIELDS))) == null) {
            return;
        }
        this.encoding = (PdfName)FdfReader.getPdfObject(pdfDictionary.get(PdfName.ENCODING));
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.KIDS, pdfArray);
        this.kidNode(pdfDictionary2, "");
    }

    public HashMap getFields() {
        return this.fields;
    }

    public PdfDictionary getField(String string) {
        return (PdfDictionary)this.fields.get(string);
    }

    public String getFieldValue(String string) {
        PdfDictionary pdfDictionary = (PdfDictionary)this.fields.get(string);
        if (pdfDictionary == null) {
            return null;
        }
        PdfObject pdfObject = FdfReader.getPdfObject(pdfDictionary.get(PdfName.V));
        if (pdfObject == null) {
            return null;
        }
        if (pdfObject.isName()) {
            return PdfName.decodeName(((PdfName)pdfObject).toString());
        }
        if (pdfObject.isString()) {
            PdfString pdfString = (PdfString)pdfObject;
            if (this.encoding == null || pdfString.getEncoding() != null) {
                return pdfString.toUnicodeString();
            }
            byte[] arrby = pdfString.getBytes();
            if (arrby.length >= 2 && arrby[0] == -2 && arrby[1] == -1) {
                return pdfString.toUnicodeString();
            }
            try {
                if (this.encoding.equals(PdfName.SHIFT_JIS)) {
                    return new String(arrby, "SJIS");
                }
                if (this.encoding.equals(PdfName.UHC)) {
                    return new String(arrby, "MS949");
                }
                if (this.encoding.equals(PdfName.GBK)) {
                    return new String(arrby, "GBK");
                }
                if (this.encoding.equals(PdfName.BIGFIVE)) {
                    return new String(arrby, "Big5");
                }
            }
            catch (Exception var6_6) {
                // empty catch block
            }
            return pdfString.toUnicodeString();
        }
        return null;
    }

    public String getFileSpec() {
        return this.fileSpec;
    }
}

