/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGraphics2D;
import java.awt.print.PrinterGraphics;
import java.awt.print.PrinterJob;

public class PdfPrinterGraphics2D
extends PdfGraphics2D
implements PrinterGraphics {
    private PrinterJob printerJob;

    public PdfPrinterGraphics2D(PdfContentByte pdfContentByte, float f, float f2, FontMapper fontMapper, boolean bl, boolean bl2, float f3, PrinterJob printerJob) {
        super(pdfContentByte, f, f2, fontMapper, bl, bl2, f3);
        this.printerJob = printerJob;
    }

    public PrinterJob getPrinterJob() {
        return this.printerJob;
    }
}

