/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import java.util.ArrayList;

public class ZapfDingbatsList
extends List {
    protected int zn;

    public ZapfDingbatsList(int n) {
        super(true);
        this.zn = n;
        float f = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", f, 0));
        this.postSymbol = " ";
    }

    public ZapfDingbatsList(int n, int n2) {
        super(true, n2);
        this.zn = n;
        float f = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", f, 0));
        this.postSymbol = " ";
    }

    public void setCharNumber(int n) {
        this.zn = n;
    }

    public int getCharNumber() {
        return this.zn;
    }

    public boolean add(Object object) {
        if (object instanceof ListItem) {
            ListItem listItem = (ListItem)object;
            Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            chunk.append(String.valueOf((char)this.zn));
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

