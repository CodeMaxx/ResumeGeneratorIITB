/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import java.util.ArrayList;

public class Chapter
extends Section {
    private static final long serialVersionUID = 1791000695779357361L;

    public Chapter(int n) {
        super(null, 1);
        this.numbers = new ArrayList();
        this.numbers.add(new Integer(n));
        this.triggerNewPage = true;
    }

    public Chapter(Paragraph paragraph, int n) {
        super(paragraph, 1);
        this.numbers = new ArrayList();
        this.numbers.add(new Integer(n));
        this.triggerNewPage = true;
    }

    public Chapter(String string, int n) {
        this(new Paragraph(string), n);
    }

    public int type() {
        return 16;
    }

    public boolean isNestable() {
        return false;
    }
}

