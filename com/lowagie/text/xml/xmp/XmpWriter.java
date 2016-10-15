/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.xml.xmp.DublinCoreSchema;
import com.lowagie.text.xml.xmp.PdfA1Schema;
import com.lowagie.text.xml.xmp.PdfSchema;
import com.lowagie.text.xml.xmp.XmpBasicSchema;
import com.lowagie.text.xml.xmp.XmpSchema;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class XmpWriter {
    public static final String UTF8 = "UTF-8";
    public static final String UTF16 = "UTF-16";
    public static final String UTF16BE = "UTF-16BE";
    public static final String UTF16LE = "UTF-16LE";
    public static final String EXTRASPACE = "                                                                                                   \n";
    protected int extraSpace;
    protected OutputStreamWriter writer;
    protected String about;
    protected char end = 119;

    public XmpWriter(OutputStream outputStream, String string, int n) throws IOException {
        this.extraSpace = n;
        this.writer = new OutputStreamWriter(outputStream, string);
        this.writer.write("<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n");
        this.writer.write("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n");
        this.writer.write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n");
        this.about = "";
    }

    public XmpWriter(OutputStream outputStream) throws IOException {
        this(outputStream, "UTF-8", 20);
    }

    public void setReadOnly() {
        this.end = 114;
    }

    public void setAbout(String string) {
        this.about = string;
    }

    public void addRdfDescription(String string, String string2) throws IOException {
        this.writer.write("<rdf:Description rdf:about=\"");
        this.writer.write(this.about);
        this.writer.write("\" ");
        this.writer.write(string);
        this.writer.write(">");
        this.writer.write(string2);
        this.writer.write("</rdf:Description>\n");
    }

    public void addRdfDescription(XmpSchema xmpSchema) throws IOException {
        this.writer.write("<rdf:Description rdf:about=\"");
        this.writer.write(this.about);
        this.writer.write("\" ");
        this.writer.write(xmpSchema.getXmlns());
        this.writer.write(">");
        this.writer.write(xmpSchema.toString());
        this.writer.write("</rdf:Description>\n");
    }

    public void close() throws IOException {
        this.writer.write("</rdf:RDF>");
        this.writer.write("</x:xmpmeta>\n");
        for (int i = 0; i < this.extraSpace; ++i) {
            this.writer.write("                                                                                                   \n");
        }
        this.writer.write("<?xpacket end=\"" + this.end + "\"?>");
        this.writer.flush();
        this.writer.close();
    }

    public XmpWriter(OutputStream outputStream, PdfDictionary pdfDictionary, int n) throws IOException {
        this(outputStream);
        if (pdfDictionary != null) {
            DublinCoreSchema dublinCoreSchema = new DublinCoreSchema();
            PdfSchema pdfSchema = new PdfSchema();
            XmpBasicSchema xmpBasicSchema = new XmpBasicSchema();
            Object object = pdfDictionary.getKeys().iterator();
            while (object.hasNext()) {
                PdfName pdfName = (PdfName)object.next();
                PdfObject pdfObject = pdfDictionary.get(pdfName);
                if (pdfObject == null) continue;
                if (PdfName.TITLE.equals(pdfName)) {
                    dublinCoreSchema.addTitle(((PdfString)pdfObject).toUnicodeString());
                }
                if (PdfName.AUTHOR.equals(pdfName)) {
                    dublinCoreSchema.addAuthor(((PdfString)pdfObject).toUnicodeString());
                }
                if (PdfName.SUBJECT.equals(pdfName)) {
                    dublinCoreSchema.addSubject(((PdfString)pdfObject).toUnicodeString());
                    dublinCoreSchema.addDescription(((PdfString)pdfObject).toUnicodeString());
                }
                if (PdfName.KEYWORDS.equals(pdfName)) {
                    pdfSchema.addKeywords(((PdfString)pdfObject).toUnicodeString());
                }
                if (PdfName.CREATOR.equals(pdfName)) {
                    xmpBasicSchema.addCreatorTool(((PdfString)pdfObject).toUnicodeString());
                }
                if (PdfName.PRODUCER.equals(pdfName)) {
                    pdfSchema.addProducer(((PdfString)pdfObject).toUnicodeString());
                }
                if (PdfName.CREATIONDATE.equals(pdfName)) {
                    xmpBasicSchema.addCreateDate(((PdfDate)pdfObject).getW3CDate());
                }
                if (!PdfName.MODDATE.equals(pdfName)) continue;
                xmpBasicSchema.addModDate(((PdfDate)pdfObject).getW3CDate());
            }
            if (dublinCoreSchema.size() > 0) {
                this.addRdfDescription(dublinCoreSchema);
            }
            if (pdfSchema.size() > 0) {
                this.addRdfDescription(pdfSchema);
            }
            if (xmpBasicSchema.size() > 0) {
                this.addRdfDescription(xmpBasicSchema);
            }
            if (n == 3 || n == 4) {
                object = new PdfA1Schema();
                if (n == 3) {
                    object.addConformance("A");
                } else {
                    object.addConformance("B");
                }
                this.addRdfDescription((XmpSchema)object);
            }
        }
    }

    public XmpWriter(OutputStream outputStream, Map map) throws IOException {
        this(outputStream);
        if (map != null) {
            DublinCoreSchema dublinCoreSchema = new DublinCoreSchema();
            PdfSchema pdfSchema = new PdfSchema();
            XmpBasicSchema xmpBasicSchema = new XmpBasicSchema();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                String string = (String)entry.getKey();
                String string2 = (String)entry.getValue();
                if (string2 == null) continue;
                if ("Title".equals(string)) {
                    dublinCoreSchema.addTitle(string2);
                }
                if ("Author".equals(string)) {
                    dublinCoreSchema.addAuthor(string2);
                }
                if ("Subject".equals(string)) {
                    dublinCoreSchema.addSubject(string2);
                    dublinCoreSchema.addDescription(string2);
                }
                if ("Keywords".equals(string)) {
                    pdfSchema.addKeywords(string2);
                }
                if ("Creator".equals(string)) {
                    xmpBasicSchema.addCreatorTool(string2);
                }
                if ("Producer".equals(string)) {
                    pdfSchema.addProducer(string2);
                }
                if ("CreationDate".equals(string)) {
                    xmpBasicSchema.addCreateDate(PdfDate.getW3CDate(string2));
                }
                if (!"ModDate".equals(string)) continue;
                xmpBasicSchema.addModDate(PdfDate.getW3CDate(string2));
            }
            if (dublinCoreSchema.size() > 0) {
                this.addRdfDescription(dublinCoreSchema);
            }
            if (pdfSchema.size() > 0) {
                this.addRdfDescription(pdfSchema);
            }
            if (xmpBasicSchema.size() > 0) {
                this.addRdfDescription(xmpBasicSchema);
            }
        }
    }
}

