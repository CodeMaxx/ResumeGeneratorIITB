/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;

public class Font
implements Comparable {
    public static final int COURIER = 0;
    public static final int HELVETICA = 1;
    public static final int TIMES_ROMAN = 2;
    public static final int SYMBOL = 3;
    public static final int ZAPFDINGBATS = 4;
    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int UNDERLINE = 4;
    public static final int STRIKETHRU = 8;
    public static final int BOLDITALIC = 3;
    public static final int UNDEFINED = -1;
    public static final int DEFAULTSIZE = 12;
    private int family = -1;
    private float size = -1.0f;
    private int style = -1;
    private Color color = null;
    private BaseFont baseFont = null;

    public Font(Font font) {
        this.family = font.family;
        this.size = font.size;
        this.style = font.style;
        this.color = font.color;
        this.baseFont = font.baseFont;
    }

    public Font(int n, float f, int n2, Color color) {
        this.family = n;
        this.size = f;
        this.style = n2;
        this.color = color;
    }

    public Font(BaseFont baseFont, float f, int n, Color color) {
        this.baseFont = baseFont;
        this.size = f;
        this.style = n;
        this.color = color;
    }

    public Font(BaseFont baseFont, float f, int n) {
        this(baseFont, f, n, null);
    }

    public Font(BaseFont baseFont, float f) {
        this(baseFont, f, -1, null);
    }

    public Font(BaseFont baseFont) {
        this(baseFont, -1.0f, -1, null);
    }

    public Font(int n, float f, int n2) {
        this(n, f, n2, null);
    }

    public Font(int n, float f) {
        this(n, f, -1, null);
    }

    public Font(int n) {
        this(n, -1.0f, -1, null);
    }

    public Font() {
        this(-1, -1.0f, -1, null);
    }

    public int compareTo(Object object) {
        if (object == null) {
            return -1;
        }
        try {
            Font font = (Font)object;
            if (this.baseFont != null && !this.baseFont.equals(font.getBaseFont())) {
                return -2;
            }
            if (this.family != font.getFamily()) {
                return 1;
            }
            if (this.size != font.getSize()) {
                return 2;
            }
            if (this.style != font.getStyle()) {
                return 3;
            }
            if (this.color == null) {
                if (font.color == null) {
                    return 0;
                }
                return 4;
            }
            if (font.color == null) {
                return 4;
            }
            if (this.color.equals(font.getColor())) {
                return 0;
            }
            return 4;
        }
        catch (ClassCastException var3_3) {
            return -3;
        }
    }

    public int getFamily() {
        return this.family;
    }

    public String getFamilyname() {
        String string = "unknown";
        switch (this.getFamily()) {
            case 0: {
                return "Courier";
            }
            case 1: {
                return "Helvetica";
            }
            case 2: {
                return "Times-Roman";
            }
            case 3: {
                return "Symbol";
            }
            case 4: {
                return "ZapfDingbats";
            }
        }
        if (this.baseFont != null) {
            String[][] arrstring = this.baseFont.getFamilyFontName();
            for (int i = 0; i < arrstring.length; ++i) {
                if ("0".equals(arrstring[i][2])) {
                    return arrstring[i][3];
                }
                if ("1033".equals(arrstring[i][2])) {
                    string = arrstring[i][3];
                }
                if (!"".equals(arrstring[i][2])) continue;
                string = arrstring[i][3];
            }
        }
        return string;
    }

    public void setFamily(String string) {
        this.family = Font.getFamilyIndex(string);
    }

    public static int getFamilyIndex(String string) {
        if (string.equalsIgnoreCase("Courier")) {
            return 0;
        }
        if (string.equalsIgnoreCase("Helvetica")) {
            return 1;
        }
        if (string.equalsIgnoreCase("Times-Roman")) {
            return 2;
        }
        if (string.equalsIgnoreCase("Symbol")) {
            return 3;
        }
        if (string.equalsIgnoreCase("ZapfDingbats")) {
            return 4;
        }
        return -1;
    }

    public float getSize() {
        return this.size;
    }

    public float getCalculatedSize() {
        float f = this.size;
        if (f == -1.0f) {
            f = 12.0f;
        }
        return f;
    }

    public float getCalculatedLeading(float f) {
        return f * this.getCalculatedSize();
    }

    public void setSize(float f) {
        this.size = f;
    }

    public int getStyle() {
        return this.style;
    }

    public int getCalculatedStyle() {
        int n = this.style;
        if (n == -1) {
            n = 0;
        }
        if (this.baseFont != null) {
            return n;
        }
        if (this.family == 3 || this.family == 4) {
            return n;
        }
        return n & -4;
    }

    public boolean isBold() {
        if (this.style == -1) {
            return false;
        }
        return (this.style & 1) == 1;
    }

    public boolean isItalic() {
        if (this.style == -1) {
            return false;
        }
        return (this.style & 2) == 2;
    }

    public boolean isUnderlined() {
        if (this.style == -1) {
            return false;
        }
        return (this.style & 4) == 4;
    }

    public boolean isStrikethru() {
        if (this.style == -1) {
            return false;
        }
        return (this.style & 8) == 8;
    }

    public void setStyle(int n) {
        if (this.style == -1) {
            this.style = 0;
        }
        this.style |= n;
    }

    public void setStyle(String string) {
        if (this.style == -1) {
            this.style = 0;
        }
        this.style |= Font.getStyleValue(string);
    }

    public static int getStyleValue(String string) {
        int n = 0;
        if (string.indexOf("normal") != -1) {
            n |= false;
        }
        if (string.indexOf("bold") != -1) {
            n |= true;
        }
        if (string.indexOf("italic") != -1) {
            n |= 2;
        }
        if (string.indexOf("oblique") != -1) {
            n |= 2;
        }
        if (string.indexOf("underline") != -1) {
            n |= 4;
        }
        if (string.indexOf("line-through") != -1) {
            n |= 8;
        }
        return n;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int n, int n2, int n3) {
        this.color = new Color(n, n2, n3);
    }

    public BaseFont getBaseFont() {
        return this.baseFont;
    }

    public BaseFont getCalculatedBaseFont(boolean bl) {
        if (this.baseFont != null) {
            return this.baseFont;
        }
        int n = this.style;
        if (n == -1) {
            n = 0;
        }
        String string = "Helvetica";
        String string2 = "Cp1252";
        BaseFont baseFont = null;
        block1 : switch (this.family) {
            case 0: {
                switch (n & 3) {
                    case 1: {
                        string = "Courier-Bold";
                        break block1;
                    }
                    case 2: {
                        string = "Courier-Oblique";
                        break block1;
                    }
                    case 3: {
                        string = "Courier-BoldOblique";
                        break block1;
                    }
                }
                string = "Courier";
                break;
            }
            case 2: {
                switch (n & 3) {
                    case 1: {
                        string = "Times-Bold";
                        break block1;
                    }
                    case 2: {
                        string = "Times-Italic";
                        break block1;
                    }
                    case 3: {
                        string = "Times-BoldItalic";
                        break block1;
                    }
                }
                string = "Times-Roman";
                break;
            }
            case 3: {
                string = "Symbol";
                if (!bl) break;
                string2 = "Symbol";
                break;
            }
            case 4: {
                string = "ZapfDingbats";
                if (!bl) break;
                string2 = "ZapfDingbats";
                break;
            }
            default: {
                switch (n & 3) {
                    case 1: {
                        string = "Helvetica-Bold";
                        break block1;
                    }
                    case 2: {
                        string = "Helvetica-Oblique";
                        break block1;
                    }
                    case 3: {
                        string = "Helvetica-BoldOblique";
                        break block1;
                    }
                }
                string = "Helvetica";
            }
        }
        try {
            baseFont = BaseFont.createFont(string, string2, false);
        }
        catch (Exception var6_6) {
            throw new ExceptionConverter(var6_6);
        }
        return baseFont;
    }

    public boolean isStandardFont() {
        return this.family == -1 && this.size == -1.0f && this.style == -1 && this.color == null && this.baseFont == null;
    }

    public Font difference(Font font) {
        Color color;
        if (font == null) {
            return this;
        }
        float f = font.size;
        if (f == -1.0f) {
            f = this.size;
        }
        int n = -1;
        int n2 = this.style;
        int n3 = font.getStyle();
        if (n2 != -1 || n3 != -1) {
            if (n2 == -1) {
                n2 = 0;
            }
            if (n3 == -1) {
                n3 = 0;
            }
            n = n2 | n3;
        }
        if ((color = font.color) == null) {
            color = this.color;
        }
        if (font.baseFont != null) {
            return new Font(font.baseFont, f, n, color);
        }
        if (font.getFamily() != -1) {
            return new Font(font.family, f, n, color);
        }
        if (this.baseFont != null) {
            if (n == n2) {
                return new Font(this.baseFont, f, n, color);
            }
            return FontFactory.getFont(this.getFamilyname(), f, n, color);
        }
        return new Font(this.family, f, n, color);
    }
}

