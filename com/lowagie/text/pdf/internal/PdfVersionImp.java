/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.interfaces.PdfVersion;
import java.io.IOException;

public class PdfVersionImp
implements PdfVersion {
    public static final byte[][] HEADER = new byte[][]{DocWriter.getISOBytes("\n"), DocWriter.getISOBytes("%PDF-"), DocWriter.getISOBytes("\n%\u00e2\u00e3\u00cf\u00d3\n")};
    protected boolean headerWasWritten = false;
    protected boolean appendmode = false;
    protected char header_version = 52;
    protected PdfName catalog_version = null;

    public void setPdfVersion(char c) {
        if (this.headerWasWritten || this.appendmode) {
            this.setPdfVersion(this.getVersionAsName(c));
        } else {
            this.header_version = c;
        }
    }

    public void setAtLeastPdfVersion(char c) {
        if (c > this.header_version) {
            this.setPdfVersion(c);
        }
    }

    public void setPdfVersion(PdfName pdfName) {
        if (this.catalog_version == null || this.catalog_version.compareTo(pdfName) < 0) {
            this.catalog_version = pdfName;
        }
    }

    public void setAppendmode(boolean bl) {
        this.appendmode = bl;
    }

    public void writeHeader(OutputStreamCounter outputStreamCounter) throws IOException {
        if (this.appendmode) {
            outputStreamCounter.write(HEADER[0]);
        } else {
            outputStreamCounter.write(HEADER[1]);
            outputStreamCounter.write(this.getVersionAsByteArray(this.header_version));
            outputStreamCounter.write(HEADER[2]);
            this.headerWasWritten = true;
        }
    }

    public PdfName getVersionAsName(char c) {
        switch (c) {
            case '2': {
                return PdfWriter.PDF_VERSION_1_2;
            }
            case '3': {
                return PdfWriter.PDF_VERSION_1_3;
            }
            case '4': {
                return PdfWriter.PDF_VERSION_1_4;
            }
            case '5': {
                return PdfWriter.PDF_VERSION_1_5;
            }
            case '6': {
                return PdfWriter.PDF_VERSION_1_6;
            }
            case '7': {
                return PdfWriter.PDF_VERSION_1_7;
            }
        }
        return PdfWriter.PDF_VERSION_1_4;
    }

    public byte[] getVersionAsByteArray(char c) {
        return DocWriter.getISOBytes(this.getVersionAsName(c).toString().substring(1));
    }

    public void addToCatalog(PdfDictionary pdfDictionary) {
        if (this.catalog_version != null) {
            pdfDictionary.put(PdfName.VERSION, this.catalog_version);
        }
    }
}

