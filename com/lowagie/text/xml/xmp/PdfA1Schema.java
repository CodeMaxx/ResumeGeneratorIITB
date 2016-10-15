/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpSchema;

public class PdfA1Schema
extends XmpSchema {
    private static final long serialVersionUID = 5300646133692948168L;
    public static final String DEFAULT_XPATH_ID = "pdfaid";
    public static final String DEFAULT_XPATH_URI = "http://www.aiim.org/pdfa/ns/id/";
    public static final String PART = "pdfaid:part";
    public static final String CONFORMANCE = "pdfaid:conformance";

    public PdfA1Schema() {
        super("xmlns:pdfaid=\"http://www.aiim.org/pdfa/ns/id/\"");
        this.addPart("1");
    }

    public void addPart(String string) {
        this.setProperty("pdfaid:part", string);
    }

    public void addConformance(String string) {
        this.setProperty("pdfaid:conformance", string);
    }
}

