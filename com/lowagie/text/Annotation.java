/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Annotation
implements Element {
    public static final int TEXT = 0;
    public static final int URL_NET = 1;
    public static final int URL_AS_STRING = 2;
    public static final int FILE_DEST = 3;
    public static final int FILE_PAGE = 4;
    public static final int NAMED_DEST = 5;
    public static final int LAUNCH = 6;
    public static final int SCREEN = 7;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String URL = "url";
    public static final String FILE = "file";
    public static final String DESTINATION = "destination";
    public static final String PAGE = "page";
    public static final String NAMED = "named";
    public static final String APPLICATION = "application";
    public static final String PARAMETERS = "parameters";
    public static final String OPERATION = "operation";
    public static final String DEFAULTDIR = "defaultdir";
    public static final String LLX = "llx";
    public static final String LLY = "lly";
    public static final String URX = "urx";
    public static final String URY = "ury";
    public static final String MIMETYPE = "mime";
    protected int annotationtype;
    protected HashMap annotationAttributes = new HashMap();
    protected float llx = Float.NaN;
    protected float lly = Float.NaN;
    protected float urx = Float.NaN;
    protected float ury = Float.NaN;

    private Annotation(float f, float f2, float f3, float f4) {
        this.llx = f;
        this.lly = f2;
        this.urx = f3;
        this.ury = f4;
    }

    public Annotation(Annotation annotation) {
        this.annotationtype = annotation.annotationtype;
        this.annotationAttributes = annotation.annotationAttributes;
        this.llx = annotation.llx;
        this.lly = annotation.lly;
        this.urx = annotation.urx;
        this.ury = annotation.ury;
    }

    public Annotation(String string, String string2) {
        this.annotationtype = 0;
        this.annotationAttributes.put("title", string);
        this.annotationAttributes.put("content", string2);
    }

    public Annotation(String string, String string2, float f, float f2, float f3, float f4) {
        this(f, f2, f3, f4);
        this.annotationtype = 0;
        this.annotationAttributes.put("title", string);
        this.annotationAttributes.put("content", string2);
    }

    public Annotation(float f, float f2, float f3, float f4, URL uRL) {
        this(f, f2, f3, f4);
        this.annotationtype = 1;
        this.annotationAttributes.put("url", uRL);
    }

    public Annotation(float f, float f2, float f3, float f4, String string) {
        this(f, f2, f3, f4);
        this.annotationtype = 2;
        this.annotationAttributes.put("file", string);
    }

    public Annotation(float f, float f2, float f3, float f4, String string, String string2) {
        this(f, f2, f3, f4);
        this.annotationtype = 3;
        this.annotationAttributes.put("file", string);
        this.annotationAttributes.put("destination", string2);
    }

    public Annotation(float f, float f2, float f3, float f4, String string, String string2, boolean bl) {
        this(f, f2, f3, f4);
        this.annotationtype = 7;
        this.annotationAttributes.put("file", string);
        this.annotationAttributes.put("mime", string2);
        this.annotationAttributes.put("parameters", new boolean[]{false, bl});
    }

    public Annotation(float f, float f2, float f3, float f4, String string, int n) {
        this(f, f2, f3, f4);
        this.annotationtype = 4;
        this.annotationAttributes.put("file", string);
        this.annotationAttributes.put("page", new Integer(n));
    }

    public Annotation(float f, float f2, float f3, float f4, int n) {
        this(f, f2, f3, f4);
        this.annotationtype = 5;
        this.annotationAttributes.put("named", new Integer(n));
    }

    public Annotation(float f, float f2, float f3, float f4, String string, String string2, String string3, String string4) {
        this(f, f2, f3, f4);
        this.annotationtype = 6;
        this.annotationAttributes.put("application", string);
        this.annotationAttributes.put("parameters", string2);
        this.annotationAttributes.put("operation", string3);
        this.annotationAttributes.put("defaultdir", string4);
    }

    public int type() {
        return 29;
    }

    public boolean process(ElementListener elementListener) {
        try {
            return elementListener.add(this);
        }
        catch (DocumentException var2_2) {
            return false;
        }
    }

    public ArrayList getChunks() {
        return new ArrayList();
    }

    public void setDimensions(float f, float f2, float f3, float f4) {
        this.llx = f;
        this.lly = f2;
        this.urx = f3;
        this.ury = f4;
    }

    public float llx() {
        return this.llx;
    }

    public float lly() {
        return this.lly;
    }

    public float urx() {
        return this.urx;
    }

    public float ury() {
        return this.ury;
    }

    public float llx(float f) {
        if (Float.isNaN(this.llx)) {
            return f;
        }
        return this.llx;
    }

    public float lly(float f) {
        if (Float.isNaN(this.lly)) {
            return f;
        }
        return this.lly;
    }

    public float urx(float f) {
        if (Float.isNaN(this.urx)) {
            return f;
        }
        return this.urx;
    }

    public float ury(float f) {
        if (Float.isNaN(this.ury)) {
            return f;
        }
        return this.ury;
    }

    public int annotationType() {
        return this.annotationtype;
    }

    public String title() {
        String string = (String)this.annotationAttributes.get("title");
        if (string == null) {
            string = "";
        }
        return string;
    }

    public String content() {
        String string = (String)this.annotationAttributes.get("content");
        if (string == null) {
            string = "";
        }
        return string;
    }

    public HashMap attributes() {
        return this.annotationAttributes;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return true;
    }
}

