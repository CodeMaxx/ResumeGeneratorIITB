/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Cell;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Row;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfCell;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PdfTable
extends Rectangle {
    private int columns;
    private ArrayList headercells;
    private ArrayList cells;
    protected Table table;
    protected float[] positions;

    PdfTable(Table table, float f, float f2, float f3) {
        super(f, f3, f2, f3);
        this.table = table;
        table.complete();
        this.cloneNonPositionParameters(table);
        this.columns = table.getColumns();
        this.positions = table.getWidths(f, f2 - f);
        this.setLeft(this.positions[0]);
        this.setRight(this.positions[this.positions.length - 1]);
        this.headercells = new ArrayList();
        this.cells = new ArrayList();
        this.updateRowAdditionsInternal();
    }

    void updateRowAdditions() {
        this.table.complete();
        this.updateRowAdditionsInternal();
        this.table.deleteAllRows();
    }

    private void updateRowAdditionsInternal() {
        PdfCell pdfCell;
        int n;
        int n2 = this.rows();
        int n3 = 0;
        int n4 = 0;
        int n5 = this.table.getLastHeaderRow() + 1;
        ArrayList<PdfCell> arrayList = new ArrayList<PdfCell>();
        int n6 = this.table.size() + 1;
        float[] arrf = new float[n6];
        for (int i = 0; i < n6; ++i) {
            arrf[i] = this.getBottom();
        }
        Iterator iterator = this.table.iterator();
        while (iterator.hasNext()) {
            boolean bl = false;
            Row row = (Row)iterator.next();
            if (row.isEmpty()) {
                if (n3 < n6 - 1 && arrf[n3 + 1] > arrf[n3]) {
                    arrf[n3 + 1] = arrf[n3];
                }
            } else {
                for (n = 0; n < row.getColumns(); ++n) {
                    Cell cell;
                    block13 : {
                        cell = (Cell)row.getCell(n);
                        if (cell == null) continue;
                        pdfCell = new PdfCell(cell, n3 + n2, this.positions[n], this.positions[n + cell.getColspan()], arrf[n3], this.cellspacing(), this.cellpadding());
                        if (n3 < n5) {
                            pdfCell.setHeader();
                            this.headercells.add(pdfCell);
                            if (!this.table.isNotAddedYet()) continue;
                        }
                        try {
                            if (arrf[n3] - pdfCell.getHeight() - this.cellpadding() < arrf[n3 + pdfCell.rowspan()]) {
                                arrf[n3 + pdfCell.rowspan()] = arrf[n3] - pdfCell.getHeight() - this.cellpadding();
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException var14_16) {
                            if (arrf[n3] - pdfCell.getHeight() >= arrf[n6 - 1]) break block13;
                            arrf[n6 - 1] = arrf[n3] - pdfCell.getHeight();
                        }
                    }
                    pdfCell.setGroupNumber(n4);
                    bl |= cell.getGroupChange();
                    arrayList.add(pdfCell);
                }
            }
            ++n3;
            if (!bl) continue;
            ++n4;
        }
        int n7 = arrayList.size();
        for (n = 0; n < n7; ++n) {
            pdfCell = (PdfCell)arrayList.get(n);
            try {
                pdfCell.setBottom(arrf[pdfCell.rownumber() - n2 + pdfCell.rowspan()]);
                continue;
            }
            catch (ArrayIndexOutOfBoundsException var14_18) {
                pdfCell.setBottom(arrf[n6 - 1]);
            }
        }
        this.cells.addAll(arrayList);
        this.setBottom(arrf[n6 - 1]);
    }

    int rows() {
        return this.cells.isEmpty() ? 0 : ((PdfCell)this.cells.get(this.cells.size() - 1)).rownumber() + 1;
    }

    public int type() {
        return 22;
    }

    ArrayList getHeaderCells() {
        return this.headercells;
    }

    boolean hasHeader() {
        return !this.headercells.isEmpty();
    }

    ArrayList getCells() {
        return this.cells;
    }

    int columns() {
        return this.columns;
    }

    final float cellpadding() {
        return this.table.getPadding();
    }

    final float cellspacing() {
        return this.table.getSpacing();
    }

    public final boolean hasToFitPageTable() {
        return this.table.isTableFitsPage();
    }

    public final boolean hasToFitPageCells() {
        return this.table.isCellsFitPage();
    }

    public float getOffset() {
        return this.table.getOffset();
    }
}

