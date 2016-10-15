/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Anchor
extends Phrase {
    private static final long serialVersionUID = -852278536049236911L;
    protected String name = null;
    protected String reference = null;

    public Anchor() {
        super(16.0f);
    }

    public Anchor(float f) {
        super(f);
    }

    public Anchor(Chunk chunk) {
        super(chunk);
    }

    public Anchor(String string) {
        super(string);
    }

    public Anchor(String string, Font font) {
        super(string, font);
    }

    public Anchor(float f, Chunk chunk) {
        super(f, chunk);
    }

    public Anchor(float f, String string) {
        super(f, string);
    }

    public Anchor(float f, String string, Font font) {
        super(f, string, font);
    }

    public Anchor(Phrase phrase) {
        super(phrase);
        if (phrase instanceof Anchor) {
            Anchor anchor = (Anchor)phrase;
            this.setName(anchor.name);
            this.setReference(anchor.reference);
        }
    }

    public boolean process(ElementListener elementListener) {
        try {
            Iterator iterator = this.getChunks().iterator();
            boolean bl = this.reference != null && this.reference.startsWith("#");
            boolean bl2 = true;
            while (iterator.hasNext()) {
                Chunk chunk = (Chunk)iterator.next();
                if (this.name != null && bl2 && !chunk.isEmpty()) {
                    chunk.setLocalDestination(this.name);
                    bl2 = false;
                }
                if (bl) {
                    chunk.setLocalGoto(this.reference.substring(1));
                }
                elementListener.add(chunk);
            }
            return true;
        }
        catch (DocumentException var2_6) {
            return false;
        }
    }

    public ArrayList getChunks() {
        ArrayList<Chunk> arrayList = new ArrayList<Chunk>();
        Iterator iterator = this.iterator();
        boolean bl = this.reference != null && this.reference.startsWith("#");
        boolean bl2 = true;
        while (iterator.hasNext()) {
            Chunk chunk = (Chunk)iterator.next();
            if (this.name != null && bl2 && !chunk.isEmpty()) {
                chunk.setLocalDestination(this.name);
                bl2 = false;
            }
            if (bl) {
                chunk.setLocalGoto(this.reference.substring(1));
            } else if (this.reference != null) {
                chunk.setAnchor(this.reference);
            }
            arrayList.add(chunk);
        }
        return arrayList;
    }

    public int type() {
        return 17;
    }

    public void setName(String string) {
        this.name = string;
    }

    public void setReference(String string) {
        this.reference = string;
    }

    public String getName() {
        return this.name;
    }

    public String getReference() {
        return this.reference;
    }

    public URL getUrl() {
        try {
            return new URL(this.reference);
        }
        catch (MalformedURLException var1_1) {
            return null;
        }
    }
}

