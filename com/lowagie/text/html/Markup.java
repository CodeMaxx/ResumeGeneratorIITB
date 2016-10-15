/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html;

import com.lowagie.text.html.WebColors;
import java.awt.Color;
import java.util.Properties;
import java.util.StringTokenizer;

public class Markup {
    public static final String ITEXT_TAG = "tag";
    public static final String HTML_TAG_BODY = "body";
    public static final String HTML_TAG_DIV = "div";
    public static final String HTML_TAG_LINK = "link";
    public static final String HTML_TAG_SPAN = "span";
    public static final String HTML_ATTR_HEIGHT = "height";
    public static final String HTML_ATTR_HREF = "href";
    public static final String HTML_ATTR_REL = "rel";
    public static final String HTML_ATTR_STYLE = "style";
    public static final String HTML_ATTR_TYPE = "type";
    public static final String HTML_ATTR_STYLESHEET = "stylesheet";
    public static final String HTML_ATTR_WIDTH = "width";
    public static final String HTML_ATTR_CSS_CLASS = "class";
    public static final String HTML_ATTR_CSS_ID = "id";
    public static final String HTML_VALUE_JAVASCRIPT = "text/javascript";
    public static final String HTML_VALUE_CSS = "text/css";
    public static final String CSS_KEY_BGCOLOR = "background-color";
    public static final String CSS_KEY_COLOR = "color";
    public static final String CSS_KEY_DISPLAY = "display";
    public static final String CSS_KEY_FONTFAMILY = "font-family";
    public static final String CSS_KEY_FONTSIZE = "font-size";
    public static final String CSS_KEY_FONTSTYLE = "font-style";
    public static final String CSS_KEY_FONTWEIGHT = "font-weight";
    public static final String CSS_KEY_LINEHEIGHT = "line-height";
    public static final String CSS_KEY_MARGIN = "margin";
    public static final String CSS_KEY_MARGINLEFT = "margin-left";
    public static final String CSS_KEY_MARGINRIGHT = "margin-right";
    public static final String CSS_KEY_MARGINTOP = "margin-top";
    public static final String CSS_KEY_MARGINBOTTOM = "margin-bottom";
    public static final String CSS_KEY_PADDING = "padding";
    public static final String CSS_KEY_PADDINGLEFT = "padding-left";
    public static final String CSS_KEY_PADDINGRIGHT = "padding-right";
    public static final String CSS_KEY_PADDINGTOP = "padding-top";
    public static final String CSS_KEY_PADDINGBOTTOM = "padding-bottom";
    public static final String CSS_KEY_BORDERCOLOR = "border-color";
    public static final String CSS_KEY_BORDERWIDTH = "border-width";
    public static final String CSS_KEY_BORDERWIDTHLEFT = "border-left-width";
    public static final String CSS_KEY_BORDERWIDTHRIGHT = "border-right-width";
    public static final String CSS_KEY_BORDERWIDTHTOP = "border-top-width";
    public static final String CSS_KEY_BORDERWIDTHBOTTOM = "border-bottom-width";
    public static final String CSS_KEY_PAGE_BREAK_AFTER = "page-break-after";
    public static final String CSS_KEY_PAGE_BREAK_BEFORE = "page-break-before";
    public static final String CSS_KEY_TEXTALIGN = "text-align";
    public static final String CSS_KEY_TEXTDECORATION = "text-decoration";
    public static final String CSS_KEY_VERTICALALIGN = "vertical-align";
    public static final String CSS_KEY_VISIBILITY = "visibility";
    public static final String CSS_VALUE_ALWAYS = "always";
    public static final String CSS_VALUE_BLOCK = "block";
    public static final String CSS_VALUE_BOLD = "bold";
    public static final String CSS_VALUE_HIDDEN = "hidden";
    public static final String CSS_VALUE_INLINE = "inline";
    public static final String CSS_VALUE_ITALIC = "italic";
    public static final String CSS_VALUE_LINETHROUGH = "line-through";
    public static final String CSS_VALUE_LISTITEM = "list-item";
    public static final String CSS_VALUE_NONE = "none";
    public static final String CSS_VALUE_NORMAL = "normal";
    public static final String CSS_VALUE_OBLIQUE = "oblique";
    public static final String CSS_VALUE_TABLE = "table";
    public static final String CSS_VALUE_TABLEROW = "table-row";
    public static final String CSS_VALUE_TABLECELL = "table-cell";
    public static final String CSS_VALUE_TEXTALIGNLEFT = "left";
    public static final String CSS_VALUE_TEXTALIGNRIGHT = "right";
    public static final String CSS_VALUE_TEXTALIGNCENTER = "center";
    public static final String CSS_VALUE_TEXTALIGNJUSTIFY = "justify";
    public static final String CSS_VALUE_UNDERLINE = "underline";
    public static final float DEFAULT_FONT_SIZE = 12.0f;

