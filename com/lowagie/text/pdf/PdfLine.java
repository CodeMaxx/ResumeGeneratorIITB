/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfFont;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfLine {
    protected ArrayList line;
    protected float left;
    protected float width;
    protected int alignment;
    protected float height;
    protected Chunk listSymbol = null;
    protected float symbolIndent;
    protected boolean newlineSplit = false;
    protected float originalWidth;
    protected boolean isRTL = false;

    PdfLine(float f, float f2, int n, float f3) {
        this.left = f;
        this.originalWidth = this.width = f2 - f;
        this.alignment = n;
        this.height = f3;
        this.line = new ArrayList();
    }

    PdfLine(float f, float f2, float f3, int n, boolean bl, ArrayList arrayList, boolean bl2) {
        this.left = f;
        this.originalWidth = f2;
        this.width = f3;
        this.alignment = n;
        this.line = arrayList;
        this.newlineSplit = bl;
        this.isRTL = bl2;
    }

    PdfChunk add(PdfChunk pdfChunk) {
        if (pdfChunk == null || pdfChunk.toString().equals("")) {
            return null;
        }
        PdfChunk pdfChunk2 = pdfChunk.split(this.width);
        boolean bl = this.newlineSplit = pdfChunk.isNewlineSplit() || pdfChunk2 == null;
        if (pdfChunk.isTab()) {
            Object[] arrobject = (Object[])pdfChunk.getAttribute("TAB");
            float f = ((Float)arrobject[1]).floatValue();
            boolean bl2 = (Boolean)arrobject[2];
            if (bl2 && f < this.originalWidth - this.width) {
                return pdfChunk;
            }
            this.width = this.originalWidth - f;
            pdfChunk.adjustLeft(this.left);
            this.addToLine(pdfChunk);
        } else if (pdfChunk.length() > 0) {
            if (pdfChunk2 != null) {
                pdfChunk.trimLastSpace();
            }
            this.width -= pdfChunk.width();
            this.addToLine(pdfChunk);
        } else {
            if (this.line.size() < 1) {
                pdfChunk = pdfChunk2;
                pdfChunk2 = pdfChunk.truncate(this.width);
                this.width -= pdfChunk.width();
                if (pdfChunk.length() > 0) {
                    this.addToLine(pdfChunk);
                    return pdfChunk2;
                }
                if (pdfChunk2 != null) {
                    this.addToLine(pdfChunk2);
                }
                return null;
            }
            this.width += ((PdfChunk)this.line.get(this.line.size() - 1)).trimLastSpace();
        }
        return pdfChunk2;
    }

    private void addToLine(PdfChunk pdfChunk) {
        float f;
        if (pdfChunk.changeLeading && pdfChunk.isImage() && (f = pdfChunk.getImage().getScaledHeight() + pdfChunk.getImageOffsetY()) > this.height) {
            this.height = f;
        }
        this.line.add(pdfChunk);
    }

    public int size() {
        return this.line.size();
    }

    public Iterator iterator() {
        return this.line.iterator();
    }

    float height() {
        return this.height;
    }

    float indentLeft() {
        if (this.isRTL) {
            switch (this.alignment) {
                case 0: {
                    return this.left + this.width;
                }
                case 1: {
                    return this.left + this.width / 2.0f;
                }
            }
            return this.left;
        }
        switch (this.alignment) {
            case 2: {
                return this.left + this.width;
            }
            case 1: {
                return this.left + this.width / 2.0f;
            }
        }
        return this.left;
    }

    public boolean hasToBeJustified() {
        return (this.alignment == 3 || this.alignment == 8) && this.width != 0.0f;
    }

    public void resetAlignment() {
        if (this.alignment == 3) {
            this.alignment = 0;
        }
    }

    void setExtraIndent(float f) {
        this.left += f;
        this.width -= f;
    }

    float widthLeft() {
        return this.width;
    }

    int numberOfSpaces() {
        String string = this.toString();
        int n = string.length();
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            if (string.charAt(i) != ' ') continue;
            ++n2;
        }
        return n2;
    }

    public void setListItem(ListItem listItem) {
        this.listSymbol = listItem.getListSymbol();
        this.symbolIndent = listItem.getIndentationLeft();
    }

    public Chunk listSymbol() {
        return this.listSymbol;
    }

    public float listIndent() {
        return this.symbolIndent;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator iterator = this.line.iterator();
        while (iterator.hasNext()) {
            stringBuffer.append(((PdfChunk)iterator.next()).toString());
        }
        return stringBuffer.toString();
    }

    public int GetLineLengthUtf32() {
        int n = 0;
        Iterator iterator = this.line.iterator();
        while (iterator.hasNext()) {
            n += ((PdfChunk)iterator.next()).lengthUtf32();
        }
        return n;
    }

    public boolean isNewlineSplit() {
        return this.newlineSplit && this.alignment != 8;
    }

    public int getLastStrokeChunk() {
        int n;
        PdfChunk pdfChunk;
        for (n = this.line.size() - 1; n >= 0 && !(pdfChunk = (PdfChunk)this.line.get(n)).isStroked(); --n) {
        }
        return n;
    }

    public PdfChunk getChunk(int n) {
        if (n < 0 || n >= this.line.size()) {
            return null;
        }
        return (PdfChunk)this.line.get(n);
    }

    public float getOriginalWidth() {
        return this.originalWidth;
    }

    float getMaxSizeSimple() {
        float f = 0.0f;
        for (int i = 0; i < this.line.size(); ++i) {
            PdfChunk pdfChunk = (PdfChunk)this.line.get(i);
            f = !pdfChunk.isImage() ? Math.max(pdfChunk.font().size(), f) : Math.max(pdfChunk.getImage().getScaledHeight() + pdfChunk.getImageOffsetY(), f);
        }
        return f;
    }

    boolean isRTL() {
        return this.isRTL;
    }

    int getSeparatorCount() {
        int n = 0;
        Iterator iterator = this.line.iterator();
        while (iterator.hasNext()) {
            PdfChunk pdfChunk = (PdfChunk)iterator.next();
            if (pdfChunk.isTab()) {
                return 0;
            }
            if (!pdfChunk.isHorizontalSeparator()) continue;
            ++n;
        }
        return n;
    }

    public float getWidthCorrected(float f, float f2) {
        float f3 = 0.0f;
        for (int i = 0; i < this.line.size(); ++i) {
            PdfChunk pdfChunk = (PdfChunk)this.line.get(i);
            f3 += pdfChunk.getWidthCorrected(f, f2);
        }
        return f3;
    }

    public float getAscender() {
        float f = 0.0f;
        for (int i = 0; i < this.line.size(); ++i) {
            PdfChunk pdfChunk = (PdfChunk)this.line.get(i);
            if (pdfChunk.isImage()) {
                f = Math.max(f, pdfChunk.getImage().getScaledHeight() + pdfChunk.getImageOffsetY());
                continue;
            }
            PdfFont pdfFont = pdfChunk.font();
            f = Math.max(f, pdfFont.getFont().getFontDescriptor(1, pdfFont.size()));
        }
        return f;
    }

    public float getDescender() {
        float f = 0.0f;
        for (int i = 0; i < this.line.size(); ++i) {
            PdfChunk pdfChunk = (PdfChunk)this.line.get(i);
            if (pdfChunk.isImage()) {
                f = Math.min(f, pdfChunk.getImageOffsetY());
                continue;
            }
            PdfFont pdfFont = pdfChunk.font();
            f = Math.min(f, pdfFont.getFont().getFontDescriptor(3, pdfFont.size()));
        }
        return f;
    }
}

