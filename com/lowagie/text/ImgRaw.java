/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import java.net.URL;

public class ImgRaw
extends Image {
    ImgRaw(Image image) {
        super(image);
    }

    public ImgRaw(int n, int n2, int n3, int n4, byte[] arrby) throws BadElementException {
        super((URL)null);
        this.type = 34;
        this.scaledHeight = n2;
        this.setTop(this.scaledHeight);
        this.scaledWidth = n;
        this.setRight(this.scaledWidth);
        if (n3 != 1 && n3 != 3 && n3 != 4) {
            throw new BadElementException("Components must be 1, 3, or 4.");
        }
        if (n4 != 1 && n4 != 2 && n4 != 4 && n4 != 8) {
            throw new BadElementException("Bits-per-component must be 1, 2, 4, or 8.");
        }
        this.colorspace = n3;
        this.bpc = n4;
        this.rawData = arrby;
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}

