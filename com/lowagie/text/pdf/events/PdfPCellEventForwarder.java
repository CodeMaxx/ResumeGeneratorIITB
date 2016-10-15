/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfPCellEventForwarder
implements PdfPCellEvent {
    protected ArrayList events = new ArrayList();

    public void addCellEvent(PdfPCellEvent pdfPCellEvent) {
        this.events.add(pdfPCellEvent);
    }

    public void cellLayout(PdfPCell pdfPCell, Rectangle rectangle, PdfContentByte[] arrpdfContentByte) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPCellEvent pdfPCellEvent = (PdfPCellEvent)iterator.next();
            pdfPCellEvent.cellLayout(pdfPCell, rectangle, arrpdfContentByte);
        }
    }
}

