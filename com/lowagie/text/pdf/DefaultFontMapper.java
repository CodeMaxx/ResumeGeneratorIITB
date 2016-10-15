/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.FontMapper;
import java.awt.Font;
import java.io.File;
import java.util.HashMap;

public class DefaultFontMapper
implements FontMapper {
    private HashMap aliases = new HashMap();
    private HashMap mapper = new HashMap();

    public BaseFont awtToPdf(Font font) {
        try {
            BaseFontParameters baseFontParameters = this.getBaseFontParameters(font.getFontName());
            if (baseFontParameters != null) {
                return BaseFont.createFont(baseFontParameters.fontName, baseFontParameters.encoding, baseFontParameters.embedded, baseFontParameters.cached, baseFontParameters.ttfAfm, baseFontParameters.pfb);
            }
            String string = null;
            String string2 = font.getName();
            string = string2.equalsIgnoreCase("DialogInput") || string2.equalsIgnoreCase("Monospaced") || string2.equalsIgnoreCase("Courier") ? (font.isItalic() ? (font.isBold() ? "Courier-BoldOblique" : "Courier-Oblique") : (font.isBold() ? "Courier-Bold" : "Courier")) : (string2.equalsIgnoreCase("Serif") || string2.equalsIgnoreCase("TimesRoman") ? (font.isItalic() ? (font.isBold() ? "Times-BoldItalic" : "Times-Italic") : (font.isBold() ? "Times-Bold" : "Times-Roman")) : (font.isItalic() ? (font.isBold() ? "Helvetica-BoldOblique" : "Helvetica-Oblique") : (font.isBold() ? "Helvetica-Bold" : "Helvetica")));
            return BaseFont.createFont(string, "Cp1252", false);
        }
        catch (Exception var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    public Font pdfToAwt(BaseFont baseFont, int n) {
        String string;
        String[][] arrstring = baseFont.getFullFontName();
        if (arrstring.length == 1) {
            return new Font(arrstring[0][3], 0, n);
        }
        String string2 = null;
        String string3 = null;
        for (int i = 0; i < arrstring.length; ++i) {
            String[] arrstring2 = arrstring[i];
            if (arrstring2[0].equals("1") && arrstring2[1].equals("0")) {
                string2 = arrstring2[3];
                continue;
            }
            if (!arrstring2[2].equals("1033")) continue;
            string3 = arrstring2[3];
            break;
        }
        if ((string = string3) == null) {
            string = string2;
        }
        if (string == null) {
            string = arrstring[0][3];
        }
        return new Font(string, 0, n);
    }

    public void putName(String string, BaseFontParameters baseFontParameters) {
        this.mapper.put(string, baseFontParameters);
    }

    public void putAlias(String string, String string2) {
        this.aliases.put(string, string2);
    }

    public BaseFontParameters getBaseFontParameters(String string) {
        String string2 = (String)this.aliases.get(string);
        if (string2 == null) {
            return (BaseFontParameters)this.mapper.get(string);
        }
        BaseFontParameters baseFontParameters = (BaseFontParameters)this.mapper.get(string2);
        if (baseFontParameters == null) {
            return (BaseFontParameters)this.mapper.get(string);
        }
        return baseFontParameters;
    }

    public void insertNames(Object[] arrobject, String string) {
        String[][] arrstring = (String[][])arrobject[2];
        String string2 = null;
        for (int i = 0; i < arrstring.length; ++i) {
            String[] arrstring2 = arrstring[i];
            if (!arrstring2[2].equals("1033")) continue;
            string2 = arrstring2[3];
            break;
        }
        if (string2 == null) {
            string2 = arrstring[0][3];
        }
        BaseFontParameters baseFontParameters = new BaseFontParameters(string);
        this.mapper.put(string2, baseFontParameters);
        for (int j = 0; j < arrstring.length; ++j) {
            this.aliases.put(arrstring[j][3], string2);
        }
        this.aliases.put(arrobject[0], string2);
    }

    public int insertDirectory(String string) {
        File file = new File(string);
        if (!file.exists() || !file.isDirectory()) {
            return 0;
        }
        File[] arrfile = file.listFiles();
        if (arrfile == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < arrfile.length; ++i) {
            file = arrfile[i];
            String string2 = file.getPath().toLowerCase();
            try {
                Object[] arrobject;
                if (string2.endsWith(".ttf") || string2.endsWith(".otf") || string2.endsWith(".afm")) {
                    arrobject = BaseFont.getAllFontNames(file.getPath(), "Cp1252", null);
                    this.insertNames(arrobject, file.getPath());
                    ++n;
                    continue;
                }
                if (!string2.endsWith(".ttc")) continue;
                arrobject = BaseFont.enumerateTTCNames(file.getPath());
                for (int j = 0; j < arrobject.length; ++j) {
                    String string3 = file.getPath() + "," + j;
                    Object[] arrobject2 = BaseFont.getAllFontNames(string3, "Cp1252", null);
                    this.insertNames(arrobject2, string3);
                }
                ++n;
                continue;
            }
            catch (Exception var7_8) {
                // empty catch block
            }
        }
        return n;
    }

    public HashMap getMapper() {
        return this.mapper;
    }

    public HashMap getAliases() {
        return this.aliases;
    }

    public static class BaseFontParameters {
        public String fontName;
        public String encoding;
        public boolean embedded;
        public boolean cached;
        public byte[] ttfAfm;
        public byte[] pfb;

        public BaseFontParameters(String string) {
            this.fontName = string;
            this.encoding = "Cp1252";
            this.embedded = true;
            this.cached = true;
        }
    }

}

