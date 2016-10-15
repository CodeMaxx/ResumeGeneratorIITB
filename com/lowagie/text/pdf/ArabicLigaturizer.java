/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BidiOrder;

public class ArabicLigaturizer {
    private static final char ALEF = '\u0627';
    private static final char ALEFHAMZA = '\u0623';
    private static final char ALEFHAMZABELOW = '\u0625';
    private static final char ALEFMADDA = '\u0622';
    private static final char LAM = '\u0644';
    private static final char HAMZA = '\u0621';
    private static final char TATWEEL = '\u0640';
    private static final char ZWJ = '\u200d';
    private static final char HAMZAABOVE = '\u0654';
    private static final char HAMZABELOW = '\u0655';
    private static final char WAWHAMZA = '\u0624';
    private static final char YEHHAMZA = '\u0626';
    private static final char WAW = '\u0648';
    private static final char ALEFMAKSURA = '\u0649';
    private static final char YEH = '\u064a';
    private static final char FARSIYEH = '\u06cc';
    private static final char SHADDA = '\u0651';
    private static final char KASRA = '\u0650';
    private static final char FATHA = '\u064e';
    private static final char DAMMA = '\u064f';
    private static final char MADDA = '\u0653';
    private static final char LAM_ALEF = '\ufefb';
    private static final char LAM_ALEFHAMZA = '\ufef7';
    private static final char LAM_ALEFHAMZABELOW = '\ufef9';
    private static final char LAM_ALEFMADDA = '\ufef5';
    private static final char[][] chartable = new char[][]{{'\u0621', '\ufe80'}, {'\u0622', '\ufe81', '\ufe82'}, {'\u0623', '\ufe83', '\ufe84'}, {'\u0624', '\ufe85', '\ufe86'}, {'\u0625', '\ufe87', '\ufe88'}, {'\u0626', '\ufe89', '\ufe8a', '\ufe8b', '\ufe8c'}, {'\u0627', '\ufe8d', '\ufe8e'}, {'\u0628', '\ufe8f', '\ufe90', '\ufe91', '\ufe92'}, {'\u0629', '\ufe93', '\ufe94'}, {'\u062a', '\ufe95', '\ufe96', '\ufe97', '\ufe98'}, {'\u062b', '\ufe99', '\ufe9a', '\ufe9b', '\ufe9c'}, {'\u062c', '\ufe9d', '\ufe9e', '\ufe9f', '\ufea0'}, {'\u062d', '\ufea1', '\ufea2', '\ufea3', '\ufea4'}, {'\u062e', '\ufea5', '\ufea6', '\ufea7', '\ufea8'}, {'\u062f', '\ufea9', '\ufeaa'}, {'\u0630', '\ufeab', '\ufeac'}, {'\u0631', '\ufead', '\ufeae'}, {'\u0632', '\ufeaf', '\ufeb0'}, {'\u0633', '\ufeb1', '\ufeb2', '\ufeb3', '\ufeb4'}, {'\u0634', '\ufeb5', '\ufeb6', '\ufeb7', '\ufeb8'}, {'\u0635', '\ufeb9', '\ufeba', '\ufebb', '\ufebc'}, {'\u0636', '\ufebd', '\ufebe', '\ufebf', '\ufec0'}, {'\u0637', '\ufec1', '\ufec2', '\ufec3', '\ufec4'}, {'\u0638', '\ufec5', '\ufec6', '\ufec7', '\ufec8'}, {'\u0639', '\ufec9', '\ufeca', '\ufecb', '\ufecc'}, {'\u063a', '\ufecd', '\ufece', '\ufecf', '\ufed0'}, {'\u0640', '\u0640', '\u0640', '\u0640', '\u0640'}, {'\u0641', '\ufed1', '\ufed2', '\ufed3', '\ufed4'}, {'\u0642', '\ufed5', '\ufed6', '\ufed7', '\ufed8'}, {'\u0643', '\ufed9', '\ufeda', '\ufedb', '\ufedc'}, {'\u0644', '\ufedd', '\ufede', '\ufedf', '\ufee0'}, {'\u0645', '\ufee1', '\ufee2', '\ufee3', '\ufee4'}, {'\u0646', '\ufee5', '\ufee6', '\ufee7', '\ufee8'}, {'\u0647', '\ufee9', '\ufeea', '\ufeeb', '\ufeec'}, {'\u0648', '\ufeed', '\ufeee'}, {'\u0649', '\ufeef', '\ufef0', '\ufbe8', '\ufbe9'}, {'\u064a', '\ufef1', '\ufef2', '\ufef3', '\ufef4'}, {'\u0671', '\ufb50', '\ufb51'}, {'\u0679', '\ufb66', '\ufb67', '\ufb68', '\ufb69'}, {'\u067a', '\ufb5e', '\ufb5f', '\ufb60', '\ufb61'}, {'\u067b', '\ufb52', '\ufb53', '\ufb54', '\ufb55'}, {'\u067e', '\ufb56', '\ufb57', '\ufb58', '\ufb59'}, {'\u067f', '\ufb62', '\ufb63', '\ufb64', '\ufb65'}, {'\u0680', '\ufb5a', '\ufb5b', '\ufb5c', '\ufb5d'}, {'\u0683', '\ufb76', '\ufb77', '\ufb78', '\ufb79'}, {'\u0684', '\ufb72', '\ufb73', '\ufb74', '\ufb75'}, {'\u0686', '\ufb7a', '\ufb7b', '\ufb7c', '\ufb7d'}, {'\u0687', '\ufb7e', '\ufb7f', '\ufb80', '\ufb81'}, {'\u0688', '\ufb88', '\ufb89'}, {'\u068c', '\ufb84', '\ufb85'}, {'\u068d', '\ufb82', '\ufb83'}, {'\u068e', '\ufb86', '\ufb87'}, {'\u0691', '\ufb8c', '\ufb8d'}, {'\u0698', '\ufb8a', '\ufb8b'}, {'\u06a4', '\ufb6a', '\ufb6b', '\ufb6c', '\ufb6d'}, {'\u06a6', '\ufb6e', '\ufb6f', '\ufb70', '\ufb71'}, {'\u06a9', '\ufb8e', '\ufb8f', '\ufb90', '\ufb91'}, {'\u06ad', '\ufbd3', '\ufbd4', '\ufbd5', '\ufbd6'}, {'\u06af', '\ufb92', '\ufb93', '\ufb94', '\ufb95'}, {'\u06b1', '\ufb9a', '\ufb9b', '\ufb9c', '\ufb9d'}, {'\u06b3', '\ufb96', '\ufb97', '\ufb98', '\ufb99'}, {'\u06ba', '\ufb9e', '\ufb9f'}, {'\u06bb', '\ufba0', '\ufba1', '\ufba2', '\ufba3'}, {'\u06be', '\ufbaa', '\ufbab', '\ufbac', '\ufbad'}, {'\u06c0', '\ufba4', '\ufba5'}, {'\u06c1', '\ufba6', '\ufba7', '\ufba8', '\ufba9'}, {'\u06c5', '\ufbe0', '\ufbe1'}, {'\u06c6', '\ufbd9', '\ufbda'}, {'\u06c7', '\ufbd7', '\ufbd8'}, {'\u06c8', '\ufbdb', '\ufbdc'}, {'\u06c9', '\ufbe2', '\ufbe3'}, {'\u06cb', '\ufbde', '\ufbdf'}, {'\u06cc', '\ufbfc', '\ufbfd', '\ufbfe', '\ufbff'}, {'\u06d0', '\ufbe4', '\ufbe5', '\ufbe6', '\ufbe7'}, {'\u06d2', '\ufbae', '\ufbaf'}, {'\u06d3', '\ufbb0', '\ufbb1'}};
    public static final int ar_nothing = 0;
    public static final int ar_novowel = 1;
    public static final int ar_composedtashkeel = 4;
    public static final int ar_lig = 8;
    public static final int DIGITS_EN2AN = 32;
    public static final int DIGITS_AN2EN = 64;
    public static final int DIGITS_EN2AN_INIT_LR = 96;
    public static final int DIGITS_EN2AN_INIT_AL = 128;
    private static final int DIGITS_RESERVED = 160;
    public static final int DIGITS_MASK = 224;
    public static final int DIGIT_TYPE_AN = 0;
    public static final int DIGIT_TYPE_AN_EXTENDED = 256;
    public static final int DIGIT_TYPE_MASK = 256;

