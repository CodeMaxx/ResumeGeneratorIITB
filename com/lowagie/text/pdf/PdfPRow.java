/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import java.awt.Color;

public class PdfPRow {
    public static final float BOTTOM_LIMIT = -1.07374182E9f;
    protected PdfPCell[] cells;
    protected float[] widths;
    protected float maxHeight = 0.0f;
    protected boolean calculated = false;
    private int[] canvasesPos;

    public PdfPRow(PdfPCell[] arrpdfPCell) {
        this.cells = arrpdfPCell;
        this.widths = new float[arrpdfPCell.length];
    }

    public PdfPRow(PdfPRow pdfPRow) {
        this.maxHeight = pdfPRow.maxHeight;
        this.calculated = pdfPRow.calculated;
        this.cells = new PdfPCell[pdfPRow.cells.length];
        for (int i = 0; i < this.cells.length; ++i) {
            if (pdfPRow.cells[i] == null) continue;
            this.cells[i] = new PdfPCell(pdfPRow.cells[i]);
        }
        this.widths = new float[this.cells.length];
        System.arraycopy(pdfPRow.widths, 0, this.widths, 0, this.cells.length);
    }

    public boolean setWidths(float[] arrf) {
        if (arrf.length != this.cells.length) {
            return false;
        }
        System.arraycopy(arrf, 0, this.widths, 0, this.cells.length);
        float f = 0.0f;
        this.calculated = false;
        for (int i = 0; i < arrf.length; ++i) {
            PdfPCell pdfPCell = this.cells[i];
            pdfPCell.setLeft(f);
            int n = i + pdfPCell.getColspan();
            while (i < n) {
                f += arrf[i];
                ++i;
            }
            --i;
            pdfPCell.setRight(f);
            pdfPCell.setTop(0.0f);
        }
        return true;
    }

    public float calculateHeights() {
        this.maxHeight = 0.0f;
        for (int i = 0; i < this.cells.length; ++i) {
            float f;
            float f2;
            PdfPCell pdfPCell = this.cells[i];
            if (pdfPCell == null) continue;
            Image image = pdfPCell.getImage();
            if (image != null) {
                image.scalePercent(100.0f);
                f2 = image.getScaledWidth();
                if (pdfPCell.getRotation() == 90 || pdfPCell.getRotation() == 270) {
                    f2 = image.getScaledHeight();
                }
                f = (pdfPCell.getRight() - pdfPCell.getEffectivePaddingRight() - pdfPCell.getEffectivePaddingLeft() - pdfPCell.getLeft()) / f2;
                image.scalePercent(f * 100.0f);
                float f3 = image.getScaledHeight();
                if (pdfPCell.getRotation() == 90 || pdfPCell.getRotation() == 270) {
                    f3 = image.getScaledWidth();
                }
                pdfPCell.setBottom(pdfPCell.getTop() - pdfPCell.getEffectivePaddingTop() - pdfPCell.getEffectivePaddingBottom() - f3);
            } else if (pdfPCell.getRotation() == 0 || pdfPCell.getRotation() == 180) {
                f2 = pdfPCell.isNoWrap() ? 20000.0f : pdfPCell.getRight() - pdfPCell.getEffectivePaddingRight();
                f = pdfPCell.getFixedHeight() > 0.0f ? pdfPCell.getTop() - pdfPCell.getEffectivePaddingTop() + pdfPCell.getEffectivePaddingBottom() - pdfPCell.getFixedHeight() : -1.07374182E9f;
                ColumnText columnText = ColumnText.duplicate(pdfPCell.getColumn());
                this.setColumn(columnText, pdfPCell.getLeft() + pdfPCell.getEffectivePaddingLeft(), f, f2, pdfPCell.getTop() - pdfPCell.getEffectivePaddingTop());
                try {
                    columnText.go(true);
                }
                catch (DocumentException var7_11) {
                    throw new ExceptionConverter(var7_11);
                }
                float f4 = columnText.getYLine();
                if (pdfPCell.isUseDescender()) {
                    f4 += columnText.getDescender();
                }
                pdfPCell.setBottom(f4 - pdfPCell.getEffectivePaddingBottom());
            } else if (pdfPCell.getFixedHeight() > 0.0f) {
                pdfPCell.setBottom(pdfPCell.getTop() - pdfPCell.getFixedHeight());
            } else {
                ColumnText columnText = ColumnText.duplicate(pdfPCell.getColumn());
                this.setColumn(columnText, 0.0f, pdfPCell.getLeft() + pdfPCell.getEffectivePaddingLeft(), 20000.0f, pdfPCell.getRight() - pdfPCell.getEffectivePaddingRight());
                try {
                    columnText.go(true);
                }
                catch (DocumentException var5_7) {
                    throw new ExceptionConverter(var5_7);
                }
                pdfPCell.setBottom(pdfPCell.getTop() - pdfPCell.getEffectivePaddingTop() - pdfPCell.getEffectivePaddingBottom() - columnText.getFilledWidth());
            }
            f2 = pdfPCell.getFixedHeight();
            if (f2 <= 0.0f) {
                f2 = pdfPCell.getHeight();
            }
            if (f2 < pdfPCell.getFixedHeight()) {
                f2 = pdfPCell.getFixedHeight();
            } else if (f2 < pdfPCell.getMinimumHeight()) {
                f2 = pdfPCell.getMinimumHeight();
            }
            if (f2 <= this.maxHeight) continue;
            this.maxHeight = f2;
        }
        this.calculated = true;
        return this.maxHeight;
    }

