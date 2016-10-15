/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Table;
import java.util.ArrayList;

public class Row
implements Element {
    public static final int NULL = 0;
    public static final int CELL = 1;
    public static final int TABLE = 2;
    protected int columns;
    protected int currentColumn;
    protected boolean[] reserved;
    protected Object[] cells;
    protected int horizontalAlignment;

    protected Row(int n) {
        this.columns = n;
        this.reserved = new boolean[n];
        this.cells = new Object[n];
        this.currentColumn = 0;
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
        return 21;
    }

    public ArrayList getChunks() {
        return new ArrayList();
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return false;
    }

    void deleteColumn(int n) {
        int n2;
        if (n >= this.columns || n < 0) {
            throw new IndexOutOfBoundsException("getCell at illegal index : " + n);
        }
        --this.columns;
        boolean[] arrbl = new boolean[this.columns];
        Cell[] arrcell = new Cell[this.columns];
        for (n2 = 0; n2 < n; ++n2) {
            arrbl[n2] = this.reserved[n2];
            arrcell[n2] = this.cells[n2];
            if (arrcell[n2] == null || n2 + arrcell[n2].getColspan() <= n) continue;
            arrcell[n2].setColspan(((Cell)this.cells[n2]).getColspan() - 1);
        }
        for (n2 = n; n2 < this.columns; ++n2) {
            arrbl[n2] = this.reserved[n2 + 1];
            arrcell[n2] = this.cells[n2 + 1];
        }
        if (this.cells[n] != null && ((Cell)this.cells[n]).getColspan() > 1) {
            arrcell[n] = this.cells[n];
            arrcell[n].setColspan(arrcell[n].getColspan() - 1);
        }
        this.reserved = arrbl;
        this.cells = arrcell;
    }

    int addElement(Object object) {
        return this.addElement(object, this.currentColumn);
    }

    int addElement(Object object, int n) {
        int n2;
        if (object == null) {
            throw new NullPointerException("addCell - null argument");
        }
        if (n < 0 || n > this.columns) {
            throw new IndexOutOfBoundsException("addCell - illegal column argument");
        }
        if (this.getObjectID(object) != 1 && this.getObjectID(object) != 2) {
            throw new IllegalArgumentException("addCell - only Cells or Tables allowed");
        }
        Class class_ = Cell.class;
        int n3 = n2 = class_.isInstance(object) ? ((Cell)object).getColspan() : 1;
        if (!this.reserve(n, n2)) {
            return -1;
        }
        this.cells[n] = object;
        this.currentColumn += n2 - 1;
        return n;
    }

    void setElement(Object object, int n) {
        if (this.reserved[n]) {
            throw new IllegalArgumentException("setElement - position already taken");
        }
        this.cells[n] = object;
        if (object != null) {
            this.reserved[n] = true;
        }
    }

    boolean reserve(int n) {
        return this.reserve(n, 1);
    }

    boolean reserve(int n, int n2) {
        if (n < 0 || n + n2 > this.columns) {
            throw new IndexOutOfBoundsException("reserve - incorrect column/size");
        }
        for (int i = n; i < n + n2; ++i) {
            if (this.reserved[i]) {
                for (int j = i; j >= n; --j) {
                    this.reserved[j] = false;
                }
                return false;
            }
            this.reserved[i] = true;
        }
        return true;
    }

    boolean isReserved(int n) {
        return this.reserved[n];
    }

    int getElementID(int n) {
        if (this.cells[n] == null) {
            return 0;
        }
        Class class_ = Cell.class;
        if (class_.isInstance(this.cells[n])) {
            return 1;
        }
        Class class_2 = Table.class;
        if (class_2.isInstance(this.cells[n])) {
            return 2;
        }
        return -1;
    }

    int getObjectID(Object object) {
        if (object == null) {
            return 0;
        }
        Class class_ = Cell.class;
        if (class_.isInstance(object)) {
            return 1;
        }
        Class class_2 = Table.class;
        if (class_2.isInstance(object)) {
            return 2;
        }
        return -1;
    }

    public Object getCell(int n) {
        if (n < 0 || n > this.columns) {
            throw new IndexOutOfBoundsException("getCell at illegal index :" + n + " max is " + this.columns);
        }
        return this.cells[n];
    }

    public boolean isEmpty() {
        for (int i = 0; i < this.columns; ++i) {
            if (this.cells[i] == null) continue;
            return false;
        }
        return true;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setHorizontalAlignment(int n) {
        this.horizontalAlignment = n;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
}

