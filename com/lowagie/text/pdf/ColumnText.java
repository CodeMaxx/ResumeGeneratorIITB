/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.pdf.BidiLine;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfFont;
import com.lowagie.text.pdf.PdfLine;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class ColumnText {
    public static final int AR_NOVOWEL = 1;
    public static final int AR_COMPOSEDTASHKEEL = 4;
    public static final int AR_LIG = 8;
    public static final int DIGITS_EN2AN = 32;
    public static final int DIGITS_AN2EN = 64;
    public static final int DIGITS_EN2AN_INIT_LR = 96;
    public static final int DIGITS_EN2AN_INIT_AL = 128;
    public static final int DIGIT_TYPE_AN = 0;
    public static final int DIGIT_TYPE_AN_EXTENDED = 256;
    protected int runDirection = 0;
    public static final float GLOBAL_SPACE_CHAR_RATIO = 0.0f;
    public static final int START_COLUMN = 0;
    public static final int NO_MORE_TEXT = 1;
    public static final int NO_MORE_COLUMN = 2;
    protected static final int LINE_STATUS_OK = 0;
    protected static final int LINE_STATUS_OFFLIMITS = 1;
    protected static final int LINE_STATUS_NOLINE = 2;
    protected float maxY;
    protected float minY;
    protected float leftX;
    protected float rightX;
    protected int alignment = 0;
    protected ArrayList leftWall;
    protected ArrayList rightWall;
    protected BidiLine bidiLine;
    protected float yLine;
    protected float currentLeading = 16.0f;
    protected float fixedLeading = 16.0f;
    protected float multipliedLeading = 0.0f;
    protected PdfContentByte canvas;
    protected PdfContentByte[] canvases;
    protected int lineStatus;
    protected float indent = 0.0f;
    protected float followingIndent = 0.0f;
    protected float rightIndent = 0.0f;
    protected float extraParagraphSpace = 0.0f;
    protected float rectangularWidth = -1.0f;
    protected boolean rectangularMode = false;
    private float spaceCharRatio = 0.0f;
    private boolean lastWasNewline = true;
    private int linesWritten;
    private float firstLineY;
    private boolean firstLineYDone = false;
    private int arabicOptions = 0;
    protected float descender;
    protected boolean composite = false;
    protected ColumnText compositeColumn;
    protected LinkedList compositeElements;
    protected int listIdx = 0;
    private boolean splittedRow;
    protected Phrase waitPhrase;
    private boolean useAscender = false;
    private float filledWidth;
    private boolean adjustFirstLine = true;

    public ColumnText(PdfContentByte pdfContentByte) {
        this.canvas = pdfContentByte;
    }

    public static ColumnText duplicate(ColumnText columnText) {
        ColumnText columnText2 = new ColumnText(null);
        columnText2.setACopy(columnText);
        return columnText2;
    }

    public ColumnText setACopy(ColumnText columnText) {
        this.setSimpleVars(columnText);
        if (columnText.bidiLine != null) {
            this.bidiLine = new BidiLine(columnText.bidiLine);
        }
        return this;
    }

    protected void setSimpleVars(ColumnText columnText) {
        this.maxY = columnText.maxY;
        this.minY = columnText.minY;
        this.alignment = columnText.alignment;
        this.leftWall = null;
        if (columnText.leftWall != null) {
            this.leftWall = new ArrayList(columnText.leftWall);
        }
        this.rightWall = null;
        if (columnText.rightWall != null) {
            this.rightWall = new ArrayList(columnText.rightWall);
        }
        this.yLine = columnText.yLine;
        this.currentLeading = columnText.currentLeading;
        this.fixedLeading = columnText.fixedLeading;
        this.multipliedLeading = columnText.multipliedLeading;
        this.canvas = columnText.canvas;
        this.canvases = columnText.canvases;
        this.lineStatus = columnText.lineStatus;
        this.indent = columnText.indent;
        this.followingIndent = columnText.followingIndent;
        this.rightIndent = columnText.rightIndent;
        this.extraParagraphSpace = columnText.extraParagraphSpace;
        this.rectangularWidth = columnText.rectangularWidth;
        this.rectangularMode = columnText.rectangularMode;
        this.spaceCharRatio = columnText.spaceCharRatio;
        this.lastWasNewline = columnText.lastWasNewline;
        this.linesWritten = columnText.linesWritten;
        this.arabicOptions = columnText.arabicOptions;
        this.runDirection = columnText.runDirection;
        this.descender = columnText.descender;
        this.composite = columnText.composite;
        this.splittedRow = columnText.splittedRow;
        if (columnText.composite) {
            this.compositeElements = new LinkedList(columnText.compositeElements);
            if (this.splittedRow) {
                PdfPTable pdfPTable = (PdfPTable)this.compositeElements.getFirst();
                this.compositeElements.set(0, new PdfPTable(pdfPTable));
            }
            if (columnText.compositeColumn != null) {
                this.compositeColumn = ColumnText.duplicate(columnText.compositeColumn);
            }
        }
        this.listIdx = columnText.listIdx;
        this.firstLineY = columnText.firstLineY;
        this.leftX = columnText.leftX;
        this.rightX = columnText.rightX;
        this.firstLineYDone = columnText.firstLineYDone;
        this.waitPhrase = columnText.waitPhrase;
        this.useAscender = columnText.useAscender;
        this.filledWidth = columnText.filledWidth;
        this.adjustFirstLine = columnText.adjustFirstLine;
    }

    private void addWaitingPhrase() {
        if (this.bidiLine == null && this.waitPhrase != null) {
            this.bidiLine = new BidiLine();
            Iterator iterator = this.waitPhrase.getChunks().iterator();
            while (iterator.hasNext()) {
                this.bidiLine.addChunk(new PdfChunk((Chunk)iterator.next(), null));
            }
            this.waitPhrase = null;
        }
    }

    public void addText(Phrase phrase) {
        if (phrase == null || this.composite) {
            return;
        }
        this.addWaitingPhrase();
        if (this.bidiLine == null) {
            this.waitPhrase = phrase;
            return;
        }
        Iterator iterator = phrase.getChunks().iterator();
        while (iterator.hasNext()) {
            this.bidiLine.addChunk(new PdfChunk((Chunk)iterator.next(), null));
        }
    }

    public void setText(Phrase phrase) {
        this.bidiLine = null;
        this.composite = false;
        this.compositeColumn = null;
        this.compositeElements = null;
        this.listIdx = 0;
        this.splittedRow = false;
        this.waitPhrase = phrase;
    }

    public void addText(Chunk chunk) {
        if (chunk == null || this.composite) {
            return;
        }
        this.addText(new Phrase(chunk));
    }

    public void addElement(Element element) {
        if (element == null) {
            return;
        }
        if (element instanceof Image) {
            Image image = (Image)element;
            PdfPTable pdfPTable = new PdfPTable(1);
            float f = image.getWidthPercentage();
            if (f == 0.0f) {
                pdfPTable.setTotalWidth(image.getScaledWidth());
                pdfPTable.setLockedWidth(true);
            } else {
                pdfPTable.setWidthPercentage(f);
            }
            pdfPTable.setSpacingAfter(image.getSpacingAfter());
            pdfPTable.setSpacingBefore(image.getSpacingBefore());
            switch (image.getAlignment()) {
                case 0: {
                    pdfPTable.setHorizontalAlignment(0);
                    break;
                }
                case 2: {
                    pdfPTable.setHorizontalAlignment(2);
                    break;
                }
                default: {
                    pdfPTable.setHorizontalAlignment(1);
                }
            }
            PdfPCell pdfPCell = new PdfPCell(image, true);
            pdfPCell.setPadding(0.0f);
            pdfPCell.setBorder(image.getBorder());
            pdfPCell.setBorderColor(image.getBorderColor());
            pdfPCell.setBorderWidth(image.getBorderWidth());
            pdfPCell.setBackgroundColor(image.getBackgroundColor());
            pdfPTable.addCell(pdfPCell);
            element = pdfPTable;
        }
        if (element.type() == 10) {
            element = new Paragraph((Chunk)element);
        } else if (element.type() == 11) {
            element = new Paragraph((Phrase)element);
        }
        if (element instanceof SimpleTable) {
            try {
                element = ((SimpleTable)element).createPdfPTable();
            }
            catch (DocumentException var2_3) {
                throw new IllegalArgumentException("Element not allowed.");
            }
        } else if (element.type() != 12 && element.type() != 14 && element.type() != 23 && element.type() != 55) {
            throw new IllegalArgumentException("Element not allowed.");
        }
        if (!this.composite) {
            this.composite = true;
            this.compositeElements = new LinkedList();
            this.bidiLine = null;
            this.waitPhrase = null;
        }
        this.compositeElements.add(element);
    }

    protected ArrayList convertColumn(float[] arrf) {
        if (arrf.length < 4) {
            throw new RuntimeException("No valid column line found.");
        }
        ArrayList<float[]> arrayList = new ArrayList<float[]>();
        for (int i = 0; i < arrf.length - 2; i += 2) {
            float f = arrf[i];
            float f2 = arrf[i + 1];
            float f3 = arrf[i + 2];
            float f4 = arrf[i + 3];
            if (f2 == f4) continue;
            float f5 = (f - f3) / (f2 - f4);
            float f6 = f - f5 * f2;
            float[] arrf2 = new float[]{Math.min(f2, f4), Math.max(f2, f4), f5, f6};
            arrayList.add(arrf2);
            this.maxY = Math.max(this.maxY, arrf2[1]);
            this.minY = Math.min(this.minY, arrf2[0]);
        }
        if (arrayList.isEmpty()) {
            throw new RuntimeException("No valid column line found.");
        }
        return arrayList;
    }

    protected float findLimitsPoint(ArrayList arrayList) {
        this.lineStatus = 0;
        if (this.yLine < this.minY || this.yLine > this.maxY) {
            this.lineStatus = 1;
            return 0.0f;
        }
        for (int i = 0; i < arrayList.size(); ++i) {
            float[] arrf = (float[])arrayList.get(i);
            if (this.yLine < arrf[0] || this.yLine > arrf[1]) continue;
            return arrf[2] * this.yLine + arrf[3];
        }
        this.lineStatus = 2;
        return 0.0f;
    }

    protected float[] findLimitsOneLine() {
        float f = this.findLimitsPoint(this.leftWall);
        if (this.lineStatus == 1 || this.lineStatus == 2) {
            return null;
        }
        float f2 = this.findLimitsPoint(this.rightWall);
        if (this.lineStatus == 2) {
            return null;
        }
        return new float[]{f, f2};
    }

    protected float[] findLimitsTwoLines() {
        float[] arrf;
        float[] arrf2;
        boolean bl = false;
        do {
            if (bl && this.currentLeading == 0.0f) {
                return null;
            }
            bl = true;
            arrf2 = this.findLimitsOneLine();
            if (this.lineStatus == 1) {
                return null;
            }
            this.yLine -= this.currentLeading;
            if (this.lineStatus == 2) continue;
            arrf = this.findLimitsOneLine();
            if (this.lineStatus == 1) {
                return null;
            }
            if (this.lineStatus == 2) {
                this.yLine -= this.currentLeading;
                continue;
            }
            if (arrf2[0] < arrf[1] && arrf[0] < arrf2[1]) break;
        } while (true);
        return new float[]{arrf2[0], arrf2[1], arrf[0], arrf[1]};
    }

    public void setColumns(float[] arrf, float[] arrf2) {
        this.maxY = -1.0E21f;
        this.minY = 1.0E21f;
        this.rightWall = this.convertColumn(arrf2);
        this.leftWall = this.convertColumn(arrf);
        this.rectangularWidth = -1.0f;
        this.rectangularMode = false;
    }

    public void setSimpleColumn(Phrase phrase, float f, float f2, float f3, float f4, float f5, int n) {
        this.addText(phrase);
        this.setSimpleColumn(f, f2, f3, f4, f5, n);
    }

    public void setSimpleColumn(float f, float f2, float f3, float f4, float f5, int n) {
        this.setLeading(f5);
        this.alignment = n;
        this.setSimpleColumn(f, f2, f3, f4);
    }

    public void setSimpleColumn(float f, float f2, float f3, float f4) {
        this.leftX = Math.min(f, f3);
        this.maxY = Math.max(f2, f4);
        this.minY = Math.min(f2, f4);
        this.rightX = Math.max(f, f3);
        this.yLine = this.maxY;
        this.rectangularWidth = this.rightX - this.leftX;
        if (this.rectangularWidth < 0.0f) {
            this.rectangularWidth = 0.0f;
        }
        this.rectangularMode = true;
    }

    public void setLeading(float f) {
        this.fixedLeading = f;
        this.multipliedLeading = 0.0f;
    }

    public void setLeading(float f, float f2) {
        this.fixedLeading = f;
        this.multipliedLeading = f2;
    }

    public float getLeading() {
        return this.fixedLeading;
    }

    public float getMultipliedLeading() {
        return this.multipliedLeading;
    }

    public void setYLine(float f) {
        this.yLine = f;
    }

    public float getYLine() {
        return this.yLine;
    }

    public void setAlignment(int n) {
        this.alignment = n;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setIndent(float f) {
        this.indent = f;
        this.lastWasNewline = true;
    }

    public float getIndent() {
        return this.indent;
    }

    public void setFollowingIndent(float f) {
        this.followingIndent = f;
        this.lastWasNewline = true;
    }

    public float getFollowingIndent() {
        return this.followingIndent;
    }

    public void setRightIndent(float f) {
        this.rightIndent = f;
        this.lastWasNewline = true;
    }

    public float getRightIndent() {
        return this.rightIndent;
    }

    public int go() throws DocumentException {
        return this.go(false);
    }

    public int go(boolean bl) throws DocumentException {
        PdfContentByte pdfContentByte;
        int n;
        boolean bl2;
        block28 : {
            Float f;
            if (this.composite) {
                return this.goComposite(bl);
            }
            this.addWaitingPhrase();
            if (this.bidiLine == null) {
                return 1;
            }
            this.descender = 0.0f;
            this.linesWritten = 0;
            bl2 = false;
            float f2 = this.spaceCharRatio;
            Object[] arrobject = new Object[2];
            PdfFont pdfFont = null;
            arrobject[1] = f = new Float(0.0f);
            PdfDocument pdfDocument = null;
            PdfContentByte pdfContentByte2 = null;
            pdfContentByte = null;
            this.firstLineY = Float.NaN;
            int n2 = 1;
            if (this.runDirection != 0) {
                n2 = this.runDirection;
            }
            if (this.canvas != null) {
                pdfContentByte2 = this.canvas;
                pdfDocument = this.canvas.getPdfDocument();
                pdfContentByte = this.canvas.getDuplicate();
            } else if (!bl) {
                throw new NullPointerException("ColumnText.go with simulate==false and text==null.");
            }
            if (!bl) {
                if (f2 == 0.0f) {
                    f2 = pdfContentByte.getPdfWriter().getSpaceCharRatio();
                } else if (f2 < 0.001f) {
                    f2 = 0.001f;
                }
            }
            float f3 = 0.0f;
            n = 0;
            if (this.rectangularMode) {
                do {
                    float f4 = f3 = this.lastWasNewline ? this.indent : this.followingIndent;
                    if (this.rectangularWidth <= f3 + this.rightIndent) {
                        n = 2;
                        if (this.bidiLine.isEmpty()) {
                            n |= 1;
                        }
                        break block28;
                    }
                    if (this.bidiLine.isEmpty()) {
                        n = 1;
                        break block28;
                    }
                    PdfLine pdfLine = this.bidiLine.processLine(this.leftX, this.rectangularWidth - f3 - this.rightIndent, this.alignment, n2, this.arabicOptions);
                    if (pdfLine == null) {
                        n = 1;
                        break block28;
                    }
                    float f5 = pdfLine.getMaxSizeSimple();
                    this.currentLeading = this.isUseAscender() && Float.isNaN(this.firstLineY) ? pdfLine.getAscender() : this.fixedLeading + f5 * this.multipliedLeading;
                    if (this.yLine > this.maxY || this.yLine - this.currentLeading < this.minY) {
                        n = 2;
                        this.bidiLine.restore();
                        break block28;
                    }
                    this.yLine -= this.currentLeading;
                    if (!bl && !bl2) {
                        pdfContentByte.beginText();
                        bl2 = true;
                    }
                    if (Float.isNaN(this.firstLineY)) {
                        this.firstLineY = this.yLine;
                    }
                    this.updateFilledWidth(this.rectangularWidth - pdfLine.widthLeft());
                    if (!bl) {
                        arrobject[0] = pdfFont;
                        pdfContentByte.setTextMatrix(this.leftX + (pdfLine.isRTL() ? this.rightIndent : f3) + pdfLine.indentLeft(), this.yLine);
                        pdfDocument.writeLineToContent(pdfLine, pdfContentByte, pdfContentByte2, arrobject, f2);
                        pdfFont = (PdfFont)arrobject[0];
                    }
                    this.lastWasNewline = pdfLine.isNewlineSplit();
                    this.yLine -= pdfLine.isNewlineSplit() ? this.extraParagraphSpace : 0.0f;
                    ++this.linesWritten;
                    this.descender = pdfLine.getDescender();
                } while (true);
            }
            this.currentLeading = this.fixedLeading;
            do {
                PdfLine pdfLine;
                f3 = this.lastWasNewline ? this.indent : this.followingIndent;
                float f6 = this.yLine;
                float[] arrf = this.findLimitsTwoLines();
                if (arrf == null) {
                    n = 2;
                    if (this.bidiLine.isEmpty()) {
                        n |= 1;
                    }
                    this.yLine = f6;
                    break;
                }
                if (this.bidiLine.isEmpty()) {
                    n = 1;
                    this.yLine = f6;
                    break;
                }
                float f7 = Math.max(arrf[0], arrf[2]);
                float f8 = Math.min(arrf[1], arrf[3]);
                if (f8 - f7 <= f3 + this.rightIndent) continue;
                if (!bl && !bl2) {
                    pdfContentByte.beginText();
                    bl2 = true;
                }
                if ((pdfLine = this.bidiLine.processLine(f7, f8 - f7 - f3 - this.rightIndent, this.alignment, n2, this.arabicOptions)) == null) {
                    n = 1;
                    this.yLine = f6;
                    break;
                }
                if (!bl) {
                    arrobject[0] = pdfFont;
                    pdfContentByte.setTextMatrix(f7 + (pdfLine.isRTL() ? this.rightIndent : f3) + pdfLine.indentLeft(), this.yLine);
                    pdfDocument.writeLineToContent(pdfLine, pdfContentByte, pdfContentByte2, arrobject, f2);
                    pdfFont = (PdfFont)arrobject[0];
                }
                this.lastWasNewline = pdfLine.isNewlineSplit();
                this.yLine -= pdfLine.isNewlineSplit() ? this.extraParagraphSpace : 0.0f;
                ++this.linesWritten;
                this.descender = pdfLine.getDescender();
            } while (true);
        }
        if (bl2) {
            pdfContentByte.endText();
            this.canvas.add(pdfContentByte);
        }
        return n;
    }

    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }

    public void setExtraParagraphSpace(float f) {
        this.extraParagraphSpace = f;
    }

    public void clearChunks() {
        if (this.bidiLine != null) {
            this.bidiLine.clearChunks();
        }
    }

    public float getSpaceCharRatio() {
        return this.spaceCharRatio;
    }

    public void setSpaceCharRatio(float f) {
        this.spaceCharRatio = f;
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

    public int getLinesWritten() {
        return this.linesWritten;
    }

    public int getArabicOptions() {
        return this.arabicOptions;
    }

    public void setArabicOptions(int n) {
        this.arabicOptions = n;
    }

    public float getDescender() {
        return this.descender;
    }

    public static float getWidth(Phrase phrase, int n, int n2) {
        ColumnText columnText = new ColumnText(null);
        columnText.addText(phrase);
        columnText.addWaitingPhrase();
        PdfLine pdfLine = columnText.bidiLine.processLine(0.0f, 20000.0f, 0, n, n2);
        if (pdfLine == null) {
            return 0.0f;
        }
        return 20000.0f - pdfLine.widthLeft();
    }

    public static float getWidth(Phrase phrase) {
        return ColumnText.getWidth(phrase, 1, 0);
    }

    public static void showTextAligned(PdfContentByte pdfContentByte, int n, Phrase phrase, float f, float f2, float f3, int n2, int n3) {
        if (n != 0 && n != 1 && n != 2) {
            n = 0;
        }
        pdfContentByte.saveState();
        ColumnText columnText = new ColumnText(pdfContentByte);
        if (f3 == 0.0f) {
            if (n == 0) {
                columnText.setSimpleColumn(phrase, f, f2 - 1.0f, 20000.0f + f, f2 + 2.0f, 2.0f, n);
            } else if (n == 2) {
                columnText.setSimpleColumn(phrase, f - 20000.0f, f2 - 1.0f, f, f2 + 2.0f, 2.0f, n);
            } else {
                columnText.setSimpleColumn(phrase, f - 20000.0f, f2 - 1.0f, f + 20000.0f, f2 + 2.0f, 2.0f, n);
            }
        } else {
            double d = (double)f3 * 3.141592653589793 / 180.0;
            float f4 = (float)Math.cos(d);
            float f5 = (float)Math.sin(d);
            pdfContentByte.concatCTM(f4, f5, - f5, f4, f, f2);
            if (n == 0) {
                columnText.setSimpleColumn(phrase, 0.0f, -1.0f, 20000.0f, 2.0f, 2.0f, n);
            } else if (n == 2) {
                columnText.setSimpleColumn(phrase, -20000.0f, -1.0f, 0.0f, 2.0f, 2.0f, n);
            } else {
                columnText.setSimpleColumn(phrase, -20000.0f, -1.0f, 20000.0f, 2.0f, 2.0f, n);
            }
        }
        if (n2 == 3) {
            if (n == 0) {
                n = 2;
            } else if (n == 2) {
                n = 0;
            }
        }
        columnText.setAlignment(n);
        columnText.setArabicOptions(n3);
        columnText.setRunDirection(n2);
        try {
            columnText.go();
        }
        catch (DocumentException var9_10) {
            throw new ExceptionConverter(var9_10);
        }
        pdfContentByte.restoreState();
    }

    public static void showTextAligned(PdfContentByte pdfContentByte, int n, Phrase phrase, float f, float f2, float f3) {
        ColumnText.showTextAligned(pdfContentByte, n, phrase, f, f2, f3, 1, 0);
    }

    protected int goComposite(boolean bl) throws DocumentException {
        if (!this.rectangularMode) {
            throw new DocumentException("Irregular columns are not supported in composite mode.");
        }
        this.linesWritten = 0;
        this.descender = 0.0f;
        boolean bl2 = this.adjustFirstLine;
        block4 : while (!this.compositeElements.isEmpty()) {
            float f;
            int n;
            boolean bl3;
            int n2;
            int n3;
            Object object;
            int n4;
            Object object2;
            Element element = (Element)this.compositeElements.getFirst();
            if (element.type() == 12) {
                object2 = (Paragraph)element;
                int n5 = 0;
                for (int i = 0; i < 2; ++i) {
                    f = this.yLine;
                    n3 = 0;
                    if (this.compositeColumn == null) {
                        this.compositeColumn = new ColumnText(this.canvas);
                        this.compositeColumn.setUseAscender(bl2 ? this.useAscender : false);
                        this.compositeColumn.setAlignment(object2.getAlignment());
                        this.compositeColumn.setIndent(object2.getIndentationLeft() + object2.getFirstLineIndent());
                        this.compositeColumn.setExtraParagraphSpace(object2.getExtraParagraphSpace());
                        this.compositeColumn.setFollowingIndent(object2.getIndentationLeft());
                        this.compositeColumn.setRightIndent(object2.getIndentationRight());
                        this.compositeColumn.setLeading(object2.getLeading(), object2.getMultipliedLeading());
                        this.compositeColumn.setRunDirection(this.runDirection);
                        this.compositeColumn.setArabicOptions(this.arabicOptions);
                        this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                        this.compositeColumn.addText((Phrase)object2);
                        if (!bl2) {
                            this.yLine -= object2.spacingBefore();
                        }
                        n3 = 1;
                    }
                    this.compositeColumn.leftX = this.leftX;
                    this.compositeColumn.rightX = this.rightX;
                    this.compositeColumn.yLine = this.yLine;
                    this.compositeColumn.rectangularWidth = this.rectangularWidth;
                    this.compositeColumn.rectangularMode = this.rectangularMode;
                    this.compositeColumn.minY = this.minY;
                    this.compositeColumn.maxY = this.maxY;
                    n2 = object2.getKeepTogether() && n3 != 0 && !bl2 ? 1 : 0;
                    n5 = this.compositeColumn.go(bl || n2 != 0 && i == 0);
                    this.updateFilledWidth(this.compositeColumn.filledWidth);
                    if ((n5 & 1) == 0 && n2 != 0) {
                        this.compositeColumn = null;
                        this.yLine = f;
                        return 2;
                    }
                    if (bl || n2 == 0) break;
                    if (i != 0) continue;
                    this.compositeColumn = null;
                    this.yLine = f;
                }
                bl2 = false;
                this.yLine = this.compositeColumn.yLine;
                this.linesWritten += this.compositeColumn.linesWritten;
                this.descender = this.compositeColumn.descender;
                if (n5 & true) {
                    this.compositeColumn = null;
                    this.compositeElements.removeFirst();
                    this.yLine -= object2.spacingAfter();
                }
                if ((n5 & 2) == 0) continue;
                return 2;
            }
            if (element.type() == 14) {
                object2 = (List)element;
                ArrayList arrayList = object2.getItems();
                ListItem listItem = null;
                f = object2.getIndentationLeft();
                n3 = 0;
                Stack<Object[]> stack = new Stack<Object[]>();
                for (n = 0; n < arrayList.size(); ++n) {
                    Object e = arrayList.get(n);
                    if (e instanceof ListItem) {
                        if (n3 == this.listIdx) {
                            listItem = (ListItem)e;
                            break;
                        }
                        ++n3;
                    } else if (e instanceof List) {
                        stack.push(new Object[]{object2, new Integer(n), new Float(f)});
                        object2 = (List)e;
                        arrayList = object2.getItems();
                        f += object2.getIndentationLeft();
                        n = -1;
                        continue;
                    }
                    if (n != arrayList.size() - 1 || stack.isEmpty()) continue;
                    object = (Object[])stack.pop();
                    object2 = (List)object[0];
                    arrayList = object2.getItems();
                    n = (Integer)object[1];
                    f = ((Float)object[2]).floatValue();
                }
                n = 0;
                for (n4 = 0; n4 < 2; ++n4) {
                    object = this.yLine;
                    boolean bl4 = false;
                    if (this.compositeColumn == null) {
                        if (listItem == null) {
                            this.listIdx = 0;
                            this.compositeElements.removeFirst();
                            continue block4;
                        }
                        this.compositeColumn = new ColumnText(this.canvas);
                        this.compositeColumn.setUseAscender(bl2 ? this.useAscender : false);
                        this.compositeColumn.setAlignment(listItem.getAlignment());
                        this.compositeColumn.setIndent(listItem.getIndentationLeft() + f + listItem.getFirstLineIndent());
                        this.compositeColumn.setExtraParagraphSpace(listItem.getExtraParagraphSpace());
                        this.compositeColumn.setFollowingIndent(this.compositeColumn.getIndent());
                        this.compositeColumn.setRightIndent(listItem.getIndentationRight() + object2.getIndentationRight());
                        this.compositeColumn.setLeading(listItem.getLeading(), listItem.getMultipliedLeading());
                        this.compositeColumn.setRunDirection(this.runDirection);
                        this.compositeColumn.setArabicOptions(this.arabicOptions);
                        this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                        this.compositeColumn.addText(listItem);
                        if (!bl2) {
                            this.yLine -= listItem.spacingBefore();
                        }
                        bl4 = true;
                    }
                    this.compositeColumn.leftX = this.leftX;
                    this.compositeColumn.rightX = this.rightX;
                    this.compositeColumn.yLine = this.yLine;
                    this.compositeColumn.rectangularWidth = this.rectangularWidth;
                    this.compositeColumn.rectangularMode = this.rectangularMode;
                    this.compositeColumn.minY = this.minY;
                    this.compositeColumn.maxY = this.maxY;
                    bl3 = listItem.getKeepTogether() && bl4 && !bl2;
                    n = this.compositeColumn.go(bl || bl3 && n4 == 0);
                    this.updateFilledWidth(this.compositeColumn.filledWidth);
                    if ((n & 1) == 0 && bl3) {
                        this.compositeColumn = null;
                        this.yLine = object;
                        return 2;
                    }
                    if (bl || !bl3) break;
                    if (n4 != 0) continue;
                    this.compositeColumn = null;
                    this.yLine = object;
                }
                bl2 = false;
                this.yLine = this.compositeColumn.yLine;
                this.linesWritten += this.compositeColumn.linesWritten;
                this.descender = this.compositeColumn.descender;
                if (!Float.isNaN(this.compositeColumn.firstLineY) && !this.compositeColumn.firstLineYDone) {
                    if (!bl) {
                        ColumnText.showTextAligned(this.canvas, 0, new Phrase(listItem.getListSymbol()), this.compositeColumn.leftX + f, this.compositeColumn.firstLineY, 0.0f);
                    }
                    this.compositeColumn.firstLineYDone = true;
                }
                if ((n & 1) != 0) {
                    this.compositeColumn = null;
                    ++this.listIdx;
                    this.yLine -= listItem.spacingAfter();
                }
                if ((n & 2) == 0) continue;
                return 2;
            }
            if (element.type() == 23) {
                int n6;
                Object object3;
                float f2;
                Object object4;
                if (this.yLine < this.minY || this.yLine > this.maxY) {
                    return 2;
                }
                object2 = (PdfPTable)element;
                if (object2.size() <= object2.getHeaderRows()) {
                    this.compositeElements.removeFirst();
                    continue;
                }
                float f3 = this.yLine;
                if (!bl2 && this.listIdx == 0) {
                    f3 -= object2.spacingBefore();
                }
                float f4 = f3;
                if (f3 < this.minY || f3 > this.maxY) {
                    return 2;
                }
                this.currentLeading = 0.0f;
                f = this.leftX;
                if (object2.isLockedWidth()) {
                    f2 = object2.getTotalWidth();
                    this.updateFilledWidth(f2);
                } else {
                    f2 = this.rectangularWidth * object2.getWidthPercentage() / 100.0f;
                    object2.setTotalWidth(f2);
                }
                n2 = object2.getHeaderRows();
                n = object2.getFooterRows();
                if (n > n2) {
                    n = n2;
                }
                n4 = n2 - n;
                object = object2.getHeaderHeight();
                float f5 = object2.getFooterHeight();
                boolean bl5 = bl3 = !bl2 && object2.isSkipFirstHeader() && this.listIdx <= n2;
                if (!(bl3 || (f3 -= object) >= this.minY && f3 <= this.maxY)) {
                    if (bl2) {
                        this.compositeElements.removeFirst();
                        continue;
                    }
                    return 2;
                }
                if (this.listIdx < n2) {
                    this.listIdx = n2;
                }
                if (!object2.isComplete()) {
                    f3 -= f5;
                }
                for (n6 = this.listIdx; n6 < object2.size() && f3 - (object4 = object2.getRowHeight(n6)) >= this.minY; ++n6) {
                    f3 -= object4;
                }
                if (!object2.isComplete()) {
                    f3 += f5;
                }
                if (n6 < object2.size()) {
                    if (object2.isSplitRows() && (!object2.isSplitLate() || n6 == this.listIdx && bl2)) {
                        if (!this.splittedRow) {
                            this.splittedRow = true;
                            object2 = new PdfPTable((PdfPTable)object2);
                            this.compositeElements.set(0, object2);
                            ArrayList arrayList = object2.getRows();
                            for (int i = n2; i < this.listIdx; ++i) {
                                arrayList.set(i, null);
                            }
                        }
                        object4 = f3 - this.minY;
                        object3 = object2.getRow(n6).splitRow((float)object4);
                        if (object3 == null) {
                            if (n6 == this.listIdx) {
                                return 2;
                            }
                        } else {
                            f3 = this.minY;
                            object2.getRows().add(++n6, object3);
                        }
                    } else {
                        if (!object2.isSplitRows() && n6 == this.listIdx && bl2) {
                            this.compositeElements.removeFirst();
                            this.splittedRow = false;
                            continue;
                        }
                        if (!(n6 != this.listIdx || bl2 || object2.isSplitRows() && !object2.isSplitLate() || object2.getFooterRows() != 0 && !object2.isComplete())) {
                            return 2;
                        }
                    }
                }
                bl2 = false;
                if (!bl) {
                    int n7;
                    PdfPRow pdfPRow;
                    switch (object2.getHorizontalAlignment()) {
                        case 0: {
                            break;
                        }
                        case 2: {
                            f += this.rectangularWidth - f2;
                            break;
                        }
                        default: {
                            f += (this.rectangularWidth - f2) / 2.0f;
                        }
                    }
                    PdfPTable pdfPTable = PdfPTable.shallowCopy((PdfPTable)object2);
                    object3 = object2.getRows();
                    ArrayList arrayList = pdfPTable.getRows();
                    if (!bl3) {
                        for (n7 = 0; n7 < n4; ++n7) {
                            pdfPRow = (PdfPRow)object3.get(n7);
                            arrayList.add(pdfPRow);
                        }
                    } else {
                        pdfPTable.setHeaderRows(n);
                    }
                    for (n7 = this.listIdx; n7 < n6; ++n7) {
                        arrayList.add(object3.get(n7));
                    }
                    if (n6 < object2.size()) {
                        pdfPTable.setComplete(true);
                    }
                    for (n7 = 0; n7 < n && pdfPTable.isComplete(); ++n7) {
                        arrayList.add(object3.get(n7 + n4));
                    }
                    float f6 = 0.0f;
                    if (object2.isExtendLastRow()) {
                        pdfPRow = (PdfPRow)arrayList.get(arrayList.size() - 1 - n);
                        f6 = pdfPRow.getMaxHeights();
                        pdfPRow.setMaxHeights(f3 - this.minY + f6);
                        f3 = this.minY;
                    }
                    if (this.canvases != null) {
                        pdfPTable.writeSelectedRows(0, -1, f, f4, this.canvases);
                    } else {
                        pdfPTable.writeSelectedRows(0, -1, f, f4, this.canvas);
                    }
                    if (object2.isExtendLastRow()) {
                        pdfPRow = (PdfPRow)arrayList.get(arrayList.size() - 1 - n);
                        pdfPRow.setMaxHeights(f6);
                    }
                } else if (object2.isExtendLastRow() && this.minY > -1.07374182E9f) {
                    f3 = this.minY;
                }
                this.yLine = f3;
                if (!bl3 && !object2.isComplete()) {
                    this.yLine += f5;
                }
                if (n6 >= object2.size()) {
                    this.yLine -= object2.spacingAfter();
                    this.compositeElements.removeFirst();
                    this.splittedRow = false;
                    this.listIdx = 0;
                    continue;
                }
                if (this.splittedRow) {
                    object4 = object2.getRows();
                    for (int i = this.listIdx; i < n6; ++i) {
                        object4.set(i, null);
                    }
                }
                this.listIdx = n6;
                return 2;
            }
            if (element.type() == 55) {
                if (!bl) {
                    object2 = (DrawInterface)((Object)element);
                    object2.draw(this.canvas, this.leftX, this.minY, this.rightX, this.maxY, this.yLine);
                }
                this.compositeElements.removeFirst();
                continue;
            }
            this.compositeElements.removeFirst();
        }
        return 1;
    }

    public PdfContentByte getCanvas() {
        return this.canvas;
    }

    public void setCanvas(PdfContentByte pdfContentByte) {
        this.canvas = pdfContentByte;
        this.canvases = null;
        if (this.compositeColumn != null) {
            this.compositeColumn.setCanvas(pdfContentByte);
        }
    }

    public void setCanvases(PdfContentByte[] arrpdfContentByte) {
        this.canvases = arrpdfContentByte;
        this.canvas = arrpdfContentByte[3];
        if (this.compositeColumn != null) {
            this.compositeColumn.setCanvases(arrpdfContentByte);
        }
    }

    public PdfContentByte[] getCanvases() {
        return this.canvases;
    }

    public boolean zeroHeightElement() {
        return this.composite && !this.compositeElements.isEmpty() && ((Element)this.compositeElements.getFirst()).type() == 55;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseAscender(boolean bl) {
        this.useAscender = bl;
    }

    public static boolean hasMoreText(int n) {
        return (n & 1) == 0;
    }

    public float getFilledWidth() {
        return this.filledWidth;
    }

    public void setFilledWidth(float f) {
        this.filledWidth = f;
    }

    public void updateFilledWidth(float f) {
        if (f > this.filledWidth) {
            this.filledWidth = f;
        }
    }

    public boolean isAdjustFirstLine() {
        return this.adjustFirstLine;
    }

    public void setAdjustFirstLine(boolean bl) {
        this.adjustFirstLine = bl;
    }
}