    public void writeBorderAndBackground(float f, float f2, PdfPCell pdfPCell, PdfContentByte[] arrpdfContentByte) {
        PdfContentByte pdfContentByte = arrpdfContentByte[2];
        PdfContentByte pdfContentByte2 = arrpdfContentByte[1];
        float f3 = pdfPCell.getLeft() + f;
        float f4 = pdfPCell.getTop() + f2;
        float f5 = pdfPCell.getRight() + f;
        float f6 = f4 - this.maxHeight;
        Color color = pdfPCell.getBackgroundColor();
        if (color != null) {
            pdfContentByte2.setColorFill(color);
            pdfContentByte2.rectangle(f3, f6, f5 - f3, f4 - f6);
            pdfContentByte2.fill();
        }
        if (pdfPCell.hasBorders()) {
            if (pdfPCell.isUseVariableBorders()) {
                Rectangle rectangle = new Rectangle(pdfPCell.getLeft() + f, pdfPCell.getTop() - this.maxHeight + f2, pdfPCell.getRight() + f, pdfPCell.getTop() + f2);
                rectangle.cloneNonPositionParameters(pdfPCell);
                rectangle.setBackgroundColor(null);
                pdfContentByte.rectangle(rectangle);
            } else {
                Color color2;
                if (pdfPCell.getBorderWidth() != -1.0f) {
                    pdfContentByte.setLineWidth(pdfPCell.getBorderWidth());
                }
                if ((color2 = pdfPCell.getBorderColor()) != null) {
                    pdfContentByte.setColorStroke(color2);
                }
                if (pdfPCell.hasBorder(15)) {
                    pdfContentByte.rectangle(f3, f6, f5 - f3, f4 - f6);
                } else {
                    if (pdfPCell.hasBorder(8)) {
                        pdfContentByte.moveTo(f5, f6);
                        pdfContentByte.lineTo(f5, f4);
                    }
                    if (pdfPCell.hasBorder(4)) {
                        pdfContentByte.moveTo(f3, f6);
                        pdfContentByte.lineTo(f3, f4);
                    }
                    if (pdfPCell.hasBorder(2)) {
                        pdfContentByte.moveTo(f3, f6);
                        pdfContentByte.lineTo(f5, f6);
                    }
                    if (pdfPCell.hasBorder(1)) {
                        pdfContentByte.moveTo(f3, f4);
                        pdfContentByte.lineTo(f5, f4);
                    }
                }
                pdfContentByte.stroke();
                if (color2 != null) {
                    pdfContentByte.resetRGBColorStroke();
                }
            }
        }
    }

