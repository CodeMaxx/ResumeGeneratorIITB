/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;
import java.util.ArrayList;

public class FontSelector {
    protected ArrayList fonts = new ArrayList();

    public void addFont(Font font) {
        if (font.getBaseFont() != null) {
            this.fonts.add(font);
            return;
        }
        BaseFont baseFont = font.getCalculatedBaseFont(true);
        Font font2 = new Font(baseFont, font.getSize(), font.getCalculatedStyle(), font.getColor());
        this.fonts.add(font2);
    }

    public Phrase process(String string) {
        int n = this.fonts.size();
        if (n == 0) {
            throw new IndexOutOfBoundsException("No font is defined.");
        }
        char[] arrc = string.toCharArray();
        int n2 = arrc.length;
        StringBuffer stringBuffer = new StringBuffer();
        Font font = null;
        int n3 = -1;
        Phrase phrase = new Phrase();
        block0 : for (int i = 0; i < n2; ++i) {
            int n4;
            char c = arrc[i];
            if (c == '\n' || c == '\r') {
                stringBuffer.append(c);
                continue;
            }
            if (Utilities.isSurrogatePair(arrc, i)) {
                n4 = Utilities.convertToUtf32(arrc, i);
                for (int j = 0; j < n; ++j) {
                    font = (Font)this.fonts.get(j);
                    if (!font.getBaseFont().charExists(n4)) continue;
                    if (n3 != j) {
                        if (stringBuffer.length() > 0 && n3 != -1) {
                            Chunk chunk = new Chunk(stringBuffer.toString(), (Font)this.fonts.get(n3));
                            phrase.add(chunk);
                            stringBuffer.setLength(0);
                        }
                        n3 = j;
                    }
                    stringBuffer.append(c);
                    stringBuffer.append(arrc[++i]);
                    continue block0;
                }
                continue;
            }
            for (n4 = 0; n4 < n; ++n4) {
                font = (Font)this.fonts.get(n4);
                if (!font.getBaseFont().charExists(c)) continue;
                if (n3 != n4) {
                    if (stringBuffer.length() > 0 && n3 != -1) {
                        Chunk chunk = new Chunk(stringBuffer.toString(), (Font)this.fonts.get(n3));
                        phrase.add(chunk);
                        stringBuffer.setLength(0);
                    }
                    n3 = n4;
                }
                stringBuffer.append(c);
                continue block0;
            }
        }
        if (stringBuffer.length() > 0) {
            Chunk chunk = new Chunk(stringBuffer.toString(), (Font)this.fonts.get(n3 == -1 ? 0 : n3));
            phrase.add(chunk);
        }
        return phrase;
    }
}

