/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.factories.RomanNumberFactory;
import java.util.ArrayList;

public class RomanList
extends List {
    public RomanList() {
        super(true);
    }

    public RomanList(int n) {
        super(true, n);
    }

    public RomanList(boolean bl, int n) {
        super(true, n);
        this.lowercase = bl;
    }

    public boolean add(Object object) {
        if (object instanceof ListItem) {
            ListItem listItem = (ListItem)object;
            Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            chunk.append(RomanNumberFactory.getString(this.first + this.list.size(), this.lowercase));
            chunk.append(this.postSymbol);
            listItem.setListSymbol(chunk);
            listItem.setIndentationLeft(this.symbolIndent, this.autoindent);
            listItem.setIndentationRight(0.0f);
            this.list.add(listItem);
        } else {
            if (object instanceof List) {
                List list = (List)object;
                list.setIndentationLeft(list.getIndentationLeft() + this.symbolIndent);
                --this.first;
                return this.list.add(list);
            }
            if (object instanceof String) {
                return this.add(new ListItem((String)object));
            }
        }
        return false;
    }
}