    static boolean isVowel(char c) {
        return c >= '\u064b' && c <= '\u0655' || c == '\u0670';
    }

    static char charshape(char c, int n) {
        if (c >= '\u0621' && c <= '\u06d3') {
            int n2 = 0;
            int n3 = chartable.length - 1;
            while (n2 <= n3) {
                int n4 = (n2 + n3) / 2;
                if (c == chartable[n4][0]) {
                    return chartable[n4][n + 1];
                }
                if (c < chartable[n4][0]) {
                    n3 = n4 - 1;
                    continue;
                }
                n2 = n4 + 1;
            }
        } else if (c >= '\ufef5' && c <= '\ufefb') {
            return (char)(c + n);
        }
        return c;
    }

    static int shapecount(char c) {
        if (c >= '\u0621' && c <= '\u06d3' && !ArabicLigaturizer.isVowel(c)) {
            int n = 0;
            int n2 = chartable.length - 1;
            while (n <= n2) {
                int n3 = (n + n2) / 2;
                if (c == chartable[n3][0]) {
                    return chartable[n3].length - 1;
                }
                if (c < chartable[n3][0]) {
                    n2 = n3 - 1;
                    continue;
                }
                n = n3 + 1;
            }
        } else if (c == '\u200d') {
            return 4;
        }
        return 1;
    }