    private void saveAndRotateCanvases(PdfContentByte[] arrpdfContentByte, float f, float f2, float f3, float f4, float f5, float f6) {
        int n = 4;
        if (this.canvasesPos == null) {
            this.canvasesPos = new int[n * 2];
        }
        for (int i = 0; i < n; ++i) {
            ByteBuffer byteBuffer = arrpdfContentByte[i].getInternalBuffer();
            this.canvasesPos[i * 2] = byteBuffer.size();
            arrpdfContentByte[i].saveState();
            arrpdfContentByte[i].concatCTM(f, f2, f3, f4, f5, f6);
            this.canvasesPos[i * 2 + 1] = byteBuffer.size();
        }
    }

    private void restoreCanvases(PdfContentByte[] arrpdfContentByte) {
        int n = 4;
        for (int i = 0; i < n; ++i) {
            ByteBuffer byteBuffer = arrpdfContentByte[i].getInternalBuffer();
            int n2 = byteBuffer.size();
            arrpdfContentByte[i].restoreState();
            if (n2 != this.canvasesPos[i * 2 + 1]) continue;
            byteBuffer.setSize(this.canvasesPos[i * 2]);
        }
    }

    private float setColumn(ColumnText columnText, float f, float f2, float f3, float f4) {
        if (f > f3) {
            f3 = f;
        }
        if (f2 > f4) {
            f4 = f2;
        }
        columnText.setSimpleColumn(f, f2, f3, f4);
        return f4;
    }

