/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.FdfReader;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class FdfWriter {
    private static final byte[] HEADER_FDF = DocWriter.getISOBytes("%FDF-1.2\n%\u00e2\u00e3\u00cf\u00d3\n");
    HashMap fields = new HashMap();
    private String file;

    public void writeTo(OutputStream outputStream) throws IOException {
        Wrt wrt = new Wrt(outputStream, this);
        wrt.writeTo();
    }

    boolean setField(String string, PdfObject pdfObject) {
        Object object;
        String string2;
        HashMap hashMap;
        block4 : {
            hashMap = this.fields;
            StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
            if (!stringTokenizer.hasMoreTokens()) {
                return false;
            }
            do {
                string2 = stringTokenizer.nextToken();
                object = hashMap.get(string2);
                if (!stringTokenizer.hasMoreTokens()) break block4;
                if (object == null) {
                    object = new HashMap();
                    hashMap.put(string2, object);
                    hashMap = (HashMap)object;
                    continue;
                }
                if (!(object instanceof HashMap)) break;
                hashMap = (HashMap)object;
            } while (true);
            return false;
        }
        if (!(object instanceof HashMap)) {
            hashMap.put(string2, pdfObject);
            return true;
        }
        return false;
    }

    void iterateFields(HashMap hashMap, HashMap hashMap2, String string) {
        Iterator iterator = hashMap2.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String string2 = (String)entry.getKey();
            Object v = entry.getValue();
            if (v instanceof HashMap) {
                this.iterateFields(hashMap, (HashMap)v, string + "." + string2);
                continue;
            }
            hashMap.put((string + "." + string2).substring(1), v);
        }
    }

    public boolean removeField(String string) {
        ArrayList<Object> arrayList;
        HashMap hashMap;
        Object object;
        block5 : {
            hashMap = this.fields;
            StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
            if (!stringTokenizer.hasMoreTokens()) {
                return false;
            }
            arrayList = new ArrayList<Object>();
            do {
                String string2;
                if ((object = hashMap.get(string2 = stringTokenizer.nextToken())) == null) {
                    return false;
                }
                arrayList.add(hashMap);
                arrayList.add(string2);
                if (!stringTokenizer.hasMoreTokens()) break block5;
                if (!(object instanceof HashMap)) break;
                hashMap = (HashMap)object;
            } while (true);
            return false;
        }
        if (object instanceof HashMap) {
            return false;
        }
        for (int i = arrayList.size() - 2; i >= 0; i -= 2) {
            hashMap = (HashMap)arrayList.get(i);
            object = (String)arrayList.get(i + 1);
            hashMap.remove(object);
            if (!hashMap.isEmpty()) break;
        }
        return true;
    }

    public HashMap getFields() {
        HashMap hashMap = new HashMap();
        this.iterateFields(hashMap, this.fields, "");
        return hashMap;
    }

    public String getField(String string) {
        Object v;
        block5 : {
            HashMap hashMap = this.fields;
            StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
            if (!stringTokenizer.hasMoreTokens()) {
                return null;
            }
            do {
                String string2;
                if ((v = hashMap.get(string2 = stringTokenizer.nextToken())) == null) {
                    return null;
                }
                if (!stringTokenizer.hasMoreTokens()) break block5;
                if (!(v instanceof HashMap)) break;
                hashMap = (HashMap)v;
            } while (true);
            return null;
        }
        if (v instanceof HashMap) {
            return null;
        }
        if (((PdfObject)v).isString()) {
            return ((PdfString)v).toUnicodeString();
        }
        return PdfName.decodeName(v.toString());
    }

    public boolean setFieldAsName(String string, String string2) {
        return this.setField(string, new PdfName(string2));
    }

    public boolean setFieldAsString(String string, String string2) {
        return this.setField(string, new PdfString(string2, "UnicodeBig"));
    }

    public void setFields(FdfReader fdfReader) {
        HashMap hashMap = fdfReader.getFields();
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String string = (String)entry.getKey();
            PdfDictionary pdfDictionary = (PdfDictionary)entry.getValue();
            PdfObject pdfObject = pdfDictionary.get(PdfName.V);
            if (pdfObject == null) continue;
            this.setField(string, pdfObject);
        }
    }

    public void setFields(PdfReader pdfReader) {
        this.setFields(pdfReader.getAcroFields());
    }

    public void setFields(AcroFields acroFields) {
        Iterator iterator = acroFields.getFields().entrySet().iterator();
        while (iterator.hasNext()) {
            PdfObject pdfObject;
            Map.Entry entry = iterator.next();
            String string = (String)entry.getKey();
            AcroFields.Item item = (AcroFields.Item)entry.getValue();
            PdfDictionary pdfDictionary = (PdfDictionary)item.merged.get(0);
            PdfObject pdfObject2 = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.V));
            if (pdfObject2 == null || (pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.FT))) == null || PdfName.SIG.equals(pdfObject)) continue;
            this.setField(string, pdfObject2);
        }
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String string) {
        this.file = string;
    }

    static class Wrt
    extends PdfWriter {
        private FdfWriter fdf;

        Wrt(OutputStream outputStream, FdfWriter fdfWriter) throws IOException {
            super(new PdfDocument(), outputStream);
            this.fdf = fdfWriter;
            this.os.write(HEADER_FDF);
            this.body = new PdfWriter.PdfBody(this);
        }

        void writeTo() throws IOException {
            PdfDictionary pdfDictionary = new PdfDictionary();
            pdfDictionary.put(PdfName.FIELDS, this.calculate(this.fdf.fields));
            if (this.fdf.file != null) {
                pdfDictionary.put(PdfName.F, new PdfString(this.fdf.file, "UnicodeBig"));
            }
            PdfDictionary pdfDictionary2 = new PdfDictionary();
            pdfDictionary2.put(PdfName.FDF, pdfDictionary);
            PdfIndirectReference pdfIndirectReference = this.addToBody(pdfDictionary2).getIndirectReference();
            this.os.write(Wrt.getISOBytes("trailer\n"));
            PdfDictionary pdfDictionary3 = new PdfDictionary();
            pdfDictionary3.put(PdfName.ROOT, pdfIndirectReference);
            pdfDictionary3.toPdf(null, this.os);
            this.os.write(Wrt.getISOBytes("\n%%EOF\n"));
            this.os.close();
        }

        PdfArray calculate(HashMap hashMap) throws IOException {
            PdfArray pdfArray = new PdfArray();
            Iterator iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                String string = (String)entry.getKey();
                Object v = entry.getValue();
                PdfDictionary pdfDictionary = new PdfDictionary();
                pdfDictionary.put(PdfName.T, new PdfString(string, "UnicodeBig"));
                if (v instanceof HashMap) {
                    pdfDictionary.put(PdfName.KIDS, this.calculate((HashMap)v));
                } else {
                    pdfDictionary.put(PdfName.V, (PdfObject)v);
                }
                pdfArray.add(pdfDictionary);
            }
            return pdfArray;
        }
    }

}

