/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.factories;

import java.io.PrintStream;

public class RomanAlphabetFactory {
    public static final String getString(int n) {
        if (n < 1) {
            throw new NumberFormatException("You can't translate a negative number into an alphabetical value.");
        }
        int n2 = 1;
        int n3 = 0;
        int n4 = 26;
        while (--n >= n4 + n3) {
            ++n2;
            n3 += n4;
            n4 *= 26;
        }
        int n5 = n - n3;
        char[] arrc = new char[n2];
        while (n2 > 0) {
            arrc[--n2] = (char)(97 + n5 % 26);
            n5 /= 26;
        }
        return new String(arrc);
    }

    public static final String getLowerCaseString(int n) {
        return RomanAlphabetFactory.getString(n);
    }

    public static final String getUpperCaseString(int n) {
        return RomanAlphabetFactory.getString(n).toUpperCase();
    }

    public static final String getString(int n, boolean bl) {
        if (bl) {
            return RomanAlphabetFactory.getLowerCaseString(n);
        }
        return RomanAlphabetFactory.getUpperCaseString(n);
    }

    public static void main(String[] arrstring) {
        for (int i = 1; i < 32000; ++i) {
            System.out.println(RomanAlphabetFactory.getString(i));
        }
    }
}