    public void writeCells(int n, int n2, float f, float f2, PdfContentByte[] arrpdfContentByte) {
        int n3;
        if (!this.calculated) {
            this.calculateHeights();
        }
        if (n2 < 0) {
            n2 = this.cells.length;
        }
        n2 = Math.min(n2, this.cells.length);
        if (n < 0) {
            n = 0;
        }
        if (n >= n2) {
            return;
        }
        for (n3 = n; n3 >= 0 && this.cells[n3] == null; --n3) {
            f -= this.widths[n3 - 1];
        }
        f -= this.cells[n3].getLeft();
        for (int i = n3; i < n2; ++i) {
            reference var13_17;
            Object object;
            float f3;
            float f32;
            PdfPCell pdfPCell = this.cells[i];
            if (pdfPCell == null) continue;
            this.writeBorderAndBackground(f, f2, pdfPCell, arrpdfContentByte);
            Image image = pdfPCell.getImage();
            float f5 = 0.0f;
            switch (pdfPCell.getVerticalAlignment()) {
                case 6: {
                    f5 = pdfPCell.getTop() + f2 - this.maxHeight + pdfPCell.getHeight() - pdfPCell.getEffectivePaddingTop();
                    break;
                }
                case 5: {
                    f5 = pdfPCell.getTop() + f2 + (pdfPCell.getHeight() - this.maxHeight) / 2.0f - pdfPCell.getEffectivePaddingTop();
                    break;
                }
                default: {
                    f5 = pdfPCell.getTop() + f2 - pdfPCell.getEffectivePaddingTop();
                }
            }
            if (image != null) {
                if (pdfPCell.getRotation() != 0) {
                    image = Image.getInstance(image);
                    image.setRotation(image.getImageRotation() + (float)((double)pdfPCell.getRotation() * 3.141592653589793 / 180.0));
                }
                boolean bl = false;
                if (pdfPCell.getHeight() > this.maxHeight) {
                    image.scalePercent(100.0f);
                    f32 = (this.maxHeight - pdfPCell.getEffectivePaddingTop() - pdfPCell.getEffectivePaddingBottom()) / image.getScaledHeight();
                    image.scalePercent(f32 * 100.0f);
                    bl = true;
                }
                f32 = pdfPCell.getLeft() + f + pdfPCell.getEffectivePaddingLeft();
                if (bl) {
                    switch (pdfPCell.getHorizontalAlignment()) {
                        case 1: {
                            f32 = f + (pdfPCell.getLeft() + pdfPCell.getEffectivePaddingLeft() + pdfPCell.getRight() - pdfPCell.getEffectivePaddingRight() - image.getScaledWidth()) / 2.0f;
                            break;
                        }
                        case 2: {
                            f32 = f + pdfPCell.getRight() - pdfPCell.getEffectivePaddingRight() - image.getScaledWidth();
                            break;
                        }
                    }
                    f5 = pdfPCell.getTop() + f2 - pdfPCell.getEffectivePaddingTop();
                }
                image.setAbsolutePosition(f32, f5 - image.getScaledHeight());
                try {
                    arrpdfContentByte[3].addImage(image);
                }
                catch (DocumentException v0) {
                    var13_17 = (reference)v0;
                    throw new ExceptionConverter(var13_17);
                }
            }
            if (pdfPCell.getRotation() == 90 || pdfPCell.getRotation() == 270) {
                float f6 = this.maxHeight - pdfPCell.getEffectivePaddingTop() - pdfPCell.getEffectivePaddingBottom();
                f32 = pdfPCell.getWidth() - pdfPCell.getEffectivePaddingLeft() - pdfPCell.getEffectivePaddingRight();
                ColumnText columnText = ColumnText.duplicate(pdfPCell.getColumn());
                columnText.setCanvases(arrpdfContentByte);
                columnText.setSimpleColumn(0.0f, 0.0f, f6 + 0.001f, - f32);
                try {
                    columnText.go(true);
                }
                catch (DocumentException columnText) {
                    throw new ExceptionConverter(columnText);
                }
                columnText = (DocumentException)(- columnText.getYLine());
                if (f6 <= 0.0f || f32 <= 0.0f) {
                    columnText = (DocumentException)0.0f;
                }
                if (columnText > 0.0f) {
                    if (pdfPCell.isUseDescender()) {
                        columnText -= columnText.getDescender();
                    }
                    ColumnText columnText2 = ColumnText.duplicate(pdfPCell.getColumn());
                    columnText2.setCanvases(arrpdfContentByte);
                    columnText2.setSimpleColumn(0.0f, -0.001f, f6 + 0.001f, (float)columnText);
                    if (pdfPCell.getRotation() == 90) {
                        object = pdfPCell.getTop() + f2 - this.maxHeight + pdfPCell.getEffectivePaddingBottom();
                        switch (pdfPCell.getVerticalAlignment()) {
                            case 6: {
                                f3 = pdfPCell.getLeft() + f + pdfPCell.getWidth() - pdfPCell.getEffectivePaddingRight();
                                break;
                            }
                            case 5: {
                                f3 = pdfPCell.getLeft() + f + (pdfPCell.getWidth() + pdfPCell.getEffectivePaddingLeft() - pdfPCell.getEffectivePaddingRight() + columnText) / 2.0f;
                                break;
                            }
                            default: {
                                f3 = pdfPCell.getLeft() + f + pdfPCell.getEffectivePaddingLeft() + columnText;
                            }
                        }
                        this.saveAndRotateCanvases(arrpdfContentByte, 0.0f, 1.0f, -1.0f, 0.0f, f3, (float)object);
                    } else {
                        object = pdfPCell.getTop() + f2 - pdfPCell.getEffectivePaddingTop();
                        switch (pdfPCell.getVerticalAlignment()) {
                            case 6: {
                                f3 = pdfPCell.getLeft() + f + pdfPCell.getEffectivePaddingLeft();
                                break;
                            }
                            case 5: {
                                f3 = pdfPCell.getLeft() + f + (pdfPCell.getWidth() + pdfPCell.getEffectivePaddingLeft() - pdfPCell.getEffectivePaddingRight() - columnText) / 2.0f;
                                break;
                            }
                            default: {
                                f3 = pdfPCell.getLeft() + f + pdfPCell.getWidth() - pdfPCell.getEffectivePaddingRight() - columnText;
                            }
                        }
                        this.saveAndRotateCanvases(arrpdfContentByte, 0.0f, -1.0f, 1.0f, 0.0f, f3, (float)object);
                    }
                    try {
                        columnText2.go();
                    }
                    catch (DocumentException var17_26) {
                        throw new ExceptionConverter(var17_26);
                    }
                    finally {
                        this.restoreCanvases(arrpdfContentByte);
                    }
                }
            } else {
                float f7 = pdfPCell.getFixedHeight();
                f32 = pdfPCell.getRight() + f - pdfPCell.getEffectivePaddingRight();
                var13_17 = pdfPCell.getLeft() + f + pdfPCell.getEffectivePaddingLeft();
                if (pdfPCell.isNoWrap()) {
                    switch (pdfPCell.getHorizontalAlignment()) {
                        case 1: {
                            f32 += 10000.0f;
                            var13_17 -= 10000.0f;
                            break;
                        }
                        case 2: {
                            var13_17 -= 20000.0f;
                            break;
                        }
                        default: {
                            f32 += 20000.0f;
                        }
                    }
                }
                ColumnText columnText = ColumnText.duplicate(pdfPCell.getColumn());
                columnText.setCanvases(arrpdfContentByte);
                f3 = f5 - (this.maxHeight - pdfPCell.getEffectivePaddingTop() - pdfPCell.getEffectivePaddingBottom());
                if (f7 > 0.0f && pdfPCell.getHeight() > this.maxHeight) {
                    f5 = pdfPCell.getTop() + f2 - pdfPCell.getEffectivePaddingTop();
                    f3 = pdfPCell.getTop() + f2 - this.maxHeight + pdfPCell.getEffectivePaddingBottom();
                }
                if ((f5 > f3 || columnText.zeroHeightElement()) && var13_17 < f32) {
                    columnText.setSimpleColumn((float)var13_17, f3 - 0.001f, f32, f5);
                    if (pdfPCell.getRotation() == 180) {
                        object = var13_17 + f32;
                        float f4 = f2 + f2 - this.maxHeight + pdfPCell.getEffectivePaddingBottom() - pdfPCell.getEffectivePaddingTop();
                        this.saveAndRotateCanvases(arrpdfContentByte, -1.0f, 0.0f, 0.0f, -1.0f, (float)object, f4);
                    }
                    try {
                        columnText.go();
                    }
                    catch (DocumentException var16_24) {
                        throw new ExceptionConverter(var16_24);
                    }
                    finally {
                        if (pdfPCell.getRotation() == 180) {
                            this.restoreCanvases(arrpdfContentByte);
                        }
                    }
                }
            }
            PdfPCellEvent pdfPCellEvent = pdfPCell.getCellEvent();
            if (pdfPCellEvent == null) continue;
            Rectangle rectangle = new Rectangle(pdfPCell.getLeft() + f, pdfPCell.getTop() + f2 - this.maxHeight, pdfPCell.getRight() + f, pdfPCell.getTop() + f2);
            pdfPCellEvent.cellLayout(pdfPCell, rectangle, arrpdfContentByte);
        }
    }

