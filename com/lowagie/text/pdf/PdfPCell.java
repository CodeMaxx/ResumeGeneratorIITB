/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.events.PdfPCellEventForwarder;
import java.util.LinkedList;
import java.util.List;

public class PdfPCell
extends Rectangle {
    private ColumnText column = new ColumnText(null);
    private int verticalAlignment = 4;
    private float paddingLeft = 2.0f;
    private float paddingRight = 2.0f;
    private float paddingTop = 2.0f;
    private float paddingBottom = 2.0f;
    private float fixedHeight = 0.0f;
    private boolean noWrap = false;
    private PdfPTable table;
    private float minimumHeight;
    private int colspan = 1;
    private Image image;
    private PdfPCellEvent cellEvent;
    private boolean useDescender;
    private boolean useBorderPadding = false;
    protected Phrase phrase;
    private int rotation;

    public PdfPCell() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.borderWidth = 0.5f;
        this.border = 15;
        this.column.setLeading(0.0f, 1.0f);
    }

    public PdfPCell(Phrase phrase) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.borderWidth = 0.5f;
        this.border = 15;
        this.phrase = phrase;
        this.column.addText(this.phrase);
        this.column.setLeading(0.0f, 1.0f);
    }

    public PdfPCell(Image image) {
        this(image, false);
    }

    public PdfPCell(Image image, boolean bl) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        if (bl) {
            this.borderWidth = 0.5f;
            this.border = 15;
            this.image = image;
            this.column.setLeading(0.0f, 1.0f);
            this.setPadding(this.borderWidth / 2.0f);
        } else {
            this.borderWidth = 0.5f;
            this.border = 15;
            this.phrase = new Phrase(new Chunk(image, 0.0f, 0.0f));
            this.column.addText(this.phrase);
            this.column.setLeading(0.0f, 1.0f);
            this.setPadding(0.0f);
        }
    }

    public PdfPCell(PdfPTable pdfPTable) {
        this(pdfPTable, null);
    }

    public PdfPCell(PdfPTable pdfPTable, PdfPCell pdfPCell) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.borderWidth = 0.5f;
        this.border = 15;
        this.column.setLeading(0.0f, 1.0f);
        this.table = pdfPTable;
        pdfPTable.setWidthPercentage(100.0f);
        pdfPTable.setExtendLastRow(true);
        this.column.addElement(pdfPTable);
        if (pdfPCell != null) {
            this.cloneNonPositionParameters(pdfPCell);
            this.verticalAlignment = pdfPCell.verticalAlignment;
            this.paddingLeft = pdfPCell.paddingLeft;
            this.paddingRight = pdfPCell.paddingRight;
            this.paddingTop = pdfPCell.paddingTop;
            this.paddingBottom = pdfPCell.paddingBottom;
            this.colspan = pdfPCell.colspan;
            this.cellEvent = pdfPCell.cellEvent;
            this.useDescender = pdfPCell.useDescender;
            this.useBorderPadding = pdfPCell.useBorderPadding;
            this.rotation = pdfPCell.rotation;
        } else {
            this.setPadding(0.0f);
        }
    }

    public PdfPCell(PdfPCell pdfPCell) {
        super(pdfPCell.llx, pdfPCell.lly, pdfPCell.urx, pdfPCell.ury);
        this.cloneNonPositionParameters(pdfPCell);
        this.verticalAlignment = pdfPCell.verticalAlignment;
        this.paddingLeft = pdfPCell.paddingLeft;
        this.paddingRight = pdfPCell.paddingRight;
        this.paddingTop = pdfPCell.paddingTop;
        this.paddingBottom = pdfPCell.paddingBottom;
        this.phrase = pdfPCell.phrase;
        this.fixedHeight = pdfPCell.fixedHeight;
        this.minimumHeight = pdfPCell.minimumHeight;
        this.noWrap = pdfPCell.noWrap;
        this.colspan = pdfPCell.colspan;
        if (pdfPCell.table != null) {
            this.table = new PdfPTable(pdfPCell.table);
        }
        this.image = Image.getInstance(pdfPCell.image);
        this.cellEvent = pdfPCell.cellEvent;
        this.useDescender = pdfPCell.useDescender;
        this.column = ColumnText.duplicate(pdfPCell.column);
        this.useBorderPadding = pdfPCell.useBorderPadding;
        this.rotation = pdfPCell.rotation;
    }

    public void addElement(Element element) {
        if (this.table != null) {
            this.table = null;
            this.column.setText(null);
        }
        this.column.addElement(element);
    }

    public Phrase getPhrase() {
        return this.phrase;
    }

    public void setPhrase(Phrase phrase) {
        this.table = null;
        this.image = null;
        this.phrase = phrase;
        this.column.setText(this.phrase);
    }

    public int getHorizontalAlignment() {
        return this.column.getAlignment();
    }

    public void setHorizontalAlignment(int n) {
        this.column.setAlignment(n);
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int n) {
        if (this.table != null) {
            this.table.setExtendLastRow(n == 4);
        }
        this.verticalAlignment = n;
    }

    public float getEffectivePaddingLeft() {
        return this.paddingLeft + (this.isUseBorderPadding() ? this.getBorderWidthLeft() / (this.isUseVariableBorders() ? 1.0f : 2.0f) : 0.0f);
    }

    public float getPaddingLeft() {
        return this.paddingLeft;
    }

    public void setPaddingLeft(float f) {
        this.paddingLeft = f;
    }

    public float getEffectivePaddingRight() {
        return this.paddingRight + (this.isUseBorderPadding() ? this.getBorderWidthRight() / (this.isUseVariableBorders() ? 1.0f : 2.0f) : 0.0f);
    }

    public float getPaddingRight() {
        return this.paddingRight;
    }

    public void setPaddingRight(float f) {
        this.paddingRight = f;
    }

    public float getEffectivePaddingTop() {
        return this.paddingTop + (this.isUseBorderPadding() ? this.getBorderWidthTop() / (this.isUseVariableBorders() ? 1.0f : 2.0f) : 0.0f);
    }

    public float getPaddingTop() {
        return this.paddingTop;
    }

    public void setPaddingTop(float f) {
        this.paddingTop = f;
    }

    public float getEffectivePaddingBottom() {
        return this.paddingBottom + (this.isUseBorderPadding() ? this.getBorderWidthBottom() / (this.isUseVariableBorders() ? 1.0f : 2.0f) : 0.0f);
    }

    public float getPaddingBottom() {
        return this.paddingBottom;
    }

    public void setPaddingBottom(float f) {
        this.paddingBottom = f;
    }

    public void setPadding(float f) {
        this.paddingBottom = f;
        this.paddingTop = f;
        this.paddingLeft = f;
        this.paddingRight = f;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }

    public void setUseBorderPadding(boolean bl) {
        this.useBorderPadding = bl;
    }

    public void setLeading(float f, float f2) {
        this.column.setLeading(f, f2);
    }

    public float getLeading() {
        return this.column.getLeading();
    }

    public float getMultipliedLeading() {
        return this.column.getMultipliedLeading();
    }

    public void setIndent(float f) {
        this.column.setIndent(f);
    }

    public float getIndent() {
        return this.column.getIndent();
    }

    public float getExtraParagraphSpace() {
        return this.column.getExtraParagraphSpace();
    }

    public void setExtraParagraphSpace(float f) {
        this.column.setExtraParagraphSpace(f);
    }

    public float getFixedHeight() {
        return this.fixedHeight;
    }

    public void setFixedHeight(float f) {
        this.fixedHeight = f;
        this.minimumHeight = 0.0f;
    }

    public boolean isNoWrap() {
        return this.noWrap;
    }

    public void setNoWrap(boolean bl) {
        this.noWrap = bl;
    }

    public PdfPTable getTable() {
        return this.table;
    }

    void setTable(PdfPTable pdfPTable) {
        this.table = pdfPTable;
        this.column.setText(null);
        this.image = null;
        if (pdfPTable != null) {
            pdfPTable.setExtendLastRow(this.verticalAlignment == 4);
            this.column.addElement(pdfPTable);
            pdfPTable.setWidthPercentage(100.0f);
        }
    }

    public float getMinimumHeight() {
        return this.minimumHeight;
    }

    public void setMinimumHeight(float f) {
        this.minimumHeight = f;
        this.fixedHeight = 0.0f;
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setColspan(int n) {
        this.colspan = n;
    }

    public void setFollowingIndent(float f) {
        this.column.setFollowingIndent(f);
    }

    public float getFollowingIndent() {
        return this.column.getFollowingIndent();
    }

    public void setRightIndent(float f) {
        this.column.setRightIndent(f);
    }

    public float getRightIndent() {
        return this.column.getRightIndent();
    }

    public float getSpaceCharRatio() {
        return this.column.getSpaceCharRatio();
    }

    public void setSpaceCharRatio(float f) {
        this.column.setSpaceCharRatio(f);
    }

    public void setRunDirection(int n) {
        this.column.setRunDirection(n);
    }

    public int getRunDirection() {
        return this.column.getRunDirection();
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.column.setText(null);
        this.table = null;
        this.image = image;
    }

    public PdfPCellEvent getCellEvent() {
        return this.cellEvent;
    }

    public void setCellEvent(PdfPCellEvent pdfPCellEvent) {
        if (pdfPCellEvent == null) {
            this.cellEvent = null;
        } else if (this.cellEvent == null) {
            this.cellEvent = pdfPCellEvent;
        } else if (this.cellEvent instanceof PdfPCellEventForwarder) {
            ((PdfPCellEventForwarder)this.cellEvent).addCellEvent(pdfPCellEvent);
        } else {
            PdfPCellEventForwarder pdfPCellEventForwarder = new PdfPCellEventForwarder();
            pdfPCellEventForwarder.addCellEvent(this.cellEvent);
            pdfPCellEventForwarder.addCellEvent(pdfPCellEvent);
            this.cellEvent = pdfPCellEventForwarder;
        }
    }

    public int getArabicOptions() {
        return this.column.getArabicOptions();
    }

    public void setArabicOptions(int n) {
        this.column.setArabicOptions(n);
    }

    public boolean isUseAscender() {
        return this.column.isUseAscender();
    }

    public void setUseAscender(boolean bl) {
        this.column.setUseAscender(bl);
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseDescender(boolean bl) {
        this.useDescender = bl;
    }

    public ColumnText getColumn() {
        return this.column;
    }

    public List getCompositeElements() {
        return this.getColumn().compositeElements;
    }

    public void setColumn(ColumnText columnText) {
        this.column = columnText;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int n) {
        if ((n %= 360) < 0) {
            n += 360;
        }
        if (n % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be a multiple of 90.");
        }
        this.rotation = n;
    }
}