    static int ligature(char c, charstruct charstruct2) {
        int n = 0;
        if (charstruct2.basechar == '\u0000') {
            return 0;
        }
        if (ArabicLigaturizer.isVowel(c)) {
            n = 1;
            if (charstruct2.vowel != '\u0000' && c != '\u0651') {
                n = 2;
            }
            block0 : switch (c) {
                case '\u0651': {
                    if (charstruct2.mark1 == '\u0000') {
                        charstruct2.mark1 = 1617;
                        break;
                    }
                    return 0;
                }
                case '\u0655': {
                    switch (charstruct2.basechar) {
                        case '\u0627': {
                            charstruct2.basechar = 1573;
                            n = 2;
                            break block0;
                        }
                        case '\ufefb': {
                            charstruct2.basechar = 65273;
                            n = 2;
                            break block0;
                        }
                    }
                    charstruct2.mark1 = 1621;
                    break;
                }
                case '\u0654': {
                    switch (charstruct2.basechar) {
                        case '\u0627': {
                            charstruct2.basechar = 1571;
                            n = 2;
                            break block0;
                        }
                        case '\ufefb': {
                            charstruct2.basechar = 65271;
                            n = 2;
                            break block0;
                        }
                        case '\u0648': {
                            charstruct2.basechar = 1572;
                            n = 2;
                            break block0;
                        }
                        case '\u0649': 
                        case '\u064a': 
                        case '\u06cc': {
                            charstruct2.basechar = 1574;
                            n = 2;
                            break block0;
                        }
                    }
                    charstruct2.mark1 = 1620;
                    break;
                }
                case '\u0653': {
                    switch (charstruct2.basechar) {
                        case '\u0627': {
                            charstruct2.basechar = 1570;
                            n = 2;
                        }
                    }
                    break;
                }
                default: {
                    charstruct2.vowel = c;
                }
            }
            if (n == 1) {
                ++charstruct2.lignum;
            }
            return n;
        }
        if (charstruct2.vowel != '\u0000') {
            return 0;
        }
        block19 : switch (charstruct2.basechar) {
            case '\u0644': {
                switch (c) {
                    case '\u0627': {
                        charstruct2.basechar = 65275;
                        charstruct2.numshapes = 2;
                        n = 3;
                        break block19;
                    }
                    case '\u0623': {
                        charstruct2.basechar = 65271;
                        charstruct2.numshapes = 2;
                        n = 3;
                        break block19;
                    }
                    case '\u0625': {
                        charstruct2.basechar = 65273;
                        charstruct2.numshapes = 2;
                        n = 3;
                        break block19;
                    }
                    case '\u0622': {
                        charstruct2.basechar = 65269;
                        charstruct2.numshapes = 2;
                        n = 3;
                    }
                }
                break;
            }
            case '\u0000': {
                charstruct2.basechar = c;
                charstruct2.numshapes = ArabicLigaturizer.shapecount(c);
                n = 1;
            }
        }
        return n;
    }

    static void copycstostring(StringBuffer stringBuffer, charstruct charstruct2, int n) {
        if (charstruct2.basechar == '\u0000') {
            return;
        }
        stringBuffer.append(charstruct2.basechar);
        --charstruct2.lignum;
        if (charstruct2.mark1 != '\u0000') {
            if ((n & 1) == 0) {
                stringBuffer.append(charstruct2.mark1);
                --charstruct2.lignum;
            } else {
                --charstruct2.lignum;
            }
        }
        if (charstruct2.vowel != '\u0000') {
            if ((n & 1) == 0) {
                stringBuffer.append(charstruct2.vowel);
                --charstruct2.lignum;
            } else {
                --charstruct2.lignum;
            }
        }
    }

