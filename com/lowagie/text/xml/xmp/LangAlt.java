/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpSchema;
import java.util.Enumeration;
import java.util.Properties;

public class LangAlt
extends Properties {
    private static final long serialVersionUID = 4396971487200843099L;
    public static final String DEFAULT = "x-default";

    public LangAlt(String string) {
        this.addLanguage("x-default", string);
    }

    public LangAlt() {
    }

    public void addLanguage(String string, String string2) {
        this.setProperty(string, XmpSchema.escape(string2));
    }

    protected void process(StringBuffer stringBuffer, Object object) {
        stringBuffer.append("<rdf:li xml:lang=\"");
        stringBuffer.append(object);
        stringBuffer.append("\" >");
        stringBuffer.append(this.get(object));
        stringBuffer.append("</rdf:li>");
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<rdf:Alt>");
        Enumeration enumeration = this.propertyNames();
        while (enumeration.hasMoreElements()) {
            this.process(stringBuffer, enumeration.nextElement());
        }
        stringBuffer.append("</rdf:Alt>");
        return stringBuffer.toString();
    }
}

