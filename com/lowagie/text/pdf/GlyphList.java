/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.fonts.FontsResourceAnchor;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.StringTokenizer;

public class GlyphList {
    private static HashMap unicode2names;
    private static HashMap names2unicode;

    public static int[] nameToUnicode(String string) {
        return (int[])names2unicode.get(string);
    }

    public static String unicodeToName(int n) {
        return (String)unicode2names.get(new Integer(n));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    static {
        GlyphList.unicode2names = new HashMap<K, V>();
        GlyphList.names2unicode = new HashMap<K, V>();
        inputStream = null;
        try {
            try {
                inputStream = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/glyphlist.txt", new FontsResourceAnchor().getClass().getClassLoader());
                if (inputStream == null) {
                    string = "glyphlist.txt not found as resource. (It must exist as resource in the package com.lowagie.text.pdf.fonts)";
                    throw new Exception(string);
                }
                arrby = new byte[1024];
                byteArrayOutputStream = new ByteArrayOutputStream();
                do {
                    if ((n = inputStream.read(arrby)) < 0) {
                        inputStream.close();
                        inputStream = null;
                        string = PdfEncodings.convertToString(byteArrayOutputStream.toByteArray(), null);
                        stringTokenizer = new StringTokenizer(string, "\r\n");
                        break;
                    }
                    byteArrayOutputStream.write(arrby, 0, n);
                } while (true);
            }
            catch (Exception exception) {
                System.err.println("glyphlist.txt loading error: " + exception.getMessage());
                var11_14 = null;
                if (inputStream == null) return;
                try {
                    inputStream.close();
                    return;
                }
                catch (Exception var12_17) {
                    return;
                }
            }
        }
        catch (Throwable throwable) {
            var11_15 = null;
            if (inputStream == null) throw throwable;
            ** try [egrp 2[TRYBLOCK] [4 : 302->309)] { 
lbl36: // 1 sources:
            inputStream.close();
            throw throwable;
lbl38: // 1 sources:
            catch (Exception var12_18) {
                // empty catch block
            }
            throw throwable;
        }
        while (stringTokenizer.hasMoreTokens()) {
            string2 = stringTokenizer.nextToken();
            if (string2.startsWith("#")) continue;
            stringTokenizer2 = new StringTokenizer(string2, " ;\r\n\t\f");
            string3 = null;
            string4 = null;
            if (!stringTokenizer2.hasMoreTokens()) continue;
            string3 = stringTokenizer2.nextToken();
            if (!stringTokenizer2.hasMoreTokens()) continue;
            string4 = stringTokenizer2.nextToken();
            n2 = Integer.valueOf(string4, 16);
            GlyphList.unicode2names.put(n2, string3);
            GlyphList.names2unicode.put(string3, new int[]{n2});
        }
        var11_13 = null;
        if (inputStream == null) return;
        try {}
        catch (Exception var12_16) {
            return;
        }
        inputStream.close();
    }
}

