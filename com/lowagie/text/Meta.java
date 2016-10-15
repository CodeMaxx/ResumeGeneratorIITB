/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import java.util.ArrayList;

public class Meta
implements Element {
    private int type;
    private StringBuffer content;

    Meta(int n, String string) {
        this.type = n;
        this.content = new StringBuffer(string);
    }

    public Meta(String string, String string2) {
        this.type = Meta.getType(string);
        this.content = new StringBuffer(string2);
    }

    public boolean process(ElementListener elementListener) {
        try {
            return elementListener.add(this);
        }
        catch (DocumentException var2_2) {
            return false;
        }
    }

    public int type() {
        return this.type;
    }

    public ArrayList getChunks() {
        return new ArrayList();
    }

    public boolean isContent() {
        return false;
    }

    public boolean isNestable() {
        return false;
    }

    public StringBuffer append(String string) {
        return this.content.append(string);
    }

    public String getContent() {
        return this.content.toString();
    }

    public String getName() {
        switch (this.type) {
            case 2: {
                return "subject";
            }
            case 3: {
                return "keywords";
            }
            case 4: {
                return "author";
            }
            case 1: {
                return "title";
            }
            case 5: {
                return "producer";
            }
            case 6: {
                return "creationdate";
            }
        }
        return "unknown";
    }

    public static int getType(String string) {
        if ("subject".equals(string)) {
            return 2;
        }
        if ("keywords".equals(string)) {
            return 3;
        }
        if ("author".equals(string)) {
            return 4;
        }
        if ("title".equals(string)) {
            return 1;
        }
        if ("producer".equals(string)) {
            return 5;
        }
        if ("creationdate".equals(string)) {
            return 6;
        }
        return 0;
    }
}

