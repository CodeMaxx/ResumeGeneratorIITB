/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.Phrase;
import java.util.ArrayList;

public class Paragraph
extends Phrase {
    private static final long serialVersionUID = 7852314969733375514L;
    protected int alignment = -1;
    protected float multipliedLeading = 0.0f;
    protected float indentationLeft;
    protected float indentationRight;
    private float firstLineIndent = 0.0f;
    protected float spacingBefore;
    protected float spacingAfter;
    private float extraParagraphSpace = 0.0f;
    protected boolean keeptogether = false;

    public Paragraph() {
    }

    public Paragraph(float f) {
        super(f);
    }

    public Paragraph(Chunk chunk) {
        super(chunk);
    }

    public Paragraph(float f, Chunk chunk) {
        super(f, chunk);
    }

    public Paragraph(String string) {
        super(string);
    }

    public Paragraph(String string, Font font) {
        super(string, font);
    }

    public Paragraph(float f, String string) {
        super(f, string);
    }

    public Paragraph(float f, String string, Font font) {
        super(f, string, font);
    }

    public Paragraph(Phrase phrase) {
        super(phrase);
        if (phrase instanceof Paragraph) {
            Paragraph paragraph = (Paragraph)phrase;
            this.setAlignment(paragraph.alignment);
            this.setLeading(phrase.getLeading(), paragraph.multipliedLeading);
            this.setIndentationLeft(paragraph.getIndentationLeft());
            this.setIndentationRight(paragraph.getIndentationRight());
            this.setFirstLineIndent(paragraph.getFirstLineIndent());
            this.setSpacingAfter(paragraph.spacingAfter());
            this.setSpacingBefore(paragraph.spacingBefore());
            this.setExtraParagraphSpace(paragraph.getExtraParagraphSpace());
        }
    }

    public int type() {
        return 12;
    }

    public boolean add(Object object) {
        if (object instanceof List) {
            List list = (List)object;
            list.setIndentationLeft(list.getIndentationLeft() + this.indentationLeft);
            list.setIndentationRight(this.indentationRight);
            return super.add(list);
        }
        if (object instanceof Image) {
            super.addSpecial(object);
            return true;
        }
        if (object instanceof Paragraph) {
            super.add(object);
            if (this.size() > 0) {
                Chunk chunk = (Chunk)this.getChunks().get(this.size() - 1);
                super.add(new Chunk("\n", chunk.getFont()));
            } else {
                super.add(Chunk.NEWLINE);
            }
            return true;
        }
        return super.add(object);
    }

    public void setAlignment(int n) {
        this.alignment = n;
    }

    public void setAlignment(String string) {
        if ("Center".equalsIgnoreCase(string)) {
            this.alignment = 1;
            return;
        }
        if ("Right".equalsIgnoreCase(string)) {
            this.alignment = 2;
            return;
        }
        if ("Justify".equalsIgnoreCase(string)) {
            this.alignment = 3;
            return;
        }
        if ("JustifyAll".equalsIgnoreCase(string)) {
            this.alignment = 8;
            return;
        }
        this.alignment = 0;
    }

    public void setLeading(float f) {
        this.leading = f;
        this.multipliedLeading = 0.0f;
    }

    public void setMultipliedLeading(float f) {
        this.leading = 0.0f;
        this.multipliedLeading = f;
    }

    public void setLeading(float f, float f2) {
        this.leading = f;
        this.multipliedLeading = f2;
    }

    public void setIndentationLeft(float f) {
        this.indentationLeft = f;
    }

    public void setIndentationRight(float f) {
        this.indentationRight = f;
    }

    public void setFirstLineIndent(float f) {
        this.firstLineIndent = f;
    }

    public void setSpacingBefore(float f) {
        this.spacingBefore = f;
    }

    public void setSpacingAfter(float f) {
        this.spacingAfter = f;
    }

    public void setKeepTogether(boolean bl) {
        this.keeptogether = bl;
    }

    public boolean getKeepTogether() {
        return this.keeptogether;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public float getMultipliedLeading() {
        return this.multipliedLeading;
    }

    public float getTotalLeading() {
        float f;
        float f2 = f = this.font == null ? 12.0f * this.multipliedLeading : this.font.getCalculatedLeading(this.multipliedLeading);
        if (f > 0.0f && !this.hasLeading()) {
            return f;
        }
        return this.getLeading() + f;
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public float getFirstLineIndent() {
        return this.firstLineIndent;
    }

    public float spacingBefore() {
        return this.spacingBefore;
    }

    public float spacingAfter() {
        return this.spacingAfter;
    }

    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }

    public void setExtraParagraphSpace(float f) {
        this.extraParagraphSpace = f;
    }
}

