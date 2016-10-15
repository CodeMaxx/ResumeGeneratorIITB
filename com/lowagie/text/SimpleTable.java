/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.SimpleCell;
import com.lowagie.text.Table;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleTable
extends Rectangle
implements PdfPTableEvent,
TextElementArray {
    private ArrayList content = new ArrayList();
    private float width = 0.0f;
    private float widthpercentage = 0.0f;
    private float cellspacing;
    private float cellpadding;
    private int alignment;

    public SimpleTable() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.setBorder(15);
        this.setBorderWidth(2.0f);
    }

    public void addElement(SimpleCell simpleCell) throws BadElementException {
        if (!simpleCell.isCellgroup()) {
            throw new BadElementException("You can't add cells to a table directly, add them to a row first.");
        }
        this.content.add(simpleCell);
    }

    public Table createTable() throws BadElementException {
        int n;
        SimpleCell simpleCell;
        if (this.content.isEmpty()) {
            throw new BadElementException("Trying to create a table without rows.");
        }
        SimpleCell simpleCell2 = (SimpleCell)this.content.get(0);
        int n2 = 0;
        float[] arrf = simpleCell2.getContent().iterator();
        while (arrf.hasNext()) {
            simpleCell = (SimpleCell)arrf.next();
            n2 += simpleCell.getColspan();
        }
        arrf = new float[n2];
        float[] arrf2 = new float[n2];
        Table table = new Table(n2);
        table.setAlignment(this.alignment);
        table.setSpacing(this.cellspacing);
        table.setPadding(this.cellpadding);
        table.cloneNonPositionParameters(this);
        Iterator iterator = this.content.iterator();
        while (iterator.hasNext()) {
            simpleCell2 = (SimpleCell)iterator.next();
            int n3 = 0;
            Iterator iterator2 = simpleCell2.getContent().iterator();
            while (iterator2.hasNext()) {
                simpleCell = (SimpleCell)iterator2.next();
                table.addCell(simpleCell.createCell(simpleCell2));
                if (simpleCell.getColspan() == 1) {
                    if (simpleCell.getWidth() > 0.0f) {
                        arrf[n3] = simpleCell.getWidth();
                    }
                    if (simpleCell.getWidthpercentage() > 0.0f) {
                        arrf2[n3] = simpleCell.getWidthpercentage();
                    }
                }
                n3 += simpleCell.getColspan();
            }
        }
        float f = 0.0f;
        for (n = 0; n < n2; ++n) {
            if (arrf[n] == 0.0f) {
                f = 0.0f;
                break;
            }
            f += arrf[n];
        }
        if (f > 0.0f) {
            table.setWidth(f);
            table.setLocked(true);
            table.setWidths(arrf);
        } else {
            for (n = 0; n < n2; ++n) {
                if (arrf2[n] == 0.0f) {
                    f = 0.0f;
                    break;
                }
                f += arrf2[n];
            }
            if (f > 0.0f) {
                table.setWidths(arrf2);
            }
        }
        if (this.width > 0.0f) {
            table.setWidth(this.width);
            table.setLocked(true);
        } else if (this.widthpercentage > 0.0f) {
            table.setWidth(this.widthpercentage);
        }
        return table;
    }

    public PdfPTable createPdfPTable() throws DocumentException {
        int n;
        SimpleCell simpleCell;
        if (this.content.isEmpty()) {
            throw new BadElementException("Trying to create a table without rows.");
        }
        SimpleCell simpleCell2 = (SimpleCell)this.content.get(0);
        int n2 = 0;
        float[] arrf = simpleCell2.getContent().iterator();
        while (arrf.hasNext()) {
            simpleCell = (SimpleCell)arrf.next();
            n2 += simpleCell.getColspan();
        }
        arrf = new float[n2];
        float[] arrf2 = new float[n2];
        PdfPTable pdfPTable = new PdfPTable(n2);
        pdfPTable.setTableEvent(this);
        pdfPTable.setHorizontalAlignment(this.alignment);
        Iterator iterator = this.content.iterator();
        while (iterator.hasNext()) {
            simpleCell2 = (SimpleCell)iterator.next();
            int n3 = 0;
            Iterator iterator2 = simpleCell2.getContent().iterator();
            while (iterator2.hasNext()) {
                simpleCell = (SimpleCell)iterator2.next();
                if (Float.isNaN(simpleCell.getSpacing_left())) {
                    simpleCell.setSpacing_left(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(simpleCell.getSpacing_right())) {
                    simpleCell.setSpacing_right(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(simpleCell.getSpacing_top())) {
                    simpleCell.setSpacing_top(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(simpleCell.getSpacing_bottom())) {
                    simpleCell.setSpacing_bottom(this.cellspacing / 2.0f);
                }
                simpleCell.setPadding(this.cellpadding);
                pdfPTable.addCell(simpleCell.createPdfPCell(simpleCell2));
                if (simpleCell.getColspan() == 1) {
                    if (simpleCell.getWidth() > 0.0f) {
                        arrf[n3] = simpleCell.getWidth();
                    }
                    if (simpleCell.getWidthpercentage() > 0.0f) {
                        arrf2[n3] = simpleCell.getWidthpercentage();
                    }
                }
                n3 += simpleCell.getColspan();
            }
        }
        float f = 0.0f;
        for (n = 0; n < n2; ++n) {
            if (arrf[n] == 0.0f) {
                f = 0.0f;
                break;
            }
            f += arrf[n];
        }
        if (f > 0.0f) {
            pdfPTable.setTotalWidth(f);
            pdfPTable.setWidths(arrf);
        } else {
            for (n = 0; n < n2; ++n) {
                if (arrf2[n] == 0.0f) {
                    f = 0.0f;
                    break;
                }
                f += arrf2[n];
            }
            if (f > 0.0f) {
                pdfPTable.setWidths(arrf2);
            }
        }
        if (this.width > 0.0f) {
            pdfPTable.setTotalWidth(this.width);
        }
        if (this.widthpercentage > 0.0f) {
            pdfPTable.setWidthPercentage(this.widthpercentage);
        }
        return pdfPTable;
    }

    public static SimpleTable getDimensionlessInstance(Rectangle rectangle, float f) {
        SimpleTable simpleTable = new SimpleTable();
        simpleTable.cloneNonPositionParameters(rectangle);
        simpleTable.setCellspacing(f);
        return simpleTable;
    }

    public void tableLayout(PdfPTable pdfPTable, float[][] arrf, float[] arrf2, int n, int n2, PdfContentByte[] arrpdfContentByte) {
        float[] arrf3 = arrf[0];
        Rectangle rectangle = new Rectangle(arrf3[0], arrf2[arrf2.length - 1], arrf3[arrf3.length - 1], arrf2[0]);
        rectangle.cloneNonPositionParameters(this);
        int n3 = rectangle.getBorder();
        rectangle.setBorder(0);
        arrpdfContentByte[1].rectangle(rectangle);
        rectangle.setBorder(n3);
        rectangle.setBackgroundColor(null);
        arrpdfContentByte[2].rectangle(rectangle);
    }

    public float getCellpadding() {
        return this.cellpadding;
    }

    public void setCellpadding(float f) {
        this.cellpadding = f;
    }

    public float getCellspacing() {
        return this.cellspacing;
    }

    public void setCellspacing(float f) {
        this.cellspacing = f;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int n) {
        this.alignment = n;
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

    public int type() {
        return 22;
    }

    public boolean isNestable() {
        return true;
    }

    public boolean add(Object object) {
        try {
            this.addElement((SimpleCell)object);
            return true;
        }
        catch (ClassCastException var2_2) {
            return false;
        }
        catch (BadElementException var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }
}

