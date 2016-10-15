/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html;

import java.awt.Color;

public final class HtmlEncoder {
    private static final String[] htmlCode;

    private HtmlEncoder() {
    }

    public static String encode(String string) {
        int n = string.length();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c < '\u0100') {
                stringBuffer.append(htmlCode[c]);
                continue;
            }
            stringBuffer.append("&#").append((int)c).append(';');
        }
        return stringBuffer.toString();
    }

    public static String encode(Color color) {
        StringBuffer stringBuffer = new StringBuffer("#");
        if (color.getRed() < 16) {
            stringBuffer.append('0');
        }
        stringBuffer.append(Integer.toString(color.getRed(), 16));
        if (color.getGreen() < 16) {
            stringBuffer.append('0');
        }
        stringBuffer.append(Integer.toString(color.getGreen(), 16));
        if (color.getBlue() < 16) {
            stringBuffer.append('0');
        }
        stringBuffer.append(Integer.toString(color.getBlue(), 16));
        return stringBuffer.toString();
    }

    public static String getAlignment(int n) {
        switch (n) {
            case 0: {
                return "Left";
            }
            case 1: {
                return "Center";
            }
            case 2: {
                return "Right";
            }
            case 3: 
            case 8: {
                return "Justify";
            }
            case 4: {
                return "Top";
            }
            case 5: {
                return "Middle";
            }
            case 6: {
                return "Bottom";
            }
            case 7: {
                return "Baseline";
            }
        }
        return "";
    }

    static {
        int n;
        htmlCode = new String[256];
        for (n = 0; n < 10; ++n) {
            HtmlEncoder.htmlCode[n] = "&#00" + n + ";";
        }
        for (n = 10; n < 32; ++n) {
            HtmlEncoder.htmlCode[n] = "&#0" + n + ";";
        }
        for (n = 32; n < 128; ++n) {
            HtmlEncoder.htmlCode[n] = String.valueOf((char)n);
        }
        HtmlEncoder.htmlCode[9] = "\t";
        HtmlEncoder.htmlCode[10] = "<br />\n";
        HtmlEncoder.htmlCode[34] = "&quot;";
        HtmlEncoder.htmlCode[38] = "&amp;";
        HtmlEncoder.htmlCode[60] = "&lt;";
        HtmlEncoder.htmlCode[62] = "&gt;";
        for (n = 128; n < 256; ++n) {
            HtmlEncoder.htmlCode[n] = "&#" + n + ";";
        }
    }
}