    public static float parseLength(String string) {
        int n = 0;
        int n2 = string.length();
        boolean bl = true;
        block3 : while (bl && n < n2) {
            switch (string.charAt(n)) {
                case '+': 
                case '-': 
                case '.': 
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': {
                    ++n;
                    continue block3;
                }
            }
            bl = false;
        }
        if (n == 0) {
            return 0.0f;
        }
        if (n == n2) {
            return Float.parseFloat(string + "f");
        }
        float f = Float.parseFloat(string.substring(0, n) + "f");
        if ((string = string.substring(n)).startsWith("in")) {
            return f * 72.0f;
        }
        if (string.startsWith("cm")) {
            return f / 2.54f * 72.0f;
        }
        if (string.startsWith("mm")) {
            return f / 25.4f * 72.0f;
        }
        if (string.startsWith("pc")) {
            return f * 12.0f;
        }
        return f;
    }

    public static float parseLength(String string, float f) {
        if (string == null) {
            return 0.0f;
        }
        int n = 0;
        int n2 = string.length();
        boolean bl = true;
        block3 : while (bl && n < n2) {
            switch (string.charAt(n)) {
                case '+': 
                case '-': 
                case '.': 
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': {
                    ++n;
                    continue block3;
                }
            }
            bl = false;
        }
        if (n == 0) {
            return 0.0f;
        }
        if (n == n2) {
            return Float.parseFloat(string + "f");
        }
        float f2 = Float.parseFloat(string.substring(0, n) + "f");
        if ((string = string.substring(n)).startsWith("in")) {
            return f2 * 72.0f;
        }
        if (string.startsWith("cm")) {
            return f2 / 2.54f * 72.0f;
        }
        if (string.startsWith("mm")) {
            return f2 / 25.4f * 72.0f;
        }
        if (string.startsWith("pc")) {
            return f2 * 12.0f;
        }
        if (string.startsWith("em")) {
            return f2 * f;
        }
        if (string.startsWith("ex")) {
            return f2 * f / 2.0f;
        }
        return f2;
    }

    public static Color decodeColor(String string) {
        if (string == null) {
            return null;
        }
        Color color = WebColors.getRGBColor(string = string.toLowerCase().trim());
        if (color != null) {
            return color;
        }
        try {
            if (string.startsWith("#")) {
                if (string.length() == 4) {
                    string = "#" + string.substring(1, 2) + string.substring(1, 2) + string.substring(2, 3) + string.substring(2, 3) + string.substring(3, 4) + string.substring(3, 4);
                }
                if (string.length() == 7) {
                    return new Color(Integer.parseInt(string.substring(1), 16));
                }
            } else if (string.startsWith("rgb")) {
                StringTokenizer stringTokenizer = new StringTokenizer(string.substring(3), " \t\r\n\f(),");
                int[] arrn = new int[3];
                for (int i = 0; i < 3; ++i) {
                    float f;
                    if (!stringTokenizer.hasMoreTokens()) {
                        return null;
                    }
                    String string2 = stringTokenizer.nextToken();
                    if (string2.endsWith("%")) {
                        f = Float.parseFloat(string2.substring(0, string2.length() - 1));
                        f = f * 255.0f / 100.0f;
                    } else {
                        f = Float.parseFloat(string2);
                    }
                    int n = (int)f;
                    if (n > 255) {
                        n = 255;
                    } else if (n < 0) {
                        n = 0;
                    }
                    arrn[i] = n;
                }
                return new Color(arrn[0], arrn[1], arrn[2]);
            }
        }
        catch (Exception var2_3) {
            // empty catch block
        }
        return null;
    }

    public static Properties parseAttributes(String string) {
        Properties properties = new Properties();
        if (string == null) {
            return properties;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, ";");
        while (stringTokenizer.hasMoreTokens()) {
            StringTokenizer stringTokenizer2 = new StringTokenizer(stringTokenizer.nextToken(), ":");
            if (!stringTokenizer2.hasMoreTokens()) continue;
            String string2 = stringTokenizer2.nextToken().trim();
            if (!stringTokenizer2.hasMoreTokens()) continue;
            String string3 = stringTokenizer2.nextToken().trim();
            if (string3.startsWith("\"")) {
                string3 = string3.substring(1);
            }
            if (string3.endsWith("\"")) {
                string3 = string3.substring(0, string3.length() - 1);
            }
            properties.setProperty(string2.toLowerCase(), string3);
        }
        return properties;
    }

    public static String removeComment(String string, String string2, String string3) {
        StringBuffer stringBuffer = new StringBuffer();
        int n = 0;
        int n2 = string3.length();
        int n3 = string.indexOf(string2, n);
        while (n3 > -1) {
            stringBuffer.append(string.substring(n, n3));
            n = string.indexOf(string3, n3) + n2;
            n3 = string.indexOf(string2, n);
        }
        stringBuffer.append(string.substring(n));
        return stringBuffer.toString();
    }
}

