/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html.simpleparser;

import java.util.ArrayList;
import java.util.HashMap;

public class ChainedProperties {
    public static final int[] fontSizes = new int[]{8, 10, 12, 14, 18, 24, 36};
    public ArrayList chain = new ArrayList();

    public String getProperty(String string) {
        for (int i = this.chain.size() - 1; i >= 0; --i) {
            Object[] arrobject = (Object[])this.chain.get(i);
            HashMap hashMap = (HashMap)arrobject[1];
            String string2 = (String)hashMap.get(string);
            if (string2 == null) continue;
            return string2;
        }
        return null;
    }

    public boolean hasProperty(String string) {
        for (int i = this.chain.size() - 1; i >= 0; --i) {
            Object[] arrobject = (Object[])this.chain.get(i);
            HashMap hashMap = (HashMap)arrobject[1];
            if (!hashMap.containsKey(string)) continue;
            return true;
        }
        return false;
    }

    public void addToChain(String string, HashMap hashMap) {
        String string2 = (String)hashMap.get("size");
        if (string2 != null) {
            if (string2.endsWith("px")) {
                hashMap.put("size", string2.substring(0, string2.length() - 2));
            } else {
                int n = 0;
                if (string2.startsWith("+") || string2.startsWith("-")) {
                    int n2;
                    String string3 = this.getProperty("basefontsize");
                    if (string3 == null) {
                        string3 = "12";
                    }
                    float f = Float.parseFloat(string3);
                    int n3 = (int)f;
                    for (n2 = ChainedProperties.fontSizes.length - 1; n2 >= 0; --n2) {
                        if (n3 < fontSizes[n2]) continue;
                        n = n2;
                        break;
                    }
                    n2 = Integer.parseInt(string2.startsWith("+") ? string2.substring(1) : string2);
                    n += n2;
                } else {
                    try {
                        n = Integer.parseInt(string2) - 1;
                    }
                    catch (NumberFormatException var5_6) {
                        n = 0;
                    }
                }
                if (n < 0) {
                    n = 0;
                } else if (n >= fontSizes.length) {
                    n = fontSizes.length - 1;
                }
                hashMap.put("size", Integer.toString(fontSizes[n]));
            }
        }
        this.chain.add(new Object[]{string, hashMap});
    }

    public void removeChain(String string) {
        for (int i = this.chain.size() - 1; i >= 0; --i) {
            if (!string.equals(((Object[])this.chain.get(i))[0])) continue;
            this.chain.remove(i);
            return;
        }
    }
}

