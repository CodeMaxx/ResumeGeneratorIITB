/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfPTableEventForwarder
implements PdfPTableEvent {
    protected ArrayList events = new ArrayList();

    public void addTableEvent(PdfPTableEvent pdfPTableEvent) {
        this.events.add(pdfPTableEvent);
    }

    public void tableLayout(PdfPTable pdfPTable, float[][] arrf, float[] arrf2, int n, int n2, PdfContentByte[] arrpdfContentByte) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPTableEvent pdfPTableEvent = (PdfPTableEvent)iterator.next();
            pdfPTableEvent.tableLayout(pdfPTable, arrf, arrf2, n, n2, arrpdfContentByte);
        }
    }
}