    static void doublelig(StringBuffer stringBuffer, int n) {
        int n2;
        int n3 = n2 = stringBuffer.length();
        int n4 = 0;
        int n5 = 1;
        while (n5 < n3) {
            int n6 = 0;
            if ((n & 4) != 0) {
                block0 : switch (stringBuffer.charAt(n4)) {
                    case '\u0651': {
                        switch (stringBuffer.charAt(n5)) {
                            case '\u0650': {
                                n6 = 64610;
                                break block0;
                            }
                            case '\u064e': {
                                n6 = 64608;
                                break block0;
                            }
                            case '\u064f': {
                                n6 = 64609;
                                break block0;
                            }
                            case '\u064c': {
                                n6 = 64606;
                                break block0;
                            }
                            case '\u064d': {
                                n6 = 64607;
                            }
                        }
                        break;
                    }
                    case '\u0650': {
                        if (stringBuffer.charAt(n5) != '\u0651') break;
                        n6 = 64610;
                        break;
                    }
                    case '\u064e': {
                        if (stringBuffer.charAt(n5) != '\u0651') break;
                        n6 = 64608;
                        break;
                    }
                    case '\u064f': {
                        if (stringBuffer.charAt(n5) != '\u0651') break;
                        n6 = 64609;
                    }
                }
            }
            if ((n & 8) != 0) {
                block13 : switch (stringBuffer.charAt(n4)) {
                    case '\ufedf': {
                        switch (stringBuffer.charAt(n5)) {
                            case '\ufe9e': {
                                n6 = 64575;
                                break block13;
                            }
                            case '\ufea0': {
                                n6 = 64713;
                                break block13;
                            }
                            case '\ufea2': {
                                n6 = 64576;
                                break block13;
                            }
                            case '\ufea4': {
                                n6 = 64714;
                                break block13;
                            }
                            case '\ufea6': {
                                n6 = 64577;
                                break block13;
                            }
                            case '\ufea8': {
                                n6 = 64715;
                                break block13;
                            }
                            case '\ufee2': {
                                n6 = 64578;
                                break block13;
                            }
                            case '\ufee4': {
                                n6 = 64716;
                            }
                        }
                        break;
                    }
                    case '\ufe97': {
                        switch (stringBuffer.charAt(n5)) {
                            case '\ufea0': {
                                n6 = 64673;
                                break block13;
                            }
                            case '\ufea4': {
                                n6 = 64674;
                                break block13;
                            }
                            case '\ufea8': {
                                n6 = 64675;
                            }
                        }
                        break;
                    }
                    case '\ufe91': {
                        switch (stringBuffer.charAt(n5)) {
                            case '\ufea0': {
                                n6 = 64668;
                                break block13;
                            }
                            case '\ufea4': {
                                n6 = 64669;
                                break block13;
                            }
                            case '\ufea8': {
                                n6 = 64670;
                            }
                        }
                        break;
                    }
                    case '\ufee7': {
                        switch (stringBuffer.charAt(n5)) {
                            case '\ufea0': {
                                n6 = 64722;
                                break block13;
                            }
                            case '\ufea4': {
                                n6 = 64723;
                                break block13;
                            }
                            case '\ufea8': {
                                n6 = 64724;
                            }
                        }
                        break;
                    }
                    case '\ufee8': {
                        switch (stringBuffer.charAt(n5)) {
                            case '\ufeae': {
                                n6 = 64650;
                                break block13;
                            }
                            case '\ufeb0': {
                                n6 = 64651;
                            }
                        }
                        break;
                    }
                    case '\ufee3': {
                        switch (stringBuffer.charAt(n5)) {
                            case '\ufea0': {
                                n6 = 64718;
                                break block13;
                            }
                            case '\ufea4': {
                                n6 = 64719;
                                break block13;
                            }
                            case '\ufea8': {
                                n6 = 64720;
                                break block13;
                            }
                            case '\ufee4': {
                                n6 = 64721;
                            }
                        }
                        break;
                    }
                    case '\ufed3': {
                        switch (stringBuffer.charAt(n5)) {
                            case '\ufef2': {
                                n6 = 64562;
                            }
                        }
                        break;
                    }
                }
            }
            if (n6 != 0) {
                stringBuffer.setCharAt(n4, (char)n6);
                --n2;
                ++n5;
                continue;
            }
            stringBuffer.setCharAt(++n4, stringBuffer.charAt(n5));
            ++n5;
        }
        stringBuffer.setLength(n2);
    }

