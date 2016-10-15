/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.SplitCharacter;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultSplitCharacter;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfFont;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PdfChunk {
    private static final char[] singleSpace = new char[]{' '};
    private static final PdfChunk[] thisChunk = new PdfChunk[1];
    private static final float ITALIC_ANGLE = 0.21256f;
    private static final HashMap keysAttributes = new HashMap();
    private static final HashMap keysNoStroke = new HashMap();
    protected String value = "";
    protected String encoding = "Cp1252";
    protected PdfFont font;
    protected BaseFont baseFont;
    protected SplitCharacter splitCharacter;
    protected HashMap attributes = new HashMap();
    protected HashMap noStroke = new HashMap();
    protected boolean newlineSplit;
    protected Image image;
    protected float offsetX;
    protected float offsetY;
    protected boolean changeLeading = false;

    PdfChunk(String string, PdfChunk pdfChunk) {
        PdfChunk.thisChunk[0] = this;
        this.value = string;
        this.font = pdfChunk.font;
        this.attributes = pdfChunk.attributes;
        this.noStroke = pdfChunk.noStroke;
        this.baseFont = pdfChunk.baseFont;
        Object[] arrobject = (Object[])this.attributes.get("IMAGE");
        if (arrobject == null) {
            this.image = null;
        } else {
            this.image = (Image)arrobject[0];
            this.offsetX = ((Float)arrobject[1]).floatValue();
            this.offsetY = ((Float)arrobject[2]).floatValue();
            this.changeLeading = (Boolean)arrobject[3];
        }
        this.encoding = this.font.getFont().getEncoding();
        this.splitCharacter = (SplitCharacter)this.noStroke.get("SPLITCHARACTER");
        if (this.splitCharacter == null) {
            this.splitCharacter = DefaultSplitCharacter.DEFAULT;
        }
    }

    PdfChunk(Chunk chunk, PdfAction pdfAction) {
        Object[][] arrobject;
        Object[] arrobject2;
        PdfChunk.thisChunk[0] = this;
        this.value = chunk.getContent();
        Font font = chunk.getFont();
        float f = font.getSize();
        if (f == -1.0f) {
            f = 12.0f;
        }
        this.baseFont = font.getBaseFont();
        int n = font.getStyle();
        if (n == -1) {
            n = 0;
        }
        if (this.baseFont == null) {
            this.baseFont = font.getCalculatedBaseFont(false);
        } else {
            if ((n & 1) != 0) {
                this.attributes.put("TEXTRENDERMODE", new Object[]{new Integer(2), new Float(f / 30.0f), null});
            }
            if ((n & 2) != 0) {
                this.attributes.put("SKEW", new float[]{0.0f, 0.21256f});
            }
        }
        this.font = new PdfFont(this.baseFont, f);
        HashMap hashMap = chunk.getAttributes();
        if (hashMap != null) {
            arrobject2 = hashMap.entrySet().iterator();
            while (arrobject2.hasNext()) {
                arrobject = arrobject2.next();
                Object k = arrobject.getKey();
                if (keysAttributes.containsKey(k)) {
                    this.attributes.put(k, arrobject.getValue());
                    continue;
                }
                if (!keysNoStroke.containsKey(k)) continue;
                this.noStroke.put(k, arrobject.getValue());
            }
            if ("".equals(hashMap.get("GENERICTAG"))) {
                this.attributes.put("GENERICTAG", chunk.getContent());
            }
        }
        if (font.isUnderlined()) {
            arrobject2 = new Object[]{null, {0.0f, 0.06666667f, 0.0f, -0.33333334f, 0.0f}};
            arrobject = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), arrobject2);
            this.attributes.put("UNDERLINE", arrobject);
        }
        if (font.isStrikethru()) {
            arrobject2 = new Object[]{null, {0.0f, 0.06666667f, 0.0f, 0.33333334f, 0.0f}};
            arrobject = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), arrobject2);
            this.attributes.put("UNDERLINE", arrobject);
        }
        if (pdfAction != null) {
            this.attributes.put("ACTION", pdfAction);
        }
        this.noStroke.put("COLOR", font.getColor());
        this.noStroke.put("ENCODING", this.font.getFont().getEncoding());
        arrobject2 = (Object[])this.attributes.get("IMAGE");
        if (arrobject2 == null) {
            this.image = null;
        } else {
            this.attributes.remove("HSCALE");
            this.image = (Image)arrobject2[0];
            this.offsetX = ((Float)arrobject2[1]).floatValue();
            this.offsetY = ((Float)arrobject2[2]).floatValue();
            this.changeLeading = (Boolean)arrobject2[3];
        }
        this.font.setImage(this.image);
        arrobject = (Float)this.attributes.get("HSCALE");
        if (arrobject != null) {
            this.font.setHorizontalScaling(arrobject.floatValue());
        }
        this.encoding = this.font.getFont().getEncoding();
        this.splitCharacter = (SplitCharacter)this.noStroke.get("SPLITCHARACTER");
        if (this.splitCharacter == null) {
            this.splitCharacter = DefaultSplitCharacter.DEFAULT;
        }
    }

    public int getUnicodeEquivalent(int n) {
        return this.baseFont.getUnicodeEquivalent(n);
    }

    protected int getWord(String string, int n) {
        int n2 = string.length();
        while (n < n2 && Character.isLetter(string.charAt(n))) {
            ++n;
        }
        return n;
    }

    PdfChunk split(float f) {
        Object object;
        int n;
        HyphenationEvent hyphenationEvent;
        float f2;
        int n2;
        int n3;
        int n4;
        int n5;
        this.newlineSplit = false;
        if (this.image != null) {
            if (this.image.getScaledWidth() > f) {
                PdfChunk pdfChunk = new PdfChunk("\ufffc", this);
                this.value = "";
                this.attributes = new HashMap();
                this.image = null;
                this.font = PdfFont.getDefaultFont();
                return pdfChunk;
            }
            return null;
        }
        hyphenationEvent = (HyphenationEvent)this.noStroke.get("HYPHENATION");
        n2 = -1;
        float f3 = 0.0f;
        n4 = -1;
        f2 = 0.0f;
        n5 = this.value.length();
        char[] arrc = this.value.toCharArray();
        char c = '\u0000';
        BaseFont baseFont = this.font.getFont();
        boolean bl = false;
        if (baseFont.getFontType() == 2 && baseFont.getUnicodeEquivalent(32) != 32) {
            for (n3 = 0; n3 < n5; ++n3) {
                n = arrc[n3];
                c = (char)baseFont.getUnicodeEquivalent(n);
                if (c == '\n') {
                    this.newlineSplit = true;
                    String string = this.value.substring(n3 + 1);
                    this.value = this.value.substring(0, n3);
                    if (this.value.length() < 1) {
                        this.value = "\u0001";
                    }
                    PdfChunk pdfChunk = new PdfChunk(string, this);
                    return pdfChunk;
                }
                f3 += this.font.width(n);
                if (c == ' ') {
                    n4 = n3 + 1;
                    f2 = f3;
                }
                if (f3 <= f) {
                    if (!this.splitCharacter.isSplitCharacter(0, n3, n5, arrc, thisChunk)) continue;
                    n2 = n3 + 1;
                    continue;
                }
                break;
            }
        } else {
            while (n3 < n5) {
                c = arrc[n3];
                if (c == '\r' || c == '\n') {
                    this.newlineSplit = true;
                    int n6 = 1;
                    if (c == '\r' && n3 + 1 < n5 && arrc[n3 + 1] == '\n') {
                        n6 = 2;
                    }
                    String string = this.value.substring(n3 + n6);
                    this.value = this.value.substring(0, n3);
                    if (this.value.length() < 1) {
                        this.value = " ";
                    }
                    PdfChunk pdfChunk = new PdfChunk(string, this);
                    return pdfChunk;
                }
                bl = Utilities.isSurrogatePair(arrc, n3);
                f3 = bl ? (f3 += this.font.width(Utilities.convertToUtf32(arrc[n3], arrc[n3 + 1]))) : (f3 += this.font.width(c));
                if (c == ' ') {
                    n4 = n3 + 1;
                    f2 = f3;
                }
                if (bl) {
                    ++n3;
                }
                if (f3 <= f) {
                    if (this.splitCharacter.isSplitCharacter(0, n3, n5, arrc, null)) {
                        n2 = n3 + 1;
                    }
                    ++n3;
                    continue;
                }
                break;
            }
        }
        if (n3 == n5) {
            return null;
        }
        if (n2 < 0) {
            String string = this.value;
            this.value = "";
            PdfChunk pdfChunk = new PdfChunk(string, this);
            return pdfChunk;
        }
        if (n4 > n2 && this.splitCharacter.isSplitCharacter(0, 0, 1, singleSpace, null)) {
            n2 = n4;
        }
        if (hyphenationEvent != null && n4 >= 0 && n4 < n3 && (n = this.getWord(this.value, n4)) > n4) {
            object = hyphenationEvent.getHyphenatedWordPre(this.value.substring(n4, n), this.font.getFont(), this.font.size(), f - f2);
            String string = hyphenationEvent.getHyphenatedWordPost();
            if (object.length() > 0) {
                String string2 = string + this.value.substring(n);
                this.value = this.trim(this.value.substring(0, n4) + (String)object);
                PdfChunk pdfChunk = new PdfChunk(string2, this);
                return pdfChunk;
            }
        }
        String string = this.value.substring(n2);
        this.value = this.trim(this.value.substring(0, n2));
        object = new PdfChunk(string, this);
        return object;
    }

    PdfChunk truncate(float f) {
        int n;
        if (this.image != null) {
            if (this.image.getScaledWidth() > f) {
                PdfChunk pdfChunk = new PdfChunk("", this);
                this.value = "";
                this.attributes.remove("IMAGE");
                this.image = null;
                this.font = PdfFont.getDefaultFont();
                return pdfChunk;
            }
            return null;
        }
        float f2 = 0.0f;
        if (f < this.font.width()) {
            String string = this.value.substring(1);
            this.value = this.value.substring(0, 1);
            PdfChunk pdfChunk = new PdfChunk(string, this);
            return pdfChunk;
        }
        int n2 = this.value.length();
        boolean bl = false;
        for (n = 0; n < n2; ++n) {
            bl = Utilities.isSurrogatePair(this.value, n);
            f2 = bl ? (f2 += this.font.width(Utilities.convertToUtf32(this.value, n))) : (f2 += this.font.width(this.value.charAt(n)));
            if (f2 > f) break;
            if (!bl) continue;
            ++n;
        }
        if (n == n2) {
            return null;
        }
        if (n == 0) {
            n = 1;
            if (bl) {
                ++n;
            }
        }
        String string = this.value.substring(n);
        this.value = this.value.substring(0, n);
        PdfChunk pdfChunk = new PdfChunk(string, this);
        return pdfChunk;
    }

    PdfFont font() {
        return this.font;
    }

    Color color() {
        return (Color)this.noStroke.get("COLOR");
    }

    float width() {
        return this.font.width(this.value);
    }

    public boolean isNewlineSplit() {
        return this.newlineSplit;
    }

    public float getWidthCorrected(float f, float f2) {
        if (this.image != null) {
            return this.image.getScaledWidth() + f;
        }
        int n = 0;
        int n2 = -1;
        while ((n2 = this.value.indexOf(32, n2 + 1)) >= 0) {
            ++n;
        }
        return this.width() + ((float)this.value.length() * f + (float)n * f2);
    }

    public float getTextRise() {
        Float f = (Float)this.getAttribute("SUBSUPSCRIPT");
        if (f != null) {
            return f.floatValue();
        }
        return 0.0f;
    }

    public float trimLastSpace() {
        BaseFont baseFont = this.font.getFont();
        if (baseFont.getFontType() == 2 && baseFont.getUnicodeEquivalent(32) != 32) {
            if (this.value.length() > 1 && this.value.endsWith("\u0001")) {
                this.value = this.value.substring(0, this.value.length() - 1);
                return this.font.width(1);
            }
        } else if (this.value.length() > 1 && this.value.endsWith(" ")) {
            this.value = this.value.substring(0, this.value.length() - 1);
            return this.font.width(32);
        }
        return 0.0f;
    }

    public float trimFirstSpace() {
        BaseFont baseFont = this.font.getFont();
        if (baseFont.getFontType() == 2 && baseFont.getUnicodeEquivalent(32) != 32) {
            if (this.value.length() > 1 && this.value.startsWith("\u0001")) {
                this.value = this.value.substring(1);
                return this.font.width(1);
            }
        } else if (this.value.length() > 1 && this.value.startsWith(" ")) {
            this.value = this.value.substring(1);
            return this.font.width(32);
        }
        return 0.0f;
    }

    Object getAttribute(String string) {
        if (this.attributes.containsKey(string)) {
            return this.attributes.get(string);
        }
        return this.noStroke.get(string);
    }

    boolean isAttribute(String string) {
        if (this.attributes.containsKey(string)) {
            return true;
        }
        return this.noStroke.containsKey(string);
    }

    boolean isStroked() {
        return !this.attributes.isEmpty();
    }

    boolean isSeparator() {
        return this.isAttribute("SEPARATOR");
    }

    boolean isHorizontalSeparator() {
        if (this.isAttribute("SEPARATOR")) {
            Object[] arrobject = (Object[])this.getAttribute("SEPARATOR");
            return (Boolean)arrobject[1] == false;
        }
        return false;
    }

    boolean isTab() {
        return this.isAttribute("TAB");
    }

    void adjustLeft(float f) {
        Object[] arrobject = (Object[])this.attributes.get("TAB");
        if (arrobject != null) {
            this.attributes.put("TAB", new Object[]{arrobject[0], arrobject[1], arrobject[2], new Float(f)});
        }
    }

    boolean isImage() {
        return this.image != null;
    }

    Image getImage() {
        return this.image;
    }

    void setImageOffsetX(float f) {
        this.offsetX = f;
    }

    float getImageOffsetX() {
        return this.offsetX;
    }

    void setImageOffsetY(float f) {
        this.offsetY = f;
    }

    float getImageOffsetY() {
        return this.offsetY;
    }

    void setValue(String string) {
        this.value = string;
    }

    public String toString() {
        return this.value;
    }

    boolean isSpecialEncoding() {
        return this.encoding.equals("UnicodeBigUnmarked") || this.encoding.equals("Identity-H");
    }

    String getEncoding() {
        return this.encoding;
    }

    int length() {
        return this.value.length();
    }

    int lengthUtf32() {
        if (!"Identity-H".equals(this.encoding)) {
            return this.value.length();
        }
        int n = 0;
        int n2 = this.value.length();
        for (int i = 0; i < n2; ++i) {
            if (Utilities.isSurrogateHigh(this.value.charAt(i))) {
                ++i;
            }
            ++n;
        }
        return n;
    }

    boolean isExtSplitCharacter(int n, int n2, int n3, char[] arrc, PdfChunk[] arrpdfChunk) {
        return this.splitCharacter.isSplitCharacter(n, n2, n3, arrc, arrpdfChunk);
    }

    String trim(String string) {
        BaseFont baseFont = this.font.getFont();
        if (baseFont.getFontType() == 2 && baseFont.getUnicodeEquivalent(32) != 32) {
            while (string.endsWith("\u0001")) {
                string = string.substring(0, string.length() - 1);
            }
        } else {
            while (string.endsWith(" ") || string.endsWith("\t")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public boolean changeLeading() {
        return this.changeLeading;
    }

    float getCharWidth(int n) {
        if (PdfChunk.noPrint(n)) {
            return 0.0f;
        }
        return this.font.width(n);
    }

    public static boolean noPrint(int n) {
        return n >= 8203 && n <= 8207 || n >= 8234 && n <= 8238;
    }

    static {
        keysAttributes.put("ACTION", null);
        keysAttributes.put("UNDERLINE", null);
        keysAttributes.put("REMOTEGOTO", null);
        keysAttributes.put("LOCALGOTO", null);
        keysAttributes.put("LOCALDESTINATION", null);
        keysAttributes.put("GENERICTAG", null);
        keysAttributes.put("NEWPAGE", null);
        keysAttributes.put("IMAGE", null);
        keysAttributes.put("BACKGROUND", null);
        keysAttributes.put("PDFANNOTATION", null);
        keysAttributes.put("SKEW", null);
        keysAttributes.put("HSCALE", null);
        keysAttributes.put("SEPARATOR", null);
        keysAttributes.put("TAB", null);
        keysNoStroke.put("SUBSUPSCRIPT", null);
        keysNoStroke.put("SPLITCHARACTER", null);
        keysNoStroke.put("HYPHENATION", null);
        keysNoStroke.put("TEXTRENDERMODE", null);
    }
}

