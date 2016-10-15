/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

class PdfContents
extends PdfStream {
    static final byte[] SAVESTATE = DocWriter.getISOBytes("q\n");
    static final byte[] RESTORESTATE = DocWriter.getISOBytes("Q\n");
    static final byte[] ROTATE90 = DocWriter.getISOBytes("0 1 -1 0 ");
    static final byte[] ROTATE180 = DocWriter.getISOBytes("-1 0 0 -1 ");
    static final byte[] ROTATE270 = DocWriter.getISOBytes("0 -1 1 0 ");
    static final byte[] ROTATEFINAL = DocWriter.getISOBytes(" cm\n");

    PdfContents(PdfContentByte pdfContentByte, PdfContentByte pdfContentByte2, PdfContentByte pdfContentByte3, PdfContentByte pdfContentByte4, Rectangle rectangle) throws BadPdfFormatException {
        try {
            OutputStream outputStream = null;
            this.streamBytes = new ByteArrayOutputStream();
            if (Document.compress) {
                this.compressed = true;
                this.compressionLevel = pdfContentByte3.getPdfWriter().getCompressionLevel();
                outputStream = new DeflaterOutputStream((OutputStream)this.streamBytes, new Deflater(this.compressionLevel));
            } else {
                outputStream = this.streamBytes;
            }
            int n = rectangle.getRotation();
            switch (n) {
                case 90: {
                    outputStream.write(ROTATE90);
                    outputStream.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(rectangle.getTop())));
                    outputStream.write(32);
                    outputStream.write(48);
                    outputStream.write(ROTATEFINAL);
                    break;
                }
                case 180: {
                    outputStream.write(ROTATE180);
                    outputStream.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(rectangle.getRight())));
                    outputStream.write(32);
                    outputStream.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(rectangle.getTop())));
                    outputStream.write(ROTATEFINAL);
                    break;
                }
                case 270: {
                    outputStream.write(ROTATE270);
                    outputStream.write(48);
                    outputStream.write(32);
                    outputStream.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(rectangle.getRight())));
                    outputStream.write(ROTATEFINAL);
                }
            }
            if (pdfContentByte.size() > 0) {
                outputStream.write(SAVESTATE);
                pdfContentByte.getInternalBuffer().writeTo(outputStream);
                outputStream.write(RESTORESTATE);
            }
            if (pdfContentByte2.size() > 0) {
                outputStream.write(SAVESTATE);
                pdfContentByte2.getInternalBuffer().writeTo(outputStream);
                outputStream.write(RESTORESTATE);
            }
            if (pdfContentByte3 != null) {
                outputStream.write(SAVESTATE);
                pdfContentByte3.getInternalBuffer().writeTo(outputStream);
                outputStream.write(RESTORESTATE);
            }
            if (pdfContentByte4.size() > 0) {
                pdfContentByte4.getInternalBuffer().writeTo(outputStream);
            }
            outputStream.close();
        }
        catch (Exception var6_7) {
            throw new BadPdfFormatException(var6_7.getMessage());
        }
        this.put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
        if (this.compressed) {
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
    }
}

