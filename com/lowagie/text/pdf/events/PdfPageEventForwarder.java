/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfPageEventForwarder
implements PdfPageEvent {
    protected ArrayList events = new ArrayList();

    public void addPageEvent(PdfPageEvent pdfPageEvent) {
        this.events.add(pdfPageEvent);
    }

    public void onOpenDocument(PdfWriter pdfWriter, Document document) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onOpenDocument(pdfWriter, document);
        }
    }

    public void onStartPage(PdfWriter pdfWriter, Document document) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onStartPage(pdfWriter, document);
        }
    }

    public void onEndPage(PdfWriter pdfWriter, Document document) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onEndPage(pdfWriter, document);
        }
    }

    public void onCloseDocument(PdfWriter pdfWriter, Document document) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onCloseDocument(pdfWriter, document);
        }
    }

    public void onParagraph(PdfWriter pdfWriter, Document document, float f) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onParagraph(pdfWriter, document, f);
        }
    }

    public void onParagraphEnd(PdfWriter pdfWriter, Document document, float f) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onParagraphEnd(pdfWriter, document, f);
        }
    }

    public void onChapter(PdfWriter pdfWriter, Document document, float f, Paragraph paragraph) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onChapter(pdfWriter, document, f, paragraph);
        }
    }

    public void onChapterEnd(PdfWriter pdfWriter, Document document, float f) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onChapterEnd(pdfWriter, document, f);
        }
    }

    public void onSection(PdfWriter pdfWriter, Document document, float f, int n, Paragraph paragraph) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onSection(pdfWriter, document, f, n, paragraph);
        }
    }

    public void onSectionEnd(PdfWriter pdfWriter, Document document, float f) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onSectionEnd(pdfWriter, document, f);
        }
    }

    public void onGenericTag(PdfWriter pdfWriter, Document document, Rectangle rectangle, String string) {
        Iterator iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent pdfPageEvent = (PdfPageEvent)iterator.next();
            pdfPageEvent.onGenericTag(pdfWriter, document, rectangle, string);
        }
    }
}

