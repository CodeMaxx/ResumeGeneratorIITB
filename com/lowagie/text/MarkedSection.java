/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

public class MarkedSection
extends MarkedObject {
    protected MarkedObject title = null;

    public MarkedSection(Section section) {
        if (section.title != null) {
            this.title = new MarkedObject(section.title);
            section.setTitle(null);
        }
        this.element = section;
    }

    public void add(int n, Object object) {
        ((Section)this.element).add(n, object);
    }

    public boolean add(Object object) {
        return ((Section)this.element).add(object);
    }

    public boolean process(ElementListener elementListener) {
        try {
            Iterator iterator = ((Section)this.element).iterator();
            while (iterator.hasNext()) {
                Element element = (Element)iterator.next();
                elementListener.add(element);
            }
            return true;
        }
        catch (DocumentException var2_4) {
            return false;
        }
    }

    public boolean addAll(Collection collection) {
        return ((Section)this.element).addAll(collection);
    }

    public MarkedSection addSection(float f, int n) {
        MarkedSection markedSection = ((Section)this.element).addMarkedSection();
        markedSection.setIndentation(f);
        markedSection.setNumberDepth(n);
        return markedSection;
    }

    public MarkedSection addSection(float f) {
        MarkedSection markedSection = ((Section)this.element).addMarkedSection();
        markedSection.setIndentation(f);
        return markedSection;
    }

    public MarkedSection addSection(int n) {
        MarkedSection markedSection = ((Section)this.element).addMarkedSection();
        markedSection.setNumberDepth(n);
        return markedSection;
    }

    public MarkedSection addSection() {
        return ((Section)this.element).addMarkedSection();
    }

    public void setTitle(MarkedObject markedObject) {
        if (markedObject.element instanceof Paragraph) {
            this.title = markedObject;
        }
    }

    public MarkedObject getTitle() {
        Paragraph paragraph = Section.constructTitle((Paragraph)this.title.element, ((Section)this.element).numbers, ((Section)this.element).numberDepth, ((Section)this.element).numberStyle);
        MarkedObject markedObject = new MarkedObject(paragraph);
        markedObject.markupAttributes = this.title.markupAttributes;
        return markedObject;
    }

    public void setNumberDepth(int n) {
        ((Section)this.element).setNumberDepth(n);
    }

    public void setIndentationLeft(float f) {
        ((Section)this.element).setIndentationLeft(f);
    }

    public void setIndentationRight(float f) {
        ((Section)this.element).setIndentationRight(f);
    }

    public void setIndentation(float f) {
        ((Section)this.element).setIndentation(f);
    }

    public void setBookmarkOpen(boolean bl) {
        ((Section)this.element).setBookmarkOpen(bl);
    }

    public void setTriggerNewPage(boolean bl) {
        ((Section)this.element).setTriggerNewPage(bl);
    }

    public void setBookmarkTitle(String string) {
        ((Section)this.element).setBookmarkTitle(string);
    }

    public void newPage() {
        ((Section)this.element).newPage();
    }
}

