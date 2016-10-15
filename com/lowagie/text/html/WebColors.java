/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html;

import java.awt.Color;
import java.util.HashMap;
import java.util.StringTokenizer;

public class WebColors
extends HashMap {
    private static final long serialVersionUID = 3542523100813372896L;
    public static final WebColors NAMES = new WebColors();

    public static Color getRGBColor(String string) throws IllegalArgumentException {
        int[] arrn = new int[]{0, 0, 0, 0};
        if (string.startsWith("#")) {
            if (string.length() == 4) {
                arrn[0] = Integer.parseInt(string.substring(1, 2), 16) * 16;
                arrn[1] = Integer.parseInt(string.substring(2, 3), 16) * 16;
                arrn[2] = Integer.parseInt(string.substring(3), 16) * 16;
                return new Color(arrn[0], arrn[1], arrn[2], arrn[3]);
            }
            if (string.length() == 7) {
                arrn[0] = Integer.parseInt(string.substring(1, 3), 16);
                arrn[1] = Integer.parseInt(string.substring(3, 5), 16);
                arrn[2] = Integer.parseInt(string.substring(5), 16);
                return new Color(arrn[0], arrn[1], arrn[2], arrn[3]);
            }
            throw new IllegalArgumentException("Unknown color format. Must be #RGB or #RRGGBB");
        }
        if (string.startsWith("rgb(")) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, "rgb(), \t\r\n\f");
            for (int i = 0; i < 3; ++i) {
                String string2 = stringTokenizer.nextToken();
                arrn[i] = string2.endsWith("%") ? Integer.parseInt(string2.substring(0, string2.length() - 1)) * 255 / 100 : Integer.parseInt(string2);
                if (arrn[i] < 0) {
                    arrn[i] = 0;
                    continue;
                }
                if (arrn[i] <= 255) continue;
                arrn[i] = 255;
            }
            return new Color(arrn[0], arrn[1], arrn[2], arrn[3]);
        }
        if (!NAMES.containsKey(string = string.toLowerCase())) {
            throw new IllegalArgumentException("Color '" + string + "' not found.");
        }
        arrn = (int[])NAMES.get(string);
        return new Color(arrn[0], arrn[1], arrn[2], arrn[3]);
    }

    static {
        NAMES.put("aliceblue", new int[]{240, 248, 255, 0});
        NAMES.put("antiquewhite", new int[]{250, 235, 215, 0});
        NAMES.put("aqua", new int[]{0, 255, 255, 0});
        NAMES.put("aquamarine", new int[]{127, 255, 212, 0});
        NAMES.put("azure", new int[]{240, 255, 255, 0});
        NAMES.put("beige", new int[]{245, 245, 220, 0});
        NAMES.put("bisque", new int[]{255, 228, 196, 0});
        NAMES.put("black", new int[]{0, 0, 0, 0});
        NAMES.put("blanchedalmond", new int[]{255, 235, 205, 0});
        NAMES.put("blue", new int[]{0, 0, 255, 0});
        NAMES.put("blueviolet", new int[]{138, 43, 226, 0});
        NAMES.put("brown", new int[]{165, 42, 42, 0});
        NAMES.put("burlywood", new int[]{222, 184, 135, 0});
        NAMES.put("cadetblue", new int[]{95, 158, 160, 0});
        NAMES.put("chartreuse", new int[]{127, 255, 0, 0});
        NAMES.put("chocolate", new int[]{210, 105, 30, 0});
        NAMES.put("coral", new int[]{255, 127, 80, 0});
        NAMES.put("cornflowerblue", new int[]{100, 149, 237, 0});
        NAMES.put("cornsilk", new int[]{255, 248, 220, 0});
        NAMES.put("crimson", new int[]{220, 20, 60, 0});
        NAMES.put("cyan", new int[]{0, 255, 255, 0});
        NAMES.put("darkblue", new int[]{0, 0, 139, 0});
        NAMES.put("darkcyan", new int[]{0, 139, 139, 0});
        NAMES.put("darkgoldenrod", new int[]{184, 134, 11, 0});
        NAMES.put("darkgray", new int[]{169, 169, 169, 0});
        NAMES.put("darkgreen", new int[]{0, 100, 0, 0});
        NAMES.put("darkkhaki", new int[]{189, 183, 107, 0});
        NAMES.put("darkmagenta", new int[]{139, 0, 139, 0});
        NAMES.put("darkolivegreen", new int[]{85, 107, 47, 0});
        NAMES.put("darkorange", new int[]{255, 140, 0, 0});
        NAMES.put("darkorchid", new int[]{153, 50, 204, 0});
        NAMES.put("darkred", new int[]{139, 0, 0, 0});
        NAMES.put("darksalmon", new int[]{233, 150, 122, 0});
        NAMES.put("darkseagreen", new int[]{143, 188, 143, 0});
        NAMES.put("darkslateblue", new int[]{72, 61, 139, 0});
        NAMES.put("darkslategray", new int[]{47, 79, 79, 0});
        NAMES.put("darkturquoise", new int[]{0, 206, 209, 0});
        NAMES.put("darkviolet", new int[]{148, 0, 211, 0});
        NAMES.put("deeppink", new int[]{255, 20, 147, 0});
        NAMES.put("deepskyblue", new int[]{0, 191, 255, 0});
        NAMES.put("dimgray", new int[]{105, 105, 105, 0});
        NAMES.put("dodgerblue", new int[]{30, 144, 255, 0});
        NAMES.put("firebrick", new int[]{178, 34, 34, 0});
        NAMES.put("floralwhite", new int[]{255, 250, 240, 0});
        NAMES.put("forestgreen", new int[]{34, 139, 34, 0});
        NAMES.put("fuchsia", new int[]{255, 0, 255, 0});
        NAMES.put("gainsboro", new int[]{220, 220, 220, 0});
        NAMES.put("ghostwhite", new int[]{248, 248, 255, 0});
        NAMES.put("gold", new int[]{255, 215, 0, 0});
        NAMES.put("goldenrod", new int[]{218, 165, 32, 0});
        NAMES.put("gray", new int[]{128, 128, 128, 0});
        NAMES.put("green", new int[]{0, 128, 0, 0});
        NAMES.put("greenyellow", new int[]{173, 255, 47, 0});
        NAMES.put("honeydew", new int[]{240, 255, 240, 0});
        NAMES.put("hotpink", new int[]{255, 105, 180, 0});
        NAMES.put("indianred", new int[]{205, 92, 92, 0});
        NAMES.put("indigo", new int[]{75, 0, 130, 0});
        NAMES.put("ivory", new int[]{255, 255, 240, 0});
        NAMES.put("khaki", new int[]{240, 230, 140, 0});
        NAMES.put("lavender", new int[]{230, 230, 250, 0});
        NAMES.put("lavenderblush", new int[]{255, 240, 245, 0});
        NAMES.put("lawngreen", new int[]{124, 252, 0, 0});
        NAMES.put("lemonchiffon", new int[]{255, 250, 205, 0});
        NAMES.put("lightblue", new int[]{173, 216, 230, 0});
        NAMES.put("lightcoral", new int[]{240, 128, 128, 0});
        NAMES.put("lightcyan", new int[]{224, 255, 255, 0});
        NAMES.put("lightgoldenrodyellow", new int[]{250, 250, 210, 0});
        NAMES.put("lightgreen", new int[]{144, 238, 144, 0});
        NAMES.put("lightgrey", new int[]{211, 211, 211, 0});
        NAMES.put("lightpink", new int[]{255, 182, 193, 0});
        NAMES.put("lightsalmon", new int[]{255, 160, 122, 0});
        NAMES.put("lightseagreen", new int[]{32, 178, 170, 0});
        NAMES.put("lightskyblue", new int[]{135, 206, 250, 0});
        NAMES.put("lightslategray", new int[]{119, 136, 153, 0});
        NAMES.put("lightsteelblue", new int[]{176, 196, 222, 0});
        NAMES.put("lightyellow", new int[]{255, 255, 224, 0});
        NAMES.put("lime", new int[]{0, 255, 0, 0});
        NAMES.put("limegreen", new int[]{50, 205, 50, 0});
        NAMES.put("linen", new int[]{250, 240, 230, 0});
        NAMES.put("magenta", new int[]{255, 0, 255, 0});
        NAMES.put("maroon", new int[]{128, 0, 0, 0});
        NAMES.put("mediumaquamarine", new int[]{102, 205, 170, 0});
        NAMES.put("mediumblue", new int[]{0, 0, 205, 0});
        NAMES.put("mediumorchid", new int[]{186, 85, 211, 0});
        NAMES.put("mediumpurple", new int[]{147, 112, 219, 0});
        NAMES.put("mediumseagreen", new int[]{60, 179, 113, 0});
        NAMES.put("mediumslateblue", new int[]{123, 104, 238, 0});
        NAMES.put("mediumspringgreen", new int[]{0, 250, 154, 0});
        NAMES.put("mediumturquoise", new int[]{72, 209, 204, 0});
        NAMES.put("mediumvioletred", new int[]{199, 21, 133, 0});
        NAMES.put("midnightblue", new int[]{25, 25, 112, 0});
        NAMES.put("mintcream", new int[]{245, 255, 250, 0});
        NAMES.put("mistyrose", new int[]{255, 228, 225, 0});
        NAMES.put("moccasin", new int[]{255, 228, 181, 0});
        NAMES.put("navajowhite", new int[]{255, 222, 173, 0});
        NAMES.put("navy", new int[]{0, 0, 128, 0});
        NAMES.put("oldlace", new int[]{253, 245, 230, 0});
        NAMES.put("olive", new int[]{128, 128, 0, 0});
        NAMES.put("olivedrab", new int[]{107, 142, 35, 0});
        NAMES.put("orange", new int[]{255, 165, 0, 0});
        NAMES.put("orangered", new int[]{255, 69, 0, 0});
        NAMES.put("orchid", new int[]{218, 112, 214, 0});
        NAMES.put("palegoldenrod", new int[]{238, 232, 170, 0});
        NAMES.put("palegreen", new int[]{152, 251, 152, 0});
        NAMES.put("paleturquoise", new int[]{175, 238, 238, 0});
        NAMES.put("palevioletred", new int[]{219, 112, 147, 0});
        NAMES.put("papayawhip", new int[]{255, 239, 213, 0});
        NAMES.put("peachpuff", new int[]{255, 218, 185, 0});
        NAMES.put("peru", new int[]{205, 133, 63, 0});
        NAMES.put("pink", new int[]{255, 192, 203, 0});
        NAMES.put("plum", new int[]{221, 160, 221, 0});
        NAMES.put("powderblue", new int[]{176, 224, 230, 0});
        NAMES.put("purple", new int[]{128, 0, 128, 0});
        NAMES.put("red", new int[]{255, 0, 0, 0});
        NAMES.put("rosybrown", new int[]{188, 143, 143, 0});
        NAMES.put("royalblue", new int[]{65, 105, 225, 0});
        NAMES.put("saddlebrown", new int[]{139, 69, 19, 0});
        NAMES.put("salmon", new int[]{250, 128, 114, 0});
        NAMES.put("sandybrown", new int[]{244, 164, 96, 0});
        NAMES.put("seagreen", new int[]{46, 139, 87, 0});
        NAMES.put("seashell", new int[]{255, 245, 238, 0});
        NAMES.put("sienna", new int[]{160, 82, 45, 0});
        NAMES.put("silver", new int[]{192, 192, 192, 0});
        NAMES.put("skyblue", new int[]{135, 206, 235, 0});
        NAMES.put("slateblue", new int[]{106, 90, 205, 0});
        NAMES.put("slategray", new int[]{112, 128, 144, 0});
        NAMES.put("snow", new int[]{255, 250, 250, 0});
        NAMES.put("springgreen", new int[]{0, 255, 127, 0});
        NAMES.put("steelblue", new int[]{70, 130, 180, 0});
        NAMES.put("tan", new int[]{210, 180, 140, 0});
        NAMES.put("transparent", new int[]{0, 0, 0, 255});
        NAMES.put("teal", new int[]{0, 128, 128, 0});
        NAMES.put("thistle", new int[]{216, 191, 216, 0});
        NAMES.put("tomato", new int[]{255, 99, 71, 0});
        NAMES.put("turquoise", new int[]{64, 224, 208, 0});
        NAMES.put("violet", new int[]{238, 130, 238, 0});
        NAMES.put("wheat", new int[]{245, 222, 179, 0});
        NAMES.put("white", new int[]{255, 255, 255, 0});
        NAMES.put("whitesmoke", new int[]{245, 245, 245, 0});
        NAMES.put("yellow", new int[]{255, 255, 0, 0});
        NAMES.put("yellowgreen", new int[]{9, 2765, 50, 0});
    }
}

