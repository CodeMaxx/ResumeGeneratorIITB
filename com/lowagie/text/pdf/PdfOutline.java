/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfOutline
extends PdfDictionary {
    private PdfIndirectReference reference;
    private int count = 0;
    private PdfOutline parent;
    private PdfDestination destination;
    private PdfAction action;
    protected ArrayList kids = new ArrayList();
    protected PdfWriter writer;
    private String tag;
    private boolean open;
    private Color color;
    private int style = 0;

    PdfOutline(PdfWriter pdfWriter) {
        super(OUTLINES);
        this.open = true;
        this.parent = null;
        this.writer = pdfWriter;
    }

    public PdfOutline(PdfOutline pdfOutline, PdfAction pdfAction, String string) {
        this(pdfOutline, pdfAction, string, true);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfAction pdfAction, String string, boolean bl) {
        this.action = pdfAction;
        this.initOutline(pdfOutline, string, bl);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfDestination pdfDestination, String string) {
        this(pdfOutline, pdfDestination, string, true);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfDestination pdfDestination, String string, boolean bl) {
        this.destination = pdfDestination;
        this.initOutline(pdfOutline, string, bl);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfAction pdfAction, PdfString pdfString) {
        this(pdfOutline, pdfAction, pdfString, true);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfAction pdfAction, PdfString pdfString, boolean bl) {
        this(pdfOutline, pdfAction, pdfString.toString(), bl);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfDestination pdfDestination, PdfString pdfString) {
        this(pdfOutline, pdfDestination, pdfString, true);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfDestination pdfDestination, PdfString pdfString, boolean bl) {
        this(pdfOutline, pdfDestination, pdfString.toString(), true);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfAction pdfAction, Paragraph paragraph) {
        this(pdfOutline, pdfAction, paragraph, true);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfAction pdfAction, Paragraph paragraph, boolean bl) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator iterator = paragraph.getChunks().iterator();
        while (iterator.hasNext()) {
            Chunk chunk = (Chunk)iterator.next();
            stringBuffer.append(chunk.getContent());
        }
        this.action = pdfAction;
        this.initOutline(pdfOutline, stringBuffer.toString(), bl);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfDestination pdfDestination, Paragraph paragraph) {
        this(pdfOutline, pdfDestination, paragraph, true);
    }

    public PdfOutline(PdfOutline pdfOutline, PdfDestination pdfDestination, Paragraph paragraph, boolean bl) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator iterator = paragraph.getChunks().iterator();
        while (iterator.hasNext()) {
            Chunk chunk = (Chunk)iterator.next();
            stringBuffer.append(chunk.getContent());
        }
        this.destination = pdfDestination;
        this.initOutline(pdfOutline, stringBuffer.toString(), bl);
    }

    void initOutline(PdfOutline pdfOutline, String string, boolean bl) {
        this.open = bl;
        this.parent = pdfOutline;
        this.writer = pdfOutline.writer;
        this.put(PdfName.TITLE, new PdfString(string, "UnicodeBig"));
        pdfOutline.addKid(this);
        if (this.destination != null && !this.destination.hasPage()) {
            this.setDestinationPage(this.writer.getCurrentPage());
        }
    }

    public void setIndirectReference(PdfIndirectReference pdfIndirectReference) {
        this.reference = pdfIndirectReference;
    }

    public PdfIndirectReference indirectReference() {
        return this.reference;
    }

    public PdfOutline parent() {
        return this.parent;
    }

    public boolean setDestinationPage(PdfIndirectReference pdfIndirectReference) {
        if (this.destination == null) {
            return false;
        }
        return this.destination.addPage(pdfIndirectReference);
    }

    public PdfDestination getPdfDestination() {
        return this.destination;
    }

    int getCount() {
        return this.count;
    }

    void setCount(int n) {
        this.count = n;
    }

    public int level() {
        if (this.parent == null) {
            return 0;
        }
        return this.parent.level() + 1;
    }

    public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
        if (this.color != null && !this.color.equals(Color.black)) {
            this.put(PdfName.C, new PdfArray(new float[]{(float)this.color.getRed() / 255.0f, (float)this.color.getGreen() / 255.0f, (float)this.color.getBlue() / 255.0f}));
        }
        int n = 0;
        if ((this.style & 1) != 0) {
            n |= 2;
        }
        if ((this.style & 2) != 0) {
            n |= 1;
        }
        if (n != 0) {
            this.put(PdfName.F, new PdfNumber(n));
        }
        if (this.parent != null) {
            this.put(PdfName.PARENT, this.parent.indirectReference());
        }
        if (this.destination != null && this.destination.hasPage()) {
            this.put(PdfName.DEST, this.destination);
        }
        if (this.action != null) {
            this.put(PdfName.A, this.action);
        }
        if (this.count != 0) {
            this.put(PdfName.COUNT, new PdfNumber(this.count));
        }
        super.toPdf(pdfWriter, outputStream);
    }

    public void addKid(PdfOutline pdfOutline) {
        this.kids.add(pdfOutline);
    }

    public ArrayList getKids() {
        return this.kids;
    }

    public void setKids(ArrayList arrayList) {
        this.kids = arrayList;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String string) {
        this.tag = string;
    }

    public String getTitle() {
        PdfString pdfString = (PdfString)this.get(PdfName.TITLE);
        return pdfString.toString();
    }

    public void setTitle(String string) {
        this.put(PdfName.TITLE, new PdfString(string, "UnicodeBig"));
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean bl) {
        this.open = bl;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getStyle() {
        return this.style;
    }

    public void setStyle(int n) {
        this.style = n;
    }
}

