/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfNumber;
import java.util.ArrayList;

public class PdfTextArray {
    ArrayList arrayList = new ArrayList();
    private String lastStr;
    private Float lastNum;

    public PdfTextArray(String string) {
        this.add(string);
    }

    public PdfTextArray() {
    }

    public void add(PdfNumber pdfNumber) {
        this.add((float)pdfNumber.doubleValue());
    }

    public void add(float f) {
        if (f != 0.0f) {
            if (this.lastNum != null) {
                this.lastNum = new Float(f + this.lastNum.floatValue());
                if (this.lastNum.floatValue() != 0.0f) {
                    this.replaceLast(this.lastNum);
                } else {
                    this.arrayList.remove(this.arrayList.size() - 1);
                }
            } else {
                this.lastNum = new Float(f);
                this.arrayList.add(this.lastNum);
            }
            this.lastStr = null;
        }
    }

    public void add(String string) {
        if (string.length() > 0) {
            if (this.lastStr != null) {
                this.lastStr = this.lastStr + string;
                this.replaceLast(this.lastStr);
            } else {
                this.lastStr = string;
                this.arrayList.add(this.lastStr);
            }
            this.lastNum = null;
        }
    }

    ArrayList getArrayList() {
        return this.arrayList;
    }

    private void replaceLast(Object object) {
        this.arrayList.set(this.arrayList.size() - 1, object);
    }
}

