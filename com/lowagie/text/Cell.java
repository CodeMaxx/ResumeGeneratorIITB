/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.ElementTags;
import com.lowagie.text.List;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Cell
extends Rectangle
implements TextElementArray {
    protected ArrayList arrayList = null;
    protected int horizontalAlignment = -1;
    protected int verticalAlignment = -1;
    protected float width;
    protected boolean percentage = false;
    protected int colspan = 1;
    protected int rowspan = 1;
    float leading = Float.NaN;
    protected boolean header;
    protected int maxLines = Integer.MAX_VALUE;
    String showTruncation;
    protected boolean useAscender = false;
    protected boolean useDescender = false;
    protected boolean useBorderPadding;
    protected boolean groupChange = true;

    public Cell() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.setBorder(-1);
        this.setBorderWidth(0.5f);
        this.arrayList = new ArrayList();
    }

    public Cell(boolean bl) {
        this();
        this.arrayList.add(new Paragraph(0.0f));
    }

    public Cell(String string) {
        this();
        try {
            this.addElement(new Paragraph(string));
        }
        catch (BadElementException var2_2) {
            // empty catch block
        }
    }

    public Cell(Element element) throws BadElementException {
        this();
        if (element instanceof Phrase) {
            this.setLeading(((Phrase)element).getLeading());
        }
        this.addElement(element);
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
        return 20;
    }

    public ArrayList getChunks() {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.arrayList.iterator();
        while (iterator.hasNext()) {
            arrayList.addAll(((Element)iterator.next()).getChunks());
        }
        return arrayList;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int n) {
        this.horizontalAlignment = n;
    }

    public void setHorizontalAlignment(String string) {
        this.setHorizontalAlignment(ElementTags.alignmentValue(string));
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int n) {
        this.verticalAlignment = n;
    }

    public void setVerticalAlignment(String string) {
        this.setVerticalAlignment(ElementTags.alignmentValue(string));
    }

    public void setWidth(float f) {
        this.width = f;
    }

    public void setWidth(String string) {
        if (string.endsWith("%")) {
            string = string.substring(0, string.length() - 1);
            this.percentage = true;
        }
        this.width = Integer.parseInt(string);
    }

    public float getWidth() {
        return this.width;
    }

    public String getWidthAsString() {
        String string = String.valueOf(this.width);
        if (string.endsWith(".0")) {
            string = string.substring(0, string.length() - 2);
        }
        if (this.percentage) {
            string = string + "%";
        }
        return string;
    }

    public void setColspan(int n) {
        this.colspan = n;
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setRowspan(int n) {
        this.rowspan = n;
    }

    public int getRowspan() {
        return this.rowspan;
    }

    public void setLeading(float f) {
        this.leading = f;
    }

    public float getLeading() {
        if (Float.isNaN(this.leading)) {
            return 16.0f;
        }
        return this.leading;
    }

    public void setHeader(boolean bl) {
        this.header = bl;
    }

    public boolean isHeader() {
        return this.header;
    }

    public void setMaxLines(int n) {
        this.maxLines = n;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public void setShowTruncation(String string) {
        this.showTruncation = string;
    }

    public String getShowTruncation() {
        return this.showTruncation;
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

    public boolean getGroupChange() {
        return this.groupChange;
    }

    public void setGroupChange(boolean bl) {
        this.groupChange = bl;
    }

    public int size() {
        return this.arrayList.size();
    }

    public Iterator getElements() {
        return this.arrayList.iterator();
    }

    public void clear() {
        this.arrayList.clear();
    }

    public boolean isEmpty() {
        switch (this.size()) {
            case 0: {
                return true;
            }
            case 1: {
                Element element = (Element)this.arrayList.get(0);
                switch (element.type()) {
                    case 10: {
                        return ((Chunk)element).isEmpty();
                    }
                    case 11: 
                    case 12: 
                    case 17: {
                        return ((Phrase)element).isEmpty();
                    }
                    case 14: {
                        return ((List)element).isEmpty();
                    }
                }
                return false;
            }
        }
        return false;
    }

    void fill() {
        if (this.size() == 0) {
            this.arrayList.add(new Paragraph(0.0f));
        }
    }

    public boolean isTable() {
        return this.size() == 1 && ((Element)this.arrayList.get(0)).type() == 22;
    }

    public void addElement(Element element) throws BadElementException {
        if (this.isTable()) {
            Table table = (Table)this.arrayList.get(0);
            Cell cell = new Cell(element);
            cell.setBorder(0);
            cell.setColspan(table.getColumns());
            table.addCell(cell);
            return;
        }
        switch (element.type()) {
            case 15: 
            case 20: 
            case 21: {
                throw new BadElementException("You can't add listitems, rows or cells to a cell.");
            }
            case 14: {
                List list = (List)element;
                if (Float.isNaN(this.leading)) {
                    this.setLeading(list.getTotalLeading());
                }
                if (list.isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 11: 
            case 12: 
            case 17: {
                Phrase phrase = (Phrase)element;
                if (Float.isNaN(this.leading)) {
                    this.setLeading(phrase.getLeading());
                }
                if (phrase.isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 10: {
                if (((Chunk)element).isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 22: {
                Cell cell;
                Table table = new Table(3);
                float[] arrf = new float[3];
                arrf[1] = ((Table)element).getWidth();
                switch (((Table)element).getAlignment()) {
                    case 0: {
                        arrf[0] = 0.0f;
                        arrf[2] = 100.0f - arrf[1];
                        break;
                    }
                    case 1: {
                        arrf[0] = (100.0f - arrf[1]) / 2.0f;
                        arrf[2] = arrf[0];
                        break;
                    }
                    case 2: {
                        arrf[0] = 100.0f - arrf[1];
                        arrf[2] = 0.0f;
                    }
                }
                table.setWidths(arrf);
                if (this.arrayList.isEmpty()) {
                    table.addCell(Cell.getDummyCell());
                } else {
                    cell = new Cell();
                    cell.setBorder(0);
                    cell.setColspan(3);
                    Iterator iterator = this.arrayList.iterator();
                    while (iterator.hasNext()) {
                        cell.add(iterator.next());
                    }
                    table.addCell(cell);
                }
                cell = new Cell();
                cell.setBorder(0);
                table.addCell(cell);
                table.insertTable((Table)element);
                cell = new Cell();
                cell.setBorder(0);
                table.addCell(cell);
                table.addCell(Cell.getDummyCell());
                this.clear();
                this.arrayList.add(table);
                return;
            }
        }
        this.arrayList.add(element);
    }

    public boolean add(Object object) {
        try {
            this.addElement((Element)object);
            return true;
        }
        catch (ClassCastException var2_2) {
            throw new ClassCastException("You can only add objects that implement the Element interface.");
        }
        catch (BadElementException var2_3) {
            throw new ClassCastException(var2_3.getMessage());
        }
    }

    private static Cell getDummyCell() {
        Cell cell = new Cell(true);
        cell.setColspan(3);
        cell.setBorder(0);
        return cell;
    }

    public PdfPCell createPdfPCell() throws BadElementException {
        if (this.rowspan > 1) {
            throw new BadElementException("PdfPCells can't have a rowspan > 1");
        }
        if (this.isTable()) {
            return new PdfPCell(((Table)this.arrayList.get(0)).createPdfPTable());
        }
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setVerticalAlignment(this.verticalAlignment);
        pdfPCell.setHorizontalAlignment(this.horizontalAlignment);
        pdfPCell.setColspan(this.colspan);
        pdfPCell.setUseBorderPadding(this.useBorderPadding);
        pdfPCell.setUseDescender(this.useDescender);
        pdfPCell.setLeading(this.getLeading(), 0.0f);
        pdfPCell.cloneNonPositionParameters(this);
        pdfPCell.setNoWrap(this.getMaxLines() == 1);
        Iterator iterator = this.getElements();
        while (iterator.hasNext()) {
            Element element = (Element)iterator.next();
            if (element.type() == 11 || element.type() == 12) {
                Paragraph paragraph = new Paragraph((Phrase)element);
                paragraph.setAlignment(this.horizontalAlignment);
                element = paragraph;
            }
            pdfPCell.addElement(element);
        }
        return pdfPCell;
    }

    public float getTop() {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public float getBottom() {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public float getLeft() {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public float getRight() {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public float top(int n) {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public float bottom(int n) {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public float left(int n) {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public float right(int n) {
        throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
    }

    public void setTop(int n) {
        throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
    }

    public void setBottom(int n) {
        throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
    }

    public void setLeft(int n) {
        throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
    }

    public void setRight(int n) {
        throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
    }
}

