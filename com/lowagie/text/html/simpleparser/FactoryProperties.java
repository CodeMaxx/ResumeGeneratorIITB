/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.FontFactoryImp;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.Markup;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.pdf.HyphenationAuto;
import com.lowagie.text.pdf.HyphenationEvent;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public class FactoryProperties {
    private FontFactoryImp fontImp = FontFactory.getFontImp();
    public static HashMap followTags = new HashMap();

    public Chunk createChunk(String string, ChainedProperties chainedProperties) {
        Font font = this.getFont(chainedProperties);
        float f = font.getSize();
        f /= 2.0f;
        Chunk chunk = new Chunk(string, font);
        if (chainedProperties.hasProperty("sub")) {
            chunk.setTextRise(- f);
        } else if (chainedProperties.hasProperty("sup")) {
            chunk.setTextRise(f);
        }
        chunk.setHyphenation(FactoryProperties.getHyphenation(chainedProperties));
        return chunk;
    }

    private static void setParagraphLeading(Paragraph paragraph, String string) {
        if (string == null) {
            paragraph.setLeading(0.0f, 1.5f);
            return;
        }
        try {
            StringTokenizer stringTokenizer = new StringTokenizer(string, " ,");
            String string2 = stringTokenizer.nextToken();
            float f = Float.parseFloat(string2);
            if (!stringTokenizer.hasMoreTokens()) {
                paragraph.setLeading(f, 0.0f);
                return;
            }
            string2 = stringTokenizer.nextToken();
            float f2 = Float.parseFloat(string2);
            paragraph.setLeading(f, f2);
        }
        catch (Exception var2_3) {
            paragraph.setLeading(0.0f, 1.5f);
        }
    }

    public static Paragraph createParagraph(HashMap hashMap) {
        Paragraph paragraph = new Paragraph();
        String string = (String)hashMap.get("align");
        if (string != null) {
            if (string.equalsIgnoreCase("center")) {
                paragraph.setAlignment(1);
            } else if (string.equalsIgnoreCase("right")) {
                paragraph.setAlignment(2);
            } else if (string.equalsIgnoreCase("justify")) {
                paragraph.setAlignment(3);
            }
        }
        paragraph.setHyphenation(FactoryProperties.getHyphenation(hashMap));
        FactoryProperties.setParagraphLeading(paragraph, (String)hashMap.get("leading"));
        return paragraph;
    }

    public static void createParagraph(Paragraph paragraph, ChainedProperties chainedProperties) {
        String string = chainedProperties.getProperty("align");
        if (string != null) {
            if (string.equalsIgnoreCase("center")) {
                paragraph.setAlignment(1);
            } else if (string.equalsIgnoreCase("right")) {
                paragraph.setAlignment(2);
            } else if (string.equalsIgnoreCase("justify")) {
                paragraph.setAlignment(3);
            }
        }
        paragraph.setHyphenation(FactoryProperties.getHyphenation(chainedProperties));
        FactoryProperties.setParagraphLeading(paragraph, chainedProperties.getProperty("leading"));
        string = chainedProperties.getProperty("before");
        if (string != null) {
            try {
                paragraph.setSpacingBefore(Float.parseFloat(string));
            }
            catch (Exception var3_3) {
                // empty catch block
            }
        }
        if ((string = chainedProperties.getProperty("after")) != null) {
            try {
                paragraph.setSpacingAfter(Float.parseFloat(string));
            }
            catch (Exception var3_4) {
                // empty catch block
            }
        }
        if ((string = chainedProperties.getProperty("extraparaspace")) != null) {
            try {
                paragraph.setExtraParagraphSpace(Float.parseFloat(string));
            }
            catch (Exception var3_5) {
                // empty catch block
            }
        }
    }

    public static Paragraph createParagraph(ChainedProperties chainedProperties) {
        Paragraph paragraph = new Paragraph();
        FactoryProperties.createParagraph(paragraph, chainedProperties);
        return paragraph;
    }

    public static ListItem createListItem(ChainedProperties chainedProperties) {
        ListItem listItem = new ListItem();
        FactoryProperties.createParagraph(listItem, chainedProperties);
        return listItem;
    }

    public Font getFont(ChainedProperties chainedProperties) {
        String string = chainedProperties.getProperty("face");
        if (string != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
            while (stringTokenizer.hasMoreTokens()) {
                string = stringTokenizer.nextToken().trim();
                if (string.startsWith("\"")) {
                    string = string.substring(1);
                }
                if (string.endsWith("\"")) {
                    string = string.substring(0, string.length() - 1);
                }
                if (!this.fontImp.isRegistered(string)) continue;
            }
        }
        int n = 0;
        if (chainedProperties.hasProperty("i")) {
            n |= 2;
        }
        if (chainedProperties.hasProperty("b")) {
            n |= 1;
        }
        if (chainedProperties.hasProperty("u")) {
            n |= 4;
        }
        if (chainedProperties.hasProperty("s")) {
            n |= 8;
        }
        String string2 = chainedProperties.getProperty("size");
        float f = 12.0f;
        if (string2 != null) {
            f = Float.parseFloat(string2);
        }
        Color color = Markup.decodeColor(chainedProperties.getProperty("color"));
        String string3 = chainedProperties.getProperty("encoding");
        if (string3 == null) {
            string3 = "Cp1252";
        }
        return this.fontImp.getFont(string, string3, true, f, n, color);
    }

    public static HyphenationEvent getHyphenation(ChainedProperties chainedProperties) {
        return FactoryProperties.getHyphenation(chainedProperties.getProperty("hyphenation"));
    }

    public static HyphenationEvent getHyphenation(HashMap hashMap) {
        return FactoryProperties.getHyphenation((String)hashMap.get("hyphenation"));
    }

    public static HyphenationEvent getHyphenation(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        String string2 = string;
        String string3 = null;
        int n = 2;
        int n2 = 2;
        int n3 = string.indexOf(95);
        if (n3 == -1) {
            return new HyphenationAuto(string2, string3, n, n2);
        }
        string2 = string.substring(0, n3);
        string3 = string.substring(n3 + 1);
        n3 = string3.indexOf(44);
        if (n3 == -1) {
            return new HyphenationAuto(string2, string3, n, n2);
        }
        string = string3.substring(n3 + 1);
        string3 = string3.substring(0, n3);
        n3 = string.indexOf(44);
        if (n3 == -1) {
            n = Integer.parseInt(string);
        } else {
            n = Integer.parseInt(string.substring(0, n3));
            n2 = Integer.parseInt(string.substring(n3 + 1));
        }
        return new HyphenationAuto(string2, string3, n, n2);
    }

    public static void insertStyle(HashMap hashMap) {
        String string = (String)hashMap.get("style");
        if (string == null) {
            return;
        }
        Properties properties = Markup.parseAttributes(string);
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            Object object;
            String string2 = (String)iterator.next();
            if (string2.equals("font-family")) {
                hashMap.put("face", properties.getProperty(string2));
                continue;
            }
            if (string2.equals("font-size")) {
                hashMap.put("size", Float.toString(Markup.parseLength(properties.getProperty(string2))) + "px");
                continue;
            }
            if (string2.equals("font-style")) {
                object = properties.getProperty(string2).trim().toLowerCase();
                if (!object.equals("italic") && !object.equals("oblique")) continue;
                hashMap.put("i", null);
                continue;
            }
            if (string2.equals("font-weight")) {
                object = properties.getProperty(string2).trim().toLowerCase();
                if (!object.equals("bold") && !object.equals("700") && !object.equals("800") && !object.equals("900")) continue;
                hashMap.put("b", null);
                continue;
            }
            if (string2.equals("font-weight")) {
                object = properties.getProperty(string2).trim().toLowerCase();
                if (!object.equals("underline")) continue;
                hashMap.put("u", null);
                continue;
            }
            if (string2.equals("color")) {
                object = Markup.decodeColor(properties.getProperty(string2));
                if (object == null) continue;
                int n = object.getRGB();
                String string3 = Integer.toHexString(n);
                string3 = "000000" + string3;
                string3 = "#" + string3.substring(string3.length() - 6);
                hashMap.put("color", string3);
                continue;
            }
            if (string2.equals("line-height")) {
                object = properties.getProperty(string2).trim();
                float f = Markup.parseLength(properties.getProperty(string2));
                if (object.endsWith("%")) {
                    hashMap.put("leading", "0," + f / 100.0f);
                    continue;
                }
                hashMap.put("leading", "" + f + ",0");
                continue;
            }
            if (!string2.equals("text-align")) continue;
            object = properties.getProperty(string2).trim().toLowerCase();
            hashMap.put("align", object);
        }
    }

    public static void insertStyle(HashMap hashMap, ChainedProperties chainedProperties) {
        String string = (String)hashMap.get("style");
        if (string == null) {
            return;
        }
        Properties properties = Markup.parseAttributes(string);
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            String string2 = (String)iterator.next();
            if (string2.equals("font-family")) {
                hashMap.put("face", properties.getProperty(string2));
                continue;
            }
            if (string2.equals("font-size")) {
                float f = Markup.parseLength(chainedProperties.getProperty("size"), 12.0f);
                if (f <= 0.0f) {
                    f = 12.0f;
                }
                hashMap.put("size", Float.toString(Markup.parseLength(properties.getProperty(string2), f)) + "pt");
                continue;
            }
            if (string2.equals("font-style")) {
                String string3 = properties.getProperty(string2).trim().toLowerCase();
                if (!string3.equals("italic") && !string3.equals("oblique")) continue;
                hashMap.put("i", null);
                continue;
            }
            if (string2.equals("font-weight")) {
                String string4 = properties.getProperty(string2).trim().toLowerCase();
                if (!string4.equals("bold") && !string4.equals("700") && !string4.equals("800") && !string4.equals("900")) continue;
                hashMap.put("b", null);
                continue;
            }
            if (string2.equals("font-weight")) {
                String string5 = properties.getProperty(string2).trim().toLowerCase();
                if (!string5.equals("underline")) continue;
                hashMap.put("u", null);
                continue;
            }
            if (string2.equals("color")) {
                Color color = Markup.decodeColor(properties.getProperty(string2));
                if (color == null) continue;
                int n = color.getRGB();
                String string6 = Integer.toHexString(n);
                string6 = "000000" + string6;
                string6 = "#" + string6.substring(string6.length() - 6);
                hashMap.put("color", string6);
                continue;
            }
            if (string2.equals("line-height")) {
                String string7 = properties.getProperty(string2).trim();
                float f = Markup.parseLength(chainedProperties.getProperty("size"), 12.0f);
                if (f <= 0.0f) {
                    f = 12.0f;
                }
                float f2 = Markup.parseLength(properties.getProperty(string2), f);
                if (string7.endsWith("%")) {
                    hashMap.put("leading", "0," + f2 / 100.0f);
                    continue;
                }
                hashMap.put("leading", "" + f2 + ",0");
                continue;
            }
            if (!string2.equals("text-align")) continue;
            String string8 = properties.getProperty(string2).trim().toLowerCase();
            hashMap.put("align", string8);
        }
    }

    public FontFactoryImp getFontImp() {
        return this.fontImp;
    }

    public void setFontImp(FontFactoryImp fontFactoryImp) {
        this.fontImp = fontFactoryImp;
    }

    static {
        followTags.put("i", "i");
        followTags.put("b", "b");
        followTags.put("u", "u");
        followTags.put("sub", "sub");
        followTags.put("sup", "sup");
        followTags.put("em", "i");
        followTags.put("strong", "b");
        followTags.put("s", "s");
        followTags.put("strike", "s");
    }
}

