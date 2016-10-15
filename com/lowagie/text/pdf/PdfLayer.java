/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;

public class PdfLayer
extends PdfDictionary
implements PdfOCG {
    protected PdfIndirectReference ref;
    protected ArrayList children;
    protected PdfLayer parent;
    protected String title;
    private boolean on = true;
    private boolean onPanel = true;

    PdfLayer(String string) {
        this.title = string;
    }

    public static PdfLayer createTitle(String string, PdfWriter pdfWriter) {
        if (string == null) {
            throw new NullPointerException("Title cannot be null.");
        }
        PdfLayer pdfLayer = new PdfLayer(string);
        pdfWriter.registerLayer(pdfLayer);
        return pdfLayer;
    }

    public PdfLayer(String string, PdfWriter pdfWriter) {
        super(PdfName.OCG);
        this.setName(string);
        this.ref = pdfWriter.getPdfIndirectReference();
        pdfWriter.registerLayer(this);
    }

    String getTitle() {
        return this.title;
    }

    public void addChild(PdfLayer pdfLayer) {
        if (pdfLayer.parent != null) {
            throw new IllegalArgumentException("The layer '" + ((PdfString)pdfLayer.get(PdfName.NAME)).toUnicodeString() + "' already has a parent.");
        }
        pdfLayer.parent = this;
        if (this.children == null) {
            this.children = new ArrayList();
        }
        this.children.add(pdfLayer);
    }

    public PdfLayer getParent() {
        return this.parent;
    }

    public ArrayList getChildren() {
        return this.children;
    }

    public PdfIndirectReference getRef() {
        return this.ref;
    }

    void setRef(PdfIndirectReference pdfIndirectReference) {
        this.ref = pdfIndirectReference;
    }

    public void setName(String string) {
        this.put(PdfName.NAME, new PdfString(string, "UnicodeBig"));
    }

    public PdfObject getPdfObject() {
        return this;
    }

    public boolean isOn() {
        return this.on;
    }

    public void setOn(boolean bl) {
        this.on = bl;
    }

    private PdfDictionary getUsage() {
        PdfDictionary pdfDictionary = (PdfDictionary)this.get(PdfName.USAGE);
        if (pdfDictionary == null) {
            pdfDictionary = new PdfDictionary();
            this.put(PdfName.USAGE, pdfDictionary);
        }
        return pdfDictionary;
    }

    public void setCreatorInfo(String string, String string2) {
        PdfDictionary pdfDictionary = this.getUsage();
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.CREATOR, new PdfString(string, "UnicodeBig"));
        pdfDictionary2.put(PdfName.SUBTYPE, new PdfName(string2));
        pdfDictionary.put(PdfName.CREATORINFO, pdfDictionary2);
    }

    public void setLanguage(String string, boolean bl) {
        PdfDictionary pdfDictionary = this.getUsage();
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.LANG, new PdfString(string, "UnicodeBig"));
        if (bl) {
            pdfDictionary2.put(PdfName.PREFERRED, PdfName.ON);
        }
        pdfDictionary.put(PdfName.LANGUAGE, pdfDictionary2);
    }

    public void setExport(boolean bl) {
        PdfDictionary pdfDictionary = this.getUsage();
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.EXPORTSTATE, bl ? PdfName.ON : PdfName.OFF);
        pdfDictionary.put(PdfName.EXPORT, pdfDictionary2);
    }

    public void setZoom(float f, float f2) {
        if (f <= 0.0f && f2 < 0.0f) {
            return;
        }
        PdfDictionary pdfDictionary = this.getUsage();
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        if (f > 0.0f) {
            pdfDictionary2.put(PdfName.MIN, new PdfNumber(f));
        }
        if (f2 >= 0.0f) {
            pdfDictionary2.put(PdfName.MAX, new PdfNumber(f2));
        }
        pdfDictionary.put(PdfName.ZOOM, pdfDictionary2);
    }

    public void setPrint(String string, boolean bl) {
        PdfDictionary pdfDictionary = this.getUsage();
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.SUBTYPE, new PdfName(string));
        pdfDictionary2.put(PdfName.PRINTSTATE, bl ? PdfName.ON : PdfName.OFF);
        pdfDictionary.put(PdfName.PRINT, pdfDictionary2);
    }

    public void setView(boolean bl) {
        PdfDictionary pdfDictionary = this.getUsage();
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        pdfDictionary2.put(PdfName.VIEWSTATE, bl ? PdfName.ON : PdfName.OFF);
        pdfDictionary.put(PdfName.VIEW, pdfDictionary2);
    }

    public boolean isOnPanel() {
        return this.onPanel;
    }

    public void setOnPanel(boolean bl) {
        this.onPanel = bl;
    }
}

