/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.ListItem;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.factories.RomanAlphabetFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class List
implements TextElementArray {
    public static final boolean ORDERED = true;
    public static final boolean UNORDERED = false;
    public static final boolean NUMERICAL = false;
    public static final boolean ALPHABETICAL = true;
    public static final boolean UPPERCASE = false;
    public static final boolean LOWERCASE = true;
    protected ArrayList list = new ArrayList();
    protected boolean numbered = false;
    protected boolean lettered = false;
    protected boolean lowercase = false;
    protected boolean autoindent = false;
    protected boolean alignindent = false;
    protected int first = 1;
    protected Chunk symbol = new Chunk("- ");
    protected String preSymbol = "";
    protected String postSymbol = ". ";
    protected float indentationLeft = 0.0f;
    protected float indentationRight = 0.0f;
    protected float symbolIndent = 0.0f;

    public List() {
        this(false, false);
    }

    public List(float f) {
        this.symbolIndent = f;
    }

    public List(boolean bl) {
        this(bl, false);
    }

    public List(boolean bl, boolean bl2) {
        this.numbered = bl;
        this.lettered = bl2;
        this.autoindent = true;
        this.alignindent = true;
    }

    public List(boolean bl, float f) {
        this(bl, false, f);
    }

    public List(boolean bl, boolean bl2, float f) {
        this.numbered = bl;
        this.lettered = bl2;
        this.symbolIndent = f;
    }

    public boolean process(ElementListener elementListener) {
        try {
            Iterator iterator = this.list.iterator();
            while (iterator.hasNext()) {
                elementListener.add((Element)iterator.next());
            }
            return true;
        }
        catch (DocumentException var2_3) {
            return false;
        }
    }

    public int type() {
        return 14;
    }

    public ArrayList getChunks() {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.list.iterator();
        while (iterator.hasNext()) {
            arrayList.addAll(((Element)iterator.next()).getChunks());
        }
        return arrayList;
    }

    public boolean add(Object object) {
        if (object instanceof ListItem) {
            ListItem listItem = (ListItem)object;
            if (this.numbered || this.lettered) {
                Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
                int n = this.first + this.list.size();
                if (this.lettered) {
                    chunk.append(RomanAlphabetFactory.getString(n, this.lowercase));
                } else {
                    chunk.append(String.valueOf(n));
                }
                chunk.append(this.postSymbol);
                listItem.setListSymbol(chunk);
            } else {
                listItem.setListSymbol(this.symbol);
            }
            listItem.setIndentationLeft(this.symbolIndent, this.autoindent);
            listItem.setIndentationRight(0.0f);
            return this.list.add(listItem);
        }
        if (object instanceof List) {
            List list = (List)object;
            list.setIndentationLeft(list.getIndentationLeft() + this.symbolIndent);
            --this.first;
            return this.list.add(list);
        }
        if (object instanceof String) {
            return this.add(new ListItem((String)object));
        }
        return false;
    }

    public void normalizeIndentation() {
        Element element;
        float f = 0.0f;
        Iterator iterator = this.list.iterator();
        while (iterator.hasNext()) {
            element = (Element)iterator.next();
            if (!(element instanceof ListItem)) continue;
            f = Math.max(f, ((ListItem)element).getIndentationLeft());
        }
        iterator = this.list.iterator();
        while (iterator.hasNext()) {
            element = (Element)iterator.next();
            if (!(element instanceof ListItem)) continue;
            ((ListItem)element).setIndentationLeft(f);
        }
    }

    public void setNumbered(boolean bl) {
        this.numbered = bl;
    }

    public void setLettered(boolean bl) {
        this.lettered = bl;
    }

    public void setLowercase(boolean bl) {
        this.lowercase = bl;
    }

    public void setAutoindent(boolean bl) {
        this.autoindent = bl;
    }

    public void setAlignindent(boolean bl) {
        this.alignindent = bl;
    }

    public void setFirst(int n) {
        this.first = n;
    }

    public void setListSymbol(Chunk chunk) {
        this.symbol = chunk;
    }

    public void setListSymbol(String string) {
        this.symbol = new Chunk(string);
    }

    public void setIndentationLeft(float f) {
        this.indentationLeft = f;
    }

    public void setIndentationRight(float f) {
        this.indentationRight = f;
    }

    public void setSymbolIndent(float f) {
        this.symbolIndent = f;
    }

    public ArrayList getItems() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public float getTotalLeading() {
        if (this.list.size() < 1) {
            return -1.0f;
        }
        ListItem listItem = (ListItem)this.list.get(0);
        return listItem.getTotalLeading();
    }

    public boolean isNumbered() {
        return this.numbered;
    }

    public boolean isLettered() {
        return this.lettered;
    }

    public boolean isLowercase() {
        return this.lowercase;
    }

    public boolean isAutoindent() {
        return this.autoindent;
    }

    public boolean isAlignindent() {
        return this.alignindent;
    }

    public int getFirst() {
        return this.first;
    }

    public Chunk getSymbol() {
        return this.symbol;
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public float getSymbolIndent() {
        return this.symbolIndent;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return true;
    }

    public String getPostSymbol() {
        return this.postSymbol;
    }

    public void setPostSymbol(String string) {
        this.postSymbol = string;
    }

    public String getPreSymbol() {
        return this.preSymbol;
    }

    public void setPreSymbol(String string) {
        this.preSymbol = string;
    }
}

