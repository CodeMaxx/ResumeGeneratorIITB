/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.codec.wmf.MetaBrush;
import com.lowagie.text.pdf.codec.wmf.MetaFont;
import com.lowagie.text.pdf.codec.wmf.MetaObject;
import com.lowagie.text.pdf.codec.wmf.MetaPen;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

public class MetaState {
    public static final int TA_NOUPDATECP = 0;
    public static final int TA_UPDATECP = 1;
    public static final int TA_LEFT = 0;
    public static final int TA_RIGHT = 2;
    public static final int TA_CENTER = 6;
    public static final int TA_TOP = 0;
    public static final int TA_BOTTOM = 8;
    public static final int TA_BASELINE = 24;
    public static final int TRANSPARENT = 1;
    public static final int OPAQUE = 2;
    public static final int ALTERNATE = 1;
    public static final int WINDING = 2;
    public Stack savedStates;
    public ArrayList MetaObjects;
    public Point currentPoint;
    public MetaPen currentPen;
    public MetaBrush currentBrush;
    public MetaFont currentFont;
    public Color currentBackgroundColor = Color.white;
    public Color currentTextColor = Color.black;
    public int backgroundMode = 2;
    public int polyFillMode = 1;
    public int lineJoin = 1;
    public int textAlign;
    public int offsetWx;
    public int offsetWy;
    public int extentWx;
    public int extentWy;
    public float scalingX;
    public float scalingY;

    public MetaState() {
        this.savedStates = new Stack();
        this.MetaObjects = new ArrayList();
        this.currentPoint = new Point(0, 0);
        this.currentPen = new MetaPen();
        this.currentBrush = new MetaBrush();
        this.currentFont = new MetaFont();
    }

    public MetaState(MetaState metaState) {
        this.setMetaState(metaState);
    }

    public void setMetaState(MetaState metaState) {
        this.savedStates = metaState.savedStates;
        this.MetaObjects = metaState.MetaObjects;
        this.currentPoint = metaState.currentPoint;
        this.currentPen = metaState.currentPen;
        this.currentBrush = metaState.currentBrush;
        this.currentFont = metaState.currentFont;
        this.currentBackgroundColor = metaState.currentBackgroundColor;
        this.currentTextColor = metaState.currentTextColor;
        this.backgroundMode = metaState.backgroundMode;
        this.polyFillMode = metaState.polyFillMode;
        this.textAlign = metaState.textAlign;
        this.lineJoin = metaState.lineJoin;
        this.offsetWx = metaState.offsetWx;
        this.offsetWy = metaState.offsetWy;
        this.extentWx = metaState.extentWx;
        this.extentWy = metaState.extentWy;
        this.scalingX = metaState.scalingX;
        this.scalingY = metaState.scalingY;
    }

    public void addMetaObject(MetaObject metaObject) {
        for (int i = 0; i < this.MetaObjects.size(); ++i) {
            if (this.MetaObjects.get(i) != null) continue;
            this.MetaObjects.set(i, metaObject);
            return;
        }
        this.MetaObjects.add(metaObject);
    }

    public void selectMetaObject(int n, PdfContentByte pdfContentByte) {
        MetaObject metaObject = (MetaObject)this.MetaObjects.get(n);
        if (metaObject == null) {
            return;
        }
        block0 : switch (metaObject.getType()) {
            case 2: {
                this.currentBrush = (MetaBrush)metaObject;
                int n2 = this.currentBrush.getStyle();
                if (n2 == 0) {
                    Color color = this.currentBrush.getColor();
                    pdfContentByte.setColorFill(color);
                    break;
                }
                if (n2 != 2) break;
                Color color = this.currentBackgroundColor;
                pdfContentByte.setColorFill(color);
                break;
            }
            case 1: {
                this.currentPen = (MetaPen)metaObject;
                int n3 = this.currentPen.getStyle();
                if (n3 == 5) break;
                Color color = this.currentPen.getColor();
                pdfContentByte.setColorStroke(color);
                pdfContentByte.setLineWidth(Math.abs((float)this.currentPen.getPenWidth() * this.scalingX / (float)this.extentWx));
                switch (n3) {
                    case 1: {
                        pdfContentByte.setLineDash(18.0f, 6.0f, 0.0f);
                        break block0;
                    }
                    case 3: {
                        pdfContentByte.setLiteral("[9 6 3 6]0 d\n");
                        break block0;
                    }
                    case 4: {
                        pdfContentByte.setLiteral("[9 3 3 3 3 3]0 d\n");
                        break block0;
                    }
                    case 2: {
                        pdfContentByte.setLineDash(3.0f, 0.0f);
                        break block0;
                    }
                }
                pdfContentByte.setLineDash(0.0f);
                break;
            }
            case 3: {
                this.currentFont = (MetaFont)metaObject;
            }
        }
    }

