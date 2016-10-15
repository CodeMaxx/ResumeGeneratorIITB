/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Utilities;
import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class FontFactoryImp {
    private Properties trueTypeFonts = new Properties();
    private static String[] TTFamilyOrder = new String[]{"3", "1", "1033", "3", "0", "1033", "1", "0", "0", "0", "3", "0"};
    private Hashtable fontFamilies = new Hashtable();
    public String defaultEncoding = "Cp1252";
    public boolean defaultEmbedding = false;

    public FontFactoryImp() {
        this.trueTypeFonts.setProperty("Courier".toLowerCase(), "Courier");
        this.trueTypeFonts.setProperty("Courier-Bold".toLowerCase(), "Courier-Bold");
        this.trueTypeFonts.setProperty("Courier-Oblique".toLowerCase(), "Courier-Oblique");
        this.trueTypeFonts.setProperty("Courier-BoldOblique".toLowerCase(), "Courier-BoldOblique");
        this.trueTypeFonts.setProperty("Helvetica".toLowerCase(), "Helvetica");
        this.trueTypeFonts.setProperty("Helvetica-Bold".toLowerCase(), "Helvetica-Bold");
        this.trueTypeFonts.setProperty("Helvetica-Oblique".toLowerCase(), "Helvetica-Oblique");
        this.trueTypeFonts.setProperty("Helvetica-BoldOblique".toLowerCase(), "Helvetica-BoldOblique");
        this.trueTypeFonts.setProperty("Symbol".toLowerCase(), "Symbol");
        this.trueTypeFonts.setProperty("Times-Roman".toLowerCase(), "Times-Roman");
        this.trueTypeFonts.setProperty("Times-Bold".toLowerCase(), "Times-Bold");
        this.trueTypeFonts.setProperty("Times-Italic".toLowerCase(), "Times-Italic");
        this.trueTypeFonts.setProperty("Times-BoldItalic".toLowerCase(), "Times-BoldItalic");
        this.trueTypeFonts.setProperty("ZapfDingbats".toLowerCase(), "ZapfDingbats");
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Courier");
        arrayList.add("Courier-Bold");
        arrayList.add("Courier-Oblique");
        arrayList.add("Courier-BoldOblique");
        this.fontFamilies.put("Courier".toLowerCase(), arrayList);
        arrayList = new ArrayList();
        arrayList.add("Helvetica");
        arrayList.add("Helvetica-Bold");
        arrayList.add("Helvetica-Oblique");
        arrayList.add("Helvetica-BoldOblique");
        this.fontFamilies.put("Helvetica".toLowerCase(), arrayList);
        arrayList = new ArrayList();
        arrayList.add("Symbol");
        this.fontFamilies.put("Symbol".toLowerCase(), arrayList);
        arrayList = new ArrayList();
        arrayList.add("Times-Roman");
        arrayList.add("Times-Bold");
        arrayList.add("Times-Italic");
        arrayList.add("Times-BoldItalic");
        this.fontFamilies.put("Times".toLowerCase(), arrayList);
        this.fontFamilies.put("Times-Roman".toLowerCase(), arrayList);
        arrayList = new ArrayList();
        arrayList.add("ZapfDingbats");
        this.fontFamilies.put("ZapfDingbats".toLowerCase(), arrayList);
    }

    public Font getFont(String string, String string2, boolean bl, float f, int n, Color color) {
        return this.getFont(string, string2, bl, f, n, color, true);
    }

    public Font getFont(String string, String string2, boolean bl, float f, int n, Color color, boolean bl2) {
        if (string == null) {
            return new Font(-1, f, n, color);
        }
        String string3 = string.toLowerCase();
        ArrayList arrayList = (ArrayList)this.fontFamilies.get(string3);
        if (arrayList != null) {
            int n2 = n == -1 ? 0 : n;
            int n3 = 0;
            boolean bl3 = false;
            Iterator iterator = arrayList.iterator();
            while (iterator.hasNext()) {
                String string4 = (String)iterator.next();
                String string5 = string4.toLowerCase();
                n3 = 0;
                if (string5.toLowerCase().indexOf("bold") != -1) {
                    n3 |= 1;
                }
                if (string5.toLowerCase().indexOf("italic") != -1 || string5.toLowerCase().indexOf("oblique") != -1) {
                    n3 |= 2;
                }
                if ((n2 & 3) != n3) continue;
                string = string4;
                bl3 = true;
                break;
            }
            if (n != -1 && bl3) {
                n &= ~ n3;
            }
        }
        BaseFont baseFont = null;
        try {
            try {
                baseFont = BaseFont.createFont(string, string2, bl, bl2, null, null, true);
            }
            catch (DocumentException var11_13) {
                // empty catch block
            }
            if (baseFont == null) {
                if ((string = this.trueTypeFonts.getProperty(string.toLowerCase())) == null) {
                    return new Font(-1, f, n, color);
                }
                baseFont = BaseFont.createFont(string, string2, bl, bl2, null, null);
            }
        }
        catch (DocumentException var11_14) {
            throw new ExceptionConverter(var11_14);
        }
        catch (IOException var11_15) {
            return new Font(-1, f, n, color);
        }
        catch (NullPointerException var11_16) {
            return new Font(-1, f, n, color);
        }
        return new Font(baseFont, f, n, color);
    }

    public Font getFont(Properties properties) {
        Object object2;
        Object object;
        String string2 = null;
        String string3 = this.defaultEncoding;
        boolean bl = this.defaultEmbedding;
        float f = -1.0f;
        int n = 0;
        Color color = null;
        String string4 = properties.getProperty("style");
        if (string4 != null && string4.length() > 0) {
            object2 = Markup.parseAttributes(string4);
            if (object2.isEmpty()) {
                properties.put("style", string4);
            } else {
                string2 = object2.getProperty("font-family");
                if (string2 != null) {
                    while (string2.indexOf(44) != -1) {
                        object = string2.substring(0, string2.indexOf(44));
                        if (this.isRegistered((String)object)) {
                            string2 = object;
                            continue;
                        }
                        string2 = string2.substring(string2.indexOf(44) + 1);
                    }
                }
                if ((string4 = object2.getProperty("font-size")) != null) {
                    f = Markup.parseLength(string4);
                }
                if ((string4 = object2.getProperty("font-weight")) != null) {
                    n |= Font.getStyleValue(string4);
                }
                if ((string4 = object2.getProperty("font-style")) != null) {
                    n |= Font.getStyleValue(string4);
                }
                if ((string4 = object2.getProperty("color")) != null) {
                    color = Markup.decodeColor(string4);
                }
                properties.putAll(object2);
                object = object2.keys();
                while (object.hasMoreElements()) {
                    Object string = object.nextElement();
                    properties.put(string, object2.get(string));
                }
            }
        }
        if ((string4 = properties.getProperty("encoding")) != null) {
            string3 = string4;
        }
        if ("true".equals(properties.getProperty("embedded"))) {
            bl = true;
        }
        if ((string4 = properties.getProperty("font")) != null) {
            string2 = string4;
        }
        if ((string4 = properties.getProperty("size")) != null) {
            f = Markup.parseLength(string4);
        }
        if ((string4 = properties.getProperty("style")) != null) {
            n |= Font.getStyleValue(string4);
        }
        if ((string4 = properties.getProperty("fontstyle")) != null) {
            n |= Font.getStyleValue(string4);
        }
        object2 = properties.getProperty("red");
        object = properties.getProperty("green");
        String string = properties.getProperty("blue");
        if (object2 != null || object != null || string != null) {
            int n2 = 0;
            int n3 = 0;
            int n4 = 0;
            if (object2 != null) {
                n2 = Integer.parseInt((String)object2);
            }
            if (object != null) {
                n3 = Integer.parseInt((String)object);
            }
            if (string != null) {
                n4 = Integer.parseInt(string);
            }
            color = new Color(n2, n3, n4);
        } else {
            string4 = properties.getProperty("color");
            if (string4 != null) {
                color = Markup.decodeColor(string4);
            }
        }
        if (string2 == null) {
            return this.getFont(null, string3, bl, f, n, color);
        }
        return this.getFont(string2, string3, bl, f, n, color);
    }

    public Font getFont(String string, String string2, boolean bl, float f, int n) {
        return this.getFont(string, string2, bl, f, n, null);
    }

    public Font getFont(String string, String string2, boolean bl, float f) {
        return this.getFont(string, string2, bl, f, -1, null);
    }

    public Font getFont(String string, String string2, boolean bl) {
        return this.getFont(string, string2, bl, -1.0f, -1, null);
    }

    public Font getFont(String string, String string2, float f, int n, Color color) {
        return this.getFont(string, string2, this.defaultEmbedding, f, n, color);
    }

    public Font getFont(String string, String string2, float f, int n) {
        return this.getFont(string, string2, this.defaultEmbedding, f, n, null);
    }

    public Font getFont(String string, String string2, float f) {
        return this.getFont(string, string2, this.defaultEmbedding, f, -1, null);
    }

    public Font getFont(String string, float f, Color color) {
        return this.getFont(string, this.defaultEncoding, this.defaultEmbedding, f, -1, color);
    }

    public Font getFont(String string, String string2) {
        return this.getFont(string, string2, this.defaultEmbedding, -1.0f, -1, null);
    }

    public Font getFont(String string, float f, int n, Color color) {
        return this.getFont(string, this.defaultEncoding, this.defaultEmbedding, f, n, color);
    }

    public Font getFont(String string, float f, int n) {
        return this.getFont(string, this.defaultEncoding, this.defaultEmbedding, f, n, null);
    }

    public Font getFont(String string, float f) {
        return this.getFont(string, this.defaultEncoding, this.defaultEmbedding, f, -1, null);
    }

    public Font getFont(String string) {
        return this.getFont(string, this.defaultEncoding, this.defaultEmbedding, -1.0f, -1, null);
    }

    public void registerFamily(String string, String string2, String string3) {
        ArrayList<String> arrayList;
        if (string3 != null) {
            this.trueTypeFonts.setProperty(string2, string3);
        }
        if ((arrayList = (ArrayList<String>)this.fontFamilies.get(string)) == null) {
            arrayList = new ArrayList<String>();
            arrayList.add(string2);
            this.fontFamilies.put(string, arrayList);
        } else {
            int n = string2.length();
            boolean bl = false;
            for (int i = 0; i < arrayList.size(); ++i) {
                if (((String)arrayList.get(i)).length() < n) continue;
                arrayList.add(i, string2);
                bl = true;
                break;
            }
            if (!bl) {
                arrayList.add(string2);
            }
        }
    }

    public void register(String string) {
        this.register(string, null);
    }

    public void register(String string, String string2) {
        try {
            if (string.toLowerCase().endsWith(".ttf") || string.toLowerCase().endsWith(".otf") || string.toLowerCase().indexOf(".ttc,") > 0) {
                int n;
                Object[] arrobject = BaseFont.getAllFontNames(string, "Cp1252", null);
                this.trueTypeFonts.setProperty(((String)arrobject[0]).toLowerCase(), string);
                if (string2 != null) {
                    this.trueTypeFonts.setProperty(string2.toLowerCase(), string);
                }
                String[][] arrstring = (String[][])arrobject[2];
                for (int i = 0; i < arrstring.length; ++i) {
                    this.trueTypeFonts.setProperty(arrstring[i][3].toLowerCase(), string);
                }
                String string3 = null;
                String string4 = null;
                arrstring = (String[][])arrobject[1];
                block4 : for (int j = 0; j < TTFamilyOrder.length; j += 3) {
                    for (n = 0; n < arrstring.length; ++n) {
                        if (!TTFamilyOrder[j].equals(arrstring[n][0]) || !TTFamilyOrder[j + 1].equals(arrstring[n][1]) || !TTFamilyOrder[j + 2].equals(arrstring[n][2])) continue;
                        string4 = arrstring[n][3].toLowerCase();
                        j = TTFamilyOrder.length;
                        continue block4;
                    }
                }
                if (string4 != null) {
                    String string5 = "";
                    arrstring = (String[][])arrobject[2];
                    block6 : for (n = 0; n < arrstring.length; ++n) {
                        for (int k = 0; k < TTFamilyOrder.length; k += 3) {
                            if (!TTFamilyOrder[k].equals(arrstring[n][0]) || !TTFamilyOrder[k + 1].equals(arrstring[n][1]) || !TTFamilyOrder[k + 2].equals(arrstring[n][2]) || (string3 = arrstring[n][3]).equals(string5)) continue;
                            string5 = string3;
                            this.registerFamily(string4, string3, null);
                            continue block6;
                        }
                    }
                }
            } else if (string.toLowerCase().endsWith(".ttc")) {
                if (string2 != null) {
                    System.err.println("class FontFactory: You can't define an alias for a true type collection.");
                }
                String[] arrstring = BaseFont.enumerateTTCNames(string);
                for (int i = 0; i < arrstring.length; ++i) {
                    this.register(string + "," + i);
                }
            } else if (string.toLowerCase().endsWith(".afm") || string.toLowerCase().endsWith(".pfm")) {
                BaseFont baseFont = BaseFont.createFont(string, "Cp1252", false);
                String string6 = baseFont.getFullFontName()[0][3].toLowerCase();
                String string7 = baseFont.getFamilyFontName()[0][3].toLowerCase();
                String string8 = baseFont.getPostscriptFontName().toLowerCase();
                this.registerFamily(string7, string6, null);
                this.trueTypeFonts.setProperty(string8, string);
                this.trueTypeFonts.setProperty(string6, string);
            }
        }
        catch (DocumentException var3_6) {
            throw new ExceptionConverter(var3_6);
        }
        catch (IOException var3_7) {
            throw new ExceptionConverter(var3_7);
        }
    }

    public int registerDirectory(String string) {
        return this.registerDirectory(string, false);
    }

    public int registerDirectory(String string, boolean bl) {
        int n = 0;
        try {
            File file = new File(string);
            if (!file.exists() || !file.isDirectory()) {
                return 0;
            }
            String[] arrstring = file.list();
            if (arrstring == null) {
                return 0;
            }
            for (int i = 0; i < arrstring.length; ++i) {
                try {
                    String string2;
                    file = new File(string, arrstring[i]);
                    if (file.isDirectory()) {
                        if (!bl) continue;
                        n += this.registerDirectory(file.getAbsolutePath(), true);
                        continue;
                    }
                    String string3 = file.getPath();
                    String string4 = string2 = string3.length() < 4 ? null : string3.substring(string3.length() - 4).toLowerCase();
                    if (".afm".equals(string2) || ".pfm".equals(string2)) {
                        File file2 = new File(string3.substring(0, string3.length() - 4) + ".pfb");
                        if (!file2.exists()) continue;
                        this.register(string3, null);
                        ++n;
                        continue;
                    }
                    if (!".ttf".equals(string2) && !".otf".equals(string2) && !".ttc".equals(string2)) continue;
                    this.register(string3, null);
                    ++n;
                    continue;
                }
                catch (Exception var7_9) {
                    // empty catch block
                }
            }
        }
        catch (Exception var4_5) {
            // empty catch block
        }
        return n;
    }

    public int registerDirectories() {
        int n = 0;
        n += this.registerDirectory("c:/windows/fonts");
        n += this.registerDirectory("c:/winnt/fonts");
        n += this.registerDirectory("d:/windows/fonts");
        n += this.registerDirectory("d:/winnt/fonts");
        n += this.registerDirectory("/usr/share/X11/fonts", true);
        n += this.registerDirectory("/usr/X/lib/X11/fonts", true);
        n += this.registerDirectory("/usr/openwin/lib/X11/fonts", true);
        n += this.registerDirectory("/usr/share/fonts", true);
        n += this.registerDirectory("/usr/X11R6/lib/X11/fonts", true);
        n += this.registerDirectory("/Library/Fonts");
        return n += this.registerDirectory("/System/Library/Fonts");
    }

    public Set getRegisteredFonts() {
        return Utilities.getKeySet(this.trueTypeFonts);
    }

    public Set getRegisteredFamilies() {
        return Utilities.getKeySet(this.fontFamilies);
    }

    public boolean isRegistered(String string) {
        return this.trueTypeFonts.containsKey(string.toLowerCase());
    }
}

