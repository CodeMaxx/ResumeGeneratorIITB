/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ExtraEncoding;
import com.lowagie.text.pdf.IntHashtable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class PdfEncodings {
    protected static final int CIDNONE = 0;
    protected static final int CIDRANGE = 1;
    protected static final int CIDCHAR = 2;
    static final char[] winansiByteToChar;
    static final char[] pdfEncodingByteToChar;
    static final IntHashtable winansi;
    static final IntHashtable pdfEncoding;
    static HashMap extraEncodings;
    static final HashMap cmaps;
    public static final byte[][] CRLF_CID_NEWLINE;

    public static final byte[] convertToBytes(String string, String string2) {
        Object object;
        if (string == null) {
            return new byte[0];
        }
        if (string2 == null || string2.length() == 0) {
            int n = string.length();
            byte[] arrby = new byte[n];
            for (int i = 0; i < n; ++i) {
                arrby[i] = (byte)string.charAt(i);
            }
            return arrby;
        }
        ExtraEncoding extraEncoding = (ExtraEncoding)extraEncodings.get(string2.toLowerCase());
        if (extraEncoding != null && (object = extraEncoding.charToByte(string, string2)) != null) {
            return object;
        }
        object = null;
        if (string2.equals("Cp1252")) {
            object = winansi;
        } else if (string2.equals("PDF")) {
            object = pdfEncoding;
        }
        if (object != null) {
            char[] arrc = string.toCharArray();
            int n = arrc.length;
            int n2 = 0;
            byte[] arrby = new byte[n];
            int n3 = 0;
            for (int i = 0; i < n; ++i) {
                int n4 = arrc[i];
                n3 = n4 < 128 || n4 > 160 && n4 <= 255 ? n4 : object.get(n4);
                if (n3 == 0) continue;
                arrby[n2++] = (byte)n3;
            }
            if (n2 == n) {
                return arrby;
            }
            byte[] arrby2 = new byte[n2];
            System.arraycopy(arrby, 0, arrby2, 0, n2);
            return arrby2;
        }
        if (string2.equals("UnicodeBig")) {
            char[] arrc = string.toCharArray();
            int n = arrc.length;
            byte[] arrby = new byte[arrc.length * 2 + 2];
            arrby[0] = -2;
            arrby[1] = -1;
            int n5 = 2;
            for (int i = 0; i < n; ++i) {
                char c = arrc[i];
                arrby[n5++] = (byte)(c >> 8);
                arrby[n5++] = (byte)(c & 255);
            }
            return arrby;
        }
        try {
            return string.getBytes(string2);
        }
        catch (UnsupportedEncodingException var4_9) {
            throw new ExceptionConverter(var4_9);
        }
    }

    public static final byte[] convertToBytes(char n, String string) {
        Object object;
        if (string == null || string.length() == 0) {
            return new byte[]{(byte)n};
        }
        ExtraEncoding extraEncoding = (ExtraEncoding)extraEncodings.get(string.toLowerCase());
        if (extraEncoding != null && (object = extraEncoding.charToByte((char)n, string)) != null) {
            return object;
        }
        object = null;
        if (string.equals("Cp1252")) {
            object = winansi;
        } else if (string.equals("PDF")) {
            object = pdfEncoding;
        }
        if (object != null) {
            int n2 = 0;
            n2 = n < 128 || n > 160 && n <= 255 ? n : object.get(n);
            if (n2 != 0) {
                return new byte[]{(byte)n2};
            }
            return new byte[0];
        }
        if (string.equals("UnicodeBig")) {
            byte[] arrby = new byte[]{-2, -1, (byte)(n >> 8), (byte)(n & 255)};
            return arrby;
        }
        try {
            return String.valueOf((char)n).getBytes(string);
        }
        catch (UnsupportedEncodingException var4_6) {
            throw new ExceptionConverter(var4_6);
        }
    }

    public static final String convertToString(byte[] arrby, String string) {
        char[] arrc;
        if (arrby == null) {
            return "";
        }
        if (string == null || string.length() == 0) {
            char[] arrc2 = new char[arrby.length];
            for (int i = 0; i < arrby.length; ++i) {
                arrc2[i] = (char)(arrby[i] & 255);
            }
            return new String(arrc2);
        }
        ExtraEncoding extraEncoding = (ExtraEncoding)extraEncodings.get(string.toLowerCase());
        if (extraEncoding != null && (arrc = extraEncoding.byteToChar(arrby, string)) != null) {
            return arrc;
        }
        arrc = null;
        if (string.equals("Cp1252")) {
            arrc = winansiByteToChar;
        } else if (string.equals("PDF")) {
            arrc = pdfEncodingByteToChar;
        }
        if (arrc != null) {
            int n = arrby.length;
            char[] arrc3 = new char[n];
            for (int i = 0; i < n; ++i) {
                arrc3[i] = arrc[arrby[i] & 255];
            }
            return new String(arrc3);
        }
        try {
            return new String(arrby, string);
        }
        catch (UnsupportedEncodingException var4_7) {
            throw new ExceptionConverter(var4_7);
        }
    }

    public static boolean isPdfDocEncoding(String string) {
        if (string == null) {
            return true;
        }
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c < '' || c > '\u00a0' && c <= '\u00ff' || pdfEncoding.containsKey(c)) continue;
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clearCmap(String string) {
        HashMap hashMap = cmaps;
        synchronized (hashMap) {
            if (string.length() == 0) {
                cmaps.clear();
            } else {
                cmaps.remove(string);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadCmap(String string, byte[][] arrby) {
        block8 : {
            try {
                char[][] arrc = null;
                HashMap hashMap = cmaps;
                synchronized (hashMap) {
                    arrc = (char[][])cmaps.get(string);
                }
                if (arrc != null) break block8;
                arrc = PdfEncodings.readCmap(string, arrby);
                hashMap = cmaps;
                synchronized (hashMap) {
                    cmaps.put(string, arrc);
                }
            }
            catch (IOException var2_3) {
                throw new ExceptionConverter(var2_3);
            }
        }
    }

    public static String convertCmap(String string, byte[] arrby) {
        return PdfEncodings.convertCmap(string, arrby, 0, arrby.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String convertCmap(String string, byte[] arrby, int n, int n2) {
        try {
            char[][] arrc = null;
            HashMap hashMap = cmaps;
            synchronized (hashMap) {
                arrc = (char[][])cmaps.get(string);
            }
            if (arrc == null) {
                arrc = PdfEncodings.readCmap(string, (byte[][])null);
                hashMap = cmaps;
                synchronized (hashMap) {
                    cmaps.put(string, arrc);
                }
            }
            return PdfEncodings.decodeSequence(arrby, n, n2, arrc);
        }
        catch (IOException var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    static String decodeSequence(byte[] arrby, int n, int n2, char[][] arrc) {
        StringBuffer stringBuffer = new StringBuffer();
        int n3 = n + n2;
        int n4 = 0;
        for (int i = n; i < n3; ++i) {
            char[] arrc2 = arrc[n4];
            int n5 = arrby[i] & 255;
            char c = arrc2[n5];
            if ((c & 32768) == 0) {
                stringBuffer.append(c);
                n4 = 0;
                continue;
            }
            n4 = c & 32767;
        }
        return stringBuffer.toString();
    }

    static char[][] readCmap(String string, byte[][] arrby) throws IOException {
        ArrayList<char[]> arrayList = new ArrayList<char[]>();
        arrayList.add(new char[256]);
        PdfEncodings.readCmap(string, arrayList);
        if (arrby != null) {
            for (int i = 0; i < arrby.length; ++i) {
                PdfEncodings.encodeSequence(arrby[i].length, arrby[i], '\u7fff', arrayList);
            }
        }
        char[][] arrarrc = new char[arrayList.size()][];
        return (char[][])arrayList.toArray((T[])arrarrc);
    }

    static void readCmap(String string, ArrayList arrayList) throws IOException {
        String string2 = "com/lowagie/text/pdf/fonts/cmaps/" + string;
        InputStream inputStream = BaseFont.getResourceStream(string2);
        if (inputStream == null) {
            throw new IOException("The Cmap " + string + " was not found.");
        }
        PdfEncodings.encodeStream(inputStream, arrayList);
        inputStream.close();
    }

    static void encodeStream(InputStream inputStream, ArrayList arrayList) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
        String string = null;
        int n = 0;
        byte[] arrby = new byte[7];
        while ((string = bufferedReader.readLine()) != null) {
            if (string.length() < 6) continue;
            switch (n) {
                long l;
                StringTokenizer stringTokenizer;
                int n2;
                String string2;
                case 0: {
                    if (string.indexOf("begincidrange") >= 0) {
                        n = 1;
                        break;
                    }
                    if (string.indexOf("begincidchar") >= 0) {
                        n = 2;
                        break;
                    }
                    if (string.indexOf("usecmap") < 0) break;
                    stringTokenizer = new StringTokenizer(string);
                    string2 = stringTokenizer.nextToken();
                    PdfEncodings.readCmap(string2.substring(1), arrayList);
                    break;
                }
                case 1: {
                    if (string.indexOf("endcidrange") >= 0) {
                        n = 0;
                        break;
                    }
                    stringTokenizer = new StringTokenizer(string);
                    string2 = stringTokenizer.nextToken();
                    n2 = string2.length() / 2 - 1;
                    l = Long.parseLong(string2.substring(1, string2.length() - 1), 16);
                    string2 = stringTokenizer.nextToken();
                    long l2 = Long.parseLong(string2.substring(1, string2.length() - 1), 16);
                    string2 = stringTokenizer.nextToken();
                    int n3 = Integer.parseInt(string2);
                    for (long i = l; i <= l2; ++i) {
                        PdfEncodings.breakLong(i, n2, arrby);
                        PdfEncodings.encodeSequence(n2, arrby, (char)n3, arrayList);
                        ++n3;
                    }
                    break;
                }
                case 2: {
                    if (string.indexOf("endcidchar") >= 0) {
                        n = 0;
                        break;
                    }
                    stringTokenizer = new StringTokenizer(string);
                    string2 = stringTokenizer.nextToken();
                    n2 = string2.length() / 2 - 1;
                    l = Long.parseLong(string2.substring(1, string2.length() - 1), 16);
                    string2 = stringTokenizer.nextToken();
                    int n4 = Integer.parseInt(string2);
                    PdfEncodings.breakLong(l, n2, arrby);
                    PdfEncodings.encodeSequence(n2, arrby, (char)n4, arrayList);
                }
            }
        }
    }

    static void breakLong(long l, int n, byte[] arrby) {
        for (int i = 0; i < n; ++i) {
            arrby[i] = (byte)(l >> (n - 1 - i) * 8);
        }
    }

    static void encodeSequence(int n, byte[] arrby, char c, ArrayList arrayList) {
        int n2;
        int n3;
        int n4 = 0;
        for (int i = 0; i < --n; ++i) {
            char[] arrc = (char[])arrayList.get(n4);
            char c2 = arrc[n2 = arrby[i] & 255];
            if (c2 != '\u0000' && (c2 & 32768) == 0) {
                throw new RuntimeException("Inconsistent mapping.");
            }
            if (c2 == '\u0000') {
                arrayList.add(new char[256]);
                arrc[n2] = c2 = (char)(arrayList.size() - 1 | 32768);
            }
            n4 = c2 & 32767;
        }
        char[] arrc = (char[])arrayList.get(n4);
        n2 = arrc[n3 = arrby[n] & 255];
        if ((n2 & 32768) != 0) {
            throw new RuntimeException("Inconsistent mapping.");
        }
        arrc[n3] = c;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addExtraEncoding(String string, ExtraEncoding extraEncoding) {
        HashMap hashMap = extraEncodings;
        synchronized (hashMap) {
            HashMap hashMap2 = (HashMap)extraEncodings.clone();
            hashMap2.put(string.toLowerCase(), extraEncoding);
            extraEncodings = hashMap2;
        }
    }

    static {
        char c;
        int n;
        winansiByteToChar = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '', '\u20ac', '\ufffd', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\ufffd', '\u017d', '\ufffd', '\ufffd', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\ufffd', '\u017e', '\u0178', '\u00a0', '\u00a1', '\u00a2', '\u00a3', '\u00a4', '\u00a5', '\u00a6', '\u00a7', '\u00a8', '\u00a9', '\u00aa', '\u00ab', '\u00ac', '\u00ad', '\u00ae', '\u00af', '\u00b0', '\u00b1', '\u00b2', '\u00b3', '\u00b4', '\u00b5', '\u00b6', '\u00b7', '\u00b8', '\u00b9', '\u00ba', '\u00bb', '\u00bc', '\u00bd', '\u00be', '\u00bf', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'};
        pdfEncodingByteToChar = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '', '\u2022', '\u2020', '\u2021', '\u2026', '\u2014', '\u2013', '\u0192', '\u2044', '\u2039', '\u203a', '\u2212', '\u2030', '\u201e', '\u201c', '\u201d', '\u2018', '\u2019', '\u201a', '\u2122', '\ufb01', '\ufb02', '\u0141', '\u0152', '\u0160', '\u0178', '\u017d', '\u0131', '\u0142', '\u0153', '\u0161', '\u017e', '\ufffd', '\u20ac', '\u00a1', '\u00a2', '\u00a3', '\u00a4', '\u00a5', '\u00a6', '\u00a7', '\u00a8', '\u00a9', '\u00aa', '\u00ab', '\u00ac', '\u00ad', '\u00ae', '\u00af', '\u00b0', '\u00b1', '\u00b2', '\u00b3', '\u00b4', '\u00b5', '\u00b6', '\u00b7', '\u00b8', '\u00b9', '\u00ba', '\u00bb', '\u00bc', '\u00bd', '\u00be', '\u00bf', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'};
        winansi = new IntHashtable();
        pdfEncoding = new IntHashtable();
        extraEncodings = new HashMap();
        for (n = 128; n < 161; ++n) {
            c = winansiByteToChar[n];
            if (c == '\ufffd') continue;
            winansi.put(c, n);
        }
        for (n = 128; n < 161; ++n) {
            c = pdfEncodingByteToChar[n];
            if (c == '\ufffd') continue;
            pdfEncoding.put(c, n);
        }
        PdfEncodings.addExtraEncoding("Wingdings", new WingdingsConversion());
        PdfEncodings.addExtraEncoding("Symbol", new SymbolConversion(true));
        PdfEncodings.addExtraEncoding("ZapfDingbats", new SymbolConversion(false));
        PdfEncodings.addExtraEncoding("SymbolTT", new SymbolTTConversion());
        PdfEncodings.addExtraEncoding("Cp437", new Cp437Conversion());
        cmaps = new HashMap();
        CRLF_CID_NEWLINE = new byte[][]{{10}, {13, 10}};
    }

    private static class SymbolTTConversion
    implements ExtraEncoding {
        private SymbolTTConversion() {
        }

        public byte[] charToByte(char c, String string) {
            if ((c & 65280) == 0 || (c & 65280) == 61440) {
                return new byte[]{(byte)c};
            }
            return new byte[0];
        }

        public byte[] charToByte(String string, String string2) {
            char[] arrc = string.toCharArray();
            byte[] arrby = new byte[arrc.length];
            int n = 0;
            int n2 = arrc.length;
            for (int i = 0; i < n2; ++i) {
                char c = arrc[i];
                if ((c & 65280) != 0 && (c & 65280) != 61440) continue;
                arrby[n++] = (byte)c;
            }
            if (n == n2) {
                return arrby;
            }
            byte[] arrby2 = new byte[n];
            System.arraycopy(arrby, 0, arrby2, 0, n);
            return arrby2;
        }

        public String byteToChar(byte[] arrby, String string) {
            return null;
        }
    }

    private static class SymbolConversion
    implements ExtraEncoding {
        private static final IntHashtable t1;
        private static final IntHashtable t2;
        private IntHashtable translation;
        private static final char[] table1;
        private static final char[] table2;

        SymbolConversion(boolean bl) {
            this.translation = bl ? t1 : t2;
        }

        public byte[] charToByte(String string, String string2) {
            char[] arrc = string.toCharArray();
            byte[] arrby = new byte[arrc.length];
            int n = 0;
            int n2 = arrc.length;
            for (int i = 0; i < n2; ++i) {
                char c = arrc[i];
                byte by = (byte)this.translation.get(c);
                if (by == 0) continue;
                arrby[n++] = by;
            }
            if (n == n2) {
                return arrby;
            }
            byte[] arrby2 = new byte[n];
            System.arraycopy(arrby, 0, arrby2, 0, n);
            return arrby2;
        }

        public byte[] charToByte(char c, String string) {
            byte by = (byte)this.translation.get(c);
            if (by != 0) {
                return new byte[]{by};
            }
            return new byte[0];
        }

        public String byteToChar(byte[] arrby, String string) {
            return null;
        }

        static {
            char c;
            int n;
            t1 = new IntHashtable();
            t2 = new IntHashtable();
            table1 = new char[]{' ', '!', '\u2200', '#', '\u2203', '%', '&', '\u220b', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '\u2245', '\u0391', '\u0392', '\u03a7', '\u0394', '\u0395', '\u03a6', '\u0393', '\u0397', '\u0399', '\u03d1', '\u039a', '\u039b', '\u039c', '\u039d', '\u039f', '\u03a0', '\u0398', '\u03a1', '\u03a3', '\u03a4', '\u03a5', '\u03c2', '\u03a9', '\u039e', '\u03a8', '\u0396', '[', '\u2234', ']', '\u22a5', '_', '\u0305', '\u03b1', '\u03b2', '\u03c7', '\u03b4', '\u03b5', '\u03d5', '\u03b3', '\u03b7', '\u03b9', '\u03c6', '\u03ba', '\u03bb', '\u03bc', '\u03bd', '\u03bf', '\u03c0', '\u03b8', '\u03c1', '\u03c3', '\u03c4', '\u03c5', '\u03d6', '\u03c9', '\u03be', '\u03c8', '\u03b6', '{', '|', '}', '~', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u20ac', '\u03d2', '\u2032', '\u2264', '\u2044', '\u221e', '\u0192', '\u2663', '\u2666', '\u2665', '\u2660', '\u2194', '\u2190', '\u2191', '\u2192', '\u2193', '\u00b0', '\u00b1', '\u2033', '\u2265', '\u00d7', '\u221d', '\u2202', '\u2022', '\u00f7', '\u2260', '\u2261', '\u2248', '\u2026', '\u2502', '\u2500', '\u21b5', '\u2135', '\u2111', '\u211c', '\u2118', '\u2297', '\u2295', '\u2205', '\u2229', '\u222a', '\u2283', '\u2287', '\u2284', '\u2282', '\u2286', '\u2208', '\u2209', '\u2220', '\u2207', '\u00ae', '\u00a9', '\u2122', '\u220f', '\u221a', '\u2022', '\u00ac', '\u2227', '\u2228', '\u21d4', '\u21d0', '\u21d1', '\u21d2', '\u21d3', '\u25ca', '\u2329', '\u0000', '\u0000', '\u0000', '\u2211', '\u239b', '\u239c', '\u239d', '\u23a1', '\u23a2', '\u23a3', '\u23a7', '\u23a8', '\u23a9', '\u23aa', '\u0000', '\u232a', '\u222b', '\u2320', '\u23ae', '\u2321', '\u239e', '\u239f', '\u23a0', '\u23a4', '\u23a5', '\u23a6', '\u23ab', '\u23ac', '\u23ad', '\u0000'};
            table2 = new char[]{' ', '\u2701', '\u2702', '\u2703', '\u2704', '\u260e', '\u2706', '\u2707', '\u2708', '\u2709', '\u261b', '\u261e', '\u270c', '\u270d', '\u270e', '\u270f', '\u2710', '\u2711', '\u2712', '\u2713', '\u2714', '\u2715', '\u2716', '\u2717', '\u2718', '\u2719', '\u271a', '\u271b', '\u271c', '\u271d', '\u271e', '\u271f', '\u2720', '\u2721', '\u2722', '\u2723', '\u2724', '\u2725', '\u2726', '\u2727', '\u2605', '\u2729', '\u272a', '\u272b', '\u272c', '\u272d', '\u272e', '\u272f', '\u2730', '\u2731', '\u2732', '\u2733', '\u2734', '\u2735', '\u2736', '\u2737', '\u2738', '\u2739', '\u273a', '\u273b', '\u273c', '\u273d', '\u273e', '\u273f', '\u2740', '\u2741', '\u2742', '\u2743', '\u2744', '\u2745', '\u2746', '\u2747', '\u2748', '\u2749', '\u274a', '\u274b', '\u25cf', '\u274d', '\u25a0', '\u274f', '\u2750', '\u2751', '\u2752', '\u25b2', '\u25bc', '\u25c6', '\u2756', '\u25d7', '\u2758', '\u2759', '\u275a', '\u275b', '\u275c', '\u275d', '\u275e', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u2761', '\u2762', '\u2763', '\u2764', '\u2765', '\u2766', '\u2767', '\u2663', '\u2666', '\u2665', '\u2660', '\u2460', '\u2461', '\u2462', '\u2463', '\u2464', '\u2465', '\u2466', '\u2467', '\u2468', '\u2469', '\u2776', '\u2777', '\u2778', '\u2779', '\u277a', '\u277b', '\u277c', '\u277d', '\u277e', '\u277f', '\u2780', '\u2781', '\u2782', '\u2783', '\u2784', '\u2785', '\u2786', '\u2787', '\u2788', '\u2789', '\u278a', '\u278b', '\u278c', '\u278d', '\u278e', '\u278f', '\u2790', '\u2791', '\u2792', '\u2793', '\u2794', '\u2192', '\u2194', '\u2195', '\u2798', '\u2799', '\u279a', '\u279b', '\u279c', '\u279d', '\u279e', '\u279f', '\u27a0', '\u27a1', '\u27a2', '\u27a3', '\u27a4', '\u27a5', '\u27a6', '\u27a7', '\u27a8', '\u27a9', '\u27aa', '\u27ab', '\u27ac', '\u27ad', '\u27ae', '\u27af', '\u0000', '\u27b1', '\u27b2', '\u27b3', '\u27b4', '\u27b5', '\u27b6', '\u27b7', '\u27b8', '\u27b9', '\u27ba', '\u27bb', '\u27bc', '\u27bd', '\u27be', '\u0000'};
            for (n = 0; n < table1.length; ++n) {
                c = table1[n];
                if (c == '\u0000') continue;
                t1.put(c, n + 32);
            }
            for (n = 0; n < table2.length; ++n) {
                c = table2[n];
                if (c == '\u0000') continue;
                t2.put(c, n + 32);
            }
        }
    }

    private static class Cp437Conversion
    implements ExtraEncoding {
        private static IntHashtable c2b = new IntHashtable();
        private static final char[] table = new char[]{'\u00c7', '\u00fc', '\u00e9', '\u00e2', '\u00e4', '\u00e0', '\u00e5', '\u00e7', '\u00ea', '\u00eb', '\u00e8', '\u00ef', '\u00ee', '\u00ec', '\u00c4', '\u00c5', '\u00c9', '\u00e6', '\u00c6', '\u00f4', '\u00f6', '\u00f2', '\u00fb', '\u00f9', '\u00ff', '\u00d6', '\u00dc', '\u00a2', '\u00a3', '\u00a5', '\u20a7', '\u0192', '\u00e1', '\u00ed', '\u00f3', '\u00fa', '\u00f1', '\u00d1', '\u00aa', '\u00ba', '\u00bf', '\u2310', '\u00ac', '\u00bd', '\u00bc', '\u00a1', '\u00ab', '\u00bb', '\u2591', '\u2592', '\u2593', '\u2502', '\u2524', '\u2561', '\u2562', '\u2556', '\u2555', '\u2563', '\u2551', '\u2557', '\u255d', '\u255c', '\u255b', '\u2510', '\u2514', '\u2534', '\u252c', '\u251c', '\u2500', '\u253c', '\u255e', '\u255f', '\u255a', '\u2554', '\u2569', '\u2566', '\u2560', '\u2550', '\u256c', '\u2567', '\u2568', '\u2564', '\u2565', '\u2559', '\u2558', '\u2552', '\u2553', '\u256b', '\u256a', '\u2518', '\u250c', '\u2588', '\u2584', '\u258c', '\u2590', '\u2580', '\u03b1', '\u00df', '\u0393', '\u03c0', '\u03a3', '\u03c3', '\u00b5', '\u03c4', '\u03a6', '\u0398', '\u03a9', '\u03b4', '\u221e', '\u03c6', '\u03b5', '\u2229', '\u2261', '\u00b1', '\u2265', '\u2264', '\u2320', '\u2321', '\u00f7', '\u2248', '\u00b0', '\u2219', '\u00b7', '\u221a', '\u207f', '\u00b2', '\u25a0', '\u00a0'};

        private Cp437Conversion() {
        }

        public byte[] charToByte(String string, String string2) {
            char[] arrc = string.toCharArray();
            byte[] arrby = new byte[arrc.length];
            int n = 0;
            int n2 = arrc.length;
            for (int i = 0; i < n2; ++i) {
                char c = arrc[i];
                if (c < '') {
                    arrby[n++] = (byte)c;
                    continue;
                }
                byte by = (byte)c2b.get(c);
                if (by == 0) continue;
                arrby[n++] = by;
            }
            if (n == n2) {
                return arrby;
            }
            byte[] arrby2 = new byte[n];
            System.arraycopy(arrby, 0, arrby2, 0, n);
            return arrby2;
        }

        public byte[] charToByte(char c, String string) {
            if (c < '') {
                return new byte[]{(byte)c};
            }
            byte by = (byte)c2b.get(c);
            if (by != 0) {
                return new byte[]{by};
            }
            return new byte[0];
        }

        public String byteToChar(byte[] arrby, String string) {
            int n = arrby.length;
            char[] arrc = new char[n];
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                int n3 = arrby[i] & 255;
                if (n3 < 32) continue;
                if (n3 < 128) {
                    arrc[n2++] = (char)n3;
                    continue;
                }
                char c = table[n3 - 128];
                arrc[n2++] = c;
            }
            return new String(arrc, 0, n2);
        }

        static {
            for (int i = 0; i < table.length; ++i) {
                c2b.put(table[i], i + 128);
            }
        }
    }

    private static class WingdingsConversion
    implements ExtraEncoding {
        private static final byte[] table = new byte[]{0, 35, 34, 0, 0, 0, 41, 62, 81, 42, 0, 0, 65, 63, 0, 0, 0, 0, 0, -4, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 86, 0, 88, 89, 0, 0, 0, 0, 0, 0, 0, 0, -75, 0, 0, 0, 0, 0, -74, 0, 0, 0, -83, -81, -84, 0, 0, 0, 0, 0, 0, 0, 0, 124, 123, 0, 0, 0, 84, 0, 0, 0, 0, 0, 0, 0, 0, -90, 0, 0, 0, 113, 114, 0, 0, 0, 117, 0, 0, 0, 0, 0, 0, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -24, -40, 0, 0, -60, -58, 0, 0, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        private WingdingsConversion() {
        }

        public byte[] charToByte(char c, String string) {
            byte by;
            if (c == ' ') {
                return new byte[]{(byte)c};
            }
            if (c >= '\u2701' && c <= '\u27be' && (by = table[c - 9984]) != 0) {
                return new byte[]{by};
            }
            return new byte[0];
        }

        public byte[] charToByte(String string, String string2) {
            char[] arrc = string.toCharArray();
            byte[] arrby = new byte[arrc.length];
            int n = 0;
            int n2 = arrc.length;
            for (int i = 0; i < n2; ++i) {
                byte by;
                char c = arrc[i];
                if (c == ' ') {
                    arrby[n++] = (byte)c;
                    continue;
                }
                if (c < '\u2701' || c > '\u27be' || (by = table[c - 9984]) == 0) continue;
                arrby[n++] = by;
            }
            if (n == n2) {
                return arrby;
            }
            byte[] arrby2 = new byte[n];
            System.arraycopy(arrby, 0, arrby2, 0, n);
            return arrby2;
        }

        public String byteToChar(byte[] arrby, String string) {
            return null;
        }
    }

}

