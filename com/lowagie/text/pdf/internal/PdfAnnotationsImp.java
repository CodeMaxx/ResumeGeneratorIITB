/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.Annotation;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PdfAnnotationsImp {
    protected PdfAcroForm acroForm;
    protected ArrayList annotations;
    protected ArrayList delayedAnnotations = new ArrayList();

    public PdfAnnotationsImp(PdfWriter pdfWriter) {
        this.acroForm = new PdfAcroForm(pdfWriter);
    }

    public boolean hasValidAcroForm() {
        return this.acroForm.isValid();
    }

    public PdfAcroForm getAcroForm() {
        return this.acroForm;
    }

    public void setSigFlags(int n) {
        this.acroForm.setSigFlags(n);
    }

    public void addCalculationOrder(PdfFormField pdfFormField) {
        this.acroForm.addCalculationOrder(pdfFormField);
    }

    public void addAnnotation(PdfAnnotation pdfAnnotation) {
        if (pdfAnnotation.isForm()) {
            PdfFormField pdfFormField = (PdfFormField)pdfAnnotation;
            if (pdfFormField.getParent() == null) {
                this.addFormFieldRaw(pdfFormField);
            }
        } else {
            this.annotations.add(pdfAnnotation);
        }
    }

    public void addPlainAnnotation(PdfAnnotation pdfAnnotation) {
        this.annotations.add(pdfAnnotation);
    }

    void addFormFieldRaw(PdfFormField pdfFormField) {
        this.annotations.add(pdfFormField);
        ArrayList arrayList = pdfFormField.getKids();
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); ++i) {
                this.addFormFieldRaw((PdfFormField)arrayList.get(i));
            }
        }
    }

    public boolean hasUnusedAnnotations() {
        return !this.annotations.isEmpty();
    }

    public void resetAnnotations() {
        this.annotations = this.delayedAnnotations;
        this.delayedAnnotations = new ArrayList();
    }

    public PdfArray rotateAnnotations(PdfWriter pdfWriter, Rectangle rectangle) {
        PdfArray pdfArray = new PdfArray();
        int n = rectangle.getRotation() % 360;
        int n2 = pdfWriter.getCurrentPageNumber();
        for (int i = 0; i < this.annotations.size(); ++i) {
            Object object;
            PdfAnnotation pdfAnnotation = (PdfAnnotation)this.annotations.get(i);
            int n3 = pdfAnnotation.getPlaceInPage();
            if (n3 > n2) {
                this.delayedAnnotations.add(pdfAnnotation);
                continue;
            }
            if (pdfAnnotation.isForm()) {
                if (!pdfAnnotation.isUsed() && (object = pdfAnnotation.getTemplates()) != null) {
                    this.acroForm.addFieldTemplates((HashMap)object);
                }
                if ((object = (PdfFormField)pdfAnnotation).getParent() == null) {
                    this.acroForm.addDocumentField(object.getIndirectReference());
                }
            }
            if (pdfAnnotation.isAnnotation()) {
                pdfArray.add(pdfAnnotation.getIndirectReference());
                if (!pdfAnnotation.isUsed() && (object = (PdfRectangle)pdfAnnotation.get(PdfName.RECT)) != null) {
                    switch (n) {
                        case 90: {
                            pdfAnnotation.put(PdfName.RECT, new PdfRectangle(rectangle.getTop() - object.bottom(), object.left(), rectangle.getTop() - object.top(), object.right()));
                            break;
                        }
                        case 180: {
                            pdfAnnotation.put(PdfName.RECT, new PdfRectangle(rectangle.getRight() - object.left(), rectangle.getTop() - object.bottom(), rectangle.getRight() - object.right(), rectangle.getTop() - object.top()));
                            break;
                        }
                        case 270: {
                            pdfAnnotation.put(PdfName.RECT, new PdfRectangle(object.bottom(), rectangle.getRight() - object.left(), object.top(), rectangle.getRight() - object.right()));
                        }
                    }
                }
            }
            if (pdfAnnotation.isUsed()) continue;
            pdfAnnotation.setUsed();
            try {
                pdfWriter.addToBody((PdfObject)pdfAnnotation, pdfAnnotation.getIndirectReference());
                continue;
            }
            catch (IOException var9_10) {
                throw new ExceptionConverter(var9_10);
            }
        }
        return pdfArray;
    }

    public static PdfAnnotation convertAnnotation(PdfWriter pdfWriter, Annotation annotation, Rectangle rectangle) throws IOException {
        switch (annotation.annotationType()) {
            case 1: {
                return new PdfAnnotation(pdfWriter, annotation.llx(), annotation.lly(), annotation.urx(), annotation.ury(), new PdfAction((URL)annotation.attributes().get("url")));
            }
            case 2: {
                return new PdfAnnotation(pdfWriter, annotation.llx(), annotation.lly(), annotation.urx(), annotation.ury(), new PdfAction((String)annotation.attributes().get("file")));
            }
            case 3: {
                return new PdfAnnotation(pdfWriter, annotation.llx(), annotation.lly(), annotation.urx(), annotation.ury(), new PdfAction((String)annotation.attributes().get("file"), (String)annotation.attributes().get("destination")));
            }
            case 7: {
                boolean[] arrbl = (boolean[])annotation.attributes().get("parameters");
                String string = (String)annotation.attributes().get("file");
                String string2 = (String)annotation.attributes().get("mime");
                PdfFileSpecification pdfFileSpecification = arrbl[0] ? PdfFileSpecification.fileEmbedded(pdfWriter, string, string, null) : PdfFileSpecification.fileExtern(pdfWriter, string);
                PdfAnnotation pdfAnnotation = PdfAnnotation.createScreen(pdfWriter, new Rectangle(annotation.llx(), annotation.lly(), annotation.urx(), annotation.ury()), string, pdfFileSpecification, string2, arrbl[1]);
                return pdfAnnotation;
            }
            case 4: {
                return new PdfAnnotation(pdfWriter, annotation.llx(), annotation.lly(), annotation.urx(), annotation.ury(), new PdfAction((String)annotation.attributes().get("file"), (Integer)annotation.attributes().get("page")));
            }
            case 5: {
                return new PdfAnnotation(pdfWriter, annotation.llx(), annotation.lly(), annotation.urx(), annotation.ury(), new PdfAction((Integer)annotation.attributes().get("named")));
            }
            case 6: {
                return new PdfAnnotation(pdfWriter, annotation.llx(), annotation.lly(), annotation.urx(), annotation.ury(), new PdfAction((String)annotation.attributes().get("application"), (String)annotation.attributes().get("parameters"), (String)annotation.attributes().get("operation"), (String)annotation.attributes().get("defaultdir")));
            }
        }
        return new PdfAnnotation(pdfWriter, rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), new PdfString(annotation.title(), "UnicodeBig"), new PdfString(annotation.content(), "UnicodeBig"));
    }
}

