/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpArray;
import com.lowagie.text.xml.xmp.XmpSchema;

public class DublinCoreSchema
extends XmpSchema {
    private static final long serialVersionUID = -4551741356374797330L;
    public static final String DEFAULT_XPATH_ID = "dc";
    public static final String DEFAULT_XPATH_URI = "http://purl.org/dc/elements/1.1/";
    public static final String CONTRIBUTOR = "dc:contributor";
    public static final String COVERAGE = "dc:coverage";
    public static final String CREATOR = "dc:creator";
    public static final String DATE = "dc:date";
    public static final String DESCRIPTION = "dc:description";
    public static final String FORMAT = "dc:format";
    public static final String IDENTIFIER = "dc:identifier";
    public static final String LANGUAGE = "dc:language";
    public static final String PUBLISHER = "dc:publisher";
    public static final String RELATION = "dc:relation";
    public static final String RIGHTS = "dc:rights";
    public static final String SOURCE = "dc:source";
    public static final String SUBJECT = "dc:subject";
    public static final String TITLE = "dc:title";
    public static final String TYPE = "dc:type";

    public DublinCoreSchema() {
        super("xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
        this.setProperty("dc:format", "application/pdf");
    }

    public void addTitle(String string) {
        this.setProperty("dc:title", string);
    }

    public void addDescription(String string) {
        this.setProperty("dc:description", string);
    }

    public void addSubject(String string) {
        XmpArray xmpArray = new XmpArray("rdf:Bag");
        xmpArray.add(string);
        this.setProperty("dc:subject", xmpArray);
    }

    public void addSubject(String[] arrstring) {
        XmpArray xmpArray = new XmpArray("rdf:Bag");
        for (int i = 0; i < arrstring.length; ++i) {
            xmpArray.add(arrstring[i]);
        }
        this.setProperty("dc:subject", xmpArray);
    }

    public void addAuthor(String string) {
        XmpArray xmpArray = new XmpArray("rdf:Seq");
        xmpArray.add(string);
        this.setProperty("dc:creator", xmpArray);
    }

    public void addAuthor(String[] arrstring) {
        XmpArray xmpArray = new XmpArray("rdf:Seq");
        for (int i = 0; i < arrstring.length; ++i) {
            xmpArray.add(arrstring[i]);
        }
        this.setProperty("dc:creator", xmpArray);
    }

    public void addPublisher(String string) {
        XmpArray xmpArray = new XmpArray("rdf:Seq");
        xmpArray.add(string);
        this.setProperty("dc:publisher", xmpArray);
    }

    public void addPublisher(String[] arrstring) {
        XmpArray xmpArray = new XmpArray("rdf:Seq");
        for (int i = 0; i < arrstring.length; ++i) {
            xmpArray.add(arrstring[i]);
        }
        this.setProperty("dc:publisher", xmpArray);
    }
}

