/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfTemplate;
import java.net.URL;

public class ImgTemplate
extends Image {
    ImgTemplate(Image image) {
        super(image);
    }

    public ImgTemplate(PdfTemplate pdfTemplate) throws BadElementException {
        super((URL)null);
        if (pdfTemplate == null) {
            throw new BadElementException("The template can not be null.");
        }
        if (pdfTemplate.getType() == 3) {
            throw new BadElementException("A pattern can not be used as a template to create an image.");
        }
        this.type = 35;
        this.scaledHeight = pdfTemplate.getHeight();
        this.setTop(this.scaledHeight);
        this.scaledWidth = pdfTemplate.getWidth();
        this.setRight(this.scaledWidth);
        this.setTemplateData(pdfTemplate);
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}

