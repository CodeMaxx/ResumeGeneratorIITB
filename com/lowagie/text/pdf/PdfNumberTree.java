/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PdfNumberTree {
    private static final int leafSize = 64;

    public static PdfDictionary writeTree(HashMap hashMap, PdfWriter pdfWriter) throws IOException {
        int n;
        int n2;
        int n3;
        if (hashMap.isEmpty()) {
            return null;
        }
        Object[] arrobject = new Integer[hashMap.size()];
        arrobject = hashMap.keySet().toArray(arrobject);
        Arrays.sort(arrobject);
        if (arrobject.length <= 64) {
            PdfDictionary pdfDictionary = new PdfDictionary();
            PdfArray pdfArray = new PdfArray();
            for (int i = 0; i < arrobject.length; ++i) {
                pdfArray.add(new PdfNumber(arrobject[i].intValue()));
                pdfArray.add((PdfObject)hashMap.get(arrobject[i]));
            }
            pdfDictionary.put(PdfName.NUMS, pdfArray);
            return pdfDictionary;
        }
        int n4 = 64;
        PdfIndirectReference[] arrpdfIndirectReference = new PdfIndirectReference[(arrobject.length + 64 - 1) / 64];
        for (n2 = 0; n2 < arrpdfIndirectReference.length; ++n2) {
            n3 = Math.min(n + 64, arrobject.length);
            PdfDictionary pdfDictionary = new PdfDictionary();
            PdfArray pdfArray = new PdfArray();
            pdfArray.add(new PdfNumber(arrobject[n].intValue()));
            pdfArray.add(new PdfNumber(arrobject[n3 - 1].intValue()));
            pdfDictionary.put(PdfName.LIMITS, pdfArray);
            pdfArray = new PdfArray();
            for (n = n2 * 64; n < n3; ++n) {
                pdfArray.add(new PdfNumber(arrobject[n].intValue()));
                pdfArray.add((PdfObject)hashMap.get(arrobject[n]));
            }
            pdfDictionary.put(PdfName.NUMS, pdfArray);
            arrpdfIndirectReference[n2] = pdfWriter.addToBody(pdfDictionary).getIndirectReference();
        }
        n2 = arrpdfIndirectReference.length;
        do {
            if (n2 <= 64) {
                PdfArray pdfArray = new PdfArray();
                for (n3 = 0; n3 < n2; ++n3) {
                    pdfArray.add(arrpdfIndirectReference[n3]);
                }
                PdfDictionary pdfDictionary = new PdfDictionary();
                pdfDictionary.put(PdfName.KIDS, pdfArray);
                return pdfDictionary;
            }
            n = (arrobject.length + n4 - 1) / (n4 *= 64);
            for (n3 = 0; n3 < n; ++n3) {
                int n5;
                int n6 = Math.min(n5 + 64, n2);
                PdfDictionary pdfDictionary = new PdfDictionary();
                PdfArray pdfArray = new PdfArray();
                pdfArray.add(new PdfNumber(arrobject[n3 * n4].intValue()));
                pdfArray.add(new PdfNumber(arrobject[Math.min((n3 + 1) * n4, arrobject.length) - 1].intValue()));
                pdfDictionary.put(PdfName.LIMITS, pdfArray);
                pdfArray = new PdfArray();
                for (n5 = n3 * 64; n5 < n6; ++n5) {
                    pdfArray.add(arrpdfIndirectReference[n5]);
                }
                pdfDictionary.put(PdfName.KIDS, pdfArray);
                arrpdfIndirectReference[n3] = pdfWriter.addToBody(pdfDictionary).getIndirectReference();
            }
            n2 = n;
        } while (true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void iterateItems(PdfDictionary pdfDictionary, HashMap hashMap) {
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.NUMS));
        if (pdfArray != null) {
            ArrayList arrayList = pdfArray.getArrayList();
            int n = 0;
            while (n < arrayList.size()) {
                PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(n++));
                hashMap.put(new Integer(pdfNumber.intValue()), arrayList.get(n));
                ++n;
            }
            return;
        }
        pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.KIDS));
        if (pdfArray == null) return;
        ArrayList arrayList = pdfArray.getArrayList();
        int n = 0;
        while (n < arrayList.size()) {
            PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)arrayList.get(n));
            PdfNumberTree.iterateItems(pdfDictionary2, hashMap);
            ++n;
        }
    }

    public static HashMap readTree(PdfDictionary pdfDictionary) {
        HashMap hashMap = new HashMap();
        if (pdfDictionary != null) {
            PdfNumberTree.iterateItems(pdfDictionary, hashMap);
        }
        return hashMap;
    }
}

