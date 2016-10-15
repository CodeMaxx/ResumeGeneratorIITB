/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TextField;
import java.io.IOException;
import java.util.HashMap;

public class FieldPositioningEvents
extends PdfPageEventHelper
implements PdfPCellEvent {
    protected HashMap genericChunkFields = new HashMap();
    protected PdfFormField cellField = null;
    protected PdfWriter fieldWriter = null;
    protected PdfFormField parent = null;
    public float padding;

    public FieldPositioningEvents() {
    }

    public void addField(String string, PdfFormField pdfFormField) {
        this.genericChunkFields.put(string, pdfFormField);
    }

    public FieldPositioningEvents(PdfWriter pdfWriter, PdfFormField pdfFormField) {
        this.cellField = pdfFormField;
        this.fieldWriter = pdfWriter;
    }

    public FieldPositioningEvents(PdfFormField pdfFormField, PdfFormField pdfFormField2) {
        this.cellField = pdfFormField2;
        this.parent = pdfFormField;
    }

    public FieldPositioningEvents(PdfWriter pdfWriter, String string) throws IOException, DocumentException {
        this.fieldWriter = pdfWriter;
        TextField textField = new TextField(pdfWriter, new Rectangle(0.0f, 0.0f), string);
        textField.setFontSize(14.0f);
        this.cellField = textField.getTextField();
    }

    public FieldPositioningEvents(PdfWriter pdfWriter, PdfFormField pdfFormField, String string) throws IOException, DocumentException {
        this.parent = pdfFormField;
        TextField textField = new TextField(pdfWriter, new Rectangle(0.0f, 0.0f), string);
        textField.setFontSize(14.0f);
        this.cellField = textField.getTextField();
    }

    public void setPadding(float f) {
        this.padding = f;
    }

    public void setParent(PdfFormField pdfFormField) {
        this.parent = pdfFormField;
    }

    public void onGenericTag(PdfWriter pdfWriter, Document document, Rectangle rectangle, String string) {
        rectangle.setBottom(rectangle.getBottom() - 3.0f);
        PdfFormField pdfFormField = (PdfFormField)this.genericChunkFields.get(string);
        if (pdfFormField == null) {
            TextField textField = new TextField(pdfWriter, new Rectangle(rectangle.getLeft(this.padding), rectangle.getBottom(this.padding), rectangle.getRight(this.padding), rectangle.getTop(this.padding)), string);
            textField.setFontSize(14.0f);
            try {
                pdfFormField = textField.getTextField();
            }
            catch (Exception var7_7) {
                throw new ExceptionConverter(var7_7);
            }
        } else {
            pdfFormField.put(PdfName.RECT, new PdfRectangle(rectangle.getLeft(this.padding), rectangle.getBottom(this.padding), rectangle.getRight(this.padding), rectangle.getTop(this.padding)));
        }
        if (this.parent == null) {
            pdfWriter.addAnnotation(pdfFormField);
        } else {
            this.parent.addKid(pdfFormField);
        }
    }

    public void cellLayout(PdfPCell pdfPCell, Rectangle rectangle, PdfContentByte[] arrpdfContentByte) {
        if (this.cellField == null || this.fieldWriter == null && this.parent == null) {
            throw new ExceptionConverter(new IllegalArgumentException("You have used the wrong constructor for this FieldPositioningEvents class."));
        }
        this.cellField.put(PdfName.RECT, new PdfRectangle(rectangle.getLeft(this.padding), rectangle.getBottom(this.padding), rectangle.getRight(this.padding), rectangle.getTop(this.padding)));
        if (this.parent == null) {
            this.fieldWriter.addAnnotation(this.cellField);
        } else {
            this.parent.addKid(this.cellField);
        }
    }
}

