/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.factories.RomanAlphabetFactory;
import com.lowagie.text.factories.RomanNumberFactory;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfNumberTree;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PdfPageLabels {
    public static final int DECIMAL_ARABIC_NUMERALS = 0;
    public static final int UPPERCASE_ROMAN_NUMERALS = 1;
    public static final int LOWERCASE_ROMAN_NUMERALS = 2;
    public static final int UPPERCASE_LETTERS = 3;
    public static final int LOWERCASE_LETTERS = 4;
    public static final int EMPTY = 5;
    static PdfName[] numberingStyle = new PdfName[]{PdfName.D, PdfName.R, new PdfName("r"), PdfName.A, new PdfName("a")};
    private HashMap map = new HashMap();

    public PdfPageLabels() {
        this.addPageLabel(1, 0, null, 1);
    }

    public void addPageLabel(int n, int n2, String string, int n3) {
        if (n < 1 || n3 < 1) {
            throw new IllegalArgumentException("In a page label the page numbers must be greater or equal to 1.");
        }
        PdfDictionary pdfDictionary = new PdfDictionary();
        if (n2 >= 0 && n2 < numberingStyle.length) {
            pdfDictionary.put(PdfName.S, numberingStyle[n2]);
        }
        if (string != null) {
            pdfDictionary.put(PdfName.P, new PdfString(string, "UnicodeBig"));
        }
        if (n3 != 1) {
            pdfDictionary.put(PdfName.ST, new PdfNumber(n3));
        }
        this.map.put(new Integer(n - 1), pdfDictionary);
    }

    public void addPageLabel(int n, int n2, String string) {
        this.addPageLabel(n, n2, string, 1);
    }

    public void addPageLabel(int n, int n2) {
        this.addPageLabel(n, n2, null, 1);
    }

    public void addPageLabel(PdfPageLabelFormat pdfPageLabelFormat) {
        this.addPageLabel(pdfPageLabelFormat.physicalPage, pdfPageLabelFormat.numberStyle, pdfPageLabelFormat.prefix, pdfPageLabelFormat.logicalPage);
    }

    public void removePageLabel(int n) {
        if (n <= 1) {
            return;
        }
        this.map.remove(new Integer(n - 1));
    }

    PdfDictionary getDictionary(PdfWriter pdfWriter) {
        try {
            return PdfNumberTree.writeTree(this.map, pdfWriter);
        }
        catch (IOException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    public static String[] getPageLabels(PdfReader pdfReader) {
        int n = pdfReader.getNumberOfPages();
        PdfDictionary pdfDictionary = pdfReader.getCatalog();
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.PAGELABELS));
        if (pdfDictionary2 == null) {
            return null;
        }
        String[] arrstring = new String[n];
        HashMap hashMap = PdfNumberTree.readTree(pdfDictionary2);
        int n2 = 1;
        String string = "";
        int n3 = 68;
        for (int i = 0; i < n; ++i) {
            Integer n4 = new Integer(i);
            if (hashMap.containsKey(n4)) {
                PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)hashMap.get(n4));
                n2 = pdfDictionary3.contains(PdfName.ST) ? ((PdfNumber)pdfDictionary3.get(PdfName.ST)).intValue() : 1;
                if (pdfDictionary3.contains(PdfName.P)) {
                    string = ((PdfString)pdfDictionary3.get(PdfName.P)).toUnicodeString();
                }
                if (pdfDictionary3.contains(PdfName.S)) {
                    n3 = ((PdfName)pdfDictionary3.get(PdfName.S)).toString().charAt(1);
                }
            }
            switch (n3) {
                default: {
                    arrstring[i] = string + n2;
                    break;
                }
                case 82: {
                    arrstring[i] = string + RomanNumberFactory.getUpperCaseString(n2);
                    break;
                }
                case 114: {
                    arrstring[i] = string + RomanNumberFactory.getLowerCaseString(n2);
                    break;
                }
                case 65: {
                    arrstring[i] = string + RomanAlphabetFactory.getUpperCaseString(n2);
                    break;
                }
                case 97: {
                    arrstring[i] = string + RomanAlphabetFactory.getLowerCaseString(n2);
                }
            }
            ++n2;
        }
        return arrstring;
    }

    public static PdfPageLabelFormat[] getPageLabelFormats(PdfReader pdfReader) {
        PdfDictionary pdfDictionary = pdfReader.getCatalog();
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.PAGELABELS));
        if (pdfDictionary2 == null) {
            return null;
        }
        HashMap hashMap = PdfNumberTree.readTree(pdfDictionary2);
        Object[] arrobject = new Integer[hashMap.size()];
        arrobject = hashMap.keySet().toArray(arrobject);
        Arrays.sort(arrobject);
        PdfPageLabelFormat[] arrpdfPageLabelFormat = new PdfPageLabelFormat[hashMap.size()];
        for (int i = 0; i < arrobject.length; ++i) {
            int n;
            Object object = arrobject[i];
            PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)hashMap.get(object));
            int n2 = pdfDictionary3.contains(PdfName.ST) ? ((PdfNumber)pdfDictionary3.get(PdfName.ST)).intValue() : 1;
            String string = pdfDictionary3.contains(PdfName.P) ? ((PdfString)pdfDictionary3.get(PdfName.P)).toUnicodeString() : "";
            if (pdfDictionary3.contains(PdfName.S)) {
                char c = ((PdfName)pdfDictionary3.get(PdfName.S)).toString().charAt(1);
                switch (c) {
                    case 'R': {
                        n = 1;
                        break;
                    }
                    case 'r': {
                        n = 2;
                        break;
                    }
                    case 'A': {
                        n = 3;
                        break;
                    }
                    case 'a': {
                        n = 4;
                        break;
                    }
                    default: {
                        n = 0;
                        break;
                    }
                }
            } else {
                n = 5;
            }
            arrpdfPageLabelFormat[i] = new PdfPageLabelFormat(object.intValue() + 1, n, string, n2);
        }
        return arrpdfPageLabelFormat;
    }

    public static class PdfPageLabelFormat {
        public int physicalPage;
        public int numberStyle;
        public String prefix;
        public int logicalPage;

        public PdfPageLabelFormat(int n, int n2, String string, int n3) {
            this.physicalPage = n;
            this.numberStyle = n2;
            this.prefix = string;
            this.logicalPage = n3;
        }
    }

}

