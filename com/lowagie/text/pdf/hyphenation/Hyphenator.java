/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.hyphenation.Hyphenation;
import com.lowagie.text.pdf.hyphenation.HyphenationTree;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

public class Hyphenator {
    private static Hashtable hyphenTrees = new Hashtable();
    private HyphenationTree hyphenTree = null;
    private int remainCharCount = 2;
    private int pushCharCount = 2;
    private static final String defaultHyphLocation = "com/lowagie/text/pdf/hyphenation/hyph/";
    private static String hyphenDir = "";

    public Hyphenator(String string, String string2, int n, int n2) {
        this.hyphenTree = Hyphenator.getHyphenationTree(string, string2);
        this.remainCharCount = n;
        this.pushCharCount = n2;
    }

    public static HyphenationTree getHyphenationTree(String string, String string2) {
        String string3 = string;
        if (string2 != null && !string2.equals("none")) {
            string3 = string3 + "_" + string2;
        }
        if (hyphenTrees.containsKey(string3)) {
            return (HyphenationTree)hyphenTrees.get(string3);
        }
        if (hyphenTrees.containsKey(string)) {
            return (HyphenationTree)hyphenTrees.get(string);
        }
        HyphenationTree hyphenationTree = Hyphenator.getResourceHyphenationTree(string3);
        if (hyphenationTree == null) {
            hyphenationTree = Hyphenator.getFileHyphenationTree(string3);
        }
        if (hyphenationTree != null) {
            hyphenTrees.put(string3, hyphenationTree);
        }
        return hyphenationTree;
    }

    public static HyphenationTree getResourceHyphenationTree(String string) {
        try {
            InputStream inputStream = BaseFont.getResourceStream("com/lowagie/text/pdf/hyphenation/hyph/" + string + ".xml");
            if (inputStream == null && string.length() > 2) {
                inputStream = BaseFont.getResourceStream("com/lowagie/text/pdf/hyphenation/hyph/" + string.substring(0, 2) + ".xml");
            }
            if (inputStream == null) {
                return null;
            }
            HyphenationTree hyphenationTree = new HyphenationTree();
            hyphenationTree.loadSimplePatterns(inputStream);
            return hyphenationTree;
        }
        catch (Exception var1_2) {
            return null;
        }
    }

    public static HyphenationTree getFileHyphenationTree(String string) {
        try {
            if (hyphenDir == null) {
                return null;
            }
            FileInputStream fileInputStream = null;
            File file = new File(hyphenDir, string + ".xml");
            if (file.canRead()) {
                fileInputStream = new FileInputStream(file);
            }
            if (fileInputStream == null && string.length() > 2 && (file = new File(hyphenDir, string.substring(0, 2) + ".xml")).canRead()) {
                fileInputStream = new FileInputStream(file);
            }
            if (fileInputStream == null) {
                return null;
            }
            HyphenationTree hyphenationTree = new HyphenationTree();
            hyphenationTree.loadSimplePatterns(fileInputStream);
            return hyphenationTree;
        }
        catch (Exception var1_2) {
            return null;
        }
    }

    public static Hyphenation hyphenate(String string, String string2, String string3, int n, int n2) {
        HyphenationTree hyphenationTree = Hyphenator.getHyphenationTree(string, string2);
        if (hyphenationTree == null) {
            return null;
        }
        return hyphenationTree.hyphenate(string3, n, n2);
    }

    public static Hyphenation hyphenate(String string, String string2, char[] arrc, int n, int n2, int n3, int n4) {
        HyphenationTree hyphenationTree = Hyphenator.getHyphenationTree(string, string2);
        if (hyphenationTree == null) {
            return null;
        }
        return hyphenationTree.hyphenate(arrc, n, n2, n3, n4);
    }

    public void setMinRemainCharCount(int n) {
        this.remainCharCount = n;
    }

    public void setMinPushCharCount(int n) {
        this.pushCharCount = n;
    }

    public void setLanguage(String string, String string2) {
        this.hyphenTree = Hyphenator.getHyphenationTree(string, string2);
    }

    public Hyphenation hyphenate(char[] arrc, int n, int n2) {
        if (this.hyphenTree == null) {
            return null;
        }
        return this.hyphenTree.hyphenate(arrc, n, n2, this.remainCharCount, this.pushCharCount);
    }

    public Hyphenation hyphenate(String string) {
        if (this.hyphenTree == null) {
            return null;
        }
        return this.hyphenTree.hyphenate(string, this.remainCharCount, this.pushCharCount);
    }

    public static String getHyphenDir() {
        return hyphenDir;
    }

    public static void setHyphenDir(String string) {
        hyphenDir = string;
    }
}

