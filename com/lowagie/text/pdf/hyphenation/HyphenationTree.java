/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

import com.lowagie.text.pdf.hyphenation.ByteVector;
import com.lowagie.text.pdf.hyphenation.CharVector;
import com.lowagie.text.pdf.hyphenation.Hyphenation;
import com.lowagie.text.pdf.hyphenation.PatternConsumer;
import com.lowagie.text.pdf.hyphenation.SimplePatternParser;
import com.lowagie.text.pdf.hyphenation.TernaryTree;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class HyphenationTree
extends TernaryTree
implements PatternConsumer {
    private static final long serialVersionUID = -7763254239309429432L;
    protected ByteVector vspace = new ByteVector();
    protected HashMap stoplist = new HashMap(23);
    protected TernaryTree classmap = new TernaryTree();
    private transient TernaryTree ivalues;

    public HyphenationTree() {
        this.vspace.alloc(1);
    }

    protected int packValues(String string) {
        int n = string.length();
        int n2 = (n & 1) == 1 ? (n >> 1) + 2 : (n >> 1) + 1;
        int n3 = this.vspace.alloc(n2);
        byte[] arrby = this.vspace.getArray();
        for (int i = 0; i < n; ++i) {
            int n4 = i >> 1;
            byte by = (byte)(string.charAt(i) - 48 + 1 & 15);
            arrby[n4 + n3] = (i & 1) == 1 ? (byte)(arrby[n4 + n3] | by) : (byte)(by << 4);
        }
        arrby[n2 - 1 + n3] = 0;
        return n3;
    }

    protected String unpackValues(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        byte by = this.vspace.get(n++);
        while (by != 0) {
            char c = (char)((by >>> 4) - 1 + 48);
            stringBuffer.append(c);
            c = (char)(by & 15);
            if (c == '\u0000') break;
            c = (char)(c - '\u0001' + 48);
            stringBuffer.append(c);
            by = this.vspace.get(n++);
        }
        return stringBuffer.toString();
    }

    public void loadSimplePatterns(InputStream inputStream) {
        SimplePatternParser simplePatternParser = new SimplePatternParser();
        this.ivalues = new TernaryTree();
        simplePatternParser.parse(inputStream, this);
        this.trimToSize();
        this.vspace.trimToSize();
        this.classmap.trimToSize();
        this.ivalues = null;
    }

    public String findPattern(String string) {
        int n = super.find(string);
        if (n >= 0) {
            return this.unpackValues(n);
        }
        return "";
    }

    protected int hstrcmp(char[] arrc, int n, char[] arrc2, int n2) {
        while (arrc[n] == arrc2[n2]) {
            if (arrc[n] == '\u0000') {
                return 0;
            }
            ++n;
            ++n2;
        }
        if (arrc2[n2] == '\u0000') {
            return 0;
        }
        return arrc[n] - arrc2[n2];
    }

    protected byte[] getValues(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        byte by = this.vspace.get(n++);
        while (by != 0) {
            char c = (char)((by >>> 4) - 1);
            stringBuffer.append(c);
            c = (char)(by & 15);
            if (c == '\u0000') break;
            c = (char)(c - '\u0001');
            stringBuffer.append(c);
            by = this.vspace.get(n++);
        }
        byte[] arrby = new byte[stringBuffer.length()];
        for (int i = 0; i < arrby.length; ++i) {
            arrby[i] = (byte)stringBuffer.charAt(i);
        }
        return arrby;
    }

    protected void searchPatterns(char[] arrc, int n, byte[] arrby) {
        int n2 = n;
        char c = arrc[n2];
        char c2 = this.root;
        block0 : while (c2 > '\u0000' && c2 < this.sc.length) {
            int n3;
            int n4;
            byte[] arrby2;
            if (this.sc[c2] == '\uffff') {
                if (this.hstrcmp(arrc, n2, this.kv.getArray(), this.lo[c2]) == 0) {
                    arrby2 = this.getValues(this.eq[c2]);
                    n4 = n;
                    for (n3 = 0; n3 < arrby2.length; ++n3) {
                        if (n4 < arrby.length && arrby2[n3] > arrby[n4]) {
                            arrby[n4] = arrby2[n3];
                        }
                        ++n4;
                    }
                }
                return;
            }
            n4 = c - this.sc[c2];
            if (n4 == 0) {
                if (c == '\u0000') break;
                c = arrc[++n2];
                char c3 = c2 = this.eq[c2];
                while (c3 > '\u0000' && c3 < this.sc.length && this.sc[c3] != '\uffff') {
                    if (this.sc[c3] == '\u0000') {
                        arrby2 = this.getValues(this.eq[c3]);
                        n3 = n;
                        for (int i = 0; i < arrby2.length; ++i) {
                            if (n3 < arrby.length && arrby2[i] > arrby[n3]) {
                                arrby[n3] = arrby2[i];
                            }
                            ++n3;
                        }
                        continue block0;
                    }
                    c3 = this.lo[c3];
                }
                continue;
            }
            c2 = n4 < 0 ? this.lo[c2] : this.hi[c2];
        }
    }

    public Hyphenation hyphenate(String string, int n, int n2) {
        char[] arrc = string.toCharArray();
        return this.hyphenate(arrc, 0, arrc.length, n, n2);
    }

    public Hyphenation hyphenate(char[] arrc, int n, int n2, int n3, int n4) {
        int[] arrn;
        int n5;
        char[] arrc2 = new char[n2 + 3];
        char[] arrc3 = new char[2];
        int n6 = 0;
        int n7 = n2;
        boolean bl = false;
        for (n5 = 1; n5 <= n2; ++n5) {
            arrc3[0] = arrc[n + n5 - 1];
            int n8 = this.classmap.find(arrc3, 0);
            if (n8 < 0) {
                if (n5 == 1 + n6) {
                    ++n6;
                } else {
                    bl = true;
                }
                --n7;
                continue;
            }
            if (!bl) {
                arrc2[n5 - n6] = (char)n8;
                continue;
            }
            return null;
        }
        n2 = n7;
        if (n2 < n3 + n4) {
            return null;
        }
        int[] arrn2 = new int[n2 + 1];
        int n9 = 0;
        String string = new String(arrc2, 1, n2);
        if (this.stoplist.containsKey(string)) {
            arrn = (int[])this.stoplist.get(string);
            int n10 = 0;
            for (n5 = 0; n5 < arrn.size(); ++n5) {
                Object e = arrn.get(n5);
                if (!(e instanceof String) || (n10 += ((String)e).length()) < n3 || n10 >= n2 - n4) continue;
                arrn2[n9++] = n10 + n6;
            }
        } else {
            arrc2[0] = 46;
            arrc2[n2 + 1] = 46;
            arrc2[n2 + 2] = '\u0000';
            arrn = new byte[n2 + 3];
            for (n5 = 0; n5 < n2 + 1; ++n5) {
                this.searchPatterns(arrc2, n5, (byte[])arrn);
            }
            for (n5 = 0; n5 < n2; ++n5) {
                if ((arrn[n5 + 1] & 1) != 1 || n5 < n3 || n5 > n2 - n4) continue;
                arrn2[n9++] = n5 + n6;
            }
        }
        if (n9 > 0) {
            arrn = new int[n9];
            System.arraycopy(arrn2, 0, arrn, 0, n9);
            return new Hyphenation(new String(arrc, n, n2), arrn);
        }
        return null;
    }

    public void addClass(String string) {
        if (string.length() > 0) {
            char c = string.charAt(0);
            char[] arrc = new char[2];
            arrc[1] = '\u0000';
            for (int i = 0; i < string.length(); ++i) {
                arrc[0] = string.charAt(i);
                this.classmap.insert(arrc, 0, c);
            }
        }
    }

    public void addException(String string, ArrayList arrayList) {
        this.stoplist.put(string, arrayList);
    }

    public void addPattern(String string, String string2) {
        int n = this.ivalues.find(string2);
        if (n <= 0) {
            n = this.packValues(string2);
            this.ivalues.insert(string2, (char)n);
        }
        this.insert(string, (char)n);
    }

    public void printStats() {
        System.out.println("Value space size = " + Integer.toString(this.vspace.length()));
        super.printStats();
    }
}

