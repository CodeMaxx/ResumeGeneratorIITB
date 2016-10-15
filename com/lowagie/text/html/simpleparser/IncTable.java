/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IncTable {
    private HashMap props = new HashMap();
    private ArrayList rows = new ArrayList();
    private ArrayList cols;

    public IncTable(HashMap hashMap) {
        this.props.putAll(hashMap);
    }

    public void addCol(PdfPCell pdfPCell) {
        if (this.cols == null) {
            this.cols = new ArrayList();
        }
        this.cols.add(pdfPCell);
    }

    public void addCols(ArrayList arrayList) {
        if (this.cols == null) {
            this.cols = new ArrayList(arrayList);
        } else {
            this.cols.addAll(arrayList);
        }
    }

    public void endRow() {
        if (this.cols != null) {
            Collections.reverse(this.cols);
            this.rows.add(this.cols);
            this.cols = null;
        }
    }

    public ArrayList getRows() {
        return this.rows;
    }

    public PdfPTable buildTable() {
        if (this.rows.isEmpty()) {
            return new PdfPTable(1);
        }
        int n = 0;
        ArrayList arrayList = (ArrayList)this.rows.get(0);
        for (int i = 0; i < arrayList.size(); ++i) {
            n += ((PdfPCell)arrayList.get(i)).getColspan();
        }
        PdfPTable pdfPTable = new PdfPTable(n);
        String string = (String)this.props.get("width");
        if (string == null) {
            pdfPTable.setWidthPercentage(100.0f);
        } else if (string.endsWith("%")) {
            pdfPTable.setWidthPercentage(Float.parseFloat(string.substring(0, string.length() - 1)));
        } else {
            pdfPTable.setTotalWidth(Float.parseFloat(string));
            pdfPTable.setLockedWidth(true);
        }
        for (int j = 0; j < this.rows.size(); ++j) {
            ArrayList arrayList2 = (ArrayList)this.rows.get(j);
            for (int k = 0; k < arrayList2.size(); ++k) {
                pdfPTable.addCell((PdfPCell)arrayList2.get(k));
            }
        }
        return pdfPTable;
    }
}

