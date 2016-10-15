/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.pdf.GrayColor;
import java.awt.Color;
import java.util.ArrayList;

public class Rectangle
implements Element {
    public static final int UNDEFINED = -1;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int NO_BORDER = 0;
    public static final int BOX = 15;
    protected float llx;
    protected float lly;
    protected float urx;
    protected float ury;
    protected int rotation = 0;
    protected int border = -1;
    protected float borderWidth = -1.0f;
    protected Color borderColor = null;
    protected Color backgroundColor = null;
    protected boolean useVariableBorders = false;
    protected float borderWidthLeft = -1.0f;
    protected float borderWidthRight = -1.0f;
    protected float borderWidthTop = -1.0f;
    protected float borderWidthBottom = -1.0f;
    protected Color borderColorLeft = null;
    protected Color borderColorRight = null;
    protected Color borderColorTop = null;
    protected Color borderColorBottom = null;

    public Rectangle(float f, float f2, float f3, float f4) {
        this.llx = f;
        this.lly = f2;
        this.urx = f3;
        this.ury = f4;
    }

    public Rectangle(float f, float f2) {
        this(0.0f, 0.0f, f, f2);
    }

    public Rectangle(Rectangle rectangle) {
        this(rectangle.llx, rectangle.lly, rectangle.urx, rectangle.ury);
        this.cloneNonPositionParameters(rectangle);
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
        return 30;
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

    public void setLeft(float f) {
        this.llx = f;
    }

    public float getLeft() {
        return this.llx;
    }

    public float getLeft(float f) {
        return this.llx + f;
    }

    public void setRight(float f) {
        this.urx = f;
    }

    public float getRight() {
        return this.urx;
    }

    public float getRight(float f) {
        return this.urx - f;
    }

    public float getWidth() {
        return this.urx - this.llx;
    }

    public void setTop(float f) {
        this.ury = f;
    }

    public float getTop() {
        return this.ury;
    }

    public float getTop(float f) {
        return this.ury - f;
    }

    public void setBottom(float f) {
        this.lly = f;
    }

    public float getBottom() {
        return this.lly;
    }

    public float getBottom(float f) {
        return this.lly + f;
    }

    public float getHeight() {
        return this.ury - this.lly;
    }

    public void normalize() {
        float f;
        if (this.llx > this.urx) {
            f = this.llx;
            this.llx = this.urx;
            this.urx = f;
        }
        if (this.lly > this.ury) {
            f = this.lly;
            this.lly = this.ury;
            this.ury = f;
        }
    }

    public int getRotation() {
        return this.rotation;
    }

    public Rectangle rotate() {
        Rectangle rectangle = new Rectangle(this.lly, this.llx, this.ury, this.urx);
        rectangle.rotation = this.rotation + 90;
        rectangle.rotation %= 360;
        return rectangle;
    }

    public int getBorder() {
        return this.border;
    }

    public boolean hasBorders() {
        return this.border > 0 && (this.borderWidth > 0.0f || this.borderWidthLeft > 0.0f || this.borderWidthRight > 0.0f || this.borderWidthTop > 0.0f || this.borderWidthBottom > 0.0f);
    }

    public boolean hasBorder(int n) {
        return this.border != -1 && (this.border & n) == n;
    }

    public void setBorder(int n) {
        this.border = n;
    }

    public void enableBorderSide(int n) {
        if (this.border == -1) {
            this.border = 0;
        }
        this.border |= n;
    }

    public void disableBorderSide(int n) {
        if (this.border == -1) {
            this.border = 0;
        }
        this.border &= ~ n;
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(float f) {
        this.borderWidth = f;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public float getGrayFill() {
        if (this.backgroundColor instanceof GrayColor) {
            return ((GrayColor)this.backgroundColor).getGray();
        }
        return 0.0f;
    }

    public void setGrayFill(float f) {
        this.backgroundColor = new GrayColor(f);
    }

    public boolean isUseVariableBorders() {
        return this.useVariableBorders;
    }

    public void setUseVariableBorders(boolean bl) {
        this.useVariableBorders = bl;
    }

    private float getVariableBorderWidth(float f, int n) {
        if ((this.border & n) != 0) {
            return f != -1.0f ? f : this.borderWidth;
        }
        return 0.0f;
    }

    private void updateBorderBasedOnWidth(float f, int n) {
        this.useVariableBorders = true;
        if (f > 0.0f) {
            this.enableBorderSide(n);
        } else {
            this.disableBorderSide(n);
        }
    }

    public float getBorderWidthLeft() {
        return this.getVariableBorderWidth(this.borderWidthLeft, 4);
    }

    public void setBorderWidthLeft(float f) {
        this.borderWidthLeft = f;
        this.updateBorderBasedOnWidth(f, 4);
    }

    public float getBorderWidthRight() {
        return this.getVariableBorderWidth(this.borderWidthRight, 8);
    }

    public void setBorderWidthRight(float f) {
        this.borderWidthRight = f;
        this.updateBorderBasedOnWidth(f, 8);
    }

    public float getBorderWidthTop() {
        return this.getVariableBorderWidth(this.borderWidthTop, 1);
    }

    public void setBorderWidthTop(float f) {
        this.borderWidthTop = f;
        this.updateBorderBasedOnWidth(f, 1);
    }

    public float getBorderWidthBottom() {
        return this.getVariableBorderWidth(this.borderWidthBottom, 2);
    }

    public void setBorderWidthBottom(float f) {
        this.borderWidthBottom = f;
        this.updateBorderBasedOnWidth(f, 2);
    }

    public Color getBorderColorLeft() {
        if (this.borderColorLeft == null) {
            return this.borderColor;
        }
        return this.borderColorLeft;
    }

    public void setBorderColorLeft(Color color) {
        this.borderColorLeft = color;
    }

    public Color getBorderColorRight() {
        if (this.borderColorRight == null) {
            return this.borderColor;
        }
        return this.borderColorRight;
    }

    public void setBorderColorRight(Color color) {
        this.borderColorRight = color;
    }

    public Color getBorderColorTop() {
        if (this.borderColorTop == null) {
            return this.borderColor;
        }
        return this.borderColorTop;
    }

    public void setBorderColorTop(Color color) {
        this.borderColorTop = color;
    }

    public Color getBorderColorBottom() {
        if (this.borderColorBottom == null) {
            return this.borderColor;
        }
        return this.borderColorBottom;
    }

    public void setBorderColorBottom(Color color) {
        this.borderColorBottom = color;
    }

    public Rectangle rectangle(float f, float f2) {
        Rectangle rectangle = new Rectangle(this);
        if (this.getTop() > f) {
            rectangle.setTop(f);
            rectangle.disableBorderSide(1);
        }
        if (this.getBottom() < f2) {
            rectangle.setBottom(f2);
            rectangle.disableBorderSide(2);
        }
        return rectangle;
    }

    public void cloneNonPositionParameters(Rectangle rectangle) {
        this.rotation = rectangle.rotation;
        this.border = rectangle.border;
        this.borderWidth = rectangle.borderWidth;
        this.borderColor = rectangle.borderColor;
        this.backgroundColor = rectangle.backgroundColor;
        this.useVariableBorders = rectangle.useVariableBorders;
        this.borderWidthLeft = rectangle.borderWidthLeft;
        this.borderWidthRight = rectangle.borderWidthRight;
        this.borderWidthTop = rectangle.borderWidthTop;
        this.borderWidthBottom = rectangle.borderWidthBottom;
        this.borderColorLeft = rectangle.borderColorLeft;
        this.borderColorRight = rectangle.borderColorRight;
        this.borderColorTop = rectangle.borderColorTop;
        this.borderColorBottom = rectangle.borderColorBottom;
    }

    public void softCloneNonPositionParameters(Rectangle rectangle) {
        if (rectangle.rotation != 0) {
            this.rotation = rectangle.rotation;
        }
        if (rectangle.border != -1) {
            this.border = rectangle.border;
        }
        if (rectangle.borderWidth != -1.0f) {
            this.borderWidth = rectangle.borderWidth;
        }
        if (rectangle.borderColor != null) {
            this.borderColor = rectangle.borderColor;
        }
        if (rectangle.backgroundColor != null) {
            this.backgroundColor = rectangle.backgroundColor;
        }
        if (this.useVariableBorders) {
            this.useVariableBorders = rectangle.useVariableBorders;
        }
        if (rectangle.borderWidthLeft != -1.0f) {
            this.borderWidthLeft = rectangle.borderWidthLeft;
        }
        if (rectangle.borderWidthRight != -1.0f) {
            this.borderWidthRight = rectangle.borderWidthRight;
        }
        if (rectangle.borderWidthTop != -1.0f) {
            this.borderWidthTop = rectangle.borderWidthTop;
        }
        if (rectangle.borderWidthBottom != -1.0f) {
            this.borderWidthBottom = rectangle.borderWidthBottom;
        }
        if (rectangle.borderColorLeft != null) {
            this.borderColorLeft = rectangle.borderColorLeft;
        }
        if (rectangle.borderColorRight != null) {
            this.borderColorRight = rectangle.borderColorRight;
        }
        if (rectangle.borderColorTop != null) {
            this.borderColorTop = rectangle.borderColorTop;
        }
        if (rectangle.borderColorBottom != null) {
            this.borderColorBottom = rectangle.borderColorBottom;
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("Rectangle: ");
        stringBuffer.append(this.getWidth());
        stringBuffer.append('x');
        stringBuffer.append(this.getHeight());
        stringBuffer.append(" (rot: ");
        stringBuffer.append(this.rotation);
        stringBuffer.append(" degrees)");
        return stringBuffer.toString();
    }
}