    public void deleteMetaObject(int n) {
        this.MetaObjects.set(n, null);
    }

    public void saveState(PdfContentByte pdfContentByte) {
        pdfContentByte.saveState();
        MetaState metaState = new MetaState(this);
        this.savedStates.push(metaState);
    }

    public void restoreState(int n, PdfContentByte pdfContentByte) {
        int n2 = n < 0 ? Math.min(- n, this.savedStates.size()) : Math.max(this.savedStates.size() - n, 0);
        if (n2 == 0) {
            return;
        }
        MetaState metaState = null;
        while (n2-- != 0) {
            pdfContentByte.restoreState();
            metaState = (MetaState)this.savedStates.pop();
        }
        this.setMetaState(metaState);
    }

    public void cleanup(PdfContentByte pdfContentByte) {
        int n = this.savedStates.size();
        while (n-- > 0) {
            pdfContentByte.restoreState();
        }
    }

    public float transformX(int n) {
        return ((float)n - (float)this.offsetWx) * this.scalingX / (float)this.extentWx;
    }

    public float transformY(int n) {
        return (1.0f - ((float)n - (float)this.offsetWy) / (float)this.extentWy) * this.scalingY;
    }

    public void setScalingX(float f) {
        this.scalingX = f;
    }

    public void setScalingY(float f) {
        this.scalingY = f;
    }

    public void setOffsetWx(int n) {
        this.offsetWx = n;
    }

    public void setOffsetWy(int n) {
        this.offsetWy = n;
    }

    public void setExtentWx(int n) {
        this.extentWx = n;
    }

    public void setExtentWy(int n) {
        this.extentWy = n;
    }

    public float transformAngle(float f) {
        float f2 = this.scalingY < 0.0f ? - f : f;
        return (float)(this.scalingX < 0.0f ? 3.141592653589793 - (double)f2 : (double)f2);
    }

    public void setCurrentPoint(Point point) {
        this.currentPoint = point;
    }

    public Point getCurrentPoint() {
        return this.currentPoint;
    }

    public MetaBrush getCurrentBrush() {
        return this.currentBrush;
    }

    public MetaPen getCurrentPen() {
        return this.currentPen;
    }

    public MetaFont getCurrentFont() {
        return this.currentFont;
    }

    public Color getCurrentBackgroundColor() {
        return this.currentBackgroundColor;
    }

    public void setCurrentBackgroundColor(Color color) {
        this.currentBackgroundColor = color;
    }

    public Color getCurrentTextColor() {
        return this.currentTextColor;
    }

    public void setCurrentTextColor(Color color) {
        this.currentTextColor = color;
    }

    public int getBackgroundMode() {
        return this.backgroundMode;
    }

    public void setBackgroundMode(int n) {
        this.backgroundMode = n;
    }

    public int getTextAlign() {
        return this.textAlign;
    }

    public void setTextAlign(int n) {
        this.textAlign = n;
    }

    public int getPolyFillMode() {
        return this.polyFillMode;
    }

    public void setPolyFillMode(int n) {
        this.polyFillMode = n;
    }

    public void setLineJoinRectangle(PdfContentByte pdfContentByte) {
        if (this.lineJoin != 0) {
            this.lineJoin = 0;
            pdfContentByte.setLineJoin(0);
        }
    }

    public void setLineJoinPolygon(PdfContentByte pdfContentByte) {
        if (this.lineJoin == 0) {
            this.lineJoin = 1;
            pdfContentByte.setLineJoin(1);
        }
    }

    public boolean getLineNeutral() {
        return this.lineJoin == 0;
    }
}

