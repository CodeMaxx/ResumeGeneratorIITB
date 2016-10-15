/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.codec.TIFFFaxDecoder;
import java.net.URL;

public class ImgCCITT
extends Image {
    ImgCCITT(Image image) {
        super(image);
    }

    public ImgCCITT(int n, int n2, boolean bl, int n3, int n4, byte[] arrby) throws BadElementException {
        super((URL)null);
        if (n3 != 256 && n3 != 257 && n3 != 258) {
            throw new BadElementException("The CCITT compression type must be CCITTG4, CCITTG3_1D or CCITTG3_2D");
        }
        if (bl) {
            TIFFFaxDecoder.reverseBits(arrby);
        }
        this.type = 34;
        this.scaledHeight = n2;
        this.setTop(this.scaledHeight);
        this.scaledWidth = n;
        this.setRight(this.scaledWidth);
        this.colorspace = n4;
        this.bpc = n3;
        this.rawData = arrby;
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}

