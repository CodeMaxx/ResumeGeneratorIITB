/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Color;
import java.awt.Image;

public class BarcodeEANSUPP
extends Barcode {
    protected Barcode ean;
    protected Barcode supp;

    public BarcodeEANSUPP(Barcode barcode, Barcode barcode2) {
        this.n = 8.0f;
        this.ean = barcode;
        this.supp = barcode2;
    }

    public Rectangle getBarcodeSize() {
        Rectangle rectangle = this.ean.getBarcodeSize();
        rectangle.setRight(rectangle.getWidth() + this.supp.getBarcodeSize().getWidth() + this.n);
        return rectangle;
    }

    public Rectangle placeBarcode(PdfContentByte pdfContentByte, Color color, Color color2) {
        if (this.supp.getFont() != null) {
            this.supp.setBarHeight(this.ean.getBarHeight() + this.supp.getBaseline() - this.supp.getFont().getFontDescriptor(2, this.supp.getSize()));
        } else {
            this.supp.setBarHeight(this.ean.getBarHeight());
        }
        Rectangle rectangle = this.ean.getBarcodeSize();
        pdfContentByte.saveState();
        this.ean.placeBarcode(pdfContentByte, color, color2);
        pdfContentByte.restoreState();
        pdfContentByte.saveState();
        pdfContentByte.concatCTM(1.0f, 0.0f, 0.0f, 1.0f, rectangle.getWidth() + this.n, rectangle.getHeight() - this.ean.getBarHeight());
        this.supp.placeBarcode(pdfContentByte, color, color2);
        pdfContentByte.restoreState();
        return this.getBarcodeSize();
    }

    public Image createAwtImage(Color color, Color color2) {
        throw new UnsupportedOperationException("The two barcodes must be composed externally.");
    }
}

