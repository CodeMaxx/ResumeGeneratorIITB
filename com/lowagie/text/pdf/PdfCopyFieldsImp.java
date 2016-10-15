/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocListener;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPages;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

class PdfCopyFieldsImp
extends PdfWriter {
    private static final PdfName iTextTag = new PdfName("_iTextTag_");
    private static final Integer zero = new Integer(0);
    ArrayList readers = new ArrayList();
    HashMap readers2intrefs = new HashMap();
    HashMap pages2intrefs = new HashMap();
    HashMap visited = new HashMap();
    ArrayList fields = new ArrayList();
    RandomAccessFileOrArray file;
    HashMap fieldTree = new HashMap();
    ArrayList pageRefs = new ArrayList();
    ArrayList pageDics = new ArrayList();
    PdfDictionary resources = new PdfDictionary();
    PdfDictionary form;
    boolean closing = false;
    Document nd;
    private HashMap tabOrder;
    private ArrayList calculationOrder = new ArrayList();
    private ArrayList calculationOrderRefs;
    protected static final HashMap widgetKeys = new HashMap();
    protected static final HashMap fieldKeys = new HashMap();

    PdfCopyFieldsImp(OutputStream outputStream) throws DocumentException {
        this(outputStream, '\u0000');
    }

    PdfCopyFieldsImp(OutputStream outputStream, char c) throws DocumentException {
        super(new PdfDocument(), outputStream);
        this.pdf.addWriter(this);
        if (c != '\u0000') {
            super.setPdfVersion(c);
        }
        this.nd = new Document();
        this.nd.addDocListener(this.pdf);
    }

    void addDocument(PdfReader pdfReader, List list) throws DocumentException {
        if (!this.readers2intrefs.containsKey(pdfReader) && pdfReader.isTampered()) {
            throw new DocumentException("The document was reused.");
        }
        pdfReader = new PdfReader(pdfReader);
        pdfReader.selectPages(list);
        if (pdfReader.getNumberOfPages() == 0) {
            return;
        }
        pdfReader.setTampered(false);
        this.addDocument(pdfReader);
    }

    void addDocument(PdfReader pdfReader) throws DocumentException {
        if (!pdfReader.isOpenedWithFullPermissions()) {
            throw new IllegalArgumentException("PdfReader not opened with owner password");
        }
        this.openDoc();
        if (this.readers2intrefs.containsKey(pdfReader)) {
            pdfReader = new PdfReader(pdfReader);
        } else {
            if (pdfReader.isTampered()) {
                throw new DocumentException("The document was reused.");
            }
            pdfReader.consolidateNamedDestinations();
            pdfReader.setTampered(true);
        }
        pdfReader.shuffleSubsetNames();
        this.readers2intrefs.put(pdfReader, new IntHashtable());
        this.readers.add(pdfReader);
        int n = pdfReader.getNumberOfPages();
        IntHashtable intHashtable = new IntHashtable();
        for (int i = 1; i <= n; ++i) {
            intHashtable.put(pdfReader.getPageOrigRef(i).getNumber(), 1);
            pdfReader.releasePage(i);
        }
        this.pages2intrefs.put(pdfReader, intHashtable);
        this.visited.put(pdfReader, new IntHashtable());
        this.fields.add(pdfReader.getAcroFields());
        this.updateCalculationOrder(pdfReader);
    }

    private static String getCOName(PdfReader pdfReader, PRIndirectReference pRIndirectReference) {
        PdfObject pdfObject;
        String string = "";
        while (pRIndirectReference != null && (pdfObject = PdfReader.getPdfObject(pRIndirectReference)) != null && pdfObject.type() == 6) {
            PdfDictionary pdfDictionary = (PdfDictionary)pdfObject;
            PdfString pdfString = (PdfString)PdfReader.getPdfObject(pdfDictionary.get(PdfName.T));
            if (pdfString != null) {
                string = pdfString.toUnicodeString() + "." + string;
            }
            pRIndirectReference = (PRIndirectReference)pdfDictionary.get(PdfName.PARENT);
        }
        if (string.endsWith(".")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    private void updateCalculationOrder(PdfReader pdfReader) {
        PdfDictionary pdfDictionary = pdfReader.getCatalog();
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ACROFORM));
        if (pdfDictionary2 == null) {
            return;
        }
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.CO));
        if (pdfArray == null || pdfArray.size() == 0) {
            return;
        }
        AcroFields acroFields = pdfReader.getAcroFields();
        ArrayList arrayList = pdfArray.getArrayList();
        for (int i = 0; i < arrayList.size(); ++i) {
            String string;
            PdfObject pdfObject = (PdfObject)arrayList.get(i);
            if (pdfObject == null || !pdfObject.isIndirect() || acroFields.getFieldItem(string = PdfCopyFieldsImp.getCOName(pdfReader, (PRIndirectReference)pdfObject)) == null || this.calculationOrder.contains(string = "." + string)) continue;
            this.calculationOrder.add(string);
        }
    }

    void propagate(PdfObject pdfObject, PdfIndirectReference pdfIndirectReference, boolean bl) throws IOException {
        if (pdfObject == null) {
            return;
        }
        if (pdfObject instanceof PdfIndirectReference) {
            return;
        }
        switch (pdfObject.type()) {
            case 6: 
            case 7: {
                PdfDictionary pdfDictionary = (PdfDictionary)pdfObject;
                Iterator iterator = pdfDictionary.getKeys().iterator();
                while (iterator.hasNext()) {
                    PdfName pdfName = (PdfName)iterator.next();
                    if (bl && (pdfName.equals(PdfName.PARENT) || pdfName.equals(PdfName.KIDS))) continue;
                    PdfObject pdfObject2 = pdfDictionary.get(pdfName);
                    if (pdfObject2 != null && pdfObject2.isIndirect()) {
                        PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfObject2;
                        if (this.setVisited(pRIndirectReference) || this.isPage(pRIndirectReference)) continue;
                        PdfIndirectReference pdfIndirectReference2 = this.getNewReference(pRIndirectReference);
                        this.propagate(PdfReader.getPdfObjectRelease(pRIndirectReference), pdfIndirectReference2, bl);
                        continue;
                    }
                    this.propagate(pdfObject2, null, bl);
                }
                break;
            }
            case 5: {
                ArrayList arrayList = ((PdfArray)pdfObject).getArrayList();
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext()) {
                    PdfObject pdfObject3 = (PdfObject)iterator.next();
                    if (pdfObject3 != null && pdfObject3.isIndirect()) {
                        PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfObject3;
                        if (this.isVisited(pRIndirectReference) || this.isPage(pRIndirectReference)) continue;
                        PdfIndirectReference pdfIndirectReference3 = this.getNewReference(pRIndirectReference);
                        this.propagate(PdfReader.getPdfObjectRelease(pRIndirectReference), pdfIndirectReference3, bl);
                        continue;
                    }
                    this.propagate(pdfObject3, null, bl);
                }
                break;
            }
            case 10: {
                throw new RuntimeException("Reference pointing to reference.");
            }
        }
    }

    private void adjustTabOrder(PdfArray pdfArray, PdfIndirectReference pdfIndirectReference, PdfNumber pdfNumber) {
        int n = pdfNumber.intValue();
        ArrayList<Integer> arrayList = (ArrayList<Integer>)this.tabOrder.get(pdfArray);
        if (arrayList == null) {
            arrayList = new ArrayList<Integer>();
            int n2 = pdfArray.size() - 1;
            for (int i = 0; i < n2; ++i) {
                arrayList.add(zero);
            }
            arrayList.add(new Integer(n));
            this.tabOrder.put(pdfArray, arrayList);
            pdfArray.add(pdfIndirectReference);
        } else {
            int n3;
            for (int i = n3 = arrayList.size() - 1; i >= 0; --i) {
                if ((Integer)arrayList.get(i) > n) continue;
                arrayList.add(i + 1, new Integer(n));
                pdfArray.getArrayList().add(i + 1, pdfIndirectReference);
                n3 = -2;
                break;
            }
            if (n3 != -2) {
                arrayList.add(0, new Integer(n));
                pdfArray.getArrayList().add(0, pdfIndirectReference);
            }
        }
    }

    protected PdfArray branchForm(HashMap hashMap, PdfIndirectReference pdfIndirectReference, String string) throws IOException {
        PdfArray pdfArray = new PdfArray();
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            PdfDictionary pdfDictionary;
            Map.Entry entry = iterator.next();
            String string2 = (String)entry.getKey();
            Object v = entry.getValue();
            PdfIndirectReference pdfIndirectReference2 = this.getPdfIndirectReference();
            PdfDictionary pdfDictionary2 = new PdfDictionary();
            if (pdfIndirectReference != null) {
                pdfDictionary2.put(PdfName.PARENT, pdfIndirectReference);
            }
            pdfDictionary2.put(PdfName.T, new PdfString(string2, "UnicodeBig"));
            String string3 = string + "." + string2;
            int n = this.calculationOrder.indexOf(string3);
            if (n >= 0) {
                this.calculationOrderRefs.set(n, pdfIndirectReference2);
            }
            if (v instanceof HashMap) {
                pdfDictionary2.put(PdfName.KIDS, this.branchForm((HashMap)v, pdfIndirectReference2, string3));
                pdfArray.add(pdfIndirectReference2);
                this.addToBody((PdfObject)pdfDictionary2, pdfIndirectReference2);
                continue;
            }
            ArrayList arrayList = (ArrayList)v;
            pdfDictionary2.mergeDifferent((PdfDictionary)arrayList.get(0));
            if (arrayList.size() == 3) {
                pdfDictionary2.mergeDifferent((PdfDictionary)arrayList.get(2));
                int n2 = (Integer)arrayList.get(1);
                PdfDictionary pdfDictionary3 = (PdfDictionary)this.pageDics.get(n2 - 1);
                PdfArray pdfArray2 = (PdfArray)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.ANNOTS));
                if (pdfArray2 == null) {
                    pdfArray2 = new PdfArray();
                    pdfDictionary3.put(PdfName.ANNOTS, pdfArray2);
                }
                pdfDictionary = (PdfNumber)pdfDictionary2.get(iTextTag);
                pdfDictionary2.remove(iTextTag);
                this.adjustTabOrder(pdfArray2, pdfIndirectReference2, (PdfNumber)((Object)pdfDictionary));
            } else {
                PdfArray pdfArray3 = new PdfArray();
                for (int i = 1; i < arrayList.size(); i += 2) {
                    int n3 = (Integer)arrayList.get(i);
                    pdfDictionary = (PdfDictionary)this.pageDics.get(n3 - 1);
                    PdfArray pdfArray4 = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.ANNOTS));
                    if (pdfArray4 == null) {
                        pdfArray4 = new PdfArray();
                        pdfDictionary.put(PdfName.ANNOTS, pdfArray4);
                    }
                    PdfDictionary pdfDictionary4 = new PdfDictionary();
                    pdfDictionary4.merge((PdfDictionary)arrayList.get(i + 1));
                    pdfDictionary4.put(PdfName.PARENT, pdfIndirectReference2);
                    PdfNumber pdfNumber = (PdfNumber)pdfDictionary4.get(iTextTag);
                    pdfDictionary4.remove(iTextTag);
                    PdfIndirectReference pdfIndirectReference3 = this.addToBody(pdfDictionary4).getIndirectReference();
                    this.adjustTabOrder(pdfArray4, pdfIndirectReference3, pdfNumber);
                    pdfArray3.add(pdfIndirectReference3);
                    this.propagate(pdfDictionary4, null, false);
                }
                pdfDictionary2.put(PdfName.KIDS, pdfArray3);
            }
            pdfArray.add(pdfIndirectReference2);
            this.addToBody((PdfObject)pdfDictionary2, pdfIndirectReference2);
            this.propagate(pdfDictionary2, null, false);
        }
        return pdfArray;
    }

    protected void createAcroForms() throws IOException {
        if (this.fieldTree.isEmpty()) {
            return;
        }
        this.form = new PdfDictionary();
        this.form.put(PdfName.DR, this.resources);
        this.propagate(this.resources, null, false);
        this.form.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        this.tabOrder = new HashMap();
        this.calculationOrderRefs = new ArrayList(this.calculationOrder);
        this.form.put(PdfName.FIELDS, this.branchForm(this.fieldTree, null, ""));
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < this.calculationOrderRefs.size(); ++i) {
            Object e = this.calculationOrderRefs.get(i);
            if (!(e instanceof PdfIndirectReference)) continue;
            pdfArray.add((PdfIndirectReference)e);
        }
        if (pdfArray.size() > 0) {
            this.form.put(PdfName.CO, pdfArray);
        }
    }

    public void close() {
        if (this.closing) {
            super.close();
            return;
        }
        this.closing = true;
        try {
            this.closeIt();
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeIt() throws IOException {
        int n;
        int[] arrn;
        int n2;
        Object object;
        Object object2;
        for (n2 = 0; n2 < this.readers.size(); ++n2) {
            ((PdfReader)this.readers.get(n2)).removeFields();
        }
        for (n2 = 0; n2 < this.readers.size(); ++n2) {
            object = (PdfReader)this.readers.get(n2);
            for (n = 1; n <= object.getNumberOfPages(); ++n) {
                this.pageRefs.add(this.getNewReference(object.getPageOrigRef(n)));
                this.pageDics.add(object.getPageN(n));
            }
        }
        this.mergeFields();
        this.createAcroForms();
        for (n2 = 0; n2 < this.readers.size(); ++n2) {
            object = (PdfReader)this.readers.get(n2);
            for (n = 1; n <= object.getNumberOfPages(); ++n) {
                object2 = object.getPageN(n);
                arrn = this.getNewReference(object.getPageOrigRef(n));
                PdfIndirectReference pdfIndirectReference = this.root.addPageRef((PdfIndirectReference)arrn);
                object2.put(PdfName.PARENT, pdfIndirectReference);
                this.propagate((PdfObject)object2, (PdfIndirectReference)arrn, false);
            }
        }
        Iterator iterator = this.readers2intrefs.entrySet().iterator();
        while (iterator.hasNext()) {
            object = iterator.next();
            PdfReader pdfReader = (PdfReader)object.getKey();
            try {
                this.file = pdfReader.getSafeFile();
                this.file.reOpen();
                object2 = (IntHashtable)object.getValue();
                arrn = object2.toOrderedKeys();
                for (int i = 0; i < arrn.length; ++i) {
                    PRIndirectReference pRIndirectReference = new PRIndirectReference(pdfReader, arrn[i]);
                    this.addToBody(PdfReader.getPdfObjectRelease(pRIndirectReference), object2.get(arrn[i]));
                }
                continue;
            }
            finally {
                try {
                    this.file.close();
                    pdfReader.close();
                    continue;
                }
                catch (Exception var4_8) {}
                continue;
            }
        }
        this.pdf.close();
    }

    void addPageOffsetToField(HashMap hashMap, int n) {
        if (n == 0) {
            return;
        }
        Iterator iterator = hashMap.values().iterator();
        while (iterator.hasNext()) {
            ArrayList arrayList = ((AcroFields.Item)iterator.next()).page;
            for (int i = 0; i < arrayList.size(); ++i) {
                arrayList.set(i, new Integer((Integer)arrayList.get(i) + n));
            }
        }
    }

    void createWidgets(ArrayList arrayList, AcroFields.Item item) {
        for (int i = 0; i < item.merged.size(); ++i) {
            arrayList.add(item.page.get(i));
            PdfDictionary pdfDictionary = (PdfDictionary)item.merged.get(i);
            PdfObject pdfObject = pdfDictionary.get(PdfName.DR);
            if (pdfObject != null) {
                PdfFormField.mergeResources(this.resources, (PdfDictionary)PdfReader.getPdfObject(pdfObject));
            }
            PdfDictionary pdfDictionary2 = new PdfDictionary();
            Iterator iterator = pdfDictionary.getKeys().iterator();
            while (iterator.hasNext()) {
                PdfName pdfName = (PdfName)iterator.next();
                if (!widgetKeys.containsKey(pdfName)) continue;
                pdfDictionary2.put(pdfName, pdfDictionary.get(pdfName));
            }
            pdfDictionary2.put(iTextTag, new PdfNumber((Integer)item.tabOrder.get(i) + 1));
            arrayList.add(pdfDictionary2);
        }
    }

    void mergeField(String string, AcroFields.Item item) {
        Object object;
        HashMap hashMap;
        String string2;
        block15 : {
            hashMap = this.fieldTree;
            StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
            if (!stringTokenizer.hasMoreTokens()) {
                return;
            }
            do {
                string2 = stringTokenizer.nextToken();
                object = hashMap.get(string2);
                if (!stringTokenizer.hasMoreTokens()) break block15;
                if (object == null) {
                    object = new HashMap();
                    hashMap.put(string2, object);
                    hashMap = (HashMap)object;
                    continue;
                }
                if (!(object instanceof HashMap)) break;
                hashMap = (HashMap)object;
            } while (true);
            return;
        }
        if (object instanceof HashMap) {
            return;
        }
        PdfDictionary pdfDictionary = (PdfDictionary)item.merged.get(0);
        if (object == null) {
            PdfDictionary pdfDictionary2 = new PdfDictionary();
            Object object2 = pdfDictionary.getKeys().iterator();
            while (object2.hasNext()) {
                PdfName pdfName = (PdfName)object2.next();
                if (!fieldKeys.containsKey(pdfName)) continue;
                pdfDictionary2.put(pdfName, pdfDictionary.get(pdfName));
            }
            object2 = new ArrayList();
            object2.add(pdfDictionary2);
            this.createWidgets((ArrayList)object2, item);
            hashMap.put(string2, object2);
        } else {
            ArrayList arrayList = (ArrayList)object;
            PdfDictionary pdfDictionary3 = (PdfDictionary)arrayList.get(0);
            PdfName pdfName = (PdfName)pdfDictionary3.get(PdfName.FT);
            PdfName pdfName2 = (PdfName)pdfDictionary.get(PdfName.FT);
            if (pdfName == null || !pdfName.equals(pdfName2)) {
                return;
            }
            int n = 0;
            PdfObject pdfObject = pdfDictionary3.get(PdfName.FF);
            if (pdfObject != null && pdfObject.isNumber()) {
                n = ((PdfNumber)pdfObject).intValue();
            }
            int n2 = 0;
            PdfObject pdfObject2 = pdfDictionary.get(PdfName.FF);
            if (pdfObject2 != null && pdfObject2.isNumber()) {
                n2 = ((PdfNumber)pdfObject2).intValue();
            }
            if (pdfName.equals(PdfName.BTN)) {
                if (((n ^ n2) & 65536) != 0) {
                    return;
                }
                if ((n & 65536) == 0 && ((n ^ n2) & 32768) != 0) {
                    return;
                }
            } else if (pdfName.equals(PdfName.CH) && ((n ^ n2) & 131072) != 0) {
                return;
            }
            this.createWidgets(arrayList, item);
        }
    }

    void mergeWithMaster(HashMap hashMap) {
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String string = (String)entry.getKey();
            this.mergeField(string, (AcroFields.Item)entry.getValue());
        }
    }

    void mergeFields() {
        int n = 0;
        for (int i = 0; i < this.fields.size(); ++i) {
            HashMap hashMap = ((AcroFields)this.fields.get(i)).getFields();
            this.addPageOffsetToField(hashMap, n);
            this.mergeWithMaster(hashMap);
            n += ((PdfReader)this.readers.get(i)).getNumberOfPages();
        }
    }

    public PdfIndirectReference getPageReference(int n) {
        return (PdfIndirectReference)this.pageRefs.get(n - 1);
    }

    protected PdfDictionary getCatalog(PdfIndirectReference pdfIndirectReference) {
        try {
            PdfDocument.PdfCatalog pdfCatalog = this.pdf.getCatalog(pdfIndirectReference);
            if (this.form != null) {
                PdfIndirectReference pdfIndirectReference2 = this.addToBody(this.form).getIndirectReference();
                pdfCatalog.put(PdfName.ACROFORM, pdfIndirectReference2);
            }
            this.writeOutlines(pdfCatalog, false);
            return pdfCatalog;
        }
        catch (IOException var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    protected PdfIndirectReference getNewReference(PRIndirectReference pRIndirectReference) {
        return new PdfIndirectReference(0, this.getNewObjectNumber(pRIndirectReference.getReader(), pRIndirectReference.getNumber(), 0));
    }

    protected int getNewObjectNumber(PdfReader pdfReader, int n, int n2) {
        IntHashtable intHashtable = (IntHashtable)this.readers2intrefs.get(pdfReader);
        int n3 = intHashtable.get(n);
        if (n3 == 0) {
            n3 = this.getIndirectReferenceNumber();
            intHashtable.put(n, n3);
        }
        return n3;
    }

    protected boolean isVisited(PdfReader pdfReader, int n, int n2) {
        IntHashtable intHashtable = (IntHashtable)this.readers2intrefs.get(pdfReader);
        return intHashtable.containsKey(n);
    }

    protected boolean isVisited(PRIndirectReference pRIndirectReference) {
        IntHashtable intHashtable = (IntHashtable)this.visited.get(pRIndirectReference.getReader());
        return intHashtable.containsKey(pRIndirectReference.getNumber());
    }

    protected boolean setVisited(PRIndirectReference pRIndirectReference) {
        IntHashtable intHashtable = (IntHashtable)this.visited.get(pRIndirectReference.getReader());
        return intHashtable.put(pRIndirectReference.getNumber(), 1) != 0;
    }

    protected boolean isPage(PRIndirectReference pRIndirectReference) {
        IntHashtable intHashtable = (IntHashtable)this.pages2intrefs.get(pRIndirectReference.getReader());
        return intHashtable.containsKey(pRIndirectReference.getNumber());
    }

    RandomAccessFileOrArray getReaderFile(PdfReader pdfReader) {
        return this.file;
    }

    public void openDoc() {
        if (!this.nd.isOpen()) {
            this.nd.open();
        }
    }

    static {
        Integer n = new Integer(1);
        widgetKeys.put(PdfName.SUBTYPE, n);
        widgetKeys.put(PdfName.CONTENTS, n);
        widgetKeys.put(PdfName.RECT, n);
        widgetKeys.put(PdfName.NM, n);
        widgetKeys.put(PdfName.M, n);
        widgetKeys.put(PdfName.F, n);
        widgetKeys.put(PdfName.BS, n);
        widgetKeys.put(PdfName.BORDER, n);
        widgetKeys.put(PdfName.AP, n);
        widgetKeys.put(PdfName.AS, n);
        widgetKeys.put(PdfName.C, n);
        widgetKeys.put(PdfName.A, n);
        widgetKeys.put(PdfName.STRUCTPARENT, n);
        widgetKeys.put(PdfName.OC, n);
        widgetKeys.put(PdfName.H, n);
        widgetKeys.put(PdfName.MK, n);
        widgetKeys.put(PdfName.DA, n);
        widgetKeys.put(PdfName.Q, n);
        fieldKeys.put(PdfName.AA, n);
        fieldKeys.put(PdfName.FT, n);
        fieldKeys.put(PdfName.TU, n);
        fieldKeys.put(PdfName.TM, n);
        fieldKeys.put(PdfName.FF, n);
        fieldKeys.put(PdfName.V, n);
        fieldKeys.put(PdfName.DV, n);
        fieldKeys.put(PdfName.DS, n);
        fieldKeys.put(PdfName.RV, n);
        fieldKeys.put(PdfName.OPT, n);
        fieldKeys.put(PdfName.MAXLEN, n);
        fieldKeys.put(PdfName.TI, n);
        fieldKeys.put(PdfName.I, n);
        fieldKeys.put(PdfName.LOCK, n);
        fieldKeys.put(PdfName.SV, n);
    }
}

