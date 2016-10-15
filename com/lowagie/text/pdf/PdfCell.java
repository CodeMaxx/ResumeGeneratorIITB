/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfLine;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfCell
extends Rectangle {
    private ArrayList lines;
    private PdfLine line;
    private ArrayList images;
    private float leading;
    private int rownumber;
    private int rowspan;
    private float cellspacing;
    private float cellpadding;
    private boolean header = false;
    private float contentHeight = 0.0f;
    private boolean useAscender;
    private boolean useDescender;
    private boolean useBorderPadding;
    private int verticalAlignment;
    private PdfLine firstLine;
    private PdfLine lastLine;
    private int groupNumber;

    public PdfCell(Cell cell, int n, float f, float f2, float f3, float f4, float f5) {
        float f6;
        super(f, f3, f2, f3);
        this.cloneNonPositionParameters(cell);
        this.cellpadding = f5;
        this.cellspacing = f4;
        this.verticalAlignment = cell.getVerticalAlignment();
        this.useAscender = cell.isUseAscender();
        this.useDescender = cell.isUseDescender();
        this.useBorderPadding = cell.isUseBorderPadding();
        this.lines = new ArrayList();
        this.images = new ArrayList();
        this.leading = cell.getLeading();
        int n2 = cell.getHorizontalAlignment();
        f += f4 + f5;
        f2 -= f4 + f5;
        f += this.getBorderWidthInside(4);
        f2 -= this.getBorderWidthInside(8);
        this.contentHeight = 0.0f;
        this.rowspan = cell.getRowspan();
        Object object = cell.getElements();
        block7 : while (object.hasNext()) {
            ArrayList arrayList;
            Element element = (Element)object.next();
            switch (element.type()) {
                case 32: 
                case 33: 
                case 34: 
                case 35: {
                    this.addImage((Image)element, f, f2, 0.4f * this.leading, n2);
                    continue block7;
                }
                case 14: {
                    if (this.line != null && this.line.size() > 0) {
                        this.line.resetAlignment();
                        this.addLine(this.line);
                    }
                    this.addList((List)element, f, f2, n2);
                    this.line = new PdfLine(f, f2, n2, this.leading);
                    continue block7;
                }
            }
            ArrayList arrayList2 = new ArrayList();
            this.processActions(element, null, arrayList2);
            int n3 = 0;
            float f7 = this.leading;
            f6 = f;
            float f8 = f2;
            if (element instanceof Phrase) {
                f7 = ((Phrase)element).getLeading();
            }
            if (element instanceof Paragraph) {
                arrayList = (Paragraph)element;
                f6 += arrayList.getIndentationLeft();
                f8 -= arrayList.getIndentationRight();
            }
            if (this.line == null) {
                this.line = new PdfLine(f6, f8, n2, f7);
            }
            if ((arrayList = element.getChunks()).isEmpty()) {
                this.addLine(this.line);
                this.line = new PdfLine(f6, f8, n2, f7);
            } else {
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext()) {
                    PdfChunk pdfChunk;
                    Chunk chunk = (Chunk)iterator.next();
                    PdfChunk pdfChunk2 = new PdfChunk(chunk, (PdfAction)arrayList2.get(n3++));
                    while ((pdfChunk = this.line.add(pdfChunk2)) != null) {
                        this.addLine(this.line);
                        this.line = new PdfLine(f6, f8, n2, f7);
                        pdfChunk2 = pdfChunk;
                    }
                }
            }
            switch (element.type()) {
                case 12: 
                case 13: 
                case 16: {
                    this.line.resetAlignment();
                    this.flushCurrentLine();
                }
            }
        }
        this.flushCurrentLine();
        if (this.lines.size() > cell.getMaxLines()) {
            while (this.lines.size() > cell.getMaxLines()) {
                this.removeLine(this.lines.size() - 1);
            }
            if (cell.getMaxLines() > 0 && (object = cell.getShowTruncation()) != null && object.length() > 0) {
                this.lastLine = (PdfLine)this.lines.get(this.lines.size() - 1);
                if (this.lastLine.size() >= 0) {
                    PdfChunk pdfChunk = this.lastLine.getChunk(this.lastLine.size() - 1);
                    f6 = new PdfChunk((String)object, pdfChunk).width();
                    while (pdfChunk.toString().length() > 0 && pdfChunk.width() + f6 > f2 - f) {
                        pdfChunk.setValue(pdfChunk.toString().substring(0, pdfChunk.length() - 1));
                    }
                    pdfChunk.setValue(pdfChunk.toString() + (String)object);
                } else {
                    this.lastLine.add(new PdfChunk(new Chunk((String)object), null));
                }
            }
        }
        if (this.useDescender && this.lastLine != null) {
            this.contentHeight -= this.lastLine.getDescender();
        }
        if (!this.lines.isEmpty()) {
            this.firstLine = (PdfLine)this.lines.get(0);
            float f9 = this.firstLineRealHeight();
            this.contentHeight -= this.firstLine.height();
            this.firstLine.height = f9;
            this.contentHeight += f9;
        }
        float f10 = f3 - this.contentHeight - 2.0f * this.cellpadding() - 2.0f * this.cellspacing();
        this.setBottom(f10 -= this.getBorderWidthInside(1) + this.getBorderWidthInside(2));
        this.rownumber = n;
    }

    private void addList(List list, float f, float f2, int n) {
        ArrayList arrayList = new ArrayList();
        this.processActions(list, null, arrayList);
        int n2 = 0;
        Iterator iterator = list.getItems().iterator();
        while (iterator.hasNext()) {
            Element element = (Element)iterator.next();
            switch (element.type()) {
                Object object;
                case 15: {
                    ListItem listItem = (ListItem)element;
                    this.line = new PdfLine(f + listItem.getIndentationLeft(), f2, n, listItem.getLeading());
                    this.line.setListItem(listItem);
                    object = listItem.getChunks().iterator();
                    while (object.hasNext()) {
                        PdfChunk pdfChunk;
                        PdfChunk pdfChunk2 = new PdfChunk((Chunk)object.next(), (PdfAction)arrayList.get(n2++));
                        while ((pdfChunk = this.line.add(pdfChunk2)) != null) {
                            this.addLine(this.line);
                            this.line = new PdfLine(f + listItem.getIndentationLeft(), f2, n, listItem.getLeading());
                            pdfChunk2 = pdfChunk;
                        }
                        this.line.resetAlignment();
                        this.addLine(this.line);
                        this.line = new PdfLine(f + listItem.getIndentationLeft(), f2, n, this.leading);
                    }
                    break;
                }
                case 14: {
                    object = (List)element;
                    this.addList((List)object, f + object.getIndentationLeft(), f2, n);
                }
            }
        }
    }

    public void setBottom(float f) {
        super.setBottom(f);
        float f2 = this.firstLineRealHeight();
        float f3 = this.ury - f;
        float f4 = this.cellpadding() * 2.0f + this.cellspacing() * 2.0f;
        float f5 = f3 - (f4 += this.getBorderWidthInside(1) + this.getBorderWidthInside(2));
        float f6 = 0.0f;
        switch (this.verticalAlignment) {
            case 6: {
                f6 = f5 - this.contentHeight;
                break;
            }
            case 5: {
                f6 = (f5 - this.contentHeight) / 2.0f;
                break;
            }
            default: {
                f6 = 0.0f;
            }
        }
        f6 += this.cellpadding() + this.cellspacing();
        f6 += this.getBorderWidthInside(1);
        if (this.firstLine != null) {
            this.firstLine.height = f2 + f6;
        }
    }

    public float getLeft() {
        return super.getLeft(this.cellspacing);
    }

    public float getRight() {
        return super.getRight(this.cellspacing);
    }

    public float getTop() {
        return super.getTop(this.cellspacing);
    }

    public float getBottom() {
        return super.getBottom(this.cellspacing);
    }

    private void addLine(PdfLine pdfLine) {
        this.lines.add(pdfLine);
        this.contentHeight += pdfLine.height();
        this.lastLine = pdfLine;
        this.line = null;
    }

    private PdfLine removeLine(int n) {
        PdfLine pdfLine = (PdfLine)this.lines.remove(n);
        this.contentHeight -= pdfLine.height();
        if (n == 0 && !this.lines.isEmpty()) {
            this.firstLine = (PdfLine)this.lines.get(0);
            float f = this.firstLineRealHeight();
            this.contentHeight -= this.firstLine.height();
            this.firstLine.height = f;
            this.contentHeight += f;
        }
        return pdfLine;
    }

    private void flushCurrentLine() {
        if (this.line != null && this.line.size() > 0) {
            this.addLine(this.line);
        }
    }

    private float firstLineRealHeight() {
        PdfChunk pdfChunk;
        float f = 0.0f;
        if (this.firstLine != null && (pdfChunk = this.firstLine.getChunk(0)) != null) {
            Image image = pdfChunk.getImage();
            f = image != null ? this.firstLine.getChunk(0).getImage().getScaledHeight() : (this.useAscender ? this.firstLine.getAscender() : this.leading);
        }
        return f;
    }

    private float getBorderWidthInside(int n) {
        float f = 0.0f;
        if (this.useBorderPadding) {
            switch (n) {
                case 4: {
                    f = this.getBorderWidthLeft();
                    break;
                }
                case 8: {
                    f = this.getBorderWidthRight();
                    break;
                }
                case 1: {
                    f = this.getBorderWidthTop();
                    break;
                }
                default: {
                    f = this.getBorderWidthBottom();
                }
            }
            if (!this.isUseVariableBorders()) {
                f /= 2.0f;
            }
        }
        return f;
    }

    private float addImage(Image image, float f, float f2, float f3, int n) {
        Image image2 = Image.getInstance(image);
        if (image2.getScaledWidth() > f2 - f) {
            image2.scaleToFit(f2 - f, Float.MAX_VALUE);
        }
        this.flushCurrentLine();
        if (this.line == null) {
            this.line = new PdfLine(f, f2, n, this.leading);
        }
        PdfLine pdfLine = this.line;
        f2 -= f;
        f = 0.0f;
        if ((image2.getAlignment() & 2) == 2) {
            f = f2 - image2.getScaledWidth();
        } else if ((image2.getAlignment() & 1) == 1) {
            f += (f2 - f - image2.getScaledWidth()) / 2.0f;
        }
        Chunk chunk = new Chunk(image2, f, 0.0f);
        pdfLine.add(new PdfChunk(chunk, null));
        this.addLine(pdfLine);
        return pdfLine.height();
    }

    public ArrayList getLines(float f, float f2) {
        float f3 = Math.min(this.getTop(), f);
        this.setTop(f3 + this.cellspacing);
        ArrayList<PdfLine> arrayList = new ArrayList<PdfLine>();
        if (this.getTop() < f2) {
            return arrayList;
        }
        int n = this.lines.size();
        boolean bl = true;
        for (int i = 0; i < n && bl; ++i) {
            this.line = (PdfLine)this.lines.get(i);
            float f4 = this.line.height();
            if ((f3 -= f4) > f2 + this.cellpadding + this.getBorderWidthInside(2)) {
                arrayList.add(this.line);
                continue;
            }
            bl = false;
        }
        float f5 = 0.0f;
        if (!this.header) {
            if (bl) {
                this.lines = new ArrayList();
                this.contentHeight = 0.0f;
            } else {
                n = arrayList.size();
                for (int j = 0; j < n; ++j) {
                    this.line = this.removeLine(0);
                    f5 += this.line.height();
                }
            }
        }
        if (f5 > 0.0f) {
            Iterator iterator = this.images.iterator();
            while (iterator.hasNext()) {
                Image image = (Image)iterator.next();
                image.setAbsolutePosition(image.getAbsoluteX(), image.getAbsoluteY() - f5 - this.leading);
            }
        }
        return arrayList;
    }

    public ArrayList getImages(float f, float f2) {
        if (this.getTop() < f2) {
            return new ArrayList();
        }
        f = Math.min(this.getTop(), f);
        ArrayList<Image> arrayList = new ArrayList<Image>();
        Iterator iterator = this.images.iterator();
        while (iterator.hasNext() && !this.header) {
            Image image = (Image)iterator.next();
            float f3 = image.getAbsoluteY();
            if (f - f3 <= f2 + this.cellpadding) continue;
            image.setAbsolutePosition(image.getAbsoluteX(), f - f3);
            arrayList.add(image);
            iterator.remove();
        }
        return arrayList;
    }

    boolean isHeader() {
        return this.header;
    }

    void setHeader() {
        this.header = true;
    }

    boolean mayBeRemoved() {
        return this.header || this.lines.isEmpty() && this.images.isEmpty();
    }

    public int size() {
        return this.lines.size();
    }

    private float remainingLinesHeight() {
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        float f = 0.0f;
        int n = this.lines.size();
        for (int i = 0; i < n; ++i) {
            PdfLine pdfLine = (PdfLine)this.lines.get(i);
            f += pdfLine.height();
        }
        return f;
    }

    public float remainingHeight() {
        float f = 0.0f;
        Iterator iterator = this.images.iterator();
        while (iterator.hasNext()) {
            Image image = (Image)iterator.next();
            f += image.getScaledHeight();
        }
        return this.remainingLinesHeight() + this.cellspacing + 2.0f * this.cellpadding + f;
    }

    public float leading() {
        return this.leading;
    }

    public int rownumber() {
        return this.rownumber;
    }

    public int rowspan() {
        return this.rowspan;
    }

    public float cellspacing() {
        return this.cellspacing;
    }

    public float cellpadding() {
        return this.cellpadding;
    }

    protected void processActions(Element element, PdfAction pdfAction, ArrayList arrayList) {
        Iterator iterator;
        if (element.type() == 17 && (iterator = ((Anchor)element).getReference()) != null) {
            pdfAction = new PdfAction((String)((Object)iterator));
        }
        switch (element.type()) {
            case 11: 
            case 12: 
            case 13: 
            case 15: 
            case 16: 
            case 17: {
                iterator = ((ArrayList)((Object)element)).iterator();
                while (iterator.hasNext()) {
                    this.processActions((Element)iterator.next(), pdfAction, arrayList);
                }
                break;
            }
            case 10: {
                arrayList.add(pdfAction);
                break;
            }
            case 14: {
                iterator = ((List)element).getItems().iterator();
                while (iterator.hasNext()) {
                    this.processActions((Element)iterator.next(), pdfAction, arrayList);
                }
                break;
            }
            default: {
                int n = element.getChunks().size();
                while (n-- > 0) {
                    arrayList.add(pdfAction);
                }
                break block0;
            }
        }
    }

    public int getGroupNumber() {
        return this.groupNumber;
    }

    void setGroupNumber(int n) {
        this.groupNumber = n;
    }

    public Rectangle rectangle(float f, float f2) {
        Rectangle rectangle = new Rectangle(this.getLeft(), this.getBottom(), this.getRight(), this.getTop());
        rectangle.cloneNonPositionParameters(this);
        if (this.getTop() > f) {
            rectangle.setTop(f);
            rectangle.setBorder(this.border - (this.border & 1));
        }
        if (this.getBottom() < f2) {
            rectangle.setBottom(f2);
            rectangle.setBorder(this.border - (this.border & 2));
        }
        return rectangle;
    }

    public void setUseAscender(boolean bl) {
        this.useAscender = bl;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseDescender(boolean bl) {
        this.useDescender = bl;
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseBorderPadding(boolean bl) {
        this.useBorderPadding = bl;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }
}

