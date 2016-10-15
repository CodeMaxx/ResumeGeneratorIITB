/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleCell
extends Rectangle
implements PdfPCellEvent,
TextElementArray {
    public static final boolean ROW = true;
    public static final boolean CELL = false;
    private ArrayList content = new ArrayList();
    private float width = 0.0f;
    private float widthpercentage = 0.0f;
    private float spacing_left = Float.NaN;
    private float spacing_right = Float.NaN;
    private float spacing_top = Float.NaN;
    private float spacing_bottom = Float.NaN;
    private float padding_left = Float.NaN;
    private float padding_right = Float.NaN;
    private float padding_top = Float.NaN;
    private float padding_bottom = Float.NaN;
    private int colspan = 1;
    private int horizontalAlignment = -1;
    private int verticalAlignment = -1;
    private boolean cellgroup = false;
    protected boolean useAscender = false;
    protected boolean useDescender = false;
    protected boolean useBorderPadding;

    public SimpleCell(boolean bl) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.cellgroup = bl;
        this.setBorder(15);
    }

    public void addElement(Element element) throws BadElementException {
        if (this.cellgroup) {
            if (element instanceof SimpleCell) {
                if (((SimpleCell)element).isCellgroup()) {
                    throw new BadElementException("You can't add one row to another row.");
                }
                this.content.add(element);
                return;
            }
            throw new BadElementException("You can only add cells to rows, no objects of type " + element.getClass().getName());
        }
        if (element.type() != 12 && element.type() != 11 && element.type() != 17 && element.type() != 10 && element.type() != 14 && element.type() != 50 && element.type() != 32 && element.type() != 33 && element.type() != 34 && element.type() != 35) {
            throw new BadElementException("You can't add an element of type " + element.getClass().getName() + " to a SimpleCell.");
        }
        this.content.add(element);
    }

    public Cell createCell(SimpleCell simpleCell) throws BadElementException {
        Cell cell = new Cell();
        cell.cloneNonPositionParameters(simpleCell);
        cell.softCloneNonPositionParameters(this);
        cell.setColspan(this.colspan);
        cell.setHorizontalAlignment(this.horizontalAlignment);
        cell.setVerticalAlignment(this.verticalAlignment);
        cell.setUseAscender(this.useAscender);
        cell.setUseBorderPadding(this.useBorderPadding);
        cell.setUseDescender(this.useDescender);
        Iterator iterator = this.content.iterator();
        while (iterator.hasNext()) {
            Element element = (Element)iterator.next();
            cell.addElement(element);
        }
        return cell;
    }

    public PdfPCell createPdfPCell(SimpleCell simpleCell) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setBorder(0);
        SimpleCell simpleCell2 = new SimpleCell(false);
        simpleCell2.setSpacing_left(this.spacing_left);
        simpleCell2.setSpacing_right(this.spacing_right);
        simpleCell2.setSpacing_top(this.spacing_top);
        simpleCell2.setSpacing_bottom(this.spacing_bottom);
        simpleCell2.cloneNonPositionParameters(simpleCell);
        simpleCell2.softCloneNonPositionParameters(this);
        pdfPCell.setCellEvent(simpleCell2);
        pdfPCell.setHorizontalAlignment(simpleCell.horizontalAlignment);
        pdfPCell.setVerticalAlignment(simpleCell.verticalAlignment);
        pdfPCell.setUseAscender(simpleCell.useAscender);
        pdfPCell.setUseBorderPadding(simpleCell.useBorderPadding);
        pdfPCell.setUseDescender(simpleCell.useDescender);
        pdfPCell.setColspan(this.colspan);
        if (this.horizontalAlignment != -1) {
            pdfPCell.setHorizontalAlignment(this.horizontalAlignment);
        }
        if (this.verticalAlignment != -1) {
            pdfPCell.setVerticalAlignment(this.verticalAlignment);
        }
        if (this.useAscender) {
            pdfPCell.setUseAscender(this.useAscender);
        }
        if (this.useBorderPadding) {
            pdfPCell.setUseBorderPadding(this.useBorderPadding);
        }
        if (this.useDescender) {
            pdfPCell.setUseDescender(this.useDescender);
        }
        if (Float.isNaN(f4 = this.spacing_left)) {
            f4 = 0.0f;
        }
        if (Float.isNaN(f2 = this.spacing_right)) {
            f2 = 0.0f;
        }
        if (Float.isNaN(f5 = this.spacing_top)) {
            f5 = 0.0f;
        }
        if (Float.isNaN(f3 = this.spacing_bottom)) {
            f3 = 0.0f;
        }
        if (Float.isNaN(f = this.padding_left)) {
            f = 0.0f;
        }
        pdfPCell.setPaddingLeft(f + f4);
        f = this.padding_right;
        if (Float.isNaN(f)) {
            f = 0.0f;
        }
        pdfPCell.setPaddingRight(f + f2);
        f = this.padding_top;
        if (Float.isNaN(f)) {
            f = 0.0f;
        }
        pdfPCell.setPaddingTop(f + f5);
        f = this.padding_bottom;
        if (Float.isNaN(f)) {
            f = 0.0f;
        }
        pdfPCell.setPaddingBottom(f + f3);
        Iterator iterator = this.content.iterator();
        while (iterator.hasNext()) {
            Element element = (Element)iterator.next();
            pdfPCell.addElement(element);
        }
        return pdfPCell;
    }

    public static SimpleCell getDimensionlessInstance(Rectangle rectangle, float f) {
        SimpleCell simpleCell = new SimpleCell(false);
        simpleCell.cloneNonPositionParameters(rectangle);
        simpleCell.setSpacing(f * 2.0f);
        return simpleCell;
    }

    public void cellLayout(PdfPCell pdfPCell, Rectangle rectangle, PdfContentByte[] arrpdfContentByte) {
        float f;
        float f2;
        float f3;
        float f4 = this.spacing_left;
        if (Float.isNaN(f4)) {
            f4 = 0.0f;
        }
        if (Float.isNaN(f2 = this.spacing_right)) {
            f2 = 0.0f;
        }
        if (Float.isNaN(f = this.spacing_top)) {
            f = 0.0f;
        }
        if (Float.isNaN(f3 = this.spacing_bottom)) {
            f3 = 0.0f;
        }
        Rectangle rectangle2 = new Rectangle(rectangle.getLeft(f4), rectangle.getBottom(f3), rectangle.getRight(f2), rectangle.getTop(f));
        rectangle2.cloneNonPositionParameters(this);
        arrpdfContentByte[1].rectangle(rectangle2);
        rectangle2.setBackgroundColor(null);
        arrpdfContentByte[2].rectangle(rectangle2);
    }

    public void setPadding(float f) {
        if (Float.isNaN(this.padding_right)) {
            this.setPadding_right(f);
        }
        if (Float.isNaN(this.padding_left)) {
            this.setPadding_left(f);
        }
        if (Float.isNaN(this.padding_top)) {
            this.setPadding_top(f);
        }
        if (Float.isNaN(this.padding_bottom)) {
            this.setPadding_bottom(f);
        }
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setColspan(int n) {
        if (n > 0) {
            this.colspan = n;
        }
    }

    public float getPadding_bottom() {
        return this.padding_bottom;
    }

    public void setPadding_bottom(float f) {
        this.padding_bottom = f;
    }

    public float getPadding_left() {
        return this.padding_left;
    }

    public void setPadding_left(float f) {
        this.padding_left = f;
    }

    public float getPadding_right() {
        return this.padding_right;
    }

    public void setPadding_right(float f) {
        this.padding_right = f;
    }

    public float getPadding_top() {
        return this.padding_top;
    }

    public void setPadding_top(float f) {
        this.padding_top = f;
    }

    public float getSpacing_left() {
        return this.spacing_left;
    }

    public float getSpacing_right() {
        return this.spacing_right;
    }

    public float getSpacing_top() {
        return this.spacing_top;
    }

    public float getSpacing_bottom() {
        return this.spacing_bottom;
    }

    public void setSpacing(float f) {
        this.spacing_left = f;
        this.spacing_right = f;
        this.spacing_top = f;
        this.spacing_bottom = f;
    }

    public void setSpacing_left(float f) {
        this.spacing_left = f;
    }

    public void setSpacing_right(float f) {
        this.spacing_right = f;
    }

    public void setSpacing_top(float f) {
        this.spacing_top = f;
    }

    public void setSpacing_bottom(float f) {
        this.spacing_bottom = f;
    }

    public boolean isCellgroup() {
        return this.cellgroup;
    }

    public void setCellgroup(boolean bl) {
        this.cellgroup = bl;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int n) {
        this.horizontalAlignment = n;
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int n) {
        this.verticalAlignment = n;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float f) {
        this.width = f;
    }

    public float getWidthpercentage() {
        return this.widthpercentage;
    }

    public void setWidthpercentage(float f) {
        this.widthpercentage = f;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseAscender(boolean bl) {
        this.useAscender = bl;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }

    public void setUseBorderPadding(boolean bl) {
        this.useBorderPadding = bl;
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseDescender(boolean bl) {
        this.useDescender = bl;
    }

    ArrayList getContent() {
        return this.content;
    }

    public boolean add(Object object) {
        try {
            this.addElement((Element)object);
            return true;
        }
        catch (ClassCastException var2_2) {
            return false;
        }
        catch (BadElementException var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    public int type() {
        return 20;
    }
}

