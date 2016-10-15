/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpArray;
import com.lowagie.text.xml.xmp.XmpSchema;

public class XmpBasicSchema
extends XmpSchema {
    private static final long serialVersionUID = -2416613941622479298L;
    public static final String DEFAULT_XPATH_ID = "xmp";
    public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/xap/1.0/";
    public static final String ADVISORY = "xmp:Advisory";
    public static final String BASEURL = "xmp:BaseURL";
    public static final String CREATEDATE = "xmp:CreateDate";
    public static final String CREATORTOOL = "xmp:CreatorTool";
    public static final String IDENTIFIER = "xmp:Identifier";
    public static final String METADATADATE = "xmp:MetadataDate";
    public static final String MODIFYDATE = "xmp:ModifyDate";
    public static final String NICKNAME = "xmp:Nickname";
    public static final String THUMBNAILS = "xmp:Thumbnails";

    public XmpBasicSchema() {
        super("xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"");
    }

    public void addCreatorTool(String string) {
        this.setProperty("xmp:CreatorTool", string);
    }

    public void addCreateDate(String string) {
        this.setProperty("xmp:CreateDate", string);
    }

    public void addModDate(String string) {
        this.setProperty("xmp:ModifyDate", string);
    }

    public void addMetaDataDate(String string) {
        this.setProperty("xmp:MetadataDate", string);
    }

    public void addIdentifiers(String[] arrstring) {
        XmpArray xmpArray = new XmpArray("rdf:Bag");
        for (int i = 0; i < arrstring.length; ++i) {
            xmpArray.add(arrstring[i]);
        }
        this.setProperty("xmp:Identifier", xmpArray);
    }

    public void addNickname(String string) {
        this.setProperty("xmp:Nickname", string);
    }
}

