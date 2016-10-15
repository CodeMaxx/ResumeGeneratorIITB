/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactoryImp;
import java.awt.Color;
import java.util.Properties;
import java.util.Set;

public final class FontFactory {
    public static final String COURIER = "Courier";
    public static final String COURIER_BOLD = "Courier-Bold";
    public static final String COURIER_OBLIQUE = "Courier-Oblique";
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
    public static final String HELVETICA = "Helvetica";
    public static final String HELVETICA_BOLD = "Helvetica-Bold";
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
    public static final String SYMBOL = "Symbol";
    public static final String TIMES = "Times";
    public static final String TIMES_ROMAN = "Times-Roman";
    public static final String TIMES_BOLD = "Times-Bold";
    public static final String TIMES_ITALIC = "Times-Italic";
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
    public static final String ZAPFDINGBATS = "ZapfDingbats";
    private static FontFactoryImp fontImp = new FontFactoryImp();
    public static String defaultEncoding = "Cp1252";
    public static boolean defaultEmbedding = false;

    private FontFactory() {
    }

    public static Font getFont(String string, String string2, boolean bl, float f, int n, Color color) {
        return fontImp.getFont(string, string2, bl, f, n, color);
    }

    public static Font getFont(String string, String string2, boolean bl, float f, int n, Color color, boolean bl2) {
        return fontImp.getFont(string, string2, bl, f, n, color, bl2);
    }

    public static Font getFont(Properties properties) {
        FontFactory.fontImp.defaultEmbedding = defaultEmbedding;
        FontFactory.fontImp.defaultEncoding = defaultEncoding;
        return fontImp.getFont(properties);
    }

    public static Font getFont(String string, String string2, boolean bl, float f, int n) {
        return FontFactory.getFont(string, string2, bl, f, n, null);
    }

    public static Font getFont(String string, String string2, boolean bl, float f) {
        return FontFactory.getFont(string, string2, bl, f, -1, null);
    }

    public static Font getFont(String string, String string2, boolean bl) {
        return FontFactory.getFont(string, string2, bl, -1.0f, -1, null);
    }

    public static Font getFont(String string, String string2, float f, int n, Color color) {
        return FontFactory.getFont(string, string2, defaultEmbedding, f, n, color);
    }

    public static Font getFont(String string, String string2, float f, int n) {
        return FontFactory.getFont(string, string2, defaultEmbedding, f, n, null);
    }

    public static Font getFont(String string, String string2, float f) {
        return FontFactory.getFont(string, string2, defaultEmbedding, f, -1, null);
    }

    public static Font getFont(String string, String string2) {
        return FontFactory.getFont(string, string2, defaultEmbedding, -1.0f, -1, null);
    }

    public static Font getFont(String string, float f, int n, Color color) {
        return FontFactory.getFont(string, defaultEncoding, defaultEmbedding, f, n, color);
    }

    public static Font getFont(String string, float f, Color color) {
        return FontFactory.getFont(string, defaultEncoding, defaultEmbedding, f, -1, color);
    }

    public static Font getFont(String string, float f, int n) {
        return FontFactory.getFont(string, defaultEncoding, defaultEmbedding, f, n, null);
    }

    public static Font getFont(String string, float f) {
        return FontFactory.getFont(string, defaultEncoding, defaultEmbedding, f, -1, null);
    }

    public static Font getFont(String string) {
        return FontFactory.getFont(string, defaultEncoding, defaultEmbedding, -1.0f, -1, null);
    }

    public void registerFamily(String string, String string2, String string3) {
        fontImp.registerFamily(string, string2, string3);
    }

    public static void register(String string) {
        FontFactory.register(string, null);
    }

    public static void register(String string, String string2) {
        fontImp.register(string, string2);
    }

    public static int registerDirectory(String string) {
        return fontImp.registerDirectory(string);
    }

    public static int registerDirectory(String string, boolean bl) {
        return fontImp.registerDirectory(string, bl);
    }

    public static int registerDirectories() {
        return fontImp.registerDirectories();
    }

    public static Set getRegisteredFonts() {
        return fontImp.getRegisteredFonts();
    }

    public static Set getRegisteredFamilies() {
        return fontImp.getRegisteredFamilies();
    }

    public static boolean contains(String string) {
        return fontImp.isRegistered(string);
    }

    public static boolean isRegistered(String string) {
        return fontImp.isRegistered(string);
    }

    public static FontFactoryImp getFontImp() {
        return fontImp;
    }

    public static void setFontImp(FontFactoryImp fontFactoryImp) {
        if (fontFactoryImp == null) {
            throw new NullPointerException("FontFactoryImp cannot be null.");
        }
        fontImp = fontFactoryImp;
    }
}

