/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Rectangle;
import com.lowagie.text.RectangleReadOnly;
import java.lang.reflect.Field;

public class PageSize {
    public static final Rectangle LETTER = new RectangleReadOnly(612.0f, 792.0f);
    public static final Rectangle NOTE = new RectangleReadOnly(540.0f, 720.0f);
    public static final Rectangle LEGAL = new RectangleReadOnly(612.0f, 1008.0f);
    public static final Rectangle TABLOID = new RectangleReadOnly(792.0f, 1224.0f);
    public static final Rectangle EXECUTIVE = new RectangleReadOnly(522.0f, 756.0f);
    public static final Rectangle POSTCARD = new RectangleReadOnly(283.0f, 416.0f);
    public static final Rectangle A0 = new RectangleReadOnly(2384.0f, 3370.0f);
    public static final Rectangle A1 = new RectangleReadOnly(1684.0f, 2384.0f);
    public static final Rectangle A2 = new RectangleReadOnly(1191.0f, 1684.0f);
    public static final Rectangle A3 = new RectangleReadOnly(842.0f, 1191.0f);
    public static final Rectangle A4 = new RectangleReadOnly(595.0f, 842.0f);
    public static final Rectangle A5 = new RectangleReadOnly(420.0f, 595.0f);
    public static final Rectangle A6 = new RectangleReadOnly(297.0f, 420.0f);
    public static final Rectangle A7 = new RectangleReadOnly(210.0f, 297.0f);
    public static final Rectangle A8 = new RectangleReadOnly(148.0f, 210.0f);
    public static final Rectangle A9 = new RectangleReadOnly(105.0f, 148.0f);
    public static final Rectangle A10 = new RectangleReadOnly(73.0f, 105.0f);
    public static final Rectangle B0 = new RectangleReadOnly(2834.0f, 4008.0f);
    public static final Rectangle B1 = new RectangleReadOnly(2004.0f, 2834.0f);
    public static final Rectangle B2 = new RectangleReadOnly(1417.0f, 2004.0f);
    public static final Rectangle B3 = new RectangleReadOnly(1000.0f, 1417.0f);
    public static final Rectangle B4 = new RectangleReadOnly(708.0f, 1000.0f);
    public static final Rectangle B5 = new RectangleReadOnly(498.0f, 708.0f);
    public static final Rectangle B6 = new RectangleReadOnly(354.0f, 498.0f);
    public static final Rectangle B7 = new RectangleReadOnly(249.0f, 354.0f);
    public static final Rectangle B8 = new RectangleReadOnly(175.0f, 249.0f);
    public static final Rectangle B9 = new RectangleReadOnly(124.0f, 175.0f);
    public static final Rectangle B10 = new RectangleReadOnly(87.0f, 124.0f);
    public static final Rectangle ARCH_E = new RectangleReadOnly(2592.0f, 3456.0f);
    public static final Rectangle ARCH_D = new RectangleReadOnly(1728.0f, 2592.0f);
    public static final Rectangle ARCH_C = new RectangleReadOnly(1296.0f, 1728.0f);
    public static final Rectangle ARCH_B = new RectangleReadOnly(864.0f, 1296.0f);
    public static final Rectangle ARCH_A = new RectangleReadOnly(648.0f, 864.0f);
    public static final Rectangle FLSA = new RectangleReadOnly(612.0f, 936.0f);
    public static final Rectangle FLSE = new RectangleReadOnly(648.0f, 936.0f);
    public static final Rectangle HALFLETTER = new RectangleReadOnly(396.0f, 612.0f);
    public static final Rectangle _11X17 = new RectangleReadOnly(792.0f, 1224.0f);
    public static final Rectangle ID_1 = new RectangleReadOnly(242.65f, 153.0f);
    public static final Rectangle ID_2 = new RectangleReadOnly(297.0f, 210.0f);
    public static final Rectangle ID_3 = new RectangleReadOnly(354.0f, 249.0f);
    public static final Rectangle LEDGER = new RectangleReadOnly(1224.0f, 792.0f);
    public static final Rectangle CROWN_QUARTO = new RectangleReadOnly(535.0f, 697.0f);
    public static final Rectangle LARGE_CROWN_QUARTO = new RectangleReadOnly(569.0f, 731.0f);
    public static final Rectangle DEMY_QUARTO = new RectangleReadOnly(620.0f, 782.0f);
    public static final Rectangle ROYAL_QUARTO = new RectangleReadOnly(671.0f, 884.0f);
    public static final Rectangle CROWN_OCTAVO = new RectangleReadOnly(348.0f, 527.0f);
    public static final Rectangle LARGE_CROWN_OCTAVO = new RectangleReadOnly(365.0f, 561.0f);
    public static final Rectangle DEMY_OCTAVO = new RectangleReadOnly(391.0f, 612.0f);
    public static final Rectangle ROYAL_OCTAVO = new RectangleReadOnly(442.0f, 663.0f);
    public static final Rectangle SMALL_PAPERBACK = new RectangleReadOnly(314.0f, 504.0f);
    public static final Rectangle PENGUIN_SMALL_PAPERBACK = new RectangleReadOnly(314.0f, 513.0f);
    public static final Rectangle PENGUIN_LARGE_PAPERBACK = new RectangleReadOnly(365.0f, 561.0f);

    public static Rectangle getRectangle(String string) {
        int n = (string = string.trim().toUpperCase()).indexOf(32);
        if (n == -1) {
            try {
                Class class_ = PageSize.class;
                Field field = class_.getDeclaredField(string.toUpperCase());
                return (Rectangle)field.get(null);
            }
            catch (Exception var2_3) {
                throw new RuntimeException("Can't find page size " + string);
            }
        }
        try {
            String string2 = string.substring(0, n);
            String string3 = string.substring(n + 1);
            return new Rectangle(Float.parseFloat(string2), Float.parseFloat(string3));
        }
        catch (Exception var2_5) {
            throw new RuntimeException(string + " is not a valid page size format; " + var2_5.getMessage());
        }
    }
}

