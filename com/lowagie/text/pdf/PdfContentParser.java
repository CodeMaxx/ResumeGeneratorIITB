/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;
import java.util.ArrayList;

public class PdfContentParser {
    public static final int COMMAND_TYPE = 200;
    private PRTokeniser tokeniser;

    public PdfContentParser(PRTokeniser pRTokeniser) {
        this.tokeniser = pRTokeniser;
    }

    public ArrayList parse(ArrayList arrayList) throws IOException {
        if (arrayList == null) {
            arrayList = new ArrayList<PdfObject>();
        } else {
            arrayList.clear();
        }
        PdfObject pdfObject = null;
        while ((pdfObject = this.readPRObject()) != null) {
            arrayList.add(pdfObject);
            if (pdfObject.type() != 200) continue;
        }
        return arrayList;
    }

    public PRTokeniser getTokeniser() {
        return this.tokeniser;
    }

    public void setTokeniser(PRTokeniser pRTokeniser) {
        this.tokeniser = pRTokeniser;
    }

    public PdfDictionary readDictionary() throws IOException {
        PdfDictionary pdfDictionary = new PdfDictionary();
        do {
            if (!this.nextValidToken()) {
                throw new IOException("Unexpected end of file.");
            }
            if (this.tokeniser.getTokenType() == 8) break;
            if (this.tokeniser.getTokenType() != 3) {
                throw new IOException("Dictionary key is not a name.");
            }
            PdfName pdfName = new PdfName(this.tokeniser.getStringValue(), false);
            PdfObject pdfObject = this.readPRObject();
            int n = pdfObject.type();
            if (- n == 8) {
                throw new IOException("Unexpected '>>'");
            }
            if (- n == 6) {
                throw new IOException("Unexpected ']'");
            }
            pdfDictionary.put(pdfName, pdfObject);
        } while (true);
        return pdfDictionary;
    }

    public PdfArray readArray() throws IOException {
        PdfObject pdfObject;
        int n;
        PdfArray pdfArray = new PdfArray();
        while (- (n = (pdfObject = this.readPRObject()).type()) != 6) {
            if (- n == 8) {
                throw new IOException("Unexpected '>>'");
            }
            pdfArray.add(pdfObject);
        }
        return pdfArray;
    }

    public PdfObject readPRObject() throws IOException {
        if (!this.nextValidToken()) {
            return null;
        }
        int n = this.tokeniser.getTokenType();
        switch (n) {
            case 7: {
                PdfDictionary pdfDictionary = this.readDictionary();
                return pdfDictionary;
            }
            case 5: {
                return this.readArray();
            }
            case 2: {
                PdfString pdfString = new PdfString(this.tokeniser.getStringValue(), null).setHexWriting(this.tokeniser.isHexString());
                return pdfString;
            }
            case 3: {
                return new PdfName(this.tokeniser.getStringValue(), false);
            }
            case 1: {
                return new PdfNumber(this.tokeniser.getStringValue());
            }
            case 10: {
                return new PdfLiteral(200, this.tokeniser.getStringValue());
            }
        }
        return new PdfLiteral(- n, this.tokeniser.getStringValue());
    }

    public boolean nextValidToken() throws IOException {
        while (this.tokeniser.nextToken()) {
            if (this.tokeniser.getTokenType() == 4) continue;
            return true;
        }
        return false;
    }
}

