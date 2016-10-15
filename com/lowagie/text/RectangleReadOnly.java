/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Rectangle;
import java.awt.Color;

public class RectangleReadOnly
extends Rectangle {
    public RectangleReadOnly(float f, float f2, float f3, float f4) {
        super(f, f2, f3, f4);
    }

    public RectangleReadOnly(float f, float f2) {
        super(0.0f, 0.0f, f, f2);
    }

    public RectangleReadOnly(Rectangle rectangle) {
        super(rectangle.llx, rectangle.lly, rectangle.urx, rectangle.ury);
        super.cloneNonPositionParameters(rectangle);
    }

    private void throwReadOnlyError() {
        throw new UnsupportedOperationException("RectangleReadOnly: this Rectangle is read only.");
    }

    public void setLeft(float f) {
        this.throwReadOnlyError();
    }

    public void setRight(float f) {
        this.throwReadOnlyError();
    }

    public void setTop(float f) {
        this.throwReadOnlyError();
    }

    public void setBottom(float f) {
        this.throwReadOnlyError();
    }

    public void normalize() {
        this.throwReadOnlyError();
    }

    public void setBorder(int n) {
        this.throwReadOnlyError();
    }

    public void enableBorderSide(int n) {
        this.throwReadOnlyError();
    }

    public void disableBorderSide(int n) {
        this.throwReadOnlyError();
    }

    public void setBorderWidth(float f) {
        this.throwReadOnlyError();
    }

    public void setBorderColor(Color color) {
        this.throwReadOnlyError();
    }

    public void setBackgroundColor(Color color) {
        this.throwReadOnlyError();
    }

    public void setGrayFill(float f) {
        this.throwReadOnlyError();
    }

    public void setUseVariableBorders(boolean bl) {
        this.throwReadOnlyError();
    }

    public void setBorderWidthLeft(float f) {
        this.throwReadOnlyError();
    }

    public void setBorderWidthRight(float f) {
        this.throwReadOnlyError();
    }

    public void setBorderWidthTop(float f) {
        this.throwReadOnlyError();
    }

    public void setBorderWidthBottom(float f) {
        this.throwReadOnlyError();
    }

    public void setBorderColorLeft(Color color) {
        this.throwReadOnlyError();
    }

    public void setBorderColorRight(Color color) {
        this.throwReadOnlyError();
    }

    public void setBorderColorTop(Color color) {
        this.throwReadOnlyError();
    }

    public void setBorderColorBottom(Color color) {
        this.throwReadOnlyError();
    }

    public void cloneNonPositionParameters(Rectangle rectangle) {
        this.throwReadOnlyError();
    }

    public void softCloneNonPositionParameters(Rectangle rectangle) {
        this.throwReadOnlyError();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("RectangleReadOnly: ");
        stringBuffer.append(this.getWidth());
        stringBuffer.append('x');
        stringBuffer.append(this.getHeight());
        stringBuffer.append(" (rot: ");
        stringBuffer.append(this.rotation);
        stringBuffer.append(" degrees)");
        return stringBuffer.toString();
    }
}

