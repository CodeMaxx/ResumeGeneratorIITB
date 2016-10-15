/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.LangAlt;
import com.lowagie.text.xml.xmp.XmpArray;
import java.util.Enumeration;
import java.util.Properties;

public abstract class XmpSchema
extends Properties {
    private static final long serialVersionUID = -176374295948945272L;
    protected String xmlns;

    public XmpSchema(String string) {
        this.xmlns = string;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        Enumeration enumeration = this.propertyNames();
        while (enumeration.hasMoreElements()) {
            this.process(stringBuffer, enumeration.nextElement());
        }
        return stringBuffer.toString();
    }

    protected void process(StringBuffer stringBuffer, Object object) {
        stringBuffer.append('<');
        stringBuffer.append(object);
        stringBuffer.append('>');
        stringBuffer.append(this.get(object));
        stringBuffer.append("</");
        stringBuffer.append(object);
        stringBuffer.append('>');
    }

    public String getXmlns() {
        return this.xmlns;
    }

    public Object addProperty(String string, String string2) {
        return this.setProperty(string, string2);
    }

    public Object setProperty(String string, String string2) {
        return super.setProperty(string, XmpSchema.escape(string2));
    }

    public Object setProperty(String string, XmpArray xmpArray) {
        return super.setProperty(string, xmpArray.toString());
    }

    public Object setProperty(String string, LangAlt langAlt) {
        return super.setProperty(string, langAlt.toString());
    }

    public static String escape(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        block7 : for (int i = 0; i < string.length(); ++i) {
            switch (string.charAt(i)) {
                case '<': {
                    stringBuffer.append("&lt;");
                    continue block7;
                }
                case '>': {
                    stringBuffer.append("&gt;");
                    continue block7;
                }
                case '\'': {
                    stringBuffer.append("&apos;");
                    continue block7;
                }
                case '\"': {
                    stringBuffer.append("&quot;");
                    continue block7;
                }
                case '&': {
                    stringBuffer.append("&amp;");
                    continue block7;
                }
                default: {
                    stringBuffer.append(string.charAt(i));
                }
            }
        }
        return stringBuffer.toString();
    }
}

