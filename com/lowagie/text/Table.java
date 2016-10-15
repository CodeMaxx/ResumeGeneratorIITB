/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.LargeElement;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Row;
import com.lowagie.text.SimpleCell;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class Table
extends Rectangle
implements LargeElement {
    private int columns;
    private ArrayList rows = new ArrayList();
    private Point curPosition = new Point(0, 0);
    private Cell defaultCell = new Cell(true);
    private int lastHeaderRow = -1;
    private int alignment = 1;
    private float cellpadding;
    private float cellspacing;
    private float width = 80.0f;
    private boolean locked = false;
    private float[] widths;
    private boolean mTableInserted = false;
    protected boolean autoFillEmptyCells = false;
    boolean tableFitsPage = false;
    boolean cellsFitPage = false;
    float offset = Float.NaN;
    protected boolean convert2pdfptable = false;
    protected boolean notAddedYet = true;
    protected boolean complete = true;
    static /* synthetic */ Class class$com$lowagie$text$Table;
    static /* synthetic */ Class class$com$lowagie$text$Cell;

    public Table(int n) throws BadElementException {
        this(n, 1);
    }

    public Table(int n, int n2) throws BadElementException {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.setBorder(15);
        this.setBorderWidth(1.0f);
        this.defaultCell.setBorder(15);
        if (n <= 0) {
            throw new BadElementException("A table should have at least 1 column.");
        }
        this.columns = n;
        for (int i = 0; i < n2; ++i) {
            this.rows.add(new Row(n));
        }
        this.curPosition = new Point(0, 0);
        this.widths = new float[n];
        float f = 100.0f / (float)n;
        for (int j = 0; j < n; ++j) {
            this.widths[j] = f;
        }
    }

    public Table(Table table) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.cloneNonPositionParameters(table);
        this.columns = table.columns;
        this.rows = table.rows;
        this.curPosition = table.curPosition;
        this.defaultCell = table.defaultCell;
        this.lastHeaderRow = table.lastHeaderRow;
        this.alignment = table.alignment;
        this.cellpadding = table.cellpadding;
        this.cellspacing = table.cellspacing;
        this.width = table.width;
        this.widths = table.widths;
        this.autoFillEmptyCells = table.autoFillEmptyCells;
        this.tableFitsPage = table.tableFitsPage;
        this.cellsFitPage = table.cellsFitPage;
        this.offset = table.offset;
        this.convert2pdfptable = table.convert2pdfptable;
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
        return 22;
    }

    public ArrayList getChunks() {
        return new ArrayList();
    }

    public boolean isNestable() {
        return true;
    }

    public int getColumns() {
        return this.columns;
    }

    public int size() {
        return this.rows.size();
    }

    public Dimension getDimension() {
        return new Dimension(this.columns, this.size());
    }

    public Cell getDefaultCell() {
        return this.defaultCell;
    }

    public void setDefaultCell(Cell cell) {
        this.defaultCell = cell;
    }

    public int getLastHeaderRow() {
        return this.lastHeaderRow;
    }

    public void setLastHeaderRow(int n) {
        this.lastHeaderRow = n;
    }

    public int endHeaders() {
        this.lastHeaderRow = this.curPosition.x - 1;
        return this.lastHeaderRow;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int n) {
        this.alignment = n;
    }

    public void setAlignment(String string) {
        if ("Left".equalsIgnoreCase(string)) {
            this.alignment = 0;
            return;
        }
        if ("right".equalsIgnoreCase(string)) {
            this.alignment = 2;
            return;
        }
        this.alignment = 1;
    }

    public float getPadding() {
        return this.cellpadding;
    }

    public void setPadding(float f) {
        this.cellpadding = f;
    }

    public float getSpacing() {
        return this.cellspacing;
    }

    public void setSpacing(float f) {
        this.cellspacing = f;
    }

    public void setAutoFillEmptyCells(boolean bl) {
        this.autoFillEmptyCells = bl;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float f) {
        this.width = f;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean bl) {
        this.locked = bl;
    }

    public float[] getProportionalWidths() {
        return this.widths;
    }

    public void setWidths(float[] arrf) throws BadElementException {
        if (arrf.length != this.columns) {
            throw new BadElementException("Wrong number of columns.");
        }
        float f = 0.0f;
        for (int i = 0; i < this.columns; ++i) {
            f += arrf[i];
        }
        this.widths[this.columns - 1] = 100.0f;
        for (int j = 0; j < this.columns - 1; ++j) {
            float f2;
            this.widths[j] = f2 = 100.0f * arrf[j] / f;
            float[] arrf2 = this.widths;
            int n = this.columns - 1;
            arrf2[n] = arrf2[n] - f2;
        }
    }

    public void setWidths(int[] arrn) throws DocumentException {
        float[] arrf = new float[arrn.length];
        for (int i = 0; i < arrn.length; ++i) {
            arrf[i] = arrn[i];
        }
        this.setWidths(arrf);
    }

    public boolean isTableFitsPage() {
        return this.tableFitsPage;
    }

    public void setTableFitsPage(boolean bl) {
        this.tableFitsPage = bl;
        if (bl) {
            this.setCellsFitPage(true);
        }
    }

    public boolean isCellsFitPage() {
        return this.cellsFitPage;
    }

    public void setCellsFitPage(boolean bl) {
        this.cellsFitPage = bl;
    }

    public void setOffset(float f) {
        this.offset = f;
    }

    public float getOffset() {
        return this.offset;
    }

    public boolean isConvert2pdfptable() {
        return this.convert2pdfptable;
    }

    public void setConvert2pdfptable(boolean bl) {
        this.convert2pdfptable = bl;
    }

    public void addCell(Cell cell, int n, int n2) throws BadElementException {
        this.addCell(cell, new Point(n, n2));
    }

    public void addCell(Cell cell, Point point) throws BadElementException {
        if (cell == null) {
            throw new NullPointerException("addCell - cell has null-value");
        }
        if (point == null) {
            throw new NullPointerException("addCell - point has null-value");
        }
        if (cell.isTable()) {
            this.insertTable((Table)cell.getElements().next(), point);
        }
        if (point.x < 0) {
            throw new BadElementException("row coordinate of location must be >= 0");
        }
        if (point.y <= 0 && point.y > this.columns) {
            throw new BadElementException("column coordinate of location must be >= 0 and < nr of columns");
        }
        if (!this.isValidLocation(cell, point)) {
            throw new BadElementException("Adding a cell at the location (" + point.x + "," + point.y + ") with a colspan of " + cell.getColspan() + " and a rowspan of " + cell.getRowspan() + " is illegal (beyond boundaries/overlapping).");
        }
        if (cell.getBorder() == -1) {
            cell.setBorder(this.defaultCell.getBorder());
        }
        cell.fill();
        this.placeCell(this.rows, cell, point);
        this.setCurrentLocationToNextValidPosition(point);
    }

    public void addCell(Cell cell) {
        try {
            this.addCell(cell, this.curPosition);
        }
        catch (BadElementException var2_2) {
            // empty catch block
        }
    }

    public void addCell(Phrase phrase) throws BadElementException {
        this.addCell(phrase, this.curPosition);
    }

    public void addCell(Phrase phrase, Point point) throws BadElementException {
        Cell cell = new Cell(phrase);
        cell.setBorder(this.defaultCell.getBorder());
        cell.setBorderWidth(this.defaultCell.getBorderWidth());
        cell.setBorderColor(this.defaultCell.getBorderColor());
        cell.setBackgroundColor(this.defaultCell.getBackgroundColor());
        cell.setHorizontalAlignment(this.defaultCell.getHorizontalAlignment());
        cell.setVerticalAlignment(this.defaultCell.getVerticalAlignment());
        cell.setColspan(this.defaultCell.getColspan());
        cell.setRowspan(this.defaultCell.getRowspan());
        this.addCell(cell, point);
    }

    public void addCell(String string) throws BadElementException {
        this.addCell(new Phrase(string), this.curPosition);
    }

    public void addCell(String string, Point point) throws BadElementException {
        this.addCell(new Phrase(string), point);
    }

    public void insertTable(Table table) {
        if (table == null) {
            throw new NullPointerException("insertTable - table has null-value");
        }
        this.insertTable(table, this.curPosition);
    }

    public void insertTable(Table table, int n, int n2) {
        if (table == null) {
            throw new NullPointerException("insertTable - table has null-value");
        }
        this.insertTable(table, new Point(n, n2));
    }

    public void insertTable(Table table, Point point) {
        if (table == null) {
            throw new NullPointerException("insertTable - table has null-value");
        }
        if (point == null) {
            throw new NullPointerException("insertTable - point has null-value");
        }
        this.mTableInserted = true;
        table.complete();
        if (point.y > this.columns) {
            throw new IllegalArgumentException("insertTable -- wrong columnposition(" + point.y + ") of location; max =" + this.columns);
        }
        int n = point.x + 1 - this.rows.size();
        if (n > 0) {
            for (int i = 0; i < n; ++i) {
                this.rows.add(new Row(this.columns));
            }
        }
        ((Row)this.rows.get(point.x)).setElement(table, point.y);
        this.setCurrentLocationToNextValidPosition(point);
    }

    public void addColumns(int n) {
        int n2;
        ArrayList<Row> arrayList = new ArrayList<Row>(this.rows.size());
        int n3 = this.columns + n;
        for (int i = 0; i < this.rows.size(); ++i) {
            Row row = new Row(n3);
            for (n2 = 0; n2 < this.columns; ++n2) {
                row.setElement(((Row)this.rows.get(i)).getCell(n2), n2);
            }
            for (n2 = this.columns; n2 < n3 && i < this.curPosition.x; ++n2) {
                row.setElement(null, n2);
            }
            arrayList.add(row);
        }
        float[] arrf = new float[n3];
        System.arraycopy(this.widths, 0, arrf, 0, this.columns);
        for (n2 = this.columns; n2 < n3; ++n2) {
            arrf[n2] = 0.0f;
        }
        this.columns = n3;
        this.widths = arrf;
        this.rows = arrayList;
    }

    public void deleteColumn(int n) throws BadElementException {
        float[] arrf = new float[--this.columns];
        System.arraycopy(this.widths, 0, arrf, 0, n);
        System.arraycopy(this.widths, n + 1, arrf, n, this.columns - n);
        this.setWidths(arrf);
        System.arraycopy(this.widths, 0, arrf, 0, this.columns);
        this.widths = arrf;
        int n2 = this.rows.size();
        for (int i = 0; i < n2; ++i) {
            Row row = (Row)this.rows.get(i);
            row.deleteColumn(n);
            this.rows.set(i, row);
        }
        if (n == this.columns) {
            this.curPosition.setLocation(this.curPosition.x + 1, 0);
        }
    }

    public boolean deleteRow(int n) {
        if (n < 0 || n >= this.rows.size()) {
            return false;
        }
        this.rows.remove(n);
        this.curPosition.setLocation(this.curPosition.x - 1, this.curPosition.y);
        return true;
    }

    public void deleteAllRows() {
        this.rows.clear();
        this.rows.add(new Row(this.columns));
        this.curPosition.setLocation(0, 0);
        this.lastHeaderRow = -1;
    }

    public boolean deleteLastRow() {
        return this.deleteRow(this.rows.size() - 1);
    }

    public void complete() {
        if (this.mTableInserted) {
            this.mergeInsertedTables();
            this.mTableInserted = false;
        }
        if (this.autoFillEmptyCells) {
            this.fillEmptyMatrixCells();
        }
    }

    public Object getElement(int n, int n2) {
        return ((Row)this.rows.get(n)).getCell(n2);
    }

    private void mergeInsertedTables() {
        int n2;
        reference var16_17;
        int n;
        int n3 = 0;
        int n4 = 0;
        float[] arrf = null;
        int[] arrn = new int[this.columns];
        float[][] arrarrf = new float[this.columns][];
        int[] arrn2 = new int[this.rows.size()];
        ArrayList<Row> arrayList = null;
        boolean bl = false;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        Table table = null;
        for (n4 = 0; n4 < this.columns; ++n4) {
            n8 = 1;
            float[] arrf2 = null;
            for (n3 = 0; n3 < this.rows.size(); ++n3) {
                if (!(class$com$lowagie$text$Table == null ? Table.class$("com.lowagie.text.Table") : class$com$lowagie$text$Table).isInstance(((Row)this.rows.get(n3)).getCell(n4))) continue;
                bl = true;
                table = (Table)((Row)this.rows.get(n3)).getCell(n4);
                if (arrf2 == null) {
                    arrf2 = table.widths;
                    n8 = arrf2.length;
                    continue;
                }
                n = table.getDimension().width;
                var16_17 = (reference)new float[n * arrf2.length];
                float f = 0.0f;
                float f2 = 0.0f;
                float f3 = 0.0f;
                n2 = 0;
                int n9 = 0;
                int n10 = 0;
                f += arrf2[0];
                f2 += table.widths[0];
                while (n2 < arrf2.length && n9 < n) {
                    if (f2 > f) {
                        var16_17[n10] = f - f3;
                        if (++n2 < arrf2.length) {
                            f += arrf2[n2];
                        }
                    } else {
                        var16_17[n10] = f2 - f3;
                        ++n9;
                        if ((double)Math.abs(f2 - f) < 1.0E-4 && ++n2 < arrf2.length) {
                            f += arrf2[n2];
                        }
                        if (n9 < n) {
                            f2 += table.widths[n9];
                        }
                    }
                    f3 += var16_17[n10];
                    ++n10;
                }
                arrf2 = new float[n10];
                System.arraycopy(var16_17, 0, arrf2, 0, n10);
                n8 = n10;
            }
            arrarrf[n4] = arrf2;
            n6 += n8;
            arrn[n4] = n8;
        }
        for (n3 = 0; n3 < this.rows.size(); ++n3) {
            n7 = 1;
            for (n4 = 0; n4 < this.columns; ++n4) {
                if (!(class$com$lowagie$text$Table == null ? Table.class$("com.lowagie.text.Table") : class$com$lowagie$text$Table).isInstance(((Row)this.rows.get(n3)).getCell(n4))) continue;
                bl = true;
                table = (Table)((Row)this.rows.get(n3)).getCell(n4);
                if (table.getDimension().height <= n7) continue;
                n7 = table.getDimension().height;
            }
            n5 += n7;
            arrn2[n3] = n7;
        }
        if (n6 != this.columns || n5 != this.rows.size() || bl) {
            arrf = new float[n6];
            int n11 = 0;
            for (n = 0; n < this.widths.length; ++n) {
                if (arrn[n] != 1) {
                    for (var16_17 = 0; var16_17 < arrn[n]; ++var16_17) {
                        arrf[n11] = this.widths[n] * arrarrf[n][var16_17] / 100.0f;
                        ++n11;
                    }
                    continue;
                }
                arrf[n11] = this.widths[n];
                ++n11;
            }
            arrayList = new ArrayList<Row>(n5);
            for (n3 = 0; n3 < n5; ++n3) {
                arrayList.add(new Row(n6));
            }
            n = 0;
            var16_17 = 0;
            Object object = null;
            for (n3 = 0; n3 < this.rows.size(); ++n3) {
                var16_17 = (reference)false ? 1 : 0;
                n7 = 1;
                for (n4 = 0; n4 < this.columns; ++n4) {
                    int[] arrn3;
                    if ((class$com$lowagie$text$Table == null ? Table.class$("com.lowagie.text.Table") : class$com$lowagie$text$Table).isInstance(((Row)this.rows.get(n3)).getCell(n4))) {
                        table = (Table)((Row)this.rows.get(n3)).getCell(n4);
                        arrn3 = new int[table.widths.length + 1];
                        n2 = 0;
                        for (int i = 0; i < table.widths.length; ++i) {
                            arrn3[i] = var16_17 + n2;
                            float f = table.widths[i];
                            float f4 = 0.0f;
                            while (n2 < arrn[n4] && (double)Math.abs(f - (f4 += arrarrf[n4][n2++])) >= 1.0E-4) {
                            }
                        }
                        arrn3[i] = var16_17 + n2;
                        for (int j = 0; j < table.getDimension().height; ++j) {
                            for (int k = 0; k < table.getDimension().width; ++k) {
                                object = table.getElement(j, k);
                                if (object == null) continue;
                                Object object2 = var16_17 + k;
                                if ((class$com$lowagie$text$Cell == null ? Table.class$("com.lowagie.text.Cell") : class$com$lowagie$text$Cell).isInstance(object)) {
                                    Cell cell = (Cell)object;
                                    object2 = arrn3[k];
                                    int n12 = arrn3[k + cell.getColspan()];
                                    cell.setColspan(n12 - object2);
                                }
                                ((Row)arrayList.get(j + n)).addElement(object, (int)object2);
                            }
                        }
                    } else if ((class$com$lowagie$text$Cell == null ? Table.class$("com.lowagie.text.Cell") : class$com$lowagie$text$Cell).isInstance(arrn3 = this.getElement(n3, n4))) {
                        ((Cell)arrn3).setRowspan(((Cell)((Row)this.rows.get(n3)).getCell(n4)).getRowspan() + arrn2[n3] - 1);
                        ((Cell)arrn3).setColspan(((Cell)((Row)this.rows.get(n3)).getCell(n4)).getColspan() + arrn[n4] - 1);
                        this.placeCell(arrayList, (Cell)arrn3, new Point(n, (int)var16_17));
                    }
                    var16_17 += arrn[n4];
                }
                n += arrn2[n3];
            }
            this.columns = n6;
            this.rows = arrayList;
            this.widths = arrf;
        }
    }

    private void fillEmptyMatrixCells() {
        try {
            for (int i = 0; i < this.rows.size(); ++i) {
                for (int j = 0; j < this.columns; ++j) {
                    if (((Row)this.rows.get(i)).isReserved(j)) continue;
                    this.addCell(this.defaultCell, new Point(i, j));
                }
            }
        }
        catch (BadElementException var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    private boolean isValidLocation(Cell cell, Point point) {
        if (point.x < this.rows.size()) {
            if (point.y + cell.getColspan() > this.columns) {
                return false;
            }
            int n = this.rows.size() - point.x > cell.getRowspan() ? cell.getRowspan() : this.rows.size() - point.x;
            int n2 = this.columns - point.y > cell.getColspan() ? cell.getColspan() : this.columns - point.y;
            for (int i = point.x; i < point.x + n; ++i) {
                for (int j = point.y; j < point.y + n2; ++j) {
                    if (!((Row)this.rows.get(i)).isReserved(j)) continue;
                    return false;
                }
            }
        } else if (point.y + cell.getColspan() > this.columns) {
            return false;
        }
        return true;
    }

    private void assumeTableDefaults(Cell cell) {
        if (cell.getBorder() == -1) {
            cell.setBorder(this.defaultCell.getBorder());
        }
        if (cell.getBorderWidth() == -1.0f) {
            cell.setBorderWidth(this.defaultCell.getBorderWidth());
        }
        if (cell.getBorderColor() == null) {
            cell.setBorderColor(this.defaultCell.getBorderColor());
        }
        if (cell.getBackgroundColor() == null) {
            cell.setBackgroundColor(this.defaultCell.getBackgroundColor());
        }
        if (cell.getHorizontalAlignment() == -1) {
            cell.setHorizontalAlignment(this.defaultCell.getHorizontalAlignment());
        }
        if (cell.getVerticalAlignment() == -1) {
            cell.setVerticalAlignment(this.defaultCell.getVerticalAlignment());
        }
    }

    private void placeCell(ArrayList arrayList, Cell cell, Point point) {
        int n;
        Row row = null;
        int n2 = point.x + cell.getRowspan() - arrayList.size();
        this.assumeTableDefaults(cell);
        if (point.x + cell.getRowspan() > arrayList.size()) {
            for (n = 0; n < n2; ++n) {
                row = new Row(this.columns);
                arrayList.add(row);
            }
        }
        for (n = point.x + 1; n < point.x + cell.getRowspan(); ++n) {
            if (((Row)arrayList.get(n)).reserve(point.y, cell.getColspan())) continue;
            throw new RuntimeException("addCell - error in reserve");
        }
        row = (Row)arrayList.get(point.x);
        row.addElement(cell, point.y);
    }

    private void setCurrentLocationToNextValidPosition(Point point) {
        int n = point.x;
        int n2 = point.y;
        do {
            if (n2 + 1 == this.columns) {
                ++n;
                n2 = 0;
                continue;
            }
            ++n2;
        } while (n < this.rows.size() && n2 < this.columns && ((Row)this.rows.get(n)).isReserved(n2));
        this.curPosition = new Point(n, n2);
    }

    public float[] getWidths(float f, float f2) {
        float[] arrf = new float[this.columns + 1];
        float f3 = this.locked ? 100.0f * this.width / f2 : this.width;
        switch (this.alignment) {
            case 0: {
                arrf[0] = f;
                break;
            }
            case 2: {
                arrf[0] = f + f2 * (100.0f - f3) / 100.0f;
                break;
            }
            default: {
                arrf[0] = f + f2 * (100.0f - f3) / 200.0f;
            }
        }
        f2 = f2 * f3 / 100.0f;
        for (int i = 1; i < this.columns; ++i) {
            arrf[i] = arrf[i - 1] + this.widths[i - 1] * f2 / 100.0f;
        }
        arrf[this.columns] = arrf[0] + f2;
        return arrf;
    }

    public Iterator iterator() {
        return this.rows.iterator();
    }

    public PdfPTable createPdfPTable() throws BadElementException {
        if (!this.convert2pdfptable) {
            throw new BadElementException("No error, just an old style table");
        }
        this.setAutoFillEmptyCells(true);
        this.complete();
        PdfPTable pdfPTable = new PdfPTable(this.widths);
        pdfPTable.setComplete(this.complete);
        if (this.isNotAddedYet()) {
            pdfPTable.setSkipFirstHeader(true);
        }
        pdfPTable.setTableEvent(SimpleTable.getDimensionlessInstance(this, this.cellspacing));
        pdfPTable.setHeaderRows(this.lastHeaderRow + 1);
        pdfPTable.setSplitLate(this.cellsFitPage);
        pdfPTable.setKeepTogether(this.tableFitsPage);
        if (!Float.isNaN(this.offset)) {
            pdfPTable.setSpacingBefore(this.offset);
        }
        pdfPTable.setHorizontalAlignment(this.alignment);
        if (this.locked) {
            pdfPTable.setTotalWidth(this.width);
            pdfPTable.setLockedWidth(true);
        } else {
            pdfPTable.setWidthPercentage(this.width);
        }
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            Row row = (Row)iterator.next();
            for (int i = 0; i < row.getColumns(); ++i) {
                PdfPCell pdfPCell;
                Element element = (Element)row.getCell(i);
                if (element == null) continue;
                if (element instanceof Table) {
                    pdfPCell = new PdfPCell(((Table)element).createPdfPTable());
                } else if (element instanceof Cell) {
                    pdfPCell = ((Cell)element).createPdfPCell();
                    pdfPCell.setPadding(this.cellpadding + this.cellspacing / 2.0f);
                    pdfPCell.setCellEvent(SimpleCell.getDimensionlessInstance((Cell)element, this.cellspacing));
                } else {
                    pdfPCell = new PdfPCell();
                }
                pdfPTable.addCell(pdfPCell);
            }
        }
        return pdfPTable;
    }

    public boolean isNotAddedYet() {
        return this.notAddedYet;
    }

    public void setNotAddedYet(boolean bl) {
        this.notAddedYet = bl;
    }

    public void flushContent() {
        this.setNotAddedYet(false);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.getLastHeaderRow() + 1; ++i) {
            arrayList.add(this.rows.get(i));
        }
        this.rows = arrayList;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean bl) {
        this.complete = bl;
    }

    public Cell getDefaultLayout() {
        return this.getDefaultCell();
    }

    public void setDefaultLayout(Cell cell) {
        this.defaultCell = cell;
    }
}