    static boolean connects_to_left(charstruct charstruct2) {
        return charstruct2.numshapes > 2;
    }

    static void shape(char[] arrc, StringBuffer stringBuffer, int n) {
        int n2;
        int n3 = 0;
        charstruct charstruct2 = new charstruct();
        charstruct charstruct3 = new charstruct();
        while (n3 < arrc.length) {
            int n4;
            char c;
            if ((n4 = ArabicLigaturizer.ligature(c = arrc[n3++], charstruct3)) == 0) {
                int n5 = ArabicLigaturizer.shapecount(c);
                n2 = n5 == 1 ? 0 : 2;
                if (ArabicLigaturizer.connects_to_left(charstruct2)) {
                    ++n2;
                }
                charstruct3.basechar = ArabicLigaturizer.charshape(charstruct3.basechar, n2 %= charstruct3.numshapes);
                ArabicLigaturizer.copycstostring(stringBuffer, charstruct2, n);
                charstruct2 = charstruct3;
                charstruct3 = new charstruct();
                charstruct3.basechar = c;
                charstruct3.numshapes = n5;
                ++charstruct3.lignum;
                continue;
            }
            if (n4 != 1) continue;
        }
        n2 = ArabicLigaturizer.connects_to_left(charstruct2) ? 1 : 0;
        charstruct3.basechar = ArabicLigaturizer.charshape(charstruct3.basechar, n2 %= charstruct3.numshapes);
        ArabicLigaturizer.copycstostring(stringBuffer, charstruct2, n);
        ArabicLigaturizer.copycstostring(stringBuffer, charstruct3, n);
    }

    static int arabic_shape(char[] arrc, int n, int n2, char[] arrc2, int n3, int n4, int n5) {
        char[] arrc3 = new char[n2];
        for (int i = n2 + n - 1; i >= n; --i) {
            arrc3[i - n] = arrc[i];
        }
        StringBuffer stringBuffer = new StringBuffer(n2);
        ArabicLigaturizer.shape(arrc3, stringBuffer, n5);
        if ((n5 & 12) != 0) {
            ArabicLigaturizer.doublelig(stringBuffer, n5);
        }
        System.arraycopy(stringBuffer.toString().toCharArray(), 0, arrc2, n3, stringBuffer.length());
        return stringBuffer.length();
    }

    static void processNumbers(char[] arrc, int n, int n2, int n3) {
        int n4 = n + n2;
        if ((n3 & 224) != 0) {
            char c = '0';
            switch (n3 & 256) {
                case 0: {
                    c = '\u0660';
                    break;
                }
                case 256: {
                    c = '\u06f0';
                    break;
                }
            }
            switch (n3 & 224) {
                case 32: {
                    int n5 = c - 48;
                    for (int i = n; i < n4; ++i) {
                        char c2 = arrc[i];
                        if (c2 > '9' || c2 < '0') continue;
                        char[] arrc2 = arrc;
                        int n6 = i;
                        arrc2[n6] = (char)(arrc2[n6] + n5);
                    }
                    break;
                }
                case 64: {
                    char c3 = (char)(c + 9);
                    int n7 = 48 - c;
                    for (int i = n; i < n4; ++i) {
                        char c4 = arrc[i];
                        if (c4 > c3 || c4 < c) continue;
                        char[] arrc3 = arrc;
                        int n8 = i;
                        arrc3[n8] = (char)(arrc3[n8] + n7);
                    }
                    break;
                }
                case 96: {
                    ArabicLigaturizer.shapeToArabicDigitsWithContext(arrc, 0, n2, c, false);
                    break;
                }
                case 128: {
                    ArabicLigaturizer.shapeToArabicDigitsWithContext(arrc, 0, n2, c, true);
                    break;
                }
            }
        }
    }

    static void shapeToArabicDigitsWithContext(char[] arrc, int n, int n2, char c, boolean bl) {
        c = (char)(c - 48);
        int n3 = n + n2;
        block5 : for (int i = n; i < n3; ++i) {
            char c2 = arrc[i];
            switch (BidiOrder.getDirection(c2)) {
                case 0: 
                case 3: {
                    bl = false;
                    continue block5;
                }
                case 4: {
                    bl = true;
                    continue block5;
                }
                case 8: {
                    if (!bl || c2 > '9') continue block5;
                    arrc[i] = (char)(c2 + c);
                    break;
                }
            }
        }
    }

    static class charstruct {
        char basechar;
        char mark1;
        char vowel;
        int lignum;
        int numshapes = 1;

        charstruct() {
        }
    }

}

