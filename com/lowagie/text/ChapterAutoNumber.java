/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chapter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;

public class ChapterAutoNumber
extends Chapter {
    private static final long serialVersionUID = -9217457637987854167L;

    public ChapterAutoNumber(Paragraph paragraph) {
        super(paragraph, 0);
    }

    public ChapterAutoNumber(String string) {
        super(string, 0);
    }

    public Section addSection(String string) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        return this.addSection(string, 2);
    }

    public Section addSection(Paragraph paragraph) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        return this.addSection(paragraph, 2);
    }
}

