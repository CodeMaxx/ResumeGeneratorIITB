/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

public class ListItem
extends Paragraph {
    private static final long serialVersionUID = 1970670787169329006L;
    private Chunk symbol;

    public ListItem() {
    }

    public ListItem(float f) {
        super(f);
    }

    public ListItem(Chunk chunk) {
        super(chunk);
    }

    public ListItem(String string) {
        super(string);
    }

    public ListItem(String string, Font font) {
        super(string, font);
    }

    public ListItem(float f, Chunk chunk) {
        super(f, chunk);
    }

    public ListItem(float f, String string) {
        super(f, string);
    }

    public ListItem(float f, String string, Font font) {
        super(f, string, font);
    }

    public ListItem(Phrase phrase) {
        super(phrase);
    }

    public int type() {
        return 15;
    }

    public void setListSymbol(Chunk chunk) {
        if (this.symbol == null) {
            this.symbol = chunk;
            if (this.symbol.getFont().isStandardFont()) {
                this.symbol.setFont(this.font);
            }
        }
    }

    public void setIndentationLeft(float f, boolean bl) {
        if (bl) {
            this.setIndentationLeft(this.getListSymbol().getWidthPoint());
        } else {
            this.setIndentationLeft(f);
        }
    }

    public Chunk getListSymbol() {
        return this.symbol;
    }
}

