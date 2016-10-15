/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.RtfElementInterface;
import com.lowagie.text.SpecialSymbol;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.HyphenationEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Phrase
extends ArrayList
implements TextElementArray {
    private static final long serialVersionUID = 2643594602455068231L;
    protected float leading = Float.NaN;
    protected Font font;
    protected HyphenationEvent hyphenation = null;

    public Phrase() {
        this(16.0f);
    }

    public Phrase(Phrase phrase) {
        this.addAll(phrase);
        this.leading = phrase.getLeading();
        this.font = phrase.getFont();
        this.setHyphenation(phrase.getHyphenation());
    }

    public Phrase(float f) {
        this.leading = f;
        this.font = new Font();
    }

    public Phrase(Chunk chunk) {
        super.add(chunk);
        this.font = chunk.getFont();
        this.setHyphenation(chunk.getHyphenation());
    }

    public Phrase(float f, Chunk chunk) {
        this.leading = f;
        super.add(chunk);
        this.font = chunk.getFont();
        this.setHyphenation(chunk.getHyphenation());
    }

    public Phrase(String string) {
        this(Float.NaN, string, new Font());
    }

    public Phrase(String string, Font font) {
        this(Float.NaN, string, font);
    }

    public Phrase(float f, String string) {
        this(f, string, new Font());
    }

    public Phrase(float f, String string, Font font) {
        this.leading = f;
        this.font = font;
        if (string != null && string.length() != 0) {
            super.add(new Chunk(string, font));
        }
    }

    public boolean process(ElementListener elementListener) {
        try {
            Iterator iterator = this.iterator();
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
        return 11;
    }

    public ArrayList getChunks() {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            arrayList.addAll(((Element)iterator.next()).getChunks());
        }
        return arrayList;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return true;
    }

    public void add(int n, Object object) {
        block7 : {
            if (object == null) {
                return;
            }
            try {
                Element element = (Element)object;
                if (element.type() == 10) {
                    Chunk chunk = (Chunk)element;
                    if (!this.font.isStandardFont()) {
                        chunk.setFont(this.font.difference(chunk.getFont()));
                    }
                    if (this.hyphenation != null) {
                        chunk.setHyphenation(this.hyphenation);
                    }
                    super.add(n, chunk);
                    break block7;
                }
                if (element.type() == 11 || element.type() == 17 || element.type() == 29 || element.type() == 22 || element.type() == 55 || element.type() == 50) {
                    super.add(n, element);
                    break block7;
                }
                throw new ClassCastException(String.valueOf(element.type()));
            }
            catch (ClassCastException var3_4) {
                throw new ClassCastException("Insertion of illegal Element: " + var3_4.getMessage());
            }
        }
    }

    public boolean add(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof String) {
            return super.add(new Chunk((String)object, this.font));
        }
        if (object instanceof RtfElementInterface) {
            return super.add(object);
        }
        try {
            Element element = (Element)object;
            switch (element.type()) {
                case 10: {
                    return this.addChunk((Chunk)object);
                }
                case 11: 
                case 12: {
                    Phrase phrase = (Phrase)object;
                    boolean bl = true;
                    Iterator iterator = phrase.iterator();
                    while (iterator.hasNext()) {
                        Element element2 = (Element)iterator.next();
                        if (element2 instanceof Chunk) {
                            bl &= this.addChunk((Chunk)element2);
                            continue;
                        }
                        bl &= this.add(element2);
                    }
                    return bl;
                }
                case 14: 
                case 17: 
                case 22: 
                case 23: 
                case 29: 
                case 50: 
                case 55: {
                    return super.add(object);
                }
            }
            throw new ClassCastException(String.valueOf(element.type()));
        }
        catch (ClassCastException var2_3) {
            throw new ClassCastException("Insertion of illegal Element: " + var2_3.getMessage());
        }
    }

    public boolean addAll(Collection collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.add(iterator.next());
        }
        return true;
    }

    protected boolean addChunk(Chunk chunk) {
        Chunk chunk2;
        Font font = chunk.getFont();
        String string = chunk.getContent();
        if (this.font != null && !this.font.isStandardFont()) {
            font = this.font.difference(chunk.getFont());
        }
        if (this.size() > 0 && !chunk.hasAttributes()) {
            try {
                chunk2 = (Chunk)this.get(this.size() - 1);
                if (!(chunk2.hasAttributes() || font != null && font.compareTo(chunk2.getFont()) != 0 || "".equals(chunk2.getContent().trim()) || "".equals(string.trim()))) {
                    chunk2.append(string);
                    return true;
                }
            }
            catch (ClassCastException var4_5) {
                // empty catch block
            }
        }
        chunk2 = new Chunk(string, font);
        chunk2.setAttributes(chunk.getAttributes());
        if (chunk2.getHyphenation() == null) {
            chunk2.setHyphenation(this.hyphenation);
        }
        return super.add(chunk2);
    }

    protected void addSpecial(Object object) {
        super.add(object);
    }

    public void setLeading(float f) {
        this.leading = f;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public float getLeading() {
        if (Float.isNaN(this.leading) && this.font != null) {
            return this.font.getCalculatedLeading(1.5f);
        }
        return this.leading;
    }

    public boolean hasLeading() {
        if (Float.isNaN(this.leading)) {
            return false;
        }
        return true;
    }

    public Font getFont() {
        return this.font;
    }

    public String getContent() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator iterator = this.getChunks().iterator();
        while (iterator.hasNext()) {
            stringBuffer.append(iterator.next().toString());
        }
        return stringBuffer.toString();
    }

    public boolean isEmpty() {
        switch (this.size()) {
            case 0: {
                return true;
            }
            case 1: {
                Element element = (Element)this.get(0);
                if (element.type() == 10 && ((Chunk)element).isEmpty()) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public HyphenationEvent getHyphenation() {
        return this.hyphenation;
    }

    public void setHyphenation(HyphenationEvent hyphenationEvent) {
        this.hyphenation = hyphenationEvent;
    }

    private Phrase(boolean bl) {
    }

    public static final Phrase getInstance(String string) {
        return Phrase.getInstance(16, string, new Font());
    }

    public static final Phrase getInstance(int n, String string) {
        return Phrase.getInstance(n, string, new Font());
    }

    public static final Phrase getInstance(int n, String string, Font font) {
        Phrase phrase = new Phrase(true);
        phrase.setLeading(n);
        phrase.font = font;
        if (font.getFamily() != 3 && font.getFamily() != 4 && font.getBaseFont() == null) {
            int n2;
            while ((n2 = SpecialSymbol.index(string)) > -1) {
                Object object;
                if (n2 > 0) {
                    object = string.substring(0, n2);
                    phrase.add(new Chunk((String)object, font));
                    string = string.substring(n2);
                }
                object = new Font(3, font.getSize(), font.getStyle(), font.getColor());
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
                string = string.substring(1);
                while (SpecialSymbol.index(string) == 0) {
                    stringBuffer.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
                    string = string.substring(1);
                }
                phrase.add(new Chunk(stringBuffer.toString(), (Font)object));
            }
        }
        if (string != null && string.length() != 0) {
            phrase.add(new Chunk(string, font));
        }
        return phrase;
    }
}

