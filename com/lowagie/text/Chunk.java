/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.SplitCharacter;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chunk
implements Element {
    public static final String OBJECT_REPLACEMENT_CHARACTER = "\ufffc";
    public static final Chunk NEWLINE = new Chunk("\n");
    public static final Chunk NEXTPAGE = new Chunk("");
    protected StringBuffer content = null;
    protected Font font = null;
    protected HashMap attributes = null;
    public static final String SEPARATOR = "SEPARATOR";
    public static final String TAB = "TAB";
    public static final String HSCALE = "HSCALE";
    public static final String UNDERLINE = "UNDERLINE";
    public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT";
    public static final String SKEW = "SKEW";
    public static final String BACKGROUND = "BACKGROUND";
    public static final String TEXTRENDERMODE = "TEXTRENDERMODE";
    public static final String SPLITCHARACTER = "SPLITCHARACTER";
    public static final String HYPHENATION = "HYPHENATION";
    public static final String REMOTEGOTO = "REMOTEGOTO";
    public static final String LOCALGOTO = "LOCALGOTO";
    public static final String LOCALDESTINATION = "LOCALDESTINATION";
    public static final String GENERICTAG = "GENERICTAG";
    public static final String IMAGE = "IMAGE";
    public static final String ACTION = "ACTION";
    public static final String NEWPAGE = "NEWPAGE";
    public static final String PDFANNOTATION = "PDFANNOTATION";
    public static final String COLOR = "COLOR";
    public static final String ENCODING = "ENCODING";

    public Chunk() {
        this.content = new StringBuffer();
        this.font = new Font();
    }

    public Chunk(Chunk chunk) {
        if (chunk.content != null) {
            this.content = new StringBuffer(chunk.content.toString());
        }
        if (chunk.font != null) {
            this.font = new Font(chunk.font);
        }
        if (chunk.attributes != null) {
            this.attributes = new HashMap(chunk.attributes);
        }
    }

    public Chunk(String string, Font font) {
        this.content = new StringBuffer(string);
        this.font = font;
    }

    public Chunk(String string) {
        this(string, new Font());
    }

    public Chunk(char c, Font font) {
        this.content = new StringBuffer();
        this.content.append(c);
        this.font = font;
    }

    public Chunk(char c) {
        this(c, new Font());
    }

    public Chunk(Image image, float f, float f2) {
        this("\ufffc", new Font());
        Image image2 = Image.getInstance(image);
        image2.setAbsolutePosition(Float.NaN, Float.NaN);
        this.setAttribute("IMAGE", new Object[]{image2, new Float(f), new Float(f2), Boolean.FALSE});
    }

    public Chunk(DrawInterface drawInterface) {
        this(drawInterface, false);
    }

    public Chunk(DrawInterface drawInterface, boolean bl) {
        this("\ufffc", new Font());
        this.setAttribute("SEPARATOR", new Object[]{drawInterface, bl});
    }

    public Chunk(DrawInterface drawInterface, float f) {
        this(drawInterface, f, false);
    }

    public Chunk(DrawInterface drawInterface, float f, boolean bl) {
        this("\ufffc", new Font());
        if (f < 0.0f) {
            throw new IllegalArgumentException("A tab position may not be lower than 0; yours is " + f);
        }
        this.setAttribute("TAB", new Object[]{drawInterface, new Float(f), bl, new Float(0.0f)});
    }

    public Chunk(Image image, float f, float f2, boolean bl) {
        this("\ufffc", new Font());
        this.setAttribute("IMAGE", new Object[]{image, new Float(f), new Float(f2), bl});
    }

    public boolean process(ElementListener elementListener) {
        try {
            return elementListener.add(this);
        }
        catch (DocumentException var2_2) {
            return false;
        }
    }

    public int type() {
        return 10;
    }

    public ArrayList getChunks() {
        ArrayList<Chunk> arrayList = new ArrayList<Chunk>();
        arrayList.add(this);
        return arrayList;
    }

    public StringBuffer append(String string) {
        return this.content.append(string);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return this.font;
    }

    public String getContent() {
        return this.content.toString();
    }

    public String toString() {
        return this.getContent();
    }

    public boolean isEmpty() {
        return this.content.toString().trim().length() == 0 && this.content.toString().indexOf("\n") == -1 && this.attributes == null;
    }

    public float getWidthPoint() {
        if (this.getImage() != null) {
            return this.getImage().getScaledWidth();
        }
        return this.font.getCalculatedBaseFont(true).getWidthPoint(this.getContent(), this.font.getCalculatedSize()) * this.getHorizontalScaling();
    }

    public boolean hasAttributes() {
        return this.attributes != null;
    }

    public HashMap getAttributes() {
        return this.attributes;
    }

    public void setAttributes(HashMap hashMap) {
        this.attributes = hashMap;
    }

    private Chunk setAttribute(String string, Object object) {
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        this.attributes.put(string, object);
        return this;
    }

    public Chunk setHorizontalScaling(float f) {
        return this.setAttribute("HSCALE", new Float(f));
    }

    public float getHorizontalScaling() {
        if (this.attributes == null) {
            return 1.0f;
        }
        Float f = (Float)this.attributes.get("HSCALE");
        if (f == null) {
            return 1.0f;
        }
        return f.floatValue();
    }

    public Chunk setUnderline(float f, float f2) {
        return this.setUnderline(null, f, 0.0f, f2, 0.0f, 0);
    }

    public Chunk setUnderline(Color color, float f, float f2, float f3, float f4, int n) {
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        Object[] arrobject = new Object[]{color, {f, f2, f3, f4, n}};
        Object[][] arrobject2 = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), arrobject);
        return this.setAttribute("UNDERLINE", arrobject2);
    }

    public Chunk setTextRise(float f) {
        return this.setAttribute("SUBSUPSCRIPT", new Float(f));
    }

    public float getTextRise() {
        if (this.attributes != null && this.attributes.containsKey("SUBSUPSCRIPT")) {
            Float f = (Float)this.attributes.get("SUBSUPSCRIPT");
            return f.floatValue();
        }
        return 0.0f;
    }

    public Chunk setSkew(float f, float f2) {
        f = (float)Math.tan((double)f * 3.141592653589793 / 180.0);
        f2 = (float)Math.tan((double)f2 * 3.141592653589793 / 180.0);
        return this.setAttribute("SKEW", new float[]{f, f2});
    }

    public Chunk setBackground(Color color) {
        return this.setBackground(color, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public Chunk setBackground(Color color, float f, float f2, float f3, float f4) {
        return this.setAttribute("BACKGROUND", new Object[]{color, {f, f2, f3, f4}});
    }

    public Chunk setTextRenderMode(int n, float f, Color color) {
        return this.setAttribute("TEXTRENDERMODE", new Object[]{new Integer(n), new Float(f), color});
    }

    public Chunk setSplitCharacter(SplitCharacter splitCharacter) {
        return this.setAttribute("SPLITCHARACTER", splitCharacter);
    }

    public Chunk setHyphenation(HyphenationEvent hyphenationEvent) {
        return this.setAttribute("HYPHENATION", hyphenationEvent);
    }

    public Chunk setRemoteGoto(String string, String string2) {
        return this.setAttribute("REMOTEGOTO", new Object[]{string, string2});
    }

    public Chunk setRemoteGoto(String string, int n) {
        return this.setAttribute("REMOTEGOTO", new Object[]{string, new Integer(n)});
    }

    public Chunk setLocalGoto(String string) {
        return this.setAttribute("LOCALGOTO", string);
    }

    public Chunk setLocalDestination(String string) {
        return this.setAttribute("LOCALDESTINATION", string);
    }

    public Chunk setGenericTag(String string) {
        return this.setAttribute("GENERICTAG", string);
    }

    public Image getImage() {
        if (this.attributes == null) {
            return null;
        }
        Object[] arrobject = (Object[])this.attributes.get("IMAGE");
        if (arrobject == null) {
            return null;
        }
        return (Image)arrobject[0];
    }

    public Chunk setAction(PdfAction pdfAction) {
        return this.setAttribute("ACTION", pdfAction);
    }

    public Chunk setAnchor(URL uRL) {
        return this.setAttribute("ACTION", new PdfAction(uRL.toExternalForm()));
    }

    public Chunk setAnchor(String string) {
        return this.setAttribute("ACTION", new PdfAction(string));
    }

    public Chunk setNewPage() {
        return this.setAttribute("NEWPAGE", null);
    }

    public Chunk setAnnotation(PdfAnnotation pdfAnnotation) {
        return this.setAttribute("PDFANNOTATION", pdfAnnotation);
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return true;
    }

    public HyphenationEvent getHyphenation() {
        if (this.attributes == null) {
            return null;
        }
        return (HyphenationEvent)this.attributes.get("HYPHENATION");
    }

    static {
        NEXTPAGE.setNewPage();
    }
}

