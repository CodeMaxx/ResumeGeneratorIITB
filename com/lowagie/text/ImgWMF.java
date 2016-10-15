/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.codec.wmf.InputMeta;
import com.lowagie.text.pdf.codec.wmf.MetaDo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImgWMF
extends Image {
    ImgWMF(Image image) {
        super(image);
    }

    public ImgWMF(URL uRL) throws BadElementException, IOException {
        super(uRL);
        this.processParameters();
    }

    public ImgWMF(String string) throws BadElementException, MalformedURLException, IOException {
        this(Utilities.toURL(string));
    }

    public ImgWMF(byte[] arrby) throws BadElementException, IOException {
        super((URL)null);
        this.rawData = arrby;
        this.originalData = arrby;
        this.processParameters();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processParameters() throws BadElementException, IOException {
        block6 : {
            this.type = 35;
            this.originalType = 6;
            InputStream inputStream = null;
            try {
                String string;
                if (this.rawData == null) {
                    inputStream = this.url.openStream();
                    string = this.url.toString();
                } else {
                    inputStream = new ByteArrayInputStream(this.rawData);
                    string = "Byte array";
                }
                InputMeta inputMeta = new InputMeta(inputStream);
                if (inputMeta.readInt() != -1698247209) {
                    throw new BadElementException(string + " is not a valid placeable windows metafile.");
                }
                inputMeta.readWord();
                int n = inputMeta.readShort();
                int n2 = inputMeta.readShort();
                int n3 = inputMeta.readShort();
                int n4 = inputMeta.readShort();
                int n5 = inputMeta.readWord();
                this.dpiX = 72;
                this.dpiY = 72;
                this.scaledHeight = (float)(n4 - n2) / (float)n5 * 72.0f;
                this.setTop(this.scaledHeight);
                this.scaledWidth = (float)(n3 - n) / (float)n5 * 72.0f;
                this.setRight(this.scaledWidth);
                java.lang.Object var10_9 = null;
                if (inputStream == null) break block6;
            }
            catch (Throwable var9_11) {
                java.lang.Object var10_10 = null;
                if (inputStream != null) {
                    inputStream.close();
                }
                this.plainWidth = this.getWidth();
                this.plainHeight = this.getHeight();
                throw var9_11;
            }
            inputStream.close();
        }
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
        {
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readWMF(PdfTemplate pdfTemplate) throws IOException, DocumentException {
        this.setTemplateData(pdfTemplate);
        pdfTemplate.setWidth(this.getWidth());
        pdfTemplate.setHeight(this.getHeight());
        InputStream inputStream = null;
        try {
            inputStream = this.rawData == null ? this.url.openStream() : new ByteArrayInputStream(this.rawData);
            MetaDo metaDo = new MetaDo(inputStream, pdfTemplate);
            metaDo.readAll();
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}