    public boolean isCalculated() {
        return this.calculated;
    }

    public float getMaxHeights() {
        if (this.calculated) {
            return this.maxHeight;
        }
        return this.calculateHeights();
    }

    public void setMaxHeights(float f) {
        this.maxHeight = f;
    }

    float[] getEventWidth(float f) {
        int n = 0;
        for (int i = 0; i < this.cells.length; ++i) {
            if (this.cells[i] == null) continue;
            ++n;
        }
        float[] arrf = new float[n + 1];
        n = 0;
        arrf[n++] = f;
        for (int j = 0; j < this.cells.length; ++j) {
            if (this.cells[j] == null) continue;
            arrf[n] = arrf[n - 1] + this.cells[j].getWidth();
            ++n;
        }
        return arrf;
    }

    public PdfPRow splitRow(float f) {
        PdfPCell pdfPCell;
        int n;
        PdfPCell[] arrpdfPCell = new PdfPCell[this.cells.length];
        float[] arrf = new float[this.cells.length * 2];
        boolean bl = true;
        for (n = 0; n < this.cells.length; ++n) {
            pdfPCell = this.cells[n];
            if (pdfPCell == null) continue;
            arrf[n * 2] = pdfPCell.getFixedHeight();
            arrf[n * 2 + 1] = pdfPCell.getMinimumHeight();
            Image image = pdfPCell.getImage();
            PdfPCell pdfPCell2 = new PdfPCell(pdfPCell);
            if (image != null) {
                if (f > pdfPCell.getEffectivePaddingBottom() + pdfPCell.getEffectivePaddingTop() + 2.0f) {
                    pdfPCell2.setPhrase(null);
                    bl = false;
                }
            } else {
                boolean bl2;
                int n2;
                float f2;
                ColumnText columnText = ColumnText.duplicate(pdfPCell.getColumn());
                if (pdfPCell.getRotation() == 90 || pdfPCell.getRotation() == 270) {
                    f2 = this.setColumn(columnText, pdfPCell.getTop() - f + pdfPCell.getEffectivePaddingBottom(), pdfPCell.getLeft() + pdfPCell.getEffectivePaddingLeft(), pdfPCell.getTop() - pdfPCell.getEffectivePaddingTop(), pdfPCell.getRight() - pdfPCell.getEffectivePaddingRight());
                } else {
                    float f3 = pdfPCell.isNoWrap() ? 20000.0f : pdfPCell.getRight() - pdfPCell.getEffectivePaddingRight();
                    float f4 = pdfPCell.getTop() - f + pdfPCell.getEffectivePaddingBottom();
                    float f5 = pdfPCell.getTop() - pdfPCell.getEffectivePaddingTop();
                    f2 = this.setColumn(columnText, pdfPCell.getLeft() + pdfPCell.getEffectivePaddingLeft(), f4, f3, f5);
                }
                try {
                    n2 = columnText.go(true);
                }
                catch (DocumentException var12_17) {
                    throw new ExceptionConverter(var12_17);
                }
                boolean bl3 = bl2 = columnText.getYLine() == f2;
                if (bl2) {
                    columnText = ColumnText.duplicate(pdfPCell.getColumn());
                }
                boolean bl4 = bl = bl && bl2;
                if ((n2 & 1) == 0 || bl2) {
                    pdfPCell2.setColumn(columnText);
                    columnText.setFilledWidth(0.0f);
                } else {
                    pdfPCell2.setPhrase(null);
                }
            }
            arrpdfPCell[n] = pdfPCell2;
            pdfPCell.setFixedHeight(f);
        }
        if (bl) {
            for (n = 0; n < this.cells.length; ++n) {
                pdfPCell = this.cells[n];
                if (pdfPCell == null) continue;
                float f6 = arrf[n * 2];
                float f7 = arrf[n * 2 + 1];
                if (f6 <= 0.0f) {
                    pdfPCell.setMinimumHeight(f7);
                    continue;
                }
                pdfPCell.setFixedHeight(f6);
            }
            return null;
        }
        this.calculateHeights();
        PdfPRow pdfPRow = new PdfPRow(arrpdfPCell);
        pdfPRow.widths = (float[])this.widths.clone();
        pdfPRow.calculateHeights();
        return pdfPRow;
    }

    public PdfPCell[] getCells() {
        return this.cells;
    }
}

