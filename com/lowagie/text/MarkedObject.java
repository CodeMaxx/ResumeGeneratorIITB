/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import java.util.ArrayList;
import java.util.Properties;

public class MarkedObject
implements Element {
    protected Element element;
    protected Properties markupAttributes = new Properties();

    protected MarkedObject() {
        this.element = null;
    }

    public MarkedObject(Element element) {
        this.element = element;
    }

    public ArrayList getChunks() {
        return this.element.getChunks();
    }

    public boolean process(ElementListener elementListener) {
        try {
            return elementListener.add(this.element);
        }
        catch (DocumentException var2_2) {
            return false;
        }
    }

    public int type() {
        return 50;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return true;
    }

    public Properties getMarkupAttributes() {
        return this.markupAttributes;
    }

    public void setMarkupAttribute(String string, String string2) {
        this.markupAttributes.setProperty(string, string2);
    }
}

