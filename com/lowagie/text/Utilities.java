/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.pdf.PRTokeniser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

public class Utilities {
    public static Set getKeySet(Hashtable hashtable) {
        return hashtable == null ? Collections.EMPTY_SET : hashtable.keySet();
    }

    public static Object[][] addToArray(Object[][] arrobject, Object[] arrobject2) {
        if (arrobject == null) {
            arrobject = new Object[][]{arrobject2};
            return arrobject;
        }
        Object[][] arrobject3 = new Object[arrobject.length + 1][];
        System.arraycopy(arrobject, 0, arrobject3, 0, arrobject.length);
        arrobject3[arrobject.length] = arrobject2;
        return arrobject3;
    }

    public static boolean checkTrueOrFalse(Properties properties, String string) {
        return "true".equalsIgnoreCase(properties.getProperty(string));
    }

    public static String unEscapeURL(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] arrc = string.toCharArray();
        for (int i = 0; i < arrc.length; ++i) {
            char c = arrc[i];
            if (c == '%') {
                if (i + 2 >= arrc.length) {
                    stringBuffer.append(c);
                    continue;
                }
                int n = PRTokeniser.getHex(arrc[i + 1]);
                int n2 = PRTokeniser.getHex(arrc[i + 2]);
                if (n < 0 || n2 < 0) {
                    stringBuffer.append(c);
                    continue;
                }
                stringBuffer.append((char)(n * 16 + n2));
                i += 2;
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public static URL toURL(String string) throws MalformedURLException {
        try {
            return new URL(string);
        }
        catch (Exception var1_1) {
            return new File(string).toURI().toURL();
        }
    }

    public static void skip(InputStream inputStream, int n) throws IOException {
        long l;
        while (n > 0 && (l = inputStream.skip(n)) > 0) {
            n = (int)((long)n - l);
        }
    }

    public static final float millimetersToPoints(float f) {
        return Utilities.inchesToPoints(Utilities.millimetersToInches(f));
    }

    public static final float millimetersToInches(float f) {
        return f / 25.4f;
    }

    public static final float pointsToMillimeters(float f) {
        return Utilities.inchesToMillimeters(Utilities.pointsToInches(f));
    }

    public static final float pointsToInches(float f) {
        return f / 72.0f;
    }

    public static final float inchesToMillimeters(float f) {
        return f * 25.4f;
    }

    public static final float inchesToPoints(float f) {
        return f * 72.0f;
    }

    public static boolean isSurrogateHigh(char c) {
        return c >= '\ud800' && c <= '\udbff';
    }

    public static boolean isSurrogateLow(char c) {
        return c >= '\udc00' && c <= '\udfff';
    }

    public static boolean isSurrogatePair(String string, int n) {
        if (n < 0 || n > string.length() - 2) {
            return false;
        }
        return Utilities.isSurrogateHigh(string.charAt(n)) && Utilities.isSurrogateLow(string.charAt(n + 1));
    }

    public static boolean isSurrogatePair(char[] arrc, int n) {
        if (n < 0 || n > arrc.length - 2) {
            return false;
        }
        return Utilities.isSurrogateHigh(arrc[n]) && Utilities.isSurrogateLow(arrc[n + 1]);
    }

    public static int convertToUtf32(char c, char c2) {
        return (c - 55296) * 1024 + (c2 - 56320) + 65536;
    }

    public static int convertToUtf32(char[] arrc, int n) {
        return (arrc[n] - 55296) * 1024 + (arrc[n + 1] - 56320) + 65536;
    }

    public static int convertToUtf32(String string, int n) {
        return (string.charAt(n) - 55296) * 1024 + (string.charAt(n + 1) - 56320) + 65536;
    }

    public static String convertFromUtf32(int n) {
        if (n < 65536) {
            return Character.toString((char)n);
        }
        return new String(new char[]{(char)((n -= 65536) / 1024 + 55296), (char)(n % 1024 + 56320)});
    }
}

