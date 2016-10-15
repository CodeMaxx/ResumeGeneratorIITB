/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpSchema;
import java.util.ArrayList;
import java.util.Iterator;

public class XmpArray
extends ArrayList {
    private static final long serialVersionUID = 5722854116328732742L;
    public static final String UNORDERED = "rdf:Bag";
    public static final String ORDERED = "rdf:Seq";
    public static final String ALTERNATIVE = "rdf:Alt";
    protected String type;

    public XmpArray(String string) {
        this.type = string;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("<");
        stringBuffer.append(this.type);
        stringBuffer.append('>');
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            String string = (String)iterator.next();
            stringBuffer.append("<rdf:li>");
            stringBuffer.append(XmpSchema.escape(string));
            stringBuffer.append("</rdf:li>");
        }
        stringBuffer.append("</");
        stringBuffer.append(this.type);
        stringBuffer.append('>');
        return stringBuffer.toString();
    }
}

