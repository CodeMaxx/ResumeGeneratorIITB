/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSigGenericPKCS;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfStamperImp;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PdfSignatureAppearance {
    public static final int SignatureRenderDescription = 0;
    public static final int SignatureRenderNameAndDescription = 1;
    public static final int SignatureRenderGraphicAndDescription = 2;
    public static final PdfName SELF_SIGNED = PdfName.ADOBE_PPKLITE;
    public static final PdfName VERISIGN_SIGNED = PdfName.VERISIGN_PPKVS;
    public static final PdfName WINCER_SIGNED = PdfName.ADOBE_PPKMS;
    public static final int NOT_CERTIFIED = 0;
    public static final int CERTIFIED_NO_CHANGES_ALLOWED = 1;
    public static final int CERTIFIED_FORM_FILLING = 2;
    public static final int CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;
    private static final float TOP_SECTION = 0.3f;
    private static final float MARGIN = 2.0f;
    private Rectangle rect;
    private Rectangle pageRect;
    private PdfTemplate[] app = new PdfTemplate[5];
    private PdfTemplate frm;
    private PdfStamperImp writer;
    private String layer2Text;
    private String reason;
    private String location;
    private Calendar signDate;
    private String provider;
    private int page = 1;
    private String fieldName;
    private PrivateKey privKey;
    private Certificate[] certChain;
    private CRL[] crlList;
    private PdfName filter;
    private boolean newField;
    private ByteBuffer sigout;
    private OutputStream originalout;
    private File tempFile;
    private PdfDictionary cryptoDictionary;
    private PdfStamper stamper;
    private boolean preClosed = false;
    private PdfSigGenericPKCS sigStandard;
    private int[] range;
    private RandomAccessFile raf;
    private byte[] bout;
    private int boutLen;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String digestEncryptionAlgorithm;
    private HashMap exclusionLocations;
    private int render = 0;
    private Image signatureGraphic = null;
    public static final String questionMark = "% DSUnknown\nq\n1 G\n1 g\n0.1 0 0 0.1 9 0 cm\n0 J 0 j 4 M []0 d\n1 i \n0 g\n313 292 m\n313 404 325 453 432 529 c\n478 561 504 597 504 645 c\n504 736 440 760 391 760 c\n286 760 271 681 265 626 c\n265 625 l\n100 625 l\n100 828 253 898 381 898 c\n451 898 679 878 679 650 c\n679 555 628 499 538 435 c\n488 399 467 376 467 292 c\n313 292 l\nh\n308 214 170 -164 re\nf\n0.44 G\n1.2 w\n1 1 0.4 rg\n287 318 m\n287 430 299 479 406 555 c\n451 587 478 623 478 671 c\n478 762 414 786 365 786 c\n260 786 245 707 239 652 c\n239 651 l\n74 651 l\n74 854 227 924 355 924 c\n425 924 653 904 653 676 c\n653 581 602 525 512 461 c\n462 425 441 402 441 318 c\n287 318 l\nh\n282 240 170 -164 re\nB\nQ\n";
    private String contact;
    private Font layer2Font;
    private String layer4Text;
    private boolean acro6Layers;
    private int runDirection = 1;
    private SignatureEvent signatureEvent;
    private Image image;
    private float imageScale;
    private int certificationLevel = 0;

    PdfSignatureAppearance(PdfStamperImp pdfStamperImp) {
        this.writer = pdfStamperImp;
        this.signDate = new GregorianCalendar();
        this.fieldName = this.getNewSigName();
    }

    public int getRender() {
        return this.render;
    }

    public void setRender(int n) {
        this.render = n;
    }

    public Image getSignatureGraphic() {
        return this.signatureGraphic;
    }

    public void setSignatureGraphic(Image image) {
        this.signatureGraphic = image;
    }

    public void setLayer2Text(String string) {
        this.layer2Text = string;
    }

    public String getLayer2Text() {
        return this.layer2Text;
    }

    public void setLayer4Text(String string) {
        this.layer4Text = string;
    }

    public String getLayer4Text() {
        return this.layer4Text;
    }

    public Rectangle getRect() {
        return this.rect;
    }

    public boolean isInvisible() {
        return this.rect == null || this.rect.getWidth() == 0.0f || this.rect.getHeight() == 0.0f;
    }

    public void setCrypto(PrivateKey privateKey, Certificate[] arrcertificate, CRL[] arrcRL, PdfName pdfName) {
        this.privKey = privateKey;
        this.certChain = arrcertificate;
        this.crlList = arrcRL;
        this.filter = pdfName;
    }

    public void setVisibleSignature(Rectangle rectangle, int n, String string) {
        if (string != null) {
            if (string.indexOf(46) >= 0) {
                throw new IllegalArgumentException("Field names cannot contain a dot.");
            }
            AcroFields acroFields = this.writer.getAcroFields();
            AcroFields.Item item = acroFields.getFieldItem(string);
            if (item != null) {
                throw new IllegalArgumentException("The field " + string + " already exists.");
            }
            this.fieldName = string;
        }
        if (n < 1 || n > this.writer.reader.getNumberOfPages()) {
            throw new IllegalArgumentException("Invalid page number: " + n);
        }
        this.pageRect = new Rectangle(rectangle);
        this.pageRect.normalize();
        this.rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
        this.page = n;
        this.newField = true;
    }

    public void setVisibleSignature(String string) {
        AcroFields acroFields = this.writer.getAcroFields();
        AcroFields.Item item = acroFields.getFieldItem(string);
        if (item == null) {
            throw new IllegalArgumentException("The field " + string + " does not exist.");
        }
        PdfDictionary pdfDictionary = (PdfDictionary)item.merged.get(0);
        if (!PdfName.SIG.equals(PdfReader.getPdfObject(pdfDictionary.get(PdfName.FT)))) {
            throw new IllegalArgumentException("The field " + string + " is not a signature field.");
        }
        this.fieldName = string;
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.RECT));
        ArrayList arrayList = pdfArray.getArrayList();
        float f = ((PdfNumber)PdfReader.getPdfObject((PdfObject)arrayList.get(0))).floatValue();
        float f2 = ((PdfNumber)PdfReader.getPdfObject((PdfObject)arrayList.get(1))).floatValue();
        float f3 = ((PdfNumber)PdfReader.getPdfObject((PdfObject)arrayList.get(2))).floatValue();
        float f4 = ((PdfNumber)PdfReader.getPdfObject((PdfObject)arrayList.get(3))).floatValue();
        this.pageRect = new Rectangle(f, f2, f3, f4);
        this.pageRect.normalize();
        this.page = (Integer)item.page.get(0);
        int n = this.writer.reader.getPageRotation(this.page);
        Rectangle rectangle = this.writer.reader.getPageSizeWithRotation(this.page);
        switch (n) {
            case 90: {
                this.pageRect = new Rectangle(this.pageRect.getBottom(), rectangle.getTop() - this.pageRect.getLeft(), this.pageRect.getTop(), rectangle.getTop() - this.pageRect.getRight());
                break;
            }
            case 180: {
                this.pageRect = new Rectangle(rectangle.getRight() - this.pageRect.getLeft(), rectangle.getTop() - this.pageRect.getBottom(), rectangle.getRight() - this.pageRect.getRight(), rectangle.getTop() - this.pageRect.getTop());
                break;
            }
            case 270: {
                this.pageRect = new Rectangle(rectangle.getRight() - this.pageRect.getBottom(), this.pageRect.getLeft(), rectangle.getRight() - this.pageRect.getTop(), this.pageRect.getRight());
            }
        }
        if (n != 0) {
            this.pageRect.normalize();
        }
        this.rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
    }

    public PdfTemplate getLayer(int n) {
        if (n < 0 || n >= this.app.length) {
            return null;
        }
        PdfTemplate pdfTemplate = this.app[n];
        if (pdfTemplate == null) {
            pdfTemplate = this.app[n] = new PdfTemplate(this.writer);
            pdfTemplate.setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple(pdfTemplate, new PdfName("n" + n));
        }
        return pdfTemplate;
    }

    public PdfTemplate getTopLayer() {
        if (this.frm == null) {
            this.frm = new PdfTemplate(this.writer);
            this.frm.setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple(this.frm, new PdfName("FRM"));
        }
        return this.frm;
    }

    public PdfTemplate getAppearance() throws DocumentException {
        Object object;
        float f;
        Object object2;
        Rectangle rectangle;
        Object object3;
        if (this.isInvisible()) {
            PdfTemplate pdfTemplate = new PdfTemplate(this.writer);
            pdfTemplate.setBoundingBox(new Rectangle(0.0f, 0.0f));
            this.writer.addDirectTemplateSimple(pdfTemplate, null);
            return pdfTemplate;
        }
        if (this.app[0] == null) {
            object3 = this.app[0] = new PdfTemplate(this.writer);
            object3.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple((PdfTemplate)object3, new PdfName("n0"));
            object3.setLiteral("% DSBlank\n");
        }
        if (this.app[1] == null && !this.acro6Layers) {
            object3 = this.app[1] = new PdfTemplate(this.writer);
            object3.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple((PdfTemplate)object3, new PdfName("n1"));
            object3.setLiteral("% DSUnknown\nq\n1 G\n1 g\n0.1 0 0 0.1 9 0 cm\n0 J 0 j 4 M []0 d\n1 i \n0 g\n313 292 m\n313 404 325 453 432 529 c\n478 561 504 597 504 645 c\n504 736 440 760 391 760 c\n286 760 271 681 265 626 c\n265 625 l\n100 625 l\n100 828 253 898 381 898 c\n451 898 679 878 679 650 c\n679 555 628 499 538 435 c\n488 399 467 376 467 292 c\n313 292 l\nh\n308 214 170 -164 re\nf\n0.44 G\n1.2 w\n1 1 0.4 rg\n287 318 m\n287 430 299 479 406 555 c\n451 587 478 623 478 671 c\n478 762 414 786 365 786 c\n260 786 245 707 239 652 c\n239 651 l\n74 651 l\n74 854 227 924 355 924 c\n425 924 653 904 653 676 c\n653 581 602 525 512 461 c\n462 425 441 402 441 318 c\n287 318 l\nh\n282 240 170 -164 re\nB\nQ\n");
        }
        if (this.app[2] == null) {
            Rectangle rectangle2;
            Object object4;
            Object object5;
            if (this.layer2Text == null) {
                object2 = new StringBuffer();
                object2.append("Digitally signed by ").append(PdfPKCS7.getSubjectFields((X509Certificate)this.certChain[0]).getField("CN")).append('\n');
                object4 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
                object2.append("Date: ").append(object4.format(this.signDate.getTime()));
                if (this.reason != null) {
                    object2.append('\n').append("Reason: ").append(this.reason);
                }
                if (this.location != null) {
                    object2.append('\n').append("Location: ").append(this.location);
                }
                object3 = object2.toString();
            } else {
                object3 = this.layer2Text;
            }
            object2 = this.app[2] = new PdfTemplate(this.writer);
            object2.setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple((PdfTemplate)object2, new PdfName("n2"));
            if (this.image != null) {
                if (this.imageScale == 0.0f) {
                    object2.addImage(this.image, this.rect.getWidth(), 0.0f, 0.0f, this.rect.getHeight(), 0.0f, 0.0f);
                } else {
                    float f2 = this.imageScale;
                    if (this.imageScale < 0.0f) {
                        f2 = Math.min(this.rect.getWidth() / this.image.getWidth(), this.rect.getHeight() / this.image.getHeight());
                    }
                    f = this.image.getWidth() * f2;
                    float f3 = this.image.getHeight() * f2;
                    float f4 = (this.rect.getWidth() - f) / 2.0f;
                    object5 = (this.rect.getHeight() - f3) / 2.0f;
                    object2.addImage(this.image, f, 0.0f, 0.0f, f3, f4, (float)object5);
                }
            }
            object4 = this.layer2Font == null ? new Font() : new Font(this.layer2Font);
            f = object4.getSize();
            rectangle = null;
            object = null;
            if (this.render == 1 || this.render == 2 && this.signatureGraphic != null) {
                object = new Rectangle(2.0f, 2.0f, this.rect.getWidth() / 2.0f - 2.0f, this.rect.getHeight() - 2.0f);
                rectangle = new Rectangle(this.rect.getWidth() / 2.0f + 1.0f, 2.0f, this.rect.getWidth() - 1.0f, this.rect.getHeight() - 2.0f);
                if (this.rect.getHeight() > this.rect.getWidth()) {
                    object = new Rectangle(2.0f, this.rect.getHeight() / 2.0f, this.rect.getWidth() - 2.0f, this.rect.getHeight());
                    rectangle = new Rectangle(2.0f, 2.0f, this.rect.getWidth() - 2.0f, this.rect.getHeight() / 2.0f - 2.0f);
                }
            } else {
                rectangle = new Rectangle(2.0f, 2.0f, this.rect.getWidth() - 2.0f, this.rect.getHeight() * 0.7f - 2.0f);
            }
            if (this.render == 1) {
                String string = PdfPKCS7.getSubjectFields((X509Certificate)this.certChain[0]).getField("CN");
                rectangle2 = new Rectangle(object.getWidth() - 2.0f, object.getHeight() - 2.0f);
                float f5 = PdfSignatureAppearance.fitText((Font)object4, string, rectangle2, -1.0f, this.runDirection);
                ColumnText columnText = new ColumnText((PdfContentByte)object2);
                columnText.setRunDirection(this.runDirection);
                columnText.setSimpleColumn(new Phrase(string, (Font)object4), object.getLeft(), object.getBottom(), object.getRight(), object.getTop(), f5, 0);
                columnText.go();
            } else if (this.render == 2) {
                ColumnText columnText = new ColumnText((PdfContentByte)object2);
                columnText.setRunDirection(this.runDirection);
                columnText.setSimpleColumn(object.getLeft(), object.getBottom(), object.getRight(), object.getTop(), 0.0f, 2);
                rectangle2 = Image.getInstance(this.signatureGraphic);
                rectangle2.scaleToFit(object.getWidth(), object.getHeight());
                Paragraph paragraph = new Paragraph();
                float f6 = 0.0f;
                float f7 = - rectangle2.getScaledHeight() + 15.0f;
                paragraph.add(new Chunk((Image)rectangle2, (f6 += (object.getWidth() - rectangle2.getScaledWidth()) / 2.0f) + (object.getWidth() - rectangle2.getScaledWidth()) / 2.0f, f7 -= (object.getHeight() - rectangle2.getScaledHeight()) / 2.0f, false));
                columnText.addElement(paragraph);
                columnText.go();
            }
            if (f <= 0.0f) {
                object5 = new Rectangle(rectangle.getWidth(), rectangle.getHeight());
                f = PdfSignatureAppearance.fitText((Font)object4, (String)object3, (Rectangle)object5, 12.0f, this.runDirection);
            }
            object5 = new ColumnText((PdfContentByte)object2);
            object5.setRunDirection(this.runDirection);
            object5.setSimpleColumn(new Phrase((String)object3, (Font)object4), rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), f, 0);
            object5.go();
        }
        if (this.app[3] == null && !this.acro6Layers) {
            object3 = this.app[3] = new PdfTemplate(this.writer);
            object3.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple((PdfTemplate)object3, new PdfName("n3"));
            object3.setLiteral("% DSBlank\n");
        }
        if (this.app[4] == null && !this.acro6Layers) {
            object3 = this.app[4] = new PdfTemplate(this.writer);
            object3.setBoundingBox(new Rectangle(0.0f, this.rect.getHeight() * 0.7f, this.rect.getRight(), this.rect.getTop()));
            this.writer.addDirectTemplateSimple((PdfTemplate)object3, new PdfName("n4"));
            object2 = this.layer2Font == null ? new Font() : new Font(this.layer2Font);
            float f8 = object2.getSize();
            String string = "Signature Not Verified";
            if (this.layer4Text != null) {
                string = this.layer4Text;
            }
            rectangle = new Rectangle(this.rect.getWidth() - 4.0f, this.rect.getHeight() * 0.3f - 4.0f);
            f8 = PdfSignatureAppearance.fitText((Font)object2, string, rectangle, 15.0f, this.runDirection);
            object = new ColumnText((PdfContentByte)object3);
            object.setRunDirection(this.runDirection);
            object.setSimpleColumn(new Phrase(string, (Font)object2), 2.0f, 0.0f, this.rect.getWidth() - 2.0f, this.rect.getHeight() - 2.0f, f8, 0);
            object.go();
        }
        int n = this.writer.reader.getPageRotation(this.page);
        object2 = new Rectangle(this.rect);
        for (int i = n; i > 0; i -= 90) {
            object2 = object2.rotate();
        }
        if (this.frm == null) {
            this.frm = new PdfTemplate(this.writer);
            this.frm.setBoundingBox((Rectangle)object2);
            this.writer.addDirectTemplateSimple(this.frm, new PdfName("FRM"));
            f = Math.min(this.rect.getWidth(), this.rect.getHeight()) * 0.9f;
            float f9 = (this.rect.getWidth() - f) / 2.0f;
            float f10 = (this.rect.getHeight() - f) / 2.0f;
            f /= 100.0f;
            if (n == 90) {
                this.frm.concatCTM(0.0f, 1.0f, -1.0f, 0.0f, this.rect.getHeight(), 0.0f);
            } else if (n == 180) {
                this.frm.concatCTM(-1.0f, 0.0f, 0.0f, -1.0f, this.rect.getWidth(), this.rect.getHeight());
            } else if (n == 270) {
                this.frm.concatCTM(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, this.rect.getWidth());
            }
            this.frm.addTemplate(this.app[0], 0.0f, 0.0f);
            if (!this.acro6Layers) {
                this.frm.addTemplate(this.app[1], f, 0.0f, 0.0f, f, f9, f10);
            }
            this.frm.addTemplate(this.app[2], 0.0f, 0.0f);
            if (!this.acro6Layers) {
                this.frm.addTemplate(this.app[3], f, 0.0f, 0.0f, f, f9, f10);
                this.frm.addTemplate(this.app[4], 0.0f, 0.0f);
            }
        }
        PdfTemplate pdfTemplate = new PdfTemplate(this.writer);
        pdfTemplate.setBoundingBox((Rectangle)object2);
        this.writer.addDirectTemplateSimple(pdfTemplate, null);
        pdfTemplate.addTemplate(this.frm, 0.0f, 0.0f);
        return pdfTemplate;
    }

    public static float fitText(Font font, String string, Rectangle rectangle, float f, int n) {
        try {
            ColumnText columnText = null;
            int n2 = 0;
            if (f <= 0.0f) {
                int n3;
                int n4 = 0;
                int n5 = 0;
                char[] arrc = string.toCharArray();
                for (n3 = 0; n3 < arrc.length; ++n3) {
                    if (arrc[n3] == '\n') {
                        ++n5;
                        continue;
                    }
                    if (arrc[n3] != '\r') continue;
                    ++n4;
                }
                n3 = Math.max(n4, n5) + 1;
                f = Math.abs(rectangle.getHeight()) / (float)n3 - 0.001f;
            }
            font.setSize(f);
            Phrase phrase = new Phrase(string, font);
            columnText = new ColumnText(null);
            columnText.setSimpleColumn(phrase, rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), f, 0);
            columnText.setRunDirection(n);
            n2 = columnText.go(true);
            if ((n2 & 1) != 0) {
                return f;
            }
            float f2 = 0.1f;
            float f3 = 0.0f;
            float f4 = f;
            float f5 = f;
            for (int i = 0; i < 50; ++i) {
                f5 = (f3 + f4) / 2.0f;
                columnText = new ColumnText(null);
                font.setSize(f5);
                columnText.setSimpleColumn(new Phrase(string, font), rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), f5, 0);
                columnText.setRunDirection(n);
                n2 = columnText.go(true);
                if ((n2 & 1) != 0) {
                    if (f4 - f3 < f5 * f2) {
                        return f5;
                    }
                    f3 = f5;
                    continue;
                }
                f4 = f5;
            }
            return f5;
        }
        catch (Exception var5_6) {
            throw new ExceptionConverter(var5_6);
        }
    }

    public void setExternalDigest(byte[] arrby, byte[] arrby2, String string) {
        this.externalDigest = arrby;
        this.externalRSAdata = arrby2;
        this.digestEncryptionAlgorithm = string;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String string) {
        this.reason = string;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String string) {
        this.location = string;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String string) {
        this.provider = string;
    }

    public PrivateKey getPrivKey() {
        return this.privKey;
    }

    public Certificate[] getCertChain() {
        return this.certChain;
    }

    public CRL[] getCrlList() {
        return this.crlList;
    }

    public PdfName getFilter() {
        return this.filter;
    }

    public boolean isNewField() {
        return this.newField;
    }

    public int getPage() {
        return this.page;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Rectangle getPageRect() {
        return this.pageRect;
    }

    public Calendar getSignDate() {
        return this.signDate;
    }

    public void setSignDate(Calendar calendar) {
        this.signDate = calendar;
    }

    ByteBuffer getSigout() {
        return this.sigout;
    }

    void setSigout(ByteBuffer byteBuffer) {
        this.sigout = byteBuffer;
    }

    OutputStream getOriginalout() {
        return this.originalout;
    }

    void setOriginalout(OutputStream outputStream) {
        this.originalout = outputStream;
    }

    public File getTempFile() {
        return this.tempFile;
    }

    void setTempFile(File file) {
        this.tempFile = file;
    }

    public String getNewSigName() {
        AcroFields acroFields = this.writer.getAcroFields();
        String string = "Signature";
        int n = 0;
        boolean bl = false;
        block0 : while (!bl) {
            String string2 = string + ++n;
            if (acroFields.getFieldItem(string2) != null) continue;
            string2 = string2 + ".";
            bl = true;
            Iterator iterator = acroFields.getFields().keySet().iterator();
            while (iterator.hasNext()) {
                String string3 = (String)iterator.next();
                if (!string3.startsWith(string2)) continue;
                bl = false;
                continue block0;
            }
        }
        string = string + n;
        return string;
    }

    public void preClose() throws IOException, DocumentException {
        this.preClose(null);
    }

    public void preClose(HashMap hashMap) throws IOException, DocumentException {
        Iterator iterator;
        Object object;
        Object object2;
        int n;
        int n2;
        Object object3;
        if (this.preClosed) {
            throw new DocumentException("Document already pre closed.");
        }
        this.preClosed = true;
        AcroFields acroFields = this.writer.getAcroFields();
        String string = this.getFieldName();
        boolean bl = !this.isInvisible() && !this.isNewField();
        PdfIndirectReference pdfIndirectReference = this.writer.getPdfIndirectReference();
        this.writer.setSigFlags(3);
        if (bl) {
            object2 = acroFields.getFieldItem((String)string).widgets;
            object = (PdfDictionary)object2.get(0);
            this.writer.markUsed((PdfObject)object);
            object.put(PdfName.P, this.writer.getPageReference(this.getPage()));
            object.put(PdfName.V, pdfIndirectReference);
            iterator = PdfReader.getPdfObjectRelease(object.get(PdfName.F));
            n = 0;
            if (iterator != null && iterator.isNumber()) {
                n = ((PdfNumber)((Object)iterator)).intValue();
            }
            object.put(PdfName.F, new PdfNumber(n |= 128));
            object3 = new PdfDictionary();
            object3.put(PdfName.N, this.getAppearance().getIndirectReference());
            object.put(PdfName.AP, (PdfObject)object3);
        } else {
            object2 = PdfFormField.createSignature(this.writer);
            object2.setFieldName(string);
            object2.put(PdfName.V, pdfIndirectReference);
            object2.setFlags(132);
            int n3 = this.getPage();
            if (!this.isInvisible()) {
                object2.setWidget(this.getPageRect(), null);
            } else {
                object2.setWidget(new Rectangle(0.0f, 0.0f), null);
            }
            object2.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, this.getAppearance());
            object2.setPage(n3);
            this.writer.addAnnotation((PdfAnnotation)object2, n3);
        }
        this.exclusionLocations = new HashMap();
        if (this.cryptoDictionary == null) {
            if (PdfName.ADOBE_PPKLITE.equals(this.getFilter())) {
                this.sigStandard = new PdfSigGenericPKCS.PPKLite(this.getProvider());
            } else if (PdfName.ADOBE_PPKMS.equals(this.getFilter())) {
                this.sigStandard = new PdfSigGenericPKCS.PPKMS(this.getProvider());
            } else if (PdfName.VERISIGN_PPKVS.equals(this.getFilter())) {
                this.sigStandard = new PdfSigGenericPKCS.VeriSign(this.getProvider());
            } else {
                throw new IllegalArgumentException("Unknown filter: " + this.getFilter());
            }
            this.sigStandard.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
            if (this.getReason() != null) {
                this.sigStandard.setReason(this.getReason());
            }
            if (this.getLocation() != null) {
                this.sigStandard.setLocation(this.getLocation());
            }
            if (this.getContact() != null) {
                this.sigStandard.setContact(this.getContact());
            }
            this.sigStandard.put(PdfName.M, new PdfDate(this.getSignDate()));
            this.sigStandard.setSignInfo(this.getPrivKey(), this.getCertChain(), this.getCrlList());
            object2 = (PdfString)this.sigStandard.get(PdfName.CONTENTS);
            object = new PdfLiteral((object2.toString().length() + (PdfName.ADOBE_PPKLITE.equals(this.getFilter()) ? 0 : 64)) * 2 + 2);
            this.exclusionLocations.put(PdfName.CONTENTS, object);
            this.sigStandard.put(PdfName.CONTENTS, (PdfObject)object);
            object = new PdfLiteral(80);
            this.exclusionLocations.put(PdfName.BYTERANGE, object);
            this.sigStandard.put(PdfName.BYTERANGE, (PdfObject)object);
            if (this.certificationLevel > 0) {
                this.addDocMDP(this.sigStandard);
            }
            if (this.signatureEvent != null) {
                this.signatureEvent.getSignatureDictionary(this.sigStandard);
            }
            this.writer.addToBody((PdfObject)this.sigStandard, pdfIndirectReference, false);
        } else {
            object2 = new PdfLiteral(80);
            this.exclusionLocations.put(PdfName.BYTERANGE, object2);
            this.cryptoDictionary.put(PdfName.BYTERANGE, (PdfObject)object2);
            object = hashMap.entrySet().iterator();
            while (object.hasNext()) {
                iterator = (Map.Entry)object.next();
                PdfName pdfName = (PdfName)iterator.getKey();
                object3 = (Integer)iterator.getValue();
                object2 = new PdfLiteral(object3.intValue());
                this.exclusionLocations.put(pdfName, object2);
                this.cryptoDictionary.put(pdfName, (PdfObject)object2);
            }
            if (this.certificationLevel > 0) {
                this.addDocMDP(this.cryptoDictionary);
            }
            if (this.signatureEvent != null) {
                this.signatureEvent.getSignatureDictionary(this.cryptoDictionary);
            }
            this.writer.addToBody((PdfObject)this.cryptoDictionary, pdfIndirectReference, false);
        }
        if (this.certificationLevel > 0) {
            object2 = new PdfDictionary();
            object2.put(new PdfName("DocMDP"), pdfIndirectReference);
            this.writer.reader.getCatalog().put(new PdfName("Perms"), (PdfObject)object2);
        }
        this.writer.close(this.stamper.getMoreInfo());
        this.range = new int[this.exclusionLocations.size() * 2];
        int n4 = ((PdfLiteral)this.exclusionLocations.get(PdfName.BYTERANGE)).getPosition();
        this.exclusionLocations.remove(PdfName.BYTERANGE);
        int n5 = 1;
        iterator = this.exclusionLocations.values().iterator();
        while (iterator.hasNext()) {
            PdfLiteral pdfLiteral = (PdfLiteral)iterator.next();
            int n6 = pdfLiteral.getPosition();
            this.range[n5++] = n6;
            this.range[n5++] = pdfLiteral.getPosLength() + n6;
        }
        Arrays.sort(this.range, 1, this.range.length - 1);
        for (n2 = 3; n2 < this.range.length - 2; n2 += 2) {
            int[] arrn = this.range;
            int n7 = n2;
            arrn[n7] = arrn[n7] - this.range[n2 - 1];
        }
        if (this.tempFile == null) {
            this.bout = this.sigout.getBuffer();
            this.boutLen = this.sigout.size();
            this.range[this.range.length - 1] = this.boutLen - this.range[this.range.length - 2];
            ByteBuffer byteBuffer = new ByteBuffer();
            byteBuffer.append('[');
            for (n = 0; n < this.range.length; ++n) {
                byteBuffer.append(this.range[n]).append(' ');
            }
            byteBuffer.append(']');
            System.arraycopy(byteBuffer.getBuffer(), 0, this.bout, n4, byteBuffer.size());
        } else {
            try {
                this.raf = new RandomAccessFile(this.tempFile, "rw");
                n2 = (int)this.raf.length();
                this.range[this.range.length - 1] = n2 - this.range[this.range.length - 2];
                ByteBuffer byteBuffer = new ByteBuffer();
                byteBuffer.append('[');
                for (int i = 0; i < this.range.length; ++i) {
                    byteBuffer.append(this.range[i]).append(' ');
                }
                byteBuffer.append(']');
                this.raf.seek(n4);
                this.raf.write(byteBuffer.getBuffer(), 0, byteBuffer.size());
            }
            catch (IOException var8_14) {
                try {
                    this.raf.close();
                }
                catch (Exception var9_19) {
                    // empty catch block
                }
                try {
                    this.tempFile.delete();
                }
                catch (Exception var9_20) {
                    // empty catch block
                }
                throw var8_14;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close(PdfDictionary pdfDictionary) throws IOException, DocumentException {
        Exception exception;
        block27 : {
            try {
                byte[] arrby;
                if (!this.preClosed) {
                    throw new DocumentException("preClose() must be called first.");
                }
                ByteBuffer byteBuffer = new ByteBuffer();
                Iterator iterator = pdfDictionary.getKeys().iterator();
                while (iterator.hasNext()) {
                    arrby = (byte[])iterator.next();
                    PdfObject pdfObject = pdfDictionary.get((PdfName)arrby);
                    PdfLiteral pdfLiteral = (PdfLiteral)this.exclusionLocations.get(arrby);
                    if (pdfLiteral == null) {
                        throw new IllegalArgumentException("The key " + arrby.toString() + " didn't reserve space in preClose().");
                    }
                    byteBuffer.reset();
                    pdfObject.toPdf(null, byteBuffer);
                    if (byteBuffer.size() > pdfLiteral.getPosLength()) {
                        throw new IllegalArgumentException("The key " + arrby.toString() + " is too big. Is " + byteBuffer.size() + ", reserved " + pdfLiteral.getPosLength());
                    }
                    if (this.tempFile == null) {
                        System.arraycopy(byteBuffer.getBuffer(), 0, this.bout, pdfLiteral.getPosition(), byteBuffer.size());
                        continue;
                    }
                    this.raf.seek(pdfLiteral.getPosition());
                    this.raf.write(byteBuffer.getBuffer(), 0, byteBuffer.size());
                }
                if (pdfDictionary.size() != this.exclusionLocations.size()) {
                    throw new IllegalArgumentException("The update dictionary has less keys than required.");
                }
                if (this.tempFile == null) {
                    this.originalout.write(this.bout, 0, this.boutLen);
                } else if (this.originalout != null) {
                    int n;
                    this.raf.seek(0);
                    arrby = new byte[8192];
                    for (int i = (int)this.raf.length(); i > 0; i -= n) {
                        n = this.raf.read(arrby, 0, Math.min(arrby.length, i));
                        if (n < 0) {
                            throw new EOFException("Unexpected EOF");
                        }
                        this.originalout.write(arrby, 0, n);
                    }
                }
                Object var8_9 = null;
                if (this.tempFile == null) break block27;
            }
            catch (Throwable var7_13) {
                Exception exception2;
                Object var8_10 = null;
                if (this.tempFile != null) {
                    try {
                        this.raf.close();
                    }
                    catch (Exception exception2) {
                        // empty catch block
                    }
                    if (this.originalout != null) {
                        try {
                            this.tempFile.delete();
                        }
                        catch (Exception exception2) {
                            // empty catch block
                        }
                    }
                }
                if (this.originalout != null) {
                    try {
                        this.originalout.close();
                    }
                    catch (Exception exception2) {
                        // empty catch block
                    }
                }
                throw var7_13;
            }
            try {
                this.raf.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (this.originalout != null) {
                try {
                    this.tempFile.delete();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        if (this.originalout != null) {
            try {
                this.originalout.close();
            }
            catch (Exception exception) {}
        }
    }

    private void addDocMDP(PdfDictionary pdfDictionary) {
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        PdfDictionary pdfDictionary3 = new PdfDictionary();
        pdfDictionary3.put(PdfName.P, new PdfNumber(this.certificationLevel));
        pdfDictionary3.put(PdfName.V, new PdfName("1.2"));
        pdfDictionary3.put(PdfName.TYPE, PdfName.TRANSFORMPARAMS);
        pdfDictionary2.put(PdfName.TRANSFORMMETHOD, PdfName.DOCMDP);
        pdfDictionary2.put(PdfName.TYPE, PdfName.SIGREF);
        pdfDictionary2.put(PdfName.TRANSFORMPARAMS, pdfDictionary3);
        pdfDictionary2.put(new PdfName("DigestValue"), new PdfString("aa"));
        PdfArray pdfArray = new PdfArray();
        pdfArray.add(new PdfNumber(0));
        pdfArray.add(new PdfNumber(0));
        pdfDictionary2.put(new PdfName("DigestLocation"), pdfArray);
        pdfDictionary2.put(new PdfName("DigestMethod"), new PdfName("MD5"));
        pdfDictionary2.put(PdfName.DATA, this.writer.reader.getTrailer().get(PdfName.ROOT));
        PdfArray pdfArray2 = new PdfArray();
        pdfArray2.add(pdfDictionary2);
        pdfDictionary.put(PdfName.REFERENCE, pdfArray2);
    }

    public InputStream getRangeStream() {
        return new RangeStream(this.raf, this.bout, this.range);
    }

    public PdfDictionary getCryptoDictionary() {
        return this.cryptoDictionary;
    }

    public void setCryptoDictionary(PdfDictionary pdfDictionary) {
        this.cryptoDictionary = pdfDictionary;
    }

    public PdfStamper getStamper() {
        return this.stamper;
    }

    void setStamper(PdfStamper pdfStamper) {
        this.stamper = pdfStamper;
    }

    public boolean isPreClosed() {
        return this.preClosed;
    }

    public PdfSigGenericPKCS getSigStandard() {
        return this.sigStandard;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String string) {
        this.contact = string;
    }

    public Font getLayer2Font() {
        return this.layer2Font;
    }

    public void setLayer2Font(Font font) {
        this.layer2Font = font;
    }

    public boolean isAcro6Layers() {
        return this.acro6Layers;
    }

    public void setAcro6Layers(boolean bl) {
        this.acro6Layers = bl;
    }

    public void setRunDirection(int n) {
        if (n < 0 || n > 3) {
            throw new RuntimeException("Invalid run direction: " + n);
        }
        this.runDirection = n;
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public SignatureEvent getSignatureEvent() {
        return this.signatureEvent;
    }

    public void setSignatureEvent(SignatureEvent signatureEvent) {
        this.signatureEvent = signatureEvent;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public float getImageScale() {
        return this.imageScale;
    }

    public void setImageScale(float f) {
        this.imageScale = f;
    }

    public int getCertificationLevel() {
        return this.certificationLevel;
    }

    public void setCertificationLevel(int n) {
        this.certificationLevel = n;
    }

    public static interface SignatureEvent {
        public void getSignatureDictionary(PdfDictionary var1);
    }

    private static class RangeStream
    extends InputStream {
        private byte[] b = new byte[1];
        private RandomAccessFile raf;
        private byte[] bout;
        private int[] range;
        private int rangePosition = 0;

        private RangeStream(RandomAccessFile randomAccessFile, byte[] arrby, int[] arrn) {
            this.raf = randomAccessFile;
            this.bout = arrby;
            this.range = arrn;
        }

        public int read() throws IOException {
            int n = this.read(this.b);
            if (n != 1) {
                return -1;
            }
            return this.b[0] & 255;
        }

        public int read(byte[] arrby, int n, int n2) throws IOException {
            if (arrby == null) {
                throw new NullPointerException();
            }
            if (n < 0 || n > arrby.length || n2 < 0 || n + n2 > arrby.length || n + n2 < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (n2 == 0) {
                return 0;
            }
            if (this.rangePosition >= this.range[this.range.length - 2] + this.range[this.range.length - 1]) {
                return -1;
            }
            for (int i = 0; i < this.range.length; i += 2) {
                int n3 = this.range[i];
                int n4 = n3 + this.range[i + 1];
                if (this.rangePosition < n3) {
                    this.rangePosition = n3;
                }
                if (this.rangePosition < n3 || this.rangePosition >= n4) continue;
                int n5 = Math.min(n2, n4 - this.rangePosition);
                if (this.raf == null) {
                    System.arraycopy(this.bout, this.rangePosition, arrby, n, n5);
                } else {
                    this.raf.seek(this.rangePosition);
                    this.raf.readFully(arrby, n, n5);
                }
                this.rangePosition += n5;
                return n5;
            }
            return -1;
        }
    }

}

