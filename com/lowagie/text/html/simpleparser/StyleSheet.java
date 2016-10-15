/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html.simpleparser;

import java.util.HashMap;
import java.util.Map;

public class StyleSheet {
    public HashMap classMap = new HashMap();
    public HashMap tagMap = new HashMap();

    public void applyStyle(String string, HashMap hashMap) {
        Object object;
        HashMap hashMap2 = (HashMap)this.tagMap.get(string.toLowerCase());
        if (hashMap2 != null) {
            object = new HashMap(hashMap2);
            object.putAll(hashMap);
            hashMap.putAll(object);
        }
        if ((object = (String)hashMap.get("class")) == null) {
            return;
        }
        hashMap2 = (HashMap)this.classMap.get(object.toLowerCase());
        if (hashMap2 == null) {
            return;
        }
        hashMap.remove("class");
        HashMap hashMap3 = new HashMap(hashMap2);
        hashMap3.putAll(hashMap);
        hashMap.putAll(hashMap3);
    }

    public void loadStyle(String string, HashMap hashMap) {
        this.classMap.put(string.toLowerCase(), hashMap);
    }

    public void loadStyle(String string, String string2, String string3) {
        HashMap<String, String> hashMap = (HashMap<String, String>)this.classMap.get(string = string.toLowerCase());
        if (hashMap == null) {
            hashMap = new HashMap<String, String>();
            this.classMap.put(string, hashMap);
        }
        hashMap.put(string2, string3);
    }

    public void loadTagStyle(String string, HashMap hashMap) {
        this.tagMap.put(string.toLowerCase(), hashMap);
    }

    public void loadTagStyle(String string, String string2, String string3) {
        HashMap<String, String> hashMap = (HashMap<String, String>)this.tagMap.get(string = string.toLowerCase());
        if (hashMap == null) {
            hashMap = new HashMap<String, String>();
            this.tagMap.put(string, hashMap);
        }
        hashMap.put(string2, string3);
    }
}

