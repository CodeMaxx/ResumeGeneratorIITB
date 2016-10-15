/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFont;
import com.lowagie.text.pdf.PdfLine;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class VerticalText {
    public static final int NO_MORE_TEXT = 1;
    public static final int NO_MORE_COLUMN = 2;
    protected ArrayList chunks = new ArrayList();
    protected PdfContentByte text;
    protected int alignment = 0;
    protected int currentChunkMarker = -1;
    protected PdfChunk currentStandbyChunk;
    protected String splittedChunkText;
    protected float leading;
    protected float startX;
    protected float startY;
    protected int maxLines;
    protected float height;

    public VerticalText(PdfContentByte pdfContentByte) {
        this.text = pdfContentByte;
    }

    public void addText(Phrase phrase) {
        Iterator iterator = phrase.getChunks().iterator();
        while (iterator.hasNext()) {
            this.chunks.add(new PdfChunk((Chunk)iterator.next(), null));
        }
    }

    public void addText(Chunk chunk) {
        this.chunks.add(new PdfChunk(chunk, null));
    }

    public void setVerticalLayout(float f, float f2, float f3, int n, float f4) {
        this.startX = f;
        this.startY = f2;
        this.height = f3;
        this.maxLines = n;
        this.setLeading(f4);
    }

    public void setLeading(float f) {
        this.leading = f;
    }

    public float getLeading() {
        return this.leading;
    }

    protected PdfLine createLine(float f) {
        if (this.chunks.isEmpty()) {
            return null;
        }
        this.splittedChunkText = null;
        this.currentStandbyChunk = null;
        PdfLine pdfLine = new PdfLine(0.0f, f, this.alignment, 0.0f);
        this.currentChunkMarker = 0;
        while (this.currentChunkMarker < this.chunks.size()) {
            PdfChunk pdfChunk = (PdfChunk)this.chunks.get(this.currentChunkMarker);
            String string = pdfChunk.toString();
            this.currentStandbyChunk = pdfLine.add(pdfChunk);
            if (this.currentStandbyChunk != null) {
                this.splittedChunkText = pdfChunk.toString();
                pdfChunk.setValue(string);
                return pdfLine;
            }
            ++this.currentChunkMarker;
        }
        return pdfLine;
    }

    protected void shortenChunkArray() {
        if (this.currentChunkMarker < 0) {
            return;
        }
        if (this.currentChunkMarker >= this.chunks.size()) {
            this.chunks.clear();
            return;
        }
        PdfChunk pdfChunk = (PdfChunk)this.chunks.get(this.currentChunkMarker);
        pdfChunk.setValue(this.splittedChunkText);
        this.chunks.set(this.currentChunkMarker, this.currentStandbyChunk);
        for (int i = this.currentChunkMarker - 1; i >= 0; --i) {
            this.chunks.remove(i);
        }
    }

    public int go() {
        return this.go(false);
    }

    public int go(boolean bl) {
        boolean bl2 = false;
        PdfContentByte pdfContentByte = null;
        if (this.text != null) {
            pdfContentByte = this.text.getDuplicate();
        } else if (!bl) {
            throw new NullPointerException("VerticalText.go with simulate==false and text==null.");
        }
        int n = 0;
        do {
            if (this.maxLines <= 0) {
                n = 2;
                if (!this.chunks.isEmpty()) break;
                n |= 1;
                break;
            }
            if (this.chunks.isEmpty()) {
                n = 1;
                break;
            }
            PdfLine pdfLine = this.createLine(this.height);
            if (!bl && !bl2) {
                this.text.beginText();
                bl2 = true;
            }
            this.shortenChunkArray();
            if (!bl) {
                this.text.setTextMatrix(this.startX, this.startY - pdfLine.indentLeft());
                this.writeLine(pdfLine, this.text, pdfContentByte);
            }
            --this.maxLines;
            this.startX -= this.leading;
        } while (true);
        if (bl2) {
            this.text.endText();
            this.text.add(pdfContentByte);
        }
        return n;
    }

    void writeLine(PdfLine pdfLine, PdfContentByte pdfContentByte, PdfContentByte pdfContentByte2) {
        PdfFont pdfFont = null;
        Iterator iterator = pdfLine.iterator();
        while (iterator.hasNext()) {
            Color color;
            PdfChunk pdfChunk = (PdfChunk)iterator.next();
            if (pdfChunk.font().compareTo(pdfFont) != 0) {
                pdfFont = pdfChunk.font();
                pdfContentByte.setFontAndSize(pdfFont.getFont(), pdfFont.size());
            }
            if ((color = pdfChunk.color()) != null) {
                pdfContentByte.setColorFill(color);
            }
            pdfContentByte.showText(pdfChunk.toString());
            if (color == null) continue;
            pdfContentByte.resetRGBColorFill();
        }
    }

    public void setOrigin(float f, float f2) {
        this.startX = f;
        this.startY = f2;
    }

    public float getOriginX() {
        return this.startX;
    }

    public float getOriginY() {
        return this.startY;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public void setMaxLines(int n) {
        this.maxLines = n;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float f) {
        this.height = f;
    }

    public void setAlignment(int n) {
        this.alignment = n;
    }

    public int getAlignment() {
        return this.alignment;
    }
}

