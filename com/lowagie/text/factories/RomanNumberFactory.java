/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.factories;

import java.io.PrintStream;

public class RomanNumberFactory {
    private static final RomanDigit[] roman = new RomanDigit[]{new RomanDigit('m', 1000, false), new RomanDigit('d', 500, false), new RomanDigit('c', 100, true), new RomanDigit('l', 50, false), new RomanDigit('x', 10, true), new RomanDigit('v', 5, false), new RomanDigit('i', 1, true)};

    public static final String getString(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        if (n < 0) {
            stringBuffer.append('-');
            n = - n;
        }
        if (n > 3000) {
            stringBuffer.append('|');
            stringBuffer.append(RomanNumberFactory.getString(n / 1000));
            stringBuffer.append('|');
            n -= n / 1000 * 1000;
        }
        int n2 = 0;
        do {
            RomanDigit romanDigit = roman[n2];
            while (n >= romanDigit.value) {
                stringBuffer.append(romanDigit.digit);
                n -= romanDigit.value;
            }
            if (n <= 0) break;
            int n3 = n2;
            while (!RomanNumberFactory.roman[++n3].pre) {
            }
            if (n + RomanNumberFactory.roman[n3].value >= romanDigit.value) {
                stringBuffer.append(RomanNumberFactory.roman[n3].digit).append(romanDigit.digit);
                n -= romanDigit.value - RomanNumberFactory.roman[n3].value;
            }
            ++n2;
        } while (true);
        return stringBuffer.toString();
    }

    public static final String getLowerCaseString(int n) {
        return RomanNumberFactory.getString(n);
    }

    public static final String getUpperCaseString(int n) {
        return RomanNumberFactory.getString(n).toUpperCase();
    }

    public static final String getString(int n, boolean bl) {
        if (bl) {
            return RomanNumberFactory.getLowerCaseString(n);
        }
        return RomanNumberFactory.getUpperCaseString(n);
    }

    public static void main(String[] arrstring) {
        for (int i = 1; i < 2000; ++i) {
            System.out.println(RomanNumberFactory.getString(i));
        }
    }

    private static class RomanDigit {
        public char digit;
        public int value;
        public boolean pre;

        RomanDigit(char c, int n, boolean bl) {
            this.digit = c;
            this.value = n;
            this.pre = bl;
        }
    }

}

