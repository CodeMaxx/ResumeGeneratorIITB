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

public class ZapfDingbatsNumberList
extends List {
    protected int type;

    public ZapfDingbatsNumberList(int n) {
        super(true);
        this.type = n;
        float f = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", f, 0));
        this.postSymbol = " ";
    }

    public ZapfDingbatsNumberList(int n, int n2) {
        super(true, n2);
        this.type = n;
        float f = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", f, 0));
        this.postSymbol = " ";
    }

    public void setType(int n) {
        this.type = n;
    }

    public int getType() {
        return this.type;
    }

    public boolean add(Object object) {
        if (object instanceof ListItem) {
            ListItem listItem = (ListItem)object;
            Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            switch (this.type) {
                case 0: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 171)));
                    break;
                }
                case 1: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 181)));
                    break;
                }
                case 2: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 191)));
                    break;
                }
                default: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 201)));
                }
            }
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

