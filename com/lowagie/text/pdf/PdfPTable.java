/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Image;
import com.lowagie.text.LargeElement;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTableEvent;
import com.lowagie.text.pdf.events.PdfPTableEventForwarder;
import java.util.ArrayList;

public class PdfPTable
implements LargeElement {
    public static final int BASECANVAS = 0;
    public static final int BACKGROUNDCANVAS = 1;
    public static final int LINECANVAS = 2;
    public static final int TEXTCANVAS = 3;
    protected ArrayList rows = new ArrayList();
    protected float totalHeight = 0.0f;
    protected PdfPCell[] currentRow;
    protected int currentRowIdx = 0;
    protected PdfPCell defaultCell = new PdfPCell((Phrase)null);
    protected float totalWidth = 0.0f;
    protected float[] relativeWidths;
    protected float[] absoluteWidths;
    protected PdfPTableEvent tableEvent;
    protected int headerRows;
    protected float widthPercentage = 80.0f;
    private int horizontalAlignment = 1;
    private boolean skipFirstHeader = false;
    protected boolean isColspan = false;
    protected int runDirection = 0;
    private boolean lockedWidth = false;
    private boolean splitRows = true;
    protected float spacingBefore;
    protected float spacingAfter;
    private boolean extendLastRow;
    private boolean headersInEvent;
    private boolean splitLate = true;
    private boolean keepTogether;
    protected boolean complete = true;
    private int footerRows;

    protected PdfPTable() {
    }

    public PdfPTable(float[] arrf) {
        if (arrf == null) {
            throw new NullPointerException("The widths array in PdfPTable constructor can not be null.");
        }
        if (arrf.length == 0) {
            throw new IllegalArgumentException("The widths array in PdfPTable constructor can not have zero length.");
        }
        this.relativeWidths = new float[arrf.length];
        System.arraycopy(arrf, 0, this.relativeWidths, 0, arrf.length);
        this.absoluteWidths = new float[arrf.length];
        this.calculateWidths();
        this.currentRow = new PdfPCell[this.absoluteWidths.length];
        this.keepTogether = false;
    }

    public PdfPTable(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The number of columns in PdfPTable constructor must be greater than zero.");
        }
        this.relativeWidths = new float[n];
        for (int i = 0; i < n; ++i) {
            this.relativeWidths[i] = 1.0f;
        }
        this.absoluteWidths = new float[this.relativeWidths.length];
        this.calculateWidths();
        this.currentRow = new PdfPCell[this.absoluteWidths.length];
        this.keepTogether = false;
    }

    public PdfPTable(PdfPTable pdfPTable) {
        int n;
        this.copyFormat(pdfPTable);
        for (n = 0; n < this.currentRow.length && pdfPTable.currentRow[n] != null; ++n) {
            this.currentRow[n] = new PdfPCell(pdfPTable.currentRow[n]);
        }
        for (n = 0; n < pdfPTable.rows.size(); ++n) {
            PdfPRow pdfPRow = (PdfPRow)pdfPTable.rows.get(n);
            if (pdfPRow != null) {
                pdfPRow = new PdfPRow(pdfPRow);
            }
            this.rows.add(pdfPRow);
        }
    }

    public static PdfPTable shallowCopy(PdfPTable pdfPTable) {
        PdfPTable pdfPTable2 = new PdfPTable();
        pdfPTable2.copyFormat(pdfPTable);
        return pdfPTable2;
    }

    private void copyFormat(PdfPTable pdfPTable) {
        this.relativeWidths = new float[pdfPTable.relativeWidths.length];
        this.absoluteWidths = new float[pdfPTable.relativeWidths.length];
        System.arraycopy(pdfPTable.relativeWidths, 0, this.relativeWidths, 0, this.relativeWidths.length);
        System.arraycopy(pdfPTable.absoluteWidths, 0, this.absoluteWidths, 0, this.relativeWidths.length);
        this.totalWidth = pdfPTable.totalWidth;
        this.totalHeight = pdfPTable.totalHeight;
        this.currentRowIdx = 0;
        this.tableEvent = pdfPTable.tableEvent;
        this.runDirection = pdfPTable.runDirection;
        this.defaultCell = new PdfPCell(pdfPTable.defaultCell);
        this.currentRow = new PdfPCell[pdfPTable.currentRow.length];
        this.isColspan = pdfPTable.isColspan;
        this.splitRows = pdfPTable.splitRows;
        this.spacingAfter = pdfPTable.spacingAfter;
        this.spacingBefore = pdfPTable.spacingBefore;
        this.headerRows = pdfPTable.headerRows;
        this.footerRows = pdfPTable.footerRows;
        this.lockedWidth = pdfPTable.lockedWidth;
        this.extendLastRow = pdfPTable.extendLastRow;
        this.headersInEvent = pdfPTable.headersInEvent;
        this.widthPercentage = pdfPTable.widthPercentage;
        this.splitLate = pdfPTable.splitLate;
        this.skipFirstHeader = pdfPTable.skipFirstHeader;
        this.horizontalAlignment = pdfPTable.horizontalAlignment;
        this.keepTogether = pdfPTable.keepTogether;
        this.complete = pdfPTable.complete;
    }

    public void setWidths(float[] arrf) throws DocumentException {
        if (arrf.length != this.relativeWidths.length) {
            throw new DocumentException("Wrong number of columns.");
        }
        this.relativeWidths = new float[arrf.length];
        System.arraycopy(arrf, 0, this.relativeWidths, 0, arrf.length);
        this.absoluteWidths = new float[arrf.length];
        this.totalHeight = 0.0f;
        this.calculateWidths();
        this.calculateHeights();
    }

    public void setWidths(int[] arrn) throws DocumentException {
        float[] arrf = new float[arrn.length];
        for (int i = 0; i < arrn.length; ++i) {
            arrf[i] = arrn[i];
        }
        this.setWidths(arrf);
    }

    private void calculateWidths() {
        int n;
        if (this.totalWidth <= 0.0f) {
            return;
        }
        float f = 0.0f;
        for (n = 0; n < this.absoluteWidths.length; ++n) {
            f += this.relativeWidths[n];
        }
        for (n = 0; n < this.absoluteWidths.length; ++n) {
            this.absoluteWidths[n] = this.totalWidth * this.relativeWidths[n] / f;
        }
    }

    public void setTotalWidth(float f) {
        if (this.totalWidth == f) {
            return;
        }
        this.totalWidth = f;
        this.totalHeight = 0.0f;
        this.calculateWidths();
        this.calculateHeights();
    }

    public void setTotalWidth(float[] arrf) throws DocumentException {
        if (arrf.length != this.relativeWidths.length) {
            throw new DocumentException("Wrong number of columns.");
        }
        this.totalWidth = 0.0f;
        for (int i = 0; i < arrf.length; ++i) {
            this.totalWidth += arrf[i];
        }
        this.setWidths(arrf);
    }

    public void setWidthPercentage(float[] arrf, Rectangle rectangle) throws DocumentException {
        if (arrf.length != this.relativeWidths.length) {
            throw new IllegalArgumentException("Wrong number of columns.");
        }
        float f = 0.0f;
        for (int i = 0; i < arrf.length; ++i) {
            f += arrf[i];
        }
        this.widthPercentage = f / (rectangle.getRight() - rectangle.getLeft()) * 100.0f;
        this.setWidths(arrf);
    }

    public float getTotalWidth() {
        return this.totalWidth;
    }

    void calculateHeights() {
        if (this.totalWidth <= 0.0f) {
            return;
        }
        this.totalHeight = 0.0f;
        for (int i = 0; i < this.rows.size(); ++i) {
            PdfPRow pdfPRow = (PdfPRow)this.rows.get(i);
            if (pdfPRow == null) continue;
            pdfPRow.setWidths(this.absoluteWidths);
            this.totalHeight += pdfPRow.getMaxHeights();
        }
    }

    public void calculateHeightsFast() {
        if (this.totalWidth <= 0.0f) {
            return;
        }
        this.totalHeight = 0.0f;
        for (int i = 0; i < this.rows.size(); ++i) {
            PdfPRow pdfPRow = (PdfPRow)this.rows.get(i);
            if (pdfPRow == null) continue;
            this.totalHeight += pdfPRow.getMaxHeights();
        }
    }

    public PdfPCell getDefaultCell() {
        return this.defaultCell;
    }

    public void addCell(PdfPCell pdfPCell) {
        int n;
        PdfPCell pdfPCell2 = new PdfPCell(pdfPCell);
        int n2 = pdfPCell2.getColspan();
        n2 = Math.max(n2, 1);
        n2 = Math.min(n2, this.currentRow.length - this.currentRowIdx);
        pdfPCell2.setColspan(n2);
        if (n2 != 1) {
            this.isColspan = true;
        }
        if ((n = pdfPCell2.getRunDirection()) == 0) {
            pdfPCell2.setRunDirection(this.runDirection);
        }
        this.currentRow[this.currentRowIdx] = pdfPCell2;
        this.currentRowIdx += n2;
        if (this.currentRowIdx >= this.currentRow.length) {
            Object object;
            if (this.runDirection == 3) {
                object = new PdfPCell[this.absoluteWidths.length];
                int n3 = this.currentRow.length;
                for (int i = 0; i < this.currentRow.length; ++i) {
                    PdfPCell pdfPCell3 = this.currentRow[i];
                    int n4 = pdfPCell3.getColspan();
                    object[n3 -= n4] = pdfPCell3;
                    i += n4 - 1;
                }
                this.currentRow = object;
            }
            object = new PdfPRow(this.currentRow);
            if (this.totalWidth > 0.0f) {
                object.setWidths(this.absoluteWidths);
                this.totalHeight += object.getMaxHeights();
            }
            this.rows.add(object);
            this.currentRow = new PdfPCell[this.absoluteWidths.length];
            this.currentRowIdx = 0;
        }
    }

    public void addCell(String string) {
        this.addCell(new Phrase(string));
    }

    public void addCell(PdfPTable pdfPTable) {
        this.defaultCell.setTable(pdfPTable);
        this.addCell(this.defaultCell);
        this.defaultCell.setTable(null);
    }

    public void addCell(Image image) {
        this.defaultCell.setImage(image);
        this.addCell(this.defaultCell);
        this.defaultCell.setImage(null);
    }

    public void addCell(Phrase phrase) {
        this.defaultCell.setPhrase(phrase);
        this.addCell(this.defaultCell);
        this.defaultCell.setPhrase(null);
    }

    public float writeSelectedRows(int n, int n2, float f, float f2, PdfContentByte[] arrpdfContentByte) {
        return this.writeSelectedRows(0, -1, n, n2, f, f2, arrpdfContentByte);
    }

    public float writeSelectedRows(int n, int n2, int n3, int n4, float f, float f2, PdfContentByte[] arrpdfContentByte) {
        if (this.totalWidth <= 0.0f) {
            throw new RuntimeException("The table width must be greater than zero.");
        }
        int n5 = this.rows.size();
        if (n4 < 0) {
            n4 = n5;
        }
        n4 = Math.min(n4, n5);
        if (n3 < 0) {
            n3 = 0;
        }
        if (n3 >= n4) {
            return f2;
        }
        if (n2 < 0) {
            n2 = this.absoluteWidths.length;
        }
        n2 = Math.min(n2, this.absoluteWidths.length);
        if (n < 0) {
            n = 0;
        }
        n = Math.min(n, this.absoluteWidths.length);
        float f3 = f2;
        for (int i = n3; i < n4; ++i) {
            PdfPRow pdfPRow = (PdfPRow)this.rows.get(i);
            if (pdfPRow == null) continue;
            pdfPRow.writeCells(n, n2, f, f2, arrpdfContentByte);
            f2 -= pdfPRow.getMaxHeights();
        }
        if (this.tableEvent != null && n == 0 && n2 == this.absoluteWidths.length) {
            float[] arrf = new float[n4 - n3 + 1];
            arrf[0] = f3;
            for (int j = n3; j < n4; ++j) {
                PdfPRow pdfPRow = (PdfPRow)this.rows.get(j);
                float f4 = 0.0f;
                if (pdfPRow != null) {
                    f4 = pdfPRow.getMaxHeights();
                }
                arrf[j - n3 + 1] = arrf[j - n3] - f4;
            }
            this.tableEvent.tableLayout(this, this.getEventWidths(f, n3, n4, this.headersInEvent), arrf, this.headersInEvent ? this.headerRows : 0, n3, arrpdfContentByte);
        }
        return f2;
    }

    public float writeSelectedRows(int n, int n2, float f, float f2, PdfContentByte pdfContentByte) {
        return this.writeSelectedRows(0, -1, n, n2, f, f2, pdfContentByte);
    }

    public float writeSelectedRows(int n, int n2, int n3, int n4, float f, float f2, PdfContentByte pdfContentByte) {
        float f3;
        if (n2 < 0) {
            n2 = this.absoluteWidths.length;
        }
        n2 = Math.min(n2, this.absoluteWidths.length);
        if (n < 0) {
            n = 0;
        }
        if ((n = Math.min(n, this.absoluteWidths.length)) != 0 || n2 != this.absoluteWidths.length) {
            float f4 = 0.0f;
            for (int i = n; i < n2; ++i) {
                f4 += this.absoluteWidths[i];
            }
            pdfContentByte.saveState();
            f3 = 0.0f;
            float f5 = 0.0f;
            if (n == 0) {
                f3 = 10000.0f;
            }
            if (n2 == this.absoluteWidths.length) {
                f5 = 10000.0f;
            }
            pdfContentByte.rectangle(f - f3, -10000.0f, f4 + f3 + f5, 20000.0f);
            pdfContentByte.clip();
            pdfContentByte.newPath();
        }
        PdfContentByte[] arrpdfContentByte = PdfPTable.beginWritingRows(pdfContentByte);
        f3 = this.writeSelectedRows(n, n2, n3, n4, f, f2, arrpdfContentByte);
        PdfPTable.endWritingRows(arrpdfContentByte);
        if (n != 0 || n2 != this.absoluteWidths.length) {
            pdfContentByte.restoreState();
        }
        return f3;
    }

    public static PdfContentByte[] beginWritingRows(PdfContentByte pdfContentByte) {
        return new PdfContentByte[]{pdfContentByte, pdfContentByte.getDuplicate(), pdfContentByte.getDuplicate(), pdfContentByte.getDuplicate()};
    }

    public static void endWritingRows(PdfContentByte[] arrpdfContentByte) {
        PdfContentByte pdfContentByte = arrpdfContentByte[0];
        pdfContentByte.saveState();
        pdfContentByte.add(arrpdfContentByte[1]);
        pdfContentByte.restoreState();
        pdfContentByte.saveState();
        pdfContentByte.setLineCap(2);
        pdfContentByte.resetRGBColorStroke();
        pdfContentByte.add(arrpdfContentByte[2]);
        pdfContentByte.restoreState();
        pdfContentByte.add(arrpdfContentByte[3]);
    }

    public int size() {
        return this.rows.size();
    }

    public float getTotalHeight() {
        return this.totalHeight;
    }

    public float getRowHeight(int n) {
        if (this.totalWidth <= 0.0f || n < 0 || n >= this.rows.size()) {
            return 0.0f;
        }
        PdfPRow pdfPRow = (PdfPRow)this.rows.get(n);
        if (pdfPRow == null) {
            return 0.0f;
        }
        return pdfPRow.getMaxHeights();
    }

    public float getHeaderHeight() {
        float f = 0.0f;
        int n = Math.min(this.rows.size(), this.headerRows);
        for (int i = 0; i < n; ++i) {
            PdfPRow pdfPRow = (PdfPRow)this.rows.get(i);
            if (pdfPRow == null) continue;
            f += pdfPRow.getMaxHeights();
        }
        return f;
    }

    public float getFooterHeight() {
        float f = 0.0f;
        int n = Math.min(0, this.headerRows - this.footerRows);
        int n2 = Math.min(this.rows.size(), this.footerRows);
        for (int i = n; i < n2; ++i) {
            PdfPRow pdfPRow = (PdfPRow)this.rows.get(i);
            if (pdfPRow == null) continue;
            f += pdfPRow.getMaxHeights();
        }
        return f;
    }

    public boolean deleteRow(int n) {
        PdfPRow pdfPRow;
        if (n < 0 || n >= this.rows.size()) {
            return false;
        }
        if (this.totalWidth > 0.0f && (pdfPRow = (PdfPRow)this.rows.get(n)) != null) {
            this.totalHeight -= pdfPRow.getMaxHeights();
        }
        this.rows.remove(n);
        return true;
    }

    public boolean deleteLastRow() {
        return this.deleteRow(this.rows.size() - 1);
    }

    public void deleteBodyRows() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.headerRows; ++i) {
            arrayList.add(this.rows.get(i));
        }
        this.rows = arrayList;
        this.totalHeight = 0.0f;
        if (this.totalWidth > 0.0f) {
            this.totalHeight = this.getHeaderHeight();
        }
    }

    public int getNumberOfColumns() {
        return this.relativeWidths.length;
    }

    public int getHeaderRows() {
        return this.headerRows;
    }

    public void setHeaderRows(int n) {
        if (n < 0) {
            n = 0;
        }
        this.headerRows = n;
    }

    public ArrayList getChunks() {
        return new ArrayList();
    }

    public int type() {
        return 23;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return true;
    }

    public boolean process(ElementListener elementListener) {
        try {
            return elementListener.add(this);
        }
        catch (DocumentException var2_2) {
            return false;
        }
    }

    public float getWidthPercentage() {
        return this.widthPercentage;
    }

    public void setWidthPercentage(float f) {
        this.widthPercentage = f;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int n) {
        this.horizontalAlignment = n;
    }

    public PdfPRow getRow(int n) {
        return (PdfPRow)this.rows.get(n);
    }

    public ArrayList getRows() {
        return this.rows;
    }

    public void setTableEvent(PdfPTableEvent pdfPTableEvent) {
        if (pdfPTableEvent == null) {
            this.tableEvent = null;
        } else if (this.tableEvent == null) {
            this.tableEvent = pdfPTableEvent;
        } else if (this.tableEvent instanceof PdfPTableEventForwarder) {
            ((PdfPTableEventForwarder)this.tableEvent).addTableEvent(pdfPTableEvent);
        } else {
            PdfPTableEventForwarder pdfPTableEventForwarder = new PdfPTableEventForwarder();
            pdfPTableEventForwarder.addTableEvent(this.tableEvent);
            pdfPTableEventForwarder.addTableEvent(pdfPTableEvent);
            this.tableEvent = pdfPTableEventForwarder;
        }
    }

    public PdfPTableEvent getTableEvent() {
        return this.tableEvent;
    }

    public float[] getAbsoluteWidths() {
        return this.absoluteWidths;
    }

    float[][] getEventWidths(float f, int n, int n2, boolean bl) {
        if (bl) {
            n = Math.max(n, this.headerRows);
            n2 = Math.max(n2, this.headerRows);
        }
        float[][] arrarrf = new float[(bl ? this.headerRows : 0) + n2 - n][];
        if (this.isColspan) {
            int n3 = 0;
            if (bl) {
                for (int i = 0; i < this.headerRows; ++i) {
                    PdfPRow pdfPRow = (PdfPRow)this.rows.get(i);
                    if (pdfPRow == null) {
                        ++n3;
                        continue;
                    }
                    arrarrf[n3++] = pdfPRow.getEventWidth(f);
                }
            }
            while (n < n2) {
                PdfPRow pdfPRow = (PdfPRow)this.rows.get(n);
                if (pdfPRow == null) {
                    ++n3;
                } else {
                    arrarrf[n3++] = pdfPRow.getEventWidth(f);
                }
                ++n;
            }
        } else {
            int n4;
            float[] arrf = new float[this.absoluteWidths.length + 1];
            arrf[0] = f;
            for (n4 = 0; n4 < this.absoluteWidths.length; ++n4) {
                arrf[n4 + 1] = arrf[n4] + this.absoluteWidths[n4];
            }
            for (n4 = 0; n4 < arrarrf.length; ++n4) {
                arrarrf[n4] = arrf;
            }
        }
        return arrarrf;
    }

    public boolean isSkipFirstHeader() {
        return this.skipFirstHeader;
    }

    public void setSkipFirstHeader(boolean bl) {
        this.skipFirstHeader = bl;
    }

    public void setRunDirection(int n) {
        if (n < 0 || n > 3) {
            throw new RuntimeException("Invalid run direction: " + n);
        }
        this.runDirection = n;
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public boolean isLockedWidth() {
        return this.lockedWidth;
    }

    public void setLockedWidth(boolean bl) {
        this.lockedWidth = bl;
    }

    public boolean isSplitRows() {
        return this.splitRows;
    }

    public void setSplitRows(boolean bl) {
        this.splitRows = bl;
    }

    public void setSpacingBefore(float f) {
        this.spacingBefore = f;
    }

    public void setSpacingAfter(float f) {
        this.spacingAfter = f;
    }

    public float spacingBefore() {
        return this.spacingBefore;
    }

    public float spacingAfter() {
        return this.spacingAfter;
    }

    public boolean isExtendLastRow() {
        return this.extendLastRow;
    }

    public void setExtendLastRow(boolean bl) {
        this.extendLastRow = bl;
    }

    public boolean isHeadersInEvent() {
        return this.headersInEvent;
    }

    public void setHeadersInEvent(boolean bl) {
        this.headersInEvent = bl;
    }

    public boolean isSplitLate() {
        return this.splitLate;
    }

    public void setSplitLate(boolean bl) {
        this.splitLate = bl;
    }

    public void setKeepTogether(boolean bl) {
        this.keepTogether = bl;
    }

    public boolean getKeepTogether() {
        return this.keepTogether;
    }

    public int getFooterRows() {
        return this.footerRows;
    }

    public void setFooterRows(int n) {
        if (n < 0) {
            n = 0;
        }
        this.footerRows = n;
    }

    public void completeRow() {
        while (this.currentRowIdx > 0) {
            this.addCell(this.defaultCell);
        }
    }

    public void flushContent() {
        this.deleteBodyRows();
        this.setSkipFirstHeader(true);
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean bl) {
        this.complete = bl;
    }
}

