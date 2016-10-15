/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfWriter;

public final class Type3Glyph
extends PdfContentByte {
    private PageResources pageResources;
    private boolean colorized;

    private Type3Glyph() {
        super(null);
    }

    Type3Glyph(PdfWriter pdfWriter, PageResources pageResources, float f, float f2, float f3, float f4, float f5, boolean bl) {
        super(pdfWriter);
        this.pageResources = pageResources;
        this.colorized = bl;
        if (bl) {
            this.content.append(f).append(" 0 d0\n");
        } else {
            this.content.append(f).append(" 0 ").append(f2).append(' ').append(f3).append(' ').append(f4).append(' ').append(f5).append(" d1\n");
        }
    }

    PageResources getPageResources() {
        return this.pageResources;
    }

    public void addImage(Image image, float f, float f2, float f3, float f4, float f5, float f6, boolean bl) throws DocumentException {
        if (!this.colorized && (!image.isMask() || image.getBpc() != 1 && image.getBpc() <= 255)) {
            throw new DocumentException("Not colorized Typed3 fonts only accept mask images.");
        }
        super.addImage(image, f, f2, f3, f4, f5, f6, bl);
    }

    public PdfContentByte getDuplicate() {
        Type3Glyph type3Glyph = new Type3Glyph();
        type3Glyph.writer = this.writer;
        type3Glyph.pdf = this.pdf;
        type3Glyph.pageResources = this.pageResources;
        type3Glyph.colorized = this.colorized;
        return type3Glyph;
    }
}

