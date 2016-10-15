/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.draw;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.util.ArrayList;

public class VerticalPositionMark
implements DrawInterface,
Element {
    protected DrawInterface drawInterface = null;
    protected float offset = 0.0f;

    public VerticalPositionMark() {
    }

    public VerticalPositionMark(DrawInterface drawInterface, float f) {
        this.drawInterface = drawInterface;
        this.offset = f;
    }

    public void draw(PdfContentByte pdfContentByte, float f, float f2, float f3, float f4, float f5) {
        if (this.drawInterface != null) {
            this.drawInterface.draw(pdfContentByte, f, f2, f3, f4, f5 + this.offset);
        }
    }

    public boolean process(ElementListener elementListener) {
        try {
            return elementListener.add(this);
        }
        catch (DocumentException var2_2) {
            return false;
        }
    }

    public int type() {
        return 55;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return false;
    }

    public ArrayList getChunks() {
        ArrayList<Chunk> arrayList = new ArrayList<Chunk>();
        arrayList.add(new Chunk((DrawInterface)this, true));
        return arrayList;
    }

    public DrawInterface getDrawInterface() {
        return this.drawInterface;
    }

    public void setDrawInterface(DrawInterface drawInterface) {
        this.drawInterface = drawInterface;
    }

    public float getOffset() {
        return this.offset;
    }

    public void setOffset(float f) {
        this.offset = f;
    }
}

