/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDocument;
import java.util.ArrayList;

public class MultiColumnText
implements Element {
    public static final float AUTOMATIC = -1.0f;
    private float desiredHeight;
    private float totalHeight;
    private boolean overflow;
    private float top;
    private float pageBottom;
    private ColumnText columnText;
    private ArrayList columnDefs = new ArrayList();
    private boolean simple = true;
    private int currentColumn = 0;
    private float nextY = -1.0f;
    private boolean columnsRightToLeft = false;
    private PdfDocument document;

    public MultiColumnText() {
        this(-1.0f);
    }

    public MultiColumnText(float f) {
        this.desiredHeight = f;
        this.top = -1.0f;
        this.columnText = new ColumnText(null);
        this.totalHeight = 0.0f;
    }

    public MultiColumnText(float f, float f2) {
        this.desiredHeight = f2;
        this.top = f;
        this.nextY = f;
        this.columnText = new ColumnText(null);
        this.totalHeight = 0.0f;
    }

    public boolean isOverflow() {
        return this.overflow;
    }

    public void useColumnParams(ColumnText columnText) {
        this.columnText.setSimpleVars(columnText);
    }

    public void addColumn(float[] arrf, float[] arrf2) {
        ColumnDef columnDef = new ColumnDef(this, arrf, arrf2);
        this.simple = columnDef.isSimple();
        this.columnDefs.add(columnDef);
    }

    public void addSimpleColumn(float f, float f2) {
        ColumnDef columnDef = new ColumnDef(this, f, f2);
        this.columnDefs.add(columnDef);
    }

    public void addRegularColumns(float f, float f2, float f3, int n) {
        float f4 = f;
        float f5 = f2 - f;
        float f6 = (f5 - f3 * (float)(n - 1)) / (float)n;
        for (int i = 0; i < n; ++i) {
            this.addSimpleColumn(f4, f4 + f6);
            f4 += f6 + f3;
        }
    }

    public void addElement(Element element) throws DocumentException {
        if (this.simple) {
            this.columnText.addElement(element);
        } else if (element instanceof Phrase) {
            this.columnText.addText((Phrase)element);
        } else if (element instanceof Chunk) {
            this.columnText.addText((Chunk)element);
        } else {
            throw new DocumentException("Can't add " + element.getClass() + " to MultiColumnText with complex columns");
        }
    }

    public float write(PdfContentByte pdfContentByte, PdfDocument pdfDocument, float f) throws DocumentException {
        float f2;
        this.document = pdfDocument;
        this.columnText.setCanvas(pdfContentByte);
        if (this.columnDefs.isEmpty()) {
            throw new DocumentException("MultiColumnText has no columns");
        }
        this.overflow = false;
        this.pageBottom = pdfDocument.bottom();
        f2 = 0.0f;
        boolean bl = false;
        try {
            while (!bl) {
                if (this.top == -1.0f) {
                    this.top = pdfDocument.getVerticalPosition(true);
                } else if (this.nextY == -1.0f) {
                    this.nextY = pdfDocument.getVerticalPosition(true);
                }
                ColumnDef columnDef = (ColumnDef)this.columnDefs.get(this.getCurrentColumn());
                this.columnText.setYLine(this.top);
                float[] arrf = columnDef.resolvePositions(4);
                float[] arrf2 = columnDef.resolvePositions(8);
                if (pdfDocument.isMarginMirroring() && pdfDocument.getPageNumber() % 2 == 0) {
                    int n;
                    float f3 = pdfDocument.rightMargin() - pdfDocument.left();
                    arrf = (float[])arrf.clone();
                    arrf2 = (float[])arrf2.clone();
                    for (n = 0; n < arrf.length; n += 2) {
                        float[] arrf3 = arrf;
                        int n2 = n;
                        arrf3[n2] = arrf3[n2] - f3;
                    }
                    for (n = 0; n < arrf2.length; n += 2) {
                        float[] arrf4 = arrf2;
                        int n3 = n;
                        arrf4[n3] = arrf4[n3] - f3;
                    }
                }
                f2 = Math.max(f2, this.getHeight(arrf, arrf2));
                if (columnDef.isSimple()) {
                    this.columnText.setSimpleColumn(arrf[2], arrf[3], arrf2[0], arrf2[1]);
                } else {
                    this.columnText.setColumns(arrf, arrf2);
                }
                int n = this.columnText.go();
                if ((n & 1) != 0) {
                    bl = true;
                    this.top = this.columnText.getYLine();
                    continue;
                }
                if (this.shiftCurrentColumn()) {
                    this.top = this.nextY;
                    continue;
                }
                this.totalHeight += f2;
                if (this.desiredHeight != -1.0f && this.totalHeight >= this.desiredHeight) {
                    this.overflow = true;
                    break;
                }
                f = this.nextY;
                this.newPage();
                f2 = 0.0f;
            }
        }
        catch (DocumentException var6_7) {
            var6_7.printStackTrace();
            throw var6_7;
        }
        if (this.desiredHeight == -1.0f && this.columnDefs.size() == 1) {
            f2 = f - this.columnText.getYLine();
        }
        return f2;
    }

    private void newPage() throws DocumentException {
        this.resetCurrentColumn();
        if (this.desiredHeight == -1.0f) {
            this.nextY = -1.0f;
            this.top = -1.0f;
        } else {
            this.top = this.nextY;
        }
        this.totalHeight = 0.0f;
        if (this.document != null) {
            this.document.newPage();
        }
    }

    private float getHeight(float[] arrf, float[] arrf2) {
        int n;
        float f = Float.MIN_VALUE;
        float f2 = Float.MAX_VALUE;
        for (n = 0; n < arrf.length; n += 2) {
            f2 = Math.min(f2, arrf[n + 1]);
            f = Math.max(f, arrf[n + 1]);
        }
        for (n = 0; n < arrf2.length; n += 2) {
            f2 = Math.min(f2, arrf2[n + 1]);
            f = Math.max(f, arrf2[n + 1]);
        }
        return f - f2;
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
        return 40;
    }

    public ArrayList getChunks() {
        return null;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return false;
    }

    private float getColumnBottom() {
        if (this.desiredHeight == -1.0f) {
            return this.pageBottom;
        }
        return Math.max(this.top - (this.desiredHeight - this.totalHeight), this.pageBottom);
    }

    public void nextColumn() throws DocumentException {
        this.currentColumn = (this.currentColumn + 1) % this.columnDefs.size();
        this.top = this.nextY;
        if (this.currentColumn == 0) {
            this.newPage();
        }
    }

    public int getCurrentColumn() {
        if (this.columnsRightToLeft) {
            return this.columnDefs.size() - this.currentColumn - 1;
        }
        return this.currentColumn;
    }

    public void resetCurrentColumn() {
        this.currentColumn = 0;
    }

    public boolean shiftCurrentColumn() {
        if (this.currentColumn + 1 < this.columnDefs.size()) {
            ++this.currentColumn;
            return true;
        }
        return false;
    }

    public void setColumnsRightToLeft(boolean bl) {
        this.columnsRightToLeft = bl;
    }

    public void setSpaceCharRatio(float f) {
        this.columnText.setSpaceCharRatio(f);
    }

    public void setRunDirection(int n) {
        this.columnText.setRunDirection(n);
    }

    public void setArabicOptions(int n) {
        this.columnText.setArabicOptions(n);
    }

    public void setAlignment(int n) {
        this.columnText.setAlignment(n);
    }

    private class ColumnDef {
        private float[] left;
        private float[] right;
        private final /* synthetic */ MultiColumnText this$0;

        ColumnDef(MultiColumnText multiColumnText, float[] arrf, float[] arrf2) {
            this.this$0 = multiColumnText;
            this.left = arrf;
            this.right = arrf2;
        }

        ColumnDef(MultiColumnText multiColumnText, float f, float f2) {
            this.this$0 = multiColumnText;
            this.left = new float[4];
            this.left[0] = f;
            this.left[1] = multiColumnText.top;
            this.left[2] = f;
            this.left[3] = multiColumnText.desiredHeight == -1.0f || multiColumnText.top == -1.0f ? -1.0f : multiColumnText.top - multiColumnText.desiredHeight;
            this.right = new float[4];
            this.right[0] = f2;
            this.right[1] = multiColumnText.top;
            this.right[2] = f2;
            this.right[3] = multiColumnText.desiredHeight == -1.0f || multiColumnText.top == -1.0f ? -1.0f : multiColumnText.top - multiColumnText.desiredHeight;
        }

        float[] resolvePositions(int n) {
            if (n == 4) {
                return this.resolvePositions(this.left);
            }
            return this.resolvePositions(this.right);
        }

        private float[] resolvePositions(float[] arrf) {
            if (!this.isSimple()) {
                return arrf;
            }
            if (this.this$0.top == -1.0f) {
                throw new RuntimeException("resolvePositions called with top=AUTOMATIC (-1).  Top position must be set befure lines can be resolved");
            }
            arrf[1] = this.this$0.top;
            arrf[3] = this.this$0.getColumnBottom();
            return arrf;
        }

        private boolean isSimple() {
            return this.left.length == 4 && this.right.length == 4 && this.left[0] == this.left[2] && this.right[0] == this.right[2];
        }
    }

}

