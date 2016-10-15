/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.factories;

import com.lowagie.text.SpecialSymbol;
import java.io.PrintStream;

public class GreekAlphabetFactory {
    public static final String getString(int n) {
        return GreekAlphabetFactory.getString(n, true);
    }

    public static final String getLowerCaseString(int n) {
        return GreekAlphabetFactory.getString(n);
    }

    public static final String getUpperCaseString(int n) {
        return GreekAlphabetFactory.getString(n).toUpperCase();
    }

    public static final String getString(int n, boolean bl) {
        if (n < 1) {
            return "";
        }
        int n2 = 1;
        int n3 = 0;
        int n4 = 24;
        while (--n >= n4 + n3) {
            ++n2;
            n3 += n4;
            n4 *= 24;
        }
        int n5 = n - n3;
        char[] arrc = new char[n2];
        while (n2 > 0) {
            arrc[--n2] = (char)(n5 % 24);
            if (arrc[n2] > '\u0010') {
                char[] arrc2 = arrc;
                int n6 = n2;
                arrc2[n6] = (char)(arrc2[n6] + '\u0001');
            }
            char[] arrc3 = arrc;
            int n7 = n2;
            arrc3[n7] = (char)(arrc3[n7] + (bl ? 945 : 913));
            arrc[n2] = SpecialSymbol.getCorrespondingSymbol(arrc[n2]);
            n5 /= 24;
        }
        return String.valueOf(arrc);
    }

    public static void main(String[] arrstring) {
        for (int i = 1; i < 1000; ++i) {
            System.out.println(GreekAlphabetFactory.getString(i));
        }
    }
}

