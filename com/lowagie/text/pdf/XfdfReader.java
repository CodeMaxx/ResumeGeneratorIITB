/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

public class XfdfReader
implements SimpleXMLDocHandler {
    private boolean foundRoot;
    private Stack fieldNames;
    private Stack fieldValues;
    HashMap fields;
    String fileSpec;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XfdfReader(String string) throws IOException {
        this.foundRoot = false;
        this.fieldNames = new Stack();
        this.fieldValues = new Stack();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(string);
            SimpleXMLParser.parse((SimpleXMLDocHandler)this, fileInputStream);
            Object var4_3 = null;
        }
        catch (Throwable var3_7) {
            Object var4_4 = null;
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            catch (Exception var5_6) {
                // empty catch block
            }
            throw var3_7;
        }
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        catch (Exception var5_5) {}
    }

    public XfdfReader(byte[] arrby) throws IOException {
        this.foundRoot = false;
        this.fieldNames = new Stack();
        this.fieldValues = new Stack();
        SimpleXMLParser.parse((SimpleXMLDocHandler)this, new ByteArrayInputStream(arrby));
    }

    public HashMap getFields() {
        return this.fields;
    }

    public String getField(String string) {
        return (String)this.fields.get(string);
    }

    public String getFieldValue(String string) {
        String string2 = (String)this.fields.get(string);
        if (string2 == null) {
            return null;
        }
        return string2;
    }

    public String getFileSpec() {
        return this.fileSpec;
    }

    public void startElement(String string, HashMap hashMap) {
        if (!this.foundRoot) {
            if (!string.equals("xfdf")) {
                throw new RuntimeException("Root element is not Bookmark.");
            }
            this.foundRoot = true;
        }
        if (!string.equals("xfdf")) {
            if (string.equals("f")) {
                this.fileSpec = (String)hashMap.get("href");
            } else if (string.equals("fields")) {
                this.fields = new HashMap();
            } else if (string.equals("field")) {
                String string2 = (String)hashMap.get("name");
                this.fieldNames.push(string2);
            } else if (string.equals("value")) {
                this.fieldValues.push("");
            }
        }
    }

    public void endElement(String string) {
        if (string.equals("value")) {
            String string2 = "";
            for (int i = 0; i < this.fieldNames.size(); ++i) {
                string2 = string2 + "." + (String)this.fieldNames.elementAt(i);
            }
            if (string2.startsWith(".")) {
                string2 = string2.substring(1);
            }
            String string3 = (String)this.fieldValues.pop();
            this.fields.put(string2, string3);
        } else if (string.equals("field") && !this.fieldNames.isEmpty()) {
            this.fieldNames.pop();
        }
    }

    public void startDocument() {
        this.fileSpec = "";
    }

    public void endDocument() {
    }

    public void text(String string) {
        if (this.fieldNames.isEmpty() || this.fieldValues.isEmpty()) {
            return;
        }
        String string2 = (String)this.fieldValues.pop();
        string2 = string2 + string;
        this.fieldValues.push(string2);
    }
}

