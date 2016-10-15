/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.FontDetails;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTransparencyGroup;
import com.lowagie.text.pdf.PdfWriter;
import java.util.HashMap;

public class PdfAppearance
extends PdfTemplate {
    public static final HashMap stdFieldFontNames = new HashMap();

    PdfAppearance() {
        this.separator = 32;
    }

    PdfAppearance(PdfIndirectReference pdfIndirectReference) {
        this.thisReference = pdfIndirectReference;
    }

    PdfAppearance(PdfWriter pdfWriter) {
        super(pdfWriter);
        this.separator = 32;
    }

    public static PdfAppearance createAppearance(PdfWriter pdfWriter, float f, float f2) {
        return PdfAppearance.createAppearance(pdfWriter, f, f2, null);
    }

    static PdfAppearance createAppearance(PdfWriter pdfWriter, float f, float f2, PdfName pdfName) {
        PdfAppearance pdfAppearance = new PdfAppearance(pdfWriter);
        pdfAppearance.setWidth(f);
        pdfAppearance.setHeight(f2);
        pdfWriter.addDirectTemplateSimple(pdfAppearance, pdfName);
        return pdfAppearance;
    }

    public void setFontAndSize(BaseFont baseFont, float f) {
        this.checkWriter();
        this.state.size = f;
        this.state.fontDetails = baseFont.getFontType() == 4 ? new FontDetails(null, ((DocumentFont)baseFont).getIndirectReference(), baseFont) : this.writer.addSimple(baseFont);
        PdfName pdfName = (PdfName)stdFieldFontNames.get(baseFont.getPostscriptFontName());
        if (pdfName == null) {
            if (baseFont.isSubset() && baseFont.getFontType() == 3) {
                pdfName = this.state.fontDetails.getFontName();
            } else {
                pdfName = new PdfName(baseFont.getPostscriptFontName());
                this.state.fontDetails.setSubset(false);
            }
        }
        PageResources pageResources = this.getPageResources();
        pageResources.addFont(pdfName, this.state.fontDetails.getIndirectReference());
        this.content.append(pdfName.getBytes()).append(' ').append(f).append(" Tf").append_i(this.separator);
    }

    public PdfContentByte getDuplicate() {
        PdfAppearance pdfAppearance = new PdfAppearance();
        pdfAppearance.writer = this.writer;
        pdfAppearance.pdf = this.pdf;
        pdfAppearance.thisReference = this.thisReference;
        pdfAppearance.pageResources = this.pageResources;
        pdfAppearance.bBox = new Rectangle(this.bBox);
        pdfAppearance.group = this.group;
        pdfAppearance.layer = this.layer;
        if (this.matrix != null) {
            pdfAppearance.matrix = new PdfArray(this.matrix);
        }
        pdfAppearance.separator = this.separator;
        return pdfAppearance;
    }

    static {
        stdFieldFontNames.put("Courier-BoldOblique", new PdfName("CoBO"));
        stdFieldFontNames.put("Courier-Bold", new PdfName("CoBo"));
        stdFieldFontNames.put("Courier-Oblique", new PdfName("CoOb"));
        stdFieldFontNames.put("Courier", new PdfName("Cour"));
        stdFieldFontNames.put("Helvetica-BoldOblique", new PdfName("HeBO"));
        stdFieldFontNames.put("Helvetica-Bold", new PdfName("HeBo"));
        stdFieldFontNames.put("Helvetica-Oblique", new PdfName("HeOb"));
        stdFieldFontNames.put("Helvetica", PdfName.HELV);
        stdFieldFontNames.put("Symbol", new PdfName("Symb"));
        stdFieldFontNames.put("Times-BoldItalic", new PdfName("TiBI"));
        stdFieldFontNames.put("Times-Bold", new PdfName("TiBo"));
        stdFieldFontNames.put("Times-Italic", new PdfName("TiIt"));
        stdFieldFontNames.put("Times-Roman", new PdfName("TiRo"));
        stdFieldFontNames.put("ZapfDingbats", PdfName.ZADB);
        stdFieldFontNames.put("HYSMyeongJo-Medium", new PdfName("HySm"));
        stdFieldFontNames.put("HYGoThic-Medium", new PdfName("HyGo"));
        stdFieldFontNames.put("HeiseiKakuGo-W5", new PdfName("KaGo"));
        stdFieldFontNames.put("HeiseiMin-W3", new PdfName("KaMi"));
        stdFieldFontNames.put("MHei-Medium", new PdfName("MHei"));
        stdFieldFontNames.put("MSung-Light", new PdfName("MSun"));
        stdFieldFontNames.put("STSong-Light", new PdfName("STSo"));
        stdFieldFontNames.put("MSungStd-Light", new PdfName("MSun"));
        stdFieldFontNames.put("STSongStd-Light", new PdfName("STSo"));
        stdFieldFontNames.put("HYSMyeongJoStd-Medium", new PdfName("HySm"));
        stdFieldFontNames.put("KozMinPro-Regular", new PdfName("KaMi"));
    }
}

